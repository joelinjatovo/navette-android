package com.navetteclub.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.navetteclub.BuildConfig;
import com.navetteclub.R;

public class SplashActivity extends AppCompatActivity {
    private static boolean loaded = false;

    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!loaded) {
            setContentView(R.layout.activity_splash);
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            TextView textView = findViewById(R.id.version_text_view);
            textView.setText(BuildConfig.VERSION_NAME);
            int secondsDelayed = 2;
            handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                finish();
            }, secondsDelayed * 1000);
            loaded = true;
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
