package isen.CedricLucieFlorent.benfit

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.activity_exercice_session.view.*
import kotlinx.android.synthetic.main.activity_program.*
import kotlinx.android.synthetic.main.activity_session.*
import kotlinx.android.synthetic.main.recycler_view_exo_session.view.*
import java.util.*
import kotlin.collections.ArrayList


/*fun addNewExo(database : FirebaseDatabase, nameExo: String, idUser: String, descExo: String, levelExo: String, sportExo: String) : String{

    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if(newId == null){
        return "false"
    }
    val exo = Exercice(newId,nameExo,idUser,descExo,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return newId
}*/
/*fun addTemporaryExoSession(database : FirebaseDatabase, idUser:String, exo : Exercice) : Int{
    val dbExos = database.getReference("temporary_exos_session")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("TAG", "Couldn't get push key for exos")
        return -1
    }
    var newExo : SessionExercice = SessionExercice(newId, idUser,exo.id, "0")
    dbExos.child(newId).setValue(newExo)
    return 0
}*/
/*fun addTemporaryNameSession(database: FirebaseDatabase,idUser: String, nameSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    if(nameSession != "null"){
        dbInfos.child(idUser).child("nameSession").setValue(nameSession)
    }
}*/
/*fun addTemporaryDescSession(database: FirebaseDatabase,idUser: String, descSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    if(descSession != null){
        dbInfos.child(idUser).child("descSession").setValue(descSession)
    }
}*/
/*fun addTemporaryLevelSession(database: FirebaseDatabase,idUser: String, levelSession: String){
    val dbInfos = database.getReference("temporary_infos_session")

    dbInfos.child(idUser).child("levelSession").setValue(levelSession)
}*/
/*fun addTemporaryRoundSession(database: FirebaseDatabase,idUser: String, roundSession: String){
    val dbInfos = database.getReference("temporary_infos_session")
    if(roundSession != null){
        dbInfos.child(idUser).child("roundSession").setValue(roundSession)
    }
}*/
/*fun updateRepExoSession(database: FirebaseDatabase, idExoSession: String, rep: String){
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("temporary_exos_session")

    dbExos.child(idExoSession).child("rep").setValue(rep)
}*/

/*fun addTemporaryNameProgram(database: FirebaseDatabase,idUser: String, nameProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    if(nameProgram != null){
        dbInfos.child(idUser).child("nameProgram").setValue(nameProgram)
    }
}*/
/*fun addTemporaryDescProgram(database: FirebaseDatabase,idUser: String, descProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    if(descProgram !=null){
        dbInfos.child(idUser).child("descProgram").setValue(descProgram)
    }
}*/
/*fun addTemporaryLevelProgram(database: FirebaseDatabase,idUser: String, levelProgram: String){
    val dbInfos = database.getReference("temporary_infos_program")
    dbInfos.child(idUser).child("levelProgram").setValue(levelProgram)
}*/
/*fun showInfosRep(database :  FirebaseDatabase, activity: View, userId: String){
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
}*/
//This function get the posts on the database and show them on the fee
/*fun showExos(database : FirebaseDatabase, view: RecyclerView, context: Context, userId: String) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {
                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString(), value.child("pictureUID").value.toString(), value.child("urlYt").value.toString())
                exos.add(exo)
            }
            exos.reverse()
            view.adapter = ExoAdapter(exos,  { exoItem : Exercice -> exoChooseSessionClicked(context,exoItem, database, userId) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}*/
/*fun showRepExosSession(database: FirebaseDatabase, idExoSession: String, textView: TextView){

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
}*/
/*fun showExosSession(database : FirebaseDatabase, view: RecyclerView, userId :String, context: Context) {

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList<SessionExercice>()
            for(value in dataSnapshot.children ) {
                var exo : SessionExercice = SessionExercice(
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
            view.adapter = ExoSessionAdapter(exos, { exoItem : SessionExercice -> deleteExoSessionClicked(database, exoItem)})
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}*/
/*fun showInfosSession(database :  FirebaseDatabase, activity: SessionActivity, userId: String){
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
                            activity.editTextNumberSerie.setText(value.child("roundSession").value.toString())
                        }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}*/
/*fun saveInfosSession(database : FirebaseDatabase,idSession: String, userId :String,nameSession:String, descSession: String, levelSession:String, roundSession: Int) {
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
}*/
/*fun deleteInfosTempSession(database : FirebaseDatabase, activity: SessionActivity, userId :String) {
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
}*/
/*fun saveSession(database : FirebaseDatabase, storageReference : StorageReference, image_uri : Uri, context : Context, userId :String,nameSession:String, descSession: String, levelSession:String, nbrRound: Int) {
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

*/

/*fun saveInfosProgram(database : FirebaseDatabase,idProgram: String, userId :String,nameProgram:String, descProgram: String, levelProgral:String) {
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
}*/
/*fun deleteInfosTempProgram(database : FirebaseDatabase, activity: ProgramActivity, userId :String) {
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
}*/
/*fun saveProgram(database : FirebaseDatabase,storageReference: StorageReference, image_uri : Uri, context: Context, userId :String,nameProgram:String, descProgram: String, levelProgram:String) {

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
                    value.child("roundSession").value.toString().toInt(),
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

}*/
/*fun showInfosProgram(database :  FirebaseDatabase, activity: ProgramActivity, userId: String){
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
}*/

/*fun showExo(database: FirebaseDatabase, exoId : String, textView: TextView, imageView : ImageView) {
    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                var exo = Exercice(
                    value.child("id").value.toString(),
                    value.child("name").value.toString(),
                    value.child("idUser").value.toString(),
                    value.child("description").value.toString(),
                    value.child("difficulty").value.toString(),
                    value.child("sport").value.toString(),
                    value.child("pictureUID").value.toString(),
                    value.child("urlYt").value.toString())
                if(exo.id == exoId){
                    textView.text = "${exo.name}"
                    if (exo.pictureUID != "" && exo.pictureUID != "null"){
                        setImageFromFirestore(ApplicationContext.applicationContext(), imageView, "exos/${exo.id}/${exo.pictureUID}")
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}*/

/*fun deleteExosSession(database : FirebaseDatabase, userId :String) {

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.child("idUser").value.toString() == userId){
                    myRef.child(userId).removeValue()
                }

            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })


}*/

/*fun deleteExoSession(database : FirebaseDatabase, exoSessionId :String) {
    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("exoSessionID").value.toString() == exoSessionId){
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}*/

/*fun deleteExoSessionTemp(database : FirebaseDatabase, userID :String) {

    val myRef = database.getReference("temporary_exos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {

                if(value.child("userID").value.toString() == userID){
                    value.key?.let { myRef.child(it).removeValue() }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("comment", "Failed to read value.", error.toException())
        }
    })
}*/

/*fun deleteSessionProgram(database : FirebaseDatabase, sessionTempId :String) {

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
}*/

/*fun deleteSessionsTempProgram(database : FirebaseDatabase, idUser :String) {
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
}*/

/*private fun exoChooseSessionClicked(context:Context, exoItem : Exercice, database : FirebaseDatabase, userId :String) {

    addTemporaryExoSession(database,userId, exoItem)
    val intent = Intent(context, SessionActivity::class.java)
    context.startActivity(intent)
}*/

/*private fun deleteExoSessionClicked(firebase : FirebaseDatabase, exoItem : SessionExercice) {
    deleteExoSession(firebase, exoItem.exoSessionID)
}*/

/*fun showSessions(database: FirebaseDatabase, view: RecyclerView, context: Context, idUser: String){

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
                    value.child("roundSession").value.toString().toInt(),
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
}*/
//VOIR DANS FIREBASE POUR LES PATH

/*fun showSessionsProgram(database: FirebaseDatabase,view: RecyclerView, context: Context, userId : String) {

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
                    value.child("descSession").value.toString(),
                    value.child("levelSession").value.toString(),
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
}*/

/*fun addTemporarySessionProgram(database : FirebaseDatabase, idUser:String, session : Session) : Int{
    val database = FirebaseDatabase.getInstance()
    val dbSession = database.getReference("temporary_session_program")
    val newId = dbSession.push().key

    if(newId == null){
        Log.d("TAG", "Couldn't get push key for exos")
        return -1
    }
    var newSession : Session = Session(session.sessionID, idUser,session.nameSession,session.descSession,session.levelSession,session.exosSession,session.roundSession,session.pictureUID)
    dbSession.child(newId).setValue(newSession)
    dbSession.child(newId).child("idSessionTemp").setValue(newId)
    return 0
}*/

/*private fun sessionChooseProgramClicked(context:Context, sessionItem : Session, idUser: String, database : FirebaseDatabase) {
    val intent = Intent(context, ProgramActivity::class.java)
    context.startActivity(intent)
    addTemporarySessionProgram(database, idUser, sessionItem)
}*/

/*private fun sessionProgramClicked(context:Context, sessionItem : SessionProgram, database : FirebaseDatabase) {


}*/

/*private fun sessionsFeedClicked(context: Context, sessionItem: SessionFeed, database: FirebaseDatabase){

}*/

/*private fun deleteSessionProgramClicked(firebase : FirebaseDatabase, sessionItem : SessionProgram) {
    sessionItem.sessionProgID?.let { deleteSessionProgram(firebase, it) }
}*/


