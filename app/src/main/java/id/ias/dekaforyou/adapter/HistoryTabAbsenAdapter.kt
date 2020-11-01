package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ias.dekaforyou.R
import id.ias.dekaforyou.http.ApiService
import id.ias.dekaforyou.model.ShiftModel
import kotlinx.android.synthetic.main.history_tab_absen.view.*

class HistoryTabAbsenAdapter(private val mContext: Context, private val dataLists: ArrayList<ShiftModel>, private val profile: String) : RecyclerView.Adapter<HistoryTabAbsenAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history_tab_absen, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataLists[position])

        Glide.with(mContext)
            .load(String.format("%s/%s", ApiService.BASE_URL, profile))
            .circleCrop()
            .placeholder(mContext.resources.getDrawable(R.drawable.ic_person))
            .into(holder.itemView.iv_profile)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ShiftModel) {
            itemView.tv_in.text = String.format("Clock In : %s", data.clockIn)
            itemView.tv_out.text = String.format("Clock Out : %s", data.clockOut)
            itemView.tv_terlambat.text = String.format("Terlambat : %s", data.terlambat)
            itemView.tv_time_stamp.text = data.timeStamp
        }
    }
}