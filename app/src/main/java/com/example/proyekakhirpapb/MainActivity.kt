package com.example.proyekakhirpapb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.proyekakhirpapb.navigation.NavigationItem
import com.example.proyekakhirpapb.navigation.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyekakhirpapb.local.TugasViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyekakhirpapb.screen.MatkulScreen
import com.example.proyekakhirpapb.screen.NotesScreen
import com.example.proyekakhirpapb.screen.ProfileScreen
import com.example.proyekakhirpapb.screen.TugasScreen
import com.example.proyekakhirpapb.ui.theme.ProyekakhirpapbTheme

class MainActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        val authViewModel: AuthViewModel by viewModels()

        // Logout every time the app starts
        authViewModel.logout()

        setContent {
            ProyekakhirpapbTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    // Memantau status autentikasi
                    val authState by authViewModel.authState.observeAsState()

                    Scaffold(
                        bottomBar = {
                            // Tampilkan BottomBar hanya jika pengguna sudah login
                            if (authState is AuthState.Authenticated) {
                                BottomBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        val tugasViewModel: TugasViewModel by viewModels()

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Login.route,
                            Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Login.route) {
                                LoginPage(authViewModel, navController)
                            }
                            composable(Screen.Matkul.route) {
                                MatkulScreen(navController, authViewModel)
                            }
                            composable(Screen.Tugas.route) {
                                TugasScreen()
                            }
                            composable(Screen.Profile.route) { // HomeScreen
                                ProfileScreen(homeViewModel = viewModel())
                            }
                            composable(Screen.Notes.route) {
                                NotesScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    NavigationBar {
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.Matkul),
                icon = Icons.Default.List,
                screen = Screen.Matkul
            ),
            NavigationItem(
                title = stringResource(R.string.Tugas),
                icon = Icons.Default.Add,
                screen = Screen.Tugas
            ),
            NavigationItem(
                title = stringResource(R.string.Notes),
                icon = Icons.Default.StickyNote2,
                screen = Screen.Notes
            ),
            NavigationItem(
                title = stringResource(R.string.Profile),
                icon = Icons.Default.Person,
                screen = Screen.Profile
            ),

        )
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                    )
                },
                label = { Text(item.title) },
                selected = navController.currentDestination?.route == item.screen.route,
                onClick = {
                    when (item.screen.route) {
                        Screen.Matkul.route -> {
                            // Navigasi ke MatkulScreen
                            navController.navigate(Screen.Matkul.route) {
                                // Hapus semua layar sebelumnya agar kembali ke LoginScreen saat menekan kembali
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        }
                        Screen.Tugas.route, Screen.Profile.route, Screen.Notes.route -> {
                            // Navigasi ke TugasScreen atau ProfileScreen
                            navController.navigate(item.screen.route) {
                                // Kembali ke MatkulScreen jika sudah di TugasScreen atau ProfileScreen
                                popUpTo(Screen.Matkul.route) {
                                    saveState = true // Menyimpan state untuk MatkulScreen
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}



