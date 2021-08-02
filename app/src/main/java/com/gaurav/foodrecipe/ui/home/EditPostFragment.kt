package com.gaurav.foodrecipe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.Post
import com.gaurav.foodrecipe.databinding.FragmentEditPostBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class EditPostFragment : Fragment() {
    private lateinit var binding: FragmentEditPostBinding
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_post, container, false)
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, DetailFragment())
                    commit()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)

        val postId = arguments?.getString("postId").toString()
        val userId = arguments?.getString("userId").toString()
        lateinit var imageUrl: String
        binding.progressBarEditPost.isVisible = true

        db.collection("posts").document(postId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBarEditPost.isVisible = false
                val post = it.result!!.toObject<Post>()
                imageUrl = post!!.imageUrl.toString()
                fillData(post)
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.ButtonEdit.setOnClickListener {
            val title = binding.outlinedTextFieldTitleEdit.editText?.text.toString()
                .replaceFirstChar { it.titlecaseChar() }
            val description = binding.outlinedTextFieldDescriptionEdit.editText?.text.toString()
                .replaceFirstChar { it.titlecaseChar() }
            val ingredient = binding.outlinedTextFieldIngredientEdit.editText?.text.toString()
                .replaceFirstChar { it.titlecaseChar() }
            val method = binding.outlinedTextFieldMethodsEdit.editText?.text.toString()
                .replaceFirstChar { it.titlecaseChar() }
            updatePost(postId, userId, title, imageUrl, description, ingredient, method)

        }


        return binding.root
    }

    private fun updatePost(
        postId:String,
        userId: String,
        title: String,
        imageUrl: String,
        description: String,
        ingredient: String,
        method: String
    ) {
        binding.progressBarEditPost.isVisible = true
        val post = Post(
            postId,
            userId,
            title,
            imageUrl,
            description,
            ingredient,
            method
        )
        db.collection("posts").document(postId).set(
            post,
            SetOptions.merge()
        ).addOnSuccessListener {
            binding.progressBarEditPost.isVisible = false
            val bundle = Bundle()
            bundle.putString("postId", postId)
            bundle.putString("userId", userId)
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

        }.addOnFailureListener{
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
            binding.progressBarEditPost.isVisible = false
        }
    }

    private fun fillData(post: Post) {
        binding.outlinedTextFieldTitleEdit.editText?.setText(post.title)
        Glide.with(this).load("${post.imageUrl}").into(
            binding.imageViewEditPost
        )
        binding.outlinedTextFieldDescriptionEdit.editText?.setText(post.description)
        binding.outlinedTextFieldIngredientEdit.editText?.setText(post.ingredient)
        binding.outlinedTextFieldMethodsEdit.editText?.setText(post.method)

    }

}