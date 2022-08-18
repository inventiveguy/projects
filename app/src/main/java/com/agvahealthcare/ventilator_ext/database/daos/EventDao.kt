package com.agvahealthcare.ventilator_ext.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel

// UserDao contains the methods used for accessing the database, including queries.
@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) // <- Annotate the 'addUser' function below. Set the onConflict strategy to IGNORE so if exactly the same user exists, it will just ignore it.
    suspend fun addEventData(eventDataModel: EventDataModel)

    @Update
    suspend fun updateEventData(eventDataModel: EventDataModel)

    @Delete
    suspend fun deleteEventData(eventDataModel: EventDataModel)

    @Query("DELETE FROM event_table")
    suspend fun deleteAllEvents()

    @Query("SELECT * FROM event_table ORDER BY id DESC") // <- Add a query to fetch all users (in user_table) in ascending order by their IDs.
    fun readAllEvents(): LiveData<List<EventDataModel>> // <- This means function return type is List. Specifically, a List of Users.
}