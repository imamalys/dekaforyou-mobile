package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.helper.ImagePickerActivity
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.LocationModel
import id.ias.dekaforyou.model.UserModel
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_beranda.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException


class BerandaFragment : Fragment() {

    private val loading = LoadingDialog()
    lateinit var userModel: UserModel
    lateinit var locationModelUser: LocationModel
    private val REQUEST_IMAGE = 100
    private lateinit var mLocation: Location

    fun putLocation(bundle: Bundle) {
        var checkLocationModel: Location? = bundle.getParcelable("location")
        if (checkLocationModel?.latitude != 0.0) {
            mLocation = checkLocationModel!!
            checkLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAction()
        reloadData()
//        launchCameraIntent()
    }

    private fun setAction() {
        iv_refresh_location.setOnClickListener {
            MainActivity.mainActivity.checkPermissionsLocation()
        }
    }

    private fun reloadData() {
        if (::userModel.isInitialized) {
            tv_name.text = userModel.name
        } else {
            getUserProfile()
        }

        if (::locationModelUser.isInitialized) {
            if (locationModelUser.isAllowed) {
                tv_jarak_lokasi.text = locationModelUser.radius
            } else {
                tv_jarak_lokasi.text = locationModelUser.distance
            }
        }
    }

    private fun setUser() {
        if (GlobalUser.currentUserModel != null) {
            userModel = GlobalUser.currentUserModel!!
        } else {
            getUserProfile()
        }
    }

    private fun launchCameraIntent() {
        val intent = Intent(MainActivity.mainActivity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private  fun getUserProfile() {
        if(!loading.showsDialog) {
            loading.show(childFragmentManager, null)
        }
        RetrofitManager.getInstance()?.apiService?.getProfile()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ result ->
                if(!loading.showsDialog) {
                    loading.dismiss()
                }
                if(result.success) {
                    try {
                        result.data?.let {
                            val user = it[0]
                            GlobalUser.currentUserModel = user
                            setUser()

                        }?: run {
                            ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                } else {
                    ToastUtils.showLong(result.msg)

                    if (result.msg == "Token salah") {
                        MainActivity.invalidLogin(MainActivity.mainActivity)
                    }
                }
            }, { error ->
                if(!loading.showsDialog) {
                    loading.dismiss()
                }
                val errBody = ErrorUtil.getErrorMessage(error)
                ToastUtils.showLong(errBody)
            })
    }

    private fun checkLocation() {
        if(!loading.showsDialog) {
            loading.show(childFragmentManager, null)
        }
        val params = HashMap<String, Any>()
        params["latitude"] = mLocation.latitude.toString()
        params["longitude"] = mLocation.longitude.toString()
        RetrofitManager.getInstance()?.apiService?.getDistanceLocation(params)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ result ->
                if(!loading.showsDialog) {
                    loading.dismiss()
                }
                if (result.success) {
                    try {
                        result.data?.let {
                            locationModelUser = it[0]
                            locationModelUser.isAllowed = true
                        }?: run {
                            ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        result.data?.let {
                            locationModelUser = it[0]
                            locationModelUser.isAllowed = false
                        }?: run {
                            ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    ToastUtils.showLong(result.msg)
                    reloadData()
                }
            }, { error ->
                if(!loading.showsDialog) {
                    loading.dismiss()
                }
                val errBody = ErrorUtil.getErrorMessage(error)
                ToastUtils.showLong(errBody)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (!(requestCode != REQUEST_IMAGE || resultCode != Activity.RESULT_OK)) {
            val uri: Uri = data!!.getParcelableExtra("path")
            try {
                // You can update this bitmap to your server
                val bitmap = MediaStore.Images.Media.getBitmap(MainActivity.mainActivity.contentResolver, uri)

                // loading profile image from local cache
//                    loadProfile(uri.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
