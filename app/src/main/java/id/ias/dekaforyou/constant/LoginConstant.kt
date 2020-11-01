package id.ias.dekaforyou.constant

import com.blankj.utilcode.util.SPUtils

class LoginConstant {

    companion object {
        private const val LOGIN_ID = "id"
        private const val LOGIN_DISPLAY_NAME = "display_name"
        private const val LOGIN_TOKEN = "token"
        private const val LOGIN_PROFILE_PIC = "profile_pic"
        private const val GUIDE_SHOWED = "show"

        fun setLoginUser(token: String, id: Int?, displayName: String?, profilePic: String?) {
            SPUtils.getInstance().put(LOGIN_TOKEN, token)
            SPUtils.getInstance().put(LOGIN_ID, id?: 0)
            SPUtils.getInstance().put(LOGIN_DISPLAY_NAME, displayName?: "")
            SPUtils.getInstance().put(LOGIN_PROFILE_PIC, profilePic?: "")
        }

        fun setGuideShowed() {
            SPUtils.getInstance().put(GUIDE_SHOWED, true)
        }

        fun getLoginToken(): String {
            return SPUtils.getInstance().getString(LOGIN_TOKEN, "")
        }

        fun getLoginId(): Int {
            return SPUtils.getInstance().getInt(LOGIN_ID, 0)
        }

        fun getProfilePic(): String {
            return SPUtils.getInstance().getString(LOGIN_PROFILE_PIC, "")
        }

        fun getGuideShow(): Boolean {
            return SPUtils.getInstance().getBoolean(GUIDE_SHOWED, false)
        }

        fun logout() {
            SPUtils.getInstance().clear()
        }
    }
}