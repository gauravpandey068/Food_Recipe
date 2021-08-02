package com.gaurav.foodrecipe.ui.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.User
import com.gaurav.foodrecipe.databinding.FragmentRegisterBinding
import com.gaurav.foodrecipe.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val imagePick = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                try {
                    imageUri = uri
                    binding.profileImage.setImageURI(uri)
                    binding.textViewImageLabel.visibility = View.GONE
                } catch (e: Exception) {
                    Toast.makeText(context, "Choose a Image", Toast.LENGTH_LONG).show()
                }
            })

        binding.loginText.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
        binding.profileImage.setOnClickListener {
            imagePick.launch("image/*")
        }

        binding.outlinedButtonRegister.setOnClickListener {
            val inputTextFieldName = binding.outlinedTextFieldUserName.editText?.text.toString()
            val inputTextFieldEmail =
                binding.outlinedTextFieldEmail.editText?.text.toString().trim { it <= ' ' }
            val inputTextFieldPassword =
                binding.outlinedTextFieldPassword.editText?.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(inputTextFieldName)) {
                binding.outlinedTextFieldUserName.error = "Name cannot be empty!"
                binding.outlinedTextFieldUserName.editText?.doOnTextChanged { inputTextFieldName, _, _, _ ->
                    binding.outlinedTextFieldUserName.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(inputTextFieldEmail)) {
                binding.outlinedTextFieldEmail.error = "Email cannot be empty!"
                binding.outlinedTextFieldEmail.editText?.doOnTextChanged { inputTextFieldEmail, _, _, _ ->
                    binding.outlinedTextFieldEmail.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(inputTextFieldPassword)) {
                binding.outlinedTextFieldPassword.error = "Password cannot be empty!"
                binding.outlinedTextFieldPassword.editText?.doOnTextChanged { inputTextFieldPassword, _, _, _ ->
                    binding.outlinedTextFieldPassword.error = null
                }
                return@setOnClickListener
            }
            registerUser(inputTextFieldEmail, inputTextFieldPassword)
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
                Log.d("register", "image upload $imageUrl")
                uploadUserData(uid)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String) {
        binding.progressBarRegister.visibility = View.VISIBLE
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser
                val uid = user!!.uid

                uploadImage(uid)

                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                binding.progressBarRegister.visibility = View.INVISIBLE
            }

        }
    }

    private fun uploadUserData(uid: String) {
        val db = Firebase.firestore
        val name = binding.outlinedTextFieldUserName.editText?.text.toString()

        val user = User(uid, name, imageUrl)
        db.collection("users").document(uid).set(user).addOnSuccessListener {
            Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}