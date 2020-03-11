package isen.cedriclucieflorent.benfit

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.cedriclucieflorent.benfit.models.Post
import kotlinx.android.synthetic.main.activity_write.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WritePostActivity : MenuActivity() {

    private val codePermImage = 10115
    private val codeReqImage = 10116
    private val codeResExt = 10117
    private lateinit var imageUri : Uri
    private lateinit var storageReference: StorageReference
    private var uniqPostID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_write, frameLayout)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
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
            askCameraPermissions()
        }

    }

    private fun askCameraPermissions(){
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        -> ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), codePermImage)
            ContextCompat.checkSelfPermission(
                this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        -> ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.CAMERA), codeResExt)
            else -> openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == codePermImage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }

    private fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DESCRIPTION, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        val notsureuri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (notsureuri != null ){
            imageUri = notsureuri}
        val imagefromgalleryIntent = Intent(Intent.ACTION_PICK)
        imagefromgalleryIntent.type = "image/png"
        imagefromgalleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)


        val chooseIntent= Intent.createChooser(imagefromgalleryIntent, getString(R.string.chooseImage))
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        startActivityForResult(chooseIntent, codeReqImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == codeReqImage){
                if(data?.data == null){
                    imageViewWritePost.setImageURI(imageUri)
                    uniqPostID = UUID.randomUUID().toString()
                }else{
                    uniqPostID = UUID.randomUUID().toString()
                    imageViewWritePost.setImageURI(data.data)
                    imageUri = data.data as Uri
                }
            }
        }
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