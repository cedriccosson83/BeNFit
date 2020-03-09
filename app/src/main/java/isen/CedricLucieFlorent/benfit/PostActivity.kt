package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.CommentAdapter
import isen.CedricLucieFlorent.benfit.Models.Comment
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_post.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_post, frameLayout)
        auth = FirebaseAuth.getInstance()

        val intent = intent
        if (intent != null) {
            showPost(intent)
        }

        recyclerViewComments.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerViewComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val postId: String? = intent.getStringExtra("post") ?: ""
        postId?.let { showComments(it) }



        buttonPublishComment.setOnClickListener {
            val content:String = editTextComment.text.toString()
            if(content != ""){
                postId?.let { it1 -> newComment(it1,content) }
                editTextComment.setText("")
            }else{
                Toast.makeText(this, "Veuillez entrer un commentaire", Toast.LENGTH_LONG).show()
            }

        }

    }

    //This function allows to show the name of the user
    private fun showPost(intent : Intent) {

        val myRef = database.getReference("posts")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var post: Post
                for(value in dataSnapshot.children ) {
                    val likes : ArrayList<String> = ArrayList()
                    post = Post(
                        value.child("userid").value.toString(),
                        value.child("postid").value.toString(),
                        value.child("date").value.toString(),
                        value.child("content").value.toString(),
                        likes,
                        value.child("postImgUID").value.toString(),
                        value.child("programId").value.toString(),
                        value.child("sessionId").value.toString(),
                        value.child("exoId").value.toString())
                    val postId: String? = intent.getStringExtra("post") ?: ""
                    if(post.postid == postId){
                        textViewContent2.text = "${post.content}"
                        showUserName(post.userid, nameProfile)
                        showDate(post.date, textViewDate2)
                        showUserNameImage(post.userid, nameProfile, imageViewUserPostAct)
                        nameProfile.setOnClickListener {
                            redirectToUserActivity(this@PostActivity, post.userid)
                        }
                        imageViewUserPostAct.setOnClickListener {
                            redirectToUserActivity(this@PostActivity, post.userid)
                        }

                        if (post.programId != "" && post.programId != "null") {
                            buttonLink.setOnClickListener {
                                val sharedLinkIntent = Intent(ApplicationContext.applicationContext(), ShowProgramActivity::class.java)
                                sharedLinkIntent.putExtra("programId", post.programId)
                                sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ApplicationContext.applicationContext().startActivity(sharedLinkIntent)
                            }
                        } else if (post.sessionId != "" && post.sessionId != "null") {
                            buttonLink.setOnClickListener {
                                val sharedLinkIntent = Intent(ApplicationContext.applicationContext(), ShowSessionActivity::class.java)
                                sharedLinkIntent.putExtra("sessionId", post.sessionId)
                                sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ApplicationContext.applicationContext().startActivity(sharedLinkIntent)
                            }
                        } else if (post.exoId != "" && post.exoId != "null") {
                            buttonLink.setOnClickListener{
                                showPopUpExercice(database, it.context, post.exoId, windowManager)
                            }
                        } else {
                            buttonLink.visibility = View.INVISIBLE
                        }
                        break
                    }


                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })
    }


    fun showComments(postId: String) {

        val myRef = database.getReference("comments")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val comments : ArrayList<Comment> = ArrayList()
                for(value in dataSnapshot.children ) {


                    val comment = Comment(
                        value.child("userid").value.toString(),
                        value.child("parentid").value.toString(),
                        value.child("date").value.toString(),
                        value.child("content").value.toString(),
                        ArrayList()
                    )

                    if(comment.parentid == postId){
                        comments.add(comment)
                    }

                }
                //comments.reverse()
                recyclerViewComments.adapter = CommentAdapter(comments) { commentItem : Comment -> userClicked(commentItem) }
                Log.d("comment", comments.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("comment", "Failed to read value.", error.toException())
            }
        })


    }

    private fun newComment(postId: String,content: String){
        val dbComments = database.getReference("comments")
        val newId = dbComments.push().key
        val currentUserID = auth.currentUser?.uid

        if (newId == null) {
            Log.w("ERROR", "Couldn't get push key for comments")
            return
        }
        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())

        val comment = currentUserID?.let { Comment(it, postId, currentDateandTime, content, ArrayList()) }
        dbComments.child(newId).setValue(comment)
    }

    //allows to redirect on the user activity
    private fun userClicked(commentItem : Comment) {
        redirectToUserActivity(this,commentItem.userid)
    }


}
