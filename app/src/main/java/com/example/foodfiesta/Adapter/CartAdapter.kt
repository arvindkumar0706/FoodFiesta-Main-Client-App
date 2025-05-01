package com.example.foodfiesta.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfiesta.Model.CartItems
import com.example.foodfiesta.databinding.CartitemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemsPrice: MutableList<String>,
    private var cartImage: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private var cartIngredients: MutableList<String>,
    private val cartQuantity:MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth=FirebaseAuth.getInstance()

    init {
        val database=FirebaseDatabase.getInstance()
        val userId=auth.currentUser?.uid?:""
        val cartItemNum=cartItems.size

        itemsQuantities=IntArray(cartItemNum){1}
        cartItemsRef=database.reference.child("CustomersUser").child(userId).child("CartItems")
    }

    companion object{
        private var itemsQuantities:IntArray= intArrayOf()
        private lateinit var cartItemsRef:DatabaseReference
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity= mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }


    inner class CartViewHolder(private val binding: CartitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantities = itemsQuantities[position]
                cartFoodname.text = cartItems[position]
                cartItemPrice.text = cartItemsPrice[position]
                val uriString =cartImage[position]
                val uri=Uri.parse(uriString)
                Glide.with(context).load(uri).into(itemImage)
                quantity.text = quantities.toString()

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    val itemPos = adapterPosition
                    if (itemPos != RecyclerView.NO_POSITION) {
                        deleteItem(itemPos)
                    }
                }


            }

        }

        private fun decreaseQuantity(position: Int) {
            if (itemsQuantities[position] > 1) {
                itemsQuantities[position]--
                cartQuantity[position]= itemsQuantities[position]
                binding.quantity.text = itemsQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemsQuantities[position] < 10) {
                itemsQuantities[position]++
                cartQuantity[position]= itemsQuantities[position]
                binding.quantity.text = itemsQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            val positionRet=position
            getUniqueKeyAtPosition(positionRet){uniqueKey ->
                if (uniqueKey != null){
                    removeItem(position,uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null){
                cartItemsRef.child(uniqueKey).removeValue()
                    .addOnSuccessListener {
                        cartItems.removeAt(position)
                        cartImage.removeAt(position)
                        cartDescription.removeAt(position)
                        cartQuantity.removeAt(position)
                        cartItemsPrice.removeAt(position)
                        Toast.makeText(context, "Item removed Successfully", Toast.LENGTH_SHORT).show()
                        itemsQuantities= itemsQuantities.filterIndexed { index, i ->
                            index!=position
                        }.toIntArray()
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position,cartItems.size)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        private fun getUniqueKeyAtPosition(positionRet: Int,onComplete:(String?)->Unit) {
            cartItemsRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey:String?=null
                    snapshot.children.forEachIndexed{index, dataSnapshot ->
                        if (index==positionRet){
                            uniqueKey=dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}

