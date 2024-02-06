package com.aubinseptier.esicards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    public fun register(view: View){
        var registerName = findViewById<EditText>(R.id.registerName)
        var registerEmail = findViewById<EditText>(R.id.registerEmail)
        var registerPassword = findViewById<EditText>(R.id.registerPassword)
        var registeredData = RegisterData(registerName.text.toString(), registerEmail.text.toString(), registerPassword.text.toString())
        Api().post<RegisterData>("https://esicards.lesmoulinsdudev.com/register", registeredData, ::registerSuccess)
    }

    public fun goToLogin(view: View){
        finish()
    }

    private fun registerSuccess(responseCode: Int){
        if(responseCode == 200){
            finish()
        }
    }
}

data class RegisterData(
    val name: String,
    val mail: String,
    val password: String
)