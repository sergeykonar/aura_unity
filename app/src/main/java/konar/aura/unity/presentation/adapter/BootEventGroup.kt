package konar.aura.unity.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import konar.aura.unity.R


data class BootEventGroup(val date: String, val eventCount: Int)

class BootEventAdapter : ListAdapter<BootEventGroup, BootEventAdapter.BootEventViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BootEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_boot_event, parent, false)
        return BootEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: BootEventViewHolder, position: Int) {
        val bootEventGroup = getItem(position)
        holder.bind(bootEventGroup)
    }

    class BootEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventDateText: TextView = itemView.findViewById(R.id.event_date_text)
        private val eventCountText: TextView = itemView.findViewById(R.id.event_count_text)

        fun bind(eventGroup: BootEventGroup) {
            eventDateText.text = eventGroup.date
            eventCountText.text = eventGroup.eventCount.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BootEventGroup>() {
        override fun areItemsTheSame(oldItem: BootEventGroup, newItem: BootEventGroup): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: BootEventGroup, newItem: BootEventGroup): Boolean {
            return oldItem == newItem
        }
    }
}
