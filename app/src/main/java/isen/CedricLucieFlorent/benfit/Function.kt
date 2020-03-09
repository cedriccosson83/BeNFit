package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.CedricLucieFlorent.benfit.Adapters.SessionAdapter
import isen.CedricLucieFlorent.benfit.Adapters.SessionFeedAdapter
import isen.CedricLucieFlorent.benfit.Adapters.SessionProgramAdapter
import isen.CedricLucieFlorent.benfit.Models.*
import kotlinx.android.synthetic.main.activity_exercice_session.*
import kotlinx.android.synthetic.main.activity_program.*
import kotlinx.android.synthetic.main.activity_session.*
import java.util.*
import kotlin.collections.ArrayList


fun addNewExo(database : FirebaseDatabase, nameExo: String, idUser: String, descExo: String, levelExo: String, sportExo: String) : String{
    Log.d("function", "addNewExo")

    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return "false"
    }
    val exo = Exercice(newId,nameExo,idUser,descExo,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return newId
}

fun addTemporaryExoSession(database : FirebaseDatabase, idUser:String, exo : Exercice) : Int{
    Log.d("function", "addTemporaryExoSession")
    val dbExos = database.getReference("temporary_exos_session")
    val newId = dbExos.push().key
    Log.d("id",exo.idUser)
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }
    var newExo : SessionExercice = SessionExercice(newId, idUser,exo.id, "0")
    dbExos.child(newId).setValue(newExo)
    return 0
}

fun addTemporaryNameSession(database: FirebaseDatabase,idUser: String, nameSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    Log.d("infos", nameSession)
    if(nameSession != "null"){
        dbInfos.child(idUser).child("nameSession").setValue(nameSession)
    }
}

fun addTemporaryDescSession(database: FirebaseDatabase,idUser: String, descSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    if(descSession != null){
        dbInfos.child(idUser).child("descSession").setValue(descSession)
    }
}

fun addTemporaryLevelSession(database: FirebaseDatabase,idUser: String, levelSession: String){
    val dbInfos = database.getReference("temporary_infos_session")

    dbInfos.child(idUser).child("levelSession").setValue(levelSession)
}

fun addTemporaryRoundSession(database: FirebaseDatabase,idUser: String, roundSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    if(roundSession != null){
        dbInfos.child(idUser).child("roundSession").setValue(roundSession)
    }
}

fun addTemporaryNameProgram(database: FirebaseDatabase,idUser: String, nameProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    if(nameProgram != null){
        dbInfos.child(idUser).child("nameProgram").setValue(nameProgram)
    }
}

fun addTemporaryDescProgram(database: FirebaseDatabase,idUser: String, descProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    if(descProgram !=null){
        dbInfos.child(idUser).child("descProgram").setValue(descProgram)
    }
}

fun addTemporaryLevelProgram(database: FirebaseDatabase,idUser: String, levelProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    dbInfos.child(idUser).child("levelProgram").setValue(levelProgram)
}


fun updateRepExoSession(database: FirebaseDatabase, idExoSession: String, rep: String){
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("temporary_exos_session")

    dbExos.child(idExoSession).child("rep").setValue(rep)
}

fun showInfosRep(database :  FirebaseDatabase, activity: ExerciceSessionActivity, userId: String){
    val myRef = database.getReference("temporary_exos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    val value  = value.child("rep").value.toString().split(" ")
                    if(value.size > 1){
                        activity.inputValueExo.setText(value[0])
                        activity.inputUnitExo.setText(value[1])
                    }else{
                        activity.inputValueExo.setText("")
                        activity.inputUnitExo.setText("")
                    }

                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
//This function get the posts on the database and show them on the feed
fun showExos(database : FirebaseDatabase, view: RecyclerView, context: Context, userId: String) {
    Log.d("function", "showExos")

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {
                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
                exos.add(exo)
            }
            exos.reverse()
            view.adapter = ExoAdapter(exos,  { exoItem : Exercice -> exoChooseSessionClicked(context,exoItem, database, userId) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}

fun showRepExosSession(database: FirebaseDatabase, idExoSession: String, textView: TextView){
    Log.d("function", "showRepExosSession")

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("exoSessionID").value.toString() == idExoSession){
                    textView.text = value.child("rep").value.toString()
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun showExosSession(database : FirebaseDatabase, view: RecyclerView, userId :String, context: Context) {
    Log.d("function", "showExosSession")

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList<SessionExercice>()
            for(value in dataSnapshot.children ) {
                var exo : SessionExercice = SessionExercice(value.child("exoSessionID").value.toString(),value.child("userID").value.toString(), value.child("exoID").value.toString())
                if(exo.userID== userId){
                    exos.add(exo)
                }
            }
            exos.reverse()
            view.adapter = ExoSessionAdapter(exos,  { exoItem : SessionExercice -> deleteExoSessionClicked(database, exoItem) },  { exoItem : SessionExercice -> exoSessionClicked(context, exoItem) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}

fun showInfosSession(database :  FirebaseDatabase, activity: SessionActivity, userId: String){
    Log.d("function", "showInfosSession")
    val myRef = database.getReference("temporary_infos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    Log.d("infos", value.key.toString())
                    Log.d("infos", value.child("nameSession").value.toString())
                        if(value.child("nameSession").exists()){
                            activity.inputNameSession.setText(value.child("nameSession").value.toString())
                        }
                        if(value.child("descSession").exists()){
                            activity.inputDescSession.setText(value.child("descSession").value.toString())
                        }
                        if(value.child("roundSession").exists()){
                            activity.editTextNumberSerie.setText(value.child("roundSession").value.toString())
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

fun saveSession(database : FirebaseDatabase, storageReference : StorageReference, image_uri : Uri, context : Context, userId :String,nameSession:String, descSession: String, levelSession:String, nbrRound: Int) {
    val myRef = database.getReference("temporary_exos_session")
    val dbSession = database.getReference("sessions")

    val newId = dbSession.push().key
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList<SessionExercice>()
            for(value in dataSnapshot.children ) {
                var exo : SessionExercice = SessionExercice(value.child("exoSessionID").value.toString(),value.child("userID").value.toString(), value.child("exoID").value.toString(),value.child("rep").value.toString())
                if(exo.userID== userId){
                    exos.add(exo)
                }
                Log.d("Exo", exo.toString())
            }
            //var session : Session = Session(newId,userId,nameSession,descSession,levelSession,exos,nbrRound)

            var session : Session = Session(newId,userId,nameSession,descSession,levelSession,exos,nbrRound,"")
            if (newId != null) {
                dbSession.child(newId).setValue(session)
                saveInfosSession(database,newId, userId,nameSession, descSession, levelSession,nbrRound)
            }

            val uniqID = UUID.randomUUID().toString()
            val stoRef = storageReference.child("sessions/${session.sessionID}/$uniqID")
            val result: UploadTask
            if(image_uri != Uri.EMPTY) {
                result = stoRef.putFile(image_uri)
            } else {
                val uri = getDrawableToURI(context,R.drawable.sessions)
                result = stoRef.putFile(uri)
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



fun saveInfosProgram(database : FirebaseDatabase,idProgram: String, userId :String,nameProgram:String, descProgram: String, levelProgral:String) {
    val myRef = database.getReference("temporary_infos_program")
    val dbSession = database.getReference("program")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    dbSession.child(idProgram).child("nameProgram").setValue(value.child("nameProgram").value.toString())
                    dbSession.child(idProgram).child("descProgram").setValue(value.child("descProgram").value.toString())
                    dbSession.child(idProgram).child("levelProgram").setValue(value.child("levelProgram").value.toString())
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
fun saveProgram(database : FirebaseDatabase,storageReference: StorageReference, image_uri : Uri, context: Context, userId :String,nameProgram:String, descProgram: String, levelProgram:String) {
    Log.d("function", "saveProgram")

    val myRef = database.getReference("temporary_session_program")
    val dbProgram = database.getReference("programs")

    val newId = dbProgram.push().key
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<Session> = ArrayList<Session>()
            for(value in dataSnapshot.children ) {

                var exosSession : ArrayList<SessionExercice> = ArrayList()
                var session : Session = Session(
                    value.child("sessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
                    exosSession,
                    value.child("nbrRound").value.toString().toInt(),
                    value.child("pictureUID").value.toString()
                )
                if(session.userID == userId){
                    sessions.add(session)
                }

            }

            var program = Program(newId,userId,nameProgram,descProgram,levelProgram,sessions)
            if (newId != null) {
                dbProgram.child(newId).setValue(program)
                saveInfosProgram(database,newId, userId,nameProgram, descProgram, levelProgram)
            }

            val uniqID = UUID.randomUUID().toString()
            val stoRef = storageReference.child("programs/${program.programID}/$uniqID")
            val result: UploadTask
            if(image_uri != Uri.EMPTY) {
                result = stoRef.putFile(image_uri)
            } else {
                val uri = getDrawableToURI(context,R.drawable.programs)
                result = stoRef.putFile(uri)
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
    Log.d("function", "showInfosProgram")
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

fun showExo(database: FirebaseDatabase, exoId : String, textView: TextView) {
    Log.d("function", "showExo")

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            var exo: Exercice
            for(value in dataSnapshot.children ) {
                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
                if(exo.id == exoId){
                    Log.d("exo", "${exo.name}")
                    textView.text = "${exo.name}"

                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun deleteExosSession(database : FirebaseDatabase, userId :String) {
    Log.d("function", "deleteExosSession")

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.child("idUser").value.toString() == userId){
                    Log.d("value", value.toString())
                    myRef.child(userId).removeValue()
                }

            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })



}

fun deleteExoSession(database : FirebaseDatabase, exoSessionId :String) {
    Log.d("function", "deleteExoSession")
    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("exoSessionID").value.toString() == exoSessionId){
                    Log.d("value", value.key)
                    Log.d("value", exoSessionId)
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}

fun deleteExoSessionTemp(database : FirebaseDatabase, userID :String) {
    Log.d("delete", "suppressionexo")

    val myRef = database.getReference("temporary_exos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("userID").value.toString() == userID){
                    Log.d("value", value.key)
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}

fun deleteSessionProgram(database : FirebaseDatabase, sessionTempId :String) {
    Log.d("delete", "suppressionprog")

    val myRef = database.getReference("temporary_session_program")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("idSessionTemp").value.toString() == sessionTempId){
                    Log.d("value", value.key)
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
    Log.d("delete", "suppression")
    val myRef = database.getReference("temporary_session_program")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("userID").value.toString() == idUser){
                    Log.d("value", value.key)
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}

private fun exoChooseSessionClicked(context:Context, exoItem : Exercice, database : FirebaseDatabase, userId :String) {
    Log.d("exo", userId)

    addTemporaryExoSession(database,userId, exoItem)
    val intent = Intent(context, SessionActivity::class.java)
    context.startActivity(intent)
}

private fun deleteExoSessionClicked(firebase : FirebaseDatabase, exoItem : SessionExercice) {
    deleteExoSession(firebase, exoItem.exoSessionID)

    Log.d("session", exoItem.exoID)
}

private fun exoSessionClicked(context:Context,exoItem : SessionExercice) {
    val intent = Intent(context, ExerciceSessionActivity::class.java)
    intent.putExtra("id", exoItem.exoID)
    intent.putExtra("idExoSession", exoItem.exoSessionID)
    context.startActivity(intent)
    Log.d("session", exoItem.exoID)
}

fun showSessions(database: FirebaseDatabase, view: RecyclerView, context: Context, idUser: String){
    Log.d("function", "showSessions")

    val myRef = database.getReference("sessions")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<Session> = ArrayList<Session>()
            for(value in dataSnapshot.children ) {
                var exosSession : ArrayList<SessionExercice> = ArrayList()
                var session : Session = Session(value.child("sessionID").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
                    exosSession,
                    value.child("nbrRound").value.toString().toInt(),
                    value.child("pictureUID").value.toString())
                sessions.add(session)
            }
            sessions.reverse()
            view.adapter = SessionAdapter(sessions,  { sessionItem : Session -> sessionChooseProgramClicked(context,sessionItem,idUser, database) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
//VOIR DANS FIREBASE POUR LES PATH

fun showSessionsProgram(database: FirebaseDatabase,view: RecyclerView, context: Context, userId : String) {

    val myRef = database.getReference("temporary_session_program")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<SessionProgram> = ArrayList<SessionProgram>()
            for(value in dataSnapshot.children ) {
                var exosSession : ArrayList<SessionExercice> = ArrayList()
               // var session : Session = Session(value.child("sessionID").value.toString(),value.child("userID").value.toString(),value.child("nameSession").value.toString(),value.child("descSession").value.toString(),value.child("levelSession").value.toString(),exosSession,value.child("nbrRound").value.toString().toInt())
                var sessionProgram : SessionProgram = SessionProgram(
                    value.child("idSessionTemp").value.toString(),
                    value.child("sessionID").value.toString(),
                    value.child("nameSession").value.toString(),
                    value.child("userID").value.toString(),
                    value.child("pictureUID").value.toString())
                if(userId == sessionProgram.userID){
                    sessions.add(sessionProgram)
                }

            }
            sessions.reverse()
            view.adapter = SessionProgramAdapter(sessions, {sessionItem : SessionProgram -> deleteSessionProgramClicked(database, sessionItem) },  { sessionItem : SessionProgram -> sessionProgramClicked(context, sessionItem,database) })
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}

fun addTemporarySessionProgram(database : FirebaseDatabase, idUser:String, session : Session) : Int{
    Log.d("function", "addTemporarySessionProgram")
    val database = FirebaseDatabase.getInstance()
    val dbSession = database.getReference("temporary_session_program")
    val newId = dbSession.push().key
    Log.d("idSession",newId)

    Log.d("id",session.userID)
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }
    var newSession : Session = Session(session.sessionID, idUser,session.nameSession,session.descSession,session.levelSession,session.exosSession,session.roundSession,session.pictureUID)
    dbSession.child(newId).setValue(newSession)
    dbSession.child(newId).child("idSessionTemp").setValue(newId)
    return 0
}

private fun sessionChooseProgramClicked(context:Context, sessionItem : Session, idUser: String, database : FirebaseDatabase) {

    Log.d("function", "sessionChooseProgramClicked")
    val intent = Intent(context, ProgramActivity::class.java)
    context.startActivity(intent)
    addTemporarySessionProgram(database, idUser, sessionItem)
}

private fun sessionProgramClicked(context:Context, sessionItem : SessionProgram, database : FirebaseDatabase) {


}

private fun sessionsFeedClicked(context: Context, sessionItem: SessionFeed, database: FirebaseDatabase){

}

private fun deleteSessionProgramClicked(firebase : FirebaseDatabase, sessionItem : SessionProgram) {
    Log.d("delete", sessionItem.sessionProgID)
    sessionItem.sessionProgID?.let { deleteSessionProgram(firebase, it) }
}


