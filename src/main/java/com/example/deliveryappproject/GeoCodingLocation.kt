package com.example.deliveryappproject

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.*
class GeoCodingLocation {
    private val TAG = "GeoCodeLocation"
    fun getAddressFromLocation(
        locationAddress: String,
        context: Context, handler: Handler
    ) {
        val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var resultLat: String? = null
                var resultLng: String? = null
                try {
                    val addressList = geoCoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList.get(0) as Address
                        resultLat = address.latitude.toString()
                        resultLng = address.longitude.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable to connect to GeoCoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    bundle.putString("addressLat", resultLat)
                    bundle.putString("addressLng", resultLng)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }
}