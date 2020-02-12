package isen.CedricLucieFlorent.benfit.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Comment
import isen.CedricLucieFlorent.benfit.Models.Post
import isen.CedricLucieFlorent.benfit.Models.User
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showDate
import isen.CedricLucieFlorent.benfit.showUserName
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*
import kotlin.collections.ArrayList


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
            view.textViewLikeNumber.text="Likes(${count})"
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
                    view.textViewCommentsNumber.text = "Commentaires(${count})"

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
            if(likes?.all { it != currentUserID } == true) {
                //likes.add(currentUserID ?: "")
                view.imageViewStar.setImageResource(R.drawable.like)
            }else{
                //likes.remove(currentUserID)
                view.imageViewStar.setImageResource(R.drawable.dislike)

            }
        }

        fun bind(post: Post, clickListener: (Post) -> Unit, clickListenerPost: (Post) -> Unit, clickListenerLike: (Post) -> Unit){
            //view.textViewName.text = "${post.userid}"
            view.textViewContent.text = "${post.content}"
            showDate(post.date, view.textViewDate)
            view.textViewName.setOnClickListener { clickListener(post) }
            view.imageViewUserPost.setOnClickListener { clickListener(post) }
            view.cardViewPost.setOnClickListener {clickListenerPost(post) }
            view.imageViewStar.setOnClickListener { clickListenerLike(post) }
            showUserName(post.userid, view.textViewName)
            showLike(post)
            countComments(post.postid)
            countLikes(post)
        }
    }


}