package isen.CedricLucieFlorent.benfit

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
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

fun redirectToProgram(context : Context, programID : String, extra : String){
    val intent = Intent(context, ShowProgramActivity::class.java)
    intent.putExtra("activity", extra)
    intent.putExtra("program", programID)
    context.startActivity(intent)
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

fun showChecked(database : FirebaseDatabase, pathToChecked : String, icon : ImageView ) {
    val myRef = database.getReference(pathToChecked)
    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (value in dataSnapshot.children) {
                if (value.value.toString() == "OK"){
                    icon.setImageResource(R.drawable.checked)
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

fun showFollowers(database: FirebaseDatabase, currentUserID: String?, programID : String, pathToFollowers : String, icon : ImageView){
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
        .child("currentProgram")
        .child(programId)

    val sessionAchieve: ArrayList<String> = ArrayList()

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var nbSupposedToBeOK = dataSnapshot.childrenCount
            for (value in dataSnapshot.children) {
                sessionAchieve.add(value.key.toString())
                if (value.value.toString() == "OK")
                    nbSupposedToBeOK--
            }
            if (nbSupposedToBeOK == 0L) { // 0L to compare a LONG type
                computeScore(database, sessionAchieve, userId, context)
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
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

fun updateUserGrade(database: FirebaseDatabase, userId: String, sumScore : Int, context: Context) {
    val myRef = database.getReference("users").child(userId)

    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var newScore = 0
            var userFN = ""
            for (value in dataSnapshot.children) {
                val currScore = value.child("grade").value.toString()
                newScore = if (currScore != "") currScore.toInt() + sumScore else sumScore
                userFN = value.child("firstname").value.toString()
            }

            myRef.child("grade").setValue(newScore.toString())
            showPopUpCongratz(userFN, newScore, context)
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
    dialog.findViewById<TextView>(R.id.popUpCongratzName).text = "Félicitation $userFirstName !"
    dialog.findViewById<TextView>(R.id.popUpCongratzScore).text = "Score Sportif : $newScore"
    dialog.show()
}

fun showPopUpExercice(database: FirebaseDatabase, context : Context, exoID: String, windowManager: WindowManager) {
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

                // SI POPUP OUVERTE DEPUIS UNE SEANCE REMPLIR LES CHAMPS CI DESSOUS AVEC LES CONSIGNES (ex : 5 KM)
                dialog.findViewById<TextView>(R.id.showExoRule).text = ""
                dialog.findViewById<TextView>(R.id.showExoRuleValue).text = ""
                val videoWeb : WebView = dialog.findViewById(R.id.showExoYTLayout)
                when {
                    exercice.urlPicture != "" -> {
                        val layout = dialog.findViewById<LinearLayout>(R.id.showExoMediaLayout)
                        val exoImView = ImageView(context)
                        setImageFromFirestore(context,exoImView, "exercices/${exercice.id}/${exercice.urlPicture}")

                        layout.addView(exoImView)
                        exoImView.layoutParams.height = 400

                        exoImView.setOnClickListener {
                            val fullScreenIntent = Intent(ApplicationContext.applicationContext(), FullScreenImageView::class.java)
                            fullScreenIntent.putExtra("url", "exercices/${exercice.id}/${exercice.urlPicture}")
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

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}