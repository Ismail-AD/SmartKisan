package com.appdev.smartkisan.Hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://ptagoakhjcfbltfjptzf.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB0YWdvYWtoamNmYmx0ZmpwdHpmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzUyMDkyNTYsImV4cCI6MjA1MDc4NTI1Nn0._ee9oAS7SxeHyZTFk0oWY_VnuWN6ZW0qNXWlsFGCZxQ"
        ) {
            install(Auth)
        }
    }
}
