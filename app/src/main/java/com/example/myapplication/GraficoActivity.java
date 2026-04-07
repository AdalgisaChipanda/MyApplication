package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

        txtMarca.setOnClickListener(v ->
                startActivity(new Intent(GraficoActivity.this, SobreActivity.class))
        );

        // Recuperação de dados
        SharedPreferences prefs = getSharedPreferences(PREFS_DESPESAS, MODE_PRIVATE);
        String json = prefs.getString(KEY_DESPESAS, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> todasDespesas = gson.fromJson(json, type);

        Map<String, Double> despesasPorMes = new HashMap<>();
        if (todasDespesas != null) {
            for (String d : todasDespesas) {
                try {
                    String mes = d.split(" - ")[0];
                    double valorTemp = Double.parseDouble(d.substring(d.indexOf(":") + 1).trim());
                    despesasPorMes.merge(mes, valorTemp, Double::sum);
                } catch (Exception ignored) {}
            }
        }

        String[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

        double maxValor = 500;
        for (Double v : despesasPorMes.values()) {
            if (v != null && v > maxValor) maxValor = v;
        }

        barChart.removeAllViews();
        legendaContainer.removeAllViews();

        for (String mes : meses) {
            Double valorObj = despesasPorMes.get(mes);
            double valor = (valorObj != null) ? valorObj : 0.0;

            // --- 1. BARRA RETANGULAR ---
            View barra = new View(this);
            int alturaPixel = (int) (valor / maxValor * 250);
            if (alturaPixel < 8 && valor > 0) alturaPixel = 8;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, alturaPixel, 1f);
            params.setMargins(8, 0, 8, 0); // Espaçamento entre as barras
            params.gravity = Gravity.BOTTOM;
            barra.setLayoutParams(params);

            // Estilização Retangular (Estatística)
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(4); // Apenas 4dp para ser retangular mas profissional

            if (valor == 0) {
                shape.setColor(Color.parseColor("#CBD5E1")); // Cinza neutro para base
            } else if (valor <= 200) {
                shape.setColor(Color.parseColor("#10B981")); // Verde
            } else if (valor <= 500) {
                shape.setColor(Color.parseColor("#F59E0B")); // Amarelo
            } else {
                shape.setColor(Color.parseColor("#EF4444")); // Vermelho
            }
            barra.setBackground(shape);

            // Animação
            ScaleAnimation anim = new ScaleAnimation(1f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            anim.setDuration(900);
            barra.startAnimation(anim);

            barChart.addView(barra);

            // --- 2. LEGENDA (MESES POR BAIXO) ---
            TextView label = new TextView(this);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            label.setLayoutParams(labelParams);
            label.setText(mes.substring(0, 3)); // Jan, Fev, Mar...
            label.setTextSize(11);
            label.setTypeface(null, android.graphics.Typeface.BOLD);
            label.setTextColor(Color.parseColor("#475569"));
            label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            legendaContainer.addView(label);
        }

        btnVoltar.setOnClickListener(v -> finish());
    }
}