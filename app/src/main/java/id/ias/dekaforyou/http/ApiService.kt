package id.ias.dekaforyou.http

import android.system.Os
import id.ias.dekaforyou.model.Location
import id.ias.dekaforyou.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import rx.Observable

interface ApiService {

    @POST("/api/login")
    fun loginUser(@Body params: HashMap<String, Any>): Observable<HttpResult<ArrayList<User>>>
    @GET("/api/profile")
    fun getProfile(): Observable<HttpResult<ArrayList<User>>>
    @POST("/api/cek-lokasi")
    fun getDistanceLocation(@Body params: HashMap<String, Any>): Observable<HttpResult<ArrayList<Location>>>
    companion object {
        const val BASE_URL = "https://dfy.dekate.id"
    }
}