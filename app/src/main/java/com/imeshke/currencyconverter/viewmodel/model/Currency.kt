package com.imeshke.currencyconverter.viewmodel.model

data class Currency (
    val currency: String,
    val rate: Float,
    var value : Float
)