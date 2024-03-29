package com.aubinseptier.esicards

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CardsListActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var adapter: CardAdapter
    private var cards = ArrayList<Card>()
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_list)
        token = intent.getStringExtra("token") ?: ""
        saveToken()
        adapter = CardAdapter(this, cards)
        initCardsList()
        loadCardsList()

        onBackPressedDispatcher.addCallback(this) {
            val alertDialogBuilder = AlertDialog.Builder(this@CardsListActivity)
            alertDialogBuilder.setMessage("Voulez-vous vraiment quitter l'application?")
            alertDialogBuilder.setPositiveButton("Oui") { dialog, which ->
                finish()
            }
            alertDialogBuilder.setNegativeButton("Non") { dialog, which ->
                dialog.cancel()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCardsList()
    }

    public fun addCard(view: View){
        val intent = Intent(this, CardActivity::class.java)
        startActivity(intent)
    }

    public fun alphaSort(view: View){
        cards.sortBy { it.name }
        adapter.notifyDataSetChanged()
    }

    public fun lastUsedSort(view: View){
        cards.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.lastUsedAt) }
        adapter.notifyDataSetChanged()
    }

    public fun seeCardDetails(view: View){
        val parentView = view.parent as View
        val listView = parentView.parent as ListView
        val position = listView.getPositionForView(parentView)
        val card = cards[position]
        val cardId = card.id
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra("cardId", cardId)
        intent.putExtra("token", token)
        startActivity(intent)
    }

    public fun deleteCard(view: View){
        val parentView = view.parent as View
        val listView = parentView.parent as ListView
        val position = listView.getPositionForView(parentView)
        val card = cards[position]

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Voulez-vous supprimer cette carte ?")
        builder.setTitle("Suppression de carte")
        builder.setCancelable(false)
        builder.setPositiveButton("Oui") { dialog, which ->
            val cardId = card.id
            Api().delete("https://esicards.lesmoulinsdudev.com/cards/$cardId", ::onCardDelete, token)
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun saveToken(){
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            tokenStorage.write(token)
        }
    }

    private fun initCardsList(){
        val cardsList = findViewById<ListView>(R.id.cardsList)
        cardsList.adapter = adapter
    }

    private fun loadCardsList(){
        Api().get("https://esicards.lesmoulinsdudev.com/cards", ::onCardsReceived, token)
    }

    private fun onCardsReceived(responseCode: Int, cardsData: ArrayList<Card>?){
        if(responseCode == 200 && cardsData != null){
            cards.clear()
            cards.addAll(cardsData)
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun onCardDelete(responseCode: Int){
        if(responseCode == 200){
            loadCardsList()
        }
    }
}

data class Card(
    val id: Int,
    val name: String,
    val createAt: String,
    val lastUsedAt: String
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
        cardItemName.text = card.name

        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = originalFormat.parse(card.lastUsedAt)
        val formattedDate = dateFormat.format(date)
        cardItemUsed.text = formattedDate

        return rowView
    }
}