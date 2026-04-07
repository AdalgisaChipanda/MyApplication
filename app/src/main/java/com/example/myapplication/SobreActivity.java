package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        // IDs atualizados para coincidir com o XML abaixo
        TextView txtInfo = findViewById(R.id.txtCreditosSobre);
        MaterialButton btnVoltar = findViewById(R.id.btnVoltarSobre);

        // Configura o texto informativo vindo do strings.xml
        if (txtInfo != null) {
            txtInfo.setText(getString(R.string.info_app));
        }

        // Fecha a tela ao clicar em voltar
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }
    }
}
