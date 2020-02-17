package isen.CedricLucieFlorent.benfit.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Comment
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showDate
import isen.CedricLucieFlorent.benfit.showUserName
import kotlinx.android.synthetic.main.activity_post.view.*
import kotlinx.android.synthetic.main.recycler_view_comment_cell.view.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*


class CommentAdapter(val comments: ArrayList<Comment>, val clickListener: (Comment) -> Unit): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_comment_cell, parent,false)
        return CommentAdapter.CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, clickListener)
    }


    class CommentViewHolder(val view: View): RecyclerView.ViewHolder(view){

        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(comment: Comment, clickListener: (Comment) -> Unit){
            view.textViewContentComment.text = "${comment.content}"
            showDate(comment.date, view.textViewDateComment)
            showUserName(comment.userid, view.textViewNameComment)
            view.textViewNameComment.setOnClickListener { clickListener(comment) }
            view.imageView.setOnClickListener { clickListener(comment) }
        }
    }

}