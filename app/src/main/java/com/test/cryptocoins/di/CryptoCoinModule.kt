package com.test.cryptocoins.di

import com.test.cryptocoins.common.NetworkUtils
import com.test.cryptocoins.repository.CryptoCoinRepository
import com.test.cryptocoins.repository.CryptoCoinRepositoryImpl
import com.test.cryptocoins.service.CryptoCoinService
import com.test.cryptocoins.usecase.CryptoCoinUseCase
import com.test.cryptocoins.usecase.CryptoCoinUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Named


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
@InstallIn(ViewModelComponent::class)
@Module
abstract class CryptoCoinModule {

    @ViewModelScoped
    @Binds
    abstract fun bindingCryptoCoinUseCase(impl: CryptoCoinUseCaseImpl): CryptoCoinUseCase

    @ViewModelScoped
    @Binds
    abstract fun bindingCryptoCoinRepository(impl: CryptoCoinRepositoryImpl): CryptoCoinRepository
}

@InstallIn(ViewModelComponent::class)
@Module
class CryptoCoinNetworkModule {

    @ViewModelScoped
    @Provides
    fun provideCryptoCoinService(
        @Named(NetworkUtils.RETROFIT_BUILDER)
        builder: Retrofit.Builder,
    ): CryptoCoinService = builder.baseUrl(NetworkUtils.BASE_URL).build()
        .create(CryptoCoinService::class.java)

}
