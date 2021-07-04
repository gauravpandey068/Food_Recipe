package com.gaurav.foodrecipe.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.MainActivity
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.User
import com.gaurav.foodrecipe.databinding.FragmentProfileBinding
import com.gaurav.foodrecipe.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, HomeFragment())
                    commit()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)

        auth = Firebase.auth
        val db = Firebase.firestore
        binding.progressBarProfile.visibility = View.VISIBLE
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        val email = currentUser.email.toString()

        val docRef = db.collection("users").document(uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val userInfo = documentSnapshot.toObject<User>()
            Glide.with(this).load(userInfo!!.imageUrl.toString()).into(binding.profileImage)
            //binding.profileName.text = userInfo.name
            binding.profileName.text = userInfo.name
            binding.progressBarProfile.visibility = View.INVISIBLE
        }
        binding.buttonEditProfile.setOnClickListener {
            TODO("Edit Profile")

        }
        binding.buttonChangePassword.setOnClickListener {
            binding.progressBarProfile.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.progressBarProfile.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Password Change Link was sent to $email. Please Check your email!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    binding.progressBarProfile.visibility = View.INVISIBLE
                    Toast.makeText(context, it.exception!!.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.buttonSignOut.setOnClickListener {
            binding.progressBarProfile.visibility = View.VISIBLE
            Firebase.auth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

}