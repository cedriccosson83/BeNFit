package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ProfileActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_profile, frameLayout)
    }
}
