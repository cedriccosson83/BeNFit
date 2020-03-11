package isen.CedricLucieFlorent.benfit

import android.app.*
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_notif.*
import java.text.SimpleDateFormat
import java.util.*


class NotifActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_notif, frameLayout)
        createNotificationChannel()

        auth = FirebaseAuth.getInstance()

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

        var dayselec = 0
        var monthselec = 0
        var yearselec = 0
        var hourselec = 0
        var minselec  = 0

        inputDate.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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
        }

        inputTime.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val tpd = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
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
        }

        btn_create.setOnClickListener{

            val sessionId = intent.getStringExtra("sessionID")
            val userID = intent.getStringExtra("userId")
            val fromAct = intent.getStringExtra("fromAct")
            val showSessionId = intent.getStringExtra("showSessionId")

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.MONTH, monthselec)
            calendar.set(Calendar.DAY_OF_MONTH, dayselec)
            calendar.set(Calendar.YEAR, yearselec)
            calendar.set(Calendar.HOUR_OF_DAY, hourselec)
            calendar.set(Calendar.MINUTE, minselec)
            val monthr = monthselec + 1

            database.getReference("notifications/${userID}/${sessionId}").setValue("${hourselec}:${minselec} ${dayselec}/${monthr}/${yearselec}")
            Toast.makeText(this, "Rappel défini !" , Toast.LENGTH_SHORT).show()
            intent = Intent(this, ReminderBroadcast::class.java)
            val random = (1 until 9).random()
            val pendingIntent = PendingIntent.getBroadcast(this, random, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            if (fromAct == "Feed"){
                this.startActivity(Intent(this, SessionFeedActivity::class.java))
            }

            else if (fromAct == "Show"){
                val intentshow = Intent(this, ShowSessionActivity::class.java)
                intentshow.putExtra("sessionId",showSessionId)
                this.startActivity(intentshow)
            }

        }

    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "BENFIT NOTIFICATION" as CharSequence
            val description = "c'est l'heure de votre entrainement"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("isen.CedricLucieFlorent.benfit", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}