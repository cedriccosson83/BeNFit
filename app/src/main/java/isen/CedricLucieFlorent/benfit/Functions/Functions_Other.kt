package isen.CedricLucieFlorent.benfit.Functions

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.*
import java.text.SimpleDateFormat
import java.util.*

fun showDate(date : String?, textview: TextView){

    val dateSplit = date?.split(" ")
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate: String = sdf.format(Date())
    if(currentDate == dateSplit?.get(1)){
        textview.text = dateSplit[0]
    }else{
        textview.text = "${dateSplit?.get(1)}"
    }
}

fun redirectToUserActivity(context: Context, userID : String){
    val intent = Intent(context, ProfileActivity::class.java)
    intent.putExtra("userId", userID)
    context.startActivity(intent)
}
fun getDrawableToURI( context : Context,  drawableId : Int) : Uri {
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.resources.getResourcePackageName(drawableId)
                + '/' + context.resources.getResourceTypeName(drawableId)
                + '/' + context.resources.getResourceEntryName(drawableId) )
}
fun redirectToProgram(context : Context, programID : String, extra : String = ""){
    val intent = Intent(context, ShowProgramActivity::class.java)
    intent.putExtra("activity", extra)
    intent.putExtra("programId", programID)
    context.startActivity(intent)
}
fun constraintValidateYoutube(btn : MaterialButton, inputText : String) : Boolean{
    if (btn.isChecked) {
        val position = inputText.indexOf("https://www.youtube.com/watch?v=")
        if(position == -1){
            return false
        }
    }
    return true
}
fun convertLevelToImg(level: String, image: ImageView) {
    when (level) {
        "Expert" -> image.setImageResource(R.drawable.level_3)
        "Intermédiaire" -> image.setImageResource(R.drawable.level_2)
        else -> image.setImageResource(R.drawable.level_1)
    }
}
fun showChecked(database : FirebaseDatabase, pathToChecked : String, icon : ImageView, sessionID :String ) {
    val myRef = database.getReference(pathToChecked)
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children) {
                if (value.key.toString() == sessionID && value.value.toString() == "OK"){
                    icon.setImageResource(R.drawable.checked)
                    break
                }
                else{
                    icon.setImageResource(R.drawable.tocheck)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("Likes", "Failed to read value.", error.toException())
        }
    })
}
fun showNotified(database: FirebaseDatabase, pathToNotif : String, sessionID: String, btn : ImageView){
    val myRef = database.getReference(pathToNotif)
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children){
                if (value.key.toString() == sessionID){
                    btn.setImageResource(R.drawable.notificationset)
                }
            }
        }
        override fun onCancelled(p0: DatabaseError) {
            Log.d("TAG", "Failed to read values")
        }
    })
}
fun removePassedNotif(database: FirebaseDatabase, userId: String, lastConn : String){
    val myRef = database.getReference("notifications/${userId}")
    myRef.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children){
                val time = value.value.toString()
                val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
                val date = format.parse(time)
                val lastCo = format.parse(lastConn)
                if (lastCo != null && lastCo > date){
                    myRef.child(value.key.toString()).removeValue()
                }

            }
        }

        override fun onCancelled(p0: DatabaseError) {

        }
    })

}
fun showFollowers(database: FirebaseDatabase, programID : String,
                  pathToFollowers : String, icon : ImageView){
    val myRef = database.getReference(pathToFollowers)
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val arrayFollowers :ArrayList<String> = ArrayList()
            for (value in dataSnapshot.children) {
                if (arrayFollowers.all { it != value.key.toString()}) {
                    arrayFollowers.add(value.key.toString())
                }
            }
            if (arrayFollowers.all { it != programID }) {
                icon.setImageResource(R.drawable.follow)
            } else {
                icon.setImageResource(R.drawable.unfollow)
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("Likes", "Failed to read value.", error.toException())
        }
    })
}
fun showNumberLikes(database: FirebaseDatabase, pathToLikes : String, textTarget : TextView){
    val myRef = database.getReference(pathToLikes)
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val arrayLikes: ArrayList<String> = ArrayList()
            for (value in dataSnapshot.children) {
                arrayLikes.add(value.value.toString())
            }
            textTarget.text = arrayLikes.size.toString()
        }
        override fun onCancelled(p0: DatabaseError) {
            Log.d("TAG", "Failed to read value")
        }
    })
}
fun showLikes(database: FirebaseDatabase, currentUserID: String?, pathToLikes : String, textTarget : TextView, icon: ImageView) {

    val myRef = database.getReference(pathToLikes)
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val arrayLikes :ArrayList<String> = ArrayList()
            for (value in dataSnapshot.children) {
                arrayLikes.add(value.value.toString())
            }
            textTarget.text = arrayLikes.size.toString()

            if (arrayLikes.all { it != currentUserID }) {
                icon.setImageResource(R.drawable.like)
            } else {
                icon.setImageResource(R.drawable.dislike)

            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("Likes", "Failed to read value.", error.toException())
        }
    })
}
fun likesHandler(database: FirebaseDatabase,
                 currentUserID: String?,
                 pathToLikes : String,
                 likes : ArrayList<String>,
                 likeIcon: ImageView
) {
    val myRef = database.getReference(pathToLikes)
    if(likes.all { it != currentUserID }) {
        likes.add(currentUserID ?: "")
        myRef.setValue(likes)
        likeIcon.setImageResource(R.drawable.dislike)
    }else{
        likes.remove(currentUserID)
        myRef.setValue(likes)
        likeIcon.setImageResource(R.drawable.like)
    }
}
fun setImageFromFirestore(target: ImageView, location: String) {
    val storeRef: StorageReference?
            = FirebaseStorage.getInstance().getReference(location)
    GlideApp.with(ApplicationContext.applicationContext()).load(storeRef).into(target)
}

fun renderCoachGrade(countTotal: Int, gradeText: TextView,
                     gradeImg1: ImageView, gradeImg2: ImageView,
                     context: Context) {
    when {
        countTotal >= 80 -> {
            gradeText.text = context.getString(R.string.coach_grade_3)
            gradeImg1.setImageResource(R.drawable.coach_grade_3)
            gradeImg2.setImageResource(R.drawable.coach_grade_3)
        } countTotal >= 45 -> {
        gradeText.text = context.getString(R.string.coach_grade_2)
        gradeImg1.setImageResource(R.drawable.coach_grade_2)
        gradeImg2.setImageResource(R.drawable.coach_grade_2)
    } countTotal >= 15 -> {
        gradeText.text = context.getString(R.string.coach_grade_1)
        gradeImg1.setImageResource(R.drawable.coach_grade_1)
        gradeImg2.setImageResource(R.drawable.coach_grade_1)
    } countTotal < 15 -> {
        gradeText.text = context.getString(R.string.coach_grade_0)
        gradeImg1.setImageResource(R.drawable.coach_grade_0)
        gradeImg2.setImageResource(R.drawable.coach_grade_0)
    }
    }
}
fun renderGrade(grade: String?, gradeText: TextView, gradeImg1: ImageView, gradeImg2: ImageView, context: Context) {
    if (grade != null) {
        val intGrade = grade.toInt()
        when {
            intGrade >= 150 -> {
                gradeText.text = context.getString(R.string.grade5)
                gradeImg1.setImageResource(R.drawable.grade5)
                gradeImg2.setImageResource(R.drawable.grade5)
            } intGrade >= 120 -> {
            gradeText.text = context.getString(R.string.grade4)
            gradeImg1.setImageResource(R.drawable.grade4)
            gradeImg2.setImageResource(R.drawable.grade4)
        } intGrade >= 90 -> {
            gradeText.text = context.getString(R.string.grade3)
            gradeImg1.setImageResource(R.drawable.grade3)
            gradeImg2.setImageResource(R.drawable.grade3)
        } intGrade >= 60 -> {
            gradeText.text = context.getString(R.string.grade2)
            gradeImg1.setImageResource(R.drawable.grade2)
            gradeImg2.setImageResource(R.drawable.grade2)
        } intGrade >= 30 -> {
            gradeText.text = context.getString(R.string.grade1)
            gradeImg1.setImageResource(R.drawable.grade1)
            gradeImg2.setImageResource(R.drawable.grade1)
        } intGrade < 30 -> {
            gradeText.text = context.getString(R.string.grade0)
            gradeImg1.setImageResource(R.drawable.grade0)
            gradeImg2.setImageResource(R.drawable.grade0)
        }
        }
    }
}
fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
fun computeScore(database: FirebaseDatabase, sessionAchieve : ArrayList<String>, userId: String, context: Context) {
    val myRef = database.getReference("sessions")

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var sumScore = 0
            for (value in dataSnapshot.children) {
                val sessCurr = value.child("sessionID").value.toString()
                val sessDiff = value.child("levelSession").value.toString()

                if (sessionAchieve.indexOf(sessCurr) != -1)
                    sumScore += difficultyToValue(sessDiff)
            }


            updateUserGrade(database, userId, sumScore, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}
fun difficultyToValue(sessDiff : String) : Int {
    if (sessDiff == "Expert")
        return 5
    else if (sessDiff == "Intermédiaire")
        return 3
    return 1
}

fun showPopUpCongratz(userFirstName: String, newScore : Int, context: Context) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.popup_congratz)
    dialog.setTitle("titre")
    dialog.findViewById<TextView>(R.id.popUpCongratzName).text =
        ApplicationContext.applicationContext().getString(
            R.string.congratzName,
            userFirstName
        )
    dialog.findViewById<TextView>(R.id.popUpCongratzScore).text = ApplicationContext.applicationContext().getString(
        R.string.scoreSport,
        newScore
    )
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setOnDismissListener {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }
    dialog.show()
}

fun fullScreenImage (context : Context,url : String) {
    val fullScreenIntent = Intent(context, FullScreenImageView::class.java)
    fullScreenIntent.putExtra("url", url)
    fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(fullScreenIntent)
}