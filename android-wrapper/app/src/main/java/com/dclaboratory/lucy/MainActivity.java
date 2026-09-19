package com.dclaboratory.lucy;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final String LOG_FILE = "hermes_logs.jsonl";

    public class LucyBridge {
        @JavascriptInterface
        public synchronized void appendLog(String line) {
            try (FileOutputStream stream = openFileOutput(LOG_FILE, MODE_APPEND);
                 OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
                writer.write(line);
                writer.write("\n");
            } catch (Exception ignored) { }
        }

        @JavascriptInterface
        public synchronized String readLogs() {
            StringBuilder result = new StringBuilder();
            try (FileInputStream stream = openFileInput(LOG_FILE);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append('\n');
                }
            } catch (Exception ignored) { }
            return result.toString();
        }

        @JavascriptInterface
        public synchronized void clearLogs() {
            deleteFile(LOG_FILE);
        }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WebView view = new WebView(this);
        view.setWebViewClient(new WebViewClient());
        view.addJavascriptInterface(new LucyBridge(), "LucyBridge");
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        view.setBackgroundColor(0xFF020817);
        view.loadUrl("file:///android_asset/lucy.html");
        setContentView(view);
    }
    @Override public void onBackPressed() { super.onBackPressed(); }
}
