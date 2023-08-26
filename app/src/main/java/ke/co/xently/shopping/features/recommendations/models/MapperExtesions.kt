package ke.co.xently.shopping.features.recommendations.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@OptIn(ExperimentalSerializationApi::class)
fun RecommendationResponse.toViewModel(): RecommendationResponse.ViewModel {
    return when (this) {
        is RecommendationResponse.ViewModel -> this
        is RecommendationResponse.ServerSide -> {
            RecommendationResponse.ViewModel(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendations = recommendations,
            )
        }

        is RecommendationResponse.LocalCache -> {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val recommendations =
                json.decodeFromString<List<Recommendation.Response>>(recommendationsJson)
            RecommendationResponse.ViewModel(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendations = recommendations,
            )
        }
    }
}


@OptIn(ExperimentalSerializationApi::class)
fun RecommendationResponse.toLocalCache(): RecommendationResponse.LocalCache {
    return when (this) {
        is RecommendationResponse.LocalCache -> this
        is RecommendationResponse.ServerSide -> {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val recommendationsJson = json.encodeToString(recommendations)
            RecommendationResponse.LocalCache(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendationsJson = recommendationsJson,
            )
        }

        is RecommendationResponse.ViewModel -> {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val recommendationsJson = json.encodeToString(recommendations)
            RecommendationResponse.LocalCache(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendationsJson = recommendationsJson,
            )
        }
    }
}
