package io.luminos

import platform.Foundation.NSUserDefaults

/**
 * Default implementation using iOS NSUserDefaults.
 */
class NSUserDefaultsCoachmarkStorage : CoachmarkStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else default

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    override fun getAllKeys(): Set<String> =
        defaults.dictionaryRepresentation().keys
            .filterIsInstance<String>()
            .toSet()
}

/**
 * Convenience function to create a [CoachmarkRepository] with iOS NSUserDefaults.
 */
fun CoachmarkRepository(): CoachmarkRepository =
    CoachmarkRepository(NSUserDefaultsCoachmarkStorage())
