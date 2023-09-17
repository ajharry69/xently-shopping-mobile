package ke.co.xently.shopping.datasource.remote.services

import androidx.annotation.Keep
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import ke.co.xently.shopping.BaseURL
import ke.co.xently.shopping.datasource.remote.exceptions.WebsocketConnectionFailedException
import ke.co.xently.shopping.datasource.remote.exceptions.WebsocketException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

open class WebsocketAutoCompleteService<in Q>(
    private val client: HttpClient,
    private val endpoint: String,
    private val searchDelayDuration: (String) -> Duration? = { 100.milliseconds },
    private val waitDuration: Duration = 1.seconds,
    private val waitActiveRetryCount: Int = 3,
    private val waitBackoffMultiplier: Int = 2,
    private val queryString: (Q) -> String,
    private val mapResponse: Json.(Response<Q>) -> AutoCompleteService.ResultState,
) : AutoCompleteService<Q> {
    companion object {
        private val TAG = WebsocketAutoCompleteService::class.java.simpleName
        private val sessions = mutableMapOf<String, WebSocketSession>()
    }

    @Keep
    @Serializable
    data class Request(val q: String, val size: Int = 5)

    data class Response<Q>(val json: String, val currentQuery: Q?)

    private val urlString: String = buildString {
        append(BaseURL.WEB_SOCKET.removeSuffix("/"))
        append('/')
        append(endpoint.removePrefix("/"))
    }

    private var currentQuery: Q? = null

    private var socket: WebSocketSession? = sessions[urlString]

    private val _resultState = MutableSharedFlow<AutoCompleteService.ResultState>()
    override val resultState = _resultState.asSharedFlow()
    override suspend fun init(logSuccessfulInitialization: Boolean) {
        val response = initSession(logSuccessfulInitialization)
        if (response is AutoCompleteService.InitState.Success) {
            initGetSearchResults()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun search(query: Q, size: Int) {
        val q = queryString(query)

        if (q.isBlank()) {
            currentQuery = null
            Timber.tag(TAG).i("[%s]: skipping search of blank query...", urlString)
            return
        }
        currentQuery = query

        val searchDelay = searchDelayDuration(q)
        if (searchDelay != null) {
            delay(searchDelay)
        }

        val request = Request(q = q, size = size)
        val content = Json.encodeToString(request)

        try {
            socket?.send(content)?.also {
                Timber.tag(TAG)
                    .i("[%s]: sent a search request for: %s", urlString, content)
            }
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "[%s]: error searching for '%s'", urlString, content)
        }
    }

    override suspend fun closeSession() {
        Timber.tag(TAG).i("[%s]: requested session closure...", urlString)
        try {
            socket?.close()?.also {
                Timber.tag(TAG).i("[%s]: successfully closed websocket session.", urlString)
            }
        } catch (ex: Exception) {
            Timber.tag(TAG).e(ex, "[%s]: error closing session", urlString)
        } finally {
            socket = null
            sessions.remove(urlString)
            currentQuery = null
        }
    }

    private suspend fun initGetSearchResults() {
        socket?.incoming?.receiveAsFlow()?.filter { it is Frame.Text }?.map { frame ->
            val response = ((frame as? Frame.Text)?.readText() ?: "[]").also {
                Timber.tag(TAG).i("[%s]: results: %s", urlString, it)
            }
            val json = Json {
                ignoreUnknownKeys = true
            }
            json.mapResponse(Response(json = response, currentQuery = currentQuery))
        }?.onStart {
            emit(AutoCompleteService.ResultState.Loading)
            Timber.tag(TAG).i(
                "[%s]: session was successfully initialised. Getting search results...",
                urlString
            )
        }?.catch {
            emit(AutoCompleteService.ResultState.Failure(it))
            Timber.tag(TAG).e(it, "[%s]: error getting search results", urlString)
        }?.collect(_resultState::emit)
    }

    private suspend fun initSession(logSuccessfulInitialization: Boolean): AutoCompleteService.InitState {
        return try {
            if (socket == null) {
                socket = sessions.getOrPut(urlString) {
                    Timber.tag(TAG).i(
                        "[%s]: reinitialising websocket session...",
                        urlString,
                    )
                    client.webSocketSession {
                        url(urlString = urlString)
                    }
                }
            }

            var retryCountDown = waitActiveRetryCount
            var newWaitDuration = waitDuration
            while (!socket!!.isActive && retryCountDown > 0) {
                Timber.tag(TAG).i(
                    "[%s]: waiting for %s for the session to be active...",
                    urlString,
                    newWaitDuration,
                )
                delay(newWaitDuration)
                retryCountDown -= 1
                newWaitDuration *= waitBackoffMultiplier
            }

            if (socket!!.isActive) {
                AutoCompleteService.InitState.Success.also {
                    if (logSuccessfulInitialization) {
                        Timber.tag(TAG).i("[%s]: successfully initialised session", urlString)
                    }
                }
            } else {
                AutoCompleteService.InitState.Failure(WebsocketConnectionFailedException())
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "[%s]: error initialising session", urlString)
            AutoCompleteService.InitState.Failure(WebsocketException(e))
        }
    }
}