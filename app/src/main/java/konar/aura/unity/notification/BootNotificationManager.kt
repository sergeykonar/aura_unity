package konar.aura.unity.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import konar.aura.domain.repository.BootRepository
import konar.aura.unity.R
import konar.aura.unity.notification.scheduler.NotificationScheduler
import konar.aura.unity.receivers.DismissNotificationReceiver
import org.koin.java.KoinJavaComponent

class BootNotificationManager(private val notificationHandler: NotificationScheduler) {

    companion object {
        private const val CHANNEL_ID = "boot_channel"
        private const val CHANNEL_NAME = "Boot Notification Channel"

        val repository: BootRepository = KoinJavaComponent.getKoin().get()
    }

    fun showNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dismissIntent = Intent(context, DismissNotificationReceiver::class.java).apply {
            action = "DISMISS_NOTIFICATION"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setDeleteIntent(pendingIntent)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context).notify(1001, builder.build())

        repository.notificationVisible(true)

        scheduleAlarm(context, repository, alarmManager)
    }

    private fun scheduleAlarm(context: Context, repository: BootRepository, alarmManager: AlarmManager) {
        val interval = repository.get15MinInterval()

        Log.i("BootNotificationManager", "$interval minutes")

        val alarmTime = notificationHandler.calculateAlarmTime(interval)
        val pendingIntent = notificationHandler.createPendingIntent(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarmTime, pendingIntent), pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
        repository.setScheduledTime(alarmTime)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
