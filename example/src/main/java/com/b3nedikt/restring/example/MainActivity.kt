package com.b3nedikt.restring.example

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ContentFrameLayout
import dev.b3nedikt.restring.Restring
import dev.b3nedikt.reword.Reword
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        APP_LOCALES.forEach { locale ->
            Restring.putStrings(locale, SampleStringsGenerator.getStrings(locale))
            Restring.putQuantityStrings(locale, SampleStringsGenerator.getQuantityStrings(locale))
            Restring.putStringArrays(locale, SampleStringsGenerator.getStringArrays(locale))
        }

        val localeStrings = APP_LOCALES.map { it.language + " " + it.country }
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, localeStrings)

        spinner.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Restring.locale = APP_LOCALES[position]

                val rootView = window.decorView.findViewById<ContentFrameLayout>(android.R.id.content)
                Reword.reword(rootView)

                stringArrayTextView.text = resources.getStringArray(R.array.string_array)
                        .joinToString("\n")

                quantityStringTextView.text = (0 until 3)
                        .joinToString("\n")
                        { resources.getQuantityString(R.plurals.quantity_string, it, it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }
}
