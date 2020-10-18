package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var id: Int?,
    var profile: String?,
    @SerializedName("id_karyawan")
    var idKaryawan: String?,
    @SerializedName("no_ktp")
    var noKtp: String?,
    var name: String?,
    var email: String?,
    var username: String?,
    var pin: String?,
    @SerializedName("no_telp_rumah")
    var noTelpRumah: String?,
    @SerializedName("no_telp_hp")
    var noTelpHp: String?,
    @SerializedName("tanggal_lahir")
    var tglLahir: String?,
    @SerializedName("tempat_lahir")
    var tempatLahir: String?,
    @SerializedName("jenis_kelamin")
    var jenisKelamin: String?,
    var divisi: String?,
    var bidang: String?,
    var unit: String?,
    var jabatan: String?,
    var shift: String?,
    var status: String?,
    var role: String?,
    var imei: String?,
    @SerializedName("kategori_cuti")
    var kategoriCuti: String?,
    @SerializedName("atasan_id")
    var atasanId: String?,
    @SerializedName("atasan_nama")
    var atasanNama: String?,
    @SerializedName("target_bulan")
    var targetBulan: String?,
    @SerializedName("target_jam")
    var targetJam: String?,
    @SerializedName("tanggal_bergabung")
    var tanggalBergabung: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    var token: String?
): Parcelable