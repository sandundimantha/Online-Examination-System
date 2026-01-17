package com.example.smartexam.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student" // "student" or "admin"
)
