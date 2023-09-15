package com.ericaskari.w3d5retrofit.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo var year: String,
    @ColumnInfo val director: String,
) {

}

