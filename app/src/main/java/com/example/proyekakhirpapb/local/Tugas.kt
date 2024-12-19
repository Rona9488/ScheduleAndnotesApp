package com.example.proyekakhirpapb.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "matkul")
    var matkul: String,
    @ColumnInfo(name = "detail_tugas")
    var detailTugas: String,
    @ColumnInfo(name = "deadline_tugas")
    var deadlineTugas: String,
    @ColumnInfo(name = "kategori")
    var kategori: String,
    @ColumnInfo(name = "selesai")
    var selesai: Boolean,
    @ColumnInfo(name = "foto_uri") // Tambahkan kolom URI foto
    var fotoUri: String? = null,
    @ColumnInfo(name = "completedDate")
    var completedDate: String? = null,
    @ColumnInfo(name = "deskripsi_foto") // Tambahkan kolom deskripsi foto
    var deskripsiFoto: String? = null
) : Parcelable
