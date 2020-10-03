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
    private static SimpleAttributeSet styleNegritoPreto;    // Usado para destacar ações gerais
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
     * Construtor criado para a Execução de Campeonatos, recicla o Interpretador
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
        // Verifica Situação de Xeque-mate e Empates /////////////////////////////////////////////////
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
                             " ficou Afogado (Ficou sem movimentos válidos)!", styleNormal);
                }
            }
            
        } else
            if (tabuleiro.podeEmpatar50Movimentos()) {

                valorHeuristicoTabuleiro = DHJOG.EMPATE;
                
                if (registrarLog) {
                    registra("\nPartida ", styleNormal);
                    registra("Empatada", styleNegritoLaranja);
                    registra(" pela Regra de 50 Movimentos sem captura ou movimentação de Peões!", styleNormal);
                }

            } else 
                if (tabuleiro.podeEmpatarTriplaRepeticao()) {

                    valorHeuristicoTabuleiro = DHJOG.EMPATE;
                    
                    if (registrarLog) {
                        registra("\nPartida ", styleNormal);
                        registra("Empatada", styleNegritoLaranja);
                        registra(" pela Regra da Tripla Repetição de um Tabuleiro!", styleNormal);
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
        // Processamento do Conjunto Heurístico //////////////////////////////////////////////////////
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
        // 1 - Avaliando heurísticas de transição de etapa ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 1 - Análise das Heurísticas de Transição de Etapas", styleNegritoPreto);
        }
        
        for (int x = 0; x < etapaAtual.getHeuristicasTransicaoEtapa().size(); x++) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvaliação cancelada", styleNegritoVermelho);
                }
                return 0;
            }

            HeuristicaTransicaoEtapa heuristicaTransicaoEtapa = etapaAtual.getHeuristicasTransicaoEtapa().get(x);

            aplicacaoHeuristicaEu = verificaCondicaoHeuristica(heuristicaTransicaoEtapa,true);
                        
            if (aplicacaoHeuristicaEu) {

                etapaAtual = heuristicaTransicaoEtapa.getProximaEtapa();

                if (registrarLog) {
                    registra("\nHeurística ATIVADA. Nova etapa selecionada [" + etapaAtual + "]", styleNegritoAzul);
                }
                
                interpreter.set("etapaAtual", etapaAtual);

                x = 0; // Volta a processar as heurísticas de Transição da nova Etapa
                
            }else{
                if (registrarLog) {
                    registra("\nHeurística NÃO ativada", styleNormal);
                }
            }
        }
        
        //////////////////////////////////////////////////////////////////////////////////////////////
        // 2 - Inicializa valores de peças para a Etapa Atual ////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 2 - Inicializa-se os valores das peças de acordo com a Etapa Atual", styleNegritoPreto);
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
            registra("\n\nValores de peças inicializados para ambos os jogadores:\nPEAO: ", styleNormal);            
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
        // 3 - Avaliando heurísticas de valoração de Peças ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////

        if (jTextPane != null){
            registra("\n\nPasso 3 - Análise das Heurísticas de Valoração de Peças", styleNegritoPreto);
        }
        
        for (HeuristicaValorPeca heuristicaValorPeca : etapaAtual.getHeuristicasValorPeca()) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvaliação cancelada", styleNegritoVermelho);
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
                            registra("\nHeurística IGNORADA por também ser vantagem para o Oponente", styleNegritoLaranja);
                        }       
                    }else{
                        if (registrarLog) {
                            registra("\nHeurística NÃO ativada", styleNormal);
                        }
                    }            
        }
        
        if (!calculouValorTabuleiro){
            calculaValorHeuristicoTabuleiro(false);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        // 4 - Avaliando Heurísticas de Valor de Tabuleiro ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////
        
        if (jTextPane != null){
            registra("\n\nPasso 4 - Análise das Heurísticas de Valoração de Tabuleiro", styleNegritoPreto);
        }
        
        for (HeuristicaValorTabuleiro heuristicaValorTabuleiro : etapaAtual.getHeuristicasValorTabuleiro()) {

            if (!avaliacaoAtivada) {
                if (registrarLog) {
                    registra("\nAvaliação cancelada", styleNegritoVermelho);
                }
                return 0;
            }

            aplicacaoHeuristicaEu       = verificaCondicaoHeuristica(heuristicaValorTabuleiro,true);
            aplicacaoHeuristicaOponente = verificaCondicaoHeuristica(heuristicaValorTabuleiro,false);

            if (aplicacaoHeuristicaEu && !aplicacaoHeuristicaOponente){
            
                valorHeuristicoTabuleiro = heuristicaValorTabuleiro.aplicaIncremento(valorHeuristicoTabuleiro);

                if (registrarLog) {
                    registra("\nHeurística ATIVADA para Jogador.", styleNegritoAzul);
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
                        registra("\nHeurística ATIVADA para Oponente.", styleNegritoVermelho);
                        registra("\nIncrementando valor final do Tabuleiro. Valor atualizado: ", styleNormal); 
                        registraValorTabuleiro();
                    }
                    
                }else
                    if (aplicacaoHeuristicaEu && aplicacaoHeuristicaOponente){
                        if (registrarLog) {
                            registra("\nHeurística IGNORADA por também ser vantagem para o OPONENTE", styleNegritoLaranja);     
                        }
                    }else{
                        if (registrarLog) {
                            registra("\nHeurística NÃO ativada", styleNormal);
                        }
                    }            
        }
        
        if (registrarLog){            
            registra("\n\nValor FINAL do TABULEIRO (Jogador - Adversário): ", styleNegritoPreto);
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
            registra("\nCondição:\n"  + heuristica.getCondicaoDHJOG(), styleNormal);
        }

        if (whiteColor) {
            scriptJava = (avaliarParaEu ? heuristica.textoCondicoesToJava() : heuristica.textoCondicoesToJavaSimetrica());
        } else {
            scriptJava = (avaliarParaEu ? heuristica.textoCondicoesToJavaSimetrica() : heuristica.textoCondicoesToJava());
        }

        if (arquivoLog != null) {
            
            if (whiteColor) {
                arquivoLog.registraMensagem("Script gerado " + (avaliarParaEu ? "Normal" : "Simétrico") + " [" + scriptJava + "]");
            } else {
                arquivoLog.registraMensagem("Script gerado " + (avaliarParaEu ? "Simétrico" : "Normal") + " [" + scriptJava + "]");
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
                registra("\nHeurística ATIVADA para Jogador.", styleNegritoAzul);
            }else{
                registra("\nHeurística ATIVADA para Oponente.",styleNegritoVermelho);
            }
            registra(" Alterando Valor de Peças.", styleNormal);
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
                    arquivoLog.registraMensagem("Script gerado " + (aplicarParaEu ? "Normal" : "Simétrico") + " para alterar peças [" + scriptJava + "]");
                } else {
                    arquivoLog.registraMensagem("Script gerado " + (aplicarParaEu ? "Simétrico" : "Normal") + " para alterar peças [" + scriptJava + "]");
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
                registra("\nTotal de peças do Jogador que tiveram o valor alterado pela heurística: ", styleNormal);
                registra(String.valueOf(totalPecas), styleNegritoAzul);
            }else{
                registra("\nTotal de peças do Oponente que tiveram o valor alterado pela heurística: ", styleNormal);
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
            
            registra(". Soma"   + (inicial ? "" : " Atualizada") + " do Adversário: ", styleNormal);
            registra(String.valueOf(somaAdversario), styleNegritoVermelho);
            
            registra("\nValor "   + (inicial ? "Inicial" : "Corrente") + " do TABULEIRO (Jogador - Adversário): ", styleNormal);
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
                                throw new IllegalArgumentException("Tipo de operador não suportado no incremento de peças " + operador + "]");                    
                            }
            
                if (registrarLog) {
                    registra("\n      A Peça [", styleNormal);
                    
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
                    registra(", pois um Rei não recebe valor no cálculo heurístico.", styleNormal);
                }
            }
        }
        
        return total;
    }
    
    public int QUANTIDADE_PECAS(ArrayList<TipoPeca> tiposPecas, ArrayList<Casa> regiao, DHJOG.Cor jogador) {
       
    /**
        QUANTIDADE_PECAS(TIPO_PECA[],CASA[],JOGADOR) RETORNA INTEIRO
         
        FUNCAO QUANTIDADE_PECAS
          DESCRICAO "Retorna a quantidade de peças que existem na região, são dos tipos especificados, e que pertencem ao jogador"
          RETORNA
             INTEIRO DESCRICAO "Quantidade de peças dos tipos especificados do jogador que estão na região"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão contadas"
             CASA[]      REGIAO DESCRICAO "Região onde será realizada a contagem de peças" 
             JOGADOR     JOG    DESCRICAO "Jogador que deverá ter as peças contadas"
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
          DESCRICAO "Retorna verdadeiro caso existam peças dos tipos passados pertencentes ao jogador na região especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna Verdadeiro caso exista pelo menos uma peça de cada tipo passado dentro da região"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão procurados"
             CASA[]      REGIAO DESCRICAO "Região onde será realizada a procura por peças" 
             JOGADOR     JOG    DESCRICAO "Jogador que terá as peças procuradas"
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
          DESCRICAO "Retorna um conjunto de casas onde as pecas dos tipos definidos e pertencentes ao jogador estão localizadas"
          RETORNA
             CASA[] DESCRICAO "Conjunto de casas onde as pecas estão localizadas"
          PARAMETROS
             TIPO_PECA[] TIPOS DESCRICAO "Tipos das Pecas que devem ser localizadas" 
             JOGADOR     JOG   DESCRICAO "Jogador que terá as peças localizadas" --> TRUE (EU)  FALSE(OPONENTE)
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
          DESCRICAO "Retorna um conjunto dos tipos de peças que estão dentro da região passada e pertecem ao jogador"
          RETORNA
             TIPO_PECA[] DESCRICAO "Conjunto de tipo de peças que estão localizadas na região passada"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Região onde será realizada a procura por peças" 
             JOGADOR JOG    DESCRICAO "Jogador que terá as peças identificadas dentro da região"
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
      
       // Retorna Casas das peças Localizadas //
        
       /**  
       PECAS_QUE_ESTAO(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]
        
       FUNCAO PECAS_QUE_ESTAO
          DESCRICAO "Retorna um conjunto com as peças que estão dentro da região passada, que são dos tipos procurados, e pertecem ao jogador"
          RETORNA
             PECA[] DESCRICAO "Conjunto de peças que estão localizadas na região passada"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos das Pecas que devem ser localizadas" 
             CASA[]      REGIAO DESCRICAO "Região onde será realizada a procura por peças" 
             JOGADOR     JOG    DESCRICAO "Jogador que terá as peças identificadas dentro da região"
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
          DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior quantidade de peças na região especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua mais peças que o oponente na região"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Região onde será realizada a contagem de peças dos jogadores" 
             JOGADOR JOG    DESCRICAO "Jogador que será testado se possui a maior quantidade de peças"
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
          DESCRICAO "Retorna verdadeiro caso o jogador tenha a maior soma de valores de peças na região especificada"
          RETORNA
             LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua uma soma de peças maior que o oponente na região"
          PARAMETROS
             CASA[]  REGIAO DESCRICAO "Região onde será realizada a soma das peças dos jogadores" 
             JOGADOR JOG    DESCRICAO "Jogador que será testado se possui a maior soma de valores de peças"
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
          DESCRICAO "Retorna a soma da peças que existem na região, são dos tipos especificados, e que pertencem ao jogador"
          RETORNA
             REAL DESCRICAO "Soma das peças dos tipos especificados do jogador que estão na região"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de Peças que serão somadas"
             CASA[]      REGIAO DESCRICAO "Região onde será realizada a procurar pelas peças" 
             JOGADOR     JOG    DESCRICAO "Jogador que deverá ter as peças somadas"
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
            DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçada na região"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçada na região"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçando outra na região"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos passados esteja ameaçando outra na região"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos de peças passados esteja protegendo outra na região"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso alguma peça do jogador dos tipos de peças passados esteja protegendo outra na região"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna verdadeiro caso todas as peças do jogador dos tipos especificados estejam protegidas se elas existirem dentro da região passada"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro caso todas as peças do jogador dos tipos especificados estejam protegidas se elas existirem dentro da região passada"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
             DESCRICAO "Retorna os tipos de peças do jogador que estão ameaçando as peças dos tipos especificados, dentro da região"
          RETORNA
             TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão ameaçando as peças dos tipos passados, na região"
          PARAMETROS
             TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem está ameaçando elas"
             CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
             JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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

         // Retorna Casas das peças que Ameaçam //
        
         /**           
          PECAS_QUE_AMEACAM(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]
           
          FUNCAO PECAS_QUE_AMEACAM
           DESCRICAO "Retorna a peças do jogador que estão ameaçando as peças dos tipos especificados, dentro da região"
           RETORNA
            PECA[] DESCRICAO "Conjunto de peças do jogador que estão ameaçando as peças dos tipos passados, na região"
           PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem está ameaçando elas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna os tipos de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados, dentro da região"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão ameaçando"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
        
        // Retorna a localização das Peças Ameaçadas Por //
        
        /**
         PECAS_AMEACADAS_POR(TIPO_PECA[],CASA[],JOGADOR) RETORNA PECA[]

         FUNCAO PECAS_AMEACADAS_POR
          DESCRICAO "Retorna as peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados, dentro da região"
          RETORNA
           PECA[] DESCRICAO "Conjunto de peças do jogador que estão sendo ameaçadas pelas peças dos tipos passados"
          PARAMETROS
           TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão ameaçando"
           CASA[]      REGIAO DESCRICAO "Região onde será verificada a ameaça das peças"
           JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna os tipos de peças do jogador que estão protegendo as peças dos tipos passados, dentro da região"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogaodr que estão protegendo as peças dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem protege elas"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Retorna os tipos de peças do jogador que estão sendo protegidos pelas peças dos tipos passados, na região"
         RETORNA
            TIPO_PECA[] DESCRICAO "Conjunto de tipos de peças do jogador que estão sendo protegidas pelas peças dos tipos passados"
         PARAMETROS
            TIPO_PECA[] TIPOS  DESCRICAO "Tipos de peças que deverão ser verificadas quem elas estão protegendo"
            CASA[]      REGIAO DESCRICAO "Região onde será verificada a proteção das peças"
            JOGADOR     JOG    DESCRICAO "Jogador que terá as peças verificadas"
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
            DESCRICAO "Verifica se é possível para o Jogador passado executar a jogada Roque Grande"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro se ainda for possível que o jogador passado executar a jogada Roque Grande"
         PARAMETROS
            JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de Roque Grande"
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
            DESCRICAO "Verifica se é possível para o Jogador passado executar a jogada Roque Pequeno"
         RETORNA
            LOGICO DESCRICAO "Retorna verdadeiro se ainda for possível que o jogador passado executar a jogada Roque Pequeno"
         PARAMETROS
            JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de Roque Pequeno"
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
          DESCRICAO "Retorna a quantidade de lances realizados sem movimento de Peões e sem capturas de peças"
          RETORNA
           INTEIRO DESCRICAO "Quantidade de lances sem captura e movimento de peões"
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
          DESCRICAO "Retorna verdadeiro caso o jogador possua um Peão passível de ser capturado En Passant"
          RETORNA
           LOGICO DESCRICAO "Retorna verdadeiro caso o jogador possua um Peão passível de ser capturado En Passant"
          PARAMETROS
           JOGADOR JOG DESCRICAO "Jogador que deverá ser verificado a possibilidade de captura En Passant"
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
        
        // Retorna a Localização dos Peões que ameaçam //
        
        ArrayList<Casa> localizacaoPeosQueAmeacam = new ArrayList();                
        
        int l, c;
        
        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        if (jogador == DHJOG.Cor.BRANCAS) { //se analisando pelas brancas avalia peões vindos do topo do tabuleiro
            
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
            
        } else {  // se analisando pelas pretas avalia peões vindos da base do tabuleiro
            
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
        
        // Retorna a Localização dos Cavalos que Ameaçam //
        
        ArrayList<Casa> localizacaoCavalosQueAmeacam = new ArrayList();               
        
        int l, c, lin, col;
        
        Peca peca;

        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. prosição

        lin = l + 2;
        col = c + 1;
        
        if ((lin <= 7) && (col <= 7)) {            
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 2a. posição
        
        lin = l + 1;
        col = c + 2;
        
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 3a. posição
        
        lin = l - 1;
        col = c + 2;
        
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 4a. posição
        
        lin = l - 2;
        col = c + 1;
        
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }

        // Avalia 5a. posição
        
        lin = l - 2;
        col = c - 1;
        
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }

        // Avalia 6a. posição
        
        lin = l - 1;
        col = c - 2;
        
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 7a. posição
        
        lin = l + 1;
        col = c - 2;
        
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (peca != null && peca.getTipo() == TipoPeca.CAVALO && peca.getCor() != jogador) {
                localizacaoCavalosQueAmeacam.add(Casa.porIndices(col,lin));
            }
        }
        
        // Avalia 8a. posição
        
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
        
        // Retorna a localização das Torres que Ameaçam //
        
        ArrayList<Casa> localizacaoTorresQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. direçao LESTE
        
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

        // Avalia 2a. direçao SUL
        
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

        // Avalia 3a. direçao OESTE
        
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

        // Avalia 4a. direçao NORTE
        
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
        
        // Retorna a localização dos Bispos que Ameaçam //
        
        ArrayList<Casa> localizacaoBisposQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();
        
        // Avalia 1a. direçao NE
        
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

        // Avalia 2a. direçao SE
        
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

        // Avalia 3a. direçao SO
        
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

        // Avalia 4a. direçao N0
        
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
        
        // Retorna a localização das Damas que Ameaçam //
        
        ArrayList<Casa> localizacaoDamasQueAmeacam = new ArrayList();
        
        int l, c, lin, col;

        Peca peca;
        
        l = casa.getIndiceLinha();
        c = casa.getIndiceColuna();

        // Avalia 1a. direçao NORDESTE
        
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

        // Avalia 2a. direçao SUDESTE
        
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

        // Avalia 3a. direçao SUDOESTE
        
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

        // Avalia 4a. direçao NOROESTE
        
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

        // Avalia 5a. direçao LESTE
        
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

        // Avalia 6a. direçao SUL
        
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

        // Avalia 7a. direçao OESTE
        
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

        // Avalia 8a. direçao NORTE
        
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
        
        // Retorna a Casa do Rei que Ameaça //
        
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
        Peca peca; //peça que terá foco da investigação
        
        if (jogador == DHJOG.Cor.BRANCAS) { //se analisando pelas brancas avalia peões vindos do base do tabuleiro
            
            l = casa.getIndiceLinha();
            c = casa.getIndiceColuna();

            if (l <= 1) {
                return false;
            }
            
            // verifica se a posição em si não tem como ser protegida por peões;
            
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
            
            // se analisando pelas pretas avalia peões vindos do topo do tabuleiro
            
            l = casa.getIndiceLinha();
            c = casa.getIndiceColuna();

            if (l >= 6) {
                return false;
            } 
            
            //verifica se a posição em si não tem como ser protegida por peões;
            
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

        // avalia primeira posição sentido horário 12:00hs

        lin = l + 2;
        col = c + 1;
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 2a. posição
        
        lin = l + 1;
        col = c + 2;
        if ((lin <= 7) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 3a. posição
        
        lin = l - 1;
        col = c + 2;
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 4a. posição
        
        lin = l - 2;
        col = c + 1;
        if ((lin >= 0) && (col <= 7)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }

        // avalia 5a. posição
        
        lin = l - 2;
        col = c - 1;
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }

        // avalia 6a. posição
        
        lin = l - 1;
        col = c - 2;
        if ((lin >= 0) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 7a. posição
        
        lin = l + 1;
        col = c - 2;
        if ((lin <= 7) && (col >= 0)) {
            peca = tabuleiro.getPeca(col,lin);
            if (((peca != null) && (peca.getTipo() == TipoPeca.CAVALO) && (peca.getCor() == jogador))) {
                return true;
            }
        }
        
        // avalia 8a. posição
        
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

        // avalia 1a. direçao NE
        
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

        // avalia 2a. direçao SE
        
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

        // avalia 3a. direçao SO
        
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

        // avalia 1a. direçao NE
        
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

        // avalia 1a. direçao LESTE
        
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

        // avalia 2a. direçao SUL
        
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

        // avalia 3a. direçao OESTE
        
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

        // avalia 4a. direçao NORTE
        
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
        
        // avalia as 8 posições onde pode estar o rei se a casa atual não está nas bordas dos tabuleiros
        
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

        // avalia 1a. direçao NE
        
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

        // avalia 2a. direçao SE
        
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

        // avalia 3a. direçao SO
        
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

        // avalia 1a. direçao NE
        
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

        // avalia 1a. direçao LESTE
        
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

        // avalia 2a. direçao SUL
        
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

        // avalia 3a. direçao OESTE
        
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

        // avalia 4a. direçao NORTE
        
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
        
        // verifica se todas os elmentos do conjunto 2 estão no conjunto 1
        
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
                        throw new IllegalArgumentException("Tipo [" + obj.getClass().getName() + "] não suportado na análise de Conjuntos!");
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
                    throw new IllegalArgumentException("Tipo de objeto não suportado para comparação de peças [" + localizacaoPecas.get(x).getClass().getName() + "]");
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
                    throw new IllegalArgumentException("Tipo de objeto não suportado para comparação de peças [" + localizacaoPecas.get(x).getClass().getName() + "]");
                }
        }
        
        return false;
    }
}