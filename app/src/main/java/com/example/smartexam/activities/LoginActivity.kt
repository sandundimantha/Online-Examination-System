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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Check role and redirect
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    FirebaseService.getUserRole(uid) { role ->
                        if (role == "admin") {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        } else {
                            startActivity(Intent(this, ExamListActivity::class.java))
                        }
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
