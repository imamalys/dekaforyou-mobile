package id.ias.dekaforyou

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.model.KaryawanModel
import id.ias.dekaforyou.model.LocationModel
import id.ias.dekaforyou.view.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var absenData: LocationModel
    lateinit var mLocation: Location
    var isSuccess: Boolean = false
    var isError: Boolean = false
    var isClockIn: Boolean = false
    var bitmapString: String = ""
    var isBottom = false
    lateinit var transaction: FragmentTransaction
    lateinit var karyawansModel: ArrayList<KaryawanModel>
    lateinit var karyawanModel: KaryawanModel
    var isAjukanCutiFragment: Boolean = false
    var isHome = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over

        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(drawerToggle)

        bottom_navigation_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation_drawer.setNavigationItemSelectedListener(mOnNavigationDrawerItemSelectedListener)

        loadBottomFragment(BerandaFragment())
    }

    private val mOnNavigationDrawerItemSelectedListener: NavigationView.OnNavigationItemSelectedListener =
        object: NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val fragment: Fragment
                isBottom = false
                when (item.itemId) {
                    R.id.tukar_shift -> {
                        fragment = TukarShiftFragment()
                        loadOtherFragment(fragment)
                        drawer.closeDrawer(navigation_drawer, true)
                        return false
                    }
                    R.id.ajukan_cuti -> {
                        fragment = AjukanCutiFragment()
                        loadOtherFragment(fragment)
                        drawer.closeDrawer(navigation_drawer, true)
                        return false
                    }
                    R.id.ajukan_lembur -> {
                        fragment = AjukanLemburFragment()
                        loadOtherFragment(fragment)
                        drawer.closeDrawer(navigation_drawer, true)
                        return false
                    }
                    R.id.notifikasi -> {
                        fragment = NotifikasiFragment()
                        loadOtherFragment(fragment)
                        drawer.closeDrawer(navigation_drawer, true)
                    }
                    R.id.logout -> {
                        invalidLogin(this@MainActivity)
                    }
                }

                return false
            }
        }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val fragment: Fragment
                isBottom = false
                when (item.itemId) {
                    R.id.beranda -> {
                        isBottom = true
                        fragment = BerandaFragment()
                        loadBottomFragment(fragment)
                        return true
                    }
                    R.id.profile -> {
                        fragment = MyProfileFragment()
                        loadBottomFragment(fragment)
                        return true
                    }
                    R.id.more -> {
                        drawer.openDrawer(GravityCompat.START)
                        return true
                    }
                }
                return false
            }
        }

    private fun loadBottomFragment(fragment: Fragment) {
        // load fragment
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.menu_frame_layout, fragment)
        transaction.commit()
    }

    private fun loadOtherFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.menu_frame_layout, fragment)
            .addToBackStack(null).commit()
    }

    fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun goToClockResponse(isSuccess: Boolean = true, isClockIn: Boolean = true, isError: Boolean = false, mLocation: Location, bitmapString: String = "", absenData: LocationModel? = null) {
        this.isSuccess = isSuccess
        this.isClockIn = isClockIn
        this.isError = isError
        this.mLocation = mLocation
        this.bitmapString = bitmapString
        if (absenData != null) {
            this.absenData = absenData
        }

        isBottom = false
        loadOtherFragment(ClockResponseFragment())
    }

    fun goToPilihKaryawan(karyawansModel: ArrayList<KaryawanModel>) {
        this.karyawansModel = karyawansModel
        val fragment = PilihKaryawanFragment().newInstance(karyawansModel)
        loadOtherFragment(fragment)
    }

    fun goToAjukanCutiCallback(karyawanModel: KaryawanModel) {
        this.karyawanModel = karyawanModel
        val fragment = AjukanCutiFragment().newInstance(karyawanModel)
        loadBottomFragment(fragment)
    }

    fun goToTukarShiftCallback(karyawanModel: KaryawanModel) {
        this.karyawanModel = karyawanModel
        val fragment = TukarShiftFragment().newInstance(karyawanModel)
        loadBottomFragment(fragment)
    }

    fun back() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (!isBottom) {
            if (backStackEntryCount == 0) {
                if(isHome) {
                    finish()
                } else {
                    isHome = true
                    isBottom = true
                    val fragment = BerandaFragment()
                    loadBottomFragment(fragment)
                }
            } else {
                back()
            }
        } else {
            finish()
        }
    }

    companion object {
        lateinit var mainActivity: MainActivity

        fun invalidLogin(mainActivity: MainActivity) {
            LoginConstant.logout()
            mainActivity.goToLoginActivity()
        }
    }
}