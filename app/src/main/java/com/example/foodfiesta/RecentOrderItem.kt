package com.example.foodfiesta

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfiesta.Adapter.RecentBuyAdapter
import com.example.foodfiesta.Model.OrderDetails
import com.example.foodfiesta.databinding.ActivityRecentOrderItemBinding



class RecentOrderItem : AppCompatActivity() {
    private lateinit var binding:ActivityRecentOrderItemBinding

    private lateinit var allFoodName:ArrayList<String>
    private lateinit var allFoodPrice:ArrayList<String>
    private lateinit var allFoodImage:ArrayList<String>
    private lateinit var allFoodQuantity:ArrayList<Int>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityRecentOrderItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }



        val recentOrderItems=intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>
        recentOrderItems?.let {orderDetails ->

            if (orderDetails.isNotEmpty()){
                val recentOrderItem=orderDetails[0]

                binding.orderId.text=recentOrderItem.orderId
                binding.address.text=recentOrderItem.address

                allFoodName=recentOrderItem.FoodNames as ArrayList<String>
                allFoodPrice=recentOrderItem.FoodPrices as ArrayList<String>
                allFoodImage=recentOrderItem.FoodImages as ArrayList<String>
                allFoodQuantity=recentOrderItem.FoodQuantities as ArrayList<Int>



            }

        }

        setAdapter()

    }

    private fun setAdapter() {
        val rv = binding.RecentItemRV
        rv.layoutManager=LinearLayoutManager(this)
        val adapter=RecentBuyAdapter(this,allFoodName,allFoodImage,allFoodPrice,allFoodQuantity)
        rv.adapter=adapter
    }
}