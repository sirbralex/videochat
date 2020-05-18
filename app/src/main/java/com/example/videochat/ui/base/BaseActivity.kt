package com.example.videochat.ui.base

import android.os.Bundle
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity {

    private val observers = mutableListOf<ActivityObserver>()

    @ContentView
    constructor() : this(0)

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers.forEach { it.onCreate(savedInstanceState) }
    }

    override fun onResume() {
        super.onResume()
        observers.forEach { it.onResume() }
    }

    override fun onPause() {
        observers.forEach { it.onPause() }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        observers.forEach { it.onSaveInstanceState(outState) }
    }

    override fun onDestroy() {
        observers.forEach { it.onDestroy() }
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        observers.forEach { it.onRequestPermissionsResult(requestCode, permissions, grantResults) }
    }

    fun addActivityObserver(observer: ActivityObserver) {
        observers.add(observer)
    }

    fun removeActivityObserver(observer: ActivityObserver) {
        observers.remove(observer)
    }
}