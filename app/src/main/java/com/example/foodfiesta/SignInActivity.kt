package com.example.foodfiesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodfiesta.Model.UserModel
import com.example.foodfiesta.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {
    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var username:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference



        binding.CrAccBtn.setOnClickListener {

            username=binding.editTextTextName.text.toString()
            email=binding.editTextTextEmailAddress.text.toString().trim()
            password=binding.editTextTextPassword.text.toString().trim()

            if (validateInputs()) {

                CreateAccount(email,password)

                // Proceed with account creation
            }
        }

        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun CreateAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->

            if (task.isSuccessful){
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Account Creation Failed!", Toast.LENGTH_SHORT).show()
                Log.e("Account","createAccount : Failure",task.exception)
            }

        }
    }

    private fun saveUserData() {
        username=binding.editTextTextName.text.toString()
        email=binding.editTextTextEmailAddress.text.toString().trim()
        password=binding.editTextTextPassword.text.toString().trim()

        val user=UserModel(username,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid

        database.child("CustomersUser").child(userId).setValue(user)
    }

    private fun validateInputs(): Boolean {
        val name = binding.editTextTextName.text.toString().trim()
        val emailOrPhone = binding.editTextTextEmailAddress.text.toString().trim()
        val password = binding.editTextTextPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.editTextTextName.error = "Name is required"
            return false
        }

        if (emailOrPhone.isEmpty()) {
            binding.editTextTextEmailAddress.error = "Email is required"
            return false
        } else if (!isValidEmailOrPhone(emailOrPhone)) {
            binding.editTextTextEmailAddress.error = "Enter a valid Email Address"
            return false
        }

        if (password.isEmpty()) {
            binding.editTextTextPassword.error = "Password is required"
            return false
        } else if (password.length < 6) {
            binding.editTextTextPassword.error = "Password must be at least 6 characters"
            return false
        }

        return true
    }

    private fun isValidEmailOrPhone(input: String): Boolean {
        return if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            true
        } else {
            input.length == 10 && input.all { it.isDigit() }
        }
    }
}
