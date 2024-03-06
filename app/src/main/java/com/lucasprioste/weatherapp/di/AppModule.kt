package com.lucasprioste.weatherapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.location.LocationServices
import com.lucasprioste.weatherapp.data.local.db.WeatherDataBase
import com.lucasprioste.weatherapp.data.local.preferences.Preferences
import com.lucasprioste.weatherapp.data.remote.DefaultLocationClient
import com.lucasprioste.weatherapp.data.remote.WeatherApi
import com.lucasprioste.weatherapp.data.repository.SessionRepositoryImp
import com.lucasprioste.weatherapp.data.repository.WeatherRepositoryImp
import com.lucasprioste.weatherapp.domain.repository.SessionRepository
import com.lucasprioste.weatherapp.domain.repository.WeatherRepository
import com.lucasprioste.weatherapp.utils.ApiKeyInterceptor
import com.lucasprioste.weatherapp.utils.BASE_URL
import com.lucasprioste.weatherapp.utils.ConnectionLiveData
import com.lucasprioste.weatherapp.utils.location.LocationClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideWeatherApi(): WeatherApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun providePreferences(app: Application): Preferences {
        return Preferences.getInstance(app)
    }

    @Singleton
    @Provides
    fun provideSessionRepository(): SessionRepository {
        return SessionRepositoryImp()
    }

    @Singleton
    @Provides
    fun provideConnection(app: Application): ConnectionLiveData {
        return ConnectionLiveData(app)
    }

    @Singleton
    @Provides
    fun provideLocationService(app: Application) : LocationClient {
        return DefaultLocationClient(app, LocationServices.getFusedLocationProviderClient(app))
    }

    @Singleton
    @Provides
    fun providesWeatherDatabase(app: Application, preferences: Preferences): WeatherDataBase {
        return Room.databaseBuilder(
            app,
            WeatherDataBase::class.java,
            "weatherDB.db"
        )
            .addCallback(object : RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    preferences.changePopulateDb(true)
                }
            })
            .build()
    }



    @Singleton
    @Provides
    fun provideWeatherRepository(
        api: WeatherApi,
        db: WeatherDataBase,
        preferences: Preferences
    ) : WeatherRepository {
        return WeatherRepositoryImp(api = api, db = db, preferences = preferences)
    }
}