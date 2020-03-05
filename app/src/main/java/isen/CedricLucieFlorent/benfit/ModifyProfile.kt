package isen.CedricLucieFlorent.benfit

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_modify_profile.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ModifyProfile : MenuActivity() {

    lateinit var userId: String
    var sportSelectedModif  = ArrayList<Sport>()

    private lateinit var storageReference: StorageReference
    val sportSel = arrayListOf<String>()
    private lateinit var image_uri : Uri

    private lateinit var stu: StreamToUri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_modify_profile, frameLayout)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.FRANCE)
        var dayselec : Int = 0
        var monthselec : Int = 0
        var yearselec : Int = 0

        val showdiffsports = findViewById<TextView>(R.id.showSports)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()
        stu = StreamToUri(this, this, contentResolver)
        userId = auth.currentUser?.uid ?: ""

        val intent = intent

        val sportArray = arrayListOf<String>()

        val myRef = database.getReference("sports")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    sportArray.add(it.child("name").value.toString())
                }
                getSports(sportArray)
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

        birthdateTextViewModify.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    birthdateTextViewModify.text = sdf.format(c.time)
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

        changeProfilImageModify.setOnClickListener{
            stu.askCameraPermissions()
        }

        validateModifyButton.setOnClickListener{
            val user = auth.currentUser
            ChangeUser(user, firstNameTextViewModify.text.toString(),lastNameTextViewModify.text.toString(), birthdateTextViewModify.text.toString(), "", weightTextViewModify.text.toString())
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        sportTextViewModify.setOnClickListener{
            val checkedColorsArray = BooleanArray(166)
            var listSportselec : ArrayList<String> = ArrayList()
            for (sport in sportSelectedModif){
                listSportselec.add(sport.getSportName())
            }
            for (sport in listSportselec){
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
                }
                .setPositiveButton("OK") { dialog, which ->
                    sportSelectedModif = ArrayList()
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

    private fun getSports (sportArray: ArrayList<String>) {
        val mySpo = database.getReference("users").child(userId)
        mySpo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.child("sports").children.forEach {
                    sportSel.add(it.child("name").value.toString())
                }
                val sportList = sportArray.toList()
                for (sport in sportSel){
                    if (sportArray.indexOf(sport) != -1){
                        sportSelectedModif.add(Sport(sportList[sportArray.indexOf(sport)], ArrayList()))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val userid = auth.currentUser?.uid ?: ""
        val uniqID = UUID.randomUUID().toString()
        stu.manageActivityResult(requestCode, data)
        image_uri = stu.imageUri
        val riversRef = storageReference.child("users/$userid/$uniqID")
        val result = riversRef.putFile(image_uri)
        result.addOnSuccessListener {
            changeProfilImageModify.setImageURI(image_uri)
            database.getReference("users/$userid/pictureUID").setValue(uniqID)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
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
                        ArrayList(value.child("sports").children.map { Sport(it.child("name").value.toString(), arrayListOf()) }),
                        value.child("weight").value.toString(),
                        value.child("pictureUID").value.toString()

                    )

                    if (user?.userid == userId) {
                        firstNameTextViewModify.setText("${user.firstname}")
                        lastNameTextViewModify.setText("${user.lastname}")
                        birthdateTextViewModify.setText("${user.birthdate.toString()}")
                        weightTextViewModify.setText("${user.weight}")
                        showSports.setText("${user.sports.map { it.getSportName()}}")
                        setImageFromFirestore(context, changeProfilImageModify, "users/$userId/${user.pictureUID}")
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
            else{
                toast(context, "liste vide")}

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }
}
