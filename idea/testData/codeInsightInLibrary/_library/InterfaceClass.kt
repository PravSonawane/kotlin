package library

interface My {
    fun foo(): Int

    val s: String
}

class Your : My {
    override fun <lineMarker>foo</lineMarker>(): Int {
        return 42
    }

    override val <lineMarker>s</lineMarker>: String
        get() = ""
}