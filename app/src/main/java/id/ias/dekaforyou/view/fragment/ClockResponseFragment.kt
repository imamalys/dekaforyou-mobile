package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonObject
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.LocationModel
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_clock_response.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList

class ClockResponseFragment : Fragment() {

    var loading = LoadingDialog()
    lateinit var locationModelUser: LocationModel
    var isSuccess: Boolean = false
    var isError: Boolean = false
    var clockIn: Boolean = true
    lateinit var mLocation: Location
    var bitmapString: String = ""
    var handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_clock_response, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSuccess = MainActivity.mainActivity.isSuccess
        clockIn = MainActivity.mainActivity.isClockIn
        isError = MainActivity.mainActivity.isError
        mLocation = MainActivity.mainActivity.mLocation
        bitmapString = MainActivity.mainActivity.bitmapString

        reloadData()

        handler.postDelayed({
            MainActivity.mainActivity.back()
        }, 5000)

        btn_action.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            absen()
        }
    }

    fun reloadData() {
        if (isSuccess) {
            if (clockIn) {
                tv_status.text = "Clock-In Sukses"
            } else {
                tv_status.text = "Clock-Out Sukses"
            }
            tv_desc_add.visibility = View.GONE
        } else if(!isSuccess && !isError) {
            tv_status.text = "Your Area out of coverage area"
            tv_desc_status.text = "Posisi anda diluar batas area absensi yang ditentukan. Jarak anda saat ini adalah:"
            iv_phone.setImageDrawable(resources.getDrawable(R.drawable.phone_not_connection))
            locationModelUser = MainActivity.mainActivity.absenData
            tv_desc_add.text = String.format("%s KM", locationModelUser.distance)
            btn_action.text = "Try Again"
        } else if(isError) {
            tv_status.text = "No internet connection available"
            tv_desc_status.text = "Your device is not connect to internet, please make sure your connection is working"
            iv_phone.setImageDrawable(resources.getDrawable(R.drawable.phone_not_connection))
            tv_desc_add.visibility = View.GONE
            btn_action.text = "Try Again"
        }
    }

    fun absen() {
        showLoading()

        val jsonObject = JsonObject()
        jsonObject.addProperty("latitude", mLocation.latitude)
        jsonObject.addProperty("longitude", mLocation.longitude)
        jsonObject.addProperty("image", String.format("data:image/jpeg;base64,%s", bitmapString))
        RetrofitManager.getInstance()?.apiService?.absen(jsonObject)?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<ResponseBody>>> {
            override fun onFailure(call: Call<HttpResult<ArrayList<ResponseBody>>>, t: Throwable) {
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                if (errBody == "Masalah jaringan, silahkan coba lagi") {
                    isSuccess = false
                    isError = true
                }

                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<ResponseBody>>>,
                response: Response<HttpResult<ArrayList<ResponseBody>>>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        isError = false
                        isSuccess = true
                    }
                } else if (response.body()?.msg == "Lokasi Tidak Valid") {
                    isSuccess = false
                    isError = false

                    ToastUtils.showShort(response.body()?.msg)
                }
            }

        })
    }

    fun showLoading() {
        if (!loading.isAdded) {
            loading.show(MainActivity.mainActivity.supportFragmentManager, null)
        }
    }

    fun hideLoading() {
        if (loading.showsDialog) {
            loading.dismiss()
        }
    }
}