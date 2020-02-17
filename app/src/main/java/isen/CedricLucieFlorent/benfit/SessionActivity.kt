package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_session.*


class SessionActivity : AppCompatActivity(){
    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        auth = FirebaseAuth.getInstance()
        showExosSession(database, recyclerViewExoSession)
        recyclerViewExoSession.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        createSpinnerLevel()
        showPopMenuExo()

    }

    override fun onResume() {
        super.onResume()
        Log.d("state", "onresume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("state", "onrestart")

    }


    fun showPopMenuExo(){
        buttonAddExoSession.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                   R.id.menu_create_exo -> {
                        val intent = Intent(this,ExerciceActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    R.id.menu_choose_exo -> {
                        val intent = Intent(this,ListExercicesActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_exo)
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception){
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }
    }
    fun createSpinnerLevel(){
        val spinner: Spinner = findViewById(R.id.spinnerLevelSession)

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

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Log.d("niveau", spinnerLevelSession.getItemAtPosition(position).toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                //Log.d("niveau", spinnerLevelSession.getItemAtPosition(0).toString())
            }
        }
    }


}
