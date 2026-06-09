package com.example.myapplication

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val _students = mutableStateListOf<Student>()
    val students: List<Student> = _students
    private val file = File(application.filesDir, "students.txt")

    init {
        loadStudents()
    }

    private fun loadStudents() {
        if (file.exists()) {
            val lines = file.readLines()
            lines.forEach { line ->
                val parts = line.split("|")
                if (parts.size == 4) {
                    _students.add(Student(parts[0], parts[1], parts[2], parts[3]))
                }
            }
        }
    }

    private fun saveStudents() {
        viewModelScope.launch(Dispatchers.IO) {
            val content = _students.joinToString("\n") { "${it.id}|${it.name}|${it.email}|${it.course}" }
            file.writeText(content)
        }
    }

    fun addStudent(name: String, email: String, course: String) {
        val newStudent = Student(
            id = System.currentTimeMillis().toString(),
            name = name,
            email = email,
            course = course
        )
        _students.add(newStudent)
        saveStudents()
    }
}
