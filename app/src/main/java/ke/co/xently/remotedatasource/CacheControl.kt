package ke.co.xently.remotedatasource

sealed class CacheControl(private val name: String) {
    override fun toString(): String {
        return name
    }

    object NoCache : CacheControl("no-cache")

    object OnlyIfCached : CacheControl("only-if-cached")
}
