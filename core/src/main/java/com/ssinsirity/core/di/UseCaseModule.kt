package com.ssinsirity.core.di

import com.ssinsirity.core.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideEmailValidator(): EmailValidator = EmailValidator.Default()

    @Provides
    fun providePasswordValidator(): PasswordValidator = PasswordValidator.Default()

    @Provides
    fun provideRepeatedPasswordValidator(): RepeatedPasswordValidator =
        RepeatedPasswordValidator.Default()

    @Provides
    fun provideNameValidator(): NameValidator = NameValidator.Default()

    @Provides
    fun providePhoneNumberValidator(): PhoneNumberValidator = PhoneNumberValidator.Default()
}