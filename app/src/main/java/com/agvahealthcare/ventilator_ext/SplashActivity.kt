package com.agvahealthcare.ventilator_ext

import android.content.*
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.agvahealthcare.ventilator_ext.connection.support_threads.HandshakingTask
import com.agvahealthcare.ventilator_ext.dashboard.BaseActivity
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.service.UsbService
import com.agvahealthcare.ventilator_ext.utility.*
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.ACK_CODE_5004
import com.agvahealthcare.ventilator_ext.utility.utils.IntentFactory
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {

    private val REQUEST_STORAGE_PERMISSION_CODE = 111
    private var isHandshakeAcknowledged = false
    private var communicationService: CommunicationService? = null
    private var handshakingTask: HandshakingTask? = null
    private var preferenceManager: PreferenceManager? = null
    private var progressThread: Thread? = null
    private var isSplashTimerRunning = false
    private var isHandshakingCompleted = false
    private var progressThreadState = true
    private var isReadingFromConnection = false
    private var isServiceBound = false
    private var hsCalibrationDialog: AlertDialog? = null
    private var hsFailureDialog: AlertDialog? = null

    private var commServiceIntent: Intent? = null


    // Service connection for bound services
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, ibinder: IBinder) {
            isServiceBound = true
            communicationService = (ibinder as CommunicationService.LocalBinder).service
            communicationService?.makeLog(SplashActivity::class.java.simpleName)
            Log.i("SERVICE_CHECK", "calling device connect from service")
            onDeviceConnect()

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
            isReadingFromConnection = false
        }
    }

    /*
     * This provides intent filter for the Gatt Data Receiver
     */

    private fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_CONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_DISCONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_HANDSHAKE_COMPLETED)
        intentFilter.addAction(IntentFactory.ACTION_HANDSHAKE_TIMEOUT)
        intentFilter.addAction(IntentFactory.ACTION_ACK_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_HANDSHAKE_CALIBRATION_AVAILABLE)
        return intentFilter
    }


    private val connReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == null) return
            when (intent.action) {

                IntentFactory.ACTION_DEVICE_CONNECTED -> {
                    Log.i("SPLASH_CHECK", "ventilator connected")
                    onDeviceConnect()
                }
                IntentFactory.ACTION_DEVICE_DISCONNECTED -> {
                    Log.i("SPLASH_CHECK", "ventilator disconnected")
                    onDeviceDisconnect()
                }

                IntentFactory.ACTION_HANDSHAKE_COMPLETED -> {
                    preferenceManager?.apply {
                        setDeepSleeped(false)
                    }
                    stopHandshaking()
                    isHandshakingCompleted = true
                    runOnUiThread {
                        tvUpdateMsg.text = getString(R.string.hint_handshake_completed)
                    }

                    Thread {
                        var i = progressBar.progress
                        while (i < 100) {
                            val prog = i
                            try {
                                Thread.sleep(10)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            runOnUiThread { progressBar.progress = prog }
                            i += 1
                        }
                        runOnUiThread {
                            Handler(Looper.getMainLooper()).postDelayed({
                                Log.i("STANDBY", "Sandby is false");

                                // Dismiss progress bar after 2 seconds
                                val intentData =
                                    Intent(this@SplashActivity, MainActivity::class.java)
                                intentData.putExtra(IS_STAND_BY, false)
                                startActivity(intentData)
                                finish()
                            }, 250)
                        }
                    }.start()
                }

                IntentFactory.ACTION_HANDSHAKE_TIMEOUT -> {
                    isHandshakeAcknowledged = false
                    showTimeoutState()
                    stopHandshaking()
                    isHandshakingCompleted = false

                    if (hsFailureDialog == null) {
                        hsFailureDialog = DialogBoxFactory.showDialog(
                            this@SplashActivity,
                            "CONNECTION FAILED",
                            "Unable to verify the connection with the ventilator",
                            "Try Again"
                        ) {
                            try {
                                validateSplash()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ToastFactory.custom(
                                    this@SplashActivity,
                                    getString(R.string.error_in_connection_restart_app)
                                )
                            }
                        }
                    }

                }

                IntentFactory.ACTION_ACK_AVAILABLE -> {
                    val ack = intent.getStringExtra(VENTILATOR_ACK)
                    Log.i("ACK_CHECK", "ACK received @$ack")

                    // FILTER FOR ACK 51 : Double handshake completed
                    if (ack != null && ack.isNotEmpty()) {
                        when (ack) {
                            ACK_CODE_5004 ->{
                               // ToastFactory.custom(this@SplashActivity,"The handshake ack received")
                                preferenceManager?.apply {
                                    setDeepSleeped(false)
                                }
                                stopHandshaking()
                                isHandshakingCompleted = true
                                runOnUiThread {
                                    tvUpdateMsg.text = getString(R.string.hint_handshake_completed)
                                }

                                Thread {
                                    var i = progressBar.progress
                                    while (i < 100) {
                                        val prog = i
                                        try {
                                            Thread.sleep(10)
                                        } catch (e: InterruptedException) {
                                            e.printStackTrace()
                                        }
                                        runOnUiThread { progressBar.progress = prog }
                                        i += 1
                                    }
                                    runOnUiThread {
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            Log.i("STANDBY", "Sandby is false");

                                            // Dismiss progress bar after 2 seconds
                                            val intentData =
                                                Intent(this@SplashActivity, MainActivity::class.java)
                                            intentData.putExtra(IS_STAND_BY, false)
                                            startActivity(intentData)
                                            finish()
                                        }, 250)
                                    }
                                }.start()
                            }
                        }
                    }

                }

                IntentFactory.ACTION_HANDSHAKE_CALIBRATION_AVAILABLE -> {
                    val calibrationValue = intent.getStringExtra(VENTILATOR_HANDSHAKE_CALIBRATION)
                    var isPressureValid = false
                    var isFlowValid = false
                    if (calibrationValue != null) {
                        try {
                            val pressure = calibrationValue.substring(0, 2).toInt()
                            val flow = calibrationValue.substring(2, 5).toInt()
                            Log.i("HSCALIB", "FLOW=$flow, PRESSURE=$pressure")
                            isPressureValid = pressure < 5
                            isFlowValid = flow < 50
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if (isHandshakeAcknowledged) {
                        if (isPressureValid && isFlowValid) {
                            runOnUiThread {
                                tvUpdateMsg.text = getString(R.string.hint_calibration_completed)
                            }
                            broadcastHandshakeCompleted()
                        } else {
                            runOnUiThread {
                                tvUpdateMsg.text = getString(R.string.hint_calibration_failure)
                            }
                            var msg: String? = null
                            if (!isPressureValid) {
                                msg = "Error detected in pressure sensor calibration"
                            }
                            if (!isFlowValid) {
                                msg = "Error detected in flow sensor calibration"
                            }
                            msg += ". Please contact the manufacturer for support"
                            if (hsCalibrationDialog == null) hsCalibrationDialog = DialogBoxFactory.showDialog(
                                this@SplashActivity,
                                getString(R.string.hint_calibration_error),
                                msg
                            ) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    communicationService?.takeIf { it.isPortsConnected}?.apply {
                                        send("CE")
                                    }
                                }, 1000)
                                broadcastHandshakeCompleted()
                            }
                        }
                    }
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)



//        hideSystemUI()
       /* try {
           throw IOException()
        } catch (e:Throwable){
           ServerLogger.d(applicationContext,e.stackTraceToString(),javaClass.simpleName)
        }*/



        setContentView(R.layout.activity_splash)

        AppUtils.keepScreenAlive(this@SplashActivity, true)


        checkPermissions()
        preferenceManager = PreferenceManager(this)

        // setting app version
        tvVersion.text = "${getString(R.string.hint_version)}  ${VentilatorApp?.getInstance()?.getVersion()}"
        startSplashTimerWithState()
        registerReceiver(connReceiver, getIntentFilter())
        initServices()
        doBindService()

    }

    private fun checkPermissions(){
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.i("APP_EXCEPTION_HANDLER", "Already has storage permissions")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_STORAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.i("APP_EXCEPTION_HANDLER", "Permission granted for storage")
        }
    }

    private fun onDeviceConnect() {
        Log.i("SERVICE_CHECK", "called device connect with com service " + communicationService
                + " | ventCOnnected = " + communicationService?.isVentilatorConnected
        + " | hidConnected = " + communicationService?.isHIDConnected)

        // FOR ALLOWING SCREEN AUTO LOCK
        AppUtils.keepScreenAlive(this@SplashActivity, true)
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            //showConnectState()
            validateSplash()
            if (!isReadingFromConnection) {
                Log.i("SERVICE_CHECK", "connection status = $isPortsConnected during DEVICE_CONNECTED check")

                isReadingFromConnection = true
                startReading()
            }
        } ?: kotlin.run {
            ToastFactory.custom(
                this@SplashActivity,
                "Unable to start the ventilator, please restart manually."
            )
        }
    }


    private fun onDeviceDisconnect() {

        // FOR ALLOWING SCREEN AUTO LOCK
        AppUtils.keepScreenAlive(this@SplashActivity, false)
        showDisconnectState()
        stopHandshaking()
        if (isReadingFromConnection) {
            communicationService?.apply {
                stopReading()
            }
            isReadingFromConnection = false
        }

        hsCalibrationDialog?.takeIf {it.isShowing}?.apply {
            cancel()
        }

        hsFailureDialog?.takeIf {it.isShowing}?.apply {
            cancel()
        }

    }

    private fun startSplashTimerWithState() {
        if (isDeepSleep()) {
            Log.i("SLEEP_CHECK", "Ventilator was in deep sleep in last session")
            showDeepSleepState()
        } else {
            Log.i("SLEEP_CHECK", "Ventilator was normally shut down")
            startSplashTimer()
        }

    }

    private fun startSplashTimer() {
        // Timer is required for the permission to resolve before validating the screen
        if(!isSplashTimerRunning) {
            isSplashTimerRunning = true
            Handler(Looper.getMainLooper()).postDelayed({
                validateSplash()
                isSplashTimerRunning = false
            }, SPLASH_SCREEN_LIFE)
        }

    }

    private fun validateSplash() {
//        Log.d("portconnected",communicationService?.isPortsConnected?.toString() ?: "False")
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            showConnectState()
            startHandshakingWithThreadSafety()
        } ?: kotlin.run {
            Log.i("CONNECTION_STATE_CHECK", "Comm service is null")
            showDisconnectState()
            // FOR ALLOWING SCREEN AUTO LOCK
            AppUtils.keepScreenAlive(this@SplashActivity, false)
        }
    }


    private fun broadcastHandshakeCompleted() {
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            sendBroadcastHandshakeCompleted()
        }
    }


    /*
  * This method will start handshaking countdown thread
  */
    private fun startHandshakingWithThreadSafety() {
        Handler(Looper.getMainLooper()).post {
            try {
                startHandshaking()
            } catch (e: Exception) {
                ToastFactory.custom(
                    this@SplashActivity,
                    getString(R.string.error_in_connection_restart_app)
                )
                e.printStackTrace()
            }
        }
    }

    // Legacy
    private fun startHandshaking() {
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            if (handshakingTask == null)
                handshakingTask = HandshakingTask(this)
            else {
                if (handshakingTask?.isRunning == true) return
            }

            // send WAKEUP for coming out of STANDBY incase ventilator is in STANDBY

            Handler(Looper.getMainLooper()).postDelayed({
                send(resources.getString(R.string.cmd_vent_wakeup))
            }, 5000)

/*
            // resend WAKEUP
            Handler(Looper.getMainLooper()).postDelayed({
                send(resources.getString(R.string.cmd_vent_wakeup))
            }, 10000)
*/


            // starting handshaking
            Handler(Looper.getMainLooper()).postDelayed({
                handshakingTask?.apply {
                    Log.i("HS_CHECK", "HS started successfully inside 1000 ms looper")
                    start()
                }
            }, 100)

            // showing progress bar
            if (progressThread == null) {
                progressThread = Thread {
                    var i = 0
                    while (i < 70) {
                        if (progressThreadState) {
                            try {
                                Thread.sleep(30)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            val finalFixI = i
                            runOnUiThread { progressBar.progress = finalFixI }
                        } else {
                            return@Thread
                        }
                        i += 1
                    }
                }
            }
            progressThreadState = true
            progressThread?.takeUnless { it.isAlive }?.start()
        }

    }

    /*
     * This method will stop handshaking thread
     */
    private fun stopHandshaking() {
        handshakingTask?.stop()
        progressThreadState = false
        progressThread = null
    }



    private fun showDisconnectState() {
        layoutProgress.visibility = View.GONE
        progressBar.progress = 0
        tvSwitchOffMsg.text = getString(R.string.press_switch_onn_manually)
        layoutVentiSwitchOff.visibility = View.VISIBLE

    }

    private fun showConnectState() {
        layoutVentiSwitchOff.visibility = View.GONE
        progressBar.progress = 0
        layoutProgress.visibility = View.VISIBLE
    }

    private fun showTimeoutState() {
        layoutVentiSwitchOff.visibility = View.GONE
        layoutProgress.visibility = View.GONE
        progressBar.progress = 0
    }

    private fun showDeepSleepState() {
        tvSwitchOffMsg.text = getString(R.string.press_switch_onn)
        layoutVentiSwitchOff.visibility = View.VISIBLE
        layoutProgress.visibility = View.GONE
        progressBar.progress = 0
    }


    override fun onDestroy() {
        doUnbindService()

        unregisterReceiver(connReceiver)
        super.onDestroy()
    }


    private fun isDeepSleep(): Boolean {
        return preferenceManager != null && (preferenceManager?.readIsDeepSleeped() == true)
    }

    private fun initServices() {
        if (commServiceIntent == null) {
            Log.i("SERVICE_CHECK", "Service intent created from init")
            commServiceIntent = Intent(this@SplashActivity, UsbService::class.java)
        }

        // checking and action

//        preferenceManager?.apply {
//            if (!readServiceStatus())
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Log.i("SERVICE_CHECK", "Service starting ")
                    startService(commServiceIntent)
                    Log.i("SERVICE_CHECK", "Service has started by startService()")
                }
//        }

    }

    /*
     * This method binds the required services
     * and set the flags to active
     */
    private fun doBindService() {
        if (!isServiceBound && commServiceIntent != null) {
            bindService(commServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
            Log.i("SERVICE_CHECK", "Service bound ")
            isServiceBound = true
        }
    }

    /*
     * This method checks the bound services
     * and unbind them
     */
    private fun doUnbindService() {
        if (isServiceBound) {
            try {
                unbindService(mServiceConnection)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                //ServerLogger.e(this@SplashActivity, e)

            }
        }
    }
}