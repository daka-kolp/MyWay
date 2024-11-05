package com.dkolp.myway.infrastructure.di

import android.content.Context
import com.dkolp.myway.core.data.GoogleMapRepository
import com.dkolp.myway.core.data.api.GoogleApiClient
import com.dkolp.myway.core.domain.MapRepository
import com.dkolp.myway.infrastructure.utils.SignInProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MyWayAppModule {
    @Singleton
    @Provides
    fun getSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://ieaqupvlthhdmrlftacu.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImllYXF1cHZsdGhoZG1ybGZ0YWN1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzAxMjQ3MzEsImV4cCI6MjA0NTcwMDczMX0.OmJ8M5O-8bc4gcrl-oJCvuyUZw0fP245tIS8ECn3kc4"
        ) {
            install(Auth)
        }
    }

    @Singleton
    @Provides
    fun getSignInProvider(supabaseClient: SupabaseClient): SignInProvider {
        return SignInProvider(supabaseClient)
    }

    @Singleton
    @Provides
    fun getLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun getApiClient(): GoogleApiClient = GoogleApiClient()

    @Provides
    @Singleton
    fun getMapRepository(client: GoogleApiClient): MapRepository = GoogleMapRepository(client)
}
