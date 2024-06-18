package com.autsing.denofatrigger.watch

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.autsing.denofatrigger.watch.presentation.StepRepository

class MainApp : Application() {
    private val stepDataStore: DataStore<Preferences> by preferencesDataStore(name = "pref_step")

    override fun onCreate() {
        super.onCreate()

        StepRepository.instance = StepRepository(this, stepDataStore)
    }
}
