package com.example.videochat.ui.main.mediaitem

import android.content.Context
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
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource

class ExoPlayerItem(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    override val circle: Circle
) : MediaItem {

    override val view: View
        get() = frameLayout
    private lateinit var textureView: TextureView
    private lateinit var frameLayout: FrameLayout

    private lateinit var player: SimpleExoPlayer

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
        player = SimpleExoPlayer.Builder(context).build()
        player.setVideoTextureView(textureView)

        val rawResourceDataSource = RawResourceDataSource(context)
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.video_sample_5))
        rawResourceDataSource.open(dataSpec)

        val factory =
            DataSource.Factory { rawResourceDataSource }
        val mediaSource = ProgressiveMediaSource.Factory(factory)
            .createMediaSource(rawResourceDataSource.uri)

        player.prepare(mediaSource)
        player.repeatMode = Player.REPEAT_MODE_ALL

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    private fun destroy() {
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        if (::player.isInitialized) {
            player.stop()
            player.release()
        }
    }

    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            player.playWhenReady = true
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            player.playWhenReady = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            destroy()
        }
    }
}