package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_show_program.*

class ShowProgramActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_show_program, frameLayout)
        auth = FirebaseAuth.getInstance()

        val intent = intent
        if (intent != null) {
            showProgram(intent)
        }
    }

    private fun showProgram(intent: Intent) {

        val programId: String? = intent.getStringExtra("program") ?: ""
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
                    for (childSession in value.child("sessions").children){
                        val sessionId : String = childSession.value.toString()
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
                        break
                    }


                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }
        })
    }
}
