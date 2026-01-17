package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartexam.adapters.QuestionAdapter
import com.example.smartexam.databinding.ActivityExamBinding
import com.example.smartexam.firebase.FirebaseService
import com.example.smartexam.models.Question
import com.example.smartexam.models.Result
import com.example.smartexam.viewmodel.ExamViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamBinding
    private val viewModel: ExamViewModel by viewModels()
    private val questionList = mutableListOf<Question>()
    private lateinit var examId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getStringExtra("EXAM_ID") ?: return
        val duration = intent.getIntExtra("EXAM_DURATION", 30)

        binding.rvQuestions.layoutManager = LinearLayoutManager(this)

        // Observe ViewModel
        viewModel.timeLeft.observe(this) { millis ->
            val min = (millis / 1000) / 60
            val sec = (millis / 1000) % 60
            binding.tvTimer.text = String.format("%02d:%02d", min, sec)
        }

        viewModel.isExamFinished.observe(this) { finished ->
            if (finished) {
                submitExam()
            }
        }

        loadQuestions(examId)
        viewModel.startExamTimer(duration)

        binding.btnSubmit.setOnClickListener {
            viewModel.finishExam() // Will trigger observer
        }
    }

    private fun loadQuestions(examId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("questions").child(examId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionList.clear()
                for (child in snapshot.children) {
                    val q = child.getValue(Question::class.java)
                    if (q != null) questionList.add(q)
                }
                setupRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExamActivity, "Error loading questions", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        val adapter = QuestionAdapter(questionList) { qId, answer ->
            viewModel.submitAnswer(qId, answer)
        }
        binding.rvQuestions.adapter = adapter
    }

    private fun submitExam() {
        val score = viewModel.calculateScore(questionList)
        val total = questionList.size
        val percentage = if (total > 0) (score.toDouble() / total) * 100 else 0.0

        val uid = FirebaseService.getUserId() ?: return
        val result = Result(uid, examId, score, total, percentage)

        FirebaseService.saveResult(result) { success, _ ->
            if (success) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("SCORE", score)
                intent.putExtra("TOTAL", total)
                intent.putExtra("PERCENTAGE", percentage)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to save results", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
