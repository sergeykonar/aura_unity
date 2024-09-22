package konar.aura.domain.repository

import konar.aura.domain.models.BootEvent

interface BootRepository {

    suspend fun insertBootEvent(timestamp: Long)

    suspend fun getBootEvents(): List<konar.aura.domain.models.BootEvent>

    suspend fun getLastTwoBootEvents(): List<konar.aura.domain.models.BootEvent>

    suspend fun getBootEventsCount(day: Long): Int

    fun incrementDismissCount(): Int

    fun getDismissCount(): Int

    fun getTotalDismissalsAllowed(): Int

    fun setTotalDismissalsAllowed(allowedDismissals: Int)

    fun getDismissalInterval(): Long

    fun get15MinInterval(): Long

    fun setDismissalInterval(interval: Long): Long

    fun resetDismissCount()

    fun wasNotificationPresent(): Boolean

    fun notificationVisible(isVisible: Boolean)

    fun getScheduledAlarmTime(): Long

    fun setScheduledTime(time: Long)
}
