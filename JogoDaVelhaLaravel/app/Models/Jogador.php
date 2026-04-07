<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Jogador extends Model
{
    use HasFactory;

    protected $table = 'tb_jogador';

    // Desativa timestamps
    public $timestamps = false;

    protected $fillable = [
        'nome',
        'dataCadastro',
        'qtdVitorias',
        'qtdEmpates',
        'qtdDerrotas',
    ];

    protected $attributes = [
        'qtdVitorias' => 0,
        'qtdEmpates' => 0,
        'qtdDerrotas' => 0,
    ];

    protected $primaryKey = 'idJogador';

    protected $casts = [
        'dataCadastro' => 'datetime',
    ];
}
