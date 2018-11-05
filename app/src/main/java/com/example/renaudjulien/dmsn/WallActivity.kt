package com.example.renaudjulien.dmsn

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*
import android.icu.util.ULocale.getLanguage
import android.media.Image
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.support.v4.view.GestureDetectorCompat
import android.view.MotionEvent
import android.widget.*
import android.view.GestureDetector
import java.io.File
import java.lang.Math.abs
import android.text.method.Touch.onTouchEvent
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder


lateinit var NewPostButton : Button
lateinit var langButton : Button
lateinit var mainImgContainer: ImageView

lateinit var gDetector: GestureDetectorCompat
var index :Int =-1
class WallActivity : AppCompatActivity(), GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
       return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
       return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        loadImages()
        return super.onTouchEvent(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        //loadImages()
        return super.onTouchEvent(e)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)

        gDetector = GestureDetectorCompat(this,this)
        gDetector.setOnDoubleTapListener(this)


        mainImgContainer = findViewById(R.id.imageView_wall)
        NewPostButton = findViewById(R.id.button_newPost)
        langButton = findViewById(R.id.button_lang)


        NewPostButton.setOnClickListener({

            val intent = Intent(this, NewPostActivity::class.java)
            startActivity(intent)

        })

        langButton.setOnClickListener({
            SwipeLanguage()
            //Toast.makeText(this@WallActivity, Locale.getDefault().toString(), Toast.LENGTH_SHORT).show()
        })

        loadImages()




    }



    fun loadImages(){

        index+=1
        val dir = File(getExternalStorageDirectory().toString() + "/DMSNIMAGES")
        val files = dir.listFiles()
        val numberOfImages = files.size


        Toast.makeText(this@WallActivity,"il y a "+numberOfImages.toString(),Toast.LENGTH_SHORT).show()
        if(numberOfImages>index){

            var bitmap  :Bitmap = BitmapFactory.decodeFile(files[index].absolutePath)
            mainImgContainer.setImageBitmap(bitmap)
        }else{
            index = -1
        }

    }
    fun changeLang(lang: String) {

    }
    fun SwipeLanguage(){

        val config = baseContext.getResources().getConfiguration()
        val locale : Locale
        if(Locale.getDefault().toString() == "en") {
            locale = Locale("fr")
        }else{
            locale = Locale("en")
        }
        Locale.setDefault(locale)
        config.locale = locale
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics())
        recreate()
    }

}
