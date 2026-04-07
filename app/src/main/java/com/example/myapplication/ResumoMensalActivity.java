package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    private static final String TAG = "ResumoMensal";

    private TextView txtTotalMes, txtMaiorDespesa, txtMenorDespesa;
    private BarChart barChart;
    private Spinner spinnerFiltroMes;

    private final String[] mesesLista = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_mensal);

        // 1. Inicializar componentes da tela
        txtTotalMes = findViewById(R.id.txtTotalMesResumo);
        txtMaiorDespesa = findViewById(R.id.txtMaiorDespesa);
        txtMenorDespesa = findViewById(R.id.txtMenorDespesa);
        barChart = findViewById(R.id.barChart);
        spinnerFiltroMes = findViewById(R.id.spinnerFiltroMes);

        // 2. Configurar o Spinner de meses
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mesesLista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroMes.setAdapter(adapter);

        // 3. Listener para quando o usuário mudar o mês
        spinnerFiltroMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarDadosPorMes(mesesLista[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 4. Botão Voltar
        findViewById(R.id.btnVoltarResumo).setOnClickListener(v -> finish());
    }

    private void carregarDadosPorMes(String mesFiltro) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DESPESAS, "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> todasDespesas = gson.fromJson(json, type);

        double totalMes = 0.0;
        double maior = 0.0;
        double menor = Double.MAX_VALUE;
        HashMap<String, Double> gastosPorCategoria = new HashMap<>();

        if (todasDespesas != null) {
            for (String d : todasDespesas) {
                try {
                    // Esperado: "Janeiro - Alimentação : 1500.0"
                    String[] partes = d.split(" - ");
                    if (partes.length < 2) continue;

                    String mesData = partes[0].trim();

                    if (mesData.equalsIgnoreCase(mesFiltro)) {
                        String[] categoriaEValor = partes[1].split(" : ");
                        if (categoriaEValor.length < 2) continue;

                        String categoria = categoriaEValor[0].trim();
                        double valor = Double.parseDouble(categoriaEValor[1].trim());

                        totalMes += valor;
                        if (valor > maior) maior = valor;
                        if (valor < menor) menor = valor;

                        // Soma o valor à categoria existente ou começa do zero
                        Double valorExistente = gastosPorCategoria.get(categoria);
                        gastosPorCategoria.put(categoria, (valorExistente == null ? 0.0 : valorExistente) + valor);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao processar linha: " + d, e);
                }
            }
        }

        // 5. Atualizar Interface (KZ)
        txtTotalMes.setText(String.format(Locale.US, "Total: %.2f KZ", totalMes));
        txtMaiorDespesa.setText(String.format(Locale.US, "Maior: %.2f KZ", maior));
        txtMenorDespesa.setText(String.format(Locale.US, "Menor: %.2f KZ", (totalMes == 0 ? 0 : menor)));

        atualizarGrafico(gastosPorCategoria);
    }

    private void atualizarGrafico(HashMap<String, Double> dados) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : dados.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            barChart.clear();
            barChart.setNoDataText("Nenhuma despesa em " + spinnerFiltroMes.getSelectedItem().toString());
            barChart.invalidate();
            return;
        }

        // Configuração visual do conjunto de dados
        BarDataSet dataSet = new BarDataSet(entries, "Despesas por Categoria (KZ)");
        dataSet.setColor(Color.parseColor("#4F46E5")); // Azul Indigo
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Configuração do Eixo X (Categorias embaixo)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(labels.size());

        // Ajustes finos do gráfico
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false); // Esconde legenda repetitiva
        barChart.animateY(1000); // Animação de subida
        barChart.setFitBars(true);
        barChart.invalidate(); // Refresh
    }
}

