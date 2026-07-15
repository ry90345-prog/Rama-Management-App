package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        TeacherEntity::class,
        AttendanceEntity::class,
        FeeEntity::class,
        CourseEntity::class,
        BatchEntity::class,
        HomeworkEntity::class,
        StudyMaterialEntity::class,
        ResultEntity::class,
        NoticeEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rama_erp_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(database.appDao())
                }
            }
        }

        private suspend fun seedDatabase(dao: AppDao) {
            // Seed Settings
            dao.insertSetting(SettingsEntity("gas_url", ""))
            dao.insertSetting(SettingsEntity("institute_name", "Rama Technical Institute"))
            dao.insertSetting(SettingsEntity("remember_login", "false"))
            dao.insertSetting(SettingsEntity("logged_in_role", "")) // Admin, Teacher, Student
            dao.insertSetting(SettingsEntity("logged_in_user_id", ""))

            // Seed Courses
            val adca = CourseEntity("C001", "Advanced Diploma in Computer Applications", "ADCA", "12 Months", "Office Suite, Tally, Photoshop, CorelDraw, Web Design, C programming")
            val dca = CourseEntity("C002", "Diploma in Computer Applications", "DCA", "6 Months", "Computer Fundamentals, Windows, MS Office, Internet")
            val tally = CourseEntity("C003", "Tally Prime ERP & GST", "TallyPrime", "3 Months", "Accounting, Inventory, Taxation, GST Returns, TDS")
            val python = CourseEntity("C004", "Python Web Development & AI", "PythonAI", "6 Months", "Python Basics, Django, Flask, SQLite, Gemini API Integration")
            dao.insertCourse(adca)
            dao.insertCourse(dca)
            dao.insertCourse(tally)
            dao.insertCourse(python)

            // Seed Batches
            val b1 = BatchEntity("B001", "Morning Batch A (8 AM - 10 AM)", "C001", "08:00 AM", "T001")
            val b2 = BatchEntity("B002", "Noon Batch B (12 PM - 2 PM)", "C003", "12:00 PM", "T002")
            val b3 = BatchEntity("B003", "Evening Batch C (4 PM - 6 PM)", "C004", "04:00 PM", "T001")
            dao.insertBatch(b1)
            dao.insertBatch(b2)
            dao.insertBatch(b3)

            // Seed Teachers
            val t1 = TeacherEntity("T001", "Prof. Rajesh Kumar", "rajesh@rama.edu.in", "9876543210", "Computer Science", "2024-01-10", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", "Active")
            val t2 = TeacherEntity("T002", "Smt. Priya Sharma", "priya@rama.edu.in", "9876543211", "Finance & Tally", "2024-03-15", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150", "Active")
            dao.insertTeacher(t1)
            dao.insertTeacher(t2)

            // Seed Students
            val s1 = StudentEntity(
                rollNumber = "R26001",
                admissionNumber = "ADM-2026-101",
                name = "Aman Verma",
                fatherName = "Shri Ramesh Verma",
                motherName = "Smt. Sunita Verma",
                dob = "2005-08-15",
                gender = "Male",
                address = "12, Saket Nagar",
                district = "Kanpur",
                state = "Uttar Pradesh",
                pinCode = "208001",
                mobile = "8888877777",
                guardianMobile = "9999988888",
                email = "aman.verma@gmail.com",
                course = "Advanced Diploma in Computer Applications",
                batch = "Morning Batch A (8 AM - 10 AM)",
                admissionDate = "2026-01-05",
                photoUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150",
                aadhaar = "1234 5678 9012",
                remark = "Excellent coding potential"
            )
            val s2 = StudentEntity(
                rollNumber = "R26002",
                admissionNumber = "ADM-2026-102",
                name = "Anjali Mishra",
                fatherName = "Shri Vipin Mishra",
                motherName = "Smt. Radha Mishra",
                dob = "2006-03-22",
                gender = "Female",
                address = "45, Civil Lines",
                district = "Kanpur",
                state = "Uttar Pradesh",
                pinCode = "208001",
                mobile = "7777766666",
                guardianMobile = "8888899999",
                email = "anjali.mishra@gmail.com",
                course = "Tally Prime ERP & GST",
                batch = "Noon Batch B (12 PM - 2 PM)",
                admissionDate = "2026-02-10",
                photoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                aadhaar = "9876 5432 1098",
                remark = "Very attentive in lab practice"
            )
            val s3 = StudentEntity(
                rollNumber = "R26003",
                admissionNumber = "ADM-2026-103",
                name = "Vikram Singh",
                fatherName = "Shri Kuldeep Singh",
                motherName = "Smt. Meena Singh",
                dob = "2004-11-30",
                gender = "Male",
                address = "102, Kakadeo",
                district = "Kanpur",
                state = "Uttar Pradesh",
                pinCode = "208025",
                mobile = "9111122222",
                guardianMobile = "9333344444",
                email = "vikram.singh@gmail.com",
                course = "Python Web Development & AI",
                batch = "Evening Batch C (4 PM - 6 PM)",
                admissionDate = "2026-03-01",
                photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                aadhaar = "4567 8901 2345",
                remark = "Interested in Machine Learning"
            )
            dao.insertStudent(s1)
            dao.insertStudent(s2)
            dao.insertStudent(s3)

            // Seed Fees
            dao.insertFee(FeeEntity("REC-1001", "R26001", 15000.0, 5000.0, 10000.0, 0.0, "2026-01-05", "UPI", "First Installment Paid"))
            dao.insertFee(FeeEntity("REC-1002", "R26002", 8000.0, 8000.0, 0.0, 500.0, "2026-02-10", "Cash", "Full Fee Cleared with discount"))
            dao.insertFee(FeeEntity("REC-1003", "R26003", 18000.0, 6000.0, 12000.0, 0.0, "2026-03-01", "Card", "First Installment Paid"))

            // Seed Attendance
            dao.insertAttendance(listOf(
                AttendanceEntity(0, "2026-07-13", "R26001", "Present", "C001", "Morning Batch A (8 AM - 10 AM)", "T001"),
                AttendanceEntity(0, "2026-07-13", "R26002", "Present", "C003", "Noon Batch B (12 PM - 2 PM)", "T002"),
                AttendanceEntity(0, "2026-07-13", "R26003", "Absent", "C004", "Evening Batch C (4 PM - 6 PM)", "T001")
            ))

            // Seed Results
            val r1 = ResultEntity(
                id = "R26001_MidTerm",
                studentRollNumber = "R26001",
                examName = "Mid Term Exam",
                marksJson = "{\"MS Word\": 85, \"Excel\": 92, \"PowerPoint\": 88}",
                totalPercentage = 88.33,
                grade = "A",
                rank = 1
            )
            val r2 = ResultEntity(
                id = "R26002_MidTerm",
                studentRollNumber = "R26002",
                examName = "Mid Term Exam",
                marksJson = "{\"Accounting Concepts\": 95, \"Ledgers & Vouchers\": 90, \"GST Compliance\": 85}",
                totalPercentage = 90.0,
                grade = "A+",
                rank = 1
            )
            dao.insertResult(r1)
            dao.insertResult(r2)

            // Seed Homework
            val hw1 = HomeworkEntity("HW001", "Build Custom Billing Format", "Design a custom multi-item invoice sheet in MS Excel using advanced formulas like VLOOKUP and SUMIF.", "C001", "Morning Batch A (8 AM - 10 AM)", "2026-07-18", "https://sheets.google.com", "Prof. Rajesh Kumar")
            val hw2 = HomeworkEntity("HW002", "Python Flask API Setup", "Create an API with 3 routes (students, courses, batches) using Flask and return JSON format.", "C004", "Evening Batch C (4 PM - 6 PM)", "2026-07-20", "https://github.com", "Prof. Rajesh Kumar")
            dao.insertHomework(hw1)
            dao.insertHomework(hw2)

            // Seed Study Materials
            val sm1 = StudyMaterialEntity("SM001", "Computer Basics Quick Handbook", "PDF", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "C001", "A full handbook covering computer components, ports, and operating system basics.")
            val sm2 = StudyMaterialEntity("SM002", "Tally Prime Ledger Ledger Creation Guide", "Document", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "C003", "Step-by-step document explaining simple and multiple ledger creation with groups.")
            val sm3 = StudyMaterialEntity("SM003", "Advanced Python Django Video Series", "Video", "https://www.w3.org/2010/05/video/mediafiles/wildlife.mp4", "C004", "Video tutorial demonstrating Django Model-View-Template pattern.")
            dao.insertStudyMaterial(sm1)
            dao.insertStudyMaterial(sm2)
            dao.insertStudyMaterial(sm3)

            // Seed Notices
            dao.insertNotice(NoticeEntity("N001", "Independence Day Celebration Notice", "Dear Students and Faculty, Rama Technical Institute is celebrating Independence Day on August 15th with flag hosting, cultural dances, and snacks. Attendance is mandatory for all.", "2026-07-14", "All"))
            dao.insertNotice(NoticeEntity("N002", "Faculty Meeting on Academic Calendar", "All teaching staff are requested to attend a brief progress review meeting in the director office tomorrow at 2:00 PM.", "2026-07-14", "Teacher"))
        }
    }
}
