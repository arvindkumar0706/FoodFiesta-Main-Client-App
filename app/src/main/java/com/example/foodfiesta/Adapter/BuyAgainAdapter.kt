package com.example.foodfiesta.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfiesta.Model.OrderDetails
import com.example.foodfiesta.databinding.BuyagainitemBinding

class BuyAgainAdapter(
    private val orderList: List<OrderDetails>, // Use a single list
    private val context: Context,
    private val onItemClick: (OrderDetails) -> Unit
) : RecyclerView.Adapter<BuyAgainAdapter.BuyagainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyagainViewHolder {
        val binding = BuyagainitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyagainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyagainViewHolder, position: Int) {
        holder.bind(orderList[position]) // Pass entire order object

        val orderItem = orderList[position]
        holder.itemView.setOnClickListener {
            onItemClick(orderItem)
        }

    }

    override fun getItemCount(): Int = orderList.size

    inner class BuyagainViewHolder(private val binding: BuyagainitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItem: OrderDetails) {
            binding.buyagainfoodname.text = orderItem.FoodNames?.firstOrNull() ?: ""
            binding.buyagainfoodprice.text = orderItem.totalPrice?.toString() ?: "0"
            val imageUri = orderItem.FoodImages?.firstOrNull() ?: ""
            Glide.with(context).load(Uri.parse(imageUri)).into(binding.buyagainfoodimage)



        }
    }
}
