package isen.cedriclucieflorent.benfit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.cedriclucieflorent.benfit.*
import isen.cedriclucieflorent.benfit.Functions.checkCompleteProgram
import isen.cedriclucieflorent.benfit.Functions.sessionFinished
import isen.cedriclucieflorent.benfit.Functions.setImageFromFirestore
import isen.cedriclucieflorent.benfit.Functions.showChecked
import isen.cedriclucieflorent.benfit.Models.ShowSessionProgram
import kotlinx.android.synthetic.main.recycler_view_show_program_sessions.view.*

class ShowSessionsAdapter (
    val sessions: ArrayList<ShowSessionProgram>,
    private val program: ShowProgram,
    val context: Context,
    val activity: String,
    val database: FirebaseDatabase,
    private val reference: String,
    private val clickSession: (ShowSessionProgram) -> Unit
): RecyclerView.Adapter<ShowSessionsAdapter.SessionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_show_program_sessions, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session, program, activity, database, reference , clickSession)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(
            session: ShowSessionProgram,
            program : ShowProgram,
            activity: String,
            database : FirebaseDatabase,
            reference: String,
            clickSession: (ShowSessionProgram) -> Unit
        ) {
            auth = FirebaseAuth.getInstance()
            view.parentViewSessionShowProgram.setOnClickListener{clickSession(session)}
            view.nameSessionShowProgram.text = session.nameSession
            setImageFromFirestore(
                view.imageViewSessionShowProgram,
                "sessions/${session.sessionID}/${session.imgURI}")

            if (activity != "SubProg"){
                view.finishedSessionBtn.visibility = View.INVISIBLE
            }
            else {
                view.finishedSessionBtn.setOnClickListener{
                    sessionFinished(database,session, program, auth.currentUser?.uid, view.finishedSessionBtn)
                    val userCurrent = auth.currentUser
                    val progId = program.programID ?: ""
                    if(userCurrent != null && progId != "") {
                        checkCompleteProgram(database,
                            userCurrent.uid,
                            it.context,
                            progId)
                    }

                }
                showChecked(database, reference, view.finishedSessionBtn, session.sessionID)
            }
        }
    }


}