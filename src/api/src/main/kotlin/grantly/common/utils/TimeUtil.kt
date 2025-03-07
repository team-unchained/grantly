package grantly.common.utils

import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

class TimeUtil {
    companion object {
        fun toRFC3339(time: Temporal): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            return formatter.format(time)
        }
    }
}
