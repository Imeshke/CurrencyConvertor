package com.imeshke.currencyconverter.ui.fragmants

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imeshke.currencyconverter.databinding.FragmentRatesBinding
import com.imeshke.currencyconverter.ui.structure.CurrencyRatesAdapter
import com.imeshke.currencyconverter.viewmodel.CurrencyRatesViewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.lang.Exception

class CurrencyRatesFragment : Fragment(), CurrencyRatesAdapter.OnRateInteraction {

    private var data_binding : FragmentRatesBinding? = null
    private val dateBinding get() = data_binding!!

    private var currencyRatesAdapter: CurrencyRatesAdapter? = null
    private var currencyRatesViewModel: CurrencyRatesViewModel? = null

    var currencyCode : ArrayList<String> = ArrayList<String>()
    var currencyAdapter: ArrayAdapter<String>? = null

    private var notifyRatesJob : Job? = null
    private var notifyUIJob : Job? = null
    private var initRatesJob : Job? = null

    private var currentSelectedCurrency  = 0 //for spinner selected

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currencyRatesViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application)).get(CurrencyRatesViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        data_binding = FragmentRatesBinding.inflate(inflater,container,false)
        return dateBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyRatesViewModel?.newCurrencyRates?.forEach {
            currencyCode?.add(it.currency)
        }

        currencyAdapter = context?.let { ArrayAdapter(it, R.layout.simple_spinner_item, currencyCode) }
        dateBinding.currencySpinner.adapter = currencyAdapter

        dateBinding.rateInputEditText.addDecimalLimiter()
        dateBinding.rateInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (!s.toString().equals("0")) {
                        var value: Float = 0.00f
                        if (!s.toString().isEmpty()) {
                            value = s.toString().toFloat()
                        }
                        onValueChanged((1.00 / currencyRatesViewModel?.newCurrencyRates?.get(currentSelectedCurrency)?.rate!! * value).toFloat())
                    }
                }catch (e : Exception){

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        dateBinding.currencySpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                var value : Float = 0.00f
                if (!dateBinding.rateInputEditText.text.toString().equals("")){
                    value = dateBinding.rateInputEditText.text.toString().toFloat()
                }
                currentSelectedCurrency = position

                onValueChanged((1.00/ currencyRatesViewModel?.newCurrencyRates?.get(position)?.rate!! *value).toFloat())
            }

        }


        currencyRatesAdapter = CurrencyRatesAdapter(this)
        dateBinding.ratesRecyclerView.adapter = currencyRatesAdapter

        val mLayoutManager: RecyclerView.LayoutManager
        mLayoutManager = GridLayoutManager(context, 3)
        dateBinding.ratesRecyclerView.layoutManager = mLayoutManager
    }

    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count{ ".".contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    override fun onStart() {
        super.onStart()

        notifyUIJob = viewLifecycleOwner.lifecycleScope.launch {
            currencyRatesViewModel?.getCurrencyRates()?.asFlow()?.collectLatest {
                currencyRatesAdapter?.updateList(it)
                currencyRatesViewModel?.newCurrencyRates?.forEach {
                    currencyCode?.add(it.currency)
                }
                currencyCode = currencyCode.distinct() as ArrayList<String>
                currencyAdapter?.notifyDataSetChanged()
            }
        }

        // Refresh data every 30 minuits
        notifyRatesJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                delay(1000*60*30)
                currencyRatesViewModel?.refreshRates()
                dateBinding.rateInputEditText.setText("1.00")
                dateBinding.currencySpinner.setSelection(0)
            }
        }

        //update ui init
        initRatesJob = viewLifecycleOwner.lifecycleScope.launch {
            currencyRatesViewModel?.refreshRates()
            dateBinding.rateInputEditText.setText("1.00")
            dateBinding.currencySpinner.setSelection(0)
        }
    }

    override fun onStop() {
        notifyRatesJob?.cancel()
        notifyUIJob?.cancel()
        initRatesJob?.cancel()
        super.onStop()
    }

    override fun scrollToTop() {
        dateBinding.ratesRecyclerView.scrollToPosition(0)
    }

    override fun onRateChanged(currencyName: String, value: Float) {
        currencyRatesViewModel?.setNewBase(currencyName,value)
    }

    override fun onValueChanged(value: Float) {
        currencyRatesViewModel?.setNewBaseValue(value)
    }

    companion object {
        fun newInstance(): CurrencyRatesFragment {
            return CurrencyRatesFragment()
        }
    }

}