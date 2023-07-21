package ke.co.xently.remotedatasource.exceptions

open class WebsocketException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
}