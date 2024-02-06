package com.aubinseptier.esicards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CardActivity : AppCompatActivity() {
    private lateinit var barcodeValue: String
    private var barcodeType by Delegates.notNull<Int>()
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
    }

    public fun scanBarcode(view: View){
        val scanLabel = findViewById<TextView>(R.id.scanLabel)
        val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        ).build()
        val scanner = GmsBarcodeScanning.getClient(this)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                scanLabel.text = "Code barre détecté avec succès"
                barcodeValue  = barcode.rawValue ?: ""
                barcodeType = barcode.format
            }
            .addOnCanceledListener {
                scanLabel.text = "Code barre non détecté, veuillez réessayer"
            }
            .addOnFailureListener { error ->
                scanLabel.text = "Une erreur s'est produite, veuillez réessayer"
            }
    }

    public fun addCard(view: View){
        val cardName = findViewById<EditText>(R.id.cardName)
        val cardData = CardData(cardName.text.toString(), barcodeValue, barcodeType)
        val tokenStorage = TokenStorage(this)
        mainScope.launch {
            val token = tokenStorage.read()
            Api().post<CardData>("https://esicards.lesmoulinsdudev.com/cards", cardData, ::addCardSuccess, token)
        }
    }

    public fun cancelAdd(view: View){
        finish()
    }

    private fun addCardSuccess(responseCode: Int){
        if(responseCode == 200){
            finish()
        }
    }
}

data class CardData(
    val name: String,
    val data: String,
    val type: Int
)