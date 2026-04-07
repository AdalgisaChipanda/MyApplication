<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Jogo extends Model
{
    protected $table = 'tb_jogo';
    public $timestamps = false;

    protected $primaryKey = 'idJogo';
    protected $fillable = [
        'idJogador1',
        'idJogador2',
        'vezJogar',
        'status',
        'idJogadorVitoria',
        'dataCadastro',
    ];

    protected $appends = [
        'jogadorVitoria'
    ];

    public function jogador1()
    {
        return $this->belongsTo(Jogador::class, 'idJogador1', 'idJogador');
    }

    public function jogador2()
    {
        return $this->belongsTo(Jogador::class, 'idJogador2', 'idJogador');
    }

    public function jogadorVitoria()
    {
        return $this->belongsTo(Jogador::class, 'idJogadorVitoria', 'idJogador');
    }

    public function getJogadorVitoriaAttribute()
    {
        return $this->jogadorVitoria()->first();
    }

    public function jogadas()
    {
        return $this->hasMany(Jogada::class, 'idJogo', 'idJogo');
    }
}
