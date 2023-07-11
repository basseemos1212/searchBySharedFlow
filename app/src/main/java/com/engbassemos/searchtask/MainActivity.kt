package com.engbassemos.searchtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var searchEditText: EditText
    private val itemList = listOf(
        "Karim essam",
        "Khalid Osama",
        "Bassem Osama",
        "Mohaned Nabil",
        "Hossam Hassan"
    )

    private val filterResults = mutableListOf<String>()
    private val searchFlow = MutableSharedFlow<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text)
        searchEditText = findViewById(R.id.editText)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                // Emit the query value to the search flow
                CoroutineScope(Dispatchers.Main).launch {
                    searchFlow.emit(query)
                }
            }
        }

        searchEditText.addTextChangedListener(textWatcher)

        // Collect and process search results from the search flow
        CoroutineScope(Dispatchers.Main).launch {
            searchFlow.asSharedFlow().collect { query ->
                filterResults.clear()
                filterResults.addAll(itemList.filter { name ->
                    name.startsWith(query, ignoreCase = true)
                })
                updateSearchView(filterResults)
            }
        }
    }

    private fun updateSearchView(results: List<String>) {
        val filteredNames = results.joinToString("\n")
        textView.text = filteredNames
    }
}
