package com.example.deliveryappproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="Commandes")
data class Commande (

    @PrimaryKey
    var NumCmd: Int,
    var NomClient: String,
    var AdresseClient: String,
    var NumTelClient: String,
    var EmailClient: String,
    var PrixCmd: Int,
    var Delivered: String?,
    var DateRecup: String?
)