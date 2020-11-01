package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.http.ApiService
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.KaryawanModel
import id.ias.dekaforyou.util.DialogUtil
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_tukar_shift.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class TukarShiftFragment : Fragment() {

    val loading = LoadingDialog()
    val myCalendar = Calendar.getInstance()
    var isAwal: Boolean = true
    var timeShiftAwal: IntArray = IntArray(2)
    var timeShiftAkhir: IntArray = IntArray(2)
    lateinit var karyawansModel: ArrayList<KaryawanModel>
    lateinit var karyawanModel: KaryawanModel
    lateinit var mContext: Context
    val KARYAWAN_KEY = "karyawan_key"

    fun newInstance(
        karyawanModel: KaryawanModel
    ): TukarShiftFragment {
        val fragment = TukarShiftFragment()
        val bundle = Bundle()
        bundle.putParcelable(KARYAWAN_KEY, karyawanModel)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_tukar_shift, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = context!!

        if(arguments != null) {
            karyawanModel = arguments!!.getParcelable(KARYAWAN_KEY)!!
            Glide.with(mContext)
                .load(String.format("%s/%s", ApiService.BASE_URL, karyawanModel.profile))
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(iv_profile)
            tv_name.text = karyawanModel.name
            scroll_view.post {
                scroll_view.fullScroll(View.FOCUS_DOWN)
            }
        }

        getAllKaryawan()

        timeShiftAwal[0] = myCalendar.get(Calendar.HOUR)
        timeShiftAwal[1] = myCalendar.get(Calendar.MINUTE)
        timeShiftAkhir[0] = myCalendar.get(Calendar.HOUR)
        timeShiftAkhir[1] = myCalendar.get(Calendar.MINUTE)
        tv_jam_shift_awal.text = String.format("%02d:%02d", myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE))
        tv_jam_shift_akhir.text = String.format("%02d:%02d", myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE))
        setAction()
    }

    private fun setAction() {
        tv_jam_shift_awal.setOnClickListener {
            isAwal = true
            timeDialogShow()
        }

        tv_jam_shift_akhir.setOnClickListener {
            isAwal = false
            timeDialogShow()
        }

        iv_back.setOnClickListener {
            MainActivity.mainActivity.back()
        }

        rl_karyawan.setOnClickListener {
            MainActivity.mainActivity.isAjukanCutiFragment = false
            MainActivity.mainActivity.goToPilihKaryawan(karyawansModel)
        }
    }

    private fun timeDialogShow() {
        var hour = 0
        var minute = 0
        if (isAwal) {
            hour = timeShiftAwal[0]
            minute = timeShiftAwal[1]
        } else {
            hour = timeShiftAkhir[0]
            minute = timeShiftAkhir[1]
        }
        val timePickerDialog = TimePickerDialog(context, R.style.CustomDatePickerDialog, date, hour, minute, true)
        timePickerDialog.setTitle("Atur Jam")
        timePickerDialog.setContentView(R.layout.time_picker_layout)
        timePickerDialog.show()
    }

    private val date = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        myCalendar.set(Calendar.HOUR_OF_DAY, hour)
        myCalendar.set(Calendar.MINUTE, minute)
        updateTime(hour, minute)
    }

    private fun updateTime(hour: Int, minute: Int) {
        if (isAwal) {
            timeShiftAwal[0] = hour
            timeShiftAwal[1] = minute
            tv_jam_shift_awal.text = String.format("%02d:%02d", hour, minute)
        } else {
            timeShiftAkhir[0] = hour
            timeShiftAkhir[1] = minute
            tv_jam_shift_akhir.text = String.format("%02d:%02d", hour, minute)
        }
    }

    fun getAllKaryawan() {
        showLoading()

        RetrofitManager.getInstance()?.apiService?.getAllKaryawan()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<KaryawanModel>>> {
            override fun onFailure(call: Call<HttpResult<ArrayList<KaryawanModel>>>, t: Throwable) {
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<KaryawanModel>>>,
                response: Response<HttpResult<ArrayList<KaryawanModel>>>
            ) {
                hideLoading()
                if (response.isSuccessful) {
                    if(response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                karyawansModel = it!!
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        ToastUtils.showShort(response.body()?.msg)
                    }
                }
            }

        })
    }

    fun showLoading() {
        loading.isCancelable = false
        loading.show(MainActivity.mainActivity.supportFragmentManager, null)
    }

    fun hideLoading() {
        loading.dismiss()
    }
}