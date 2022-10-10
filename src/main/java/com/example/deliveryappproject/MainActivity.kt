package com.example.deliveryappproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.deliveryappproject.RoomService.context
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_blank_list.*
import org.jetbrains.anko.async

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val pref = getSharedPreferences("LOGdelivery",
            Context.MODE_PRIVATE)


        val con = pref.getBoolean("Connected", false)

        if (!con) {
            val intent =
                Intent(this@MainActivity, LoginActivity::class.java)

            this@MainActivity.startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Disabled Back Press", Toast.LENGTH_SHORT).show()
    }
}