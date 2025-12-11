package projectichif.DriveNusa.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

// ----------- EXTENSION WAJIB ADA --------------
val Context.dataStore by preferencesDataStore(name = "user_pref")
// ----------------------------------------------

class DataStoreManager(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { pref ->
            pref[USERNAME_KEY] = username
        }
    }

    val getUsername: Flow<String> = context.dataStore.data.map { pref ->
        pref[USERNAME_KEY] ?: ""
    }
}