package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResumoMensalActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    private TextView txtTotalMes;
    private TextView txtMaiorDespesa;
    private TextView txtMenorDespesa;
    private TextView txtCategoriaMaisUsada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_mensal);

        txtTotalMes = findViewById(R.id.txtTotalMesResumo);
        txtMaiorDespesa = findViewById(R.id.txtMaiorDespesa);
        txtMenorDespesa = findViewById(R.id.txtMenorDespesa);
        txtCategoriaMaisUsada = findViewById(R.id.txtCategoriaMaisUsada);

        // Carregar resumo das despesas
        carregarResumo();

        // Botão Voltar
        Button btnVoltar = findViewById(R.id.btnVoltarResumo);
        btnVoltar.setOnClickListener(v -> finish()); // volta para a tela anterior

        // Saber mais
        TextView txtSaberMais = findViewById(R.id.txtMarcaRodapeResumo);
        txtSaberMais.setOnClickListener(v ->
                startActivity(new Intent(ResumoMensalActivity.this, SobreActivity.class))
        );
    }

    private void carregarResumo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DESPESAS, null);
        ArrayList<String> todasDespesas = new ArrayList<>();

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            todasDespesas = gson.fromJson(json, type);
        }

        if (todasDespesas.isEmpty()) {
            txtTotalMes.setText(getString(R.string.total_mes_default));
            txtMaiorDespesa.setText(getString(R.string.maior_despesa_default));
            txtMenorDespesa.setText(getString(R.string.menor_despesa_default));
            txtCategoriaMaisUsada.setText(getString(R.string.categoria_mais_usada_default));
            return;
        }

        double total = 0.0;
        double maior = Double.MIN_VALUE;
        double menor = Double.MAX_VALUE;
        HashMap<String, Integer> categorias = new HashMap<>();

        for (String d : todasDespesas) {
            try {
                String[] partes = d.split(" - ");
                String categoria = partes[1].split(":")[0].trim();
                double valor = Double.parseDouble(partes[1].split(":")[1].trim());

                total += valor;
                if (valor > maior) maior = valor;
                if (valor < menor) menor = valor;

                categorias.compute(categoria, (key, value) -> (value == null ? 0 : value) + 1);
            } catch (Exception ignored) {}
        }

        String categoriaMaisUsada = "N/A";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categorias.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                categoriaMaisUsada = entry.getKey();
            }
        }

        txtTotalMes.setText(String.format(Locale.getDefault(),
                getString(R.string.total_mes_format), total));
        txtMaiorDespesa.setText(String.format(Locale.getDefault(),
                getString(R.string.maior_despesa_format), maior));
        txtMenorDespesa.setText(String.format(Locale.getDefault(),
                getString(R.string.menor_despesa_format), menor));
        txtCategoriaMaisUsada.setText(String.format(
                getString(R.string.categoria_mais_usada_format), categoriaMaisUsada));
    }
}


