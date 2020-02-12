package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.CommentAdapter
import isen.CedricLucieFlorent.benfit.Models.Comment
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.accessFeedBTN
import kotlinx.android.synthetic.main.activity_post.nameProfile
import kotlinx.android.synthetic.main.recycler_view_post_cell.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        auth = FirebaseAuth.getInstance()

        val intent = intent
        if (intent != null) {
            showPost(intent)

        }

        accessNewBTN.setOnClickListener{
            startActivity(Intent(this, WritePostActivity::class.java))
        }

        accessFeedBTN.setOnClickListener{
            startActivity(Intent(this, FeedActivity::class.java))
        }
        accessProfileBTN.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            val id = auth.currentUser?.uid
            intent.putExtra("userId", id)
            startActivity(intent)
        }
        recyclerViewComments.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerViewComments.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val postId: String = intent.getStringExtra("post")
        showComments(postId)


        buttonPublishComment.setOnClickListener {
            var content:String = editTextComment.text.toString()
            if(content != ""){
                newComment(postId,content)
                editTextComment.setText("")
                Toast.makeText(this, "Commentaire posté!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Veuillez entrer un commentaire", Toast.LENGTH_LONG).show()
            }

        }

    }

    //This function allows to show the name of the user
    fun showPost(intent : Intent) {

        val myRef = database.getReference("posts")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var post: Post
                for(value in dataSnapshot.children ) {
                    var likes : ArrayList<String> = ArrayList()
                    post = Post(value.child("userid").value.toString(), value.child("postid").value.toString(), value.child("date").value.toString(), value.child("content").value.toString(),likes)
                    val postId: String = intent.getStringExtra("post")
                    if(post.postid == postId){
                        textViewContent2.text = "${post.content}"
                        showUserName(post.userid, nameProfile)
                        showDate(post.date, textViewDate2)
                        nameProfile.setOnClickListener {
                            redirectToUserActivity(this@PostActivity, post.userid)
                        }
                        imageViewUser.setOnClickListener {
                            redirectToUserActivity(this@PostActivity, post.userid)
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


                    var comment : Comment = Comment(
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
                recyclerViewComments.adapter = CommentAdapter(comments,{ commentItem : Comment -> userClicked(commentItem) })
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
        Toast.makeText(this, "Clicked: ${commentItem.userid}", Toast.LENGTH_LONG).show()
        redirectToUserActivity(this,commentItem.userid)
    }


}
