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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSerializationApi::class)
open class WebsocketAutoCompleteService<in Q>(
    private val client: HttpClient,
    private val endpoint: String,
    private val waitDuration: Duration = 1.seconds,
    private val waitActiveRetryCount: Int = 3,
    private val waitBackoffMultiplier: Int = 2,
    private val queryString: (Q) -> String,
    private val mapResponse: Json.(response: String) -> AutoCompleteService.ResultState = {
        decodeFromString(it)
    },
) : AutoCompleteService<Q> {
    companion object {
        private val TAG = WebsocketAutoCompleteService::class.java.simpleName
    }

    private val urlString: String = buildString {
        append(BuildConfig.AUTO_COMPLETE_BASE_URL.removeSuffix("/"))
        append('/')
        append(endpoint.removePrefix("/"))
    }

    private var socket: WebSocketSession? = null

    override suspend fun initSession(logSuccessfulInitialization: Boolean): AutoCompleteService.InitState {
        return try {
            socket = socket ?: client.webSocketSession {
                url(urlString = urlString)
            }

            var retryCountDown = waitActiveRetryCount
            var newWaitDuration = waitDuration
            while (!socket!!.isActive && retryCountDown > 0) {
                Log.i(
                    TAG,
                    "Waiting for $newWaitDuration for the session to be active...",
                )
                delay(newWaitDuration)
                retryCountDown -= 1
                newWaitDuration *= waitBackoffMultiplier
            }

            if (socket!!.isActive) {
                AutoCompleteService.InitState.Success.also {
                    if (logSuccessfulInitialization) {
                        Log.i(TAG, "Successfully initialised session")
                    }
                }
            } else {
                AutoCompleteService.InitState.Failure(WebsocketConnectionFailedException())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initialising session", e)
            AutoCompleteService.InitState.Failure(WebsocketException(e))
        }
    }

    @Serializable
    data class Request(val q: String, val size: Int = 5)

    override suspend fun search(query: Q, size: Int) {
        val request = Request(q = queryString(query), size = size)
        val content = Json.encodeToString(request)

        val state = initSession(logSuccessfulInitialization = false)
        if (state !is AutoCompleteService.InitState.Success) {
            Log.i(
                TAG,
                "Skipping search propagation for ${content}. Search session is not successfully initialised!",
            )
            return
        }

        try {
            socket?.run {
                send(content)
                Log.i(TAG, "Sent a search request for: $content")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error searching for '$content'", ex)
        }
    }

    override fun getSearchResults(): Flow<AutoCompleteService.ResultState> {
        return socket?.run {
            incoming.receiveAsFlow()
                .filter { it is Frame.Text }
                .map { frame ->
                    val response = ((frame as? Frame.Text)?.readText() ?: "[]").also {
                        Log.i(TAG, "Results: $it")
                    }
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    json.mapResponse(response)
                }
                .onStart {
                    emit(AutoCompleteService.ResultState.Loading)
                    Log.i(TAG, "Session was successfully initialised. Getting search results...")
                }
                .catch {
                    emit(AutoCompleteService.ResultState.Failure(it))
                    Log.e(TAG, "Error getting search results", it)
                }
        } ?: flowOf<AutoCompleteService.ResultState>().onEach {
            Log.i(TAG, "getSearchResults: returned default flow...")
        }
    }

    override suspend fun closeSession() {
        Log.i(TAG, "Requested session closure...")
        try {
            socket?.close()?.also {
                Log.i(TAG, "Successfully closed websocket session.")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error closing session", ex)
        } finally {
            socket = null
        }
    }
}