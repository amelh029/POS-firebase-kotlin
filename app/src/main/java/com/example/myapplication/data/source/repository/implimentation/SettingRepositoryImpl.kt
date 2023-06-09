package com.example.myapplication.data.source.repository.implimentation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.source.repository.SettingRepository
import kotlinx.coroutines.flow.map


private const val SETTING_DATASTORE = "setting_datastore"
private val Context.settingDataStore by preferencesDataStore(
    SETTING_DATASTORE
)


class SettingRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingRepository {
    companion object {
        fun getDataStoreInstance(context: Context): SettingRepositoryImpl {
            val dataStore = context.settingDataStore
            return SettingRepositoryImpl(dataStore)
        }
    }

    private object PreferencesKeys {
        val SELECTED_STORE = longPreferencesKey("selected_store")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    override fun getSelectedStore() = dataStore.data.map {
        it[PreferencesKeys.SELECTED_STORE] ?: 0L
    }

    override fun getIsDarkModeActive() = dataStore.data.map {
        it[PreferencesKeys.IS_DARK_MODE] ?: false
    }

    override suspend fun selectStore(storeId: Long) {
        dataStore.edit {
            it[PreferencesKeys.SELECTED_STORE] = storeId
        }
    }

    override suspend fun setDarkMode(isActive: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_DARK_MODE] = isActive
        }
    }
}