package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat

import java.util.*


class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    lateinit var currUser: User

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
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
                val putsport = whatsport()
                val user = auth.currentUser
                registerNewUser(user,
                    firstnameEditTextSignUp.text.toString(),
                    lastnameEditTextSignUp.text.toString(),
                    birthdayEditTextSignUp.text.toString(),
                    putsport,
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

    private fun registerNewUser(user: FirebaseUser?, fname:String, lname:String, birthdate:String, sport:String, weight:String) {
        if (user?.uid != null) {
            val sdf = SimpleDateFormat("dd/mm/yyyy")
            val date = sdf.format(Date())
            currUser = User(user.uid, user.email, fname, lname, birthdate, sport, weight)
            val root = database.getReference("myusers")
            root.child(currUser.userid).setValue(currUser)

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, getString(R.string.vous_connecte)+ user.uid, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.vous_avez_un_compte), Toast.LENGTH_LONG).show()
        }
    }

    fun whatsport():String{
        if(checkBoxCourse.isChecked()){
            return "Course"
        }
        else if(checkBoxMuscu.isChecked()){
            return "Musculation"
        }
        else if(checkBoxVelo.isChecked()){
            return "Cyclisme"
        }
        else if(checkBoxVelo.isChecked() && checkBoxMuscu.isChecked()){
            return "Cyclisme et musculation"
        }
        else if(checkBoxVelo.isChecked() && checkBoxCourse.isChecked()){
            return "Cyclisme et course"
        }
        else if(checkBoxCourse.isChecked() && checkBoxMuscu.isChecked()){
            return "Course et musculation"
        }
        else if(checkBoxVelo.isChecked() && checkBoxMuscu.isChecked() && checkBoxCourse.isChecked()){
            return "Cyclisme, musculation et course"
        }
        return "aucun"
    }
}
