package isen.CedricLucieFlorent.benfit

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.CedricLucieFlorent.benfit.Models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view_comment_cell.view.*
import kotlinx.android.synthetic.main.recycler_view_post_cell.view.*
import java.io.File
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
    val id : String = userID
    intent.putExtra("user", id)
    context.startActivity(intent)
    Toast.makeText(context, "Clicked: ${id}", Toast.LENGTH_LONG).show()
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
            "://" + context.getResources().getResourcePackageName(drawableId)
            + '/' + context.getResources().getResourceTypeName(drawableId)
            + '/' + context.getResources().getResourceEntryName(drawableId) )
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

fun setImageFromFirestore(context: Context, target: ImageView, location: String) {
    val storeRef: StorageReference?
        = FirebaseStorage.getInstance().getReference(location)
    GlideApp.with(context).load(storeRef).into(target)
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
