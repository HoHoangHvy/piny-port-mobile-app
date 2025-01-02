package com.example.pinyport.ui;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pinyport.R;

public class PaymentWebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.webView);

        // Get the payment URL from the intent
        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        // Configure WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript (if required by the payment page)
        webSettings.setDomStorageEnabled(true); // Enable DOM storage (if required by the payment page)

        // Set a WebViewClient to handle page navigation within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Load all URLs within the WebView
                view.loadUrl(url);
                return true;
            }
        });

        // Load the payment URL
        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            webView.loadUrl(paymentUrl);
        } else {
            finish(); // Close the activity if the URL is invalid
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Allow navigating back within the WebView
        } else {
            super.onBackPressed(); // Close the activity if no back history
        }
    }
}