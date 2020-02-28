package isen.CedricLucieFlorent.benfit

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
import isen.CedricLucieFlorent.benfit.Adapters.PostAdapter
import isen.CedricLucieFlorent.benfit.Adapters.SessionFeedAdapter
import isen.CedricLucieFlorent.benfit.Models.Post
import isen.CedricLucieFlorent.benfit.Models.Session
import isen.CedricLucieFlorent.benfit.Models.SessionFeed
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_program.*
import kotlinx.android.synthetic.main.activity_session_feed.*

class SessionFeedActivity : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_session_feed, frameLayout)
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        if (id != null) {
            showSessionsFeed(database, recycler_view_session_feed, this, id)
        }

        recycler_view_session_feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }
    private fun userClicked(sessionItem : SessionFeed) {
        val intent = Intent(this, ProfileActivity::class.java)
        val id = auth.currentUser?.uid
        intent.putExtra("userId", id)
        startActivity(intent)
    }

    fun showSessionsFeed(database : FirebaseDatabase, view : RecyclerView, context: Context, userId: String) {

        val myRef = database.getReference("sessions")

        Log.d("SESSIOON1" , myRef.toString())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessions: ArrayList<SessionFeed> = ArrayList()
                for (value in dataSnapshot.children) {
                    Log.d("SESSIOON1" , "iter")
                    // var session : Session = Session(value.child("sessionID").value.toString(),value.child("userID").value.toString(),value.child("nameSession").value.toString(),value.child("descSession").value.toString(),value.child("levelSession").value.toString(),exosSession,value.child("nbrRound").value.toString().toInt())
                    val sessionFeed = SessionFeed(
                            value.child("sessionID").value.toString(),
                            value.child("nameSession").value.toString(),
                            value.child("descSession").value.toString(),
                            value.child("userID").value.toString(),
                            value.child("nbrRound").value.toString(),
                            value.child("levelSession").value.toString()
                    )
                    if (userId == sessionFeed.userID) {
                        sessions.add(sessionFeed)
                    }
                }
                Log.d("SESSIOON" , sessions.toString())
                sessions.reverse()
                recycler_view_session_feed.adapter = SessionFeedAdapter(sessions,{ sessionsItem : SessionFeed -> userClicked(sessionsItem) })
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}