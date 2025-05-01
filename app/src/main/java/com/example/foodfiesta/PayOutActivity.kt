package com.example.foodfiesta

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.foodfiesta.Fragment.HomeFragment
import com.example.foodfiesta.Model.OrderDetails
import com.example.foodfiesta.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlin.uuid.Uuid


class PayOutActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var phone:String
    private lateinit var totalAmount:String
    private lateinit var totalPrice:String
    private lateinit var foodItemName:ArrayList<String>
    private lateinit var foodItemPrice:ArrayList<String>
    private lateinit var foodItemImage:ArrayList<String>
    private lateinit var foodItemDescription:ArrayList<String>
    private lateinit var foodItemIngredients:ArrayList<String>
    private lateinit var foodItemQuantities:ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId:String


    var accessToken=""
    private var orderid=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paymentMethodSpinner = binding.paymentMethodSpinner
        val paymentOptions = arrayOf("Select Payment Method","Cash On Delivery", "Online")

        val adp = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, paymentOptions)
        paymentMethodSpinner.adapter = adp



        auth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().getReference()
        setUserData()

        val intent=intent
        foodItemName = intent.getStringArrayListExtra("FoodItemName") ?: arrayListOf()
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") ?: arrayListOf()
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") ?: arrayListOf()
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") ?: arrayListOf()
        foodItemIngredients = intent.getStringArrayListExtra("FoodItemIngredients") ?: arrayListOf()
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantity") ?: arrayListOf()

        totalAmount=calculateTotalAmount().toString() + "$"
        totalPrice=calculateTotalAmount().toString()
//        binding.totalAmount.isEnabled=false
        binding.totalAmount.setText(totalAmount)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.placeOrderBtn.setOnClickListener {
            val selectedPaymentMethod = binding.paymentMethodSpinner.selectedItem.toString()

            when(selectedPaymentMethod){
                "Select Payment Method" -> {
                    Toast.makeText(this, "Please select a payment option", Toast.LENGTH_SHORT).show()
                }
                "Online" -> {
                    Toast.makeText(this, "Proceeding to online payment", Toast.LENGTH_SHORT).show()
                    OnlinePayment()
                }
                "Cash On Delivery" -> {
                    Toast.makeText(this, "Order placed with Cash on Delivery", Toast.LENGTH_SHORT).show()
                    name=binding.name.text.toString().trim()
                    address=binding.address.text.toString().trim()
                    phone=binding.phone.text.toString().trim()
                    if (name.isBlank() &&address.isBlank()&&phone.isBlank()){
                        Toast.makeText(this, "Enter All Details", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        placeOrder()
                    }




                }
            }
        }



    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("https://foodfiesta.com/paypal-redirect")) {
                Log.d(TAG, "Payment Success Callback Triggered")
                placeOrder()
            }
        }
    }


    private fun OnlinePayment() {

//        Online Payment Integration here

    }


    companion object{
        const val TAG="MYTAG"
    }

    private fun placeOrder() {
        userId=auth.currentUser?.uid?:""
        val selectedPaymentMethod = binding.paymentMethodSpinner.selectedItem.toString()
        var orderStatus:String=""
        if (selectedPaymentMethod != "Online Payment"){
            orderStatus="Pending"
        }
        var paymentrev:Boolean=false
        if (selectedPaymentMethod != "Online Payment"){
            paymentrev=true
        }
        val time = System.currentTimeMillis()
        val itempushKey=databaseReference.child("OrderDetails").push().key
        val orderDetails=OrderDetails(userId,name,foodItemName,foodItemImage,foodItemPrice,foodItemQuantities,address,totalAmount,phone,time,itempushKey,false,paymentrev,selectedPaymentMethod,orderStatus)
        val orderRef=databaseReference.child("OrderDetails").child(itempushKey!!)
        orderRef.setValue(orderDetails)
            .addOnSuccessListener {
                val bottomSheetDialog=CongratsBottomSheet()
                bottomSheetDialog.show(supportFragmentManager,"test")
                removeItemFromCart()
                addOrderToHistory(orderDetails)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("CustomersUser").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
            .addOnSuccessListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemsRef=databaseReference.child("CustomersUser").child(userId).child("CartItems")
        cartItemsRef.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount=0
        for (i in 0 until foodItemPrice.size){
            var price=foodItemPrice[i]
            val lastchar=price.last()
            val priceIntValue=if (lastchar=='$'){
                price.dropLast(1).toInt()
            }else{
                price.toInt()
            }
            var quantity=foodItemQuantities[i]
            totalAmount+=priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user=auth.currentUser
        if (user!=null){
            val userId=user.uid
            val userRef=databaseReference.child("CustomersUser").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val names=snapshot.child("name").getValue(String::class.java)?:""
                        val addresses=snapshot.child("address").getValue(String::class.java)?:""
                        val phones=snapshot.child("phone").getValue(String::class.java)?:""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
}