package com.example.proyekakhirpapb.screen

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.proyekakhirpapb.local.Tugas
import com.example.proyekakhirpapb.local.TugasViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proyekakhirpapb.local.ReminderWorker
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity

@Composable
fun TugasScreen(tugasViewModel: TugasViewModel = viewModel()) {
    var matkul by remember { mutableStateOf(TextFieldValue("")) }
    var detailTugas by remember { mutableStateOf(TextFieldValue("")) }
    var deadlineTugas by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    var selectedTab by remember { mutableStateOf("To Do") }
    var photoDescription by remember { mutableStateOf("") }
    var showTopSection by remember { mutableStateOf(true) } // State untuk kontrol visibilitas bagian atas
    val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001

    var showFilterKategoriDialog by remember { mutableStateOf(false) }
    var filterKategori by remember { mutableStateOf("") } // State untuk filter kategori
    var filterTanggal by remember { mutableStateOf("") } // State untuk filter tanggal
    var showFilterDatePicker by remember { mutableStateOf(false) } // DatePicker untuk filter tanggal
    val scrollState = rememberLazyListState()

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val context = LocalContext.current

    val daftarTugas by tugasViewModel.tugasList.observeAsState(emptyList())

    // Filter tugas berdasarkan kategori, tanggal, dan status
    val filteredTasks = remember(daftarTugas, filterKategori, filterTanggal, selectedTab) {
        daftarTugas.filter { tugas ->
            (filterKategori.isEmpty() || tugas.kategori == filterKategori) &&
                    (filterTanggal.isEmpty() || tugas.deadlineTugas == filterTanggal) &&
                    (selectedTab == "To Do" && !tugas.selesai || selectedTab == "Done" && tugas.selesai)
        }
    }

    val kategoriOptions = listOf("Kegiatan", "Tugas", "Kelas")

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                showCamera = true
            }
        }
    )

    LaunchedEffect(scrollState.firstVisibleItemIndex) {
        showTopSection = scrollState.firstVisibleItemIndex == 0
    }

    if (showCamera) {
        CameraCapture(
            onImageCaptured = { uri ->
                imageUris = imageUris + uri
                showCamera = false
            },
            onError = { _ ->
                showCamera = false
            },
            cameraExecutor = cameraExecutor
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (showTopSection) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = matkul,
                            onValueChange = { matkul = it },
                            label = { Text("Mata Kuliah / Kegiatan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = detailTugas,
                            onValueChange = { detailTugas = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    if (kategori.isNotEmpty()) kategori else "Pilih Kategori",
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    if (deadlineTugas.isNotEmpty()) "Deadline: $deadlineTugas" else "Pilih Deadline",
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Camera Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Buka Kamera")
                }
            }

            if (imageUris.isNotEmpty()) {
                Text("Preview Foto:", modifier = Modifier.align(Alignment.CenterHorizontally))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(imageUris) { uri ->
                        Column(
                            modifier = Modifier
                                .padding(4.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            OutlinedTextField(
                                value = photoDescription,
                                onValueChange = { photoDescription = it },
                                label = { Text("Deskripsi Foto") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Tambah Tugas
            Button(
                onClick = {
                    // Cek apakah izin untuk notifikasi sudah diberikan
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Jika belum diberikan, minta izin
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            REQUEST_CODE_NOTIFICATION_PERMISSION
                        )
                    } else {
                        // Jika izin sudah diberikan, lanjutkan dengan menambahkan tugas dan penjadwalan pengingat
                        if (matkul.text.isNotBlank() && detailTugas.text.isNotBlank() &&
                            deadlineTugas.isNotBlank() && kategori.isNotBlank()
                        ) {
                            tugasViewModel.insert(
                                Tugas(
                                    matkul = matkul.text,
                                    detailTugas = detailTugas.text,
                                    deadlineTugas = deadlineTugas,
                                    selesai = false,
                                    kategori = kategori,
                                    fotoUri = if (imageUris.isNotEmpty()) imageUris.last().toString() else null,
                                    deskripsiFoto = photoDescription.takeIf { it.isNotBlank() }
                                )
                            )

                            // Menjadwalkan pengingat
                            scheduleReminder(context, matkul.text, deadlineTugas)

                            // Reset fields setelah menambahkan tugas
                            matkul = TextFieldValue("")
                            detailTugas = TextFieldValue("")
                            deadlineTugas = ""
                            kategori = ""
                            imageUris = listOf()
                            photoDescription = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tambah")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Filter Kategori dan Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showFilterKategoriDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterKategori.isNotEmpty()) "Kategori: $filterKategori" else "Filter Kategori")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showFilterDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterTanggal.isNotEmpty()) "Tanggal: $filterTanggal" else "Filter Tanggal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row untuk "To Do" dan "Done"
            TabRow(selectedTabIndex = if (selectedTab == "To Do") 0 else 1) {
                Tab(
                    selected = selectedTab == "To Do",
                    onClick = { selectedTab = "To Do" },
                    text = { Text("To Do") }
                )
                Tab(
                    selected = selectedTab == "Done",
                    onClick = { selectedTab = "Done" },
                    text = { Text("Done") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredTasks.isEmpty()) {
                    Text(
                        text = "Tidak ada tugas yang ditemukan sesuai filter.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(filteredTasks) { tugas ->
                            TugasItem(
                                tugas = tugas,
                                onComplete = {
                                    if (tugas.selesai) {
                                        tugasViewModel.undoTask(tugas.id)
                                    } else {
                                        tugasViewModel.updateStatus(tugas.id, true)
                                    }
                                },
                                onCompletedDateChange = { today ->
                                    tugasViewModel.updateCompletedDate(tugas.id, today)
                                },
                                onDelete = {
                                    tugasViewModel.deleteTugas(tugas.id)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol reset filter
            Button(
                onClick = {
                    filterKategori = ""
                    filterTanggal = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Filter")
            }
        }
    }


// Dialog untuk filter kategori
    if (showFilterKategoriDialog) {
        AlertDialog(
            onDismissRequest = { showFilterKategoriDialog = false },
            title = { Text("Pilih Kategori") },
            text = {
                Column {
                    kategoriOptions.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    filterKategori = option
                                    showFilterKategoriDialog = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFilterKategoriDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // DatePicker untuk Filter Tanggal
    if (showFilterDatePicker) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            LocalContext.current,
            { _, selectedYear, selectedMonth, selectedDay ->
                filterTanggal = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                showFilterDatePicker = false
            },
            year, month, day
        ).show()
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                deadlineTugas = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                showDatePicker = false
            },
            year, month, day
        ).show()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pilih Kategori") },
            text = {
                Column {
                    kategoriOptions.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    kategori = option
                                    showDialog = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun CameraCapture(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    cameraExecutor: ExecutorService
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    ctx as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                onError(exc as ImageCaptureException)
            }
            previewView
        })

        // Centered Camera Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    val file = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onImageCaptured(Uri.fromFile(file))
                        }

                        override fun onError(exception: ImageCaptureException) {
                            onError(exception)
                        }
                    })
                }
            ) {
                Text("Capture")
            }
        }
    }
}

@Composable
fun TugasItem(tugas: Tugas, onComplete: () -> Unit, onCompletedDateChange: (String) -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tugas.matkul, style = MaterialTheme.typography.bodyLarge)
                Text(tugas.detailTugas, style = MaterialTheme.typography.bodyMedium)
                Text("Deadline: ${tugas.deadlineTugas}", color = Color.Gray)
                Text("Kategori: ${tugas.kategori}", color = Color.Gray)
                if (!tugas.fotoUri.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(tugas.fotoUri),
                        contentDescription = "Foto",
                        modifier = Modifier.size(100.dp)
                    )
                }
                if (!tugas.deskripsiFoto.isNullOrEmpty()) {
                    Text("Deskripsi Foto: ${tugas.deskripsiFoto}")
                }
            }
            Column(horizontalAlignment = Alignment.End) {
            Button(onClick = {
                onComplete()
                if (!tugas.selesai) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    onCompletedDateChange(today)
                }
            }
            ) {
                Text(if (tugas.selesai) "Undo" else "Done")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Tombol Delete dengan ikon tempat sampah
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}
    }

fun scheduleReminder(context: Context, taskTitle: String, deadline: String) {
    val workManager = WorkManager.getInstance(context)

    // Hitung waktu untuk H-1 sebelum deadline
    val deadlineMillis = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(deadline)?.time ?: return
    val reminderTimeMillis = deadlineMillis - TimeUnit.DAYS.toMillis(1)
    val delay = reminderTimeMillis - System.currentTimeMillis()

    if (delay > 0) {
        val inputData = Data.Builder()
            .putString("taskTitle", taskTitle)
            .putString("deadline", deadline)
            .build()

        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueue(reminderRequest)
    }
}
