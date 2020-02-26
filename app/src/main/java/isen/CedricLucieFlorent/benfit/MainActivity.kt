package isen.CedricLucieFlorent.benfit

import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_main, frameLayout)
        val img = findViewById<ImageView>(R.id.mainImage)
        val auth = FirebaseAuth.getInstance()
        val userid = auth.currentUser?.uid

        mainImage.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
            db.firestoreSettings = settings

            val storeRef: StorageReference? =
                FirebaseStorage.getInstance().getReference("users/$userid/profile.png")
            GlideApp.with(context).load(storeRef).into(img)
        }

        setImageFromFirestore(this, img, "users/$userid/profile.png")
    }
}
