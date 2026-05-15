package hu.tb.datastore.di

import hu.tb.datastore.UserDatastoreRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val datastoreModule = module {
    singleOf(::UserDatastoreRepository)
}