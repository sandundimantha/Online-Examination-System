package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityRoleBinding

class RoleSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This screen might be used if we allow manual role selection before login/reg
        // But for better flow, role is handled in code.
        // We can keep this as a stub or testing utility.
        
        binding.btnStudent.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnAdmin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
