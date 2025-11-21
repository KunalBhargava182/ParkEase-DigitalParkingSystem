package com.parkease.app.util

import java.util.Calendar
import kotlin.math.roundToInt

object PricingUtil {

    // Define peak hour ranges
    private val peakRanges = listOf(
        Pair(8, 11),   // 08:00 – 10:59
        Pair(17, 20)   // 17:00 – 19:59
    )

    private const val PEAK_MULTIPLIER = 2.0   // Always double the price at peak

    /**
     * Returns Pair(price, isPeak) where:
     *   - price is ALWAYS a clean multiple of 10
     *   - no paise/decimals
     */
    fun computePrice(basePrice: Double): Pair<Int, Boolean> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val isPeak = peakRanges.any { hour >= it.first && hour < it.second }

        val rawPrice = if (isPeak) basePrice * PEAK_MULTIPLIER else basePrice

        // FORCE clean pricing → always nearest 10
        val rounded = roundToNearestTen(rawPrice)

        return Pair(rounded, isPeak)
    }

    /**
     * Round a number to nearest 10 rupees.
     */
    private fun roundToNearestTen(value: Double): Int {
        return ((value / 10.0).roundToInt() * 10)
    }

    fun isPeakNow(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return peakRanges.any { hour >= it.first && hour < it.second }
    }
}
