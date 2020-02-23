package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.common.io.Files.isFile
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.net.URI


class MainActivity : MenuActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            //deleteCache(this)
            layoutInflater.inflate(R.layout.activity_main, frameLayout)
            //val img = findViewById<ImageView>(R.id.mainImage)
            //val auth = FirebaseAuth.getInstance()
            //val userid = auth.currentUser?.uid

            //mainImage.setImageURI(Uri.parse("gs://benfit-284ff.appspot.com/users/VA1wZJSkGZYnMLHU09XNRIyaCF43/088567df-a9b7-4810-aa14-118f8c9d0b10"))
    }

}
