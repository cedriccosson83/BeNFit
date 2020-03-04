package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_program.*
import kotlinx.android.synthetic.main.activity_session.*

class ProgramActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_program, frameLayout)
        showPopMenuSession()
        createSpinnerLevel()
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        if (id != null) {
            showSessionsProgram(database,recyclerViewSessionProgram,this,id)
        }

        recyclerViewSessionProgram.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        btnSaveProgram.setOnClickListener {
            if (id != null) {
                saveProgram(database, id,inputNameProgram.text.toString(),inputDescProgram.text.toString(),spinnerLevelProgram.selectedItem.toString() )
                Toast.makeText(this,"Programme sauvegardé!", Toast.LENGTH_SHORT).show()
                deleteSessionsTempProgram(database,id)
                val intent = Intent(this,ProgramActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }
    }


    fun showPopMenuSession(){
        buttonAddSessionProgram.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.menu_create_session -> {
                        val intent = Intent(this,SessionActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    R.id.menu_choose_session -> {
                        val intent = Intent(this,ListSessionActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_session)
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
        val spinner: Spinner = findViewById(R.id.spinnerLevelProgram)

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

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                Log.d("niveau", spinnerLevelProgram.getItemAtPosition(position).toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                //Log.d("niveau", spinnerLevelSession.getItemAtPosition(0).toString())
            }
        }
    }

}
