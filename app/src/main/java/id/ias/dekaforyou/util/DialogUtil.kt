package id.ias.dekaforyou.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import id.ias.dekaforyou.R
import id.ias.dekaforyou.adapter.KaryawanAdapter
import id.ias.dekaforyou.model.KaryawanModel
import kotlinx.android.synthetic.main.karyawan_dialog.view.*

object DialogUtil {
    fun show(
        context: Context,
        data: ArrayList<KaryawanModel>,
        listener: ClickListener
    ) {
        val dialogParam =
            DialogParam(data, listener)
        initDialog(context, dialogParam)
    }

    private fun initDialog(
        context: Context,
        dialogParam: DialogParam
    ) {
        val builder =
            AlertDialog.Builder(context)
        val view =
            View.inflate(context, R.layout.karyawan_dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        var listener = dialogParam.getListener()
        var cariKaryawanModel: ArrayList<KaryawanModel>?
        var karyawanModel: ArrayList<KaryawanModel> = dialogParam.getData()!!
        val handler = Handler()

        view.recycler_view.layoutManager = LinearLayoutManager(context)
        var adapter = KaryawanAdapter(context, karyawanModel, clickListener = object: KaryawanAdapter.Listener {
            override fun onClick(data: KaryawanModel) {
                listener?.onclick(data)
                dialog.dismiss()
            }
        })

        view.recycler_view.adapter = adapter

        view.et_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.toString().let {
                    if(it != "") {
                        handler.postDelayed({
                            cariKaryawanModel = dialogParam.getData()
                            cariKaryawanModel!!.removeAll(cariKaryawanModel!!)

                            for (karyawan in dialogParam.getData()!!) {
                                if(it == karyawan.name) {
                                    cariKaryawanModel!!.add(karyawan)
                                }
                            }

                            karyawanModel = cariKaryawanModel!!
                            adapter.notifyDataSetChanged()

                        }, 2000)
                    } else {
                        handler.postDelayed({
                            karyawanModel = dialogParam.getData()!!
                            adapter.notifyDataSetChanged()
                        }, 2000)
                    }
                }
            }
        })

        dialog.setOnDismissListener { handler.removeCallbacksAndMessages(null) }
    }

    interface ClickListener {
        fun onclick(data: KaryawanModel)
    }

    private class DialogParam {
        private var listener: ClickListener? = null
        private var data: ArrayList<KaryawanModel>? = null

        constructor(
            data: ArrayList<KaryawanModel>,
            listener: ClickListener
        ) {
            this.listener = listener
            this.data = data
        }


        fun getData(): ArrayList<KaryawanModel>? {
            return data
        }

        fun getListener(): ClickListener? {
            return listener
        }
    }
}
