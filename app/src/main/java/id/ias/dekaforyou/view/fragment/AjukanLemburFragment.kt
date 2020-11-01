package id.ias.dekaforyou.view.fragment

import LoadingDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.ias.dekaforyou.MainActivity
import id.ias.dekaforyou.R
import kotlinx.android.synthetic.main.fragment_ajukan_lembur.*
import java.util.*

class AjukanLemburFragment : Fragment() {

    val loading = LoadingDialog()
    val myCalendar = Calendar.getInstance()
    var isAwal: Boolean = true
    var timeShiftAwal: IntArray = IntArray(2)
    var timeShiftAkhir: IntArray = IntArray(2)
    lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajukan_lembur, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = context!!

        timeShiftAwal[0] = myCalendar.get(Calendar.HOUR_OF_DAY)
        timeShiftAwal[1] = myCalendar.get(Calendar.MINUTE)
        timeShiftAkhir[0] = myCalendar.get(Calendar.HOUR_OF_DAY)
        timeShiftAkhir[1] = myCalendar.get(Calendar.MINUTE)
        tv_jam_shift_awal.text = String.format("%02d:%02d", myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE))
        tv_jam_shift_akhir.text = String.format("%02d:%02d", myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE))
        setAction()
    }

    private fun setAction() {
        tv_jam_shift_awal.setOnClickListener {
            isAwal = true
            timeDialogShow()
        }

        tv_jam_shift_akhir.setOnClickListener {
            isAwal = false
            timeDialogShow()
        }

        iv_back.setOnClickListener {
            MainActivity.mainActivity.back()
        }
    }

    private fun timeDialogShow() {
        var hour = 0
        var minute = 0
        if (isAwal) {
            hour = timeShiftAwal[0]
            minute = timeShiftAwal[1]
        } else {
            hour = timeShiftAkhir[0]
            minute = timeShiftAkhir[1]
        }
        val timePickerDialog = TimePickerDialog(context, R.style.CustomDatePickerDialog, date, hour, minute, true)
        timePickerDialog.setTitle("Atur Jam")
        timePickerDialog.setContentView(R.layout.time_picker_layout)
        timePickerDialog.show()
    }

    private val date = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        myCalendar.set(Calendar.HOUR_OF_DAY, hour)
        myCalendar.set(Calendar.MINUTE, minute)
        updateTime(hour, minute)
    }

    private fun updateTime(hour: Int, minute: Int) {
        if (isAwal) {
            timeShiftAwal[0] = hour
            timeShiftAwal[1] = minute
            tv_jam_shift_awal.text = String.format("%02d:%02d", hour, minute)
        } else {
            timeShiftAkhir[0] = hour
            timeShiftAkhir[1] = minute
            tv_jam_shift_akhir.text = String.format("%02d:%02d", hour, minute)
        }
    }
}