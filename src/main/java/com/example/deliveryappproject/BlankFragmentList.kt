package com.example.deliveryappproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.Tasks.await
import kotlinx.android.synthetic.main.fragment_blank_list.*
import org.jetbrains.anko.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.BASIC_ISO_DATE


/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragmentList.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragmentList : Fragment(){


    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun getLastLocation(): LatLng {

        var currentLocation = LatLng(36.77332160791047,3.058955043029448)
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                val task = mFusedLocationClient.lastLocation
                task.addOnSuccessListener { location ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)

                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

        return currentLocation
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }



    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun onResume(_listCmd:List<Commande>, _listCmdRecup:List<Commande>,currentLocation:LatLng) {
        super.onResume()



        val pref = requireActivity().getSharedPreferences("LOGdelivery",
            Context.MODE_PRIVATE)

        val userID: Int = pref.getInt("UserID", 0)
        var latLngOrigin = LatLng(currentLocation.latitude, currentLocation.longitude)


        val myRCmdRecyclerViewAdapter: MyRCmdRecyclerViewAdapter
        val myRecyclerViewAdapter: MyRecyclerViewAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        myRecyclerViewAdapter = MyRecyclerViewAdapter(requireActivity(), _listCmd,userID,_listCmdRecup,this,latLngOrigin)
        recyclerView.adapter = myRecyclerViewAdapter

        var totalEncaiss = 0
        var nbLivRetard = 0
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var period = Period.of(0, 0, 2)
        val todayStr = current.format(formatter)
        val today = LocalDate.parse(todayStr,formatter)

        if (_listCmd.size > 0) {
            for (i in 0.._listCmd.size - 1) {

                if (_listCmd[i].Delivered == "1") {
                    totalEncaiss = totalEncaiss + _listCmd[i].PrixCmd
                }
                val date = LocalDate.parse(_listCmd[i].DateRecup, formatter)
                var modifiedDate = date.plus(period)

                if (today > modifiedDate )  {

                    if (_listCmd[i].Delivered == "0") {
                        nbLivRetard = nbLivRetard + 1
                    }
                }

            }
            textViewLivRetard.text = nbLivRetard.toString()
            textViewNbLiv.text = _listCmd.size.toString()
            textViewTtlEncaiss.text = separ1000(totalEncaiss.toLong())
        }

        recyclerViewCmdRecup.layoutManager = LinearLayoutManager(requireActivity())
        myRCmdRecyclerViewAdapter = MyRCmdRecyclerViewAdapter(requireActivity(), _listCmdRecup, userID, _listCmd,this,latLngOrigin)
        recyclerViewCmdRecup.adapter = myRCmdRecyclerViewAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_list, container, false)
    }



    var listCmd:List<Commande> = listOf()
    var listCmdRecup:List<Commande> = listOf()



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var currentLocation = LatLng(36.77332160791047,3.058955043029448)

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                val task = mFusedLocationClient.lastLocation
                task.addOnSuccessListener { location ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)

                        // using google.maps.LatLng class


                        val pref = requireActivity().getSharedPreferences(
                            "LOGdelivery",
                            Context.MODE_PRIVATE
                        )

                        val userID: Int = pref.getInt("UserID", 0)
                        val username: String = pref.getString("UserName", "NomUser")!!
                        val con = pref.getBoolean("Connected", false)
                        var latLngOrigin = LatLng(currentLocation.latitude, currentLocation.longitude)


                        if (con) {

                            textViewNomUser.text = username.toUpperCase()

                            imageView2.layoutParams.width = convertDpToPixelInt(270f, requireContext())

                            var totalEncaiss = 0
                            var nbLivRetardz = 0
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            var period = Period.of(0, 0, 2)
                            val todayStr = current.format(formatter)
                            val today = LocalDate.parse(todayStr, formatter)

                            if (listCmd.size > 0) {
                                for (i in 0..listCmd.size - 1) {
                                    if (listCmd[i].Delivered == "1") {
                                        totalEncaiss = totalEncaiss + listCmd[i].PrixCmd
                                    }
                                    val date = LocalDate.parse(listCmd[i].DateRecup, formatter)
                                    var modifiedDate = date.plus(period)

                                    if (today > modifiedDate) {
                                        if (listCmd[i].Delivered == "0") {
                                            nbLivRetardz = nbLivRetardz + 1
                                        }
                                    }

                                }
                                textViewLivRetard.text = nbLivRetardz.toString()
                                textViewNbLiv.text = listCmd.size.toString()
                                textViewTtlEncaiss.text = separ1000(totalEncaiss.toLong())
                            }
                            progressBar2.visibility = View.VISIBLE

                            var myRCmdRecyclerViewAdapter =
                                MyRCmdRecyclerViewAdapter(
                                    requireActivity(),
                                    listCmdRecup,
                                    userID,
                                    listCmd,
                                    this@BlankFragmentList,
                                    latLngOrigin
                                )
                            var myRecyclerViewAdapter =
                                MyRecyclerViewAdapter(
                                    requireActivity(),
                                    listCmd,
                                    userID,
                                    listCmdRecup,
                                    this@BlankFragmentList,
                                    latLngOrigin
                                )

                            val call = RetrofitService.endpoint.getCommandsFromUserByID(userID)
                            call.enqueue(object : Callback<List<Commande>> {
                                override fun onResponse(
                                    call: Call<List<Commande>>?, response:
                                    Response<List<Commande>>?
                                ) {
                                    progressBar2.visibility = View.INVISIBLE
                                    if (response?.isSuccessful!!) {
                                        val data: List<Commande>? = response.body()
                                        if (data != null) {
                                            if (data.size > 0) {
                                                listCmd = data
                                                recyclerView.layoutManager =
                                                    LinearLayoutManager(requireActivity())
                                                myRecyclerViewAdapter = MyRecyclerViewAdapter(
                                                    requireActivity(),
                                                    listCmd,
                                                    userID,
                                                    listCmdRecup,
                                                    this@BlankFragmentList, latLngOrigin
                                                )
                                                recyclerView.adapter = myRecyclerViewAdapter

                                                var totalEncaiss = 0
                                                var nbLivRetard = 0
                                                val current = LocalDateTime.now()
                                                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                                var period = Period.of(0, 0, 2)
                                                val todayStr = current.format(formatter)
                                                val today = LocalDate.parse(todayStr, formatter)

                                                if (listCmd.size > 0) {
                                                    for (i in 0..listCmd.size - 1) {
                                                        if (listCmd[i].Delivered == "1") {
                                                            totalEncaiss = totalEncaiss + listCmd[i].PrixCmd
                                                        }
                                                        val date =
                                                            LocalDate.parse(listCmd[i].DateRecup, formatter)
                                                        var modifiedDate = date.plus(period)
                                                        if (today > modifiedDate && listCmd[i].Delivered != "1") {
                                                            nbLivRetard = nbLivRetard + 1
                                                        }
                                                    }

                                                    textViewLivRetard.text = nbLivRetard.toString()
                                                    textViewNbLiv.text = listCmd.size.toString()
                                                    textViewTtlEncaiss.text = separ1000(totalEncaiss.toLong())
                                                }

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No Command found",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // erreur dans la réponse
                                    }
                                }

                                override fun onFailure(call: Call<List<Commande>>?, t: Throwable?) {

                                    progressBar2.visibility = View.INVISIBLE
                                    // Pour le débogage
                                    Log.e("erreur retrofit", t.toString())
                                    // Un toast pour l'utilisateur
                                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })


                            progressBar2.visibility = View.VISIBLE

                            val call2 = RetrofitService.endpoint.getCommandsFromUserByID(0)
                            call2.enqueue(object : Callback<List<Commande>> {
                                override fun onResponse(
                                    call: Call<List<Commande>>?, response:
                                    Response<List<Commande>>?
                                ) {
                                    progressBar2.visibility = View.INVISIBLE
                                    if (response?.isSuccessful!!) {
                                        val data: List<Commande>? = response.body()
                                        if (data != null) {
                                            if (data.size > 0) {
                                                listCmdRecup = data
                                                recyclerViewCmdRecup.layoutManager =
                                                    LinearLayoutManager(requireActivity())
                                                myRCmdRecyclerViewAdapter = MyRCmdRecyclerViewAdapter(
                                                    requireActivity(),
                                                    listCmdRecup,
                                                    userID,
                                                    listCmd,
                                                    this@BlankFragmentList, latLngOrigin
                                                )
                                                recyclerViewCmdRecup.adapter = myRCmdRecyclerViewAdapter
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "No Command found",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // erreur dans la réponse
                                    }
                                }

                                override fun onFailure(call: Call<List<Commande>>?, t: Throwable?) {

                                    progressBar2.visibility = View.INVISIBLE
                                    // Pour le débogage
                                    Log.e("erreur retrofit", t.toString())
                                    // Un toast pour l'utilisateur
                                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })
                        }


                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }
}

fun separ1000(nb:Long?):String {

    var formatter: NumberFormat = NumberFormat.getNumberInstance()
    formatter.setGroupingUsed(true)
    var nbstr:String = formatter.format(nb)
    var crctInvalid:Int = 160
    var space:Char = ' '
    var virgule:Char = ','
    var c:Char = crctInvalid.toChar()
    nbstr = nbstr.replace(c,space)
    nbstr = nbstr.replace(virgule,space)
    return nbstr
}

fun convertDpToPixelInt(dp: Float, context: Context): Int {
    return (dp * (context.resources
        .displayMetrics.densityDpi.toFloat() / 160.0f)).toInt()
}

