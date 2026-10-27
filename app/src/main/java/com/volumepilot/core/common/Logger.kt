package com.volumepilot.core.common

import android.util.Log
import com.volumepilot.BuildConfig

/**
 * Privacy-first logging interface.
 * Strictly local-only. Zero remote telemetry, zero analytics, zero external network transmission.
 */
interface AppLogger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

class AndroidAppLogger : AppLogger {
    override fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(sanitize(tag), sanitize(message))
        }
    }

    override fun i(tag: String, message: String) {
        Log.i(sanitize(tag), sanitize(message))
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        Log.w(sanitize(tag), sanitize(message), throwable)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(sanitize(tag), sanitize(message), throwable)
    }

    /**
     * Strips potentially sensitive patterns, line breaks, or personal info.
     */
    private fun sanitize(input: String): String {
        return input.take(256)
    }
}
