package projectichif.DriveNusa.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("notification_prefs")

class NotificationPreferences(private val context: Context) {

    companion object {
        val PENGINGAT = booleanPreferencesKey("pengingat")
        val PEMBARUAN_APLIKASI = booleanPreferencesKey("pembaruan_aplikasi")
        val PEMBARUAN_PRODUK = booleanPreferencesKey("pembaruan_produk")
        val PROMO = booleanPreferencesKey("promo")
    }

    // ---------- FLOW GET DATA ----------
    val pengingat = context.dataStore.data.map { it[PENGINGAT] ?: false }
    val pembaruanAplikasi = context.dataStore.data.map { it[PEMBARUAN_APLIKASI] ?: false }
    val pembaruanProduk = context.dataStore.data.map { it[PEMBARUAN_PRODUK] ?: false }
    val promo = context.dataStore.data.map { it[PROMO] ?: false }

    // ---------- SAVE DATA ----------
    suspend fun setPengingat(value: Boolean) {
        context.dataStore.edit { it[PENGINGAT] = value }
    }

    suspend fun setPembaruanAplikasi(value: Boolean) {
        context.dataStore.edit { it[PEMBARUAN_APLIKASI] = value }
    }

    suspend fun setPembaruanProduk(value: Boolean) {
        context.dataStore.edit { it[PEMBARUAN_PRODUK] = value }
    }

    suspend fun setPromo(value: Boolean) {
        context.dataStore.edit { it[PROMO] = value }
    }
}
