package com.example.aigiftrecommender.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aigiftrecommender.model.Holiday
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holiday: Holiday)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHolidays(holidays: List<Holiday>)

    @Update
    suspend fun updateHoliday(holiday: Holiday)

    @Query("SELECT * FROM holidays")
    fun getAllHolidays(): Flow<List<Holiday>>

    @Query("SELECT * FROM holidays WHERE isSelected = 1")
    fun getSelectedHolidays(): Flow<List<Holiday>>

    @Query("SELECT * FROM holidays WHERE name = :name")
    suspend fun getHolidayByName(name: String): Holiday?
}

