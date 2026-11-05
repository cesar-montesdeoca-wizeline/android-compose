package com.example.androidaccessibilityauditor.di

import com.example.androidaccessibilityauditor.data.repository.CartRepository
import com.example.androidaccessibilityauditor.data.repository.InMemoryCartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Application-scoped DI module.
 *
 * Provides [CartRepository] bound to [InMemoryCartRepository].
 * Unscoped: [InMemoryCartRepository] is stateless (returns a hardcoded list),
 * so no singleton identity is required.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideCartRepository(): CartRepository = InMemoryCartRepository()
}
