package com.example.snapchatandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    var snapImageView : ImageView? = null
    var messageEditText: TextView? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        snapImageView = findViewById(R.id.snapImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    fun getPhoto() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
    fun nextClicked(view: View){
        // Get the data from an ImageView as bytes
        snapImageView?.isDrawingCacheEnabled = true
        snapImageView?.buildDrawingCache()
        val bitmap = (snapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()



        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(baseContext, "Upload Failed",
                Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {taskSnapshot ->
            val downloadUrl = taskSnapshot.getMetadata()!!.getReference()!!.getDownloadUrl()
            //Log.i("msg",downloadUrl.toString())
            val intent = Intent(this,CreateUserActivity::class.java)
            intent.putExtra("imageUrl",downloadUrl.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEditText?.text.toString())
            startActivity(intent)
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
    fun imageClicked(view: View){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            getPhoto()
        }

    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

                snapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }
}
