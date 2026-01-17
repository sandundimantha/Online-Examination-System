package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)
        val percentage = intent.getDoubleExtra("PERCENTAGE", 0.0)

        binding.tvScore.text = "Score: $score / $total"
        binding.tvPercentage.text = String.format("%.1f%%", percentage)

        if (percentage >= 50) {
            binding.tvStatus.text = "PASSED"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvStatus.text = "FAILED"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        binding.btnHome.setOnClickListener {
            // Ideally clear stack or go to main list
            val intent = Intent(this, ExamListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
