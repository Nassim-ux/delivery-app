package com.example.deliveryappproject

import android.content.Context
import androidx.room.Room

object RoomService {

    lateinit var context: Context

    val appDatabase:AppDatabase by lazy {
        Room.databaseBuilder(context,AppDatabase::class.java, "deliveryappprojectdb").allowMainThreadQueries().build()
    }

}