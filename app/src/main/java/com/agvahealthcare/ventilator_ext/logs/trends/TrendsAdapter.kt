package com.agvahealthcare.ventilator_ext.logs.trends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R

class TrendsAdapter : RecyclerView.Adapter<TrendsAdapter.TrendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trends_data, parent, false)

        return TrendsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrendsViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

    class TrendsViewHolder(row: View) : RecyclerView.ViewHolder(row) {

    }
}