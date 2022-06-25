package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.R
import java.lang.Exception
private const val TAG=".BlurWorker"
class BlurWorker(private val context: Context,
                   private val params:WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {
        val context = context.applicationContext
        makeStatusNotification("Blurring Image...",context)
        try{
            val picture = BitmapFactory.decodeResource(context.resources, R.drawable.android_cupcake)
            val output = blurBitmap(picture,context.applicationContext)
            val uri = writeBitmapToFile(context,output)

            makeStatusNotification("Output is $uri",context)
            return Result.success()
        }catch (throwable:Throwable){
            Log.e(TAG,"Error occurred")
            return Result.failure()
        }
    }

}