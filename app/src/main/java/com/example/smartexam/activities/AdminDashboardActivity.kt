package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddExam.setOnClickListener {
            startActivity(Intent(this, AddExamActivity::class.java))
        }

        binding.btnViewResults.setOnClickListener {
            startActivity(Intent(this, ViewResultsActivity::class.java)) // Need to create ViewResultsActivity
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
