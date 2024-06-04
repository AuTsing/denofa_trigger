package com.autsing.denofatrigger.watch

import android.app.Application
import com.autsing.denofatrigger.watch.presentation.StepRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {
    @Inject
    lateinit var stepRepo: StepRepository

    override fun onCreate() {
        super.onCreate()

        StepRepository.instance = stepRepo
    }
}
