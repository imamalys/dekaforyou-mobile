package id.ias.dekaforyou.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AnnouncementModel(
    var id: Int?,
    var pesan: String?
):Parcelable