package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Students ---
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE rollNumber = :roll")
    suspend fun getStudentByRoll(roll: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE rollNumber = :roll")
    suspend fun deleteStudentByRoll(roll: String)

    // --- Teachers ---
    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: String): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)

    @Delete
    suspend fun deleteTeacher(teacher: TeacherEntity)

    // --- Attendance ---
    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE date = :date AND course = :course AND batch = :batch")
    suspend fun clearAttendanceForBatch(date: String, course: String, batch: String)

    // --- Fees ---
    @Query("SELECT * FROM fees")
    fun getAllFees(): Flow<List<FeeEntity>>

    @Query("SELECT * FROM fees WHERE studentRollNumber = :roll")
    fun getFeesForStudent(roll: String): Flow<List<FeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: FeeEntity)

    @Delete
    suspend fun deleteFee(fee: FeeEntity)

    // --- Courses ---
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    // --- Batches ---
    @Query("SELECT * FROM batches")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Delete
    suspend fun deleteBatch(batch: BatchEntity)

    // --- Homework ---
    @Query("SELECT * FROM homework")
    fun getAllHomework(): Flow<List<HomeworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: HomeworkEntity)

    @Delete
    suspend fun deleteHomework(homework: HomeworkEntity)

    // --- Study Materials ---
    @Query("SELECT * FROM study_materials")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)

    @Delete
    suspend fun deleteStudyMaterial(material: StudyMaterialEntity)

    // --- Results ---
    @Query("SELECT * FROM results")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE studentRollNumber = :roll")
    fun getResultsForStudent(roll: String): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity)

    @Delete
    suspend fun deleteResult(result: ResultEntity)

    // --- Notices ---
    @Query("SELECT * FROM notices ORDER BY date DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity)

    @Delete
    suspend fun deleteNotice(notice: NoticeEntity)

    // --- Settings ---
    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingsEntity)
}
