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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ErpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboard(
    viewModel: ErpViewModel,
    onLogout: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Profile") } // Profile, Academics, Exam, Finance, Chat

    val currentUserStudent by viewModel.currentUserStudent.collectAsState()
    val homework by viewModel.homework.collectAsState()
    val studyMaterials by viewModel.studyMaterials.collectAsState()
    val results by viewModel.results.collectAsState()
    val fees by viewModel.fees.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Student Campus Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                        Text(currentUserStudent?.name ?: "Aman Verma", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
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
                    selected = activeTab == "Profile",
                    onClick = { activeTab = "Profile" },
                    icon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                    label = { Text("ID Card") }
                )
                NavigationBarItem(
                    selected = activeTab == "Academics",
                    onClick = { activeTab = "Academics" },
                    icon = { Icon(Icons.Filled.LibraryBooks, contentDescription = null) },
                    label = { Text("Lessons") }
                )
                NavigationBarItem(
                    selected = activeTab == "Exam",
                    onClick = { activeTab = "Exam" },
                    icon = { Icon(Icons.Filled.Quiz, contentDescription = null) },
                    label = { Text("Exam") }
                )
                NavigationBarItem(
                    selected = activeTab == "Finance",
                    onClick = { activeTab = "Finance" },
                    icon = { Icon(Icons.Filled.Payments, contentDescription = null) },
                    label = { Text("Fees") }
                )
                NavigationBarItem(
                    selected = activeTab == "Chat",
                    onClick = { activeTab = "Chat" },
                    icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
                    label = { Text("Tutor") }
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
            val student = currentUserStudent ?: StudentEntity(
                rollNumber = "R26001", admissionNumber = "ADM-2026-101", name = "Aman Verma",
                fatherName = "Shri Ramesh Verma", motherName = "Smt. Sunita Verma", dob = "2005-08-15",
                gender = "Male", address = "12, Saket Nagar", district = "Kanpur", state = "Uttar Pradesh",
                pinCode = "208001", mobile = "8888877777", guardianMobile = "9999988888", email = "aman.verma@gmail.com",
                course = "Advanced Diploma in Computer Applications", batch = "Morning Batch A (8 AM - 10 AM)",
                admissionDate = "2026-01-05", photoUrl = "", aadhaar = "1234 5678 9012", remark = ""
            )

            when (activeTab) {
                "Profile" -> StudentProfileScreen(student)
                "Academics" -> StudentAcademicsScreen(student, homework, studyMaterials)
                "Exam" -> StudentOnlineExamScreen()
                "Finance" -> StudentFinanceScreen(student, fees, results)
                "Chat" -> StudentAiTutorScreen(viewModel, student)
            }
        }
    }
}

@Composable
fun StudentProfileScreen(student: StudentEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-Fidelity Glassmorphism Student ID Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(RoyalTealLight, AccentGradientEnd)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "RAMA TECHNICAL INSTITUTE",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "STUDENT DIGITAL IDENTITY CARD",
                                color = AmberAccentDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(Icons.Filled.QrCode, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(student.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Roll Number: ${student.rollNumber}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                            Text("Course: ${student.course}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Session: 2026-27", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        Text("Authorized Signature", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Profile Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Personal & Guardian Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTealLight)
                Divider()
                ProfileField("Father's Name", student.fatherName)
                ProfileField("Mother's Name", student.motherName)
                ProfileField("Date of Birth", student.dob)
                ProfileField("Aadhaar ID", student.aadhaar)
                ProfileField("Registered Email", student.email)
                ProfileField("Mobile Number", student.mobile)
                ProfileField("Guardian Mobile", student.guardianMobile)
                ProfileField("Permanent Address", "${student.address}, ${student.district}, ${student.state}")
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StudentAcademicsScreen(
    student: StudentEntity,
    homeworkList: List<HomeworkEntity>,
    materials: List<StudyMaterialEntity>
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf("Lessons") } // Lessons, Assignments

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Your Classroom Study Hub", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Pill Navigation
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Lessons", "Assignments").forEach { tab ->
                val isSel = activeSubTab == tab
                val bg = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
                val tc = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = tab }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(tab, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = tc)
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (activeSubTab == "Lessons") {
                // Study Materials List
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(materials) { mat ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (mat.type == "Video") Icons.Filled.PlayCircle else Icons.Filled.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (mat.type == "Video") ErrorRed else RoyalTealLight,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mat.title, fontWeight = FontWeight.Bold)
                                    Text("${mat.type} Resource | Course: ADCA", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = { Toast.makeText(context, "Opening resource: ${mat.title}", Toast.LENGTH_SHORT).show() },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Open")
                                }
                            }
                        }
                    }
                }
            } else {
                // Homework Assignments
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(homeworkList) { hw ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(hw.title, fontWeight = FontWeight.Bold)
                                Text(hw.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Divider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Due Date: ${hw.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberAccentLight)
                                    Button(
                                        onClick = { Toast.makeText(context, "Submitting assignment file to teacher...", Toast.LENGTH_LONG).show() },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                    ) {
                                        Text("Submit Solution")
                                    }
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
fun StudentOnlineExamScreen() {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var isExamFinished by remember { mutableStateOf(false) }

    val questions = listOf(
        QuizQuestion("Which key combination is used to COPY selected text in Windows?", listOf("Ctrl + C", "Ctrl + V", "Ctrl + X", "Ctrl + Z"), 0),
        QuizQuestion("What is the primary extension used for Microsoft Word documents?", listOf(".txt", ".docx", ".xlsx", ".pdf"), 1),
        QuizQuestion("In accounting and Tally, what is the default keyboard shortcut for payment voucher?", listOf("F4", "F5", "F6", "F7"), 1),
        QuizQuestion("Which programming language is commonly used for AI and Django web development?", listOf("Java", "C++", "Python", "Javascript"), 2),
        QuizQuestion("What does CPU stand for?", listOf("Central Processing Unit", "Central Program Utility", "Computer Parts Union", "Core Process Unit"), 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Online Term Examination", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        if (!isExamFinished) {
            val q = questions[currentQuestionIndex]
            Text("Question ${currentQuestionIndex + 1} of ${questions.size}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = RoyalTealLight)
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(q.text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    q.options.forEachIndexed { idx, option ->
                        val isSelected = selectedAnswer == idx
                        val btnBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        val btnColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(btnBg, RoundedCornerShape(8.dp))
                                .clickable { selectedAnswer = idx }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(option, fontWeight = FontWeight.SemiBold, color = btnColor)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (selectedAnswer == -1) {
                        Toast.makeText(context, "Please select an answer first.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedAnswer == q.correctIndex) {
                        score++
                    }
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        selectedAnswer = -1
                    } else {
                        isExamFinished = true
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (currentQuestionIndex == questions.size - 1) "Finish Exam" else "Next Question")
            }
        } else {
            // Exam Finished Score layout
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(72.dp))
                    Text("Exam Completed!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Result Analysis", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Text("Total Score: $score / ${questions.size}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    
                    val percentage = (score.toDouble() / questions.size) * 100
                    Text("Percentage: ${percentage.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    
                    val grade = if (percentage >= 80) "Grade A+" else if (percentage >= 60) "Grade A" else "Grade B"
                    Text(grade, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RoyalTealLight)

                    Button(
                        onClick = {
                            currentQuestionIndex = 0
                            selectedAnswer = -1
                            score = 0
                            isExamFinished = false
                        }
                    ) {
                        Text("Retake Test")
                    }
                }
            }
        }
    }
}

data class QuizQuestion(val text: String, val options: List<String>, val correctIndex: Int)

@Composable
fun StudentFinanceScreen(
    student: StudentEntity,
    feesList: List<FeeEntity>,
    resultsList: List<ResultEntity>
) {
    val context = LocalContext.current
    val studentFees = feesList.filter { it.studentRollNumber == student.rollNumber }
    val studentResult = resultsList.firstOrNull { it.studentRollNumber == student.rollNumber }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Academics Report & Fees", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Fees summary card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Fee Structure Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalTealLight)
                Divider()
                
                val totalFee = studentFees.sumOf { it.courseFee }
                val paidFee = studentFees.sumOf { it.paidAmount }
                val pendingFee = studentFees.sumOf { it.pendingAmount }

                ProfileField("Total Course Fee", "₹$totalFee")
                ProfileField("Total Amount Paid", "₹$paidFee")
                ProfileField("Pending Balance", "₹$pendingFee")

                if (pendingFee > 0) {
                    Box(
                        modifier = Modifier
                            .background(ErrorRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text("Pending fee alert. Please clear your dues at the administrative cabin.", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = { Toast.makeText(context, "Downloading verified digital fee receipt PDF...", Toast.LENGTH_LONG).show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Fee Receipt")
                }
            }
        }

        // Mid Term Exam Report Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Term Marksheet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateBlueLight)
                Divider()

                if (studentResult != null) {
                    ProfileField("Exam Name", studentResult.examName)
                    ProfileField("Grade Scored", studentResult.grade)
                    ProfileField("Percentage", "${studentResult.totalPercentage}%")
                    ProfileField("Batch Rank", "Rank ${studentResult.rank}")

                    Button(
                        onClick = { Toast.makeText(context, "Certificate of Completion downloaded successfully!", Toast.LENGTH_LONG).show() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Certificate")
                    }
                } else {
                    Text("No exam scores found yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun StudentAiTutorScreen(
    viewModel: ErpViewModel,
    student: StudentEntity
) {
    var chatQuery by remember { mutableStateOf("Explain how a double-entry ledger works in Tally accounting.") }
    val aiResponse by viewModel.aiResponse.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AI Campus Tutor (Gemini)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Need assistance with coding, accounting, or design homework? Ask Rama AI Tutor anytime.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        OutlinedTextField(
            value = chatQuery,
            onValueChange = { chatQuery = it },
            label = { Text("Ask your doubt") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.solveDoubt(chatQuery, student.course) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask Tutor")
            }

            Button(
                onClick = { viewModel.analyzeStudentPerformance(student.rollNumber) },
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SlateBlueLight)
            ) {
                Icon(Icons.Filled.Analytics, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Performance Review")
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
                    Text("AI Tutor Advice:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(aiResponse, fontSize = 13.sp)
                }
            }
        }
    }
}
