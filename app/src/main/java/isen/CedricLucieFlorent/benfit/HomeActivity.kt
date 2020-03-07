package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        home_programs.setOnClickListener {
            startActivity(Intent(this, ProgramFeedActivity::class.java))
        }

        home_sessions.setOnClickListener {
            startActivity(Intent(this, SessionFeedActivity::class.java))
        }

        home_feed.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
        }

        home_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }



}
