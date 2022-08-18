package com.agvahealthcare.ventilator_ext.graphics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnGraphSelectListener
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.duo_graph.DuoFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.pent_graph.DividePentFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.quad_graph.DivideQuadFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.quad_graph.QuadFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.trio_graph.DivideTrioFragmentGraph
import com.agvahealthcare.ventilator_ext.dashboard.trio_graph.TrioFragmentGraph
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import kotlinx.android.synthetic.main.fragment_graphics_dialog.*
import kotlinx.android.synthetic.main.item_mode_data_layout.view.*


class GraphicsDialogFragment  (private val onGraphSelectListener: OnGraphSelectListener?, val closeListener : OnDismissDialogListener?, val graphLayoutListener: GraphLayoutListener?) : DialogFragment() {


    interface GraphLayoutListener{
        fun getCurrentLayoutFragment(): GraphLayoutFragment?
    }

    companion object {
        const val TAG = "GraphicsDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_graphics_dialog, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setupClickListener()

        initView()


    }

    private fun initView() {
        includeButtonLayout1.buttonView.text = getString(R.string.hint_layout_1)
        includeButtonLayout2.buttonView.text = getString(R.string.hint_layout_2)
        includeButtonLayout3.buttonView.text = getString(R.string.hint_layout_3)
        includeButtonLayout4.buttonView.text = getString(R.string.hint_layout_4)
        includeButtonLayout5.buttonView.text = getString(R.string.hint_layout_5)
        includeButtonLayout6.buttonView.text = getString(R.string.hint_layout_6)
        includeButtonDefault.buttonView.text = getString(R.string.hint_defaults)


       /* includeButtonLayout1.buttonView.setPadding(35,10,35,10)
        includeButtonLayout2.buttonView.setPadding(35,10,35,10)
        includeButtonLayout3.buttonView.setPadding(35,10,35,10)
        includeButtonLayout4.buttonView.setPadding(35,10,35,10)
        includeButtonLayout5.buttonView.setPadding(35,10,35,10)*/

        includeButtonDefault.buttonView.setPadding(35,10,35,10)
        includeButtonLayout1.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout1.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


        includeButtonLayout2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonLayout3.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout3.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonLayout4.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout4.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonLayout5.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout5.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonLayout6.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLayout6.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonDefault.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonDefault.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


       var graph= graphLayoutListener?.getCurrentLayoutFragment()
         if(graph!=null)
        if(graph is QuadFragmentGraph) {
            //Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout1.buttonView.setBackgroundResource(R.drawable.background_green_border)

        }
        else if(graph is DivideQuadFragmentGraph){
           // Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout2.buttonView.setBackgroundResource(R.drawable.background_green_border)

        }else if(graph is DuoFragmentGraph){
            //Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout3.buttonView.setBackgroundResource(R.drawable.background_green_border)

        }else if(graph is TrioFragmentGraph){
           // Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout4.buttonView.setBackgroundResource(R.drawable.background_green_border)

        }else  if (graph is DivideTrioFragmentGraph){
           // Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout5.buttonView.setBackgroundResource(R.drawable.background_green_border)

        }
        else if(graph is DividePentFragmentGraph) {
            // Log.i("GRAPHCHECK", "Name of layout = ${this.name}")


            includeButtonLayout6.buttonView.setBackgroundResource(R.drawable.background_green_border)
        }




    }

    // ClickListener on Buttons
    private fun setupClickListener() {


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

        includeButtonLayout1.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectQuadGraph()
            closeListener?.handleDialogClose()
            closeDialog()

            //dismiss()
        }

        layoutPanelQuad.setOnClickListener {
            onGraphSelectListener?.onSelectQuadGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        includeButtonLayout2.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectDivideQuadGraph()
            closeListener?.handleDialogClose()
            closeDialog()

        }

        layoutPanelDividePent.setOnClickListener {
            onGraphSelectListener?.onSelectDividePentGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        includeButtonLayout6.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectDividePentGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        layoutPanelDivideQuad.setOnClickListener {
            onGraphSelectListener?.onSelectDivideQuadGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }


        includeButtonLayout3.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectDuoGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }


        layoutPanelDuo.setOnClickListener {
            onGraphSelectListener?.onSelectDuoGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        includeButtonLayout4.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectTrioGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        layoutPanelTrio.setOnClickListener {
            onGraphSelectListener?.onSelectTrioGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        includeButtonLayout5.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectDivideTrioGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        layoutPanelDivideTrio.setOnClickListener {
            onGraphSelectListener?.onSelectDivideTrioGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }

        includeButtonDefault.buttonView.setOnClickListener {
            onGraphSelectListener?.onSelectDivideQuadGraph()
            closeListener?.handleDialogClose()
            closeDialog()
        }
    }

   fun closeDialog(){
       requireActivity().supportFragmentManager
               .beginTransaction()
               .remove(this)
               .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

       closeListener?.handleDialogClose()
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)

        setHeightWidthPercent(heightDialog , widthDialog , true)

    }


}