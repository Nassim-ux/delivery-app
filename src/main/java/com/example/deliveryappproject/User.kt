package com.example.deliveryappproject

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity(tableName ="Users")
data class User (

    @PrimaryKey
    var ID: Int?,
    var Username: String,
    var Password: String
)

