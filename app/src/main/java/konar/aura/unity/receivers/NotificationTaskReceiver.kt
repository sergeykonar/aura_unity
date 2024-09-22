package konar.aura.unity.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import konar.aura.unity.notification.BootNotificationManager
import konar.aura.domain.repository.BootRepository
import konar.aura.unity.notification.scheduler.NotificationScheduler
import konar.aura.unity.utils.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class NotificationTaskReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        val repository: BootRepository = KoinJavaComponent.getKoin().get()
        val notificationHandler: NotificationScheduler = KoinJavaComponent.getKoin().get()
        val bootNotificationManager = BootNotificationManager(notificationHandler)
        CoroutineScope(Dispatchers.Default).launch{// Get the last two boot events
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

            Log.e("NotificationWorker", "$events $dismissCount")
            bootNotificationManager.showNotification(
                p0,
                "Boot Event",
                notificationMessage
            )
        }
    }

}