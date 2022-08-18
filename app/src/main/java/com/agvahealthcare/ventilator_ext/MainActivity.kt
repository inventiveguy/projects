package com.agvahealthcare.ventilator_ext

import android.app.ProgressDialog
import android.content.*
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.*
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agvahealthcare.ventilator_ext.alarm.limit_one.EncoderValue
import com.agvahealthcare.ventilator_ext.alarm.limit_one.KnobParameterModel
import com.agvahealthcare.ventilator_ext.callback.*
import com.agvahealthcare.ventilator_ext.connection.support_threads.PingingTask
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterClickListener
import com.agvahealthcare.ventilator_ext.dashboard.BaseLockActivity
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardActivity
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.logs.event.EventViewModel
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.model.SensorCalibration
import com.agvahealthcare.ventilator_ext.modes.ModeDialogFragment
import com.agvahealthcare.ventilator_ext.modes.OnModeConfirmListener
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.service.UsbService
import com.agvahealthcare.ventilator_ext.standby.StandbyControlDialogFragment
import com.agvahealthcare.ventilator_ext.system.SystemDialogFragment
import com.agvahealthcare.ventilator_ext.utility.*
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import com.agvahealthcare.ventilator_ext.utility.utils.CustomMediaPlayer
import com.agvahealthcare.ventilator_ext.utility.utils.IntentFactory
import kotlinx.android.synthetic.main.activity_dashboard.*

import kotlinx.android.synthetic.main.content_female_layout.view.*
import kotlinx.android.synthetic.main.content_male_layout.view.*
import kotlinx.android.synthetic.main.knob_progress_view_red.view.*
import org.jetbrains.anko.ctx

class MainActivity : BaseLockActivity(), OnCalibrationOxygen,
    View.OnClickListener,
    ControlParameterClickListener,
    OnLoudnessAdjustmentListener {
    private val TAG=MainActivity::class.java.simpleName

    private var shutDownProgress: ProgressDialog? = null
    private var ackVisibilities: BooleanArray = BooleanArray(6000)
    private var powerOffConfirmDialog: AlertDialog? = null
    private var heightSize: Int? = null
    private var widthSize: Int? = null

    private var selectedBasicPosition: Int? = null
    private var selectAdvancedPosition: Int? = null
    private var selectedBackupPosition: Int? = null
    private var communicationService: CommunicationService? = null
    private var prefManager: PreferenceManager? = null

    private var basicControlParameterList:List<ControlParameterModel>? = null
    private var backupControlParameterList:List<ControlParameterModel>? = null
    private var advancedControlParameterList:List<ControlParameterModel>? = null
    private var isLocked: Boolean
        get() = prefManager?.readLockScreenStatus() ?: false
        set(status){
            prefManager?.setLockScreenStatus(status)
            ToastFactory.custom(this, "Lock Screen Status $status")
        }
    private var isReadingFromConnection = false
    private var pingingTask: PingingTask? = null

    var isServiceBound = false
    private var progressDialog: KnobDialog? = null
    private var gender: Gender? = null
    private var height: Float? = null
    private var age: Float? = null
    private var weight: Float? = null


    internal var requestedModeCode: Int = 0
    private lateinit var mEventViewModel: EventViewModel
    private var customCountDownTimer: CustomCountDownTimer? = null
    private var modeDialogFragment: ModeDialogFragment? = null
    private var currentButtonID: View? = null

    private var standbyControlFragment: StandbyControlDialogFragment? = null
    private var systemDialogFragment: SystemDialogFragment? = null
    private var mediaPlayer: CustomMediaPlayer? = null
    private var isExistingVentilation: Boolean? = null;


    //variable for the mainactivity viewmodel for scoping the value in the child fragments as well
    private lateinit var mMainActivityViewModel: MainActivityViewModel
    private val settingsCountDownTimer = SettingsCountDownTimer(2500, 700)


    // Service connection for bound services
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, ibinder: IBinder) {
            isServiceBound = true
            communicationService = (ibinder as CommunicationService.LocalBinder).service
            communicationService?.makeLog(MainActivity::class.java.simpleName)
            onDeviceConnect()

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
            isReadingFromConnection = false
        }
    }

    private val modeConfirmationListener = object : OnModeConfirmListener {

        override fun onConfirm(modeCode: Int) {
            progressIndicator.visibility = View.VISIBLE

           normaliseButtons()

            modeDialogFragment?.dismiss()
            highlightButton(buttonControls)

           /*  if (isVentilatorInStandby()) {
                 sendWakeupCommandToVentilator()
                 Log.i("First_Time_Mode_Code", "Send_Code_InStand_by = $modeCode")
             }*/
        }

        override fun onCancel() {

        }

    }

    //this send the mode code to the underlying system
    private val startNewVentilationListener = object : OnStartVentilationListener {
        override fun onStart() {
            Log.d("ackworkmodecode",requestedModeCode.toString())
            communicationService?.send(requestedModeCode.toString())
        }
    }


    private val fragmentDismissListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            //normaliseButtons()
            progressIndicator?.visibility = View.GONE
            normaliseButtons()
            disablePresence()
        }
    }


    private val standbyControlFragmentDismissListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            //normaliseButtons()
            fragmentDismissListener.handleDialogClose()
            prefManager?.readVentilationMode()?.apply { requestedModeCode = this }
        }
    }

    private val onBodyParamsKnobPressListener = object : OnKnobPressListener{
        override fun onKnobPress(previousValue: Float, newValue: Float) {
            when (currentButtonID) {

                includeProgressWeight.progress_bar -> {

                    currentButtonID = includeProgressWeight.progress_bar
                    includeProgressWeight.textView.text = newValue.toDouble().toInt().toString()
                    includeProgressWeight.progress_bar.progress = newValue.toInt()
                    prefManager?.setBodyWeight(newValue.toDouble().toFloat())
                    weight = newValue
                }


                includeProgressAge.progress_bar -> {

                    currentButtonID = includeProgressWeight.progress_bar
                    includeProgressAge.textView.text = newValue.toDouble().toInt().toString()
                    includeProgressAge.progress_bar.progress = newValue.toInt()
                    prefManager?.setAge(newValue.toDouble().toFloat())
                    age = newValue

                }

                includeProgressHeight.progress_bar -> {

                    currentButtonID = includeProgressWeight.progress_bar
                    includeProgressHeight.textView.text = newValue.toDouble().toInt().toString()
                    includeProgressHeight.progress_bar.progress = newValue.toInt()
                    prefManager?.setBodyHeight(newValue.toDouble().toFloat())
                    height = newValue
                }
            }
        }
    }

    private val onBodyParamsLimitChangeListener = fun (view: View): OnLimitChangeListener{
        return object: OnLimitChangeListener{
            override fun onLimitChange(previousValue: Float, newValue: Float) {
                view.let {
                    (it.progress_bar as? ProgressBar)?.apply {
                        this.progress = newValue.toInt()
                    }
                    (it.textView as? TextView)?.apply {
                        this.text = newValue.toInt().toString()
                    }
                }

            }
        }
    }

    private val onBodyParamsCloseListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            normalizeProgressBars()
            initBodyParamsViaPreferences()
        }
    }

    private val onBodyParamsTimeoutListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            progressDialog?.takeIf { it.isVisible }?.dismiss()
            normalizeProgressBars()
            initBodyParamsViaPreferences()
        }
    }


    /*
     * This provides intent filter for the Gatt Data Receiver
     */
    private fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_CONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_DISCONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_KNOB_CHANGE)
        intentFilter.addAction(IntentFactory.ACTION_LOCK)
        intentFilter.addAction(IntentFactory.ACTION_ACK_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_MODE_SET)
        intentFilter.addAction(IntentFactory.ACTION_SENSOR_AVAILABILITY_RESPONSE)
        intentFilter.addAction(IntentFactory.ACTION_SENSOR_CALIBRATION_RESPONSE)
        intentFilter.addAction(IntentFactory.ACTION_BATTERY_STATUS_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_POWER_SWITCH)
        intentFilter.addAction(IntentFactory.ACTION_BATTERY_CONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_BATTERY_DISCONNECTED)
        return intentFilter
    }


    private val connReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.apply {
                when (this) {



                    IntentFactory.ACTION_BATTERY_STATUS_AVAILABLE -> {

                        val batteryLevel = intent.getIntExtra(VENTILATOR_BATTERY_LEVEL, -1)
                        val batteryHealth = intent.getIntExtra(VENTILATOR_BATTERY_HEALTH, -1)
                        val batteryRemainingTime = intent.getIntExtra(VENTILATOR_BATTERY_TTE, -1).let {
                            if (it>30) it
                            else -1
                        }
                        setbatteryLevelImage(batteryLevel)

                        // update battery level on view
                       /* if (batteryLevel in 0..100 && batteryHealth in 0..100 && batteryRemainingTime >= 0) {
                            updateBatteryLevel(batteryLevel, batteryHealth, batteryRemainingTime)
                        }*/
                        var tempBatteryLevel=mMainActivityViewModel.ventBatteryLevel.value
                        var tempBatteryHealth=mMainActivityViewModel.ventBatteryHealth.value
                        var tempBatteryRemainingTime=mMainActivityViewModel.ventBatteryRemainingTime.value
                        if (tempBatteryHealth==null || tempBatteryHealth!=batteryHealth){
                            mMainActivityViewModel.setVentBatteryHealth(batteryHealth)
                        }
                        if (tempBatteryLevel!=null || tempBatteryLevel!=batteryLevel){
                            mMainActivityViewModel.setVentBatteryLevel(batteryLevel)
                        }

                        mMainActivityViewModel.setVentBatteryRemainingTime(batteryRemainingTime)

                    }

                    IntentFactory.ACTION_SENSOR_AVAILABILITY_RESPONSE -> {
                        // requestedModeCode = intent.getIntExtra(SENSOR_ANALYSIS, -1)

                        intent.getStringArrayListExtra(SENSOR_ANALYSIS)?.takeIf { it.isNotEmpty() }
                            ?.apply {
                                Log.i("SENSOR_ANALYSIS_CHECK", this.toString())
                                prefManager?.setSensorInhaleFlow(this[0].toInt())
                                prefManager?.setSensorExhaleFlow(this[1].toInt())
                                prefManager?.setSensorHighO2(this[2].toInt())
                                prefManager?.setSensorLowO2(this[3].toInt())
                                prefManager?.setSensorCO2(this[4].toInt())
                                prefManager?.setSensorSPO2(this[5].toInt())
                                prefManager?.setSensorTemp(this[6].toInt())

                            }

                    }

                    IntentFactory.ACTION_SENSOR_CALIBRATION_RESPONSE -> {
                        val status = intent.getStringExtra(VENTILATOR_SENSOR_CALIBRATION_TAG)
                        val calibResult =
                            intent.getIntExtra(VENTILATOR_SENSOR_CALIBRATION_RESULT, -1)

                        try {
                            status?.apply {
                                if (calibResult == -1) {
                                    Log.i("CALIBCHECK", "Sensor calibration status not available")
                                    return
                                }

                                val calibration = SensorCalibration(calibResult)

                                Log.i(
                                    "CALIBCHECK",
                                    "Sensor = $status calibrated with value = $calibResult"
                                )

                                when (this) {
                                    TAG_SENSOR_FLOW -> {
                                        prefManager?.setFlowCalibration(calibration)
                                    }

                                    TAG_SENSOR_OXYGEN -> {
//                                        prefManager?.setOxygenCalibration(calibration)
                                    }
                                    TAG_SENSOR_PRESSURE -> {
                                        prefManager?.setPresserCalibration(calibration)
                                    }
                                    TAG_SENSOR_TURBINE -> {
                                        prefManager?.setTurbineCalibration(calibration)
                                    }
                                    else -> {
                                        Log.i("CALIBCHECK", "Invalid sensor tag for calibration")
                                      /*  ServerLogger.w(
                                            context,
                                            "Invalid sensor tag for calibration"
                                        )*/
                                    }
                                }
                                Log.i("CALIBCHECK", "out of when")

                                systemDialogFragment?.getTestCalibFragment()
                                    ?.updateSensorCalibrationStatus()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    IntentFactory.ACTION_KNOB_CHANGE -> {
                        val data = intent.getStringExtra(VENTILATOR_CONTROL_KNOB)
                        data?.takeIf { it.isNotEmpty() }?.apply {
                            Log.i("KNOB_DATADASH", this)

                            if (progressDialog?.isVisible == true) {
                                progressDialog?.updateWithTimeoutDebounce(data)
                            }

                            systemDialogFragment?.takeIf { it.isVisible }?.apply {
                                updateKnob(data)
                            }

                            /* modeDialogFragment?.takeIf { it.isVisible }?.apply {
                                 updateKnob(data)
                             }*/
                        }

                    }

                    IntentFactory.ACTION_DEVICE_CONNECTED -> {
                        Log.i("CONN_CHECK", "ventilator connected")
                        onDeviceConnect()
                    }
                    IntentFactory.ACTION_DEVICE_DISCONNECTED -> {
                        Log.i("CONN_CHECK", "ventilator disconnected")
                        onDeviceDisconnect()
                    }
                    //ToDo:-The integration check point
                    IntentFactory.ACTION_ACK_AVAILABLE -> {
                        val ackValue = intent.getStringExtra(VENTILATOR_ACK)
                        Log.i("ACK_CHECK", "ACK received @$ackValue")
                        val runningModeFromIntent = intent.getIntExtra(VENTILATOR_MODES, -1)
                        //code for the action ack 00

                        if (ackValue != null && ackValue.isNotEmpty()
//                            && isAcknowledgementAcceptable(
//                                runningModeFromIntent,
//                                ackValue
//                            )
                        )
                            handleAcknowledgements(ackValue)
                    }

                    IntentFactory.ACTION_MODE_SET -> {
                        requestedModeCode = intent.getIntExtra(VENTILATOR_MODES, -1)
                        Log.i("First_Time_Mode_Code", "Mode_code_SET  $requestedModeCode")


                        val controlParameters = filterControlParameterViaMode(
                            this@MainActivity,
                            requestedModeCode,
                            getAllControlParameterLists(
                                this@MainActivity,
                                requestedModeCode
                            ).flatten()
                        )

                        basicControlParameterList = controlParameters.getOrNull(0)
                        advancedControlParameterList=controlParameters.getOrNull(1)
                        backupControlParameterList = controlParameters.getOrNull(2)



                        Log.d("List", advancedControlParameterList?.get(0)?.reading.toString())
                        standbyControlFragment = basicControlParameterList?.let {
                            StandbyControlDialogFragment.newInstance(
                                heightSize,
                                widthSize,
                                false,
                                it.toMutableList(),
                                advancedControlParameterList?.toMutableList(),
                                backupControlParameterList?.toMutableList(),
                                standbyControlFragmentDismissListener,
                                this@MainActivity,
                                onAdvanceControlParameterClickListener,
                                onBackupControlParameterClickListener,
                                startNewVentilationListener
                            )
                        }
                        progressDialog?.isCancelable = false
                        standbyControlFragment?.isCancelable = false
                        standbyControlFragment?.show(
                            supportFragmentManager,

                            StandbyControlDialogFragment.TAG
                        )


                    }

                    IntentFactory.ACTION_LOCK -> {

                        if (ACCESS_LOCK_AVAILABLE) {
                            hideKnob()
                            hideAllDialogFragment()

                            isLocked = !isLocked
                            ACCESS_LOCK_AVAILABLE = false
                            Log.i("ACTION_HARDWARE_BUTTON", "Lock button is clicked.........")

                            Handler(Looper.getMainLooper()).postDelayed({

                                ACCESS_LOCK_AVAILABLE = true
                            }, 1000)
                        }


                    }


                    IntentFactory.ACTION_POWER_SWITCH-> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@MainActivity,ShutDownActivity::class.java))
                            finish()
                        },3000)

                    }
                    IntentFactory.ACTION_BATTERY_CONNECTED->{
                        mMainActivityViewModel.setBAtteryConnectedFlag(true)
                    }
                    IntentFactory.ACTION_BATTERY_DISCONNECTED->{
                        mMainActivityViewModel.setBAtteryConnectedFlag(false)
                    }
                }
            }
        }
    }

    private fun hideAllDialogFragment() {
        standbyControlFragment?.takeIf { it.isVisible }?.apply {
            dismiss()
        }

        systemDialogFragment?.takeIf { it.isVisible }?.apply {
            dismiss()
        }

        modeDialogFragment?.takeIf { it.isVisible }?.apply {
            dismiss()
        }

    }


    private fun setbatteryLevelImage(batteryLevel: Int){

        if(batteryLevel in 76..100){
            batteryStatus.setImageResource(R.drawable.ic_battery_full)

        }
        else if(batteryLevel in 51..75){
            batteryStatus.setImageResource(R.drawable.ic_threefourth)
        }
        else if (batteryLevel in 26..50){
            batteryStatus.setImageResource(R.drawable.ic_battery_half)
        }
        else if(batteryLevel in 0..25){
            batteryStatus.setImageResource(R.drawable.ic_battery_low)
        }
        else if(batteryLevel <10){
            batteryStatus.setImageResource(R.drawable.ic_battery_full)

        }
    }
    private fun checkForAntiORAck(ack:String) : String{
        val sizeOfString=ack.length
        val processAck=ack.replace("ACK","")
        val processingDigit =processAck.get(processAck.length-2).digitToInt()
        if (processingDigit%2==0 || processingDigit==0 ){
            return "ACK"
        } else {
            return "ANTIACK"
        }
    }
    private fun returnACKOFAntiAck(ack:String) : String{
        val sizeOfString=ack.length
        val processAck=ack.replace("ACK","")
        val processingDigit =processAck.get(processAck.length-2).digitToInt()
        if (processingDigit%2==0 || processingDigit==0 ){
            return "none"
        } else {
            val mainAck="ACK"+processAck.substring(0,processAck.length-2)+processAck[processAck.length-2].minus(1)+processAck[processAck.length-1]
            return mainAck
        }
    }
    private fun insertAndRemoveACKFromArray(ack : String){
        val sizeOfString = ack.length
        val processAck=ack.replace("ACK","").toInt()
        val checkCodeType = checkForAntiORAck(ack)
        if (processAck<=960){
            if (checkCodeType.contains("ANTIACK")){
                val ackOfAnti = returnACKOFAntiAck(ack).replace("ACK","").toInt()
                if(ackVisibilities[ackOfAnti]){
                    ackVisibilities[ackOfAnti] = false
                }
            } else if (checkCodeType.contains("ACK")){
                ackVisibilities[processAck] = true
            }
        }
    }
    private fun handleAcknowledgements(ackValue: String) {
        if (ackValue != null) {
            insertAndRemoveACKFromArray(ackValue)
        }
        when (ackValue) {
            ACK_CODE_5006->{
               // ToastFactory.custom(this@MainActivity,"THe $ACK_CODE_5006 is received")
                if(isExistingVentilation == true) {
                    sendConfigurationToVentilatorWithWatchDog()
                } else {
                    prefManager?.apply {
                        clearProfilePreferences(readCurrentUid())
                        setVentilationMode(requestedModeCode)
                    }

                    // send the configuration list to the ventilator on mode confirmation
                    standbyControlFragment?.apply {
                        val status = getApneaStatus() ?: false
                        getAllControlParameters()?.let{ sendParametersToVentilator(it, status) }
                    }
                }

            }
            //setting saved
            ACK_CODE_5005->{
                progressIndicator.visibility = View.VISIBLE
                settingsCountDownTimer.safeStop()
                // ToastFactory.custom(this@MainActivity,"THe $ACK_CODE_5005 is received")
                standbyControlFragment?.dismiss()
                renderControlParameterTilesViaPreference()  // WHY ?
                startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
                finish()
            }
        }
    }


    private fun onDeviceConnect() {

        // FOR ALLOWING SCREEN AUTO LOCK
        AppUtils.keepScreenAlive(this, true)
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            if (!isReadingFromConnection) {
                isReadingFromConnection = true
                Log.i("CONN_CHECK", "Pinging has started from MainActivity")
                startPinging()
                startReading()
            }
        } ?: kotlin.run {
            ToastFactory.custom(
                this@MainActivity,
                "Unable to start the ventilator, please restart manually."
            )
        }

    }

    private fun onDeviceDisconnect() {
        // FOR ALLOWING SCREEN AUTO LOCK
        AppUtils.keepScreenAlive(this@MainActivity, false)

        if (isReadingFromConnection) {
            communicationService?.takeIf { it.isPortsConnected }?.apply {
                stopPinging()
                stopReading()
            }
            isReadingFromConnection = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        // init preference manager

        prefManager = PreferenceManager(this@MainActivity)

        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        mMainActivityViewModel=ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mMainActivityViewModel.setBAtteryConnectedFlag(false)

        hideSystemUI()
        setContentView(R.layout.activity_main)
        initView()
        setOnClickListener()
        doBindService()
        normaliseButtons()
        disablePresence()


        normalizeProgressBars()  // ToDo : fix the progress view in xml too

        //deleting the data before 7 days to maintain the data
        val dataLogger = DataLogger(this@MainActivity)
        dataLogger.deleteDataFromSqlite()
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(
            AudioManager.STREAM_ALARM,
            am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connReceiver, getIntentFilter())
    }

        private fun disablePresence() {
            listOf<AppCompatButton>(
                buttonControls,
                //buttonNeonatal

            ).forEach {
                it.setBackgroundResource(R.drawable.background_light_grey_disable)
                it.setTextColor(ContextCompat.getColor(this, R.color.disable_grey))
            }
            setPaddingOnButtons()

        }


        private fun isVentilatorInStandby(): Boolean = intent.getBooleanExtra(IS_STAND_BY, false)

        private fun isExistingVentilationModeAvailable(): Boolean {
            val ventMode = prefManager?.readVentilationMode()
            return ventMode != null && isValidVentilatorMode(this@MainActivity, ventMode)

        }


        private fun showShutDownProgress(txt: String) {
            if (shutDownProgress == null) {
                shutDownProgress = ProgressDialog(this)
                shutDownProgress?.setCancelable(false)
            }
            shutDownProgress?.setMessage(txt)
            shutDownProgress?.show()
        }

        private fun showShutDownConfirmation() {
            var dialogDisplayMessage = ""
            if (powerOffConfirmDialog?.isShowing() == true) return;

            if (ackVisibilities[5]) {
                dialogDisplayMessage = this.getString(R.string.patient_disconnected_shutdown)
                powerOffConfirmDialog =
                    DialogBoxFactory.showShutDownStatusDialog(dialogDisplayMessage, this) { ->
                        showShutDownProgress("Shut Down in Progress")
                        val eventDataModel = EventDataModel("ShutDown Request")
                        mEventViewModel.addEvent(eventDataModel)
                        sendShutDownCommandToVentilator()
                    }
            } else {
                dialogDisplayMessage = this.getString(R.string.patient_connected_shutdown)
                powerOffConfirmDialog =
                    DialogBoxFactory.showShutDownStatusDialog(dialogDisplayMessage, this) { ->
                        showShutDownProgress("Shut Down in Progress")
                        val eventDataModel = EventDataModel("ShutDown Request")
                        mEventViewModel.addEvent(eventDataModel)
                        sendShutDownCommandToVentilator()
                    }
            }
        }

        private fun initView() {
            includeProgressHeight.progress_bar.progressDrawable =
                AppCompatResources.getDrawable(this, R.drawable.progresscircle)
            includeProgressWeight.progress_bar.progressDrawable =
                AppCompatResources.getDrawable(this, R.drawable.progresscircle)
            includeProgressAge.progress_bar.progressDrawable =
                AppCompatResources.getDrawable(this, R.drawable.progresscircle)



            initViewViaPreferences()

            Log.i("STAND_BY_STATUS", "Status" + isVentilatorInStandby())

            if (isVentilatorInStandby()) {
                textStandBy.visibility = View.VISIBLE
                textTimer.visibility = View.VISIBLE
                // buttonPowerOff.visibility = View.VISIBLE
                //textNoVentilation.visibility = View.VISIBLE
                textNoVentDelivered.visibility = View.VISIBLE
                layoutPanelMode.visibility = View.VISIBLE
            } else {
                //layoutPanelMode.visibility = if (isExistingVentilationModeAvailable()) View.VISIBLE else View.INVISIBLE
                layoutPanelMode.visibility = View.VISIBLE
                //buttonPowerOff.visibility = View.GONE
                textStandBy.visibility = View.VISIBLE
                textNoVentDelivered.visibility = View.VISIBLE
                textTimer.visibility = View.VISIBLE
            }


            /*buttonNeonatal.isEnabled = false
        buttonNeonatal.isClickable = false*/

            includeProgressHeight.textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            includeProgressWeight.textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            includeProgressAge.textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )

            )

            initViewStandByTime()

        }

        private fun initViewStandByTime() {
//        if (isVentilatorInStandby()) startTime()
//        else
            startTime()

        }

        private fun startTime() {

            val liveData: MutableLiveData<String> = MutableLiveData()
            customCountDownTimer = CustomCountDownTimer(liveData)
            customCountDownTimer?.start(100) //Epoch timestamp
            customCountDownTimer?.mutableLiveData?.observe(this, Observer { counterState ->
                counterState?.let {
                    textTimer.text = counterState
                }
            })
        }


        private fun initViewViaPreferences() {

            prefManager?.apply {

                readCurrentUid()?.let {
                    when (it) {
                        PatientProfile.TYPE_ADULT -> {
                            highlightProfiles(buttonAdult)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                PATIENT_ADULT_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = PATIENT_ADULT_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                PATIENT_AGE_LOWER
                            includeProgressAge.progress_bar.max = PATIENT_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                PATIENT_ADULT_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = PATIENT_ADULT_WEIGHT_UPPER

                        }
                        PatientProfile.TYPE_PED -> {
                            highlightProfiles(buttonPediatric)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                PED_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = PED_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                PATIENT_AGE_LOWER
                            includeProgressAge.progress_bar.max = PATIENT_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                PED_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = PED_WEIGHT_UPPER

                        }

                        PatientProfile.TYPE_NEONAT -> {
                            highlightProfiles(buttonNeonatal)


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                NEO_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = NEO_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                PATIENT_AGE_LOWER
                            includeProgressAge.progress_bar.max = PATIENT_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                NEO_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = NEO_WEIGHT_UPPER

                        }

                    }
                }

                initBodyParamsViaPreferences()

                if (Gender.TYPE_MALE == readGender()) setDataMale()
                else setDataFemale()




                if (isExistingVentilationModeAvailable()) {

                    setExistingVentilationMode(readVentilationMode())

                }
                checkMode.visibility =
                    if (isExistingVentilationModeAvailable()) View.VISIBLE else View.INVISIBLE
                existinglabel.visibility =
                    if (isExistingVentilationModeAvailable()) View.VISIBLE else View.INVISIBLE
                buttonStartExistingVentilation.visibility =

                    if (isExistingVentilationModeAvailable()) View.VISIBLE else View.INVISIBLE
                layoutPanelMode.visibility =
                    if (isExistingVentilationModeAvailable()) View.VISIBLE else View.INVISIBLE

            }

        }

        private fun initBodyParamsViaPreferences() {

            prefManager?.apply {
                readBodyHeight()?.toDouble()?.toInt()?.let {
                    includeProgressHeight.progress_bar.progress = it
                    includeProgressHeight.textView.text = it.toString()
                }
                readAge()?.toDouble()?.toInt()?.let {
                    includeProgressAge.progress_bar.progress = it
                    includeProgressAge.textView.text = it.toString()
                }
                readBodyWeight()?.toDouble()?.toInt()?.let {
                    includeProgressWeight.progress_bar.progress = it
                    includeProgressWeight.textView.text = it.toString()
                }
            }

        }

        // ClickListener on Buttons
        private fun setOnClickListener() {

            /*buttonAdultPed.isClickable=false
        buttonLastPatient.isClickable=false*/

            prefManager?.readVentilationMode()?.apply {
                buttonModes.isEnabled = Configs.isValidVentilatorMode(this@MainActivity, this)

            }

            buttonControls.isClickable = false
            buttonAdult.isClickable = true
            buttonPediatric.isClickable = true
           // buttonNeonatal.isEnabled = false
            includeProgressHeight.setOnClickListener(this)
            includeProgressWeight.setOnClickListener(this)
            includeProgressAge.setOnClickListener(this)
            includeMale?.buttonMale?.setOnClickListener(this)
            includeFemale?.buttonFemale?.setOnClickListener(this)
            buttonModes.setOnClickListener(this)
            buttonControls.setOnClickListener(this)
            buttonPreopCheck.setOnClickListener(this)
            buttonAdult.setOnClickListener(this)
            buttonPediatric.setOnClickListener(this)
            buttonNeonatal.setOnClickListener(this)
            buttonStartExistingVentilation.setOnClickListener(this)
            buttonStartNewVentilation.setOnClickListener(this)
       //     imageViewPowerMainActivity.setOnClickListener(this)
            //buttonPowerOff.setOnClickListener(this)

        }


        private fun setPaddingOnButtons() {

            buttonModes.setPadding(0, 25, 0, 25)
            buttonControls.setPadding(0, 25, 0, 25)
            buttonPreopCheck.setPadding(0, 25, 0, 25)
            buttonNeonatal.setPadding(0, 25, 0, 25)
            buttonAdult.setPadding(0, 25, 0, 25)
            buttonPediatric.setPadding(0, 25, 0, 25)

            // put all this in xml
            buttonStartExistingVentilation.setPadding(98, 25, 98, 25)
            buttonStartNewVentilation.setPadding(120, 25, 120, 25)
            /*  buttonPreOpCheck.setPadding(180, 25, 180, 25)*/

        }

        private fun normalizeProgressBars() {
            listOf<View>(
                includeProgressHeight,
                includeProgressWeight,
                includeProgressAge
            ).forEach {
                ContextCompat.getDrawable(this, R.drawable.progresscircle)
                // (it.progress_bar as? ProgressBar)?.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progresscircle)

                it.textView.setTextColor(Color.WHITE)
            }
        }

        private fun highlightProgressBar(view: View) {
            normalizeProgressBars()
            view.let {
                //ContextCompat.getDrawable(this,R.drawable.progresscircle_with_selection)
                (it.progress_bar as? ProgressBar)?.progressDrawable =
                    ContextCompat.getDrawable(this, R.drawable.progresscircle_with_selection)
                //.also { (it.progress_bar as? ProgressBar)?.progressDrawable = it }
                //   ctx.getDrawable(R.drawable.progresscircle_with_selection)
                (it.textView as? TextView)?.setTextColor(Color.WHITE)

            }

        }

        private fun normalizeProfiles() {

            listOf<AppCompatButton>(
                buttonAdult,
                buttonPediatric,
                buttonNeonatal
            ).forEach {
                it.setBackgroundResource(R.drawable.background_medium_grey)
                it.setTextColor(ContextCompat.getColor(this, R.color.black))
            }

            setPaddingOnButtons()
        }

        private fun highlightProfiles(btn_profile: AppCompatButton) {
            normalizeProfiles()
            btn_profile.apply {
                setBackgroundResource(R.drawable.background_green_border)
                setTextColor(ContextCompat.getColor(ctx, R.color.white))
                setPaddingOnButtons()
            }
        }

        private fun normaliseButtons() {
            listOf<AppCompatButton>(
               // buttonModes,
               // buttonControls,
                buttonStartExistingVentilation,
                buttonStartNewVentilation,
                buttonPreopCheck
            ).forEach {
                it.setBackgroundResource(R.drawable.background_medium_grey)
                it.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            if(isExistingVentilationModeAvailable() == false) {
                buttonModes.apply {
                    setBackgroundResource(R.drawable.background_light_grey_disable)
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.disable_grey))

                }
            }
            else {
                buttonModes.apply {
                    setBackgroundResource(R.drawable.background_medium_grey)
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))

                }

            }
            setPaddingOnButtons()
        }


        private fun setDataMale() {
            includeMale.imageViewMale.setImageResource(R.drawable.ic_male_select)
            includeMale.buttonMale.setBackgroundResource(R.drawable.background_green_border)
            includeMale.buttonMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            includeFemale.imageViewFemale.setImageResource(R.drawable.ic_female_unselect)
            includeFemale.buttonFemale.setBackgroundResource(R.drawable.background_medium_grey)
            includeFemale.buttonFemale.setTextColor(ContextCompat.getColor(this, R.color.black))
            prefManager?.setGender(Gender.TYPE_MALE)
            gender = Gender.TYPE_MALE
        }

        private fun setDataFemale() {
            includeMale.imageViewMale.setImageResource(R.drawable.ic_male_unselect)
            includeMale.buttonMale.setBackgroundResource(R.drawable.background_medium_grey)
            includeMale.buttonMale.setTextColor(ContextCompat.getColor(this, R.color.black))
            includeFemale.imageViewFemale.setImageResource(R.drawable.ic_female_select)
            includeFemale.buttonFemale.setBackgroundResource(R.drawable.background_green_border)
            includeFemale.buttonFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            prefManager?.setGender(Gender.TYPE_FEMALE)
            gender = Gender.TYPE_FEMALE
        }


        override fun onWindowFocusChanged(hasFocus: Boolean) {
            super.onWindowFocusChanged(hasFocus)

            heightSize = layoutPanelMiddle.height
            widthSize = layoutPanelMiddle.width
            hideSystemUI()

        }

    override fun onStop() {
        unregisterReceiver(connReceiver)
        super.onStop()
    }

        override fun onDestroy() {
            progressIndicator.visibility = View.GONE
            settingsCountDownTimer.safeStop()
            customCountDownTimer?.stop()
            stopPinging()
            doUnbindService()
            super.onDestroy()
        }

        /*
     * This method binds the required services
     * and set the flags to active
     */
        private fun doBindService() {
            if (!isServiceBound) {
                val serviceIntent = Intent(this, UsbService::class.java)
                bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE)
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
                    //ServerLogger.e(this@MainActivity, e)


                }
            }
        }

        override fun onCalibration() {

        }

    private fun sendConfigurationToVentilatorWithWatchDog() {

            communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
                sendConfigurationToVentilator()
                settingsCountDownTimer.startRunning()
            }

    }

    private fun sendControlModeToVentilatorAfterStandby() {

            communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
                send(resources.getString(R.string.cmd_vent_wakeup))
                mEventViewModel.addEvent(EventDataModel("WakeUp from standby"))

                Handler(Looper.getMainLooper()).postDelayed({
                    prefManager?.apply {
                        readVentilationMode().let { sendControlModeToVentilator(it) }
                    }
                }, 500)

                // set fio2 to normal state
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Dismiss progress bar after 2 seconds
//                sendConfigurationToVentilator()
//            }, 700)

                // start pinging
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Dismiss progress bar after 3 seconds
//                startPinging()
//            }, 1000)

//            Handler(Looper.getMainLooper()).postDelayed({
//                // Dismiss progress bar after 3 seconds
//                val intent = Intent(this, DashBoardActivity::class.java)
//                startActivity(intent)
//                finish()
//              //  progressIndicator.visibility=View.GONE
//
//
//                mEventViewModel.addEvent(EventDataModel("WakeUp from standby"))
//            }, 1000)

            }

        }


    fun sendControlModeToVentilator(mode: Int) {
        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            mode.takeIf { isValidVentilatorMode(this@MainActivity, it) }?.let {
                send(it.toString())
                requestedModeCode = it
            }

        }
    }


        /*
     * This methods start pinging to ventilator
     */
        private fun startPinging() {
            communicationService?.apply {
                if (pingingTask == null) pingingTask = PingingTask(this)
                if (pingingTask?.isRunning == false) pingingTask?.start()
            }

        }

        /*
    * This methods stop pinging to ventilator
    */
        private fun stopPinging() {
            pingingTask?.apply {
                if (isRunning) stop()
            }
        }

        private fun setExistingVentilationMode(readVentilationMode: Int) {

            when (readVentilationMode) {
                MODE_VCV_CMV -> {
                    textModeType.text = getString(R.string.hint_vc_cmv)
                    checkMode.text = getString(R.string.hint_vc_cmv)

                }

                MODE_VCV_SIMV -> {
                    textModeType.text = getString(R.string.hint_vc_simv)
                    checkMode.text = getString(R.string.hint_vc_simv)

                }

                MODE_VCV_ACV -> {

                    textModeType.text = getString(R.string.hint_vc_cv)
                    checkMode.text = getString(R.string.hint_vc_cv)
                }

                MODE_VCV_PRVC -> {
                    textModeType.text = getString(R.string.hint_prvc)
                    textModeType.text = getString(R.string.hint_prvc)
                }

                MODE_PC_CMV -> {
                    textModeType.text = getString(R.string.hint_pc_cmv)
                    checkMode.text = getString(R.string.hint_pc_cmv)
                }

                MODE_PC_SIMV -> {
                    textModeType.text = getString(R.string.hint_pc_imv)
                    checkMode.text = getString(R.string.hint_pc_imv)
                }

                MODE_PC_SPONTANEOUS -> {
                    textModeType.text = getString(R.string.hint_spont)
                    checkMode.text = getString(R.string.hint_spont)
                }

                MODE_PC_PSV -> {
                    textModeType.text = getString(R.string.hint_psv)
                    checkMode.text = getString(R.string.hint_psv)
                }

                MODE_AUTO_VENTILATION -> {
                    textModeType.text = getString(R.string.hint_ai_vent)
                    checkMode.text = getString(R.string.hint_ai_vent)
                }
                MODE_NIV_BPAP -> {
                    textModeType.text = getString(R.string.hint_bpap)
                    checkMode.text = getString(R.string.hint_bpap)
                }
                MODE_NIV_CPAP -> {
                    textModeType.text = getString(R.string.hint_cpap)
                    checkMode.text = getString(R.string.hint_cpap)
                }
            }
        }

        private fun updateBatteryLevel(
            batteryLevel: Int,
            batteryHealth: Int,
            batteryRemainingTime: Int
        ) {
            //change delay to 100 from 1000
            systemDialogFragment?.takeIf { it.isVisible }?.apply {
                Handler(Looper.getMainLooper()).postDelayed({
                    //setBatteryLevelUpdate(batteryLevel)
                    //setBatteryHealthUpdate(batteryHealth)
                    //setBatteryTTEUpdate(batteryRemainingTime)
                }, 100)
            }
            Log.i(
                "battery",
                "LEVEL = $batteryLevel %\nHEALTH = $batteryHealth %\nREMAINING TIME = $batteryRemainingTime mins"
            )
        }

        override fun onClick(clickedView: View?) {
            clickedView?.also { view ->
                when (view) {

                    includeProgressHeight -> {

                        currentButtonID = includeProgressHeight.progress_bar

                        var encoder: EncoderValue? = null
                        var param: KnobParameterModel? = null

                        prefManager?.readCurrentUid()?.apply {
                            encoder = when (this) {
                                PatientProfile.TYPE_ADULT -> {
                                    EncoderValue(
                                        PATIENT_ADULT_HEIGHT_LOWER.toFloat(),
                                        PATIENT_ADULT_HEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_PED -> {
                                    EncoderValue(
                                        PED_HEIGHT_LOWER.toFloat(),
                                        PED_HEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_NEONAT -> {
                                    EncoderValue(
                                        NEO_HEIGHT_LOWER.toFloat(),
                                        NEO_HEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                            }

                            param = prefManager?.readBodyHeight()?.let { it1 ->
                                KnobParameterModel(
                                    Configs.LBL_HEIGHT_KEY,
                                    Configs.LBL_HEIGHT_KEY,
                                    1,
                                    it1,
                                    ""
                                )
                            }
                        }

                        // highlightProgressBar(view)

                        showKnobForBodyParams(view, param, encoder)

                    }

                    includeProgressWeight -> {

                        currentButtonID = includeProgressWeight.progress_bar

                        var encoder: EncoderValue? = null
                        var param: KnobParameterModel? = null

                        prefManager?.readCurrentUid()?.apply {
                            encoder = when (this) {
                                PatientProfile.TYPE_ADULT -> {
                                    EncoderValue(
                                        PATIENT_ADULT_WEIGHT_LOWER.toFloat(),
                                        PATIENT_ADULT_WEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_PED -> {
                                    EncoderValue(
                                        PED_WEIGHT_LOWER.toFloat(),
                                        PED_WEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_NEONAT -> {
                                    EncoderValue(
                                        NEO_WEIGHT_LOWER.toFloat(),
                                        NEO_WEIGHT_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                            }

                            param = prefManager?.readBodyWeight()?.let { it1 ->
                                KnobParameterModel(
                                    Configs.LBL_WEIGHT_KEY,
                                    Configs.LBL_WEIGHT_KEY,
                                    1,
                                    it1,
                                    ""
                                )
                            }


                        }

                        //  highlightProgressBar(view)

                        showKnobForBodyParams(view, param, encoder)


                    }

                    includeProgressAge -> {
                        currentButtonID = includeProgressAge.progress_bar

                        var encoder: EncoderValue? = null
                        var param: KnobParameterModel? = null

                        prefManager?.readCurrentUid()?.apply {
                            encoder = when (this) {
                                PatientProfile.TYPE_ADULT -> {
                                    EncoderValue(
                                        PATIENT_AGE_LOWER.toFloat(),
                                        PATIENT_AGE_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_PED -> {
                                    EncoderValue(
                                        PED_AGE_LOWER.toFloat(),
                                        PED_AGE_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                                PatientProfile.TYPE_NEONAT -> {
                                    EncoderValue(
                                        NEO_AGE_LOWER.toFloat(),
                                        NEO_AGE_UPPER.toFloat(),
                                        1.0f
                                    )
                                }
                            }

                            param = prefManager?.readAge()?.let { it1 ->
                                KnobParameterModel(
                                    Configs.LBL_AGE_KEY,
                                    Configs.LBL_AGE_KEY,
                                    1,
                                    it1,
                                    ""
                                )
                            }

                        }

                        //  highlightProgressBar(view)

                        showKnobForBodyParams(view, param, encoder)

                    }


                    includeMale?.buttonMale -> {
                        setDataMale()

                    }

                    includeFemale?.buttonFemale -> {
                        setDataFemale()

                    }


                    buttonPreopCheck -> {
                        highlightButton(buttonPreopCheck)
                        communicationService?.takeIf { it.isPortsConnected && isServiceBound }
                            ?.apply {
                                send(resources.getString(R.string.cmd_preop))
                            }
                        systemDialogFragment = SystemDialogFragment.newInstance(
                            heightSize,
                            widthSize,
                            false,
                            fragmentDismissListener,
                            this,
                            this,
                            communicationService
                        ).apply { show(supportFragmentManager, TAG) }

                        systemDialogFragment?.isCancelable = false

                    }

                    buttonNeonatal -> {
                        highlightProfiles(buttonNeonatal)
                        val textV: TextView = findViewById(R.id.age) as TextView
                        textV.text = "Days"
                        prefManager?.apply {
                            setCurrentUid(PatientProfile.TYPE_NEONAT)

                            readBodyHeight()?.toDouble()?.toInt()?.let {
                                includeProgressHeight.progress_bar.progress = it
                                includeProgressHeight.textView.text = it.toString()
                            }
                            readAge()?.toDouble()?.toInt()?.let {
                                includeProgressAge.progress_bar.progress = it
                                includeProgressAge.textView.text = it.toString()
                            }
                            readBodyWeight()?.toDouble()?.toInt()?.let {
                                includeProgressWeight.progress_bar.progress = it
                                includeProgressWeight.textView.text = it.toString()
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                NEO_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = NEO_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                NEO_AGE_LOWER
                            includeProgressAge.progress_bar.max = NEO_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                NEO_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = NEO_WEIGHT_UPPER

                        }


                    }

                    buttonPediatric -> {
                        highlightProfiles(buttonPediatric)
                        val textV: TextView = findViewById(R.id.age) as TextView
                        textV.setText("Years")
                        prefManager?.apply {

                            setCurrentUid(PatientProfile.TYPE_PED)

                            readBodyHeight()?.toDouble()?.toInt()?.let {
                                includeProgressHeight.progress_bar.progress = it
                                includeProgressHeight.textView.text = it.toString()
                            }
                            readAge()?.toDouble()?.toInt()?.let {
                                includeProgressAge.progress_bar.progress = it
                                includeProgressAge.textView.text = it.toString()
                            }
                            readBodyWeight()?.toDouble()?.toInt()?.let {
                                includeProgressWeight.progress_bar.progress = it
                                includeProgressWeight.textView.text = it.toString()
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                PED_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = PED_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                PED_AGE_LOWER
                            includeProgressAge.progress_bar.max = PED_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                PED_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = PED_WEIGHT_UPPER

                        }

                    }

                    buttonAdult -> {
                        val a = listOf<Int>(2, 24, 24)
                        //val x = a[4]

                        highlightProfiles(buttonAdult)

                        val textV: TextView = findViewById(R.id.age) as TextView
                        textV.text = "Years"
                        prefManager?.apply {
                            setCurrentUid(PatientProfile.TYPE_ADULT)

                            readBodyHeight()?.toDouble()?.toInt()?.let {
                                includeProgressHeight.progress_bar.progress = it
                                includeProgressHeight.textView.text = it.toString()
                            }
                            readAge()?.toDouble()?.toInt()?.let {
                                includeProgressAge.progress_bar.progress = it
                                includeProgressAge.textView.text = it.toString()
                            }
                            readBodyWeight()?.toDouble()?.toInt()?.let {
                                includeProgressWeight.progress_bar.progress = it
                                includeProgressWeight.textView.text = it.toString()
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressHeight.progress_bar.min =
                                PATIENT_ADULT_HEIGHT_LOWER
                            includeProgressHeight.progress_bar.max = PATIENT_ADULT_HEIGHT_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressAge.progress_bar.min =
                                PATIENT_AGE_LOWER
                            includeProgressAge.progress_bar.max = PATIENT_AGE_UPPER

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) includeProgressWeight.progress_bar.min =
                                PATIENT_ADULT_WEIGHT_LOWER
                            includeProgressWeight.progress_bar.max = PATIENT_ADULT_WEIGHT_UPPER

                        }

                    }


                    buttonStartExistingVentilation -> {
                        isExistingVentilation = true
                        progressIndicator.visibility = View.VISIBLE
                        Log.i("CheckModem", "Checking mode flow")

                        prefManager?.readVentilationMode()?.apply {
                            sendControlModeToVentilator(this)
                        }

                    }


                    buttonModes -> {

                        highlightButton(buttonModes)

                        modeDialogFragment = ModeDialogFragment.newInstance(
                            heightSize,
                            widthSize,
                            false,
                            closeListener = fragmentDismissListener,
                            onModeConfirmListener = modeConfirmationListener
                        )

                        modeDialogFragment?.show(supportFragmentManager, ModeDialogFragment.TAG)

                        modeDialogFragment?.isCancelable = false


                    }


                    buttonStartNewVentilation -> {
                        isExistingVentilation = false
                        highlightButton(buttonModes)
                        if (isVentilatorInStandby()) {
                            communicationService?.takeIf { it.isPortsConnected && isServiceBound }
                                ?.apply {
                                    send(resources.getString(R.string.cmd_vent_wakeup))
                                }
                        }

                        modeDialogFragment = ModeDialogFragment.newInstance(
                            heightSize,
                            widthSize,
                            false,
                            closeListener = fragmentDismissListener,
                            onModeConfirmListener = modeConfirmationListener
                        )

                        modeDialogFragment?.show(supportFragmentManager, ModeDialogFragment.TAG)
                        modeDialogFragment?.isCancelable = false


                    }
//                    imageViewPowerMainActivity -> {
//                        showShutDownConfirmation()
//                    }
                    /*buttonPowerOff->{
                    if(isVentilatorInStandby())
                    //sendShutDownCommandToVentilator()
                    showShutDownConfirmation()
                }*/

                }


            }
        }

        private fun sendShutDownCommandToVentilator() {
            communicationService?.send(getString(R.string.cmd_vent_shutdown))
            sendBroadcast(Intent(IntentFactory.ACTION_POWER_SWITCH))
        }

    /*
   * Customized countdown timer for Control settings
   */
    open inner class SettingsCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        private var isSafeStop = false
        private var isRunning = false
        private var isFirstCallElapsed = false
        fun startRunning() {
            if (!isRunning) {
                isSafeStop = false
                isRunning = true
                start()
            }
        }

        fun safeStop() {
            isSafeStop = true
            if (isRunning) cancel()
        }

        override fun onTick(millis: Long) {

            communicationService?.takeIf { it.isPortsConnected }?.apply {
                sendConfigurationToVentilator()
            }
        }

        override fun onFinish() {
            if (isFirstCallElapsed) {
                if (!isSafeStop) {
//                    prefManager?.apply {
//                        if (readOxygenHoldStatus()) {
//                            setOxygenHoldStatus(false)
//                        }
//                    }
                    ToastFactory.setSnackBar(
                        mainViewPanel,
                        getString(R.string.hint_setting_failure)
                    )


                }
            } else isFirstCallElapsed = true
            isRunning = false
        }
    }


    private fun hideKnob() {
        progressDialog?.dismiss()
        progressDialog = null

    }

    private fun showKnob(param: KnobParameterModel?, encoder: EncoderValue?, ) {


            if (param == null || encoder == null) return

            hideKnob()

            progressDialog = KnobDialog.newInstance(
                onKnobPressListener = object : OnKnobPressListener {

                    override fun onKnobPress(previousValue: Float, newValue: Float) {
                        basicControlParameterList?.let {
                            selectedBasicPosition?.apply {
                                var lbl = it[this].ventKey
                                val unit = it[this].units

                                updateParameter(lbl, newValue.toString())

                                val eventDataModel = EventDataModel(
                                    "Set $lbl from ${
                                        String.format(
                                            "%.1f",
                                            previousValue
                                        )
                                    }  $unit to $newValue $unit"
                                )
                                mEventViewModel.addEvent(eventDataModel)

                                selectedBasicPosition = null

                            }
                        }

                        advancedControlParameterList?.let {
                            selectAdvancedPosition?.apply {
                                var lbl = it[this].ventKey
                                val unit = it[this].units

                                updateParameter(lbl, newValue.toString())


                                val eventDataModel = EventDataModel(
                                    "Set $lbl from ${
                                        String.format(
                                            "%.1f",
                                            previousValue
                                        )
                                    }  $unit to $newValue $unit"
                                )
                                mEventViewModel.addEvent(eventDataModel)

                                selectAdvancedPosition = null
                            }
                        }
                        backupControlParameterList?.let {
                            selectedBackupPosition?.apply {
                                val lbl = it[this].ventKey
                                val unit = it[this].units

                                updateParameter(lbl, newValue.toString())

                                val eventDataModel = EventDataModel(
                                    "Set $lbl from ${
                                        String.format(
                                            "%.1f",
                                            previousValue
                                        )
                                    }  $unit to $newValue $unit"
                                )
                                mEventViewModel.addEvent(eventDataModel)

                                selectedBackupPosition = null

                            }
                        }

                        normaliseParameterTiles()


                    }

                },
                onLimitChangeListener = object : OnLimitChangeListener {
                    override fun onLimitChange(previousValue: Float, newValue: Float) {
                        selectedBasicPosition?.let { pos ->
                            basicControlParameterList?.getOrNull(pos)?.apply {
                                updateParameter(ventKey, newValue.toString())

                            }

                        }

                        selectAdvancedPosition?.let { pos ->
                            advancedControlParameterList?.getOrNull(pos)?.apply {
                                updateParameter(ventKey, newValue.toString())
                            }
                        }
                        selectedBackupPosition?.let { pos ->
                            backupControlParameterList?.getOrNull(pos)?.apply {
                                updateParameter(ventKey, newValue.toString())
                            }

                        }
                    }
                },
                onCloseListener = object : OnDismissDialogListener {
                    override fun handleDialogClose() {
                        normaliseParameterTiles()
                        normaliseButtons()
                        disablePresence()
                        renderControlParameterTilesViaPreference()
                    }
                },
                onTimeoutListener = object : OnDismissDialogListener {
                    override fun handleDialogClose() {
                        progressDialog?.takeIf { it.isVisible }?.dismiss()
                        normaliseParameterTiles()
                        renderControlParameterTilesViaPreference()
                    }
                },
                parameterModel = param,
                encoderValue = encoder,
                cancelableStatus = true
            )

            progressDialog?.apply {
                show(supportFragmentManager, "CONTROL_PROGRESS")
                startTimeoutWithDebounce()
            }
        }


    private fun showKnobForBodyParams(

        view: View,
        param: KnobParameterModel?,
        encoder: EncoderValue?,

        ) {
        if (param != null && encoder != null) {

            hideKnob()

            progressDialog = KnobDialog.newInstance(
                onKnobPressListener = onBodyParamsKnobPressListener,
                onLimitChangeListener = onBodyParamsLimitChangeListener(view),
                onCloseListener = onBodyParamsCloseListener,
                onTimeoutListener = onBodyParamsTimeoutListener,
                parameterModel = param,
                encoderValue = encoder,
                cancelableStatus = true  // cancellable,
            )
            progressDialog?.apply {
                show(supportFragmentManager, "BODY_PARAM_PROGRESS")
                startTimeoutWithDebounce()
            }
        }
    }

        //invoked on knob value change
        private fun updateParameter(key: String, value: String) {

            val isDecimalSupported = Configs.supportPrecision(key,value)


            basicControlParameterList
                ?.filter { it.ventKey == key }
                ?.takeIf { it.isNotEmpty() }
                ?.getOrNull(0)
                ?.apply {
                   this.reading = supportPrecision(key,value)
                }
            advancedControlParameterList
                ?.filter { it.ventKey == key }
                ?.takeIf { it.isNotEmpty() }
                ?.getOrNull(0)
                ?.apply {
                    this.reading = supportPrecision(key,value)
                }

            backupControlParameterList
                ?.filter { it.ventKey == key }
                ?.takeIf { it.isNotEmpty() }
                ?.getOrNull(0)
                ?.apply {
                    this.reading = supportPrecision(key,value)
                }


            standbyControlFragment?.notifyParameterAdapter()
        }

        private fun renderControlParameterTilesViaPreference() {
            prefManager?.apply {
                updateParameter(LBL_PEEP, readPEEP().toInt().toString())
                updateParameter(LBL_TRIG_FLOW, readTrigFlow().toInt().toString())
                updateParameter(LBL_PPLAT, readPplat().toInt().toString())
                updateParameter(LBL_VTI, readVti().toInt().toString())
                updateParameter(LBL_PIP, readPip().toInt().toString())
                updateParameter(LBL_RR, readRR().toInt().toString())
                updateParameter(LBL_TINSP, readTinsp().toString())
                updateParameter(LBL_FIO2, readFiO2().toInt().toString())
                updateParameter(LBL_SUPPORT_PRESSURE, readSupportPressure().toInt().toString())
                updateParameter(LBL_SLOPE, readSlope().toInt().toString())
                updateParameter(LBL_TLOW, readTlow().toString())
                updateParameter(LBL_TEXP, readTexp().toInt().toString())
                updateParameter(LBL_APNEA_RR, readRRApnea().toInt().toString())
                updateParameter(LBL_APNEA_VT, readVtApnea().toInt().toString())
                updateParameter(LBL_TAPNEA, readTApnea().toString())
                updateParameter(LBL_APNEA_TRIG_FLOW, readTrigFlowApnea().toInt().toString())
            }
        }


        private fun highlightButton(btn: AppCompatButton) {
            normaliseButtons()
            btn.apply {
                setBackgroundResource(R.drawable.background_green_border)
                setTextColor(ContextCompat.getColor(ctx, R.color.white))
                setPaddingOnButtons()
            }
        }

        override fun onAdjustLoudness(volumeLevel: Float) {
            Log.i("LOUDCHECK", "Adjusted loudness to $volumeLevel")
            mediaPlayer?.apply {
                setVolume(
                    volumeLevel / VOLUME_MAX_VALUE,
                    volumeLevel / VOLUME_MAX_VALUE
                )
            }
        }

        override fun onCheckLoudness() {
            mediaPlayer?.takeIf { it.isRunning }?.apply {
                stop()
                release()
            }
            mediaPlayer = null
            mediaPlayer = CustomMediaPlayer()

            mediaPlayer?.apply {
                setDataSource(this@MainActivity, Configs.URI_ALARM_HIGH_LEVEL)
                val attrib: AudioAttributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .build()
                setAudioAttributes(attrib)
                setAudioAttributes(attrib)
                prepare()
                isLooping = true
                prefManager?.let {
                    Log.i(
                        "LOUDCHECK",
                        "Volume level from pref = " + it.readVolume() / VOLUME_MAX_VALUE
                    )
                    setVolume(
                        it.readVolume() / VOLUME_MAX_VALUE,
                        it.readVolume() / VOLUME_MAX_VALUE
                    )
                    start()
                }
            }


            object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    mediaPlayer?.release()
                    mediaPlayer = null;
                }
            }.start()


        }


        private fun sendParametersToVentilator(
            parameters: List<ControlParameterModel>,
            apneaStatus: Boolean
        ) {

            parameters.forEach {
                try {
                    Log.i(
                        "ONSTARTVENTILATION",
                        "${it.ventKey} updated with value = ${it.reading}"
                    )
                    prefManager?.updateParameterViaName(
                        it.ventKey,
                        it.reading.toFloat()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i(
                        "ONSTARTVENTILATION",
                        "Unable to parse some parameters to Float"
                    )
                }
            }
            prefManager?.setApneaSettingsStatus(apneaStatus)
            sendConfigurationToVentilatorWithWatchDog()
        }


        private fun highlightParameterTile(params: List<ControlParameterModel>, at: Int) {
            if (at < params.size) {
                normaliseParameterTiles()
                params.get(at).isIsselected = true

            }

        }

    //This method is being invoked by knob for control parameters BASIC
    val onAdvanceControlParameterClickListener=object :ControlParameterClickListener{
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked){
                advancedControlParameterList?.let {
                    selectAdvancedPosition = position
                    val knobModel=KnobParameterModel.fromControlParameter(model)
                    highlightParameterTile(it,position)
                    val encoderValue=EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )
                    showKnob(knobModel, encoderValue)

                }
                standbyControlFragment?.notifyParameterAdapter()
            }
        }

        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {}
    }

    val onBackupControlParameterClickListener=object :ControlParameterClickListener{
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked){
                backupControlParameterList?.let {
                    selectedBackupPosition = position
                    val knobModel=KnobParameterModel.fromControlParameter(model)
                    highlightParameterTile(it,position)
                    val encoderValue=EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )
                    showKnob(knobModel, encoderValue)

                }
                standbyControlFragment?.notifyParameterAdapter()
            }
        }
        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {}

    }

        private fun normaliseParameterTiles() {
            basicControlParameterList?.forEach {
                it.isIsselected = false
            }

            backupControlParameterList?.forEach {
                it.isIsselected = false
            }
            advancedControlParameterList?.forEach {
                it.isIsselected = false
            }
            standbyControlFragment?.notifyParameterAdapter()
        }

    //This method is being invoked by knob for body parameters only
    override fun onClick(position: Int, model: ControlParameterModel) {

        if (isLocked){
            basicControlParameterList?.let {
                selectedBasicPosition = position
                val knobModel=KnobParameterModel.fromControlParameter(model)
                highlightParameterTile(it,position)
                val encoderValue=EncoderValue(
                    model.lowerLimit.toFloat(),
                    model.upperLimit.toFloat(),
                    model.step.toFloat()
                )

                showKnob(knobModel, encoderValue)

                //showKnobViews(knobModel,encoderValue)
                //standbyControlFragment?.notifyParameterAdapter()
            }
            standbyControlFragment?.notifyParameterAdapter()
        }
    }

    override fun onStateChange(isActive: Boolean, type: ControlSettingType) {
        if(type == ControlSettingType.BACKUP) prefManager?.setApneaSettingsStatus(isActive)
    }

}

