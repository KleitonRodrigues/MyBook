package com.mybook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListActivity extends AppCompatActivity {
    private RequestQueue mQueue2;
    public String sValor="Kotlin";
    ProgressBar progressBar;
    private TextView tvReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        tvReturn = findViewById(R.id.tvAlReturn);
        mQueue2 = Volley.newRequestQueue(this);
        //
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
        String history1 = myPreferences.getString("HISTORY1", "");
        getSupportActionBar().setTitle("SEARCH");
        getSupportActionBar().setSubtitle("In Your Library | Search by: "+history1);
        getBooks(history1);
    }

    private void getBooks(String value) {
        String url = "https://itunes.apple.com/search?term="+value+"&entity=ibook";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject book = jsonArray.getJSONObject(i);
                                String artistIds = book.getString("artistIds");
                                String artworkUrl60 = book.getString("artworkUrl60");
                                String trackCensoredName = book.getString("trackCensoredName");
                                String price = book.getString("price");

                                tvReturn.append("<img src='"+artworkUrl60+"'><b>"+trackCensoredName + "</b> <br>R$ "+ price + "<br>" +
                                        "<input type='button' value='Detail' onClick=\\\"alert('Hello Android!');\\\" />\n<hr>");
                                sValor = tvReturn.getText().toString();
                                // Web View
                                WebView mbrowser = (WebView) findViewById(R.id.webview_list);
                                // Configurações da ProgressBar no WebView
                                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                progressBar.getIndeterminateDrawable().setColorFilter(0xFF77b3d4, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                progressBar.setMax(100);
                                String unencodedHtml ="<!DOCTYPE html><html><head><script type=\"text/javascript\">\n" +
                                        "    function showAndroidToast(toast) {\n" +
                                        "        Android.showToast(toast);\n" +
                                        "    }\n" +
                                        "</script><body>"+
                                        sValor+"</body></head></html>";
                                String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
                                mbrowser.loadData(encodedHtml, "text/html", "base64");
                                //
                                mbrowser.getSettings().setJavaScriptEnabled(true);
                                /*class JsObject {
                                    @JavascriptInterface
                                    public String toString() { return "Android"; }
                                }*/
                                //mbrowser.addJavascriptInterface(new JsObject(), "Android");
                                mbrowser.setWebViewClient(new ListActivity.HelloWebViewClient());
                                mbrowser.setWebChromeClient(new WebChromeClient(){
                                    @Override
                                    public void onProgressChanged(WebView view, int newProgress) {
                                        super.onProgressChanged(view, newProgress);
                                        progressBar.setProgress(newProgress);
                                        if(newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE){
                                            progressBar.setVisibility(ProgressBar.VISIBLE);
                                        }
                                        progressBar.setProgress(newProgress);
                                        if(newProgress == 100) {
                                            progressBar.setVisibility(ProgressBar.GONE);
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue2.add(request);
    }
    // Classe ultilizada para funcionalidade do ProgressBar
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    // Função ultilizada para Voltar para tela inicial no Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void janela2(String title, String value){
        AlertDialog alert = new AlertDialog.Builder(ListActivity.this).create();
        alert.setTitle(title);
        alert.setMessage(value);
        alert.show();
        //Toast.makeText(MainActivity.this, "Testesss", Toast.LENGTH_SHORT).show();
    }

    public class WebAppInterface extends Activity {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}