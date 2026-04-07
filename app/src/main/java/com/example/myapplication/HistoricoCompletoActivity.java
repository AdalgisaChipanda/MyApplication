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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Botão Voltar
        Button btnVoltar = findViewById(R.id.btnVoltarHistorico);
        btnVoltar.setOnClickListener(v -> finish()); // volta para tela anterior

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

        // Adapter da lista
        final ArrayAdapter<String> adapterLista = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, despesasFiltradas);
        listView.setAdapter(adapterLista);

        // Método para filtrar despesas (Agora apenas por texto e ordem invertida)
        Runnable filtrar = () -> {
            String busca = edtBuscar.getText().toString().toLowerCase(Locale.getDefault());

            despesasFiltradas.clear();

            // --- AQUI ESTÁ A MUDANÇA PARA ORDEM RECENTE ---
            // Loop de trás para frente (size-1 até 0)
            for (int i = todasDespesas.size() - 1; i >= 0; i--) {
                String d = todasDespesas.get(i);

                // Verifica apenas a busca por texto
                boolean contemBusca = d.toLowerCase(Locale.getDefault()).contains(busca);

                if (contemBusca) {
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

        // Inicializa a lista (já exibe tudo invertido ao abrir)
        filtrar.run();
    }
}