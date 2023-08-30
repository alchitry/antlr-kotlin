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

class BitSet {
    private val _wrapped = java.util.BitSet()

    constructor()

    fun set(bitIndex: Int) {
        _wrapped.set(bitIndex)
    }

    fun clear(bitIndex: Int) {
        _wrapped.clear(bitIndex)
    }

    fun get(bitIndex: Int): Boolean {
        return _wrapped.get(bitIndex)
    }

    fun cardinality(): Int {
        return _wrapped.cardinality()
    }

    fun nextSetBit(i: Int): Int {
        return _wrapped.nextSetBit(i)
    }

    fun or(alts: BitSet) {
        return _wrapped.or(alts._wrapped)
    }

}