package com.alvarlagerlof.koda.QrCodeShare;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alvarlagerlof.koda.Play.PlayActivity;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alvar-mac on 2/11/17.
 */

public class QrScanner extends AppCompatActivity {

    private QRCodeReaderView qrScannerView;

    private final int FLASH_OFF = 0;
    private final int FLASH_ON = 1;

    private int FLASH_STATE = FLASH_OFF;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner_activity);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);


        // Scanner
        qrScannerView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrScannerView.setQRDecodingEnabled(true);
        qrScannerView.setAutofocusInterval(100L);
        qrScannerView.setBackCamera();
        qrScannerView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {

                try {

                    Bundle params = new Bundle();
                    FirebaseAnalytics.getInstance(QrScanner.this).logEvent("qr_scan_successful", params);

                    JSONObject jsonObject = new JSONObject(text);

                    Intent intent = new Intent(QrScanner.this, PlayActivity.class);
                    intent.putExtra("url", Base64Utils.decode(jsonObject.getString("url")));
                    intent.putExtra("title", Base64Utils.decode(jsonObject.getString("title")));
                    intent.putExtra("author", Base64Utils.decode(jsonObject.getString("author")));
                    startActivity(intent);
                    qrScannerView.setQRDecodingEnabled(false);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        qrScannerView.startCamera();
        qrScannerView.setQRDecodingEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrScannerView.stopCamera();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scan, menu);

        if (FLASH_STATE == FLASH_ON) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.flash_off:
                Bundle params = new Bundle();
                FirebaseAnalytics.getInstance(QrScanner.this).logEvent("qr_scan_flash_on", params);

                qrScannerView.setTorchEnabled(true);
                FLASH_STATE = FLASH_ON;
                invalidateOptionsMenu();
                break;

            case R.id.flash_on:
                Bundle params2 = new Bundle();
                FirebaseAnalytics.getInstance(QrScanner.this).logEvent("qr_scan_flash_off", params2);

                qrScannerView.setTorchEnabled(false);
                FLASH_STATE = FLASH_OFF;
                invalidateOptionsMenu();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
