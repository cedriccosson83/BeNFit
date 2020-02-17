package isen.CedricLucieFlorent.benfit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Models.Sport
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_modify_profile.*

class ModifyProfile : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    lateinit var userId: String
    lateinit var currUser: User
    var sportSelectedModif  = ArrayList<Sport>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)

        val showdiffsports = findViewById<TextView>(R.id.showSports)

        auth = FirebaseAuth.getInstance()
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

        if (intent != null) {
            if (userId != "") {
                showUserM(userId)
            }
        }

        changeProfilImageModify.setOnClickListener(){
            val imagefromgalleryIntent = Intent(Intent.ACTION_PICK)
            imagefromgalleryIntent.setType("image/png")

            val imagefromcameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val chooseIntent= Intent.createChooser(imagefromgalleryIntent, "Gallery")
            chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(imagefromcameraIntent))
            startActivityForResult(chooseIntent,11)
        }

        validateModifyButton.setOnClickListener(){
            val user = auth.currentUser
            ChangeUser(user, firstNameTextViewModify.text.toString(),lastNameTextViewModify.text.toString(), birthdateTextViewModify.text.toString(), "Sport", weightTextViewModify.text.toString())
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        sportTextViewModify.setOnClickListener(){
            val checkedColorsArray = BooleanArray(166)
            val sportList = sportArray.toList()
            AlertDialog.Builder(this@ModifyProfile)
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
                    // return to ModifyProfile
                    //val mysports = arrayListOf<String>()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ){
            if(data?.data == null){
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let{
                    changeProfilImageModify.setImageBitmap(it)
                }
            }else{
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
                        weightTextViewModify.setText("${user.weight} kg")
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
            root.child(user?.uid).child("sports").setValue(sportSelectedModif)
            //root.child(user?.uid).child("sport").setValue(sportnew)

        } else
            Toast.makeText(this, getString(R.string.err_inscription), Toast.LENGTH_LONG).show()
    }
}
