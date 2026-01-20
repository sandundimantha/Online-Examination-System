package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.R
import com.example.smartexam.firebase.FirebaseService

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, 2000)
    }

    private fun checkUserSession() {
        val user = FirebaseService.getCurrentUser()
        if (user != null) {
            // Safety timeout: If fetching role takes too long (e.g. offline), default to ExamList
            val handler = Handler(Looper.getMainLooper())
            val timeoutRunnable = Runnable {
                startActivity(Intent(this, ExamListActivity::class.java))
                finish()
            }
            handler.postDelayed(timeoutRunnable, 3000) // 3 seconds timeout

            FirebaseService.getUserRole(user.uid) { role ->
                handler.removeCallbacks(timeoutRunnable) // Cancel timeout if successful
                if (role == "admin") {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, ExamListActivity::class.java))
                }
                finish()
            }
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}
