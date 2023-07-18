package ke.co.xently.remotedatasource.services

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import ke.co.xently.BuildConfig
import ke.co.xently.remotedatasource.exceptions.WebsocketConnectionFailedException
import ke.co.xently.remotedatasource.exceptions.WebsocketException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class WebsocketAutoCompleteService<in Q, out R>(
    private val client: HttpClient,
    private val endpoint: String,
    private val waitDuration: Duration = 1.seconds,
    private val waitActiveRetryCount: Int = 3,
    private val waitBackoffMultiplier: Int = 1,
    private val queryString: (Q) -> String,
) : AutoCompleteService<Q, R> {
    companion object {
        private val TAG = WebsocketAutoCompleteService::class.java.simpleName
    }

    private val urlString: String = buildString {
        append(BuildConfig.AUTO_COMPLETE_BASE_URL.removeSuffix("/"))
        append('/')
        append(endpoint.removePrefix("/"))
    }

    private var socket: WebSocketSession? = null

    override suspend fun initSession(): Result<Unit> {
        return try {
            Log.i(TAG, "initSession: ...")
            socket = socket ?: client.webSocketSession {
                url(urlString = urlString)
            }

            var retryCountDown = waitActiveRetryCount
            var newWaitDuration = waitDuration
            while (!socket!!.isActive && retryCountDown > 0) {
                Log.i(
                    TAG,
                    "initSession: waiting for $newWaitDuration for the socket to be active...",
                )
                delay(newWaitDuration)
                retryCountDown -= 1
                newWaitDuration *= waitBackoffMultiplier
            }

            if (socket!!.isActive) {
                Result.success(Unit).also {
                    Log.i(TAG, "initSession: successfully initialized session")
                }
            } else {
                Result.failure(WebsocketConnectionFailedException())
            }
        } catch (e: Exception) {
            Result.failure(WebsocketException(e))
        }.onFailure {
            Log.e(TAG, "initSession: error: ${it.localizedMessage}", it)
        }
    }

    @Serializable
    data class Request(val q: String, val size: Int = 5)

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun search(query: Q, size: Int) {
        initSession().onSuccess {
            try {
                socket?.run {
                    val request = Request(q = queryString(query), size = size)
                    val content = Json.encodeToString(request)
                    send(content)
                    Log.i(TAG, "search: sent search request for: $content")
                }
            } catch (ex: Exception) {
                Log.e(TAG, "search: ${ex.localizedMessage}", ex)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun getSearchResults(): Flow<List<R>> {
        Log.i(TAG, "getSearchResults: ...")
        return socket?.run {
            Log.i(TAG, "getSearchResults: socket was initialised")
            incoming.receiveAsFlow()
                .filter { it is Frame.Text }
                .map { frame ->
                    val response = ((frame as? Frame.Text)?.readText() ?: "[]").also {
                        Log.i(TAG, "getSearchResults: $it")
                    }
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    json.decodeFromString(response)
                }
        } ?: flow { }
    }

    override suspend fun closeSession() {
        Log.i(TAG, "closeSession: ...")
        try {
            socket?.close()?.also {
                Log.i(TAG, "closeSession: closed websocket session.")
            }
        } finally {
            socket = null
        }
    }
}