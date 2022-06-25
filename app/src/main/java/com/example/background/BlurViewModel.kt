/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.Gravity.apply
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanUpWorker
import com.example.background.workers.SavingWorker


class BlurViewModel(application: Application) : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    var workStat: LiveData<List<WorkInfo>>
    val workManager:WorkManager = WorkManager.getInstance(application.applicationContext)
    init {
        imageUri = getImageUri(application.applicationContext)
        workStat = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }
    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    internal fun applyBlur(blurLevel: Int) {
        val continuation = workManager.beginUniqueWork(IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanUpWorker::class.java))
        val workRequest = OneTimeWorkRequestBuilder<BlurWorker>().setInputData(createInputDataFromUri()).build()
        val save = OneTimeWorkRequest.Builder(SavingWorker::class.java).addTag(TAG_OUTPUT).build()
        continuation.then(workRequest).then(save).enqueue()
    }
    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }
    private fun createInputDataFromUri() = Data.Builder().apply {
            imageUri?.let {
                putString(KEY_IMAGE_URI,imageUri.toString())
            }
        }.build()

    fun cancelWork(){
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()

        return imageUri
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }

    class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>):T {
            return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
                BlurViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
