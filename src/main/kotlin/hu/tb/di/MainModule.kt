package hu.tb.di

import hu.tb.repository.SampleRepository
import hu.tb.datasource.data.repository.SampleRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    singleOf(::SampleRepositoryImpl).bind<SampleRepository>()
}