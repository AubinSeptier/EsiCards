package com.aubinseptier.esicards

import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.zxing.MultiFormatWriter
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.MainScope
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class CardDetailsActivity : AppCompatActivity() {
    private var cardId by Delegates.notNull<Int>()
    private lateinit var token: String
    private lateinit var data: String
    private lateinit var barcodeFormat: BarcodeFormat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        cardId = intent.getIntExtra("cardId", 0)
        token = intent.getStringExtra("token") ?: ""
        initCardDetails()
    }

    public fun deleteCardDetail(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Voulez-vous supprimer cette carte ?")
        builder.setTitle("Suppression de carte")
        builder.setCancelable(false)
        builder.setPositiveButton("Oui") { dialog, which ->
            val cardId = cardId
            Api().delete("https://esicards.lesmoulinsdudev.com/cards/$cardId", ::onCardDetailDelete, token)
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
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

                data = cardData.data
                val barcodeType = cardData.type
                barcodeFormat = convertBarcodeFormat(barcodeType)
                val barcodeImage = findViewById<ImageView>(R.id.barcodeImage)

                barcodeImage.post {
                    generateBarcode(barcodeImage)
                }
            }
        }
    }

    private fun generateBarcode(barcodeImage: ImageView){
        val width = barcodeImage.width
        val height = barcodeImage.height

        val bitMatrix = MultiFormatWriter().encode(
            data,
            barcodeFormat,
            width,
            height
        )
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
        )
        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap.setPixel(i, j, if(bitMatrix[i, j]) Color.BLACK else Color.WHITE)
            }
        }
        barcodeImage.setImageBitmap(bitmap)
    }

    private fun convertBarcodeFormat(type: Int): BarcodeFormat {
        return when (type) {
            1 -> BarcodeFormat.CODE_128
            2 -> BarcodeFormat.CODE_39
            4 -> BarcodeFormat.CODE_93
            8 -> BarcodeFormat.CODABAR
            16 -> BarcodeFormat.DATA_MATRIX
            32 -> BarcodeFormat.EAN_13
            64 -> BarcodeFormat.EAN_8
            128 -> BarcodeFormat.ITF
            256 -> BarcodeFormat.QR_CODE
            512 -> BarcodeFormat.UPC_A
            1024 -> BarcodeFormat.UPC_E
            2048 -> BarcodeFormat.PDF_417
            4096 -> BarcodeFormat.AZTEC
            else -> throw IllegalArgumentException("Type Inconnu")
        }
    }

    private fun onCardDetailDelete(responseCode: Int){
        if(responseCode == 200){
            finish()
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