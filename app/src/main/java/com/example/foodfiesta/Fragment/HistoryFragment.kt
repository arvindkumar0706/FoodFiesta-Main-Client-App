package com.example.foodfiesta.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodfiesta.Adapter.BuyAgainAdapter
import com.example.foodfiesta.Model.OrderDetails
import com.example.foodfiesta.R
import com.example.foodfiesta.RecentOrderItem
import com.example.foodfiesta.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveBuyHistory()

        binding.recentBuyItem.setOnClickListener {
            seeItemRecentBuys()
        }


        return binding.root
    }

    private fun seeItemRecentBuys() {
        listOfOrderItem.firstOrNull()?.let { recentBuy->
            val intent=Intent(requireContext(),RecentOrderItem::class.java)
            intent.putExtra("RecentBuyOrderItem",ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }


    private fun retrieveBuyHistory() {
        userId = auth.currentUser?.uid ?: ""
        val buyItemRef: DatabaseReference = database.reference
            .child("CustomersUser")
            .child(userId)
            .child("BuyHistory")

        buyItemRef.orderByChild("currentTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                for (buySnapshot in snapshot.children) {
                    val orderId = buySnapshot.key
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        it.orderId = orderId ?: ""
                        listOfOrderItem.add(it)
                    }
                }

                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    setPreviousBuyItemRecyView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })
    }



    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()

        recentOrderItem?.let {
            with(binding) {
                Foodname.text = it.FoodNames?.firstOrNull() ?: ""
                foodprice.text = (it.totalPrice).toString()
                val image = it.FoodImages?.firstOrNull() ?: ""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyagainfoodimage)

                // 🔹 Change CardView color based on orderStatus
                when (it.orderStatus) {
                    "Accepted" -> orderstatus.setCardBackgroundColor(Color.parseColor("#FFA500")) // Orange
                    "Dispatched" -> orderstatus.setCardBackgroundColor(Color.parseColor("#FFFF00")) // Yellow
                    "Completed" -> orderstatus.setCardBackgroundColor(Color.parseColor("#53E88B")) // Green ✅
                    else -> orderstatus.setCardBackgroundColor(Color.parseColor("#53E88B")) // Default (Gray)
                }
            }
        }
    }



    private fun setPreviousBuyItemRecyView() {
        val rv = binding.historyRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Pass the full list of order items to the adapter
        buyAgainAdapter = BuyAgainAdapter(listOfOrderItem, requireContext()){selectedOrder->
            openRecentOrderItem(selectedOrder)

        }

        rv.adapter = buyAgainAdapter
        buyAgainAdapter.notifyDataSetChanged() // Ensure RecyclerView updates
    }

    private fun openRecentOrderItem(orderDetails: OrderDetails) {
        val intent = Intent(requireContext(), RecentOrderItem::class.java)
        intent.putExtra("RecentBuyOrderItem", arrayListOf(orderDetails)) // Pass single order
        startActivity(intent)
    }

    companion object {

    }
}