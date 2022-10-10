package com.example.deliveryappproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="Products")
data class Product (

    @PrimaryKey
    var ID: Int?,
    var Nom: String,
    var Description: String,
    var StockQte: Int,
    var PrixProduct: Int,
    var ProductQte: Int?
)