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
                // Seed General Science
                val scienceExam = Exam("", "General Science", 10)
                createExam(scienceExam) { success, examId ->
                    if (success && examId != null) {
                        addQuestionToExam(examId, Question("", "What is the chemical symbol for Water?", "H2O", "O2", "CO2", "NaCl", "H2O"))
                        addQuestionToExam(examId, Question("", "Which planet is known as the Red Planet?", "Venus", "Mars", "Jupiter", "Saturn", "Mars"))
                        addQuestionToExam(examId, Question("", "What gas do plants absorb?", "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", "Carbon Dioxide"))
                        addQuestionToExam(examId, Question("", "What is the center of an atom called?", "Electron", "Proton", "Nucleus", "Neutron", "Nucleus"))
                        addQuestionToExam(examId, Question("", "Speed of light is faster than sound?", "True", "False", "Equal", "None", "True"))
                    }
                }

                // Seed Mathematics
                val mathExam = Exam("", "Mathematics", 15)
                createExam(mathExam) { success, examId ->
                    if (success && examId != null) {
                        addQuestionToExam(examId, Question("", "What is 2 + 2?", "3", "4", "5", "6", "4"))
                        addQuestionToExam(examId, Question("", "Solve: 5 * 6", "30", "25", "35", "20", "30"))
                        addQuestionToExam(examId, Question("", "Square root of 81?", "7", "8", "9", "10", "9"))
                    }
                }
            }
        }
    }
}
