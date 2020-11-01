package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApprovalRequestModel(
    var id: Int?,
    @SerializedName("nama_karyawan")
    var namaKaryawan: String?,
    @SerializedName("tanggal_awal")
    var tanggalAwal: String?,
    @SerializedName("tanggal_akhir")
    var tanggalAkhir: String?,
    var data: RequestPelengkap?,
    var jenis: String?,
    var status: String?
): Parcelable

@Parcelize
data class RequestPelengkap(
    var alasan: String?,
    var jenis: String?
): Parcelable