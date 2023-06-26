package ke.co.xently.locationtracker.exceptions


open class LocationTrackerException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
}

