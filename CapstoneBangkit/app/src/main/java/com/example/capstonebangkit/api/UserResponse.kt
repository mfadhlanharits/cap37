package com.example.capstonebangkit.api

import java.io.Serializable


class UserResponse : Serializable {
    var id = 0
    var image_url: String? = null
    var nama_ikm: String? = null
    var instagram_url: String? = null
    var whatsapp_number: String? = null
    var description: String? = null
    var kota: String? = null
    var kategori: String? = null
    var date: String? = null
    var link_shopee: String? = null
    var link_tokopedia: String? = null
    var link_bukalapak: String? = null
    var link_blibli: String? = null
    var link_lazada: String? = null

    override fun toString(): String {
        return "UserResponse{" +
                "id ='" + id + '\'' +
                ", image_url ='" + image_url + '\'' +
                ", nama_ikm ='" + nama_ikm + '\'' +
                ", instagram_url ='" + instagram_url + '\'' +
                ", whatsapp_number ='" + whatsapp_number + '\'' +
                ", description ='" + description + '\'' +
                ", kota ='" + kota + '\'' +
                ", kategori ='" + kategori + '\'' +
                ", date ='" + date + '\'' +
                ", link_shopee ='" + link_shopee + '\'' +
                ", link_tokopedia ='" + link_tokopedia + '\'' +
                ", link_bukalapak='" + link_bukalapak + '\'' +
                ", link_blibli ='" + link_blibli + '\'' +
                ", link_lazada ='" + link_lazada + '\'' +
                '}'
    }
}