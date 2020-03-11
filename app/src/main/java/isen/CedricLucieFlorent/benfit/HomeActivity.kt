package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        homePrograms.setOnClickListener {
            startActivity(Intent(this, ProgramFeedActivity::class.java))
        }

        homeSessions.setOnClickListener {
            startActivity(Intent(this, SessionFeedActivity::class.java))
        }

        homeFeed.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
        }

        homeProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }



}
