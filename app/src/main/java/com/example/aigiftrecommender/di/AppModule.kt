package com.example.aigiftrecommender.di

import android.content.ContentResolver
import android.content.Context
import com.example.aigiftrecommender.data.local.AppDatabase
import com.example.aigiftrecommender.data.local.HolidayDao
import com.example.aigiftrecommender.data.remote.ChatGPTApiService
import com.example.aigiftrecommender.repository.ContactRepository
import com.example.aigiftrecommender.repository.ContactRepositoryImpl
import com.example.aigiftrecommender.repository.HolidayRepository
import com.example.aigiftrecommender.repository.HolidayRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val CHATGPT_API_BASE_URL = "https://api.openai.com/"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideHolidayDao(appDatabase: AppDatabase): HolidayDao {
        return appDatabase.holidayDao()
    }

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext appContext: Context): ContentResolver {
        return appContext.contentResolver
    }

    @Provides
    @Singleton
    fun provideContactRepository(contentResolver: ContentResolver): ContactRepository {
        return ContactRepositoryImpl(contentResolver)
    }

    @Provides
    @Singleton
    fun provideHolidayRepository(holidayDao: HolidayDao): HolidayRepository {
        return HolidayRepositoryImpl(holidayDao)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        // Set to Level.BODY for development to see request/response bodies
        // Set to Level.NONE or Level.BASIC for production
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) 

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CHATGPT_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Ensure GSON dependency is added
            .build()
    }

    @Provides
    @Singleton
    fun provideChatGPTApiService(retrofit: Retrofit): ChatGPTApiService {
        return retrofit.create(ChatGPTApiService::class.java)
    }
}

