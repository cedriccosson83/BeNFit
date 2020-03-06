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
import isen.CedricLucieFlorent.benfit.Adapters.ShowSessionsAdapter
import isen.CedricLucieFlorent.benfit.Models.ShowSessionProgram
import kotlinx.android.synthetic.main.activity_show_program.*
import kotlinx.android.synthetic.main.recycler_view_show_program_sessions.*

class ShowProgramActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_show_program, frameLayout)
        auth = FirebaseAuth.getInstance()

        showProgramRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val intent = intent
        if (intent != null) {
            showProgram(intent)
        }
    }

    private fun showProgram(intent: Intent) {

        val programId: String? = intent.getStringExtra("program") ?: ""
        val activity:String? = intent.getStringExtra("activity") ?: ""

        val myRef = database.getReference("programs")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var program: ShowProgram
                for(value in dataSnapshot.children ) {
                    val arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        val userId : String = childLike.value.toString()
                        arrayLikes.add(userId)
                    }
                    val arraySession :ArrayList<String> = ArrayList()
                    for (childSession in value.child("sessionsProgram").children){
                        val sessionId : String = childSession.child("sessionID").value.toString()
                        Log.d("SESSIONID", sessionId)
                        arraySession.add(sessionId)
                    }
                    program = ShowProgram(
                        value.child("programID").value.toString(),
                        value.child("userID").value.toString(),
                        value.child("nameProgram").value.toString(),
                        value.child("descProgram").value.toString(),
                        value.child("levelProgram").value.toString(),
                        arraySession,
                        arrayLikes
                    )
                    if(program.programID == programId){
                        showProgramName.text = program.nameProgram
                        showProgramDesc.text = program.descProgram
                        convertLevelToImg(program.levelProgram, showProgramLevelIcon)
                        showProgramLevelText.text = program.levelProgram
                        showUserName(program.userID, showProgramAuthor)
                        val path = "programs/${program.programID}/likes"
                        val userId = auth.currentUser?.uid
                        showLikes(database,userId ,path , showProgramLike, showProgramLikeIcon)
                        showProgramLikeIcon.setOnClickListener{
                            likesHandler(database,userId,path,program.likes, showProgramLikeIcon)
                        }

                        showProgramAuthor.setOnClickListener {
                            redirectToUserActivity(this@ShowProgramActivity, program.userID)
                        }
                        if (activity != null && programId != null )
                        showSessionsFromProgram(database, program.sessionsProgram, activity, program)
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })
    }

    private fun sessionClicked(session : ShowSessionProgram) {
         val intent = Intent(context, ShowSessionActivity::class.java)
         val id : String = session.sessionID
         intent.putExtra("session", id)
         context.startActivity(intent)
    }

    private fun sessionFinished(session: ShowSessionProgram, program: ShowProgram){
        var currentUser = auth.currentUser
        var id = currentUser?.uid ?:""
        var programID = program.programID ?: ""
        if (id != ""){
            val myRef = database.getReference("users").child(id).child("currentPrograms").child(programID)
            for (value in program.sessionsProgram) {
                if (session.sessionID == value) {
                    myRef.child(session.sessionID).setValue("OK")
                    finishedSessionBtn.setImageResource(R.drawable.checked)
                }
            }
        }
    }

    fun showSessionsFromProgram(database : FirebaseDatabase, prog_sessions: ArrayList<String>, activity : String, program : ShowProgram) {

        val myRef = database.getReference("sessions")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessionsIn: ArrayList<ShowSessionProgram> = ArrayList()
                for (value in dataSnapshot.children) {
                    val sessId = value.child("sessionID").value.toString()
                    if (prog_sessions.indexOf(sessId) != -1) {
                        val sess = ShowSessionProgram(
                            value.child("sessionID").value.toString(),
                            value.child("nameSession").value.toString(),
                            value.child("userID").value.toString()
                        )
                        sessionsIn.add(sess)
                    }
                }
                sessionsIn.reverse()
                val reference = "users/${auth.currentUser?.uid}/currentPrograms/${program.programID}/"
                showProgramRecyclerView.adapter = ShowSessionsAdapter(sessionsIn, activity, database, reference,
                    {session : ShowSessionProgram -> sessionClicked(session)},
                    {session : ShowSessionProgram -> sessionFinished(session, program)})
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}
