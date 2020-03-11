package isen.cedriclucieflorent.benfit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val builder = NotificationCompat.Builder(context, "isen.CedricLucieFlorent.benfit")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("N'oubliez pas votre séance")
            .setContentText("Commencez votre séance maintenant")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManger = NotificationManagerCompat.from(context)
        val random = (1 until 9).random()
        val m = random * 100
        notificationManger.notify(m,builder.build())
    }
}