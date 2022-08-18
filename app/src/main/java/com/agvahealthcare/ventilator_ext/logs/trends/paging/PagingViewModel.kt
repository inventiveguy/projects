package com.agvahealthcare.ventilator_ext.logs.trends.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.agvahealthcare.ventilator_ext.logging.DataLogger

class PagingViewModel constructor(private val dataLogger: DataLogger): ViewModel(){
    val listOfData = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(2), pagingSourceFactory = {
            PagingDataSource(dataLogger)
        }, initialKey = 1
    ) .liveData
        .cachedIn(viewModelScope)
}