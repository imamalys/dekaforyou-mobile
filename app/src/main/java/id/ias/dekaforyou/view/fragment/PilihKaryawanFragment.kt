package id.ias.dekaforyou.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import id.ias.dekaforyou.adapter.KaryawanAdapter
import id.ias.dekaforyou.model.KaryawanModel
import kotlinx.android.synthetic.main.karyawan_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class PilihKaryawanFragment : Fragment() {
    lateinit var mContext: Context
    val KARYAWAN_KEY = "karyawan_key"
    lateinit var karyawanModel: KaryawanModel
    lateinit var karyawansModel: ArrayList<KaryawanModel>
    lateinit var karyawanDataModel: ArrayList<KaryawanModel>
    val handler = Handler()

    fun newInstance(
        karyawanModel: ArrayList<KaryawanModel>
    ): PilihKaryawanFragment {
        val fragment = PilihKaryawanFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList(KARYAWAN_KEY, karyawanModel)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null) {
            karyawansModel = arguments!!.getParcelableArrayList(KARYAWAN_KEY)!!
            karyawanDataModel = karyawansModel
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mContext = context!!

        return inflater.inflate(R.layout.karyawan_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadAdapter()

        et_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s.toString().let {
                    handler.removeCallbacksAndMessages(null)
                    if(it != "") {
                        handler.postDelayed({
                            karyawanDataModel = ArrayList()
                            for (karyawan in karyawansModel) {
                                if(karyawan.name.toLowerCase().contains(it)) {
                                    karyawanDataModel.add(karyawan)
                                }
                            }

                            reloadAdapter()

                        }, 2000)
                    } else {
                        handler.postDelayed({
                            karyawanDataModel = karyawansModel
                            reloadAdapter()
                        }, 2000)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        iv_back.setOnClickListener {
            MainActivity.mainActivity.back()
        }
    }

    private fun reloadAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = KaryawanAdapter(mContext, karyawanDataModel, clickListener = object: KaryawanAdapter.Listener {
            override fun onClick(data: KaryawanModel) {
                karyawanModel = data
                if(MainActivity.mainActivity.isAjukanCutiFragment) {
                    MainActivity.mainActivity.goToAjukanCutiCallback(karyawanModel)
                } else {
                    MainActivity.mainActivity.goToTukarShiftCallback(karyawanModel)
                }
            }
        })

        recycler_view.adapter = adapter
    }
}