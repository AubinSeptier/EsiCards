package com.aubinseptier.esicards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun registerNewAccount(view: View){
       val intent = Intent(this, RegisterActivity::class.java)
       startActivity(intent)
    }
}

data class LoginData(
    val mail: String,
    val password: String
)