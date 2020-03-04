package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.ProgramFeed
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.likesHandler
import isen.CedricLucieFlorent.benfit.showLikes
import kotlinx.android.synthetic.main.recycler_view_feed_program.view.*

class ProgramFeedAdapter (val programs: ArrayList<ProgramFeed>,
                          val clickListenersubscribe: (ProgramFeed) -> Unit,
                          val clickListenerProgram: (ProgramFeed) -> Unit
): RecyclerView.Adapter<ProgramFeedAdapter.ProgramViewHolder>(){

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
        holder.bind(program,clickListenersubscribe, clickListenerProgram)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class ProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        val database = FirebaseDatabase.getInstance()

        fun bind(program: ProgramFeed, clickListenersubscribe: (ProgramFeed) -> Unit,
                 clickListenerProgram: (ProgramFeed) -> Unit) {
            view.nameProgTextView.text = program.nameProgramFeed
            view.descriptionProgTextView.text = program.descrProgramFeed
            view.btnSubscribeProg.setOnClickListener { clickListenersubscribe(program) }
            view.btnLikeProgram.setOnClickListener {
                likesHandler(database,auth.currentUser?.uid, "programs/${program.programID}/likes",program.likes, view.btnLikeProgram)
            }
            view.nameProgTextView.setOnClickListener { clickListenerProgram(program) }
            view.descriptionProgTextView.setOnClickListener { clickListenerProgram(program) }
            showLikes(database, currentUserID, "programs/${program.programID}/likes",view.NbLikeProgram, view.btnLikeProgram)
        }
    }


}