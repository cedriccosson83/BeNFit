package isen.CedricLucieFlorent.benfit

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var image_uri : Uri
    private val code_perm_image = 101
    private val code_req_image = 102
    private val code_res_ext = 101
    private lateinit var storageReference: StorageReference
    private lateinit var pictureUID : String

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val showdiffsportss = findViewById<TextView>(R.id.showsporttextView)


        storageReference = FirebaseStorage.getInstance().getReference()

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
        birthdayEditTextSignUp.setOnFocusChangeListener( { view, hasFocus ->
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

        newPictureImageView.setOnClickListener{
            askCameraPermissions()
        }
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                openCamera()
            }else{
                Toast.makeText(this, "Camera permissions required", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DESCRIPTION, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        val notsureuri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
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
        if (requestCode == code_req_image){
            if(data?.data != null){
                image_uri = data.data as Uri
            }
            newPictureImageView.setImageURI(image_uri)
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
                //updateUI(user, userName)
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

        Log.d("IMAGEURI", image_uri.toString())
        var userName = ""
        if (user?.uid != null) {
            val sdf = SimpleDateFormat("dd/mm/yyyy")
            val date = sdf.format(Date())
            currUser = User(user.uid, user.email, fname, lname, birthdate,sports, weight)
            val root = database.getReference("users")
            root.child(currUser.userid).setValue(currUser)
            userName = currUser.firstname.toString()

            // Photo

            val uniqID = UUID.randomUUID().toString()
            //val riversRef = storageReference.child("users/${currUser.userid}/$uniqID")
            val riversRef = storageReference.child("users/${currUser.userid}/$uniqID")
            val result = riversRef.putFile(image_uri)
            result.addOnSuccessListener {
                database.getReference("users/${currUser.userid}/pictureUID").setValue(uniqID)
            }

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
