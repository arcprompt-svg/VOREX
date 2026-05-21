package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HandoffDao {
    @Query("SELECT * FROM handoff_records ORDER BY timestamp DESC")
    fun getAllRecordsFlow(): Flow<List<HandoffRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HandoffRecord): Long

    @Update
    suspend fun updateRecord(record: HandoffRecord)

    @Query("DELETE FROM handoff_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM handoff_records")
    suspend fun clearAllRecords()
}
