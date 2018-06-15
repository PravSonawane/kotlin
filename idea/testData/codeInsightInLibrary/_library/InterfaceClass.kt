package library

interface <lineMarker>My</lineMarker> {
    fun foo(): Int

    val s: String
}

class Your : My {
    override fun foo(): Int {
        return 42
    }

    override val s: String
        get() = ""
}