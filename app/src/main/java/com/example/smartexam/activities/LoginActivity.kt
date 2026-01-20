package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityLoginBinding
import com.example.smartexam.firebase.FirebaseService
import com.example.smartexam.utils.ValidationUtil
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!ValidationUtil.isValidEmail(email)) {
            binding.etEmail.error = "Invalid Email"
            return
        }
        if (!ValidationUtil.isValidPassword(password)) {
            binding.etPassword.error = "Password must be at least 6 chars"
            return
        }

        // Show Loading
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnLogin.isEnabled = false
        
        // Timeout Handler
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            binding.progressBar.visibility = android.view.View.GONE
            binding.btnLogin.isEnabled = true
            Toast.makeText(this, "Request timed out. Please check your internet connection.", Toast.LENGTH_LONG).show()
        }
        handler.postDelayed(timeoutRunnable, 15000) // 15 seconds

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Auth Success
                    Toast.makeText(this, "Verified. Fetching Profile...", Toast.LENGTH_SHORT).show()
                    
                    val uid = task.result?.user?.uid 
                    if (uid == null) {
                         handler.removeCallbacks(timeoutRunnable)
                         binding.progressBar.visibility = android.view.View.GONE
                         binding.btnLogin.isEnabled = true
                         return@addOnCompleteListener
                    }

                    FirebaseService.getUserRole(uid) { role ->
                        handler.removeCallbacks(timeoutRunnable) // Cancel timeout
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnLogin.isEnabled = true
                        
                        if (role == "admin") {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        } else {
                            startActivity(Intent(this, ExamListActivity::class.java))
                        }
                        finish()
                    }
                } else {
                    handler.removeCallbacks(timeoutRunnable) // Cancel timeout
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnLogin.isEnabled = true
                    val errorMsg = task.exception?.message ?: "Unknown Error"
                    Toast.makeText(this, "Login Failed: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }
}
