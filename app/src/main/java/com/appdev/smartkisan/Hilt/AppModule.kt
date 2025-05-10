package com.appdev.smartkisan.Hilt

import android.content.Context
import android.content.SharedPreferences
import com.appdev.smartkisan.Utils.SessionManagement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    @Named("user_session_prefs")
    fun provideUserSessionSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSessionManagement(
        @Named("user_session_prefs") sharedPreferences: SharedPreferences
    ): SessionManagement {
        return SessionManagement(sharedPreferences)
    }
}