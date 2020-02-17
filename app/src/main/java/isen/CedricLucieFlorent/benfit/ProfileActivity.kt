package isen.CedricLucieFlorent.benfit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""

        val intent = intent

        if (intent != null) {
            if (userId != "") {
                showUser(userId)
            }
        }
        settingsButton.setOnClickListener{
            startActivity(Intent(this, ModifyProfile::class.java))
        }

        sportLevelImageView.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    fun showUser(userId: String) {
        val myRef = database.getReference("users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user: User?
                for (value in dataSnapshot.children) {
                    user = User(
                        value.child("userid").value.toString(),
                        value.child("email").value.toString(),
                        value.child("firstname").value.toString(),
                        value.child("lastname").value.toString(),
                        value.child("birthdate").value.toString(),
                        ArrayList(value.child("sports").children.map { Sport(it.value.toString(), arrayListOf()) }),
                        value.child("weight").value.toString()

                    )
                    if (user?.userid == userId) {
                        fullNameTextView.text = "${user.firstname} ${user.lastname}"
                        descriptionTextView.text = "Date de naissance : ${user.birthdate.toString()}" + "\nEmail : ${user.email} " + "\nPoids : ${user.weight}" + "\nSport(s) pratiqué(s) : ${user.sports.map { it.name }}"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }
}
