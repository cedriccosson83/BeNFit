package isen.cedriclucieflorent.benfit.functions

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.*
import isen.cedriclucieflorent.benfit.models.Exercice
import isen.cedriclucieflorent.benfit.models.SessionExercice
import isen.cedriclucieflorent.benfit.models.ShowExerciceSession
import kotlinx.android.synthetic.main.activity_exercice_session.view.*

fun addNewExo(database : FirebaseDatabase, nameExo: String, idUser: String, descExo: String, levelExo: String, sportExo: String) : String{
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key ?: return "false"
    val exo = Exercice(newId,nameExo,idUser,descExo,levelExo,sportExo)
    dbExos.child(newId).setValue(exo)
    return newId
}
fun addTemporaryExoSession(database : FirebaseDatabase, idUser:String, exo : Exercice) : Int{
    val dbExos = database.getReference("temporary_exos_session")
    val newId = dbExos.push().key
    if(newId == null){
        Log.d("TAG", "Couldn't get push key for exos")
        return -1
    }
    val newExo = SessionExercice(newId, idUser,exo.id, "0")
    dbExos.child(newId).setValue(newExo)
    return 0
}
fun showInfosRep(database :  FirebaseDatabase, activity: View, userId: String){
    val myRef = database.getReference("temporary_exos_session")
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                if(value.key == userId){
                    val valueRep  = value.child("rep").value.toString().split(" ")
                    if(valueRep.size > 1){
                        activity.inputValueExo.setText(valueRep[0])
                        activity.inputUnitExo.setText(valueRep[1])
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
fun showExos(database : FirebaseDatabase, view: RecyclerView, context: Context, userId: String) {

    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            val exos : ArrayList<Exercice> = ArrayList()
            for(value in dataSnapshot.children ) {
                val exo = Exercice(
                    value.child("id").value.toString(),
                    value.child("name").value.toString(),
                    value.child("idUser").value.toString(),
                    value.child("description").value.toString(),
                    value.child("difficulty").value.toString(),
                    value.child("sport").value.toString(),
                    value.child("pictureUID").value.toString(),
                    value.child("urlYt").value.toString())
                exos.add(exo)
            }
            exos.reverse()
            view.adapter = ExoAdapter(exos) {
                    exoItem : Exercice -> exoChooseSessionClicked(context,exoItem, database, userId) }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("post", "Failed to read value.", error.toException())
        }
    })

}
fun showExo(database: FirebaseDatabase, exoId : String, textView: TextView, imageView : ImageView) {
    val myRef = database.getReference("exos")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot){
            for(value in dataSnapshot.children ) {
                val exo = Exercice(
                    value.child("id").value.toString(),
                    value.child("name").value.toString(),
                    value.child("idUser").value.toString(),
                    value.child("description").value.toString(),
                    value.child("difficulty").value.toString(),
                    value.child("sport").value.toString(),
                    value.child("pictureUID").value.toString(),
                    value.child("urlYt").value.toString())
                if(exo.id == exoId){
                    textView.text = exo.name
                    if (exo.pictureUID != "" && exo.pictureUID != "null"){
                        setImageFromFirestore(imageView, "exos/${exo.id}/${exo.pictureUID}")
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun deleteExoSession(database : FirebaseDatabase, exoSessionId :String) {
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
}
fun deleteExoSessionTemp(database : FirebaseDatabase, userID :String) {

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
}
fun exoChooseSessionClicked(context:Context, exoItem : Exercice, database : FirebaseDatabase, userId :String) {

    addTemporaryExoSession(database,userId, exoItem)
    val intent = Intent(context, SessionActivity::class.java)
    context.startActivity(intent)
}
fun deleteExoSessionClicked(firebase : FirebaseDatabase, exoItem : SessionExercice) {
    deleteExoSession(firebase, exoItem.exoSessionID)
}

fun showPopUpDetails(database : FirebaseDatabase, context: Context, exoItem : SessionExercice){
    val exoID = exoItem.exoID
    val exoSessionID = exoItem.exoSessionID
    val img = ImageView(context)
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.activity_exercice_session)
    showExo(database, exoID, dialog.findViewById(R.id.textNameExoSession), img)
    showInfosRep(database, dialog.findViewById(R.id.constraintLayoutDetails) , exoSessionID)

    val btnDone = dialog.findViewById<Button>(R.id.btnDoneExoDetails)
    btnDone.setOnClickListener {
        val value = dialog.findViewById<EditText>(R.id.inputValueExo).text.toString()
        val unit: String = dialog.findViewById<EditText>(R.id.inputUnitExo).text.toString()
        val strCombined = ApplicationContext.applicationContext().getString(
            R.string.doubleWordsSpaced,
            value,
            unit
        )
        updateRepExoSession(database, exoSessionID, strCombined)
        dialog.dismiss()
    }
    dialog.show()
}
fun showPopUpExercice(database: FirebaseDatabase, context : Context, exoID: String, sessionParent: String? = "") {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.popup_show_exo)
    dialog.setTitle("titre")

    val dbRef = database.getReference("exos/$exoID")

    dbRef.addValueEventListener(object : ValueEventListener {
        @SuppressLint("SetJavaScriptEnabled")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val exercice = dataSnapshot.getValue(ShowExerciceSession::class.java)
            if (exercice != null) {
                showUserName(exercice.idUser,dialog.findViewById(R.id.showExoAuthor))
                convertLevelToImg(exercice.difficulty,dialog.findViewById(R.id.showExoLevelIcon))
                dialog.findViewById<TextView>(R.id.showExoName).text = exercice.name
                dialog.findViewById<TextView>(R.id.showExoDesc).text = exercice.description
                dialog.findViewById<TextView>(R.id.showExoLevelText).text = exercice.difficulty
                dialog.findViewById<TextView>(R.id.showExosport).text = exercice.sport
                dialog.findViewById<TextView>(R.id.showExoAuthor).setOnClickListener{
                    redirectToUserActivity(context, exercice.idUser)
                }
                dialog.findViewById<ImageView>(R.id.showExoShare).setOnClickListener {
                    val writePostIntent = Intent(context, WritePostActivity::class.java)
                    writePostIntent.putExtra("sharedExo", exoID)
                    writePostIntent.putExtra("sharedName", exercice.name)
                    context.startActivity(writePostIntent)
                }

                dialog.findViewById<ImageView>(R.id.showExoDismiss).setOnClickListener {
                    dialog.dismiss()
                }

                setRulesIfInSession(database,dialog.findViewById(R.id.showExoRuleValue),
                    dialog.findViewById(R.id.showExoRule),exercice.id,sessionParent)

                val videoWeb : WebView = dialog.findViewById(R.id.showExoYTLayout)


                when {
                    exercice.pictureUID != "" -> {
                        val layout = dialog.findViewById<LinearLayout>(R.id.showExoMediaLayout)
                        val exoImView = ImageView(context)
                        setImageFromFirestore(exoImView, "exos/${exercice.id}/${exercice.pictureUID}")

                        layout.addView(exoImView)
                        exoImView.layoutParams.height = 400

                        exoImView.setOnClickListener {
                            val fullScreenIntent = Intent(ApplicationContext.applicationContext(), FullScreenImageView::class.java)
                            fullScreenIntent.putExtra("url", "exos/${exercice.id}/${exercice.pictureUID}")
                            fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            ApplicationContext.applicationContext().startActivity(fullScreenIntent)
                        }
                        videoWeb.visibility = View.INVISIBLE
                    }
                    exercice.urlYt != "" -> {

                        videoWeb.visibility = View.VISIBLE
                        videoWeb.setInitialScale(1)
                        videoWeb.settings.loadWithOverviewMode = true
                        videoWeb.settings.useWideViewPort = true
                        var ytUrl = exercice.urlYt.replace("watch?v=", "embed/")
                        ytUrl = ytUrl.replace("youtu.be", "youtube.com/embed/")

                        val url = "<iframe width=\"100%\" height=\"100%\" src=\"$ytUrl\" frameborder=\"0\" allowfullscreen></iframe>"

                        videoWeb.settings.javaScriptEnabled = true
                        videoWeb.loadData(url, "text/html" , "utf-8" )
                        videoWeb.webChromeClient = WebChromeClient()
                    }
                    else -> videoWeb.visibility = View.INVISIBLE
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
    dialog.show()
}