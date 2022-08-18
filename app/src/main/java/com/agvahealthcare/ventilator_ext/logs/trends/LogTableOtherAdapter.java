package com.agvahealthcare.ventilator_ext.logs.trends;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agvahealthcare.ventilator_ext.R;
import com.agvahealthcare.ventilator_ext.database.entities.DataTableModel;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class LogTableOtherAdapter extends TableDataAdapter<DataTableModel> {
    private Context context;
    private List<DataTableModel> dataList;

    public LogTableOtherAdapter(Context context, List<DataTableModel> data) {
        super(context, data);
        this.context = context;
        this.dataList = dataList;
        Log.d("Modallist", dataList.size()+"");
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final DataTableModel modal=dataList.get(columnIndex);
        View renderedView=null;
        switch (rowIndex) {
            case 0:
                //renderedView = renderCell(AppUtils.dateTimeFormatter.format(new Date(modal.getTime())));
                //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                renderedView = renderCell(modal.getTime());

                //renderedView = renderCell(LocalDateTime.parse(modal.getTime(),formatter));
                break;
            case 1:
                renderedView = renderCell(String.valueOf((int)(modal.getPressure())));
                break;
            case 2:
                renderedView = renderCell(String.valueOf((int)modal.getVolume()));
                break;
            case 3:
                renderedView = renderCell(String.valueOf((int)modal.getRr()));
                break;
            case 4:
                renderedView = renderCell(String.valueOf((int)modal.getFiO2()));
                break;
            case 5:
                renderedView = renderCell(String.valueOf((int)modal.getPeep()));
                break;
            case 6:
                renderedView = renderCell(String.valueOf((int)modal.getLeak()));
                break;

            case 7:
                renderedView = renderCell(String.format("%.2f", modal.getIeratio()));
                break;

            case 8:
                renderedView = renderCell(String.valueOf((int)modal.getMve()));
                break;
        }
        return renderedView;
    }
    private View renderCell(String content){
        TextView tv = new TextView(this.getContext());
        tv.setText(content);
        tv.setPadding(10, 10, 20, 10);
        tv.setTextSize(12f);
        tv.setTextColor(context.getResources().getColor(android.R.color.black));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tv;
    }
    private View renderColoredCell(String amount){
        TextView tv = (TextView) renderCell(amount);
        tv.setTextSize(14f);
        tv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        return tv;
    }
}
