package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_list_exercices.*

class ListExercicesActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_exercices)
        auth = FirebaseAuth.getInstance()


        showExos(database, recycler_view_list_exo, this)
        recycler_view_list_exo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


}
