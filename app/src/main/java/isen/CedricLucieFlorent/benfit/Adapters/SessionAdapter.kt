package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isen.CedricLucieFlorent.benfit.ApplicationContext
import isen.CedricLucieFlorent.benfit.Functions.setImageFromFirestore
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_session_cell.view.*

class SessionAdapter(
        val sessions: ArrayList<Session>,
        private val clickListener: (Session) -> Unit)
    : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_session_cell, parent, false)
        return SessionViewHolder(view)
    }



    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session,clickListener)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }


    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(session: Session, clickListener: (Session) -> Unit){
            view.nameSessionProgram.text = session.nameSession
            view.descrSessionCell.text = session.descSession
            view.levelSessionCell.text = session.levelSession
            view.btnAddSessionProgram.setOnClickListener { clickListener(session) }
            val img = session.pictureUID
            setImageFromFirestore(
                ApplicationContext.applicationContext(),
                view.imageViewSessionProgram,
                "sessions/${session.sessionID}/${img}")
        }
    }



}