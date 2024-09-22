package konar.aura.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BootEvent(
    @PrimaryKey val timestamp: Long
)