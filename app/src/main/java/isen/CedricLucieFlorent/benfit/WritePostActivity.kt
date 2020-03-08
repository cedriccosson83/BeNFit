package isen.CedricLucieFlorent.benfit

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.Models.Post
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.activity_write.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WritePostActivity : MenuActivity() {


    private val code_perm_image = 101
    private val code_req_image = 102
    private val code_res_ext = 101
    private lateinit var image_uri : Uri
    private lateinit var storageReference: StorageReference
    private var uniqPostID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_write, frameLayout)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()
        image_uri = Uri.EMPTY
        val intent = intent
        val programId = intent.getStringExtra("sharedProgram")?: ""
        val sessionId = intent.getStringExtra("sharedSession")?: ""
        val exoId = intent.getStringExtra("sharedExo")?: ""
        val sharedName = intent.getStringExtra("sharedName")?: ""
        if (programId != "") {
            Log.d("CEDRIC_prog", programId)
            sharedLink.text = sharedName
        } else if (sessionId != "") {
            Log.d("CEDRIC_sess", sessionId)
            sharedLink.text = sharedName
        } else if (exoId != "") {
            Log.d("CEDRIC_exo", exoId)
            sharedLink.text = sharedName
        } else {
            sharedLink.visibility = View.INVISIBLE
        }

        publishBTN.setOnClickListener{
            val userid = auth.currentUser?.uid

            if (userid != null){
                if(publish_field.text.toString() != ""){
                    newPost(userid, publish_field.text.toString(), programId, sessionId, exoId)
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

        imageViewWritePost.setOnClickListener{
            askCameraPermissions()
        }

    }

    private fun askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), code_perm_image)
        }
        else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), code_res_ext)
        }
        else { openCamera()}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == code_perm_image){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                openCamera()
            }else{
                Toast.makeText(this, "Camera permissions required", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DESCRIPTION, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        var notsureuri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (notsureuri != null ){
            image_uri = notsureuri}
        val imagefromgalleryIntent = Intent(Intent.ACTION_PICK)
        imagefromgalleryIntent.setType("image/png")
        imagefromgalleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)


        val chooseIntent= Intent.createChooser(imagefromgalleryIntent, "Gallery")
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        startActivityForResult(chooseIntent, code_req_image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val userid = auth.currentUser?.uid ?: ""
        if (requestCode == code_req_image){
            if(data?.data == null){
                    imageViewWritePost.setImageURI(image_uri)
                    uniqPostID = UUID.randomUUID().toString()
            }else{
                uniqPostID = UUID.randomUUID().toString()
                imageViewWritePost.setImageURI(data.data)
                image_uri = data.data as Uri
            }
        }
    }

    private fun newPost(userId: String, content: String, programId : String, sessionId : String, exoId : String) {
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
        val post = Post(userId, newId, currentDateandTime, content,array, "", programId, sessionId, exoId)
        dbPosts.child(newId).setValue(post)

        // Post photo
        if (image_uri != Uri.EMPTY) {
        val PostsImgRef = storageReference.child("posts/$newId/$uniqPostID")
        val result = PostsImgRef.putFile(image_uri)
            result.addOnSuccessListener {
                dbPosts.child(newId).child("postImgUID").setValue(uniqPostID)
            }
        }
        else {dbPosts.child(newId).child("postImgUID").setValue("null")}

    }

}