package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexam.R
import com.example.smartexam.databinding.ActivityExamListBinding
import com.example.smartexam.models.Exam
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamListBinding
    private val examList = mutableListOf<Exam>()
    private lateinit var adapter: ExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvExams.layoutManager = LinearLayoutManager(this)
        adapter = ExamAdapter(examList) { exam ->
            val intent = Intent(this, InstructionsActivity::class.java)
            intent.putExtra("EXAM_ID", exam.examId)
            intent.putExtra("EXAM_TITLE", exam.title)
            intent.putExtra("EXAM_DURATION", exam.duration)
            startActivity(intent)
        }
        binding.rvExams.adapter = adapter

        fetchExams()
    }

    private fun fetchExams() {
        val ref = FirebaseDatabase.getInstance().getReference("exams")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                examList.clear()
                for (child in snapshot.children) {
                    val exam = child.getValue(Exam::class.java)
                    if (exam != null) {
                        examList.add(exam)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExamListActivity, "Failed to load exams", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Inner Adapter class for simplicity
    inner class ExamAdapter(
        private val exams: List<Exam>,
        private val onClick: (Exam) -> Unit
    ) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

        inner class ExamViewHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(android.R.id.text1)
            val subtitle: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return ExamViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
            val exam = exams[position]
            holder.title.text = exam.title
            holder.subtitle.text = "${exam.duration} Minutes"
            holder.itemView.setOnClickListener { onClick(exam) }
        }

        override fun getItemCount() = exams.size
    }
}
