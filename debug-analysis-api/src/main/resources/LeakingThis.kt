/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

//class Outer {
//    init {
//        Inner().leak()
//    }
//
//    inner class Inner {
//        fun leak() = println(this@Outer)
//    }
//}

class LeakingExample {
    init {
        register(this)
    }
}

fun register(obj: LeakingExample): String {
    return Registered: $obj"
}

fun main() {
    val string = LeakingExample()
}