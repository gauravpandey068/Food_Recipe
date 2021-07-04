package com.gaurav.foodrecipe.data

data class Post(
    val postId: String?=null,
    val uid:String?=null,
    val title: String?=null,
    val imageUrl: String?=null,
    val description: String?=null,
    val ingredient: String?=null,
    val method: String?=null
)
