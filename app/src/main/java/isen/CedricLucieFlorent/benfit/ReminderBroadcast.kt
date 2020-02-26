package isen.CedricLucieFlorent.benfit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

public class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        var builder = NotificationCompat.Builder(context, "isen.CedricLucieFlorent.benfit")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("N'oubliez pas votre séance")
            .setContentText("Commencez votre séance maintenant")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notificationManger = NotificationManagerCompat.from(context)

        notificationManger.notify(200,builder.build())
    }
}