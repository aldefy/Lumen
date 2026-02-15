package io.luminos

import platform.Foundation.NSUserDefaults
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NSUserDefaultsCoachmarkStorageTest {

    private val storage = NSUserDefaultsCoachmarkStorage()

    // Keys used during tests — cleaned up after each test
    private val testKeys = mutableListOf<String>()

    private fun testKey(name: String): String {
        val key = "test_lumen_$name"
        testKeys.add(key)
        return key
    }

    @AfterTest
    fun cleanup() {
        val defaults = NSUserDefaults.standardUserDefaults
        testKeys.forEach { defaults.removeObjectForKey(it) }
        testKeys.clear()
    }

    // ── getBoolean ───────────────────────────────────────────────────────

    @Test
    fun getBoolean_returns_default_when_key_missing() {
        val key = testKey("missing")
        assertTrue(storage.getBoolean(key, default = true))
        assertFalse(storage.getBoolean(key, default = false))
    }

    @Test
    fun getBoolean_returns_stored_value_not_default() {
        val key = testKey("stored_true")
        storage.putBoolean(key, true)
        assertTrue(storage.getBoolean(key, default = false))

        val key2 = testKey("stored_false")
        storage.putBoolean(key2, false)
        assertFalse(storage.getBoolean(key2, default = true))
    }

    // ── putBoolean ───────────────────────────────────────────────────────

    @Test
    fun putBoolean_writes_true() {
        val key = testKey("put_true")
        storage.putBoolean(key, true)
        assertTrue(storage.getBoolean(key, default = false))
    }

    @Test
    fun putBoolean_writes_false() {
        val key = testKey("put_false")
        storage.putBoolean(key, false)
        assertFalse(storage.getBoolean(key, default = true))
    }

    @Test
    fun putBoolean_overwrites_existing_value() {
        val key = testKey("overwrite")
        storage.putBoolean(key, true)
        assertTrue(storage.getBoolean(key, default = false))

        storage.putBoolean(key, false)
        assertFalse(storage.getBoolean(key, default = true))
    }

    // ── remove ───────────────────────────────────────────────────────────

    @Test
    fun remove_deletes_key() {
        val key = testKey("to_remove")
        storage.putBoolean(key, true)
        assertTrue(storage.getBoolean(key, default = false))

        storage.remove(key)
        // After removal, should return default
        assertFalse(storage.getBoolean(key, default = false))
    }

    @Test
    fun remove_nonexistent_key_is_safe() {
        val key = testKey("never_existed")
        storage.remove(key) // should not throw
    }

    @Test
    fun remove_then_getBoolean_returns_default() {
        val key = testKey("remove_default")
        storage.putBoolean(key, true)
        storage.remove(key)
        assertTrue(storage.getBoolean(key, default = true))
        assertFalse(storage.getBoolean(key, default = false))
    }

    // ── getAllKeys ────────────────────────────────────────────────────────

    @Test
    fun getAllKeys_contains_stored_keys() {
        val key1 = testKey("allkeys_a")
        val key2 = testKey("allkeys_b")
        storage.putBoolean(key1, true)
        storage.putBoolean(key2, false)

        val keys = storage.getAllKeys()
        assertTrue(keys.contains(key1))
        assertTrue(keys.contains(key2))
    }

    @Test
    fun getAllKeys_does_not_contain_removed_key() {
        val key = testKey("allkeys_removed")
        storage.putBoolean(key, true)
        storage.remove(key)

        val keys = storage.getAllKeys()
        assertFalse(keys.contains(key))
    }

    @Test
    fun getAllKeys_returns_strings() {
        val key = testKey("allkeys_type")
        storage.putBoolean(key, true)

        val keys = storage.getAllKeys()
        // Should be a Set<String>, not crash on filterIsInstance
        assertTrue(keys is Set<String>)
    }

    // ── Integration with CoachmarkRepository ─────────────────────────────

    @Test
    fun repository_roundtrip_via_NSUserDefaults() {
        // Use unique prefix to avoid collisions with other tests
        val repo = CoachmarkRepository(storage)
        val id = "test_lumen_integration"
        testKeys.add("coachmark_${id}_shown")

        assertFalse(repo.hasSeenCoachmark(id))
        repo.markCoachmarkSeen(id)
        assertTrue(repo.hasSeenCoachmark(id))
        repo.resetCoachmark(id)
        assertFalse(repo.hasSeenCoachmark(id))
    }

    @Test
    fun repository_resetAll_via_NSUserDefaults() {
        val repo = CoachmarkRepository(storage)
        val id1 = "test_lumen_resetall_a"
        val id2 = "test_lumen_resetall_b"
        testKeys.add("coachmark_${id1}_shown")
        testKeys.add("coachmark_${id2}_shown")

        repo.markCoachmarkSeen(id1)
        repo.markCoachmarkSeen(id2)
        assertTrue(repo.hasSeenCoachmark(id1))
        assertTrue(repo.hasSeenCoachmark(id2))

        repo.resetAllCoachmarks()
        assertFalse(repo.hasSeenCoachmark(id1))
        assertFalse(repo.hasSeenCoachmark(id2))
    }

    // ── Convenience factory ──────────────────────────────────────────────

    @Test
    fun convenience_factory_creates_working_repository() {
        val repo = CoachmarkRepository()
        val id = "test_lumen_factory"
        testKeys.add("coachmark_${id}_shown")

        assertFalse(repo.hasSeenCoachmark(id))
        repo.markCoachmarkSeen(id)
        assertTrue(repo.hasSeenCoachmark(id))
    }
}
