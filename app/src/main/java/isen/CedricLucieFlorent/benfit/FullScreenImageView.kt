package isen.CedricLucieFlorent.benfit

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FullScreenImageView: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreenimage)
        val fullScreenImageView = findViewById<ImageView>(R.id.fullScreenImageView)
        val callingActivityIntent = intent
        if (callingActivityIntent != null) {
            val imageUri: String = callingActivityIntent.getStringExtra("url") ?: ""
            if (imageUri != "" && fullScreenImageView != null) {
                val storeRef: StorageReference?
                        = FirebaseStorage.getInstance().getReference(imageUri)
                GlideApp.with(this).load(storeRef).into(fullScreenImageView)
            }
        }
    }

}