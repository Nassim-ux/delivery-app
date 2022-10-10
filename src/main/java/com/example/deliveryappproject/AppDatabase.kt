package com.example.deliveryappproject

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class,Commande::class,Product::class,ProductCommand::class),version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getUserDao():UserDAO

}