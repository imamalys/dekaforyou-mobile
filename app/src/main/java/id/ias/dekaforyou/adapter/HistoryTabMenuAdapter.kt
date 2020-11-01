package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ias.dekaforyou.R
import id.ias.dekaforyou.http.ApiService
import id.ias.dekaforyou.model.HistoryEntityModel
import kotlinx.android.synthetic.main.history_tab_main_menu.view.*

class HistoryTabMenuAdapter(private val mContext: Context, private val dataLists: ArrayList<HistoryEntityModel>, private val profile: String) : RecyclerView.Adapter<HistoryTabMenuAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history_tab_main_menu, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataLists[position])
        holder.itemView.setOnClickListener {

        }

        Glide.with(mContext)
            .load(String.format("%s/%s", ApiService.BASE_URL, profile))
            .circleCrop()
            .placeholder(mContext.resources.getDrawable(R.drawable.ic_person))
            .into(holder.itemView.iv_profile)

            when(dataLists[position].status) {
            "reject" -> holder.itemView.btn_status.background = mContext.resources.getDrawable(R.drawable.red_corner)
            "pending" -> holder.itemView.btn_status.background = mContext.resources.getDrawable(R.drawable.orange_corner)
            "accept" -> holder.itemView.btn_status.background = mContext.resources.getDrawable(R.drawable.blue_corner)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: HistoryEntityModel) {
            itemView.tv_nama.text = data.name
            itemView.btn_status.text = data.status
            itemView.tv_tambahan.text = data.tambahan
            itemView.tv_alasan.text = data.alasan
        }
    }
}