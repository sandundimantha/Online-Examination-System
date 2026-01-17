package com.example.smartexam.utils

import android.text.TextUtils
import android.util.Patterns

object ValidationUtil {
    
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return !TextUtils.isEmpty(password) && password.length >= 6
    }

    fun isValidField(field: String): Boolean {
        return !TextUtils.isEmpty(field)
    }
}
