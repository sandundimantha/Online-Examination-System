package com.example.smartexam.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.databinding.ActivityAddQuestionBinding
import com.example.smartexam.firebase.FirebaseService
import com.example.smartexam.models.Question
import com.example.smartexam.utils.ValidationUtil

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding
    private lateinit var examId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getStringExtra("EXAM_ID") ?: run {
            Toast.makeText(this, "Invalid Exam ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnAddQuestion.setOnClickListener {
            addQuestion()
        }

        binding.btnFinish.setOnClickListener {
            finish()
        }
    }

    private fun addQuestion() {
        val text = binding.etQuestionText.text.toString().trim()
        val opA = binding.etOptionA.text.toString().trim()
        val opB = binding.etOptionB.text.toString().trim()
        val opC = binding.etOptionC.text.toString().trim()
        val opD = binding.etOptionD.text.toString().trim()
        
        var correct: String? = null
        when (binding.rgCorrectAnswer.checkedRadioButtonId) {
            binding.rbA.id -> correct = "A"
            binding.rbB.id -> correct = "B"
            binding.rbC.id -> correct = "C"
            binding.rbD.id -> correct = "D"
        }

        if (!ValidationUtil.isValidField(text) || !ValidationUtil.isValidField(opA) ||
            !ValidationUtil.isValidField(opB) || !ValidationUtil.isValidField(opC) ||
            !ValidationUtil.isValidField(opD) || correct == null) {
            Toast.makeText(this, "Please fill all fields and select correct answer", Toast.LENGTH_SHORT).show()
            return
        }

        val question = Question(
            questionText = text,
            optionA = opA,
            optionB = opB,
            optionC = opC,
            optionD = opD,
            correctAnswer = correct!!
        )

        FirebaseService.addQuestionToExam(examId, question) { success, _ ->
            if (success) {
                Toast.makeText(this, "Question Added!", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(this, "Failed to add question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        binding.etQuestionText.text?.clear()
        binding.etOptionA.text?.clear()
        binding.etOptionB.text?.clear()
        binding.etOptionC.text?.clear()
        binding.etOptionD.text?.clear()
        binding.rgCorrectAnswer.clearCheck()
    }
}
