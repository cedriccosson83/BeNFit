package isen.cedriclucieflorent.benfit.functions

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.cedriclucieflorent.benfit.*

fun showUserName(userId : String, textview: TextView) {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children) {
                val fname = value.child("firstname").value.toString()
                val lname = value.child("lastname").value.toString()
                val retrievedUserId = value.child("userid").value?.toString()
                if (retrievedUserId == userId) {
                    textview.text = ApplicationContext.applicationContext().getString(
                        R.string.doubleWordsSpaced,
                        fname,
                        lname
                    )
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun showUserNameSessionFeed(userId: String, textview : TextView){
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children) {
                val fname = value.child("firstname").value.toString()
                val lname = value.child("lastname").value.toString()
                val retrievedUserId = value.child("userid").value?.toString()
                if (retrievedUserId == userId) {
                    textview.text = ApplicationContext.applicationContext().getString(
                        R.string.doubleWordsSpaced,
                        fname,
                        lname
                    )
                }
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            Log.d("TAG", "Error reading value")
        }
    })
}
fun showUserNameImage(userId : String, textview: TextView,  imgView : ImageView) {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children) {
                val fname = value.child("firstname").value.toString()
                val lname = value.child("lastname").value.toString()
                val imgPath = value.child("pictureUID").value.toString()
                val retrievedUserId = value.child("userid").value?.toString()
                if (retrievedUserId == userId) {
                    textview.text = ApplicationContext.applicationContext().getString(
                        R.string.doubleWordsSpaced,
                        fname,
                        lname
                    )
                    setImageFromFirestore(imgView, "users/$userId/$imgPath")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}
fun checkUserSessionDone(database: FirebaseDatabase, userId: String?, programID: String,
                         countSessTotProgram: Int, programProgress: ProgressBar
) {
    if (userId == null) return
    val myRef = database.getReference("users").child(userId).child("currentPrograms").child(programID)
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalDoneSess = 0
            for (value in dataSnapshot.children)
                if (value.value.toString() == "OK")
                    countTotalDoneSess++

            renderProgressProgram(countSessTotProgram,countTotalDoneSess, programProgress)

        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("programs", "Failed to read value.", error.toException())
        }
    })
}
fun updateUserGrade(database: FirebaseDatabase, userId: String, sumScore : Int, context: Context) {
    val myRef = database.getReference("users").child(userId)

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var newScore = 0
            var userFN = ""
            for (value in dataSnapshot.children) {
                if (value.key.toString() == "grade") {
                    val currScore = value.value.toString()
                    newScore = if (currScore != "" && currScore != "null") currScore.toInt() + sumScore else sumScore
                } else if (value.key.toString() == "firstname") {
                    userFN = value.value.toString()
                }
            }

            myRef.child("grade").setValue(newScore.toString())
            showPopUpCongratz(userFN, newScore, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
