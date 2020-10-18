package id.ias.dekaforyou

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.helper.ImagePickerActivity
import id.ias.dekaforyou.view.fragment.BerandaFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocation: FusedLocationProviderClient
    val berandaFragment = BerandaFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivity = this

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this)

        // GET MY CURRENT LOCATION
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)

        bottom_navigation_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragment(BerandaFragment())
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mFusedLocation.lastLocation.addOnSuccessListener(this
        ) { location -> // Do it all with location
            var bundle = Bundle()
            bundle.putParcelable("location", location)
            berandaFragment.putLocation(bundle)
            mLocation = location
        }
    }

    public fun checkPermissionsLocation() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    getLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest();
                }

            }).check()
    }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val fragment: Fragment
                when (item.itemId) {
                    R.id.beranda -> {
                        fragment = BerandaFragment()
                        loadFragment(fragment)
                        return true
                    }
                }
                return false
            }
        }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.menu_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        lateinit var mainActivity: MainActivity
        lateinit var mLocation: Location

        fun invalidLogin(mainActivity: MainActivity) {
            LoginConstant.logout()
            mainActivity.goToLoginActivity()
        }
    }
}
