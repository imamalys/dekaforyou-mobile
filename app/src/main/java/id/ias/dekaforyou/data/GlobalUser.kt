package id.ias.dekaforyou.data

import android.app.Application
import id.ias.dekaforyou.model.UserModel

class GlobalUser : Application() {
    companion object {
        var currentUserModel: UserModel? = null
    }
}