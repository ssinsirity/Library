package com.ssinsirity.core.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.ssinsirity.core.data.model.ReaderCard
import java.util.*
import javax.inject.Inject

class UserSharedPref @Inject constructor(context: Context) {
    companion object {
        private const val CURRENT_READER = "current_reader"

        private val defaultUser = ReaderCard(
            id = "4kPI8D8hD0aNFknQif6d",
            email = "example@mail.com",
            cardNumber = "123456",
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "+380987654321"
        )
    }

    private val sharedPref =
        context.getSharedPreferences("reader_shared_prefs", Context.MODE_PRIVATE)

    private val gson by lazy { Gson() }

    var currentReader: ReaderCard
        set(value) {
            sharedPref.edit {
                val json = gson.toJson(value)
                putString(CURRENT_READER, json)
            }
        }
        get() {
            val defaultUserJson = gson.toJson(defaultUser)
            val json = sharedPref.getString(CURRENT_READER, defaultUserJson)
            return gson.fromJson(json, ReaderCard::class.java)
        }
}