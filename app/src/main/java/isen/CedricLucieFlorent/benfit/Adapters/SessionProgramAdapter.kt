package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isen.CedricLucieFlorent.benfit.*
import isen.CedricLucieFlorent.benfit.Functions.setImageFromFirestore
import isen.CedricLucieFlorent.benfit.Models.SessionProgram
import kotlinx.android.synthetic.main.recycler_view_session_program.view.*

class SessionProgramAdapter(
    val sessions: ArrayList<SessionProgram>,
        private val deleteListener: (SessionProgram) -> Unit,
        private val sessionListener: (SessionProgram) -> Unit)
    : RecyclerView.Adapter<SessionProgramAdapter.SessionProgramViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_session_program, parent, false)
        return SessionProgramViewHolder(view)
    }


    override fun onBindViewHolder(holder: SessionProgramViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session, deleteListener, sessionListener)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }


    class SessionProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(session: SessionProgram, deleteListener: (SessionProgram) -> Unit, sessionListener: (SessionProgram) -> Unit) {
            view.nameSessionCreateProgram.text = session.nameSessionProgram
            view.btnDeleteSessionCreateProgram.setOnClickListener { deleteListener(session) }
            val img = session.imgURI
            setImageFromFirestore(ApplicationContext.applicationContext(), view.imageViewSessionCreateProgram, "sessions/${session.sessionID}/${img}")
            //view.cardViewExoSession.setOnClickListener { exoListener(exo) }
        }
    }

}



