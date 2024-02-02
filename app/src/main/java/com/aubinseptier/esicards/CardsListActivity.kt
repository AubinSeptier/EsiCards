package com.aubinseptier.esicards

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.window.OnBackInvokedDispatcher
import java.util.Date

class CardsListActivity : AppCompatActivity() {
    private lateinit var token: String
    lateinit var adapter: CardAdapter
    var cards = ArrayList<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_list)
        token = intent.getStringExtra("token") ?: ""
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

        return rowView
    }
}