package isen.CedricLucieFlorent.benfit

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.widget.ImageView


class MainActivity : MenuActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            layoutInflater.inflate(R.layout.activity_main, frameLayout)
            val img = findViewById<ImageView>(R.id.mainImage)
            setImageFromFirestore(this, img, "images/image.png")
    }
}
