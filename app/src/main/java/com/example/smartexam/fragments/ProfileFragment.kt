package com.example.smartexam.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartexam.activities.LoginActivity
import com.example.smartexam.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            binding.tvEmail.text = user.email ?: "No Email"
            
            // Initial Check to update UI text (Output only)
            val uid = user.uid
            val ref = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users").child(uid).child("role")
            
            // Default text
            binding.btnSwitchRole.text = "Switch Role (Loading...)"
            binding.btnSwitchRole.isEnabled = false

            ref.get().addOnSuccessListener { snapshot ->
                val currentRole = snapshot.value as? String ?: "student"
                if (currentRole == "admin") {
                    binding.btnSwitchRole.text = "Switch to Student Role (Dev)"
                } else {
                    binding.btnSwitchRole.text = "Switch to Admin Role (Dev)"
                }
                binding.btnSwitchRole.isEnabled = true
            }.addOnFailureListener {
                 binding.btnSwitchRole.text = "Switch Role (Retry)"
                 binding.btnSwitchRole.isEnabled = true
            }

            // Click Listener attached immediately
            binding.btnSwitchRole.setOnClickListener {
                binding.btnSwitchRole.isEnabled = false
                binding.btnSwitchRole.text = "Switching..."
                
                // Fetch fresh role to toggle
                ref.get().addOnSuccessListener { snapshot ->
                    val freshRole = snapshot.value as? String ?: "student"
                    val newRole = if (freshRole == "admin") "student" else "admin"
                    
                    ref.setValue(newRole).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val msg = if (newRole == "admin") "Role updated to Admin! Please Re-Login." else "Role updated to Student! Please Re-Login."
                            android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_LONG).show()
                            
                            // Logout
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                             android.widget.Toast.makeText(requireContext(), "Failed to update role", android.widget.Toast.LENGTH_SHORT).show()
                             binding.btnSwitchRole.isEnabled = true
                             binding.btnSwitchRole.text = "Try Switch Role Again"
                        }
                    }
                }.addOnFailureListener {
                    android.widget.Toast.makeText(requireContext(), "Failed to fetch current role", android.widget.Toast.LENGTH_SHORT).show()
                    binding.btnSwitchRole.isEnabled = true
                    binding.btnSwitchRole.text = "Try Switch Role Again"
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
