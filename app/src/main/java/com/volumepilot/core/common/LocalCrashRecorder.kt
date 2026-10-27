package com.volumepilot.core.common

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LocalCrashRecorder {

    private const val CRASH_FILE_NAME = "local_crash_log.txt"
    private const val MAX_LOG_SIZE_BYTES = 100 * 1024 // 100 KB ceiling to avoid storage bloat

    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                recordCrash(context.applicationContext, thread, throwable)
            } catch (_: Exception) {
                // Never crash inside the crash handler
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun recordCrash(context: Context, thread: Thread, throwable: Throwable) {
        val file = File(context.filesDir, CRASH_FILE_NAME)
        if (file.exists() && file.length() > MAX_LOG_SIZE_BYTES) {
            file.delete()
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val logEntry = buildString {
            appendLine("=== CRASH REPORT ===")
            appendLine("Timestamp: $timestamp")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
            appendLine("Android OS: API ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
            appendLine("Thread: ${thread.name} (id: ${thread.id})")
            appendLine("Exception: ${throwable::class.java.name}: ${throwable.message}")
            appendLine("Stack Trace:")
            appendLine(stackTrace)
            appendLine("====================")
            appendLine()
        }

        file.appendText(logEntry)
    }

    fun getCrashLogs(context: Context): String? {
        val file = File(context.filesDir, CRASH_FILE_NAME)
        return if (file.exists() && file.length() > 0) {
            file.readText()
        } else {
            null
        }
    }

    fun clearCrashLogs(context: Context): Boolean {
        val file = File(context.filesDir, CRASH_FILE_NAME)
        return if (file.exists()) {
            file.delete()
        } else {
            true
        }
    }
}
