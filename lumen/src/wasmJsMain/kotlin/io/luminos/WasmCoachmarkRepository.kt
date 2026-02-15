package io.luminos

import kotlinx.browser.localStorage

/**
 * Default implementation using browser localStorage for Wasm.
 */
class LocalStorageCoachmarkStorage : CoachmarkStorage {

    override fun getBoolean(key: String, default: Boolean): Boolean {
        val value = localStorage.getItem(key) ?: return default
        return value.toBooleanStrictOrNull() ?: default
    }

    override fun putBoolean(key: String, value: Boolean) {
        localStorage.setItem(key, value.toString())
    }

    override fun remove(key: String) {
        localStorage.removeItem(key)
    }

    override fun getAllKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        for (i in 0 until localStorage.length) {
            localStorage.key(i)?.let { keys.add(it) }
        }
        return keys
    }
}

/**
 * Convenience function to create a [CoachmarkRepository] with browser localStorage.
 */
fun CoachmarkRepository(): CoachmarkRepository =
    CoachmarkRepository(LocalStorageCoachmarkStorage())
