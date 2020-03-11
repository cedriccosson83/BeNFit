package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Functions.showExos
import kotlinx.android.synthetic.main.activity_list_exercices.*
import kotlinx.android.synthetic.main.activity_list_session.*

class ListExercicesActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_list_session, frameLayout)
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid


        if (id != null) {
            showExos(database, recycler_view_list_session, this,id)
        }
        recycler_view_list_session.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


}
