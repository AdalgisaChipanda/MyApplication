<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('tb_jogador', function (Blueprint $table) {
            $table->bigIncrements('idJogador');
            $table->string('nome');
            $table->dateTime('dataCadastro')->default(DB::raw('CURRENT_TIMESTAMP'));
            $table->integer('qtdVitorias')->default(0);
            $table->integer('qtdEmpates')->default(0);
            $table->integer('qtdDerrotas')->default(0);
        });

        DB::table('tb_jogador')->insert([
            'nome' => 'Ada',
            'dataCadastro' => now(),
            'qtdVitorias' => 0,
            'qtdEmpates' => 0,
            'qtdDerrotas' => 0,
        ]);

        DB::table('tb_jogador')->insert([
            'nome' => 'Player X',
            'dataCadastro' => now(),
            'qtdVitorias' => 0,
            'qtdEmpates' => 0,
            'qtdDerrotas' => 0,
        ]);

        DB::table('tb_jogador')->insert([
            'nome' => 'Player Y',
            'dataCadastro' => now(),
            'qtdVitorias' => 0,
            'qtdEmpates' => 0,
            'qtdDerrotas' => 0,
        ]);
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('tb_jogador');
    }
};
