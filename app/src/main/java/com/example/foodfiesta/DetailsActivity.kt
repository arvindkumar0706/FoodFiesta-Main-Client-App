package com.example.foodfiesta

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodfiesta.Model.CartItems
import com.example.foodfiesta.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodIngredient: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("menuItemName")
        foodPrice = intent.getStringExtra("menuItemPrice")
        foodDescription = intent.getStringExtra("menuItemDecrip")
        foodIngredient = intent.getStringExtra("menuItemIngred")
        foodImage = intent.getStringExtra("menuItemImage")

        with(binding) {
            detailsFoodname.text = foodName
            detailsFoodPrice.text = "$ ${foodPrice}"
            detailDescription.text = foodDescription
            detailIngredients.text = foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }


        binding.addTocartDeatils.setOnClickListener {
            addItemToCart()
        }

        binding.backDetailsBtn.setOnClickListener {
            finish()
        }

    }

    private fun addItemToCart() {
        val database=FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""
        val cartItem=CartItems(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(),
            1
        )
        database.child("CustomersUser").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "item added to Cart Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "item not added to Cart Successfully", Toast.LENGTH_SHORT).show()
            }
    }
}