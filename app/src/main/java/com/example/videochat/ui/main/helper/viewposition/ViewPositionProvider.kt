package com.example.videochat.ui.main.helper.viewposition

import com.example.videochat.utils.math.MathUtils
import kotlin.random.Random

/**
 * Manages free space of rectangle.
 */
class ViewPositionProvider {

    private var circles = mutableListOf<Circle>()

    private var width = 0
    private var height = 0
    private var radius = 0

    fun init(width: Int, height: Int, radius: Int) {
        this.width = width
        this.height = height
        this.radius = radius
    }

    fun acquirePosition(radius: Int): Circle? {
        return tryAddCircle(radius)
    }

    fun releasePosition(circle: Circle) {
        circles.remove(circle)
    }

    private fun tryAddCircle(radius: Int): Circle? {
        for (i in 1..MAX_ATTEMPTS) {
            val x = Random.nextInt(width - radius * 2) + radius
            val y = Random.nextInt(height - radius * 2) + radius
            if (!overlapsExisting(x, y, radius)) {
                val circle =
                    Circle(
                        x,
                        y,
                        radius
                    )
                circles.add(circle)
                return circle
            }
        }
        return null
    }

    private fun overlapsExisting(x: Int, y: Int, r: Int): Boolean {
        for (circle in circles) {
            if (MathUtils.distance(x, y, circle.x, circle.y) < circle.radius + r) {
                return true
            }
        }
        return false
    }

    fun tryMoveToPosition(circle: Circle, x: Int, y: Int) {
        CircleMover(width, height, circles).tryMoveToPosition(circle, x, y)
    }

    private companion object {
        private const val MAX_ATTEMPTS = 100
    }
}