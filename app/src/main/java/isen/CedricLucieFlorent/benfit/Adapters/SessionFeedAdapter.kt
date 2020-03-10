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
import isen.CedricLucieFlorent.benfit.*
import isen.CedricLucieFlorent.benfit.Models.Post
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.Models.SessionFeed
import kotlinx.android.synthetic.main.recycler_view_feed_session.*
import kotlinx.android.synthetic.main.recycler_view_feed_session.view.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*

class SessionFeedAdapter (val sessions: ArrayList<SessionFeed>,
                          val clickListenernotif: (SessionFeed) -> Unit,
                          val sessionClicked: (SessionFeed) -> Unit): RecyclerView.Adapter<SessionFeedAdapter.SessionViewHolder>(){

    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_feed_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.bind(session,clickListenernotif, sessionClicked)
    }

    override fun getItemCount(): Int {
        return sessions.count()
    }

    class SessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()

        fun bind(session: SessionFeed, clickListenernotif: (SessionFeed) -> Unit, sessionClicked: (SessionFeed) -> Unit) {
            view.sessionNameTextView.text = session.nameSessionFeed
            view.descriptionSessionTextView.text = session.descrSessionFeed
            view.sessionNameTextView.setOnClickListener { sessionClicked(session) }
            view.parentFeedSession.setOnClickListener { sessionClicked(session) }
            view.btnNotifFeedSession.setOnClickListener { clickListenernotif(session) }
            showNotified(database, "notifications/${auth.currentUser?.uid}", session.sessionID, view.btnNotifFeedSession)
            var img = session.imgURI
            setImageFromFirestore(ApplicationContext.applicationContext(), view.imageViewFeedSession, "sessions/${session.sessionID}/${img}")
            view.btnLikeFeedSession.setOnClickListener {
                likesHandler(database,auth.currentUser?.uid, "sessions/${session.sessionID}/likes",session.likes, view.btnLikeFeedSession)
            }
            showUserNameSessionFeed(session.userID, view.authorSessionFeed)
            convertLevelToImg(session.levelSession, view.levelSessionFeed)
            showLikes(database, auth.currentUser?.uid, "sessions/${session.sessionID}/likes",view.nbLikeTextView, view.btnLikeFeedSession)
        }
    }


}