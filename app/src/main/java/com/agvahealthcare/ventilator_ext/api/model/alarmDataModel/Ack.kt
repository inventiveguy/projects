package com.agvahealthcare.ventilator_ext.api.model.alarmDataModel

import com.google.gson.annotations.SerializedName


data class Ack (

    @SerializedName("msg"       ) var msg       : String? = null,
    @SerializedName("code"      ) var code      : String? = null,
    @SerializedName("timestamp" ) var timestamp : String? = null

)