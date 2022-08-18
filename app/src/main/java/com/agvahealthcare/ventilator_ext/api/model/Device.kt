package com.agvahealthcare.ventilator_ext.api.model

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("did") var did: String? = "10:EC:81:1C:12:71",
    @SerializedName("name") var name: String? = "Lenovo Tab 1",
    @SerializedName("manufacturer") var manufacturer: String? = "Agva Healthcare",
    @SerializedName("battery") var battery: String? = "Agva Healthcare",
    @SerializedName("os") var os: Os? = Os()
)