package konar.aura.unity.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import konar.aura.unity.notification.scheduler.NotificationScheduler
import konar.aura.unity.utils.formatDate
import org.koin.java.KoinJavaComponent.getKoin

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository: konar.aura.domain.repository.BootRepository = getKoin().get()
        val notificationHandler = getKoin().get<NotificationScheduler>()
        val bootNotificationManager = BootNotificationManager(notificationHandler)
        val events = repository.getLastTwoBootEvents()
        val dismissCount = repository.getDismissCount()

        val notificationMessage = when (events.size) {
            0 -> "No boots detected"
            1 -> "The boot was detected = ${formatDate(events[0].timestamp)}"
            2 -> {
                val delta = events[0].timestamp - events[1].timestamp
                "Last boots time delta = $delta ms"
            }
            else -> "Unexpected state"
        }

        Log.i("NotificationWorker", "Events: $events Dismiss count: $dismissCount")
        bootNotificationManager.showNotification(applicationContext, "Boot Event", notificationMessage)
        return Result.success()
    }
}