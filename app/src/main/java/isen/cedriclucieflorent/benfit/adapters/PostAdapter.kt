package isen.cedriclucieflorent.benfit.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.models.Post
import isen.cedriclucieflorent.benfit.R
import isen.cedriclucieflorent.benfit.*
import isen.cedriclucieflorent.benfit.functions.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*
import kotlin.collections.ArrayList

class PostAdapter(
        private val posts: ArrayList<Post>,
        private val clickListener: (Post) -> Unit,
        private val clickListenerPost: (Post) -> Unit)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>(){
    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_post_cell, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.count()
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post, clickListener, clickListenerPost)
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        private fun countComments(postId: String) {

            val myRef = database.getReference("comments")
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var count = 0
                    for (value in dataSnapshot.children) {
                        val retrievedPostId = value.child("parentid").value.toString()
                        if (retrievedPostId == postId)
                            count++
                    }
                    view.textViewCommentsNumberPost.text = count.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("comment", "Failed to read value.", error.toException())
                }
            })


        }


        fun bind(post: Post, clickListener: (Post) -> Unit, clickListenerPost: (Post) -> Unit){
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
                setImageFromFirestore(postImView, "posts/${post.postid}/${post.postImgUID}")

                layout.addView(postImView)
                postImView.layoutParams.height = 400

                postImView.setOnClickListener {
                    fullScreenImage(ApplicationContext.applicationContext(), "posts/${post.postid}/${post.postImgUID}" )
                }
            }
            showLikes(database, auth.currentUser?.uid, "posts/${post.postid}/likes",view.textViewLikeNumberPost, view.btnLikePost)
            countComments(post.postid)

            if (post.programId != "" && post.programId != "null") {
                view.btnShareInFeed.setOnClickListener {linkToProgram(ApplicationContext.applicationContext(),post.programId)}
            } else if (post.sessionId != "" && post.sessionId != "null") {
                view.btnShareInFeed.setOnClickListener  {linkToSession(ApplicationContext.applicationContext(), post.sessionId)}
            } else if (post.exoId != "" && post.exoId != "null") {
                view.btnShareInFeed.setOnClickListener{showPopUpExercice(database, it.context, post.exoId)}
            } else {
                view.btnShareInFeed.visibility = View.INVISIBLE
            }
        }
    }


}