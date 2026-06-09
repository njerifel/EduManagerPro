package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: StudentViewModel = viewModel()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(onTimeout = {
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        })
                    }
                    composable("login") {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            studentCount = viewModel.students.size,
                            onNavigateToList = { navController.navigate("list") },
                            onNavigateToAdd = { navController.navigate("add") },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("list") {
                        StudentListScreen(
                            students = viewModel.students,
                            onBack = { navController.popBackStack() },
                            onStudentClick = { student ->
                                navController.navigate("detail/${student.id}")
                            },
                            onAddClick = { navController.navigate("add") }
                        )
                    }
                    composable(
                        "detail/{studentId}",
                        arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val studentId = backStackEntry.arguments?.getString("studentId")
                        val student = viewModel.students.find { it.id == studentId }
                        StudentDetailScreen(
                            student = student,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("add") {
                        AddStudentScreen(
                            onStudentAdded = { name, email, course ->
                                viewModel.addStudent(name, email, course)
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
