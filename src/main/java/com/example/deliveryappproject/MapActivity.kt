package com.example.deliveryappproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@MapActivity)
        fetchLocation()

        imageViewBackBtn.setOnClickListener(){
            onBackPressed()
        }


    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origins=" + from.latitude + "," + from.longitude
        val dest = "destinations=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val departure_time = "departure_time=now"
        val key = R.string.DISTANCE_MATRIX_API_TOKEN
        val params = "$origin&$dest&$departure_time&$key"
        //return "https://maps.googleapis.com/maps/api/directions/json?$params"
        return "https://api.distancematrix.ai/maps/api/distancematrix/json?$params"
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                //Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" + currentLocation.longitude, Toast.LENGTH_SHORT).show()
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.getUiSettings()?.setZoomControlsEnabled(true)
        mMap?.setOnMarkerClickListener(this)

        val LatLongB = LatLngBounds.Builder()

        val zoomLevel = 10f
        val destination = intent.getStringExtra("DestAddress")


        val latLngOrigin = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptionsOrigin = MarkerOptions().position(latLngOrigin).title("Ma position").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_local_map))



        mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLngOrigin))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, zoomLevel))
        mMap?.addMarker(markerOptionsOrigin)

        var latitude = 36.77332160791047
        var longitude = 3.058955043029448

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocationName(destination, 1)
        val address: Address = addresses[0]
        if (addresses.size > 0) {
            latitude = addresses[0].getLatitude()
            longitude = addresses[0].getLongitude()
        }
        var latLngDestination = LatLng(36.77332160791047, 3.058955043029448)
        if (destination == null) {
            latLngDestination = LatLng(36.77332160791047, 3.058955043029448) // Alger
            mMap!!.addMarker(MarkerOptions().position(latLngDestination).title("Alger Centre"))
        }
        else {
            latLngDestination = LatLng(latitude,longitude)
            mMap!!.addMarker(MarkerOptions().position(latLngDestination).title("Destination").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_dest)))
        }


        // Declare polyline object and set up color and width
        /**val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        // build URL to call API
        val url = getURL(latLngOrigin, latLngDestination)

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
                // Add  points to polyline and bounds
                options.add(latLngOrigin)
                LatLongB.include(latLngOrigin)
                for (point in polypts)  {
                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(latLngDestination)
                LatLongB.include(latLngDestination)
                // build bounds
                val bounds = LatLongB.build()
                // add polyline to the map
                mMap!!.addPolyline(options)
                // show map with route centered
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }



        }*/

    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }
}