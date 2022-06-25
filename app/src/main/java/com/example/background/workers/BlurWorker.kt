package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.Exception
import java.lang.IllegalArgumentException

private const val TAG=".BlurWorker"
class BlurWorker(private val context: Context,
                   private val params:WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {
        val context = context.applicationContext
        makeStatusNotification("Blurring Image...",context)
        sleep()
        try{

            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            if(resourceUri!!.isEmpty()){
                throw IllegalArgumentException("Empty data")
            }
            val cr = context.contentResolver

            val picture = BitmapFactory.decodeStream(cr.openInputStream(Uri.parse(resourceUri)))
            val output = blurBitmap(picture,context.applicationContext)
            val uri = writeBitmapToFile(context,output)

            makeStatusNotification("Output is $uri",context)
            val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())
            return Result.success()
        }catch (throwable:Throwable){
            Log.e(TAG,"Error occurred")
            return Result.failure()
        }
    }
}