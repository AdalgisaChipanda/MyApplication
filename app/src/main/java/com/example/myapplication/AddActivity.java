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

        EditText edtDescricao = findViewById(R.id.edtDescricao);
        EditText edtValor = findViewById(R.id.edtValor);
        Spinner spinnerMes = findViewById(R.id.spinnerMes);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnVoltar = findViewById(R.id.btnVoltarAdd);
        TextView txtSaberMais = findViewById(R.id.txtSaberMaisAdd);

        // Spinner com meses
        ArrayList<String> meses = new ArrayList<>(Arrays.asList(
                getString(R.string.mes_todos),
                "Janeiro","Fevereiro","Março","Abril",
                "Maio","Junho","Julho","Agosto",
                "Setembro","Outubro","Novembro","Dezembro"
        ));
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

        // Salvar despesa
        btnSalvar.setOnClickListener(v -> {
            String descricao = edtDescricao.getText().toString().trim();
            String valor = edtValor.getText().toString().trim();
            String mes = spinnerMes.getSelectedItem().toString();

            if (!descricao.isEmpty() && !valor.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences(PREFS_DESPESAS, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String>>() {}.getType();

                String json = prefs.getString(KEY_DESPESAS, "[]");
                ArrayList<String> todasDespesas = gson.fromJson(json, type);

                String item = mes + " - " + descricao + " : " + valor;
                todasDespesas.add(item);

                prefs.edit().putString(KEY_DESPESAS, gson.toJson(todasDespesas)).apply();

                setResult(RESULT_OK);
                finish();
            }
        });

        // Voltar sem salvar
        btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Saber mais → SobreActivity
        txtSaberMais.setOnClickListener(v ->
                startActivity(new Intent(AddActivity.this, SobreActivity.class))
        );
    }
}
