package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityInstructionsBinding

class InstructionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstructionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val examId = intent.getStringExtra("EXAM_ID")
        val examTitle = intent.getStringExtra("EXAM_TITLE")
        val duration = intent.getIntExtra("EXAM_DURATION", 30)

        binding.tvExamTitle.text = examTitle
        binding.tvDuration.text = "Duration: $duration Mins"

        binding.btnStartExam.setOnClickListener {
            val intent = Intent(this, ExamActivity::class.java)
            intent.putExtra("EXAM_ID", examId)
            intent.putExtra("EXAM_DURATION", duration)
            startActivity(intent)
            finish()
        }
    }
}
