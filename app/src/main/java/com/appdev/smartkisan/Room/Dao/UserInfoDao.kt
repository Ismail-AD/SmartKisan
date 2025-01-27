package com.appdev.smartkisan.Room.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appdev.smartkisan.data.UserInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM userInfo_table")
    fun getUserInfo(): Flow<UserInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInfo)

    @Query("DELETE FROM userInfo_table WHERE userId = :id")
    suspend fun deleteUserInfoById(id: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserInfo(userInfo: UserInfo)
}