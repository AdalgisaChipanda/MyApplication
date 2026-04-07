package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String usuario = getIntent().getStringExtra("usuario");
        TextView txtTitulo = findViewById(R.id.txtDashboardTitulo);

        if (usuario != null && txtTitulo != null) {
            // Usa o template "Olá, %s" definido no seu strings.xml
            txtTitulo.setText(getString(R.string.boas_vindas, usuario));
        }

        // Vincular os Cards do layout
        MaterialCardView cardLista = findViewById(R.id.cardLista);
        MaterialCardView cardHistorico = findViewById(R.id.cardHistorico);
        MaterialCardView cardResumo = findViewById(R.id.cardResumo);
        MaterialButton btnSair = findViewById(R.id.btnVoltarLogin);

        // Ações de clique com os nomes de classes corrigidos
        if (cardLista != null) {
            cardLista.setOnClickListener(v -> abrirTela(ListaActivity.class));
        }

        if (cardHistorico != null) {
            // CORREÇÃO: HistoricoCompletoActivity (com 'o')
            cardHistorico.setOnClickListener(v -> abrirTela(HistoricoCompletoActivity.class));
        }

        if (cardResumo != null) {
            cardResumo.setOnClickListener(v -> abrirTela(ResumoMensalActivity.class));
        }

        if (btnSair != null) {
            btnSair.setOnClickListener(v -> finish());
        }
    }

    // Método auxiliar para navegação com Log de erro profissional
    private void abrirTela(Class<?> activityClass) {
        try {
            startActivity(new Intent(this, activityClass));
        } catch (Exception e) {
            Log.e("Dashboard", "Erro ao abrir: " + activityClass.getSimpleName() +
                    ". Verifique se declarou no AndroidManifest.xml", e);
        }
    }
}