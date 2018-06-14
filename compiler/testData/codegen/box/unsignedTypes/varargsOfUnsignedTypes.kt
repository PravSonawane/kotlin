// !LANGUAGE: +InlineClasses
// !SKIP_METADATA_VERSION_CHECK
// WITH_UNSIGNED

fun uint(vararg us: UInt): UIntArray = us

fun box(): String {
    val uints = uint(1u, 2u, 3u)
    var sum: UInt = 0u
    for (i in uints) {
        sum += i
    }

    if (sum != 6u) return "Fail: sum: $sum"

    return "OK"
}