package id.ias.dekaforyou

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ias.dekaforyou.adapter.GuideAdapter
import id.ias.dekaforyou.constant.LoginConstant
import kotlinx.android.synthetic.main.activity_guide.*


class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val guideAdapter = GuideAdapter(this)
        view_pager.adapter = guideAdapter

        addDots(0)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                addDots(position)
            }
        })
        
        tv_skip.setOnClickListener {
            goToLoginActivity()
        }

        iv_back.setOnClickListener {
            if (view_pager.currentItem != 0) {
                view_pager.setCurrentItem(view_pager.currentItem - 1, true)
            }
        }

        iv_next.setOnClickListener {
            if (view_pager.currentItem != 2) {
                view_pager.setCurrentItem(view_pager.currentItem + 1, true)
            } else if(view_pager.currentItem == 2) {
                goToLoginActivity()
            }
        }
    }
    
    fun goToLoginActivity() {
        LoginConstant.setGuideShowed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun addDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(3)
        val colorsActive = resources.getColor(R.color.white)
        val colorsInactive = resources.getColor(R.color.grey)

        v_dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35F
            dots[i]?.setTextColor(colorsInactive)
            v_dots.addView(dots[i])
        }

        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive)
    }
}
