package com.example.socialmedia.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.Model.Post
import com.example.socialmedia.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_set_up.*

class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listener : IRvAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {

    class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val userprofile = itemView.findViewById<ImageView>(R.id.postcard_profie)
        val username = itemView.findViewById<TextView>(R.id.postcard_username)
        val postimage = itemView.findViewById<ImageView>(R.id.postcard_postimage)
        val likecount =  itemView.findViewById<TextView>(R.id.postcard_likecount)
        val likebutton = itemView.findViewById<ImageView>(R.id.postcard_likebutton)
        val caption =  itemView.findViewById<TextView>(R.id.postcard_caption)
        val date =  itemView.findViewById<TextView>(R.id.postcard_datetime)
        val deletepost = itemView.findViewById<ImageView>(R.id.postcard_deletepost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewholder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.postcard, parent,false))
        viewholder.deletepost.setOnClickListener {
            listener.onDeleteClicked(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }
        viewholder.likebutton.setOnClickListener { 
            listener.onliked(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.caption.text = model.Post_caption
        holder.likecount.text = model.Likearray.size.toString()
        holder.date.text = model.Time.toString()
        Glide.with(holder.postimage.context).load(model.Post_image).into(holder.postimage)
        val uid = model.User

        if (uid==Firebase.auth.uid.toString()){
            holder.deletepost.visibility=View.VISIBLE
            holder.deletepost.isClickable = true
        }
        else{
            holder.deletepost.visibility=View.INVISIBLE
            holder.deletepost.isClickable = false

        }

        FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnSuccessListener {documentSnapshot ->
            if (documentSnapshot!=null){
                if (documentSnapshot != null) {
                    if (documentSnapshot.getString("Name")!=null && documentSnapshot.getString("Tag Line")!=null &&documentSnapshot.getString("Profile Photo")!=null){

                        holder.username.text =  documentSnapshot.getString("Name")
                        if (documentSnapshot.getString("Profile Photo")!= "No Profile"){
                            Glide.with(holder.userprofile.context).load(documentSnapshot.getString("Profile Photo")).into(holder.userprofile)
                        }
                    }
                    Log.d("Post Adapter tag", "DocumentSnapshot data: ${documentSnapshot.data}")
                } else {
                    Log.d("Post Adapter tag", "No such document")
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("Post Adapter tag", "get failed with ", exception)
        }


    }




}

interface IRvAdapter{

    fun onDeleteClicked(id: String) {

    }

    fun onliked(id: String) {

    }
}