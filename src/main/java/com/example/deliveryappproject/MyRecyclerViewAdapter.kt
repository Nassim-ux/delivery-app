package com.example.deliveryappproject

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.Path
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.Settings.System.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_blank_list.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class MyRecyclerViewAdapter(val context: Context, var data:List<Commande>, var userID:Int, var listCmdRecup:List<Commande>, var activity: BlankFragmentList, var latLngOrigin: LatLng):RecyclerView.Adapter<MyViewHolder>()
{

    private val DISTANCE_MATRIX_API_TOKEN = "xI6Rm2XYQeCXKwhOQg8zV85yy9rcM"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_cmd, parent, false))

    }

    override fun getItemCount() = data.size

    // - Put your api key (https://developers.google.com/maps/documentation/directions/get-api-key) here:
    //private val API_KEY = "AIzaSyBeMbzfp9dADxuTLLe_UgwgwnXjdU1Abq0"



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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

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


                holder._textViewETA.text = duration_text
                holder._textViewDist.text = distance_text

            }
        }




        holder._imageViewRetard.visibility = View.INVISIBLE
        holder._textViewCmdNum.text = cmd.NumCmd.toString()
        holder._textViewPrice.text = separ1000(cmd.PrixCmd.toLong())

        var livree = false

        if (cmd.Delivered == "1") {
            livree = true
        }
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var period = Period.of(0, 0, 2)
        val todayStr = current.format(formatter)
        val today = LocalDate.parse(todayStr,formatter)


        val date = LocalDate.parse(cmd.DateRecup,formatter)
        var modifiedDate = date.plus(period)
        if (today > modifiedDate && !livree) {
            holder._imageViewRetard.visibility = View.VISIBLE
            holder._textViewRetard.visibility = View.VISIBLE
            Log.w("pppppppppppppp","$modifiedDate , $today, ${cmd.Delivered}")

        }


        if (livree) {
            holder._imageViewDelivered.setImageResource(R.drawable.checked)
            holder._textViewDelivered.text = "Livrée"
            holder._textViewDelivered.setTextColor(Color.parseColor("#16DA09"))
            holder._imageViewCancel.visibility = View.INVISIBLE
            holder._textViewRetard.visibility =View.INVISIBLE
            holder._imageViewRetard.visibility = View.INVISIBLE
        }
        else {
            holder._imageViewDelivered.setImageResource(R.drawable.stopwatch)
            holder._textViewDelivered.text = "à Livrer"
            holder._textViewDelivered.setTextColor(Color.parseColor("#FF5722"))
        }




        holder._imageViewCancel.setOnClickListener() {
            val usercmd = UserCommand(cmd.NumCmd,0,"0",null)
            val call = RetrofitService.endpoint.updtCmd(usercmd)
            call.enqueue( object: Callback<String> {
                override fun onResponse(call: Call<String>?, response:
                Response<String>?) {
                    if(response?.isSuccessful!!) {
                        val data1: String? = response.body()
                        if (data1 != null) {
                            val data1: String? = response?.body()
                            var listCmd: List<Commande> = listOf()
                            val call1 = RetrofitService.endpoint.getCommandsFromUserByID(userID)
                            call1.enqueue(object : Callback<List<Commande>> {
                                override fun onResponse(
                                    call: Call<List<Commande>>?, response:
                                    Response<List<Commande>>?
                                ) {
                                    if (response?.isSuccessful!!) {
                                        val data2: List<Commande>? = response.body()
                                        if (data2 != null) {
                                            listCmd = data2
                                            val call2 = RetrofitService.endpoint.getCommandsFromUserByID(0)
                                            call2.enqueue( object: Callback<List<Commande>> {
                                                @RequiresApi(Build.VERSION_CODES.O)
                                                override fun onResponse(call: Call<List<Commande>>?, response:
                                                Response<List<Commande>>?) {
                                                    if(response?.isSuccessful!!) {
                                                        val data2:List<Commande>? = response.body()
                                                        if (data2 != null) {
                                                            if (data2.size > 0) {
                                                                listCmdRecup = data2
                                                                //recreate(activity)
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
                                    } else {
                                        // erreur dans la réponse
                                    }
                                }

                                override fun onFailure(call: Call<List<Commande>>?, t: Throwable?) {

                                    // Pour le débogage
                                    Log.e("erreur retrofit", t.toString())
                                    // Un toast pour l'utilisateur
                                    Toast.makeText(
                                        context,
                                        "Une erreur s'est produite",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            })


                        }
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

        holder._imageViewMap.setOnClickListener(){

            val intent = Intent(activity.context, MapActivity::class.java)
            intent.putExtra("DestAddress",cmd.AdresseClient)
            startActivity(context,intent,null)

        }


        holder.itemView.setOnClickListener { view ->


            var canValidate = "1"
            if (data[position].Delivered == "1") canValidate="0"
            val bundle = bundleOf("CommandeNum" to data[position].NumCmd,"NomClient" to data[position].NomClient,"NumTelClient" to data[position].NumTelClient,"AdresseClient" to data[position].AdresseClient,"CommandePrix" to data[position].PrixCmd,"canValidate" to canValidate)

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

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val _imageViewDelivered = view.findViewById(R.id.imageViewDelivered) as ImageView
    val _imageViewMap = view.findViewById(R.id.imageViewMap) as ImageView
    val _imageViewCancel = view.findViewById(R.id.imageViewCancel) as ImageView
    val _textViewCmdNum = view.findViewById(R.id.textViewCmdNum) as TextView
    val _textViewDelivered = view.findViewById(R.id.textViewDelivered) as TextView
    val _textViewPrice = view.findViewById(R.id.textViewPrice) as TextView
    val _textViewETA = view.findViewById(R.id.textViewETA) as TextView
    val _textViewDist = view.findViewById(R.id.textViewDist) as TextView
    val _textViewRetard = view.findViewById(R.id.textViewRetard) as TextView
    val _imageViewRetard = view.findViewById(R.id.imageViewRetard) as ImageView
}