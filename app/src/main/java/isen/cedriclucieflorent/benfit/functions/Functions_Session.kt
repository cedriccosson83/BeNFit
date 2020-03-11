package isen.cedriclucieflorent.benfit.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.cedriclucieflorent.benfit.*
import isen.cedriclucieflorent.benfit.adapters.SessionAdapter
import isen.cedriclucieflorent.benfit.adapters.SessionProgramAdapter
import isen.cedriclucieflorent.benfit.models.*
import kotlinx.android.synthetic.main.activity_session.*
import java.util.*
import kotlin.collections.ArrayList

fun addTemporaryNameSession(database: FirebaseDatabase, idUser: String, nameSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    Log.d("infos", nameSession)
    if(nameSession != "null"){
        dbInfos.child(idUser).child("nameSession").setValue(nameSession)
    }
}
fun addTemporaryDescSession(database: FirebaseDatabase,idUser: String, descSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    dbInfos.child(idUser).child("descSession").setValue(descSession)

}
fun addTemporaryLevelSession(database: FirebaseDatabase,idUser: String, levelSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    dbInfos.child(idUser).child("levelSession").setValue(levelSession)
}
fun addTemporaryRoundSession(database: FirebaseDatabase,idUser: String, roundSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    dbInfos.child(idUser).child("roundSession").setValue(roundSession)

}
fun updateRepExoSession(database: FirebaseDatabase, idExoSession: String, rep: String){
    val dbExos = database.getReference("temporary_exos_session")
    dbExos.child(idExoSession).child("rep").setValue(rep)
}
fun showExosSession(database : FirebaseDatabase, view: RecyclerView, userId :String) {
    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList()
            for(value in dataSnapshot.children ) {
                val exo = SessionExercice(
                    value.child("exoSessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("exoID").value.toString(),
                    value.child("rep").value.toString(),
                    value.child("pictureUID").value.toString(),
                    value.child("urlYt").value.toString())
                if(exo.userID== userId){
                    exos.add(exo)
                }
            }
            exos.reverse()
            view.adapter = ExoSessionAdapter(exos) {
                    exoItem : SessionExercice -> deleteExoSessionClicked(database, exoItem) }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}
fun showInfosSession(database :  FirebaseDatabase, activity: SessionActivity, userId: String){
    val myRef = database.getReference("temporary_infos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    if(value.child("nameSession").exists()){
                        activity.inputNameSession.setText(value.child("nameSession").value.toString())
                    }
                    if(value.child("descSession").exists()){
                        activity.inputDescSession.setText(value.child("descSession").value.toString())
                    }
                    if(value.child("roundSession").exists()){
                        activity.editTextNumberSerie.text = value.child("roundSession").value.toString()
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun saveInfosSession(database : FirebaseDatabase,idSession: String, userId :String,nameSession:String, descSession: String, levelSession:String, roundSession: Int) {
    val myRef = database.getReference("temporary_infos_session")
    val dbSession = database.getReference("sessions")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    dbSession.child(idSession).child("nameSession").setValue(nameSession)
                    dbSession.child(idSession).child("descSession").setValue(descSession)
                    dbSession.child(idSession).child("levelSession").setValue(levelSession)
                    dbSession.child(idSession).child("roundSession").setValue(roundSession)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun deleteInfosTempSession(database : FirebaseDatabase, activity: SessionActivity, userId :String) {
    activity.inputNameSession.setText("")
    activity.inputDescSession.setText("")

    val myRef = database.getReference("temporary_infos_session")
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
fun saveSession(database : FirebaseDatabase, storageReference : StorageReference, image_uri : Uri, context : Context, userId :String, nameSession:String, descSession: String, levelSession:String, nbrRound: Int) {
    val myRef = database.getReference("temporary_exos_session")
    val dbSession = database.getReference("sessions")

    val newId = dbSession.push().key
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList()
            for(value in dataSnapshot.children ) {
                val exo = SessionExercice(
                    value.child("exoSessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("exoID").value.toString(),
                    value.child("rep").value.toString())
                if(exo.userID== userId){
                    exos.add(exo)
                }
            }

            val session = Session(newId,userId,nameSession,descSession,levelSession,exos,nbrRound,"")
            if (newId != null) {
                dbSession.child(newId).setValue(session)
                saveInfosSession(database,newId, userId,nameSession, descSession, levelSession,nbrRound)
            }

            val uniqID = UUID.randomUUID().toString()
            val stoRef = storageReference.child("sessions/${session.sessionID}/$uniqID")
            val result: UploadTask
            result = if(image_uri != Uri.EMPTY) {
                stoRef.putFile(image_uri)
            } else {
                val uri = getDrawableToURI(context, R.drawable.sessions)
                stoRef.putFile(uri)
            }
            result.addOnSuccessListener {
                database.getReference("sessions/${session.sessionID}/pictureUID").setValue(uniqID)
            }

        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun deleteSessionProgram(database : FirebaseDatabase, sessionTempId :String) {

    val myRef = database.getReference("temporary_session_program")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("idSessionTemp").value.toString() == sessionTempId){
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}
fun deleteSessionsTempProgram(database : FirebaseDatabase, idUser :String) {
    val myRef = database.getReference("temporary_session_program")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("userID").value.toString() == idUser){
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}
fun showSessions(database: FirebaseDatabase, view: RecyclerView, context: Context, idUser: String){

    val myRef = database.getReference("sessions")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<Session> = ArrayList()
            for(value in dataSnapshot.children ) {
                val exosSession : ArrayList<SessionExercice> = ArrayList()
                val session = Session(value.child("sessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
                    exosSession,
                    value.child("roundSession").value.toString().toInt(),
                    value.child("pictureUID").value.toString())
                sessions.add(session)
            }
            sessions.reverse()
            view.adapter = SessionAdapter(sessions) {
                    sessionItem : Session
                        -> sessionChooseProgramClicked(context,sessionItem,idUser, database) }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
fun showSessionsProgram(database: FirebaseDatabase,view: RecyclerView, userId : String) {

    val myRef = database.getReference("temporary_session_program")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<SessionProgram> = ArrayList()
            for(value in dataSnapshot.children ) {
                val sessionProgram = SessionProgram(
                    value.child("idSessionTemp").value.toString(),
                    value.child("sessionID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("pictureUID").value.toString())
                if(userId == sessionProgram.userID){
                    sessions.add(sessionProgram)
                }

            }
            sessions.reverse()
            view.adapter = SessionProgramAdapter(sessions
            ) { sessionItem : SessionProgram -> deleteSessionProgramClicked(database, sessionItem) }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
fun addTemporarySessionProgram(database : FirebaseDatabase, idUser:String, session : Session) : Int{
    val dbSession = database.getReference("temporary_session_program")
    val newId = dbSession.push().key

    if(newId == null){
        Log.d("TAG", "Couldn't get push key for exos")
        return -1
    }
    val newSession = Session(
        session.sessionID,
        idUser,
        session.nameSession,
        session.descSession,
        session.levelSession,
        session.exosSession,
        session.roundSession,
        session.pictureUID)
    dbSession.child(newId).setValue(newSession)
    dbSession.child(newId).child("idSessionTemp").setValue(newId)
    return 0
}
private fun sessionChooseProgramClicked(context:Context, sessionItem : Session, idUser: String, database : FirebaseDatabase) {
    val intent = Intent(context, ProgramActivity::class.java)
    context.startActivity(intent)
    addTemporarySessionProgram(database, idUser, sessionItem)
}

private fun deleteSessionProgramClicked(firebase : FirebaseDatabase, sessionItem : SessionProgram) {
    deleteSessionProgram(firebase, sessionItem.sessionProgID)
}
fun sessionFinished(database: FirebaseDatabase, session: ShowSessionProgram, program: ShowProgram, currentUserID: String?, icon: ImageView){
    val programID = program.programID ?: ""
    val id = currentUserID ?:""
    if (id != ""){
        val myRef = database.getReference("users").child(id).child("currentPrograms").child(programID)
        for (value in program.sessionsProgram) {
            if (session.sessionID == value) {
                myRef.child(session.sessionID).setValue("OK")
                icon.setImageResource(R.drawable.checked)
            }
        }
    }
}
fun countTotalSessionLikes(database: FirebaseDatabase, userId: String, countTotalLikesProgram: Int,
                           gradeText: TextView, gradeImg1: ImageView, gradeImg2: ImageView, context: Context) {
    val myRef = database.getReference("sessions")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalLikesSess = 0
            for (value in dataSnapshot.children)
                if (value.child("userID").value.toString() == userId)
                    for (childLike in value.child("likes").children)
                        countTotalLikesSess++
            val countTotal = countTotalLikesProgram + countTotalLikesSess
            renderCoachGrade(countTotal, gradeText, gradeImg1, gradeImg2, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
fun linkToSession(context : Context,id : String) {
    val sharedLinkIntent = Intent(context, ShowSessionActivity::class.java)
    sharedLinkIntent.putExtra("sessionId", id)
    sharedLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(sharedLinkIntent)
}
fun setRulesIfInSession(database: FirebaseDatabase, targetTextView : TextView
                        , targetLabel : TextView , exerciceID: String, sessionParent: String?) {
    if (sessionParent != "" && sessionParent != null) {
        val myRef = database.getReference("sessions")
            .child(sessionParent).child("exosSession")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var consigne = ""
                for (value in dataSnapshot.children) {
                    if (value.child("exoID").value.toString() == exerciceID) {
                        consigne = value.child("rep").value.toString()
                    }
                }

                targetTextView.text = consigne
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    } else {
        targetTextView.text = ""
        targetLabel.text = ""
    }
}
