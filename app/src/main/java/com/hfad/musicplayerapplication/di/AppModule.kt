package com.hfad.musicplayerapplication.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.data.mapper.TrackMapper
import com.hfad.musicplayerapplication.data.network.DeezerApiService
import com.hfad.musicplayerapplication.data.network.RetrofitInstance
import com.hfad.musicplayerapplication.data.repository.AccountRepositoryImpl
import com.hfad.musicplayerapplication.data.repository.AuthRepositoryImpl
import com.hfad.musicplayerapplication.data.repository.TrackRepositoryImpl
import com.hfad.musicplayerapplication.data.repository.UserRepositoryImpl
import com.hfad.musicplayerapplication.domain.repository.AccountRepository
import com.hfad.musicplayerapplication.domain.repository.AuthRepository
import com.hfad.musicplayerapplication.domain.usecase.GetTracksUseCase
import com.hfad.musicplayerapplication.domain.repository.TrackRepository
import com.hfad.musicplayerapplication.domain.repository.UserRepository
import com.hfad.musicplayerapplication.domain.usecase.GetAllUsersUseCase
import com.hfad.musicplayerapplication.domain.usecase.GetFriendsUseCase
import com.hfad.musicplayerapplication.domain.usecase.LoginUseCase
import com.hfad.musicplayerapplication.domain.usecase.RegisterUseCase
import com.hfad.musicplayerapplication.domain.usecase.UpdateUserPremiumStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideDeezerApiService(): DeezerApiService {
//        return Retrofit.Builder()
//            .baseUrl("https://api.deezer.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(DeezerApiService::class.java)
//    }

    @Provides
    @Singleton
    fun provideDeezerApiService(): DeezerApiService {
        return RetrofitInstance.api
    }

    @Provides
    fun provideTrackRepository(
        mapper: TrackMapper,
        apiService: DeezerApiService
    ): TrackRepository {
        return TrackRepositoryImpl(mapper,apiService)
    }

    @Provides
    fun provideGetTracksUseCase(repository: TrackRepository): GetTracksUseCase {
        return GetTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideUserRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): UserRepository{
        return UserRepositoryImpl(firestore, auth)
    }

    @Provides
    fun provideUpdateUserPremiumStatusUseCase(
        repository: UserRepository
    ): UpdateUserPremiumStatusUseCase {
        return UpdateUserPremiumStatusUseCase(repository)
    }

    @Provides
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(auth)
    }

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }


    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    fun provideAccountRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AccountRepository{
        return AccountRepositoryImpl(auth = auth, firestore = firestore)
    }

    @Provides
    fun provideGetFriendsUseCase(repository: AccountRepository): GetFriendsUseCase{
        return GetFriendsUseCase(repository)
    }

    @Provides
    fun provideGetAllUsersUseCase(repository: AccountRepository): GetAllUsersUseCase{
        return GetAllUsersUseCase(repository)
    }
}