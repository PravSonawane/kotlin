// !LANGUAGE: +InlineClasses
// !SKIP_METADATA_VERSION_CHECK
// WITH_UNSIGNED

fun test(us: UIntArray): Int {
    return us.size
}

fun myUIntArrayOf(vararg us: UInt): UIntArray = us

fun box(): String {
    if (test(myUIntArrayOf(1u, 2u, 3u, 4u)) != 4) return "Fail"
    return "OK"
}