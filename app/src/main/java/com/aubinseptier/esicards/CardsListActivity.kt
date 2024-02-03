package com.aubinseptier.esicards

import android.content.Context
import android.content.Intent
import android.media.session.MediaSession.Token
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Date

class CardsListActivity : AppCompatActivity() {
    private lateinit var token: String
    lateinit var adapter: CardAdapter
    var cards = ArrayList<Card>()
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_list)
        token = intent.getStringExtra("token") ?: ""
        saveToken()
        adapter = CardAdapter(this, cards)
        initCardsList()
    }

    public fun addCard(view: View){
        val intent = Intent(this, CardActivity::class.java)
        startActivity(intent)
    }

    public fun alphaSort(view: View){
        cards.sortedWith(compareBy({ it.name }))
    }

    public fun lastUsedSort(view: View){
        cards.sortedWith(compareByDescending( { it.lastUsedAt }))
    }

    private fun saveToken(){
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write(token)
        }
    }

    private fun readToken(){
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.read()
        }
    }

    private fun initCardsList(){
        var cardsList = findViewById<ListView>(R.id.cardsList)
        cardsList.adapter = adapter
    }
}

data class Card(
    val id: Int,
    val name: String,
    val createAt: Date,
    val lastUsedAt: Date
)

class CardAdapter(private val context: Context, private val dataSource: ArrayList<Card>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.cards_list_item, parent, false)
        val card = getItem(position) as Card

        val cardItemName = rowView.findViewById<TextView>(R.id.cardItemName)
        val cardItemUsed = rowView.findViewById<TextView>(R.id.cardItemUsed)

        return rowView
    }
}