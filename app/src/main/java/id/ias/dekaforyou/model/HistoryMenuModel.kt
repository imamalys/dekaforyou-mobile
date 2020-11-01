package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoryMenuModel(
    @SerializedName("tukar_shift")
    var tukarShift: ArrayList<HistoryEntityModel>?,
    var cuti: ArrayList<HistoryEntityModel>?,
    var lembur: ArrayList<HistoryEntityModel>?,
    var absen: ArrayList<ShiftModel>?
): Parcelable

@Parcelize
data class HistoryEntityModel (
    var name: String?,
    var alasan: String?,
    var status: String?,
    var tambahan: String?,
    @SerializedName("time_stamp")
    var timeStamp: String?
): Parcelable