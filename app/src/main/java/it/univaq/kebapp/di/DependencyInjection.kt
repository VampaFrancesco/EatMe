package it.univaq.kebapp.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.univaq.kebapp.data.remote.KebabbariRetrofitRepository
import it.univaq.kebapp.data.remote.KebabbariService
import it.univaq.kebapp.domain.repository.KebabbariRemoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton



    @Module
    @InstallIn(SingletonComponent::class)
    object RetrofitModule{
        @Provides
        @Singleton
        fun retrofitClient() : Retrofit =  Retrofit.Builder()
                .baseUrl("BASE_URL")
                .addConverterFactory(GsonConverterFactory.create())
                .build()


    @Provides
    @Singleton
    fun kebabbariService(retrofit: Retrofit): KebabbariService =
        retrofit.create(KebabbariService::class.java)

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{

    @Binds
    @Singleton
    abstract fun remoteRepository(repository: KebabbariRetrofitRepository): KebabbariRemoteRepository

}