package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityAddExamBinding
import com.example.smartexam.firebase.FirebaseService
import com.example.smartexam.models.Exam
import com.example.smartexam.utils.ValidationUtil

class AddExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveExam.setOnClickListener {
            saveExam()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun saveExam() {
        val title = binding.etExamTitle.text.toString().trim()
        val durationStr = binding.etDuration.text.toString().trim()

        if (!ValidationUtil.isValidField(title)) {
            binding.etExamTitle.error = "Required"
            return
        }
        if (!ValidationUtil.isValidField(durationStr)) {
            binding.etDuration.error = "Required"
            return
        }

        val duration = durationStr.toIntOrNull() ?: 30
        val exam = Exam(title = title, duration = duration) // ID generated in service

        binding.btnSaveExam.isEnabled = false
        binding.btnSaveExam.text = "Saving..."

        FirebaseService.createExam(exam) { success, resultId ->
            binding.btnSaveExam.isEnabled = true
            binding.btnSaveExam.text = "Create & Add Questions"
            
            if (success && resultId != null) {
                Toast.makeText(this, "Exam Created", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AddQuestionActivity::class.java)
                intent.putExtra("EXAM_ID", resultId)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error: $resultId", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
