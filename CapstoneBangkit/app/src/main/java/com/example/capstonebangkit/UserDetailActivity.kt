package com.example.capstonebangkit

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.capstonebangkit.api.UserResponse


class UserDetailActivity : AppCompatActivity() {
    var namaikm: TextView? = null
    var description: TextView? = null
    var kota: TextView? = null
    var kategori: TextView? = null
    var instagram: TextView? = null
    var whatsapp: TextView? = null
    var tokopedia: TextView? = null
    var shopee: TextView? = null
    var bukalapak: TextView? = null
    var blibli: TextView? = null
    var lazada: TextView? = null
    var userResponse: UserResponse? = null
    var detailikm: ImageView? = null
    private val context: Context? = null
    private val activity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        title = "Detail IKM"
        detailikm = findViewById(R.id.iv_detailikm)
        namaikm = findViewById(R.id.tv_detail_namaikm)
        description = findViewById(R.id.tv_detail_description)
        kota = findViewById(R.id.tv_detail_kota)
        kategori = findViewById(R.id.tv_detail_kategori)
        instagram = findViewById(R.id.tv_instagram)
        whatsapp = findViewById(R.id.tv_whatsapp)
        tokopedia = findViewById(R.id.tv_tokopedia)
        shopee = findViewById(R.id.tv_shopee)
        bukalapak = findViewById(R.id.tv_bukalapak)
        blibli = findViewById(R.id.tv_blibli)
        lazada = findViewById(R.id.tv_lazada)

        val intent = intent
        if (intent.extras != null) {
            userResponse = intent.getSerializableExtra("data") as UserResponse?
            val namaikmData: String? = userResponse?.nama_ikm
            val descriptionData: String? = userResponse?.description
            val kotaData: String? = userResponse?.kota
            val kategoriData: String? = userResponse?.kategori
            val detailikmData: String? = userResponse?.image_url
            val instagramData: String? = userResponse?.instagram_url
            val whatsappData: String? = userResponse?.whatsapp_number
            val tokopediaData: String? = userResponse?.link_tokopedia
            val shopeeData: String? = userResponse?.link_shopee
            val bukalapakData: String? = userResponse?.link_bukalapak
            val blibliData: String? = userResponse?.link_blibli
            val lazadaData: String? = userResponse?.link_lazada
            Glide.with(this)
                .load(detailikmData)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(findViewById(R.id.iv_detailikm))
            namaikm?.setText(namaikmData)
            description?.setText(descriptionData)
            kota?.setText(kotaData)
            kategori?.setText(kategoriData)
            instagram?.setText(instagramData)
            whatsapp?.setText(whatsappData)
            tokopedia?.setText(tokopediaData)
            shopee?.setText(shopeeData)
            bukalapak?.setText(bukalapakData)
            blibli?.setText(blibliData)
            lazada?.setText(lazadaData)
            if (instagramData == null) {
                findViewById<View>(R.id.tv_instagram).visibility = View.GONE
                findViewById<View>(R.id.iv_instagram).visibility = View.GONE
            }
            if (whatsappData == null) {
                findViewById<View>(R.id.tv_whatsapp).visibility = View.GONE
                findViewById<View>(R.id.iv_whatsapp).visibility = View.GONE
            }
            if (tokopediaData == null) {
                findViewById<View>(R.id.tv_tokopedia).visibility = View.GONE
                findViewById<View>(R.id.iv_tokopedia).visibility = View.GONE
            }
            if (shopeeData == null) {
                findViewById<View>(R.id.tv_shopee).visibility = View.GONE
                findViewById<View>(R.id.iv_shopee).visibility = View.GONE
            }
            if (bukalapakData == null) {
                findViewById<View>(R.id.tv_bukalapak).visibility = View.GONE
                findViewById<View>(R.id.iv_bukalapak).visibility = View.GONE
            }
            if (blibliData == null) {
                findViewById<View>(R.id.tv_blibli).visibility = View.GONE
                findViewById<View>(R.id.iv_blibli).visibility = View.GONE
            }
            if (lazadaData == null) {
                findViewById<View>(R.id.tv_lazada).visibility = View.GONE
                findViewById<View>(R.id.iv_lazada).visibility = View.GONE
            }
        }
    }
}