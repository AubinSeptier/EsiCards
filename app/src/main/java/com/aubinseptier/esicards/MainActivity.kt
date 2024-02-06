package com.aubinseptier.esicards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun login(view: View){
        var loginEmail = findViewById<EditText>(R.id.loginEmail)
        var loginPassword = findViewById<EditText>(R.id.loginPassword)
        var logData = LoginData(loginEmail.text.toString(), loginPassword.text.toString())
        Api().post<LoginData, String>("https://esicards.lesmoulinsdudev.com/auth", logData, ::LoginSuccess)
    }

    public fun registerNewAccount(view: View){
       val intent = Intent(this, RegisterActivity::class.java)
       startActivity(intent)
    }

    private fun LoginSuccess(responseCode: Int, token: String?){
        if(responseCode == 200){
            val intent = Intent(this, CardsListActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
            finish()
        }
    }
}

data class LoginData(
    val mail: String,
    val password: String
)