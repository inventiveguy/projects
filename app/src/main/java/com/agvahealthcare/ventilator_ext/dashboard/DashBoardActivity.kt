package com.agvahealthcare.ventilator_ext.dashboard

import ControlDialogFragment
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.View
import android.view.Window
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.agvahealthcare.ventilator_ext.BuildConfig
import com.agvahealthcare.ventilator_ext.MainActivity
import com.agvahealthcare.ventilator_ext.alarm.AlarmDialogFragment
import com.agvahealthcare.ventilator_ext.alarm.limit_one.EncoderValue
import com.agvahealthcare.ventilator_ext.alarm.limit_one.KnobParameterModel
import com.agvahealthcare.ventilator_ext.api.ServerLogger
import com.agvahealthcare.ventilator_ext.api.services.ScheduleDataBaseUpdate
import com.agvahealthcare.ventilator_ext.callback.*
import com.agvahealthcare.ventilator_ext.connection.parser.RaspiParser
import com.agvahealthcare.ventilator_ext.connection.parser.RaspiParser.TYPE_EXHALATION
import com.agvahealthcare.ventilator_ext.connection.parser.SpO2ParserExtension
import com.agvahealthcare.ventilator_ext.connection.support_threads.PingingTask
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterClickListener
import com.agvahealthcare.ventilator_ext.dashboard.adapter.*
import com.agvahealthcare.ventilator_ext.dashboard.duo_graph.DuoFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.pent_graph.DividePentFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.quad_graph.DivideQuadFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.quad_graph.QuadFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.trio_graph.DivideTrioFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.trio_graph.TrioFragmentGraph
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel
import com.agvahealthcare.ventilator_ext.graphics.GraphicsDialogFragment
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.logs.LogsDialogFragment
import com.agvahealthcare.ventilator_ext.logs.alarm.AlarmViewModel
import com.agvahealthcare.ventilator_ext.logs.event.EventViewModel
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.maneuvers.ExpiratoryInspiratoryFragmentDialog
import com.agvahealthcare.ventilator_ext.maneuvers.ManeuversDialogFragment
import com.agvahealthcare.ventilator_ext.model.*
import com.agvahealthcare.ventilator_ext.modes.GraphicTooltipFragment
import com.agvahealthcare.ventilator_ext.modes.ModeDialogFragment
import com.agvahealthcare.ventilator_ext.modes.OnModeConfirmListener
import com.agvahealthcare.ventilator_ext.modes.GeneralGraphicalToolTipFragment
import com.agvahealthcare.ventilator_ext.monitoring.MonitoringDialogFragment
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.service.UsbService
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.standby.StandbyControlDialogFragment
import com.agvahealthcare.ventilator_ext.system.SystemDialogFragment
import com.agvahealthcare.ventilator_ext.utility.*
import com.agvahealthcare.ventilator_ext.utility.utils.*
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils.getCurrentDateTime
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scichart.extensions.builders.SciChartBuilder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.ctx
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs


class DashBoardActivity : BaseLockActivity(), OnGraphSelectListener, OnDismissDialogListener,
    OnCalibrationOxygen, OnKnobPressListener, OnLimitChangeListener,
    OnLoudnessAdjustmentListener, OnAlarmLimitChangeListener,
    GraphicsDialogFragment.GraphLayoutListener,
    OnObserveValueSwapListener, PrimaryObservedParameterClickListener {

    private val TAG = DashBoardActivity::class.java.simpleName
    private val ACK_TAG = "ACK_CHECK"
    private var heightSize: Int? = null
    private var isFromKnob: String = EXPIRATORY_HOLD
    private var widthSize: Int? = null
    private var alarmLayoutPosition: Int? = null


    private var raspiParser: RaspiParser? = RaspiParser().addExtension(SpO2ParserExtension::class.java)// SpO2 extension added
    private var communicationService: CommunicationService? = null
    private var pingingTask: PingingTask? = null

    private var machineTriggerCount = 0
    private var prefManager: PreferenceManager? = null
    private var ventBasicParameterMap: MutableMap<String, ControlParameterModel> = HashMap()
    private var ventAdvancedParameterMap  : MutableMap<String, ControlParameterModel> = HashMap()
    private var ventBackupParameterMap: MutableMap<String, ControlParameterModel> = HashMap()
    private var basicControlParameterList = ArrayList<ControlParameterModel>()
    private var advancedControlParameterList = ArrayList<ControlParameterModel>()
    private var backupControlParameterList = ArrayList<ControlParameterModel>()
    private var primaryTileMap: MutableMap<String, ObservedParameterModel> = HashMap()
    private var selectedTiles = ArrayList<ObservedParameterModel>()
    private var observedValueMap: MutableMap<String, ObservedParameterModel> = HashMap()
    //data source for the swapable recyclerview
    private var observedValueList = ArrayList<ObservedParameterModel>()
    //intermediate copy of observedValueList for the swappable adapter for the diff check purpose
    private var observedValueListCopy = ArrayList<ObservedParameterModel>()

    private var observedValueSpHO2Map: MutableMap<String, ObservedParameterModel> = HashMap()
    private var observedValueSpHOList = ArrayList<ObservedParameterModel>()


    private var primaryObservedParameterAdapter: PrimaryObservedParameterAdapter? = null
    private var primaryControlParameterAdapter: PrimaryControlParameterAdapter? = null
    private var secondaryObservedParameterAdapter: SecondaryObservedParameterAdapter? = null
    private var swappableAdapter: SwappableAdapter? = null
    private lateinit var mEventViewModel: EventViewModel
    private lateinit var mAlarmViewModel: AlarmViewModel
    private lateinit var mDashBoardViewModel: DashBoardViewModel
    private lateinit var mAlarmDBModel: AlarmDBModel
//    private lateinit var alarmDBModelList: MutableList<AlarmDBModel>
    private val isLogsEnabled = true
    private var dataLogger: DataLogger? = null
    private var parseMap: Map<String, Map<String, String>>? = null

    //ToDo:-Different graph layouts set on the change from the layouts button selection
    private var divideQuadGraphFragment: DivideQuadFragmentGraph = DivideQuadFragmentGraph()
    private var quadGraphFragment: QuadFragmentGraph = QuadFragmentGraph()
    private var divideTrioGraphFragment: DivideTrioFragmentGraph = DivideTrioFragmentGraph()
    private var trioGraphFragment: TrioFragmentGraph = TrioFragmentGraph()
    private var duoGraphFragment: DuoFragmentGraph = DuoFragmentGraph()
    private var pentGraphFragment: DividePentFragmentGraph = DividePentFragmentGraph()
    private var monitoringDialogFragment: MonitoringDialogFragment? = null
    private var graphicsDialogFragment: GraphicsDialogFragment? = null
    private var maneuversDialogFragment: ManeuversDialogFragment? = null
    private var expiratoryInspiratoryFragmentDialog: ExpiratoryInspiratoryFragmentDialog? = null
    private var graphicTooltipFragment: GraphicTooltipFragment? = null

    private var logsDialogFragment: LogsDialogFragment? = null
    private var systemDialogFragment: SystemDialogFragment? = null
    private var modeDialogFragment: ModeDialogFragment? = null
    private var controlDialogFragment: ControlDialogFragment? = null
    private var alarmDialogFragment: AlarmDialogFragment? = null
    private var ackVisibilities: BooleanArray = BooleanArray(6000)
    var modeCode: Int = 0
    private var isPatientAvailable = true
    private var shutDownConfirmDialog: AlertDialog? = null
    private var standByConfirmDialog: AlertDialog? = null
    private var standByProgress: ProgressDialog? = null
    private var shutDownProgress: ProgressDialog? = null
    private var alarmToggleVisibility: Boolean = false
    private var knobDialog: KnobDialog? = null

    private val knobViewsCloseListner = object: OnDismissDialogListener{

        override fun handleDialogClose() {
            this@DashBoardActivity.handleDialogClose()
            renderControlParameterTilesViaPreference()
        }

    }

    private val modeConfirmListener = object :
        OnModeConfirmListener {
        override fun onConfirm(modeCode: Int) {
            Log.i("MODECHANGE", "Selected mode = $modeCode")
            handleDialogClose()

        }

        override fun onCancel() {
            Log.i("MODECHANGE", "Dialog mode cancelled")
            handleDialogClose()
        }

    }

    private var selectedBasicPosition: Int? = null
    private var selectedAdvancedPosition: Int? = null
    private var selectedBackupPosition: Int? = null
    private var pCount = 0
    private var vCount = 0
    private var fCount = 0
    private var rrCheckCount = 0
    private var leakCheckCount = 0
    private var leakBasedAlarmCheckCount = 0
    private var highLeakInaccuracy = 0
    private var complianceCheckCount = 0
    private var tbCount = 0
    private var lastAvgLeak: Float? = null
    private var alarmsMap: MutableMap<String, AlarmModel> = LinkedHashMap()
//    private var recyclerView: RecyclerView? = null
//    private var mDropDownAdapter: DropDownAdapter? = null
//    private var selectedStandId = 0
//    private var ackList: ArrayList<AlarmModel> = ArrayList<AlarmModel>()
//    private var tvddErrorPanel: TextView? = null
//    private var ddErrorPanel: RelativeLayout? = null
//    private var headerView: View? = null
//    private var dropDownView: DropDownView? = null
//    private var expandedView: View? = null
//    private var headerAlarmtext: ImageView? = null
    private var stpWindowHolder: STPWindowHolder? = null
    private var isServiceBound: Boolean = false

    private var isLocked: Boolean
        get() = prefManager?.readLockScreenStatus() ?: false
        set(status){
            button_maneuvers.isEnabled = status
            button_systems.isEnabled = status
            button_modes.isEnabled = status
            button_controls.isEnabled = status
            button_alarms.isEnabled = status
            button_loop.isEnabled = status
            recyclerViewSetValue.isEnabled = status

            prefManager?.setLockScreenStatus(status)
            ToastFactory.custom(this, "Lock Screen Status $status")
        }


    private var mediaPlayer: CustomMediaPlayer? = null
    private var standbyControlFragment: StandbyControlDialogFragment? = null
    private var customCountDownTimer: CustomCountDownTimer? = null

    private var isBatteryLowAlerted = false
    private var cachedBatteryLevel: BatteryLevelType? = null
    private var currentPriority = AlarmType.ALARM_NO_LEVEL
    private var alarmIsPlaying: Boolean? = false
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var previousFragment: Fragment? = null
    private var currentFragment: Fragment? = null

    enum class AlarmState {
        LOW, HIGH, BATTERY
    }

    private var currentAlarmState: AlarmState? = null
    private var isAlarmMuted: Boolean = false


    init {
        Arrays.fill(ackVisibilities, false)
    }


    /*
     * This provides intent filter for the Gatt Data Receiver
     */
    private fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentFactory.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_ACK_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_BATTERY_STATUS_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_SOFTWARE_VERSION_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_DATA_SENT)
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_CONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_DEVICE_DISCONNECTED)
        intentFilter.addAction(IntentFactory.ACTION_INACTIVE)
        intentFilter.addAction(IntentFactory.ACTION_LOW_O2)
        intentFilter.addAction(IntentFactory.ACTION_MODE_SET)
        intentFilter.addAction(IntentFactory.ACTION_TUBE_BLOCKAGE_DETECTED)
        intentFilter.addAction(IntentFactory.ACTION_TUBE_BLOCKAGE_RESOLVED)
        intentFilter.addAction(IntentFactory.ACTION_HIGH_LEAK_INACCURACY_DETECTED)
        intentFilter.addAction(IntentFactory.ACTION_HIGH_LEAK_INACCURACY_RESOLVED)
        intentFilter.addAction(IntentFactory.ACTION_CUFF_LEAKAGE_DETECTED)
        intentFilter.addAction(IntentFactory.ACTION_CUFF_LEAKAGE_RESOLVED)
        intentFilter.addAction(IntentFactory.ACTION_FLOW_SENSOR_OCCLUSION_DETECTED)
        intentFilter.addAction(IntentFactory.ACTION_FLOW_SENSOR_OCCLUSION_RESOLVED)
        intentFilter.addAction(IntentFactory.ACTION_EXPIRE_TIME_OUT_OF_RANGE)
        intentFilter.addAction(IntentFactory.ACTION_POWER_SWITCH)
        intentFilter.addAction(IntentFactory.ACTION_EXPIRE_TIME_UNDER_RANGE)
        intentFilter.addAction(IntentFactory.ACTION_MOTOR_LIFE_STATUS_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_STANDBY_STATUS_AVAILABLE)
        intentFilter.addAction(IntentFactory.ACTION_SELF_TEST_STATUS_AVAILABLE)
        // Software alarm Intents
        intentFilter.addAction(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW)
        intentFilter.addAction(IntentFactory.ACTION_VENT_PARAM_LIMIT_OVERFLOW)

        intentFilter.addAction(IntentFactory.ACTION_KNOB_CHANGE)
        intentFilter.addAction(IntentFactory.ACTION_MUTE_UNMUTE)
        intentFilter.addAction(IntentFactory.ACTION_HOME)
        intentFilter.addAction(IntentFactory.ACTION_LOCK)
        intentFilter.addAction(IntentFactory.ACTION_INSPIRATORY_HOLD)
        intentFilter.addAction(IntentFactory.ACTION_EXPIRATORY_HOLD)
        intentFilter.addAction(IntentFactory.ACTION_OXYGEN_100)
        intentFilter.addAction(IntentFactory.ACTION_NEBULISER)
        intentFilter.addAction(IntentFactory.ACTION_MANUAL_BREATH)

        intentFilter.addAction(IntentFactory.ACTION_SENSOR_AVAILABILITY_RESPONSE)
        intentFilter.addAction(IntentFactory.ACTION_SENSOR_CALIBRATION_RESPONSE)

        // ONLY FOR TESTING
        if (BuildConfig.DEBUG) {
            intentFilter.addAction(IntentFactory.ACTION_HEATSENSE_STATUS_AVAILABLE)
        }

        return intentFilter
    }


    // BroadCast Receiver Getting Data from IntentFactory
    private val gattReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            intent.action?.apply {
                when (this) {

                    IntentFactory.ACTION_DATA_AVAILABLE -> {

                        runOnUiThread {
                            val data = intent.getStringExtra(VENTILATOR_DATA)
                            data?.takeIf { it.isNotBlank() }?.apply {
                                Log.i("VENT DATA", this)
                                stpWindowHolder?.takeIf { it.isVisible }?.apply {
                                    putResponse("\$AgVa : " + data.substring(0, data.length - 1))
                                    Log.i("STP_CHECK", data)
                                } ?: kotlin.run {
                                    parseMap = raspiParser?.parser(data)
                                    Log.i("VENT_DATA_PARSED", parseMap.toString())
                                    parseMap?.apply {
                                        setDataOnScreen(this)
                                    }
                                }
                            }
                        }
                    }
                    IntentFactory.ACTION_ACK_AVAILABLE -> {
                        val ackValue = intent.getStringExtra(VENTILATOR_ACK)
                        Log.i(TAG, "Broadcast  receiver receives ACK @$ackValue")
                        val runningModeFromIntent = intent.getIntExtra(VENTILATOR_MODES, -1)

                        ackValue?.takeIf {
                            isNotEmpty()
//                                    && isAcknowledgementAcceptable(
//                                runningModeFromIntent,
//                                ackValue
//                            )
                        }?.apply { handleAcknowledgements(this) }

                    }

                    IntentFactory.ACTION_BATTERY_STATUS_AVAILABLE -> {

                        val isPowerConnected = !ackVisibilities[0]
                        val batteryLevel = intent.getIntExtra(VENTILATOR_BATTERY_LEVEL, -1)
                        val batteryHealth = intent.getIntExtra(VENTILATOR_BATTERY_HEALTH, -1)
                        val batteryRemainingTime = intent.getIntExtra(VENTILATOR_BATTERY_TTE, -1).let {
                            if(isPowerConnected || it < 30) -1
                            else it
                        }
                        updateBatteryImage(batteryLevel)
                        // update battery level on view
                       /* if (batteryLevel in 0..100 && batteryHealth in 0..100 && batteryRemainingTime >= 0) {
                            updateBatteryLevel(batteryLevel, batteryHealth, batteryRemainingTime)
                        }*/
                        mDashBoardViewModel.setVentBatteryHealth(batteryHealth)
                        mDashBoardViewModel.setVentBatteryLevel(batteryLevel)
                        mDashBoardViewModel.setVentBatteryRemainingTime(batteryRemainingTime)
                    }

                    IntentFactory.ACTION_HEATSENSE_STATUS_AVAILABLE -> {

                        val sensorData = intent.getIntegerArrayListExtra(VENTILATOR_HEATSENSE_DATA)
                        Log.i("HEATSENSE_CHECK", "DATA = $sensorData")
                        sensorData?.apply { updateHeatSensorData(this) }
                    }

                    IntentFactory.ACTION_SOFTWARE_VERSION_AVAILABLE -> {
                        val data = intent.getStringExtra(VENTILATOR_SOFTWARE_VERSION)
                        data?.let {
                            Log.i("SOFTWARE_VERSION", it)
                            prefManager?.setVentilatorSoftwareVersion(it)
                            updateSoftwareVersion(it)
                        }

                    }

                    IntentFactory.ACTION_MOTOR_LIFE_STATUS_AVAILABLE -> {
                        val motorLifeHrs = intent.getIntExtra(VENTILATOR_MOTOR_LIFE, 0)
                        Log.i("MOTOR_LIFE_CHECK", "Motor life level at " + motorLifeHrs + "hrs")

                    }

                    IntentFactory.ACTION_POWER_SWITCH -> {

                        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
                            send(resources.getString(R.string.cmd_vent_shutdown))


                        }
                        mediaPlayer?.takeIf { it.isRunning }?.apply {
                            stop()
                            release()
                        }
                    }


//                    IntentFactory.ACTION_STANDBY_STATUS_AVAILABLE -> {
//                        val standbyResponse = intent.getIntExtra(VENTILATOR_STANDBY_STATUS, 0)
//                        Log.i("STAND_BY_RESPONSE", "" + standbyResponse)
//                        if (standbyResponse == 1) {
//                            prefManager?.setStandbyStatus(true)
//
//
//
//                            Log.i("STAND_BY_RESPONSE", "STANDBY")
//                            Handler(Looper.getMainLooper()).postDelayed({
//
//                                // Dismiss progress bar after 2 seconds
//                                hideStandbyProgress()
//
//                            }, 500)
//
//                            val eventDataModel = EventDataModel("Standby Successfully")
//                            mEventViewModel.addEvent(eventDataModel)
//
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                Log.i("STANDBY", "Standby is true")
//                                // Dismiss progress bar after 2 seconds
//                                val intentData =
//                                    Intent(this@DashBoardActivity, MainActivity::class.java)
//                                intentData.putExtra(IS_STAND_BY, true)
//                                startActivity(intentData)
//                                finish()
//
//                                mediaPlayer?.takeIf { it.isRunning }?.apply {
//                                    stop()
//                                    release()
//                                }
//                            }, 2000)
//
//                        }
//
//                    }

                    IntentFactory.ACTION_MODE_SET -> {
                        modeCode = intent.getIntExtra(VENTILATOR_MODES, -1)
                        Log.i("MODE_CODE", "MODE  $modeCode")
                        showStandbyControlFragment()
                    }

                    IntentFactory.ACTION_DEVICE_DISCONNECTED -> {
                        Log.i("CONN_CHECK", "Connection closed")
                        communicationService?.apply {
                            stopReading()
                            stopPinging()
                        }
                        layoutErrorBar?.setBackgroundResource(R.color.ack_red)
                        tvErrorBar?.text = "USB DEVICE DISCONNECTED"


                    }

                    IntentFactory.ACTION_DEVICE_CONNECTED -> {
                        Log.i("CONN_CHECK", "Connection established")
                        communicationService?.takeIf { it.isPortsConnected }?.apply {
                            startPinging()
                            layoutErrorBar?.setBackgroundResource(R.color.black)
                            tvErrorBar?.text = "USB DEVICE CONNECTED"
                            startReading()
                            send(getString(R.string.cmd_version))
                        }
                    }

                    IntentFactory.ACTION_INACTIVE -> {
                        Log.i("INACTIVE_WATCH_DOG", "INACTIVE_ALERT")

                    }

                    IntentFactory.ACTION_VENT_PARAM_LIMIT_OVERFLOW -> {

                        val lbl = intent.getStringExtra(ALERT_LABEL)
                        val message = intent.getStringExtra(ALERT_MSG)

                        lbl?.let {
                            if (alarmsMap.containsKey(it)) return
                            else {
                                Log.i("OVERFLOW", "Added $it -> $message")
                                message?.let { alarmMsg -> addAlarm(it, alarmMsg) }
                                // for tile warning state
                                primaryObservedParameterAdapter?.notifyDataSetChanged()
                                secondaryObservedParameterAdapter?.notifyDataSetChanged()
//                                validateErrorBar()
                            }
                        }


                    }
                    IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW -> {

                        val lbl = intent.getStringExtra(ALERT_LABEL)
//                        val message = intent.getStringExtra(ALERT_MSG)
                        Log.i("UNDERFLOW", (alarmsMap.keys).toString())
                        lbl?.let {
                            if (alarmsMap.containsKey(it)) {
                                removeAlarm(it)
                                // for tile warning state
                                primaryObservedParameterAdapter?.notifyDataSetChanged()
                                secondaryObservedParameterAdapter?.notifyDataSetChanged()
//                                validateErrorBar()
                            }
                        }
                    }


                    IntentFactory.ACTION_EXPIRE_TIME_OUT_OF_RANGE -> {

                    }

                    IntentFactory.ACTION_EXPIRE_TIME_UNDER_RANGE -> {


                    }

                    IntentFactory.ACTION_KNOB_CHANGE -> {
                        val data = intent.getStringExtra(VENTILATOR_CONTROL_KNOB)
                        data?.takeIf { it.isNotEmpty() }?.apply {
                            Log.i("KNOB_DATADASH", this)

                            alarmDialogFragment?.takeIf { it.isVisible }?.apply {
                                update(data)
                            }

                            updateKnobViews(this)

                            updateSwappableLayout(this)

                            maneuversDialogFragment?.takeIf { it.isVisible }?.apply {
                                updateKnob(data)
                            }
                            systemDialogFragment?.takeIf { it.isVisible }?.apply {
                                updateKnob(data)
                            }

                            logsDialogFragment?.takeIf { it.isVisible }?.apply {
                                updateLogsTendsScrollState(data)
                            }


                            /*modeDialogFragment?.takeIf { it.isVisible }?.apply {
                                updateKnob(data)
                            }*/

                        }

                    }

                    IntentFactory.ACTION_MUTE_UNMUTE -> {
//

                        if (ACCESS_MUTEUNMUTE_AVAILABLE) {
                            imageViewAlarm.callOnClick()
                            Log.i("ACTION_HARDWARE_BUTTON", "Mute button is clicked.........")
                            ACCESS_MUTEUNMUTE_AVAILABLE = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_MUTEUNMUTE_AVAILABLE = true
                            }, 120000)
                        }


                    }
                    IntentFactory.ACTION_NEBULISER -> {
                        Log.i("ACTION_HARDWARE_BUTTON", "Nebuliser button is clicked.........")

                        if (ACCESS_NEBULISER_AVAILABLE) {
                            if (isLocked)
                                communicationService?.takeIf { it.isPortsConnected && isServiceBound }
                                    ?.apply {

                                        send(getString(R.string.cmd_vent_nebuliser))
                                        //AppUtils.NEBULISER=true

                                    }
                            ACCESS_NEBULISER_AVAILABLE = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_NEBULISER_AVAILABLE = true
                            }, 1000)
                        }

                    }

                    IntentFactory.ACTION_OXYGEN_100 -> {
                        Log.i("ACTION_HARDWARE_BUTTON", "Oxygen 100% button is clicked.........")

                        if (ACCESS_OXYGEN_AVAILABLE) {
                            if (isLocked) {
                                //AppUtils.OXEGEN=true

                                prefManager?.apply {
                                    val oxygen = readFiO2()?.toInt()
                                    val eventDataModel =
                                        EventDataModel("Set Oxygen from ${oxygen}% to 100%")
                                    mEventViewModel.addEvent(eventDataModel)
                                }

                                sendO2FullCommandToVentilator()
                                ACCESS_OXYGEN_AVAILABLE = false
                            }

                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_OXYGEN_AVAILABLE = true
                            }, 1000)
                        }


                    }

                    IntentFactory.ACTION_HOME -> {
                        //AppUtils.HOME=true
                        Log.i("ACTION_HARDWARE_BUTTON", "Home button is clicked.........")
                        if (ACCESS_HOME_AVAILABLE) {
                            hideAllAlertDialogBox()
                            hideKnobViews()
                            //hideModeConfirmation()
                            hideAllDialogFragment()
                            ACCESS_HOME_AVAILABLE = false


                            Log.i("ACTION_HARDWARE_BUTTON", "Home button is clicked.........")

                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_HOME_AVAILABLE = true
                            }, 1000)
                        }
                    }

                    IntentFactory.ACTION_LOCK -> {

                        if (ACCESS_LOCK_AVAILABLE) {
                            hideAllAlertDialogBox()
                            hideKnobViews()
                            //hideModeConfirmation()
                            hideAllDialogFragment()

                            isLocked = !isLocked
                            ACCESS_LOCK_AVAILABLE = false
                            Log.i("ACTION_HARDWARE_BUTTON", "Lock button is clicked.........")

                            Handler(Looper.getMainLooper()).postDelayed({

                                ACCESS_LOCK_AVAILABLE = true
                            }, 1000)
                        }


                    }

                    IntentFactory.ACTION_INSPIRATORY_HOLD -> {
                        isFromKnob = INSPIRATORY_HOLD
                        //AppUtils.INSPIRATORY=true


                        if (ACCESS_INSPIRATORY_AVAILABLE) {


                            if (maneuversDialogFragment == null) {

                                maneuversDialogFragment =
                                    ManeuversDialogFragment.newInstance(
                                        heightSize,
                                        widthSize,
                                        isFromKnob,
                                        this@DashBoardActivity,
                                        communicationService
                                    )
                                currentFragment = maneuversDialogFragment
                            }

                            maneuversDialogFragment?.let {


                                supportFragmentManager.apply {
                                    beginTransaction().replace(
                                        R.id.dashboardFragment_nav_container,
                                        it,
                                        ManeuversDialogFragment.TAG
                                    )
                                        .commitNow()
                                }


                            }

                            checkDialogPresence()


                            ACCESS_INSPIRATORY_AVAILABLE = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_INSPIRATORY_AVAILABLE = true
                            }, 1000)
                        }


                    }

                    IntentFactory.ACTION_EXPIRATORY_HOLD -> {
                        // AppUtils.EXPIRATORY=true

                        isFromKnob = EXPIRATORY_HOLD

                        if (ACCESS_EXPIRATORY_AVAILABLE) {

                            if (maneuversDialogFragment == null) {

                                maneuversDialogFragment =
                                    ManeuversDialogFragment.newInstance(
                                        heightSize,
                                        widthSize,
                                        isFromKnob,
                                        this@DashBoardActivity,
                                        communicationService
                                    )
                                currentFragment = maneuversDialogFragment

                            }

                            maneuversDialogFragment?.let {

                                supportFragmentManager.apply {
                                    beginTransaction().replace(
                                        R.id.dashboardFragment_nav_container,
                                        it,
                                        ManeuversDialogFragment.TAG
                                    )
                                        .commitNow()
                                }

                            }

                            checkDialogPresence()



                            ACCESS_EXPIRATORY_AVAILABLE = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_EXPIRATORY_AVAILABLE = true
                            }, 1000)
                        }


                    }
                    IntentFactory.ACTION_MANUAL_BREATH -> {
                        //  AppUtils.MANUALBREATH=true

                        Log.i("ACTION_HARDWARE_BUTTON", "Manual breath button is clicked.........")



                        if (ACCESS_MANUALBREATH_AVAILABLE) {
                            communicationService?.takeIf { isServiceBound }?.apply {
                                send(getString(R.string.cmd_vent_breath))
                            }

                            val eventDataModel = EventDataModel("Manual Breath requested")
                            mEventViewModel.addEvent(eventDataModel)
                            ACCESS_MANUALBREATH_AVAILABLE = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                ACCESS_MANUALBREATH_AVAILABLE = true
                            }, 1000)
                        }


                    }

                    IntentFactory.ACTION_SELF_TEST_STATUS_AVAILABLE -> {
                        val stpResponse = intent.getIntExtra(VENTILATOR_SELF_TEST_STATUS, 0)
                        if (stpResponse == 7) {
                            showSelfTestWindow()

                            val data = intent.getStringExtra(VENTILATOR_DATA)
                            data?.let {
                                Log.i("VENT DATA", data)
                                stpWindowHolder?.apply {
                                    putResponse(it)
                                }
                            }

                        } else {
                            dismissSelfTestWindow()
                        }
                    }

                    IntentFactory.ACTION_SENSOR_AVAILABILITY_RESPONSE -> {
                        Log.i("SENSOR_ANALYSIS_CHECK", "Welcome to sensor avail")
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
                        Log.i("CALIBCHECK", "Sensor = $status calibrated with value = $calibResult")


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
                                Log.i("CALIBCHECK", "Sensor calib = $calibration")

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


                }
            }
        }
    }



    private fun sendO2FullCommandToVentilator() {
        prefManager?.setFiO2(
            resources.getString(R.string.max_fio2).toFloatOrNull()
        ) // Set Oxygen level to 100%
        Log.i("OXYGEN_CHEK", "Oxygen  ${resources.getString(R.string.max_fio2).toFloatOrNull()}")
        val eventDataModel = EventDataModel("Set 100% Oxygen  ")
        mEventViewModel.addEvent(eventDataModel)
        sendConfigurationToVentilatorWithWatchDog()

    }

    private fun hideAllAlertDialogBox() {
        hideStandByDialog()
        hideStandbyProgress()
        dismissSelfTestWindow()
    }

    private fun hideAllDialogFragment() {
        fragmentManager.findFragmentById(R.id.dashboardFragment_nav_container)?.apply {
            supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
        }
        handleDialogClose()

    }


    private fun showSelfTestWindow() {
        if (stpWindowHolder == null) {
            stpWindowHolder = STPWindowHolder(this@DashBoardActivity)
        }
        stpWindowHolder?.show()
    }

    private fun dismissSelfTestWindow() {
        stpWindowHolder?.takeIf { it.isVisible }?.apply {
            hide()
        }

    }

    private val standbyControlDismissListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            this@DashBoardActivity.handleDialogClose()
            prefManager?.readVentilationMode()
                ?.let {
                    modeCode = it
                    Log.i("MODE_CODE", "MODE  $modeCode")

                } // shift to previous mode if mode dialog is cancelled
            //renderControlParameterTilesViaPreference()

        }
    }

    private val controlDismissListener = object : OnDismissDialogListener {
        override fun handleDialogClose() {
            this@DashBoardActivity.handleDialogClose()
            selectedBasicPosition = null
            selectedBackupPosition = null
            selectedAdvancedPosition = null


        }
    }

    private val startNewVentilationListener = object : OnStartVentilationListener {
        override fun onStart() {
            communicationService?.send(modeCode.toString())
        }
    }


    private fun showKnobDialogForStandbyControl(parameterModel: KnobParameterModel, encoderValue: EncoderValue, parameterList: MutableList<ControlParameterModel>, pos: Int, type: ControlSettingType) {
        hideKnobDialog()

        knobDialog = KnobDialog.newInstance(
            onKnobPressListener = object: OnKnobPressListener{
                override fun onKnobPress(previousValue: Float, newValue: Float) {
                    parameterList.also {
                            val lbl = it[pos].ventKey
                            val unit = it[pos].units

                            it.filter { it.ventKey == lbl }
                                .takeIf { it.isNotEmpty() }
                                ?.getOrNull(0)
                                ?.apply {
                                   // this.reading = if(isDecimalSupported) String.format("%.1f", newValue) else newValue.toInt().toString()
                                       this.reading = supportPrecision(lbl, newValue)
                                }


                            val eventDataModel = EventDataModel(
                                "Set $lbl from ${
                                    supportPrecision(lbl, previousValue)
                                }  $unit to $newValue $unit"
                            )

                            mEventViewModel.addEvent(eventDataModel)

                    }.forEach {
                        it.isIsselected = false
                    }


                    standbyControlFragment?.notifyParameterAdapter()
                    Log.i("STANDBYCONTROL_CHECK", "ARR[$pos] -> $${parameterList[pos].reading}")

                    hideKnobViews()
                    hideSystemUI()
                   // normaliseParameterTiles()

                }

            },
            onLimitChangeListener = object: OnLimitChangeListener{
                override fun onLimitChange(
                    previousValue: Float,
                    newValue: Float
                ) {
                    parameterList.getOrNull(pos)?.let { param ->
                        Log.i("ValueUpdates","ARR[$pos] -> $${parameterList[pos].reading}" + newValue.toString())
                        val model = standbyControlFragment?.updateParameterValue(param.ventKey, newValue.toString(), type)
                       // ToastFactory.custom(ctx,newValue.toString())

                        model?.let { updateGraphictoolTip(it) }
                    }
                }
            },
            onTimeoutListener = object : OnDismissDialogListener {
                override fun handleDialogClose() {
                    this@DashBoardActivity.handleDialogClose()
                    parameterList.forEach {
                        it.isIsselected = false
                    }
                    renderStandbyControlParameterTilesViaPreference()
                }

            },
            onCloseListener = object : OnDismissDialogListener {
                override fun handleDialogClose() {
                    parameterList.forEach {
                        it.isIsselected = false
                    }
                    renderStandbyControlParameterTilesViaPreference()
                }
            },
            parameterModel = parameterModel,
            encoderValue = encoderValue,
//                width = widthSize  // check if it is necessary
        ).also {


            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.knob_nav_container,
                    it,
                    ControlDialogFragment.TAG
                )
                    .commitNow()
            }
            it.startTimeoutWithDebounce()
            knob_nav_container.visibility = View.VISIBLE

        }

    }

    private fun showKnobDialog(parameterModel: KnobParameterModel, encoderValue: EncoderValue) {


        hideKnobDialog()

        knobDialog = KnobDialog.newInstance(
            onKnobPressListener = this,
            onLimitChangeListener = this,
            onTimeoutListener = knobViewsCloseListner,
            onCloseListener = object : OnDismissDialogListener {
                override fun handleDialogClose() {
                    renderControlParameterTilesViaPreference()
                }

            },
            parameterModel = parameterModel,
            encoderValue = encoderValue,
//                width = widthSize  // check if it is necessary
        ).also {
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.knob_nav_container,
                    it,
                    ControlDialogFragment.TAG
                )
                    .commitNow()


            }
            it.startTimeoutWithDebounce()
            knob_nav_container.visibility = View.VISIBLE

        }


    }

    private fun hideKnobDialog() {

        if (knobDialog?.isVisible == true) {
            knob_nav_container.visibility = View.GONE

            knobDialog?.let {
                supportFragmentManager.beginTransaction().remove(it).commitNow()
                knobDialog = null
            }

//            simv_nav_container.visibility=View.GONE
//            knobDialog?.dismiss()

        }
        /* knobDialog?.takeIf { it.isVisible }?.apply {
             knob_nav_container.visibility=View.GONE
             simv_nav_container.visibility=View.GONE
             //dismiss()

         }*/
    }




    private fun showGraphicTooltip() {

        prefManager?.readVentilationMode()?.run {

            hideGraphicTooltip()

            /* if(Configs.getModeCategory(this) ==  Configs.MODE_PCV) {
                 if(this == Configs.MODE_PC_SIMV){

 //                    SettingSimvDialogFragment.newInstance(knobViewsCloseListner)
                     SettingPcvDialogFragment.newInstance(knobViewsCloseListner)
                 } else {
                     SettingPcvDialogFragment.newInstance(knobViewsCloseListner)
                     //SettingSimvDialogFragment.newInstance(knobViewsCloseListner)
                 }
             }
             else if(Configs.getModeCategory(this)== Configs.MODE_VCV) {

                 if(this == Configs.MODE_VCV_SIMV) {
                     //SettingSimvDialogFragment.newInstance(knobViewsCloseListner)
                     SettingPcvDialogFragment.newInstance(knobViewsCloseListner)
                 }else{
                     //SettingVcvDialogFragment.newInstance(knobViewsCloseListner)
                     SettingPcvDialogFragment.newInstance(knobViewsCloseListner)
                 }
             }
             else if(this == Configs.MODE_NIV){
                 //SettingVcvDialogFragment.newInstance(knobViewsCloseListner)
                 SettingPcvDialogFragment.newInstance(knobViewsCloseListner)
             }
             else {
                 Log.i("GTCHECK", "No tooltip for this mode = ${this}")
                 null
             }*/
            //above code previous followed by the current
            GeneralGraphicalToolTipFragment.newInstance(knobViewsCloseListner)
        }?.apply {
//            if(supportFragmentManager.findFragmentByTag(this.TAG) == null){
            //show(supportFragmentManager, this.TAG)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.simv_nav_container, this, this.TAG)
                .commitNow()

            graphicTooltipFragment = this
            graphicTooltipFragment?.startTimeoutWithDebounce()

//            } else{
//                graphicTooltipFragment?.startTimeoutWithDebounce()
//                Log.i("GRAPHICTOOLTIP", "Demanded tooltip is already visible")
//            };


        }

        simv_nav_container.visibility = View.VISIBLE

    }

    private fun hideGraphicTooltip() {


        if (graphicTooltipFragment?.isVisible == true) {
//            knob_nav_container.visibility=View.GONE
            simv_nav_container.visibility = View.GONE
            graphicTooltipFragment?.let {
                supportFragmentManager.beginTransaction().remove(it).commitNow()
                graphicTooltipFragment = null
            }

        }
        /* graphicTooltipFragment?.takeIf { it.isVisible }?.apply {
             dismiss()

         }*/
    }


    private fun showKnobViews(parameterModel: KnobParameterModel, encoderValue: EncoderValue) {

        closeSwappableLayout()
        showGraphicTooltip()
        showKnobDialog(parameterModel, encoderValue)
        // start handler
    }


    private fun showKnobViewsForStandbyControls(parameterModel: KnobParameterModel, encoderValue: EncoderValue, parameterList: MutableList<ControlParameterModel>, pos: Int,type: ControlSettingType) {

        closeSwappableLayout()
        showGraphicTooltip()
        showKnobDialogForStandbyControl(parameterModel, encoderValue, parameterList, pos, type)

        // start handler
    }

    private fun hideKnobViews() {
        normaliseParameterTiles()
        hideKnobDialog()
        hideGraphicTooltip()
    }

    //   private fun hideModeConfirmation() = modeDialogFragment?.getModeConfirmationDialog()?.dismiss()


    private fun updateKnobViews(data: String) {
        knobDialog?.takeIf { it.isVisible }?.apply {
            updateWithTimeoutDebounce(data)
            // Debouncing of knob views
        }

        graphicTooltipFragment?.takeIf { it.isVisible }?.apply {
            startTimeoutWithDebounce()
        }
    }

    private fun updateLogsTendsScrollState(data: String){
        when(data){
            PREFIX_PLUS -> logsDialogFragment?.scrollTrendsForward()
            PREFIX_MINUS -> logsDialogFragment?.scrollTrendsBack()
        }
    }

    private fun updateSwappableLayout(data: String) {
        if (swappableLayout.visibility == View.VISIBLE) {
            swappableAdapter?.apply {
                when (data) {
                    PREFIX_PLUS -> {
                        setSelectionForward()
                        swappableRecyclerView.smoothScrollToPosition(getSelection())
                    }
//                    scrollSwappableList(1, Direction.DOWN)
                    PREFIX_MINUS -> {
                        setSelectionBackward()
                        swappableRecyclerView.smoothScrollToPosition(getSelection())
                    }
//                          scrollSwappableList(1,  Direction.UP)
                    PREFIX_AND -> {
                        swapWithCurrentSelection()
                        closeSwappableLayout()
                    }
                }
            }
        }

    }


    private fun updateBatteryLevel(
        batteryLevel: Int,
        batteryHealth: Int,
        batteryRemainingTime: Int
    ) {

        Log.i(
            "battery",
            "LEVEL = $batteryLevel %\nHEALTH = $batteryHealth %\nREMAINING TIME = $batteryRemainingTime mins"
        )
    }

    private fun updateHeatSensorData(dataList: ArrayList<Int>) {
        dataList.takeIf { it.isNotEmpty() }?.run {
            dataList.takeIf { it.size > 0 }?.get(0)?.apply {
                tvTempPropValve.let {
                    decorateTemperatureViews(it, 50, this)
                    it.text = "Prop Valve = ${this} °C"
                }
            }
            dataList.takeIf { it.size > 1 }?.get(1)?.apply {
                tvTempTurbine.let {
                    decorateTemperatureViews(it, 60, this)
                    it.text = "Turbine = ${this} °C"
                }
            }
            dataList.takeIf { it.size > 2 }?.get(2)?.apply {
                tvTempRaspi.let {
                    decorateTemperatureViews(it, 60, this)
                    it.text = "Processor = ${this} °C"
                }
            }
            dataList.takeIf { it.size > 3 }?.get(3)?.apply {
                tvTempBattery.let {
                    decorateTemperatureViews(it, 70, this)
                    it.text = "Battery = ${this} °C"
                }
            }
        }
    }

    private fun decorateTemperatureViews(
        tv: AppCompatTextView,
        thresholdTemp: Int,
        actualTemp: Int?
    ) {
        actualTemp?.apply {
            @ColorRes val colorRes = if (this < thresholdTemp) R.color.white else R.color.red
            tv.setTextColor(getColor(colorRes))
        }
    }

    private fun updateSoftwareVersion(softVersion: String?) {

        systemDialogFragment?.takeIf { it.isVisible }?.apply {
            Handler(Looper.getMainLooper()).postDelayed({
                setSoftWareUpdate(softVersion)
            }, 1000)
        }

    }




    private fun handleAcknowledgements(ackValue: String) {

        /* if active ack is same as current
         * then do not perform any action
         */
        val isAckValid = ackValue.startsWith(Configs.PREFIX_ACK)
        if (!isAckValid) return

        if(alarmsMap.containsKey(ackValue)){
            // IF ACK IS REPEATED, IGNORE MESSAGE
            Log.i(ACK_TAG, "IGNORING EXISTING ACK $ackValue")
            return
        }  else {
            val ackMsg = MessageFactory.getAckMessage(this, ackValue)
            //code for displaying error msg on the dashboardActivity
            when(AlarmConfiguration.getAckType(ackValue)){
                AckType.ACK -> {
                    val ackCode = ackValue.replace("ACK", "").toIntOrNull()
                    addAlarm(ackValue, ackMsg)
                    ackCode?.let { ackVisibilities[it] = true }
                }
                AckType.NACK -> {
                    val correspondingAckValue = AlarmConfiguration.getAckFor(ackValue)
                    correspondingAckValue?.let {
                        val nackCode = it.replace("ACK", "").toIntOrNull()
                        removeAlarm(it)
                        nackCode?.apply {  ackVisibilities[this] = false }
                    }

                }
                AckType.INVALID_ACK -> {
                    Log.i(ACK_TAG, "Invalid (untracked) ack code found in handleAcknowledgement")
                    return
                }
                AckType.OP_ACK->{

                }

            }
        }

        when (ackValue) {
            //replacement of ACK_CODE_2
            ACK_CODE_5001 -> {

                ackVisibilities[5001]=true
                Log.d("ackvalueInsp",ackVisibilities[5001].toString())
            }
            ACK_CODE_5002 -> {
                ackVisibilities[5002]=true

                Log.d("ackvalueExp",ackVisibilities[5002].toString())
            }

            ACK_CODE_5006 ->{

                prefManager?.apply {
                    clearProfilePreferences(readCurrentUid())
                    setVentilationMode(modeCode)
                    Log.i("", "Mode changed to $modeCode")
                }

                standbyControlFragment?.apply{
                    val status = getApneaStatus() ?: false
                    getAllControlParameters()?.let{ sendParametersToVentilator(it, status) }
                }
                renderAllTiles()
               // clearDataSeries()
            }

            //replacement of ACK_CODE_4
            ACK_CODE_5005 -> {

                //to avoid Re-Draw Mechanisms for Graph
                isEndOfExhalationOccured = false
                settingsCountDownTimer?.safeStop()
                standbyControlFragment?.let {
                    supportFragmentManager.beginTransaction().remove(it).commitNow()
                }
//                ToastFactory.setSnackBar(mainViewPanel, getString(R.string.hint_setting_saved))
                clearDataSeries()
                renderControlParameterTilesViaPreference()
                checkDialogPresence()
                settingsCountDownTimer = null // safe stop will get some time to execute before assigning to null
            }

            ACK_CODE_746 ->{
                prefManager?.setStandbyStatus(true)
                Log.i("STAND_BY_RESPONSE", "STANDBY")
                Handler(Looper.getMainLooper()).postDelayed({

                    // Dismiss progress bar after 2 seconds
                    hideStandbyProgress()

                }, 500)

                val eventDataModel = EventDataModel("Standby Successfully")
                mEventViewModel.addEvent(eventDataModel)

                Handler(Looper.getMainLooper()).postDelayed({
                    Log.i("STANDBY", "Standby is true")
                    // Dismiss progress bar after 2 seconds
                    val intentData =
                        Intent(this@DashBoardActivity, MainActivity::class.java)
                    intentData.putExtra(IS_STAND_BY, true)
                    startActivity(intentData)
                    finish()

                    mediaPlayer?.takeIf { it.isRunning }?.apply {
                        stop()
                        release()
                    }
                }, 2000)
            }
        }
    }

    private fun updateBatteryStatusLabel(batteryType: BatteryLevelType) {

        when (batteryType) {
            BatteryLevelType.CRITICALLY_LOW -> {
                batteryViewLevel.visibility = View.GONE
                imageViewBattery.visibility = View.VISIBLE
                cachedBatteryLevel = BatteryLevelType.CRITICALLY_LOW
            }
            BatteryLevelType.CHARGING -> {
                batteryViewLevel.visibility = View.VISIBLE
                imageViewBattery.visibility = View.GONE
                cachedBatteryLevel = BatteryLevelType.CHARGING
            }
            BatteryLevelType.ON_BATTERY -> if (cachedBatteryLevel !== BatteryLevelType.CRITICALLY_LOW) {
                batteryViewLevel.visibility = View.VISIBLE
                imageViewBattery.visibility = View.GONE
                cachedBatteryLevel = BatteryLevelType.ON_BATTERY
            }
        }

    }
    private fun updateBatteryImage(batteryLevel: Int){

        if(batteryLevel in 76..100){
            batteryViewLevel.setImageResource(R.drawable.ic_battery_full)

        }
        else if(batteryLevel in 51..75){
            batteryViewLevel.setImageResource(R.drawable.ic_threefourth)
        }
        else if (batteryLevel in 26..50){
            batteryViewLevel.setImageResource(R.drawable.ic_battery_half)
        }
        else if(batteryLevel in 0..25){
            batteryViewLevel.setImageResource(R.drawable.ic_battery_low)
        }
        else {
            batteryViewLevel.setImageResource(R.drawable.ic_battery_full)
        }
    }

//    private fun updateDropDownAdapter() {

//        if (mDropDownAdapter != null) {
//            ackList.clear()
//            for (i in ackAlertMapList) {
//                ackList.add(i.value)
//            }


//            Log.i("ALARM_LIST", "Size = ${ackList.size}")
//
//            if (ackList.isNotEmpty()) {
//                for (i in ackList) {
//                    setStandStateWithId(i.message.toString(), ackList.indexOf(i))
//                    Log.i("ALARM_LIST", i.message.toString())
//                }
//            } else {
//                setStandStateWithId("", 0)
//            }
//
//            mDropDownAdapter?.notifyDataSetChanged()

            //update the adapter of the BufferFragment Alarm recycler

//            validateErrorBar()
//        }
//    }




    private fun validateErrorBar() {

        val isAlarmPlaying = mediaPlayer?.isRunning == true

        if (mDashBoardViewModel.alarms.value?.isNotEmpty() == true) {
            layoutErrorBar?.setBackgroundResource(R.color.ack_red)
            mDashBoardViewModel.alarms.value?.last()?.let { tvErrorBar?.text = it.message  }

            if (mDashBoardViewModel.alarms.value?.any { a -> a.priority == AlarmType.ALARM_HIGH_LEVEL } == true) {
                // IN CASE OF HIGH PRIORITY
                if (isAlarmPlaying) {
                    if (currentAlarmState == AlarmState.LOW) {
                        mediaPlayer?.apply {
                            stop()
                            release()
                            playAlarm(AlarmType.ALARM_HIGH_LEVEL)
                            currentAlarmState = AlarmState.HIGH
                        }
                    } else return
                } else {
                    playAlarm(AlarmType.ALARM_HIGH_LEVEL)
                    currentAlarmState = AlarmState.HIGH
                }
            }
            else {
                // IN CASE OF LOW PRIORITY
                if (isAlarmPlaying) {
                    if (currentAlarmState == AlarmState.HIGH) {
                        mediaPlayer?.apply {
                            stop()
                            release()

                            if (!isAlarmMuted) {
                                playAlarm(AlarmType.ALARM_LOW_LEVEL)
                                currentAlarmState = AlarmState.LOW
                            }
                        }
                    } else return
                } else {
                    if (!isAlarmMuted) {
                        playAlarm(AlarmType.ALARM_LOW_LEVEL)
                        currentAlarmState = AlarmState.LOW
                    }
                }
            }
        } else {
            // NO ALARM SHOULD PLAY WHEN COUNT = 0
            layoutErrorBar?.setBackgroundResource(R.drawable.background_black)
            tvErrorBar?.text = ""
            mediaPlayer?.takeIf { it.isRunning }?.apply {
                stop()
                release()
                currentAlarmState = null
            }

        }
    }

    private fun playAlarm(priority: AlarmType) {

        currentPriority = priority
        onPlayAlarm()
        // onCheckLoudness()
    }


//    private fun sortAlarmByPriority() {
//
//
//        val entries: List<Map.Entry<String, AlarmModel>> =
//            ArrayList<Map.Entry<String, AlarmModel>>(ackAlertMapList.entries)
//        //sorting the list by the rule provided by the comparator which is used passed as a second parameter
//        Collections.sort(entries, PriorityComparator())
//
//        val sortedMap: MutableMap<String, AlarmModel> = LinkedHashMap()
//
//        for ((key, value) in entries) {
//            sortedMap.put(key, value)
//        }
//        ackAlertMapList = sortedMap
//        Log.i("SHORT_LIST", ackAlertMapList.entries.toString())
//
//
//    }

    private fun addAlarm(alarmKey: String, alarmMsg: String) {

        AlarmModel(
            alarmMsg,
            alarmKey,
            getCurrentDateTime(),
        ).let {
            alarmsMap.put(alarmKey, it)
            mDashBoardViewModel.addAlarm(it)
            mAlarmViewModel.addAlarm(it.toDBModel())
            uploadAlarm(it)
        }


    }

    private fun uploadAlarm(alarmModel: AlarmModel){
        if(alarmModel.priority.run { this == AlarmType.ALARM_MEDIUM_LEVEL && this == AlarmType.ALARM_HIGH_LEVEL }){
            CoroutineScope(Dispatchers.IO).launch {
                ServerLogger.sendAlarm(this@DashBoardActivity, alarmModel.code)
            }
        }
    }

    private fun removeAlarm(ackValue: String){
        alarmsMap.getOrDefault(ackValue, null)?.let {
            mDashBoardViewModel.removeAlarm(it)
            alarmsMap.remove(ackValue)
//                            mDashBoardViewModel.ackListViewModel.value?.removeIf{ this == it.code }
        }

    }

//    private fun colorPriority(code: Int?): Int {
//        return when (code) {
//            ALARM_MEDIUM_LEVEL -> {
//                R.color.ack_yellow
//            }
//            ALARM_HIGH_LEVEL -> {
//                R.color.ack_red
//            }
//
//            else -> R.color.ack_green
//        }
//
//    }


//    private val calibrationProgressTimer: CalibrationCountDownTimer = CalibrationCountDownTimer(10000, 100)
    private var settingsCountDownTimer: SettingsCountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ServerLogger.i(this, "On Create")
        Log.i("ACTIVITY_LIFECYCLE", "ON_CREATE")
        Log.i("ACKVALUE",MessageFactory.getAckMessage(this,"ACK0341"))
        // init preference manager
        prefManager = PreferenceManager(this@DashBoardActivity)
        breathDataReader.start()
        dataLogger = DataLogger(this@DashBoardActivity)
        mediaPlayer = CustomMediaPlayer()

        mAlarmViewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        mEventViewModel = ViewModelProvider(this)[EventViewModel::class.java]


        mDashBoardViewModel = ViewModelProvider(this)[DashBoardViewModel::class.java]
        mDashBoardViewModel.select(2)
        mDashBoardViewModel.selected.observe(this, androidx.lifecycle.Observer { it->
            if (it==4){
                mediaPlayer?.takeIf { it.isRunning }?.apply {
                    stop()
                    release()
                }
            }
        })



//        alarmDBModelList = ArrayList<AckAlarmDatabaseModel>()
        mDashBoardViewModel.alarms.observe(this) { validateErrorBar() }


        //observerAdapeterClicListner=this


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        hideSystemUI()
        setContentView(R.layout.activity_dashboard)
        alarmLayoutPosition = buttonModeType.width + patientLayout.width

        val scheduleDataBaseClean: WorkRequest = PeriodicWorkRequestBuilder<ScheduleDataBaseUpdate>(12,TimeUnit.HOURS)
        .build()

        val workManagerAlarm= WorkManager.getInstance(this@DashBoardActivity)
        workManagerAlarm.apply {
            enqueue(scheduleDataBaseClean)
        }


//        setupViews()


        initData()
        initStartVentilationTime()
        renderTopBarViaPreference()
//        swappableLayout.setOnClickListener {
//            swappableLayout.visibility=View.GONE
//        }


        // observedPopUpRecyclerView.last
        ivParamMenuCross.setOnClickListener {
            closeSwappableLayout()
        }

        ivSwappableDown.setOnClickListener { scrollSwappableListDown() }

        ivSwappableUp.setOnClickListener { scrollSwappableListUp() }

//        setupList()


        imageViewAlarm.setOnClickListener {

            alarmMuteUnMuteVisibility()
        }



        layoutErrorBar?.setOnClickListener(View.OnClickListener {

            showAlarmFragment( Bundle().apply{ putString("fragment_val", "BufferFragment") })
//            if(dropDownView?.isExpanded == true){
//                dropDownView?.collapseDropDown()
//                Log.i("collapse","Collapse")
//                ToastFactory.custom(this,"The header click is working")
//            }else{
//                if(ackList.size>0){
//                    dropDownView?.expandDropDown()
//                    Log.i("collapse", "expand")
//                }
//            }
        })



        Intent(this, UsbService::class.java).also { intent ->
            startService(intent)
        }

        graphicsDialogFragment?.requireDialog()?.setOnDismissListener {
            button_graphics.setBackgroundResource(R.drawable.background_black)
            button_graphics.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }
        val visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE

      //  layoutTempData.visibility = visibility
    }

    private fun scrollSwappableListUp() = scrollSwappableList(null, Direction.UP)
    private fun scrollSwappableListDown() = scrollSwappableList(null, Direction.DOWN)


    // scroll index at top
    private fun scrollSwappableList(index: Int?, direction: Direction) {
        (swappableRecyclerView.getLayoutManager() as? LinearLayoutManager)
            ?.apply {
                val visibleOnsetCount = index
                    ?: (findLastCompletelyVisibleItemPosition() - findFirstCompletelyVisibleItemPosition())


                val expectedPos = when (direction) {
                    Direction.UP -> findFirstCompletelyVisibleItemPosition() - visibleOnsetCount
                    Direction.DOWN -> findLastCompletelyVisibleItemPosition() + visibleOnsetCount
                }

                val lastPossibleIndex = itemCount - 1
                val actualPos = when {
                    expectedPos in 0..lastPossibleIndex -> expectedPos
                    expectedPos > lastPossibleIndex -> lastPossibleIndex
                    else -> 0
                }
                Log.i("SCROLLCHECK", "Scrolling to ACTUAL $actualPos | EXPECTED $expectedPos")
                swappableRecyclerView.smoothScrollToPosition(actualPos)
            }
    }

    private fun renderTopBarViaPreference() {
        prefManager?.apply {

            if (Gender.TYPE_MALE == readGender()) {
                imageViewGender.setImageResource(R.drawable.ic_male_select)
            } else imageViewGender.setImageResource(R.drawable.ic_female_select)

            ageView.text = readAge().toInt().toString() + " years"
            heightView.text = readBodyHeight().toInt().toString() + " cms"
            weightView.text = readBodyWeight().toInt().toString() + " kg"

        }

    }

    private fun initStartVentilationTime() {
        val liveData: MutableLiveData<String> = MutableLiveData()
        customCountDownTimer = CustomCountDownTimer(liveData)
        customCountDownTimer?.start(1000) //Epoch timestamp
        customCountDownTimer?.mutableLiveData?.observe(
            this,
            androidx.lifecycle.Observer { counterState ->
                counterState?.let {

                    controlDialogFragment?.takeIf { it.isVisible }?.apply {
                        setTime(counterState, customCountDownTimer!!)


                    }

                    setDateTime(counterState)
                    println(counterState)
                }
            })

    }

    private fun setDateTime(counterState: String) {

        timeView.text = counterState
        dateView.text = AppUtils.getCurrentDate()

    }


    private fun alarmMute() {

        if (currentAlarmState == AlarmState.LOW) {

            communicationService?.takeIf { isServiceBound }?.apply {
                send(getString(R.string.cmd_vent_mute_alarm_permanent))
            }

            mediaPlayer?.pause()
            imageViewAlarm.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, // Context
                    R.drawable.ic_alarm_stop // Drawable
                )
            )
            val eventDataModel = EventDataModel("Alarm are muted")
            mEventViewModel.addEvent(eventDataModel)
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer?.start()
                imageViewAlarm.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, // Context
                        R.drawable.ic_alarm // Drawable
                    )
                )
                isAlarmMuted = false

            }, (1000 * 60 * 2).toLong())


        } else {
            val eventDataModel = EventDataModel("Alarm are muted")
            mEventViewModel.addEvent(eventDataModel)
            mediaPlayer?.pause()
            communicationService?.takeIf { isServiceBound }?.apply {
                send(getString(R.string.cmd_vent_mute_alarm_temporary))
            }

            imageViewAlarm.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext, // Context
                    R.drawable.ic_alarm_stop // Drawable
                )
            )
            Handler(Looper.getMainLooper()).postDelayed({

                imageViewAlarm.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext, // Context
                        R.drawable.ic_alarm // Drawable
                    )
                )
                isAlarmMuted = false

            }, 2000)
        }

    }


    private fun alarmUnMute() {
        communicationService?.takeIf { isServiceBound }?.apply {
            send(getString(R.string.cmd_vent_unmute_alarm))
        }


        val eventDataModel = EventDataModel("Alarm are unmuted")
        mEventViewModel.addEvent(eventDataModel)
        imageViewAlarm.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext, // Context
                R.drawable.ic_alarm // Drawable
            )
        )
    }


    private fun alarmMuteUnMuteVisibility() {

        if (isAlarmMuted) {
            isAlarmMuted = false
            alarmUnMute()
        } else {
            isAlarmMuted = true
            alarmMute()
        }

    }

//    private fun setupList() {
//
//        for (i in ackAlertMapList) {
//
//            ackList.add(i.value)
//        }
//        Log.i("list_SIZE", ackAlertMapList.size.toString())
//
////        viewActions.selectedStand = 0
////        mDropDownAdapter = DropDownAdapter(viewActions, ackList,this)
////        /*recyclerView?.apply {
////            layoutManager = LinearLayoutManager(this@DashBoardActivity)
////            adapter = mDropDownAdapter
////        }*/
//        for (i in ackList) {
//            setStandStateWithId(i.message.toString(), ackList.indexOf(i))
//        }
//
//    }

//    private fun setStandStateWithId(mesg: String?, standId: Int) {
//        if (standId >= 0 && standId < ackAlertMapList.size) {
//            ackList[standId].message = mesg.toString()
//            mDropDownAdapter?.notifyItemChanged(standId)
//            Log.i("message", "messs1    " + mesg.toString())
//        }
//
//        // Should update currently selected stand wait time as well
//        if (selectedStandId == standId) {
//            tvddErrorPanel?.text = mesg
//
//            Log.i("message", "messs    " + mesg.toString())
//
//        }
//
//    }

//    private fun setupViews() {
//        dropDownView = findViewById<DropDownView>(R.id.button_ventilating)
//        headerView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_header, null, false)
//        expandedView = LayoutInflater.from(this).inflate(R.layout.view_my_drop_down_expanded, null, false)
//        ddErrorPanel = headerView?.findViewById<View>(R.id.layoutErrorPanel) as RelativeLayout
//        tvddErrorPanel = headerView?.findViewById<View>(R.id.tvErrorPanel) as TextView
//        recyclerView = expandedView?.findViewById<View>(R.id.recyclerView) as RecyclerView



//    }


//    private val dropDownListener: DropDownView.DropDownListener = object :
//        DropDownView.DropDownListener {
//
//        override fun onExpandDropDown() {
//            mDropDownAdapter?.notifyDataSetChanged()
//            ObjectAnimator.ofFloat(headerAlarmtext, View.ROTATION.name, 180f).start()
//
//        }
//
//        override fun onCollapseDropDown() {
//            ObjectAnimator.ofFloat(headerAlarmtext, View.ROTATION.name, -180f, 0f).start()
//        }
//    }

//    private val viewActions: DropDownAdapter.ViewActions =
//        object : DropDownAdapter.ViewActions {
//            override fun collapseDropDown() {
//                dropDownView?.collapseDropDown()
//            }
//
//            override fun getStandTitle(standId: Int): String {
//                return if (ackList.isNotEmpty()) {
//                    Log.i("TITLE_CHECK", ackList[standId].message)
//                    val title = ackList[standId].message
//                    title
//                } else ""
//            }
//
//            override fun getStandStatus(standId: Int): String {
//                return if (ackList[standId].message != null) ackList[standId].message.toString() else ""
//            }
//
//            override var selectedStand: Int
//                get() = selectedStandId
//                set(standId) {
//                    tvddErrorPanel?.text = ""
//                    selectedStandId = standId
//                }
//        }


    private fun initData() {

        createAndRenderAllTiles()
        SciChartBuilder.init(this)
        setUpDivideQuadFragment()
        //onSelectTrioGraph()
        setUpOnClickListener()
        doBindService()

    }

    override fun onStart() {
        super.onStart()
        //ServerLogger.i(this, "On Start")

        Log.i("ACTIVITY_LIFECYCLE", "ON_START")

        // Registering receiver for listening to USB service
        registerReceiver(gattReceiver, getIntentFilter())

    }
    var dataStoreModelAvg=DataStoreModel()
    override fun onResume() {
        super.onResume()

        //ServerLogger.i(this, "On Resume")

        Log.i("ACTIVITY_LIFECYCLE", "ON_RESUME $javaClass")
        mDashBoardViewModel.breathData.observe(this@DashBoardActivity, androidx.lifecycle.Observer {
            breathCacheList.add(it)
        })
//        headerAlarmtext?.rotation = if (dropDownView?.isExpanded == true) 180f else 0f
    }




    /*fun avgDataStoreModels( dsList: List<DataStoreModel>): DataStoreModel {

        // Solution 2
        return dsList.reduce { acc, model -> acc + model }.apply {
            val size = dsList.size
            this.pressure = pressure / size
            this.volume = volume / size
            this.rr = rr / size
            this.fio2 = fio2 / size
            this.mve = mve / size
            this.vte = vte / size
            this.leak = leak / size
            this.peep = peep / size
            this.ieRatio = ieRatio / size
        }
    }*/

    override fun onPause() {
        super.onPause()
        Log.i("ACTIVITY_LIFECYCLE", "ON_PAUSE $javaClass")
    }

    override fun onStop() {
        // Unregistering receiver for listening to USB service
        Log.i("ACTIVITY_LIFECYCLE", "ON_STOP $javaClass")

//        for (j in alarmDBModelList) {
//
//            mAlarmDBModel = AlarmDBModel(j.message,j.ackCode, j.startDateTime, getCurrentDateTime())
//            mAlarmViewModel.addAlarm(mAlarmDBModel)
//        }
//        alarmDBModelList.clear()
        unregisterReceiver(gattReceiver)

        super.onStop()

    }

    override fun onDestroy() {
        settingsCountDownTimer?.safeStop()
        settingsCountDownTimer = null
        customCountDownTimer?.stop()
        stopPinging()
        doUnbindService()
        //  mediaPlayer?.stop()
        super.onDestroy()
    }


    // ClickListener on Buttons
    private fun setUpOnClickListener() {

        imageViewPower.setOnClickListener { showStandbyConfirmation() }

        imageViewShutDown.setOnClickListener { showShutDownConfirmation() }

        buttonModeType.setOnClickListener { showModeFragment() }

        button_monitoring.setOnClickListener { showMonitoringFragment() }

        button_graphics.setOnClickListener { showLayoutFragment() }

        button_maneuvers.setOnClickListener {
            isFromKnob = ""
            showManeuversFragment()
        }

        button_logs.setOnClickListener { showLogsFragment() }

        button_systems.setOnClickListener { showSystemFragment() }

        button_modes.setOnClickListener { showModeFragment() }

        button_controls.setOnClickListener { showControlFragment() }

        button_alarms.setOnClickListener {
            showAlarmFragment(Bundle().apply{ putString("fragment_val", "FromButton") })
        }

        button_loop.setOnClickListener {
            button_loop.setBackgroundResource(R.drawable.background_white_border_white)
            button_loop.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
            onSelectDivideTrioGraph()
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    button_loop.setBackgroundResource(R.drawable.background_black_border_black)
                    button_loop.setTextColor(
                        ContextCompat.getColor(
                            this@DashBoardActivity,
                            R.color.dim_grey
                        )
                    )
                    //checkDialogPresence()
                },1000
            )
            hideAllAlertDialogBox()
            hideKnobViews()
            hideAllDialogFragment()
            onSelectDivideTrioGraph()
            checkDialogPresence()

        }
    }


    private fun showAlarmFragment(bundle: Bundle? = null) {
        closeSwappableLayout()
        val isNotEqual = alarmDialogFragment?.arguments?.get("fragment_val") != bundle?.get("fragment_val")
        if (alarmDialogFragment == null || isNotEqual) {
            alarmDialogFragment = AlarmDialogFragment.newInstance(
                heightSize,
                widthSize,
                communicationService,
                this,
                this,
            )
        }

        alarmDialogFragment?.let {

            alarmDialogFragment?.arguments = bundle
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    AlarmDialogFragment.TAG
                )
                    .commitNow()
            }
        }

        checkDialogPresence()
    }

    private fun showStandbyControlFragment() {

        val controlParameters = filterControlParameterViaMode(
            this@DashBoardActivity,
            modeCode,
            getAllControlParameterLists(this@DashBoardActivity, modeCode).flatten()
        )

        val basicParams = controlParameters.getOrNull(0)
        val advancedParams = controlParameters.getOrNull(1)
        val backupParams = controlParameters.getOrNull(2)


        standbyControlFragment = basicParams?.let {
            StandbyControlDialogFragment.newInstance(
                height = heightSize,
                width = widthSize,
                status = false,
                basicParams = it,
                advancedParams = advancedParams,
                backupParams = backupParams,
                closeListener = standbyControlDismissListener,
                basicParameterClickListener = getStandbyParameterClickListener(
                    it,
                    ControlSettingType.BASIC
                ),
                advancedParameterClickListener = getStandbyParameterClickListener(
                    advancedParams,
                    ControlSettingType.ADVANCED
                ),
                backupParameterClickListener = getStandbyParameterClickListener(
                    backupParams,
                    ControlSettingType.BACKUP
                ),
                onStartVentilationListener = startNewVentilationListener
            )
        }


        standbyControlFragment?.let {


            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    "FromDashBoardActivity"
                )
                    .commitNow()
            }

        }

        checkDialogPresence()
    }
    private fun showControlFragment() {
        closeSwappableLayout()
        if (controlDialogFragment == null) {
            controlDialogFragment = ControlDialogFragment.newInstance(
                height = heightSize,
                width = widthSize,
                basicParams = basicControlParameterList.filter { it.isVoid },
                // ensure that no black tiles are sent
                advancedParams = advancedControlParameterList.filter { it.isVoid },
                backupParams = backupControlParameterList.filter { it.isVoid },  // ensure that no black tiles are sent
                closeListener = controlDismissListener,
                basicParameterClickListener = onBasicParameterClickListener,
                advancedParameterClickListener = onAdvancedParameterClickListener,
                backupParameterClickListener = onBackupParameterClickListener,
                onToggledListener = { _, isOn -> communicationService?.sendConfigurationToVentilator() }
            )
        }

        controlDialogFragment?.also {
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    ControlDialogFragment.TAG
                ).commitNow()
            }
        }

        checkDialogPresence()
    }

    private fun showLayoutFragment() {
        closeSwappableLayout()
        if (graphicsDialogFragment == null) {
            graphicsDialogFragment = GraphicsDialogFragment(this, this, this)
        }

        graphicsDialogFragment?.let {
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    GraphicsDialogFragment.TAG
                )
                    .commitNow()


            }

        }
        checkDialogPresence()
    }

    private fun showManeuversFragment() {
        closeSwappableLayout()
        if (maneuversDialogFragment == null) {
            maneuversDialogFragment = ManeuversDialogFragment.newInstance(
                heightSize,
                widthSize,
                isFromKnob,
                this,
                communicationService
            )
        }
        maneuversDialogFragment?.let {

            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    ManeuversDialogFragment.TAG
                )
                    .commitNow()
            }

        }
        checkDialogPresence()
    }

    //ToDo:- trends lag integration point
    private fun showLogsFragment() {
        closeSwappableLayout()
        if (logsDialogFragment == null) {
            logsDialogFragment = LogsDialogFragment.newInstance(heightSize, widthSize, this)

        }

        logsDialogFragment?.let {
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    LogsDialogFragment.TAG
                )
                    .commitNow()


            }


        }
        checkDialogPresence()
    }

    private fun showSystemFragment() {
        closeSwappableLayout()
        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            send(resources.getString(R.string.cmd_preop))
            send(resources.getString(R.string.cmd_calibration_flow))
            send(resources.getString(R.string.cmd_calibration_presser))
            send(resources.getString(R.string.cmd_calibration_oxyzen))
            send(resources.getString(R.string.cmd_calibration_turbine))
        }

        if (systemDialogFragment == null) {
            systemDialogFragment = SystemDialogFragment.newInstance(
                heightSize,
                widthSize,
                true,
                this,
                this,
                this,
                communicationService
            )
        }

        systemDialogFragment?.let {

            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    TAG
                )
                    .commitNow()

            }
        }
        checkDialogPresence()
    }
    //ToDo:- passing off the observed tiles list
    private fun showMonitoringFragment() {
        closeSwappableLayout()
        if (monitoringDialogFragment == null) {
            monitoringDialogFragment = MonitoringDialogFragment.newInstance(
                heightSize,
                widthSize,
                observedValueList,
                observedValueSpHOList,
                this
            )
        }

        monitoringDialogFragment?.let {


            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    MonitoringDialogFragment.TAG
                )
                    .commitNow()


            }


        }
        checkDialogPresence()
    }

    private fun showModeFragment() {
        closeSwappableLayout()
        if (modeDialogFragment == null) {
            modeDialogFragment = ModeDialogFragment.newInstance(
                heightSize,
                widthSize,
                true,
                onModeConfirmListener = modeConfirmListener,
                this
            )
        }

        modeDialogFragment?.let {

            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboardFragment_nav_container,
                    it,
                    ModeDialogFragment.TAG
                ).commitNow()
            }
        }

        checkDialogPresence()

    }

    private fun showShutDownConfirmation() {
        var dialogMessage=""
        if (shutDownConfirmDialog?.isShowing() == true) return;

        if (ackVisibilities[5]){
            dialogMessage=this.getString(R.string.patient_disconnected_shutdown)
            shutDownConfirmDialog = DialogBoxFactory.showShutDownStatusDialog(dialogMessage,this) { ->
                showShutDownProgress("Shut Down in Progress")
                val eventDataModel = EventDataModel("ShutDown Request")
                mEventViewModel.addEvent(eventDataModel)
                sendShutDownCommandToVentilator()
            }
        } else {
            dialogMessage=this.getString(R.string.patient_connected_shutdown)
            shutDownConfirmDialog = DialogBoxFactory.showShutDownStatusDialog(dialogMessage,this) { ->
                showShutDownProgress("Shut Down in Progress")
                val eventDataModel = EventDataModel("ShutDown Request")
                mEventViewModel.addEvent(eventDataModel)
                sendShutDownCommandToVentilator()
            }

        }
    }

    private fun showShutDownProgress(txt: String) {
        if (shutDownProgress == null) {
            shutDownProgress = ProgressDialog(this)
            shutDownProgress?.setCancelable(false)
        }

        shutDownProgress?.setMessage(txt)
        shutDownProgress?.show()
    }

    private fun hideShutDownDialog() {
        shutDownConfirmDialog?.takeIf { it.isShowing }?.apply {
            dismiss()
        }
    }

    fun hideShutDownProgress() {
        shutDownProgress?.apply {
            cancel()
        }
    }

    private fun hideShutDownConfirmation() {
        shutDownConfirmDialog?.apply {
            cancel()
        }
    }


    private fun showStandbyConfirmation() {
        var dialogDisplayMessage=""
        if (standByConfirmDialog?.isShowing() == true) return;

        if (ackVisibilities[5] ==true){
            dialogDisplayMessage=this.getString(R.string.patient_disconnected_standby)
            standByConfirmDialog = DialogBoxFactory.showVentilationStatusDialog(dialogDisplayMessage,this) { ->
                showStandByProgress("Initiating Standby")
                val eventDataModel = EventDataModel("Standby Requested")
                mEventViewModel.addEvent(eventDataModel)
                sendStandbyCommandToVentilator()
            }
        } else {
            dialogDisplayMessage=this.getString(R.string.patient_connected_standby)
            standByConfirmDialog = DialogBoxFactory.showVentilationStatusDialog(dialogDisplayMessage,this) { ->
                showStandByProgress("Initiating Standby")
                val eventDataModel = EventDataModel("Standby Requested")
                mEventViewModel.addEvent(eventDataModel)
                sendStandbyCommandToVentilator()
            }
        }
    }



    private fun showStandByProgress(txt: String) {
        if (standByProgress == null) {
            standByProgress = ProgressDialog(this)
            standByProgress?.setCancelable(false)
        }
        standByProgress?.setMessage(txt)
        standByProgress?.show()
    }


    fun hideStandbyProgress() {
        standByProgress?.apply {
            cancel()
        }
    }


    private fun hideStandByConfirmation() {
        standByConfirmDialog?.apply {
            cancel()
        }
    }


    private fun hideStandByDialog() {
        standByConfirmDialog?.takeIf { it.isShowing }?.apply {
            dismiss()
        }

    }

    // Override Methods
    private fun setUpDivideQuadFragment() = replaceGraphFragment(divideQuadGraphFragment)
    override fun onSelectQuadGraph() = replaceGraphFragment(quadGraphFragment)
    override fun onSelectDividePentGraph() = replaceGraphFragment(pentGraphFragment)
    override fun onSelectDuoGraph() = replaceGraphFragment(duoGraphFragment)
    override fun onSelectDivideTrioGraph() = replaceGraphFragment(divideTrioGraphFragment)
    override fun onSelectTrioGraph() = replaceGraphFragment(trioGraphFragment)
    override fun onSelectDivideQuadGraph() = setUpDivideQuadFragment()


    override fun getCurrentLayoutFragment() =
        supportFragmentManager.findFragmentById(R.id.dashboard_nav_container) as? GraphLayoutFragment


    private fun replaceGraphFragment(fragmentGraph: GraphLayoutFragment) {
        Log.i("GRAPH_TAG", "Replace fragment name is ${fragmentGraph.name}")

        currentFragment = fragmentGraph
        if (previousFragment == null || currentFragment != previousFragment) {
            previousFragment = fragmentGraph
            supportFragmentManager.apply {
                beginTransaction().replace(
                    R.id.dashboard_nav_container,
                    fragmentGraph,
                    fragmentGraph.name
                )
                    .commitNow()
            }
            resetGraphCounter()
        }

    }

    private fun resetGraphCounter() {
        pCount = 0
        vCount = 0
        fCount = 0
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        heightSize = dashboard_nav_container.height
//        widthSize = dashboard_nav_container.width
//
//
//        manuverheightSize = manuverfragmentlayout.height
//        manuverwidthSize = manuverfragmentlayout.width

        /* val buttonLayoutParams: LinearLayout.LayoutParams =
             LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
             buttonLayoutParams.setMargins(alarmLayoutPosition!!, 10, alarmLayoutPosition!!, 0)
 */


        Log.i("FOCUSCHECK", "Focus state = $hasFocus")

        hideSystemUI()
        checkDialogPresence()


        if (knobDialog?.isVisible == false) normaliseParameterTiles()

//        knobDialog?.apply {
//            if (isVisible || !isVisible) {
//                hideSystemUI()
//            }
//        }
    }

    private fun checkDialogPresence() {

        hideKnobViews()

        isFromKnob = ""



//        if (getCurrentLayoutFragment() is DivideTrioFragmentGraph) {
//            Log.i("LOOPCHECK", "LOOP is selected ")
//            button_loop.apply {
//                setBackgroundResource(R.drawable.background_white_border)
//                setTextColor(
//                    ContextCompat.getColor(
//                        this@DashBoardActivity,
//                        R.color.black
//                    )
//                )
//            }
//        } else {
//            Log.i("LOOPCHECK", "LOOP is not selected ")
//
//            button_loop.apply {
//                setBackgroundResource(R.drawable.background_black)
//                setTextColor(
//                    ContextCompat.getColor(
//                        this@DashBoardActivity,
//                        R.color.dim_grey
//                    )
//                )
//            }
//        }

        val visibleDialogFragment: Fragment? =
            fragmentManager.findFragmentById(R.id.dashboardFragment_nav_container)



        if (visibleDialogFragment is MonitoringDialogFragment) {
            button_monitoring.setBackgroundResource(R.drawable.background_white_border)
            button_monitoring.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            button_monitoring.setBackgroundResource(R.drawable.background_black)
            button_monitoring.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is GraphicsDialogFragment) {
            button_graphics.setBackgroundResource(R.drawable.background_white_border)
            button_graphics.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            button_graphics.setBackgroundResource(R.drawable.background_black)
            button_graphics.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is ManeuversDialogFragment) {
            isFromKnob = ""
            button_maneuvers.setBackgroundResource(R.drawable.background_white_border)
            button_maneuvers.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            isFromKnob = ""
            maneuversDialogFragment = null
            button_maneuvers.setBackgroundResource(R.drawable.background_black)
            button_maneuvers.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is LogsDialogFragment) {
            button_logs.setBackgroundResource(R.drawable.background_white_border)
            button_logs.setTextColor(ContextCompat.getColor(this@DashBoardActivity, R.color.black))
        } else {
            button_logs.setBackgroundResource(R.drawable.background_black)
            button_logs.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is SystemDialogFragment) {
            button_systems.setBackgroundResource(R.drawable.background_white_border)
            button_systems.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            button_systems.setBackgroundResource(R.drawable.background_black)
            button_systems.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }


        if (visibleDialogFragment is ModeDialogFragment) {
            button_modes.setBackgroundResource(R.drawable.background_white_border)
            button_modes.setTextColor(ContextCompat.getColor(this@DashBoardActivity, R.color.black))
        } else {
            button_modes.setBackgroundResource(R.drawable.background_black)
            button_modes.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is ControlDialogFragment || visibleDialogFragment is StandbyControlDialogFragment) {
            button_controls.setBackgroundResource(R.drawable.background_white_border)
            button_controls.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            button_controls.setBackgroundResource(R.drawable.background_black)
            button_controls.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

        if (visibleDialogFragment is AlarmDialogFragment) {
            button_alarms.setBackgroundResource(R.drawable.background_white_border)
            button_alarms.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.black
                )
            )
        } else {
            button_alarms.setBackgroundResource(R.drawable.background_black)
            button_alarms.setTextColor(
                ContextCompat.getColor(
                    this@DashBoardActivity,
                    R.color.dim_grey
                )
            )
        }

//        if (visibleDialogFragment is DivideTrioFragmentGraph) {
//            button_loop.setBackgroundResource(R.drawable.background_white_border)
//            button_loop.setTextColor(
//                ContextCompat.getColor(
//                    this@DashBoardActivity,
//                    R.color.black
//                )
//            )
//        } else {
//            button_loop.setBackgroundResource(R.drawable.background_black)
//            button_loop.setTextColor(
//                ContextCompat.getColor(
//                    this@DashBoardActivity,
//                    R.color.dim_grey
//                )
//            )
//        }

    }

    override fun handleDialogClose() {
        hideSystemUI()
        checkDialogPresence()
        hideKnobViews()
    }


    // Service connection for bound services
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, ibinder: IBinder) {
            isServiceBound = true
            communicationService = (ibinder as CommunicationService.LocalBinder).service
            communicationService?.takeIf { it.isPortsConnected }?.apply {
                makeLog(DashBoardActivity::class.java.simpleName)
                startPinging()
            }

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
        }
    }

    /*
    * This method binds the required services
    * and set the flags to active
    */
    private fun doBindService() {
        if (!isServiceBound) {
            val serviceIntent = Intent(this, UsbService::class.java)
            bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE)
            Log.i("COMM_SERVICE_CHECK", "service connected ")
            isServiceBound = true
        }
    }

    /*
    * Inflating general info tiles and configuring their styles
     */
    private fun createAndRenderAllTiles() {

        // set Observe tiles Side Panel Monitoring -> General Fragment
        // and set Observe titles Side Panel SpO2 -> General Fragment
        prefManager?.readVentilationMode()?.apply {
            createObservedParameterList(this)
            renderObservedDataTiles(this)
        }
        // set Ventilator Parameter
        prefManager?.readVentilationMode()?.apply {
            createControlParameterTiles(this)
            renderControlParameterTiles(this)
            Log.i("MODECHECK", "CODE = $this")

        }

    }

    private fun renderAllTiles() {

        // set Observe tiles Side Panel Monitoring -> General Fragment
        prefManager?.readVentilationMode()?.apply { renderObservedDataTiles(this) }
        // set Ventilator Parameter
        prefManager?.readVentilationMode()?.apply {
            renderControlParameterTiles(this)
        }

    }


    private fun createSecondaryObservedList(containerList: List<ObservedParameterModel>) {
        val defaultSecondaryObservedParameters = listOf<String>(
            LBL_SPO2,
            LBL_HR,
        )

        // perform mapping
        containerList.filter {
            defaultSecondaryObservedParameters.contains(it.label + it.labelSubscript)
        }.apply {
            for (tile in this) {
                observedValueSpHO2Map.put(tile.label + tile.labelSubscript, tile)
            }
        }


    }

    private fun renderSecondaryObservedList() {
        observedValueSpHOList.clear()

        observedValueSpHO2Map.forEach {
            observedValueSpHOList.add(it.value)
        }

        setSecondaryObservedParameterAdapter()


    }


    private fun createObservedParameterList(modeCode: Int) {

        val containerList = arrayListOf<ObservedParameterModel>()

        prefManager?.apply {
            ObservedParameterModel(
                LBL_PIP,
                "",
                getString(R.string.hint_cmH2o),
                "-",
                "",
                readPipLimits()?.get(0)?.toString()!!,
                readPipLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_PEEP,
                "",
                getString(R.string.hint_cmH2o),
                "-",
                "",
                readPeepLimits()?.get(0)?.toString()!!,
                readPeepLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_VTI,
                "",
                getString(R.string.hint_ml),
                "-",
                "",
                readVtiLimits()?.get(0)?.toString()!!,
                readVtiLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_VTE,
                "",
                getString(R.string.hint_ml),
                "-",
                "",
                readVteLimits()?.get(0)?.toString()!!,
                readVteLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_MVE,
                "",
                getString(R.string.hint_litre),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_MVI,
                "",
                getString(R.string.hint_litre),
                "-",
                "",
                readMviLimits()?.get(0)?.toString()!!,
                readMviLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

//            ObservedParameterModel(
//                LBL_PMEAN,
//                "",
//                getString(R.string.hint_cmH2o),
//                "-",
//                "",
//                null,
//                null
//            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_VPEAK_I,
                "",
                getString(R.string.hint_l_min),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_VPEAK_E,
                "",
                getString(R.string.hint_l_min),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_TRIG_FLOW,
                "",
                "",
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_RR,
                "",
                getString(R.string.hint_bpm),
                "-",
                "",
                readRRLimits()?.get(0)?.toString()!!,
                readRRLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_TITOT,
                "",
                "",
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_FIO2,
                "",
                getString(R.string.hint_percentage),
                "-",
                "",
                readFiO2Limits()?.get(0)?.toString()!!,
                readFiO2Limits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }


            ObservedParameterModel(
                LBL_AVERAGE_LEAK,
                "",
                getString(R.string.hint_percentage),
                "-",
                "",
                readLeakLimits()?.get(0)?.toString()!!,
                readLeakLimits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

//        ObservedParameterModel(
//            LBL_LEAK_FLOW,
//            "",
//            getString(R.string.hint_percentage),
//            "-",
//            "",
//            null,
//            null
//        ).apply { containerList.add(this) }

//           ObservedParameterModel(
//               LBL_VLEAK,
//               "",
//               getString(R.string.hint_percentage),
//               "-",
//               "",
//               null,
//               null
//           ).apply { containerList.add(this) }


            ObservedParameterModel(
                LBL_OBSERVED_PLATEAU_PRESSURE,
                "",
                getString(R.string.hint_cmH2o),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }


            // ObservedParameterModel(LBL_VPLAT, "", getString(R.string.hint_sec), "-", "", null, null).apply { containerList.add(this) }
            ObservedParameterModel(
                LBL_MEAN_AIRWAY_PRESSURE,
                "",
                getString(R.string.hint_cmH2o),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_TINSP,
                "",
                getString(R.string.hint_sec),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }


            ObservedParameterModel(
                LBL_TEXPR,
                "",
                getString(R.string.hint_sec),
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

//           ObservedParameterModel(
//               LBL_TRISE,
//               "",
//               getString(R.string.hint_sec),
//               "-",
//               "",
//               null,
//               null
//           ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_TRIGGER,
                "",
                "",
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_DYNAMIC_COMPLIANCE,
                "",
                "",
                "-",
                "",
                null,
                null
            ).apply {
                containerList.add(this)
            }

            ObservedParameterModel(
                LBL_IE_RATIO,
                "",
                "",
                "-",
                "",
                null,
                null
            ).apply { containerList.add(this) }


            ObservedParameterModel(
                LBL_SPO2,
                "",
                getString(R.string.hint_percentage),
                "-",
                "",
                readSpO2Limits()?.get(0)?.toString()!!,
                readSpO2Limits()?.get(1)?.toString()!!
            ).apply { containerList.add(this) }

            ObservedParameterModel(
                LBL_HR,
                "",
                getString(R.string.hint_bpm),
                "-",
                "",
                "0",
                "0"
            ).apply { containerList.add(this) }

        }

        // perform mapping
        containerList.apply {
            for (tile in this) {
                observedValueMap.put(tile.label + tile.labelSubscript, tile)
            }

            createPrimaryObservedDataTiles(modeCode, this)
            createSecondaryObservedList(this)
        }




    }

    private fun renderObservedDataTiles(modeCode: Int) {

        observedValueList.clear()

        observedValueMap.apply {

            val pip = this[LBL_PIP]
            val vti = this[LBL_VTI]
            val vte = this[LBL_VTE]
            val mve = this[LBL_MVE]
            val mvi = this[LBL_MVI]
//            val pMean = this[LBL_PMEAN]
            val vPeakI = this[LBL_VPEAK_I]
            val vPeakE = this[LBL_VPEAK_E]
            val peep = this[LBL_PEEP]
            val rr = this[LBL_RR]
            val tiTot = this[LBL_TITOT]
            val fio2 = this[LBL_FIO2]
            val leak = this[LBL_AVERAGE_LEAK]
//            val leakFlow = this[LBL_LEAK_FLOW]
            val trigFlow = this[LBL_TRIG_FLOW]
            val observedPplat = this[LBL_OBSERVED_PLATEAU_PRESSURE]
            val tInsp = this[LBL_TINSP]
            val tExpire = this[LBL_TEXPR]
            val tRise = this[LBL_TRISE]
            val trigger = this[LBL_TRIGGER]
            val dynamicCompliance = this[LBL_DYNAMIC_COMPLIANCE]
            val ieRatio = this[LBL_IE_RATIO]
            val meanAireWayPressure = this[LBL_MEAN_AIRWAY_PRESSURE]

            when (modeCode) {


                MODE_VCV_CMV -> {

                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this)}
                    vti?.apply { observedValueList.add(this)}
                    tInsp?.apply { observedValueList.add(this)}
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this)}
                    tExpire?.apply { observedValueList.add(this)}
                    peep?.apply { observedValueList.add(this)}
                    ieRatio?.apply { observedValueList.add(this)}
                    fio2?.apply { observedValueList.add(this)}
                    mve?.apply { observedValueList.add(this)}
                    mvi?.apply { observedValueList.add(this)}
                    vPeakI?.apply { observedValueList.add(this)}
                    vPeakE?.apply { observedValueList.add(this)}
                    rr?.apply { observedValueList.add(this)}
                    tiTot?.apply { observedValueList.add(this)}
                    leak?.apply { observedValueList.add(this)}
                    //leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this)}
                    tRise?.apply { observedValueList.add(this)}
                    trigger?.apply { observedValueList.add(this)}
                    dynamicCompliance?.apply { observedValueList.add(this) }
                    meanAireWayPressure?.apply { observedValueList.add(this) }


                }
                MODE_VCV_SIMV -> {

                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this) }
                    vti?.apply { observedValueList.add(this) }
                    tInsp?.apply { observedValueList.add(this) }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this) }
                    tExpire?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this) }
                    ieRatio?.apply { observedValueList.add(this) }
                    fio2?.apply { observedValueList.add(this) }
                    mve?.apply { observedValueList.add(this) }
                    mvi?.apply { observedValueList.add(this) }
                    vPeakI?.apply { observedValueList.add(this) }
                    vPeakE?.apply { observedValueList.add(this) }
                    rr?.apply { observedValueList.add(this) }
                    tiTot?.apply { observedValueList.add(this) }
                    leak?.apply { observedValueList.add(this) }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this) }
                    tRise?.apply { observedValueList.add(this) }
                    trigger?.apply { observedValueList.add(this) }
                    dynamicCompliance?.apply { observedValueList.add(this) }
                    meanAireWayPressure?.apply { observedValueList.add(this) }


                }

                MODE_VCV_ACV -> {

                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this) }
                    vti?.apply { observedValueList.add(this) }
                    tInsp?.apply { observedValueList.add(this) }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this) }
                    tExpire?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this) }
                    ieRatio?.apply { observedValueList.add(this) }
                    fio2?.apply { observedValueList.add(this) }
                    mve?.apply { observedValueList.add(this) }
                    mvi?.apply { observedValueList.add(this) }
                    vPeakI?.apply { observedValueList.add(this) }
                    vPeakE?.apply { observedValueList.add(this) }
                    rr?.apply { observedValueList.add(this) }
                    tiTot?.apply { observedValueList.add(this) }
                    leak?.apply { observedValueList.add(this) }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this) }
                    tRise?.apply { observedValueList.add(this) }
                    trigger?.apply { observedValueList.add(this) }
                    dynamicCompliance?.apply { observedValueList.add(this) }
                    meanAireWayPressure?.apply { observedValueList.add(this) }

                }

                MODE_PC_CMV -> {
                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this) }
                    vti?.apply { observedValueList.add(this) }
                    tInsp?.apply { observedValueList.add(this) }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this) }
                    tExpire?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this) }
                    ieRatio?.apply { observedValueList.add(this) }
                    fio2?.apply { observedValueList.add(this) }
                    mve?.apply { observedValueList.add(this) }
                    mvi?.apply { observedValueList.add(this) }
                    vPeakI?.apply { observedValueList.add(this) }
                    vPeakE?.apply { observedValueList.add(this) }
                    rr?.apply { observedValueList.add(this) }
                    tiTot?.apply { observedValueList.add(this) }
                    leak?.apply { observedValueList.add(this) }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this) }
                    tRise?.apply { observedValueList.add(this) }
                    trigger?.apply { observedValueList.add(this) }
                    dynamicCompliance?.apply { observedValueList.add(this) }
                    meanAireWayPressure?.apply { observedValueList.add(this) }

                }

                MODE_PC_SIMV -> {
                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this) }
                    vti?.apply { observedValueList.add(this) }
                    tInsp?.apply { observedValueList.add(this) }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this) }
                    tExpire?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this) }
                    ieRatio?.apply { observedValueList.add(this) }
                    fio2?.apply { observedValueList.add(this) }
                    mve?.apply { observedValueList.add(this) }
                    mvi?.apply { observedValueList.add(this) }
                    vPeakI?.apply { observedValueList.add(this) }
                    vPeakE?.apply { observedValueList.add(this) }
                    rr?.apply { observedValueList.add(this) }
                    tiTot?.apply { observedValueList.add(this) }
                    leak?.apply { observedValueList.add(this) }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this) }
                    tRise?.apply { observedValueList.add(this) }
                    trigger?.apply { observedValueList.add(this) }
                    dynamicCompliance?.apply { observedValueList.add(this) }
                    meanAireWayPressure?.apply { observedValueList.add(this) }

                }

                MODE_PC_AC -> {
                    pip?.apply { observedValueList.add(this) }
                    observedPplat?.apply { observedValueList.add(this) }
                    vti?.apply { observedValueList.add(this) }
                    tInsp?.apply { observedValueList.add(this) }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this) }
                    tExpire?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this) }
                    ieRatio?.apply { observedValueList.add(this) }
                    fio2?.apply { observedValueList.add(this) }
                    mve?.apply { observedValueList.add(this) }
                    mvi?.apply { observedValueList.add(this) }
                    vPeakI?.apply { observedValueList.add(this) }
                    vPeakE?.apply { observedValueList.add(this) }
                    rr?.apply { observedValueList.add(this) }
                    tiTot?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this)  }
                    tRise?.apply { observedValueList.add(this)  }
                    trigger?.apply { observedValueList.add(this)  }
                    dynamicCompliance?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }

                }

                MODE_PC_PSV -> {
                    pip?.apply { observedValueList.add(this)  }
                    observedPplat?.apply { observedValueList.add(this)  }
                    vti?.apply { observedValueList.add(this)  }
                    tInsp?.apply { observedValueList.add(this)  }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this)  }
                    tExpire?.apply { observedValueList.add(this)  }
                    peep?.apply { observedValueList.add(this)  }
                    ieRatio?.apply { observedValueList.add(this)  }
                    fio2?.apply { observedValueList.add(this)  }
                    mve?.apply { observedValueList.add(this)  }
                    mvi?.apply { observedValueList.add(this)  }
                    vPeakI?.apply { observedValueList.add(this)  }
                    vPeakE?.apply { observedValueList.add(this)  }
                    rr?.apply { observedValueList.add(this)  }
                    tiTot?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this)  }
                    tRise?.apply { observedValueList.add(this)  }
                    trigger?.apply { observedValueList.add(this)  }
                    dynamicCompliance?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }

                }

                MODE_PC_APRV -> {
                    pip?.apply { observedValueList.add(this)  }
                    observedPplat?.apply { observedValueList.add(this)  }
                    vti?.apply { observedValueList.add(this)  }
                    tInsp?.apply { observedValueList.add(this)  }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this)  }
                    tExpire?.apply { observedValueList.add(this)  }
                    peep?.apply { observedValueList.add(this)  }
                    ieRatio?.apply { observedValueList.add(this)  }
                    fio2?.apply { observedValueList.add(this)  }
                    mve?.apply { observedValueList.add(this)  }
                    mvi?.apply { observedValueList.add(this)  }
                    vPeakI?.apply { observedValueList.add(this)  }
                    vPeakE?.apply { observedValueList.add(this)  }
                    rr?.apply { observedValueList.add(this)  }
                    tiTot?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this)  }
                    tRise?.apply { observedValueList.add(this)  }
                    trigger?.apply { observedValueList.add(this)  }
                    dynamicCompliance?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }

                }


                MODE_AUTO_VENTILATION -> {
                    pip?.apply { observedValueList.add(this)  }
                    observedPplat?.apply { observedValueList.add(this)  }
                    vti?.apply { observedValueList.add(this)  }
                    tInsp?.apply { observedValueList.add(this)  }
//                    pMean?.apply { observedValueList.add(this) }
                    vte?.apply { observedValueList.add(this)  }
                    tExpire?.apply { observedValueList.add(this)  }
                    peep?.apply { observedValueList.add(this)  }
                    ieRatio?.apply { observedValueList.add(this)  }
                    fio2?.apply { observedValueList.add(this)  }
                    mve?.apply { observedValueList.add(this)  }
                    mvi?.apply { observedValueList.add(this)  }
                    vPeakI?.apply { observedValueList.add(this)  }
                    vPeakE?.apply { observedValueList.add(this)  }
                    rr?.apply { observedValueList.add(this)  }
                    tiTot?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    // leakFlow?.apply { observedValueList.add(this) }
                    trigFlow?.apply { observedValueList.add(this)  }
                    tRise?.apply { observedValueList.add(this)  }
                    trigger?.apply { observedValueList.add(this)  }
                    dynamicCompliance?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }

                }

                MODE_NIV_BPAP -> {

                    pip?.apply { observedValueList.add(this)  }
                    vti?.apply { observedValueList.add(this)  }
                    tInsp?.apply { observedValueList.add(this)  }
//                    pMean?.apply { observedValueList.add(this) }
                    peep?.apply { observedValueList.add(this)  }
                    fio2?.apply { observedValueList.add(this)  }
                    mvi?.apply { observedValueList.add(this)  }
                    rr?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }

                }

                MODE_NIV_CPAP -> {

                    pip?.apply { observedValueList.add(this)  }
                    vti?.apply { observedValueList.add(this)  }
                    tInsp?.apply { observedValueList.add(this)  }
                    peep?.apply { observedValueList.add(this)  }
                    fio2?.apply { observedValueList.add(this)  }
                    mvi?.apply { observedValueList.add(this)  }
                    rr?.apply { observedValueList.add(this)  }
                    leak?.apply { observedValueList.add(this)  }
                    meanAireWayPressure?.apply { observedValueList.add(this)  }


                }
            }
        }
        observedValueListCopy=observedValueList
        renderPrimaryObservedDataTiles()
        renderSecondaryObservedList()
    }

    /*
    * Inflating data tiles and configuring their styles
    */
    private fun createPrimaryObservedDataTiles(
        modeCode: Int,
        containerList: List<ObservedParameterModel>
    ) {


        val defaultPrimaryObservedParameters = listOf<String>(
            LBL_PIP,
            LBL_PEEP,
            if (getModeCategory(modeCode) == MODE_NIV) LBL_VTI else LBL_VTE,
            LBL_FIO2,
            LBL_RR
        )

        // perform mapping
        containerList.filter {
            defaultPrimaryObservedParameters.contains(it.label + it.labelSubscript)
        }.apply {
            for (tile in this) {
                primaryTileMap.put(tile.label + tile.labelSubscript, tile)
            }
        }

    }
    //ToDo:- current task RenderIssue
    private fun renderPrimaryObservedDataTiles() {

        selectedTiles.clear()

        primaryTileMap.forEach {
            selectedTiles.add(it.value)
        }

        setUpPrimaryObservedParameterAdapter()
    }


    private fun createControlParameterTiles(modeCode: Int) {


        // perform mapping
        getAllControlParameterLists(this, modeCode).apply {
            ventBasicParameterMap.clear()
            ventAdvancedParameterMap.clear()
            ventBackupParameterMap.clear()

            this.getOrNull(0)?.let { basicParams ->
                for (tile in basicParams) {
                    ventBasicParameterMap.put(tile.ventKey.toString(), tile)
                }
            }
            this.getOrNull(1)?.let { advancedParams ->
                for (tile in advancedParams) {
                    ventAdvancedParameterMap.put(tile.ventKey.toString(), tile)
                }
            }

            this.getOrNull(2)?.let { backupParams ->
                for (tile in backupParams) {
                    ventBackupParameterMap.put(tile.ventKey.toString(), tile)
                }
            }

        }


    }

    private fun renderControlParameterTiles(modeCode: Int) {

        basicControlParameterList.clear()
        backupControlParameterList.clear()
        advancedControlParameterList.clear()

        val voidTile = ControlParameterModel(
            LBL_VOID_TILE,
            "",
            "",
            "",
            0.0,
            0.0,
            0.0
        )

        // modifying parameter names according to modes
        ventBasicParameterMap.apply {

            val peep = this[LBL_PEEP]?.apply { this.title = getString(R.string.peep) }
            //   val triggerFlow = this[LBL_TRIG_FLOW]?.apply { this.title = getString(R.string.trigger_flow) }
            val pPlat = this[LBL_PPLAT]?.apply { this.title = getString(R.string.pinsp) }
            val vti = this[LBL_VTI]?.apply { this.title = getString(R.string.vti) }
            // val pip = this[LBL_PIP]?.apply { this.title = getString(R.string.plimit) }
            val rr = this[LBL_RR]?.apply { this.title = getString(R.string.respiratory_rate) }
            val tInsp = this[LBL_TINSP]?.apply { this.title = getString(R.string.inhale_time) }
            val fio2 = this[LBL_FIO2]?.apply { this.title = getString(R.string.fio2) }
            val supportPressure = this[LBL_SUPPORT_PRESSURE]?.apply {
                this.title = getString(R.string.support_pressure)
            }
            val slope = this[LBL_SLOPE]?.apply { this.title = getString(R.string.slope) }
            val tLow = this[LBL_TLOW]?.apply { this.title = getString(R.string.tLow) }
            //    val texp = this[LBL_TEXP]?.apply { this.title = getString(R.string.tExp) }

            when (modeCode) {
                MODE_VCV_CMV -> {
                    buttonModeType.text = getString(R.string.hint_vc_cmv)
                }

                MODE_VCV_ACV -> {
                    buttonModeType.text = getString(R.string.hint_vc_cv)
                }

                MODE_VCV_SIMV -> {
                    buttonModeType.text = getString(R.string.hint_vc_simv)
                }

                MODE_PC_CMV -> {
                    buttonModeType.text = getString(R.string.hint_pc_cmv)
                }

                MODE_PC_SIMV -> {
                    buttonModeType.text = getString(R.string.hint_pc_simv)
                }

                MODE_PC_AC -> {
                    buttonModeType.text = getString(R.string.hint_spont)
                }

                MODE_PC_PSV -> {
                    buttonModeType.text = getString(R.string.hint_psv)
                }

                MODE_PC_APRV -> {
                    buttonModeType.text = getString(R.string.hint_pc_aprv)
                    pPlat?.title = getString(R.string.phigh) // PHigh
                    tInsp?.title = getString(R.string.thigh) // THigh
                    peep?.title = getString(R.string.plow) // PLow
                }

                MODE_AUTO_VENTILATION -> {
                    buttonModeType.text = getString(R.string.hint_ai_vent)
                }

                MODE_NIV_BPAP -> {
                    buttonModeType.text = getString(R.string.hint_bpap)
                }

                MODE_NIV_CPAP -> {
                    buttonModeType.text = getString(R.string.hint_cpap)
                }
            }

        }

        filterControlParameterViaMode(
            this@DashBoardActivity,
            modeCode,
            (ventBasicParameterMap + ventAdvancedParameterMap +  ventBackupParameterMap).values.toList()
        )?.apply {
            this.getOrNull(0)?.let { basicControlParameterList = ArrayList(it) }
            this.getOrNull(1)?.let { advancedControlParameterList = ArrayList(it) }
            this.getOrNull(2)?.let { backupControlParameterList = ArrayList(it) }
        }



        if (basicControlParameterList.size < PARAMETER_TILE_COUNT) {
            for (i in 0 until (PARAMETER_TILE_COUNT - basicControlParameterList.size)) voidTile.apply {
                basicControlParameterList.add(
                    this
                )
            }

        }

        setUpControlParameterAdapter()
    }

    private fun renderStandbyControlParameterTilesViaPreference() {

        Log.i("STANDBYCONTROL_CHECK", "Render standby control params via prefs")

        standbyControlFragment?.let {
            prefManager?.apply {
                Log.i("PRefValues","Check Values on View")
                it.updateBasicParameterValue(LBL_PEEP, readPEEP().toInt().toString())
                //it.updateBasicParameterValue(LBL_TRIG_FLOW, readTrigFlow().toInt().toString())
                it.updateBasicParameterValue(LBL_PPLAT, readPplat().toInt().toString())
                it.updateBasicParameterValue(LBL_VTI, readVti().toInt().toString())
                //it.updateBasicParameterValue(LBL_PIP, readPip().toInt().toString())
                it.updateBasicParameterValue(LBL_RR, readRR().toInt().toString())
                it.updateBasicParameterValue(LBL_TINSP, readTinsp().toString())
                it.updateBasicParameterValue(LBL_FIO2, readFiO2().toInt().toString())
                it.updateBasicParameterValue(LBL_SUPPORT_PRESSURE, readSupportPressure().toInt().toString())
                it.updateBasicParameterValue(LBL_SLOPE, readSlope().toInt().toString())
                it.updateBasicParameterValue(LBL_TLOW, readTlow().toString())
                //it.updateBasicParameterValue(LBL_TEXP, readTexp().toInt().toString())
                // ADVANCED SETTINGS
                it.updateAdvancedParameterValue(LBL_PIP, readPip().toInt().toString())
                it.updateAdvancedParameterValue(LBL_TEXP, readTexp().toInt().toString())
                it.updateAdvancedParameterValue(LBL_TRIG_FLOW, readTrigFlow().toInt().toString())

                // BACKUP SETTINGS
                it.updateBackupParameterValue(LBL_APNEA_RR, readRRApnea().toInt().toString())
                it.updateBackupParameterValue(LBL_APNEA_VT, readVtApnea().toInt().toString())
                it.updateBackupParameterValue(LBL_TAPNEA, readTApnea().toString())
                it.updateBackupParameterValue(LBL_APNEA_TRIG_FLOW, readTrigFlowApnea().toInt().toString())
            }
        }
    }

    private fun renderControlParameterTilesViaPreference() {

        prefManager?.apply {
            updateVentParameterValue(LBL_PEEP, readPEEP().toInt().toString())
            updateVentParameterValue(LBL_TRIG_FLOW, readTrigFlow().toInt().toString())
            updateVentParameterValue(LBL_PPLAT, readPplat().toInt().toString())
            updateVentParameterValue(LBL_VTI, readVti().toInt().toString())
            updateVentParameterValue(LBL_PIP, readPip().toInt().toString())
            updateVentParameterValue(LBL_RR, readRR().toInt().toString())
            updateVentParameterValue(LBL_TINSP, readTinsp().toString())
            updateVentParameterValue(LBL_FIO2, readFiO2().toInt().toString())
            updateVentParameterValue(LBL_SUPPORT_PRESSURE, readSupportPressure().toInt().toString())
            updateVentParameterValue(LBL_SLOPE, readSlope().toInt().toString())
            updateVentParameterValue(LBL_TLOW, readTlow().toString())
            updateVentParameterValue(LBL_TEXP, readTexp().toInt().toString())

            updateVentBackupParameterValue(LBL_APNEA_RR, readRRApnea().toInt().toString())
            updateVentBackupParameterValue(LBL_APNEA_VT, readVtApnea().toInt().toString())
            updateVentBackupParameterValue(LBL_TAPNEA, readTApnea().toString())
            updateVentBackupParameterValue(LBL_APNEA_TRIG_FLOW, readTrigFlowApnea().toInt().toString())
        }
    }


    // RecyclerView Set value
    private fun setUpControlParameterAdapter() {

        val primaryControlParameters = try {
            ArrayList(basicControlParameterList.subList(0, PARAMETER_TILE_COUNT))
        } catch (e: Exception) {
            Log.i(
                "PRIMARYTILE_CHECK",
                "Invalid size (size required = $PARAMETER_TILE_COUNT | current size = ${basicControlParameterList.size}"
            )
            basicControlParameterList
        }


        primaryControlParameterAdapter =
            PrimaryControlParameterAdapter(this, primaryControlParameters, onBasicParameterClickListener)
        recyclerViewSetValue?.apply {
            layoutManager =
                LinearLayoutManager(this@DashBoardActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = primaryControlParameterAdapter
            isNestedScrollingEnabled = false
        }


    }

    //ToDo:- current task RecyclerView Primary Observe value
    private fun setUpPrimaryObservedParameterAdapter() {
        primaryObservedParameterAdapter = PrimaryObservedParameterAdapter(this, selectedTiles, this)
        recyclerViewObserveValue?.apply {
            layoutManager = LinearLayoutManager(this@DashBoardActivity)
            adapter = primaryObservedParameterAdapter

            isNestedScrollingEnabled = false
        }
    }

    // RecyclerView Secondary Observe value
    private fun setSecondaryObservedParameterAdapter() {

        secondaryObservedParameterAdapter =
            SecondaryObservedParameterAdapter(this, observedValueSpHOList)


        observeValueRecyclerView?.apply {
            layoutManager =
                LinearLayoutManager(this@DashBoardActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = secondaryObservedParameterAdapter
            isNestedScrollingEnabled = false
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
                Log.i("COMM_SERVICE_CHECK", "service disconnected ")
                isServiceBound = false
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()

            }
        }
    }

    /*
    * This methods start pinging to ventilator
    */
    private fun startPinging() {
        if (communicationService != null) {
            if (pingingTask == null) pingingTask = PingingTask(communicationService)
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

    /*
     * This will split the map data into co-ordinates
     * and render them on REALTIME graph
     */
    private fun setDataOnScreen(dataMap: Map<String, Map<String, String>>?) {

        dataMap?.takeIf { it.isNotEmpty() }?.apply {
            val lbl = this.keys.iterator().next()
            this[lbl]?.apply {
                updateData(lbl, this)
            }
        }
    }


    // Define Variable
    private var cacheTrigger: Float? = null
    private var isCmvActive = false
    private var isNivActive = false
    private var cachedVolume: Float? = null
    private var cachedPip: Float? = null
    private var cachedVti: Float? = null
    private var cachedRR: Float? = null
    private var cachedMvi: Float? = null
    private var cachedMve: Float? = null
    private var cachedFiO2: Float? = null
    private var cachedVte: Float? = null
    private var cachedPeep: Float? = null
    private var cachedLeak: Float? = null
    private var cachedIERatio: Float? = null
    private var cachedRawVolume: Float? = null
    private var cachedTinsp : Float? = null
    private var cachedTexp : Float? = null
    private var cachedMeanAirwayPressure : Float? = null
    private var isEndOfExhalationOccured: Boolean = false


    private fun updateData(label: String, map: Map<String, String>) {

        var pressure: Float? = getMapValueFromLabel(map, RaspiParser.DATA_PRESSURE)
        val flow: Float? = getMapValueFromLabel(map, RaspiParser.DATA_FLOW)
        var volume: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VOLUME)

        val pip: Float? = getMapValueFromLabel(map, RaspiParser.DATA_PIP)
        val vti: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VTI)
        val vte: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VTE)
        val mve: Float? = getMapValueFromLabel(map, RaspiParser.DATA_MVE)
        val mvi: Float? = getMapValueFromLabel(map, RaspiParser.DATA_MVI)
        val pMean: Float? = getMapValueFromLabel(map, RaspiParser.DATA_PMEAN)
        val vPeakI: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VPEAK_I)
        val vPeakE: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VPEAK_E)
        var peep: Float? = getMapValueFromLabel(map, RaspiParser.DATA_PEEP)
        val rr: Float? = getMapValueFromLabel(map, RaspiParser.DATA_RR)
        val tiTot: Float? = getMapValueFromLabel(map, RaspiParser.DATA_TITOT)
        var fio2: Float? = getMapValueFromLabel(map, RaspiParser.DATA_FIO2)
        val leak: Float? = getMapValueFromLabel(map, RaspiParser.DATA_LEAK)
//        val leakFlow: Float? = getMapValueFromLabel(map, RaspiParser.DATA_LEAK_FLOW)
        val volLeak: Float? = getMapValueFromLabel(map, RaspiParser.DATA_VOLUME_LEAK)
        var trigFlow: Float? = getMapValueFromLabel(map, RaspiParser.DATA_TRIGFLOW)
        val observedPplat: Float? = getMapValueFromLabel(map, RaspiParser.DATA_PLATEAU_PRESSURE)
        val meanAirwayPressure: Float? = getMapValueFromLabel(map, RaspiParser.DATA_MEAN_AIRWAY_PRESSURE)?.also {
            cachedMeanAirwayPressure = it
        }
        val tInsp: Float? = getMapValueFromLabel(map, RaspiParser.DATA_INSPIRE_TIME)?.also {
            cachedTinsp = it
        }
        val tExpire: Float? = getMapValueFromLabel(map, RaspiParser.DATA_EXPIRE_TIME)?.also {
            cachedTexp = it
        }
        val tRise: Float? = getMapValueFromLabel(map, RaspiParser.DATA_RISE_TIME)
        var trigger: String? = null
        var dynamicCompliance: Float? = null

        // SPO2 Parameters
        val spo2: Float? = getMapValueFromLabel(map, SpO2ParserExtension.DATA_SPO2)
        val heartRate: Float? = getMapValueFromLabel(map, SpO2ParserExtension.DATA_HEARTRATE)

        //ToDo:-check this line out
        val triggerValue = getMapValueFromLabel(map, RaspiParser.DATA_TRIGGER)?.also {
            cacheTrigger = it
        }


        if (triggerValue != null) {
            val isPatientTriggered =
                !isCmvActive && triggerValue.toInt() == TRIGGER_PATIENT
            if (isPatientTriggered) {
                trigger = LABEL_PATIENT
//            ############### APP VALID TRIGGERING ###############
                machineTriggerCount = 0
            } else {
                trigger = LABEL_MACHINE
            }
        }



        Log.i("LABEL CHECK", "Entry mode is $label")
        when (label) {

            SpO2ParserExtension.TYPE_SPO2 -> {
                spo2?.apply { updateSecondaryModelActualValue(LBL_SPO2, this) }
//            ?: run{ if(peep != null) updateSecondaryModelActualValue(LBL_SPO2, 85 + Random.nextInt(10)) }

            }

            SpO2ParserExtension.TYPE_HEARTRATE -> {
                heartRate?.apply { updateSecondaryModelActualValue(LBL_HR, this) }

            }


            // Prefix : A@
            RaspiParser.TYPE_INHALATION -> {
                cachedRawVolume = volume
                cachedVolume = volume
                updatePrimaryModelActualValue(LBL_RESPIRATORY_PHASE, "INSPIRE")

            }
            // Prefix : B@
            RaspiParser.TYPE_END_OF_INHALATION -> {
                cachedPip = pip
                cachedVti = vti
                cachedMvi = mvi
                cachedRawVolume?.apply {
                    updatePrimaryModelActualValue(LBL_RAW_VOLUME, this)

                }

                /* prefManager?.takeIf { getModeCategory(it.readVentilationMode()) == MODE_VCV }?.apply {
                     val setVti: Float = readVti()
                     vti?.takeIf { setVti > 0 }?.apply {
                         val deltaVti = abs(setVti - this) / setVti * 100

                     }

                 }*/


                /*  if (getModeCategory(prefManager?.readVentilationMode()!!) == MODE_VCV) {
                      val setVti: Float? = prefManager?.readVti()
                      if (vti != null && setVti != null && setVti > 0) {
                          val deltaVti = abs(setVti - vti) / setVti * 100
                          vti = if (deltaVti <= VTI_TOLERANCE_PERCENT_THRESHOLD) setVti else vti
                      }
                  }*/
            }
            // Prefix : C@
            TYPE_EXHALATION -> {
                updatePrimaryModelActualValue(LBL_RESPIRATORY_PHASE, "EXPIRE")

                volume = volume?.let {
                    cachedVolume?.let { it1 ->
                        it1 - it
                    } ?: it
                }
                isEndOfExhalationOccured = true
            }

            // Prefix : D@
            RaspiParser.TYPE_END_OF_EXHALATION -> {

                if (peep != null) {
                    peep = if (peep < 0) 0f else peep
                }

                /* CONSTRAINTS for 2.2.13+
                * for set value 21 discard any fio2 actual reading
                * else take the lowest value for range 21...60
                 */
//                val setFio2: Float? = prefManager?.readFiO2()
//                if (fio2 != null && setFio2 != null) {
//                    fio2 = if (fio2 >= 23 && abs(setFio2 - fio2) <= FIO2_TOLERANCE_THRESHOLD) setFio2 else fio2
//                }
                cachedRR = rr
                cachedMve = mve
                cachedFiO2 = fio2
                cachedVte = vte
                cachedPeep = peep

                cachedLeak = leak

                // FIO2 coming in D string only
                val model = DataStoreModel()
                cachedPip?.apply {
                    model.pressure = this
                }
                cachedVti?.apply {
                    model.volume = this
                }

                cachedRR?.apply {
                    model.setRR(this)
                }

                cachedFiO2?.apply {
                    model.fiO2 = this
                }

                cachedMve?.apply {
                    model.mve = this
                }

                cachedVte?.apply {
                    model.vte = this
                }

                cachedPeep?.apply {
                    model.peep = this
                }

                cachedLeak?.apply {
                    model.leak = this
                }

                cachedIERatio?.apply {
                    model.ieRatio = this
                }
                //newly added values


                cachedMvi?.apply {
                    model.mvi=this
                }

                cachedTinsp?.apply {
                    model.tinsp=this
                }

                cachedTexp?.apply {
                    model.texp=this
                }

                cachedMeanAirwayPressure?.apply {
                    model.meanAirwayPressure=this
                }

                cacheTrigger?.apply {
                    model.trigger=this
                }

                Log.i("LOG_CHECK", model.toString())
                //ToDo:- Save the value check of the duplicacy
                registerDataLogs(model)
                //mDashBoardViewModel.breathData.value=model

            }
        }

        // OSC - Order sensitive code
        // Rendering of loop graphs
        // After D the first A will re render the charts
        val isRedrawRequired = isEndOfExhalationOccured && RaspiParser.TYPE_INHALATION.equals(label)

        if (pressure != null && volume != null) {
            divideQuadGraphFragment.takeIf { it.isVisible }?.apply {
                addGraphPressureVolumeData( volume, pressure, isRedrawRequired)
            }

            divideTrioGraphFragment.takeIf { it.isVisible }?.apply {
                addGraphPressureVolumeData( volume, pressure, isRedrawRequired)
            }
            pentGraphFragment.takeIf{it.isVisible}?.apply {
                addGraphPressureVolumeData(pressure, volume, isRedrawRequired)
            }

        }





        if (flow != null && volume != null) {
//            divideTrioGraphFragment.takeIf { it.isVisible }?.apply {
//                addGraphFlowVolumeData(flow, volume, isRedrawRequired)
//            }
        }

        if (flow != null && pressure != null) {
            divideTrioGraphFragment.takeIf { it.isVisible }?.apply {
                addGraphFlowPressureData(pressure, flow, isRedrawRequired)
            }
            pentGraphFragment.takeIf { it.isVisible }?.apply {
                addGraphFlowPressureData(flow,pressure,isRedrawRequired)
            }
        }

        // OSC - Order sensitive code
        if (isRedrawRequired) {
            // disable the flag as first TYPE_INHALATION after a TYPE_END_OF_EXHALATION is encountered
            isEndOfExhalationOccured = false
        }


        // updating graphs
        pressure?.apply {
            Log.i("DIVIDE_PRESSURE", pressure.toString())
            pressure = if (this < 0) 0f else this
            addPressureGraphEntry(this, pCount++)
//            pCount +=  PressureChartFragment.X_AXIS_LIMIT / GraphFragment.DEFAULT_X_AXIS_LIMIT
//            Random.nextInt(15, 80).apply {
//                Log.i("DIVIDE_PRESSURE", "Random y value = $this")
//                addPressureGraphEntry(this.toFloat(), pCount++)
//            }

        }

        flow?.apply {
            Log.i("DIVIDE_FLOW", flow.toString())
            addFlowGraphEntry(this, fCount++)

        }

        volume?.apply {
            Log.i("DIVIDE_VOLUME", volume.toString())
            addVolumeGraphEntry(this, vCount++)
        }


        // ============== updating titles ==============

        pip?.apply { updatePrimaryModelActualValue(LBL_PIP, this) }

        if (isPatientAvailable) {
            vti?.apply { updatePrimaryModelActualValue(LBL_VTI, this) }

            vte?.apply {
                updatePrimaryModelActualValue(LBL_VTE, this.toInt().toString())

                // Software fix of expire volume error
                /*  cachedVti?.takeIf {
                      this > it
                  }?.let {
                      (0.95 * it).toFloat()
                  }?.let {
                      updateModelActualValue(LBL_VTE, it.toInt().toString())
                  }*/
            }

            mve?.apply {
                updatePrimaryModelActualValue(LBL_MVE, String.format("%.1f", this))

                // Software fix of expire volume error
                /*  cachedMvi?.takeIf {
                      this > it
                  }?.let {
                      (0.95 * it).toFloat()
                  }?.let {
                      updateModelActualValue(LBL_MVE, String.format("%.1f", it))

                  }*/
            }


            mvi?.apply { updatePrimaryModelActualValue(LBL_MVI, String.format("%.1f", this)) }

        } else {
            updatePrimaryModelActualValue(LBL_VTI, "-")
            updatePrimaryModelActualValue(LBL_VTE, "-")
            updatePrimaryModelActualValue(LBL_MVI, "-")
            updatePrimaryModelActualValue(LBL_MVE, "-")
        }

        pMean?.apply { updatePrimaryModelActualValue(LBL_PMEAN, this) }

        vPeakI?.apply { updatePrimaryModelActualValue(LBL_VPEAK_I, this) }

        vPeakE?.apply { updatePrimaryModelActualValue(LBL_VPEAK_E, this) }

        peep?.apply {
            val setPeep = prefManager?.readPEEP()
            if (setPeep != null) {
                val deviation = abs(this - setPeep)
                // controlling PEEP
                peep = if (setPeep >= 2 && deviation <= THRESHOLD_PEEP_DEVIATION) setPeep else this
            }
            updatePrimaryModelActualValue(LBL_PEEP, this)
        }

        rr?.apply {
            if (this > THRESHOLD_RR_FOR_FLOW_SENSOR) {
                if (++rrCheckCount > RR_THRESHOLD_CYCLE_COUNT)
                    Log.i("RR", "Shooting out of range")
                sendBroadcast(Intent(IntentFactory.ACTION_FLOW_SENSOR_OCCLUSION_DETECTED))
            } else {
                Log.i("RR", "Under range")
                sendBroadcast(Intent(IntentFactory.ACTION_FLOW_SENSOR_OCCLUSION_RESOLVED))
                rrCheckCount = 0

            }
            updatePrimaryModelActualValue(LBL_RR, this)
        }

        if (tiTot != null) {
            updatePrimaryModelActualValue(LBL_TITOT, tiTot.toInt().toString() + " %")
            var ieRatio = "-"
            try {
                if (tiTot != 0f) {
                    val eiRatio: Float = 100 / tiTot - 1
                    ieRatio = "1:" + String.format("%.1f", eiRatio)
                    cachedIERatio = eiRatio
                }
            } catch (e: Exception) {
                cachedIERatio = 0f
                e.printStackTrace()
                Log.i("IERATIO CHECK", "error : " + e.message)
            } finally {
                updatePrimaryModelActualValue(LBL_IE_RATIO, ieRatio)
            }

        }

        fio2?.apply { updatePrimaryModelActualValue(LBL_FIO2, this) }

        leak?.let {
            prefManager?.apply {

                if (readCuffLeakageAlarmStatus()) {
                    val isRRValid = rr != null && rr < THRESHOLD_RR_FOR_CUFF_LEAK
                    val isLeakValid = it < MAX_LEAK_THRESHOLD && it > MIN_LEAK_THRESHOLD
                    if (isLeakValid && isRRValid) {
                        if (++leakCheckCount > LEAK_THRESHOLD_CYCLE_COUNT)
                            Log.i("CUFF_LEAK", "Shooting out of range")
                        sendBroadcast(Intent(IntentFactory.ACTION_CUFF_LEAKAGE_DETECTED))
                    } else {
                        Log.i("CUFF_LEAK", "Under range")
                        sendBroadcast(Intent(IntentFactory.ACTION_CUFF_LEAKAGE_RESOLVED))
                        leakCheckCount = 0
                    }

                } else {
                    Log.i("CUFF_LEAK", "Under range")
                    sendBroadcast(Intent(IntentFactory.ACTION_CUFF_LEAKAGE_RESOLVED))
                    leakCheckCount = 0
                }


                if (readLeakBasedAlarmStatus() && !isNivActive) {
                    if (it >= LEAK_BASED_ALARM_THRESHOLD) {


                        if (++leakBasedAlarmCheckCount > LEAK_BASED_ALARM_THRESHOLD_CYCLE_COUNT)
                            Log.i("LEAK_BASED_ALARM", "Shooting out of range")
                        sendBroadcast(Intent(IntentFactory.ACTION_LEAK_BASED_ALARM_DETECTED))
                    } else {
                        Log.i("LEAK_BASED_ALARM", "Under range")
                        sendBroadcast(Intent(IntentFactory.ACTION_LEAK_BASED_ALARM_RESOLVED))
                        leakBasedAlarmCheckCount = 0
                    }
                } else {
                    Log.i("LEAK_BASED_ALARM", "Under range")
                    sendBroadcast(Intent(IntentFactory.ACTION_LEAK_BASED_ALARM_RESOLVED))
                    leakBasedAlarmCheckCount = 0
                }

                /*
             HIGH LEAK VTI MAY BE INACCURACY ALARM :
             Condition :  Leak should be greater than or equal to 90%
             */

                if (it >= HIGH_LEAK_INACCURACY_ALARM_THRESHOLD) {
                    if (++highLeakInaccuracy > HIGH_LEAK_INACCURACY_THRESHOLD_CYCLE_COUNT)
                        Log.i("LEAK_INACCURACY_ALARM", "Shooting out of range")
                    sendBroadcast(Intent(IntentFactory.ACTION_HIGH_LEAK_INACCURACY_DETECTED))
                } else {
                    Log.i("LEAK_INACCURACY_ALARM", "Under range")
                    sendBroadcast(Intent(IntentFactory.ACTION_HIGH_LEAK_INACCURACY_RESOLVED))
                    highLeakInaccuracy = 0
                }
                updatePrimaryModelActualValue(LBL_AVERAGE_LEAK, it)
                lastAvgLeak = it

            }
        }

//        leakFlow?.apply { updateModelActualValue(LBL_LEAK_FLOW, this) }

        volLeak?.apply { updatePrimaryModelActualValue(LBL_VLEAK, this) }

        trigFlow?.apply {
            trigFlow = if (this >= 0) 0f else this
            updatePrimaryModelActualValue(LBL_TRIG_FLOW, this)
        }

        observedPplat?.apply { updatePrimaryModelActualValue(LBL_OBSERVED_PLATEAU_PRESSURE, this) }

        meanAirwayPressure?.apply {
            updatePrimaryModelActualValue(
                LBL_MEAN_AIRWAY_PRESSURE,
                String.format("%.1f", this)
            )
        }

        tInsp?.apply { updatePrimaryModelActualValue(LBL_TINSP, String.format("%.1f", this)) }

        tExpire?.apply { updatePrimaryModelActualValue(LBL_TEXPR, String.format("%.1f", this)) }

        tRise?.apply { updatePrimaryModelActualValue(LBL_TRISE, String.format("%.1f", this)) }

        if (trigger != null && (tbCount++) % TRIGG_BALANCE_COUNT == 0) {
            updatePrimaryModelActualValue(LBL_TRIGGER, trigger)
        }


        if (peep != null && cachedPip != null && cachedPip!! > peep!!) {
            dynamicCompliance = cachedVti!! / (cachedPip!! - peep!!)
        }
        Log.d("ackvisibilitycheck",ackVisibilities[5001].toString())
        val check= ackVisibilities[5001].toString()
        if (ackVisibilities[5001]) {
            // Maneuvers specific block

            observedPplat?.apply {
                var staticCompliance: Float? = null
                try {
                    cachedPeep?.let { it1 ->
                        val pressureVariation = observedPplat - it1
                        staticCompliance = (vti)?.div(pressureVariation)

                        expiratoryInspiratoryFragmentDialog =
                            ExpiratoryInspiratoryFragmentDialog.newInstance(
                                observedPplat,
                                staticCompliance,
                                null,
                                true
                            )
                        expiratoryInspiratoryFragmentDialog?.show(
                            supportFragmentManager,
                            ExpiratoryInspiratoryFragmentDialog.TAG
                        )

                        if (expiratoryInspiratoryFragmentDialog != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                expiratoryInspiratoryFragmentDialog?.dismiss()
                            }, 30000)
                        }
                        ackVisibilities[5001] = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

        }
        Log.d("ackflow",ackVisibilities[5002].toString())
        if (ackVisibilities[5002]) {

            cachedPeep?.let {
                expiratoryInspiratoryFragmentDialog =
                    ExpiratoryInspiratoryFragmentDialog.newInstance(
                        null,
                        null,
                        cachedPeep,
                        false

                    )

                expiratoryInspiratoryFragmentDialog?.show(
                    supportFragmentManager,
                    ExpiratoryInspiratoryFragmentDialog.TAG
                )
                ackVisibilities[5002] = false

            }


        }

        dynamicCompliance?.let {
            prefManager?.apply {
                if (readTubeBlockageAlarmStatus()) {
                    // patient should not be disconnected ie leak < 70%
                    lastAvgLeak?.apply {
                        isPatientAvailable = this < MAX_LEAK_THRESHOLD

                    }

                    val isRRValid = rr != null && rr < THRESHOLD_RR_FOR_TUBE_BLOCKAGE

                    if (it < DYNAMIC_COMPLIANCE_THRESHOLD && isPatientAvailable && isRRValid) {
                        if (++complianceCheckCount > COMPLIANCE_THRESHOLD_CYCLE_COUNT)
                            sendBroadcast(Intent(IntentFactory.ACTION_TUBE_BLOCKAGE_DETECTED))
                    } else {
                        sendBroadcast(Intent(IntentFactory.ACTION_TUBE_BLOCKAGE_RESOLVED))
                        complianceCheckCount = 0
                    }
                } else {
                    sendBroadcast(Intent(IntentFactory.ACTION_TUBE_BLOCKAGE_RESOLVED))
                    complianceCheckCount = 0
                }

                updatePrimaryModelActualValue(LBL_DYNAMIC_COMPLIANCE, it)

            }

        }


    }


    private fun addVolumeGraphEntry(volume: Float, vCount: Int) {
        when (val graphLayoutFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_container)) {

            is DividePentFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )

            is DivideQuadFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )

            is QuadFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )
            is TrioFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )
            is DuoFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )
            is DivideTrioFragmentGraph -> graphLayoutFragment.addGraphVolumeData(
                vCount,
                volume
            )

            else -> throw UnsupportedOperationException("Invalid fragment type")
        }

        Log.i("VOLUME_GRAPH", volume.toString())
    }

    private fun addFlowGraphEntry(flow: Float, fCount: Int) {
        when (val graphLayoutFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_container)) {

            is DividePentFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount,
                flow
            )
            is DivideQuadFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount,
                flow
            )

            is QuadFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount,
                flow
            )
            is DuoFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount,
                flow
            )

            is TrioFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount,
                flow
            )
            is DivideTrioFragmentGraph -> graphLayoutFragment.addGraphFlowData(
                fCount, flow
            )


            else -> throw UnsupportedOperationException("Invalid fragment type")
        }

        Log.i("FLOW_GRAPH", flow.toString())


    }

    private fun addPressureGraphEntry(pressure: Float, pCount: Int) {


        when (val graphLayoutFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_container)) {

            is DividePentFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )
            is DivideQuadFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )
            is QuadFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )
            is DuoFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )
            is TrioFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )
            is DivideTrioFragmentGraph -> graphLayoutFragment.addGraphPressureData(
                pCount,
                pressure
            )


            else -> {
                Log.i("FRAGMENTTOP", "GraphFragment = " + (graphLayoutFragment == null))
                throw UnsupportedOperationException("Invalid fragment type")
            }
        }

        Log.i("PRESSURE_GRAPH", pressure.toString())

    }

    private fun clearDataSeries() {
        when (val graphLayoutFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_container)) {

            is DividePentFragmentGraph -> graphLayoutFragment.clearSeries()

            is DivideQuadFragmentGraph -> graphLayoutFragment.clearSeries()

            is QuadFragmentGraph -> graphLayoutFragment.clearSeries()

            is DuoFragmentGraph -> graphLayoutFragment.clearSeries()

            is TrioFragmentGraph -> graphLayoutFragment.clearSeries()

            is DivideTrioFragmentGraph -> graphLayoutFragment.clearSeries()

            else -> throw UnsupportedOperationException("Invalid fragment type")
        }


    }


    /*
    * add updated data to tiles
    * Returns the update data model
     */
    private fun updatePrimaryModelActualValue(lbl: String, value: Float?): ObservedParameterModel? {
        return updatePrimaryModelActualValue(lbl, value?.toInt())
    }

    private fun updatePrimaryModelActualValue(lbl: String, value: Int?): ObservedParameterModel? {
        return updatePrimaryModelActualValue(lbl, value.toString())
    }
     //ToDo:- current task where map  data inserted which will be used for the primaryobserved
     // adapter list will be added by
    private fun updatePrimaryModelActualValue(lbl: String, value: String): ObservedParameterModel? {

        //invoked for Range filtration
        try {
            /*
            * Ti/Tot provide string directly
            * In form of ...%
            * Removing % character from the end is required
             */
            val tValue = if (LBL_TITOT == lbl) value.substring(0, value.length - 1) else value
            tValue.toFloatOrNull()?.let { validateRangeLimits(lbl, it) }
        } catch (e: Exception) {
            Log.i(
                "DASHBOARD_ACTIVITY",
                "Error while validating value= $value against software limits"
            )
            e.printStackTrace()
        }

        val model = primaryTileMap.get(lbl)
        if (model != null) {
            // not to update if the value is same
            if (value == model.actualValue) {
                return model
            }
            model.actualValue = value
            primaryTileMap.put(lbl, model)

            selectedTiles.let {
                if (it.contains(model)) {
                    it[it.indexOf(model)] = model
                    primaryObservedParameterAdapter?.notifyItemChanged(it.indexOf(model))

                }
            }


        }

        observedValueUpdate(lbl, value)

        return model
    }

    private fun updateSecondaryModelActualValue(lbl: String, value: Float?): ObservedParameterModel? {
        return updateSecondaryModelActualValue(lbl, value?.toInt())
    }

    private fun updateSecondaryModelActualValue(lbl: String, value: Int?): ObservedParameterModel? {
        return updateSecondaryModelActualValue(lbl, value.toString())
    }

    private fun updateSecondaryModelActualValue(lbl: String, value: String): ObservedParameterModel? {
        val model = observedValueSpHO2Map.get(lbl)
        if (model != null) {
            // not to update if the value is same
            if (value == model.actualValue) {
                return model
            }
            model.actualValue = value
            observedValueSpHO2Map.put(lbl, model)

            observedValueSpHOList.let {
                if (it.contains(model)) {
                    it[it.indexOf(model)] = model
                    secondaryObservedParameterAdapter?.notifyItemChanged(it.indexOf(model))
                }
            }
        }

        observedValueUpdate(lbl, value)

        return model

    }
    //ToDo:- the second iteration of the  General fragment
    private fun observedValueUpdate(lbl: String, value: String): ObservedParameterModel? {

        Log.i("OBSERVEDMAP", "Map is not null")
        val model = observedValueMap.get(lbl)
        if (model != null) {
            // not to update if the value is same
            if (value == model.actualValue) {
                Log.i("OBSERVEDMAP", "Map value is same ")

                return model
            }
            model.actualValue = value
            observedValueMap.put(lbl, model)

            observedValueList.let { it1 ->
                if (it1.contains(model)) {
                    Log.i("OBSERVEDMAP", "Map contains the key")

                    it1[it1.indexOf(model)] = model
                    monitoringDialogFragment?.takeIf { it.isVisible }?.apply {
                        Log.i("OBSERVEDMAP", "Map is now updating")
                        setModeList(it1)
                        primaryObservedParameterAdapter?.notifyDataSetChanged()
                    }
                }
            }


        }
        return model
    }

    private fun updateGraphictoolTip(controlParameterModel: ControlParameterModel) {
        graphicTooltipFragment?.takeIf { it.isVisible }?.updateDataOnView(controlParameterModel)
    }

    private fun updateVentParameterValue(lbl: String, value: String): ControlParameterModel? {

        Log.i("PARAMCHECK", "Updating $lbl at value $value")

        val model = ventBasicParameterMap[lbl]

        if (model != null) {
            // not to update if the value is same
            if (value == model.reading) {
                return model
            }

            model.reading = Configs.supportPrecision(lbl, value)

//            if (lbl.equals(LBL_TINSP) || lbl.equals(LBL_TLOW) || lbl.equals(LBL_TRIG_FLOW)) {
//                Log.i("TIMECHECK", "value of tinsp/tlow = ${value}")
//                try {
//                    model.reading = String.format("%.1f", parseDouble(value))
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//                model.reading = value
//            } else {
//                try {
//                    model.reading = parseDouble(value).toInt().toString()
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//            }
            ventBasicParameterMap.put(lbl, model)

            basicControlParameterList.let {
                if (it.contains(model)) {
                    it[it.indexOf(model)] = model
                    // update list view item wise
                    primaryControlParameterAdapter?.notifyItemChanged(it.indexOf(model))
                } else Log.i("ADAPTER", "Unable to update")
            }

            if (controlDialogFragment?.isVisible == true) {
                controlDialogFragment?.notifyParameterAdapter()
            }


            // show changes in graphic tool tip
            updateGraphictoolTip(model)
        }

        return model
    }
    private fun updateVentAdvancedParameterValue(lbl: String, value: String): ControlParameterModel? {

        Log.i("PARAMCHECK", "Updating advanced $lbl at value $value")

        val model = ventAdvancedParameterMap[lbl]

        if (model != null) {
            // not to update if the value is same
            if (value == model.reading) {
                return model
            }

            model.reading = Configs.supportPrecision(lbl, value)


//            if (lbl.equals(LBL_TINSP ) || (lbl.equals(LBL_TRIG_FLOW))) {
//                Log.i("TIMECHECK", "value of tinsp/trigFlow = ${value}")
//                try {
//                    model.reading = String.format("%.1f", parseDouble(value))
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//                model.reading = value
//            } else {
//                try {
//                    model.reading = parseDouble(value).toInt().toString()
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//            }
            ventAdvancedParameterMap.put(lbl, model)

            advancedControlParameterList.let {
                if (it.contains(model)) {
                    it[it.indexOf(model)] = model
                } else Log.i("ADAPTER", "Unable to update")
            }

            if (controlDialogFragment?.isVisible == true) {
                controlDialogFragment?.notifyParameterAdapter()
            }


            // show changes in graphic tool tip
//            updateGraphictoolTip(model)
        }

        return model
    }



    private fun updateVentBackupParameterValue(lbl: String, value: String): ControlParameterModel? {

        Log.i("PARAMCHECK", "Updating backup $lbl at value $value")

        val model = ventBackupParameterMap[lbl]

        if (model != null) {
            // not to update if the value is same
            if (value == model.reading) {
                return model
            }

            model.reading = Configs.supportPrecision(lbl, value)


//            if (lbl.equals(LBL_APNEA_TRIG_FLOW)) {
//                Log.i("TIMECHECK", "value of tinsp/tlow = ${value}")
//                try {
//                    model.reading = String.format("%.1f", parseDouble(value))
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//                model.reading = value
//            } else {
//                try {
//                    model.reading = parseDouble(value).toInt().toString()
//                } catch (e: Exception) {
//                    model.reading = value
//                    e.printStackTrace()
//                }
//            }
            ventBackupParameterMap.put(lbl, model)

            backupControlParameterList.let {
                if (it.contains(model)) {
                    it[it.indexOf(model)] = model
                } else Log.i("ADAPTER", "Unable to update")
            }

            if (controlDialogFragment?.isVisible == true) {
                controlDialogFragment?.notifyParameterAdapter()
            }


            // show changes in graphic tool tip
//            updateGraphictoolTip(model)
        }

        return model
    }


    private var cachedPIPAlarmState = false
    private var cachedVTEAlarmState = false
    private var cachedRRAlarmState = false
    private var cachedPEEPAlarmState = false
    private var cachedMVIAlarmState = false
    private var cachedTiTOTAlarmState = false
    private var cachedAvgLeakAlarmState = false
    private var cachedFiO2AlarmState = false
    private var cachedSpO2AlarmState = false

    // Validate user set limits
    private fun validateRangeLimits(key: String, value: Float) {

        var currentLable: String? = null
        var i: Intent? = null
        prefManager?.apply {
            var limits: Array<Float?>? = null
            var parametername: String? = null

            when (key) {
                LBL_PIP -> {
                    currentLable = Configs.ALARM_PIP
                    if (readPipLimitState()) {
                        limits = readPipLimits()
                        parametername = getString(R.string.plimit)

                    } else {
                        if (cachedPIPAlarmState) {


                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedPIPAlarmState = readPipLimitState()
                }
                LBL_VTE -> {
                    currentLable = Configs.ALARM_VTE
                    if (readVteLimitState()) {
                        limits = readVteLimits()
                        parametername = getString(R.string.hint_vte)


                    } else {
                        if (cachedVTEAlarmState) {


                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedVTEAlarmState = readVteLimitState()
                }
                LBL_RR -> {
                    currentLable = Configs.ALARM_RR
                    if (readRRLimitState()) {
                        limits = readRRLimits()
                        parametername = getString(R.string.respiratory_rate)


                        Log.i("SOFT_ALARM_STATE", "${readRRLimitState()}")
                        // Toast.makeText(this@DashBoardActivity,""+readRRLimitState()+"",Toast.LENGTH_LONG).show()

                    } else {
                        if (cachedRRAlarmState) {


                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedRRAlarmState = readRRLimitState()
                }
                LBL_PEEP -> {
                    currentLable = Configs.ALARM_PEEP
                    if (readPeepLimitState()) {
                        limits = readPeepLimits()
                        parametername = getString(R.string.peep)
                    } else {
                        if (cachedPEEPAlarmState) {


                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedPEEPAlarmState = readPeepLimitState()
                }
                LBL_MVI -> {
                    currentLable = Configs.ALARM_MVI
                    if (readMviLimitState()) {
                        limits = readMviLimits()
                        parametername = getString(R.string.mvi)

                    } else {
                        if (cachedMVIAlarmState) {
                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedMVIAlarmState = readMviLimitState()
                }
                LBL_FIO2 -> {
                    currentLable = LBL_FIO2
                    if (readFio2LimitState()) {
                        limits = readFiO2Limits()
                        parametername = getString(R.string.fio2)

                    } else {
                        if (cachedFiO2AlarmState) {

                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedFiO2AlarmState = readFio2LimitState()
                }

                LBL_SPO2 -> {
                    currentLable = LBL_SPO2
                    if (readSpO2LimitState()) {
                        limits = readSpO2Limits()
                        parametername = getString(R.string.spo2)

                    } else {
                        if (cachedSpO2AlarmState) {


                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
                                putExtra(ALERT_LABEL, currentLable)
                                putExtra(
                                    ALERT_MSG,
                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
                                )

                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                            }
                        }
                    }

                    cachedSpO2AlarmState = readSpO2LimitState()
                }

//                LBL_TITOT -> {
//                    currentLable = Configs.ALARM_TITOT
//
//                    if (readTiTotLimitState() && false) {  // Temporary down state
//                        limits = readTiTotLimits()
//                        parametername = getString(R.string.titot)
//                    } else {
//                        if (cachedTiTOTAlarmState) {
//
//                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
//                                putExtra(ALERT_LABEL, currentLable)
//                                putExtra(
//                                    ALERT_MSG,
//                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
//                                )
//
//                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
//                            }
//                        }
//                    }
//
//                    cachedTiTOTAlarmState = readTiTotLimitState()
//                }
//                LBL_AVERAGE_LEAK -> {
//                    currentLable = Configs.ALARM_AVERAGE_LEAK
//
//                    if (readLeakLimitState()) {
//                        limits = readLeakLimits()
//                        parametername = getString(R.string.avg_leak)
//
//                    } else {
//                        if (cachedAvgLeakAlarmState) {
//
//
//                            i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW).apply {
//                                putExtra(ALERT_LABEL, currentLable)
//                                putExtra(
//                                    ALERT_MSG,
//                                    parametername + " " + getString(R.string.normal_value_msg_suffix)
//                                )
//
//                                Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
//                            }
//                        }
//                    }
//
//                    cachedAvgLeakAlarmState = readLeakLimitState()
//                }

            }
            // null safety & condition check
            limits?.takeIf { it.size >= 2 && it[0] != null && it[1] != null }?.apply {
                val lowerLimit = this[0]!!
                val upperLimit = this[1]!!
                // var i: Intent? = null

                when {
                    value < lowerLimit -> {
                        i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_OVERFLOW)
                        i?.putExtra(ALERT_LABEL, currentLable)
                        i?.putExtra(
                            ALERT_MSG,
                            parametername + " " + getString(R.string.low_value_msg_suffix)
                        )
                        Log.i("SOFT_ALARM CHECK", "$parametername is detected to be low")

                    }
                    value > upperLimit -> {
                        i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_OVERFLOW)
                        i?.putExtra(ALERT_LABEL, currentLable)
                        i?.putExtra(
                            ALERT_MSG,
                            parametername + " " + getString(R.string.high_value_msg_suffix)
                        )

                        //    Log.i("SOFT_ALARM CHECK", "$parametername is detected to be high")
                    }
                    else -> {
                        i = Intent(IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW)

                        i?.putExtra(ALERT_LABEL, currentLable)
                        i?.putExtra(
                            ALERT_MSG,
                            parametername + " " + getString(R.string.normal_value_msg_suffix)
                        )

                        ///  Log.i("SOFTALARM CHECK", "$parametername is detected to be STABLE")
                    }
                }

                if (isNivActive && LBL_VTI == key && IntentFactory.ACTION_VENT_PARAM_LIMIT_OVERFLOW.equals(
                        i?.action
                    )
                ) {
                    i?.action = IntentFactory.ACTION_VENT_PARAM_LIMIT_UNDERFLOW
                    //Log.i("SOFT_ALARM CHECK", "$parametername is suppressed due to Non Invasive mode")
                }


            }

            i?.run { sendBroadcast(this) }

        }


    }

    /*
    * Register Logs for Inspiratory Pressure and Volume
     */
    //ToDo:- CurrentTask point
    var logValueToBeSaved: HashMap<String?,DataStoreModel?> = HashMap<String?, DataStoreModel?> ()
    var breathCacheList: ArrayList<DataStoreModel> = ArrayList<DataStoreModel>()
    var breathCopyCacheList:ArrayList<DataStoreModel> = ArrayList<DataStoreModel>()
    var logdata:StringBuilder= StringBuilder()
    var lastSavedMinute:Int=-1
    var breathDataReader:CountDownTimer=object : CountDownTimer(900000,2000){
        override fun onTick(millisUntilFinished: Long) {
            /*mDashBoardViewModel.breathData.observe(this@DashBoardActivity, androidx.lifecycle.Observer {
                registerDataLogs(it)
            })*/
            val minuteVal = LocalDateTime.now().minute
            if (LocalDateTime.now().minute % 2 == 0 && lastSavedMinute!=LocalDateTime.now().minute){
                var listAsJson = Gson().toJson(breathCacheList)
                breathCopyCacheList=Gson().fromJson(listAsJson, object : TypeToken<ArrayList<DataStoreModel>>(){}.type)
                /*for (i in 0..(breathCopyCacheList.size-1)){
                    logdata.append("value at $i ${breathCacheList.get(i).time} ${breathCacheList.get(i).mve}  " +
                            "${breathCacheList.get(i).leak}  ${breathCacheList.get(i).ieRatio}")
                    //avgDataStoreModels(breathCopyCacheList.get(i))
                }*/
                var volumeAvg = 0f
                var rrAvg = 0f
                var fiO2Avg = 0f
                var mveAvg = 0f
                var vteAvg = 0f
                var leakAvg = 0f
                var peepAvg = 0f
                var ieRatioAvg = 0f
                var pressureAvg = 0f
                var tinspAvg = 0f
                var texpAvg = 0f
                var mviAvg = 0f
                var triggerAvg = 0f
                var meanAirwayPressureAvg = 0f
                //DataStoreModel.time=it.time
                for (i in 0..(breathCopyCacheList.size-1)){
                    volumeAvg += breathCopyCacheList.get(i).volume
                    rrAvg += breathCopyCacheList.get(i).rr
                    fiO2Avg += breathCopyCacheList.get(i).fiO2
                    mveAvg += breathCopyCacheList.get(i).mve
                    vteAvg += breathCopyCacheList.get(i).vte
                    leakAvg += breathCopyCacheList.get(i).leak
                    peepAvg += breathCopyCacheList.get(i).peep
                    ieRatioAvg += breathCopyCacheList.get(i).ieRatio
                    pressureAvg += breathCopyCacheList.get(i).pressure

                    tinspAvg += breathCopyCacheList.get(i).tinsp
                    texpAvg += breathCopyCacheList.get(i).texp
                    mviAvg += breathCopyCacheList.get(i).mvi
                    triggerAvg += breathCopyCacheList.get(i).trigger
                    meanAirwayPressureAvg += breathCopyCacheList.get(i).meanAirwayPressure

                    if (i==(breathCopyCacheList.size-1)){
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        var dateString:String=LocalDateTime.now().format(formatter)
                        dataStoreModelAvg.volume=volumeAvg/breathCopyCacheList.size
                        dataStoreModelAvg.setRR(rrAvg/breathCopyCacheList.size)
                        dataStoreModelAvg.fiO2=fiO2Avg/breathCopyCacheList.size
                        dataStoreModelAvg.mve=mveAvg/breathCopyCacheList.size
                        dataStoreModelAvg.vte=vteAvg/breathCopyCacheList.size
                        dataStoreModelAvg.leak=leakAvg/breathCopyCacheList.size
                        dataStoreModelAvg.peep=peepAvg/breathCopyCacheList.size
                        dataStoreModelAvg.ieRatio=ieRatioAvg/breathCopyCacheList.size
                        dataStoreModelAvg.pressure=pressureAvg/breathCopyCacheList.size
                        dataStoreModelAvg.tinsp=tinspAvg/breathCopyCacheList.size
                        dataStoreModelAvg.texp=texpAvg/breathCopyCacheList.size
                        dataStoreModelAvg.mvi=mviAvg/breathCopyCacheList.size
                        dataStoreModelAvg.trigger=triggerAvg/breathCopyCacheList.size
                        dataStoreModelAvg.meanAirwayPressure=meanAirwayPressureAvg/breathCopyCacheList.size
                        dataStoreModelAvg.time=dateString
                        dataLogger?.apply {
                            addLog(dataStoreModelAvg)
                            lastSavedMinute=minuteVal
                        }
                        breathCacheList.clear()
                        breathCopyCacheList.clear()
                        this.onFinish()
                    }
                }
                Log.d("TheList", logdata.toString())
            }

        }

        override fun onFinish() {
            this.start()

        }

    }
    
    private fun registerDataLogs(model: DataStoreModel) {
        if (!isLogsEnabled) return
        if (model != null) {
            // registering logs
            mDashBoardViewModel.breathData.value=model


            //logValueToBeSaved.put(getCurrentTimeStamp(),model)

            /*dataLogger?.apply {
                addLog(model)
            }*/
        }

    }

    private fun getCurrentTimeStamp(): String? {
        return Date().toString()

    }
    /*
    * Fetch data from map
    * and cast it to desired data type
    */
    private fun getMapValueFromLabel(map: Map<String, String>, lbl: String): Float? {
        try {
            if (map.containsKey(lbl)) return (map[lbl]?.toFloatOrNull())
        } catch (exception: NumberFormatException) {
            exception.printStackTrace()
            //e(this@DashBoardActivity, exception.stackTrace)
            //ServerLogger.e(this,exception.stackTraceToString())

            return null
        }
        return null
    }


    fun sendControlModeToVentilator(mode: Int) {
        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            send(mode.toString())
        }

    }

    private fun sendConfigurationToVentilatorWithWatchDog() {

        if(settingsCountDownTimer == null) {
            settingsCountDownTimer = SettingsCountDownTimer(2500, 700)
        }

        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            //sendConfigurationToVentilator()
            settingsCountDownTimer?.startRunning()
        }

    }

    private fun sendO2CalibrationCommandToVentilator() {

        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            send(getString(R.string.cmd_calibration_oxyzen))


        }

    }

    private fun sendShutDownCommandToVentilator() {

            sendBroadcast(Intent(IntentFactory.ACTION_POWER_SWITCH))
          //  communicationService?.send(getString(R.string.cmd_vent_shutdown))
    }

    private fun sendStandbyCommandToVentilator() {
        communicationService?.takeIf { it.isPortsConnected && isServiceBound }?.apply {
            sendConfigurationToVentilator { config ->
                val list: ConfigurationArrayList = config.clone() as ConfigurationArrayList
                if (list.size > 8) {
                    val fio2 = getString(R.string.fio2_on_standby)
                    list.set(8, fio2)
                }
                list
            }

            // stop pinging
//            Handler(Looper.getMainLooper()).postDelayed({
//                // Dismiss progress bar after 2 seconds
//                stopPinging()
//            }, 500)

            Handler(Looper.getMainLooper()).postDelayed({
                // Dismiss progress bar after 2 seconds
                send(resources.getString(R.string.cmd_vent_standby))
            }, 1000)

        }

    }

    /*
     * Customized countdown timer for Oxygen calibration
     */
    open inner class CalibrationCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        private var counter = 0
        fun startProgress() {
            counter = 0
            start()
        }

        override fun onTick(millis: Long) {
            if (counter <= 100)
                systemDialogFragment?.takeIf { it.isVisible }?.apply {
                    updateOxygenCalibrateProgressStatus(
                        counter++,
                        "Calibration in progress",
                        View.TEXT_ALIGNMENT_CENTER
                    )
                }


        }

        override fun onFinish() {
            cancel()
        }
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
                Log.i("WATCHDOG_CHECK", "Starting watchdog")
                start()
            }
        }

        fun safeStop() {
            isSafeStop = true
            if (isRunning) {
                Log.i("WATCHDOG_CHECK", "Stoping watchdog")
                cancel()
            }
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


    override fun onCalibration() {
        sendO2CalibrationCommandToVentilator()
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
        val uri = AlarmConfiguration.getAlarmUri(currentPriority)
        uri?.let{

            mediaPlayer?.takeIf { it.isRunning }?.apply {
                stop()
                release()
            }
            mediaPlayer = null
            mediaPlayer = CustomMediaPlayer()
            mediaPlayer?.apply {
                setDataSource(this@DashBoardActivity, it)
                val attrib: AudioAttributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .build()
                setAudioAttributes(attrib)
                setAudioAttributes(attrib)
                prepare()
                isLooping = true
                prefManager?.let {
                    Log.i("LOUDCHECK", "Volume level from pref = " + it.readVolume())
                    this.setVolume(
                        it.readVolume() / VOLUME_MAX_VALUE,
                        it.readVolume() / VOLUME_MAX_VALUE
                    )
                    start()
                }
            }
            alarmIsPlaying = true
            alarmToggleVisibility = true

            object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    mediaPlayer?.release()
                    mediaPlayer = null;

                }
            }.start()
        }


    }

    private fun onPlayAlarm() {

        val uri = AlarmConfiguration.getAlarmUri(currentPriority)
        uri?.also {
            mediaPlayer = null
            mediaPlayer = CustomMediaPlayer()
            mediaPlayer?.apply {
                setDataSource(this@DashBoardActivity, it)
                val attrib: AudioAttributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .build()
                setAudioAttributes(attrib)
                setAudioAttributes(attrib)
                prepare()
                isLooping = true
                prefManager?.let {
                    Log.i("ALARMCHECK", "Volume level = " + it.readVolume() / VOLUME_MAX_VALUE)
                    setVolume(it.readVolume() / VOLUME_MAX_VALUE, it.readVolume() / VOLUME_MAX_VALUE)
                    start()
                }

            }
            alarmIsPlaying = true
            alarmToggleVisibility = true
        }




    }

    override fun onLimitChange(previousValue: Float, newValue: Float) {
      //  val isBasicFragmentVisible = controlDialogFragment?.isBasicFragmentVisible() == true
        val isAdvancedFragmentVisible = controlDialogFragment?.isAdvancedFragmentVisible() == true
        val isBackupFragmentVisible = controlDialogFragment?.isBackupFragmentVisible() == true



        if(isBackupFragmentVisible){
            selectedBackupPosition?.let { pos ->
                backupControlParameterList.getOrNull(pos)?.apply {
                    updateVentBackupParameterValue(ventKey, newValue.toString())
                }
            }
        }else if (isAdvancedFragmentVisible){
            selectedAdvancedPosition?.let { pos ->
                advancedControlParameterList.getOrNull(pos)?.apply {
                    updateVentAdvancedParameterValue(ventKey, newValue.toString())
                }
            }
        }
//        else {
            selectedBasicPosition?.let { pos ->
                basicControlParameterList.getOrNull(pos)?.apply {
                    updateVentParameterValue(ventKey, newValue.toString())
                }
            }
//        }
    }


    override fun onKnobPress(previousValue: Float, newValue: Float) {
        // save value
        // ventParameterTiles[selectedPosition].isIsselected=false

        val isAdvancedFragmentVisible = controlDialogFragment?.isAdvancedFragmentVisible() == true
       // val isBasicFragmentVisible = controlDialogFragment?.isBasicFragmentVisible() == true
        val isBackupFragmentVisible = controlDialogFragment?.isBackupFragmentVisible() == true


        if(isBackupFragmentVisible){
            backupControlParameterList.let {
                selectedBackupPosition?.apply {
                    val lbl = it[this].ventKey
                    val unit = it[this].units
                    lbl?.let { paramLabel ->
                        Log.i("LBL", "Selected label = $paramLabel, postion  = $this")
                        // ToDO : Save on shared pref


                        prefManager?.apply { updateParameterViaName(paramLabel, newValue) }


                        Log.i("SetValue", "Sett $lbl from $previousValue to $newValue")

                        updateVentBackupParameterValue(paramLabel, newValue.toString())
                        sendConfigurationToVentilatorWithWatchDog()


                        val eventDataModel = EventDataModel(
                            "Set $paramLabel from ${
                                supportPrecision(paramLabel, previousValue)
                            }  $unit to $newValue $unit"
                        )
                        mEventViewModel.addEvent(eventDataModel)
                    }

                    selectedBackupPosition = null

                }

            }
        }
        else if(isAdvancedFragmentVisible){
            advancedControlParameterList.let {
                // ventParameterTiles[selectedPosition].isIsselected=false

                selectedAdvancedPosition?.apply {
                    val lbl = it[this].ventKey
                    val unit = it[this].units
                    lbl?.let { paramLabel ->
                        Log.i("LBL", "Selected label = $paramLabel, postion  = $this")



                        prefManager?.apply { updateParameterViaName(paramLabel, newValue) }


                        Log.i("SetValue", "Sett $lbl from $previousValue to $newValue")

                        updateVentAdvancedParameterValue(paramLabel, newValue.toString())
                        sendConfigurationToVentilatorWithWatchDog()


                        val eventDataModel = EventDataModel(
                            "Set $paramLabel from ${
                                supportPrecision(paramLabel, previousValue)
                            }  $unit to $newValue $unit"
                        )
                        mEventViewModel.addEvent(eventDataModel)
                    }

                    selectedAdvancedPosition = null

                }
            }

        }
//        else {
            basicControlParameterList.let {
                // ventParameterTiles[selectedPosition].isIsselected=false

                selectedBasicPosition?.apply {
                    val lbl = it[this].ventKey
                    val unit = it[this].units
                    lbl?.let { paramLabel ->
                        Log.i("LBL", "Selected label = $paramLabel, postion  = $this")
                        // ToDO : Save on shared pref


                        prefManager?.apply { updateParameterViaName(paramLabel, newValue) }


                        Log.i("SetValue", "Sett $lbl from $previousValue to $newValue")

                        updateVentParameterValue(paramLabel, newValue.toString())
                        sendConfigurationToVentilatorWithWatchDog()


                        val eventDataModel = EventDataModel(
                            "Set $paramLabel from ${
                                supportPrecision(paramLabel, previousValue)
                            }  $unit to $newValue $unit"
                        )
                        mEventViewModel.addEvent(eventDataModel)
                    }

                    selectedBasicPosition = null

                }
            }
//        }


        normaliseParameterTiles()

        hideKnobViews()

        hideSystemUI()

    }


    private fun normaliseParameterTiles() {
        basicControlParameterList.forEach {
            it.isIsselected = false
        }
        advancedControlParameterList.forEach{
            it.isIsselected = false
        }

        backupControlParameterList.forEach {
            it.isIsselected = false
        }

        selectedTiles.forEach {
            it.isSelectedAsSwappable = false
        }

        primaryControlParameterAdapter?.notifyDataSetChanged()
        controlDialogFragment?.notifyParameterAdapter()
        primaryObservedParameterAdapter?.notifyDataSetChanged()

    }

    private fun setControlParameterSelected(params: List<ControlParameterModel>, at: Int) {
        if (at < params.size) {
            normaliseParameterTiles()
            params.get(at).isIsselected = true
        }
    }



    private fun sendParametersToVentilator(parameters: List<ControlParameterModel>, apneaStatus: Boolean) {

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

    // update limits on the observed tiles

    override fun onChangeAlarmLimit(
        currentKey: String?,
        lowerLimit: Float,
        upperLimit: Float?
    ) {

        if (selectedTiles.any { (it.label + it.labelSubscript) == currentKey }) {
            Log.i("ALARMLIMITCHECK", "Key = $currentKey found in primary observed tiles")
            val tempList = arrayListOf<ObservedParameterModel>()
            for (i in selectedTiles) {
                if (i.label.equals(currentKey)) {

                    i.lowerLimitValue = lowerLimit.toString()
                    i.upperLimitValue = upperLimit.toString()
                    tempList.add(i)

                } else {
                    tempList.add(i)
                }
            }
            selectedTiles = tempList

            primaryObservedParameterAdapter?.notifyDataSetChanged()
            //        setUpObserveValueFromVentilator()

        }


        if (observedValueSpHOList.any { (it.label + it.labelSubscript) == currentKey }) {
            Log.i("ALARMLIMITCHECK", "Key = $currentKey found in secondary observed tiles")

            val tempList = arrayListOf<ObservedParameterModel>()
            for (i in observedValueSpHOList) {
                if (i.label.equals(currentKey)) {

                    i.lowerLimitValue = lowerLimit.toString()
                    i.upperLimitValue = upperLimit.toString()
                    tempList.add(i)

                } else {
                    tempList.add(i)
                }
            }
            observedValueSpHOList = tempList

            secondaryObservedParameterAdapter?.notifyDataSetChanged()
        }


        /*  containerList.filter { it.ventKey.equals(currentKey) }?.apply {
              this[0].lowerLimit=mviLowerLimit.toDouble()
              this[0].upperLimit=mviUpperLimit?.toDouble()
          }*/


    }


    private fun getStandbyParameterClickListener(params: MutableList<ControlParameterModel>?, type: ControlSettingType) = object: ControlParameterClickListener{
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked) {
                params?.apply {
                    val knobModel = KnobParameterModel.fromControlParameter(model)
                    this.forEach{ it.isIsselected = false }
                    this.getOrNull(position)?.isIsselected = true

                    val encoderValue = EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )

                    showKnobViewsForStandbyControls(
                        knobModel,
                        encoderValue,
                        this,
                        position,
                        type
                    )

                }

                standbyControlFragment?.notifyParameterAdapter()
            }
        }

        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {}
    }


    private val onBasicParameterClickListener = object: ControlParameterClickListener{
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked) {
                basicControlParameterList.let {
                    selectedBasicPosition = position
                    selectedAdvancedPosition = null
                    selectedBackupPosition = null
                    val knobModel = KnobParameterModel.fromControlParameter(model)
                    setControlParameterSelected(it, position)

                    val encoderValue = EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )

                    Log.i("MODECODE", "current mode = $modeCode")

                    showKnobViews(knobModel, encoderValue)
                    primaryControlParameterAdapter?.notifyDataSetChanged()
                }

                controlDialogFragment?.notifyParameterAdapter()
            }


        }


        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {}


    }

    private val onAdvancedParameterClickListener = object : ControlParameterClickListener {
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked) {
                advancedControlParameterList.let {
                    selectedBasicPosition = null
                    selectedAdvancedPosition = position
                    selectedBackupPosition = null
                    val knobModel = KnobParameterModel.fromControlParameter(model)
                    setControlParameterSelected(it, position)
                    val encoderValue = EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )
                    Log.i("ADVANCEDCHECK", "Encoder = " + encoderValue.toString())
                    showKnobViews(knobModel, encoderValue)
                }
                controlDialogFragment?.notifyParameterAdapter()
            }
        }

        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {
            Log.i("IRVMODE_CHECK", "Toggle state changed to " + isActive)
            if(type == ControlSettingType.ADVANCED) {
                Log.i("IRVMODE_CHECK", "In Backup Toggle state changed to " + isActive)
                prefManager?.setIRVStatus(isActive)

            }
        }

    }


    private val onBackupParameterClickListener = object : ControlParameterClickListener {
        override fun onClick(position: Int, model: ControlParameterModel) {
            if (isLocked) {
                backupControlParameterList.let {
                    selectedBasicPosition = null
                    selectedAdvancedPosition = null
                    selectedBackupPosition = position
                    val knobModel = KnobParameterModel.fromControlParameter(model)
                    setControlParameterSelected(it, position)


                    val encoderValue = EncoderValue(
                        model.lowerLimit.toFloat(),
                        model.upperLimit.toFloat(),
                        model.step.toFloat()
                    )

                    Log.i("BACKUPCHECK", "Encoder = $encoderValue.toString()")

                    showKnobViews(knobModel, encoderValue)
                }

                controlDialogFragment?.notifyParameterAdapter()
            }
        }

        override fun onStateChange(isActive: Boolean, type: ControlSettingType) {
            Log.i("STATECHANGE_CHECK", "Toggle state changed to " + isActive)
            if(type == ControlSettingType.BACKUP) {
                Log.i("STATECHANGE_CHECK", "In Backup Toggle state changed to " + isActive)
                prefManager?.setApneaSettingsStatus(isActive)
                sendConfigurationToVentilatorWithWatchDog()
            }
        }
    }


    // runs when swap is initiated
    //ToDo:-current task the definition of the click event that has issue.
    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(position: Int, model: ObservedParameterModel) {


        swappableRecyclerView.layoutManager = LinearLayoutManager(this)
        swappableAdapter = SwappableAdapter(observedValueList, model, this)


        // Setting the Adapter with the recyclerview
        // observedPopUpLayout.visibility=View.GONE


        swappableRecyclerView.adapter = swappableAdapter
        swappableLayout.visibility = View.VISIBLE
//


        selectedTiles.forEach { it.isSelected = false }
        selectedTiles[position].isSelected = true;
        //primaryObservedParameterAdapter?.notifyItemChanged(position)
        primaryObservedParameterAdapter?.notifyDataSetChanged()
        hideKnobViews()


        // select first element
        swappableAdapter?.setSelection(0)
    }

    private var observeredParameterClickIndex=0
    private var swapableAdapterClickIndex=0
    // runs after selection and swap is completed
    @SuppressLint("NotifyDataSetChanged")
    override fun onSwap(
        containerModel: ObservedParameterModel,
        desiredModel: ObservedParameterModel
    ) {
        //get the index of the selected swapable list view item
        val desiredModelIndex = observedValueList.indexOf(desiredModel)
        //the index of the selected value in the observed parameter adapter i.e the recyclerview
        val containerModelIndex = selectedTiles.indexOf(containerModel)

        observeredParameterClickIndex=containerModelIndex
        swapableAdapterClickIndex=desiredModelIndex


        if (desiredModelIndex < 0 || containerModelIndex < 0) {
            Log.i(
                "INDEXSWAPCHECK",
                "Unable to swap index $containerModelIndex with $desiredModelIndex"
            )
            return
        }

//        observedValueList.forEach { it.isSelectedAsSwappable = false }
//        observedValueList[desiredModelIndex].isSelectedAsSwappable =true;
//        swappableAdapter?.notifyDataSetChanged()
        swappableAdapter?.setSelection(desiredModelIndex)

      /*  if (containerModelIndex < selectedTiles.size) {
            selectedTiles[containerModelIndex] = desiredModel
        }*/

        closeSwappableLayout()
    }


    //Update the swapable list  as well
    //observedValueList=upDateList(selectedTiles,observedValueListCopy) as (ArrayList<ObservedParameterModel>)
    //swappableAdapter?.notifyDataSetChanged()

    //ToDo:-The pin point of the problem
    @SuppressLint("NotifyDataSetChanged")
    private fun closeSwappableLayout() {
        Handler(Looper.getMainLooper()).postDelayed({
            swappableLayout.visibility = View.GONE // ToDo: Add to all layout of XMl
            observedValueList.forEach {
                it.isSelectedAsSwappable = false
                it.isSelected = false
            }
            swappableAdapter?.notifyDataSetChanged()

            if (selectedTiles.contains(observedValueList[swapableAdapterClickIndex])){
            } else {
                selectedTiles.removeAt(observeredParameterClickIndex)
                selectedTiles.add(observeredParameterClickIndex,
                    observedValueList[swapableAdapterClickIndex]
                )
                primaryObservedParameterAdapter?.notifyItemChanged(observeredParameterClickIndex)
            }

            // ToDo: clean up adapter and recycler view
        }, 100)
    }

    private fun upDateList(observedParameterList:List<ObservedParameterModel>,swappableAdapterList:List<ObservedParameterModel>): List<ObservedParameterModel> {
        val list:MutableList<ObservedParameterModel> = observedParameterList.toMutableList()
        val listSwappable:MutableList<ObservedParameterModel> = swappableAdapterList.toMutableList()
        listSwappable.removeAll(list)
        return listSwappable.toList()
    }

}





