package id.ias.dekaforyou.http

import com.google.gson.JsonObject
import id.ias.dekaforyou.model.*
import kotlinx.android.parcel.RawValue
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/login")
    fun loginUser(@Body params: HashMap<String, Any>): Call<HttpResult<ArrayList<UserModel>>>
    @POST("/api/update-imei")
    fun updateImei(@Body params: String?): Call<HttpResult<ArrayList<ResponseBody>>>
    @GET("/api/profile")
    fun getProfile(): Call<HttpResult<ArrayList<UserModel>>>
    @POST("/api/cek-lokasi")
    fun getDistanceLocation(@Body params: HashMap<String, Any>): Call<HttpResult<ArrayList<LocationModel>>>
    @GET("/api/get-shift")
    fun getShift(): Call<HttpResult<ShiftModel>>
    @GET("/api/status")
    fun getAbsenStatus(): Call<HttpResult<ArrayList<ShiftStatusModel>>>
    @POST("/api/absen")
    fun absen(@Body params: JsonObject): Call<HttpResult<ArrayList<ResponseBody>>>
    @GET("/api/get-tukar-shift-cuti-lembur-absen")
    fun getHistoryTabMenu(): Call<HttpResult<HistoryMenuModel>>
    @GET("/api/get-pengumuman")
    fun getAnnouncement(): Call<HttpResult<ArrayList<AnnouncementModel>>>
    @GET("api/get-pengumuman-android")
    fun getMainMenuAnnouncement(): Call<HttpResult<ArrayList<AnnouncementModel>>>
    @GET("/api/get-karyawan")
    fun getAllKaryawan(): Call<HttpResult<ArrayList<KaryawanModel>>>
    @GET("/api/get-notification")
    fun getNotification(): Call<HttpResult<ArrayList<NotificationDataModel>>>
    @GET("api/get-approval")
    fun getApprovalRequest(): Call<HttpResult<ArrayList<ApprovalRequestModel>>>
    companion object {
        const val BASE_URL = "https://dfy.dekate.id"
    }
}