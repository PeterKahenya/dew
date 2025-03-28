package dew.app.mobile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DewApplication : Application()

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dew_datastore")