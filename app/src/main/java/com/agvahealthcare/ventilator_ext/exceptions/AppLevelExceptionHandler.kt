package com.agvahealthcare.ventilator_ext.exceptions

import android.annotation.SuppressLint
import android.util.Log
import com.agvahealthcare.ventilator_ext.VentilatorApp
import com.agvahealthcare.ventilator_ext.logging.FileLogger
import java.text.SimpleDateFormat

class AppLevelExceptionHandler : Thread.UncaughtExceptionHandler {

    @SuppressLint("SimpleDateFormat") private val dateTimeFormatter = SimpleDateFormat("YYYYMMdd_HHmmss")

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        Log.i("APP_EXCEPTION_HANDLER", "Generating a crash report")

//        Log.e("THREAD STACKTRACE", "===============\n")
//        thread.stackTrace printTrace null
        Log.e("\nERROR STACKTRACE", "===============\n")
       exception.stackTrace printTrace null
        Log.e("\nCAUSE STACKTRACE", "===============\n")
        exception.cause?.stackTrace?.printTrace(0)

        val ctx=VentilatorApp.getInstance()?.applicationContext
        ctx?.let {
            Log.i("SERVERLOGGER-RAW", exception.stackTraceToString())

            //ServerLogger.e(it,exception)
           // FileLogger.e(it, exception)

  //          ServerLogger.e(it,exception)
            try {
                FileLogger.e(it, exception)
            }catch(e:Exception){

            }
        }
        // local file generating and saving
//        Environment.getExternalStorageDirectory()?.also {
//            val parentDir = File(it, "AgVa")
//            try {
//                if(parentDir.exists() || parentDir.mkdirs()) {
//
//                    Log.i("APP_EXCEPTION_HANDLER", "Creating folder for AgVa")
//
//
//                    val file = File(parentDir, "crash_report_${dateTimeFormatter.format(Date())}.txt")
//                    if (file.createNewFile()) {
//                        val writer = FileWriter(file)
//                        writer.append(exception.getStackTraceString())
//                        writer.flush()
//                        writer.close()
//                        Log.i("APP_EXCEPTION_HANDLER", "Generated the crash report")
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.i("APP_EXCEPTION_HANDLER", "Unable to create the crash report")
//            }
//
//        }

    }

     infix fun Array<StackTraceElement>.printTrace(index: Int?){
         if(index != null){
             get(index).apply {
                 Log.e("STACKTRACE POSITION $index", "==========================\n\n")
                 Log.e("STACKTRACE FILENAME", this.fileName)
                 Log.e("STACKTRACE METHOD", methodName)
                 Log.e("STACKTRACE LINE NUMBER", lineNumber.toString())
                 Log.e("STACKTRACE CLASSNAME", className)
             }
         } else {
             this.forEachIndexed { i, data ->
                 data.apply {
                     Log.e("\nSTACKTRACE POSITION $i", "==========================\n\n")
                     Log.e("STACKTRACE FILENAME", this.fileName)
                     Log.e("STACKTRACE METHOD", methodName)
                     Log.e("STACKTRACE LINE NUMBER", lineNumber.toString())
                     Log.e("STACKTRACE CLASSNAME", className)
                 }
             }
         }
    }
}