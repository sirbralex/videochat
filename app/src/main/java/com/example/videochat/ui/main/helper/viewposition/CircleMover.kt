package com.example.videochat.ui.main.helper.viewposition

import android.graphics.Point
import com.example.videochat.utils.math.MathUtils
import kotlin.math.*

/**
 * Moves the circle in a straight line to the target point
 * as far as possible avoiding collisions.
 */
class CircleMover(
    private val width: Int,
    private val height: Int,
    private val circles: List<Circle>
) {

    fun tryMoveToPosition(circle: Circle, x: Int, y: Int) {
        val safeX = max(circle.radius, min(width - circle.radius, x))
        val safeY = max(circle.radius, min(height - circle.radius, y))

        if (circle.x == safeX && circle.y == safeY) {
            return
        }

        var actualX = safeX
        var actualY = safeY

        // Used to avoid endless collisions due to rounding numbers
        // For example, when we have collisions at x = 3 and x = 4, but not at x = 3.5
        val resolvedCollisions = mutableSetOf(circle)

        while (true) {

            val collision = getCollision(circle, actualX, actualY, resolvedCollisions) ?: break

            // Safe area starts at least at (circle.radius + collision.radius) from collision center
            val intersection = findCircleLineIntersection(
                circle.x,
                circle.y,
                safeX,
                safeY,
                collision.x,
                collision.y,
                circle.radius + collision.radius
            ) ?: break

            actualX = intersection.x
            actualY = intersection.y

            resolvedCollisions.add(collision)
        }
        circle.x = actualX
        circle.y = actualY
    }

    private fun findCircleLineIntersection(
        startX: Int,
        startY: Int,
        targetX: Int,
        targetY: Int,
        circleX: Int,
        circleY: Int,
        circleR: Int
    ): Point? {

        val solution = if (startX == targetX) {
            MathUtils.findIntersectionWithVerticalLine(
                circleX.toDouble(),
                circleY.toDouble(),
                circleR.toDouble(),
                startX.toDouble()
            )
        } else {
            val k = 1.0 * (startY - targetY) / (startX - targetX)
            val b = startY - k * startX

            MathUtils.findIntersectionWithLine(
                circleX.toDouble(),
                circleY.toDouble(),
                circleR.toDouble(),
                k,
                b
            )
        }

        solution ?: return null

        // Choose first intersection on the way
        val resX: Double
        val resY: Double
        if (abs(solution.x1 - startX) < abs(solution.x2 - startX)
            || abs(solution.y1 - startY) < abs(solution.y2 - startY)
        ) {
            resX = solution.x1
            resY = solution.y1
        } else {
            resX = solution.x2
            resY = solution.y2
        }

        // Round to int to avoid collision
        val x = if (resX < circleX) {
            floor(resX)
        } else {
            ceil(resX)
        }

        // Round to int to avoid collision
        val y = if (resY < circleY) {
            floor(resY)
        } else {
            ceil(resY)
        }

        return Point(x.toInt(), y.toInt())
    }

    private fun getCollision(
        circle: Circle,
        x: Int,
        y: Int,
        resolvedCollisions: Set<Circle>
    ): Circle? {
        for (other in circles) {
            if (resolvedCollisions.contains(other)) {
                continue
            }
            // Two circles intersect each other if
            // distance between their centers less than sum of their radiuses
            if (MathUtils.distance(x, y, other.x, other.y) < circle.radius + other.radius) {
                return other
            }
        }
        return null
    }
}