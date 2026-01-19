package com.example.smartexam.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartexam.R
import com.example.smartexam.databinding.ActivityExamListBinding
import com.example.smartexam.fragments.ProfileFragment
import com.example.smartexam.fragments.StudentHomeFragment

class ExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(StudentHomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_student_home -> fragment = StudentHomeFragment()
                R.id.nav_profile -> fragment = ProfileFragment()
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_student, fragment)
            .commit()
    }
}
