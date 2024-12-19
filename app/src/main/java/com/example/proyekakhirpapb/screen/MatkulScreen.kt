package com.example.proyekakhirpapb.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavHostController
import com.example.proyekakhirpapb.navigation.Screen
import com.example.proyekakhirpapb.AuthViewModel
import com.google.firebase.firestore.Query

@Composable
fun MatkulScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""
    val jadwalKuliahList = remember { mutableStateListOf<Jadwal>() }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var jadwalToDelete by remember { mutableStateOf<Jadwal?>(null) }
    var jadwalToEdit by remember { mutableStateOf<Jadwal?>(null) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            fetchJadwalKuliah(userId, firestore, jadwalKuliahList) {
                isLoading = false
            }
        }
    }

    BackHandler {
        authViewModel.logout()
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Tambah Jadwal")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Jadwal Kuliah 🐾",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF0062FF),
                    modifier = Modifier.padding(bottom = 13.dp)
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFBA68C8))
                } else if (jadwalKuliahList.isEmpty()) {
                    Text("Tidak ada jadwal 😿")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(jadwalKuliahList) { index, jadwal ->
                            val backgroundColor = if (index % 2 == 0) Color(0xFFFFEEDD) else Color(0xFFDDF2FF)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Pets,
                                            contentDescription = null,
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = jadwal.mataKuliah,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CalendarToday,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Hari: ${jadwal.hari}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Dosen: ${jadwal.dosen}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.AccessTime,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Waktu: ${jadwal.jam}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Place,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Ruang: ${jadwal.ruang}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = {
                                            jadwalToEdit = jadwal
                                            showEditDialog = true
                                        }) {
                                            Text("Edit", color = Color.Blue)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TextButton(onClick = {
                                            jadwalToDelete = jadwal
                                            showDeleteConfirmation = true
                                        }) {
                                            Text("Hapus", color = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddJadwalDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { jadwal ->
                firestore.collection("Jadwal").add(jadwal.copy(userId = userId)).addOnSuccessListener {
                    jadwalKuliahList.add(jadwal)
                    showAddDialog = false
                }
            }
        )
    }

    if (showEditDialog && jadwalToEdit != null) {
        EditJadwalDialog(
            jadwal = jadwalToEdit!!,
            firestore = firestore,
            jadwalKuliahList = jadwalKuliahList,
            onDismiss = { showEditDialog = false }
        )
    }

    if (showDeleteConfirmation && jadwalToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Hapus Jadwal") },
            text = { Text("Apakah Anda yakin ingin menghapus jadwal ini?") },
            confirmButton = {
                TextButton(onClick = {
                    deleteJadwal(jadwalToDelete!!, firestore, jadwalKuliahList)
                    showDeleteConfirmation = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun AddJadwalDialog(
    onDismiss: () -> Unit,
    onAdd: (Jadwal) -> Unit
) {
    var matkul by remember { mutableStateOf("") }
    var hari by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var dosen by remember { mutableStateOf("") }
    var ruang by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (matkul.isNotBlank() && hari.isNotBlank() && jam.isNotBlank() && dosen.isNotBlank()) {
                        onAdd(
                            Jadwal(
                                mataKuliah = matkul,
                                hari = hari,
                                jam = jam,
                                dosen = dosen,
                                ruang = ruang
                            )
                        )
                    }
                }
            ) {
                Text("Tambah", color = Color(0xFFBA68C8))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        title = { Text("Tambah Jadwal 🐱", color = Color(0xFFBA68C8)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = matkul, onValueChange = { matkul = it }, label = { Text("Nama Matkul") })
                TextField(value = hari, onValueChange = { hari = it }, label = { Text("Hari (e.g., Senin)") })
                TextField(value = jam, onValueChange = { jam = it }, label = { Text("Jam (e.g., 18:30)") })
                TextField(value = dosen, onValueChange = { dosen = it }, label = { Text("Dosen") })
                TextField(value = ruang, onValueChange = { ruang = it }, label = { Text("Ruang") })
            }
        }
    )
}

@Composable
fun EditJadwalDialog(
    jadwal: Jadwal,
    firestore: FirebaseFirestore,
    jadwalKuliahList: MutableList<Jadwal>,
    onDismiss: () -> Unit
) {
    var matkul by remember { mutableStateOf(jadwal.mataKuliah) }
    var hari by remember { mutableStateOf(jadwal.hari) }
    var jam by remember { mutableStateOf(jadwal.jam) }
    var dosen by remember { mutableStateOf(jadwal.dosen) }
    var ruang by remember { mutableStateOf(jadwal.ruang) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Update data in Firestore
                firestore.collection("Jadwal").document(jadwal.documentId)
                    .update(
                        mapOf(
                            "mataKuliah" to matkul,
                            "hari" to hari,
                            "jam" to jam,
                            "dosen" to dosen,
                            "ruang" to ruang
                        )
                    ).addOnSuccessListener {
                        // Update data in the local list
                        val index = jadwalKuliahList.indexOfFirst { it.documentId == jadwal.documentId }
                        if (index != -1) {
                            jadwalKuliahList[index] = jadwal.copy(
                                mataKuliah = matkul,
                                hari = hari,
                                jam = jam,
                                dosen = dosen,
                                ruang = ruang
                            )
                        }
                        onDismiss()
                    }
            }) {
                Text("Simpan", color = Color(0xFFBA68C8))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        title = { Text("Edit Jadwal", color = Color(0xFFBA68C8)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = matkul, onValueChange = { matkul = it }, label = { Text("Nama Matkul") })
                TextField(value = hari, onValueChange = { hari = it }, label = { Text("Hari (e.g., Senin)") })
                TextField(value = jam, onValueChange = { jam = it }, label = { Text("Jam (e.g., 18:30)") })
                TextField(value = dosen, onValueChange = { dosen = it }, label = { Text("Dosen") })
                TextField(value = ruang, onValueChange = { ruang = it }, label = { Text("Ruang") })
            }
        }
    )
}

fun fetchJadwalKuliah(
    userId: String,
    firestore: FirebaseFirestore,
    jadwalKuliahList: MutableList<Jadwal>,
    onComplete: () -> Unit
) {

    // Query ke Firestore berdasarkan userId
    firestore.collection("Jadwal")
        .whereEqualTo("userId", userId) // Pastikan field userId ada di dokumen Firestore
        .orderBy("hari", Query.Direction.DESCENDING) // Mengurutkan berdasarkan Hari secara descending
        .orderBy("jam", Query.Direction.DESCENDING)  // Mengurutkan berdasarkan Jam secara descending
        .get()
        .addOnSuccessListener { snapshot ->
            jadwalKuliahList.clear() // Bersihkan daftar sebelum menambahkan data baru
            if (!snapshot.isEmpty) {
                val jadwalList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Jadwal::class.java)?.copy(documentId = doc.id)
                }
                jadwalKuliahList.addAll(jadwalList)
            } else {
                Log.d("fetchJadwalKuliah", "Tidak ada jadwal ditemukan untuk userId: $userId")
            }
            onComplete() // Pemanggilan selesai
        }
        .addOnFailureListener { e ->
            onComplete() // Tetap panggil onComplete meski ada error
            Log.e("fetchJadwalKuliah", "Error fetching data", e)
        }
}


fun deleteJadwal(jadwal: Jadwal, firestore: FirebaseFirestore, jadwalKuliahList: MutableList<Jadwal>) {
    firestore.collection("Jadwal").document(jadwal.documentId).delete()
        .addOnSuccessListener {
            jadwalKuliahList.remove(jadwal)
        }
        .addOnFailureListener { exception ->
            Log.e("MatkulScreen", "Error deleting jadwal: $exception")
        }
}

data class Jadwal(
    val documentId: String = "",
    val mataKuliah: String = "",
    val hari: String = "",
    val jam: String = "",
    val dosen: String = "",
    val ruang: String = "",
    val userId: String = ""
)
