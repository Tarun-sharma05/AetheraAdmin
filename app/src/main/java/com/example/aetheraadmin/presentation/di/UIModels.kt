package com.example.aetheraadmin.presentation.di

import com.example.aetheraadmin.data.repoimpl.repoImpal
import com.example.aetheraadmin.domain.repo.repo
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UIModels {


    @Provides
    fun provideRepo(
        firestore: FirebaseFirestore
    ): repo{
        return repoImpal(
            firestore
        )
    }
}