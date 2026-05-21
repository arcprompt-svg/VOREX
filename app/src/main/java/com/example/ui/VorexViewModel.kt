package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.HandoffRecord
import com.example.data.VorexRepository
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.GenerationConfig
import com.example.network.Part
import com.example.network.GeminiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import android.content.SharedPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VorexViewModel(
    private val repository: VorexRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    // Persistent Operator Name saved in SharedPreferences
    private val _operatorName = MutableStateFlow("VOREX Operator")
    val operatorName: StateFlow<String> = _operatorName.asStateFlow()

    init {
        _operatorName.value = sharedPreferences.getString("operator_name", "VOREX Operator") ?: "VOREX Operator"
    }

    fun saveOperatorName(newName: String) {
        _operatorName.value = newName
        sharedPreferences.edit().putString("operator_name", newName).apply()
    }

    // Observed from database
    val history: StateFlow<List<HandoffRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Log terminal output
    private val _terminalLogs = MutableStateFlow<List<String>>(
        listOf(
            "[System] VOREX & Promptcraft-Ai Orchestrator loaded successfully.",
            "[System] Listening for entity handoffs and operational payloads..."
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    // Interactive custom form state
    private val _senderProfile = MutableStateFlow("Profile 1: Research & Architect")
    val senderProfile: StateFlow<String> = _senderProfile.asStateFlow()

    private val _targetProfile = MutableStateFlow("Profile 2: DevOps & Execution")
    val targetProfile: StateFlow<String> = _targetProfile.asStateFlow()

    private val _customEntity = MutableStateFlow("Ecosystem Database Core")
    val customEntity: StateFlow<String> = _customEntity.asStateFlow()

    private val _customAttribute = MutableStateFlow("RoomPersistenceManager")
    val customAttribute: StateFlow<String> = _customAttribute.asStateFlow()

    private val _customContext = MutableStateFlow("Configuring local SQLite cache layer for high performance VOREX transitions")
    val customContext: StateFlow<String> = _customContext.asStateFlow()

    private val _customValue = MutableStateFlow("schemaVersion=1; exportSchema=false; journalMode=WRITE_AHEAD_LOGGING")
    val customValue: StateFlow<String> = _customValue.asStateFlow()

    private val _customLatentVariables = MutableStateFlow("concurrency_threshold_exceeded=false")
    val customLatentVariables: StateFlow<String> = _customLatentVariables.asStateFlow()

    private val _customActionRequired = MutableStateFlow("Construct optimized DB class and verify compilation with KSP compile_applet")
    val customActionRequired: StateFlow<String> = _customActionRequired.asStateFlow()

    // Run-time variables
    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private val _activeProfileIndex = MutableStateFlow(1) // 1 to 5
    val activeProfileIndex: StateFlow<Int> = _activeProfileIndex.asStateFlow()

    // Active continuous task flow
    private val _activeSagaTaskText = MutableStateFlow("Create an encrypted login passkey module with SQLite audits")
    val activeSagaTaskText: StateFlow<String> = _activeSagaTaskText.asStateFlow()

    // Last processed handoff object
    private val _lastPayloadJson = MutableStateFlow<String?>(null)
    val lastPayloadJson: StateFlow<String?> = _lastPayloadJson.asStateFlow()

    // API loading state
    private val _apiLoading = MutableStateFlow(false)
    val apiLoading: StateFlow<Boolean> = _apiLoading.asStateFlow()

    fun updateSenderProfile(p: String) { _senderProfile.value = p }
    fun updateTargetProfile(p: String) { _targetProfile.value = p }
    fun updateCustomEntity(e: String) { _customEntity.value = e }
    fun updateCustomAttribute(a: String) { _customAttribute.value = a }
    fun updateCustomContext(c: String) { _customContext.value = c }
    fun updateCustomValue(v: String) { _customValue.value = v }
    fun updateCustomLatentVariables(lv: String) { _customLatentVariables.value = lv }
    fun updateCustomActionRequired(ar: String) { _customActionRequired.value = ar }
    fun updateActiveSagaTask(task: String) { _activeSagaTaskText.value = task }
    fun selectActiveProfileIndex(index: Int) { _activeProfileIndex.value = index }

    // Log printer
    fun addTerminalLog(message: String) {
        val stamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val newList = _terminalLogs.value.toMutableList()
        newList.add(0, "[$stamp] $message") // show newer on top
        if (newList.size > 150) newList.removeAt(newList.lastIndex)
        _terminalLogs.value = newList
    }

    // Quick helper to format dates for logs in record list
    fun formatDateTime(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }

    fun deleteRecordId(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Trigger local repository cleanup
    fun clearLogsAndDB() {
        viewModelScope.launch {
            repository.clearAll()
            _terminalLogs.value = listOf(
                "[System] Archive log storage and entity handoffs purged.",
                "[System] Listening for active entity handoff payloads..."
            )
            _lastPayloadJson.value = null
            addTerminalLog("History cleared.")
        }
    }

    // Call live Gemini API with Retrofit or fallback gracefully to local design framework
    private suspend fun callGeminiAPI(systemInstructionText: String, prompt: String): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Log notice of simulation
            addTerminalLog("[Notice] Gemini API Key not set. Running simulated AI reasoning...")
            delay(1200) // realistic wait
            return generateMockAIReasoning(systemInstructionText, prompt)
        }

        return try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.5f, maxOutputTokens = 800),
                systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
            )
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No valid response text returned from model."
        } catch (e: Exception) {
            addTerminalLog("[Error API] ${e.message}. Switched to local fallback.")
            generateMockAIReasoning(systemInstructionText, prompt)
        }
    }

    // Executed when user creates or emits manual form payload
    fun fireManualHandoff() {
        if (_isSimulating.value) return
        viewModelScope.launch {
            _apiLoading.value = true
            val sender = _senderProfile.value
            val target = _targetProfile.value
            val entity = _customEntity.value
            val attribute = _customAttribute.value
            val context = _customContext.value
            val value = _customValue.value
            val latentVars = _customLatentVariables.value
            val actionReq = _customActionRequired.value

            addTerminalLog("Emit manual handoff: $sender ➔ $target")
            addTerminalLog("Scanning payload integrity via Atmospheric Security Check...")

            delay(600) // step animations

            // Handle Profile 4 Security intervention
            var isCleared = "Pending"
            var taskStat = "Requires_Action"
            var scanned = false
            var finalAiReasoning = ""

            // Profile 4 acts as Security validator
            if (sender == "Profile 4: Security & Identity Broker" || target == "Profile 4: Security & Identity Broker") {
                isCleared = "Approved"
                taskStat = "Completed"
                scanned = true
                addTerminalLog("[Sec-Broker] Active/Passive Verification complete. Admin token mapped.")
            } else {
                // If it goes between profiles, let's run a security scan
                addTerminalLog("[Sec-Broker] Guardrail scanner initialized. Scanning values: '$value'")
                val securityReviewPrompt = """
                    You are Profile 4: Security & Identity Broker. You operate the Atmospheric Security Model.
                    Conduct an instant automated security review of this payload context and value:
                    Entity: $entity
                    Context: $context
                    Value: $value
                    Verify if it violates security controls, leaks plain-text API keys, has SQL injections, or passes passkey parameters.
                    State clearly whether it should be 'Approved' or 'Rejected' on line 1, followed by short bullet points explaining why.
                """.trimIndent()

                val result = callGeminiAPI(
                    "You are Profile 4, enforcement gateway. Maintain a high security posture.",
                    securityReviewPrompt
                )
                finalAiReasoning = result
                isCleared = if (result.contains("Reject", ignoreCase = true) || result.contains("Violation", ignoreCase = true)) {
                    "Rejected"
                } else {
                    "Approved"
                }
                taskStat = if (isCleared == "Approved") "Requires_Action" else "Error_Encountered"
                scanned = true
                addTerminalLog("[Sec-Broker] Scan Result: $isCleared.")
            }

            // If approved, let's also invoke target profile AI reasoning simulation
            if (isCleared == "Approved" && sender != "Profile 4: Security & Identity Broker") {
                addTerminalLog("Invoking Target [${target}] to evaluate the payload...")
                val designPrompt = """
                    You are simulating ${target} of the Multi-Agent VOREX system.
                    You just received a handoff from ${sender} regarding:
                    Entity: $entity | Attribute: $attribute
                    Context: $context
                    Input value: $value
                    Latent variables: $latentVars
                    Instruction: $actionReq
                    
                    Explain how you execute this action in 2-3 logical sentences and structure the technical outcome inside standard JSON format or code statements.
                """.trimIndent()

                val targetResult = callGeminiAPI(
                    "You are a specialized ecosystem agent module of VOREX. Do not output conversational preamble.",
                    designPrompt
                )
                finalAiReasoning = if (finalAiReasoning.isEmpty()) targetResult else "=== SECURITY REPORT ===\n$finalAiReasoning\n\n=== EXECUTION LOG ===\n$targetResult"
            }

            val record = HandoffRecord(
                senderProfile = sender,
                targetProfile = target,
                clearanceStatus = isCleared,
                taskStatus = taskStat,
                entity = entity,
                attribute = attribute,
                context = context,
                payloadValue = value,
                latentVariables = latentVars,
                actionRequired = actionReq,
                isSecurityScanned = scanned,
                aiReasoning = finalAiReasoning
            )

            val recordId = repository.insert(record)
            addTerminalLog("[Success] Record #$recordId stored in Room Database.")

            // Format JSON block for viewer
            _lastPayloadJson.value = buildHandoffJsonBlock(record)
            _apiLoading.value = false
        }
    }

    // Automates a complete VOREX Multi-Agent saga step-by-step
    fun runAutomatedSaga() {
        if (_isSimulating.value) return
        viewModelScope.launch {
            _isSimulating.value = true
            val sagaTask = _activeSagaTaskText.value
            _terminalLogs.value = listOf(
                "[Saga] Initializing Multi-Agent Auto Run Sequence for task: '$sagaTask'",
                "[Saga] Multi-Profile handoffs will execute sequentially according to VOREX framework rules!"
            )
            addTerminalLog("Current Time UTC: 2026-05-21T21:25:00")

            // --- STEP 1: Research & Architect (R&A) creates Blueprint ---
            _activeProfileIndex.value = 1
            addTerminalLog("[R&A] Modeling entity relations and architectural blueprint...")
            val p1Prompt = "Create a robust technical architectural blueprint for: '$sagaTask'. Define entities, recommended attributes, and flow."
            val p1Response = callGeminiAPI("You are Profile 1: Research & Architect. Design Ground Truth blueprints.", p1Prompt)

            val step1Record = HandoffRecord(
                senderProfile = "Profile 1: Research & Architect",
                targetProfile = "Profile 4: Security & Identity Broker", // Goes to Security control first!
                clearanceStatus = "Pending",
                taskStatus = "Requires_Action",
                entity = sagaTask,
                attribute = "ArchitecturalBlueprint",
                context = "Evaluating blueprint layout before code execution",
                payloadValue = p1Response,
                latentVariables = "experience_density=0.88; K_PHIL_level=high",
                actionRequired = "Review security guardrails on dynamic inputs and passkeys",
                isSecurityScanned = false,
                aiReasoning = "Structural blueprint designed successfully."
            )
            val id1 = repository.insert(step1Record)
            _lastPayloadJson.value = buildHandoffJsonBlock(step1Record)
            addTerminalLog("[R&A] Structural Blueprint complete. File stored. Saved Record #$id1.")
            delay(3000)

            // --- STEP 2: Security & Identity Broker scans layout ---
            _activeProfileIndex.value = 4
            addTerminalLog("[Security] Gateway intercepted payload #$id1. Initiating static scan ...")
            val p4Prompt = "You are Profile 4: Security Broker. Review this blueprint: ${step1Record.payloadValue}. State if compliant. Output 'Approved' in capital letters."
            val p4Response = callGeminiAPI("You are Profile 4: Security & Identity Broker. Keep it secure.", p4Prompt)

            val updatedStep1 = step1Record.copy(
                id = id1,
                clearanceStatus = "Approved",
                taskStatus = "Completed",
                isSecurityScanned = true,
                aiReasoning = "=== SECURITY SYSTEM SCAN COMPLETED ===\n$p4Response"
            )
            repository.update(updatedStep1)
            _lastPayloadJson.value = buildHandoffJsonBlock(updatedStep1)
            addTerminalLog("[Security] Signature validated. Security Clearance status: APPROVED. Dispatching to execution module.")
            delay(3000)

            // --- STEP 3: DevOps & Execution builds the code ---
            _activeProfileIndex.value = 2
            addTerminalLog("[DevOps] Transforming blueprint into functional code script...")
            val p2Prompt = "Translate this blueprint: ${step1Record.payloadValue} into clean, robust Kotlin/Compose source segments. Write direct executable files."
            val p2Response = callGeminiAPI("You are Profile 2: DevOps & Execution. Generate clean, robust code statements.", p2Prompt)

            val step2Record = HandoffRecord(
                senderProfile = "Profile 2: DevOps & Execution",
                targetProfile = "Profile 3: Data & Ecosystem Manager",
                clearanceStatus = "Approved",
                taskStatus = "Requires_Action",
                entity = sagaTask,
                attribute = "KotlinModuleCode",
                context = "DevOps packaged executable modules. Verification: Passed.",
                payloadValue = p2Response,
                latentVariables = "code_conformance=1.0; latency_ms=120",
                actionRequired = "Establish database schemas and index local variable probabilities.",
                isSecurityScanned = true,
                aiReasoning = "Generated code templates according to architectural standards."
            )
            val id2 = repository.insert(step2Record)
            _lastPayloadJson.value = buildHandoffJsonBlock(step2Record)
            addTerminalLog("[DevOps] Code creation finished. Deployed compiled module. Record #$id2 generated.")
            delay(3000)

            // --- STEP 4: Data & Ecosystem Manager configures database ---
            _activeProfileIndex.value = 3
            addTerminalLog("[Data] Schema registration active. Mapping relational entities to Sqlite db...")
            val p3Prompt = "You are Profile 3: Data Manager. Map the inputs of this DevOps code payload: ${step2Record.payloadValue} to a Room Database Schema list. Write the Entity structures."
            val p3Response = callGeminiAPI("You are Profile 3: Data Manager. Design Room databases.", p3Prompt)

            val step3Record = HandoffRecord(
                senderProfile = "Profile 3: Data & Ecosystem Manager",
                targetProfile = "Profile 5: External Operations & API",
                clearanceStatus = "Approved",
                taskStatus = "Requires_Action",
                entity = sagaTask,
                attribute = "RoomSQLiteSchemas",
                context = "Active schemas compiled with modern KSP Room processors successfully.",
                payloadValue = p3Response,
                latentVariables = "persistence_mode=RoomDB; transaction_safe=true",
                actionRequired = "Trigger webhook status notifications on API deployment",
                isSecurityScanned = true,
                aiReasoning = "Created database mappings for persistent attributes."
            )
            val id3 = repository.insert(step3Record)
            _lastPayloadJson.value = buildHandoffJsonBlock(step3Record)
            addTerminalLog("[Data] Transaction completed. SQLite database structures mapped. Record #$id3 stored.")
            delay(3000)

            // --- STEP 5: External Operations triggers webhooks and finishes Saga ---
            _activeProfileIndex.value = 5
            addTerminalLog("[External] Invoking webhook gateways and syncing logs...")
            val p5Prompt = "You are Profile 5. Output a sample JSON block representing webhook body notifying successful deployment of: $sagaTask"
            val p5Response = callGeminiAPI("You are Profile 5. Interface with API webhooks.", p5Prompt)

            val step4Record = HandoffRecord(
                senderProfile = "Profile 5: External Operations & API",
                targetProfile = "Profile 3: Data & Ecosystem Manager", // Handoff to Data to log response!
                clearanceStatus = "Approved",
                taskStatus = "Completed",
                entity = sagaTask,
                attribute = "ExternalWebhookNotification",
                context = "Dispatched webhook payload logs back to database supervisor",
                payloadValue = p5Response,
                latentVariables = "network_latency_ms=45; status_code=200",
                actionRequired = "Persist execution audit and terminate multi-agent stream",
                isSecurityScanned = true,
                aiReasoning = "Triggered API endpoints and saved callback verification."
            )
            val id4 = repository.insert(step4Record)
            _lastPayloadJson.value = buildHandoffJsonBlock(step4Record)
            addTerminalLog("[External] Cloud triggers activated. Callback returned status 200.")
            delay(1500)

            _activeProfileIndex.value = 1
            addTerminalLog("[Saga-Success] Multi-Agent Saga completed successfully! All records verified and locked in local SQLite storage.")
            _isSimulating.value = false
        }
    }

    private fun buildHandoffJsonBlock(record: HandoffRecord): String {
        return """
        {
          "handoff_protocol": {
            "sender_profile": "${record.senderProfile}",
            "target_profile": "${record.targetProfile}",
            "clearance_status": "${record.clearanceStatus}",
            "task_status": "${record.taskStatus}"
          },
          "e_eacv_context": {
            "entity": "${record.entity.replace("\"", "\\\"").replace("\n", "\\n")}",
            "attribute": "${record.attribute.replace("\"", "\\\"").replace("\n", "\\n")}",
            "context": "${record.context.replace("\"", "\\\"").replace("\n", "\\n")}",
            "value": "${record.payloadValue.replace("\"", "\\\"").replace("\n", "\\n")}"
          },
          "latent_variables": "${record.latentVariables.replace("\"", "\\\"").replace("\n", "\\n")}",
          "action_required": "${record.actionRequired.replace("\"", "\\\"").replace("\n", "\\n")}"
        }
        """.trimIndent()
    }

    private fun generateMockAIReasoning(systemInstructionText: String, prompt: String): String {
        return when {
            systemInstructionText.contains("Profile 1") || prompt.contains("Profile 1") -> {
                """
                === SIMULATED BLUEPRINT [Profile 1] ===
                - SYSTEM MODELING ARCHITECTURE:
                  Entity: "VoreX Core Node" with experience density map.
                  Aesthetic layout: Spaces and borders aligned precisely utilizing negative space.
                  Local Database Engine: Room SQLite layer using reactive Flows.
                - SCHEMA DECREE:
                  1. active_variables (variable_id Text PRIMARY KEY, value_type Text, value Text)
                  2. state_records (record_id Integer PRIMARY KEY, sender Text, target Text)
                - STABILITY CONTROL: Enabled passive encryption parameters dynamically.
                """.trimIndent()
            }
            systemInstructionText.contains("Profile 2") || prompt.contains("Profile 2") -> {
                """
                === SIMULATED DEVOPS OUTLOG [Profile 2] ===
                ```kotlin
                // Jetpack Compose Screen representation
                @Composable
                fun VorexConsole(modifier: Modifier = Modifier) {
                  LazyColumn(
                    modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
                  ) {
                    item { StatusHeader(status = "Saga Executing") }
                    items(records) { record -> HandoffCard(record) }
                  }
                }
                ```
                - Build command executed: gradle compileApplet compileDebugKotlin
                - Return Code: 0 (Success)
                """.trimIndent()
            }
            systemInstructionText.contains("Profile 3") || prompt.contains("Profile 3") -> {
                """
                === PERSISTENCE INDEXING MAP [Profile 3] ===
                {
                  "database": "vorex_database.db",
                  "tables": [ "handoff_records" ],
                  "index_variables": {
                    "experience_density": 0.95,
                    "latent_probabilities": [0.01, 0.05, 0.12]
                  },
                  "integrity_compliance": "Fully compliant with Room entity guidelines"
                }
                """.trimIndent()
            }
            systemInstructionText.contains("Profile 4") || prompt.contains("Profile 4") -> {
                """
                === SYSTEM SECURITY VERIFICATION [Profile 4] ===
                Approved
                - Passive identity passkey matched.
                - Sanitized payload attributes. No SQL/NoSQL injection signatures discovered.
                - No plain-text API keys or client certificates leaked.
                - Clearance status: APPROVED. Outgoing dispatch validated successfully.
                """.trimIndent()
            }
            else -> {
                """
                === SIMULATED RESPONSE ===
                [Status] API Webhook mapped correctly.
                [Payload Sent] 201 Created
                [Gateway Callback] Handled by VOREX API controller.
                """.trimIndent()
            }
        }
    }
}

class VorexViewModelFactory(
    private val repository: VorexRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VorexViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VorexViewModel(repository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
