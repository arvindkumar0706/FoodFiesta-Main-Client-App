package com.example.foodfiesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.foodfiesta.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var signInClient:SignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()




        // ✅ Initialize signInClient here
        signInClient = com.google.android.gms.auth.api.identity.Identity.getSignInClient(this)

        val clientId = "270094502602-01tho7ohhgvp3fj47nvnm4p47uj5lohv.apps.googleusercontent.com"
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false) // Allow new accounts
                    .build()
            )
            .build()

        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    saveUserToDatabase(user) // Save Google user details
                                }
                            } else {
                                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                                Log.e("LoginActivity", "Google sign-in failed", task.exception)
                            }
                        }
                }
            }
        }

        binding.Googlebutton.setOnClickListener {
            signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    signInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Google Sign-In Error", e)
                }
        }


        binding.loginButton.setOnClickListener {
            email=binding.EmailAddress.text.toString().trim()
            password=binding.Password.text.toString().trim()
            if (validateInputs()) {

                loginUser()

            }

        }

        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserToDatabase(user: FirebaseUser) {
        val customerRef = database.reference.child("CustomersUser").child(user.uid)

        customerRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // User doesn't exist in CustomersUser, add them
                val userData = mapOf(
                    "userId" to user.uid,
                    "name" to (user.displayName ?: "Unknown"),
                    "email" to (user.email ?: "No Email"),
                    "profileImage" to (user.photoUrl?.toString() ?: "")
                )

                customerRef.setValue(userData)
                    .addOnSuccessListener {
                        Log.d("LoginActivity", "Google user saved successfully")
                        updateUi(user)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginActivity", "Failed to save Google user", e)
                    }
            } else {
                // User already exists, proceed to UI update
                updateUi(user)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LoginActivity", "Database Error", e)
        }
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
            if (task.isSuccessful){
                val user=auth.currentUser
                if (user != null) {
                    checkUserInCustomersNode(user.uid)
                }
            }else{
                Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserInCustomersNode(userId: String) {
        val customerRef = database.reference.child("CustomersUser").child(userId)

        customerRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // User exists in CustomersUser node, allow login
                updateUi(auth.currentUser)
            } else {
                // User is not a customer, sign them out
                auth.signOut()
                Toast.makeText(this, "Access Denied: Not a Customer", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Database Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        val emailOrPhone = binding.EmailAddress.text.toString().trim()
        val password = binding.Password.text.toString().trim()

        var isValid = true

        // Validate Email or Phone Number
        if (emailOrPhone.isEmpty()) {
            binding.EmailAddress.error = "Email or Phone Number is required"
            isValid = false
        } else if (!isValidEmailOrPhone(emailOrPhone)) {
            binding.EmailAddress.error = "Enter a valid Email or Phone Number"
            isValid = false
        } else {
            binding.EmailAddress.error = null
        }

        // Validate Password
        if (password.isEmpty()) {
            binding.Password.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.Password.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.Password.error = null
        }

        return isValid
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun isValidEmailOrPhone(input: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches() || input.matches(Regex("^[0-9]{10}$"))
    }
    private fun updateUi(user: FirebaseUser?) {

        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}
