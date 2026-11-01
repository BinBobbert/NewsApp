package com.dejevij.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Triggers Hilt's code generation and creates the app-wide SingletonComponent
// when the process starts. Every other Hilt component is a child of it.
@HiltAndroidApp
class NewsApplication : Application()
