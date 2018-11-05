package com.example.renaudjulien.dmsn

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Collections.rotate

class NewPostActivity : AppCompatActivity() {
    private val IMAGE_DIRECTORY = "/DMSNIMAGES"
    private val GALLERY = 1
    private val CAMERA = 2
    private val EXT_STORAGE = 3

    private lateinit var imageviewPhoto: ImageView
    private lateinit var buttonPost: Button
    private lateinit var buttonMap: Button

    lateinit var desc : String
    lateinit var intentdata : Intent
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        imageviewPhoto = findViewById(R.id.imageView_photo)
        buttonPost = findViewById(R.id.button_Post)
        buttonMap = findViewById(R.id.button_location)



        showPictureDialog()

        buttonMap.setOnClickListener({
            val intent = Intent(this@NewPostActivity,MapsActivity::class.java)
            startActivity(intent)
        })

        buttonPost.setOnClickListener({

            //imageviewPhoto.buildDrawingCache()
            //val bitmap = imageviewPhoto.getDrawingCache()
            //desc = findViewById<EditText>(R.id.editText_comments).text.toString()
            //postPhoto(bitmap, "Image DMSN", desc)
            //on fait quelque chose quand on publie..

            val intent = Intent(this@NewPostActivity,WallActivity::class.java)
            startActivity(intent)
        })


    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.l_select))
        val pictureDialogItems = arrayOf(getString(R.string.l_useGallery), getString(R.string.l_useCamera))
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallary()
                        1 -> takePhotoFromCamera()
                    }
                })
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val rxPermissions = RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA) // ask single or multiple permission once
                .subscribe({
                    if(it){
                        Toast.makeText(this@NewPostActivity, "Can use camera !", Toast.LENGTH_SHORT).show()
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }else{
                        Toast.makeText(this@NewPostActivity, "Can not use camera !", Toast.LENGTH_SHORT).show()
                    }
                })
    }



    private fun postPhoto(yourBitmap: Bitmap,yourTitle:String, yourDescription:String ){

        val rxPermissions = RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE) // ask single or multiple permission once
                .subscribe({
                    if(it){
                        Toast.makeText(this@NewPostActivity, "Can use External storage!", Toast.LENGTH_SHORT).show()
                        MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle , yourDescription)
                        Toast.makeText(this@NewPostActivity, "Image Saved in gallery!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@NewPostActivity, "Can not use External storage !", Toast.LENGTH_SHORT).show()
                    }
                })


    }
    fun getPath(uri: Uri): String {
        var cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = contentResolver.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null)
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }
    @RequiresApi(Build.VERSION_CODES.N)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@NewPostActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageviewPhoto?.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@NewPostActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageviewPhoto?.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@NewPostActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis().toString() + ".jpg")
            f.createNewFile()

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

}
