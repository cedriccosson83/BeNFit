package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Functions.removePassedNotif
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.*

class SignInActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    
    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_sign_in)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        if (user != null) {
            updateUI(user)
        }

        signInButton.setOnClickListener{
            if (mailEditTextSignIn.text.toString().isNotEmpty()) {
                signin()
            }
            else {
                Toast.makeText(this,getString(R.string.mail_mdp_incorrect), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signin() {
        auth.signInWithEmailAndPassword(mailEditTextSignIn.text.toString(), passwordEditTextSignIn.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null ) {
            val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy")
            val date = sdf.format(Date())
            database.getReference("users")
                .child(user.uid).child("lastConn")
                .setValue(date)

            val currentTime = sdf.format(Calendar.getInstance().time)
            removePassedNotif(database, user.uid, currentTime)
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.mail_mdp_incorrect), Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        noAccountButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


}
