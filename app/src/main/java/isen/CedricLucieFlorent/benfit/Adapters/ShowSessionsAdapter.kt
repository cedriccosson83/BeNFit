package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.SessionFeed
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_show_program_sessions.view.*

class ShowSessionsAdapter (val sessions: ArrayList<SessionFeed>,
                           private val clickSession: (SessionFeed) -> Unit)
    : RecyclerView.Adapter<ShowSessionsAdapter.SessionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session,clickSession)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(session: SessionFeed,
                 clickSession: (SessionFeed) -> Unit) {
            view.nameSessionShowProgram.text = session.nameSessionFeed
            view.nameSessionShowProgram.setOnClickListener { clickSession(session) }
        }
    }


}