<?php

use App\Http\Controllers\JogoVelhaController;
use Illuminate\Support\Facades\Route;


Route::post('/jogar', [JogoVelhaController::class, 'jogar']); // criar jogo ou fazer jogada
Route::get('/jogos/{idJogo}', [JogoVelhaController::class, 'verJogo']); // retorna status e tabuleiro
Route::get('/jogadores', [JogoVelhaController::class, 'listarJogadores']); // lista jogadores
Route::get('/jogos', [JogoVelhaController::class, 'listarJogos']); // todos os jogos, abertos primeiro
