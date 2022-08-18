
package com.agvahealthcare.ventilator_ext

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val ventBatteryLevel = MutableLiveData<Int>()
    fun setVentBatteryLevel(batteryLevel: Int) {
        ventBatteryLevel.value = batteryLevel
    }

    val ventBatteryHealth = MutableLiveData<Int>()
    fun setVentBatteryHealth(ventBattHealth: Int) {
        ventBatteryHealth.value = ventBattHealth
    }

    val ventBatteryRemainingTime = MutableLiveData<Int>()
    fun setVentBatteryRemainingTime(ventBR: Int) {
        ventBatteryRemainingTime.value = ventBR
    }

    val isBatteryConnected=MutableLiveData<Boolean>()
    fun setBAtteryConnectedFlag(isConnected:Boolean){
        isBatteryConnected.value=isConnected
    }
}