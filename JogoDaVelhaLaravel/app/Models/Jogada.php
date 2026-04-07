<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Jogada extends Model
{
    protected $table = 'tb_jogada';
    public $timestamps = false;
    protected $primaryKey = 'idJogada';
    protected $fillable = [
        'idJogo',
        'idJogador',
        'posicao',
        'simbolo',
        'dataCadastro',
    ];

    public function jogo()
    {
        return $this->belongsTo(Jogo::class, 'idJogo', 'idJogo');
    }

    public function jogador()
    {
        return $this->belongsTo(Jogador::class, 'idJogador', 'idJogador');
    }
}
