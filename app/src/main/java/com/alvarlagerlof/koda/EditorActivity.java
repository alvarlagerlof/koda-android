package com.alvarlagerlof.koda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MyProjects.MyProjectsRealmObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

public class EditorActivity extends AppCompatActivity {

    Realm realm;

    Toolbar toolbar;
    WebView webView;

    ShaderEditor editorEdittext;

    String private_id;
    String public_id;
    String title;

    int fontSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webview);
        editorEdittext = (ShaderEditor) findViewById(R.id.editor_editext);


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);


        // From intent
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            private_id = sharedPreferences.getString("private_id", "null");
            public_id = sharedPreferences.getString("public_id", "null");
            title = sharedPreferences.getString("title", "null");
        } else {
            private_id = extras.getString("private_id");
            public_id = extras.getString("public_id");
            title = extras.getString("title");

            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("private_id", private_id);
            editor.putString("public_id", public_id);
            editor.putString("title", title);
            editor.apply();
        }

        realm = Realm.getDefaultInstance();


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadData("", "text/html", "UTF-8");


        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setWebChromeClient(new WebChromeClient(){


            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(EditorActivity.this)
                        .setTitle(title + " " + getString(R.string.says) + "...")
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
                new AlertDialog.Builder(EditorActivity.this)
                        .setTitle(title + " " + getString(R.string.says) + "...")
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
                final LayoutInflater factory = LayoutInflater.from(EditorActivity.this);
                final View v = factory.inflate(R.layout.javascript_prompt_dialog, null);

                ((TextView)v.findViewById(R.id.prompt_message_text)).setText(message);

                new AlertDialog.Builder(EditorActivity.this)
                        .setTitle(title + " " + getString(R.string.says) + "...")
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
            };

        });


        // Set code
        MyProjectsRealmObject object = realm.where(MyProjectsRealmObject.class)
                .contains("privateId", private_id)
                .findFirst();

        editorEdittext.setText(object.getCode());

        
        Typeface type = Typeface.createFromAsset(getAssets(),"SourceCodePro-Regular.ttf");
        editorEdittext.setTypeface(type);
        editorEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //save save = new save();
                //save.execute(String.valueOf(s));
                // TODO: FIX AUTOSAVE WHEN DATABASE IS FASTER AND ADD A 3 SEC DELAY
            }
        });


    }

    private class save extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {

            // Send to server
            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(EditorActivity.this), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("code", params[0])
                    .build();


            Request request = new Request.Builder()
                    .url("https://koda.nu/labbet/" + private_id)
                    .post(formBody)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String response_s = response.body().string();
                return response_s;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Save locally
            final Realm realm = Realm.getDefaultInstance();

            MyProjectsRealmObject object = realm.where(MyProjectsRealmObject.class)
                    .equalTo("privateId", private_id)
                    .findFirst();

            realm.beginTransaction();
            object.setCode(editorEdittext.getCleanText());
            realm.commitTransaction();

        }
    }


    public void fontSize(int change) {
        if (fontSize + change > 5 && fontSize + change < 40) {
            fontSize += change;
            editorEdittext.setTextSize(fontSize);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        toolbar.getMenu().findItem(R.id.webview).setVisible(true);
        toolbar.getMenu().findItem(R.id.editor).setVisible(false);
        toolbar.getMenu().findItem(R.id.reload).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.webview:
                webView.setVisibility(View.VISIBLE);
                editorEdittext.setVisibility(View.INVISIBLE);
                webView.loadData(String.valueOf(editorEdittext.getText()), "text/html", "UTF-8");
                //webView.loadDataWithBaseURL(null, String.valueOf(editorEdittext.getText()), "text/html", "UTF-8", null);
                //webView.loadUrl("http://koda.nu/arkivet/" + public_id);
                save save = new save();
                save.execute(String.valueOf(editorEdittext.getCleanText()));
                toolbar.getMenu().findItem(R.id.webview).setVisible(false);
                toolbar.getMenu().findItem(R.id.editor).setVisible(true);
                toolbar.getMenu().findItem(R.id.fontminus).setVisible(false);
                toolbar.getMenu().findItem(R.id.fontplus).setVisible(false);
                toolbar.getMenu().findItem(R.id.reload).setVisible(true);
                return true;
            case R.id.editor:
                webView.setVisibility(View.INVISIBLE);
                editorEdittext.setVisibility(View.VISIBLE);
                toolbar.getMenu().findItem(R.id.webview).setVisible(true);
                toolbar.getMenu().findItem(R.id.editor).setVisible(false);
                toolbar.getMenu().findItem(R.id.fontminus).setVisible(true);
                toolbar.getMenu().findItem(R.id.fontplus).setVisible(true);
                toolbar.getMenu().findItem(R.id.reload).setVisible(false);
                return true;
            case R.id.fontplus:
                fontSize(1);
                return true;
            case R.id.fontminus:
                fontSize(-1);
                return true;
            case R.id.reload:
                webView.reload();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
