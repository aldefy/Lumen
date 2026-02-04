package io.luminos

import android.content.Context

/**
 * Interface for coachmark persistence storage.
 * Implement this interface to provide custom storage mechanisms.
 */
interface CoachmarkStorage {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun remove(key: String)
    fun getAllKeys(): Set<String>
}

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
 * Repository for persisting coachmark shown state.
 *
 * Coachmarks are typically shown only once per user. This repository
 * tracks which coachmarks have been seen to prevent showing them again.
 *
 * @param storage The storage mechanism to use for persistence.
 *                Use [SharedPrefsCoachmarkStorage] for the default Android implementation.
 */
class CoachmarkRepository(
    private val storage: CoachmarkStorage,
) {
    companion object {
        private const val PREFIX = "coachmark_"
        private const val SUFFIX_SHOWN = "_shown"
    }

    /**
     * Checks if a coachmark has been seen by the user.
     *
     * @param id The unique identifier of the coachmark
     * @return true if the coachmark has been seen, false otherwise
     */
    fun hasSeenCoachmark(id: String): Boolean {
        return storage.getBoolean(buildKey(id), false)
    }

    /**
     * Marks a coachmark as seen.
     *
     * @param id The unique identifier of the coachmark
     */
    fun markCoachmarkSeen(id: String) {
        storage.putBoolean(buildKey(id), true)
    }

    /**
     * Resets a coachmark to unseen state.
     * Useful for testing or allowing users to see tutorials again.
     *
     * @param id The unique identifier of the coachmark
     */
    fun resetCoachmark(id: String) {
        storage.putBoolean(buildKey(id), false)
    }

    /**
     * Resets all coachmarks to unseen state.
     * Useful for testing or full tutorial reset.
     */
    fun resetAllCoachmarks() {
        storage.getAllKeys().filter { it.startsWith(PREFIX) }.forEach { key ->
            storage.remove(key)
        }
    }

    private fun buildKey(id: String): String {
        return "$PREFIX$id$SUFFIX_SHOWN"
    }
}

/**
 * Convenience function to create a [CoachmarkRepository] with Android SharedPreferences.
 */
fun CoachmarkRepository(context: Context): CoachmarkRepository =
    CoachmarkRepository(SharedPrefsCoachmarkStorage(context))
