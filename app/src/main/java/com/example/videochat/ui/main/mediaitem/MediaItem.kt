package com.example.videochat.ui.main.mediaitem

import android.view.View
import com.example.videochat.ui.main.helper.viewposition.Circle
import com.example.videochat.ui.main.helper.viewposition.ViewPositionProvider

interface MediaItem {

    val circle: Circle
    val view: View

    fun onAdded()

    fun onDeleted()
}