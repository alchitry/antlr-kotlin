/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.kotlinruntime

import org.antlr.v4.kotlinruntime.IntStream.Companion.UNKNOWN_SOURCE_NAME
import org.antlr.v4.kotlinruntime.misc.Interval
import java.nio.charset.StandardCharsets

/**
 * Alternative to [ANTLRInputStream] which treats the input
 * as a series of Unicode code points, instead of a series of UTF-16
 * code units.
 *
 * Use this if you need to parse input which potentially contains
 * Unicode values > U+FFFF.
 */
abstract class CodePointCharStream private constructor(position: Int, remaining: Int, name: String) : CharStream {
    protected val size: Int
    protected val name: String?

    // To avoid lots of virtual method calls, we directly access
    // the state of the underlying code points in the
    // CodePointBuffer.
    protected var position: Int

    // Use the factory method {@link #fromBuffer(CodePointBuffer)} to
    // construct instances of this type.
    init {
        // TODO
        assert(position == 0)
        size = remaining
        this.name = name
        this.position = 0
    }

    // Visible for testing.
    abstract val internalStorage: Any

    override fun consume() {
        if (size - position == 0) {
            assert(LA(1) == IntStream.EOF)
            throw IllegalStateException("cannot consume EOF")
        }
        position = position + 1
    }

    override fun index(): Int {
        return position
    }

    override fun size(): Int {
        return size
    }

    /** mark/release do nothing; we have entire buffer  */
    override fun mark(): Int {
        return -1
    }

    override fun release(marker: Int) {}
    override fun seek(index: Int) {
        position = index
    }

    override val sourceName: String
        get() = if (name == null || name.isEmpty()) {
            UNKNOWN_SOURCE_NAME
        } else name

    override fun toString(): String {
        return getText(Interval.of(0, size - 1))
    }

    // 8-bit storage for code points <= U+00FF.
    private class CodePoint8BitCharStream(
        position: Int,
        remaining: Int,
        name: String,
        byteArray: ByteArray,
        arrayOffset: Int
    ) : CodePointCharStream(position, remaining, name) {
        private val byteArray: ByteArray

        init {
            assert(arrayOffset == 0)
            this.byteArray = byteArray
        }

        /** Return the UTF-16 encoded string for the given interval  */
        override fun getText(interval: Interval): String {
            val startIdx = Math.min(interval.a, size)
            val len = Math.min(interval.b - interval.a + 1, size - startIdx)

            // We know the maximum code point in byteArray is U+00FF,
            // so we can treat this as if it were ISO-8859-1, aka Latin-1,
            // which shares the same code points up to 0xFF.
            return String(byteArray, startIdx, len, StandardCharsets.ISO_8859_1)
        }

        override fun LA(i: Int): Int {
            val offset: Int
            when (Integer.signum(i)) {
                -1 -> {
                    offset = position + i
                    return if (offset < 0) {
                        IntStream.EOF
                    } else byteArray[offset].toInt() and 0xFF
                }

                0 ->                    // Undefined
                    return 0

                1 -> {
                    offset = position + i - 1
                    return if (offset >= size) {
                        IntStream.EOF
                    } else byteArray[offset].toInt() and 0xFF
                }
            }
            throw UnsupportedOperationException("Not reached")
        }

        override val internalStorage get() = byteArray
    }

    // 16-bit internal storage for code points between U+0100 and U+FFFF.
    private class CodePoint16BitCharStream(
        position: Int,
        remaining: Int,
        name: String,
        private val charArray: CharArray,
        arrayOffset: Int
    ) : CodePointCharStream(position, remaining, name) {
        init {
            assert(arrayOffset == 0)
        }

        /** Return the UTF-16 encoded string for the given interval  */
        override fun getText(interval: Interval): String {
            val startIdx = Math.min(interval.a, size)
            val len = Math.min(interval.b - interval.a + 1, size - startIdx)

            // We know there are no surrogates in this
            // array, since otherwise we would be given a
            // 32-bit int[] array.
            //
            // So, it's safe to treat this as if it were
            // UTF-16.
            return String(charArray, startIdx, len)
        }

        override fun LA(i: Int): Int {
            val offset: Int
            when (Integer.signum(i)) {
                -1 -> {
                    offset = position + i
                    return if (offset < 0) {
                        IntStream.EOF
                    } else charArray[offset].code and 0xFFFF
                }

                0 ->                    // Undefined
                    return 0

                1 -> {
                    offset = position + i - 1
                    return if (offset >= size) {
                        IntStream.EOF
                    } else charArray[offset].code and 0xFFFF
                }
            }
            throw UnsupportedOperationException("Not reached")
        }

        override val internalStorage get() = charArray

    }

    // 32-bit internal storage for code points between U+10000 and U+10FFFF.
    private class CodePoint32BitCharStream(
        position: Int,
        remaining: Int,
        name: String,
        private val intArray: IntArray,
        arrayOffset: Int
    ) : CodePointCharStream(position, remaining, name) {
        init {
            assert(arrayOffset == 0)
        }

        /** Return the UTF-16 encoded string for the given interval  */
        override fun getText(interval: Interval): String {
            val startIdx = Math.min(interval.a, size)
            val len = Math.min(interval.b - interval.a + 1, size - startIdx)

            // Note that we pass the int[] code points to the String constructor --
            // this is supported, and the constructor will convert to UTF-16 internally.
            return String(intArray, startIdx, len)
        }

        override fun LA(i: Int): Int {
            val offset: Int
            when (Integer.signum(i)) {
                -1 -> {
                    offset = position + i
                    return if (offset < 0) {
                        IntStream.EOF
                    } else intArray[offset]
                }

                0 ->                    // Undefined
                    return 0

                1 -> {
                    offset = position + i - 1
                    return if (offset >= size) {
                        IntStream.EOF
                    } else intArray[offset]
                }
            }
            throw UnsupportedOperationException("Not reached")
        }

        override val internalStorage get() = intArray
    }

    companion object {
        /**
         * Constructs a named [CodePointCharStream] which provides access
         * to the Unicode code points stored in `codePointBuffer`.
         */
        /**
         * Constructs a [CodePointCharStream] which provides access
         * to the Unicode code points stored in `codePointBuffer`.
         */
        @JvmOverloads
        fun fromBuffer(codePointBuffer: CodePointBuffer, name: String = UNKNOWN_SOURCE_NAME): CodePointCharStream {
            // Java lacks generics on primitive types.
            //
            // To avoid lots of calls to virtual methods in the
            // very hot codepath of LA() below, we construct one
            // of three concrete subclasses.
            //
            // The concrete subclasses directly access the code
            // points stored in the underlying array (byte[],
            // char[], or int[]), so we can avoid lots of virtual
            // method calls to ByteBuffer.get(offset).
            return when (codePointBuffer.type) {
                CodePointBuffer.Type.BYTE -> CodePoint8BitCharStream(
                    codePointBuffer.position(),
                    codePointBuffer.remaining(),
                    name,
                    codePointBuffer.byteArray(),
                    codePointBuffer.arrayOffset()
                )

                CodePointBuffer.Type.CHAR -> CodePoint16BitCharStream(
                    codePointBuffer.position(),
                    codePointBuffer.remaining(),
                    name,
                    codePointBuffer.charArray(),
                    codePointBuffer.arrayOffset()
                )

                CodePointBuffer.Type.INT -> CodePoint32BitCharStream(
                    codePointBuffer.position(),
                    codePointBuffer.remaining(),
                    name,
                    codePointBuffer.intArray(),
                    codePointBuffer.arrayOffset()
                )
            }
        }
    }
}
