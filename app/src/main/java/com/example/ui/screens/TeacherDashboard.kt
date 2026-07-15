package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ErpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(
    viewModel: ErpViewModel,
    onLogout: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Attendance") } // Attendance, Academics, Grades, Chat

    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val batches by viewModel.batches.collectAsState()
    val homework by viewModel.homework.collectAsState()
    val studyMaterials by viewModel.studyMaterials.collectAsState()
    val currentUserTeacher by viewModel.currentUserTeacher.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Teacher Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                        Text(currentUserTeacher?.name ?: "Prof. Rajesh Kumar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
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
                    selected = activeTab == "Attendance",
                    onClick = { activeTab = "Attendance" },
                    icon = { Icon(Icons.Filled.HowToReg, contentDescription = null) },
                    label = { Text("Attendance") }
                )
                NavigationBarItem(
                    selected = activeTab == "Academics",
                    onClick = { activeTab = "Academics" },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                    label = { Text("Academics") }
                )
                NavigationBarItem(
                    selected = activeTab == "Grades",
                    onClick = { activeTab = "Grades" },
                    icon = { Icon(Icons.Filled.Grading, contentDescription = null) },
                    label = { Text("Grades") }
                )
                NavigationBarItem(
                    selected = activeTab == "Chat",
                    onClick = { activeTab = "Chat" },
                    icon = { Icon(Icons.Filled.Chat, contentDescription = null) },
                    label = { Text("AI Assistant") }
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
                "Attendance" -> TeacherAttendanceScreen(viewModel, students, courses, batches)
                "Academics" -> TeacherAcademicsScreen(viewModel, courses, batches, homework, studyMaterials)
                "Grades" -> TeacherGradesScreen(viewModel, students)
                "Chat" -> TeacherAiChatScreen(viewModel, courses)
            }
        }
    }
}

@Composable
fun TeacherAttendanceScreen(
    viewModel: ErpViewModel,
    students: List<StudentEntity>,
    courses: List<CourseEntity>,
    batches: List<BatchEntity>
) {
    val context = LocalContext.current
    val currentDate = "2026-07-14"
    var selectedBatch by remember { mutableStateOf(batches.firstOrNull()?.name ?: "Morning Batch A (8 AM - 10 AM)") }
    
    // Store attendance map locally: Roll Number -> "Present", "Absent", "Late"
    val attendanceStates = remember { mutableStateMapOf<String, String>() }

    // Seed/update map when students/batch changes
    LaunchedEffect(selectedBatch, students) {
        students.forEach { s ->
            if (attendanceStates[s.rollNumber] == null) {
                attendanceStates[s.rollNumber] = "Present"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Daily Class Attendance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Date: $currentDate", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Batch Selector dropdown replacement for quick action
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FilterList, contentDescription = null, tint = RoyalTealLight)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Select Target Class/Batch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(selectedBatch, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RoyalTealLight)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val batchStudents = students.filter { it.batch == selectedBatch || selectedBatch.contains(it.course.take(5)) || true }
            items(batchStudents) { student ->
                val status = attendanceStates[student.rollNumber] ?: "Present"
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(student.name, fontWeight = FontWeight.Bold)
                            Text("Roll: ${student.rollNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // M3 Segmented Buttons logic (Custom row)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val options = listOf("Present", "Absent", "Late")
                            options.forEach { option ->
                                val isSelected = status == option
                                val optBg = when {
                                    isSelected && option == "Present" -> SuccessGreen
                                    isSelected && option == "Absent" -> ErrorRed
                                    isSelected && option == "Late" -> GoldYellow
                                    else -> MaterialTheme.colorScheme.surfaceContainer
                                }
                                val optColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                                Box(
                                    modifier = Modifier
                                        .background(optBg, RoundedCornerShape(8.dp))
                                        .clickable { attendanceStates[student.rollNumber] = option }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(option, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = optColor)
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val records = attendanceStates.map { (roll, status) ->
                    AttendanceEntity(
                        id = 0,
                        date = currentDate,
                        studentRollNumber = roll,
                        status = status,
                        course = "Computer Application",
                        batch = selectedBatch,
                        markedBy = "T001"
                    )
                }
                viewModel.saveAttendance(currentDate, "Computer Application", selectedBatch, records)
                Toast.makeText(context, "Daily Attendance marked and cached offline successfully!", Toast.LENGTH_LONG).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submit Attendance Logs")
        }
    }
}

@Composable
fun TeacherAcademicsScreen(
    viewModel: ErpViewModel,
    courses: List<CourseEntity>,
    batches: List<BatchEntity>,
    homework: List<HomeworkEntity>,
    studyMaterials: List<StudyMaterialEntity>
) {
    val context = LocalContext.current
    var hwTitle by remember { mutableStateOf("") }
    var hwDesc by remember { mutableStateOf("") }
    var targetBatch by remember { mutableStateOf(batches.firstOrNull()?.name ?: "Morning Batch A (8 AM - 10 AM)") }

    var matTitle by remember { mutableStateOf("") }
    var matType by remember { mutableStateOf("PDF") } // PDF, Video, Image, Note
    var matUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Academics Hub", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // 1. Upload Homework Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Post New Homework", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTealLight)
                
                OutlinedTextField(
                    value = hwTitle,
                    onValueChange = { hwTitle = it },
                    label = { Text("Homework Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hwDesc,
                    onValueChange = { hwDesc = it },
                    label = { Text("Assignment Details / Questions") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (hwTitle.isNotEmpty()) {
                            val hw = HomeworkEntity(
                                id = "HW" + (100 + homework.size),
                                title = hwTitle,
                                description = hwDesc,
                                course = "Computer Applications",
                                batch = targetBatch,
                                dueDate = "2026-07-20",
                                fileUrl = "https://drive.google.com/drive",
                                teacherName = "Prof. Rajesh Kumar"
                            )
                            viewModel.addHomework(hw)
                            hwTitle = ""
                            hwDesc = ""
                            Toast.makeText(context, "Homework assigned and synced successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Publish Assignment")
                }
            }
        }

        // 2. Upload Study Materials Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Upload Study Material (PDF, Video, PPT)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateBlueLight)

                OutlinedTextField(
                    value = matTitle,
                    onValueChange = { matTitle = it },
                    label = { Text("Material Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = matUrl,
                    onValueChange = { matUrl = it },
                    label = { Text("Google Drive File Link / Video URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("PDF", "Video", "Note").forEach { type ->
                        val isSel = matType == type
                        FilterChip(
                            selected = isSel,
                            onClick = { matType = type },
                            label = { Text(type) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (matTitle.isNotEmpty() && matUrl.isNotEmpty()) {
                            val material = StudyMaterialEntity(
                                id = "SM" + (100 + studyMaterials.size),
                                title = matTitle,
                                type = matType,
                                url = matUrl,
                                course = "C001",
                                description = "Academic notes uploaded by tutor"
                            )
                            viewModel.addStudyMaterial(material)
                            matTitle = ""
                            matUrl = ""
                            Toast.makeText(context, "Study material added successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateBlueLight)
                ) {
                    Text("Register Resource")
                }
            }
        }
    }
}

@Composable
fun TeacherGradesScreen(
    viewModel: ErpViewModel,
    students: List<StudentEntity>
) {
    val context = LocalContext.current
    var selectedStudentRoll by remember { mutableStateOf(students.firstOrNull()?.rollNumber ?: "") }
    var examName by remember { mutableStateOf("Monthly Test - July 2026") }
    var sub1Marks by remember { mutableStateOf("") }
    var sub2Marks by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Results & Mark Entry", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Record Student Marks", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SuccessGreen)

                OutlinedTextField(
                    value = selectedStudentRoll,
                    onValueChange = { selectedStudentRoll = it },
                    label = { Text("Student Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Exam / Test Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sub1Marks,
                    onValueChange = { sub1Marks = it },
                    label = { Text("Subject 1 Marks (Out of 100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sub2Marks,
                    onValueChange = { sub2Marks = it },
                    label = { Text("Subject 2 Marks (Out of 100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val s1 = sub1Marks.toDoubleOrNull() ?: 0.0
                        val s2 = sub2Marks.toDoubleOrNull() ?: 0.0
                        val avg = (s1 + s2) / 2.0
                        val grade = if (avg >= 90) "A+" else if (avg >= 75) "A" else if (avg >= 50) "B" else "C"

                        if (selectedStudentRoll.isNotEmpty()) {
                            val r = ResultEntity(
                                id = "${selectedStudentRoll}_JulyTest",
                                studentRollNumber = selectedStudentRoll,
                                examName = examName,
                                marksJson = "{\"Lab Practice\": $s1, \"Theoretical Concepts\": $s2}",
                                totalPercentage = avg,
                                grade = grade,
                                rank = 1
                            )
                            viewModel.addResult(r)
                            sub1Marks = ""
                            sub2Marks = ""
                            Toast.makeText(context, "Student marks published offline!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Publish Marks")
                }
            }
        }
    }
}

@Composable
fun TeacherAiChatScreen(
    viewModel: ErpViewModel,
    courses: List<CourseEntity>
) {
    var prompt by remember { mutableStateOf("Create a syllabus structure for a 3-month Course in Internet & Web security.") }
    val aiResponse by viewModel.aiResponse.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AI Assistant & Generator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Generate custom homework, question papers, or syllabus layouts instantly using Gemini 3.5 Flash.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter prompt or topic details") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.generateHomework(prompt, "Computer Science") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("HW Gen", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.generateQuestionPaper(prompt, "Computer Science") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SlateBlueLight)
            ) {
                Text("Exam Gen", fontSize = 12.sp)
            }
        }

        if (aiLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (aiResponse.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Output Response:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(aiResponse, fontSize = 13.sp)
                }
            }
        }
    }
}
