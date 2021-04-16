package com.stupidtree.stupiduser.data.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stupidtree.stupiduser.data.model.service.UserProfile

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM profile WHERE id IS :userId")
    fun queryProfile(userId:String):LiveData<UserProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProfiles(data: List<UserProfile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProfile(data: UserProfile)


    @Query("DELETE FROM profile")
    fun clearTable()

}