package isen.CedricLucieFlorent.benfit.Adapters

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.Models.ShowSessionProgram
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showChecked
import kotlinx.android.synthetic.main.recycler_view_show_program_sessions.view.*

class ShowSessionsAdapter (val sessions: ArrayList<ShowSessionProgram>, val activity: String, val database: FirebaseDatabase, val reference: String,
                           private val clickSession: (ShowSessionProgram) -> Unit, private val clickFinished: (ShowSessionProgram)-> Unit
): RecyclerView.Adapter<ShowSessionsAdapter.SessionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_show_program_sessions, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session, activity, database, reference , clickSession, clickFinished)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(session: ShowSessionProgram, activity: String, database : FirebaseDatabase, reference: String,
                 clickSession: (ShowSessionProgram) -> Unit, clickFinished: (ShowSessionProgram) -> Unit) {
            view.nameSessionShowProgram.text = session.nameSession
            view.nameSessionShowProgram.setOnClickListener { clickSession(session) }
            if (activity != "SubProg"){
                view.finishedSessionBtn.visibility = View.INVISIBLE
            }
            else {
                view.finishedSessionBtn.setOnClickListener{clickFinished(session)}
                showChecked(database, reference, view.finishedSessionBtn)
            }
        }
    }


}