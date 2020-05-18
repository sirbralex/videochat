package com.example.videochat.ui.widget

import android.graphics.Point
import android.view.MotionEvent
import android.view.View

fun View.setOnClickListenerWithPoint(action: (Point) -> Unit) {
    val point = Point()
    setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            point.set(
                event.x.toInt(),
                event.y.toInt()
            )
        }
        false
    }
    setOnClickListener {
        action.invoke(point)
    }
}