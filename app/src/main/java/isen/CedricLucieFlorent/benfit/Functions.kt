package isen.CedricLucieFlorent.benfit

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.TextUtils.replace
import android.util.Log
import android.view.View
import android.view.Window
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
import kotlinx.android.synthetic.main.popup_show_exo.*
import java.text.SimpleDateFormat
import java.util.*

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

fun showPopUpExercice(database: FirebaseDatabase, context : Context, exoID: String) {
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
                if (exercice.urlPicture != "") {
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
                } else if(exercice.urlYTB != "") {
                    val ytUrl = exercice.urlYTB.replace("watch?v=", "embed/")
                    val url = "<iframe width=\"100%\" height=\"100%\" src=\"$ytUrl\" frameborder=\"0\" allowfullscreen></iframe>"
                    videoWeb.settings.javaScriptEnabled = true
                    videoWeb.loadData(url, "text/html" , "utf-8" )
                    videoWeb.webChromeClient = WebChromeClient()
                } else {
                    videoWeb.visibility = View.INVISIBLE
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.w("session", "Failed to read value.", error.toException())
        }
    })
    dialog.show()
}