package hu.tb.di

import hu.tb.datasource.MessageRepository
import hu.tb.datasource.MessageRepositoryImpl
import hu.tb.datasource.sample.SampleRepository
import hu.tb.datasource.sample.SampleRepositoryImpl
import hu.tb.group.GroupController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {

    factory { GroupController(get()) }

    single<MessageRepository> {
        MessageRepositoryImpl(get())
    }

    singleOf(::SampleRepositoryImpl).bind<SampleRepository>()
}