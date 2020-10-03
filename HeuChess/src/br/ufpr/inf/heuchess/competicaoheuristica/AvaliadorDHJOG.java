package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.*;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro.*;
import br.ufpr.inf.utils.ArquivoLog;
import br.ufpr.inf.utils.Utils;
import br.ufpr.inf.utils.gui.UtilsGUI;
import bsh.Interpreter;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class AvaliadorDHJOG implements Avaliador {
    
    private ConjuntoHeuristico conjuntoHeuristico;    
    private ArquivoLog         arquivoLog;
    private Tabuleiro          tabuleiro;
    private JTextPane          jTextPane;
    private boolean            avaliacaoAtivada;    
    private boolean            registrarLog;
    private boolean            whiteColor;        
    private Interpreter        interpreter;
    
    private double valorPecasPEAO;
    private double valorPecasTORRE;
    private double valorPecasCAVALO;
    private double valorPecasBISPO;
    private double valorPecasDAMA;
    
    private final double[] valoresPecas;
    
    private double valorHeuristicoTabuleiro;
    
    private static Style              styleNormal = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    private static SimpleAttributeSet styleNegritoPreto;    // Usado para destacar a��es gerais
    private static SimpleAttributeSet styleNegritoAzul;     // Usado para destacar vantagem para Jogador 
    private static SimpleAttributeSet styleNegritoLaranja;  // Usado para indicar igualdade (empate)
    private static SimpleAttributeSet styleNegritoVermelho; // Usado para destacar vantagem para Oponente 
    
    static {
        styleNegritoAzul = new SimpleAttributeSet();
        StyleConstants.setBold(styleNegritoAzul, true);
        StyleConstants.setForeground(styleNegritoAzul, Color.BLUE);

        styleNegritoVermelho = new SimpleAttributeSet();
        StyleConstants.setBold(styleNegritoVermelho, true);
        StyleConstants.setForeground(styleNegritoVermelho, Color.RED);

        styleNegritoLaranja = new SimpleAttributeSet();
        StyleConstants.setBold(styleNegritoLaranja, true);
        StyleConstants.setForeground(styleNegritoLaranja, Color.ORANGE);

        styleNegritoPreto = new SimpleAttributeSet();
        StyleConstants.setBold(styleNegritoPreto, true);
        StyleConstants.setForeground(styleNegritoPreto, Color.BLACK);
    }
    
    /**
     * Construtor criado para a Execu��o de Campeonatos, recicla o Interpretador
     */
    public AvaliadorDHJOG(ArquivoLog arquivoLog) throws Exception {
                
        valoresPecas = new double[TOTAL_CASAS];
        
        interpreter = new Interpreter();
        
        this.arquivoLog   =  arquivoLog;
        this.registrarLog = (arquivoLog != null ?  true : false);
    }
    
    public AvaliadorDHJOG(ConjuntoHeuristico conjuntoHeuristico, ArquivoLog arquivoLog) throws Exception {

        this(arquivoLog);
                
        setConjuntoHeuristico(conjuntoHeuristico);
    }
    
    public final void setConjuntoHeuristico(ConjuntoHeuristico conjunto) throws Exception {
        
        assert conjuntoHeuristico != null;
        
        conjuntoHeuristico = conjunto;
        conjuntoHeuristico.preparaParaAnaliseHeuristica();
    }
    
    public ConjuntoHeuristico getConjuntoHeuristico(){
        return conjuntoHeuristico;
    }
    
    @Override
    public double avalia(final Tabuleiro tabuleiro, final JTextPane jTextPane, final boolean whiteColor) throws Exception {
        
        this.tabuleiro  = tabuleiro;
        this.whiteColor = whiteColor;
        
        if (jTextPane != null) {
            
            this.jTextPane = jTextPane;

            boolean backupRegistrarLog  = registrarLog;
            ArquivoLog backupArquivoLog = arquivoLog;

            registrarLog = true;
            arquivoLog   = null;

            double valor = avalia();

            registrarLog   = backupRegistrarLog;
            arquivoLog     = backupArquivoLog;
            this.jTextPane = null;
            
            return valor;
            
        } else {

            return avalia();
        }
    }
    
    private double avalia() throws Exception {

        if (registrarLog) {
            if (arquivoLog != null) {
                arquivoLog.registraLinhaSeparacao();
                registra("\nAvaliando Tabuleiro [" + tabuleiro.getFEN() + "]", styleNormal);
                registra("\nConjunto Heuristico [" + conjuntoHeuristico + "] para jogador " + (whiteColor ? "Branco\n" : "Preto\n"), styleNormal);
            }else{
                registra("Conjunto Heuristico [" + conjuntoHeuristico + "] para jogador " + (whiteColor ? "Branco\n" : "Preto\n"), styleNormal);
            }
        }
        
        valorHeuristicoTabuleiro = 0;        
        avaliacaoAtivada = true;
        
        //////////////////////////////////////////////////////////////////////////////////////////////
        // Verifica Situa��o de Xeque-mate e Empates /////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        boolean isCurrentColorWhite = tabuleiro.isWhiteActive();
        
        if (tabuleiro.getMovimentosValidosJogador(isCurrentColorWhite).length == 0) {

            if (tabuleiro.estaEmXeque(isCurrentColorWhite)) {
                
                if (isCurrentColorWhite == whiteColor){
                    
                    valorHeuristicoTabuleiro = DHJOG.XEQUE_MATE_OPONENTE;
                    
                    if (registrarLog) {
                        registra("\nJogador " + (whiteColor ? "Branco " : "Preto "), styleNormal); 
                        registra("Perdeu", styleNegritoVermelho);
                        registra(" pois recebeu um Xeque-mate!", styleNormal);
                    }
                }else{
                    
                    valorHeuristicoTabuleiro = DHJOG.XEQUE_MATE_EU;
                    
                    if (registrarLog) {
                        registra("\nJogador " + (whiteColor ? "Branco " : "Preto "), styleNormal); 
                        registra("Venceu", styleNegritoAzul);
                        registra(" pois aplicou Xeque-mate!", styleNormal);
                    }
                }                
            } else{
                
                valorHeuristicoTabuleiro = DHJOG.EMPATE;
                
                if (registrarLog) {
                    registra("\nPartida ", styleNormal);
                    registra("Empatada", styleNegritoLaranja);
                    registra(" pois o Rei " + (isCurrentColorWhite ? "Branco" : "Preto") + 
                             " ficou Afogado (Ficou sem movimentos v�lidos)!", styleNormal);
                }
            }
            
        } else
            if (tabuleiro.podeEmpatar50Movimentos()) {

                valorHeuristicoTabuleiro = DHJOG.EMPATE;
                
                if (registrarLog) {
                    registra("\nPartida ", styleNormal);
                    registra("Empatada", styleNegritoLaranja);
                    registra(" pela Regra de 50 Movimentos sem captura ou movimenta��o de Pe�es!", styleNormal);
                }

            } else 
                if (tabuleiro.podeEmpatarTriplaRepeticao()) {

                    valorHeuristicoTabuleiro = DHJOG.EMPATE;
                    
                    if (registrarLog) {
                        registra("\nPartida ", styleNormal);
                        registra("Empatada", styleNegritoLaranja);
                        registra(" pela Regra da Tripla Repeti��o de um Tabuleiro!", styleNormal);
                    }
                }
        
        //////////////////////////////////////////////////////////////////////////////////////////////
        // Achou um estado terminal de jogo //////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (valorHeuristicoTabuleiro != 0) {
            
            if (registrarLog) {
                registra("\n\nValor FINAL do TABULEIRO: ", styleNormal);
                registra(DHJOG.textoValorTabuleiro(valorHeuristicoTabuleiro, false), valorHeuristicoTabuleiro > 0 ? styleNegritoAzul : 
                                                           valorHeuristicoTabuleiro < 0 ? styleNegritoVermelho : styleNegritoLaranja);
                
                if (arquivoLog != null) {
                    arquivoLog.registraLinhaSeparacao();
                }
            }

            return valorHeuristicoTabuleiro;
        }
        
        //////////////////////////////////////////////////////////////////////////////////////////////
        // Processamento do Conjunto Heur�stico //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
                
        Arrays.fill(valoresPecas, 0);
        
        boolean aplicacaoHeuristicaEu, aplicacaoHeuristicaOponente, calculouValorTabuleiro = false;                
                
        Etapa etapaAtual = conjuntoHeuristico.getEtapaInicial();

        interpreter.set("analise",    this);
        interpreter.set("etapaAtual", etapaAtual);
        
        if (registrarLog) {
            registra("\nEtapa Inicial [" + etapaAtual + "]", styleNormal);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 1 - Avaliando heur�sticas de transi��o de etapa ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 1 - An�lise das Heur�sticas de Transi��o de Etapas", styleNegritoPreto);
        }
        
        for (int x = 0; x < etapaAtual.getHeuristicasTransicaoEtapa().size(); x++) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvalia��o cancelada", styleNegritoVermelho);
                }
                return 0;
            }

            HeuristicaTransicaoEtapa heuristicaTransicaoEtapa = etapaAtual.getHeuristicasTransicaoEtapa().get(x);

            aplicacaoHeuristicaEu = verificaCondicaoHeuristica(heuristicaTransicaoEtapa,true);
                        
            if (aplicacaoHeuristicaEu) {

                etapaAtual = heuristicaTransicaoEtapa.getProximaEtapa();

                if (registrarLog) {
                    registra("\nHeur�stica ATIVADA. Nova etapa selecionada [" + etapaAtual + "]", styleNegritoAzul);
                }
                
                interpreter.set("etapaAtual", etapaAtual);

                x = 0; // Volta a processar as heur�sticas de Transi��o da nova Etapa
                
            }else{
                if (registrarLog) {
                    registra("\nHeur�stica N�O ativada", styleNormal);
                }
            }
        }
        
        //////////////////////////////////////////////////////////////////////////////////////////////
        // 2 - Inicializa valores de pe�as para a Etapa Atual ////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 2 - Inicializa-se os valores das pe�as de acordo com a Etapa Atual", styleNegritoPreto);
        }
        
        valorPecasPEAO   = etapaAtual.getValorPeao();
        valorPecasTORRE  = etapaAtual.getValorTorre();
        valorPecasCAVALO = etapaAtual.getValorCavalo();
        valorPecasBISPO  = etapaAtual.getValorBispo();
        valorPecasDAMA   = etapaAtual.getValorDama();
        
        for (int indice = 0; indice < TOTAL_CASAS; indice++) {

            Peca peca = tabuleiro.getPeca(indice);

            if (peca != null) {
                
                switch (peca.getTipo()) {

                    case PEAO:
                        valoresPecas[indice] = valorPecasPEAO;
                        break;

                    case TORRE:
                        valoresPecas[indice] = valorPecasTORRE;
                        break;

                    case CAVALO:
                        valoresPecas[indice] = valorPecasCAVALO;
                        break;

                    case BISPO:
                        valoresPecas[indice] = valorPecasBISPO;
                        break;

                    case DAMA:
                        valoresPecas[indice] = valorPecasDAMA;
                        break;
                }
            }
        }
        
        if (registrarLog) {
            registra("\n\nValores de pe�as inicializados para ambos os jogadores:\nPEAO: ", styleNormal);            
            registra(String.valueOf(valorPecasPEAO),  styleNegritoLaranja);
            registra(", TORRE: ", styleNormal);
            registra(String.valueOf(valorPecasTORRE), styleNegritoLaranja);
            registra(", CAVALO: ",styleNormal);
            registra(String.valueOf(valorPecasCAVALO),styleNegritoLaranja);
            registra(", BISPO: ", styleNormal);
            registra(String.valueOf(valorPecasBISPO), styleNegritoLaranja);
            registra(", DAMA: ",  styleNormal);
            registra(String.valueOf(valorPecasDAMA),  styleNegritoLaranja);
            
            calculaValorHeuristicoTabuleiro(true);
            calculouValorTabuleiro = true;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 3 - Avaliando heur�sticas de valora��o de Pe�as ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////

        if (jTextPane != null){
            registra("\n\nPasso 3 - An�lise das Heur�sticas de Valora��o de Pe�as", styleNegritoPreto);
        }
        
        for (HeuristicaValorPeca heuristicaValorPeca : etapaAtual.getHeuristicasValorPeca()) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvalia��o cancelada", styleNegritoVermelho);
                }
                return 0;
            }
            
            aplicacaoHeuristicaEu       = verificaCondicaoHeuristica(heuristicaValorPeca,true);
            aplicacaoHeuristicaOponente = verificaCondicaoHeuristica(heuristicaValorPeca,false);

            if (aplicacaoHeuristicaEu && !aplicacaoHeuristicaOponente){
            
                calculouValorTabuleiro = processaAcoesHeuristicaValorPeca(heuristicaValorPeca, true);
                        
            }else
                if (!aplicacaoHeuristicaEu && aplicacaoHeuristicaOponente){    
                    
                    calculouValorTabuleiro = processaAcoesHeuristicaValorPeca(heuristicaValorPeca, false);
                
                }else
                    if (aplicacaoHeuristicaEu && aplicacaoHeuristicaOponente){    
                        
                        if (registrarLog) {
                            registra("\nHeur�stica IGNORADA por tamb�m ser vantagem para o Oponente", styleNegritoLaranja);
                        }       
                    }else{
                        if (registrarLog) {
                            registra("\nHeur�stica N�O ativada", styleNormal);
                        }
                    }            
        }
        
        if (!calculouValorTabuleiro){
            calculaValorHeuristicoTabuleiro(false);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 4 - Avaliando Heur�sticas de Valor de Tabuleiro ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 4 - An�lise das Heur�sticas de Valora��o de Tabuleiro", styleNegritoPreto);
        }
        
        for (HeuristicaValorTabuleiro heuristicaValorTabuleiro : etapaAtual.getHeuristicasValorTabuleiro()) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvalia��o cancelada", styleNegritoVermelho);
                }
                return 0;
            }

            aplicacaoHeuristicaEu       = verificaCondicaoHeuristica(heuristicaValorTabuleiro,true);
            aplicacaoHeuristicaOponente = verificaCondicaoHeuristica(heuristicaValorTabuleiro,false);

            if (aplicacaoHeuristicaEu && !aplicacaoHeuristicaOponente){
            
                valorHeuristicoTabuleiro = heuristicaValorTabuleiro.aplicaIncremento(valorHeuristicoTabuleiro);

                if (registrarLog) {
                    registra("\nHeur�stica ATIVADA para Jogador.", styleNegritoAzul);
                    registra("\nIncrementando valor final do Tabuleiro. Valor atualizado: ", styleNormal);
                    registraValorTabuleiro();
                }
                
            }else
                if (!aplicacaoHeuristicaEu && aplicacaoHeuristicaOponente){
                    
                    double novoValor = heuristicaValorTabuleiro.aplicaIncremento(valorHeuristicoTabuleiro);
                    
                    if (novoValor > valorHeuristicoTabuleiro){
                        
                        // iria aumentar o valor - agora vai diminuir //
                        
                        valorHeuristicoTabuleiro -= (novoValor - valorHeuristicoTabuleiro);
                    }else{
                        // iria diminuir o valor - agora vai aumentar //
                        
                        valorHeuristicoTabuleiro += (valorHeuristicoTabuleiro - novoValor);
                    }                    

                    if (registrarLog) {
                        registra("\nHeur�stica ATIVADA para Oponente.", styleNegritoVermelho);
                        registra("\nIncrementando valor final do Tabuleiro. Valor atualizado: ", styleNormal); 
                        registraValorTabuleiro();
                    }
                    
                }else
                    if (aplicacaoHeuristicaEu && aplicacaoHeuristicaOponente){
                        if (registrarLog) {
                            registra("\nHeur�stica IGNORADA por tamb�m ser vantagem para o OPONENTE", styleNegritoLaranja);     
                        }
                    }else{
                        if (registrarLog) {
                            registra("\nHeur�stica N�O ativada", styleNormal);
                        }
                    }            
        }
        
        if (registrarLog){            
            registra("\n\nValor FINAL do TABULEIRO (Jogador - Advers�rio): ", styleNegritoPreto);
            registraValorTabuleiro();
            
            if (arquivoLog != null) {
                arquivoLog.registraLinhaSeparacao();
            }
        }
        
        return valorHeuristicoTabuleiro;
    }
     
    private void registraValorTabuleiro(){
        
        registra(String.valueOf(valorHeuristicoTabuleiro), valorHeuristicoTabuleiro > 0 ? styleNegritoAzul : 
                                 valorHeuristicoTabuleiro < 0 ? styleNegritoVermelho : styleNegritoLaranja);
    }
    
    private double somaPecasJogador(DHJOG.Cor corJogador){
        
        double soma = 0;
        
        for (int indice = 0; indice < TOTAL_CASAS; indice++){
            
            Peca peca = tabuleiro.getPeca(indice);
            
            if (peca != null && peca.getCor() == corJogador){
                soma += valoresPecas[indice];
            }
        }
        
        return soma;
    }
    
    private boolean verificaCondicaoHeuristica(Heuristica heuristica, boolean avaliarParaEu) throws Exception {
        
        String scriptJava;
        
        if (registrarLog && avaliarParaEu) {
            registra("\n\nAvaliando " + heuristica.getTipo() + (avaliarParaEu ? " para Jogador: " : " para Oponente: ") + heuristica.getNome(), styleNormal);
            registra("\nCondi��o:\n"  + heuristica.getCondicaoDHJOG(), styleNormal);
        }

        if (whiteColor) {
            scriptJava = (avaliarParaEu ? heuristica.textoCondicoesToJava() : heuristica.textoCondicoesToJavaSimetrica());
        } else {
            scriptJava = (avaliarParaEu ? heuristica.textoCondicoesToJavaSimetrica() : heuristica.textoCondicoesToJava());
        }

        if (arquivoLog != null) {
            
            if (whiteColor) {
                arquivoLog.registraMensagem("Script gerado " + (avaliarParaEu ? "Normal" : "Sim�trico") + " [" + scriptJava + "]");
            } else {
                arquivoLog.registraMensagem("Script gerado " + (avaliarParaEu ? "Sim�trico" : "Normal") + " [" + scriptJava + "]");
            }
        }

        interpreter.set("condicao", false);
        
        if (avaliarParaEu){
            interpreter.set("EU",      (whiteColor ? DHJOG.Cor.BRANCAS : DHJOG.Cor.PRETAS));
            interpreter.set("OPONENTE",(whiteColor ? DHJOG.Cor.PRETAS  : DHJOG.Cor.BRANCAS));
        }else{
            interpreter.set("EU",      (whiteColor ? DHJOG.Cor.PRETAS  : DHJOG.Cor.BRANCAS));
            interpreter.set("OPONENTE",(whiteColor ? DHJOG.Cor.BRANCAS : DHJOG.Cor.PRETAS));
        }
        
        interpreter.eval(scriptJava);

        return (Boolean) interpreter.get("condicao");
    }
    
    private boolean processaAcoesHeuristicaValorPeca(HeuristicaValorPeca heuristicaValorPeca, boolean aplicarParaEu) throws Exception {
        
        String scriptJava;
        
        int totalPecas = 0;

        if (registrarLog) {
            if (aplicarParaEu){
                registra("\nHeur�stica ATIVADA para Jogador.", styleNegritoAzul);
            }else{
                registra("\nHeur�stica ATIVADA para Oponente.",styleNegritoVermelho);
            }
            registra(" Alterando Valor de Pe�as.", styleNormal);
        }
        
        for (AcaoValorPeca acaoValorPeca : heuristicaValorPeca.getAcoesValorPeca()) {

            if (registrarLog) {
                registra("\n      " + acaoValorPeca.toDHJOG(), styleNormal);                
            }

            if (whiteColor) {
                scriptJava = (aplicarParaEu ? acaoValorPeca.toJava() : acaoValorPeca.toJavaSimetrica());
            } else {
                scriptJava = (aplicarParaEu ? acaoValorPeca.toJavaSimetrica() : acaoValorPeca.toJava());
            }

            if (arquivoLog != null) {
                if (whiteColor) {
                    arquivoLog.registraMensagem("Script gerado " + (aplicarParaEu ? "Normal" : "Sim�trico") + " para alterar pe�as [" + scriptJava + "]");
                } else {
                    arquivoLog.registraMensagem("Script gerado " + (aplicarParaEu ? "Sim�trico" : "Normal") + " para alterar pe�as [" + scriptJava + "]");
                }
            }

            interpreter.set("total", 0);
            
            if (aplicarParaEu){
                interpreter.set("EU",      (whiteColor ? DHJOG.Cor.BRANCAS : DHJOG.Cor.PRETAS));
                interpreter.set("OPONENTE",(whiteColor ? DHJOG.Cor.PRETAS  : DHJOG.Cor.BRANCAS));
            }else{
                interpreter.set("EU",      (whiteColor ? DHJOG.Cor.PRETAS  : DHJOG.Cor.BRANCAS));
                interpreter.set("OPONENTE",(whiteColor ? DHJOG.Cor.BRANCAS : DHJOG.Cor.PRETAS));
            }
            
            interpreter.eval(scriptJava);

            totalPecas += (Integer) interpreter.get("total");
        }

        if (registrarLog) {
            
            if (aplicarParaEu){
                registra("\nTotal de pe�as do Jogador que tiveram o valor alterado pela heur�stica: ", styleNormal);
                registra(String.valueOf(totalPecas), styleNegritoAzul);
            }else{
                registra("\nTotal de pe�as do Oponente que tiveram o valor alterado pela heur�stica: ", styleNormal);
                registra(String.valueOf(totalPecas), styleNegritoVermelho);
            }
            
            calculaValorHeuristicoTabuleiro(false);
            
            return true;
        }else{
            return false;
        }
    }
    
    private void calculaValorHeuristicoTabuleiro(boolean inicial) {
        
        double somaJogador    = somaPecasJogador(whiteColor ? DHJOG.Cor.BRANCAS : DHJOG.Cor.PRETAS);        
        double somaAdversario = somaPecasJogador(whiteColor ? DHJOG.Cor.PRETAS  : DHJOG.Cor.BRANCAS);

        valorHeuristicoTabuleiro = somaJogador - somaAdversario;

        if (registrarLog) {
            registra("\n\nSoma" + (inicial ? "" : " Atualizada") + " do Jogador: ", styleNormal);
            registra(String.valueOf(somaJogador), styleNegritoAzul);
            
            registra(". Soma"   + (inicial ? "" : " Atualizada") + " do Advers�rio: ", styleNormal);
            registra(String.valueOf(somaAdversario), styleNegritoVermelho);
            
            registra("\nValor "   + (inicial ? "Inicial" : "Corrente") + " do TABULEIRO (Jogador - Advers�rio): ", styleNormal);
            registraValorTabuleiro();            
        }
    }
    
    private void registra(String texto, AttributeSet estiloTexto){
        
        if (arquivoLog != null) {
            arquivoLog.registraMensagemSemPular(texto);
        }
        
        if (jTextPane != null) {                        
            UtilsGUI.adicionaTextoComFormato(jTextPane, texto, estiloTexto);              
        }
    }
    
    @Override
    public void cancelaAvaliacao(){
        avaliacaoAtivada = false;
    }
    
    public ArrayList<Casa> getCasas() {
        return Casa.todoTabuleiro();
    }
    
    public int aplicaIncrementoPecas(ArrayList<Casa> localizacaoPecas, char operador, double valorIncremento){
        
        int total = 0;        
        
        for (Casa casa : localizacaoPecas){
            
            Peca peca = tabuleiro.getPeca(casa);
            
            if (peca.getTipo() != TipoPeca.REI){
                
                if (operador == DHJOG.OperadorMatematico.MAIS.toChar()){
                    valoresPecas[casa.getIndice()] += valorIncremento;                    
                }else
                    if (operador == DHJOG.OperadorMatematico.MENOS.toChar()){
                        valoresPecas[casa.getIndice()] -= valorIncremento;                        
                    }else
                        if (operador == DHJOG.OperadorMatematico.MULTIPLICACAO.toChar()){
                            valoresPecas[casa.getIndice()] *= valorIncremento;                            
                        }else
                            if (operador == DHJOG.OperadorMatematico.DIVISAO.toChar()){
                                valoresPecas[casa.getIndice()] /= valorIncremento;                                
                            }else{
                                throw new IllegalArgumentException("Tipo de operador n�o suportado no incremento de pe�as " + operador + "]");                    
                            }
            
                if (registrarLog) {
                    registra("\n      A Pe�a [", styleNormal);
                    
                    if (peca.isWhite() == whiteColor){
                        registra(peca.toString(), styleNegritoAzul);
                    }else{
                        registra(peca.toString(), styleNegritoVermelho);
                    }
                    
                    registra("] da casa [" + casa + "] foi alterada. Novo Valor: ", styleNormal);
                            
                    if (peca.isWhite() == whiteColor){
                        registra(String.valueOf(valoresPecas[casa.getIndice()]), styleNegritoAzul);
                    }else{
                        registra(String.valueOf(valoresPecas[casa.getIndice()]), styleNegritoVermelho);
                    }
                }
            
                total++;
            }else{
                
                if (registrarLog) {
                    registra("\n      O Rei da casa [" + casa + "] foi ", styleNormal);
                    registra("IGNORADO", styleNegritoLaranja);
                    registra(", pois um Rei n�o recebe valor no c�lculo heur�stico.", styleNormal);
                }
            }
        }
        
        return total;
    }
    
    public int QUANTIDADE_PECAS(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
       
    /**
        QUANTIDADE_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA INTEIRO
         
        FUNCAO QUANTIDADE_PECAS
          DESCRICAO "Retorna a quantidade de pe�as que existem na regi�o, s�o dos tipos especificados, e que pertencem ao jogador"
          RETORNA
             INTEIRO DESCRICAO "Quantidade de pe�as dos tipos especificados do jogador que est�o na regi�o"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Pe�as que ser�o contadas"
             CASA[]      REGIAO DESCRICAO "Regi�o onde ser� realizada a contagem de pe�as" 
             JOGADOR     JOG    DESCRICAO "Jogador que dever� ter as pe�as contadas"
          COMANDOS
             INTEIRO INDICE1
             INTEIRO RESPOSTA
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO
                   RESPOSTA <- RESPOSTA + 1
                FIM SE
             FIM PARA
             RETORNA RESPOSTA
        FIM FUNCAO
        */
        
        int resposta = 0;        
        
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
                        
            peca = tabuleiro.getPeca(regiao.get(x));
            
            if (peca != null && Utils.pertence(peca.getTipo(),tiposPecas) && peca.getCor() == jogador) {
                resposta++;
            }
        }
        
        return resposta;
    }
    
    public boolean PRESENCA_PECAS(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
      /**  
        PRESENCA_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO
         
        FUNCAO PRESENCA_PECAS
          DESCRICAO "Retorna verdadeiro caso existam pe�as dos tipos passados pertencentes ao jogador na regi�o especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna Verdadeiro caso exista pelo menos uma pe�a de cada tipo passado dentro da regi�o"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Pe�as que ser�o procurados"
             CASA[]      REGIAO DESCRICAO "Regi�o onde ser� realizada a procura por pe�as" 
             JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as procuradas"
          COMANDOS
             INTEIRO INDICE1
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO
                   TIPOS.REMOVER(PECA1.TIPO)
                FIM SE
             FIM PARA
             SE (TIPOS.TOTAL() IGUAL 0) ENTAO
                RETORNA VERDADEIRO
             SENAO
                RETORNA FALSO
            FIM SE
        FIM FUNCAO
        */
         
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {            
            
            peca = tabuleiro.getPeca(regiao.get(x));
            
            if (peca != null && Utils.pertence(peca.getTipo(),tiposPecas) && peca.getCor() == jogador) {
                tiposPecas.remove(peca.getTipo());
            }
        }
        
        return tiposPecas.isEmpty();
    }
    
    public ArrayList<Casa> ONDE_ESTAO_PECAS(ArrayList<TipoPeca> tiposPecas, DHJOG.Cor jogador) {
        
       /**
       ONDE_ESTAO_PECAS(TIPO_PECA[],JOGADOR) RETORNA CASA[]
        
       FUNCAO ONDE_ESTAO_PECAS
          DESCRICAO "Retorna um conjunto de casas onde as pecas dos tipos definidos e pertencentes ao jogador est�o localizadas"
          RETORNA
             CASA[] DESCRICAO "Conjunto de casas onde as pecas est�o localizadas"
          PARAMETROS
             TIPO_PECA[] TIPOS DESCRICAO "Tipos das Pecas que devem ser localizadas" 
             JOGADOR     JOG   DESCRICAO "Jogador que ter� as pe�as localizadas" --> TRUE (EU)  FALSE(OPONENTE)
          COMANDOS
             INTEIRO INDICE1
             CASA[]  RESPOSTA
             PECA    ATUAL
             PARA INDICE1 DE 1 ATE TABULEIRO.PECAS.TOTAL() FACA
                ATUAL <- TABULEIRO.PECAS.ELEMENTO(INDICE1)
                SE (ATUAL.TIPO PERTENCE TIPOS) E (ATUAL.DONO IGUAL JOG) ENTAO
                   RESPOSTA.ADICIONAR(ATUAL.CASA)
                FIM SE
             FIM PARA
             RETORNA RESPOSTA
        FIM FUNCAO  
        */
        
        ArrayList<Casa> casasAchadas = new ArrayList();

        for (int indice = 0; indice < TOTAL_CASAS; indice++){
        
            Peca peca = tabuleiro.getPeca(indice);
            
            if (peca != null && Utils.pertence(peca.getTipo(),tiposPecas) && peca.getCor() == jogador){
                casasAchadas.add(Casa.porIndice(indice));
            }
        }
        
        return casasAchadas;
    }
    
    public ArrayList<TipoPeca> QUAIS_PECAS_ESTAO(ArrayList<Casa> regiao, DHJOG.Cor jogador) {
              
       /**         
       QUAIS_PECAS_ESTAO(CASA[],JOGADOR) RETORNA TIPO_PECA[]
        
       FUNCAO QUAIS_PECAS_ESTAO
          DESCRICAO "Retorna um conjunto dos tipos de pe�as que est�o dentro da regi�o passada e pertecem ao jogador"
          RETORNA
             TIPO_PECA[] DESCRICAO "Conjunto de tipo de pe�as que est�o localizadas na regi�o passada"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Regi�o onde ser� realizada a procura por pe�as" 
             JOGADOR JOG    DESCRICAO "Jogador que ter� as pe�as identificadas dentro da regi�o"
          COMANDOS
             INTEIRO     INDICE1
             TIPO_PECA[] RESPOSTA
             CASA        ATUAL
             PECA        PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1 DIFERENTE VAZIO) E (PECA1.DONO IGUAL JOG) ENTAO
                   RESPOSTA.ADICIONAR(PECA1.TIPO)
                FIM SE
             FIM PARA
             RETORNA RESPOSTA
        FIM FUNCAO
        */
        
        ArrayList<TipoPeca> tiposPecasAchadas = new ArrayList();
        
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            peca = tabuleiro.getPeca(regiao.get(x));
            
            if (peca != null && peca.getCor() == jogador) {
                tiposPecasAchadas.add(peca.getTipo());
            }
        }
        
        return tiposPecasAchadas;
    }
  
    public ArrayList<Casa> PECAS_QUE_ESTAO(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
      
       // Retorna Casas das pe�as Localizadas //
        
       /**  
       PECAS_QUE_ESTAO(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]
        
       FUNCAO PECAS_QUE_ESTAO
          DESCRICAO "Retorna um conjunto com as pe�as que est�o dentro da regi�o passada, que s�o dos tipos procurados, e pertecem ao jogador"
          RETORNA
             PECA[] DESCRICAO "Conjunto de pe�as que est�o localizadas na regi�o passada"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos das Pecas que devem ser localizadas" 
             CASA[]      REGIAO DESCRICAO "Regi�o onde ser� realizada a procura por pe�as" 
             JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as identificadas dentro da regi�o"
          COMANDOS
             INTEIRO INDICE1
             PECA[]  RESPOSTA
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1 DIFERENTE VAZIO) E (PECA1.DONO IGUAL JOG) E (PECA1.TIPO PERTENCE TIPOS) ENTAO
                   RESPOSTA.ADICIONAR(PECA1)
                FIM SE
             FIM PARA
             RETORNA RESPOSTA
        FIM FUNCAO
        */
        
        ArrayList<Casa> localizacaoPecasAchadas = new ArrayList();
        
        Casa casa;
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);            
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador && Utils.pertence(peca.getTipo(),tiposPecas)) {
                localizacaoPecasAchadas.add(casa);
            }
        }
        
        return localizacaoPecasAchadas;
    }
    
    public boolean MAIOR_QUANTIDADE_PECAS(ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
        MAIOR_QUANTIDADE_PECAS(CASA[],JOGADOR) RETORNA LOGICO
         
        FUNCAO MAIOR_QUANTIDADE_PECAS
          DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior quantidade de pe�as na regi�o especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua mais pe�as que o oponente na regi�o"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Regi�o onde ser� realizada a contagem de pe�as dos jogadores" 
             JOGADOR JOG    DESCRICAO "Jogador que ser� testado se possui a maior quantidade de pe�as"
          COMANDOS
             INTEIRO INDICE1
             INTEIRO QUANTIDADE_JOG1
             INTEIRO QUANTIDADE_JOG2
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1.DONO IGUAL JOG) ENTAO
                   QUANTIDADE_JOG1 <- QUANTIDADE_JOG1 + 1
                 SENAO
                   QUANTIDADE_JOG2 <- QUANTIDADE_JOG2 + 1
                FIM SE
             FIM PARA
             SE (QUANTIDADE_JOG1 > QUANTIDADE_JOG2) ENTAO
                RETORNA VERDADEIRO
              SENAO
                RETORNA FALSO
             FIM SE
        FIM FUNCAO
        */
        
        int quantidadeJog1 = 0;
        int quantidadeJog2 = 0;
        
        Peca peca;
        
        for (int x = 1; x < regiao.size(); x++) {
            
            peca = tabuleiro.getPeca(regiao.get(x));
            
            if (peca != null) {
                if (peca.getCor() == jogador) {
                    quantidadeJog1 += 1;
                } else {
                    quantidadeJog2 += 1;
                }
            }
        }
        
        return (quantidadeJog1 > quantidadeJog2);
    }
    
    public boolean MAIOR_VALOR_PECAS(ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
        MAIOR_VALOR_PECAS(CASA[],JOGADOR) RETORNA LOGICO
         
        FUNCAO MAIOR_VALOR_PECAS
          DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior soma de valores de pe�as na regi�o especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua uma soma de pe�as maior que o oponente na regi�o"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Regi�o onde ser� realizada a soma das pe�as dos jogadores" 
             JOGADOR JOG    DESCRICAO "Jogador que ser� testado se possui a maior soma de valores de pe�as"
          COMANDOS
             INTEIRO INDICE1
             INTEIRO SOMA_JOG1
             INTEIRO SOMA_JOG2
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1.DONO IGUAL JOG) ENTAO
                   SOMA_JOG1 <- SOMA_JOG1 + 1
                 SENAO
                  SOMA_JOG2 <- SOMA_JOG2 + 1
                FIM SE
             FIM PARA
             SE (SOMA_JOG1 > SOMA_JOG2) ENTAO
                RETORNA VERDADEIRO
              SENAO
                RETORNA FALSO
             FIM SE
        FIM FUNCAO
        */
        
        double soma1 = 0;
        double soma2 = 0;
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null) {
                
                if (peca.getCor() == jogador) {
                    soma1 += valoresPecas[casa.getIndice()];
                } else {
                    soma2 += valoresPecas[casa.getIndice()];
                }
            }
        }
        
        return (soma1 > soma2);
    }
    
    public double SOMA_PECAS(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor corJogador) {
        
        /**
        SOMA_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA REAL
         
        FUNCAO SOMA_PECAS
          DESCRICAO "Retorna a soma da pe�as que existem na regi�o, s�o dos tipos especificados, e que pertencem ao jogador"
          RETORNA
             REAL DESCRICAO "Soma das pe�as dos tipos especificados do jogador que est�o na regi�o"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Pe�as que ser�o somadas"
             CASA[]      REGIAO DESCRICAO "Regi�o onde ser� realizada a procurar pelas pe�as" 
             JOGADOR     JOG    DESCRICAO "Jogador que dever� ter as pe�as somadas"
          COMANDOS
             INTEIRO INDICE1
             REAL    RESPOSTA
             CASA    ATUAL
             PECA    PECA1
             PARA INDICE1 DE 1 ATE REGIAO.TOTAL() FACA
                ATUAL <- REGIAO.ELEMENTO(INDICE1)
                PECA1 <- ATUAL.PECA_ATUAL
                SE (PECA1 PERTENCE TIPOS) E (PECA1.DONO IGUAL JOG) ENTAO
                   RESPOSTA <- RESPOSTA + PECA1.TIPO.VALOR
                FIM SE
             FIM PARA
             RETORNA RESPOSTA
        FIM FUNCAO
        */
        
        double soma = 0;
        
        Casa casa;
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && Utils.pertence(peca.getTipo(),tiposPecas) && peca.getCor() == corJogador) {
                soma += valoresPecas[casa.getIndice()];
            }
        }
        
        return soma;
    }
        
    public boolean ESTA_AMEACADA(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {

        /**
         ESTA_AMEACADA(TIPO_PECA[],CASA[],JOGADOR) RETORNO LOGICO

         FUNCAO ESTA_AMEACADA
            DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos passados esteja amea�ada na regi�o"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos passados esteja amea�ada na regi�o"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador && Utils.pertence(peca.getTipo(),tiposPecas)) {
                
                if (estaAmeacadaPorPEAO(casa,jogador)  ||
                    estaAmeacadaPorCAVALO(casa,jogador)||
                    estaAmeacadaPorBISPO(casa,jogador) ||
                    estaAmeacadaPorTORRE(casa,jogador) ||        
                    estaAmeacadaPorDAMA(casa,jogador)  ||     
                    estaAmeacadaPorREI(casa,jogador)){
                    
                    return true;
                }                
            }
        }
        
        return false;
    }
    
    public boolean ESTA_AMEACANDO(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {

        /**
         ESTA_AMEACANDO(TIPO_PECA[],CASA[],JOGADOR) RETORNO LOGICO

         FUNCAO ESTA_AMEACANDO
            DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos passados esteja amea�ando outra na regi�o"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos passados esteja amea�ando outra na regi�o"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        DHJOG.Cor oponente = DHJOG.corJogadorOponente(jogador);
        
        Casa casa;
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == oponente) {
                
                if (Utils.pertence(TipoPeca.PEAO,tiposPecas) && estaAmeacadaPorPEAO(casa,oponente)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.CAVALO,tiposPecas) && estaAmeacadaPorCAVALO(casa,oponente)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.BISPO,tiposPecas) && estaAmeacadaPorBISPO(casa,oponente)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.TORRE,tiposPecas) && estaAmeacadaPorTORRE(casa,oponente)){
                    return true;    
                }
                if (Utils.pertence(TipoPeca.DAMA,tiposPecas) && estaAmeacadaPorDAMA(casa,oponente)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.REI,tiposPecas) && estaAmeacadaPorREI(casa,oponente)){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean ESTA_PROTEGENDO(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
    
        /**
         ESTA_PROTEGENDO(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO

         FUNCAO ESTA_PROTEGENDO
            DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos de pe�as passados esteja protegendo outra na regi�o"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma pe�a do jogador dos tipos de pe�as passados esteja protegendo outra na regi�o"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a prote��o das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador) {
                
                if (Utils.pertence(TipoPeca.PEAO,tiposPecas) && protegida_por_peao(casa,jogador)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.CAVALO,tiposPecas) && protegida_por_cavalo(casa,jogador)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.BISPO,tiposPecas) && protegida_por_bispo(casa,jogador)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.TORRE,tiposPecas) && protegida_por_torre(casa,jogador)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.DAMA,tiposPecas) && protegida_por_dama(casa,jogador)){
                    return true;
                }
                if (Utils.pertence(TipoPeca.REI,tiposPecas) && protegida_por_rei(casa,jogador)){
                    return true;
                }
            }
        }
        
        return false;
    }
        
    public boolean ESTA_PROTEGIDA(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
         ESTA_PROTEGIDA(TIPO_PECA[],CASA[],JOGADOR) RETORNA LOGICO

         FUNCAO ESTA_PROTEGIDA
            DESCRICAO "Retorna verdadeiro caso todas as pe�as do jogador dos tipos especificados estejam protegidas se elas existirem dentro da regi�o passada"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso todas as pe�as do jogador dos tipos especificados estejam protegidas se elas existirem dentro da regi�o passada"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a prote��o das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador && Utils.pertence(peca.getTipo(),tiposPecas)) {
                
                if (!(protegida_por_peao(casa,jogador)  || 
                      protegida_por_cavalo(casa,jogador)||
                      protegida_por_bispo(casa,jogador) ||        
                      protegida_por_torre(casa,jogador) ||        
                      protegida_por_dama(casa,jogador)  ||
                      protegida_por_rei(casa,jogador))){
                    
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public ArrayList<TipoPeca> QUEM_AMEACA(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {

         /**           
          QUEM_AMEACA(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]
           
          FUNCAO QUEM_AMEACA
             DESCRICAO "Retorna os tipos de pe�as do jogador que est�o amea�ando as pe�as dos tipos especificados, dentro da regi�o"
          RETORNA
             TIPO_PECA[] DESCRICAO "Conjunto de tipos de pe�as do jogador que est�o amea�ando as pe�as dos tipos passados, na regi�o"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem est� amea�ando elas"
             CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
             JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
          COMANDOS
             # FALTA IMPLEMENTAR 
          FIM FUNCAO 
          */
        
        DHJOG.Cor oponente = DHJOG.corJogadorOponente(jogador);
        
        ArrayList<TipoPeca> tiposPecasQueAmeacam = new ArrayList();
        
        Casa casa;
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == oponente && Utils.pertence(peca.getTipo(),tiposPecas)) {

                if (estaAmeacadaPorPEAO(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.PEAO);
                }
                if (estaAmeacadaPorCAVALO(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.CAVALO);
                }
                if (estaAmeacadaPorTORRE(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.TORRE);
                }
                if (estaAmeacadaPorBISPO(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.BISPO);
                }                
                if (estaAmeacadaPorDAMA(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.DAMA);
                }
                if (estaAmeacadaPorREI(casa,oponente)) {
                    tiposPecasQueAmeacam.add(TipoPeca.REI);
                }
            }
        }
        
        return tiposPecasQueAmeacam;
    }
    
    public ArrayList<Casa> PECAS_QUE_AMEACAM(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {

         // Retorna Casas das pe�as que Amea�am //
        
         /**           
          PECAS_QUE_AMEACAM(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]
           
          FUNCAO PECAS_QUE_AMEACAM
           DESCRICAO "Retorna a pe�as do jogador que est�o amea�ando as pe�as dos tipos especificados, dentro da regi�o"
           RETORNA
            PECA[] DESCRICAO "Conjunto de pe�as do jogador que est�o amea�ando as pe�as dos tipos passados, na regi�o"
           PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem est� amea�ando elas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
           COMANDOS
            # FALTA IMPLEMENTAR 
          FIM FUNCAO 
          */
        
        DHJOG.Cor oponente = DHJOG.corJogadorOponente(jogador);
        
        ArrayList<Casa> localizacaoPecas, localizacaoPecasQueAmeacam = new ArrayList();
        
        Casa  casa, casaRei;
        Peca peca;

        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == oponente && Utils.pertence(peca.getTipo(),tiposPecas)) {

                localizacaoPecas = peoesQueAmeacam(casa,oponente);
                if (!localizacaoPecas.isEmpty()) {
                    localizacaoPecasQueAmeacam.addAll(localizacaoPecas);
                }
                
                localizacaoPecas = cavalosQueAmeacam(casa,oponente);
                if (!localizacaoPecas.isEmpty()) {
                    localizacaoPecasQueAmeacam.addAll(localizacaoPecas);
                }
                
                localizacaoPecas = torresQueAmeacam(casa,oponente);
                if (!localizacaoPecas.isEmpty()) {
                    localizacaoPecasQueAmeacam.addAll(localizacaoPecas);
                }
                
                localizacaoPecas = bisposQueAmeacam(casa,oponente);
                if (!localizacaoPecas.isEmpty()) {
                    localizacaoPecasQueAmeacam.addAll(localizacaoPecas);
                }
                
                localizacaoPecas = damasQueAmeacam(casa,oponente);
                if (!localizacaoPecas.isEmpty()) {
                    localizacaoPecasQueAmeacam.addAll(localizacaoPecas);
                }
                
                casaRei = reiQueAmeaca(casa,oponente);
                if (casaRei != null) {
                    localizacaoPecasQueAmeacam.add(casaRei);
                }
            }
        }
        
        return localizacaoPecasQueAmeacam;
    }

    public ArrayList<TipoPeca> QUEM_ESTA_AMEACADO(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
         QUEM_ESTA_AMEACADO(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]

         FUNCAO QUEM_ESTA_AMEACADO
            DESCRICAO "Retorna os tipos de pe�as do jogador que est�o sendo amea�adas pelas pe�as dos tipos passados, dentro da regi�o"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de pe�as do jogador que est�o sendo amea�adas pelas pe�as dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem elas est�o amea�ando"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
                
        ArrayList<TipoPeca> tiposPecasAmeacadas = new ArrayList();
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador) {
                
                if ((Utils.pertence(TipoPeca.PEAO,tiposPecas)  && estaAmeacadaPorPEAO(casa,jogador))  ||
                    (Utils.pertence(TipoPeca.CAVALO,tiposPecas)&& estaAmeacadaPorCAVALO(casa,jogador))||
                    (Utils.pertence(TipoPeca.BISPO,tiposPecas) && estaAmeacadaPorBISPO(casa,jogador)) ||
                    (Utils.pertence(TipoPeca.TORRE,tiposPecas) && estaAmeacadaPorTORRE(casa,jogador)) ||
                    (Utils.pertence(TipoPeca.DAMA,tiposPecas)  && estaAmeacadaPorDAMA(casa,jogador))  ||
                    (Utils.pertence(TipoPeca.REI,tiposPecas)   && estaAmeacadaPorREI(casa,jogador))){
                
                    tiposPecasAmeacadas.add(peca.getTipo());
                }    
            }
        }
        
        return tiposPecasAmeacadas;
    }
    
    public ArrayList<Casa> PECAS_AMEACADAS_POR(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        // Retorna a localiza��o das Pe�as Amea�adas Por //
        
        /**
         PECAS_AMEACADAS_POR(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]

         FUNCAO PECAS_AMEACADAS_POR
          DESCRICAO "Retorna as pe�as do jogador que est�o sendo amea�adas pelas pe�as dos tipos passados, dentro da regi�o"
          RETORNA
           PECA[] DESCRICAO "Conjunto de pe�as do jogador que est�o sendo amea�adas pelas pe�as dos tipos passados"
          PARAMETROS
           TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem elas est�o amea�ando"
           CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a amea�a das pe�as"
           JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
          COMANDOS
           # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
                
        ArrayList<Casa> localizacaoPecasAmeacadas = new ArrayList();
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
           
            if (peca != null && peca.getCor() == jogador) {

                if ((Utils.pertence(TipoPeca.PEAO,tiposPecas)  && estaAmeacadaPorPEAO(casa,  jogador)) ||
                    (Utils.pertence(TipoPeca.CAVALO,tiposPecas)&& estaAmeacadaPorCAVALO(casa,jogador)) ||
                    (Utils.pertence(TipoPeca.BISPO,tiposPecas) && estaAmeacadaPorBISPO(casa, jogador)) ||
                    (Utils.pertence(TipoPeca.TORRE,tiposPecas) && estaAmeacadaPorTORRE(casa, jogador)) ||
                    (Utils.pertence(TipoPeca.DAMA,tiposPecas)  && estaAmeacadaPorDAMA(casa,  jogador)) ||
                    (Utils.pertence(TipoPeca.REI,tiposPecas)   && estaAmeacadaPorREI(casa,   jogador))){
                
                    localizacaoPecasAmeacadas.add(casa);
                }  
            }
        }
        
        return localizacaoPecasAmeacadas;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////        
    public ArrayList<TipoPeca> QUEM_PROTEGE(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
         QUEM_PROTEGE(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]

         FUNCAO QUEM_PROTEGE
            DESCRICAO "Retorna os tipos de pe�as do jogador que est�o protegendo as pe�as dos tipos passados, dentro da regi�o"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de pe�as do jogaodr que est�o protegendo as pe�as dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem protege elas"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a prote��o das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        ArrayList<TipoPeca> tiposPecasQueProtegem = new ArrayList();

        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador && Utils.pertence(peca.getTipo(),tiposPecas)) {
                
                if (protegida_por_peao(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.PEAO);
                }
                if (protegida_por_cavalo(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.CAVALO);
                }
                if (protegida_por_bispo(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.BISPO);
                }
                if (protegida_por_torre(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.TORRE);
                }
                if (protegida_por_dama(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.DAMA);
                }
                if (protegida_por_rei(casa,jogador)) {
                    tiposPecasQueProtegem.add(TipoPeca.REI);
                }
            }
        }
        
        return tiposPecasQueProtegem;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////        
    public ArrayList<TipoPeca> QUEM_ESTA_PROTEGIDO(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
        
        /**
         QUEM_ESTA_PROTEGIDO(TIPO_PECA[],CASA[],JOGADOR) RETORNA TIPO_PECA[]

         FUNCAO QUEM_ESTA_PROTEGIDO
            DESCRICAO "Retorna os tipos de pe�as do jogador que est�o sendo protegidos pelas pe�as dos tipos passados, na regi�o"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de pe�as do jogador que est�o sendo protegidas pelas pe�as dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de pe�as que dever�o ser verificadas quem elas est�o protegendo"
            CASA[]      REGIAO DESCRICAO "Regi�o onde ser� verificada a prote��o das pe�as"
            JOGADOR     JOG    DESCRICAO "Jogador que ter� as pe�as verificadas"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        ArrayList<TipoPeca> tiposPecasProtegidos = new ArrayList();
        
        Casa casa;
        Peca peca;
        
        for (int x = 0; x < regiao.size(); x++) {
            
            casa = regiao.get(x);
            peca = tabuleiro.getPeca(casa);
            
            if (peca != null && peca.getCor() == jogador) {
                
                if ((Utils.pertence(TipoPeca.PEAO,tiposPecas)  && protegida_por_peao(casa,jogador))   ||
                    (Utils.pertence(TipoPeca.CAVALO,tiposPecas)&& protegida_por_cavalo(casa,jogador)) ||    
                    (Utils.pertence(TipoPeca.BISPO,tiposPecas) && protegida_por_bispo(casa,jogador))  ||    
                    (Utils.pertence(TipoPeca.TORRE,tiposPecas) && protegida_por_torre(casa,jogador))  ||
                    (Utils.pertence(TipoPeca.DAMA,tiposPecas)  && protegida_por_dama(casa,jogador))   ||
                    (Utils.pertence(TipoPeca.REI,tiposPecas)   && protegida_por_rei(casa,jogador))){
                    
                    tiposPecasProtegidos.add(peca.getTipo());
                }
            }
        }
        
        return tiposPecasProtegidos;
    }
    
    public boolean POSSIVEL_ROQUE_GRANDE(DHJOG.Cor jogador){
        
       /**
         POSSIVEL_ROQUE_GRANDE(JOGADOR) RETORNA LOGICO

         FUNCAO POSSIVEL_ROQUE_GRANDE
            DESCRICAO "Verifica se � poss�vel para o Jogador passado executar a jogada Roque Grande"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro se ainda for poss�vel que o jogador passado executar a jogada Roque Grande"
         PARAMETROS
            JOGADOR JOG DESCRICAO "Jogador que dever� ser verificado a possibilidade de Roque Grande"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO 
        */
        
        return tabuleiro.getPodeRoqueMaior(jogador == DHJOG.Cor.BRANCAS ? true : false);
    }
    
    public boolean POSSIVEL_ROQUE_PEQUENO(DHJOG.Cor jogador){
    
       /**
         POSSIVEL_ROQUE_PEQUENO(JOGADOR) RETORNA LOGICO

         FUNCAO POSSIVEL_ROQUE_PEQUENO
            DESCRICAO "Verifica se � poss�vel para o Jogador passado executar a jogada Roque Pequeno"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro se ainda for poss�vel que o jogador passado executar a jogada Roque Pequeno"
         PARAMETROS
            JOGADOR JOG DESCRICAO "Jogador que dever� ser verificado a possibilidade de Roque Pequeno"
         COMANDOS
            # FALTA IMPLEMENTAR 
         FIM FUNCAO
        */    
        
        return tabuleiro.getPodeRoqueMenor(jogador == DHJOG.Cor.BRANCAS ? true : false);
    }
    
    public int QUANTIDADE_JOGADAS(){
        
        /**
         QUANTIDADE_JOGADAS() RETORNA INTEIRO
         
         FUNCAO QUANTIDADE_JOGADAS
          DESCRICAO "Retorna a quantidade de jogadas realizadas ate o momento na partida"
          RETORNA
           INTEIRO DESCRICAO "Quantidade de jogadas"
          COMANDOS
           RETORNA TABULEIRO.QUANTIDADE_JOGADAS
         FIM FUNCAO
         */
        
        return tabuleiro.getQuantidadeJogadas();
    }
    
    public int LANCES_SEM_CAPTURA_E_PEAO(){
               
        /**
         LANCES_SEM_CAPTURA_E_PEAO() RETORNA INTEIRO
          
         FUNCAO LANCES_SEM_CAPTURA_E_PEAO
          DESCRICAO "Retorna a quantidade de lances realizados sem movimento de Pe�es e sem capturas de pe�as"
          RETORNA
           INTEIRO DESCRICAO "Quantidade de lances sem captura e movimento de pe�es"
          COMANDOS
           RETORNA TABULEIRO.LANCES_SEM_CAPTURA_E_MOVIMENTO_PEAO
         FIM FUNCAO 
         */
        
        return tabuleiro.getQuantidadeMovimentos();
    }
    
    public boolean POSSUI_PECA_EN_PASSANT(DHJOG.Cor jogador){
    
        /**
         POSSUI_PECA_EN_PASSANT(JOGADOR) RETORNA LOGICO

         FUNCAO POSSUI_PECA_EN_PASSANT
          DESCRICAO "Retorna verdadeiro caso o jogador possua um Pe�o pass�vel de ser capturado En Passant"
          RETORNA
           LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua um Pe�o pass�vel de ser capturado En Passant"
          PARAMETROS
           JOGADOR JOG DESCRICAO "Jogador que dever� ser verificado a possibilidade de captura En Passant"
          COMANDOS
           # FALTA IMPLEMENTAR 
         FIM FUNCAO 
         */
        
        Casa casa = tabuleiro.getCasaEnPassant();
        
        if (casa == null){
            return false;
        }
                
        if (jogador == DHJOG.Cor.BRANCAS){
            if (casa.getIndiceLinha() == 2){
                return true;
            }else{
                return false;    
            }    
        }else{
            if (casa.getIndiceLinha() == 5){
                return true;
            }else{
                return false;    
            }
        }
    }
    
    public ArrayList<Casa> peoesQueAmeacam(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a Localiza��o dos Pe�es que amea�am //
        
        ArrayList<Casa> localizacaoPeosQueAmeacam = new ArrayList();                
        
        int l, c;
        
        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        if (jogador == DHJOG.Cor.BRANCAS) { //se analisando pelas brancas avalia pe�es vindos do topo do tabuleiro
            
            if (l >= 6) {
                return localizacaoPeosQueAmeacam;
            }
            
            if (c == 0) {                
                peca = tabuleiro.getPeca(c + 1, l + 1);
                if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                    localizacaoPeosQueAmeacam.add(Casa.porIndices(c + 1,l + 1));
                }
                
            } else 
                if (c == 7) {
                    peca = tabuleiro.getPeca(c - 1, l + 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c - 1, l + 1));
                    }
                    
                } else {
                    peca = tabuleiro.getPeca(c - 1, l + 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c - 1, l + 1));
                    }
                    
                    peca = tabuleiro.getPeca(c + 1, l + 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c + 1, l + 1));
                    }
                }
            
        } else {  // se analisando pelas pretas avalia pe�es vindos da base do tabuleiro
            
            if (l <= 1) {
                return localizacaoPeosQueAmeacam;
            } 
            
            if (c == 0) {
                peca = tabuleiro.getPeca(c + 1, l - 1);
                if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                    localizacaoPeosQueAmeacam.add(Casa.porIndices(c + 1, l - 1));
                }
                
            } else 
                if (c == 7) {
                    peca = tabuleiro.getPeca(c - 1, l - 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c - 1, l - 1));
                    }
                    
                } else {
                    peca = tabuleiro.getPeca(c - 1, l - 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c - 1, l - 1));
                    }
                    
                    peca = tabuleiro.getPeca(c + 1, l - 1);
                    if (peca != null && peca.getTipo() == TipoPeca.PEAO && peca.getCor() != jogador){
                        localizacaoPeosQueAmeacam.add(Casa.porIndices(c + 1, l - 1));
                    }
            }
        }
        
        return localizacaoPeosQueAmeacam;
    }
    
    public ArrayList<Casa> cavalosQueAmeacam(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a Localiza��o dos Cavalos que Amea�am //
        
        ArrayList<Casa> localizacaoCavalosQueAmeacam = new ArrayList();               
        
        int l, c, lin, col;
        
        Peca peca;

        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. prosi��o

        lin = l + 2;
        col = c + 1;
        
        if ((lin <= 7) && (col <= 7)) {            
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 2a. posi��o
        
        lin = l + 1;
        col = c + 2;
        
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 3a. posi��o
        
        lin = l - 1;
        col = c + 2;
        
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 4a. posi��o
        
        lin = l - 2;
        col = c + 1;
        
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }

        // Avalia 5a. posi��o
        
        lin = l - 2;
        col = c - 1;
        
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }

        // Avalia 6a. posi��o
        
        lin = l - 1;
        col = c - 2;
        
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 7a. posi��o
        
        lin = l + 1;
        col = c - 2;
        
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 8a. posi��o
        
        lin = l + 2;
        col = c - 1;
        
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }

        return localizacaoCavalosQueAmeacam;
    }

    public ArrayList<Casa> torresQueAmeacam(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a localiza��o das Torres que Amea�am //
        
        ArrayList<Casa> localizacaoTorresQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. dire�ao LESTE
        
        lin = l;
        col = c + 1;
        
        while (col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.TORRE && peca.getCor() != jogador) {
                    localizacaoTorresQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            col++;
        }

        // Avalia 2a. dire�ao SUL
        
        lin = l - 1;
        col = c;
        
        while (lin >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.TORRE && peca.getCor() != jogador) {
                    localizacaoTorresQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
        }

        // Avalia 3a. dire�ao OESTE
        
        lin = l;
        col = c - 1;
        
        while (col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.TORRE && peca.getCor() != jogador) {
                    localizacaoTorresQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            col--;
        }

        // Avalia 4a. dire�ao NORTE
        
        lin = l + 1;
        col = c;
        
        while (lin <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.TORRE && peca.getCor() != jogador) {
                    localizacaoTorresQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
        }
        
        return localizacaoTorresQueAmeacam;
    }
    
    public ArrayList<Casa> bisposQueAmeacam(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a localiza��o dos Bispos que Amea�am //
        
        ArrayList<Casa> localizacaoBisposQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        // Avalia 1a. dire�ao NE
        
        lin = l + 1;
        col = c + 1;
        
        while (lin <= 7 && col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.BISPO && peca.getCor() != jogador) {
                    localizacaoBisposQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
            col++;
        }

        // Avalia 2a. dire�ao SE
        
        lin = l - 1;
        col = c + 1;
        
        while (lin >= 0 && col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.BISPO && peca.getCor() != jogador) {
                    localizacaoBisposQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
            col++;
        }

        // Avalia 3a. dire�ao SO
        
        lin = l - 1;
        col = c - 1;
        
        while (lin >= 0 && col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.BISPO && peca.getCor() != jogador) {
                    localizacaoBisposQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
            col--;
        }

        // Avalia 4a. dire�ao N0
        
        lin = l + 1;
        col = c - 1;
        
        while (lin <= 7 && col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.BISPO && peca.getCor() != jogador) {
                    localizacaoBisposQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
            col--;
        }
        
        return localizacaoBisposQueAmeacam;
    }
    
    public ArrayList<Casa> damasQueAmeacam(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a localiza��o das Damas que Amea�am //
        
        ArrayList<Casa> localizacaoDamasQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. dire�ao NORDESTE
        
        lin = l + 1;
        col = c + 1;
        
        while (lin <= 7 && col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
            col++;
        }

        // Avalia 2a. dire�ao SUDESTE
        
        lin = l - 1;
        col = c + 1;
        
        while (lin >= 0 && col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
            col++;
        }

        // Avalia 3a. dire�ao SUDOESTE
        
        lin = l - 1;
        col = c - 1;
        
        while (lin >= 0 && col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
            col--;
        }

        // Avalia 4a. dire�ao NOROESTE
        
        lin = l + 1;
        col = c - 1;
        
        while (lin <= 7 && col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
            col--;
        }

        // Avalia 5a. dire�ao LESTE
        
        lin = l;
        col = c + 1;
        
        while (col <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            col++;
        }

        // Avalia 6a. dire�ao SUL
        
        lin = l - 1;
        col = c;
        
        while (lin >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin--;
        }

        // Avalia 7a. dire�ao OESTE
        
        lin = l;
        col = c - 1;
        
        while (col >= 0) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            col--;
        }

        // Avalia 8a. dire�ao NORTE
        
        lin = l + 1;
        col = c;
        
        while (lin <= 7) {
            
            peca = tabuleiro.getPeca(col,lin);
            
            if (peca != null) {
                
                if (peca.getTipo() == TipoPeca.DAMA && peca.getCor() != jogador) {
                    localizacaoDamasQueAmeacam.add(Casa.porIndices(col,lin));
                } else {
                    break;
                }
            }
            
            lin++;
        }

        return localizacaoDamasQueAmeacam;
    }
    
    public Casa reiQueAmeaca(Casa casa, DHJOG.Cor jogador) {
        
        // Retorna a Casa do Rei que Amea�a //
        
        int l, c;
        
        Peca peca;
                
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        if (l + 1 <= 7){
            
            peca = tabuleiro.getPeca(c, l + 1);     // NORTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c, l + 1);
            }
        }
        
        if ((c + 1 <= 7) && (l + 1 <= 7)){
            
            peca = tabuleiro.getPeca(c + 1, l + 1); // NORDESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c + 1, l + 1);
            }
        }
        
        if (c + 1 <= 7){
            
            peca = tabuleiro.getPeca(c + 1, l);     // LESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c + 1, l);
            }
        }
        
        if ((c + 1 <= 7) && (l - 1 >= 0)){
            
            peca = tabuleiro.getPeca(c + 1, l - 1); // SUDESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c + 1, l - 1);
            }
        }
        
        if (l - 1 >= 0){
            
            peca = tabuleiro.getPeca(c, l - 1);     // SUL
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c, l - 1);
            }
        }
        
        if ((c - 1 >= 0) && (l - 1 >= 0)){
            
            peca = tabuleiro.getPeca(c - 1, l - 1); // SUDOESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c - 1, l - 1);
            }
        }
        
        if (c - 1 >= 0){
            
            peca = tabuleiro.getPeca(c - 1, l);     // OESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c - 1, l);
            }
        }
        
        if ((c - 1 >= 0) && (l + 1 <= 7)){
            
            peca = tabuleiro.getPeca(c - 1, l + 1); // NOROESTE
            
            if (peca != null && peca.getTipo() == TipoPeca.REI && peca.getCor() != jogador) {
                return Casa.porIndices(c - 1, l + 1);
            }
        }
        
        return null;
    }
    
    public boolean estaAmeacadaPorPEAO(Casa casa, DHJOG.Cor jogador) {
        
        if (peoesQueAmeacam(casa,jogador).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean estaAmeacadaPorCAVALO(Casa casa, DHJOG.Cor jogador) {
        
        if (cavalosQueAmeacam(casa,jogador).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean estaAmeacadaPorTORRE(Casa casa, DHJOG.Cor jogador) {

        if (torresQueAmeacam(casa,jogador).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean estaAmeacadaPorBISPO(Casa casa, DHJOG.Cor jogador) {
        
        if (bisposQueAmeacam(casa,jogador).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean estaAmeacadaPorDAMA(Casa casa, DHJOG.Cor jogador) {
      
        if (damasQueAmeacam(casa,jogador).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean estaAmeacadaPorREI(Casa casa, DHJOG.Cor jogador) {

        if (reiQueAmeaca(casa,jogador) == null){
            return false;
        }else{
            return true;
        }
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////        
    
    public boolean protegida_por_peao(Casa casa, DHJOG.Cor jogador) {
        
        int l, c;
        Peca peca; //pe�a que ter� foco da investiga��o
        
        if (jogador == DHJOG.Cor.BRANCAS) { //se analisando pelas brancas avalia pe�es vindos do base do tabuleiro
            
            l = casa.getIndiceLinha();
            c = casa.getIndiceColuna();

            if (l <= 1) {
                return false;
            }
            
            // verifica se a posi��o em si n�o tem como ser protegida por pe�es;
            
            if (c == 0) { //tratamento especial para coluna 0 e coluna 7;
                peca = tabuleiro.getPeca(c + 1, l - 1);
                return ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
            } else 
                if (c == 7) {
                    peca = tabuleiro.getPeca(c - 1, l - 1);
                    return ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
                } else {
                    peca = tabuleiro.getPeca(c + 1, l - 1);
                    boolean a = ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
                    peca = tabuleiro.getPeca(c - 1, l - 1);
                    return (a || ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador)));
                }
            
        } else {  
            
            // se analisando pelas pretas avalia pe�es vindos do topo do tabuleiro
            
            l = casa.getIndiceLinha();
            c = casa.getIndiceColuna();

            if (l >= 6) {
                return false;
            } 
            
            //verifica se a posi��o em si n�o tem como ser protegida por pe�es;
            
            if (c == 0) { //tratamento especial para coluna 0 e coluna 7;
                peca = tabuleiro.getPeca(c + 1, l + 1);
                return ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
            } else 
                if (c == 7) {
                    peca = tabuleiro.getPeca(c - 1, l + 1);
                    return ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
                } else {
                    peca = tabuleiro.getPeca(c - 1, l + 1);
                    boolean a = ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador));
                    peca = tabuleiro.getPeca(c + 1, l + 1);
                    return (a || ((peca != null) && (peca.getTipo() == TipoPeca.PEAO) && (peca.getCor() == jogador)));
            }
        }
    }

    public boolean protegida_por_cavalo(Casa casa, DHJOG.Cor jogador) {
        
        int l, c, lin, col;
        Peca peca;

        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // avalia primeira posi��o sentido hor�rio 12:00hs

        lin = l + 2;
        col = c + 1;
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 2a. posi��o
        
        lin = l + 1;
        col = c + 2;
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 3a. posi��o
        
        lin = l - 1;
        col = c + 2;
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 4a. posi��o
        
        lin = l - 2;
        col = c + 1;
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }

        // avalia 5a. posi��o
        
        lin = l - 2;
        col = c - 1;
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }

        // avalia 6a. posi��o
        
        lin = l - 1;
        col = c - 2;
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 7a. posi��o
        
        lin = l + 1;
        col = c - 2;
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 8a. posi��o
        
        lin = l + 2;
        col = c - 1;
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }

        return false;
    }

    public boolean protegida_por_bispo(Casa casa, DHJOG.Cor jogador) {
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // avalia 1a. dire�ao NE
        
        lin = l + 1;
        col = c + 1;
        while ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.BISPO) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
            col++;
        }

        // avalia 2a. dire�ao SE
        
        lin = l - 1;
        col = c + 1;
        while ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.BISPO) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
            col++;
        }

        // avalia 3a. dire�ao SO
        
        lin = l - 1;
        col = c - 1;
        while ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.BISPO) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
            col--;
        }

        // avalia 1a. dire�ao NE
        
        lin = l + 1;
        col = c - 1;
        while ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.BISPO) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
            col--;
        }
        
        return false;
    }

    public boolean protegida_por_torre(Casa casa, DHJOG.Cor jogador) {
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // avalia 1a. dire�ao LESTE
        
        lin = l;
        col = c + 1;
        while (col <= 7) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.TORRE) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            col++;
        }

        // avalia 2a. dire�ao SUL
        
        lin = l - 1;
        col = c;
        while (lin >= 0) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.TORRE) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
        }

        // avalia 3a. dire�ao OESTE
        
        lin = l;
        col = c - 1;
        while (col >= 0) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.TORRE) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            col--;
        }

        // avalia 4a. dire�ao NORTE
        
        lin = l + 1;
        col = c;
        while ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.TORRE) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
        }
        
        return false;
    }

    public boolean protegida_por_rei(Casa casa, DHJOG.Cor jogador) {
        
        int l, c;
        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        // avalia as 8 posi��es onde pode estar o rei se a casa atual n�o est� nas bordas dos tabuleiros
        
        if ((l > 0) && (l < 7) && (c > 0) && (c < 7)) {
            
            peca = tabuleiro.getPeca(c, l + 1);     // NORTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c + 1, l + 1); // NORDESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c + 1, l);     // LESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c + 1, l - 1); // SUDESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c, l - 1);     // SUL
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c - 1, l - 1); // SUDOESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c - 1, l);     // OESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
            
            peca = tabuleiro.getPeca(c - 1, l + 1); // NOROESTE
            if ((peca != null) && (peca.getTipo() == TipoPeca.REI) && (peca.getCor() == jogador)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean protegida_por_dama(Casa casa, DHJOG.Cor jogador) {
        
        int l, c, lin, col;

        Peca peca;
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // avalia 1a. dire�ao NE
        
        lin = l + 1;
        col = c + 1;
        while ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
            col++;
        }

        // avalia 2a. dire�ao SE
        
        lin = l - 1;
        col = c + 1;
        while ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
            col++;
        }

        // avalia 3a. dire�ao SO
        
        lin = l - 1;
        col = c - 1;
        while ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
            col--;
        }

        // avalia 1a. dire�ao NE
        
        lin = l + 1;
        col = c - 1;
        while ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
            col--;
        }

        // avalia 1a. dire�ao LESTE
        
        lin = l;
        col = c + 1;
        while (col <= 7) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            col++;
        }

        // avalia 2a. dire�ao SUL
        
        lin = l - 1;
        col = c;
        while (lin >= 0) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin--;
        }

        // avalia 3a. dire�ao OESTE
        
        lin = l;
        col = c - 1;
        while (col >= 0) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            col--;
        }

        // avalia 4a. dire�ao NORTE
        
        lin = l + 1;
        col = c;
        while ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null) {
                if ((peca.getTipo() == TipoPeca.DAMA) && (peca.getCor() == jogador)) {
                    return true;
                } else {
                    break;
                }
            }
            lin++;
        }

        return false;
    }    
    
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////        
    
    public static ArrayList<TipoPeca> montaArrayTipos(String textoTipoPecas) {
        
        ArrayList<TipoPeca> tiposPecas = new ArrayList();
        
        StringTokenizer tp = new StringTokenizer(textoTipoPecas,",");
        
        while (tp.hasMoreTokens()) {
            tiposPecas.add(DHJOG.converteTextoTipoPeca(tp.nextToken()));
        }
        
        return tiposPecas;
    }
    
    public static ArrayList<DHJOG.Peca> montaArrayPecas(String textoPecas) {
        
        ArrayList<DHJOG.Peca> pecas = new ArrayList();
        
        StringTokenizer tp = new StringTokenizer(textoPecas,",");
        
        while (tp.hasMoreTokens()) {
            pecas.add(DHJOG.converteTextoPeca(tp.nextToken()));
        }
        
        return pecas;
    }
    
    public boolean conjuntoIgual(boolean pecas, DHJOG.Cor jogador, ArrayList conjunto1, ArrayList conjunto2) {
        
        if (conjunto1.size() != conjunto2.size()){
            return false;
        }
        
        if (!conjuntoContem(pecas, jogador, conjunto1, conjunto2)){
            return false;
        }

        return true;
    }
    
    public boolean conjuntoDiferente(boolean pecas, DHJOG.Cor jogador, ArrayList conjunto1, ArrayList conjunto2) {
        return (!conjuntoIgual(pecas, jogador, conjunto1, conjunto2));
    }

    public boolean conjuntoContem(boolean pecas, DHJOG.Cor jogador, ArrayList conjunto1, ArrayList conjunto2) {
        
        boolean res;
        
        if (conjunto2.isEmpty()){
            return false;
        }
        
        // verifica se todas os elmentos do conjunto 2 est�o no conjunto 1
        
        for (Object obj : conjunto2) {
            
            if (obj instanceof Casa){
                
                if (pecas){
                    res = pertencePeca(jogador, (Casa) obj, conjunto1);
                }else{
                    res = pertenceCasa((Casa) obj, conjunto1);
                }                
            }else
                if (obj instanceof TipoPeca){
                    res = Utils.pertence((TipoPeca) obj, conjunto1);
                }else
                    if (obj instanceof DHJOG.Peca){
                        res = pertenceDHJOGPeca(jogador, (DHJOG.Peca) obj, conjunto1);
                    }else{
                        throw new IllegalArgumentException("Tipo [" + obj.getClass().getName() + "] n�o suportado na an�lise de Conjuntos!");
                    }
            
            if (!res) {
                return false;
            }
        }
        
        return true;
    }

    public boolean conjuntoContido(boolean pecas, DHJOG.Cor jogador, ArrayList conjunto1, ArrayList conjunto2) {
        return conjuntoContem(pecas, jogador, conjunto2, conjunto1);
    }

    public boolean pertenceCasa(Casa casa, ArrayList<Casa> regiao) {
        
        for (int x = 0; x < regiao.size(); x++) {
            
            if (regiao.get(x).equals(casa)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean pertenceDHJOGPeca(DHJOG.Cor jogador, DHJOG.Peca pecaDHJOG, ArrayList localizacaoPecas) {
        
        for (int x = 0; x < localizacaoPecas.size(); x++) {
           
            if (localizacaoPecas.get(x) instanceof DHJOG.Peca){
            
                if (localizacaoPecas.get(x) == pecaDHJOG){
                    return true;
                }
            }else
                if (localizacaoPecas.get(x) instanceof Casa){                
                    
                    Peca peca = tabuleiro.getPeca((Casa) localizacaoPecas.get(x));
                    
                    if (pecaDHJOG.igual(jogador, peca)){
                        return true;
                    }
                        
                }else{
                    throw new IllegalArgumentException("Tipo de objeto n�o suportado para compara��o de pe�as [" + localizacaoPecas.get(x).getClass().getName() + "]");
                }
        }
        
        return false;
    }
    
    public boolean pertencePeca(DHJOG.Cor jogador, Casa localizacaoPeca, ArrayList localizacaoPecas) {
        
        Peca peca1 = tabuleiro.getPeca(localizacaoPeca);
        
        if (peca1 == null){
            return false;
        }
        
        for (int x = 0; x < localizacaoPecas.size(); x++) {
            
            if (localizacaoPecas.get(x) instanceof Casa){
                
                Peca peca2 = tabuleiro.getPeca((Casa) localizacaoPecas.get(x));
                        
                if (peca1.igual(peca2)) {
                    return true;
                }
                
            }else
                if (localizacaoPecas.get(x) instanceof DHJOG.Peca){
                    
                    DHJOG.Peca pecaDHJOG = (DHJOG.Peca) localizacaoPecas.get(x);
                    
                    if (pecaDHJOG.igual(jogador, peca1)){
                        return true;
                    }
                    
                }else{
                    throw new IllegalArgumentException("Tipo de objeto n�o suportado para compara��o de pe�as [" + localizacaoPecas.get(x).getClass().getName() + "]");
                }
        }
        
        return false;
    }
}