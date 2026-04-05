package hu.tb.di

import hu.tb.repository.ChatRepository
import hu.tb.datasource.data.repository.ChatRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    singleOf(::ChatRepositoryImpl).bind<ChatRepository>()
}