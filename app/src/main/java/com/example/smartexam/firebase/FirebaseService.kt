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
}
