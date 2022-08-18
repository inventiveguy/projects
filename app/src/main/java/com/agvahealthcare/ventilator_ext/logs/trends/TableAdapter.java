package com.agvahealthcare.ventilator_ext.logs.trends;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class TableAdapter  extends TableDataAdapter {
   private Context context;


    public TableAdapter(Context context, List data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        View renderedView = null;
        switch (rowIndex) {
            case 0:
                renderedView = renderCell("10:30");

                break;
            case 1:
                renderedView = renderCell("12");
                break;
            case 2:
                Log.i("hello",String.valueOf(rowIndex));
                renderedView = renderCell("30");
                break;
            case 3:

                renderedView = renderCell("34");
                break;
            case 4:
                renderedView = renderCell("98");
                break;
            case 5:
                renderedView = renderCell("24");
                break;
            case 6:
                renderedView = renderCell("52");
                break;

            case 7:
                renderedView = renderCell("26");
                break;

            case 8:
                renderedView = renderCell("76");
                break;
        }
        return renderedView;
    }
    private View renderCell(String content){
        TextView tv = new TextView(this.getContext());
        tv.setText(content);
        tv.setPadding(10, 10, 20, 10);
        tv.setTextSize(20f);
        tv.setWidth(10);
        tv.setTextColor(context.getResources().getColor(android.R.color.black));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tv;
    }
}
