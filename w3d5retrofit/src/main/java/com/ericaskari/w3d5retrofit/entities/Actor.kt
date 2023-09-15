package com.ericaskari.w3d5retrofit.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Actor(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val movieId: String,
    @ColumnInfo val role: String,
) {

}

