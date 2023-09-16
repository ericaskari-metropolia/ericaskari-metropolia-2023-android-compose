package com.ericaskari.w3d5beacon.datasource

import androidx.room.TypeConverter
import java.util.Date


/**
 * @author Mohammad Askari
 * Convert Dates to string unix when saving to db and convert to Date when retrieving.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        if (value == null) {
            return null
        }

        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}