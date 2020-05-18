package com.example.videochat.ui.base.delegate

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.videochat.R
import java.util.*

/**
 * Base class to check Runtime Permissions.
 */
abstract class BaseRtPermissionsDelegate(private val activity: Activity) {

    var listener: ((granted: Boolean) -> Unit)? = null
    private var lightRationaleAlertDialog: AlertDialog? = null
    private var hardRationaleAlertDialog: AlertDialog? = null

    protected var requestInProgress: Boolean = false

    fun onCreate(savedInstanceState: Bundle?) {
        val bundle = savedInstanceState?.getBundle(SS_PERMISSION_DELEGATE_BUNDLE) ?: return
        requestInProgress = bundle.getBoolean(SS_REQUEST_IN_PROGRESS)
    }

    fun onSaveInstanceState(outState: Bundle) {
        val bundle = Bundle()
        bundle.putBoolean(SS_REQUEST_IN_PROGRESS, requestInProgress)
        bundle.putBoolean(SS_LIGHT_RATIONALE_SHOWN, lightRationaleAlertDialog != null)
        bundle.putBoolean(SS_HARD_RATIONALE_SHOWN, hardRationaleAlertDialog != null)
        outState.putBundle(SS_PERMISSION_DELEGATE_BUNDLE, bundle)
    }

    fun onDestroy() {
        dismissRationaleHardDialog()
        dismissRationaleLightDialog()
    }

    fun checkPermissions() {
        check(!requestInProgress) { "Permission request is already in progress" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = getPermissionNames()
            val grantedPermissions = ArrayList<String>()
            val notGrantedRationalePermissions = ArrayList<String>()
            val notGrantedPermissions = ArrayList<String>()
            for (permission in permissions) {
                when {
                    ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                        grantedPermissions.add(permission)
                    }
                    activity.shouldShowRequestPermissionRationale(permission) -> {
                        notGrantedRationalePermissions.add(permission)
                    }
                    else -> {
                        notGrantedPermissions.add(permission)
                    }
                }
            }
            if (notGrantedPermissions.isNotEmpty()) {
                requestPermissionsInternal(getPermissionNames(),
                    RC_PERMISSION
                )
            } else if (notGrantedRationalePermissions.isNotEmpty()) {
                showRationaleLightDialog(notGrantedRationalePermissions.toTypedArray())
            } else {
                listener?.invoke(true)
            }
        } else {
            listener?.invoke(true)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissionsInternal(permissions: Array<String>, requestCode: Int) {
        requestInProgress = true
        requestPermissions(permissions, requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION || requestCode == RC_PERMISSION_AFTER_RATIONALE) {
            requestInProgress = false
            val grantedPermissions = ArrayList<String>()
            val notGrantedHardPermissions = ArrayList<String>()
            val notGrantedPermissions = ArrayList<String>()
            if (grantResults.size == getPermissionNames().size) {
                for (i in permissions.indices) {
                    val grantResult = grantResults[i]
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permissions[i])
                        continue
                    }
                    if (!activity.shouldShowRequestPermissionRationale(permissions[i]) && requestCode == RC_PERMISSION) {
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            notGrantedHardPermissions.add(permissions[i])
                            continue
                        }
                    }
                    notGrantedPermissions.add(permissions[i])
                }
                if (notGrantedPermissions.isNotEmpty()) {
                    listener?.invoke(false)
                } else if (notGrantedHardPermissions.isNotEmpty()) {
                    showRationaleHardDialog(notGrantedHardPermissions.toTypedArray())
                } else {
                    listener?.invoke(true)
                }
                return
            }
            listener?.invoke(false)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showRationaleLightDialog(permissionNames: Array<String>) {
        dismissRationaleHardDialog()
        if (lightRationaleAlertDialog != null) {
            return
        }
        lightRationaleAlertDialog = AlertDialog.Builder(activity)
                .setMessage(getRationaleLight(permissionNames))
                .setPositiveButton(R.string.common_rt_permissions_rationale_light_continue) { _, _ -> requestPermissionsInternal(permissionNames,
                    RC_PERMISSION_AFTER_RATIONALE
                ) }
                .setNegativeButton(R.string.common_rt_permissions_rationale_light_cancel) { _, _ -> listener?.invoke(false) }
                .setOnDismissListener { dismissRationaleLightDialog() }
                .setCancelable(false)
                .show()
    }

    private fun dismissRationaleLightDialog() {
        lightRationaleAlertDialog?.let {
            it.dismiss()
            lightRationaleAlertDialog = null
        }
    }

    private fun showRationaleHardDialog(permissionNames: Array<String>) {
        dismissRationaleLightDialog()
        if (hardRationaleAlertDialog != null) {
            return
        }
        hardRationaleAlertDialog = AlertDialog.Builder(activity)
                .setMessage(getRationaleHard(permissionNames))
                .setPositiveButton(R.string.common_rt_permissions_rationale_hard_continue) { _, _ -> navigateToAppSettings(activity) }
                .setNegativeButton(R.string.common_rt_permissions_rationale_hard_cancel) { _, _ -> listener?.invoke(false) }
                .setOnDismissListener { dismissRationaleHardDialog() }
                .setCancelable(false)
                .show()
    }

    private fun dismissRationaleHardDialog() {
        hardRationaleAlertDialog?.let {
            it.dismiss()
            hardRationaleAlertDialog = null
        }
    }

    private fun navigateToAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + activity.packageName)
        activity.startActivity(intent)
    }

    /**
     * Returns permission names, which app uses
     */
    protected abstract fun getPermissionNames(): Array<String>

    /**
     * Returns explanation about need for permission when
     * user has already denied without "do not ask again" flag
     */
    protected abstract fun getRationaleLight(permissionNames: Array<String>): String

    /**
     * Returns explanation about need for permission when
     * user has already denied with "do not ask again" flag
     */
    protected abstract fun getRationaleHard(permissionNames: Array<String>): String

    /**
     * Implements call of system api for permission request
     */
    protected abstract fun requestPermissions(permissions: Array<String>, requestCode: Int)

    companion object {
        //region Request codes
        private const val RC_PERMISSION_AFTER_RATIONALE = 102
        private const val RC_PERMISSION = 103
        //endregion
        //region SavedState
        private const val SS_PERMISSION_DELEGATE_BUNDLE = "SS_PERMISSION_DELEGATE_BUNDLE"
        private const val SS_REQUEST_IN_PROGRESS = "SS_REQUEST_IN_PROGRESS"
        private const val SS_LIGHT_RATIONALE_SHOWN = "SS_LIGHT_RATIONALE_SHOWN"
        private const val SS_HARD_RATIONALE_SHOWN = "SS_HARD_RATIONALE_SHOWN"
        //endregion
    }
}
