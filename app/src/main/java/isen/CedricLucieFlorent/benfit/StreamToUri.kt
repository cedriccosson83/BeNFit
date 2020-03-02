package isen.CedricLucieFlorent.benfit

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_sign_up.*

class StreamToUri/* : AppCompatActivity */{
    private var context : Context
    private var activity : Activity
    private val codePermImage = 101
    private val codeReqImage = 102
    private val codeResExt = 101
    var imageUri: Uri
    private var contResolv: ContentResolver
    //private var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    constructor(context: Context, activity: Activity, contResolv: ContentResolver) {
        this.context = context
        this.activity = activity
        this.contResolv = contResolv
        imageUri = Uri.EMPTY
    }

    fun askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA), codePermImage)
        }
        else if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), codeResExt)
        }
        else { openCamera()}
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DESCRIPTION, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        val notsureuri = contResolv.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (notsureuri != null ){
            imageUri = notsureuri}
        val imagefromgalleryIntent = Intent(Intent.ACTION_PICK)
        imagefromgalleryIntent.type = "image/png"
        imagefromgalleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)


        val chooseIntent= Intent.createChooser(imagefromgalleryIntent, "Gallery")
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        activity.startActivityForResult(chooseIntent, codeReqImage)
    }

    fun manageRequestPermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == codePermImage){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                openCamera()
            }else{
                Toast.makeText(context, "Camera permissions required", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun manageActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == codeReqImage){
            if(data?.data != null){
                imageUri = data.data as Uri
            }
        }
    }
}