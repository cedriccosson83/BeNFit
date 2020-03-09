package isen.CedricLucieFlorent.benfit

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.Models.ShowExerciceSession
import isen.CedricLucieFlorent.benfit.Models.ShowSessionProgram
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

fun showDate(date : String?, textview: TextView){

    var dateSplit = date?.split(" ")
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate: String = sdf.format(Date())
    if(currentDate == dateSplit?.get(1)){
        textview.text = dateSplit[0]
    }else{
        textview.text = "${dateSplit?.get(1)}"
    }
}

fun creernotif(context: Context?, notificationManager:NotificationManager){

    if (context == null) return

    val notificationChannel : NotificationChannel
    val builder : Notification.Builder
    val channelId = "isen.CedricLucieFlorent.benfit"
    val description = "Test notification"

    val intent = Intent(context, NotifActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val contentView = RemoteViews(context.packageName, R.layout.notification_layout)
    contentView.setTextViewText(R.id.tv_title,"Motivez vous ! ")
    contentView.setTextViewText(R.id.tv_content, "N'oubliez pas votre séance d'entrainement")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel) // ça tu l'init ou ?

        builder = Notification.Builder(context,channelId)
            .setContent(contentView)
            .setSmallIcon(R.mipmap.benfit_logo_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.benfit_logo_round))
            .setContentIntent(pendingIntent)

    }
    else{
        builder = Notification.Builder(context) // fais gaffe aux fonctions Deprecated (les barrées) ca veut dire qyoioui merci jai compris mais ca fait quoi
            .setContent(contentView)
            .setSmallIcon(R.mipmap.benfit_logo_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.benfit_logo_round))
            .setContentIntent(pendingIntent)
    }
    notificationManager.notify(123, builder.build())
}

fun redirectToUserActivity(context: Context, userID : String){
    val intent = Intent(context, ProfileActivity::class.java)
    intent.putExtra("userId", userID)
    context.startActivity(intent)
}

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
                    textview.text = "$fname $lname"
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun getDrawableToURI( context : Context,  drawableId : Int) : Uri {
    val imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + context.resources.getResourcePackageName(drawableId)
            + '/' + context.resources.getResourceTypeName(drawableId)
            + '/' + context.resources.getResourceEntryName(drawableId) )
    return imageUri
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
                    textview.text = "${fname} ${lname}"
                }
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })
}

fun redirectToProgram(context : Context, programID : String, extra : String = ""){
    val intent = Intent(context, ShowProgramActivity::class.java)
    intent.putExtra("activity", extra)
    intent.putExtra("programId", programID)
    context.startActivity(intent)
}

fun constraintValidateYoutube(btn : MaterialButton, inputText : String) : Boolean{
    if (btn.isChecked) {
        var position = inputText.indexOf("https://www.youtube.com/watch?v=")
        if(position == -1){
            return false
        }
    }
    return true
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
                    textview.text = "$fname $lname"
                    setImageFromFirestore(ApplicationContext.applicationContext(), imgView, "users/$userId/$imgPath")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {

            Log.w("post", "Failed to read value.", error.toException())
        }
    })
}

fun convertLevelToImg(level: String, image: ImageView) {
    when (level) {
        "Expert" -> image.setImageResource(R.drawable.level_3)
        "Intermédiaire" -> image.setImageResource(R.drawable.level_2)
        else -> image.setImageResource(R.drawable.level_1)
    }
}

fun sessionFinished(database: FirebaseDatabase,session: ShowSessionProgram, program: ShowProgram, currentUserID: String?, icon: ImageView){
    var programID = program.programID ?: ""
    var id = currentUserID ?:""
    if (id != ""){
        val myRef = database.getReference("users").child(id).child("currentPrograms").child(programID)
        for (value in program.sessionsProgram) {
            if (session.sessionID == value) {
                myRef.child(session.sessionID).setValue("OK")
                icon.setImageResource(R.drawable.checked)
            }
        }
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

fun showFollowers(database: FirebaseDatabase, currentUserID: String?, programID : String,
                  pathToFollowers : String, icon : ImageView){
    val myRef = database.getReference(pathToFollowers)
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val arrayFollowers :ArrayList<String> = ArrayList()
            for (value in dataSnapshot.children) {
                Log.d("img", value.toString())
                if (arrayFollowers.all { it != value.key.toString()}) {
                    arrayFollowers.add(value.key.toString())
                }
            }
            if (arrayFollowers.all { it != programID }) {
                icon.setImageResource(R.drawable.add)
            } else {
                icon.setImageResource(R.drawable.remove)
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

fun setImageFromFirestore(context: Context, target: ImageView, location: String) {
    val storeRef: StorageReference?
        = FirebaseStorage.getInstance().getReference(location)
    GlideApp.with(ApplicationContext.applicationContext()).load(storeRef).into(target)
}

fun getProgramProgression(database: FirebaseDatabase, userId: String?,
                          programID: String, programProgress: ProgressBar) {
    val myRef = database.getReference("programs").child(programID)
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalSess = 0
            for (value in dataSnapshot.children) {
                if (value.key.toString() == "sessionsProgram") {
                    for (childSess in value.children)
                        countTotalSess++
                    break
                }
            }
            checkUserSessionDone(database, userId, programID, countTotalSess, programProgress)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("programs", "Failed to read value.", error.toException())
        }
    })
}

fun checkUserSessionDone(database: FirebaseDatabase, userId: String?, programID: String,
                         countSessTotProgram: Int, programProgress: ProgressBar) {
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

fun renderProgressProgram(countSessTotProgram : Int,countTotalDoneSess : Int, programProgress: ProgressBar) {
    Log.d("CEDRIC_TOT", countSessTotProgram.toString())
    Log.d("CEDRIC_SUB", countTotalDoneSess.toString())
    val percent : Float = if (countTotalDoneSess > 0)
        round(((countTotalDoneSess.toFloat()/ countSessTotProgram.toFloat())  * 100).toFloat())
    else
        0F
    Log.d("CEDRIC_percent", percent.toString())
    programProgress.progress = percent.toInt()
}

fun countTotalProgramLikes(database: FirebaseDatabase, userId: String,
                           gradeText: TextView, gradeImg1: ImageView,
                           gradeImg2: ImageView, context: Context) {
    val myRef = database.getReference("programs")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalLikes = 0
            for (value in dataSnapshot.children)
                if (value.child("userID").value.toString() == userId)
                    for (childLike in value.child("likes").children)
                        countTotalLikes++

            countTotalSessionLikes(database, userId, countTotalLikes, gradeText, gradeImg1, gradeImg2, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}

fun countTotalSessionLikes(database: FirebaseDatabase, userId: String, countTotalLikesProgram: Int,
                           gradeText: TextView, gradeImg1: ImageView, gradeImg2: ImageView, context: Context) {
    val myRef = database.getReference("sessions")
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var countTotalLikesSess = 0
            for (value in dataSnapshot.children)
                if (value.child("userID").value.toString() == userId)
                    for (childLike in value.child("likes").children)
                        countTotalLikesSess++
            val countTotal = countTotalLikesProgram + countTotalLikesSess
            renderCoachGrade(countTotal, gradeText, gradeImg1, gradeImg2, context)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
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

fun checkCompleteProgram(database: FirebaseDatabase,
                         userId: String,
                         context: Context,
                         programId : String
) {
    val myRef = database
        .getReference("users")
        .child(userId)
        .child("currentPrograms")
        .child(programId)

    Log.d("CEDRIC_ref", "users/$userId/currentPrograms/$programId")
    val sessionAchieve: ArrayList<String> = ArrayList()

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var nbSupposedToBeOK = 0
            for (value in dataSnapshot.children) {
                Log.d("CEDRIC_for", "tour")
                sessionAchieve.add(value.key.toString())
                if (value.value.toString() == "OK")
                    nbSupposedToBeOK++
            }
            Log.d("CEDRIC_nbSupposedToBeOK", nbSupposedToBeOK.toString())
            if (nbSupposedToBeOK == sessionAchieve.size) { // 0L to compare a LONG type
                Log.d("CEDRIC_nbSupposed", "IS OK")
                computeScore(database, sessionAchieve, userId, context, programId)
                dataSnapshot.ref.removeValue()
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}

fun computeScore(database: FirebaseDatabase, sessionAchieve : ArrayList<String>, userId: String, context: Context, programId:String) {
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

            Log.d("CEDRIC_sumScore", sumScore.toString())

            updateUserGrade(database, userId, sumScore, context, programId)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
}

fun updateUserGrade(database: FirebaseDatabase, userId: String, sumScore : Int, context: Context, programId: String) {
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
            Log.d("CEDRIC_newScore", newScore.toString())

            myRef.child("grade").setValue(newScore.toString())
            Log.d("CEDRIC_SCORE", newScore.toString())
            showPopUpCongratz(userFN, newScore, context)
           // removeProgramFromCurrentProgram(database, userId,programId)
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

//fun removeProgramFromCurrentProgram(database, userId,programId)

fun showPopUpCongratz(userFirstName: String, newScore : Int, context: Context) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.popup_congratz)
    dialog.setTitle("titre")
    dialog.findViewById<TextView>(R.id.popUpCongratzName).text = "Félicitation $userFirstName !"
    dialog.findViewById<TextView>(R.id.popUpCongratzScore).text = "Score Sportif : $newScore"
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setOnDismissListener {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }
    dialog.show()
}

fun showPopUpExercice(database: FirebaseDatabase, context : Context, exoID: String, windowManager: WindowManager, sessionParent: String? = "") {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.popup_show_exo)
    dialog.setTitle("titre")

    val dbRef = database.getReference("exos/$exoID")

    dbRef.addValueEventListener(object : ValueEventListener {
        @SuppressLint("SetJavaScriptEnabled")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val exercice = dataSnapshot.getValue(ShowExerciceSession::class.java)
            if (exercice != null) {
                showUserName(exercice.idUser,dialog.findViewById(R.id.showExoAuthor))
                convertLevelToImg(exercice.difficulty,dialog.findViewById(R.id.showExoLevelIcon))
                dialog.findViewById<TextView>(R.id.showExoName).text = exercice.name
                dialog.findViewById<TextView>(R.id.showExoDesc).text = exercice.description
                dialog.findViewById<TextView>(R.id.showExoLevelText).text = exercice.difficulty
                dialog.findViewById<ImageView>(R.id.showExoShare).setOnClickListener {
                    val writePostIntent = Intent(context, WritePostActivity::class.java)
                    writePostIntent.putExtra("sharedExo", exoID)
                    writePostIntent.putExtra("sharedName", exercice.name)
                    context.startActivity(writePostIntent)
                }


                //consignes
                setRulesIfInSession(database,dialog.findViewById(R.id.showExoRuleValue),
                    dialog.findViewById(R.id.showExoRule),exercice.id,sessionParent)

                val videoWeb : WebView = dialog.findViewById(R.id.showExoYTLayout)
                when {
                    exercice.pictureUID != "" -> {
                        val layout = dialog.findViewById<LinearLayout>(R.id.showExoMediaLayout)
                        val exoImView = ImageView(context)
                        setImageFromFirestore(context,exoImView, "exos/${exercice.id}/${exercice.pictureUID}")

                        layout.addView(exoImView)
                        exoImView.layoutParams.height = 400

                        exoImView.setOnClickListener {
                            val fullScreenIntent = Intent(ApplicationContext.applicationContext(), FullScreenImageView::class.java)
                            fullScreenIntent.putExtra("url", "exos/${exercice.id}/${exercice.pictureUID}")
                            fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            ApplicationContext.applicationContext().startActivity(fullScreenIntent)
                        }
                        videoWeb.visibility = View.INVISIBLE
                    }
                    exercice.urlYTB != "" -> {

                        // compute margin based on screen width to set specific margin for Responsive WebView
                        val displayMetrics = DisplayMetrics()
                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                        var marginWidth = pxToDp(displayMetrics.widthPixels)
                        val minWidth = 355
                        val difference = marginWidth - minWidth
                        if (difference > 0) {
                            marginWidth = if (difference > 20)
                                (marginWidth - 320 - ((marginWidth-320)/2)) + (difference/2)
                            else
                                difference/2
                            val p : ViewGroup.MarginLayoutParams = videoWeb.layoutParams as  ViewGroup.MarginLayoutParams
                            p.leftMargin = marginWidth
                            videoWeb.layoutParams = p
                        }

                        videoWeb.visibility = View.VISIBLE
                        videoWeb.setInitialScale(1)
                        videoWeb.settings.loadWithOverviewMode = true
                        videoWeb.settings.useWideViewPort = true
                        val ytUrl = exercice.urlYTB.replace("watch?v=", "embed/")

                        val url = "<iframe width=\"100%\" height=\"100%\" src=\"$ytUrl\" frameborder=\"0\" allowfullscreen></iframe>"

                        Log.d("VIDEOOO", url)
                        videoWeb.settings.javaScriptEnabled = true
                        videoWeb.loadData(url, "text/html" , "utf-8" )
                        videoWeb.webChromeClient = WebChromeClient()
                    }
                    else -> videoWeb.visibility = View.INVISIBLE
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
    dialog.show()
}

fun setRulesIfInSession(database: FirebaseDatabase, targetTextView : TextView
                        , targetLabel : TextView , exerciceID: String, sessionParent: String?) {
    if (sessionParent != "" && sessionParent != null) {
        val myRef = database.getReference("sessions")
                                    .child(sessionParent).child("exosSession")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var consigne = ""
                for (value in dataSnapshot.children) {
                    if (value.child("exoID").value.toString() == exerciceID) {
                        consigne = value.child("rep").value.toString()
                    }
                }

                targetTextView.text = consigne
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    } else {
        targetTextView.text = ""
        targetLabel.text = ""
    }
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}