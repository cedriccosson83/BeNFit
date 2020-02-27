package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_exercice_session.*
import kotlinx.android.synthetic.main.fragment_rep.*
import kotlinx.android.synthetic.main.fragment_time.*
import kotlinx.android.synthetic.main.recycler_view_exo_session.*
import kotlinx.android.synthetic.main.recycler_view_exo_session.view.*

class ExerciceSessionActivity : MenuActivity(), OnFragmentInteractionListener  {
    var etatFragment : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_exercice_session)
        layoutInflater.inflate(R.layout.activity_exercice_session, frameLayout)
        val database = FirebaseDatabase.getInstance()

        val intent = intent
        val exoIdSession: String? = intent.getStringExtra("idExoSession")
        val exoId: String? = intent.getStringExtra("id")
        if (exoId != null) {
            showExo(database, exoId,  textNameExoSession)
        }

        val firstFragment = FragmentRepExo()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, firstFragment).commit()

        btnRep.setOnClickListener {
            val repFragment = FragmentRepExo()
            etatFragment = 0
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, repFragment).commit()
        }

        btnTime.setOnClickListener {
            val timeFragment = FragmentTimeExo()
            etatFragment = 1
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, timeFragment).commit()
        }

        btnDoneExo.setOnClickListener {

            if(etatFragment == 0){
                val numberPickerRep = numberPickerRep.value
                Log.d("sessionExo", numberPickerRep.toString())
                if (exoIdSession != null) {
                    updateRepExoSession(database,exoIdSession , numberPickerRep.toString())
                }
            }
            if(etatFragment == 1){
                val numberPickerMin = numberPickerMin.value
                val numberPickerSec = numberPickerSec.value
                if (exoIdSession != null) {
                    updateRepExoSession(database,exoIdSession , "${numberPickerMin}:${numberPickerSec}")
                }
                Log.d("sessionExo", "${numberPickerMin} : ${numberPickerSec}")

            }

            val intent = Intent(this,SessionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


        }
    }

    override fun swipeFragment() {
        if(etatFragment == 0){
            val secondFragment = FragmentTimeExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, secondFragment).commit()
            etatFragment = 1
        }else{
            val firstFragment = FragmentRepExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, firstFragment).commit()
            etatFragment = 0
        }

    }

}
