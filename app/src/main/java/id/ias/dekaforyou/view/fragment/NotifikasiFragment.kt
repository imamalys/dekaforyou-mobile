package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import id.ias.dekaforyou.MainActivity

import id.ias.dekaforyou.R
import id.ias.dekaforyou.adapter.NotificationAdapter
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.ApprovalRequestModel
import id.ias.dekaforyou.model.NotificationDataModel
import kotlinx.android.synthetic.main.fragment_notifikasi.*
import retrofit2.Call
import retrofit2.Response

class NotifikasiFragment : Fragment() {

    val loading = LoadingDialog()
    lateinit var mContext: Context
    var ApprovalRequestsModel: ArrayList<ApprovalRequestModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mContext = context!!
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifikasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNotification()
        setAction()
    }

    private fun setAction() {
        iv_back.setOnClickListener {
            MainActivity.mainActivity.back()
        }
    }

    private fun reloadData() {
        if (ApprovalRequestsModel != null) {
            rv_notifikasi.layoutManager = LinearLayoutManager(mContext)
            val adapter = NotificationAdapter(mContext, ApprovalRequestsModel!!)
            rv_notifikasi.adapter = adapter
        }
    }

    private fun getNotification() {
        showLoading()
        RetrofitManager.getInstance()?.apiService?.getApprovalRequest()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<ApprovalRequestModel>>> {
            override fun onFailure(
                call: Call<HttpResult<ArrayList<ApprovalRequestModel>>>,
                t: Throwable
            ) {
                hideLoading()
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<ApprovalRequestModel>>>,
                response: Response<HttpResult<ArrayList<ApprovalRequestModel>>>
            ) {
                hideLoading()

                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        response.body()!!.data.let {
                            ApprovalRequestsModel = it
                            reloadData()
                        }
                    }
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
