package id.ias.dekaforyou

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import id.ias.dekaforyou.constant.LoginConstant

class SplashScreenActivity : AppCompatActivity() {

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler.postDelayed({
            if(!LoginConstant.getGuideShow()) {
                goToGuideActivity()
            } else {
                goToLoginActivity()
            }
        }, 3000)
    }

    fun goToLoginActivity() {
        LoginConstant.setGuideShowed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToGuideActivity() {
        val intent = Intent(this, GuideActivity::class.java)
        startActivity(intent)
        finish()
    }
}
