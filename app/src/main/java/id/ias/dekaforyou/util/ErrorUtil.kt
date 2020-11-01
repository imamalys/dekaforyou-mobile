package id.ias.dekaforyou.util

import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException
import java.lang.Exception

class ErrorUtil {
    companion object {
        var errorMessage: String = ""
        fun getErrorMessage(e: Throwable): String {
            if (e is HttpException) {
                // server 异常
                try {
                    val errorObject = JSONObject(e.response()!!.errorBody()!!.string())
                    errorMessage = errorObject.getString("message")
                } catch (exception: Exception) {
                    if (e.response()?.raw()?.code() == 400) {
                        errorMessage = "Sesi login tidak valid, silahkan login ulang"
                    } else if(e.response()?.raw()?.code() == 500) {
                        errorMessage = "Masalah jaringan, silahkan coba lagi"
                    }
                    else {
                        errorMessage = "Masalah jaringan, silahkan coba lagi"
                    }
                }
            } else {
                errorMessage = "Masalah jaringan, silahkan coba lagi"
            }
            return errorMessage
        }
    }
}