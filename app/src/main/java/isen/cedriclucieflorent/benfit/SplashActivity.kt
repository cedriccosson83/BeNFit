package isen.cedriclucieflorent.benfit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private val splashDuration: Long = 1600
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        imageBenfit.startAnimation(animation)

        val intent = Intent(this,SignInActivity::class.java)
        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}
