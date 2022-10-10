package com.example.deliveryappproject

import androidx.room.Entity

@Entity(tableName = "UsersCommands",
    primaryKeys = ["CommandID","UserID"]/*,
        foreignKeys = arrayOf(
            ForeignKey(entity =
                Movie::class, parentColumns = arrayOf("id"),
                childColumns = arrayOf("idMovie"),
                onDelete = ForeignKey.CASCADE),
            ForeignKey(entity =
                Actor::class, parentColumns = arrayOf("id"),
                childColumns = arrayOf("idActor"),
                onDelete = ForeignKey.CASCADE)
        )*/

)
data class UserCommand (

    var CommandID: Int,
    var UserID: Int,
    var Delivered: String,
    var DateRecup: String?
)