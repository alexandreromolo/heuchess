package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaCampeonato;
import br.ufpr.inf.utils.ArquivoLog;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 4, 2013
 */
public class Campeonato {

    public enum Situacao {    
        AGUARDANDO,
        EXECUTANDO,
        CANCELADO,
        TERMINADO;        
    }
    
    public class Pontuacao {
        
        public ConjuntoHeuristico conjuntoHeuristico;
        
        public int quantidadePartidas;
        public int quantidadeVitorias;
        public int quantidadeEmpates;
        public int quantidadeDerrotas;
        public int pontos;
        
        Pontuacao(ConjuntoHeuristico conjunto){
            this.conjuntoHeuristico = conjunto;
        }
    }
    
    public class OrdenaPontuacao implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            
            Pontuacao pontuacao1 = (Pontuacao) o1;
            Pontuacao pontuacao2 = (Pontuacao) o2;
            
            if (pontuacao1.pontos > pontuacao2.pontos){
                return -1;
            }else
                if (pontuacao1.pontos < pontuacao2.pontos){
                    return +1;
                }else
                    if (pontuacao1.quantidadeVitorias > pontuacao2.quantidadeVitorias){
                        return -1;
                    }else
                        if (pontuacao1.quantidadeVitorias < pontuacao2.quantidadeVitorias){
                            return +1;
                        }else
                            if (pontuacao1.quantidadeEmpates > pontuacao2.quantidadeEmpates){
                                return -1;
                            }else
                                if (pontuacao1.quantidadeEmpates > pontuacao2.quantidadeEmpates){    
                                    return -1;                                
                                }else
                                    if (pontuacao1.quantidadeEmpates < pontuacao2.quantidadeEmpates){    
                                        return +1;
                                    }else
                                        if (pontuacao1.quantidadeDerrotas < pontuacao2.quantidadeDerrotas){
                                            return -1;
                                        }else
                                            if (pontuacao1.quantidadeDerrotas > pontuacao2.quantidadeDerrotas){    
                                                return +1;
                                            }else{
                                                return 0;
                                            }
        }
    }
    
    private final ArrayList<ConjuntoHeuristico>          conjuntosHeuristicos;
    private final HashMap<Long, Usuario>                 autoresConjuntosHeuristicos;    
    private final HashMap<ConjuntoHeuristico, Pontuacao> conjuntosPontuacoes;    
    private final ArrayList<Partida>                     partidas;
    private final ArrayList<ExecutaPartida>              executoresPartida; 
    private final ArrayList<Pontuacao>                   classificacao;
    
    private final OrdenaPontuacao ordenador;
    
    private int indiceProximaPartida, totalPartidasRealizadas;
    
    private Tabuleiro tabuleiroInicial;
    
    private int profundidadeBusca;
    private int quantidadePartidasSimultaneas;
    
    private int valorVitoria;
    private int valorEmpate;
    private int valorDerrota;
    
    private boolean geracaoLancesOtimizada;
    
    private Situacao situacao;
    
    private final TelaCampeonato telaCampeonato;
    
    private ArquivoLog arquivoLog;
    
    private long tempoInicionMilissegundos, tempoFinalMilissegundos;
    
    private class ExecutaPartida extends Thread {

        private boolean ativado;
        private Partida partida;

        private int numero;
        
        public ExecutaPartida(int numero) {
            setDaemon(true);
            this.numero = numero;
        }

        public void parar() {
            
            ativado = false;
            
            if (partida != null && partida.getState() == Partida.Estado.IN_PROGRESS){
                partida.cancel();
            }
        }

        @Override
        public synchronized void run() {

            ativado = true;
            
            Lance           movimentoEscolhido;
            EngineRandomica engineRandomica;
            EngineRapida    engineBrancas, enginePretas;
            AvaliadorDHJOG  avaliadorDHJOGBrancas, avaliadorDHJOGPretas;
            
            engineRandomica = new EngineRandomica();
            
            if (geracaoLancesOtimizada) {
                engineBrancas = new EngineRapidaAlphaBetaNega(null, profundidadeBusca);
                enginePretas  = new EngineRapidaAlphaBetaNega(null, profundidadeBusca);
            } else {
                engineBrancas = new EngineRapidaMiniMax(null, profundidadeBusca);
                enginePretas  = new EngineRapidaMiniMax(null, profundidadeBusca);
            }
            
            int indicePartida = indiceProximaPartida();
            
            try {
                //System.out.println("partida " + indicePartida + " executor " + numero);
                
                avaliadorDHJOGBrancas = new AvaliadorDHJOG(null);
                avaliadorDHJOGPretas  = new AvaliadorDHJOG(null);
                
                engineBrancas.setAvaliador(avaliadorDHJOGBrancas);
                enginePretas.setAvaliador(avaliadorDHJOGPretas);
                
                while (ativado && indicePartida != -1) {
            
                    partida = recuperaPartida(indicePartida);
                    //partida = partidas.get(indicePartida);
                    
                    if (partida.getJogadorBranco().getConjuntoHeuristico().getIdAutor() == 0){                        
                        // Jogador Aleatório //                        
                        partida.getJogadorBranco().setEngine(engineRandomica);
                        
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                        }
                         
                    }else{
                        partida.getJogadorBranco().setEngine(engineBrancas);
                        avaliadorDHJOGBrancas.setConjuntoHeuristico(partida.getJogadorBranco().getConjuntoHeuristico());
                    }
                                        
                    if (partida.getJogadorPreto().getConjuntoHeuristico().getIdAutor() == 0){                        
                        
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        
                        // Jogador Aleatório //                        
                        partida.getJogadorPreto().setEngine(engineRandomica);
                    }else{
                        partida.getJogadorPreto().setEngine(enginePretas);
                        avaliadorDHJOGPretas.setConjuntoHeuristico(partida.getJogadorPreto().getConjuntoHeuristico());
                    }

                    partida.restart();

                    telaCampeonato.iniciandoPartida(indicePartida);
                    
                    while (partida.getState() == Partida.Estado.IN_PROGRESS) {

                        Tabuleiro tabuleiroAtual = partida.getTabuleiro();
                
                        if (tabuleiroAtual.isWhiteActive()){
                            movimentoEscolhido = partida.getJogadorBranco().getEngine().getProximoLance(partida.getTabuleiro(),tabuleiroAtual.isWhiteActive());
                        }else{
                            movimentoEscolhido = partida.getJogadorPreto().getEngine().getProximoLance(partida.getTabuleiro(), tabuleiroAtual.isWhiteActive());
                        }
                        
                        partida.applyMovement(movimentoEscolhido);
                    }

                    if (ativado) {

                        partidaTerminou(indicePartida);
                        
                        //partida.getJogadorBranco().setEngine(null);
                        //partida.getJogadorPreto().setEngine(null);

                        indicePartida = indiceProximaPartida();
                    }
                }

                ativado = false;
                
            } catch (Exception e) {

                HeuChess.registraExcecao(e);

                Campeonato.this.parar();

                if (HeuChess.somAtivado) {
                    HeuChess.somPartidaCancelada.play();
                }

                UtilsGUI.dialogoErro(telaCampeonato, "Erro grave na execução de uma partida. O Campeonato será cancelado!\n\n" +
                                                     UtilsString.cortaTextoMaior(e.getMessage(), 100, true));
            }
        }
    }
     
    private synchronized Partida recuperaPartida(int indice){
        return partidas.get(indice);
    }
            
    public Campeonato(TelaCampeonato telaCampeonato) throws Exception {        
        
        this.telaCampeonato = telaCampeonato;
        
        conjuntosHeuristicos = new ArrayList();
        partidas             = new ArrayList();
        executoresPartida    = new ArrayList();
        classificacao        = new ArrayList();
        
        autoresConjuntosHeuristicos = new HashMap();
        conjuntosPontuacoes         = new HashMap();
        
        ordenador = new OrdenaPontuacao();
        
        situacao = Situacao.AGUARDANDO;
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Futility Measure - Medida Futilidade - Usado para comparar a eficiência dos conjuntos com um placebo //
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        ConjuntoHeuristico conjuntoAleatorio = new ConjuntoHeuristico("ESCOLHA ALEATÓRIA", 0, ConjuntoHeuristico.NIVEL_1_INICIANTE);
        
        adicionaConjuntoHeuristico(conjuntoAleatorio);
    }
    
    public final void adicionaConjuntoHeuristico(ConjuntoHeuristico conjuntoHeuristico) throws Exception {
        
        conjuntosHeuristicos.add(conjuntoHeuristico);
        
        Usuario autor = autoresConjuntosHeuristicos.get(conjuntoHeuristico.getIdAutor());
        
        if (autor == null){
            Usuario usuario = UsuarioDAO.busca(conjuntoHeuristico.getIdAutor());        
            autoresConjuntosHeuristicos.put(usuario.getId(), usuario);
        }
    }
    
    public void removeConjuntoHeuristico(ConjuntoHeuristico conjunto){
        
        conjuntosHeuristicos.remove(conjunto);
        
        // Procura se outro Conjunto Heurístico tem o mesmo autor //
        
        for (ConjuntoHeuristico conj : conjuntosHeuristicos){
            
            if (conj.getIdAutor() == conjunto.getIdAutor()){
                // Achou, então não remove o Autor //
                return;
            }
        }
        
        // Não Achou, então remove o autor //
        
        autoresConjuntosHeuristicos.remove(conjunto.getIdAutor());
    }
    
    public ArrayList<ConjuntoHeuristico> conjuntosHeuristicos(){
        return conjuntosHeuristicos;
    }
    
    public Usuario autorConjuntoHeuristico(ConjuntoHeuristico conjunto){
        return autoresConjuntosHeuristicos.get(conjunto.getIdAutor());
    }    

    public ArrayList<Partida> partidas(){
        return partidas;
    }    
    
    public int getProfundidadeBusca() {
        return profundidadeBusca;
    }
    
    public void setProfundidadeBusca(int profundidadeBusca) {
        this.profundidadeBusca = profundidadeBusca;
    }

    public int getQuantidadePartidasSimultaneas() {
        return quantidadePartidasSimultaneas;
    }

    public void setQuantidadePartidasSimultaneas(int quantidadePartidasSimultaneas) {
        this.quantidadePartidasSimultaneas = quantidadePartidasSimultaneas;
    }
    
    public Tabuleiro getTabuleiroInicial() {
        return tabuleiroInicial;
    }

    public void setTabuleiroInicial(Tabuleiro tabuleiroInicial) {
        this.tabuleiroInicial = tabuleiroInicial;
    }

    public int getValorVitoria() {
        return valorVitoria;
    }

    public void setValorVitoria(int valorVitoria) {
        this.valorVitoria = valorVitoria;
    }

    public int getValorEmpate() {
        return valorEmpate;
    }

    public void setValorEmpate(int valorEmpate) {
        this.valorEmpate = valorEmpate;
    }

    public int getValorDerrota() {
        return valorDerrota;
    }

    public void setValorDerrota(int valorDerrota) {
        this.valorDerrota = valorDerrota;
    }

    public boolean isGeracaoLancesOtimizada() {
        return geracaoLancesOtimizada;
    }

    public void setGeracaoLancesOtimizada(boolean geracaoLancesOtimizada) {
        this.geracaoLancesOtimizada = geracaoLancesOtimizada;
    }
    
    public void gerarPartidas() {
        
        partidas.clear();
        
        for (int pos1 = 0; pos1 < conjuntosHeuristicos.size(); pos1++){
        
            Jogador jogadorBrancas = new Jogador(conjuntosHeuristicos.get(pos1), true);
            
            for (int pos2 = 0; pos2 < conjuntosHeuristicos.size(); pos2++){
            
                if (pos1 != pos2){
                    
                    Jogador jogadorPretas  = new Jogador(conjuntosHeuristicos.get(pos2), false);
                    
                    Partida partida = new Partida(jogadorBrancas, jogadorPretas, tabuleiroInicial);
                    partida.setAtualizacaoConstanteTempo(false);
                    
                    partidas.add(partida);
                }
            }
        }
    }
    
    public Situacao getSituacao(){
        return situacao;
    }
    
    public int getTotalPartidasRealizadas(){
        return totalPartidasRealizadas;    
    }
    
    private synchronized int indiceProximaPartida(){
    
        if (indiceProximaPartida < partidas.size()){
            
            int indice = indiceProximaPartida;
            
            indiceProximaPartida++;
            
            return indice;
        }else{
            return -1;
        }
    }
    
    private synchronized void partidaTerminou(int indice){

        totalPartidasRealizadas++;
        
        telaCampeonato.partidaTerminou(indice);
        
        ////////////////////////////
        // Loga Resultado Partida //
        ////////////////////////////
        
        //Partida partida = partidas.get(indicePartida);
        Partida partida = recuperaPartida(indice);
        
        arquivoLog.registraMensagemTempo("Partida Terminou: " + String.valueOf(indice + 1) + " - " + 
                                        autoresConjuntosHeuristicos.get(partida.getJogadorBranco().getConjuntoHeuristico().getIdAutor()) +
                                        " - " + partida.getJogadorBranco().getConjuntoHeuristico().getNome() + " X " + 
                                        autoresConjuntosHeuristicos.get(partida.getJogadorPreto().getConjuntoHeuristico().getIdAutor())  +
                                        " - " + partida.getJogadorPreto().getConjuntoHeuristico().getNome());
            
        arquivoLog.registraMensagem("Tempo Partida: " + UtilsDataTempo.formataTempoNanossegundos(partida.getTempoNanossegundos(), false));
        arquivoLog.registraMensagem("Total Lances:  " + partida.getTotalLancesPartida());
        arquivoLog.registraMensagem("Resultado:     " + partida.getState().getDescricao());
        
        StringBuilder builder = new StringBuilder();
        
        for (String texto : partida.getStringsSAN()){
            builder.append(texto);    
            builder.append(' ');
        }
               
        arquivoLog.registraMensagem("Lances: " + builder.toString());
        arquivoLog.registraLinhaSeparacao();
        
        if (totalPartidasRealizadas == partidas.size()){
                    
            // Campeonato Terminou //
        
            situacao = Situacao.TERMINADO;
            
            arquivoLog.registraMensagemTempo("Campeonato Terminou");
            
            registraDuracaoCampeonato();
                    
            arquivoLog.registraLinhaSeparacao();
            
            telaCampeonato.campeonatoTerminou();
            
            arquivoLog.fechaArquivo();
        }
    }
    
    public void iniciar(){
    
        if (situacao == Situacao.EXECUTANDO){
            UtilsGUI.dialogoErro(telaCampeonato,"O Campeonato já está em Execução!");
            return;
        }
        
        /////////////////////////
        // Cria arquivo de Log //
        /////////////////////////
        
        arquivoLog = ArquivoLog.createArquivoLog("./logs/campeonatos/","Campeonato de Xadrez ","HeuChess", false);
        
        if (!arquivoLog.isOpen()){
            arquivoLog.createNewLogFile();
        }
        
        arquivoLog.registraMensagemTempo("Iniciando Campeonato");
        arquivoLog.registraMensagem("Profundidade de Busca: " + profundidadeBusca);
        arquivoLog.registraMensagem("Geração de Lances: " + (geracaoLancesOtimizada ? "Otimizada" : "Completa"));
        arquivoLog.registraMensagem("Quantidade de Partidas Simultâneas: " + quantidadePartidasSimultaneas);
        arquivoLog.registraLinhaSeparacao();
        
        arquivoLog.registraMensagem("Conjuntos Heurísticos Participantes (Ordem-Autor-Conjunto)");
        for (int cont = 0; cont < conjuntosHeuristicos.size(); cont++){
            ConjuntoHeuristico conjunto = conjuntosHeuristicos.get(cont);
            arquivoLog.registraMensagem(String.valueOf(cont+1) + " - " + 
                                        autoresConjuntosHeuristicos.get(conjunto.getIdAutor()) + " - " + 
                                        conjunto.getNome());
        }
        arquivoLog.registraLinhaSeparacao();
        
        arquivoLog.registraMensagem("Partidas Planejadas - Total de " + partidas.size() + " - (Ordem - Autor Brancas - Brancas X Autor Pretas - Pretas");
        for (int cont = 0; cont < partidas.size(); cont++){
            Partida partida = partidas.get(cont);
            arquivoLog.registraMensagem(String.valueOf(cont+1) + " - " + 
                                        autoresConjuntosHeuristicos.get(partida.getJogadorBranco().getConjuntoHeuristico().getIdAutor()) +
                                        " - " + partida.getJogadorBranco().getConjuntoHeuristico().getNome() + " X " + 
                                        autoresConjuntosHeuristicos.get(partida.getJogadorPreto().getConjuntoHeuristico().getIdAutor()) +
                                        " - " + partida.getJogadorPreto().getConjuntoHeuristico().getNome());
        }
        arquivoLog.registraLinhaSeparacao();
        
        /////////////////////////
        /////////////////////////
        
        situacao = Situacao.EXECUTANDO;
        
        tempoInicionMilissegundos = System.currentTimeMillis();
                
        executoresPartida.clear();
                
        indiceProximaPartida    = 0;
        totalPartidasRealizadas = 0;
        
        int totalExecutores = (quantidadePartidasSimultaneas > partidas.size() ? partidas.size() : quantidadePartidasSimultaneas);
        
        for (int cont = 0; cont < totalExecutores; cont++){
            
            ExecutaPartida executaPartida = new ExecutaPartida(cont);
            
            executoresPartida.add(executaPartida);
                    
            executaPartida.start();
        }
    }
    
    public void parar() {

        if (situacao == Situacao.EXECUTANDO) {

            situacao = Situacao.CANCELADO;
            
            for (ExecutaPartida executa : executoresPartida) {
                executa.parar();
            }
            
            for (Partida partida : partidas){  
                
                if (partida.getState() == Partida.Estado.CREATED){
                    partida.cancel();
                }
            }
           
            arquivoLog.registraMensagemTempo("Campeonato Interrompido!");            
            
            registraDuracaoCampeonato();
            
            arquivoLog.fechaArquivo();
        }
    }
    
    private void registraDuracaoCampeonato(){
        
        tempoFinalMilissegundos = System.currentTimeMillis();            
            
        arquivoLog.registraMensagem("Tempo Total: " + UtilsDataTempo.formataTempoMilissegundos(getDuracaoMilissegundos(), false));
    }
    
    public long getDuracaoMilissegundos(){
        
        if (situacao == Situacao.EXECUTANDO){
            return System.currentTimeMillis() - tempoInicionMilissegundos;
        }else{
            return tempoFinalMilissegundos - tempoInicionMilissegundos;        
        }
    }
    
    public ArrayList<Pontuacao> classificacao(){
        return classificacao;
    }
    
    public void gerarClassificacao(){
    
        classificacao.clear();
        conjuntosPontuacoes.clear();
        
        for (ConjuntoHeuristico conjunto : conjuntosHeuristicos){
            
            Pontuacao pontuacao = new Pontuacao(conjunto);
            
            classificacao.add(pontuacao);
            
            conjuntosPontuacoes.put(conjunto, pontuacao);
        }
        
        for (Partida partida : partidas){
            
            Pontuacao pontuacaoBrancas = conjuntosPontuacoes.get(partida.getJogadorBranco().getConjuntoHeuristico());
            Pontuacao pontuacaoPretas  = conjuntosPontuacoes.get(partida.getJogadorPreto().getConjuntoHeuristico());
            
            pontuacaoBrancas.quantidadePartidas++;
            pontuacaoPretas.quantidadePartidas++;
            
            switch (partida.getState()){
                
                case BLACK_MATES:        //"Vitória das Pretas"
                case WHITE_SUDDEN_DEATH: //"Vitória das Pretas - Derrota das Brancas por Morte Súbita"
                    pontuacaoPretas.quantidadeVitorias++;
                    pontuacaoBrancas.quantidadeDerrotas++;
                    break;
                
                case WHITE_MATES:        // "Vitória das Brancas"
                case BLACK_SUDDEN_DEATH: // "Vitória das Brancas - Derrota das Pretas por Morte Súbita"
                    pontuacaoBrancas.quantidadeVitorias++;
                    pontuacaoPretas.quantidadeDerrotas++;
                    break;
                
                case STALEMATE:                  // "Empatou pois o Rei ficou Afogado"
                case DRAWN_BY_50_MOVE_RULE:      // "Empatou pela Regra dos 50 movimentos"
                case DRAWN_BY_TRIPLE_REPETITION: // "Empatou pela Regra da Tripla Repetição de um Tabuleiro"
                    pontuacaoBrancas.quantidadeEmpates++;
                    pontuacaoPretas.quantidadeEmpates++;
                    break;                
            }
        }
        
        for (Pontuacao pontuacao : classificacao){
            
            pontuacao.pontos = (pontuacao.quantidadeVitorias * valorVitoria) +
                               (pontuacao.quantidadeEmpates  * valorEmpate)  +
                               (pontuacao.quantidadeDerrotas * valorDerrota);
        }
        
        Collections.sort(classificacao, ordenador);
        
        arquivoLog.registraMensagemTempo("Classificação Final (Ordem - Autor - Conjunto - Pontos - Partidas - Vitórias - Empates - Derrotas)");
                
        for (int cont = 0; cont < classificacao.size(); cont++){
            Pontuacao pontuacao = classificacao.get(cont);
            arquivoLog.registraMensagem(String.valueOf(cont + 1) + " - " + 
                                        autoresConjuntosHeuristicos.get(pontuacao.conjuntoHeuristico.getIdAutor()) + " - " + 
                                        pontuacao.conjuntoHeuristico.getNome() + " - " + 
                                        pontuacao.pontos + " - " +
                                        pontuacao.quantidadePartidas + " - " +
                                        pontuacao.quantidadeVitorias + " - " +
                                        pontuacao.quantidadeEmpates  + " - " +
                                        pontuacao.quantidadeDerrotas);
        }
    }
}