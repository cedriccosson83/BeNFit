package isen.CedricLucieFlorent.benfit

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StreamToUri(
    private var context: Context,
    private var activity: Activity,
    private var contResolv: ContentResolver
) {
    private val codePermImage = 101
    private val codeReqImage = 102
    private val codeResExt = 101
    var imageUri: Uri

    init {
        imageUri = Uri.EMPTY
    }

    fun askCameraPermissions(){
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), codePermImage)
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), codeResExt)
            else -> openCamera()
        }
    }

    private fun openCamera() {
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