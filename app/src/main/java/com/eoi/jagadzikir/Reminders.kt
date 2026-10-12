package com.eoi.jagadzikir

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

// How reminders work: the app sets ONE alarm with the phone's own alarm clock,
// for the next reminder time. When it rings, the app shows the notification
// and sets the alarm for the time after that. No server, no internet.
object Reminders {
    private const val CHANNEL = "dzikir"
    private const val NOTIFICATION_ID = 1

    fun schedule(context: Context) {
        val s = Store.load(context)
        val alarms = context.getSystemService(AlarmManager::class.java)
        val alarm = alarmIntent(context)
        alarms.cancel(alarm)
        if (!s.running) return
        val at = nextReminderAt(s, Calendar.getInstance()) ?: return
        // Allowed even in battery saver. Android may delay it by a few minutes.
        alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, alarm)
    }

    fun notificationsAllowed(context: Context): Boolean =
        context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val channel = NotificationChannel(CHANNEL, "Pengingat dzikir", NotificationManager.IMPORTANCE_DEFAULT).apply {
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(context: Context) {
        if (!notificationsAllowed(context)) return
        ensureChannel(context)
        val d = dzikirById(Store.load(context).dzikir)
        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = builder(context)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(GREEN)
            .setContentTitle(d.arabic)
            .setContentText(d.latin)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        // Same ID every time, so a new reminder replaces the old one instead of piling up.
        context.getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    private fun builder(context: Context): Notification.Builder =
        if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, CHANNEL) else legacyBuilder(context)

    @Suppress("DEPRECATION")
    private fun legacyBuilder(context: Context): Notification.Builder =
        Notification.Builder(context)
            .setSound(null)
            .setVibrate(null)
            .setPriority(Notification.PRIORITY_DEFAULT)

    private fun alarmIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, 0,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val s = Store.load(context)
        val now = Calendar.getInstance()
        val minute = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        // If Android delayed the alarm into quiet hours, stay silent.
        if (s.running && !isQuietTime(s, minute)) Reminders.show(context)
        Reminders.schedule(context)
    }
}

class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Reminders.schedule(context)
    }
}
