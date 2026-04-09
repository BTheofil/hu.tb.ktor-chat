package hu.tb.di

import hu.tb.datasource.data.repository.ChatRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule = module {
    singleOf(::ChatRepository)
}