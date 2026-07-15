package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ErpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val viewModel: ErpViewModel = viewModel()
                    val loggedInRole by viewModel.loggedInRole.collectAsState()

                    AnimatedContent(
                        targetState = loggedInRole,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "MainScreenRouting"
                    ) { role ->
                        when (role) {
                            "Admin" -> {
                                AdminDashboard(
                                    viewModel = viewModel,
                                    onLogout = { viewModel.logout() }
                                )
                            }
                            "Teacher" -> {
                                TeacherDashboard(
                                    viewModel = viewModel,
                                    onLogout = { viewModel.logout() }
                                )
                            }
                            "Student" -> {
                                StudentDashboard(
                                    viewModel = viewModel,
                                    onLogout = { viewModel.logout() }
                                )
                            }
                            else -> {
                                AuthScreen(
                                    viewModel = viewModel,
                                    onLoginSuccess = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
