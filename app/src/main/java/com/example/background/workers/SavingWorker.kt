package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*
private const val TAG=".SavingWorker.kt"

class SavingWorker(private val context: Context, private val params:WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault()
        )
        val title = "Blurred Image"
        makeStatusNotification("Saving Image",context)
        sleep()
        val cr = context.contentResolver
        try{
            val picture = inputData.getString(KEY_IMAGE_URI)
            val bitmap =  BitmapFactory.decodeStream(cr.openInputStream(Uri.parse(picture)))
            val imageUri = MediaStore.Images.Media.insertImage(cr,bitmap,title,dateFormat.format(Date()))
            if(imageUri.isNullOrEmpty()){
                workDataOf(KEY_IMAGE_URI to imageUri)
                return Result.success()
            }else{
                return Result.failure()
            }
        }catch (e:Exception){
            Log.e(TAG,"Error Exception")
            return Result.failure()
        }
    }
}