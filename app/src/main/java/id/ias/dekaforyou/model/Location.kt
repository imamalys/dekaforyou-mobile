package id.ias.dekaforyou.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (
    var name: String?,
    @SerializedName("jarak")
    var distance: String?,
    var radius: String?,
    var latitude: String?,
    var longitude: String?,
    var isAllowed: Boolean = false
): Parcelable