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
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Post
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.Models.SessionFeed
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showUserNameSessionFeed
import kotlinx.android.synthetic.main.recycler_view_feed_session.view.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*

class SessionFeedAdapter (val sessions: ArrayList<SessionFeed>, val clickListenernotif: (SessionFeed) -> Unit, val clickListenerlike: (SessionFeed) -> Unit): RecyclerView.Adapter<SessionFeedAdapter.SessionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionFeedAdapter.SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_session, parent, false)
        return SessionFeedAdapter.SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionFeedAdapter.SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session,clickListenernotif, clickListenerlike)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun countLikes(session: SessionFeed) {
            var array: ArrayList<String> = session.likes

            var count: Int = array.size
            view.nbLikeTextView.text = "${count}"

        }

        fun showLike(session: SessionFeed) {
            auth = FirebaseAuth.getInstance()
            val currentUserID = auth.currentUser?.uid
            val likes = session.likes
            if (likes.all { it != currentUserID }) {
                view.btnLikeFeedSession.setImageResource(R.drawable.like)
            } else {
                view.btnLikeFeedSession.setImageResource(R.drawable.dislike)

            }
        }
        fun bind(session: SessionFeed, clickListenernotif: (SessionFeed) -> Unit, clickListenerlike: (SessionFeed) -> Unit) {
            view.sessionNameTextView.text = session.nameSessionFeed
            view.descriptionSessionTextView.text = session.descrSessionFeed
            view.btnNotifFeedSession.setOnClickListener { clickListenernotif(session) }
            view.btnLikeFeedSession.setOnClickListener { clickListenerlike(session) }
            showUserNameSessionFeed(session.userID, view.authorSessionFeed)
            view.levelSessionFeed.text = "Niveau " + session.levelSession
            showLike(session)
            countLikes(session)
        }
    }


}