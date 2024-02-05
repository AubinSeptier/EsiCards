package com.aubinseptier.esicards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class CardDetailsActivity : AppCompatActivity() {
    private var cardId by Delegates.notNull<Int>()
    private lateinit var token: String
    private val mainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        cardId = intent.getIntExtra("cardId", 0)
        token = intent.getStringExtra("token") ?: ""
        initCardDetails()
    }

    public fun backToList(view: View){
        finish()
    }

    private fun initCardDetails(){
        Api().get<CardDetailsData>("https://esicards.lesmoulinsdudev.com/cards/$cardId", ::onCardDataReceived, token)
    }

    private fun onCardDataReceived(responseCode: Int, cardData: CardDetailsData?){
        if(responseCode == 200){
            if (cardData != null) {
                val cardDetailsName = findViewById<TextView>(R.id.cardDetailsName)
                val creationDate = findViewById<TextView>(R.id.creationDate)
                val lastUsedDate = findViewById<TextView>(R.id.lastUsedDate)
                cardDetailsName.text = cardData.name

                val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val creationDateData = originalFormat.parse(cardData.createdAt)
                val lastUsedDateData = originalFormat.parse(cardData.lastUsedAt)
                val formattedCreationDate = dateFormat.format(creationDateData)
                val formattedLastUsedDate = dateFormat.format(lastUsedDateData)
                creationDate.text = formattedCreationDate
                lastUsedDate.text = formattedLastUsedDate
            }
        }
    }
}

data class CardDetailsData(
    val id: Int,
    val name: String,
    val data: String,
    val type: Int,
    val createdAt: String,
    val lastUsedAt: String
)