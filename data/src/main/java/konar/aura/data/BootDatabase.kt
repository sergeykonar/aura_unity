package konar.aura.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [konar.aura.data.model.BootEvent::class], version = 1)
abstract class BootDatabase : RoomDatabase() {

    abstract fun bootEventDao(): konar.aura.data.dao.BootEventDao

}