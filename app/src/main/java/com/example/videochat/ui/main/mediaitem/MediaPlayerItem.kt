package com.example.videochat.ui.main.mediaitem

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Build
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.videochat.R
import com.example.videochat.ui.main.helper.viewposition.Circle

class MediaPlayerItem(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    override val circle: Circle
) : MediaItem {

    override val view: View
        get() = frameLayout
    private lateinit var textureView: TextureView
    private lateinit var frameLayout: FrameLayout

    private lateinit var player: MediaPlayer

    init {
        createView()
    }

    private fun createView() {
        textureView = TextureView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        frameLayout = FrameLayout(context).apply {
            setBackgroundResource(R.drawable.bg_circle)
            clipToOutline = true
            addView(textureView)
        }
    }

    override fun onAdded() {
        startVideo()
    }

    override fun onDeleted() {
        destroy()
    }

    private fun startVideo() {
        textureView.surfaceTextureListener = surfaceTextureListener
    }

    private fun destroy() {
        textureView.surfaceTextureListener = null
        if (::player.isInitialized) {
            player.release()
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            // nop
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            // nop
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            player.setSurface(null)
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            if (!::player.isInitialized) {
                player = MediaPlayer.create(context, R.raw.video_sample_5)
                player.isLooping = true
                lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
            }
            player.setSurface(Surface(surface))
        }
    }

    private val lifecycleObserver = object : LifecycleObserver {

        private var pausedAtPosition = 0

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            player.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                player.seekTo(pausedAtPosition.toLong(), MediaPlayer.SEEK_CLOSEST)
            } else {
                player.seekTo(pausedAtPosition)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            pausedAtPosition = player.currentPosition
            player.pause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            destroy()
        }
    }
}