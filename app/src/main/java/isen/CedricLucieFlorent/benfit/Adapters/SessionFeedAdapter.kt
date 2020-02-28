package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.Models.SessionFeed
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_feed_session.view.*

class SessionFeedAdapter (val sessions: ArrayList<SessionFeed>, val clickListener: (SessionFeed) -> Unit): RecyclerView.Adapter<SessionFeedAdapter.SessionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionFeedAdapter.SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_session, parent, false)
        return SessionFeedAdapter.SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionFeedAdapter.SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session,clickListener)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(session: SessionFeed, clickListener: (SessionFeed) -> Unit) {
        view.sessionNameTextView.text = session.nameSessionFeed
        view.descriptionSessionTextView.text = session.descrSessionFeed
    }

}
}