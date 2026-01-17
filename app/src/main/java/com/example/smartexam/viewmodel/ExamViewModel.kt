package com.example.smartexam.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartexam.models.Question
import com.example.smartexam.utils.TimerUtil

class ExamViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _isExamFinished = MutableLiveData<Boolean>()
    val isExamFinished: LiveData<Boolean> = _isExamFinished

    private val userAnswers = mutableMapOf<String, String>()
    private var timer: CountDownTimer? = null

    fun startExamTimer(durationMinutes: Int) {
        val durationMillis = durationMinutes * 60 * 1000L
        timer = TimerUtil.startTimer(
            durationMillis,
            onTick = { millisUntilFinished ->
                _timeLeft.value = millisUntilFinished
            },
            onFinish = {
                _isExamFinished.value = true
            }
        )
    }

    fun submitAnswer(questionId: String, selectedOption: String) {
        userAnswers[questionId] = selectedOption
    }

    fun finishExam() {
        timer?.cancel()
        _isExamFinished.value = true
    }

    fun calculateScore(questions: List<Question>): Int {
        var score = 0
        for (q in questions) {
            if (userAnswers[q.questionId] == q.correctAnswer) {
                score++
            }
        }
        return score
    }
    
    // Helper to get total questions attempted if needed
    fun getAttemptedCount(): Int = userAnswers.size

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
