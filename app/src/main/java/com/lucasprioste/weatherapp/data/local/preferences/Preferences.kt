package com.lucasprioste.weatherapp.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.lucasprioste.weatherapp.utils.FILE_NAME_PREFERENCES
import com.lucasprioste.weatherapp.utils.POPULATE_DB_KEY

interface Preferences {

    fun changePopulateDb(value: Boolean){
        preferences.edit().putBoolean(POPULATE_DB_KEY, value).apply()
    }

    fun getPopulateDb(): Boolean{
        return preferences.getBoolean(POPULATE_DB_KEY,false)
    }

    companion object{
        private lateinit var preferences: SharedPreferences

        fun getInstance(context: Context): Preferences{
            if (!this::preferences.isInitialized){
                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                preferences = EncryptedSharedPreferences.create(
                    FILE_NAME_PREFERENCES,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
            return object :Preferences{}
        }
    }
}