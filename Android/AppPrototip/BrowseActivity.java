package ortega.tomas.hlstream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class BrowseActivity extends AppCompatActivity {

    private EditText barraURL;
    private WebView webView;

    private TextView.OnEditorActionListener goListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (textView == barraURL && actionId == EditorInfo.IME_ACTION_GO) {
                String inputBarra = barraURL.getText().toString();
                if (!inputBarra.startsWith("http://")) inputBarra = "http://" + inputBarra;
                webView.loadUrl(inputBarra);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        barraURL = (EditText) findViewById(R.id.editText);
        barraURL.setOnEditorActionListener(goListener);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new myWebViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class myWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith("m3u8")) {
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("uri" , url);
                startActivity(intent);
                return true;
            }
            return false;
        }
    }
}
