package com.alvarlagerlof.koda.Play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.StandardBottomSheetFragment;
import com.alvarlagerlof.koda.WebClient;

public class PlayActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;
    ProgressBar progressBar;

    String url;
    String title;
    String author;



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        }
                    });
        }
        setContentView(R.layout.play_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);




        // From intent
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            url = sharedPreferences.getString("url", "");
            title = sharedPreferences.getString("title", "");
            author = sharedPreferences.getString("author", "");

        } else {
            url = extras.getString("url");
            title = extras.getString("title");
            author = extras.getString("author");
            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("url", url).apply();
            editor.putString("title", title).apply();
            editor.putString("author", author).apply();
        }


        getSupportActionBar().setTitle(title.equals("") ? "Spela" : title);

        load();

    }

    void load() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setWebChromeClient(new WebClient().getClient(this, title));
        webView.setLayerType((Build.VERSION.SDK_INT >= 19) ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.reload:
                load();
                return true;
            case R.id.more:
                StandardBottomSheetFragment bottomSheetFragment = new StandardBottomSheetFragment();
                bottomSheetFragment.passData(url, title, author);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        load();
    }
}
