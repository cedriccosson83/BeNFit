package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.PostAdapter
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.*


class FeedActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_feed, frameLayout)

        auth = FirebaseAuth.getInstance()


        showPosts()
        recyclerViewFeed.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    //This function get the posts on the database and show them on the feed
    fun showPosts() {

        val myRef = database.getReference("posts")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val posts : ArrayList<Post> = ArrayList<Post>()
                for(value in dataSnapshot.children ) {

                    var arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        var userId : String = childLike.value.toString()
                        arrayLikes.add(userId)
                    }

                    var post : Post = Post(value.child("userid").value.toString(), value.child("postid").value.toString(), value.child("date").value.toString(), value.child("content").value.toString(),arrayLikes, value.child("postImgUID").value.toString())
                    posts.add(post)
                }
                posts.reverse()
                recyclerViewFeed.adapter = PostAdapter(posts,  { postItem : Post -> userClicked(postItem) }, { postItem : Post -> postClicked(postItem) },{ post : Post -> postLiked(post) } )
                Log.d("post", posts.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })

    }

    //allows to redirect on the user activity
    private fun userClicked(postItem : Post) {
        val intent = Intent(this, ProfileActivity::class.java)
        val id = auth.currentUser?.uid
        intent.putExtra("userId", id)
        startActivity(intent)
    }

    private fun postLiked(postItem : Post) {

        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid

        val myRef = database.getReference("posts")

        val likes = postItem.likes
        Log.d("like", likes.toString())
        if(likes.all { it != currentUserID }) {
            likes.add(currentUserID ?: "")
            myRef.child(postItem.postid).child("likes").setValue(likes)
        }else{
            likes.remove(currentUserID)
            myRef.child(postItem.postid).child("likes").setValue(likes)
        }
    }

    //allow to redirect on the post activity
    private fun postClicked(postItem: Post) {
        val intent = Intent(this, PostActivity::class.java)
        var id: String = postItem.postid
        var name: String = textViewName.text.toString()
        intent.putExtra("post", id)
        intent.putExtra("name", name)
        //intent.putExtra("post", post)
        startActivity(intent)
    }


}
