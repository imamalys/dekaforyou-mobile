package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.Location
import id.ias.dekaforyou.model.User
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.fragment_beranda.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BerandaFragment : Fragment() {

    private val loading = LoadingDialog()
    lateinit var user: User
    lateinit var locationUser: Location

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
        checkLocation()
        reloadData()
    }

    private fun setAction() {

    }

    private fun reloadData() {
        if (::user.isInitialized) {
            tv_name.text = user.name
        } else {
            getUserProfile()
        }

        if (::locationUser.isInitialized) {
            if (locationUser.isAllowed) {
                tv_jarak_lokasi.text = locationUser.radius
            } else {
                tv_jarak_lokasi.text = locationUser.distance
            }
        } else {
            checkLocation()
        }
    }

    private fun setUser() {
        if (GlobalUser.currentUser != null) {
            user = GlobalUser.currentUser!!
        } else {
            getUserProfile()
        }
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
                            GlobalUser.currentUser = user
                            setUser()

                            checkLocation()
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
        params["latitude"] = "-7.073790296376713"
        params["longitude"] = "110.44048759667872"
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
                            locationUser = it[0]
                            locationUser.isAllowed = true
                            reloadData()
                        }?: run {
                            ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        result.data?.let {
                            locationUser = it[0]
                            locationUser.isAllowed = false
                            reloadData()
                        }?: run {
                            ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    ToastUtils.showLong(result.msg)
                }
            }, { error ->
                if(!loading.showsDialog) {
                    loading.dismiss()
                }
                val errBody = ErrorUtil.getErrorMessage(error)
                ToastUtils.showLong(errBody)
            })
    }
}
