package isen.cedriclucieflorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.cedriclucieflorent.benfit.models.Post
import kotlinx.android.synthetic.main.activity_write.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WritePostActivity : MenuActivity() {

    private lateinit var imageUri : Uri
    private lateinit var storageReference: StorageReference
    private var uniqPostID = ""
    private lateinit var stu: StreamToUri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_write, frameLayout)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        stu = StreamToUri(this, this, contentResolver)

        imageUri = Uri.EMPTY
        val intent = intent
        val programId = intent.getStringExtra("sharedProgram")?: ""
        val sessionId = intent.getStringExtra("sharedSession")?: ""
        val exoId = intent.getStringExtra("sharedExo")?: ""
        val sharedName = intent.getStringExtra("sharedName")?: ""
        when {
            programId != "" -> sharedLink.text = sharedName
            sessionId != "" -> sharedLink.text = sharedName
            exoId != "" -> sharedLink.text = sharedName
            else -> {
                sharedLink.visibility = View.INVISIBLE
                imageViewSharedLink.visibility = View.INVISIBLE
            }
        }

        publishBTN.setOnClickListener{
            val userid = auth.currentUser?.uid

            if (userid != null){
                if(publish_field.text.toString() != ""){
                    newPost(userid, publish_field.text.toString(), programId, sessionId, exoId)
                    publish_field.setText("")
                    startActivity(Intent(this, FeedActivity::class.java))
                }
                else{
                    Toast.makeText(this, "Publication vide", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_LONG).show()
            }
        }

        imageViewWritePost.setOnClickListener{
            stu.askCameraPermissions()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stu.manageActivityResult(requestCode, resultCode, data)
        imageUri = stu.imageUri
        imageViewWritePost.setImageURI(imageUri)
    }

    private fun newPost(userId: String, content: String,
                        programId : String, sessionId : String, exoId : String) {
        val dbPosts = database.getReference("posts")
        val newId = dbPosts.push().key
        if (newId == null) {
            Log.w("ERROR", "Couldn't get push key for posts")
            return
        }

        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        val array : ArrayList<String> = ArrayList()
        val post = Post(userId, newId, currentDateandTime, content,array, "", programId, sessionId, exoId)
        dbPosts.child(newId).setValue(post)

        if (imageUri != Uri.EMPTY) {
        val postsImgRef = storageReference.child("posts/$newId/$uniqPostID")
        val result = postsImgRef.putFile(imageUri)
            result.addOnSuccessListener {
                dbPosts.child(newId).child("postImgUID").setValue(uniqPostID)
            }
        }
        else {dbPosts.child(newId).child("postImgUID").setValue("null")}

    }

}