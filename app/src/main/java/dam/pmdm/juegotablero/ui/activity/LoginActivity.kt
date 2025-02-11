package dam.pmdm.juegotablero.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dam.pmdm.juegotablero.R
import dam.pmdm.juegotablero.models.login.LoginRequest
import dam.pmdm.juegotablero.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                login(username, password)
            } else {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val sharedPref = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("JWT_TOKEN", loginResponse.access_token)
                            apply()
                        }
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } ?: run {
                        Toast.makeText(this@LoginActivity, "Respuesta nula", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error: ${e.message}")
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

