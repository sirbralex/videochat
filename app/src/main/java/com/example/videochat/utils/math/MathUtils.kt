package com.example.videochat.utils.math

import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {

    fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble())
    }

    fun findIntersectionWithVerticalLine(
        cx: Double,
        cy: Double,
        r: Double,
        x: Double
    ): TwoPoints? {
        // (x - cx) ^ 2 + (y - cy) ^ 2 = r ^ 2
        //
        //
        // ((x ^ 2) - (2 * x * cx) + (cx ^ 2))
        // +
        // ((y ^ 2) - (2 * y * cy) + (cy ^ 2))
        // =
        // r ^ 2
        //
        // (y ^ 2) - ((2 * cy) * y) + (x ^ 2 - 2 * x * cx + cx ^ 2 + cy ^ 2 - r ^ 2) = 0

        val qa = 1
        val qb = (-2 * cy)
        val qc = (x.pow(2) - 2 * x * cx + cx.pow(2) + cy.pow(2) - r.pow(2))

        val d = qb.pow(2) - 4 * qa * qc

        if (d < 0) {
            return null
        }

        val y1 = (-qb + sqrt(d)) / (2 * qa)
        val y2 = (-qb - sqrt(d)) / (2 * qa)

        return TwoPoints(x, y1, x, y2)
    }

    fun findIntersectionWithLine(
        cx: Double,
        cy: Double,
        r: Double,
        k: Double,
        b: Double
    ): TwoPoints? {
        // (x - cx) ^ 2 + (y - cy) ^ 2 = r ^ 2
        // y = k * x + b
        //
        //
        // ((x ^ 2) - (2 * x * cx) + (cx ^ 2))
        // +
        // ((y ^ 2) - (2 * y * cy) + (cy ^ 2))
        // =
        // r ^ 2
        //
        // ((x ^ 2) - (2 * x * cx) + (cx ^ 2))
        // +
        // ((k * x + b) ^ 2) - (2 * (k * x + b) * cy) + (cy ^ 2)
        // =
        // r ^ 2
        //
        // ((x ^ 2) - (2 * x * cx) + (cx ^ 2))
        // +
        // ((k ^ 2 * x ^ 2) + (2 * k * x * b) + (b ^ 2)) - (2 * k * cy * x + 2 * b * cy) + (cy ^ 2)
        // =
        // r ^ 2
        //
        // ((1 + k ^ 2) * x ^ 2) + ((-2 * cx + 2 * k * b - 2 * k * cy) * x) + (cx ^ 2 + b ^ 2 - 2 * b * cy + cy ^ 2 - r ^ 2) = 0

        val qa = (1 + k.pow(2))
        val qb = (-2 * cx + 2 * k * b - 2 * k * cy)
        val qc = (cx.pow(2) + b.pow(2) - 2 * b * cy + cy.pow(2) - r.pow(2))

        val d = qb.pow(2) - 4 * qa * qc

        if (d < 0) {
            return null
        }

        val x1 = (-qb + sqrt(d)) / (2 * qa)
        val x2 = (-qb - sqrt(d)) / (2 * qa)

        val y1 = k * x1 + b
        val y2 = k * x2 + b

        return TwoPoints(x1, y1, x2, y2)
    }
}

class TwoPoints(val x1: Double, val y1: Double, val x2: Double, val y2: Double)