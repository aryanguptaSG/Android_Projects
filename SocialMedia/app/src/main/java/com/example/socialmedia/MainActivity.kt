package com.example.socialmedia

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmedia.Adapter.IRvAdapter
import com.example.socialmedia.Adapter.PostAdapter
import com.example.socialmedia.Daos.DeletePost
import com.example.socialmedia.Daos.LikePost
import com.example.socialmedia.Model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), IRvAdapter {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var uid:String
    private lateinit var adapter: PostAdapter
    private lateinit var storageReference:StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        auth=Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(maintoolbar)
        uid = Firebase.auth.uid.toString()
        firestore = FirebaseFirestore.getInstance()
        storageReference= FirebaseStorage.getInstance().getReference()


        setUpRecyclerView()



        firestore.collection("Users").document(uid).get().addOnSuccessListener {documentSnapshot ->
            if (documentSnapshot!=null){
                if (documentSnapshot != null) {
                    if (documentSnapshot.getString("Name")!=null && documentSnapshot.getString("Tag Line")!=null &&documentSnapshot.getString("Profile Photo")!=null){
                        username_mainactivity.text= documentSnapshot.getString("Name")
                        tagline_mainactivity.text =  documentSnapshot.getString("Tag Line")
                        if (documentSnapshot.getString("Profile Photo")!= "No Profile"){
                        Glide.with(this).load(documentSnapshot.getString("Profile Photo")).into(profilephoto_mainactivity)
                        }
                    }
                    mainactivityprogressbar.visibility = View.INVISIBLE
                    Log.d("main activity tag", "DocumentSnapshot data: ${documentSnapshot.data}")
                } else {
                    mainactivityprogressbar.visibility = View.INVISIBLE
                    Log.d("main activity tag", "No such document")
                }
            }
        }.addOnFailureListener { exception ->
            mainactivityprogressbar.visibility = View.INVISIBLE
            Log.d("main activity tag", "get failed with ", exception)
        }

        addpostbutton.setOnClickListener {
            startActivity(Intent(this,AddPostActivity::class.java))
            finish()
        }


    }

    private fun setUpRecyclerView() {
        val query = firestore.collection("Posts")

        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

        adapter = PostAdapter(recyclerViewOption,this)

        postrecyclerview.layoutManager = LinearLayoutManager(this)
        postrecyclerview.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.myprofilemenu){
            startActivity(Intent(this,SetUpActivity::class.java))
            finish()
        }
        else if (item.itemId==R.id.signoutmenu){
            auth.signOut()
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
        else if (item.itemId==R.id.deleteacoount){
            startActivity(Intent(this,DeleteAccountActivity::class.java))
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDeleteClicked(id: String) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Are you sure to delete post !!")
        alert.setMessage("after deleting it will no longer remain!!")
        alert.setPositiveButton("Yes", { dialogInterface: DialogInterface?, i: Int ->
            val dao = DeletePost()
            dao.deletepost(id)
            adapter.notifyDataSetChanged()
        })
        alert.setNegativeButton("No",{dialogInterface: DialogInterface?, i: Int ->

        })
        alert.show()
    }

    override fun onliked(id: String) {
        val like = LikePost()
        like.liked(id)
        super.onliked(id)
        adapter.notifyDataSetChanged()
    }
}

