package com.firsov.rza.di

import com.firsov.rza.data.parser.DocxParser
import com.firsov.rza.data.repository.DocxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDocxParser(): DocxParser = DocxParser()

    @Provides
    @Singleton
    fun provideDocxRepository(parser: DocxParser): DocxRepository = DocxRepository(parser)
}

