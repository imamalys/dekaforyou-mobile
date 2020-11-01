package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftModel(
    var name: String?,
    @SerializedName("in")
    var clockIn: String?,
    @SerializedName("out")
    var clockOut: String?,
    var terlambat: String?,
    @SerializedName("time_stamp")
    var timeStamp: String?
): Parcelable

@Parcelize
data class ShiftStatusModel(
    @SerializedName("clock_in")
    var clockIn: Boolean?,
    @SerializedName("clock_out")
    var clockOut: Boolean?
): Parcelable

@Parcelize
data class KaryawanModel(
    var id: Int?,
    var profile: String?,
    var name: String
):Parcelable