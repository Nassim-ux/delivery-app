package com.example.deliveryappproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.green
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.Settings.Global.getString
import android.provider.Settings.System.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.Tasks.await
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsLeg
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.Duration
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.fragment_blank_list.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.BASIC_ISO_DATE
import java.util.*

class MyRCmdRecyclerViewAdapter(val context: Context, var data:List<Commande>, var userID:Int, var listCmd:List<Commande>, var activity: BlankFragmentList, var latLngOrigin: LatLng):RecyclerView.Adapter<MyRCmdViewHolder>()
{


    private val DISTANCE_MATRIX_API_TOKEN = "xI6Rm2XYQeCXKwhOQg8zV85yy9rcM"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRCmdViewHolder {
        return MyRCmdViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_newcmd, parent, false))

    }

    // - Put your api key (https://developers.google.com/maps/documentation/directions/get-api-key) here:
    //private val API_KEY = "AIzaSyBeMbzfp9dADxuTLLe_UgwgwnXjdU1Abq0"



    override fun getItemCount() = data.size



    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origins=" + from.latitude + "," + from.longitude
        val dest = "destinations=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val departure_time = "departure_time=now"
        val key = "key="+DISTANCE_MATRIX_API_TOKEN
        val params = "$origin&$dest&$departure_time&$key"
        //return "https://maps.googleapis.com/maps/api/directions/json?$params"
        return "https://api.distancematrix.ai/maps/api/distancematrix/json?$params"
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyRCmdViewHolder, position: Int) {

        val cmd = data[position]

        val destination = cmd.AdresseClient

        var latitude = 36.77332160791047
        var longitude = 3.058955043029448
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocationName(destination, 1)
        if (addresses.size > 0) {
            latitude = addresses[0].getLatitude()
            longitude = addresses[0].getLongitude()
        }

        val latLngDestination = LatLng(latitude, longitude)





        val url = getURL(latLngOrigin,latLngDestination)

        async {
            val result = URL(url).readText()
            // - Perform the actual request
            uiThread {
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                // - Parse the result
                val rows = json.array<JsonObject>("rows")
                val elements = rows!![0].array<JsonObject>("elements")
                val distance: JsonObject = elements!![0].get("distance") as JsonObject
                val duration: JsonObject = elements!![0].get("duration") as JsonObject


                val distance_text = distance.get("text") as String
                val duration_text = duration.get("text") as String

                holder._textViewETA1.text = duration_text
                holder._textViewDist1.text = distance_text

            }
        }


        holder._textViewCmdNum1.text = cmd.NumCmd.toString()
        holder._textViewPrice1.text = separ1000(cmd.PrixCmd.toLong())


        holder._imageViewAdd1.setOnClickListener(){

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val today = current.format(formatter)

            val usercmd = UserCommand(cmd.NumCmd,userID,"0",today)
            val call = RetrofitService.endpoint.updtCmd(usercmd)
            call.enqueue( object: Callback<String> {
                override fun onResponse(call: Call<String>?, response:
                Response<String>?) {
                    if(response?.isSuccessful!!) {
                        val data1:String? = response.body()
                        if (data1 != null) {

                            var listCmdRecup:List<Commande> = listOf()
                            val call1 = RetrofitService.endpoint.getCommandsFromUserByID(0)
                            call1.enqueue( object: Callback<List<Commande>> {
                                override fun onResponse(call: Call<List<Commande>>?, response:
                                Response<List<Commande>>?) {
                                    if(response?.isSuccessful!!) {
                                        val data2:List<Commande>? = response.body()
                                        if (data2 != null) {
                                            listCmdRecup = data2
                                            val call2 = RetrofitService.endpoint.getCommandsFromUserByID(userID)
                                            call2.enqueue( object: Callback<List<Commande>> {
                                                override fun onResponse(call: Call<List<Commande>>?, response:
                                                Response<List<Commande>>?) {
                                                    if(response?.isSuccessful!!) {
                                                        val data2:List<Commande>? = response.body()
                                                        if (data2 != null) {
                                                            if (data.size > 0) {
                                                                listCmd = data2
                                                                activity.onResume(listCmd,listCmdRecup,latLngOrigin)
                                                            } else {

                                                                //holder.itemView.visibility = View.INVISIBLE
                                                                activity.onResume(listCmd,listCmdRecup,latLngOrigin)
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        // erreur dans la réponse
                                                    }
                                                }
                                                override fun onFailure(call: Call<List<Commande>>?, t: Throwable?) {

                                                    // Pour le débogage
                                                    Log.e("erreur retrofit", t. toString())
                                                    // Un toast pour l'utilisateur
                                                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()

                                                }
                                            })

                                        }
                                    }
                                    else {
                                        // erreur dans la réponse
                                    }
                                }
                                override fun onFailure(call: Call<List<Commande>>?, t: Throwable?) {

                                    // Pour le débogage
                                    Log.e("erreur retrofit", t. toString())
                                    // Un toast pour l'utilisateur
                                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()

                                }
                            })



                        }
                    }
                    else {
                        // erreur dans la réponse
                    }
                }
                override fun onFailure(call: Call<String>?, t: Throwable?) {

                    // Pour le débogage
                    Log.e("erreur retrofit", t. toString())
                    // Un toast pour l'utilisateur
                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()

                }
            })

        }




        holder.itemView.setOnClickListener { view ->


            val bundle = bundleOf("CommandeNum" to data[position].NumCmd,"NomClient" to data[position].NomClient,"NumTelClient" to data[position].NumTelClient,"AdresseClient" to data[position].AdresseClient,"CommandePrix" to data[position].PrixCmd,"canValidate" to "0")

            view.findNavController().navigate(R.id.action_blankFragmentList_to_blankFragmentDetail,bundle)

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

}

class MyRCmdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val _imageViewAdd1 = view.findViewById(R.id.imageViewAdd1) as ImageView
    val _textViewCmdNum1 = view.findViewById(R.id.textViewCmdNum1) as TextView
    val _textViewPrice1 = view.findViewById(R.id.textViewPrice1) as TextView
    val _textViewETA1 = view.findViewById(R.id.textViewETA1) as TextView
    val _textViewDist1 = view.findViewById(R.id.textViewDist1) as TextView
}