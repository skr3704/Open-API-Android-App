package com.sonu.openapi.di.main

import androidx.lifecycle.ViewModel
import com.sonu.openapi.di.ViewModelKey
import com.sonu.openapi.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class MainViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accoutViewModel: AccountViewModel): ViewModel

}