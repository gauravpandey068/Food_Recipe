package com.gaurav.foodrecipe.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.databinding.ActivityHomeBinding
import com.gaurav.foodrecipe.ui.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val homeFragment = HomeFragment()
        val addFragment = AddNewPostFragment()
        val searchFragment = SearchFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.post -> setCurrentFragment(addFragment)
                R.id.profile -> setCurrentFragment(profileFragment)

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_layout, fragment)
            commit()
        }
}