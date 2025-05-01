package com.example.foodfiesta.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.foodfiesta.Adapter.CartAdapter
import com.example.foodfiesta.CongratsBottomSheet
import com.example.foodfiesta.Model.CartItems
import com.example.foodfiesta.PayOutActivity
import com.example.foodfiesta.R
import com.example.foodfiesta.databinding.CartitemBinding
import com.example.foodfiesta.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImage: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retreiveCartItems()



        binding.proceedBtn.setOnClickListener {
            getOrderItemDetails()
        }

        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdRef: DatabaseReference =
            database.reference.child("CustomersUser").child(userId).child("CartItems")
        val foodname = mutableListOf<String>()
        val foodprice = mutableListOf<String>()
        val foodimage = mutableListOf<String>()
        val fooddescription = mutableListOf<String>()
        val foodingredients = mutableListOf<String>()
        val Itemquantities = cartAdapter.getUpdatedItemsQuantities()
        orderIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodname.add(it) }
                    orderItems?.foodPrice?.let { foodprice.add(it) }
                    orderItems?.foodDescription?.let { fooddescription.add(it) }
                    orderItems?.foodImage?.let { foodimage.add(it) }
                    orderItems?.foodingredients?.let { foodingredients.add(it) }

                }
                orderNow(
                    foodname,
                    foodprice,
                    fooddescription,
                    foodimage,
                    foodingredients,
                    Itemquantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "order Making Failed Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun orderNow(
        foodname: MutableList<String>,
        foodprice: MutableList<String>,
        fooddescription: MutableList<String>,
        foodimage: MutableList<String>,
        foodingredients: MutableList<String>,
        itemquantities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodname as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodprice as ArrayList<String>)
            intent.putExtra("FoodItemDescription", fooddescription as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodimage as ArrayList<String>)
            intent.putExtra("FoodItemIngredients", foodingredients as ArrayList<String>)
            intent.putExtra("FoodItemQuantity", itemquantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retreiveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodRef: DatabaseReference =
            database.reference.child("CustomersUser").child(userId).child("CartItems")

        foodName = mutableListOf()
        foodPrice = mutableListOf()
        foodDescription = mutableListOf()
        foodIngredients = mutableListOf()
        foodImage = mutableListOf()
        quantity = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    cartItems?.foodName?.let { foodName.add(it) }
                    cartItems?.foodPrice?.let { foodPrice.add(it) }
                    cartItems?.foodDescription?.let { foodDescription.add(it) }
                    cartItems?.foodImage?.let { foodImage.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodingredients?.let { foodIngredients.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {
                cartAdapter = CartAdapter(
                    requireContext(),
                    foodName,
                    foodPrice,
                    foodImage,
                    foodDescription,
                    foodIngredients,
                    quantity
                )
                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not Fetch", Toast.LENGTH_SHORT).show()

            }

        })
    }

    companion object {

    }
}