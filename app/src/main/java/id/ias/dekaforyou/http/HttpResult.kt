package id.ias.dekaforyou.http

import java.io.Serializable

class HttpResult<T> : Serializable {
    var errorMsg: String? = null
    var code = 0
    var msg = ""
    var success = false
    var data: T? = null
        private set
}