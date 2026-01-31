package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        TextView txtInfo = findViewById(R.id.txtInfoApp);
        Button btnVoltar = findViewById(R.id.btnVoltarSobre);

        // Usa string de recurso, sem warning e pronto para tradução
        txtInfo.setText(getString(R.string.info_app));

        btnVoltar.setOnClickListener(v -> finish());
    }
}
