package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edtNome = findViewById(R.id.edtNome);
        EditText edtSenha = findViewById(R.id.edtSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        TextView txtMarca = findViewById(R.id.txtMarca);

        // Rodapé “Saber mais”
        txtMarca.setText(getString(R.string.marca_app));
        txtMarca.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SobreActivity.class))
        );

        // Login
        btnEntrar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();

            if (nome.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha nome e senha!", Toast.LENGTH_SHORT).show();
            } else {
                // Vai para o Dashboard
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("usuario", nome);
                startActivity(intent);
            }
        });
    }
}

