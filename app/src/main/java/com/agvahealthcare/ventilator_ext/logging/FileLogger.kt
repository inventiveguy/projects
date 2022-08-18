package com.agvahealthcare.ventilator_ext.logging

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MOHIT MALHOTRA
 *
 * FileLogger : Class is customized to log data
 * in a separate file under a specific app folder
 */

abstract class FileLogger {

    companion object {

        private const val TAG = "FileLogger"
        private const val PATH_DEBUG_LOG_FILE = "debug_report"
        private const val PATH_EXCEPTION_LOG_FILE = "crash_report"
        @SuppressLint("SimpleDateFormat") private val fileDateTimeFormatter = SimpleDateFormat("YYYYMMdd_HHmmss")


        fun d(ctx: Context?, err: Throwable) {
            if (ctx == null) {
                Log.w(TAG, "Context is null, Unable to log data")
                return
            }

            d(ctx, err.stackTraceToString())
        }

        fun d(ctx: Context, data: String) {
            if (!writeFile(ctx, PATH_DEBUG_LOG_FILE, data)) {
                Log.w(TAG, "Unable to write to " + PATH_DEBUG_LOG_FILE)
            }
        }

        fun e(ctx: Context?, err: Throwable) {
            if (ctx == null) {
                Log.w(TAG, "Context is null, Unable to log data")
                return
            }
            e(ctx, err.stackTraceToString())
        }

        fun e(ctx: Context, data: String) {
            if (!writeFile(ctx, PATH_EXCEPTION_LOG_FILE, data)) {
                Log.w(TAG, "Unable to write to " + PATH_EXCEPTION_LOG_FILE)
            }
        }

        private fun writeFile(ctx: Context, fileName: String, data: String): Boolean {
            var isSuccess = false

            val path = File(Environment.getExternalStorageDirectory(), AppUtils.PATH_FOLDER_AGVA + File.separator + AppUtils.PATH_FOLDER_LOGS)
            val isPathAccessible = path.exists() || path.mkdirs()
            if (isPathAccessible) {
                Log.i(TAG, "Creating folder for AgVa")

                val file = File(path, "${fileName}_${fileDateTimeFormatter.format(Date())}.log")
                try {
                    if(file.createNewFile()){
                        FileWriter(file, true).use {
                            it.write(data)
                            it.flush()
                            isSuccess = true
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return isSuccess
        }

        private fun readFile(ctx: Context, fileName: String): String? {
            val filePath = Environment.getExternalStorageDirectory()
                .toString() + File.separator + AppUtils.PATH_FOLDER_AGVA + File.separator + AppUtils.PATH_FOLDER_LOGS + File.separator + "${fileName}_${fileDateTimeFormatter.format(Date())}.log"
            try {
                ctx.openFileInput(filePath).use { fis ->
                    try {
                        BufferedReader(InputStreamReader(fis)).use { reader ->
                            val buffer = StringBuffer()
                            var line: String? = ""
                            while (reader.readLine().also { line = it } != null) buffer.append(line)
                            return buffer.toString()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}