// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND: JS

// WITH_RUNTIME
// KOTLIN_CONFIGURATION_FLAGS: +JVM.INHERIT_MULTIFILE_PARTS
// FILE: box.kt

import a.*

fun box(): String = OK.okRef.get()

// FILE: part1.kt
@file:[JvmName("MultifileClass") JvmMultifileClass]
package a

private val ok = run { "OK" }

object OK {
    val okRef = ::ok
}