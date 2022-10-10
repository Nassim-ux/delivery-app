package com.example.deliveryappproject

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoint {
    // Call<List<Movie>: une fonction callback qui retourne une liste
    //@GET("getMovies")
    //fun getMovies(): Call<List<Movie>>

    // Call<List<Actor>: une fonction callback qui retourne une liste
    //@GET("getActors")
    //fun getActors(): Call<List<Actor>>

    // Envoi d'un paramètre name
    //@GET("getMovies/{title}")
    //fun detailMovie(@Path("title") title:String):Call<Movie>

    //envoi d'un parametre titre du film et recevoir la liste des acteurs du film
    //@GET("getActorsFromMovie/{title}")
    //fun getActorsFromMovie(@Path("title") title:String):Call<List<Actor>>

    // Envoi d'un paramètre actor dans le message body
    //@POST("addActor")
    //fun addActor(@Body actor: Actor):Call<String>

    @POST("getUser")
    fun getUser(@Body user: User):Call<List<User>>

    @POST("updtCmd")
    fun updtCmd(@Body user: UserCommand):Call<String>

    @POST("updtCmdDelivered")
    fun updtCmdDelivered(@Body user: UserCommand):Call<String>

    @POST("getUsername")
    fun getUsername(@Body user: User):Call<List<User>>

    @GET("getCommands")
    fun getCommands(): Call<List<Commande>>

    @GET("getProductsFromCommandByNum/{NumCmd}")
    fun getProductsFromCommandByNum(@Path("NumCmd") NumCmd:Int): Call<List<Product>>

    @GET("getCommandsFromUserByID/{ID}")
    fun getCommandsFromUserByID(@Path("ID") ID:Int): Call<List<Commande>>
}