package com.example.aetheraadmin.data.di

import com.example.aetheraadmin.data.repository.CategoryRepositoryImpl
import com.example.aetheraadmin.data.repository.DashboardRepositoryImpl
import com.example.aetheraadmin.data.repository.OrderRepositoryImpl
import com.example.aetheraadmin.data.repository.ProductRepositoryImpl
import com.example.aetheraadmin.data.repository.StorageRepositoryImpl
import com.example.aetheraadmin.data.repository.UserRepositoryImpl
import com.example.aetheraadmin.domain.repository.CategoryRepository
import com.example.aetheraadmin.domain.repository.DashboardRepository
import com.example.aetheraadmin.domain.repository.OrderRepository
import com.example.aetheraadmin.domain.repository.ProductRepository
import com.example.aetheraadmin.domain.repository.StorageRepository
import com.example.aetheraadmin.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideProductRepository(firestore: FirebaseFirestore): ProductRepository =
        ProductRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideCategoryRepository(firestore: FirebaseFirestore): CategoryRepository =
        CategoryRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository =
        StorageRepositoryImpl(storage)

    @Provides
    @Singleton
    fun provideDashboardRepository(firestore: FirebaseFirestore): DashboardRepository =
        DashboardRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideOrderRepository(firestore: FirebaseFirestore): OrderRepository =
        OrderRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository =
        UserRepositoryImpl(firestore)
}