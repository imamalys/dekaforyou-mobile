package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import id.ias.dekaforyou.R
import kotlinx.android.synthetic.main.guide_view_pager.view.*

class GuideAdapter(private val mContext: Context) : PagerAdapter() {

    var resId = intArrayOf(
        R.drawable.guide_one,
        R.drawable.guide_two,
        R.drawable.guide_three
    )
    var title = arrayOf("Isi Absensi di Aplikasi", "Penjadwalan Shift, Cuti, dan Lembur", "Tracking dan Monitoring")
    var desc = arrayOf(
        "Isi absensi karyawan cukup dengan smartphone Anda, Notifikasi real-time untuk keterlmbatan dan kehadiran",
    "Ajukan jadwal cuti, lembur & pertukaran jadwal kerja antar karyawan. semudah update status di media sosial. Mudah, cepat dan efisien",
    "Pantau kinerja dan laporan kehadiran, cuti serta lembur Anda dari satu antar muka yang simpel, detil dan lengkap")
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return 3
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = View.inflate(mContext, R.layout.guide_view_pager, null)
        Glide.with(mContext)
            .load(resId[position])
            .into(view.iv_guide_image)
        view.tv_title.text = title[position]
        view.tv_desc.text = desc[position]

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}