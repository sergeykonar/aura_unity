package konar.aura.repository

import android.content.Context
import konar.aura.mappers.toAppUI
import konar.aura.mappers.toData

class BootRepositoryImpl(
    private val bootEventDao: konar.aura.data.dao.BootEventDao,
    context: Context
) : konar.aura.domain.repository.BootRepository {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFERENCES_NAME = "app_preferences"
        const val DISMISS_COUNT_KEY = "dismiss_count"
        const val TOTAL_DISMISSALS_KEY = "total_dismissals"
        const val DISMISSAL_INTERVAL_KEY = "dismissal_interval"
        const val NOTIFICATION_PRESENT_KEY = "is_notification_present"
        const val NOTIFICATION_TIME_KEY = "notification_time"

        const val DEFAULT_DISMISSALS = 5
        const val DEFAULT_INTERVAL = 1L
    }

    override suspend fun insertBootEvent(timestamp: Long) {
        val bootEvent = konar.aura.domain.models.BootEvent(timestamp)
        bootEventDao.insertBootEvent(bootEvent.toData())
    }

    override suspend fun getBootEvents(): List<konar.aura.domain.models.BootEvent> {
        return bootEventDao.getAllBootEvents().map { it.toAppUI() }
    }

    override suspend fun getLastTwoBootEvents(): List<konar.aura.domain.models.BootEvent> {
        return bootEventDao.getLastTwoBootEvents().map { it.toAppUI() }
    }

    override suspend fun getBootEventsCount(day: Long): Int {
        return bootEventDao.getBootEventsCount(day)
    }

    override fun incrementDismissCount(): Int {
        val dismissCount = sharedPreferences.getInt(DISMISS_COUNT_KEY, 0) + 1
        sharedPreferences.edit().putInt(DISMISS_COUNT_KEY, dismissCount).apply()
        return dismissCount
    }

    override fun getDismissCount(): Int {
        return sharedPreferences.getInt(DISMISS_COUNT_KEY, 0)
    }

    override fun getTotalDismissalsAllowed(): Int {
        return sharedPreferences.getInt(TOTAL_DISMISSALS_KEY, DEFAULT_DISMISSALS)
    }

    override fun setTotalDismissalsAllowed(allowedDismissals: Int) {
        sharedPreferences.edit().putInt(TOTAL_DISMISSALS_KEY, allowedDismissals).apply()
    }

    override fun getDismissalInterval(): Long {
        return if (getDismissCount() > getTotalDismissalsAllowed()) {
            15L
        } else {
            sharedPreferences.getLong(DISMISSAL_INTERVAL_KEY, DEFAULT_INTERVAL)  // Otherwise, return the stored interval
        }
    }

    override fun get15MinInterval(): Long {
        return 15L
    }

    override fun setDismissalInterval(interval: Long): Long {
        return sharedPreferences.getLong(DISMISSAL_INTERVAL_KEY, interval)
    }

    override fun resetDismissCount() {
        sharedPreferences.edit().putInt(DISMISS_COUNT_KEY, 0).apply()
    }

    override fun wasNotificationPresent(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICATION_PRESENT_KEY, false)
    }

    override fun notificationVisible(isVisible: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICATION_PRESENT_KEY, isVisible).apply()
    }

    override fun getScheduledAlarmTime(): Long {
        return sharedPreferences.getLong(NOTIFICATION_TIME_KEY, System.currentTimeMillis())
    }

    override fun setScheduledTime(time: Long) {
        sharedPreferences.edit().putLong(NOTIFICATION_TIME_KEY, time).apply()
    }
}
