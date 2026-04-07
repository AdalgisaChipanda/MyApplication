const API_BASE = "http://localhost:8000/api";
const URL_JOGAR = `${API_BASE}/jogar`;
const URL_JOGADORES = `${API_BASE}/jogadores`;
const URL_JOGOS = `${API_BASE}/jogos`;

// Elementos DOM
const tabuleiro = document.getElementById("tabuleiro");
const rankingEl = document.getElementById("ranking-jogadores");
const listaJogosEl = document.getElementById("lista-jogos");
const replayTabuleiro = document.getElementById("replay-tabuleiro");
let replayJogadas = [];
let replayIndex = 0;
let replayInterval = null;

// penas para o jogo o momento nao o total de todos
let placarX = 0;
let placarO = 0;

let jogoAtual = null;

// Botão Novo Jogo
document.getElementById("btn-novo-jogo").addEventListener("click", () => {
    // Resetar tudo e jogoAtual
    jogoAtual = null;

    document.getElementById("nomeJogador1").disabled = false;
    document.getElementById("nomeJogador2").disabled = false;
    document.getElementById("nomeJogador1").value = "";
    document.getElementById("nomeJogador2").value = "";
    document.getElementById("vitorias1").innerText = "0";
    document.getElementById("vitorias2").innerText = "0";
    document.getElementById("vez-jogador").innerText = "X";

    document.getElementById("score-x").innerText = "0";
    document.getElementById("score-o").innerText = "0";
    placarX = 0;
    placarO = 0;

    document
        .querySelectorAll(".tabuleiro .celula")
        .forEach((c) => (c.textContent = ""));
    console.log("Sistema resetado para novo jogo.");
});

async function jogar(idJogo = null, nomeJogador1, nomeJogador2, posicao) {
    try {
        const res = await fetch(URL_JOGAR, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                idJogo,
                nomeJogador1,
                nomeJogador2,
                posicao,
            }),
        });

        const data = await res.json();
        if (res.status != 200) throw new Error(data.error || "Erro");

        jogoAtual = data.jogo;
        atualizarTabuleiro(data.jogo.jogadas);
        atualizarListaJogos();
        atualizarRanking();
        iniciarReplay(jogoAtual);

        const docVes = document.getElementById("vez-jogador");
        if (jogoAtual.status == "decorrer") {
            const vezJogador =
                jogoAtual.vezJogar == 1
                    ? jogoAtual.jogador1.nome
                    : jogoAtual.jogador2.nome;
            docVes.innerText = `${vezJogador}`;
        }
        if (jogoAtual.status != "decorrer") {
            placarX +=
                jogoAtual.jogador1.idJogador ===
                jogoAtual.jogadorVitoria?.idJogador
                    ? 1
                    : 0;
            placarO +=
                jogoAtual.jogador2.idJogador ===
                jogoAtual.jogadorVitoria?.idJogador
                    ? 1
                    : 0;

            document.getElementById("score-x").innerText = placarX;
            document.getElementById("score-o").innerText = placarO;

            // Pequeno delay para a animação da última peça terminar
            setTimeout(() => {
                mostrarAlerta(
                    jogoAtual.status,
                    jogoAtual.jogadorVitoria ? jogoAtual.jogadorVitoria : null
                );
            }, 300);

            docVes.innerText = `Finalizado`;
            // Limpar tabuleiro após breve delay

            setTimeout(() => {
                jogoAtual = null;
                document
                    .querySelectorAll(".tabuleiro .celula")
                    .forEach((c) => (c.textContent = ""));
            }, 3300);
        }
    } catch (err) {
        mostrarErro(err.message);
    }
}

function atualizarTabuleiro(jogadas) {
    const i1 = document.getElementById("nomeJogador1");
    const i2 = document.getElementById("nomeJogador2");

    i1.value = jogoAtual?.jogador1?.nome ?? "";
    i1.disabled = true;
    i2.value = jogoAtual?.jogador2?.nome ?? "";
    i2.disabled = true;

    document.getElementById("vitorias1").innerText =
        jogoAtual?.jogador1?.qtdVitorias;
    document.getElementById("vitorias2").innerText =
        jogoAtual?.jogador2?.qtdVitorias;

    document
        .querySelectorAll(".tabuleiro .celula")
        .forEach((c) => (c.textContent = ""));

    const docVes = document.getElementById("vez-jogador");
    if (!jogoAtual) {
        docVes.innerText = `X`;
        i1.disabled = false;
        i2.disabled = false;
    } else if (jogoAtual?.status == "decorrer") {
        const vezJogador =
            jogoAtual?.vezJogar === 1
                ? jogoAtual?.jogador1?.nome
                : jogoAtual?.jogador2?.nome;
        docVes.innerText = `${vezJogador}`;
    } else {
        docVes.innerText = `Finalizado`;
    }

    jogadas.forEach((j) => {
        const celula = document.querySelector(
            `.tabuleiro .celula[data-posicao='${j.posicao}']`
        );
        if (celula) {
            celula.textContent = j.simbolo;
            celula.style.color = j.simbolo === "X" ? "#00f2ff" : "#ff0055";
            celula.style.textShadow = `0 0 10px ${
                j.simbolo === "X" ? "#00f2ff" : "#ff0055"
            }`;
        }
    });
}

// Event Listeners para o Tabuleiro
document.querySelectorAll(".tabuleiro .celula").forEach((c) => {
    c.addEventListener("click", () => {
        const pos = parseInt(c.dataset.posicao);
        const n1 = document.getElementById("nomeJogador1").value;
        const n2 = document.getElementById("nomeJogador2").value;

        //if (!n1 || !n2) return mostrarErro("Insira os nomes dos jogadores!");

        jogar(jogoAtual?.idJogo, n1, n2, pos);
    });
});

async function atualizarRanking() {
    const res = await fetch(URL_JOGADORES);
    const jogadores = await res.json();
    const rankingEl = document.getElementById("ranking-jogadores");

    rankingEl.innerHTML = jogadores
        .map(
            (j, index) => `
        <li>
            <span class="jogador-info"><strong>#${index + 1}</strong> ${
                j.nome
            }</span>
            <span class="stats" style="display: flex; align-items: center; gap: 10px;">
                <span class="v-count" style="display: flex; align-items: center; gap: 1px;">
                    <span style="font-size:1.4rem">${j.qtdVitorias}</span>
                    <span style="font-size:0.8rem">🏆</span>
                </span>
                <span class="d-count" style="display: flex; align-items: center; gap: 1px;">
                    <span style="font-size:1.4rem">${j.qtdDerrotas}</span>
                    <span style="font-size:0.8rem">❌</span>
                </span>
                <span class="e-count" style="display: flex; align-items: center; gap: 1px;">
                    <span style="font-size:1.4rem">${j.qtdEmpates}</span>
                    <span style="font-size:0.8rem">🤝</span>
                </span>
            </span>
        </li>
    `
        )
        .join("");
}

async function atualizarListaJogos() {
    const res = await fetch(URL_JOGOS);
    const jogos = await res.json();
    const listaEl = document.getElementById("lista-jogos");

    listaEl.innerHTML = jogos
        .map(
            (j) => `
        <li class="${
            j.idJogo === jogoAtual?.idJogo ? "active" : ""
        }" id="jogo-${j.idJogo}">
            <div >
                <small>ID #${j.idJogo}</small>
                <div class="jogo-info">
                    <span class="${
                        j.jogador1.idJogador == j.jogadorVitoria?.idJogador
                            ? "vencedor"
                            : ""
                    }">${j.jogador1.nome}</span>
                    vs
                    <span class="${
                        j.jogador2.idJogador == j.jogadorVitoria?.idJogador
                            ? "vencedor"
                            : ""
                    }">${j.jogador2.nome}</span>
                </div>
            </div>
            <span class="status-badge">${j.status}</span>
        </li>
    `
        )
        .join("");

    // click no item do jogo histórico
    listaEl.querySelectorAll("li").forEach((li, index) => {
        li.addEventListener("click", () => {
            document.getElementById("score-x").innerText = "0";
            document.getElementById("score-o").innerText = "0";
            placarX = 0;
            placarO = 0;

            li.classList.add("active");
            document
                .querySelectorAll("#lista-jogos li")
                .forEach((sibling, sIndex) => {
                    if (sIndex !== index) sibling.classList.remove("active");
                });
            iniciarReplay(jogos[index]);
        });
    });

    // scrolar a lista ao topo com animacao
}

// ==========================
// REPLAY
// ==========================

function iniciarReplay(jogo) {
    jogoAtual = jogo;
    const i1 = document.getElementById("nomeJogador1");
    const i2 = document.getElementById("nomeJogador2");

    i1.value = jogo.jogador1.nome;
    i1.disabled = true;
    i2.value = jogo.jogador2.nome;
    i2.disabled = true;

    document.getElementById("vitorias1").innerText = jogo.jogador1.qtdVitorias;
    document.getElementById("vitorias2").innerText = jogo.jogador2.qtdVitorias;

    atualizarTabuleiro(jogo.jogadas);

    // Configurar replay
    replayJogadas = jogo.jogadas;
    replayIndex = 0;
    atualizarReplayTabuleiro();
    pausarReplay();

    const docresultadoReplay = document.getElementById("replay-resultado");
    docresultadoReplay.classList.remove("vitoria-texto");
    docresultadoReplay.classList.remove("empate-texto");
    if (jogo.status != "decorrer") {
        if (jogo.jogadorVitoria) {
            docresultadoReplay.innerHTML = `Vencedor: <strong style="color: #00f2ff">${jogo.jogadorVitoria.nome}</strong>`;
            docresultadoReplay.classList.add("vitoria-texto");
        } else {
            docresultadoReplay.innerText = "Empate!";
            docresultadoReplay.classList.add("empate-texto");
        }
    } else {
        docresultadoReplay.innerText = "Jogo em andamento";
    }

    const docpassoatual = document.getElementById("passo-atual");
    docpassoatual.innerText = replayIndex + 1;
    const doctotalpassos = document.getElementById("total-passos");
    doctotalpassos.innerText = jogo.jogadas.length;
}

function atualizarReplayTabuleiro() {
    // SELECIONA AS CÉLULAS MINI
    const celulasMini = document.querySelectorAll(".celula-mini");

    // Limpa todas
    celulasMini.forEach((c) => (c.textContent = ""));

    // Preenche até o index atual
    for (let i = 0; i <= replayIndex; i++) {
        const jogada = replayJogadas[i];
        if (jogada) {
            const celula = document.querySelector(
                `.celula-mini[data-posicao='${jogada.posicao}']`
            );
            if (celula) {
                celula.textContent = jogada.simbolo;
                celula.style.color =
                    jogada.simbolo === "X" ? "var(--primary)" : "var(--accent)";
            }
        }
    }
}

// Controles replay
function avancarReplay() {
    if (replayIndex < replayJogadas.length - 1) {
        replayIndex++;
        atualizarReplayTabuleiro();
        const docpassoatual = document.getElementById("passo-atual");
        docpassoatual.innerText = replayIndex + 1;
    }
}
function voltarReplay() {
    if (replayIndex > 0) {
        replayIndex--;
        atualizarReplayTabuleiro();
        const docpassoatual = document.getElementById("passo-atual");
        docpassoatual.innerText = replayIndex + 1;
    }
}
function pausarReplay() {
    if (replayInterval) clearInterval(replayInterval);
}

function continuarReplay() {
    pausarReplay();
    replayInterval = setInterval(() => {
        if (replayIndex < replayJogadas.length - 1) {
            replayIndex++;
            atualizarReplayTabuleiro();
            const docpassoatual = document.getElementById("passo-atual");
            docpassoatual.innerText = replayIndex + 1;
        } else {
            pausarReplay();
        }
    }, 1000);
}

// Botões replay
document
    .getElementById("replay-avancar")
    .addEventListener("click", avancarReplay);
document
    .getElementById("replay-voltar")
    .addEventListener("click", voltarReplay);
document
    .getElementById("replay-pausar")
    .addEventListener("click", pausarReplay);
document
    .getElementById("replay-continuar")
    .addEventListener("click", continuarReplay);

// Função para mostrar o alerta customizado
function mostrarAlerta(status, vencedor) {
    const modal = document.getElementById("modal-alerta");
    const titulo = document.getElementById("modal-titulo");
    const mensagem = document.getElementById("modal-mensagem");
    const icone = document.querySelector(".modal-icon");

    // Define o texto baseado no resultado
    titulo.innerText = status.toUpperCase();

    if (vencedor) {
        mensagem.innerHTML = `Vencedor: <strong style="color: #00f2ff">${vencedor.nome}</strong>`;
        icone.innerText = "🏆";
    } else {
        mensagem.innerText = "A partida terminou em empate!";
        icone.innerText = "🤝";
    }

    // Mostra o modal
    modal.classList.add("active");
}

// Evento para fechar o modal
document.getElementById("btn-continuar").addEventListener("click", function () {
    const modal = document.getElementById("modal-alerta");
    modal.classList.remove("active");

    // Opcional: Reiniciar o jogo automaticamente ao clicar em continuar
    // reiniciarJogo();
});

/**
 * Exibe o alerta de erro customizado
 * @param {string} mensagem - A mensagem de erro que será exibida
 */
function mostrarErro(mensagem) {
    const modalErro = document.getElementById("modal-erro");
    const campoMensagem = document.getElementById("modal-erro-mensagem");

    // Define a mensagem recebida
    campoMensagem.innerText = mensagem;

    // Exibe o modal
    modalErro.classList.add("active");
}

// Evento para fechar o modal de erro
document
    .getElementById("btn-erro-fechar")
    .addEventListener("click", function () {
        document.getElementById("modal-erro").classList.remove("active");
    });

// ==========================
// INICIALIZAÇÃO
// ==========================

// Inicialização
atualizarRanking();
atualizarListaJogos();
