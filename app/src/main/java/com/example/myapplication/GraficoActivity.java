package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraficoActivity extends AppCompatActivity {

    private static final String PREFS_DESPESAS = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        LinearLayout barChart = findViewById(R.id.barChartContainer);
        LinearLayout legendaContainer = findViewById(R.id.legendaContainer);
        Button btnVoltar = findViewById(R.id.btnVoltarGrafico);
        TextView txtMarca = findViewById(R.id.txtMarca);

        // Rodapé "Saber mais"
        txtMarca.setText(getString(R.string.marca_app));
        txtMarca.setOnClickListener(v ->
                startActivity(new Intent(GraficoActivity.this, SobreActivity.class))
        );

        // Ler despesas salvas
        SharedPreferences prefs = getSharedPreferences(PREFS_DESPESAS, MODE_PRIVATE);
        String json = prefs.getString(KEY_DESPESAS, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> todasDespesas = gson.fromJson(json, type);

        // Agrupar despesas por mês
        Map<String, Double> despesasPorMes = new HashMap<>();
        for (String d : todasDespesas) {
            String mes = d.split(" - ")[0];
            double valorTemp = 0.0;
            try {
                valorTemp = Double.parseDouble(d.substring(d.indexOf(":") + 1).trim());
            } catch (Exception ignored) {}
            // Se já existe, somamos; senão, inicializamos
            Double total = despesasPorMes.get(mes);
            despesasPorMes.put(mes, (total != null ? total : 0.0) + valorTemp);
        }

        // Meses do ano
        String[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

        // Encontrar valor máximo para escala do gráfico
        double maxValor = 1;
        for (Double v : despesasPorMes.values())
            if (v != null && v > maxValor) maxValor = v;

        // Criar gráfico
        for (String mes : meses) {
            Double valorObj = despesasPorMes.get(mes);
            double valor = (valorObj != null) ? valorObj : 0.0; // safe unboxing

            // Barra
            View barra = new View(this);
            int altura = (int) (valor / maxValor * 300); // altura proporcional
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, altura, 1f);
            params.setMargins(4,0,4,0);
            barra.setLayoutParams(params);

            if (valor <= 200) barra.setBackgroundColor(Color.parseColor("#4CAF50")); // verde
            else if (valor <= 500) barra.setBackgroundColor(Color.parseColor("#FFC107")); // amarelo
            else barra.setBackgroundColor(Color.parseColor("#F44336")); // vermelho

            barChart.addView(barra);

            // Label do mês
            TextView label = new TextView(this);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            labelParams.setMargins(4,4,4,0);
            label.setLayoutParams(labelParams);
            label.setText(mes.substring(0,3));
            label.setTextSize(12);
            label.setTextColor(Color.BLACK);
            label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            legendaContainer.addView(label);
        }

        // Botão Voltar
        btnVoltar.setOnClickListener(v -> finish());
    }
}
