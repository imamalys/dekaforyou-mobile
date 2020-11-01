package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import id.ias.dekaforyou.MainActivity

import id.ias.dekaforyou.R
import id.ias.dekaforyou.adapter.AnnouncementAdapter
import id.ias.dekaforyou.adapter.GuideAdapter
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.AnnouncementModel
import id.ias.dekaforyou.model.UserModel
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import retrofit2.Call
import retrofit2.Response

class MyProfileFragment : Fragment() {

    val loading = LoadingDialog()
    var isLoading = false
    lateinit var announcementModel: ArrayList<AnnouncementModel>
    lateinit var mContext: Context
    var isFinish = BooleanArray(2)
    var isFirstLoading = true
    var handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mContext = context!!

        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstLoading()
        reloadProfile()
        getAnnouncement()
    }

    private fun reloadProfile() {
        if (GlobalUser.currentUserModel!!.atasanNama == null) {
            getUserProfile()
        } else {
            isFinish[0] = true
            hideLoading()
            tv_name.text = GlobalUser.currentUserModel!!.name
            tv_nik_isi.text = GlobalUser.currentUserModel!!.noKtp
            tv_jabatan_isi.text = GlobalUser.currentUserModel!!.jabatan
            tv_imei_isi.text = GlobalUser.currentUserModel!!.imei
            tv_atasan_langsung_isi.text = GlobalUser.currentUserModel!!.atasanNama
            tv_divisi_isi.text = GlobalUser.currentUserModel!!.divisi
            tv_bidang_isi.text = GlobalUser.currentUserModel!!.bidang
            tv_unit_isi.text = GlobalUser.currentUserModel!!.unit
            tv_shift_isi.text = GlobalUser.currentUserModel!!.shift
        }
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacksAndMessages(null)
    }

    private fun reloadAnnouncement() {
        val guideAdapter = AnnouncementAdapter(mContext, announcementModel)
        vp_announcement.adapter = guideAdapter

        handler.postDelayed({
            vp_announcement.setCurrentItem(vp_announcement.currentItem + 1, true)
        }, 5000)

        vp_announcement.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                handler.removeCallbacksAndMessages(null)
                if (announcementModel.size - 1 == position) {
                    handler.postDelayed({
                        vp_announcement.setCurrentItem(vp_announcement.currentItem - 1, true)
                    }, 5000)
                } else {
                    handler.postDelayed({
                        vp_announcement.setCurrentItem(vp_announcement.currentItem + 1, true)
                    }, 5000)
                }
            }
        })
    }

    private fun getAnnouncement() {
        showLoading()
        RetrofitManager.getInstance()?.apiService?.getAnnouncement()?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<AnnouncementModel>>> {
            override fun onFailure(
                call: Call<HttpResult<ArrayList<AnnouncementModel>>>,
                t: Throwable
            ) {
                isFinish[1] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<ArrayList<AnnouncementModel>>>,
                response: Response<HttpResult<ArrayList<AnnouncementModel>>>
            ) {
                isFinish[1] = true
                hideLoading()
                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                announcementModel = response.body()!!.data!!
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

    private  fun getUserProfile() {
        showLoading()
        RetrofitManager.getInstance()?.apiService?.getProfile()?.enqueue(object : retrofit2.Callback<HttpResult<java.util.ArrayList<UserModel>>> {
            override fun onFailure(call: Call<HttpResult<java.util.ArrayList<UserModel>>>, t: Throwable) {
                isFinish[0] = true
                hideLoading()

                val errBody = ErrorUtil.getErrorMessage(t)
                ToastUtils.showShort(errBody)
            }

            override fun onResponse(
                call: Call<HttpResult<java.util.ArrayList<UserModel>>>,
                response: Response<HttpResult<java.util.ArrayList<UserModel>>>
            ) {
                isFinish[0] = true
                hideLoading()

                if (response.isSuccessful) {
                    if(response.body()!!.success) {
                        try {
                            response.body()?.data.let {
                                val user = it?.get(0)
                                GlobalUser.currentUserModel = user
                                reloadProfile()
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
                if (countFinish == 2) {
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
}
