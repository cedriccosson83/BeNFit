package isen.cedriclucieflorent.benfit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.cedriclucieflorent.benfit.functions.showDate
import isen.cedriclucieflorent.benfit.functions.showUserNameImage
import isen.cedriclucieflorent.benfit.models.Comment
import isen.cedriclucieflorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_comment_cell.view.*


class CommentAdapter(
        private val comments: ArrayList<Comment>,
        private val clickListener: (Comment) -> Unit)
    : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_comment_cell, parent,false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, clickListener)
    }


    class CommentViewHolder(val view: View): RecyclerView.ViewHolder(view){

        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(comment: Comment, clickListener: (Comment) -> Unit){
            view.textViewContentComment.text = "${comment.content}"
            showDate(comment.date, view.textViewDateComment)
            val imgView = view.profileImageViewComment
            showUserNameImage(comment.userid, view.textViewNameComment, imgView)
            view.textViewNameComment.setOnClickListener { clickListener(comment) }
            view.profileImageViewComment.setOnClickListener { clickListener(comment) }
        }
    }

}