package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.ProgramFeed
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_feed_program.view.*

class ProgramFeedAdapter (private val programs: ArrayList<ProgramFeed>,
                          private val clickListenersubscribe: (ProgramFeed) -> Unit,
                          private val clickListenerlike: (ProgramFeed) -> Unit)
    : RecyclerView.Adapter<ProgramFeedAdapter.ProgramViewHolder>(){

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    val follow = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_program, parent, false)
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program,clickListenersubscribe, clickListenerlike)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class ProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        val database = FirebaseDatabase.getInstance()


        fun countLikes(program: ProgramFeed) {
            var array: ArrayList<String> = program.likes
            var count: Int = array.size
            view.NbLikeProgram.text = "${count}"
        }

        fun showLike(program: ProgramFeed) {
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
            showLike(program)
            countLikes(program)
        }
    }


}