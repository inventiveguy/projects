package com.agvahealthcare.ventilator_ext.database;

import android.content.Context;
import android.util.Log;

import com.agvahealthcare.ventilator_ext.R;
import com.agvahealthcare.ventilator_ext.database.entities.DataTableModel;
import com.agvahealthcare.ventilator_ext.logs.trends.LogTableAdapter;

import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

//SortableTableView value integration class and setting the Table view initial values
//@Deprecated("Deprecated. No more required")
public class LogsTableManager extends TableManager<DataTableModel> {
        private Context ctx;
        private SortableTableView<DataTableModel> tableView;

        //initializing constructor with the non-null parameters
        public LogsTableManager(Context ctx, SortableTableView<DataTableModel> tableView) {
            super(ctx, tableView);
            this.ctx = ctx;
            this.tableView = tableView;
        }




        public void populateData(List<DataTableModel> modalList) {
            initTableHeaders();
            initTableWeighting();
          //  if (ctx!=null)
            try{
                tableView.setDataAdapter( new LogTableAdapter(ctx, modalList));
            }catch (Exception e){
                Log.i("INITTABLEHEADERS",e.getMessage());
            }

        }

        private void initTableHeaders() {
            //SimpleTableHeaderAdapter th = new SimpleTableHeaderAdapter(ctx, "Time", "PIP (cm H₂O)", "VTi (ml)", "RR (bmp)", "FiO₂ (%)","PEEP (cm H₂O)","LEAK (%)", "MVE (ml)", "1:E");


            try{
                SimpleTableHeaderAdapter th = new SimpleTableHeaderAdapter(ctx, "Time", "PIP", "VTi", "RR", "FiO₂", "PEEP", "LEAK", "1:E");
                th.setTextSize(14);
                th.setPaddings(55, 20, 20, 20);
                th.setTextColor(ctx.getResources().getColor(android.R.color.white));
                tableView.setHeaderAdapter(th);
                tableView.setHeaderBackgroundColor(ctx.getResources().getColor(R.color.black));
            }catch (Exception e){
                Log.i("INITTABLEHEADERS",e.getMessage());

            }

         /* if(ctx!=null) {
              SimpleTableHeaderAdapter th = new SimpleTableHeaderAdapter(ctx, "Time", "PIP", "VTi", "RR", "FiO₂", "PEEP", "LEAK", "1:E");
              th.setTextSize(14);
              th.setPaddings(55, 20, 20, 20);
              th.setTextColor(ctx.getResources().getColor(android.R.color.white));
              tableView.setHeaderAdapter(th);
              tableView.setHeaderBackgroundColor(ctx.getResources().getColor(R.color.black));
          }*/
        }

    private void initTableWeighting() {
        TableColumnWeightModel columnModel = new TableColumnWeightModel(8);
//            columnModel.setColumnWeight(0, 2); // S No
        columnModel.setColumnWeight(0, 5); // time
        columnModel.setColumnWeight(1, 4); // pip
        columnModel.setColumnWeight(2, 4); // vti
        columnModel.setColumnWeight(3, 4); // rr
        columnModel.setColumnWeight(4, 4); // fio2
        columnModel.setColumnWeight(5, 5); // peep
        columnModel.setColumnWeight(6, 5); // leak
//            columnModel.setColumnWeight(7, 4); // mve
        columnModel.setColumnWeight(7, 4); // i:e
        tableView.setColumnModel(columnModel);
    }
    }        
