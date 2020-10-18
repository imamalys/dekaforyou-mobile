package id.ias.dekaforyou

import LoadingDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ToastUtils
import id.ias.dekaforyou.constant.LoginConstant
import id.ias.dekaforyou.data.GlobalUser
import id.ias.dekaforyou.http.RetrofitManager
import id.ias.dekaforyou.util.ErrorUtil
import kotlinx.android.synthetic.main.activity_login.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {

    private val loading = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(LoginConstant.getLoginToken() != "") {
            goToMainActivity()
        }
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
                RetrofitManager.getInstance()?.apiService?.loginUser(params)
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeOn(Schedulers.io())
                    ?.subscribe({ result ->
                        loading.dismiss()
                        if (result.success) {
                            try {
                                result.data?.let {
                                    val user = it[0]
                                    LoginConstant.setLoginUser(user.token!!, user.id, user.name)
                                    ToastUtils.showLong("Sukses")
                                    GlobalUser.currentUser = user
                                    goToMainActivity()
                                }?: run {
                                    ToastUtils.showLong("Terdapat masalah pada Pengguna, Silahkan Hubungi Admin")
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        } else {
                            ToastUtils.showLong(result.msg)
                        }
                    }, {error ->
                        loading.dismiss()
                        val errBody = ErrorUtil.getErrorMessage(error)
                        ToastUtils.showLong(errBody)
                    })
            } else {
                ToastUtils.showLong("Username dan Password tidak boleh kosong")
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
