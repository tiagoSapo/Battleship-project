package com.example.projetofinalcm2022.models;

import static java.lang.Math.sqrt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projetofinalcm2022.MainActivity;
import com.example.projetofinalcm2022.R;
import com.example.projetofinalcm2022.fragments.gamefragments.GameArduinoFragment;
import com.example.projetofinalcm2022.fragments.gamefragments.GameMultiPlayerFragment;
import com.example.projetofinalcm2022.threads.TaskManager;
import com.example.projetofinalcm2022.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BatalhaNaval implements Serializable {

    /** Established number of turns per player: 3 **/
    public static final int JOGADAS = 3;

    /** Game mode [Single-player, multiplayer, single-player-arduino] **/
    ModoDeJogo modoDeJogo;

    /** Game boards **/
    int tabuleiroJogador[][];
    int tabuleiroCPU[][];
    int tabuleiroArduino[][];
    int tabuleiroFirebase[][];

    /** Number of turns left for each player **/
    int jogadas_jogador = 0; int tiros_jogador_certeiros = 0;
    int jogadas_adversario = 0; int tiros_adversario_certeiros = 0; // Single player CPU only
    int jogadas_arduino = 0; int tiros_arduino_certeiros = 0; // Single player ARDUINO only
    public int jogadas_firebase = 0; int tiros_firebase_certeiros = 0; // Multiplayer FIREBASE only

    int cnt = 0, c1N = 0, c2N = 0; // NOVO
    CoordXY c1, c2;

    public BatalhaNaval(int[][] tabuleiroJogador, ModoDeJogo modoDeJogo) {
        this.tabuleiroJogador = tabuleiroJogador;
        this.modoDeJogo = modoDeJogo;
    }

    /**
     * Method that starts game
     */
    public void startGame(Fragment fragment) {
        switch (this.modoDeJogo) {
            case SINGLE_PLAYER_CPU:
                criarTabuleiroAdversario();
                break;
            case SINGLE_PLAYER_ARDUINO:
                criarTabuleiroArduino();
                break;
            case MULTIPLAYER:
                comecaJogoMultiplayer();
                return;
            default:
                throw new RuntimeException("Invalid game mode");
        }
        comecaJogo(fragment);
    }



    /** Metodos relativos ao Inicio do jogo **/
    private void comecaJogo(Fragment fragment) {

        Jogadores jogadorQueComecaJogo = quemComeca();

        switch (jogadorQueComecaJogo) {
            case JOGADOR:
                jogadas_jogador = JOGADAS;
                break;
            case CPU:
                jogadas_adversario = JOGADAS;
                jogaCPU(fragment);
                break;
            case ARDUINO:
                jogadas_arduino = JOGADAS;
                //Toast.makeText(activity, "Waiting for Arduino", Toast.LENGTH_SHORT).show();
                /* ASK ARDUINO */
                new TaskManager().asyncAskArduino((GameArduinoFragment) fragment, this);
                break;
            default:
                throw new RuntimeException("Jogador invalido");
        }
    }
    private void comecaJogoMultiplayer() {
        jogadas_jogador = JOGADAS;
    }

    /** Metodos relativos 'a criacao dos tabuleiros **/
    private void criarTabuleiroAdversario() {

        resetTabuleiro(Jogadores.CPU);
        int cntTotal = 0; // Conta o numero de vezes que o pc escolheu uma posicao invalida
        int TENTATIVAS = 50; // Numero maximo de posicoes falhadas, caso este limite seja ultrapassado o pc escolhe o mesmo tabuleiro do jogador

        while (true) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            boolean posAlternativa = random.nextBoolean();
            if (colocaBarco5(pos, vertical, posAlternativa, Jogadores.CPU, tabuleiroCPU))
                break;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorCPU();
                return;
            }
        }

        // Este contador indica o numero de barcos colocados (limite de dois 'barcos de 3' para colocar)
        int cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            if (colocaBarco3(pos, vertical, Jogadores.CPU, tabuleiroCPU))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorCPU();
                return;
            }
        }

        cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            if (colocaBarco2(pos, vertical, Jogadores.CPU, tabuleiroCPU))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorCPU();
                return;
            }
        }

        cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            if (colocaBarco1(pos, Jogadores.CPU, tabuleiroCPU))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorCPU();
                return;
            }
        }

    }
    private void criarTabuleiroArduino() {

        resetTabuleiro(Jogadores.ARDUINO);
        int cntTotal = 0; // Conta o numero de vezes que o pc escolheu uma posicao invalida
        int TENTATIVAS = 50; // Numero maximo de posicoes falhadas, caso este limite seja ultrapassado o pc escolhe o mesmo tabuleiro do jogador

        while (true) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            boolean posAlternativa = random.nextBoolean();
            if (colocaBarco5(pos, vertical, posAlternativa, Jogadores.ARDUINO, tabuleiroArduino))
                break;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorArduino();
                return;
            }
        }

        // Este contador indica o numero de barcos colocados (limite de dois 'barcos de 3' para colocar)
        int cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            if (colocaBarco3(pos, vertical, Jogadores.ARDUINO, tabuleiroArduino))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorArduino();
                return;
            }
        }

        cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            boolean vertical = random.nextBoolean();
            if (colocaBarco2(pos, vertical, Jogadores.ARDUINO, tabuleiroArduino))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorArduino();
                return;
            }
        }

        cnt = 2;
        while (cnt > 0) {
            Random random = new Random();
            int pos = random.nextInt(Utils.BOARD_TAM);
            if (colocaBarco1(pos, Jogadores.ARDUINO, tabuleiroArduino))
                cnt--;
            cntTotal++;
            if (cntTotal > TENTATIVAS) {
                resetTabuleiroEColocaAsMesmasPosicoesDoJogadorArduino();
                return;
            }
        }

    }

    /** Metodos relativos 'a realizacao das jogadas **/
    public boolean jogaJogador(int pos, final Fragment fragment) {

        int [][] tabuleiroAdversario;

        Log.e("temp", "123");

        if (jogoAcabou() > 0) // verifica se o jogo acabou
            return false;

        Log.e("temp", "abc");

        if (jogadas_jogador <= 0) // verifica se o numero maximo de jogadas do Jogador foi atingido
            return false;

        Log.e("temp", "456");
        if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_CPU) {
            tabuleiroAdversario = tabuleiroCPU;
        } else if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_ARDUINO){
            tabuleiroAdversario = tabuleiroArduino;
        } else {
            tabuleiroAdversario = tabuleiroFirebase;
        }

        Log.e("temp", "789");
        if (!ataca(pos, Jogadores.JOGADOR, tabuleiroAdversario, fragment)) // verifica se o ataque nao foi valido
            return false;
        jogadas_jogador--; // descontar uma jogada ao jogador

        /* Passa a vez ao adversario */
        if (jogadas_jogador == 0) {
            tiros_jogador_certeiros = 0;

            if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_CPU) {
                jogadas_adversario = JOGADAS; // reset das jogadas do adversario
                jogaCPU(fragment);
            } else if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_ARDUINO) {
                jogadas_arduino = JOGADAS;
                // JOGAR AGORA O ARDUINO
                // ASK ARDUINO (via MQTT)
                new TaskManager().asyncAskArduino((GameArduinoFragment) fragment, this);
            } else if (modoDeJogo == ModoDeJogo.MULTIPLAYER) {
                jogadas_firebase = JOGADAS;
                new TaskManager().asyncWaitMultiplayer((GameMultiPlayerFragment) fragment, this);
            }
        }
        return true;
    }
    public boolean jogaCPU(Fragment fragment) {

        if (jogoAcabou() > 0)
            return false;

        Random random = new Random();

        while (jogadas_adversario > 0) {
            int pos = random.nextInt(Utils.BOARD_TAM);
            if (ataca(pos, Jogadores.CPU, tabuleiroCPU, fragment))
                jogadas_adversario--;
            if (jogoAcabou() > 0)
                return false;
        }
        jogadas_jogador = JOGADAS;
        return true;
    }
    public synchronized boolean jogaArduino(int position, GameArduinoFragment gameFragment) {

        if (jogoAcabou() > 0) // Se o jogo acabou, termina jogo
            return false;

        if (jogadas_arduino <= 0) // Se o numero de jogadas do arduino
            return false;

        boolean ataqueOk = ataca(position, Jogadores.ARDUINO, tabuleiroArduino, gameFragment);
        if (!ataqueOk) {
            Log.w("Arduino_Tiago", "Invalid position " + converter1DPara2D(position) + " by Arduino");
            return false;
        }

        jogadas_arduino--; // jogada OK, entao descontar uma jogada

        if (jogoAcabou() > 0)
            return false;

        if (jogadas_arduino <= 0) {
            jogadas_jogador = JOGADAS;
            Toast.makeText(gameFragment.getActivity(), "Your turn", Toast.LENGTH_SHORT).show();
        }

        gameFragment.updateViews(); // updates players' boards
        return true;
    }
    public boolean jogaMultiplayer(int position, GameMultiPlayerFragment gameFragment) {

        if (jogoAcabou() > 0) // Se o jogo acabou, termina jogo
            return false;

        if (jogadas_firebase <= 0) // Se o numero de jogadas do arduino
            return false;

        boolean ataqueOk = ataca(position, Jogadores.FIREBASE, tabuleiroFirebase, gameFragment);
        if (!ataqueOk) {
            Log.w("firebase", "Invalid position " + converter1DPara2D(position) + " by Firebase");
            return false;
        }

        jogadas_firebase--; // jogada OK, entao descontar uma jogada

        if (jogoAcabou() > 0)
            return false;

        if (jogadas_firebase <= 0) {
            jogadas_jogador = JOGADAS;
            Toast.makeText(gameFragment.getActivity(), "Your turn", Toast.LENGTH_SHORT).show();
        }

        gameFragment.updateViews(); // updates players' boards
        return true;
    }

    /**
     * Ataca o tabuleiro do adversario
     * @param pos - posicao no tabuleiro do adversario a atacar
     * @param opcao - o jogador que faz o ataque
     * @param tabuleiroAdversario - pode ser o tabuleiro do CPU ou do Arduino (consoante o tipo de jogo)
     * @return diz se a posicao de ataque e' ou nao valida
     */
    private boolean ataca(int pos, Jogadores opcao, int [][] tabuleiroAdversario, Fragment fragment) {
        CoordXY c = new CoordXY(pos / obtemColunas(), pos % obtemColunas());

        if (!verificaSePosicaoValida(c.x, c.y, obtemColunas()))
            return false;

        if (opcao == Jogadores.JOGADOR) {
            if (tabuleiroAdversario[c.x][c.y] <= 0)
                return false;

            if (tabuleiroAdversario[c.x][c.y] > 1)
                tiros_jogador_certeiros++;
            //NOVO INICIO

            if (cnt < 2) {
                if (cnt == 0) {
                    c1 = new CoordXY(c.x, c.y);
                    c1N = tabuleiroAdversario[c.x][c.y];
                    tabuleiroAdversario[c.x][c.y] = 0;
                }
                if (cnt == 1) {
                    c2 = new CoordXY(c.x, c.y);
                    c2N = tabuleiroAdversario[c.x][c.y];
                    tabuleiroAdversario[c.x][c.y] = 0;
                }
                cnt++;
            }
            else {
                tabuleiroAdversario[c.x][c.y] *= -1;
                tabuleiroAdversario[c1.x][c1.y] = c1N * -1;
                tabuleiroAdversario[c2.x][c2.y] = c2N * -1;

                int letraNum = c.y, lN1 = c1.y, lN2 = c2.y;
                String letra, l1, l2;

                letra = String.valueOf((char)(letraNum + 65));
                l1 = String.valueOf((char)(lN1 + 65));
                l2 = String.valueOf((char)(lN2 + 65));

                String notificationTitle = "Shots made";
                String notificationMessage = "";

                int v = tabuleiroAdversario[c.x][c.y];
                if (v == -2)
                    notificationMessage += "[" + (c.x + 1) + ", " + letra + "] " + "Shot boat 1";
                else if (v == -3)
                    notificationMessage += "[" + (c.x + 1) + ", " + letra + "] " + "Shot boat 2";
                else if (v == -4)
                    notificationMessage += "[" + (c.x + 1) + ", " + letra + "] " + "Shot boat 3";
                else if (v == -6)
                    notificationMessage += "[" + (c.x + 1) + ", " + letra + "] " + "Shot boat 5";
                else
                    notificationMessage += "[" + (c.x + 1) + ", " + letra + "] " + "Water";

                v = tabuleiroAdversario[c1.x][c1.y];
                if (v == -2)
                    notificationMessage += "\n[" + (c1.x + 1) + ", " + l1 + "] " + "Shot boat 1";
                else if (v == -3)
                    notificationMessage += "\n[" + (c1.x + 1) + ", " + l1 + "] " + "Shot boat 2";
                else if (v == -4)
                    notificationMessage += "\n[" + (c1.x + 1) + ", " + l1 + "] " + "Shot boat 3";
                else if (v == -6)
                    notificationMessage += "\n[" + (c1.x + 1) + ", " + l1 + "] " + "Shot boat 5";
                else
                    notificationMessage += "\n[" + (c1.x + 1) + ", " + l1 + "] " + "Water";

                v = tabuleiroAdversario[c2.x][c2.y];
                if (v == -2)
                    notificationMessage += "\n[" + (c2.x + 1) + ", " + l2 + "] " + "Shot boat 1";
                else if (v == -3)
                    notificationMessage += "\n[" + (c2.x + 1) + ", " + l2 + "] " + "Shot boat 2";
                else if (v == -4)
                    notificationMessage += "\n[" + (c2.x + 1) + ", " + l2 + "] " + "Shot boat 3";
                else if (v == -6)
                    notificationMessage += "\n[" + (c2.x + 1) + ", " + l2 + "] " + "Shot boat 5";
                else
                    notificationMessage += "\n[" + (c2.x + 1) + ", " + l2 + "] " + "Water";

                Utils.createInfoDialog(fragment.getActivity(), notificationTitle, notificationMessage, R.drawable.info).show();

                c1 = null;
                c2 = null;
                c1N = 0;
                c2N = 0;
                cnt = 0;
            }
            //NOVO FIM
            return true;
        }
        else {
            int v = tabuleiroJogador[c.x][c.y];
            if (v < 0)
                return false;

            tabuleiroJogador[c.x][c.y] *= -1;
            return true;
        }
    }

    public void mudarTabuleiro(int tabuleiroEnvio[][], Jogadores opcao, int [][] tabuleiroAdversario) {

        if (opcao == Jogadores.JOGADOR) {
            for (int i = 0; i < obtemColunas(); i++)
                System.arraycopy(tabuleiroEnvio[i], 0, tabuleiroJogador[i], 0, obtemColunas());
        }
        else {

            for (int i = 0; i < obtemColunas(); i++)
                System.arraycopy(tabuleiroEnvio[i], 0, tabuleiroAdversario[i], 0, obtemColunas());
        }

    }
    private void resetTabuleiroEColocaAsMesmasPosicoesDoJogadorCPU() {
        for (int i = 0; i < obtemColunas(); i++)
            System.arraycopy(tabuleiroJogador[i], 0, tabuleiroCPU[i], 0, obtemColunas());
    }
    private void resetTabuleiroEColocaAsMesmasPosicoesDoJogadorArduino() {
        for (int i = 0; i < obtemColunas(); i++)
            System.arraycopy(tabuleiroJogador[i], 0, tabuleiroArduino[i], 0, obtemColunas());
    }

    // Apenas para a criacao do tabuleiro do adversario
    public boolean colocaBarco1(int pos, Jogadores opcao, int [][] tabuleiroAdversario) {
        CoordXY c = converter1DPara2D(pos);
        if (opcao == Jogadores.JOGADOR) {
            if (verificaPosicaoParaBarco1(c, opcao)) {
                tabuleiroJogador[c.x][c.y] = 2;
                return true;
            } else
                return false;
        }
        else {
            if (verificaPosicaoParaBarco1(c, opcao)) {
                tabuleiroAdversario[c.x][c.y] = 2;
                return true;
            } else
                return false;
        }
    }
    public boolean colocaBarco2(int pos, boolean vertical, Jogadores opcao, int[][] tabuleiroAdversario) {
        CoordXY c = converter1DPara2D(pos);
        if (opcao == Jogadores.JOGADOR) {
            if (verificaPosicaoParaBarco2(c, vertical, opcao)) {
                if (vertical) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                        tabuleiroJogador[c.x][c.y] = 3;
                        tabuleiroJogador[c.x + 1][c.y] = 3;
                        return true;
                    }
                    return false;
                } else if (verificaSePosicaoValida(c.x, c.y + 1, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                    tabuleiroJogador[c.x][c.y] = 3;
                    tabuleiroJogador[c.x][c.y + 1] = 3;
                    return true;
                }
                return false;
            } else
                return false;
        }
        else {
            if (verificaPosicaoParaBarco2(c, vertical, opcao)) {
                if (vertical) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                        tabuleiroAdversario[c.x][c.y] = 3;
                        tabuleiroAdversario[c.x + 1][c.y] = 3;
                        return true;
                    }
                    return false;
                } else if (verificaSePosicaoValida(c.x, c.y + 1, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                    tabuleiroAdversario[c.x][c.y] = 3;
                    tabuleiroAdversario[c.x][c.y + 1] = 3;
                    return true;
                }
                return false;
            } else
                return false;
        }
    }
    public boolean colocaBarco3(int pos, boolean vertical, Jogadores opcao, int[][] tabuleiroAdversario) {
        CoordXY c = converter1DPara2D(pos);
        if (opcao == Jogadores.JOGADOR) {
            if (verificaPosicaoParaBarco3(c, vertical, opcao)) {
                if (vertical) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue()) && verificaSePosicaoValida(c.x + 2, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                        tabuleiroJogador[c.x][c.y] = 4;
                        tabuleiroJogador[c.x + 1][c.y] = 4;
                        tabuleiroJogador[c.x + 2][c.y] = 4;
                        return true;
                    }
                    return false;
                } else if (verificaSePosicaoValida(c.x, c.y + 1, new Double(sqrt(Utils.BOARD_TAM)).intValue()) && verificaSePosicaoValida(c.x, c.y + 2, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                    tabuleiroJogador[c.x][c.y] = 4;
                    tabuleiroJogador[c.x][c.y + 1] = 4;
                    tabuleiroJogador[c.x][c.y + 2] = 4;
                    return true;
                }
                return false;
            } else
                return false;
        }
        else {
            if (verificaPosicaoParaBarco3(c, vertical, opcao)) {
                if (vertical) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue()) && verificaSePosicaoValida(c.x + 2, c.y, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                        tabuleiroAdversario[c.x][c.y] = 4;
                        tabuleiroAdversario[c.x + 1][c.y] = 4;
                        tabuleiroAdversario[c.x + 2][c.y] = 4;
                        return true;
                    }
                    return false;
                } else if (verificaSePosicaoValida(c.x, c.y + 1, new Double(sqrt(Utils.BOARD_TAM)).intValue()) && verificaSePosicaoValida(c.x, c.y + 2, new Double(sqrt(Utils.BOARD_TAM)).intValue())) {
                    tabuleiroAdversario[c.x][c.y] = 4;
                    tabuleiroAdversario[c.x][c.y + 1] = 4;
                    tabuleiroAdversario[c.x][c.y + 2] = 4;
                    return true;
                }
            }
            return false;
        }
    }
    public boolean colocaBarco5(int pos, boolean vertical, boolean posAlternativa, Jogadores opcao, int [][] tabuleiroAdversario) {

        if (opcao == Jogadores.JOGADOR) {
            CoordXY c = converter1DPara2D(pos);
            if (verificaPosicaoParaBarco5(c, vertical, posAlternativa, opcao)) {
                int COLUNAS = Utils.BOARD_COLUMNS;
                if (vertical && !posAlternativa) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 2, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)) {
                        tabuleiroJogador[c.x][c.y] = 6;
                        tabuleiroJogador[c.x + 1][c.y] = 6;
                        tabuleiroJogador[c.x + 2][c.y] = 6;
                        tabuleiroJogador[c.x][c.y - 1] = 6;
                        tabuleiroJogador[c.x][c.y + 1] = 6;
                        return true;
                    }
                    return false;
                } else if (!vertical && !posAlternativa) {
                    if (verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 2, COLUNAS)
                            && verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)) {
                        tabuleiroJogador[c.x][c.y] = 6;
                        tabuleiroJogador[c.x][c.y + 1] = 6;
                        tabuleiroJogador[c.x][c.y + 2] = 6;
                        tabuleiroJogador[c.x - 1][c.y] = 6;
                        tabuleiroJogador[c.x + 1][c.y] = 6;
                        return true;
                    }
                    return false;
                } else if (vertical && posAlternativa) {
                    if (verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x - 2, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)) {
                        tabuleiroJogador[c.x][c.y] = 6;
                        tabuleiroJogador[c.x - 1][c.y] = 6;
                        tabuleiroJogador[c.x - 2][c.y] = 6;
                        tabuleiroJogador[c.x][c.y - 1] = 6;
                        tabuleiroJogador[c.x][c.y + 1] = 6;
                        return true;
                    }
                    return false;
                } else {
                    if (verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 2, COLUNAS)
                            && verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)) {
                        tabuleiroJogador[c.x][c.y] = 6;
                        tabuleiroJogador[c.x][c.y - 1] = 6;
                        tabuleiroJogador[c.x][c.y - 2] = 6;
                        tabuleiroJogador[c.x - 1][c.y] = 6;
                        tabuleiroJogador[c.x + 1][c.y] = 6;
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }
        else {
            CoordXY c = converter1DPara2D(pos);
            if (verificaPosicaoParaBarco5(c, vertical, posAlternativa, opcao)) {
                int COLUNAS = Utils.BOARD_COLUMNS;
                if (vertical && !posAlternativa) {
                    if (verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 2, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)) {
                        tabuleiroAdversario[c.x][c.y] = 6;
                        tabuleiroAdversario[c.x + 1][c.y] = 6;
                        tabuleiroAdversario[c.x + 2][c.y] = 6;
                        tabuleiroAdversario[c.x][c.y - 1] = 6;
                        tabuleiroAdversario[c.x][c.y + 1] = 6;
                        return true;
                    }
                    return false;
                } else if (!vertical && !posAlternativa) {
                    if (verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 2, COLUNAS)
                            && verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)) {
                        tabuleiroAdversario[c.x][c.y] = 6;
                        tabuleiroAdversario[c.x][c.y + 1] = 6;
                        tabuleiroAdversario[c.x][c.y + 2] = 6;
                        tabuleiroAdversario[c.x - 1][c.y] = 6;
                        tabuleiroAdversario[c.x + 1][c.y] = 6;
                        return true;
                    }
                    return false;
                } else if (vertical && posAlternativa) {
                    if (verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x - 2, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y + 1, COLUNAS)) {
                        tabuleiroAdversario[c.x][c.y] = 6;
                        tabuleiroAdversario[c.x - 1][c.y] = 6;
                        tabuleiroAdversario[c.x - 2][c.y] = 6;
                        tabuleiroAdversario[c.x][c.y - 1] = 6;
                        tabuleiroAdversario[c.x][c.y + 1] = 6;
                        return true;
                    }
                    return false;
                } else {
                    if (verificaSePosicaoValida(c.x, c.y - 1, COLUNAS)
                            && verificaSePosicaoValida(c.x, c.y - 2, COLUNAS)
                            && verificaSePosicaoValida(c.x - 1, c.y, COLUNAS)
                            && verificaSePosicaoValida(c.x + 1, c.y, COLUNAS)) {
                        tabuleiroAdversario[c.x][c.y] = 6;
                        tabuleiroAdversario[c.x][c.y - 1] = 6;
                        tabuleiroAdversario[c.x][c.y - 2] = 6;
                        tabuleiroAdversario[c.x - 1][c.y] = 6;
                        tabuleiroAdversario[c.x + 1][c.y] = 6;
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }
    }

    // Apenas para a criacao do tabuleiro do adversario
    private boolean verificaPosicaoParaBarco1(CoordXY posXY, Jogadores opcao) {

        if (posXY == null)
            return false;

        int COLUNAS = new Double(sqrt(Utils.BOARD_TAM)).intValue();

        int l = posXY.x;
        int c = posXY.y;

        int tabuleiro[][];
        if (opcao == Jogadores.JOGADOR)
            tabuleiro = tabuleiroJogador;
        else if (opcao == Jogadores.ARDUINO)
            tabuleiro = tabuleiroArduino;
        else
            tabuleiro = tabuleiroCPU;

        // ESQ CIMA
        if (verificaSePosicaoValida(l - 1,c - 1, COLUNAS)) {
            if (tabuleiro[l - 1][c - 1] != 1)
                return false;
        }
        // CIMA
        if (verificaSePosicaoValida(l - 1, c, COLUNAS)) {
            if (tabuleiro[l - 1][c] != 1)
                return false;
        }
        // DIR CIMA
        if (verificaSePosicaoValida(l - 1,c + 1, COLUNAS)) {
            if (tabuleiro[l - 1][c + 1] != 1)
                return false;
        }
        // ESQ
        if (verificaSePosicaoValida(l,c - 1, COLUNAS)) {
            if (tabuleiro[l][c - 1] != 1)
                return false;
        }
        // POS ESCOLHIDA
        if (tabuleiro[l][c] != 1)
            return false;
        // DIR
        if (verificaSePosicaoValida(l,c + 1, COLUNAS)) {
            if (tabuleiro[l][c + 1] != 1)
                return false;
        }
        // ESQ BAIXO
        if (verificaSePosicaoValida(l + 1, c - 1, COLUNAS)) {
            if (tabuleiro[l + 1][c - 1] != 1)
                return false;
        }
        // BAIXO
        if (verificaSePosicaoValida(l + 1, c, COLUNAS)) {
            if (tabuleiro[l + 1][c] != 1)
                return false;
        }
        // DIR BAIXO
        if (verificaSePosicaoValida(l + 1,c + 1, COLUNAS)) {
            if (tabuleiro[l + 1][c + 1] != 1)
                return false;
        }

        return true;
    }
    private boolean verificaPosicaoParaBarco2(CoordXY posXY, boolean vertical, Jogadores opcao) {

        if (posXY == null)
            return false;

        int COLUNAS = new Double(sqrt(Utils.BOARD_TAM)).intValue();

        int l = posXY.x;
        int c = posXY.y;

        int tabuleiro[][];
        if (opcao == Jogadores.JOGADOR)
            tabuleiro = tabuleiroJogador;
        else if (opcao == Jogadores.ARDUINO)
            tabuleiro = tabuleiroArduino;
        else
            tabuleiro = tabuleiroCPU;

        if (vertical) {
            // ESQ BAIXO2
            if (verificaSePosicaoValida(l + 2, c - 1, COLUNAS)) {
                if (tabuleiro[l + 2][c - 1] != 1) {
                    return false;
                }
            }
            // BAIXO2
            if (verificaSePosicaoValida(l + 2, c, COLUNAS)) {
                if (tabuleiro[l + 2][c] != 1) {
                    return false;
                }
            }
            // DIR BAIXO2
            if (verificaSePosicaoValida(l + 2, c + 1, COLUNAS)) {
                if (tabuleiro[l + 2][c + 1] != 1) {
                    return false;
                }
            }
        }
        else {
            // CIMA DIR2
            if (verificaSePosicaoValida(l - 1, c + 2, COLUNAS)) {
                if (tabuleiro[l - 1][c + 2] != 1) {
                    return false;
                }
            }
            // DIR2
            if (verificaSePosicaoValida(l, c + 2, COLUNAS)) {
                if (tabuleiro[l][c + 2] != 1) {
                    return false;
                }
            }
            // BAIXO DIR2
            if (verificaSePosicaoValida(l + 1, c + 2, COLUNAS)) {
                if (tabuleiro[l + 1][c + 2] != 1) {
                    return false;
                }
            }
        }

        return verificaPosicaoParaBarco1(posXY, opcao);
    }
    private boolean verificaPosicaoParaBarco3(CoordXY posXY, boolean vertical, Jogadores opcao) {

        if (posXY == null)
            return false;

        int COLUNAS = new Double(sqrt(Utils.BOARD_TAM)).intValue();

        int l = posXY.x;
        int c = posXY.y;

        int tabuleiro[][];
        if (opcao == Jogadores.JOGADOR)
            tabuleiro = tabuleiroJogador;
        else if (opcao == Jogadores.ARDUINO)
            tabuleiro = tabuleiroArduino;
        else
            tabuleiro = tabuleiroCPU;


        if (vertical) {
            // ESQ BAIXO3
            if (verificaSePosicaoValida(l + 3, c - 1, COLUNAS)) {
                if (tabuleiro[l + 3][c - 1] != 1) {
                    return false;
                }
            }
            // BAIXO3
            if (verificaSePosicaoValida(l + 3, c, COLUNAS)) {
                if (tabuleiro[l + 3][c] != 1) {
                    return false;
                }
            }
            // DIR BAIXO3
            if (verificaSePosicaoValida(l + 3, c + 1, COLUNAS)) {
                if (tabuleiro[l + 3][c + 1] != 1) {
                    return false;
                }
            }
        }
        else {
            // CIMA DIR2
            if (verificaSePosicaoValida(l - 1, c + 3, COLUNAS)) {
                if (tabuleiro[l - 1][c + 3] != 1) {
                    return false;
                }
            }
            // DIR2
            if (verificaSePosicaoValida(l, c + 3, COLUNAS)) {
                if (tabuleiro[l][c + 3] != 1) {
                    return false;
                }
            }
            // BAIXO DIR2
            if (verificaSePosicaoValida(l + 1, c + 3, COLUNAS)) {
                if (tabuleiro[l + 1][c + 3] != 1) {
                    return false;
                }
            }
        }

        return verificaPosicaoParaBarco2(posXY, vertical, opcao);
    }
    private boolean verificaPosicaoParaBarco5(CoordXY posXY, boolean vertical, boolean posAlternativa,
                                              Jogadores opcao) {

        if (posXY == null)
            return false;

        int COLUNAS = Utils.BOARD_COLUMNS;

        int l = posXY.x;
        int c = posXY.y;


        int tabuleiro[][];
        if (opcao == Jogadores.JOGADOR)
            tabuleiro = tabuleiroJogador;
        else if (opcao == Jogadores.ARDUINO)
            tabuleiro = tabuleiroArduino;
        else
            tabuleiro = tabuleiroCPU;
        Log.e("aqui", "Tiago " + Arrays.toString(tabuleiro));

        if (vertical && !posAlternativa) {
            // ESQ2 CIMA
            if (verificaSePosicaoValida(l - 1, c - 2, COLUNAS)) {
                if (tabuleiro[l - 1][c - 2] > 1) {
                    return false;
                }
            }
            // ESQ2
            if (verificaSePosicaoValida(l, c - 2, COLUNAS)) {
                if (tabuleiro[l][c - 2] > 1) {
                    return false;
                }
            }
            // ESQ2 BAIXO
            if (verificaSePosicaoValida(l + 1, c - 2, COLUNAS)) {
                if (tabuleiro[l + 1][c - 2] > 1) {
                    return false;
                }
            }

            // DIR2 CIMA
            if (verificaSePosicaoValida(l - 1, c + 2, COLUNAS)) {
                if (tabuleiro[l - 1][c + 2] > 1) {
                    return false;
                }
            }
            // DIR2
            if (verificaSePosicaoValida(l, c + 2, COLUNAS)) {
                if (tabuleiro[l][c + 2] > 1) {
                    return false;
                }
            }
            // DIR2 BAIXO
            if (verificaSePosicaoValida(l + 1, c + 2, COLUNAS)) {
                if (tabuleiro[l + 1][c + 2] > 1) {
                    return false;
                }
            }
            return verificaPosicaoParaBarco3(posXY, vertical, opcao);
        }
        else if (!vertical && !posAlternativa) {
            // CIMA2 ESQ
            if (verificaSePosicaoValida(l - 2, c - 1, COLUNAS)) {
                if (tabuleiro[l - 2][c - 1] > 1) {
                    return false;
                }
            }
            // CIMA2
            if (verificaSePosicaoValida(l - 2, c, COLUNAS)) {
                if (tabuleiro[l - 2][c] > 1) {
                    return false;
                }
            }
            // CIMA2 DIR
            if (verificaSePosicaoValida(l - 2, c + 1, COLUNAS)) {
                if (tabuleiro[l - 2][c + 1] > 1) {
                    return false;
                }
            }

            // BAIXO2 ESQ
            if (verificaSePosicaoValida(l + 2, c - 1, COLUNAS)) {
                if (tabuleiro[l + 2][c - 1] > 1) {
                    return false;
                }
            }
            // BAIXO2
            if (verificaSePosicaoValida(l + 2, c, COLUNAS)) {
                if (tabuleiro[l + 2][c] > 1) {
                    return false;
                }
            }
            // BAIXO2 DIR
            if (verificaSePosicaoValida(l + 2, c + 1, COLUNAS)) {
                if (tabuleiro[l + 2][c + 1] > 1) {
                    return false;
                }
            }
            return verificaPosicaoParaBarco3(posXY, vertical, opcao);
        }
        else if (vertical && posAlternativa) {

            // Posicao Actual
            if (verificaSePosicaoValida(l, c, COLUNAS)) {
                if (tabuleiro[l][c] > 1)
                    return false;
            }
            // ESQ
            if (verificaSePosicaoValida(l, c - 1, COLUNAS)) {
                if (tabuleiro[l][c - 1] > 1)
                    return false;
            }
            // ESQ2
            if (verificaSePosicaoValida(l, c - 2, COLUNAS)) {
                if (tabuleiro[l][c - 2] > 1)
                    return false;
            }
            // DIR
            if (verificaSePosicaoValida(l, c + 1, COLUNAS)) {
                if (tabuleiro[l][c + 1] > 1)
                    return false;
            }
            // DIR2
            if (verificaSePosicaoValida(l, c + 2, COLUNAS)) {
                if (tabuleiro[l][c + 2] > 1)
                    return false;
            }

            // BAIXO
            if (verificaSePosicaoValida(l + 1, c, COLUNAS)) {
                if (tabuleiro[l + 1][c] > 1)
                    return false;
            }
            // BAIXO ESQ
            if (verificaSePosicaoValida(l + 1, c - 1, COLUNAS)) {
                if (tabuleiro[l + 1][c - 1] > 1)
                    return false;
            }
            // BAIXO ESQ2
            if (verificaSePosicaoValida(l + 1, c - 2, COLUNAS)) {
                if (tabuleiro[l + 1][c - 2] > 1)
                    return false;
            }
            // BAIXO DIR
            if (verificaSePosicaoValida(l + 1, c + 1, COLUNAS)) {
                if (tabuleiro[l + 1][c + 1] > 1)
                    return false;
            }
            // BAIXO DIR2
            if (verificaSePosicaoValida(l + 1, c + 2, COLUNAS)) {
                if (tabuleiro[l + 1][c + 2] > 1)
                    return false;
            }

            // CIMA
            if (verificaSePosicaoValida(l - 1, c, COLUNAS)) {
                if (tabuleiro[l - 1][c] > 1)
                    return false;
            }
            // CIMA ESQ
            if (verificaSePosicaoValida(l - 1, c - 1, COLUNAS)) {
                if (tabuleiro[l - 1][c - 1] > 1)
                    return false;
            }
            // CIMA ESQ2
            if (verificaSePosicaoValida(l - 1, c - 2, COLUNAS)) {
                if (tabuleiro[l - 1][c - 2] > 1)
                    return false;
            }
            // CIMA DIR
            if (verificaSePosicaoValida(l - 1, c + 1, COLUNAS)) {
                if (tabuleiro[l - 1][c + 1] > 1)
                    return false;
            }
            // CIMA DIR2
            if (verificaSePosicaoValida(l - 1, c + 2, COLUNAS)) {
                if (tabuleiro[l - 1][c + 2] > 1)
                    return false;
            }

            // CIMA2
            if (verificaSePosicaoValida(l - 2, c, COLUNAS)) {
                if (tabuleiro[l - 2][c] > 1)
                    return false;
            }
            // CIMA2 ESQ
            if (verificaSePosicaoValida(l - 2, c - 1, COLUNAS)) {
                if (tabuleiro[l - 2][c - 1] > 1)
                    return false;
            }
            // CIMA2 DIR
            if (verificaSePosicaoValida(l - 2, c + 1, COLUNAS)) {
                if (tabuleiro[l - 2][c + 1] > 1)
                    return false;
            }

            // CIMA3
            if (verificaSePosicaoValida(l - 3, c, COLUNAS)) {
                if (tabuleiro[l - 3][c] > 1)
                    return false;
            }
            // CIMA3 ESQ
            if (verificaSePosicaoValida(l - 3, c - 1, COLUNAS)) {
                if (tabuleiro[l - 3][c - 1] > 1)
                    return false;
            }
            // CIMA3 DIR
            if (verificaSePosicaoValida(l - 3, c + 1, COLUNAS)) {
                if (tabuleiro[l - 3][c + 1] > 1)
                    return false;
            }
            return true;
        }
        else {
            // Posicao Actual
            if (verificaSePosicaoValida(l, c, COLUNAS)) {
                if (tabuleiro[l][c] > 1)
                    return false;
            }
            // DIR
            if (verificaSePosicaoValida(l, c + 1, COLUNAS)) {
                if (tabuleiro[l][c + 1] > 1)
                    return false;
            }
            // ESQ
            if (verificaSePosicaoValida(l, c - 1, COLUNAS)) {
                if (tabuleiro[l][c - 1] > 1)
                    return false;
            }
            // ESQ2
            if (verificaSePosicaoValida(l, c - 2, COLUNAS)) {
                if (tabuleiro[l][c - 2] > 1)
                    return false;
            }
            // ESQ3
            if (verificaSePosicaoValida(l, c - 3, COLUNAS)) {
                if (tabuleiro[l][c - 3] > 1)
                    return false;
            }

            // BAIXO
            if (verificaSePosicaoValida(l + 1, c, COLUNAS)) {
                if (tabuleiro[l + 1][c] > 1)
                    return false;
            }
            // BAIXO DIR
            if (verificaSePosicaoValida(l + 1, c + 1, COLUNAS)) {
                if (tabuleiro[l + 1][c + 1] > 1)
                    return false;
            }
            // BAIXO ESQ
            if (verificaSePosicaoValida(l + 1, c - 1, COLUNAS)) {
                if (tabuleiro[l + 1][c - 1] > 1)
                    return false;
            }
            // BAIXO ESQ2
            if (verificaSePosicaoValida(l + 1, c - 2, COLUNAS)) {
                if (tabuleiro[l + 1][c - 2] > 1)
                    return false;
            }
            // BAIXO ESQ3
            if (verificaSePosicaoValida(l + 1, c - 3, COLUNAS)) {
                if (tabuleiro[l + 1][c - 3] > 1)
                    return false;
            }

            // CIMA
            if (verificaSePosicaoValida(l - 1, c, COLUNAS)) {
                if (tabuleiro[l - 1][c] > 1)
                    return false;
            }
            // CIMA DIR
            if (verificaSePosicaoValida(l - 1, c + 1, COLUNAS)) {
                if (tabuleiro[l - 1][c + 1] > 1)
                    return false;
            }
            // CIMA ESQ
            if (verificaSePosicaoValida(l - 1, c - 1, COLUNAS)) {
                if (tabuleiro[l - 1][c - 1] > 1)
                    return false;
            }
            // CIMA ESQ2
            if (verificaSePosicaoValida(l - 1, c - 2, COLUNAS)) {
                if (tabuleiro[l - 1][c - 2] > 1)
                    return false;
            }
            // CIMA ESQ3
            if (verificaSePosicaoValida(l - 1, c - 3, COLUNAS)) {
                if (tabuleiro[l - 1][c - 3] > 1)
                    return false;
            }

            // BAIXO2
            if (verificaSePosicaoValida(l + 2, c, COLUNAS)) {
                if (tabuleiro[l + 2][c] > 1)
                    return false;
            }
            // BAIXO2 DIR
            if (verificaSePosicaoValida(l + 2, c + 1, COLUNAS)) {
                if (tabuleiro[l + 2][c + 1] > 1)
                    return false;
            }
            // BAIXO2 ESQ
            if (verificaSePosicaoValida(l + 2, c - 1, COLUNAS)) {
                if (tabuleiro[l + 2][c - 1] > 1)
                    return false;
            }

            // CIMA2
            if (verificaSePosicaoValida(l - 2, c, COLUNAS)) {
                if (tabuleiro[l - 2][c] > 1)
                    return false;
            }
            // CIMA2 DIR
            if (verificaSePosicaoValida(l - 2, c + 1, COLUNAS)) {
                if (tabuleiro[l - 2][c + 1] > 1)
                    return false;
            }
            // CIMA2 ESQ
            if (verificaSePosicaoValida(l - 2, c - 1, COLUNAS)) {
                if (tabuleiro[l - 2][c - 1] > 1)
                    return false;
            }
            return true;
        }

    }

    public int obtemNumeroTiros() {

        int tabuleiro[][];

        if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_ARDUINO) {
            tabuleiro = tabuleiroArduino;
        } else if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_CPU) {
            tabuleiro = tabuleiroCPU;
        } else {
            throw new RuntimeException("Invalido para multiplayer");
        }

        final int col = obtemColunas();
        int barcos_atingidos = 0;

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < col; j++) {
                if (tabuleiro[i][j] == -2)
                    barcos_atingidos++;
                else if (tabuleiro[i][j] == -3)
                    barcos_atingidos++;
                else if (tabuleiro[i][j] == -4)
                    barcos_atingidos++;
                else if (tabuleiro[i][j] == -6)
                    barcos_atingidos++;
            }
        }

        return barcos_atingidos;
    }
    public int obtemNumeroTirosMultiplayer() {

        final int col = obtemColunas();
        int barcos_atingidos = 0;

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < col; j++) {
                if (tabuleiroFirebase[i][j] == -2)
                    barcos_atingidos++;
                else if (tabuleiroFirebase[i][j] == -3)
                    barcos_atingidos++;
                else if (tabuleiroFirebase[i][j] == -4)
                    barcos_atingidos++;
                else if (tabuleiroFirebase[i][j] == -6)
                    barcos_atingidos++;
            }
        }

        return barcos_atingidos;
    }

    /**
     *
     * @return array com duas posicoes, sendo a primeira o numero de barcos atingidos, e segunda
     * o numero de barcos afundados
     */
    public int[] barcosAtingidosAfundadosMultiplayer() {

        int atingidos, afundados;
        atingidos = afundados = 0;

        int tabuleiro[][] = tabuleiroFirebase;

        for (int i = 0; i < Utils.BOARD_COLUMNS; i++) {
            for (int j = 0; j < Utils.BOARD_COLUMNS; j++) {
                if (tabuleiroFirebase[i][j] == 2 || tabuleiroFirebase[i][j] == -2) {
                    int a[] = barcos1Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
                else if (tabuleiroFirebase[i][j] == 3 || tabuleiroFirebase[i][j] == -3) {
                    int a[] = barcos2Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
                else if (tabuleiroFirebase[i][j] == 4 || tabuleiroFirebase[i][j] == -4) {
                    int a[] = barcos3Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
            }
        }

        int a[] = barcos5Atingidos(tabuleiro);
        atingidos += a[0];
        afundados += a[1];

        int p[] = new int[2];
        p[0] = atingidos;
        p[1] = afundados;
        return p;
    }
    public int[] barcosAtingidosAfundados() {

        int atingidos, afundados;
        atingidos = afundados = 0;

        int tabuleiro[][];

        if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_CPU)
            tabuleiro = tabuleiroCPU;
        else
            tabuleiro = tabuleiroArduino;

        for (int i = 0; i < Utils.BOARD_COLUMNS; i++) {
            for (int j = 0; j < Utils.BOARD_COLUMNS; j++) {
                if (tabuleiro[i][j] == 2 || tabuleiro[i][j] == -2) {
                    int a[] = barcos1Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
                else if (tabuleiro[i][j] == 3 || tabuleiro[i][j] == -3) {
                    int a[] = barcos2Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
                else if (tabuleiro[i][j] == 4 || tabuleiro[i][j] == -4) {
                    int a[] = barcos3Atingidos(i, j, tabuleiro);
                    atingidos += a[0];
                    afundados += a[1];
                }
            }
        }

        int a[] = barcos5Atingidos(tabuleiro);
        atingidos += a[0];
        afundados += a[1];

        int p[] = new int[2];
        p[0] = atingidos;
        p[1] = afundados;
        return p;
    }
    public int[] barcos1Atingidos(int l, int c, int[][] tabuleiro) {

        int p[] = new int[2];
        int afundado = 0, atingido = 0;

        if (verificaSePosicaoValida(l, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == -2) {
                tabuleiro[l][c] = 0;
                atingido++;
                afundado++;
            }
            if (tabuleiro[l][c] == 2)
                tabuleiro[l][c] = 0;
        }

        p[0] = atingido;
        p[1] = afundado;
        return p;
    }
    public int[] barcos2Atingidos(int l, int c, int[][] tabuleiro) {

        int p[] = new int[2];
        int afundado = 0, atingido = 0;


        if (verificaSePosicaoValida(l, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == -3)
                atingido++;
        }
        if (verificaSePosicaoValida(l, c - 1, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c - 1] == -3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
                if (atingido != 0)
                    afundado++;
                else
                    atingido++;
            }
        }
        if (verificaSePosicaoValida(l, c + 1, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c + 1] == -3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
                if (atingido != 0)
                    afundado++;
                else
                    atingido++;
            }
        }
        if (verificaSePosicaoValida(l - 1, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l - 1][c] == -3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
                if (atingido != 0)
                    afundado++;
                else
                    atingido++;
            }
        }
        if (verificaSePosicaoValida(l + 1, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l + 1][c] == -3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
                if (atingido != 0)
                    afundado++;
                else
                    atingido++;
            }
        }
        if (verificaSePosicaoValida(l, c - 1, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c - 1] == 3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
            }
        }
        if (verificaSePosicaoValida(l, c + 1, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c + 1] == 3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
            }
        }
        if (verificaSePosicaoValida(l - 1, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l - 1][c] == 3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
            }
        }
        if (verificaSePosicaoValida(l + 1, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l + 1][c] == 3) {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
            }
        }

        p[0] = atingido;
        p[1] = afundado;
        return p;
    }
    public int[] barcos3Atingidos(int l, int c, int [][]tabuleiro) {

        int p[] = new int[2];
        int afundado = 0, atingido = 0;

        // DIR
        if (verificaSePosicaoValida(l, c + 1, Utils.BOARD_COLUMNS) && verificaSePosicaoValida(l, c + 2, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == 4 && tabuleiro[l][c + 1] == 4 && tabuleiro[l][c + 2] == 4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
                tabuleiro[l][c + 2] = 0;
            }
            else if (tabuleiro[l][c] == -4 && tabuleiro[l][c + 1] == -4 && tabuleiro[l][c + 2] == -4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
                tabuleiro[l][c + 2] = 0;
                atingido++;
                afundado++;
            }
            else if (tabuleiro[l][c] == 4
                    && (tabuleiro[l][c + 1] == 4 || tabuleiro[l][c + 1] == -4)
                    && (tabuleiro[l][c + 2] == -4 || tabuleiro[l][c + 2] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
                tabuleiro[l][c + 2] = 0;
                atingido++;
            }
            else if (tabuleiro[l][c] == -4
                    && (tabuleiro[l][c + 1] == 4 || tabuleiro[l][c + 1] == -4)
                    && (tabuleiro[l][c + 2] == -4 || tabuleiro[l][c + 2] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c + 1] = 0;
                tabuleiro[l][c + 2] = 0;
                atingido++;
            }
        }
        // ESQ
        if (verificaSePosicaoValida(l, c - 1, Utils.BOARD_COLUMNS) && verificaSePosicaoValida(l, c - 2, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == 4 && tabuleiro[l][c - 1] == 4 && tabuleiro[l][c - 2] == 4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
                tabuleiro[l][c - 2] = 0;
            }
            else if (tabuleiro[l][c] == -4 && tabuleiro[l][c - 1] == -4 && tabuleiro[l][c - 2] == -4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
                tabuleiro[l][c - 2] = 0;
                atingido++;
                afundado++;
            }
            else if (tabuleiro[l][c] == 4
                    && (tabuleiro[l][c - 1] == 4 || tabuleiro[l][c - 1] == -4)
                    && (tabuleiro[l][c - 2] == -4 || tabuleiro[l][c - 2] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
                tabuleiro[l][c - 2] = 0;
                atingido++;
            }
            else if (tabuleiro[l][c] == -4
                    && (tabuleiro[l][c - 1] == 4 || tabuleiro[l][c - 1] == -4)
                    && (tabuleiro[l][c - 2] == -4 || tabuleiro[l][c - 2] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l][c - 1] = 0;
                tabuleiro[l][c - 2] = 0;
                atingido++;
            }
        }
        // BAIXO
        if (verificaSePosicaoValida(l + 1, c, Utils.BOARD_COLUMNS) && verificaSePosicaoValida(l + 2, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == 4 && tabuleiro[l + 1][c] == 4 && tabuleiro[l + 2][c] == 4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
                tabuleiro[l + 2][c] = 0;
            }
            else if (tabuleiro[l][c] == -4 && tabuleiro[l + 1][c] == -4 && tabuleiro[l + 2][c] == -4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
                tabuleiro[l + 2][c] = 0;
                atingido++;
                afundado++;
            }
            else if (tabuleiro[l][c] == 4
                    && (tabuleiro[l + 1][c] == 4 || tabuleiro[l + 1][c] == -4)
                    && (tabuleiro[l + 2][c] == -4 || tabuleiro[l + 2][c] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
                tabuleiro[l + 2][c] = 0;
                atingido++;
            }
            else if (tabuleiro[l][c] == -4
                    && (tabuleiro[l + 1][c] == 4 || tabuleiro[l + 1][c] == -4)
                    && (tabuleiro[l + 2][c] == -4 || tabuleiro[l + 2][c] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l + 1][c] = 0;
                tabuleiro[l + 2][c] = 0;
                atingido++;
            }
        }
        // CIMA
        if (verificaSePosicaoValida(l - 1, c, Utils.BOARD_COLUMNS) && verificaSePosicaoValida(l - 2, c, Utils.BOARD_COLUMNS)) {
            if (tabuleiro[l][c] == 4 && tabuleiro[l - 1][c] == 4 && tabuleiro[l - 2][c] == 4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
                tabuleiro[l - 2][c] = 0;
            }
            else if (tabuleiro[l][c] == -4 && tabuleiro[l - 1][c] == -4 && tabuleiro[l - 2][c] == -4) {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
                tabuleiro[l - 2][c] = 0;
                atingido++;
                afundado++;
            }
            else if (tabuleiro[l][c] == 4
                    && (tabuleiro[l - 1][c] == 4 || tabuleiro[l - 1][c] == -4)
                    && (tabuleiro[l - 2][c] == -4 || tabuleiro[l - 2][c] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
                tabuleiro[l - 2][c] = 0;
                atingido++;
            }
            else if (tabuleiro[l][c] == -4
                    && (tabuleiro[l - 1][c] == 4 || tabuleiro[l - 1][c] == -4)
                    && (tabuleiro[l - 2][c] == -4 || tabuleiro[l - 2][c] == 4))
            {
                tabuleiro[l][c] = 0;
                tabuleiro[l - 1][c] = 0;
                tabuleiro[l - 2][c] = 0;
                atingido++;
            }
        }

        p[0] = atingido;
        p[1] = afundado;
        return p;
    }
    public int[] barcos5Atingidos(int[][] tabuleiro) {

        int p[] = new int[2];
        int cnt = 0;

        p[0] = 0;
        p[1] = 0;

        ciclo:
        for (int i = 0; i < Utils.BOARD_COLUMNS; i++) {
            for (int j = 0; j < Utils.BOARD_COLUMNS; j++) {
                if (tabuleiro[i][j] == -6)
                    cnt++;
                if (cnt >= 5)
                    break ciclo;
            }
        }
        if (cnt >= 5) {
            p[0] = 1;
            p[1] = 1;
        }
        else if (cnt > 0) {
            p[0] = 1;
            p[0] = 0;
        }
        else {
            p[0] = 0;
            p[1] = 0;
        }
        return p;
    }

    private boolean verificaSePosicaoValida(int x, int y, int colunas) {
        return ((x >= 0 && x < colunas) && (y >= 0 && y < colunas));
    }
    public CoordXY converter1DPara2D(int pos) {
        Double colunas = sqrt(Utils.BOARD_TAM);
        int col = colunas.intValue();
        if (!verificaSePosicaoValida(pos/col, pos % col, col))
            return null;
        return new CoordXY(pos / col, pos % col);
    }

    public int obtemTamanhoDosTabuleiros() {
        return Utils.BOARD_TAM;
    }
    public void resetTabuleiro(Jogadores jogador) {

        int[][] tabuleiro;

        switch (jogador) {
            case JOGADOR:
                tabuleiro = tabuleiroJogador;
                break;
            case CPU:
                if (tabuleiroCPU == null) tabuleiroCPU = new int[obtemColunas()][obtemColunas()];
                tabuleiro = tabuleiroCPU;
                break;
            case ARDUINO:
                if (tabuleiroArduino == null) tabuleiroArduino = new int[obtemColunas()][obtemColunas()];
                tabuleiro = tabuleiroArduino;
                break;
            default:
                throw new RuntimeException("Jogador invalido para criar tabuleiro");
        }

        // preencher o tabuleiro a 1
        for (int[] linha : tabuleiro) {
            Arrays.fill(linha, 1);
        }
    }

    /**
     * Check if game is over (and return who won)
     * @return 2 - you won, 1 - opponent won, 0 - game is not over yet
     */
    public int jogoAcabou() {
        int jogadorCnt = 0, advCnt = 0; // numero de pecas do jogador e do Adversario (CPU/Arduino)

        // numero de celulas ocupadas pelos barcos
        // (uma celula * dois barcos 1) + (duas celulas * dois barcos 2) + ...
        int PECAS = (2 * 1) + (2 * 2) + (2 * 3) + (1 * 5);

        // Estive ciclo verifica se o barco foi atingido (-2 = celula com barco atingido)
        ciclo:
        for (int i = 0; i < obtemColunas(); i++) {
            for (int j = 0; j < obtemColunas(); j++) {
                if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_CPU && tabuleiroCPU[i][j] <= -2)
                    jogadorCnt++;
                if (modoDeJogo == ModoDeJogo.SINGLE_PLAYER_ARDUINO && tabuleiroArduino[i][j] <= -2)
                    jogadorCnt++;
                if (modoDeJogo == ModoDeJogo.MULTIPLAYER && tabuleiroFirebase[i][j] <= -2)
                    jogadorCnt++;
                if (tabuleiroJogador[i][j] <= -2)
                    advCnt++;
                if (jogadorCnt >= PECAS || advCnt >= PECAS)
                    break ciclo;
            }
        }
        if (jogadorCnt >= PECAS)
            return 2;
        else if (advCnt >= PECAS)
            return 1;
        else
            return 0;
    }
    public int obtemColunas() {
        return new Double(sqrt(Utils.BOARD_TAM)).intValue();
    }
    public Jogadores quemComeca() {

        Random random = new Random();
        boolean whoStarts = random.nextBoolean();

        switch (modoDeJogo) {
            case SINGLE_PLAYER_CPU:
                return whoStarts ? Jogadores.JOGADOR : Jogadores.CPU;
            case SINGLE_PLAYER_ARDUINO:
                return whoStarts ? Jogadores.JOGADOR : Jogadores.ARDUINO;
            default:
                return whoStarts ? Jogadores.JOGADOR : Jogadores.CPU;
        }
    }

    /**
     * Associa e coloca a imagem correspondente ao valor no tabuleiro
     * @param iv
     * @param pos
     * @param opcao
     * @return objeto ImageView com a imagem do valor no tabuleiro
     */
    public ImageView colocaImagemBaseadoNosNumeros(ImageView iv, int pos, Jogadores opcao) {

        int col = obtemColunas();
        int l = pos / col;
        int c = pos % col;

        if (opcao == Jogadores.JOGADOR) {
            if (tabuleiroJogador[l][c] == 1)
                iv.setImageResource(R.drawable.water);
            else if (tabuleiroJogador[l][c] > 1)
                iv.setImageResource(R.drawable.boat_alt);
            else if (tabuleiroJogador[l][c] == -1)
                iv.setImageResource(R.drawable.bad_shot_alt);
            else
                iv.setImageResource(R.drawable.boat_sinking);
        }
        else if (opcao == Jogadores.ARDUINO) {
            if (tabuleiroArduino[l][c] >= 1)
                iv.setImageResource(R.drawable.card_alt);
            else if (tabuleiroArduino[l][c] == 0)
                iv.setImageResource(R.drawable.selected);
            else if (tabuleiroArduino[l][c] == -1)
                iv.setImageResource(R.drawable.bad_shot_alt);
            else
                iv.setImageResource(R.drawable.boat_sinking);
        }
        else {
            if (tabuleiroCPU[l][c] >= 1)
                iv.setImageResource(R.drawable.card_alt);
            else if (tabuleiroCPU[l][c] == 0)
                iv.setImageResource(R.drawable.selected);
            else if (tabuleiroCPU[l][c] == -1)
                iv.setImageResource(R.drawable.bad_shot_alt);
            else
                iv.setImageResource(R.drawable.boat_sinking);
        }
        return iv;
    }

    /**
     * MULTIPLAYER ONLY!
     * @param iv
     * @param pos
     * @return
     */
    public ImageView colocaImagemBaseadoNosNumerosMultiplayerPlayer(ImageView iv, int pos) {

        int col = obtemColunas();
        int l = pos / col;
        int c = pos % col;

        if (tabuleiroJogador == null)
            throw new RuntimeException("O tabuleiro do jogador esta' a null!");

        if (tabuleiroJogador[l][c] == 1)
            iv.setImageResource(R.drawable.water);
        else if (tabuleiroJogador[l][c] > 1)
            iv.setImageResource(R.drawable.boat_alt);
        else if (tabuleiroJogador[l][c] == -1)
            iv.setImageResource(R.drawable.bad_shot_alt);
        else
            iv.setImageResource(R.drawable.boat_sinking);

        return iv;
    }
    /**
     * MULTIPLAYER ONLY!
     * @param iv
     * @param pos
     * @return
     */
    public ImageView colocaImagemBaseadoNosNumerosMultiplayerFirebase(ImageView iv, int pos) {

        int col = obtemColunas();
        int l = pos / col;
        int c = pos % col;

        if (tabuleiroFirebase == null)
            throw new RuntimeException("O tabuleiro da Firebase esta' a null!");

        if (tabuleiroFirebase[l][c] >= 1)
            iv.setImageResource(R.drawable.card_alt);
        else if (tabuleiroFirebase[l][c] == 0)
            iv.setImageResource(R.drawable.selected);
        else if (tabuleiroFirebase[l][c] == -1)
            iv.setImageResource(R.drawable.bad_shot_alt);
        else
            iv.setImageResource(R.drawable.boat_sinking);

        return iv;
    }

    public void setFirebaseBoard(int [][]firebaseBoard) {
        this.tabuleiroFirebase = firebaseBoard;
        //System.arraycopy(firebaseBoard[i], 0, this.tabuleiroFirebase[i], 0, obtemColunas());
    }

    public synchronized boolean arduinoFinished() {
        return (jogadas_arduino <= 0);
    }
    public boolean firebaseFinished() {
        return (jogadas_firebase <= 0);
    }

    public class CoordXY implements Serializable {
        int x, y;
        CoordXY(int x, int y) {this.x = x; this.y = y;}

        @Override
        public String toString() {
            return "[" + x + "," + y + "]";
        }
    }

    public enum Jogadores {
        JOGADOR, CPU, ARDUINO, FIREBASE
    }
    public enum ModoDeJogo {
        SINGLE_PLAYER_CPU, MULTIPLAYER, SINGLE_PLAYER_ARDUINO
    }
}
