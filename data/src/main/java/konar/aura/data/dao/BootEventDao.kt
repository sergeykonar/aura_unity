package konar.aura.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import konar.aura.data.model.BootEvent

@Dao
interface BootEventDao {
    @Insert
    suspend fun insertBootEvent(event: BootEvent)

    @Query("SELECT * FROM BootEvent ORDER BY timestamp DESC LIMIT 2")
    suspend fun getLastTwoBootEvents(): List<BootEvent>

    @Query("SELECT COUNT(*) FROM BootEvent WHERE date(timestamp/1000, 'unixepoch') = date(:day/1000, 'unixepoch')")
    suspend fun getBootEventsCount(day: Long): Int

    @Query("SELECT * FROM BootEvent")
    suspend fun getAllBootEvents(): List<BootEvent>
}