package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_exercice.*
import kotlinx.android.synthetic.main.activity_session.*

class ExerciceActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()

    //var etatFragment : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercice)
        auth = FirebaseAuth.getInstance()


        createSpinnerCategory()
        createSpinnerLevel()

        btnDoneExo.setOnClickListener {
            var nameExo : String = inputNameExo.text.toString()
            var descExo : String = inputDescExo.text.toString()
            var urlExo : String = inputURLExo.text.toString()

            var categoryExo :String = spinnerSportExo.selectedItem.toString()
            var levelExo : String = spinnerLevelExo.selectedItem.toString()


            var res_request = addNewExo(database,nameExo,descExo,urlExo,levelExo,categoryExo)

            if(res_request == 0){
                Toast.makeText(this, "Nouvel exercice créé !", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Erreur! Veuillez réessayer!", Toast.LENGTH_SHORT).show()

            }



        }
        /*val firstFragment = FragmentRepExo()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, firstFragment).commit()

        btnRep.setOnClickListener {
            val firstFragment = FragmentRepExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, firstFragment).commit()
        }

        btnTime.setOnClickListener {
            val secondFragment = FragmentTimeExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, secondFragment).commit()
        }*/


    }

    /*override fun swipeFragment() {
        if(etatFragment == 0){
            val secondFragment = FragmentTimeExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, secondFragment).commit()
            etatFragment = 1
        }else{
            val firstFragment = FragmentRepExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, firstFragment).commit()
            etatFragment = 0
        }

    }*/

    fun createSpinnerLevel(){
        val spinner: Spinner = findViewById(R.id.spinnerLevelExo)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.level_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    fun createSpinnerCategory(){
        val spinner: Spinner = findViewById(R.id.spinnerSportExo)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.category_exo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

    }





}
