package com.alvarlagerlof.koda.QrCodeShare;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alvar on 2016-09-01.
 */
public class QrViewer extends AppCompatActivity {


    ImageView qrImageView;
    ProgressBar progressBar;
    TextView howToScan;

    RelativeLayout progressLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_view_activity);

        qrImageView = (ImageView) findViewById(R.id.qr_image_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        howToScan = (TextView) findViewById(R.id.how_to_scan);

        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        //progressLayout.setLayoutParams(new RelativeLayout.LayoutParams(progressLayout.getMeasuredWidth(), progressLayout.getMeasuredWidth()));

        DisplayMetrics displayMetrics = QrViewer.this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        dpWidth -= 110;

        Resources r = getResources();
        int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, r.getDisplayMetrics()));
        progressLayout.getLayoutParams().height = px;



        JSONObject data = new JSONObject();
        try {
            data.put("url", Base64Utils.encode(getIntent().getExtras().getString("url")));
            data.put("title", Base64Utils.encode(getIntent().getExtras().getString("title")));
            data.put("author", Base64Utils.encode(getIntent().getExtras().getString("author")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        generate generate = new generate();
        generate.execute(data.toString());


        Bundle params = new Bundle();
        FirebaseAnalytics.getInstance(QrViewer.this).logEvent("qr_share", params);

    }

    class generate extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = null;

            try {
                bitmap = encodeAsBitmap(strings[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {

                qrImageView.setImageBitmap(bitmap);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setDuration(300);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //projects_add this
                fadeIn.setDuration(300);
                fadeIn.setStartOffset(300);
                fadeIn.setFillAfter(true);

                qrImageView.setVisibility(View.VISIBLE);
                howToScan.setVisibility(View.VISIBLE);

                progressBar.startAnimation(fadeOut);
                qrImageView.startAnimation(fadeIn);
                howToScan.startAnimation(fadeIn);

            }
        }
    }


    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 250, 250, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(this, R.color.black):ContextCompat.getColor(this, R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 250, 0, 0, w, h);
        return bitmap;
    }


    public void finish(View view) {
        finish();
    }



}
