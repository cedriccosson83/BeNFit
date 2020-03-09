package isen.CedricLucieFlorent.benfit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

public class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        var builder = NotificationCompat.Builder(context, "isen.CedricLucieFlorent.benfit")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("N'oubliez pas votre séance")
            .setContentText("Commencez votre séance maintenant")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notificationManger = NotificationManagerCompat.from(context)
        val random = (1 until 9).random()
        val m = random * 100
        Log.d("NOTIF", m.toString())
        notificationManger.notify(m,builder.build())
    }
}