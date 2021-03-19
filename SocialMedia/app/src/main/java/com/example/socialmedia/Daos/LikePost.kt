package com.example.socialmedia.Daos

import com.example.socialmedia.Model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LikePost {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth:FirebaseAuth
    public fun liked(postid:String){
        auth=Firebase.auth
        firestore= FirebaseFirestore.getInstance()
       firestore.collection("Posts").document(postid).get().addOnSuccessListener {DocumentSnapshot->
           val uid = auth.uid.toString()
           val post = DocumentSnapshot.toObject(Post::class.java)
           val isliked = post!!.Likearray.contains(uid)

           if (isliked){
               post.Likearray.remove(uid)
           }
           else{
               post.Likearray.add(uid)
           }
           firestore.collection("Posts").document(postid).set(post)

       }
    }
}