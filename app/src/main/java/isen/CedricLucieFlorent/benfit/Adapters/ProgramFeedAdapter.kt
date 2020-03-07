package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.*
import isen.CedricLucieFlorent.benfit.Models.ProgramFeed
import kotlinx.android.synthetic.main.recycler_view_feed_program.view.*
import kotlinx.android.synthetic.main.recycler_view_feed_session.view.*

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
            var img = program.imgURI
            setImageFromFirestore(ApplicationContext.applicationContext(), view.imageViewFeedProg, "programs/${program.programID}/${img}")
            view.btnLikeProgram.setOnClickListener {
                likesHandler(database,auth.currentUser?.uid, "programs/${program.programID}/likes",program.likes, view.btnLikeProgram)
            }
            view.nameProgTextView.setOnClickListener { clickListenerProgram(program) }
            view.descriptionProgTextView.setOnClickListener { clickListenerProgram(program) }
            showUserNameSessionFeed(program.userID, view.authorProgramFeed)
            convertLevelToImg(program.levelProgram, view.btnLevelProgFeed)
            showLikes(database, currentUserID, "programs/${program.programID}/likes",view.NbLikeProgram, view.btnLikeProgram)
            showFollowers(database, currentUserID,program.programID,"users/${currentUserID}/currentPrograms", view.btnSubscribeProg)
        }
    }


}