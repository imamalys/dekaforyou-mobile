package id.ias.dekaforyou.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun dateNotificationFormat(date: String): String? {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatNotification = SimpleDateFormat("dd MM yyyy hh:mma")
            var dateFormat: Date = Date()
            var notificationDate: String = ""
            try {
                dateFormat = format.parse(date)
                System.out.println(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            try {
                notificationDate = formatNotification.format(dateFormat)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return notificationDate
        }
    }
}