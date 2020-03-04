package isen.CedricLucieFlorent.benfit.Adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Post
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showDate
import isen.CedricLucieFlorent.benfit.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*
import kotlin.collections.ArrayList
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity


class PostAdapter(val posts: ArrayList<Post>, val clickListener: (Post) -> Unit, val clickListenerPost: (Post) -> Unit, val clickListenerLike: (Post) -> Unit): RecyclerView.Adapter<PostAdapter.PostViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_post_cell, parent, false)
        return PostAdapter.PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.count()
    }

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post,clickListener, clickListenerPost, clickListenerLike)
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun countLikes(post: Post) {
            var array: ArrayList<String> = post.likes
            var count: Int = array.size
            view.textViewLikeNumberPost.text="${count}"
            Log.d("like", array.toString())
        }

        fun countComments(postId: String) {

            val myRef = database.getReference("comments")
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var count: Int = 0
                    for (value in dataSnapshot.children) {
                        val retrievedPostId = value.child("parentid").value.toString()
                        if (retrievedPostId == postId)
                            count++
                    }
                    view.textViewCommentsNumberPost.text = "${count}"

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("comment", "Failed to read value.", error.toException())
                }
            })


        }

        fun showLike(post: Post){
            auth = FirebaseAuth.getInstance()
            val currentUserID = auth.currentUser?.uid
            val likes = post.likes
            Log.d("like", likes.toString())
            if(likes.all { it != currentUserID }) {
                //likes.add(currentUserID ?: "")
                view.btnLikePost.setImageResource(R.drawable.like)
            }else{
                //likes.remove(currentUserID)
                view.btnLikePost.setImageResource(R.drawable.dislike)

            }
        }

        fun bind(post: Post, clickListener: (Post) -> Unit, clickListenerPost: (Post) -> Unit, clickListenerLike: (Post) -> Unit){
            //view.textViewName.text = "${post.userid}"
            view.textViewContent.text = "${post.content}"
            showDate(post.date, view.textViewDate)
            view.textViewName.setOnClickListener { clickListener(post) }
            view.imageViewUserPost.setOnClickListener { clickListener(post) }
            view.btnCommentExo.setOnClickListener {clickListenerPost(post) }
            view.btnLikePost.setOnClickListener { clickListenerLike(post) }
            val imgView = view.imageViewUserPost
            showUserNameImage(post.userid, view.textViewName, imgView)

            if(post.postImgUID != "null"){
                val layout = view.layoutImgPost
                val postImView = ImageView(ApplicationContext.applicationContext())
                setImageFromFirestore(ApplicationContext.applicationContext(),postImView, "posts/${post.postid}/${post.postImgUID}")

                layout.addView(postImView)
                postImView.layoutParams.height = 400

                postImView.setOnClickListener {
                    val fullScreenIntent = Intent(ApplicationContext.applicationContext(), FullScreenImageView::class.java)
                    fullScreenIntent.putExtra("url", "posts/${post.postid}/${post.postImgUID}")
                    fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ApplicationContext.applicationContext().startActivity(fullScreenIntent)
                }
            }
            showLike(post)
            countComments(post.postid)
            countLikes(post)
        }
    }


}