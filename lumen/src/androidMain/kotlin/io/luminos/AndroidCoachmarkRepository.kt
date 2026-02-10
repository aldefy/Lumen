package io.luminos

import android.content.Context

/**
 * Default implementation using Android SharedPreferences.
 */
class SharedPrefsCoachmarkStorage(context: Context) : CoachmarkStorage {
    private val prefs = context.getSharedPreferences("lumen_coachmark", Context.MODE_PRIVATE)

    override fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    override fun getAllKeys(): Set<String> =
        prefs.all.keys.toSet()
}

/**
 * Convenience function to create a [CoachmarkRepository] with Android SharedPreferences.
 */
fun CoachmarkRepository(context: Context): CoachmarkRepository =
    CoachmarkRepository(SharedPrefsCoachmarkStorage(context))
