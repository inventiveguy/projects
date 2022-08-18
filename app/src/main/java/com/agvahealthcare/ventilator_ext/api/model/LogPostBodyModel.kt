package com.agvahealthcare.ventilator_ext.api.model

data class LogPostBodyModel(
    val device: Device,
    val log: Log,
    val type: String,
    val version: String
)