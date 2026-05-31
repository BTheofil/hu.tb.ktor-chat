package hu.tb.presentation.di

import hu.tb.presentation.dashboard.DashboardViewModel
import hu.tb.presentation.user_search.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SearchViewModel)
}