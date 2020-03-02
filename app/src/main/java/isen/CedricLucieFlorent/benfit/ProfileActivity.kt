package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.ArrayList

class ProfileActivity : MenuActivity() {

    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_profile, frameLayout)
        overridePendingTransition(R.anim.zoom_enter, 0)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        context = this

        val intent = intent
        val userFromIntent = intent.getStringExtra("userId")
        if (intent != null)
            if (userId != userFromIntent) {
                showUser(userFromIntent)
                settingsButton.visibility = View.INVISIBLE
            }
            else {
                showUser(userId)
            }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, ModifyProfile::class.java))
        }

        myProgramButton.setOnClickListener(){
            toast(context, "mes programmes actif")
        }

        subscribeProgramButton.setOnClickListener(){
            toast(context, "mes programmes suivis actif")
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
                        ArrayList(value.child("sports").children.map { Sport(it.child("name").value.toString(), arrayListOf()) }),
                        value.child("weight").value.toString(),
                        value.child("pictureUID").value.toString()
                    )
                    if (user.userid == userId) {
                        val imagePath = user.pictureUID
                        var sportsaff = ""
                        for (sport in user.sports){
                            sportsaff += "${sport.getSportName()}" + " "
                        }
                        fullNameTextView.text = "${user.firstname} ${user.lastname}"
                        descriptionTextView.text = "Date de naissance : ${user.birthdate.toString()}" + "\nEmail : ${user.email} " + "\nPoids : ${user.weight} kg"  + "\nSport(s) pratiqué(s) : ${sportsaff}"
                        setImageFromFirestore(context, ProfilImage, "users/$userId/$imagePath")

                        ProfilImage.setOnClickListener {
                            val fullScreenIntent = Intent(context, FullScreenImageView::class.java)
                            fullScreenIntent.putExtra("url", "users/$userId/$imagePath")
                            startActivity(fullScreenIntent)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }
}
