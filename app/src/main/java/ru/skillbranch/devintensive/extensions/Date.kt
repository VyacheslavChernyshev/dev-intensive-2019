package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var timeDiff = date.time - this.time
    val isFuture = (timeDiff < 0)
    var result = ""
    timeDiff = abs(timeDiff)
    result +=
        when (timeDiff) {
            in 0L..(1 * SECOND) -> "только что"
            in (1 * SECOND)..(45 * SECOND) -> if (isFuture) "через несколько секунд" else "несколько секунд назад"
            in (45 * SECOND)..(75 * SECOND) -> if (isFuture) "через минуту" else "минуту назад"
            in (75 * SECOND)..(45 * MINUTE) -> if (isFuture) "через ${TimeUnits.MINUTE.plural((timeDiff / MINUTE).toInt())}" else "${TimeUnits.MINUTE.plural((timeDiff / MINUTE).toInt())} назад"
            in (45 * MINUTE)..(75 * MINUTE) -> if (isFuture) "через час" else "час назад"
            in (75 * MINUTE)..(22 * HOUR) -> if (isFuture) "через ${TimeUnits.HOUR.plural((timeDiff / HOUR).toInt())}" else "${TimeUnits.HOUR.plural((timeDiff / HOUR).toInt())} назад"
            in (22 * HOUR)..(26 * HOUR) -> if (isFuture) "через день" else "день назад"
            in (26 * HOUR)..(360 * DAY) -> if (isFuture) "через ${TimeUnits.DAY.plural((timeDiff / DAY).toInt())}" else "${TimeUnits.DAY.plural((timeDiff / DAY).toInt())} назад"
            else -> if (isFuture) "более чем через год" else "более года назад"
        }
    return result
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;
    fun plural(i: Int): String? {
        val pluralMultipl = when {
            i % 10 == 1 && i % 100 != 11 -> 0
            i % 10 in 2..4 && (i % 100 < 10 || i % 100 >= 20) -> 1
            else -> 2
        }
        return when (this) {
            SECOND -> "$i ${if (pluralMultipl == 0) "секунду" else if (pluralMultipl == 1) "секунды" else "секунд"}"
            MINUTE -> "$i ${if (pluralMultipl == 0) "минуту" else if (pluralMultipl == 1) "минуты" else "минут"}"
            HOUR -> "$i ${if (pluralMultipl == 0) "час" else if (pluralMultipl == 1) "часа" else "часов"}"
            DAY -> "$i ${if (pluralMultipl == 0) "день" else if (pluralMultipl == 1) "дня" else "дней"}"
        }
    }

}

