package com.example.smartexam.models

data class Question(
    val questionId: String = "",
    val questionText: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = "" // Should match one of the option values
)
