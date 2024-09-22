package konar.aura.unity.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import konar.aura.unity.databinding.ActivityMainBinding

import konar.aura.unity.presentation.adapter.BootEventAdapter
import konar.aura.unity.notification.NotificationWorker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bootEventAdapter: BootEventAdapter
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bootEventsRecyclerView.layoutManager = LinearLayoutManager(this)
        bootEventAdapter = BootEventAdapter()
        binding.bootEventsRecyclerView.adapter = bootEventAdapter


        viewModel.bootEventGroups.observe(this) { groupedEvents ->
            bootEventAdapter.submitList(groupedEvents)
        }


        viewModel.noBootEvents.observe(this) { noEvents ->
            if (noEvents) {
                binding.bootEventText.visibility = View.VISIBLE
                binding.bootEventText.text = "No boots detected"
                binding.bootEventsRecyclerView.visibility = View.GONE
            } else {
                binding.bootEventText.visibility = View.GONE
                binding.bootEventsRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.loadBootEvents()

        binding.buttonUpdateSettings.setOnClickListener {
            updateDismissalSettings()
        }

        handleNotificationPermission()
    }

    private fun handleNotificationPermission() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val dismissCount = viewModel.getDismissCount()
                scheduleNotificationWorker(dismissCount)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            val dismissCount = viewModel.getDismissCount()
            scheduleNotificationWorker(dismissCount)
        }
    }

    private fun scheduleNotificationWorker(dismissCount: Int) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(0, TimeUnit.MINUTES)
            .setInputData(workDataOf("dismiss_count" to dismissCount))
            .build()

        WorkManager.getInstance(baseContext).enqueue(workRequest)
    }

    private fun updateDismissalSettings() {
        val totalDismissals = binding.editTotalDismissals.text.toString().toIntOrNull() ?: 0
        val dismissalInterval = binding.editDismissalInterval.text.toString().toLongOrNull() ?: 0

        viewModel.setTotalDismissals(totalDismissals)
        viewModel.resetDismissCount()
        viewModel.setDismissalInterval(dismissalInterval)

        Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
    }
}