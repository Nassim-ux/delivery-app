package com.example.deliveryappproject

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat

class MyProductRecyclerViewAdapter(val context: Context, var data:List<Product>): RecyclerView.Adapter<MyViewHolderP>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderP {
        return MyViewHolderP(LayoutInflater.from(context).inflate(R.layout.custom_product, parent, false))

    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyViewHolderP, position: Int) {

        val product = data[position]

        holder._textViewNomProduct.text = product.Nom.toUpperCase()
        holder._textViewQteProduct.text = product.ProductQte.toString()
        holder._textViewPriceProduct.text = separ1000(product.PrixProduct.toLong())


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

class MyViewHolderP(view: View) : RecyclerView.ViewHolder(view) {
    val _textViewNomProduct = view.findViewById(R.id.textViewNomProduct) as TextView
    val _textViewPriceProduct = view.findViewById(R.id.textViewPriceProduct) as TextView
    val _textViewQteProduct = view.findViewById(R.id.textViewQteProduct) as TextView
}