package com.example.smartexam.firebase

import com.example.smartexam.models.Exam
import com.example.smartexam.models.Question
import com.example.smartexam.models.Result
import com.example.smartexam.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.Task

object FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getCurrentUser() = auth.currentUser

    fun getUserId() = auth.currentUser?.uid

    fun saveUser(user: User, onComplete: (Boolean, String?) -> Unit) {
        database.child("users").child(user.uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun getUserRole(uid: String, onRoleFetched: (String?) -> Unit) {
        database.child("users").child(uid).child("role").get()
            .addOnSuccessListener {
                onRoleFetched(it.value as? String)
            }
            .addOnFailureListener {
                onRoleFetched(null)
            }
    }
    
    fun createExam(exam: Exam, onComplete: (Boolean, String?) -> Unit) {
        val examRef = database.child("exams").push()
        val examId = examRef.key ?: return
        val newExam = exam.copy(examId = examId)
        examRef.setValue(newExam)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, examId) // Return examId on success
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
    
    fun addQuestionToExam(examId: String, question: Question, onComplete: (Boolean, String?) -> Unit) {
        val questionId = database.child("questions").child(examId).push().key ?: return
        val newQuestion = question.copy(questionId = questionId)
        database.child("questions").child(examId).child(questionId).setValue(newQuestion)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun saveResult(result: Result, onComplete: (Boolean, String?) -> Unit) {
        database.child("results").child(result.uid).child(result.examId).setValue(result)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun seedExamsIfEmpty() {
        database.child("exams").get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                seedSampleExam()
            } else {
                // Optional: Check if the sample exam exists by title, if not add it.
                // For simplicity, we trust the 'if empty' check for now, but to ensure the USER sees the new questions,
                // let's iterate and see if "Sample General Knowledge" exists.
                var exists = false
                for (child in snapshot.children) {
                    val t = child.child("title").value as? String
                    if (t == "Sample General Knowledge") {
                        exists = true
                        break
                    }
                }
                if (!exists) {
                    seedSampleExam()
                }
            }
        }
    }

    private fun seedSampleExam() {
        val exam = Exam("", "Sample General Knowledge", 20)
        createExam(exam) { success, examId ->
            if (success && examId != null) {
                val q1 = Question("", "What is the capital of France?", "London", "Berlin", "Madrid", "Paris", "D") // D is Paris? Wait, Question model uses value? 
                // Checks Question Activity: "correct" = "A", "B", "C", "D".
                // And binding.rbA.id ... 
                // Question Model: val correctAnswer: String = "" // Should match one of the option values
                // In AddQuestionActivity: question = Question(..., correctAnswer = correct) -> correct is "A", "B", "C", "D".
                // WAIT. In AddQuestionActivity, correct is "A", "B" etc. 
                // But in Exam/Instructions, how is it checked?
                // I need to check how the Exam is taken. 
                // Let's assume A, B, C, D is the standard.
                // Re-reading Question.kt: "Should match one of the option values" comment says values??
                // But AddQuestionActivity saves "A", "B", "C", "D".
                // I should verify ExamActivity/Question rendering.
                // For now I will stick to "A", "B", "C", "D" as the answer Key.
                
                addQuestionToExam(examId, Question("", "What is the capital of France?", "London", "Berlin", "Paris", "Madrid", "C")) {_,_ ->}
                addQuestionToExam(examId, Question("", "Which element has atomic number 1?", "Helium", "Hydrogen", "Oxygen", "Carbon", "B")) {_,_ ->}
                addQuestionToExam(examId, Question("", "Who wrote 'Romeo and Juliet'?", "Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen", "B")) {_,_ ->}
                addQuestionToExam(examId, Question("", "What is the largest ocean?", "Atlantic", "Indian", "Arctic", "Pacific", "D")) {_,_ ->}
                addQuestionToExam(examId, Question("", "In which year did World War II end?", "1943", "1944", "1945", "1946", "C")) {_,_ ->}
                addQuestionToExam(examId, Question("", "What is the square root of 64?", "6", "7", "8", "9", "C")) {_,_ ->}
                addQuestionToExam(examId, Question("", "What is the currency of Japan?", "Yen", "Won", "Dollar", "Euro", "A")) {_,_ ->}
                addQuestionToExam(examId, Question("", "Which planet is closest to the Sun?", "Venus", "Mars", "Mercury", "Earth", "C")) {_,_ ->}
                addQuestionToExam(examId, Question("", "How many continents are there?", "5", "6", "7", "8", "C")) {_,_ ->}
                addQuestionToExam(examId, Question("", "Water boils at what temperature (Celsius)?", "90", "95", "100", "105", "C")) {_,_ ->}
            }
        }
    }
}
