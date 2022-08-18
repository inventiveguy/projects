package com.agvahealthcare.ventilator_ext

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log
import com.agvahealthcare.ventilator_ext.exceptions.AppLevelExceptionHandler
import com.agvahealthcare.ventilator_ext.utility.SystemMonitor
import com.scichart.charting.visuals.SciChartSurface


class VentilatorApp : Application() {

    companion object {
        private var sInstance: VentilatorApp? = null

        private var appVersion = "N/A"

        fun getInstance(): VentilatorApp? {
            return sInstance
        }



    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this

        setupAppLevelExceptionHandler()

        if(isSystemSigned()) {
            Log.i("SYSCHECK", "App is signed by system")
            setUpKiosk(true)
        }


        setUpSciChartLicense()

        // init app version
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            appVersion = pInfo.versionName
        } catch (e: NameNotFoundException) {
            Log.e("VERSION_CHECK", "Unable to fetch the version name from gradle file")
            e.printStackTrace()
        }
        SystemMonitor.start()
    }




    private fun setUpSciChartLicense() {

        try {
            SciChartSurface.setRuntimeLicenseKey(BuildConfig.SCHICHART_API)
        } catch (e: Exception) {
            Log.e("SciChart", "Error when setting the license", e)
        }

    }

    override fun onTerminate() {
        SystemMonitor.stop()
        super.onTerminate()

    }





    /**
     * Requires system signature to
     * hide/unhide status bar
     */
    private fun setUpKiosk(isActive: Boolean){
        val action = if(isActive) "com.outform.hidebar" else "com.outform.unhidebar"
        sendBroadcast(Intent(action))
    }

    fun setupAppLevelExceptionHandler() {
        Log.i("APP_EXCEPTION_HANDLER", "Setting default error handler")
        Thread.setDefaultUncaughtExceptionHandler(AppLevelExceptionHandler())
    }

    fun getVersion(): String {
        return appVersion
    }

    private fun isSystemSigned(): Boolean {
        val pm = packageManager
        return try {
            val pi_app = pm.getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES)
            val pi_sys = pm.getPackageInfo("android", PackageManager.GET_SIGNATURES)
            Log.i("SYSCHECK", "Found signatures")

            pi_app?.signatures != null && pi_sys.signatures[0] == pi_app.signatures[0]
        } catch (e: NameNotFoundException) {
            Log.i("SYSCHECK", "Unable to load the signatures")
            e.printStackTrace()
            false
        }
    }

}