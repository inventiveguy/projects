package com.agvahealthcare.ventilator_ext.logs.trends

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.database.LogsTableManager
import com.agvahealthcare.ventilator_ext.database.entities.DataTableModel
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import de.codecrafters.tableview.SortableTableView
import org.jetbrains.anko.runOnUiThread

@Deprecated("Deprecated. Use LogsTrendsFragment instead")
class TrendsFragment : Fragment() {

    private var tableViewData: SortableTableView<DataTableModel>? = null
    private var refreshLayout : SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val itemView = inflater.inflate(R.layout.fragment_logs_trends, container, false)
        renderLogs()

        tableViewData = itemView.findViewById(R.id.tableView) as SortableTableView<DataTableModel>?

        return itemView
    }

    private fun renderLogs() {
        RenderTableTask(requireContext()).execute()

    }

    @SuppressLint("StaticFieldLeak")
    open inner class RenderTableTask(context: Context) : AsyncTask<Void, Void, Void>() {
        private var logManager: DataLogger? = null

        override fun onPreExecute() {
           logManager = DataLogger(context)
        }

        override fun doInBackground(vararg params: Void): Void? {
            val models: ArrayList<DataTableModel>? = prepareTableModels()
            if (models != null) {
                // reverse the data list
                models.reverse()

                context?.runOnUiThread {
                    LogsTableManager(requireContext(), tableViewData ).populateData(models)
                }

                Log.i("DATA_LOG_BACK" ,"doInBackground")

                Log.i("DATA_LOG_BACK" ,"" +models.toString())
            }

            return null
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)
            Log.i("DATA_LOG_BACK" ,"onPostExecute" )
            refreshLayout?.isRefreshing = false

        }


        private fun prepareTableModels(): ArrayList<DataTableModel>? {
            val tableModels: ArrayList<DataTableModel> = ArrayList<DataTableModel>()
            val dataModels: ArrayList<DataStoreModel>? = logManager?.logs
            if (dataModels != null && dataModels.size > 0) {
                for (dataModel in dataModels) {
                    tableModels.add(DataTableModel(dataModel))
                }
                return tableModels
            }
            return null
        }

    }

}
