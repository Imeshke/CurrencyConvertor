package com.imeshke.currencyconverter


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.imeshke.currencyconverter.ui.fragmants.CurrencyRatesFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragmentCurrencyCoverter()
    }

    private fun loadFragmentCurrencyCoverter() {
        val sfm = supportFragmentManager
        val fragmentTransaction = sfm.beginTransaction()
        fragmentTransaction.replace(R.id.contentMain, CurrencyRatesFragment.newInstance())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}

