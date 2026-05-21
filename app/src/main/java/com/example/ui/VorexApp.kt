package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HandoffRecord
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VorexApp(viewModel: VorexViewModel) {
    val history by viewModel.history.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val activeProfileIndex by viewModel.activeProfileIndex.collectAsState()
    val lastPayloadJson by viewModel.lastPayloadJson.collectAsState()
    val apiLoading by viewModel.apiLoading.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Guided Saga, 1 = Manual Payload Customizer
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSimulating) SignalRed else SignalGreen,
                            modifier = Modifier
                                .size(10.dp)
                                .border(1.5.dp, Color.White, CircleShape)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOREX",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp,
                                color = ElegantPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ":: orchestrator console",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = ElegantOnSurface
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearLogsAndDB() },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear History",
                            tint = SignalRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElegantSurface,
                    titleContentColor = ElegantOnBackground
                ),
                modifier = Modifier.border(0.dp, Color.Transparent)
            )
        },
        containerColor = ElegantBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ElegantBackground)
        ) {
            // Live UTC status band
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElegantSurface)
                    .border(BorderStroke(1.dp, ElegantBorder))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UTC CLOCK: 2026-05-21 21:25:00",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = ElegantOnSurface
                )
                Text(
                    text = if (isSimulating) "MAS STATUS: RUNNING_saga" else "MAS STATUS: LISTEN",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSimulating) ElegantPrimary else SignalGreen
                )
            }

            // Body container: Split layout simulation using LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Intro and system profile state map visualizer
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val opName by viewModel.operatorName.collectAsState()
                    var textInputName by remember(opName) { mutableStateOf(opName) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                        border = BorderStroke(1.dp, ElegantBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "👤 OPERATOR PRIVILEGE ENFORCEMENT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Active Terminal Authority: $opName",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElegantOnBackground,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            OutlinedTextField(
                                value = textInputName,
                                onValueChange = { textInputName = it },
                                label = { Text("Enter Operator Name") },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantPrimary,
                                    unfocusedBorderColor = ElegantBorder,
                                    focusedLabelColor = ElegantPrimary,
                                    unfocusedLabelColor = ElegantOnSurface,
                                    focusedTextColor = ElegantOnBackground,
                                    unfocusedTextColor = ElegantOnBackground
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("operator_name_input")
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.saveOperatorName(textInputName)
                                    viewModel.addTerminalLog("Operator name updated & persisted as: $textInputName")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElegantPrimary,
                                    contentColor = ElegantOnPrimary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("save_operator_button")
                            ) {
                                Text("SAVE OPERATOR PATH", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text(
                        text = "VOREX E-EACV Multi-Agent System State Map:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantOnSurface
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Displays the 5 Profiles reactively
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        ProfileBlock(
                            id = 1,
                            name = "R&A",
                            role = "Research",
                            icon = Icons.Default.Search,
                            isActive = activeProfileIndex == 1,
                            colorValue = NeonCyan
                        )
                        ProfileBlock(
                            id = 4,
                            name = "Broker",
                            role = "Security",
                            icon = Icons.Default.Lock,
                            isActive = activeProfileIndex == 4,
                            colorValue = ElectricPurple
                        )
                        ProfileBlock(
                            id = 2,
                            name = "Exec",
                            role = "DevOps",
                            icon = Icons.Default.Build,
                            isActive = activeProfileIndex == 2,
                            colorValue = SignalGreen
                        )
                        ProfileBlock(
                            id = 3,
                            name = "Manager",
                            role = "Data",
                            icon = Icons.Default.List,
                            isActive = activeProfileIndex == 3,
                            colorValue = NeonCyan
                        )
                        ProfileBlock(
                            id = 5,
                            name = "Web",
                            role = "API Ops",
                            icon = Icons.Default.Share,
                            isActive = activeProfileIndex == 5,
                            colorValue = ElectricPurple
                        )
                    }
                }

                // Selector Tabs
                item {
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = ElegantSurface,
                        contentColor = ElegantPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                color = ElegantPrimary
                            )
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, ElegantBorder, RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("🛰️ Guided Saga (Auto)") }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("🎛️ Manual Dispatcher") }
                        )
                    }
                }

                // Dynamic Workspace Content
                item {
                    WorkspaceSection(
                        activeTab = activeTab,
                        viewModel = viewModel,
                        isSimulating = isSimulating,
                        apiLoading = apiLoading,
                        onDismissFocus = { focusManager.clearFocus() }
                    )
                }

                // Last Output Payload Viewer (JSON structure E-EACV)
                if (lastPayloadJson != null) {
                    item {
                        PayloadJSONViewer(jsonStr = lastPayloadJson!!)
                    }
                }

                // Terminals Logs (Scroll box)
                item {
                    TerminalConsoleLogBox(logs = terminalLogs)
                }

                // History logs header
                item {
                    Text(
                        text = "ARCHIVED TRANSITIONS LOG STORE (${history.size} records in SQLite)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SteelGray
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // List items from Room Database
                if (history.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            border = BorderStroke(0.5.dp, CyberSurfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recorded handoff transits in local storage.\nSimulate a saga or fire manual handoffs.",
                                    textAlign = TextAlign.Center,
                                    color = SteelGray,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(history, key = { it.id }) { record ->
                        HistoryRecordRow(
                            record = record,
                            viewModel = viewModel,
                            onDelete = {
                                viewModel.addTerminalLog("Deleting Handoff Record #${record.id}")
                                viewModel.deleteRecordId(record.id)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileBlock(
    id: Int,
    name: String,
    role: String,
    icon: ImageVector,
    isActive: Boolean,
    colorValue: Color
) {
    val pulsate = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by pulsate.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaPulse"
    )

    val scaleValue by pulsate.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val borderAndBgColor by animateColorAsState(
        targetValue = if (isActive) colorValue else ElegantBorder,
        animationSpec = tween(400),
        label = "colorAnim"
    )

    Box(
        modifier = Modifier
            .height(58.dp)
            .width(106.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ElegantSurface)
            .border(
                BorderStroke(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) borderAndBgColor else ElegantBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isActive) colorValue else ElegantOnSurface,
                modifier = Modifier
                    .size(20.dp)
                    .alpha(if (isActive) alphaAnim else 0.7f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) colorValue else ElegantOnBackground,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ElegantOnSurface,
                        fontSize = 9.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(6.dp)
                    .background(colorValue, CircleShape)
            )
        }
    }
}

@Composable
fun WorkspaceSection(
    activeTab: Int,
    viewModel: VorexViewModel,
    isSimulating: Boolean,
    apiLoading: Boolean,
    onDismissFocus: () -> Unit
) {
    if (activeTab == 0) {
        // Guided Saga
        val activeSagaTaskText by viewModel.activeSagaTaskText.collectAsState()

        Card(
            colors = CardDefaults.cardColors(containerColor = ElegantSurface),
            border = BorderStroke(1.dp, ElegantBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🛰️ VOREX AUTO FLOW STREAM",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ElegantPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Provide a continuous task and let the Multi-Agent System automate blueprints, security scans, execution statements, persistence design, and API relays in sequence.",
                    style = MaterialTheme.typography.bodySmall.copy(color = ElegantOnSurface)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = activeSagaTaskText,
                    onValueChange = { viewModel.updateActiveSagaTask(it) },
                    label = { Text("Task Directive / Architectural Goal") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantPrimary,
                        unfocusedBorderColor = ElegantBorder,
                        focusedLabelColor = ElegantPrimary,
                        unfocusedLabelColor = ElegantOnSurface,
                        focusedTextColor = ElegantOnBackground,
                        unfocusedTextColor = ElegantOnBackground
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saga_input_field"),
                    maxLines = 3,
                    enabled = !isSimulating
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isSimulating) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            color = ElegantPrimary,
                            trackColor = ElegantBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agents are collaborating actively ...",
                            color = ElegantPrimary,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onDismissFocus()
                            viewModel.runAutomatedSaga()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantPrimary,
                            contentColor = ElegantOnPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("init_auto_run_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElegantOnPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INITIALIZE AUTO OPERATOR STREAM",
                            fontWeight = FontWeight.Bold,
                            color = ElegantOnPrimary
                        )
                    }
                }
            }
        }
    } else {
        // Manual Payload Customizer
        val senderProfile by viewModel.senderProfile.collectAsState()
        val targetProfile by viewModel.targetProfile.collectAsState()
        val customEntity by viewModel.customEntity.collectAsState()
        val customAttribute by viewModel.customAttribute.collectAsState()
        val customContext by viewModel.customContext.collectAsState()
        val customValue by viewModel.customValue.collectAsState()
        val customLatentVariables by viewModel.customLatentVariables.collectAsState()
        val customActionRequired by viewModel.customActionRequired.collectAsState()

        val profiles = listOf(
            "Profile 1: Research & Architect",
            "Profile 2: DevOps & Execution",
            "Profile 3: Data & Ecosystem Manager",
            "Profile 4: Security & Identity Broker",
            "Profile 5: External Operations & API"
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = ElegantSurface),
            border = BorderStroke(1.dp, ElegantBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🎛️ COPROCESSED E-EACV DISPATCHER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ElegantPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manually forge an Enterprise-Entity Attribute Context Value payload and designate custom transitions for sandbox audit.",
                    style = MaterialTheme.typography.bodySmall.copy(color = ElegantOnSurface)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sender and Target Selectors
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sender Profile", fontSize = 11.sp, color = ElegantOnSurface, fontFamily = FontFamily.Monospace)
                        ProfileDropdown(selected = senderProfile, options = profiles, onSelect = { viewModel.updateSenderProfile(it) })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Target Profile", fontSize = 11.sp, color = ElegantOnSurface, fontFamily = FontFamily.Monospace)
                        ProfileDropdown(selected = targetProfile, options = profiles, onSelect = { viewModel.updateTargetProfile(it) })
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Entity and Attribute Inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customEntity,
                        onValueChange = { viewModel.updateCustomEntity(it) },
                        label = { Text("Entity") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                            focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                            focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_entity"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customAttribute,
                        onValueChange = { viewModel.updateCustomAttribute(it) },
                        label = { Text("Attribute") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                            focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                            focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_attribute"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Context
                OutlinedTextField(
                    value = customContext,
                    onValueChange = { viewModel.updateCustomContext(it) },
                    label = { Text("Context") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                        focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                        focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_context"),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Value
                OutlinedTextField(
                    value = customValue,
                    onValueChange = { viewModel.updateCustomValue(it) },
                    label = { Text("Value") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                        focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                        focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_value"),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customLatentVariables,
                        onValueChange = { viewModel.updateCustomLatentVariables(it) },
                        label = { Text("Latent Vars") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                            focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                            focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_latent"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                    OutlinedTextField(
                        value = customActionRequired,
                        onValueChange = { viewModel.updateCustomActionRequired(it) },
                        label = { Text("Action Required") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                            focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                            focusedLabelColor = ElegantPrimary, unfocusedLabelColor = ElegantOnSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_action"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (apiLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElegantPrimary)
                    }
                } else {
                    Button(
                        onClick = {
                            onDismissFocus()
                            viewModel.fireManualHandoff()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantPrimary,
                            contentColor = ElegantOnPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("fire_custom_handoff_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = ElegantOnPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EMIT INTEGRATED HANDOFF", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.take(15) + "...",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ElegantOnBackground, unfocusedTextColor = ElegantOnBackground,
                focusedBorderColor = ElegantPrimary, unfocusedBorderColor = ElegantBorder,
                focusedContainerColor = ElegantBackground, unfocusedContainerColor = ElegantBackground
            ),
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(ElegantSurface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White, fontSize = 12.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    modifier = Modifier.background(CyberSurface)
                )
            }
        }
    }
}

@Composable
fun PayloadJSONViewer(jsonStr: String) {
    var collapsed by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
        border = BorderStroke(1.dp, ElegantPrimary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { collapsed = !collapsed },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElegantPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "E-EACV JSON",
                            color = ElegantPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "LAST GENERATED PAYLOAD PROTOCOL",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElegantOnBackground,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Icon(
                    imageVector = if (collapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = ElegantOnSurface
                )
            }

            AnimatedVisibility(visible = !collapsed) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElegantBackground, RoundedCornerShape(16.dp))
                            .border(BorderStroke(1.dp, ElegantBorder), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = jsonStr,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = SignalGreen,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalConsoleLogBox(logs: List<String>) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
        border = BorderStroke(1.dp, ElegantBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(8.dp)
                            .background(SignalGreen, CircleShape)
                    )
                    Text(
                        text = "LIVE ATMOSPHERIC CONSOLE OUTPUT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElegantOnBackground,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ElegantOnSurface
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .background(ElegantBackground, RoundedCornerShape(16.dp))
                            .border(BorderStroke(1.dp, ElegantBorder), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            logs.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 1.5.dp)
                                ) {
                                    val isAlert = log.contains("Reject", ignoreCase = true) || log.contains("Error", ignoreCase = true)
                                    val isSuccess = log.contains("Success", ignoreCase = true) || log.contains("Approved", ignoreCase = true)
                                    val clr = when {
                                        isAlert -> SignalRed
                                        isSuccess -> SignalGreen
                                        log.startsWith("[Saga") -> ElegantPrimary
                                        else -> ElegantOnSurface
                                    }
                                    Text(
                                        text = log,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = clr,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRecordRow(
    record: HandoffRecord,
    viewModel: VorexViewModel,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) ElegantPrimary else ElegantBorder
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("record_card_${record.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = record.senderProfile.substringAfter("Profile ").substringBefore(":"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantPrimary,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .background(ElegantPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "➔",
                            fontSize = 11.sp,
                            color = ElegantOnSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = record.targetProfile.substringAfter("Profile ").substringBefore(":"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantOnSecondaryContainer,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .background(ElegantSecondaryContainer, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Entity: ${record.entity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElegantOnBackground
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val clearanceColor = when (record.clearanceStatus) {
                        "Approved" -> SignalGreen
                        "Rejected" -> SignalRed
                        else -> ElegantOnSurface
                    }
                    Text(
                        text = record.clearanceStatus,
                        color = clearanceColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .border(BorderStroke(1.dp, clearanceColor), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("delete_record_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Delete",
                            tint = ElegantOnSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Short Description of context
            Text(
                text = "Context: ${record.context}",
                fontSize = 11.sp,
                color = ElegantOnSurface,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = ElegantBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("E-EACV Attribute Parameters:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimary)
                    Text("Attribute: ${record.attribute}", fontSize = 11.sp, color = ElegantOnBackground, fontFamily = FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Payload Value (Input):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElegantBackground, RoundedCornerShape(16.dp))
                            .border(BorderStroke(1.dp, ElegantBorder), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = record.payloadValue,
                            fontSize = 11.sp,
                            color = ElegantOnBackground,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Latent Variables", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElegantOnSurface)
                            Text(record.latentVariables, fontSize = 11.sp, color = ElegantOnBackground, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Action Required", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElegantOnSurface)
                            Text(record.actionRequired, fontSize = 11.sp, color = ElegantOnBackground)
                        }
                    }

                    if (!record.aiReasoning.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("📡 Target Agent AI Output & Security Report:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElegantBackground, RoundedCornerShape(16.dp))
                                .border(BorderStroke(1.dp, ElegantPrimary.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = record.aiReasoning,
                                fontSize = 11.sp,
                                color = ElegantOnBackground,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TIMESTAMP: ${viewModel.formatDateTime(record.timestamp)}",
                        fontSize = 9.sp,
                        color = ElegantOnSurface,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
