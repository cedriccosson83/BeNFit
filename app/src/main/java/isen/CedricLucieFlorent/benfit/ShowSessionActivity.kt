package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.ShowExercicesAdapter
import isen.CedricLucieFlorent.benfit.Models.ShowExerciceSession
import kotlinx.android.synthetic.main.activity_show_session.*

class ShowSessionActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_show_session, frameLayout)
        auth = FirebaseAuth.getInstance()

        showSessionRecyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false)


        val intent = intent
        if (intent != null) {
            showSession(intent)
        }
    }

    private fun notifClicked(sessionID : String) {
        val intent = Intent(this, NotifActivity::class.java)
        val id = auth.currentUser?.uid
        intent.putExtra("userId", id)
        intent.putExtra("showSessionId",sessionID)
        intent.putExtra("fromAct", "Show")
        startActivity(intent)
    }

    private fun showSession(intent: Intent) {

        val sessionId: String? = intent.getStringExtra("sessionId") ?: ""
        val myRef = database.getReference("sessions")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var session: ShowSession
                for(value in dataSnapshot.children ) {
                    val arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        arrayLikes.add(childLike.value.toString())
                    }
                    val arrayExercices :ArrayList<String> = ArrayList()
                    for (childExo in value.child("exosSession").children){
                        arrayExercices.add(childExo.child("exoID").value.toString())
                    }
                    session = ShowSession(
                        value.child("sessionID").value.toString(),
                        value.child("userID").value.toString(),
                        value.child("nameSession").value.toString(),
                        value.child("descSession").value.toString(),
                        value.child("nbrRound").value.toString(),
                        value.child("levelSession").value.toString(),
                        arrayExercices,
                        arrayLikes,
                        value.child("pictureUID").value.toString()
                    )
                    if(session.sessionID == sessionId){
                        showSessionName.text = session.nameSession
                        showSessionDesc.text = session.descSession
                        textNumberExo.text = arrayExercices.size.toString()

                        convertLevelToImg(session.levelSession, showSessionLevelIcon)
                        //showSessionLevelText.text = session.levelSession
                        showUserName(session.userID, showSessionAuthor)
                        val path = "sessions/${session.sessionID}/likes"
                        val userId = auth.currentUser?.uid
                        showLikes(database,userId ,path , showSessionLike, showSessionLikeIcon)
                        showSessionLikeIcon.setOnClickListener{
                            likesHandler(database,userId,path,session.likes, showSessionLikeIcon)
                        }


                        showSessionAuthor.setOnClickListener {
                            redirectToUserActivity(this@ShowSessionActivity, session.userID)
                        }
                        showExercicesFromSession(database, session.exosSession, session.sessionID)

                        showSessionShare.setOnClickListener{
                            val writePostIntent = Intent(context, WritePostActivity::class.java)
                            writePostIntent.putExtra("sharedSession", session.sessionID)
                            writePostIntent.putExtra("sharedName", session.nameSession)
                            startActivity(writePostIntent)
                        }

                        showSessionNotif.setOnClickListener {
                            val sessionID = session.sessionID ?: ""
                            notifClicked(sessionID)
                        }
                        break
                    }


                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })
    }

    private fun exerciceClicked(exoID : String, sessId: String?) {
        showPopUpExercice(database,context,exoID, windowManager, sessId)
    }

    fun showExercicesFromSession(database : FirebaseDatabase, sess_exercices: ArrayList<String>,
                                 sessId: String?) {

        val myRef = database.getReference("exos")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exercicesIn: ArrayList<ShowExerciceSession> = ArrayList()
                for (value in dataSnapshot.children) {
                    val exoId = value.child("id").value.toString()
                    if (sess_exercices.indexOf(exoId) != -1) {
                        val exo = ShowExerciceSession(
                            value.child("id").value.toString(),
                            value.child("name").value.toString(),
                            value.child("idUser").value.toString(),
                            value.child("difficulty").value.toString(),
                            value.child("description").value.toString(),
                            value.child("urlYt").value.toString(),
                            value.child("pictureUID").value.toString(),
                            value.child("sport").value.toString()
                        )
                        exercicesIn.add(exo)
                    }

                }
                exercicesIn.reverse()

                showSessionRecyclerView.adapter = ShowExercicesAdapter(exercicesIn, sessId
                ) { exo : ShowExerciceSession, sessId: String? -> exerciceClicked(exo.id, sessId) }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }

}
