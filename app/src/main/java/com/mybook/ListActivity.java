package com.mybook;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    public String sValueHtml;
    ProgressBar progressBar;
    private TextView tvReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        // Inicializa referência dos componentes
        tvReturn = findViewById(R.id.tvAlReturn);
        mQueue2 = Volley.newRequestQueue(this);
        // Armazena Consulta na Memoria do smartphone
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
        String history1 = myPreferences.getString("HISTORY1", "");
        // Exibe configura ActionBar
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                                String description = book.getString("description");
                                String trackCensoredName = book.getString("trackCensoredName");
                                String price = book.getString("price");

                                tvReturn.append(
                                  "<div class=\"col-sm\">\n" +
                                        "   <img src='" + artworkUrl60 + "'>\n" +
                                        "</div>" +
                                        "<div class=\"col-sm\">\n" +
                                        "   <h5>" + trackCensoredName + "</h5>\n" +
                                        "   <p>R$ "+ price + "</p>\n" +
                                        "   <p><a class=\"btn btn-info\" data-toggle=\"modal\" data-target=\"#modalExemplo"+artistIds+"\" role=\"button\">View details &raquo;</a></p>\n" +
                                        "</div><div class=\"modal fade\" id=\"modalExemplo"+artistIds+"\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"exampleModalLabel\" aria-hidden=\"true\">\n" +
                                          "  <div class=\"modal-dialog\" role=\"document\">\n" +
                                          "    <div class=\"modal-content\">\n" +
                                          "      <div class=\"modal-header\">\n" +
                                          "        <h5>" + trackCensoredName + "</h5>\n" +
                                          "        <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                                          "          <span aria-hidden=\"true\">&times;</span>\n" +
                                          "        </button>\n" +
                                          "      </div>\n" +
                                          "      <div class=\"modal-body\">\n" +
                                          "      <p>R$ "+ description + "</p>\n" +
                                          "      <p><strong>R$ "+ price + "</strong></p>\n" +
                                          "      </div>\n" +
                                          "      <div class=\"modal-footer\">\n" +
                                          "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Close</button>\n" +
                                          "      </div>\n" +
                                          "    </div>\n" +
                                          "  </div>\n" +
                                          "</div>" +
                                          "<hr>");
                                sValueHtml = tvReturn.getText().toString();
                                // Web View
                                WebView mbrowser = (WebView) findViewById(R.id.webview_list);
                                // Configurações da ProgressBar no WebView
                                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                progressBar.getIndeterminateDrawable().setColorFilter(0xFF77b3d4, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                progressBar.setMax(100);
                                String unencodedHtml ="<html><head><link href=\"https://getbootstrap.com/docs/4.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">" +
                                        "</head><body><div class=\"container\"><div class=\"row\">" +
                                        "          "+sValueHtml+"</div></div></body> <script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>" +
                                        "    <script src=\"https://getbootstrap.com/docs/4.0/dist/js/bootstrap.min.js\"></script>\n</html>";
                                String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
                                mbrowser.loadData(encodedHtml, "text/html", "base64");
                                mbrowser.getSettings().setJavaScriptEnabled(true);
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

}