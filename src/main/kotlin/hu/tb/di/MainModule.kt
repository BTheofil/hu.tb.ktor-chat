package hu.tb.di

import hu.tb.datasource.sample.SampleRepository
import hu.tb.datasource.sample.SampleRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    singleOf(::SampleRepositoryImpl).bind<SampleRepository>()
}