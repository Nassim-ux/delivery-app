package com.example.deliveryappproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        buttonLogin.setOnClickListener() {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            if (username != "" && password != "") {
                progressBar.visibility = View.VISIBLE

                var user: User = User(0,username,password)

                val call = RetrofitService.endpoint.getUsername(user)
                call.enqueue( object: Callback<List<User>> {
                    override fun onResponse(call: Call<List<User>>?, response:
                    Response<List<User>>?) {
                        progressBar.visibility = View.INVISIBLE
                        if(response?.isSuccessful!!) {
                            val data:List<User>? = response.body()
                            if (data != null) {
                                if (data.size > 0) {

                                    val call2 = RetrofitService.endpoint.getUser(user)
                                    call2.enqueue( object: Callback<List<User>> {
                                        override fun onResponse(call: Call<List<User>>?, response:
                                        Response<List<User>>?) {
                                            progressBar.visibility = View.INVISIBLE
                                            if(response?.isSuccessful!!) {
                                                val data:List<User>? = response.body()
                                                if (data != null) {
                                                    if (data.size > 0) {

                                                        val pref = getSharedPreferences("LOGdelivery", Context.MODE_PRIVATE)

                                                        pref.edit {
                                                            putBoolean("Connected",true)
                                                            putString("UserName",data[0].Username)
                                                            putInt("UserID", data[0].ID!!)
                                                        }

                                                        val intent =
                                                            Intent(this@LoginActivity, MainActivity::class.java)
                                                        intent.putExtra("userID",data[0].ID)
                                                        intent.putExtra("UserName", data[0].Username)
                                                        this@LoginActivity.startActivity(intent)
                                                    } else {
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Password invalid, LOGIN failed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                            else {
                                                // erreur dans la réponse
                                            }
                                        }
                                        override fun onFailure(call: Call<List<User>>?, t: Throwable?) {

                                            progressBar.visibility = View.INVISIBLE
                                            // Pour le débogage
                                            Log.e("erreur retrofit", t. toString())
                                            // Un toast pour l'utilisateur
                                            Toast.makeText(this@LoginActivity, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()

                                        }
                                    })

                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Username invalid, LOGIN failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        else {
                            // erreur dans la réponse
                        }
                    }
                    override fun onFailure(call: Call<List<User>>?, t: Throwable?) {

                        progressBar.visibility = View.INVISIBLE
                        // Pour le débogage
                        Log.e("erreur retrofit", t. toString())
                        // Un toast pour l'utilisateur
                        Toast.makeText(this@LoginActivity, "Une erreur s'est produite lors de la verification du username", Toast.LENGTH_SHORT).show()

                    }
                })
            }
            else {
                Toast.makeText(this@LoginActivity, "Veuillez saisir tous les champs !", Toast.LENGTH_SHORT).show()
            }
        }

        imageViewShowHidePwd.setOnClickListener(){
            if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                imageViewShowHidePwd.setImageResource(R.drawable.ic_invisible)
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            }
            else {
                imageViewShowHidePwd.setImageResource(R.drawable.ic_eye)
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
    }






}

