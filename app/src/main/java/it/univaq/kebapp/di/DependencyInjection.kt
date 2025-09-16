package it.univaq.kebapp.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.univaq.kebapp.data.local.KebabbariDatabase
import it.univaq.kebapp.data.local.KebabbariRoomRepository
import it.univaq.kebapp.data.local.dao.KebabbariDao
import it.univaq.kebapp.data.remote.KebabbariRetrofitRepository
import it.univaq.kebapp.data.remote.KebabbariService
import it.univaq.kebapp.domain.repository.KebabbariLocalRepository
import it.univaq.kebapp.domain.repository.KebabbariRemoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java


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

    @Binds
    @Singleton
    abstract fun localRepository(repository: KebabbariRoomRepository): KebabbariLocalRepository

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): KebabbariDatabase =
        Room.databaseBuilder(
            context,
            KebabbariDatabase::class.java,
            "kebabbari_database"
        ).build()


    @Provides
    @Singleton
    fun kebabbariDao(database: KebabbariDatabase): KebabbariDao = database.getKebabbariDao()

}