package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.SessionAdapter
import isen.CedricLucieFlorent.benfit.Adapters.SessionFeedAdapter
import isen.CedricLucieFlorent.benfit.Adapters.SessionProgramAdapter
import isen.CedricLucieFlorent.benfit.Models.*


fun addNewExo(database : FirebaseDatabase, nameExo: String, idUser: String, descExo: String, urlYtb: String, levelExo: String, sportExo: String) : String{
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return "false"
    }
    val exo = Exercice(newId,nameExo,idUser,descExo, urlYtb,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return newId
}

fun addTemporaryExoSession(database : FirebaseDatabase, idUser:String, exo : Exercice) : Int{
    val database = FirebaseDatabase.getInstance()
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

fun updateRepExoSession(database: FirebaseDatabase, idExoSession: String, rep: String){
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("temporary_exos_session")
    dbExos.child(idExoSession).child("rep").setValue(rep)
}

//This function get the posts on the database and show them on the feed
fun showExos(database : FirebaseDatabase, view: RecyclerView, context: Context, userId: String) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {


                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("urlYTB").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
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

fun saveSession(database : FirebaseDatabase, userId :String,nameSession:String, descSession: String, levelSession:String, nbrRound: Int) {

    val myRef = database.getReference("temporary_exos_session")
    val dbSession = database.getReference("sessions")

    val newId = dbSession.push().key
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<SessionExercice> = ArrayList<SessionExercice>()
            for(value in dataSnapshot.children ) {


                var exo : SessionExercice = SessionExercice(value.child("exoSessionID").value.toString(),value.child("userID").value.toString(), value.child("exoID").value.toString(),value.child("rep").value.toString())
                if(exo.userID== userId){
                    exos.add(exo)
                }

            }

            var session : Session = Session(newId,userId,nameSession,descSession,levelSession,exos,nbrRound)
            if (newId != null) {
                dbSession.child(newId).setValue(session)
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}

fun saveProgram(database : FirebaseDatabase, userId :String,nameProgram:String, descProgram: String, levelProgram:String) {

    val myRef = database.getReference("temporary_session_program")
    val dbProgram = database.getReference("programs")

    val newId = dbProgram.push().key
    myRef.addValueEventListener(object : ValueEventListener {
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
                    exosSession,value.child("nbrRound").value.toString().toInt())
                if(session.userID== userId){
                    sessions.add(session)
                }

            }

            var program : Program = Program(newId,userId,nameProgram,descProgram,levelProgram,sessions)
            if (newId != null) {
                dbProgram.child(newId).setValue(program)
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("program", "Failed to read value.", error.toException())
        }
    })

}

fun showExo(database: FirebaseDatabase, exoId : String, textView: TextView) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            var exo: Exercice
            for(value in dataSnapshot.children ) {
                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("urlYTB").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
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

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
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

    val myRef = database.getReference("temporary_session_program")
    myRef.addValueEventListener(object : ValueEventListener {
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

    val myRef = database.getReference("sessions")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val sessions : ArrayList<Session> = ArrayList<Session>()
            for(value in dataSnapshot.children ) {
                var exosSession : ArrayList<SessionExercice> = ArrayList()
                var session : Session = Session(value.child("sessionID").value.toString(),value.child("userID").value.toString(),value.child("nameSession").value.toString(),value.child("descSession").value.toString(),value.child("levelSession").value.toString(),exosSession,value.child("nbrRound").value.toString().toInt())
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
                var sessionProgram : SessionProgram = SessionProgram(value.child("idSessionTemp").value.toString(), value.child("sessionID").value.toString(), value.child("nameSession").value.toString(),value.child("userID").value.toString())
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
    val database = FirebaseDatabase.getInstance()
    val dbSession = database.getReference("temporary_session_program")
    val newId = dbSession.push().key
    Log.d("id",session.userID)
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }
    var newSession : Session = Session(session.sessionID, idUser,session.nameSession,session.descSession,session.levelSession,session.exosSession,session.nbrRound)
    dbSession.child(newId).setValue(newSession)
    dbSession.child(newId).child("idSessionTemp").setValue(newId)
    return 0
}

private fun sessionChooseProgramClicked(context:Context, sessionItem : Session, idUser: String, database : FirebaseDatabase) {

    addTemporarySessionProgram(database, idUser, sessionItem)
    val intent = Intent(context, ProgramActivity::class.java)
    context.startActivity(intent)
}

private fun sessionProgramClicked(context:Context, sessionItem : SessionProgram, database : FirebaseDatabase) {


}

private fun sessionsFeedClicked(context: Context, sessionItem: SessionFeed, database: FirebaseDatabase){

}

private fun deleteSessionProgramClicked(firebase : FirebaseDatabase, sessionItem : SessionProgram) {
    Log.d("delete", sessionItem.sessionProgID)
    sessionItem.sessionProgID?.let { deleteSessionProgram(firebase, it) }
}


