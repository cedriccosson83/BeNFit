package isen.CedricLucieFlorent.benfit

import android.app.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notif.*
import java.text.SimpleDateFormat
import java.util.*


class NotifActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notif)
        createNotificationChannel()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val t = Calendar.getInstance()
        val hour = t.get(Calendar.HOUR_OF_DAY)
        val minute = t.get(Calendar.MINUTE)

        val dateFormat = "dd/MM/yyyy" // mention the format you need
        val timeFormat = "hh:mm a"

        val sdf = SimpleDateFormat(dateFormat, Locale.FRANCE)
        val stf = SimpleDateFormat(timeFormat, Locale.FRANCE)

        var dayselec : Int = 0
        var monthselec : Int = 0
        var yearselec : Int = 0
        var hourselec : Int = 0
        var minselec : Int = 0

        inputDate.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        // Display Selected date in TextView
                        inputDate.setText(sdf.format(c.time))
                        dayselec = dayOfMonth
                        monthselec = monthOfYear
                        yearselec = year
                    },
                    year,
                    month,
                    day
                )
                dpd.show()
            }
        })

        inputTime.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val tpd = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        t.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        t.set(Calendar.MINUTE, minute)
                        // Display Selected time in TextView
                        inputTime.setText(stf.format(t.time))
                        hourselec = hourOfDay
                        minselec = minute
                    },
                    hour,
                    minute,
                    false
                )
                tpd.show()
            }
        })

        btn_create.setOnClickListener(){

            val calendar = Calendar.getInstance()
            calendar.setTimeInMillis(System.currentTimeMillis())
            calendar.set(Calendar.MONTH, monthselec)
            calendar.set(Calendar.DAY_OF_MONTH, dayselec)
            calendar.set(Calendar.YEAR, yearselec)
            calendar.set(Calendar.HOUR_OF_DAY, hourselec)
            calendar.set(Calendar.MINUTE, minselec)
            Toast.makeText(this, "reminder set!" , Toast.LENGTH_SHORT).show()

            intent = Intent(this, ReminderBroadcast::class.java)
            var pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

            var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        }

    }
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var name = "BENFIT NOTIFICATION" as CharSequence
            var description = "c'est l'heure de votre entrainement"
            var importance = NotificationManager.IMPORTANCE_DEFAULT
            var channel = NotificationChannel("isen.CedricLucieFlorent.benfit", name, importance)
            channel.description = description
            var notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}