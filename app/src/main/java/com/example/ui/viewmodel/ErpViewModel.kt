package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ErpViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database.appDao())

    // --- State Observables ---
    val students: StateFlow<List<StudentEntity>> = repository.allStudents.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val teachers: StateFlow<List<TeacherEntity>> = repository.allTeachers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val fees: StateFlow<List<FeeEntity>> = repository.allFees.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val courses: StateFlow<List<CourseEntity>> = repository.allCourses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val batches: StateFlow<List<BatchEntity>> = repository.allBatches.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val homework: StateFlow<List<HomeworkEntity>> = repository.allHomework.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val studyMaterials: StateFlow<List<StudyMaterialEntity>> = repository.allStudyMaterials.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val results: StateFlow<List<ResultEntity>> = repository.allResults.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val notices: StateFlow<List<NoticeEntity>> = repository.allNotices.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Auth States ---
    private val _loggedInRole = MutableStateFlow("") // "Admin", "Teacher", "Student", ""
    val loggedInRole: StateFlow<String> = _loggedInRole.asStateFlow()

    private val _loggedInUserId = MutableStateFlow("") // Roll Number, Teacher ID, "admin"
    val loggedInUserId: StateFlow<String> = _loggedInUserId.asStateFlow()

    private val _currentUserStudent = MutableStateFlow<StudentEntity?>(null)
    val currentUserStudent: StateFlow<StudentEntity?> = _currentUserStudent.asStateFlow()

    private val _currentUserTeacher = MutableStateFlow<TeacherEntity?>(null)
    val currentUserTeacher: StateFlow<TeacherEntity?> = _currentUserTeacher.asStateFlow()

    private val _gasUrl = MutableStateFlow("")
    val gasUrl: StateFlow<String> = _gasUrl.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    private val _syncStatus = MutableStateFlow("")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    // --- AI Interaction State ---
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _gasUrl.value = repository.getSetting("gas_url")
            val autoLogin = repository.getSetting("remember_login") == "true"
            if (autoLogin) {
                val savedRole = repository.getSetting("logged_in_role")
                val savedId = repository.getSetting("logged_in_user_id")
                if (savedRole.isNotEmpty() && savedId.isNotEmpty()) {
                    performSessionLogin(savedRole, savedId)
                }
            }
        }
    }

    // --- Authentication Actions ---
    fun login(emailOrMobile: String, password: String, role: String, rememberMe: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            // Simple validation: check if emailOrMobile matches any local record
            // Admin default check: admin / admin
            if (role == "Admin") {
                if ((emailOrMobile.lowercase() == "admin@rama.in" || emailOrMobile == "9999999999") && password == "admin123") {
                    _loggedInRole.value = "Admin"
                    _loggedInUserId.value = "admin"
                    if (rememberMe) {
                        repository.saveSetting("remember_login", "true")
                        repository.saveSetting("logged_in_role", "Admin")
                        repository.saveSetting("logged_in_user_id", "admin")
                    }
                    _isLoggingIn.value = false
                    onSuccess()
                    return@launch
                } else {
                    _isLoggingIn.value = false
                    onError("Invalid Admin credentials. Try admin@rama.in / admin123")
                    return@launch
                }
            }

            if (role == "Teacher") {
                val match = teachers.value.firstOrNull {
                    (it.email.equals(emailOrMobile, ignoreCase = true) || it.mobile == emailOrMobile)
                }
                if (match != null && password == "teacher123") {
                    _loggedInRole.value = "Teacher"
                    _loggedInUserId.value = match.id
                    _currentUserTeacher.value = match
                    if (rememberMe) {
                        repository.saveSetting("remember_login", "true")
                        repository.saveSetting("logged_in_role", "Teacher")
                        repository.saveSetting("logged_in_user_id", match.id)
                    }
                    _isLoggingIn.value = false
                    onSuccess()
                } else {
                    _isLoggingIn.value = false
                    onError("Invalid Teacher credentials. Try rajesh@rama.edu.in / teacher123")
                }
                return@launch
            }

            if (role == "Student") {
                val match = students.value.firstOrNull {
                    (it.email.equals(emailOrMobile, ignoreCase = true) || it.mobile == emailOrMobile)
                }
                if (match != null && password == "student123") {
                    _loggedInRole.value = "Student"
                    _loggedInUserId.value = match.rollNumber
                    _currentUserStudent.value = match
                    if (rememberMe) {
                        repository.saveSetting("remember_login", "true")
                        repository.saveSetting("logged_in_role", "Student")
                        repository.saveSetting("logged_in_user_id", match.rollNumber)
                    }
                    _isLoggingIn.value = false
                    onSuccess()
                } else {
                    _isLoggingIn.value = false
                    onError("Invalid Student credentials. Try aman.verma@gmail.com / student123")
                }
                return@launch
            }

            _isLoggingIn.value = false
            onError("User or role mismatch.")
        }
    }

    private suspend fun performSessionLogin(role: String, userId: String) {
        _loggedInRole.value = role
        _loggedInUserId.value = userId
        if (role == "Teacher") {
            _currentUserTeacher.value = repository.getTeacherById(userId)
        } else if (role == "Student") {
            _currentUserStudent.value = repository.getStudentByRoll(userId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loggedInRole.value = ""
            _loggedInUserId.value = ""
            _currentUserStudent.value = null
            _currentUserTeacher.value = null
            repository.saveSetting("remember_login", "false")
            repository.saveSetting("logged_in_role", "")
            repository.saveSetting("logged_in_user_id", "")
        }
    }

    // --- Admin Operations ---
    fun addStudent(student: StudentEntity) = viewModelScope.launch { repository.insertStudent(student) }
    fun removeStudent(student: StudentEntity) = viewModelScope.launch { repository.deleteStudent(student) }
    fun removeStudentByRoll(roll: String) = viewModelScope.launch { repository.deleteStudentByRoll(roll) }

    fun addTeacher(teacher: TeacherEntity) = viewModelScope.launch { repository.insertTeacher(teacher) }
    fun removeTeacher(teacher: TeacherEntity) = viewModelScope.launch { repository.deleteTeacher(teacher) }

    fun addCourse(course: CourseEntity) = viewModelScope.launch { repository.insertCourse(course) }
    fun removeCourse(course: CourseEntity) = viewModelScope.launch { repository.deleteCourse(course) }

    fun addBatch(batch: BatchEntity) = viewModelScope.launch { repository.insertBatch(batch) }
    fun removeBatch(batch: BatchEntity) = viewModelScope.launch { repository.deleteBatch(batch) }

    fun addFee(fee: FeeEntity) = viewModelScope.launch { repository.insertFee(fee) }
    fun removeFee(fee: FeeEntity) = viewModelScope.launch { repository.deleteFee(fee) }

    fun addNotice(notice: NoticeEntity) = viewModelScope.launch { repository.insertNotice(notice) }
    fun removeNotice(notice: NoticeEntity) = viewModelScope.launch { repository.deleteNotice(notice) }

    // --- Teacher Operations ---
    fun saveAttendance(date: String, course: String, batch: String, list: List<AttendanceEntity>) {
        viewModelScope.launch {
            repository.clearAttendanceForBatch(date, course, batch)
            repository.insertAttendance(list)
        }
    }

    fun addHomework(hw: HomeworkEntity) = viewModelScope.launch { repository.insertHomework(hw) }
    fun removeHomework(hw: HomeworkEntity) = viewModelScope.launch { repository.deleteHomework(hw) }

    fun addStudyMaterial(material: StudyMaterialEntity) = viewModelScope.launch { repository.insertStudyMaterial(material) }
    fun removeStudyMaterial(material: StudyMaterialEntity) = viewModelScope.launch { repository.deleteStudyMaterial(material) }

    fun addResult(result: ResultEntity) = viewModelScope.launch { repository.insertResult(result) }
    fun removeResult(result: ResultEntity) = viewModelScope.launch { repository.deleteResult(result) }

    // --- Settings / GAS Operations ---
    fun saveGasUrl(url: String) {
        viewModelScope.launch {
            _gasUrl.value = url
            repository.saveSetting("gas_url", url)
        }
    }

    fun syncAllSheets(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _syncStatus.value = "Synchronizing all ERP tables with Google Sheets..."
            val result = repository.syncAllTablesToSheets()
            _syncStatus.value = ""
            onComplete(result)
        }
    }

    // --- AI Operations ---
    fun askGemini(prompt: String, systemInstruction: String? = null) {
        viewModelScope.launch {
            _aiLoading.value = true
            _aiResponse.value = ""
            val response = GeminiClient.generateContent(prompt, systemInstruction)
            _aiResponse.value = response
            _aiLoading.value = false
        }
    }

    fun solveDoubt(doubt: String, course: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val response = GeminiClient.solveDoubt(doubt, course)
            _aiResponse.value = response
            _aiLoading.value = false
        }
    }

    fun generateHomework(topic: String, course: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val response = GeminiClient.generateHomework(topic, course)
            _aiResponse.value = response
            _aiLoading.value = false
        }
    }

    fun generateQuestionPaper(subject: String, course: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val response = GeminiClient.generateQuestionPaper(subject, course)
            _aiResponse.value = response
            _aiLoading.value = false
        }
    }

    fun analyzeStudentPerformance(rollNumber: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val studentFees = fees.value.filter { it.studentRollNumber == rollNumber }
            val studentResults = results.value.filter { it.studentRollNumber == rollNumber }
            val studentAtt = attendance.value.filter { it.studentRollNumber == rollNumber }
            
            val totalAtt = studentAtt.size
            val presentAtt = studentAtt.count { it.status == "Present" }
            val attRate = if (totalAtt > 0) (presentAtt.toDouble() / totalAtt * 100.0) else 85.0 // default reasonable mock if no history

            val reportData = JSONObject().apply {
                put("feesPaid", studentFees.sumOf { it.paidAmount })
                put("feesPending", studentFees.sumOf { it.pendingAmount })
                val exams = JSONArray()
                studentResults.forEach { r ->
                    exams.put(JSONObject().apply {
                        put("exam", r.examName)
                        put("percentage", r.totalPercentage)
                        put("grade", r.grade)
                    })
                }
                put("exams", exams)
            }

            val response = GeminiClient.analyzePerformance(reportData.toString(), attRate)
            _aiResponse.value = response
            _aiLoading.value = false
        }
    }
}
