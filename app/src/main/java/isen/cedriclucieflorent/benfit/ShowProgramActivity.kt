package isen.cedriclucieflorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.Adapters.ShowSessionsAdapter
import isen.cedriclucieflorent.benfit.Functions.*
import isen.cedriclucieflorent.benfit.Models.ShowSessionProgram
import kotlinx.android.synthetic.main.activity_show_program.*

class ShowProgramActivity : MenuActivity() {
    val follow = ArrayList<String>()
    var currentUser: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_show_program, frameLayout)
        auth = FirebaseAuth.getInstance()

        showProgramRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        currentUser = auth.currentUser?.uid

        val intent = intent
        if (intent != null) {
            if (currentUser != "")
                fillCurrentProg(currentUser)
            showProgram(intent)
        }
    }

    private fun fillCurrentProg(currentUserID : String?) {
        if (currentUserID != null) {
            val myRef = database.getReference("users").child(currentUserID)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (value in dataSnapshot.child("currentPrograms").children) {
                        if(follow.all { it != value.value.toString()}){
                            follow.add(value.key.toString())
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TAG", "Failed to read value")
                }
            })
        }
    }

    private fun showProgram(intent: Intent) {

        val programId: String = intent.getStringExtra("programId") ?: ""

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
                        numberSessionProgram.text = arraySession.size.toString()
                        //showProgramLevelText.text = program.levelProgram
                        showUserName(program.userID, showProgramAuthor)
                        showFollowers(database,programId,"users/${currentUser}/currentPrograms", showProgramSub)

                        val path = "programs/${program.programID}/likes"
                        val userId = auth.currentUser?.uid
                        showLikes(database,userId ,path , showProgramLike, showProgramLikeIcon)
                        showProgramLikeIcon.setOnClickListener{
                            likesHandler(database,userId,path,program.likes, showProgramLikeIcon)
                        }

                        showProgramAuthor.setOnClickListener {
                            redirectToUserActivity(this@ShowProgramActivity, program.userID)
                        }
                        if (activity != null)
                            showSessionsFromProgram(database, program.sessionsProgram, activity, program)


                        showProgramShare.setOnClickListener{
                            val writePostIntent = Intent(context, WritePostActivity::class.java)
                            writePostIntent.putExtra("sharedProgram", programId)
                            writePostIntent.putExtra("sharedName", program.nameProgram)
                            startActivity(writePostIntent)
                        }

                        showProgramSub.setOnClickListener {
                            subscribeClicked(program, currentUser)
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

    private fun sessionClicked(session : ShowSessionProgram) {
         val intent = Intent(context, ShowSessionActivity::class.java)
         val id : String = session.sessionID
         intent.putExtra("sessionId", id)
         context.startActivity(intent)
    }

    private fun subscribeClicked(program : ShowProgram, currentUserID : String?) {
        val idProg = program.programID
        if (idProg == null || currentUserID == null) return

        val myRef = database.getReference("users").child(currentUserID)
        val sessions = program.sessionsProgram
        if(follow.all { it != idProg}) {
            follow.add(idProg)
            //myRef.child("currentPrograms").setValue(follow)
            val sessionMap = HashMap<String, String>()
            for (sess in sessions)
                sessionMap[sess] = "KO"
            myRef.child("currentPrograms").child(idProg).setValue(sessionMap)
            showProgramSub.setImageResource(R.drawable.unfollow)

            val redirectIntent = Intent(this, ShowProgramActivity::class.java)
            redirectIntent.putExtra("programId", program.programID)
            redirectIntent.putExtra("activity", "SubProg")
            redirectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(redirectIntent)

        }else{
            follow.remove(idProg)
            myRef.child("currentPrograms").child(idProg).removeValue()
            showProgramSub.setImageResource(R.drawable.follow)
            val redirectIntent = Intent(this, ProgramFeedActivity::class.java)
            startActivity(redirectIntent)
        }
    }

    fun showSessionsFromProgram(database : FirebaseDatabase, prog_sessions: ArrayList<String>, activity : String, program : ShowProgram) {

        val myRef = database.getReference("sessions")
        var activityto = activity
        if(!(follow.all { it != program.programID})){
            activityto = "SubProg"
        }


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessionsIn: ArrayList<ShowSessionProgram> = ArrayList()
                for (value in dataSnapshot.children) {
                    val sessId = value.child("sessionID").value.toString()
                    if (prog_sessions.indexOf(sessId) != -1) {
                        val sess = ShowSessionProgram(
                            value.child("sessionID").value.toString(),
                            value.child("nameSession").value.toString(),
                            value.child("userID").value.toString(),
                            value.child("pictureUID").value.toString()
                        )
                        sessionsIn.add(sess)
                    }
                }
                sessionsIn.reverse()
                val reference = "users/${auth.currentUser?.uid}/currentPrograms/${program.programID}/"
                showProgramRecyclerView.adapter = ShowSessionsAdapter(sessionsIn, program, ApplicationContext.applicationContext(), activityto, database, reference
                ) { session : ShowSessionProgram -> sessionClicked(session)}
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}
