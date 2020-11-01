package id.ias.dekaforyou.http.interceptor

import id.ias.dekaforyou.constant.LoginConstant
import okhttp3.*
import java.io.IOException

class HeaderInterceptor : Interceptor, Authenticator {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request()
            .newBuilder()
        if (LoginConstant.getLoginToken() != "") {
            builder
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Connection", "close")
                .header("Authorization", LoginConstant.getLoginToken())
                .build()
        } else {
            builder
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
        }
        return chain.proceed(builder.build())
    }

    @Throws(IOException::class)
    override fun authenticate (route: Route?, response: Response): Request? {
        var request: Request? = null
        if (LoginConstant.getLoginToken() != "") {
            try {
                request = response?.request()?.newBuilder()
                    ?.addHeader("Authorization", LoginConstant.getLoginToken())
                    ?.build()
                return request
            } catch (ex: Exception) { }
        }

        return request
    }
}