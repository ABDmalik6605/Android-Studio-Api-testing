package com.example.api1.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferenceManager {
    private const val PREFS_NAME = "network_application"
    private lateinit var preferences: SharedPreferences
    val gson = Gson()

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Save a value in SharedPreferences
    fun <T> save(key: String, value: T) {
        val editor = preferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> editor.putString(key, gson.toJson(value)) // Serialize custom objects as JSON strings
        }
        editor.apply()
    }

    // Retrieve a value from SharedPreferences
    fun <T> get(key: String, defaultValue: T): T? {
        return when (defaultValue) {
            is String -> preferences.getString(key, defaultValue) as T?
            is Int -> preferences.getInt(key, defaultValue) as T?
            is Long -> preferences.getLong(key, defaultValue) as T?
            is Float -> preferences.getFloat(key, defaultValue) as T?
            is Boolean -> preferences.getBoolean(key, defaultValue) as T?
            else -> gson.fromJson(preferences.getString(key, ""), object : TypeToken<T>() {}.type) // Deserialize custom objects from JSON strings
        }
    }

    // Remove a specific key from SharedPreferences
    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    // Clear all SharedPreferences
    fun clear() {
        preferences.edit().clear().apply()
    }
}
