/*
 * Copyright (C) 2021 Strumenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alchitry.kotlinmultiplatform

import org.antlr.v4.kotlinruntime.tree.ParseTree
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

fun <T> Array<T>.indices(): List<Int> = this.indices.toList()

fun assert(condition: Boolean) = require(condition)

fun CharArray.convertToString(): String = this.joinToString("")
fun String.asCharArray(): CharArray = this.map { it }.toCharArray()

object Arrays {
    fun equals(a: Array<*>?, b: Array<*>?): Boolean {
        return (a == null && b == null) ||
                ((a != null && b != null) && a.contentEquals(b))
    }

    fun equals(a: IntArray?, b: IntArray?): Boolean {
        return (a == null && b == null) ||
                ((a != null && b != null) && a.contentEquals(b))
    }
}

object Math {
    fun min(a: Int, b: Int): Int {
        return kotlin.math.min(a, b)
    }

    fun max(a: Int, b: Int): Int {
        return kotlin.math.max(a, b)
    }

    fun floor(d: Double): Double {
        return kotlin.math.floor(d)
    }

}

fun outMessage(message: String) {
    println(message)
}

interface TypeDeclarator {
    val classesByName: List<KClass<*>>

    fun getType(name: String): KClass<*> {
        return classesByName.first { it.simpleName == name }
    }
}

@OptIn(ExperimentalContracts::class)
inline fun scoped(block: ()->Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
}