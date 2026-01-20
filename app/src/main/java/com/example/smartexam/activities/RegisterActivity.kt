package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityRegisterBinding
import com.example.smartexam.models.User
import com.example.smartexam.firebase.FirebaseService
import com.example.smartexam.utils.ValidationUtil
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!ValidationUtil.isValidField(name)) {
            binding.etName.error = "Name required"
            return
        }
        if (!ValidationUtil.isValidEmail(email)) {
            binding.etEmail.error = "Invalid Email"
            return
        }
        if (!ValidationUtil.isValidPassword(password)) {
            binding.etPassword.error = "Password much be at least 6 chars"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    
                    // Determine Role
                    val selectedRoleId = binding.rgRole.checkedRadioButtonId
                    val role = if (selectedRoleId == binding.rbAdmin.id) "admin" else "student"

                    val newUser = User(uid, name, email, role)
                    
                    FirebaseService.saveUser(newUser) { success, error ->
                        if (success) {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            if (role == "admin") {
                                startActivity(Intent(this, AdminDashboardActivity::class.java))
                            } else {
                                startActivity(Intent(this, ExamListActivity::class.java))
                            }
                            finishAffinity()
                        } else {
                            Toast.makeText(this, "Db Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Account already exists! Please Login.", Toast.LENGTH_LONG).show()
                        // Optional: Redirect to Login automatically or highlight login text
                    } else {
                        Toast.makeText(this, "Register Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
