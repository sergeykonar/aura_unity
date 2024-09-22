package konar.aura.unity.receivers

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import konar.aura.unity.notification.scheduler.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class BootReceiver : BroadcastReceiver() {

    private val notificationHandler: NotificationScheduler = getKoin().get()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            handleBootCompleted(context)
        }
    }

    private fun handleBootCompleted(context: Context) {
        val repository: konar.aura.domain.repository.BootRepository = getKoin().get()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        CoroutineScope(Dispatchers.IO).launch {
            repository.insertBootEvent(System.currentTimeMillis())

            if (repository.wasNotificationPresent()) {
                handleExistingNotification(context, repository, alarmManager)
            } else {
                val scheduledAlarmTime = repository.getScheduledAlarmTime()
                val currentTime = System.currentTimeMillis()

                if (scheduledAlarmTime > currentTime) {
                    Log.i("BootReceiver", "Notification was dismissed before reboot. Scheduling alarm at $scheduledAlarmTime")
                    rescheduleAlarm(context, repository, alarmManager, scheduledAlarmTime)
                } else {
                    scheduleInitialAlarm(context, repository, alarmManager)
                }
            }
        }
    }

    private fun rescheduleAlarm(context: Context, repository: konar.aura.domain.repository.BootRepository, alarmManager: AlarmManager, scheduledAlarmTime: Long) {
        val pendingIntent = notificationHandler.createPendingIntent(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(scheduledAlarmTime, pendingIntent), pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledAlarmTime, pendingIntent)
        }
        repository.setScheduledTime(scheduledAlarmTime)
    }

    private fun handleExistingNotification(context: Context, repository: konar.aura.domain.repository.BootRepository, alarmManager: AlarmManager) {
        showNotification(context, alarmManager, System.currentTimeMillis(), repository)
    }

    private fun scheduleInitialAlarm(context: Context, repository: konar.aura.domain.repository.BootRepository, alarmManager: AlarmManager) {
        val interval = repository.getDismissalInterval()

        Log.i("BootReceiver", "$interval minutes")

        val alarmTime = notificationHandler.calculateAlarmTime(interval)
        val pendingIntent = notificationHandler.createPendingIntent(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarmTime, pendingIntent), pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
        repository.setScheduledTime(alarmTime)
    }

    private fun showNotification(context: Context, alarmManager: AlarmManager, alarmTime: Long, repository: konar.aura.domain.repository.BootRepository) {
        val pendingIntent = notificationHandler.createPendingIntent(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(alarmTime, pendingIntent), pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }
        repository.setScheduledTime(alarmTime)
    }
}
