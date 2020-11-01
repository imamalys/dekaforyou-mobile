package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ncorti.slidetoact.SlideToActView
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.adapter.HistoryTabAbsenAdapter
import id.ias.dekaforyou.adapter.HistoryTabMenuAdapter
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.helper.ImagePickerActivity
import id.ias.dekaforyou.http.ApiService
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.*
import id.ias.dekaforyou.util.BitmapUtil
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_beranda.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BerandaFragment : Fragment() {

    val loading = LoadingDialog()
    var isLoading = false
    private var userModel: UserModel? = null
    private var locationModelUser: LocationModel? = null
    private var shiftModel: ShiftModel? = null
    private var shiftStatusModel: ShiftStatusModel? = null
    private val REQUEST_IMAGE = 100
    private lateinit var mLocation: Location
    private lateinit var mFusedLocation: FusedLocationProviderClient
    lateinit var mContext: Context
    var bitmap: Bitmap? = null
    var bitmapString: String? = ""
    var isAbsenResult: Boolean = false
    var isFinish = BooleanArray(5)
    var isFirstLoading = true
    var historyMenuModel: HistoryMenuModel? = null
    private lateinit var historyTabMenuAdapter: HistoryTabMenuAdapter
    lateinit var historyTabAbsen: HistoryTabAbsenAdapter
    private lateinit var locationCallback: LocationCallback
    private var isFirst: Boolean = true
    private var isClockIn: Boolean = true
    var announcementMainMenuModel: AnnouncementModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        firstLoading()
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GET MY CURRENT LOCATION
        mContext = this.context!!
        mFusedLocation = LocationServices.getFusedLocationProviderClient(mContext)
        ImagePickerActivity.clearCache(mContext)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    mLocation = location
                    if(isFirst) {
                        isFirst = false
                        checkLocation()
                    }
                    return
                }
            }
        }

        checkPermissionsLocation()
        setAction()
        getUserProfile()
        getShift()
        getHistoryTabMenu()
        getMainMenuAnnouncement()

        tv_tgl.text = getDate()
        Glide.with(mContext)
            .load(String.format("%s/%s", ApiService.BASE_URL, GlobalUser.currentUserModel?.profile))
            .placeholder(R.drawable.ic_person)
            .circleCrop()
            .into(iv_profile)
    }

    override fun onStart() {
        super.onStart()

        mFusedLocation.requestLocationUpdates(
            LocationRequest().setInterval(15000).setPriority(PRIORITY_HIGH_ACCURACY),
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onStop() {
        super.onStop()

        mFusedLocation.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        getAbsenStatus()
    }

    private fun setAction() {
        iv_refresh_location.setOnClickListener {
            checkLocation()
        }
        sliderAct.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                launchCameraIntent()
            }
        }

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (historyMenuModel != null) {
                    when(tab?.position) {
                        0 -> {
                            rv_select_tab.layoutManager = LinearLayoutManager(mContext)
                            historyTabMenuAdapter = HistoryTabMenuAdapter(mContext, historyMenuModel?.tukarShift!!, GlobalUser.currentUserModel?.profile?: LoginConstant.getProfilePic())
                            rv_select_tab.adapter = historyTabMenuAdapter
                        }
                        1 -> {
                            rv_select_tab.layoutManager = LinearLayoutManager(mContext)
                            historyTabMenuAdapter = HistoryTabMenuAdapter(mContext, historyMenuModel?.cuti!!, GlobalUser.currentUserModel?.profile?: LoginConstant.getProfilePic())
                            rv_select_tab.adapter = historyTabMenuAdapter
                        }
                        2-> {
                            rv_select_tab.layoutManager = LinearLayoutManager(mContext)
                            historyTabMenuAdapter = HistoryTabMenuAdapter(mContext, historyMenuModel?.lembur!!, GlobalUser.currentUserModel?.profile?: LoginConstant.getProfilePic())
                            rv_select_tab.adapter = historyTabMenuAdapter
                        }
                        3-> {
                            rv_select_tab.layoutManager = LinearLayoutManager(mContext)
                            historyTabAbsen = HistoryTabAbsenAdapter(mContext, historyMenuModel?.absen!!, GlobalUser.currentUserModel?.profile?: LoginConstant.getProfilePic())
                            rv_select_tab.adapter = historyTabAbsen
                        }
                    }
                }
            }
        })
    }

    private fun reloadUser() {
        if (userModel != null) {
            tv_name.text = userModel!!.name
        }
    }

    private fun reloadShiftStatus() {
        if (shiftModel != null) {
            tv_shift_isi.text = shiftModel!!.name
            tv_jadwal_masuk_isi.text = shiftModel!!.clockIn
            tv_jadwal_pulang_isi.text = shiftModel!!.clockOut
            tv_terlambat_isi.text = shiftModel!!.terlambat
        }
    }

    private fun reloadLocation() {
        if (locationModelUser != null) {
            if (locationModelUser!!.isAllowed) {
                tv_jarak_lokasi.text = "Lokasi Valid"
                tv_jarak_label.visibility = View.GONE
            } else {
                tv_jarak_lokasi.text = String.format("%s M", locationModelUser!!.distance)
                tv_jarak_label.visibility = View.VISIBLE
            }
        }
    }

    private fun reloadHistory() {
        if (historyMenuModel != null) {
            rv_select_tab.layoutManager = LinearLayoutManager(mContext)
            historyTabMenuAdapter = HistoryTabMenuAdapter(mContext, historyMenuModel?.tukarShift!!, GlobalUser.currentUserModel?.profile?: LoginConstant.getProfilePic())
            rv_select_tab.adapter = historyTabMenuAdapter
        }
    }

    private fun reloadAnnouncement() {
        if (announcementMainMenuModel != null) {
            tv_announcement.text = announcementMainMenuModel!!.pesan
        }
    }

    private fun setUser() {
        if (GlobalUser.currentUserModel != null) {
            userModel = GlobalUser.currentUserModel!!
        } else {
            getUserProfile()
        }
    }

    private fun getLocation() {
        mFusedLocation.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    mLocation = location
                    checkLocation()
                }
            }
    }

    private fun checkPermissionsLocation() {
        Dexter.withContext(context)
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
        showLoading()

        RetrofitManager.getInstance()?.apiService?.getProfile()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<UserModel>>> {
            override fun onFailure(call: Call<HttpResult<ArrayList<UserModel>>>, t: Throwable) {
                isFinish[0] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<UserModel>>>,
                response: Response<HttpResult<ArrayList<UserModel>>>
            ) {
                isFinish[0] = true
                hideLoading()

                if (response.isSuccessful) {
                    if(response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                val user = it?.get(0)
                                GlobalUser.currentUserModel = user
                                setUser()
                                reloadUser()
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        ToastUtils.showShort(response.body()?.msg)
                        if (response.message() == "Token salah") {
                            MainActivity.invalidLogin(MainActivity.mainActivity)
                        }
                    }
                }
            }
        })
    }

    private fun checkLocation() {
        showLoading()

        val params = HashMap<String, Any>()
        params["latitude"] = mLocation.latitude.toString()
        params["longitude"] = mLocation.longitude.toString()
        RetrofitManager.getInstance()?.apiService?.getDistanceLocation(params)?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<LocationModel>>> {
            override fun onFailure(call: Call<HttpResult<ArrayList<LocationModel>>>, t: Throwable) {
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<LocationModel>>>,
                response: Response<HttpResult<ArrayList<LocationModel>>>
            ) {
                hideLoading()

                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                locationModelUser = it?.get(0)
                                locationModelUser!!.isAllowed = true
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }

                        reloadLocation()

                        ToastUtils.showShort(response.body()?.msg)
                    } else {
                        try {
                            response.body()?.data.let {
                                locationModelUser = it?.get(0)
                                locationModelUser!!.isAllowed = false

                                reloadLocation()
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                        if (isAbsenResult) {
                            isAbsenResult = false
                            var clockIn = true
                            if (shiftStatusModel?.clockIn == true) {
                                clockIn = false
                            }
                            MainActivity.mainActivity.goToClockResponse(
                                isSuccess = false,
                                isClockIn = clockIn,
                                isError = false,
                                mLocation =  mLocation,
                                bitmapString = bitmapString!!,
                                absenData = locationModelUser
                            )
                        } else {
                            ToastUtils.showShort(response.body()?.msg)
                        }
                    }
                }
            }
        })
    }

    private fun getShift() {

        RetrofitManager.getInstance()?.apiService?.getShift()?.enqueue(object : retrofit2.Callback<HttpResult<ShiftModel>> {
            override fun onFailure(call: Call<HttpResult<ShiftModel>>, t: Throwable) {
                isFinish[1] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ShiftModel>>,
                response: Response<HttpResult<ShiftModel>>
            ) {
                isFinish[1] = true
                hideLoading()

                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                shiftModel = it
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            response.body()?.data.let {
                                shiftModel = it
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                        ToastUtils.showShort(response.body()?.msg)
                    }
                    reloadShiftStatus()
                }
            }
        })
    }

    fun absen() {
            showLoading()
            bitmapString = BitmapUtil.getBase64String(bitmap!!, 100)?.replace("\n", "")
                ?.replace(" ", "+")?: ""
            val jsonObject = JsonObject()
            jsonObject.addProperty("latitude", mLocation.latitude)
            jsonObject.addProperty("longitude", mLocation.longitude)
            jsonObject.addProperty("image", String.format("data:image/jpeg;base64,%s", bitmapString))
            RetrofitManager.getInstance()?.apiService?.absen(jsonObject)?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<ResponseBody>>> {
                override fun onFailure(call: Call<HttpResult<ArrayList<ResponseBody>>>, t: Throwable) {
                    hideLoading()

                    val errBody = ErrorUtil.getErrorMessage(t)
                    if (errBody == "Masalah jaringan, silahkan coba lagi") {
                        MainActivity.mainActivity.goToClockResponse(
                            isSuccess = false,
                            isError = true,
                            mLocation =  mLocation,
                            bitmapString = bitmapString!!
                        )
                    } else {
                        ToastUtils.showShort(errBody)
                    }
                }

                override fun onResponse(
                    call: Call<HttpResult<ArrayList<ResponseBody>>>,
                    response: Response<HttpResult<ArrayList<ResponseBody>>>
                ) {
                    hideLoading()
                    if (response.isSuccessful) {
                        if (response.body()!!.success) {
                            var clockIn = true
                            if (shiftStatusModel!!.clockIn!!) {
                                clockIn = false
                            }
                            MainActivity.mainActivity.goToClockResponse(
                                isSuccess = true,
                                isError = false,
                                isClockIn = clockIn,
                                mLocation =  mLocation,
                                bitmapString = bitmapString!!
                            )
                        } else if (response.body()?.msg == "Lokasi Tidak Valid") {
                            isAbsenResult = true
                            checkLocation()
                        } else {
                            ToastUtils.showShort(response.body()!!.msg)
                        }
                    }
                }

            })
    }

    fun getAbsenStatus() {
        showLoading()
        RetrofitManager.getInstance()?.apiService?.getAbsenStatus()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<ShiftStatusModel>>> {
            override fun onFailure(call: Call<HttpResult<ArrayList<ShiftStatusModel>>>, t: Throwable) {
                isFinish[2] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<ShiftStatusModel>>>,
                response: Response<HttpResult<ArrayList<ShiftStatusModel>>>
            ) {
                isFinish[2] = true
                hideLoading()
                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                if (it != null) {
                                    shiftStatusModel = it[0]
                                    if (it[0].clockIn!! && it[0].clockOut!!) {
                                        sliderAct.visibility = View.GONE
                                    }
                                    else if (it[0].clockIn!!) {
                                        sliderAct.resetSlider()
                                        sliderAct.isReversed = true
                                        sliderAct.text = "Slide to Clock Out"
                                        sliderAct.background = resources.getDrawable(R.drawable.green_background_slide)
                                        sliderAct.innerColor = resources.getColor(R.color.green_button)
                                        isClockIn = false
                                    }
                                    else {
                                        sliderAct.resetSlider()
                                        sliderAct.isReversed = false
                                        sliderAct.text = "Slide to Clock In"
                                        sliderAct.background = resources.getDrawable(R.drawable.red_background_slide)
                                        sliderAct.innerColor = resources.getColor(R.color.red_button)
                                        isClockIn = true
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            response.body()?.data.let {
                                sliderAct.visibility = View.GONE
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                        ToastUtils.showShort(response.body()?.msg)
                    }
                }
            }
        })
    }

    fun getHistoryTabMenu() {
        RetrofitManager.getInstance()?.apiService?.getHistoryTabMenu()?.enqueue(object : retrofit2.Callback<HttpResult<HistoryMenuModel>> {
            override fun onFailure(call: Call<HttpResult<HistoryMenuModel>>, t: Throwable) {
                isFinish[3] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<HistoryMenuModel>>,
                response: Response<HttpResult<HistoryMenuModel>>
            ) {
                isFinish[3] = true
                hideLoading()

                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                historyMenuModel = response.body()!!.data
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }

                    reloadHistory()
                }
            }

        })
    }

    private fun getMainMenuAnnouncement() {
        showLoading()
        RetrofitManager.getInstance()?.apiService?.getMainMenuAnnouncement()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<AnnouncementModel>>> {
            override fun onFailure(
                call: Call<HttpResult<ArrayList<AnnouncementModel>>>,
                t: Throwable
            ) {
                isFinish[4] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<AnnouncementModel>>>,
                response: Response<HttpResult<ArrayList<AnnouncementModel>>>
            ) {
                isFinish[4] = true
                hideLoading()
                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                announcementMainMenuModel = response.body()!!.data!![0]
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }

                    reloadAnnouncement()
                }
            }

        })
    }

    fun firstLoading() {
        isLoading = true
        loading.isCancelable = false
        loading.show(MainActivity.mainActivity.supportFragmentManager, null)
    }

    fun showLoading() {
        if (!isLoading) {
            isLoading = true
            loading.isCancelable = false
            loading.show(MainActivity.mainActivity.supportFragmentManager, null)
        }
    }

    fun hideLoading() {
        if (isFirstLoading) {
            var countFinish = 0
            for (finish in isFinish) {
                if (finish) {
                    countFinish += 1
                }
                if (countFinish == 5) {
                    isFirstLoading = false
                    loading.dismiss()
                }
            }
        } else {
            if (isLoading) {
                loading.dismiss()
            }
        }
    }

    fun getDate(): String {
        val currentDate: Date = Calendar.getInstance().time
        val format = SimpleDateFormat("EEEE dd MMMM YYYY", Locale.getDefault()).format(Date())
        return format.format(currentDate)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (!(requestCode != REQUEST_IMAGE || resultCode != Activity.RESULT_OK)) {
            val uri: Uri = data!!.getParcelableExtra("path")
            try {
                // You can update this bitmap to your server
                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.mainActivity.contentResolver, uri)
                absen()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            sliderAct.resetSlider()
        }
    }
}
