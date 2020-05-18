package com.example.videochat.ui.main.delegate

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.videochat.R
import com.example.videochat.ui.base.delegate.BaseRtPermissionsDelegate

class RtPermissionsDelegate(
    private val activity: AppCompatActivity
) : BaseRtPermissionsDelegate(activity) {

    private var pausedForRequest: Boolean = false

    override fun getPermissionNames() = arrayOf(Manifest.permission.CAMERA)

    override fun getRationaleLight(permissionNames: Array<String>) =
        activity.getString(R.string.main_permission_rationale_light)

    override fun getRationaleHard(permissionNames: Array<String>) =
        activity.getString(R.string.main_permission_rationale_hard)

    override fun requestPermissions(permissions: Array<String>, requestCode: Int) =
        ActivityCompat.requestPermissions(activity, permissions, requestCode)

    fun onResume() {
        if (pausedForRequest) {
            pausedForRequest = false
        } else {
            checkPermissions()
        }
    }

    fun onPause() {
        pausedForRequest = requestInProgress
    }
}
