package isen.CedricLucieFlorent.benfit

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_modify_profile.*

class ModifyProfile : MenuActivity() {

    lateinit var userId: String
    lateinit var currUser: User

    var sportSelectedModif  = ArrayList<Sport>()

    private lateinit var storageReference: StorageReference
    val sportSel = arrayListOf<String>()

    private val code_perm_image = 101
    private val code_req_image = 102
    private val code_res_ext = 101
    private lateinit var image_uri : Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_modify_profile, frameLayout)

        val showdiffsports = findViewById<TextView>(R.id.showSports)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()

        userId = auth.currentUser?.uid ?: ""

        val intent = intent

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



        val mySpo = database.getReference("users").child(userId)
        mySpo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.child("sports").children.forEach {
                    sportSel.add(it.child("name").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }

        })

        if (intent != null) {
            if (userId != "") {
                showUserM(userId)
            }
        }

        changeProfilImageModify.setOnClickListener(){
            askCameraPermissions()
        }

        validateModifyButton.setOnClickListener(){
            val user = auth.currentUser
            ChangeUser(user, firstNameTextViewModify.text.toString(),lastNameTextViewModify.text.toString(), birthdateTextViewModify.text.toString(), "", weightTextViewModify.text.toString())
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        sportTextViewModify.setOnClickListener(){
            val checkedColorsArray = BooleanArray(166)
            for (sport in sportSel){
                if (sportArray.indexOf(sport) != -1){
                    checkedColorsArray[sportArray.indexOf(sport)] = true
                }
            }

            val sportList = sportArray.toList()
            AlertDialog.Builder(this@ModifyProfile)
                .setTitle("Select colors")
                .setMultiChoiceItems(
                    sportArray.toTypedArray(),
                    checkedColorsArray
                ) { _, which, isChecked ->
                    checkedColorsArray[which] = isChecked
                    val currentItem = sportList[which]
                    Toast.makeText(
                        applicationContext,
                        "$currentItem $isChecked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setPositiveButton("OK") { dialog, which ->
                    showdiffsports.text = "Vos sports préférés..... \n"
                    for (i in checkedColorsArray.indices) {
                        val checked = checkedColorsArray[i]
                        if (checked) {
                            showdiffsports.text = showdiffsports.text.toString() + sportList[i] + "\n"
                            sportSelectedModif.add(Sport(sportList[i], ArrayList()))
                        }
                    }
                }
                .create()
                .show()
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

    fun openCamera() {
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
        val riversRef = storageReference.child("users/$userid/profile.png")
        if (requestCode == code_req_image){
            if(data?.data == null){
                changeProfilImageModify.setImageURI(image_uri)
                riversRef.putFile(image_uri)
            }else{
                riversRef.putFile(data?.data as Uri)
                changeProfilImageModify.setImageURI(data?.data)
            }
        }
    }

    fun showUserM(userId: String) {
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
                        firstNameTextViewModify.setText("${user.firstname}")
                        lastNameTextViewModify.setText("${user.lastname}")
                        birthdateTextViewModify.setText("${user.birthdate.toString()}")
                        weightTextViewModify.setText("${user.weight}")
                        showSports.setText("${user.sports.map { it.name }}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }

    private fun ChangeUser(user: FirebaseUser?, fnamenew:String, lnamenew:String, birthdatenew:String, sportnew:String, weightnew:String) {
        if (user?.uid != null) {
            val root = database.getReference("users")
            root.child(user?.uid).child("firstname").setValue(fnamenew)
            root.child(user?.uid).child("lastname").setValue(lnamenew)
            root.child(user?.uid).child("birthdate").setValue(birthdatenew)
            root.child(user?.uid).child("weight").setValue(weightnew)
            if (sportSelectedModif.isNotEmpty()){ root.child(user?.uid).child("sports").setValue(sportSelectedModif)}
            else{root.child(user?.uid).child("sports").setValue(sportSel)}

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }
}
