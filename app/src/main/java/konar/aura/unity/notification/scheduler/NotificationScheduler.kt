package konar.aura.unity.notification.scheduler

import android.app.PendingIntent
import android.content.Context

interface NotificationScheduler {
    fun createPendingIntent(context: Context): PendingIntent
    fun calculateAlarmTime(interval: Long): Long
}