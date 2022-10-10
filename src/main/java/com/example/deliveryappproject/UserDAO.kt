package com.example.deliveryappproject

import androidx.room.*

@Dao
interface UserDAO {

    //@Insert
    //fun addActor(actor: Actor)

    //@Delete
    //fun deleteActor(actor: Actor)

    //@Update
    //fun updateActor(actor: Actor)

    //@Query("SELECT * FROM Actors WHERE FirstName = :ActorFirstName")
    //fun getActorByFName(ActorFirstName: String):Actor

    //@Query("SELECT * FROM Actors WHERE id = :ActorID")
    //fun getActorByID(ActorID: Long):Actor

    @Query("SELECT * FROM Users WHERE Username = :UserUsername AND Password = :UserPassword ")
    fun getUser(UserUsername: String, UserPassword: String):User
}