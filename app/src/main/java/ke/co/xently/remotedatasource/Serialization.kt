package ke.co.xently.remotedatasource

import android.net.Uri
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import ke.co.xently.features.recommendations.models.Recommendation
import ke.co.xently.remotedatasource.Exclude.During.BOTH
import ke.co.xently.remotedatasource.Exclude.During.DESERIALIZATION
import ke.co.xently.remotedatasource.Exclude.During.SERIALIZATION
import java.math.BigDecimal

object Serialization {
    private fun getExclusionStrategy(during: Exclude.During = BOTH): ExclusionStrategy {
        return object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                return if (f == null) true else {
                    val annotation = f.getAnnotation(Exclude::class.java)
                    if (annotation == null) {
                        false
                    } else {
                        annotation.during == during
                    }
                }
            }
        }
    }

    val JSON_CONVERTER: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .addSerializationExclusionStrategy(getExclusionStrategy(SERIALIZATION))
        .addDeserializationExclusionStrategy(getExclusionStrategy(DESERIALIZATION))
        .setExclusionStrategies(getExclusionStrategy())
        .serializeNulls()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        // https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.0/com/google/gson/TypeAdapter.html
        .registerTypeAdapter(Uri::class.java, object : TypeAdapter<Uri>() {
            override fun write(out: JsonWriter?, value: Uri?) {
                out?.value(value?.toString())
            }

            override fun read(`in`: JsonReader?): Uri? {
                val uri = `in`?.nextString() ?: return null
                return Uri.parse(uri)
            }
        }.nullSafe())
        .registerTypeAdapter(BigDecimal::class.java, object : TypeAdapter<BigDecimal>() {
            override fun write(out: JsonWriter?, value: BigDecimal?) {
                out?.value(value?.toString()?.toDouble())
            }

            override fun read(`in`: JsonReader?): BigDecimal? {
                val value = `in`?.nextDouble() ?: return null
                return BigDecimal(value)
            }
        }.nullSafe())
        .registerTypeAdapter(
            android.icu.math.BigDecimal::class.java,
            object : TypeAdapter<android.icu.math.BigDecimal>() {
                override fun write(out: JsonWriter?, value: android.icu.math.BigDecimal?) {
                    out?.value(value?.toString()?.toDouble())
                }

                override fun read(`in`: JsonReader?): android.icu.math.BigDecimal? {
                    val value = `in`?.nextDouble() ?: return null
                    return android.icu.math.BigDecimal(value)
                }
            }.nullSafe()
        )
        .registerTypeAdapter(
            Recommendation.Response.Miss.Item::class.java,
            object : TypeAdapter<Recommendation.Response.Miss.Item>() {
                override fun write(out: JsonWriter?, value: Recommendation.Response.Miss.Item?) {
                    out?.value(value?.toString())
                }

                override fun read(`in`: JsonReader?): Recommendation.Response.Miss.Item? {
                    val value = `in`?.nextString() ?: return null
                    return Recommendation.Response.Miss.Item(value)
                }
            }.nullSafe()
        )
        /*.setPrettyPrinting()
        .setVersion(1.0)*/
        .create()
}