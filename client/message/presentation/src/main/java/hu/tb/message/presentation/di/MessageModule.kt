package hu.tb.message.presentation.di

import hu.tb.message.presentation.MessageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val messageModule = module {
    viewModelOf(::MessageViewModel)
}