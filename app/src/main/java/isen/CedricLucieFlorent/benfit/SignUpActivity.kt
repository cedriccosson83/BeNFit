package isen.CedricLucieFlorent.benfit

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    lateinit var currUser: User
    var sportSelected = ArrayList<Sport>()
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val dateFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(dateFormat, Locale.FRANCE)
    var dayselec : Int = 0
    var monthselec : Int = 0
    var yearselec : Int = 0


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
        birthdayEditTextSignUp.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            c.set(Calendar.YEAR, year)
                            c.set(Calendar.MONTH, monthOfYear)
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            // Display Selected date in TextView
                            birthdateTextViewModify.setText(sdf.format(c.time))
                            dayselec = dayOfMonth
                            monthselec = monthOfYear
                            yearselec = year
                        },
                        year,
                        month,
                        day
                )
                dpd.show()
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
                val userName = registerNewUser(user,
                    firstnameEditTextSignUp.text.toString(),
                    lastnameEditTextSignUp.text.toString(),
                    birthdayEditTextSignUp.text.toString(),
                    sportSelected,
                    weightEditText.text.toString()
                    )
                updateUI(user, userName)
            } else {
                Toast.makeText(baseContext, getString(R.string.err_inscription), Toast.LENGTH_SHORT).show()
                updateUI(null, "")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        accountExistButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun registerNewUser(user: FirebaseUser?, fname:String, lname:String, birthdate:String, sports:ArrayList<Sport>, weight:String): String {
        var userName = ""
        if (user?.uid != null) {
            val sdf = SimpleDateFormat("dd/mm/yyyy")
            val date = sdf.format(Date())
            currUser = User(user.uid, user.email, fname, lname, birthdate,sports, weight)
            val root = database.getReference("users")
            root.child(currUser.userid).setValue(currUser)
            userName = currUser.firstname.toString()

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
        return userName
    }

    fun updateUI(user: FirebaseUser?, firstname : String) {
        if (user != null) {
            Toast.makeText(this, getString(R.string.welcomeBack) + " " + firstname + " !", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.vous_avez_un_compte), Toast.LENGTH_LONG).show()
        }
    }

}
