package ke.co.xently.remotedatasource.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.co.xently.BuildConfig
import ke.co.xently.remotedatasource.Serialization
import ke.co.xently.remotedatasource.di.qualifiers.CacheInterceptor
import ke.co.xently.remotedatasource.di.qualifiers.RequestHeadersInterceptor
import ke.co.xently.remotedatasource.di.qualifiers.RequestQueriesInterceptor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @RequestQueriesInterceptor
    fun provideRequestQueriesInterceptors(): Interceptor = Interceptor { chain ->
        val request = chain.request()

        return@Interceptor chain.proceed(
            request.newBuilder().apply {
                val url = build().url
                if (url.queryParameter("q").isNullOrBlank()) {
                    url(url.newBuilder().removeAllQueryParameters("q").build())
                }
            }.build(),
        )
    }

    @Provides
    @Singleton
    @RequestHeadersInterceptor
    fun provideRequestHeadersInterceptors(): Interceptor = Interceptor { chain ->
        val request = chain.request()

        return@Interceptor chain.proceed(
            request.newBuilder().apply {
                // Add the following headers iff they weren't already added by the
                // incoming request

                if (request.header("Accept-Language") == null) {
                    addHeader("Accept-Language", Locale.getDefault().language)
                }

                /*if (request.header("Accept") == null) {
                    val version = if (BuildConfig.API_VERSION.isNotBlank()) {
                        "; version=${BuildConfig.API_VERSION}"
                    } else ""
                    addHeader("Accept", "application/json${version}")
                }

                if (request.header("Authorization") == null) {
                    preferences.getString(TOKEN_VALUE, null)?.also {
                        addHeader("Authorization", "Bearer $it")
                    }
                }*/
            }.build(),
        )
    }

    @Provides
    @Singleton
    @CacheInterceptor
    fun provideCacheInterceptors(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        var response = chain.proceed(
            request.newBuilder()
                .cacheControl(CacheControl.parse(request.headers)).build()
        )
        if (response.code == 504 && response.request.cacheControl.onlyIfCached) {
            // See, https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#other
            response = chain.proceed(
                response.request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK).build()
            )
        }
        return@Interceptor response
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            redactHeader("Authorization")
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, (5 * 1024 * 1024).toLong())

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
        @CacheInterceptor cacheInterceptor: Interceptor,
        @RequestHeadersInterceptor headerInterceptor: Interceptor,
        @RequestQueriesInterceptor queriesInterceptor: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(headerInterceptor)
        .addInterceptor(queriesInterceptor)
        .addInterceptor(cacheInterceptor) // maintain order - cache may depend on the headers
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(15L, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(Serialization.JSON_CONVERTER))
        .client(okHttpClient)
        .build()
}