package com.example.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File
private const val TAG=".CleanUpWorker"
class CleanUpWorker(private val context: Context, private val params:WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {
        makeStatusNotification("CleaningUp Resorces",context)
        try{
            val file = File(context.filesDir, OUTPUT_PATH)
            if(file.exists()) {
              file.listFiles()?.let {
                    for (f in it) {
                        val name = f.name
                        if (!name.isEmpty()&&name.endsWith(".png")){
                            val deleted = f.delete()
                            Log.e(TAG,"File $name - $deleted")
                        }
                    }
                }
            }
            return Result.success()
        }catch (throwable:Throwable){
            return Result.failure()
        }
    }
}