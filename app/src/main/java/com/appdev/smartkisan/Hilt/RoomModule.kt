package com.appdev.smartkisan.Hilt

import android.content.Context
import androidx.room.Room
import com.appdev.smartkisan.data.local.db.Dao.UserInfoDao
import com.appdev.smartkisan.data.local.db.RoomDB
import com.appdev.smartkisan.Utils.Constants.Companion.DatabaseName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Provides
    @Singleton
    fun roomBuilder(@ApplicationContext context: Context): RoomDB {
        return Room.databaseBuilder(context, RoomDB::class.java, DatabaseName)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDatabaseTheDaoClass(roomDatabase: RoomDB): UserInfoDao {
        return roomDatabase.userInfoDao()
    }

}