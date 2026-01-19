package com.example.smartexam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexam.databinding.FragmentAdminResultsBinding
import com.example.smartexam.models.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminResultsFragment : Fragment() {

    private var _binding: FragmentAdminResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        fetchResults()
    }

    private fun fetchResults() {
        val resultsRef = FirebaseDatabase.getInstance().getReference("results")
        val resultList = mutableListOf<String>()

        resultsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resultList.clear()
                for (userSnapshot in snapshot.children) {
                    for (examSnapshot in userSnapshot.children) {
                        val result = examSnapshot.getValue(Result::class.java)
                        if (result != null) {
                            resultList.add("Student: ${userSnapshot.key} \nExam: ${result.examId} \nScore: ${result.score}/${result.totalQuestions}")
                        }
                    }
                }
                binding.rvResults.adapter = SimpleResultAdapter(resultList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    class SimpleResultAdapter(private val data: List<String>) : RecyclerView.Adapter<SimpleResultAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val text: TextView = v.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.text.text = data[position]
        }

        override fun getItemCount() = data.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
