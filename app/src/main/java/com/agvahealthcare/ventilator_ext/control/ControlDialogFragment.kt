import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.control.advanced.AdvancedFragment
import com.agvahealthcare.ventilator_ext.control.backup.BackupFragment
import com.agvahealthcare.ventilator_ext.control.basic.BasicFragment
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterClickListener
import com.agvahealthcare.ventilator_ext.control.patient.PatientFragment
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.standby.StandbyControlSettingFragment
import com.agvahealthcare.ventilator_ext.utility.CustomCountDownTimer
import com.agvahealthcare.ventilator_ext.utility.replaceFragment
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import com.github.angads25.toggle.interfaces.OnToggledListener
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_contol_dialog.*


//interface OnStartVentilationListener {
//    fun onModeConfirmed()
//    fun onSettingsConfirmed(parameters: List<ControlParameterModel>)
//}

class ControlDialogFragment : DialogFragment() {

    private var closeListener: OnDismissDialogListener? = null
    private var basicParameterClickListener: ControlParameterClickListener?=null
    private  var advancedFragment: StandbyControlSettingFragment?=null
    private var advancedControlParams : List<ControlParameterModel>? = null
    private var backupParameterClickListener: ControlParameterClickListener?=null
    private var onToggledListener: OnToggledListener?=null
    private var basicControlParams :List<ControlParameterModel>? = null
    private var backupControlParams :List<ControlParameterModel>? = null
//    private var onStartVentilationListener: OnStartVentilationListener?=null
    private var patientFragment : PatientFragment? = null
    private var currentMode: String? = null
    private var dialogModeConfirmation: AlertDialog? = null
    private var basicFragment : BasicFragment? = null
    private var advancedParameterClickListener : ControlParameterClickListener?=null
    private var backupFragment : BackupFragment? = null
    private var visibilityTimeout: CountDownTimer? =null
    private var isStatus: Boolean? = null
    public var cancelableStatus: Boolean = false;



    companion object {
        const val TAG = "ControlDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        //private const val KEY_POSITION = "KEY_POSITION"


        fun newInstance(
            height: Int?,
            width: Int?,
         //   positionx : Int?,
            basicParams: List<ControlParameterModel>,
            advancedParams: List<ControlParameterModel>,
            backupParams: List<ControlParameterModel>?,
            closeListener: OnDismissDialogListener?,
            basicParameterClickListener: ControlParameterClickListener?,
            advancedParameterClickListener: ControlParameterClickListener?,
            backupParameterClickListener: ControlParameterClickListener?,
            onToggledListener: OnToggledListener?
//            onStartVentilationListener: OnStartVentilationListener?
        ): ControlDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            val fragment = ControlDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            fragment.basicParameterClickListener = basicParameterClickListener
            fragment.advancedParameterClickListener = advancedParameterClickListener
            fragment.backupParameterClickListener = backupParameterClickListener
            fragment.onToggledListener = onToggledListener
            fragment.basicControlParams = basicParams
            fragment.advancedControlParams = advancedParams
            fragment.backupControlParams = backupParams

//            fragment.onStartVentilationListener = onStartVentilationListener

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,

    ): View {
        return inflater.inflate(R.layout.fragment_contol_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpBasic()
        setPaddingOnButton()
        setOnClickListener()

        includeButtonApnea.visibility = if (backupControlParams?.isNotEmpty() == true) View.VISIBLE else View.GONE
    }


    private fun setPaddingOnButton() {

        //includeButtonBasic.buttonView.setPadding(38, 0, 38, 0)
        //includeButtonMore.buttonView.setPadding(38, 0, 38, 0)
       // includeButtonPatient.buttonView.setPadding(33, 0, 33, 0)

       // includeButtonPatient.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
       // includeButtonBasic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    fun notifyParameterAdapter(){
        if (backupFragment?.isVisible==true){
            backupFragment?.notifyAdapter()
        }

        else if (advancedFragment?.isVisible == true){
            advancedFragment?.notifyAdapter()
        }
        else {
            basicFragment?.notifyAdapter()
        }
    }

   // fun isBasicFragmentVisible() = basicFragment?.isVisible == true
   fun isAdvancedFragmentVisible() = advancedFragment?.isVisible == true
    fun isBackupFragmentVisible() = backupFragment?.isVisible == true

    //By Default Fragment

    private fun setUpBasic() {

       // patientFragment = null
        basicControlParams?.let {
            Log.i("CONTROLPARAMCHECK", "BASIC SIZE = ${it.size}")
            if(basicFragment==null) basicFragment = BasicFragment(ArrayList(it), basicParameterClickListener)
            basicFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.control_nav_container )
            }
        }

        /*btn_update_settings.visibility = View.GONE*/
        includeButtonPatient.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonPatient.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonApnea.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonApnea.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonAdvance.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonAdvance.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonBasic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)

        setPaddingOnButton()

    }


    private fun setUpBackup() {

        if( backupControlParams?.isNotEmpty() == true){

            // patientFragment = null
            backupControlParams?.let {
                Log.i("CONTROLPARAMCHECK", "BACKUP SIZE = ${it.size}")
                Log.i("BACKUPCHECK", " STEP = ${it.get(0).step}, ${it.get(1).step}")
                if (backupFragment == null) backupFragment = BackupFragment(ArrayList(it), backupParameterClickListener,onToggledListener)

                backupFragment?.apply {
                    replaceFragment(
                        this,
                        this::class.java.javaClass.simpleName,
                        R.id.control_nav_container
                    )
                }
            }

            includeButtonPatient.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonPatient.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

            includeButtonApnea.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonApnea.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)

            includeButtonBasic.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

            includeButtonAdvance.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonAdvance.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


            setPaddingOnButton()
        }

    }
    private fun setUpAdvanced() {

        if(advancedControlParams?.isNotEmpty() == true){
            advancedControlParams?.let {
                if(advancedFragment == null) advancedFragment = AdvancedFragment(ArrayList(it), advancedParameterClickListener)
                advancedFragment?.apply {
                    replaceFragment(
                        this,
                        this::class.java.javaClass.simpleName,
                        R.id.control_nav_container
                    )
                }
            }
        }

        includeButtonPatient.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonPatient.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonApnea.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonApnea.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonAdvance.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonAdvance.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)


        includeButtonBasic.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        setPaddingOnButton()


    }

    // ClickListener on Buttons
    private fun setOnClickListener() {

        includeButtonBasic.buttonView.text = getString(R.string.hint_basic)
        includeButtonApnea.buttonView.text = getString(R.string.hint_apneasettings)
        includeButtonAdvance.buttonView.text = getString(R.string.hint_advancedsettings)
        includeButtonPatient.buttonView.text = getString(R.string.hint_patient)

        imageViewCross.setOnClickListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()

           /* closeListener?.handleDialogClose()
            dismiss()*/
        }
        includeButtonBasic.buttonView.setOnClickListener {
            setUpBasic()
        }


        includeButtonApnea.buttonView.setOnClickListener {
            setUpBackup()
        }
        includeButtonAdvance.buttonView.setOnClickListener {
            setUpAdvanced()
        }


        includeButtonPatient.buttonView.setOnClickListener {
            setUpPatient()
        }

    }



    private fun setUpPatient() {
       // patientFragment=null;
        if(patientFragment==null)
        patientFragment = PatientFragment()
        patientFragment?.apply {
            replaceFragment(this,this::class.java.javaClass.simpleName, R.id.control_nav_container )
        }

        includeButtonBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonBasic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonApnea.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonApnea.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonPatient.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
        includeButtonPatient.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonAdvance.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonAdvance.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        setPaddingOnButton()
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)

        setHeightWidthPercent(heightDialog , widthDialog , true)
    }

    fun setTime(counterState: String, customCountDownTimer: CustomCountDownTimer) {
        patientFragment?.takeIf { it.isVisible }?.apply {
            setVentilationTime(counterState,customCountDownTimer)
        }

    }

}

