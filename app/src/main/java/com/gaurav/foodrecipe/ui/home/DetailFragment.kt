package com.gaurav.foodrecipe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.adapter.CommentAdapter
import com.gaurav.foodrecipe.data.Comment
import com.gaurav.foodrecipe.databinding.FragmentDetailBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentList: ArrayList<Comment>
    private lateinit var postId: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.progressBarDetail.visibility = View.VISIBLE
        auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        postId = arguments?.getString("postId").toString()
        val userId = arguments?.getString("userId").toString()
        val title = arguments?.getString("title")
        val description = arguments?.getString("description")
        val imageUrl = arguments?.getString("imageUrl")
        val ingredient = arguments?.getString("ingredient")
        val method = arguments?.getString("method")


        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, HomeFragment())
                    commit()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)

        binding.imageViewBackToHome.setOnClickListener {
            setCurrentFragment(HomeFragment())
        }

        if (uid == userId) {
            binding.imageViewMorePost.visibility = View.VISIBLE
            binding.imageViewMorePost.setOnClickListener {
                val popup = PopupMenu(context, binding.imageViewMorePost)
                popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> updatePost(postId, userId)
                        R.id.delete -> deletePost(postId)
                    }
                    true
                }
                popup.show()
            }
        }

        binding.textViewDetailTitle.text = "$title"
        Glide.with(this).load("$imageUrl").into(
            binding.imageViewDetailPic
        )
        binding.textViewDetailDescription.text = "$description"
        binding.textViewIngredientData.text = "$ingredient"
        binding.textViewMethodData.text = "$method"
        binding.progressBarDetail.visibility = View.INVISIBLE

        binding.outlinedButtonPostComment.setOnClickListener {
            val comment = binding.outlinedTextFieldComment.editText?.text.toString()
            if (TextUtils.isEmpty(comment)) {
                binding.outlinedTextFieldComment.error = "Comment Cannot be Empty!"
                binding.outlinedTextFieldComment.editText?.doOnTextChanged { comment, _, _, _ ->
                    binding.outlinedTextFieldComment.error = null
                }
                return@setOnClickListener
            }
            hideKeyboard()
            postComment(comment, uid)

        }
        commentRecyclerView = binding.recyclerViewComment
        commentRecyclerView.layoutManager = LinearLayoutManager(context)
        commentRecyclerView.setHasFixedSize(true)
        commentList = arrayListOf()
        getComment()


        return binding.root
    }

    private fun updatePost(postId: String, userId: String) {
        val bundle = Bundle()
        bundle.putString("postId", postId)
        bundle.putString("userId", userId)
        val fragmentNext = EditPostFragment()
        fragmentNext.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_layout, fragmentNext)
        fragmentTransaction.commit()
    }

    private fun getComment() {
        db.collection("comments").document(postId).collection("comment").get()
            .addOnCompleteListener(
                OnCompleteListener<QuerySnapshot?> {
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            val post = document.toObject<Comment>()
                            commentList.add(post)
                        }
                        val adapter = CommentAdapter(commentList)
                        commentRecyclerView.adapter = adapter

                    } else {
                        Toast.makeText(
                            activity,
                            it.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })

    }

    private fun postComment(comment: String, userId: String) {
        binding.progressBarDetail.visibility = View.VISIBLE

        val inputComment = Comment(
            userId,
            comment
        )
        db.collection("comments").document(postId).collection("comment").add(inputComment)
            .addOnSuccessListener {
                binding.progressBarDetail.visibility = View.INVISIBLE
                binding.outlinedTextFieldComment.editText?.setText(" ")
                commentList.add(inputComment)
                val adapter = CommentAdapter(commentList)
                commentRecyclerView.adapter = adapter

            }.addOnFailureListener { e ->
                binding.progressBarDetail.visibility = View.INVISIBLE
                Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

    }

    private fun deletePost(postId: String) {
        binding.progressBarDetail.visibility = View.VISIBLE
        db.collection("posts").document(postId).delete().addOnCompleteListener {
            db.collection("comments").document(postId).delete()
            Toast.makeText(context, "Post Deleted!", Toast.LENGTH_SHORT).show()
            setCurrentFragment(HomeFragment())
        }.addOnFailureListener {
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_layout, fragment)
            commit()
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
    //\//
    //
    //
    //
}