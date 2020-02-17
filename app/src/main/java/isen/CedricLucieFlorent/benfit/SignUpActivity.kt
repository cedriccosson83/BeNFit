package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    lateinit var currUser: User
    var sportSelected = ArrayList<Sport>()


    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val showdiffsportss = findViewById<TextView>(R.id.showsporttextView)



        val sportArray = arrayListOf<String>()

        val myRef = database.getReference("sports")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    sportArray.add(it.child("name").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }

        })

        sportTewtView.setOnClickListener(){
            val checkedColorsArray = BooleanArray(166)
            val sportList = sportArray.toList()
            AlertDialog.Builder(this@SignUpActivity)
                .setTitle("Select colors")
                .setMultiChoiceItems(
                    sportArray.toTypedArray(),
                    checkedColorsArray
                ) { _, which, isChecked ->
                    // Update the current focused item's checked status
                    checkedColorsArray[which] = isChecked
                    // Get the current focused item
                    val currentItem = sportList[which]
                    // Notify the current action
                    Toast.makeText(
                        applicationContext,
                        "$currentItem $isChecked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setPositiveButton("OK") { dialog, which ->
                    showdiffsportss.text = "Vos sports préférés..... \n"
                    for (i in checkedColorsArray.indices) {
                        val checked = checkedColorsArray[i]
                        if (checked) {
                            showdiffsportss.text = showdiffsportss.text.toString() + sportList[i] + "\n"
                            sportSelected.add(Sport(sportList[i], ArrayList()))
                        }
                    }
                }
                .create()
                .show()

        }
        subscribeButton.setOnClickListener {

             if (mailEditTextSignUp.text.toString().isNotEmpty()) {
                signup()
            }
            else {
                Toast.makeText(this, getString(R.string.mail_mdp_incorrect), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signup() {
        auth.createUserWithEmailAndPassword(
            mailEditTextSignUp.text.toString(),
            passwordEditTextSignUp.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //val putsport = ArrayList<String>()
                val user = auth.currentUser
                registerNewUser(user,
                    firstnameEditTextSignUp.text.toString(),
                    lastnameEditTextSignUp.text.toString(),
                    birthdayEditTextSignUp.text.toString(),
                    sportSelected,
                    weightEditText.text.toString()
                    )
                updateUI(user)
            } else {
                Toast.makeText(baseContext, getString(R.string.err_inscription), Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        accountExistButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun registerNewUser(user: FirebaseUser?, fname:String, lname:String, birthdate:String, sports:ArrayList<Sport>, weight:String) {
        if (user?.uid != null) {
            val sdf = SimpleDateFormat("dd/mm/yyyy")
            val date = sdf.format(Date())
            currUser = User(user.uid, user.email, fname, lname, birthdate,sports, weight)
            val root = database.getReference("users")
            root.child(currUser.userid).setValue(currUser)

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, getString(R.string.vous_connecte)+ user.uid, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, ProfileActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.vous_avez_un_compte), Toast.LENGTH_LONG).show()
        }
    }

}
