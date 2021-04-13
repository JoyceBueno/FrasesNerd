package com.br.frasesnerdsii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Area responsavel por fazer o Splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAutenticacao();
            }
        }, 2000);
    }

    //Area responsavel por fazer a chamada da autenticacao
    private void abrirAutenticacao(){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish(); //  para encerrar a acticity
    }
}