package com.agvahealthcare.ventilator_ext.logs.trends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardViewModel
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.logs.trends.paging.PagingAAdapter
import com.agvahealthcare.ventilator_ext.logs.trends.paging.PagingViewModel
import com.agvahealthcare.ventilator_ext.logs.trends.paging.TrendsPagingAdapter
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import kotlinx.android.synthetic.main.fragment_logs_table_demo.*



/**
 * A simple [Fragment] subclass.
 * Use the [LogsTrendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogsTrendsFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var parameterAndUnitsAdapter: ParameterAndUnitsAdapter? = null
    private var dataFromDataBaseAdapter: DataFromDataBaseAdapter? = null
    private var dateAdapter: DateAdapter? = null
    private var dashBoardViewModel: DashBoardViewModel? = null
    private lateinit var logManager: DataLogger
    private var list: ArrayList<DataStoreModel>? = null
    private var listOFList: ArrayList<ArrayList<DataStoreModel>>? = null
    private var containerOfNested: ContainerOfNested? = null
    private lateinit var adapterPaging: TrendsPagingAdapter
    private lateinit var pagingAAdapter: PagingAAdapter
    private lateinit var pagingViewModel: PagingViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logs_table_demo, container, false)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LogsTableDemoFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): LogsTrendsFragment {
            val fragment = LogsTrendsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leftButton.setOnClickListener(this)
        rightButton.setOnClickListener(this)
        dashBoardViewModel =
            ViewModelProvider(requireActivity()).get(DashBoardViewModel::class.java)
        logManager = DataLogger(requireContext())
        logManager.getNewLogs(0).apply {
            dashBoardViewModel?.updateDataStoreList(this)
        }
        list = dashBoardViewModel?.dataStoreList?.value
        parameterAndUnitsAdapter = ParameterAndUnitsAdapter()
        dataFromDataBaseAdapter = list?.let { DataFromDataBaseAdapter(requireContext(), it) }
        rvParameterConstants.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = parameterAndUnitsAdapter
        }
        rvTwo.setHasFixedSize(true)
        rvTwo.setItemViewCacheSize(10);
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTwo.apply {
            layoutManager = linearLayoutManager
            adapter = dataFromDataBaseAdapter
        }
        dashBoardViewModel?.dataStoreList?.observe(viewLifecycleOwner, Observer {
            dataFromDataBaseAdapter?.notifyItemRangeChanged(
                0,
                dataFromDataBaseAdapter!!.getItemCount()
            );
        })

        rvTwo.addOnScrolledToEnd {
            var tempList = dashBoardViewModel?.dataStoreList?.value
            //condition to check if the returned list is not null and empty
            if (tempList != null || (tempList?.isEmpty()
                    ?: emptyList<DataStoreModel>()) as Boolean
            ) {
                tempList?.addAll(logManager.getNewLogs(counter))
                counter = counter.inc()
                tempList?.let { dashBoardViewModel?.updateDataStoreList(it) }
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                upDateSeekBar()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun upDateSeekBar() {
        val firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        seekBar.max = dashBoardViewModel?.dataStoreList?.value?.size ?: 0
        seekBar.min = 0
        seekBar.progress = firstVisibleItem
    }

    fun scrollAt(offset: Int){
        val firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        rvTwo.smoothScrollToPosition(firstVisibleItem + offset)
    }
    fun scrollForward() = rightButton.callOnClick()
    fun scrollBack() = leftButton.callOnClick()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rightButton -> {
                var sizeOfList = dashBoardViewModel?.dataStoreList?.value?.size
                val firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItem >= (sizeOfList?.minus(8)!!)) {
                    rightButton.isClickable = false
                    rvTwo.smoothScrollToPosition(sizeOfList.minus(8))
                } else {
                    rightButton.isClickable = false
                    rvTwo.smoothScrollToPosition(firstVisibleItem + 7)
                    upDateSeekBar()
                }
                rightButton.isClickable = true
            }
            R.id.leftButton -> {
                val firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItem <= 7) {
                    leftButton.isClickable = false
                    rvTwo.smoothScrollToPosition(0)
                } else {
                    leftButton.isClickable = false
                    rvTwo.smoothScrollToPosition(firstVisibleItem - 7)
                    upDateSeekBar()
                }
                leftButton.isClickable = true
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun calculateTheSizeOfScroll(sizeOfList: Int): Int {
        var listStepsUnit = (1 * sizeOfList) / 100
        return listStepsUnit
    }

    var counter = 1
    fun RecyclerView.addOnScrolledToEnd(onScrolledToEnd: () -> Unit) {

        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private val VISIBLE_THRESHOLD = 7

            private var loading = true
            private var previousTotal = 0

            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {

                with(layoutManager as LinearLayoutManager) {

                    val visibleItemCount = childCount
                    val totalItemCount = itemCount
                    val firstVisibleItem = findFirstVisibleItemPosition()

                    if (loading && totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }

                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {

                        onScrolledToEnd()
                        loading = true
                    }
                }
                upDateSeekBar()
            }
        })
    }
}