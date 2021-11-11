package br.com.up.mypins.model

data class Places(
    var name: String,
    var lat: Double,
    var lng: Double,
    var openNow: String,
    var address: String  )
//nao precisa get set