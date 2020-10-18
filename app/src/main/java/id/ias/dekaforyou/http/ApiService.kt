package id.ias.dekaforyou.http

import id.ias.dekaforyou.model.LocationModel
import id.ias.dekaforyou.model.UserModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

interface ApiService {

    @POST("/api/login")
    fun loginUser(@Body params: HashMap<String, Any>): Observable<HttpResult<ArrayList<UserModel>>>
    @GET("/api/profile")
    fun getProfile(): Observable<HttpResult<ArrayList<UserModel>>>
    @POST("/api/cek-lokasi")
    fun getDistanceLocation(@Body params: HashMap<String, Any>): Observable<HttpResult<ArrayList<LocationModel>>>
    companion object {
        const val BASE_URL = "https://dfy.dekate.id"
    }
}