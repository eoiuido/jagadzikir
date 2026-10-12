package com.eoi.jagadzikir

import java.util.Calendar
import java.util.Locale

private const val DAY = 24 * 60

val INTERVALS = listOf(30, 60, 90, 120, 180)

// Every reminder time in one day (minutes after midnight), skipping quiet hours.
// Example: every 60 min, quiet 22:00-04:30 -> 05:30, 06:30, ..., 21:30.
fun reminderTimes(s: Settings): List<Int> {
    val times = mutableListOf<Int>()
    if (!s.quietEnabled) {
        for (t in 0 until DAY step s.intervalMinutes) times.add(t)
        return times
    }
    if (s.quietStart == s.quietEnd) return times
    val activeLength = (s.quietStart - s.quietEnd + DAY) % DAY
    var k = 1
    while (k * s.intervalMinutes < activeLength) {
        times.add((s.quietEnd + k * s.intervalMinutes) % DAY)
        k++
    }
    return times.sorted()
}

// The moment of the next reminder after `now`, or null if there are no reminder times.
fun nextReminderAt(s: Settings, now: Calendar): Long? {
    val times = reminderTimes(s)
    if (times.isEmpty()) return null
    val nowMinute = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    val next = (now.clone() as Calendar).apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val t = times.firstOrNull { it > nowMinute } ?: times.first().also { next.add(Calendar.DAY_OF_YEAR, 1) }
    next.set(Calendar.HOUR_OF_DAY, t / 60)
    next.set(Calendar.MINUTE, t % 60)
    return next.timeInMillis
}

fun isQuietTime(s: Settings, minute: Int): Boolean {
    if (!s.quietEnabled || s.quietStart == s.quietEnd) return false
    return if (s.quietStart < s.quietEnd) minute >= s.quietStart && minute < s.quietEnd
    else minute >= s.quietStart || minute < s.quietEnd
}

fun formatTime(minutes: Int): String =
    String.format(Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60)

fun formatInterval(minutes: Int): String = when {
    minutes < 60 -> "$minutes menit"
    minutes % 60 == 0 -> "${minutes / 60} jam"
    else -> "${minutes / 60},${minutes % 60 * 10 / 60} jam"
}
