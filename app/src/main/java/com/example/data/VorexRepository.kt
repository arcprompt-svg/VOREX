package com.example.data

import kotlinx.coroutines.flow.Flow

class VorexRepository(private val handoffDao: HandoffDao) {
    val allRecords: Flow<List<HandoffRecord>> = handoffDao.getAllRecordsFlow()

    suspend fun insert(record: HandoffRecord): Long {
        return handoffDao.insertRecord(record)
    }

    suspend fun update(record: HandoffRecord) {
        handoffDao.updateRecord(record)
    }

    suspend fun deleteById(id: Long) {
        handoffDao.deleteRecordById(id)
    }

    suspend fun clearAll() {
        handoffDao.clearAllRecords()
    }
}
