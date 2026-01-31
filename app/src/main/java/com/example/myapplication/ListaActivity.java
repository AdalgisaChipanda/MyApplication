package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ListaActivity extends AppCompatActivity {

    private final ArrayList<String> todasDespesas = new ArrayList<>();
    private final ArrayList<String> despesasFiltradas = new ArrayList<>();
    private ArrayAdapter<String> adapterLista;
    private TextView txtTotalMes;
    private Spinner spinnerMes;

    private SharedPreferences prefs;
    private static final String PREFS_DESPESAS = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        prefs = getSharedPreferences(PREFS_DESPESAS, Context.MODE_PRIVATE);

        // Views
        ListView listView = findViewById(R.id.listViewDespesas);
        txtTotalMes = findViewById(R.id.txtTotalMes);
        spinnerMes = findViewById(R.id.spinnerMesLista);

        Button btnAdicionar = findViewById(R.id.btnAdicionarDespesa);
        Button btnVerGrafico = findViewById(R.id.btnVerGrafico);
        Button btnVoltar = findViewById(R.id.btnVoltar);
        TextView txtSaberMais = findViewById(R.id.txtMarcaRodape);

        // Spinner de meses
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

        // Adapter da lista
        adapterLista = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, despesasFiltradas);
        listView.setAdapter(adapterLista);

        // Carregar despesas salvas
        carregarDespesas();
        filtrarPorMes(spinnerMes.getSelectedItem().toString());

        // Botões
        btnAdicionar.setOnClickListener(v ->
                startActivity(new Intent(this, AddActivity.class))
        );

        btnVerGrafico.setOnClickListener(v -> {
            Intent intent = new Intent(this, GraficoActivity.class);
            intent.putStringArrayListExtra("despesas", todasDespesas);
            startActivity(intent);
        });

        btnVoltar.setOnClickListener(v -> finish());

        txtSaberMais.setOnClickListener(v ->
                startActivity(new Intent(ListaActivity.this, SobreActivity.class))
        );

        spinnerMes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                filtrarPorMes(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void carregarDespesas() {
        Gson gson = new Gson();
        String json = prefs.getString(KEY_DESPESAS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> listaSalva = gson.fromJson(json, type);
            todasDespesas.clear();
            todasDespesas.addAll(listaSalva);
        }
    }

    private void filtrarPorMes(String mes) {
        despesasFiltradas.clear();
        double total = 0.0;
        for (String d : todasDespesas) {
            if (mes.equals(getString(R.string.mes_todos)) || d.startsWith(mes)) {
                despesasFiltradas.add(d);
                try {
                    String valorStr = d.substring(d.indexOf(":") + 1).trim();
                    total += Double.parseDouble(valorStr);
                } catch (Exception ignored) {}
            }
        }
        adapterLista.notifyDataSetChanged();
        txtTotalMes.setText(String.format(Locale.getDefault(), getString(R.string.total_mes), total));
    }
}
