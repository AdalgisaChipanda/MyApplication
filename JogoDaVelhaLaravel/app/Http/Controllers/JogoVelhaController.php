<?php

namespace App\Http\Controllers;


use App\Models\Jogador;
use App\Models\Jogo;
use App\Models\Jogada;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;
class JogoVelhaController extends Controller
{
    private function getOrCreateJogador(string $nome)
    {
        $nome = trim($nome);

        // Se o nome estiver vazio, gera um aleatório
        if ($nome === null || $nome === '') {
            $nome = 'Jogador_' . substr(md5(uniqid()), 0, 8);
        }

        // Primeiro tenta buscar
        $jogador = Jogador::where('nome', $nome)->first();

        // Se não encontrou, cria
        if (!$jogador) {
            $jogador = Jogador::create([
                'nome' => $nome,
                'dataCadastro' => now()
            ]);
        }

        return $jogador;
    }


    public function jogar(Request $request)
    {
        try {
            $idJogo = $request->input('idJogo'); // pode ser null
            $nome1 = $request->input('nomeJogador1') ?? 'Player X';
            $nome2 = $request->input('nomeJogador2') ?? 'Player O';
            $vezJogar = $request->input('vezJogar'); // indica qual jogador está jogando (1 ou 2), apenas no momento de criar o jogo, depois o sistema controla
            $posicao = $request->input('posicao');

            if (!in_array($vezJogar, [1, 2])) {
                $vezJogar = 1;
            }

            if ($posicao < 0 || $posicao > 8) {
                return response()->json(['error' => 'Posição inválida'], 400);
            }

            $jogo = Jogo::where('idJogo', $idJogo)->first();

            // Se idJogo é nulo, cria o jogo
            if ($jogo == null) {
                if (!$nome1 || !$nome2) {
                    return response()->json(['error' => 'Nomes dos jogadores são necessários para criar um jogo'], 400);
                }

                $jogador1 = $this->getOrCreateJogador($nome1 ?? 'Player X');
                $jogador2 = $this->getOrCreateJogador($nome2 ?? 'Player O');

                if ($jogador1->idJogador === $jogador2->idJogador) {
                    return response()->json(['error' => 'Jogadores devem ser diferentes'], 400);
                }

                $jogo = Jogo::create([
                    'idJogador1' => $jogador1->idJogador,
                    'idJogador2' => $jogador2->idJogador,
                    'status' => 'decorrer',
                    'vezJogar' => $vezJogar === 1 ? '2' : '1', // o próximo a jogar será o outro
                    'dataCadastro' => now(),
                ]);
            } else {
                // Verifica se o jogo está em andamento
                if ($jogo->status != 'decorrer') {
                    return response()->json(['error' => 'Jogo já finalizado.'], 400);
                }

                // Verifica se posição já ocupada
                if (Jogada::where('idJogo', $jogo->idJogo)->where('posicao', $posicao)->exists()) {
                    return response()->json(['error' => 'Posição já ocupada.'], 400);
                }

                // Atualiza a vez de jogar para o próximo jogador
                $jogo->vezJogar = $jogo->vezJogar == 1 ? '2' : '1';
                $jogo->save();
            }

            // Verifica se posição já ocupada
            if (Jogada::where('idJogo', $jogo->idJogo)->where('posicao', $posicao)->exists()) {
                return response()->json(['error' => 'Posição já ocupada.'], 400);
            }

            $jogada = Jogada::create([
                'idJogo' => $jogo->idJogo,
                'idJogador' => $jogo->vezJogar == 1 ? $jogo->idJogador2 : $jogo->idJogador1, // o jogador que está jogando é o oposto da vezJogar atual
                'posicao' => $posicao,
                'simbolo' => $jogo->vezJogar == 1 ? 'O' : 'X', // o símbolo do jogador que acabou de jogar
                'dataCadastro' => now(),
            ]);

            // Atualiza status do jogo


            $jogo = Jogo::with('jogadas.jogador', 'jogador1', 'jogador2')
                ->orderByRaw("CASE WHEN status = 'decorrer' THEN 0 ELSE 1 END") // decorrer primeiro
                ->orderByDesc('idJogo') // mais recentes primeiro
                ->where('idJogo', $jogo->idJogo)
                ->first();

            $this->atualizarStatusJogo($jogo);

            $jogo = Jogo::with('jogadas.jogador', 'jogador1', 'jogador2')
                ->orderByRaw("CASE WHEN status = 'decorrer' THEN 0 ELSE 1 END") // decorrer primeiro
                ->orderByDesc('idJogo') // mais recentes primeiro
                ->where('idJogo', $jogo->idJogo)
                ->first();

            return response()->json([
                'jogo' => $jogo,
                'jogada' => $jogo->jogadas,
            ], 200);
        } catch (\Throwable $th) {
            return response()->json(['error' => 'Erro ao processar a jogada: ' . $th->getMessage()], 500);
        }
    }

    private function atualizarStatusJogo(Jogo $jogo)
    {
        $jogadas = $jogo->jogadas;

        // Tabuleiro 0 a 8 = idJogador ou null
        $tabuleiro = array_fill(0, 9, null);
        foreach ($jogadas as $j) {
            $tabuleiro[$j->posicao] = $j->idJogador . '';
        }

        $vencedorId = $this->verificarVencedor($tabuleiro);
        if ($vencedorId) {
            $jogo->status = 'vitoria';
            $jogo->idJogadorVitoria = $vencedorId;

            // Atualiza número de vitórias do jogador vencedor
            $jogadorVencedor = Jogador::find($vencedorId);
            if ($jogadorVencedor) {
                $jogadorVencedor->qtdVitorias += 1;
                $jogadorVencedor->save();
            }

            // Atualiza número de derrotas do jogador perdedor
            $idJogadorPerdedor = ($jogo->idJogador1 == $vencedorId) ? $jogo->idJogador2 : $jogo->idJogador1;
            $jogadorPerdedor = Jogador::find($idJogadorPerdedor);
            if ($jogadorPerdedor) {
                $jogadorPerdedor->qtdDerrotas += 1;
                $jogadorPerdedor->save();
            }
        } elseif (count($jogadas->toArray()) == 9) {
            $jogo->status = 'empate';
            $jogo->idJogadorVitoria = null;
            // Atualiza número de empates dos dois jogadores
            $jogador1 = Jogador::find($jogo->idJogador1);
            if ($jogador1) {
                $jogador1->qtdEmpates += 1;
                $jogador1->save();
            }
            $jogador2 = Jogador::find($jogo->idJogador2);
            if ($jogador2) {
                $jogador2->qtdEmpates += 1;
                $jogador2->save();
            }
        }

        $jogo->save();
    }

    private function verificarVencedor(array $tabuleiro): ?int
    {
        $linhas = [
            [0, 1, 2],
            [3, 4, 5],
            [6, 7, 8],
            [0, 3, 6],
            [1, 4, 7],
            [2, 5, 8],
            [0, 4, 8],
            [2, 4, 6],
        ];

        foreach ($linhas as $linha) {
            $a = $tabuleiro[$linha[0]];
            $b = $tabuleiro[$linha[1]];
            $c = $tabuleiro[$linha[2]];
            if ($a && $a == $b && $b == $c) {
                return $a; // retorna idJogador vencedor
            }
        }

        return null;
    }

    public function listarJogadores()
    {
        $jogadores = Jogador::orderByDesc('qtdVitorias') // maior número de vitórias primeiro
            ->orderByDesc('dataCadastro') // jogos mais recentes primeiro
            ->get();

        return response()->json($jogadores);
    }

    public function listarJogos()
    {
        $jogos = Jogo::with('jogadas.jogador', 'jogador1', 'jogador2', 'jogadorVitoria')
            //->orderByRaw("CASE WHEN status = 'decorrer' THEN 0 ELSE 1 END") // decorrer primeiro
            ->orderByDesc('idJogo') // mais recentes primeiro
            ->get();

        return response()->json($jogos);
    }
}
