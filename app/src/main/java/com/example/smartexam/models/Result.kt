package com.example.smartexam.models

data class Result(
    val uid: String = "",
    val examId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
