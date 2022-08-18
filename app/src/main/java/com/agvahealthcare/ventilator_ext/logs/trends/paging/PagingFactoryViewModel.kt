package com.agvahealthcare.ventilator_ext.logs.trends.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agvahealthcare.ventilator_ext.logging.DataLogger
import java.lang.IllegalArgumentException

class PagingFactoryViewModel constructor(private val dataLogger: DataLogger): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PagingViewModel::class.java)){
            PagingViewModel(this.dataLogger) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}