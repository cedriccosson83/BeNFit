package isen.cedriclucieflorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.adapters.PostAdapter
import isen.cedriclucieflorent.benfit.models.Post
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.*


class FeedActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_feed, frameLayout)

        auth = FirebaseAuth.getInstance()


        showPosts()
        feedPublishBtn.setOnClickListener {
            startActivity(Intent(context, WritePostActivity::class.java))
        }

        recyclerViewFeed.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun showPosts() {
        val myRef = database.getReference("posts")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val posts : ArrayList<Post> = ArrayList()
                for(value in dataSnapshot.children ) {

                    val arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        val userId : String = childLike.value.toString()
                        arrayLikes.add(userId)
                    }

                    val post = Post(
                        value.child("userid").value.toString(),
                        value.child("postid").value.toString(),
                        value.child("date").value.toString(),
                        value.child("content").value.toString(),
                        arrayLikes,
                        value.child("postImgUID").value.toString(),
                        value.child("programId").value.toString(),
                        value.child("sessionId").value.toString(),
                        value.child("exoId").value.toString())
                    posts.add(post)
                }
                posts.reverse()
                recyclerViewFeed.adapter = PostAdapter(posts,
                    { postItem : Post -> userClicked(postItem) },
                    { postItem : Post -> postClicked(postItem) })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })

    }

    private fun userClicked(postItem : Post) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userId", postItem.userid)
        startActivity(intent)
    }

    private fun postClicked(postItem: Post) {
        val intent = Intent(this, PostActivity::class.java)
        val id: String = postItem.postid
        val name: String = textViewName.text.toString()
        intent.putExtra("post", id)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}
