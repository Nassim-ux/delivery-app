package com.example.deliveryappproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_blank_detail.*
import kotlinx.android.synthetic.main.fragment_blank_list.*
import org.jetbrains.anko.enabled
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat


/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragmentDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragmentDetail : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_detail, container, false)


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        textViewNumCmd.text = (requireArguments().getSerializable("CommandeNum") as Int).toString()
        textViewPriceP.text = (requireArguments().getSerializable("CommandePrix") as Int).toString()
        textViewClientNameP.text = (requireArguments().getSerializable("NomClient") as String).toString()
        textViewClientNumP.text = (requireArguments().getSerializable("NumTelClient") as String).toString()
        textViewClientAddressP.text = (requireArguments().getSerializable("AdresseClient") as String).toString()

        val canValidate = requireArguments().getSerializable("canValidate") as String

        imageViewBackButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_blankFragmentDetail_to_blankFragmentList)
        }

        progressBar3.visibility = View.VISIBLE
        var listProduct:List<Product> = listOf()
        val numCmd = requireArguments().getSerializable("CommandeNum") as Int
        val call = RetrofitService.endpoint.getProductsFromCommandByNum(numCmd)
        call.enqueue( object: Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>?, response:
            Response<List<Product>>?) {
                progressBar3.visibility = View.INVISIBLE
                if(response?.isSuccessful!!) {
                    val data:List<Product>? = response.body()
                    if (data != null) {
                        if (data.size > 0) {

                            listProduct = data
                            recyclerView2.layoutManager = LinearLayoutManager(requireActivity())
                            val MyProductRecyclerViewAdapter = MyProductRecyclerViewAdapter(requireActivity(), listProduct)
                            recyclerView2.adapter = MyProductRecyclerViewAdapter
                        } else {
                            Toast.makeText(
                                context,
                                "No Product found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                else {
                    // erreur dans la réponse
                }
            }
            override fun onFailure(call: Call<List<Product>>?, t: Throwable?) {

                progressBar3.visibility = View.INVISIBLE
                // Pour le débogage
                Log.e("erreur retrofit", t. toString())
                // Un toast pour l'utilisateur
                Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()

            }
        })

        if (canValidate == "0"){
            buttonValidateCmd.enabled = false
            buttonValidateCmd.setTextColor(Color.parseColor("#000000"))
            buttonValidateCmd.setBackgroundResource(R.drawable.disabled_btn)
        }



        buttonValidateCmd.setOnClickListener(){




            requireView().findNavController().navigate(R.id.action_blankFragmentDetail_to_blankFragmentList)


            val intentIntegrator = IntentIntegrator(requireActivity())
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()

        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Scan Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Scanned : ${result.contents}")

                val numCmd = requireArguments().getSerializable("CommandeNum") as Int
                val usercmd = UserCommand(numCmd,0,"1",null)
                val call1 = RetrofitService.endpoint.updtCmdDelivered(usercmd)
                call1.enqueue( object: Callback<String> {
                    override fun onResponse(call: Call<String>?, response:
                    Response<String>?) {
                        if(response?.isSuccessful!!) {
                            val data:String? = response.body()
                            if (data != null) {

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

                Toast.makeText(context, "Scanned -> " + result.contents, Toast.LENGTH_SHORT)
                    .show()
                requireView().findNavController().navigate(R.id.action_blankFragmentDetail_to_blankFragmentList)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}

