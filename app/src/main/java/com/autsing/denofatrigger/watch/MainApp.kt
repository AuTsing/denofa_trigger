package com.autsing.denofatrigger.watch

import android.app.Application
import com.autsing.denofatrigger.watch.presentation.StepUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {
    @Inject
    lateinit var stepUtil: StepUtil

    override fun onCreate() {
        super.onCreate()

        StepUtil.instance = stepUtil
    }
}
