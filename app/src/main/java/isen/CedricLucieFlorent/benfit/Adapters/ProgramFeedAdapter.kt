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
import isen.CedricLucieFlorent.benfit.Models.ProgramFeed
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_feed_program.view.*

class ProgramFeedAdapter (val programs: ArrayList<ProgramFeed>, val clickListenersubscribe: (ProgramFeed) -> Unit, val clickListenerlike: (ProgramFeed) -> Unit): RecyclerView.Adapter<ProgramFeedAdapter.ProgramViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramFeedAdapter.ProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_program, parent, false)
        return ProgramFeedAdapter.ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramFeedAdapter.ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program,clickListenersubscribe, clickListenerlike)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class ProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun countLikes(program: ProgramFeed) {
            var array: ArrayList<String> = program.likes

            var count: Int = array.size
            view.NbLikeProgram.text = "${count}"

        }

        fun showLike(program: ProgramFeed) {
            auth = FirebaseAuth.getInstance()
            val currentUserID = auth.currentUser?.uid
            val likes = program.likes
            if (likes.all { it != currentUserID }) {
                view.btnLikeProgram.setImageResource(R.drawable.like)
            } else {
                view.btnLikeProgram.setImageResource(R.drawable.dislike)

            }
        }
        fun bind(program: ProgramFeed, clickListenersubscribe: (ProgramFeed) -> Unit, clickListenerlike: (ProgramFeed) -> Unit) {
            view.nameProgTextView.text = program.nameProgramFeed
            view.descriptionProgTextView.text = program.descrProgramFeed
            view.btnSubscribeProg.setOnClickListener { clickListenersubscribe(program) }
            view.btnLikeProgram.setOnClickListener { clickListenerlike(program) }
            //showUserNameSessionFeed(program.userID, view.authorSessionFeed)
            //view.levelSessionFeed.text = "Niveau " + session.levelSession
            showLike(program)
            countLikes(program)
        }
    }


}