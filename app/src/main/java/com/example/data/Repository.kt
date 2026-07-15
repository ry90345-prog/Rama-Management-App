package com.example.data

import com.example.api.GasClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class Repository(private val appDao: AppDao) {

    // --- Students ---
    val allStudents: Flow<List<StudentEntity>> = appDao.getAllStudents()
    suspend fun getStudentByRoll(roll: String) = appDao.getStudentByRoll(roll)
    suspend fun insertStudent(student: StudentEntity) = appDao.insertStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = appDao.deleteStudent(student)
    suspend fun deleteStudentByRoll(roll: String) = appDao.deleteStudentByRoll(roll)

    // --- Teachers ---
    val allTeachers: Flow<List<TeacherEntity>> = appDao.getAllTeachers()
    suspend fun getTeacherById(id: String) = appDao.getTeacherById(id)
    suspend fun insertTeacher(teacher: TeacherEntity) = appDao.insertTeacher(teacher)
    suspend fun deleteTeacher(teacher: TeacherEntity) = appDao.deleteTeacher(teacher)

    // --- Attendance ---
    val allAttendance: Flow<List<AttendanceEntity>> = appDao.getAllAttendance()
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>> = appDao.getAttendanceByDate(date)
    suspend fun insertAttendance(attendance: List<AttendanceEntity>) = appDao.insertAttendance(attendance)
    suspend fun clearAttendanceForBatch(date: String, course: String, batch: String) = 
        appDao.clearAttendanceForBatch(date, course, batch)

    // --- Fees ---
    val allFees: Flow<List<FeeEntity>> = appDao.getAllFees()
    fun getFeesForStudent(roll: String): Flow<List<FeeEntity>> = appDao.getFeesForStudent(roll)
    suspend fun insertFee(fee: FeeEntity) = appDao.insertFee(fee)
    suspend fun deleteFee(fee: FeeEntity) = appDao.deleteFee(fee)

    // --- Courses ---
    val allCourses: Flow<List<CourseEntity>> = appDao.getAllCourses()
    suspend fun insertCourse(course: CourseEntity) = appDao.insertCourse(course)
    suspend fun deleteCourse(course: CourseEntity) = appDao.deleteCourse(course)

    // --- Batches ---
    val allBatches: Flow<List<BatchEntity>> = appDao.getAllBatches()
    suspend fun insertBatch(batch: BatchEntity) = appDao.insertBatch(batch)
    suspend fun deleteBatch(batch: BatchEntity) = appDao.deleteBatch(batch)

    // --- Homework ---
    val allHomework: Flow<List<HomeworkEntity>> = appDao.getAllHomework()
    suspend fun insertHomework(homework: HomeworkEntity) = appDao.insertHomework(homework)
    suspend fun deleteHomework(homework: HomeworkEntity) = appDao.deleteHomework(homework)

    // --- Study Materials ---
    val allStudyMaterials: Flow<List<StudyMaterialEntity>> = appDao.getAllStudyMaterials()
    suspend fun insertStudyMaterial(material: StudyMaterialEntity) = appDao.insertStudyMaterial(material)
    suspend fun deleteStudyMaterial(material: StudyMaterialEntity) = appDao.deleteStudyMaterial(material)

    // --- Results ---
    val allResults: Flow<List<ResultEntity>> = appDao.getAllResults()
    fun getResultsForStudent(roll: String): Flow<List<ResultEntity>> = appDao.getResultsForStudent(roll)
    suspend fun insertResult(result: ResultEntity) = appDao.insertResult(result)
    suspend fun deleteResult(result: ResultEntity) = appDao.deleteResult(result)

    // --- Notices ---
    val allNotices: Flow<List<NoticeEntity>> = appDao.getAllNotices()
    suspend fun insertNotice(notice: NoticeEntity) = appDao.insertNotice(notice)
    suspend fun deleteNotice(notice: NoticeEntity) = appDao.deleteNotice(notice)

    // --- Settings ---
    suspend fun getSetting(key: String): String {
        return appDao.getSetting(key)?.value ?: ""
    }
    suspend fun saveSetting(key: String, value: String) {
        appDao.insertSetting(SettingsEntity(key, value))
    }

    // --- Global Sync to Google Sheets ---
    suspend fun syncAllTablesToSheets(): String {
        val gasUrl = getSetting("gas_url")
        if (gasUrl.isEmpty()) {
            return "Error: Please enter your Google Apps Script URL in App Settings first."
        }

        val results = mutableListOf<String>()

        // 1. Sync Students
        try {
            val students = allStudents.first()
            val array = JSONArray()
            students.forEach { s ->
                val obj = JSONObject().apply {
                    put("rollNumber", s.rollNumber)
                    put("admissionNumber", s.admissionNumber)
                    put("name", s.name)
                    put("fatherName", s.fatherName)
                    put("motherName", s.motherName)
                    put("dob", s.dob)
                    put("gender", s.gender)
                    put("address", s.address)
                    put("district", s.district)
                    put("state", s.state)
                    put("pinCode", s.pinCode)
                    put("mobile", s.mobile)
                    put("guardianMobile", s.guardianMobile)
                    put("email", s.email)
                    put("course", s.course)
                    put("batch", s.batch)
                    put("admissionDate", s.admissionDate)
                    put("photoUrl", s.photoUrl)
                    put("aadhaar", s.aadhaar)
                    put("remark", s.remark)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Students", array)
            results.add("Students: $res")
        } catch (e: Exception) {
            results.add("Students error: ${e.message}")
        }

        // 2. Sync Teachers
        try {
            val teachers = allTeachers.first()
            val array = JSONArray()
            teachers.forEach { t ->
                val obj = JSONObject().apply {
                    put("id", t.id)
                    put("name", t.name)
                    put("email", t.email)
                    put("mobile", t.mobile)
                    put("department", t.department)
                    put("joinDate", t.joinDate)
                    put("photoUrl", t.photoUrl)
                    put("status", t.status)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Teachers", array)
            results.add("Teachers: $res")
        } catch (e: Exception) {
            results.add("Teachers error: ${e.message}")
        }

        // 3. Sync Fees
        try {
            val fees = allFees.first()
            val array = JSONArray()
            fees.forEach { f ->
                val obj = JSONObject().apply {
                    put("receiptNumber", f.receiptNumber)
                    put("studentRollNumber", f.studentRollNumber)
                    put("courseFee", f.courseFee)
                    put("paidAmount", f.paidAmount)
                    put("pendingAmount", f.pendingAmount)
                    put("discount", f.discount)
                    put("paymentDate", f.paymentDate)
                    put("paymentMode", f.paymentMode)
                    put("remarks", f.remarks)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Fees", array)
            results.add("Fees: $res")
        } catch (e: Exception) {
            results.add("Fees error: ${e.message}")
        }

        // 4. Sync Attendance
        try {
            val att = allAttendance.first()
            val array = JSONArray()
            att.forEach { a ->
                val obj = JSONObject().apply {
                    put("id", a.id)
                    put("date", a.date)
                    put("studentRollNumber", a.studentRollNumber)
                    put("status", a.status)
                    put("course", a.course)
                    put("batch", a.batch)
                    put("markedBy", a.markedBy)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Attendance", array)
            results.add("Attendance: $res")
        } catch (e: Exception) {
            results.add("Attendance error: ${e.message}")
        }

        // 5. Sync Results
        try {
            val resultsList = allResults.first()
            val array = JSONArray()
            resultsList.forEach { r ->
                val obj = JSONObject().apply {
                    put("id", r.id)
                    put("studentRollNumber", r.studentRollNumber)
                    put("examName", r.examName)
                    put("marksJson", r.marksJson)
                    put("totalPercentage", r.totalPercentage)
                    put("grade", r.grade)
                    put("rank", r.rank)
                    put("generatedPdfUrl", r.generatedPdfUrl)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Results", array)
            results.add("Results: $res")
        } catch (e: Exception) {
            results.add("Results error: ${e.message}")
        }

        // 6. Sync Notices
        try {
            val notices = allNotices.first()
            val array = JSONArray()
            notices.forEach { n ->
                val obj = JSONObject().apply {
                    put("id", n.id)
                    put("title", n.title)
                    put("content", n.content)
                    put("date", n.date)
                    put("roleTarget", n.roleTarget)
                    put("attachmentUrl", n.attachmentUrl)
                }
                array.put(obj)
            }
            val res = GasClient.syncTable(gasUrl, "Notices", array)
            results.add("Notices: $res")
        } catch (e: Exception) {
            results.add("Notices error: ${e.message}")
        }

        return results.joinToString("\n")
    }
}
