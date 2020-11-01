package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import id.ias.dekaforyou.R
import id.ias.dekaforyou.model.AnnouncementModel
import kotlinx.android.synthetic.main.announcement_profile_view_pager.view.*

class AnnouncementAdapter(private val mContext: Context, private val mData: ArrayList<AnnouncementModel>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = View.inflate(mContext, R.layout.announcement_profile_view_pager, null)
        view.tv_content.text = mData[position].pesan
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}