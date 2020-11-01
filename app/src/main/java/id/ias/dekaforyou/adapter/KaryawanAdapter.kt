package id.ias.dekaforyou.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ias.dekaforyou.R
import id.ias.dekaforyou.http.ApiService
import id.ias.dekaforyou.model.KaryawanModel
import kotlinx.android.synthetic.main.karyawan_selection_item.view.*

class KaryawanAdapter(private val mContext: Context, private val dataLists: ArrayList<KaryawanModel>, private var clickListener: Listener) : RecyclerView.Adapter<KaryawanAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.karyawan_selection_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataLists[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(dataLists[position])
        }

        Glide.with(mContext)
            .load(String.format("%s/%s", ApiService.BASE_URL, dataLists[position].profile))
            .circleCrop()
            .placeholder(R.drawable.ic_person)
            .into(holder.itemView.iv_profile)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: KaryawanModel) {
            itemView.tv_name.text = data.name
        }
    }

    interface Listener {
        fun onClick(data: KaryawanModel)
    }
}