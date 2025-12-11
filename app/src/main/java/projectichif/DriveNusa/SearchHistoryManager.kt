package projectichif.DriveNusa


import android.content.Context

object SearchHistoryManager {

    private const val PREF_NAME = "search_history"
    private const val KEY_HISTORY = "history_list"

    fun saveQuery(context: Context, query: String) {
        if (query.isBlank()) return

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_HISTORY, mutableSetOf()) ?: mutableSetOf()

        set.add(query) // simpan tanpa duplikat

        prefs.edit().putStringSet(KEY_HISTORY, set).apply()
    }

    fun getHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_HISTORY, setOf())?.toList() ?: emptyList()
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
