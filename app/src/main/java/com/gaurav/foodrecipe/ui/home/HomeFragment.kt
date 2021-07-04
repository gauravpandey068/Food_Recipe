package com.gaurav.foodrecipe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.adapter.PostAdapter
import com.gaurav.foodrecipe.data.Post
import com.gaurav.foodrecipe.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.progressBarHome.visibility = View.VISIBLE

        postRecyclerView = binding.recycleViewHome

        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)
        postList = arrayListOf()
        getDataFromFirestore()
        return binding.root
    }

    private fun getDataFromFirestore() {
        val db = Firebase.firestore
        db.collection("posts").get().addOnCompleteListener{
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    binding.progressBarHome.visibility = View.INVISIBLE
                    val post = document.toObject<Post>()
                    postList.add(post)
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

}