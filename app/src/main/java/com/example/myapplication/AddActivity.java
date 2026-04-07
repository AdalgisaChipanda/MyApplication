package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class AddActivity extends AppCompatActivity {

    private static final String PREFS_DESPESAS = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Inicialização dos componentes
        EditText edtDescricao = findViewById(R.id.edtDescricao);
        EditText edtValor = findViewById(R.id.edtValor);
        Spinner spinnerMes = findViewById(R.id.spinnerMes);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnVoltar = findViewById(R.id.btnVoltarAdd);
        TextView txtSaberMais = findViewById(R.id.txtSaberMaisAdd);

        // Configuração do Spinner (Removido "Todos os meses" para evitar erro no gráfico)
        ArrayList<String> meses = new ArrayList<>(Arrays.asList(
                "Janeiro", "Fevereiro", "Março", "Abril",
                "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"
        ));

        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

        // Lógica para Salvar
        btnSalvar.setOnClickListener(v -> {
            String descricao = edtDescricao.getText().toString().trim();
            String valorStr = edtValor.getText().toString().trim();
            String mesSelecionado = spinnerMes.getSelectedItem().toString();

            if (descricao.isEmpty() || valorStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // SharedPreferences e Gson para persistência
                SharedPreferences prefs = getSharedPreferences(PREFS_DESPESAS, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String>>() {}.getType();

                String json = prefs.getString(KEY_DESPESAS, "[]");
                ArrayList<String> todasDespesas = gson.fromJson(json, type);

                // Salvando no formato que seu app consome
                // DICA: O gráfico usará o 'mesSelecionado' para agrupar os valores
                String item = mesSelecionado + " - " + descricao + " : " + valorStr;
                todasDespesas.add(item);

                prefs.edit().putString(KEY_DESPESAS, gson.toJson(todasDespesas)).apply();

                Toast.makeText(this, "Despesa salva com sucesso!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Erro ao salvar dados.", Toast.LENGTH_SHORT).show();
            }
        });

        // Voltar sem salvar
        btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Link para tela Sobre
        txtSaberMais.setOnClickListener(v ->
                startActivity(new Intent(AddActivity.this, SobreActivity.class))
        );
    }
}