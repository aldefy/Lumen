package io.luminos

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeCoachmarkStorage : CoachmarkStorage {
    val data = mutableMapOf<String, Boolean>()

    override fun getBoolean(key: String, default: Boolean): Boolean = data[key] ?: default

    override fun putBoolean(key: String, value: Boolean) {
        data[key] = value
    }

    override fun remove(key: String) {
        data.remove(key)
    }

    override fun getAllKeys(): Set<String> = data.keys.toSet()
}

class CoachmarkRepositoryTest {

    private fun createRepository(): Pair<CoachmarkRepository, FakeCoachmarkStorage> {
        val storage = FakeCoachmarkStorage()
        return CoachmarkRepository(storage) to storage
    }

    // ── hasSeenCoachmark ─────────────────────────────────────────────────

    @Test
    fun hasSeenCoachmark_returns_false_by_default() {
        val (repo, _) = createRepository()
        assertFalse(repo.hasSeenCoachmark("intro"))
    }

    @Test
    fun hasSeenCoachmark_returns_false_for_arbitrary_unknown_id() {
        val (repo, _) = createRepository()
        assertFalse(repo.hasSeenCoachmark("does_not_exist_xyz"))
    }

    // ── markCoachmarkSeen ────────────────────────────────────────────────

    @Test
    fun markCoachmarkSeen_then_hasSeenCoachmark_returns_true() {
        val (repo, _) = createRepository()
        repo.markCoachmarkSeen("intro")
        assertTrue(repo.hasSeenCoachmark("intro"))
    }

    @Test
    fun markCoachmarkSeen_is_idempotent() {
        val (repo, _) = createRepository()
        repo.markCoachmarkSeen("intro")
        repo.markCoachmarkSeen("intro")
        assertTrue(repo.hasSeenCoachmark("intro"))
    }

    @Test
    fun multiple_coachmarks_tracked_independently() {
        val (repo, _) = createRepository()
        repo.markCoachmarkSeen("a")
        assertTrue(repo.hasSeenCoachmark("a"))
        assertFalse(repo.hasSeenCoachmark("b"))
        assertFalse(repo.hasSeenCoachmark("c"))

        repo.markCoachmarkSeen("c")
        assertTrue(repo.hasSeenCoachmark("a"))
        assertFalse(repo.hasSeenCoachmark("b"))
        assertTrue(repo.hasSeenCoachmark("c"))
    }

    // ── resetCoachmark ───────────────────────────────────────────────────

    @Test
    fun resetCoachmark_sets_back_to_false() {
        val (repo, _) = createRepository()
        repo.markCoachmarkSeen("intro")
        assertTrue(repo.hasSeenCoachmark("intro"))

        repo.resetCoachmark("intro")
        assertFalse(repo.hasSeenCoachmark("intro"))
    }

    @Test
    fun resetCoachmark_does_not_affect_other_coachmarks() {
        val (repo, _) = createRepository()
        repo.markCoachmarkSeen("a")
        repo.markCoachmarkSeen("b")
        repo.resetCoachmark("a")
        assertFalse(repo.hasSeenCoachmark("a"))
        assertTrue(repo.hasSeenCoachmark("b"))
    }

    @Test
    fun resetCoachmark_on_unseen_is_safe() {
        val (repo, _) = createRepository()
        repo.resetCoachmark("never_seen")
        assertFalse(repo.hasSeenCoachmark("never_seen"))
    }

    // ── resetAllCoachmarks ───────────────────────────────────────────────

    @Test
    fun resetAllCoachmarks_clears_only_coachmark_keys() {
        val (repo, storage) = createRepository()
        repo.markCoachmarkSeen("a")
        repo.markCoachmarkSeen("b")
        storage.putBoolean("user_preference_dark_mode", true)

        repo.resetAllCoachmarks()

        assertFalse(repo.hasSeenCoachmark("a"))
        assertFalse(repo.hasSeenCoachmark("b"))
        assertTrue(storage.getBoolean("user_preference_dark_mode", false))
    }

    @Test
    fun resetAllCoachmarks_on_empty_storage_is_safe() {
        val (repo, _) = createRepository()
        repo.resetAllCoachmarks() // should not throw
    }

    @Test
    fun resetAllCoachmarks_removes_keys_from_storage() {
        val (repo, storage) = createRepository()
        repo.markCoachmarkSeen("x")
        repo.markCoachmarkSeen("y")
        assertEquals(2, storage.data.size)

        repo.resetAllCoachmarks()
        assertEquals(0, storage.data.size)
    }

    // ── key format ───────────────────────────────────────────────────────

    @Test
    fun key_format_is_coachmark_id_shown() {
        val (repo, storage) = createRepository()
        repo.markCoachmarkSeen("intro")
        assertTrue(storage.data.containsKey("coachmark_intro_shown"))
    }

    @Test
    fun empty_id_produces_valid_key() {
        val (repo, storage) = createRepository()
        repo.markCoachmarkSeen("")
        assertTrue(storage.data.containsKey("coachmark__shown"))
        assertTrue(repo.hasSeenCoachmark(""))
    }

    @Test
    fun special_characters_in_id() {
        val (repo, storage) = createRepository()
        repo.markCoachmarkSeen("step-1.intro/main")
        assertTrue(storage.data.containsKey("coachmark_step-1.intro/main_shown"))
        assertTrue(repo.hasSeenCoachmark("step-1.intro/main"))
    }
}
