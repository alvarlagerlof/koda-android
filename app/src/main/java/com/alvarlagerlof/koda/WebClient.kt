package com.alvarlagerlof.koda

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import com.alvarlagerlof.koda.Extensions.hideKeyboard

/**
 * Created by alvar on 2017-06-18.
 */

class WebClient(internal var context: Context, internal var title: String) : WebChromeClient() {

    val alertTitle = title

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        AlertDialog.Builder(context)
                .setTitle(alertTitle)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    view.hideKeyboard()
                    result.confirm()
                }
                .setCancelable(false)
                .create()
                .show()

        return true
    }

    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        AlertDialog.Builder(context)
                .setTitle(alertTitle)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    view.hideKeyboard()
                    result.confirm()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    view.hideKeyboard()
                    result.cancel()
                }
                .setCancelable(false)
                .create()
                .show()
        return true
    }

    override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
        val factory = LayoutInflater.from(context)
        val v = factory.inflate(R.layout.javascript_prompt_dialog, null)

        val prompt_message_text: TextView = v.findViewById(R.id.prompt_message_text)
        val prompt_input_field: EditText = v.findViewById(R.id.prompt_input_field)
        prompt_message_text.text = message

        AlertDialog.Builder(context)
                .setTitle(alertTitle)
                .setView(v)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val value = prompt_input_field.text.toString()
                    view.hideKeyboard()
                    result.confirm(value)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    view.hideKeyboard()
                    result.cancel()
                }
                .setCancelable(false)
                .show()

        return true
    }
}
