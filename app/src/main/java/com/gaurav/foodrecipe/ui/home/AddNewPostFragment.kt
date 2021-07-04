package com.gaurav.foodrecipe.ui.home

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.Post
import com.gaurav.foodrecipe.databinding.FragmentAddNewPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*


class AddNewPostFragment : Fragment() {

    private lateinit var binding: FragmentAddNewPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String
    private lateinit var storageReference: StorageReference

    private lateinit var title: String
    private lateinit var description: String
    private lateinit var ingredient: String
    private lateinit var method: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_post, container, false)
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, HomeFragment())
                    commit()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)

        //image choose form gallery
        val imagePick = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                imageUri = uri
                binding.imageViewNewPost.setImageURI(uri)
                binding.textViewImageLabelAddPost.visibility = View.GONE
            }
        )
        binding.imageViewNewPost.setOnClickListener {
            imagePick.launch("image/*")
        }

        binding.ButtonPost.setOnClickListener {
            hideKeyboard()
            //check empty
            title = binding.outlinedTextFieldTitle.editText?.text.toString().replaceFirstChar { it.titlecaseChar() }
            description = binding.outlinedTextFieldDescription.editText?.text.toString().replaceFirstChar { it.titlecaseChar() }
            ingredient = binding.outlinedTextFieldIngredient.editText?.text.toString().replaceFirstChar { it.titlecaseChar() }
            method = binding.outlinedTextFieldMethods.editText?.text.toString().replaceFirstChar { it.titlecaseChar() }

            if (TextUtils.isEmpty(title)) {
                binding.outlinedTextFieldTitle.error = "Food Recipe Name cannot be Empty!"
                binding.outlinedTextFieldTitle.editText?.doOnTextChanged { title, _, _, _ ->
                    binding.outlinedTextFieldTitle.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(description)) {
                binding.outlinedTextFieldDescription.error = "Food Description cannot be Empty!"
                binding.outlinedTextFieldDescription.editText?.doOnTextChanged { description, _, _, _ ->
                    binding.outlinedTextFieldTitle.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(ingredient)) {
                binding.outlinedTextFieldIngredient.error = "Food Ingredients cannot be Empty!"
                binding.outlinedTextFieldIngredient.editText?.doOnTextChanged { ingredient, _, _, _ ->
                    binding.outlinedTextFieldIngredient.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(method)) {
                binding.outlinedTextFieldMethods.error = "Methods cannot be Empty!"
                binding.outlinedTextFieldMethods.editText?.doOnTextChanged { method, _, _, _ ->
                    binding.outlinedTextFieldMethods.error = null
                }
                return@setOnClickListener
            }
            uploadImage()
        }

        return binding.root
    }

    private fun uploadData() {
        val db = Firebase.firestore
        auth = Firebase.auth
        val postId = title.substringBefore(" ") + "-" + UUID.randomUUID()
        val uid = auth.currentUser!!.uid
        val post = Post(
            postId,
            uid,
            title,
            imageUrl,
            description,
            ingredient,
            method
        )
        db.collection("posts").document(postId).set(post).addOnSuccessListener {
            Toast.makeText(context, "Upload Successfully", Toast.LENGTH_SHORT).show()
            binding.progressBarPost.visibility = View.INVISIBLE
            val bundle = Bundle()
            bundle.putString("postId", postId)
            bundle.putString("userId", uid)
            bundle.putString("title", title)
            bundle.putString("description",description)
            bundle.putString("imageUrl", imageUrl)
            bundle.putString("ingredient", ingredient)
            bundle.putString("method", method)
            val fragmentNext = DetailFragment()
            fragmentNext.arguments = bundle
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fl_layout, fragmentNext)
            fragmentTransaction.commit()

        }.addOnFailureListener {
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
            binding.progressBarPost.visibility = View.INVISIBLE

        }

    }

    private fun uploadImage() {
        binding.progressBarPost.visibility = View.VISIBLE
        val storage = Firebase.storage
        val storageRef = storage.reference

        storageReference = storageRef.child("PostImages/${UUID.randomUUID()}")
        storageReference.putFile(imageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                uploadData()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            binding.progressBarPost.visibility = View.INVISIBLE

        }
    }

    //
    //To hide keyboard
    //
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    //
    //
    //

}