package com.gaurav.foodrecipe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.adapter.PostAdapter
import com.gaurav.foodrecipe.data.Post
import com.gaurav.foodrecipe.databinding.FragmentSearchBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.editTextSearch.requestFocus()
        if(binding.editTextSearch.requestFocus())
        {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fl_layout, HomeFragment())
                    commit()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callBack)

        binding.imageViewSearchBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_layout, HomeFragment())
                commit()
            }
        }
        binding.buttonSearch.setOnClickListener {
            val keyword = binding.editTextSearch.text.toString()
            hideKeyboard()
            postList.clear()
            getDataFromFirestore(keyword.lowercase())
        }
        postRecyclerView = binding.recycleViewSearch

        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)
        postList = arrayListOf()

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    private fun getDataFromFirestore(keyword: String) {
        binding.progressBarSearch.visibility = View.VISIBLE
        val db = Firebase.firestore
        db.collection("posts").get().addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBarSearch.visibility = View.INVISIBLE

                for (document in it.result!!) {
                    binding.progressBarSearch.visibility = View.INVISIBLE
                    val post = document.toObject<Post>()
                    if (post.title!!.lowercase().contains(keyword)) {
                        postList.add(post)
                    }
                }
                if (postList.isEmpty()) {
                    binding.textViewNoResultFound.visibility = View.VISIBLE
                    binding.textViewNoResultFound.text = "No Result Found"
                }
                val adapter = PostAdapter(postList)
                postRecyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : PostAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val bundle = Bundle()
                        bundle.putString("postId", postList[position].postId)
                        bundle.putString("userId", postList[position].uid)
                        bundle.putString("title", postList[position].title)
                        bundle.putString("description", postList[position].description)
                        bundle.putString("imageUrl", postList[position].imageUrl)
                        bundle.putString("ingredient", postList[position].ingredient)
                        bundle.putString("method", postList[position].method)
                        val fragmentNext = DetailFragment()
                        fragmentNext.arguments = bundle
                        val fragmentManager = activity!!.supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fl_layout, fragmentNext)
                        fragmentTransaction.commit()
                    }
                })

            } else {
                Toast.makeText(activity, it.exception!!.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
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