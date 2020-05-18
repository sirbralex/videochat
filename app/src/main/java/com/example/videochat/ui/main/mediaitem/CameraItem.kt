package com.example.videochat.ui.main.mediaitem

import android.content.Context
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.videochat.R
import com.example.videochat.ui.base.ActivityObserver
import com.example.videochat.ui.base.BaseActivity
import com.example.videochat.ui.main.delegate.RtPermissionsDelegate
import com.example.videochat.ui.main.helper.viewposition.Circle
import com.google.common.util.concurrent.ListenableFuture

class CameraItem(
    private val context: Context,
    private val activity: BaseActivity,
    override val circle: Circle
) : MediaItem {

    override val view: View
        get() = previewView
    private lateinit var previewView: PreviewView

    private val rtPermissionsDelegate = RtPermissionsDelegate(activity)

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreview: Preview

    init {
        createView()
        rtPermissionsDelegate.listener = ::onPermissionGranted
    }

    private fun createView() {
        previewView = PreviewView(context).apply {
            setBackgroundResource(R.drawable.bg_circle)
            clipToOutline = true
        }
    }

    override fun onAdded() {
        activity.addActivityObserver(activityObserver)
        rtPermissionsDelegate.checkPermissions()
    }

    override fun onDeleted() {
        activity.removeActivityObserver(activityObserver)
    }

    private fun onPermissionGranted(granted: Boolean) {
        if (granted) {
            if (!::cameraProviderFuture.isInitialized) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        imagePreview = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_4_3)
            setTargetRotation(previewView.display.rotation)
        }.build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val camera =
                cameraProvider.bindToLifecycle(activity, cameraSelector, imagePreview)
            previewView.preferredImplementationMode = PreviewView.ImplementationMode.TEXTURE_VIEW
            imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
        }, ContextCompat.getMainExecutor(context))
    }

    private val activityObserver = object :
        ActivityObserver {
        override fun onResume() {
            rtPermissionsDelegate.onResume()
        }

        override fun onPause() {
            rtPermissionsDelegate.onPause()
        }

        override fun onDestroy() {
            rtPermissionsDelegate.onDestroy()
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            rtPermissionsDelegate.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }
}