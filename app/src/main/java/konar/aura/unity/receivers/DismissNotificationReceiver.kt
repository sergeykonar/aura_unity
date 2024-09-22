package konar.aura.unity.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import konar.aura.domain.repository.BootRepository
import org.koin.java.KoinJavaComponent.getKoin
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.TimeUnit

class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "DISMISS_NOTIFICATION") {
            val repository: BootRepository = getKoin().get()
            repository.incrementDismissCount()
            repository.notificationVisible(false)

            val dismissCount = repository.getDismissCount()
            val totalDismissalsAllowed = repository.getTotalDismissalsAllowed()
            val intervalBetweenDismissals = repository.getDismissalInterval()


            Log.i("DismissNotificationReceiver", "Count: $dismissCount and Allowed $totalDismissalsAllowed")

            rescheduleAlarm(context, intervalBetweenDismissals, repository)

        }
    }

    private fun rescheduleAlarm(context: Context, interval: Long, bootRepository: BootRepository) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationTaskReceiver::class.java).apply { action = "NOTIFICATION_TASK" }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val futureTime = LocalDateTime.now().plusMinutes(interval)
            futureTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        } else {
            val now = Date()
            now.time + TimeUnit.MINUTES.toMillis(interval)
        }
        val alarmClockInfo = AlarmManager.AlarmClockInfo(alarmTime, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAlarmClock(
                        alarmClockInfo,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }

        bootRepository.setScheduledTime(alarmTime)

        Log.i("Alarm time", alarmTime.toString())
        Log.i("Reschedule", "Alarm rescheduled for $interval minutes later")
        Toast.makeText(context, "Alarm rescheduled for $interval minutes later", Toast.LENGTH_SHORT).show()
    }
}
