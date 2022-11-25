package com.ssinsirity.core.di

import android.content.Context
import com.ssinsirity.core.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideAuthManager(): AuthManager = AuthManagerImpl()

    @Provides
    fun provideCatalogRepository(): CatalogRepository = CatalogRepositoryImpl()

    @Provides
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    fun provideBookedCatalogRepository(userSharedPref: UserSharedPref): BookedCatalogRepository =
        BookedCatalogRepositoryImpl(userSharedPref = userSharedPref)

    @Provides
    fun provideUserSharedPref(@ApplicationContext context: Context): UserSharedPref =
        UserSharedPref(context)
}