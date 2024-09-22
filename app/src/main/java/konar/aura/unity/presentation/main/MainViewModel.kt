package konar.aura.unity.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import konar.aura.unity.presentation.adapter.BootEventGroup
import konar.aura.domain.models.BootEvent
import konar.aura.domain.repository.BootRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private val repository: BootRepository) : ViewModel() {

    private val _bootEventGroups = MutableLiveData<List<BootEventGroup>>()
    val bootEventGroups: LiveData<List<BootEventGroup>> get() = _bootEventGroups

    private val _noBootEvents = MutableLiveData<Boolean>()
    val noBootEvents: LiveData<Boolean> get() = _noBootEvents

    fun loadBootEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val bootEvents = repository.getBootEvents()
            withContext(Dispatchers.Main) {
                if (bootEvents.isEmpty()) {
                    _noBootEvents.value = true
                } else {
                    _noBootEvents.value = false
                    val groupedEvents = groupBootEventsByDay(bootEvents)
                    _bootEventGroups.value = groupedEvents
                }
            }
        }
    }

    private fun groupBootEventsByDay(events: List<BootEvent>): List<BootEventGroup> {
        return events.groupBy {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.format(Date(it.timestamp))
        }.map { entry ->
            BootEventGroup(entry.key, entry.value.size)
        }
    }

    fun getDismissCount(): Int {
        return repository.getDismissCount()
    }

    fun resetDismissCount() {
        repository.resetDismissCount()
    }

    fun setTotalDismissals(totalDismissals: Int) {
        repository.setTotalDismissalsAllowed(totalDismissals)
    }

    fun setDismissalInterval(dismissalInterval: Long) {
        repository.setDismissalInterval(dismissalInterval)
    }
}