package konar.aura.unity.notification.scheduler

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import konar.aura.unity.receivers.NotificationTaskReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.TimeUnit

class NotificationSchedulerImpl: NotificationScheduler {
    override fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationTaskReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun calculateAlarmTime(interval: Long): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val futureTime = LocalDateTime.now().plusMinutes(interval)
            futureTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        } else {
            Date().time + TimeUnit.MINUTES.toMillis(interval)
        }
    }

}