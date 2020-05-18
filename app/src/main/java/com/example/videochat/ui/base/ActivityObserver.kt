package com.example.videochat.ui.base

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle

interface ActivityObserver {

    fun onCreate(savedInstanceState: Bundle?) {}

    fun onResume() {}

    fun onPause() {}

    fun onSaveInstanceState(outState: Bundle) {}

    fun onDestroy() {}

    @TargetApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
    }
}