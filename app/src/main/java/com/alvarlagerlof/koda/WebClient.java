package com.alvarlagerlof.koda;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by alvar on 2016-11-08.
 */

public class WebClient {

    public android.webkit.WebChromeClient getClient(final Context context, final String title) {

        final String alertTitle = title + " " + context.getString(R.string.says) + "...";

        final android.webkit.WebChromeClient webChromeClient = new android.webkit.WebChromeClient(){

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(context)
                        .setTitle(alertTitle)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                }).setCancelable(false).create().show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(context)
                        .setTitle(alertTitle)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                }).setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        }).create().show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                final LayoutInflater factory = LayoutInflater.from(context);
                final View v = factory.inflate(R.layout.javascript_prompt_dialog, null);

                ((TextView)v.findViewById(R.id.prompt_message_text)).setText(message);

                new AlertDialog.Builder(context)
                        .setTitle(alertTitle)
                        .setView(v)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String value = ((EditText)v.findViewById(R.id.prompt_input_field)).getText().toString();
                                        result.confirm(value);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        result.cancel();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        result.cancel();
                                    }
                                })
                        .show();

                return true;
            }

        };

        return webChromeClient;
    }

}
