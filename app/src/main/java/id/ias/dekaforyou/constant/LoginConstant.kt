package id.ias.dekaforyou.constant

import com.blankj.utilcode.util.SPUtils

class LoginConstant {

    companion object {
        private const val LOGIN_ID = "id"
        private const val LOGIN_DISPLAY_NAME = "display_name"
        private const val LOGIN_TOKEN = "token"

        fun setLoginUser(token: String, id: Int?, displayName: String?) {
            SPUtils.getInstance().put(LOGIN_TOKEN, token)
            SPUtils.getInstance().put(LOGIN_ID, id?: 0)
            SPUtils.getInstance().put(LOGIN_DISPLAY_NAME, displayName?: "")
        }

        fun getLoginToken(): String {
            return SPUtils.getInstance().getString(LOGIN_TOKEN, "")
        }

        fun getLoginId(): Int {
            return SPUtils.getInstance().getInt(LOGIN_ID, 0)
        }

        fun logout() {
            SPUtils.getInstance().clear()
        }
    }
}