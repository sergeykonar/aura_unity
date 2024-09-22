package konar.aura.unity.di

import androidx.room.Room
import konar.aura.unity.presentation.main.MainViewModel
import konar.aura.data.BootDatabase
import konar.aura.repository.BootRepositoryImpl
import konar.aura.unity.notification.scheduler.NotificationScheduler
import konar.aura.unity.notification.scheduler.NotificationSchedulerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_DATABASE = "app_database"

val appModule = module {

    single(named(APP_DATABASE)) {
        Room.databaseBuilder(
            androidContext(),
            BootDatabase::class.java, "boot-db"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        val db: BootDatabase = get(named(APP_DATABASE))
        db.bootEventDao()
    }

    factory<konar.aura.domain.repository.BootRepository> {
        BootRepositoryImpl(get(), androidContext())
    }

    viewModel {
        MainViewModel(get())
    }

    single<NotificationScheduler>{
        NotificationSchedulerImpl()
    }
}