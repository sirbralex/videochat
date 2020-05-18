package com.mapswithme.maps.core.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.AnyRes

object AndroidUtils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun pxToDp(px: Float, context: Context): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * Creates [Uri] from android resource
     * @param resId Resource id
     * @param context Context to get packageName
     */
    fun uriFromResource(@AnyRes resId: Int, context: Context) = Uri
            .parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + resId)
}
