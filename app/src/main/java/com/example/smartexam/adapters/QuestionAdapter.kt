package com.example.smartexam.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexam.databinding.ItemQuestionBinding
import com.example.smartexam.models.Question

class QuestionAdapter(
    private val questions: List<Question>,
    private val onAnswerSelected: (String, String) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Question) {
            binding.tvQuestionText.text = question.questionText
            binding.rbOptionA.text = question.optionA
            binding.rbOptionB.text = question.optionB
            binding.rbOptionC.text = question.optionC
            binding.rbOptionD.text = question.optionD

            binding.rgOptions.setOnCheckedChangeListener { group, checkedId ->
                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                // Use simplified keys "A", "B", "C", "D" to match model if that's how we store correct answer
                // OR store actual text. The user request sample doesn't specify, but assume A/B/C/D mapping is cleaner.
                // However, the model has "optionA", "optionB" string values. 
                // Let's assume the correct answer stored in DB is one of "A", "B", "C", "D" 
                // OR it matches the text. Let's make it robust by sending the KEY (A,B,C,D).
                
                var answerKey = ""
                when(checkedId) {
                    binding.rbOptionA.id -> answerKey = "A"
                    binding.rbOptionB.id -> answerKey = "B"
                    binding.rbOptionC.id -> answerKey = "C"
                    binding.rbOptionD.id -> answerKey = "D"
                }
                
                // If model expects exact text match, careful. 
                // User's sample data: just "correctAnswer". 
                // Let's stick to "A", "B", "C", "D" keys for logic simplicity.
                
                onAnswerSelected(question.questionId, answerKey)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun getItemCount() = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
}
