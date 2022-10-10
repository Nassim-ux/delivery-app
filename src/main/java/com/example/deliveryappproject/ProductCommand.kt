package com.example.deliveryappproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ProductsCommands",
    primaryKeys = ["CommandID","ProductID"]/*,
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
data class ProductCommand (

    var CommandID: Int,
    var ProductID: Int,
    var ProductQte: Int
)