package com.example.proyekakhirpapb.navigation

sealed class Screen (val route: String){
    object Matkul : Screen(route = "Matkul")
    object Tugas : Screen(route = "Tugas")
    object Profile : Screen(route = "Profile")
    object Login : Screen(route = "Login")
    object Notes : Screen(route = "Notes")
}