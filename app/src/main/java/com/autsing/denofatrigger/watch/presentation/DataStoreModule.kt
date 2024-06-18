package com.autsing.denofatrigger.watch.presentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StepDataStore

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
    object PrefKeys {
        private const val PREF_KEY_STEPS = "pref_key_steps"
        private const val PREF_KEY_STEP_INDEX = "pref_key_step_index"

        val prefKeySteps: Preferences.Key<String> = stringPreferencesKey(PREF_KEY_STEPS)
        val prefKeyStepIndex: Preferences.Key<Int> = intPreferencesKey(PREF_KEY_STEP_INDEX)
    }

    @StepDataStore
    @Singleton
    @Provides
    fun provideStepDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(SharedPreferencesMigration(context, "pref_step")),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("pref_step") }
    )
}
