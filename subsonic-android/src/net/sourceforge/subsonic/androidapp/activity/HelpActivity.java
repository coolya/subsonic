/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */

package net.sourceforge.subsonic.androidapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * An HTML-based help screen with Back and Done buttons at the bottom.
 *
 * @author Sindre Mehus
 */
public final class HelpActivity extends Activity {
    // TODO
    private static final String DEFAULT_URL = "file:///android_asset/html/en/index.html";

    private WebView webView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.help);
        setProgressBarVisibility(true);
//        setProgressBarVisibility(true);

        webView = (WebView) findViewById(R.id.help_contents);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                setProgress(progress * 1000);
                Log.i("FOO", "Progress: " + progress);
            }
        });

        webView.setWebViewClient(new HelpClient());
        if (bundle != null) {
            webView.restoreState(bundle);
        } else {
            webView.loadUrl(DEFAULT_URL);
        }

        backButton = (Button) findViewById(R.id.help_back);
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.goBack();
            }
        });

        Button doneButton = (Button) findViewById(R.id.help_close);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button donateButton = (Button) findViewById(R.id.help_donate);
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATION_URL)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        webView.saveState(state);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private final class HelpClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
//            setTitle(view.getTitle());
            backButton.setEnabled(view.canGoBack());
            setProgressBarVisibility(true);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Util.toast(HelpActivity.this, description);
        }

    }

}
