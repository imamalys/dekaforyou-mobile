package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ias.dekaforyou.R
import id.ias.dekaforyou.model.ApprovalRequestModel
import id.ias.dekaforyou.util.DateUtil
import kotlinx.android.synthetic.main.notifikasi_item_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(private val mContext: Context, private val dataLists: ArrayList<ApprovalRequestModel>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.notifikasi_item_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataLists[position])

        when(dataLists[position].status) {
            "pending" -> {
                holder.itemView.tv_detail.setTextColor(mContext.resources.getColor(R.color.colorAccent))
            }
            "accept" -> {
                holder.itemView.tv_detail.setTextColor(mContext.resources.getColor(R.color.green_text))
            }
            "reject" -> {
                holder.itemView.tv_detail.setTextColor(mContext.resources.getColor(R.color.red_background))
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ApprovalRequestModel) {
            var detail = "Pengajuan %s %s"
            var status = "status"
            when(data.status) {
                "pending" -> {
                    status = "Diproses"
                }
                "accept" -> {
                    status = "Disetujui"
                }
                "reject" -> {
                    status = "Ditolak"
                }
            }
            itemView.tv_name.text = data.namaKaryawan
            itemView.tv_detail.text = String.format(detail, data.jenis, status)
            itemView.tv_time.text = DateUtil.dateNotificationFormat(data.tanggalAwal!!)
        }
    }
}