package com.example.aigiftrecommender.repository

import com.example.aigiftrecommender.data.local.HolidayDao
import com.example.aigiftrecommender.model.Holiday
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface HolidayRepository {
    fun getAllHolidays(): Flow<List<Holiday>>
    fun getSelectedHolidays(): Flow<List<Holiday>>
    suspend fun updateHoliday(holiday: Holiday)
    suspend fun addHoliday(holiday: Holiday)
    suspend fun prePopulateHolidaysIfNeeded()
}

@Singleton
class HolidayRepositoryImpl @Inject constructor(
    private val holidayDao: HolidayDao
) : HolidayRepository {

    override fun getAllHolidays(): Flow<List<Holiday>> = holidayDao.getAllHolidays()

    override fun getSelectedHolidays(): Flow<List<Holiday>> = holidayDao.getSelectedHolidays()

    override suspend fun updateHoliday(holiday: Holiday) {
        holidayDao.updateHoliday(holiday)
    }

    override suspend fun addHoliday(holiday: Holiday) {
        holidayDao.insertHoliday(holiday)
    }

    override suspend fun prePopulateHolidaysIfNeeded() {
        // Check if holidays are already populated
        val firstHoliday = holidayDao.getHolidayByName("New Year's Day") // Check for a common holiday
        if (firstHoliday == null) {
            val commonHolidays = listOf(
                Holiday(name = "New Year's Day", date = "01-01", isSelected = true),
                Holiday(name = "Valentine's Day", date = "02-14", isSelected = true),
                Holiday(name = "St. Patrick's Day", date = "03-17", isSelected = false),
                Holiday(name = "Easter Sunday", date = "", isSelected = true), // Date varies, user might need to set this or app logic to calculate
                Holiday(name = "Mother's Day", date = "", isSelected = true), // Date varies (e.g., Second Sunday in May for US)
                Holiday(name = "Father's Day", date = "", isSelected = true), // Date varies (e.g., Third Sunday in June for US)
                Holiday(name = "Independence Day (US)", date = "07-04", isSelected = false),
                Holiday(name = "Halloween", date = "10-31", isSelected = true),
                Holiday(name = "Thanksgiving (US)", date = "", isSelected = true), // Date varies (Fourth Thursday in November for US)
                Holiday(name = "Christmas Eve", date = "12-24", isSelected = false),
                Holiday(name = "Christmas Day", date = "12-25", isSelected = true),
                Holiday(name = "New Year's Eve", date = "12-31", isSelected = false)
            )
            holidayDao.insertAllHolidays(commonHolidays)
        }
    }
}

