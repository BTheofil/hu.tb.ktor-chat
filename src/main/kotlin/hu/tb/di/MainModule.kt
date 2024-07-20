package hu.tb.di

import hu.tb.datasource.MessageRepository
import hu.tb.datasource.MessageRepositoryImpl
import hu.tb.group.GroupController
import org.koin.dsl.module

val mainModule = module {

    factory { GroupController(get()) }

    single<MessageRepository> {
        MessageRepositoryImpl(get())
    }

}