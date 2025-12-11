package projectichif.DriveNusa.utils

import android.content.Context
import android.content.SharedPreferences

object JadwalPrefs {

    private const val PREF_NAME = "jadwal_prefs"
    private const val KEY_STATUS_LIST = "status_list"

    // 14 pertemuan semua status default = 0
    private val DEFAULT_STATUS = MutableList(14) { 0 }

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getStatusList(context: Context): MutableList<Int> {
        val saved = prefs(context).getString(KEY_STATUS_LIST, null)

        return if (saved != null) {
            val list = saved.split(",").map { it.toInt() }.toMutableList()
            while (list.size < 14) list.add(0)
            list
        } else {
            DEFAULT_STATUS.toMutableList()
        }
    }

    fun saveStatus(context: Context, index: Int, status: Int) {
        val list = getStatusList(context)
        list[index] = status
        prefs(context).edit().putString(KEY_STATUS_LIST, list.joinToString(",")).apply()
    }
}
