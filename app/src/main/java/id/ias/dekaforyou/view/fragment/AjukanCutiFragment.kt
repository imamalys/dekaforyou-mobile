package id.ias.dekaforyou.view.fragment

import LoadingDialog
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
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_ajukan_cuti.*
import retrofit2.Call
import retrofit2.Response

class AjukanCutiFragment: Fragment() {

    val loading = LoadingDialog()
    lateinit var karyawansModel: ArrayList<KaryawanModel>
    lateinit var karyawanModel: KaryawanModel
    lateinit var mContext: Context
    val KARYAWAN_KEY = "karyawan_key"

    fun newInstance(
        karyawanModel: KaryawanModel
    ): AjukanCutiFragment {
        val fragment = AjukanCutiFragment()
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

        return inflater.inflate(R.layout.fragment_ajukan_cuti, container, false)
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
            scrool_view.post {
                scrool_view.fullScroll(View.FOCUS_DOWN)
            }
        }

        getAllKaryawan()

        setAction()
    }

    fun setAction() {
        iv_back.setOnClickListener {
            MainActivity.mainActivity.back()
        }

        rl_karyawan.setOnClickListener {
            MainActivity.mainActivity.isAjukanCutiFragment = true
            MainActivity.mainActivity.goToPilihKaryawan(karyawansModel)
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