package com.example.smartexam.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexam.activities.InstructionsActivity
import com.example.smartexam.databinding.FragmentStudentHomeBinding
import com.example.smartexam.models.Exam
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentHomeFragment : Fragment() {

    private var _binding: FragmentStudentHomeBinding? = null
    private val binding get() = _binding!!
    private val examList = mutableListOf<Exam>()
    private lateinit var adapter: ExamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvExams.layoutManager = LinearLayoutManager(requireContext())
        adapter = ExamAdapter(examList) { exam ->
            val intent = Intent(requireContext(), InstructionsActivity::class.java)
            intent.putExtra("EXAM_ID", exam.examId)
            intent.putExtra("EXAM_TITLE", exam.title)
            intent.putExtra("EXAM_DURATION", exam.duration)
            startActivity(intent)
        }
        binding.rvExams.adapter = adapter

        // Auto-seed sample exams if none exist
        com.example.smartexam.firebase.FirebaseService.seedExamsIfEmpty()
        
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
                Toast.makeText(requireContext(), "Failed to load exams", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inner Adapter class
    inner class ExamAdapter(
        private val exams: List<Exam>,
        private val onClick: (Exam) -> Unit
    ) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

        inner class ExamViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
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
