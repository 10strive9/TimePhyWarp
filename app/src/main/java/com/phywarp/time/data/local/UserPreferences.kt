package com.phywarp.time.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    companion object {
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val TASK_TITLE = stringPreferencesKey("task_title")
        val START_TIME = longPreferencesKey("start_time")
        val TARGET_SECONDS = longPreferencesKey("target_seconds")
        val IS_RUNNING = booleanPreferencesKey("is_running")
        val IS_COUNT_UP = booleanPreferencesKey("is_count_up")
    }

    val themeColor: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_COLOR] ?: "BLUE"
    }

    val timerState: Flow<TimerState> = context.dataStore.data.map { preferences ->
        TimerState(
            title = preferences[TASK_TITLE] ?: "",
            startTime = preferences[START_TIME] ?: 0L,
            targetSeconds = preferences[TARGET_SECONDS] ?: 0L,
            isRunning = preferences[IS_RUNNING] ?: false,
            isCountUp = preferences[IS_COUNT_UP] ?: true
        )
    }

    suspend fun setThemeColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR] = color
        }
    }

    suspend fun saveTimerState(state: TimerState) {
        context.dataStore.edit { preferences ->
            preferences[TASK_TITLE] = state.title
            preferences[START_TIME] = state.startTime
            preferences[TARGET_SECONDS] = state.targetSeconds
            preferences[IS_RUNNING] = state.isRunning
            preferences[IS_COUNT_UP] = state.isCountUp
        }
    }
}

data class TimerState(
    val title: String,
    val startTime: Long,
    val targetSeconds: Long,
    val isRunning: Boolean,
    val isCountUp: Boolean
)
