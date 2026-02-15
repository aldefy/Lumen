package io.luminos

import java.util.prefs.Preferences

/**
 * Default implementation using Java Preferences API for desktop.
 */
class PreferencesCoachmarkStorage(
    private val prefs: Preferences = Preferences.userRoot().node("lumen_coachmark"),
) : CoachmarkStorage {

    override fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
        prefs.flush()
    }

    override fun remove(key: String) {
        prefs.remove(key)
        prefs.flush()
    }

    override fun getAllKeys(): Set<String> =
        prefs.keys().toSet()
}

/**
 * Convenience function to create a [CoachmarkRepository] with Java Preferences storage.
 */
fun CoachmarkRepository(): CoachmarkRepository =
    CoachmarkRepository(PreferencesCoachmarkStorage())
