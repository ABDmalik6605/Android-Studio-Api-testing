package com.example.api1

import android.app.Application
import com.example.api1.preferences.PreferenceManager

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(this)
    }
}

