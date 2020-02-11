package isen.CedricLucieFlorent.benfit

import android.util.Log


/*fun newExercice(auth : FirebaseAuth,idExo : String, nameExo: String, urlYTExo: String, descExo: String){

    val database = FireBase.getInstance()
    auth = FirebaseAuth.getInstance()
    val dbExos = database.getReference("exos")
    val newId = dbExos.push().key
    if (newId == null) {
        Log.w("ERROR", "Couldn't get push key for posts")
        return
    }
    val exo = Exercice(newId, nameExo,urlYTExo,descExo)
    dbExos.child(newId).setValue(exo)
}*/