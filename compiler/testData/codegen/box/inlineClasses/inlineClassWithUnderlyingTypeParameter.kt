// !LANGUAGE: +InlineClasses

inline class Generic<T>(val x: T)

fun concat(primitive: Generic<Int>, reference: Generic<String>): String {
    return primitive.x.toString() + reference.x
}

fun <T> boxUnbox(g: Generic<T>?): Generic<T> = g!!

fun box(): String {
    val p = Generic(3)
    val r = Generic("foo")

    if (concat(p, r) != "3foo") return "Fail 1"

    val nullable: Generic<Long>? = Generic(1L)
    if (nullable!!.x != 1L) return "Fail 2"

    if (boxUnbox(nullable).x != 1L) return "Fail 3"

    return "OK"
}