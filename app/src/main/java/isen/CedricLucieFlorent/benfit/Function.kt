package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Exercice
import isen.CedricLucieFlorent.benfit.Models.SessionExercice


fun addNewExo(database : FirebaseDatabase, nameExo: String, idUser: String, descExo: String, urlYtb: String, levelExo: String, sportExo: String) : Int{
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }
    val exo = Exercice(newId,nameExo,idUser,descExo, urlYtb,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return 0
}

fun addTemporaryExoSession(database : FirebaseDatabase, idUser:String, exo : Exercice) : Int{
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("temporary_exos_session")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }
    dbExos.child(newId).setValue(exo)
    return 0
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

//This function get the posts on the database and show them on the feed
fun showExosSession(database : FirebaseDatabase, view: RecyclerView, userId :String) {

    val myRef = database.getReference("temporary_exos_session")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {


                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("idUser").value.toString(), value.child("description").value.toString(),value.child("urlYTB").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())


                if(exo.idUser== userId){
                    exos.add(exo)
                }
            }
            exos.reverse()
            view.adapter = ExoSessionAdapter(exos,  { exoItem : Exercice -> exoClickedSession( exoItem) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}


//This function get the posts on the database and show them on the feed
fun deleteExoSession(database : FirebaseDatabase, userId :String) {

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


private fun exoChooseSessionClicked(context:Context, exoItem : Exercice, database : FirebaseDatabase, userId :String) {
    Log.d("exo", exoItem.name)

    addTemporaryExoSession(database,userId, exoItem)
    val intent = Intent(context, SessionActivity::class.java)
    context.startActivity(intent)
}

private fun exoClickedSession(exoItem : Exercice) {

}

