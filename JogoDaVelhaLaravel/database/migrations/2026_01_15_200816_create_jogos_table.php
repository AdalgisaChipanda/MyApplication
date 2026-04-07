<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('tb_jogo', function (Blueprint $table) {
            $table->bigIncrements('idJogo');

            // Jogadores do jogo
            $table->foreignId('idJogador1')->constrained('tb_jogador', 'idJogador');
            $table->foreignId('idJogador2')->constrained('tb_jogador', 'idJogador');

            // Status do jogo
            $table->enum('status', ['decorrer', 'vitoria', 'empate'])->default('decorrer');
            $table->enum('vezJogar', ['1', '2',])->default('1');

            // Jogador vencedor (nulo enquanto estiver decorrendo ou empate)
            $table->foreignId('idJogadorVitoria')->nullable()->constrained('tb_jogador', 'idJogador');

            $table->dateTime('dataCadastro')->nullable();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('tb_jogo');
    }
};
