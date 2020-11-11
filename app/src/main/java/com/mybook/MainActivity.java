package com.mybook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private EditText txtSearch;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewResult    = findViewById(R.id.tvResult);
        Button buttonParse = findViewById(R.id.btnSearch);
        txtSearch          = (EditText) findViewById(R.id.etMain_book);
        txtTitle           = (TextView) findViewById(R.id.tvTitle);
        mQueue             = Volley.newRequestQueue(this);

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSearch.getText().length()>0) {
                    gravarHistorico(v);
                    startActivity(new Intent(MainActivity.this, ListActivity.class));
                }else{
                    showMsg("Info","Enter a value in the search!");
                }
            }
        });
    }

    public void gravarHistorico(View view){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("HISTORY1", txtSearch.getText().toString() );
        myEditor.commit();
        String history1 = myPreferences.getString("HISTORY1", "");
        // Armazena Historico das consultas
        txtTitle.setText(txtSearch.getText() + "\n" + txtTitle.getText());
    }

    // Exibe mensagem customizada
    public void showMsg(String title, String value){
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
        alert.setTitle(title);
        alert.setMessage(value);
        alert.show();
    }

}