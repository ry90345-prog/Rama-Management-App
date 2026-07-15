package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val rollNumber: String,
    val admissionNumber: String,
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dob: String,
    val gender: String,
    val address: String,
    val district: String,
    val state: String,
    val pinCode: String,
    val mobile: String,
    val guardianMobile: String,
    val email: String,
    val course: String,
    val batch: String,
    val admissionDate: String,
    val photoUrl: String,
    val aadhaar: String,
    val remark: String
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val department: String,
    val joinDate: String,
    val photoUrl: String,
    val status: String // Active / Inactive
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val studentRollNumber: String,
    val status: String, // Present, Absent, Late
    val course: String,
    val batch: String,
    val markedBy: String // Teacher ID / Admin
)

@Entity(tableName = "fees")
data class FeeEntity(
    @PrimaryKey val receiptNumber: String,
    val studentRollNumber: String,
    val courseFee: Double,
    val paidAmount: Double,
    val pendingAmount: Double,
    val discount: Double,
    val paymentDate: String,
    val paymentMode: String, // Cash, UPI, Card, NetBanking
    val remarks: String
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val duration: String,
    val syllabus: String
)

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val courseId: String,
    val timing: String,
    val teacherId: String
)

@Entity(tableName = "homework")
data class HomeworkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val course: String,
    val batch: String,
    val dueDate: String,
    val fileUrl: String,
    val teacherName: String,
    val submissionsJson: String = "[]" // JSON list of student submissions
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // PDF, Video, Image, Note, Document
    val url: String,
    val course: String,
    val description: String
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey val id: String, // studentRollNumber + "_" + examName
    val studentRollNumber: String,
    val examName: String,
    val marksJson: String, // JSON map of subject to marks
    val totalPercentage: Double,
    val grade: String,
    val rank: Int,
    val generatedPdfUrl: String = ""
)

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val date: String,
    val roleTarget: String, // All, Teacher, Student
    val attachmentUrl: String = ""
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val key: String,
    val value: String
)
