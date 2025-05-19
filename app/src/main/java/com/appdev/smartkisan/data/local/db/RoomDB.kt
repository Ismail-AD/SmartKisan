package com.appdev.smartkisan.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appdev.smartkisan.data.local.db.Dao.UserInfoDao
import com.appdev.smartkisan.domain.model.UserInfo

@Database(
    entities = [UserInfo::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
}