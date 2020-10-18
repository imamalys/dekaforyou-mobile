package id.ias.dekaforyou.data

import android.app.Application
import id.ias.dekaforyou.model.User

class GlobalUser : Application() {
    companion object {
        var currentUser: User? = null
    }
}