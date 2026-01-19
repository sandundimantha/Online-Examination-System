package com.example.smartexam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartexam.R
import com.example.smartexam.databinding.ActivityAdminBinding
import com.example.smartexam.fragments.AdminHomeFragment
import com.example.smartexam.fragments.AdminResultsFragment
import com.example.smartexam.fragments.ProfileFragment

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(AdminHomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_admin_home -> fragment = AdminHomeFragment()
                R.id.nav_admin_results -> fragment = AdminResultsFragment()
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
            .replace(R.id.nav_host_fragment_admin, fragment)
            .commit()
    }
}
