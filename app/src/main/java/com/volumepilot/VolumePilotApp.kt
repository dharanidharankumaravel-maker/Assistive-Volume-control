package com.volumepilot

import android.app.Application
import com.volumepilot.core.common.AppLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VolumePilotApp : Application() {

    @Inject
    lateinit var logger: AppLogger

    override fun onCreate() {
        super.onCreate()
        com.volumepilot.core.common.LocalCrashRecorder.install(this)
        logger.i(TAG, "VolumePilot initialized. Privacy-first, offline architecture.")
    }


    companion object {
        private const val TAG = "VolumePilotApp"
    }
}
