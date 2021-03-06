package isen.cedriclucieflorent.benfit.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.cedriclucieflorent.benfit.*
import isen.cedriclucieflorent.benfit.models.Session
import isen.cedriclucieflorent.benfit.models.SessionExercice
import kotlinx.android.synthetic.main.activity_program.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

fun addTemporaryNameProgram(database: FirebaseDatabase, idUser: String, nameProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    dbInfos.child(idUser).child("nameProgram").setValue(nameProgram)
}

fun addTemporaryDescProgram(database: FirebaseDatabase,idUser: String, descProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    dbInfos.child(idUser).child("descProgram").setValue(descProgram)
}

fun addTemporaryLevelProgram(database: FirebaseDatabase,idUser: String, levelProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    dbInfos.child(idUser).child("levelProgram").setValue(levelProgram)
}

fun saveInfosProgram(database : FirebaseDatabase,idProgram: String, userId :String, program: Program) {
    val myRef = database.getReference("temporary_infos_program")
    val dbSession = database.getReference("programs")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    dbSession.child(idProgram).child("nameProgram")
                        .setValue(program.nameProgram)

                    dbSession.child(idProgram).child("descProgram")
                        .setValue(program.descProgram)

                    dbSession.child(idProgram).child("levelProgram")
                        .setValue(program.levelProgram)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun deleteInfosTempProgram(database : FirebaseDatabase, activity: ProgramActivity, userId :String) {
    activity.inputNameProgram.setText("")
    activity.inputDescProgram.setText("")

    val myRef = database.getReference("temporary_infos_program")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    myRef.child(userId).removeValue()
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("temp", "Failed to read value.", error.toException())
        }
    })
}

fun saveProgram(database : FirebaseDatabase, storageReference: StorageReference, image_uri : Uri,
                context: Context, userId :String, nameProgram:String, descProgram: String, levelProgram:String) {

    val myRef = database.getReference("temporary_session_program")
    val dbProgram = database.getReference("programs")

    val newId = dbProgram.push().key
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<Session> = ArrayList()
            for(value in dataSnapshot.children ) {

                val exosSession : ArrayList<SessionExercice> = ArrayList()
                val session = Session(
                    value.child("sessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
                    exosSession,
                    value.child("roundSession").value.toString().toInt(),
                    value.child("pictureUID").value.toString()
                )
                if(session.userID == userId){
                    sessions.add(session)
                }
            }

            val program = Program(newId,userId,nameProgram,descProgram,levelProgram,sessions)
            if (newId != null) {
                dbProgram.child(newId).setValue(program)
                saveInfosProgram(database,newId, userId, program)
            }

            val uniqID = UUID.randomUUID().toString()
            val stoRef = storageReference.child("programs/${program.programID}/$uniqID")
            val result: UploadTask
            result = if(image_uri != Uri.EMPTY) {
                stoRef.putFile(image_uri)
            } else {
                val uri = getDrawableToURI(context, R.drawable.programs)
                stoRef.putFile(uri)
            }
            result.addOnSuccessListener {
                database.getReference("programs/${program.programID}/pictureUID").setValue(uniqID)
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("program", "Failed to read value.", error.toException())
        }
    })

}

fun showInfosProgram(database :  FirebaseDatabase, activity: ProgramActivity, userId: String){
    val myRef = database.getReference("temporary_infos_program")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    if(value.child("nameProgram").exists()){
                        activity.inputNameProgram.setText(value.child("nameProgram").value.toString())
                    }
                    if(value.child("descProgram").exists()){
                        activity.inputDescProgram.setText(value.child("descProgram").value.toString())

                    }

                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun getProgramProgression(database: FirebaseDatabase, userId: String?,
                          programID: String, programProgress: ProgressBar
) {
    val myRef = database.getReference("programs").child(programID)
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalSess = 0
            for (value in dataSnapshot.children) {
                if (value.key.toString() == "sessionsProgram") {
                    for (childSess in value.children)
                        countTotalSess++
                    break
                }
            }
            checkUserSessionDone(database, userId, programID, countTotalSess, programProgress)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("programs", "Failed to read value.", error.toException())
        }
    })
}

fun renderProgressProgram(countSessTotProgram : Int,countTotalDoneSess : Int,
                          programProgress: ProgressBar) {
    val percent : Float = if (countTotalDoneSess > 0)
        round(((countTotalDoneSess.toFloat()/ countSessTotProgram.toFloat())  * 100))
    else
        0F
    programProgress.progress = percent.toInt()
}

fun countTotalProgramLikes(database: FirebaseDatabase, userId: String,
                           gradeText: TextView, gradeImg1: ImageView,
                           gradeImg2: ImageView, context: Context) {
    val myRef = database.getReference("programs")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalLikes = 0
            for (value in dataSnapshot.children)
                if (value.child("userID").value.toString() == userId)
                    for (childLike in value.child("likes").children)
                        countTotalLikes++

            countTotalSessionLikes(database, userId, countTotalLikes, gradeText,
                gradeImg1, gradeImg2, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}

fun checkCompleteProgram(database: FirebaseDatabase,
                         userId: String,
                         context: Context,
                         programId : String
) {
    val myRef = database
        .getReference("users")
        .child(userId)
        .child("currentPrograms")
        .child(programId)

    val sessionAchieve: ArrayList<String> = ArrayList()

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var nbSupposedToBeOK = 0
            for (value in dataSnapshot.children) {
                sessionAchieve.add(value.key.toString())
                if (value.value.toString() == "OK")
                    nbSupposedToBeOK++
            }
            if (nbSupposedToBeOK == sessionAchieve.size) {
                computeScore(database, sessionAchieve, userId, context)
                dataSnapshot.ref.removeValue()
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
fun linkToProgram(context : Context,id : String) {
    val sharedLinkIntent = Intent(context, ShowProgramActivity::class.java)
    sharedLinkIntent.putExtra("programId", id)
    sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(sharedLinkIntent)
}
