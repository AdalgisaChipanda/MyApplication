package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputEditText edtNome = findViewById(R.id.edtNome);
        MaterialButton btnEntrar = findViewById(R.id.btnEntrar);
        TextView txtSaberMais = findViewById(R.id.txtSaberMais);

        // Botão Entrar
        if (btnEntrar != null) {
            btnEntrar.setOnClickListener(v -> {
                if (edtNome != null && edtNome.getText() != null) {
                    String nome = edtNome.getText().toString().trim();

                    if (nome.isEmpty()) {
                        Toast.makeText(this, "Por favor, digite seu nome!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.putExtra("usuario", nome);
                        startActivity(intent);
                    }
                }
            });
        }

        // SABER MAIS → abrir tela SobreActivity
        if (txtSaberMais != null) {
            txtSaberMais.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SobreActivity.class);
                startActivity(intent);
            });
        }
    }
}
