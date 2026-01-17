package com.example.smartexam.utils

import android.os.CountDownTimer

object TimerUtil {
    fun startTimer(
        durationMillis: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }
            override fun onFinish() {
                onFinish()
            }
        }.start()
    }
}
