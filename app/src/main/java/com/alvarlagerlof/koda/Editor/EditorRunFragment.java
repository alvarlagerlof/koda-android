package com.alvarlagerlof.koda.Editor;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.WebClient;

/**
 * Created by alvar on 2016-11-08.
 */

public class EditorRunFragment extends Fragment {

    private String title;
    private String code;

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.editor_run_fragment, container, false);
        webView = (WebView) view.findViewById(R.id.webview);

        return view;
    }

    public void startLoad() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadDataWithBaseURL("file:///android-asset", code, "text/html", "UTF-8", null);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setWebChromeClient(new WebClient().getClient(getContext(), title));
        webView.setLayerType((Build.VERSION.SDK_INT >= 19) ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_SOFTWARE, null);
    }

    public void clearWebView() {
        webView.loadUrl("settings_about:blank");
        webView.pauseTimers();
    }

    public void reload() {
        webView.reload();
    }


    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }





}
