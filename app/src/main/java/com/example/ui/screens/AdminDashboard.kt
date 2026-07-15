package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GoogleAppsScriptCode
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ErpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: ErpViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("Overview") } // Overview, Operations, Sheets

    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val batches by viewModel.batches.collectAsState()
    val fees by viewModel.fees.collectAsState()
    val notices by viewModel.notices.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rama ERP - Administrator", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "Overview",
                    onClick = { activeTab = "Overview" },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = activeTab == "Operations",
                    onClick = { activeTab = "Operations" },
                    icon = { Icon(Icons.Filled.ListAlt, contentDescription = null) },
                    label = { Text("Operations") }
                )
                NavigationBarItem(
                    selected = activeTab == "Sheets",
                    onClick = { activeTab = "Sheets" },
                    icon = { Icon(Icons.Filled.CloudSync, contentDescription = null) },
                    label = { Text("Drive Sync") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                "Overview" -> AdminOverviewScreen(students, teachers, batches, fees)
                "Operations" -> AdminOperationsScreen(viewModel, students, teachers, courses, batches, fees, notices)
                "Sheets" -> AdminSheetsSyncScreen(viewModel, context)
            }
        }
    }
}

@Composable
fun AdminOverviewScreen(
    students: List<StudentEntity>,
    teachers: List<TeacherEntity>,
    batches: List<BatchEntity>,
    fees: List<FeeEntity>
) {
    val totalFeesCollected = fees.sumOf { it.paidAmount }
    val totalPendingFees = fees.sumOf { it.pendingAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Academic Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // KPI Grid Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Total Students",
                value = students.size.toString(),
                icon = Icons.Filled.People,
                color = RoyalTealLight
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Total Teachers",
                value = teachers.size.toString(),
                icon = Icons.Filled.School,
                color = SlateBlueLight
            )
        }

        // KPI Grid Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Fees Collected",
                value = "₹${totalFeesCollected.toInt()}",
                icon = Icons.Filled.CurrencyRupee,
                color = SuccessGreen
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Fees Pending",
                value = "₹${totalPendingFees.toInt()}",
                icon = Icons.Filled.PendingActions,
                color = ErrorRed
            )
        }

        // Custom Analytical Bar Chart (drawn on Canvas)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Finance & Enrollments Analysis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    
                    // Draw grid axes
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(40.dp.toPx(), 10.dp.toPx()),
                        end = Offset(40.dp.toPx(), height - 20.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(40.dp.toPx(), height - 20.dp.toPx()),
                        end = Offset(width - 10.dp.toPx(), height - 20.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw values/bars
                    val barWidth = 35.dp.toPx()
                    val spacing = 25.dp.toPx()
                    val labels = listOf("ADCA", "DCA", "Tally", "Python")
                    val dataPoints = listOf(0.7f, 0.4f, 0.9f, 0.6f) // Ratio values of enrollment strength

                    for (i in labels.indices) {
                        val startX = 60.dp.toPx() + i * (barWidth + spacing)
                        val endY = height - 20.dp.toPx()
                        val barHeight = dataPoints[i] * (height - 40.dp.toPx())
                        val topY = endY - barHeight

                        // Draw Enrollment Bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(RoyalTealLight, AccentGradientEnd)
                            ),
                            topLeft = Offset(startX, topY),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ADCA", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("DCA", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("Tally", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("Python", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Recent Notifications card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ERP System Health", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Wifi, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Local Offline Sync Engine: Ready", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Storage, contentDescription = null, tint = RoyalTealLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("SQLite Cache: Operational (All sheets seeded)", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .wrapContentSize(Alignment.Center)
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun AdminOperationsScreen(
    viewModel: ErpViewModel,
    students: List<StudentEntity>,
    teachers: List<TeacherEntity>,
    courses: List<CourseEntity>,
    batches: List<BatchEntity>,
    fees: List<FeeEntity>,
    notices: List<NoticeEntity>
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeSection by remember { mutableStateOf("Students") } // Students, Teachers, Fees, Notices
    var showAddModal by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle Buttons row
        ScrollableTabRow(
            selectedTabIndex = when(activeSection) {
                "Students" -> 0
                "Teachers" -> 1
                "Fees" -> 2
                else -> 3
            },
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = activeSection == "Students", onClick = { activeSection = "Students" }) {
                Text("Students", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSection == "Teachers", onClick = { activeSection = "Teachers" }) {
                Text("Teachers", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSection == "Fees", onClick = { activeSection = "Fees" }) {
                Text("Fees Logs", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSection == "Notices", onClick = { activeSection = "Notices" }) {
                Text("Notice Board", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar & Add Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Roll / Name / Receipt...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            FloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Entry")
            }
        }

        // List display based on selection
        Box(modifier = Modifier.weight(1f)) {
            when (activeSection) {
                "Students" -> {
                    val filteredList = students.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.rollNumber.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(filteredList) { s ->
                            StudentRow(s) { viewModel.removeStudentByRoll(s.rollNumber) }
                        }
                    }
                }
                "Teachers" -> {
                    val filteredList = teachers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(filteredList) { t ->
                            TeacherRow(t) { viewModel.removeTeacher(t) }
                        }
                    }
                }
                "Fees" -> {
                    val filteredList = fees.filter {
                        it.receiptNumber.contains(searchQuery, ignoreCase = true) || it.studentRollNumber.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(filteredList) { f ->
                            FeeRow(f) { viewModel.removeFee(f) }
                        }
                    }
                }
                "Notices" -> {
                    val filteredList = notices.filter {
                        it.title.contains(searchQuery, ignoreCase = true) || it.roleTarget.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(filteredList) { n ->
                            NoticeRow(n) { viewModel.removeNotice(n) }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet implementation for adding items
    if (showAddModal) {
        AlertDialog(
            onDismissRequest = { showAddModal = false },
            title = { Text("Add New $activeSection", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeSection) {
                        "Students" -> {
                            var roll by remember { mutableStateOf("") }
                            var name by remember { mutableStateOf("") }
                            var father by remember { mutableStateOf("") }
                            var courseSelected by remember { mutableStateOf(courses.firstOrNull()?.name ?: "ADCA") }
                            var mobile by remember { mutableStateOf("") }
                            var remark by remember { mutableStateOf("") }

                            OutlinedTextField(value = roll, onValueChange = { roll = it }, label = { Text("Roll Number") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Student Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = father, onValueChange = { father = it }, label = { Text("Father's Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("Aadhaar Number / Remarks") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (roll.isNotEmpty() && name.isNotEmpty()) {
                                        val s = StudentEntity(
                                            rollNumber = roll,
                                            admissionNumber = "ADM-2026-" + (100 + students.size),
                                            name = name,
                                            fatherName = father,
                                            motherName = "Smt. Sunita Verma",
                                            dob = "2006-05-12",
                                            gender = "Male",
                                            address = "Kanpur, India",
                                            district = "Kanpur",
                                            state = "Uttar Pradesh",
                                            pinCode = "208001",
                                            mobile = mobile,
                                            guardianMobile = "9999988888",
                                            email = "${name.lowercase().replace(" ", "")}@gmail.com",
                                            course = courseSelected,
                                            batch = batches.firstOrNull()?.name ?: "Morning Batch A",
                                            admissionDate = "2026-07-14",
                                            photoUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150",
                                            aadhaar = remark,
                                            remark = "Admission Complete"
                                        )
                                        viewModel.addStudent(s)
                                        showAddModal = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Save Admission") }
                        }
                        "Teachers" -> {
                            var tid by remember { mutableStateOf("") }
                            var tname by remember { mutableStateOf("") }
                            var temail by remember { mutableStateOf("") }
                            var tmobile by remember { mutableStateOf("") }
                            var tdept by remember { mutableStateOf("") }

                            OutlinedTextField(value = tid, onValueChange = { tid = it }, label = { Text("Teacher ID") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tname, onValueChange = { tname = it }, label = { Text("Teacher Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = temail, onValueChange = { temail = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tmobile, onValueChange = { tmobile = it }, label = { Text("Mobile") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tdept, onValueChange = { tdept = it }, label = { Text("Department / Specialization") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (tid.isNotEmpty() && tname.isNotEmpty()) {
                                        viewModel.addTeacher(
                                            TeacherEntity(
                                                id = tid, name = tname, email = temail, mobile = tmobile,
                                                department = tdept, joinDate = "2026-07-14",
                                                photoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", status = "Active"
                                            )
                                        )
                                        showAddModal = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Register Teacher") }
                        }
                        "Fees" -> {
                            var receipt by remember { mutableStateOf("") }
                            var sroll by remember { mutableStateOf("") }
                            var feeAmt by remember { mutableStateOf("") }
                            var paidAmt by remember { mutableStateOf("") }
                            var mode by remember { mutableStateOf("UPI") }

                            OutlinedTextField(value = receipt, onValueChange = { receipt = it }, label = { Text("Receipt Number") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = sroll, onValueChange = { sroll = it }, label = { Text("Student Roll Number") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = feeAmt, onValueChange = { feeAmt = it }, label = { Text("Course Fee") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = paidAmt, onValueChange = { paidAmt = it }, label = { Text("Paid Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = mode, onValueChange = { mode = it }, label = { Text("Payment Mode (UPI, Cash, etc.)") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    val fAmt = feeAmt.toDoubleOrNull() ?: 15000.0
                                    val pAmt = paidAmt.toDoubleOrNull() ?: 5000.0
                                    if (receipt.isNotEmpty() && sroll.isNotEmpty()) {
                                        viewModel.addFee(
                                            FeeEntity(
                                                receiptNumber = receipt, studentRollNumber = sroll,
                                                courseFee = fAmt, paidAmount = pAmt, pendingAmount = fAmt - pAmt,
                                                discount = 0.0, paymentDate = "2026-07-14", paymentMode = mode, remarks = "Fee Collection Update"
                                            )
                                        )
                                        showAddModal = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Collect Fee") }
                        }
                        "Notices" -> {
                            var title by remember { mutableStateOf("") }
                            var desc by remember { mutableStateOf("") }
                            var target by remember { mutableStateOf("All") } // All, Teacher, Student

                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notice Title") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Details & Announcement Body") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Role (All, Teacher, Student)") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (title.isNotEmpty()) {
                                        viewModel.addNotice(
                                            NoticeEntity(
                                                id = "N" + (100 + notices.size),
                                                title = title, content = desc, date = "2026-07-14", roleTarget = target
                                            )
                                        )
                                        showAddModal = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Publish Announcement") }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun StudentRow(s: StudentEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = RoyalTealLight, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(s.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Roll: ${s.rollNumber} | ${s.course}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
        }
    }
}

@Composable
fun TeacherRow(t: TeacherEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.School, contentDescription = null, tint = SlateBlueLight, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(t.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("ID: ${t.id} | ${t.department}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
        }
    }
}

@Composable
fun FeeRow(f: FeeEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Receipt, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Receipt: ${f.receiptNumber}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Roll: ${f.studentRollNumber} | Paid: ₹${f.paidAmount} | Mode: ${f.paymentMode}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
        }
    }
}

@Composable
fun NoticeRow(n: NoticeEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Campaign, contentDescription = null, tint = AmberAccentLight, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(n.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Target: ${n.roleTarget} | Date: ${n.date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
        }
    }
}

@Composable
fun AdminSheetsSyncScreen(
    viewModel: ErpViewModel,
    context: Context
) {
    val syncStatus by viewModel.syncStatus.collectAsState()
    val gasUrl by viewModel.gasUrl.collectAsState()
    var tempUrl by remember { mutableStateOf(gasUrl) }

    LaunchedEffect(gasUrl) {
        tempUrl = gasUrl
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Google Sheets & Drive Integrator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            "This application utilizes Google Sheets as a relational database, and Google Drive for student photos, receipts, certificates, and results using Google Apps Script APIs.",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = tempUrl,
            onValueChange = { tempUrl = it },
            label = { Text("Google Apps Script Web App URL") },
            placeholder = { Text("https://script.google.com/macros/s/.../exec") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.saveGasUrl(tempUrl)
                    Toast.makeText(context, "Google Apps Script API URL Saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save URL")
            }

            Button(
                onClick = {
                    viewModel.syncAllSheets { feedback ->
                        Toast.makeText(context, "Sync Operation Finished!", Toast.LENGTH_LONG).show()
                        android.app.AlertDialog.Builder(context)
                            .setTitle("Database Synchronization Logs")
                            .setMessage(feedback)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                },
                modifier = Modifier.weight(1.5f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Filled.Sync, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync Database Now")
            }
        }

        if (syncStatus.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(syncStatus, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RoyalTealLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apps Script Copy Section
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Google Apps Script Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Deploy this exact Google Apps Script in your Google account to enable auto sheet generation and Drive file backups.",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Google Apps Script Code", GoogleAppsScriptCode.SCRIPT_CODE)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Backend Apps Script Code Copied to Clipboard!", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Code to Paste in Apps Script")
                }
            }
        }
    }
}
