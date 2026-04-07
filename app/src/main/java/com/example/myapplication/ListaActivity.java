package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private DespesaAdapter adapterLista; // Adaptador personalizado
    private TextView txtTotalMes;
    private Spinner spinnerMes;

    private SharedPreferences prefs;
    private static final String PREFS_DESPESAS = "MinhasDespesasPrefs";
    private static final String KEY_DESPESAS = "despesas";

    // Tag usada para marcar itens excluídos sem apagá-los do histórico
    private static final String TAG_EXCLUIDO = "[X] ";

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

        // Spinner
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

        // Configura o Adaptador Personalizado
        adapterLista = new DespesaAdapter(this, despesasFiltradas);
        listView.setAdapter(adapterLista);

        // Botões
        btnAdicionar.setOnClickListener(v ->
                startActivity(new Intent(this, AddActivity.class))
        );

        btnVerGrafico.setOnClickListener(v -> {
            Intent intent = new Intent(this, GraficoActivity.class);
            // Passamos a lista completa (incluindo excluídos, o gráfico deve tratar ou exibir tudo)
            intent.putStringArrayListExtra("despesas", todasDespesas);
            startActivity(intent);
        });

        btnVoltar.setOnClickListener(v -> finish());

        txtSaberMais.setOnClickListener(v ->
                startActivity(new Intent(ListaActivity.this, SobreActivity.class))
        );

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarPorMes(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDespesas();
        if (spinnerMes != null && spinnerMes.getSelectedItem() != null) {
            filtrarPorMes(spinnerMes.getSelectedItem().toString());
        }
    }

    private void carregarDespesas() {
        Gson gson = new Gson();
        String json = prefs.getString(KEY_DESPESAS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> listaSalva = gson.fromJson(json, type);
            todasDespesas.clear();
            if (listaSalva != null) {
                todasDespesas.addAll(listaSalva);
            }
        }
    }

    private void filtrarPorMes(String mes) {
        despesasFiltradas.clear();
        double total = 0.0;

        // Loop invertido para mostrar recentes primeiro
        for (int i = todasDespesas.size() - 1; i >= 0; i--) {
            String d = todasDespesas.get(i);

            // Verifica se está marcado como excluído (começa com [X])
            boolean isExcluido = d.startsWith(TAG_EXCLUIDO);

            // Só exibe se NÃO estiver excluído E corresponder ao mês
            if (!isExcluido && (mes.equals(getString(R.string.mes_todos)) || d.startsWith(mes))) {
                despesasFiltradas.add(d);
                try {
                    String valorStr = d.substring(d.indexOf(":") + 1).trim();
                    total += Double.parseDouble(valorStr);
                } catch (Exception ignored) {}
            }
        }

        if (adapterLista != null) adapterLista.notifyDataSetChanged();
        if (txtTotalMes != null) {
            txtTotalMes.setText(String.format(Locale.getDefault(), getString(R.string.total_mes), total));
        }
    }

    // Método chamado ao clicar no X
    private void excluirDespesa(int position) {
        // A posição recebida é da lista FILTRADA (que está invertida e filtrada)
        String itemParaExcluir = despesasFiltradas.get(position);

        // Precisamos achar esse item na lista ORIGINAL (todasDespesas)
        // Como strings podem ser iguais, procuramos de trás para frente para achar o mais recente correspondente
        int indexOriginal = -1;
        for (int i = todasDespesas.size() - 1; i >= 0; i--) {
            if (todasDespesas.get(i).equals(itemParaExcluir)) {
                indexOriginal = i;
                break;
            }
        }

        if (indexOriginal != -1) {
            // Em vez de remover, adicionamos a marca [X] no início
            String itemMarcado = TAG_EXCLUIDO + itemParaExcluir;
            todasDespesas.set(indexOriginal, itemMarcado);

            // Salva no SharedPreferences
            salvarAlteracoes();

            // Atualiza a tela (vai sumir da lista visual, mas fica no salvo)
            filtrarPorMes(spinnerMes.getSelectedItem().toString());
            Toast.makeText(this, "Despesa removida da lista", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(todasDespesas);
        editor.putString(KEY_DESPESAS, json);
        editor.apply();
    }

    // --- CLASSE DO ADAPTADOR PERSONALIZADO ---
    // Esta classe controla como cada linha aparece (texto + botão X)
    private class DespesaAdapter extends ArrayAdapter<String> {
        public DespesaAdapter(Context context, ArrayList<String> despesas) {
            super(context, 0, despesas);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Se não houver uma view reciclável, cria uma nova usando nosso layout customizado
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_despesa_custom, parent, false);
            }

            // Pega o item atual
            String despesa = getItem(position);

            // Vincula os componentes do layout
            TextView txtDescricao = convertView.findViewById(R.id.txtDescricaoDespesa);
            ImageView btnExcluir = convertView.findViewById(R.id.btnExcluirItem);

            // Define o texto
            txtDescricao.setText(despesa);

            // Define a ação do clique no X
            btnExcluir.setOnClickListener(v -> {
                // Chama o método da Activity para lidar com a lógica
                excluirDespesa(position);
            });

            return convertView;
        }
    }
}