package com.gaurav.foodrecipe.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.User
import com.gaurav.foodrecipe.databinding.FragmentEditProfileBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        val userId = arguments?.getString("userId")
        imageUrl = arguments?.getString("imageUrl").toString()
        val username = arguments?.getString("userName")

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, ProfileFragment())
                    commit()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)
        binding.progressBarEditProfile.isVisible = true

        val imagePick = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                imageUri = uri
                binding.updateProfileImage.setImageURI(uri)
            }
        )

        Glide.with(this).load(imageUrl).into(binding.updateProfileImage)
        binding.editTextTextPersonName.setText(username)
        binding.progressBarEditProfile.isVisible = false


        binding.updateProfileImage.setOnClickListener {
           try{
               imagePick.launch("image/*")
           }catch (e:Exception){
               Toast.makeText(context, "Choose A Image", Toast.LENGTH_LONG).show()
           }
        }

        binding.buttonUpdateProfile.setOnClickListener {
            binding.progressBarEditProfile.isVisible = true
            uploadImage(userId.toString())
        }

        return binding.root
    }

    private fun uploadImage(uid: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference

        storageReference = storageRef.child("ProfilePics/$uid")
        storageReference.putFile(imageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                uploadUserData(uid)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadUserData(uid: String) {
        val db = Firebase.firestore
        val name = binding.editTextTextPersonName.text.toString()

        val user = User(uid, name, imageUrl)
        db.collection("users").document(uid).set(
            user,
            SetOptions.merge()
        ).addOnSuccessListener {
            Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
            binding.progressBarEditProfile.isVisible = false
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fl_layout, ProfileFragment())
            fragmentTransaction.commit()
        }.addOnFailureListener { e ->
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            binding.progressBarEditProfile.isVisible = false
        }
    }

}