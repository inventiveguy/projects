package com.agvahealthcare.ventilator_ext.utility.utils

import android.net.Uri
import com.agvahealthcare.ventilator_ext.R

class AlarmConfiguration {

    companion object{
      /*  @Deprecated("Deprecated medium level alarm list")
        var mediumLevelAck = listOf(
            Configs.ACK_CODE_12,
            Configs.ACK_CODE_13,
            Configs.ACK_CODE_16,
            Configs.ACK_CODE_17,
            Configs.ACK_CODE_18,
            Configs.ACK_CODE_19,
            Configs.ACK_CODE_30
        )

        @Deprecated("Deprecated low level alarm list")
        private var lowLevelAck = listOf(

            Configs.ACK_CODE_10,
            Configs.ACK_CODE_11,
            Configs.ACK_CODE_14,
            Configs.ACK_CODE_15,
            Configs.ACK_CODE_35,
            Configs.ACK_CODE_50,
            Configs.ACK_CODE_62,
            Configs.ACK_CODE_68,
            Configs.ACK_CODE_80,
        )

        @Deprecated("Deprecated high level alarm list")
        var highLevelAck = listOf<String>()

        @Deprecated("Deprecated critical level alarm list")
        var criticalLevelAck = listOf(
            Configs.ACK_CODE_0,
            Configs.ACK_CODE_5,
        )*/

        private val controlLimitAlarms= listOf(
            Configs.ALARM_PIP,
            Configs.ALARM_VTE,
            Configs.ALARM_RAW_VOLUME,
            Configs.ALARM_RR,
            Configs.ALARM_PEEP,
            Configs.ALARM_MVI,
            Configs.ALARM_TRIGGER,
            Configs.ALARM_RESPIRATORY_PHASE,
            Configs.ALARM_TITOT,


            )


        private fun isAckValid(ack: String) = ack.startsWith(Configs.PREFIX_ACK)
                && ack.replace(Configs.PREFIX_ACK, "").toIntOrNull()?.let { it in 0..6000 } == true

        @JvmStatic
        fun getColor(alarm: String): Int{
            if(alarm.startsWith(Configs.PREFIX_ACK)) {
                alarm.replace(Configs.PREFIX_ACK, "").toIntOrNull()?.let {
                    return when (it) {
                        in 0..320 -> {
                            R.color.ack_yellow
                        }
                        in 320..640 -> {
                            R.color.ack_red
                        }
                        in 640..960 -> {
                            R.color.ack_red
                        }
                        else -> R.color.ack_red
                    }
                }

            } else{
                if(alarm in controlLimitAlarms) return  R.color.black
            }

            return R.color.black
        }


        @JvmStatic
        fun getPriority(alarm:String): Configs.AlarmType{

            if(alarm.startsWith(Configs.PREFIX_ACK)) {
                 alarm.replace(Configs.PREFIX_ACK, "").toIntOrNull()?.let {
                     return when (it) {
                        in 0..320 -> {
                            Configs.AlarmType.ALARM_LOW_LEVEL
                        }
                        in 320..640 -> {
                            Configs.AlarmType.ALARM_MEDIUM_LEVEL
                        }
                        in 640..960 -> {
                            Configs.AlarmType.ALARM_HIGH_LEVEL
                        }
                        else -> Configs.AlarmType.ALARM_NO_LEVEL
                    }
                }

            } else {
                if(alarm in controlLimitAlarms) return Configs.AlarmType.ALARM_MEDIUM_LEVEL
            }


            return Configs.AlarmType.ALARM_NO_LEVEL
        }


        @JvmStatic
        fun getAckType(ack: String): Configs.AckType {
            return ack.takeIf { isAckValid(it) }?.replace(Configs.PREFIX_ACK, "")?.toIntOrNull()?.let {
                   return if (it < 5000){
                       val tensDigit = (it %  100) / 10
                       if(tensDigit % 2 == 0)  Configs.AckType.ACK
                       else Configs.AckType.NACK
                   } else Configs.AckType.OP_ACK
                } ?: Configs.AckType.INVALID_ACK
        }

        @JvmStatic
        fun getAckFor(ack: String): String?{
            return ack.replace(Configs.PREFIX_ACK, "").toIntOrNull()?.let {
                Configs.PREFIX_ACK + (it - 10)
            }
        }

        @JvmStatic
        fun getNackFor(ack: String): String?{
            return ack.replace(Configs.PREFIX_ACK, "").toIntOrNull()?.let {
                Configs.PREFIX_ACK + (it + 10)
            }
        }



        @JvmStatic
        fun getAlarmUri(priority: Configs.AlarmType): Uri? {
            return when (priority) {

                Configs.AlarmType.ALARM_NO_LEVEL -> null
                Configs.AlarmType.ALARM_LOW_LEVEL -> Configs.URI_ALARM_LOW_LEVEL
                Configs.AlarmType.ALARM_MEDIUM_LEVEL -> Configs.URI_ALARM_HIGH_LEVEL
                Configs.AlarmType.ALARM_HIGH_LEVEL -> Configs.URI_ALARM_HIGH_LEVEL
//                else -> Configs.URI_ALARM_BATTERY_LOW

            }
        }
    }
}