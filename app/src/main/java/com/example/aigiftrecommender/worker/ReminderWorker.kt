package com.example.aigiftrecommender.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aigiftrecommender.repository.ContactRepository
import com.example.aigiftrecommender.repository.HolidayRepository
import com.example.aigiftrecommender.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val contactRepository: ContactRepository,
    private val holidayRepository: HolidayRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "GiftReminderWorker"
        private const val NOTIFICATION_LEAD_DAYS = 14
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            NotificationHelper.createNotificationChannel(applicationContext)

            checkUpcomingBirthdays()
            checkUpcomingHolidays()

            Result.success()
        } catch (e: Exception) {
            // Log error e.g., Timber.e(e, "Error in ReminderWorker")
            Result.failure()
        }
    }

    private suspend fun checkUpcomingBirthdays() {
        val contacts = contactRepository.getAllContacts() // This fetches basic info
        val today = Calendar.getInstance()

        for (contact in contacts) {
            val detailedContact = contactRepository.getContactDetails(contact.id) // Fetch full details
            detailedContact?.birthday?.let { birthdayString ->
                parseDate(birthdayString)?.let { birthdayCal ->
                    // Set year to current year for comparison, then add lead days
                    birthdayCal.set(Calendar.YEAR, today.get(Calendar.YEAR))
                    
                    val reminderDate = Calendar.getInstance().apply {
                        time = birthdayCal.time
                        add(Calendar.DAY_OF_YEAR, -NOTIFICATION_LEAD_DAYS)
                    }

                    if (isSameDay(reminderDate, today)) {
                        val notificationId = ("birthday_${contact.id}").hashCode()
                        NotificationHelper.sendNotification(
                            applicationContext,
                            notificationId,
                            "Birthday Reminder!",
                            "${contact.name}"s birthday is in $NOTIFICATION_LEAD_DAYS days!",
                            contactId = contact.id,
                            eventId = "birthday"
                        )
                    }
                }
            }
        }
    }

    private suspend fun checkUpcomingHolidays() {
        holidayRepository.getSelectedHolidays().collect { holidays ->
            val today = Calendar.getInstance()
            for (holiday in holidays) {
                parseDate(holiday.date)?.let { holidayCal ->
                    // Set year to current year for comparison
                    holidayCal.set(Calendar.YEAR, today.get(Calendar.YEAR))

                    val reminderDate = Calendar.getInstance().apply{
                        time = holidayCal.time
                        add(Calendar.DAY_OF_YEAR, -NOTIFICATION_LEAD_DAYS)
                    }

                    if (isSameDay(reminderDate, today)) {
                        val notificationId = ("holiday_${holiday.name}").hashCode()
                        NotificationHelper.sendNotification(
                            applicationContext,
                            notificationId,
                            "Holiday Reminder!",
                            "${holiday.name} is in $NOTIFICATION_LEAD_DAYS days!",
                            eventId = holiday.name
                        )
                    }
                }
            }
        }
    }

    private fun parseDate(dateStr: String): Calendar? {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("--MM-dd", Locale.US), // For birthdays without year
            SimpleDateFormat("MM-dd", Locale.US) // For holidays without year
        )
        for (format in formats) {
            try {
                val date = format.parse(dateStr)
                if (date != null) {
                    val cal = Calendar.getInstance()
                    cal.time = date
                    // If year is 1970 (default for MM-dd parsing), it means no year was specified.
                    // We will set current year later for comparison.
                    return cal
                }
            } catch (e: Exception) {
                // Try next format
            }
        }
        return null
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}

