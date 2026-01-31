package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class HistoricoCompletoActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_completo);

        final ListView listView = findViewById(R.id.listViewHistorico);
        final EditText edtBuscar = findViewById(R.id.edtBuscarHistorico);
        final Spinner spinnerMesFiltro = findViewById(R.id.spinnerMesFiltro);

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Botão Voltar
        Button btnVoltar = findViewById(R.id.btnVoltarHistorico);
        btnVoltar.setOnClickListener(v -> finish()); // volta para tela anterior (Dashboard)

        // Rodapé "Saber mais"
        TextView txtMarcaRodape = findViewById(R.id.txtMarcaRodape);
        txtMarcaRodape.setOnClickListener(v ->
                startActivity(new Intent(HistoricoCompletoActivity.this, SobreActivity.class))
        );

        // Listas para despesas
        final ArrayList<String> todasDespesas = new ArrayList<>();
        final ArrayList<String> despesasFiltradas = new ArrayList<>();

        // Carregar despesas do SharedPreferences
        Gson gson = new Gson();
        String json = prefs.getString(KEY_DESPESAS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> lista = gson.fromJson(json, type);
            if (lista != null) {
                todasDespesas.addAll(lista);
            }
        }

        // Spinner de meses
        final ArrayList<String> meses = new ArrayList<>(Arrays.asList(
                getString(R.string.mes_todos),
                "Janeiro","Fevereiro","Março","Abril",
                "Maio","Junho","Julho","Agosto",
                "Setembro","Outubro","Novembro","Dezembro"
        ));
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMesFiltro.setAdapter(adapterMes);

        // Adapter da lista
        final ArrayAdapter<String> adapterLista = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, despesasFiltradas);
        listView.setAdapter(adapterLista);

        // Método para filtrar despesas
        Runnable filtrar = () -> {
            String busca = edtBuscar.getText().toString().toLowerCase(Locale.getDefault());
            String mesSelecionado = spinnerMesFiltro.getSelectedItem().toString();

            despesasFiltradas.clear();
            for (String d : todasDespesas) {
                boolean contemBusca = d.toLowerCase(Locale.getDefault()).contains(busca);
                boolean correspondeMes = mesSelecionado.equals(getString(R.string.mes_todos)) || d.startsWith(mesSelecionado);
                if (contemBusca && correspondeMes) {
                    despesasFiltradas.add(d);
                }
            }
            adapterLista.notifyDataSetChanged();
        };

        // Busca em tempo real
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
                filtrar.run();
            }
            @Override
            public void afterTextChanged(Editable s){}
        });

        // Filtra ao mudar o mês
        spinnerMesFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                filtrar.run();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Inicializa filtro completo
        filtrar.run();
    }
}
