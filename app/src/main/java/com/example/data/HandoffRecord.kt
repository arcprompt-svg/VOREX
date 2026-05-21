package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "handoff_records")
data class HandoffRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderProfile: String,
    val targetProfile: String,
    val clearanceStatus: String, // "Approved", "Pending", "Rejected"
    val taskStatus: String, // "Completed", "Requires_Action", "Error_Encountered"
    val entity: String,
    val attribute: String,
    val context: String,
    val payloadValue: String, // named payloadValue to avoid database keyword conflicts
    val latentVariables: String,
    val actionRequired: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSecurityScanned: Boolean = false,
    val aiReasoning: String? = null
)
