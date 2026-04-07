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

        Schema::create('tb_jogada', function (Blueprint $table) {
            $table->bigIncrements('idJogada');

            $table->foreignId('idJogo')->constrained('tb_jogo', 'idJogo');
            $table->foreignId('idJogador')->constrained('tb_jogador', 'idJogador');

            // Posição jogada (0 a 8)
            $table->unsignedTinyInteger('posicao');

            // Símbolo da jogada: X ou O
            $table->enum('simbolo', ['X', 'O']);

            $table->dateTime('dataCadastro')->nullable();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('tb_jogada');
    }
};
