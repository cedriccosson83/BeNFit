package isen.CedricLucieFlorent.benfit.Adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
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


class PostAdapter(val posts: ArrayList<Post>, val windowManager: WindowManager, val clickListener: (Post) -> Unit, val clickListenerPost: (Post) -> Unit): RecyclerView.Adapter<PostAdapter.PostViewHolder>(){
    lateinit var auth: FirebaseAuth

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
        holder.bind(post, windowManager, clickListener, clickListenerPost)
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

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


        fun bind(post: Post, windowManager: WindowManager, clickListener: (Post) -> Unit, clickListenerPost: (Post) -> Unit){
            //view.textViewName.text = "${post.userid}"
            view.textViewContent.text = "${post.content}"
            showDate(post.date, view.textViewDate)
            view.textViewName.setOnClickListener { clickListener(post) }
            view.imageViewUserPost.setOnClickListener { clickListener(post) }
            view.btnCommentExo.setOnClickListener {clickListenerPost(post) }
            view.btnLikePost.setOnClickListener {
                likesHandler(database, auth.currentUser?.uid, "posts/${post.postid}/likes",post.likes, view.btnLikePost)
            }
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
            showLikes(database, auth.currentUser?.uid, "posts/${post.postid}/likes",view.textViewLikeNumberPost, view.btnLikePost)
            countComments(post.postid)

            if (post.programId != "") {
                view.btnShareInFeed.setOnClickListener {
                    val sharedLinkIntent = Intent(ApplicationContext.applicationContext(), ShowProgramActivity::class.java)
                    sharedLinkIntent.putExtra("programId", post.programId)
                    sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ApplicationContext.applicationContext().startActivity(sharedLinkIntent)
                }
            } else if (post.sessionId != "") {
                view.btnShareInFeed.setOnClickListener {
                    val sharedLinkIntent = Intent(ApplicationContext.applicationContext(), ShowSessionActivity::class.java)
                    sharedLinkIntent.putExtra("sessionId", post.sessionId)
                    sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ApplicationContext.applicationContext().startActivity(sharedLinkIntent)
                }
            } else if (post.exoId != "") {
                view.btnShareInFeed.setOnClickListener{
                    showPopUpExercice(database, it.context, post.exoId, windowManager)
                }
            } else {
                view.btnShareInFeed.visibility = View.INVISIBLE
            }
        }
    }


}