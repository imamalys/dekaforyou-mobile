package id.ias.dekaforyou.http

import android.util.Log
import com.google.gson.Gson
import id.ias.dekaforyou.http.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitManager private constructor() {
    var apiService: ApiService? = null
        get() {
            if (field == null) field = retrofit.create(ApiService::class.java)
            return field
        }

    private val retrofit: Retrofit

    private fun genericClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(
                DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS
            )
            .retryOnConnectionFailure(false)
            .build()
    }

    fun destroy() {
        instance = null
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        return try {
            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory)
            builder.hostnameVerifier { hostname: String?, session: SSLSession? -> true }
            builder.connectTimeout(
                DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS
            )
            builder.addInterceptor(HeaderInterceptor())
            builder.authenticator(HeaderInterceptor())
            builder.addInterceptor { chain ->
                val request: Request = chain.request()

                // try the request
                var response: Response = chain.proceed(request)
                var tryCount = 0
                while (!response.isSuccessful && tryCount < 3) {
                    Log.d("intercept", "Request is not successful - $tryCount")
                    tryCount++

                    // retry the request
                    response = chain.proceed(request)
                }

                // otherwise just pass the original response on
                response
            }
            builder.retryOnConnectionFailure(false)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 30000L

        @Volatile
        private var instance: RetrofitManager? = null

        fun getInstance(): RetrofitManager? {
            if (instance == null) {
                synchronized(RetrofitManager::class.java) {
                    //未初始化，则初始instance变量
                    if (instance == null) {
                        instance = RetrofitManager()
                    }
                }
            }
            return instance
        }
    }

    fun <T> createService(clazz: Class<T>?): T {
        return retrofit.create(clazz)
    }

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(getUnsafeOkHttpClient())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }
}