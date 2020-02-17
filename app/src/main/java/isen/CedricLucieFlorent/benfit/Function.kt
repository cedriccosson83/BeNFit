package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Exercice
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.recycler_view_exo_cell.*


fun addNewExo(database : FirebaseDatabase, nameExo: String,descExo: String, urlYtb: String, levelExo: String, sportExo: String) : Int{
    val database = FirebaseDatabase.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("ERROR", "Couldn't get push key for exos")
        return -1
    }

    val exo = Exercice(newId,nameExo,"test",descExo, urlYtb,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return 0
}


//This function get the posts on the database and show them on the feed
fun showExos(database : FirebaseDatabase, view: RecyclerView, context: Context) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {


                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("author").value.toString(), value.child("description").value.toString(),value.child("urlYTB").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
                exos.add(exo)
            }
            exos.reverse()
            view.adapter = ExoAdapter(exos,  { exoItem : Exercice -> exoClicked(context,exoItem) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}

//This function get the posts on the database and show them on the feed
fun showExosSession(database : FirebaseDatabase, view: RecyclerView) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList<Exercice>()
            for(value in dataSnapshot.children ) {


                var exo : Exercice = Exercice(value.child("id").value.toString(), value.child("name").value.toString(), value.child("author").value.toString(), value.child("description").value.toString(),value.child("urlYTB").value.toString(),value.child("difficulty").value.toString(), value.child("sport").value.toString())
                exos.add(exo)
            }
            exos.reverse()
            view.adapter = ExoSessionAdapter(exos,  { exoItem : Exercice -> exoClickedSession( exoItem) } )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}


private fun exoClicked(context:Context, exoItem : Exercice) {
    Log.d("exo", exoItem.name)
    val intent = Intent(context, SessionActivity::class.java)

    context.startActivity(intent)
}

private fun exoClickedSession(exoItem : Exercice) {

}

