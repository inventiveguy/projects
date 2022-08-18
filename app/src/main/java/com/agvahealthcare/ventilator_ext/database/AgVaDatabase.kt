package com.agvahealthcare.ventilator_ext.database

import android.content.Context
import androidx.room.*
import com.agvahealthcare.ventilator_ext.database.daos.AlarmDao
import com.agvahealthcare.ventilator_ext.database.daos.EventDao
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel


@Database(
    entities = [(EventDataModel::class),AlarmDBModel::class],
    version = 1,                // <- Database version
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AgVaDatabase: RoomDatabase() { // <- Add 'abstract' keyword and extends RoomDatabase

    abstract fun eventDao(): EventDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile private var INSTANCE: AgVaDatabase? = null

        fun getInstance(ctx: Context): AgVaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(ctx).also { INSTANCE = it }
            }


        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AgVaDatabase::class.java, "agva_pro.db")
                .fallbackToDestructiveMigration()
                .build()

        fun destroyInstance(){
            INSTANCE = null
        }
    }}