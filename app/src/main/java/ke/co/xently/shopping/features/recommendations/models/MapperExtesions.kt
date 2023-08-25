package ke.co.xently.shopping.features.recommendations.models

import com.google.gson.reflect.TypeToken
import ke.co.xently.shopping.remotedatasource.Serialization


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
            val recommendations: List<Recommendation.Response> =
                Serialization.JSON_CONVERTER.fromJson(
                    recommendationsJson,
                    object : TypeToken<List<Recommendation.Response>>() {
                    }.type,
                )
            RecommendationResponse.ViewModel(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendations = recommendations,
            )
        }
    }
}


fun RecommendationResponse.toLocalCache(): RecommendationResponse.LocalCache {
    return when (this) {
        is RecommendationResponse.LocalCache -> this
        is RecommendationResponse.ServerSide -> {
            val recommendationsJson = Serialization.JSON_CONVERTER.toJson(recommendations)
            RecommendationResponse.LocalCache(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendationsJson = recommendationsJson,
            )
        }

        is RecommendationResponse.ViewModel -> {
            val recommendationsJson = Serialization.JSON_CONVERTER.toJson(recommendations)
            RecommendationResponse.LocalCache(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendationsJson = recommendationsJson,
            )
        }
    }
}

fun RecommendationResponse.toServerSide(): RecommendationResponse.ServerSide {
    return when (this) {
        is RecommendationResponse.ServerSide -> this
        is RecommendationResponse.ViewModel -> {
            RecommendationResponse.ServerSide(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendations = recommendations,
            )
        }

        is RecommendationResponse.LocalCache -> {
            val recommendations: List<Recommendation.Response> =
                Serialization.JSON_CONVERTER.fromJson(
                    recommendationsJson,
                    object : TypeToken<List<Recommendation.Response>>() {
                    }.type,
                )
            RecommendationResponse.ServerSide(
                requestId = requestId,
                serviceCharge = serviceCharge,
                recommendations = recommendations,
            )
        }

    }
}