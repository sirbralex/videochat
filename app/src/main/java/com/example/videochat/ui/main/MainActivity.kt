package com.example.videochat.ui.main

import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.example.videochat.R
import com.example.videochat.ui.base.BaseActivity
import com.example.videochat.ui.main.helper.viewposition.Circle
import com.example.videochat.ui.main.helper.viewposition.ViewPositionProvider
import com.example.videochat.ui.main.mediaitem.CameraItem
import com.example.videochat.ui.main.mediaitem.ExoPlayerItem
import com.example.videochat.ui.main.mediaitem.MediaItem
import com.example.videochat.ui.widget.setOnClickListenerWithPoint
import com.mapswithme.maps.core.utils.AndroidUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(R.layout.activity_main) {

    private val items = mutableMapOf<Circle, MediaItem>()
    private val viewPositionProvider = ViewPositionProvider()
    private var radius = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        radius = AndroidUtils.dpToPx(30f, this).toInt()

        globalContainer.doOnLayout {
            viewPositionProvider.init(it.width, it.height, radius)
            globalContainer.post { addItems() }
        }
    }

    private fun addItems() {
        addCameraView()

        for (i in 1..9) {
            addVideoView()
        }
    }

    private fun addCameraView() {
        val circle = viewPositionProvider.acquirePosition(radius) ?: return
        val mediaItem = CameraItem(
            this,
            this,
            circle
        )
        addMediaItem(mediaItem)

        globalContainer.setOnClickListenerWithPoint { point ->
            viewPositionProvider.tryMoveToPosition(mediaItem.circle, point.x, point.y)
            val targetX = mediaItem.circle.x - mediaItem.view.width / 2f
            val targetY = mediaItem.circle.y - mediaItem.view.height / 2f
            mediaItem.view.animate().translationX(targetX).translationY(targetY).start()
        }
    }

    private fun addVideoView() {
        val circle = viewPositionProvider.acquirePosition(radius) ?: return
        val mediaItem = ExoPlayerItem(this, this, circle)
        addMediaItem(mediaItem)
    }

    private fun addMediaItem(mediaItem: MediaItem) {
        val circle = mediaItem.circle
        val videoView = mediaItem.view.apply {
            layoutParams = FrameLayout.LayoutParams(radius * 2, radius * 2)
            translationX = (circle.x - circle.radius).toFloat()
            translationY = (circle.y - circle.radius).toFloat()
        }
        globalContainer.addView(videoView, globalContainer.childCount - 1)
        items[circle] = mediaItem
        mediaItem.onAdded()
    }
}
