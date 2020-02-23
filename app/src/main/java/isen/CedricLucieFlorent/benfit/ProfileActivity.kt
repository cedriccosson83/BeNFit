package isen.CedricLucieFlorent.benfit

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*
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

        if (intent != null)
            if (userId != "")
                showUser(userId)

        settingsButton.setOnClickListener {
            startActivity(Intent(this, ModifyProfile::class.java))
        }

        sportLevelImageView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
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
                        value.child("weight").value.toString(),
                        value.child("pictureUID").value.toString()
                    )
                    if (user.userid == userId) {
                        val imagePath = user.pictureUID
                        fullNameTextView.text = "${user.firstname} ${user.lastname}"
                        descriptionTextView.text = "Date de naissance : ${user.birthdate.toString()}" + "\nEmail : ${user.email} " + "\nPoids : ${user.weight}" + "\nSport(s) pratiqué(s) : ${user.sports.map { it.name }}"
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
