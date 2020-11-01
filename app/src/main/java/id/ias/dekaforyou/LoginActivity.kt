package id.ias.dekaforyou

import LoadingDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ToastUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.http.HttpResult
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.model.UserModel
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val loading = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(LoginConstant.getLoginToken() != "") {
            goToMainActivity()
        }

        checkPermission()
        setupAction()
    }

    private fun setupAction() {
        et_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    iv_email_true.visibility = View.VISIBLE
                } else {
                    iv_email_true.visibility = View.GONE
                }
            }
        })

        tv_show_password.setOnClickListener {
            if (tv_show_password.text.toString() == resources.getString(R.string.show)) {
                et_password.inputType = InputType.TYPE_CLASS_TEXT
                tv_show_password.text = resources.getString(R.string.hide)
            } else {
                et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                tv_show_password.text = resources.getString(R.string.show)
            }
        }

        btn_sign_in.setOnClickListener {
            if (et_username.text.isNotEmpty() && et_password.text.isNotEmpty()) {
                loading.show(supportFragmentManager, null)
                val params = HashMap<String, Any>()
                params["username"] = et_username.text.toString()
                params["password"] = et_password.text.toString()
                params["imei"] = PhoneUtils.getIMEI()

                RetrofitManager.getInstance()?.apiService?.loginUser(params)?.enqueue(object : retrofit2.Callback<HttpResult<ArrayList<UserModel>>> {
                    override fun onFailure(
                        call: Call<HttpResult<ArrayList<UserModel>>>,
                        t: Throwable
                    ) {
                        loading.dismiss()
                        val errBody = ErrorUtil.getErrorMessage(t)
                        ToastUtils.showLong(errBody)
                    }

                    override fun onResponse(
                        call: Call<HttpResult<ArrayList<UserModel>>>,
                        response: Response<HttpResult<ArrayList<UserModel>>>
                    ) {
                        loading.dismiss()
                        if (response.isSuccessful) {
                            if (response.body()!!.success) {
                                try {
                                    response.body()?.data.let {
                                        val user = it?.get(0)
                                        LoginConstant.setLoginUser(user?.token!!, user.id, user.name, user.profile)
                                        ToastUtils.showLong("Sukses")
                                        GlobalUser.currentUserModel = user
                                        goToMainActivity()
                                    }
                                } catch (e: Throwable) {
                                    e.printStackTrace()
                                }
                            } else {
                                ToastUtils.showLong(response.body()?.msg)
                            }
                        }
                    }
                })
            } else {
                ToastUtils.showLong("Username dan Password tidak boleh kosong")
            }
        }
    }

    private fun checkPermission() {
        Dexter.withContext(applicationContext)
            .withPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest();
                }

            }).check()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
