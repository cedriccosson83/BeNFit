package isen.cedriclucieflorent.benfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.adapters.SessionFeedAdapter
import isen.cedriclucieflorent.benfit.models.SessionFeed
import kotlinx.android.synthetic.main.activity_session_feed.*

class SessionFeedActivity : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_session_feed, frameLayout)
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        if (id != null) {
            showSessionsFeed(database)
        }

        recycler_view_session_feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        feedSessionNewBtn.setOnClickListener{startActivity(Intent(context, SessionActivity::class.java))}
    }
    private fun notifClicked(session : SessionFeed) {
        val intent = Intent(this, NotifActivity::class.java)
        val id = auth.currentUser?.uid
        intent.putExtra("userId", id)
        intent.putExtra("sessionID", session.sessionID)
        intent.putExtra("fromAct", "Feed")
        startActivity(intent)
    }


    private fun sessionClicked(session : SessionFeed) {
        val intent = Intent(context, ShowSessionActivity::class.java)
        val id : String = session.sessionID
        intent.putExtra("sessionId", id)
        context.startActivity(intent)
    }

    private fun showSessionsFeed(database : FirebaseDatabase) {

        val myRef = database.getReference("sessions")
        auth = FirebaseAuth.getInstance()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessions: ArrayList<SessionFeed> = ArrayList()
                for (value in dataSnapshot.children) {
                    val arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        val likesUserId : String = childLike.value.toString()
                        arrayLikes.add(likesUserId)
                    }

                    val sessionFeed = SessionFeed(
                        value.child("sessionID").value.toString(),
                        value.child("nameSession").value.toString(),
                        value.child("descSession").value.toString(),
                        value.child("userID").value.toString(),
                        value.child("roundSession").value.toString(),
                        value.child("levelSession").value.toString(),
                        arrayLikes,
                        value.child("pictureUID").value.toString()
                    )
                    sessions.add(sessionFeed)
                }
                sessions.reverse()
                recycler_view_session_feed.adapter = SessionFeedAdapter(
                    sessions,
                    { sess : SessionFeed -> notifClicked(sess)},
                    { sess : SessionFeed -> sessionClicked(sess)})
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}