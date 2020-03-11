package isen.CedricLucieFlorent.benfit

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
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
    private lateinit var imageUri : Uri

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

        val showdiffsports = findViewById<TextView>(R.id.showSports)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        stu = StreamToUri(this, this, contentResolver)
        userId = auth.currentUser?.uid ?: ""

        val intent = intent

        val sportArray = arrayListOf<String>()

        val myRef = database.getReference("sports")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

        birthdateTextViewModify.onFocusChangeListener = View.OnFocusChangeListener {
            _, hasFocus ->
                if (hasFocus) {
                    val dpd = DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear,
                                                             dayOfMonth ->
                            c.set(Calendar.YEAR, year)
                            c.set(Calendar.MONTH, monthOfYear)
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            birthdateTextViewModify.setText(sdf.format(c.time))
                        },
                        year,
                        month,
                        day
                    )
                    dpd.show()
                }
        }

        changeProfilImageModify.setOnClickListener{
            stu.askCameraPermissions()
        }

        validateModifyButton.setOnClickListener{
            val user = auth.currentUser
            changeUser(
                user,
                firstNameTextViewModify.text.toString(),
                lastNameTextViewModify.text.toString(),
                birthdateTextViewModify.text.toString(),
                weightTextViewModify.text.toString())
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        sportTextViewModify.setOnClickListener{
            val checkedColorsArray = BooleanArray(166)
            val listSportselec : ArrayList<String> = ArrayList()
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
                .setTitle("Select sports")
                .setMultiChoiceItems(
                    sportArray.toTypedArray(),
                    checkedColorsArray
                ) { _, which, isChecked ->
                    checkedColorsArray[which] = isChecked
                }
                .setPositiveButton("OK") { _, _ ->
                    sportSelectedModif = ArrayList()
                    showdiffsports.text = getString(R.string.sportsPracticed)
                    for (i in checkedColorsArray.indices) {
                        val checked = checkedColorsArray[i]
                        if (checked) {
                            showdiffsports.text =
                                ApplicationContext.applicationContext().getString(
                                    R.string.ConcatSport,
                                    showdiffsports.text.toString(),
                                    sportList[i]
                                )
                                showdiffsports.text.toString() + sportList[i] + " "
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
        mySpo.addListenerForSingleValueEvent(object : ValueEventListener {
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
        imageUri = stu.imageUri
        val riversRef = storageReference.child("users/$userid/$uniqID")
        val result = riversRef.putFile(imageUri)
        result.addOnSuccessListener {
            changeProfilImageModify.setImageURI(imageUri)
            database.getReference("users/$userid/pictureUID").setValue(uniqID)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }

    private fun showUserM(userId: String) {
        val myRef = database.getReference("users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user: User?
                for (value in dataSnapshot.children) {
                    user = User(
                        value.child("userid").value.toString(),
                        value.child("email").value.toString(),
                        value.child("firstname").value.toString(),
                        value.child("lastname").value.toString(),
                        value.child("birthdate").value.toString(),
                        ArrayList(value.child("sports").children.map {
                            Sport(it.child("name").value.toString(), arrayListOf()) }),
                        value.child("weight").value.toString(),
                        value.child("pictureUID").value.toString(),
                        value.child("grade").value.toString()
                    )

                    if (user.userid == userId) {
                        firstNameTextViewModify.setText("${user.firstname}")
                        lastNameTextViewModify.setText("${user.lastname}")
                        birthdateTextViewModify.setText(user.birthdate.toString())
                        weightTextViewModify.setText("${user.weight}")
                        var sportText = "Sports pratiqués : "
                        for (sp in user.sports)
                            sportText += sp.getSportName() + " "

                        if (user.sports.isEmpty())
                            sportText = "Aucun sport sélectionné"

                        showSports.text = sportText
                        setImageFromFirestore(
                            context,
                            changeProfilImageModify,
                            "users/$userId/${user.pictureUID}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }

    private fun changeUser(user: FirebaseUser?, fnamenew:String, lnamenew:String,
                           birthdatenew:String, weightnew:String) {
        if (user?.uid != null) {
            val root = database.getReference("users")
            root.child(user.uid).child("firstname").setValue(fnamenew)
            root.child(user.uid).child("lastname").setValue(lnamenew)
            root.child(user.uid).child("birthdate").setValue(birthdatenew)
            root.child(user.uid).child("weight").setValue(weightnew)
            if (sportSelectedModif.isNotEmpty()){
                root.child(user.uid).child("sports").setValue(sportSelectedModif)
            } else {
                toast(context, "liste vide")
            }
        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }
}
