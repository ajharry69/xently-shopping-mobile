package ke.co.xently.shopping.datasource.remote.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.auth.providers.bearer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import ke.co.xently.shopping.BaseURL
import ke.co.xently.shopping.BuildConfig
import ke.co.xently.shopping.datasource.local.Database
import ke.co.xently.shopping.datasource.remote.Serialization
import ke.co.xently.shopping.datasource.remote.di.qualifiers.CacheInterceptor
import ke.co.xently.shopping.datasource.remote.di.qualifiers.RequestHeadersInterceptor
import ke.co.xently.shopping.datasource.remote.di.qualifiers.RequestQueriesInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
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
    fun provideRequestHeadersInterceptors(database: Database): Interceptor = Interceptor { chain ->
        val request = chain.request()

        return@Interceptor chain.proceed(
            request.newBuilder().apply {
                // Add the following headers iff they weren't already added by the
                // incoming request

                if (request.header("Accept-Language") == null) {
                    addHeader("Accept-Language", Locale.getDefault().language)
                }

                if (request.header("Accept") == null) {
                    val version = if (BuildConfig.API_VERSION.isNotBlank()) {
                        "; version=${BuildConfig.API_VERSION}"
                    } else ""
                    addHeader("Accept", "application/json${version}")
                }

                if (request.header("Authorization") == null) {
                    runBlocking(Dispatchers.IO) {
                        database.userDao.getAuthorizationToken()
                    }?.also {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
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
        .baseUrl(BaseURL.API.removeSuffix("/").plus("/api/"))
        .addConverterFactory(GsonConverterFactory.create(Serialization.JSON_CONVERTER))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json(from = KotlinxSerializer.DefaultJson) {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json, database: Database): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer(json = json)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        database.userDao.getAuthorizationToken()?.let {
                            BearerTokens(it, it)
                        }
                    }
                }
            }
        }
    }
}