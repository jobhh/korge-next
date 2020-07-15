package com.soywiz.kmem

import com.soywiz.kmem.annotations.*
import kotlin.math.*

/**
 * Represents and integral value of 52-bits + sign using a Double as internal representation.
 * Trying to avoid allocations on the JS target by not using [Long] when 53 bits is enough.
 */
@KmemExperimental
inline class Int53(val value: Double) : Comparable<Int53> {
    companion object {
        // Double.fromBits(0x000FFFFFFFFFFFFFL)
        //val MAX_VALUE = Int53(Double.fromBits(0x000FFFFFFFFFFFFFL)) // 2**52 - 1
        val MAX_VALUE = Int53(4503599627370495.0) // 2**52 - 1
        val MIN_VALUE = Int53(-4503599627370495.0) // -(2**52 - 1)
        private val MAX_UINT32 = 4294967295.0
        fun fromDoubleClamped(value: Double): Int53 = when {
            value > MAX_VALUE.value -> MAX_VALUE
            value < MIN_VALUE.value -> MIN_VALUE
            else -> Int53(if (value < 0.0) ceil(value) else floor(value))
        }
        // @TODO: Do not use Long to do this
        fun fromLowHigh(low: Int, high: Int): Int53 {

            return (low.toLong() or ((high and 0x1FFFFF).signExtend(21).toLong() shl 32)).toInt53()

            //if ((high and 0x80000) != 0) {
            //    // Negative
            //    val uhigh = ((high).inv() and 0xFFFFF).toDouble()
            //    val ulow = low.inv().toUInt().toDouble()
            //    val svalue = -(ulow + (uhigh * 4294967296)) - 1
            //    return fromDoubleClamped(svalue)
            //} else {
            //    return fromDoubleClamped(low.toUInt().toDouble() + ((high and 0xFFFFF) * 4294967296))
            //}
        }
    }

    // @TODO: Do not use Long to do this
    //val high: Int get() = ((double.toRawBits() ushr 32) and 20.mask().toLong()).toInt()
    val high: Int get() = ((long ushr 32) and 21.mask().toLong()).toInt()
    val low: Int get() {
        return long.toInt()
        //var v = this
        //val llow = (v % 0x10000).toInt()
        //v /= 0x10000
        //val lhigh = (v % 0x10000).toInt()
        //return llow or (lhigh shl 16)
    }

    operator fun unaryMinus(): Int53 = Int53(-value)
    operator fun unaryPlus(): Int53 = Int53(+value)

    // @TODO: handle overflows
    operator fun plus(other: Double): Int53 = fromDoubleClamped(this.value + other)
    operator fun plus(other: Int): Int53 = this + other.toDouble()
    operator fun plus(other: Int53): Int53 = this + other.toDouble()
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline operator fun plus(other: Number): Int53 = this + other.toDouble()

    operator fun minus(other: Double): Int53 = fromDoubleClamped(this.value - other)
    operator fun minus(other: Int): Int53 = this - other.toDouble()
    operator fun minus(other: Int53): Int53 = this - other.toDouble()
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline operator fun minus(other: Number): Int53 = this - other.toDouble()

    operator fun times(other: Double): Int53 = fromDoubleClamped(this.value * other)
    operator fun times(other: Int): Int53 = this * other.toDouble()
    operator fun times(other: Int53): Int53 = this * other.toDouble()
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline operator fun times(other: Number): Int53 = this * other.toDouble()

    operator fun div(other: Double): Int53 = fromDoubleClamped(this.value / other)
    operator fun div(other: Int): Int53 = this / other.toDouble()
    operator fun div(other: Int53): Int53 = this / other.toDouble()
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline operator fun div(other: Number): Int53 = this / other.toDouble()

    operator fun rem(other: Double): Int53 = fromDoubleClamped(this.value % other.toDouble())
    operator fun rem(other: Int): Int53 = this % other.toDouble()
    operator fun rem(other: Int53): Int53 = this % other.toDouble()
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline operator fun rem(other: Number): Int53 = this % other.toDouble()

    infix fun and(other: Int53): Int53 = Int53.fromLowHigh(this.low and other.low, this.high and other.high)
    infix fun or(other: Int53): Int53 = Int53.fromLowHigh(this.low or other.low, this.high or other.high)
    infix fun xor(other: Int53): Int53 = Int53.fromLowHigh(this.low xor other.low, this.high xor other.high)
    fun inv(): Int53 = Int53.fromLowHigh(this.low.inv(), this.high.inv())

    val int get() = value.toInt()
    val long get() = value.toLong()
    val double get() = value

    fun toInt() = int
    fun toLong() = long
    fun toDouble() = double

    fun compareTo(other: Double): Int = this.value.compareTo(other)
    fun compareTo(other: Int): Int = this.compareTo(other.toDouble())
    override fun compareTo(other: Int53): Int = this.compareTo(other.toDouble())
    @Deprecated("Kotlin/Native boxes inline + Number")
    inline fun compareTo(other: Number): Int = this.compareTo(other.toDouble())

    // @TODO: We are casting to Long that might allocate on JS
    override fun toString(): String = value.toLong().toString().removeSuffix(".0")
}

@KmemExperimental
inline fun String.toInt53(): Int53 = this.toDouble().toInt53()
@KmemExperimental
inline fun String.toInt53OrNull(): Int53? = this.toDoubleOrNull()?.toInt53()
@KmemExperimental
inline fun Int.toInt53(): Int53 = Int53.fromDoubleClamped(this.toDouble())
@KmemExperimental
inline fun Double.toInt53(): Int53 = Int53.fromDoubleClamped(this)
@KmemExperimental
inline fun Long.toInt53(): Int53 = Int53.fromDoubleClamped(this.toDouble())
@KmemExperimental
inline fun Number.toInt53(): Int53 = Int53.fromDoubleClamped(this.toDouble())
