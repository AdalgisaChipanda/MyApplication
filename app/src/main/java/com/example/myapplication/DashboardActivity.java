package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Botões principais
        Button btnLista = findViewById(R.id.btnLista);
        Button btnHistorico = findViewById(R.id.btnHistoricoCompleto);
        Button btnResumo = findViewById(R.id.btnResumoMensal);

        // Rodapé "Saber mais"
        TextView txtMarcaRodape = findViewById(R.id.txtMarcaRodape);

        // Clique em Lista de Despesas
        btnLista.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ListaActivity.class);
            startActivity(intent);
        });

        // Clique em Histórico Completo
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, HistoricoCompletoActivity.class);
            startActivity(intent);
        });

        // Clique em Resumo Mensal
        btnResumo.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ResumoMensalActivity.class);
            startActivity(intent);
        });

        // Clique em Saber mais -> abrir sua tela de informações do dispositivo (SobreActivity)
        txtMarcaRodape.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SobreActivity.class);
            startActivity(intent);
        });
    }
}
