package konar.aura.mappers

import konar.aura.domain.models.BootEvent

fun BootEvent.toData(): konar.aura.data.model.BootEvent {
    return konar.aura.data.model.BootEvent(this.timestamp)
}

fun konar.aura.data.model.BootEvent.toAppUI(): BootEvent {
    return BootEvent(this.timestamp)
}