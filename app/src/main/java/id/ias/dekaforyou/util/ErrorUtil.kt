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
                        errorMessage = "Login session expired, please try re-login"
                    } else {
                        errorMessage = "Network issues, please try again"
                    }
                }
            } else {
                errorMessage = "Network issues, please try again"
            }
            return errorMessage
        }
    }
}