package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(
    @SerializedName("approval_id")
    var approvalId: Int?,
    var name: String?,
    var date: String?,
    var category: String?,
    var alasan: String?,
    var clockIn: Boolean?,
    var ClockOut: Boolean?,
    var pengganti: String?
): Parcelable

@Parcelize
data class NotificationDataModel(
    var data: NotificationModel?,
    var tglPengajuan: String?
): Parcelable