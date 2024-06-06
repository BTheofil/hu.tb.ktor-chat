package hu.tb.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.tb.datasource.MessageRepository
import hu.tb.datasource.MessageRepositoryImpl
import hu.tb.group.GroupController
import org.koin.dsl.module

val mainModule = module {

    single {
        MongoClient.create(
            "mongodb+srv://admin:admin@cluster0.wtbp8u6.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        ).getDatabase("sample_mflix")
    }

    factory { GroupController(get()) }

    single<MessageRepository> {
        MessageRepositoryImpl(get())
    }

}