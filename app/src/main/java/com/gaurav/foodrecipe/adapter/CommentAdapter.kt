package com.gaurav.foodrecipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.Comment
import com.gaurav.foodrecipe.data.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CommentAdapter(private val commentList: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private var db = Firebase.firestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_item_comment, parent, false)
        return CommentAdapter.CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val currentItem = commentList[position]
        val uid = currentItem.uid
        val docRef = db.collection("users").document("$uid")
        docRef.get().addOnSuccessListener {
            val users = it.toObject<User>()
            holder.userName.text = "${users!!.name}"
            Glide.with(holder.itemView.context).load(users.imageUrl).into(
                holder.image
            )
        }
        holder.userComment.text = currentItem.comment
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    class CommentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.textViewUserName)
        val image: ImageView = itemView.findViewById(R.id.profile_image)
        val userComment: TextView = itemView.findViewById(R.id.textViewCommentData)
    }
}