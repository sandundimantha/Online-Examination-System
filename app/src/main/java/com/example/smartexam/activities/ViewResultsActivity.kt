package com.example.smartexam.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartexam.R
import com.example.smartexam.models.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_results)

        // Ideally use RecyclerView, but for quick implementation matching layout ID rvResults
        // Wait, I check layout activity_view_results.xml, it HAS a RecyclerView with id rvResults.
        // So I must use RecyclerView.

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvResults)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        
        // I need a generic adapter or create a specific one. 
        // For brevity, I'll create an anonymous inner adapter or similar.
        // Actually, let's just fetch data first.
        
        val resultsRef = FirebaseDatabase.getInstance().getReference("results")
        val resultList = mutableListOf<String>() // Just showing strings for now "User: Score"
        
        // Results structure: results -> uid -> examId -> ResultObj
        // This is nested. Admin wants to view ALL results.
        
        resultsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Flatten the data
                for (userSnapshot in snapshot.children) {
                   // val uid = userSnapshot.key
                    for (examSnapshot in userSnapshot.children) {
                        val result = examSnapshot.getValue(Result::class.java)
                        if (result != null) {
                            resultList.add("Student: ${result.uid} \nExam: ${result.examId} \nScore: ${result.score}/${result.totalQuestions}")
                        }
                    }
                }
                
                // Simple adapter for RecyclerView? 
                // I need a ViewHolder.
                recyclerView.adapter = SimpleResultAdapter(resultList)
            }

            override fun onCancelled(error: DatabaseError) {
                
            }
        })
    }

    class SimpleResultAdapter(private val data: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<SimpleResultAdapter.VH>() {
        class VH(v: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
            val text: android.widget.TextView = v.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
            val v = android.view.LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.text.text = data[position]
        }

        override fun getItemCount() = data.size
    }
}
