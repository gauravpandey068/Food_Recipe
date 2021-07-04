package com.gaurav.foodrecipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.data.Post

class PostAdapter(private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return PostViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.title.text = currentItem.title
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textViewTitle)
        val image: ImageView = itemView.findViewById(R.id.imageViewHome)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}