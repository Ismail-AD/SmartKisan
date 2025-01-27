package com.appdev.smartkisan.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appdev.smartkisan.Room.Dao.UserInfoDao
import com.appdev.smartkisan.data.UserInfo

@Database(
    entities = [UserInfo::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
}