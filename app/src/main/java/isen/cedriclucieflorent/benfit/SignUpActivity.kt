package isen.cedriclucieflorent.benfit

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.cedriclucieflorent.benfit.functions.getDrawableToURI
import isen.cedriclucieflorent.benfit.models.Sport
import isen.cedriclucieflorent.benfit.models.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    private lateinit var currUser: User
    private var sportSelected = ArrayList<Sport>()
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dateFormat = "dd/MM/yyyy"
    private val sdf = SimpleDateFormat(dateFormat, Locale.FRANCE)
    private var dayselec : Int = 0
    private var monthselec : Int = 0
    private var yearselec : Int = 0
    private var imageUri : Uri = Uri.EMPTY
    private lateinit var storageReference: StorageReference
    private lateinit var stu: StreamToUri
    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val showdiffsportss = findViewById<TextView>(R.id.showsporttextView)
        stu = StreamToUri(this, this, contentResolver)

        storageReference = FirebaseStorage.getInstance().reference

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
        birthdayEditTextSignUp.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        birthdayEditTextSignUp.setText(sdf.format(c.time))
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
        }

        newPictureImageView.setOnClickListener{
            stu.askCameraPermissions()
        }

        sportTewtView.setOnClickListener{
            val checkedColorsArray = BooleanArray(34)
            val listSportselec : ArrayList<String> = ArrayList()
            for (sport in sportSelected){
                listSportselec.add(sport.getSportName())
            }
            for (sport in listSportselec){
                if (sportArray.indexOf(sport) != -1){
                    checkedColorsArray[sportArray.indexOf(sport)] = true
                }
            }
            val sportList = sportArray.toList()
            AlertDialog.Builder(this@SignUpActivity)
                .setTitle("Select sports")
                .setMultiChoiceItems(
                    sportArray.toTypedArray(),
                    checkedColorsArray
                ) { _, which, isChecked ->
                    checkedColorsArray[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    sportSelected = ArrayList()
                    showdiffsportss.text = ""
                    for (i in checkedColorsArray.indices) {
                        val checked = checkedColorsArray[i]
                        if (checked) {
                            showdiffsportss.text =
                                ApplicationContext.applicationContext().getString(
                                    R.string.concatTwoStringNewLine,
                                    showdiffsportss.text.toString(),
                                    sportList[i]
                                )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stu.manageActivityResult(requestCode, data)
        imageUri = stu.imageUri
        newPictureImageView.setImageURI(imageUri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }

    private fun signup() {
        auth.createUserWithEmailAndPassword(
            mailEditTextSignUp.text.toString(),
            passwordEditTextSignUp.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                registerNewUser(user,
                    firstnameEditTextSignUp.text.toString(),
                    lastnameEditTextSignUp.text.toString(),
                    birthdayEditTextSignUp.text.toString(),
                    sportSelected,
                    weightEditText.text.toString()
                )

                updateUI(user)
            } else {
                Toast.makeText(baseContext, getString(R.string.err_inscription), Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        accountExistButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun registerNewUser(user: FirebaseUser?, fname:String, lname:String, birthdate:String,
                                sports:ArrayList<Sport>, weight:String) {

        if (user?.uid != null) {
            currUser = User(user.uid, user.email, fname, lname, birthdate,sports,
                weight, "", "0")
            val root = database.getReference("users")
            root.child(currUser.userid).setValue(currUser)


            val uniqID = UUID.randomUUID().toString()
            val stoRef = storageReference.child("users/${currUser.userid}/$uniqID")
            val result: UploadTask
            result = if(imageUri != Uri.EMPTY) {
                stoRef.putFile(imageUri)
            } else {
                val uri = getDrawableToURI(this,R.drawable.default_profile)
                stoRef.putFile(uri)
            }
            result.addOnSuccessListener {
                database.getReference("users/${currUser.userid}/pictureUID").setValue(uniqID)
            }


        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Toast.makeText(this, getString(R.string.vous_avez_un_compte), Toast.LENGTH_LONG).show()
        }
    }

}
