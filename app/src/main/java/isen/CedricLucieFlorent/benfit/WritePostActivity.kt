package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_write.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WritePostActivity : MenuActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_write, frameLayout)

        auth = FirebaseAuth.getInstance()

        publishBTN.setOnClickListener{
            val userid = auth.currentUser?.uid

            if (userid != null){
                if(publish_field.text.toString() != ""){
                    newPost(userid, publish_field.text.toString())
                    publish_field.setText("")
                    Toast.makeText(this, "Post publié!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)

                }
                else{
                    Log.d("erreur", "vide")
                    Toast.makeText(this, "Publication vide", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun newPost(userId: String, content: String) {

        val dbPosts = database.getReference("posts")
        val newId = dbPosts.push().key
        if (newId == null) {
            Log.w("ERROR", "Couldn't get push key for posts")
            return
        }

        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        Log.d("heure", currentDateandTime)
        val array : ArrayList<String> = ArrayList()
        val post = Post(userId, newId, currentDateandTime, content,array)
        dbPosts.child(newId).setValue(post)
    }



}