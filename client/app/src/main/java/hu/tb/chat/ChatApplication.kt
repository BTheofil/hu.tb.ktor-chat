package hu.tb.chat

import android.app.Application
import hu.tb.data.di.authDataModule
import hu.tb.datastore.di.datastoreModule
import hu.tb.presentation.di.authPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ChatApplication)
            modules(
                authDataModule,
                authPresentationModule,
                datastoreModule
            )
        }
    }
}