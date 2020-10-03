package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.utils.ArquivoLog;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Partida {

    public static enum Estado {
        
        CREATED("Aguardando"),
        IN_PROGRESS("Executando"),
        CANCELED("Cancelada"),
        BLACK_MATES("Vitória das Pretas"),
        BLACK_SUDDEN_DEATH("Vitória das Brancas - Derrota das Pretas por Morte Súbita"),
        WHITE_MATES("Vitória das Brancas"),
        WHITE_SUDDEN_DEATH("Vitória das Pretas - Derrota das Brancas por Morte Súbita"),
        STALEMATE("Empatou pois o Rei ficou Afogado"),
        DRAWN_BY_TRIPLE_REPETITION("Empatou pela Regra da Tripla Repetição de um Tabuleiro"),
        DRAWN_BY_50_MOVE_RULE("Empatou pela Regra dos 50 movimentos");
        
        private String descricao;
        
        private Estado(String descricao){
            this.descricao = descricao;
        }
        
        public String getDescricao(){
            return descricao;
        }
    }
   
    public static enum Modalidade {
        TEMPO_ILIMITADO,
        TEMPO_LIMITADO
    }
    
    public static enum ModoRelogio {
        RELOGIO_FISCHER,
        RELOGIO_BRONSTEIN
    }
    
    private final Tabuleiro tabuleiroInicial;
    private final Jogador   jogadorBranco, jogadorPreto;
    
    private final List<Lance>     lances    = new ArrayList<>();
    private final List<String>    lancesSAN = new ArrayList<>();
    private final List<Tabuleiro> posicoes = new ArrayList<>();
    
    private List<Lance>     backupLances    = new ArrayList<>();
    private List<String>    backupLancesSAN = new ArrayList<>();
    private List<Tabuleiro> backupPositions = new ArrayList<>();
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private int lanceCorrente, posicaoCorrente;
    
    private Timer timer;
    private long  tempoUltimoTickMilissegundos;
    private long  tempoInicialNanossegundos, tempoTotalPartidalNanossegundos; 
    
    private Estado      estadoAtual;
    private Modalidade  modalidade  = Modalidade.TEMPO_ILIMITADO;
    private ModoRelogio modoRelogio = null;
    
    private ArquivoLog arquivoLog;
    
    private boolean terminou, passoAPasso , atualizacaoConstanteTempo = true;
        
    public Partida(final Jogador jogadorBrancas, final Jogador jogadorPretas, final Tabuleiro tabuleiroInicial) {
        
        this.tabuleiroInicial = tabuleiroInicial;
        
        this.jogadorBranco = jogadorBrancas;
        this.jogadorPreto = jogadorPretas;
        
        /*
        if (criarLog){
            arquivoLog = ArquivoLog.createArquivoLog("./logs/","Partida de Xadrez ","HeuChess", false);
        }*/
        
        lanceCorrente = lances.size();
        
        posicoes.add(tabuleiroInicial);
        posicaoCorrente = posicoes.size();
        
        estadoAtual = Estado.CREATED;
    }
    
    public void setArquivoLog(final ArquivoLog arquivoLog){
        this.arquivoLog = arquivoLog;
    }
    
    public boolean isTerminou() {
        return terminou;
    }
    
    public int getTotalLancesPartida(){
        
        if (terminou){
            if (passoAPasso){
                return backupLances.size();
            }else{
                return lances.size();
            }                        
        }else{
            return -1;
        }
    }
    
    public int getMovesCount() {
        
        return lances.size();
    }
    
    public int getCurrentLanceIndex() {
        
        assert (lanceCorrente >= 0) && (lanceCorrente <= lances.size());
        
        return lanceCorrente - 1;
    }
    
    public void setPassoAPasso(boolean passoAPasso) {
        this.passoAPasso = passoAPasso;
    }
    
    public boolean isPassoAPasso(){
        return passoAPasso;
    }
    
    public void setAtualizacaoConstanteTempo(boolean atualizacaoConstanteTempo){
        this.atualizacaoConstanteTempo = atualizacaoConstanteTempo;
    }
    
    public boolean isAtualizacaoConstanteTempo(){
        return atualizacaoConstanteTempo;
    }
    
    public Jogador getJogadorBranco() {
        return jogadorBranco;
    }

    public Jogador getJogadorPreto() {
        return jogadorPreto;
    }
    
    public ModoRelogio getModoRelogio(){
        return modoRelogio;
    }
    
    public Modalidade getModalidade(){
        return modalidade;
    }

    public Tabuleiro getTabuleiro() {
        
        assert (posicaoCorrente > 0) && (posicaoCorrente <= posicoes.size());

        return posicoes.get(posicaoCorrente - 1);
    }
    
    public Lance getCurrentMove() {
        
        Lance res = null;

        if ((lanceCorrente > 0) && (lanceCorrente <= lances.size())) {
            res = lances.get(getCurrentLanceIndex());
        }

        return res;
    }
    
    public String getPositionFEN() {
        
        assert (posicaoCorrente > 0) && (posicaoCorrente <= posicoes.size());

        return getTabuleiro().getFEN();
    }
    
    public String[] getStringsSAN() {
        return lancesSAN.toArray(new String[lancesSAN.size()]);
    }
    
    public Lance[] getLancesToCurrent() {
        
        final Lance[] res = new Lance[lanceCorrente];

        for (int i = 0; i < lanceCorrente; i++) {
            res[i] = lances.get(i);
        }

        return res;
    }
 
    public long getTempoNanossegundos(){
        
        if (terminou){
            return tempoTotalPartidalNanossegundos;
        }else{
            return System.nanoTime() - tempoInicialNanossegundos;
        }        
    }
     
    public void addPropertyChangeListener(final String propriedade, final PropertyChangeListener changeListener) {
        
        assert propriedade    != null;
        assert changeListener != null;

        propertyChangeSupport.addPropertyChangeListener(propriedade, changeListener);
    }
    
    public void removePropertyChangeListener(final String propriedade, final PropertyChangeListener changeListener) {
        
        assert propriedade     != null;
        assert changeListener  != null;

        propertyChangeSupport.removePropertyChangeListener(propriedade, changeListener);
    }
    
    public void goFirst() {
            
        if (lanceCorrente > 0) {
            
            lanceCorrente = 0;
            posicaoCorrente = 1;
            
            if (passoAPasso && estadoAtual != Estado.IN_PROGRESS){
                estadoAtual = Estado.IN_PROGRESS;
            }
            
            propertyChangeSupport.firePropertyChange("position", null, null);            
        }
    }
    
    public void goLast() {

        int s;
        
        if (passoAPasso && terminou) {

            lances.clear();
            lances.addAll(backupLances);
            lancesSAN.clear();
            lancesSAN.addAll(backupLancesSAN);
            posicoes.clear();
            posicoes.addAll(backupPositions);            
            
            s = lances.size() - 1;
        } else {            
            s = lances.size();            
        }
        
        if (lanceCorrente < s) {

            lanceCorrente = s;
            posicaoCorrente = (passoAPasso ? posicoes.size() - 1 : posicoes.size());

            propertyChangeSupport.firePropertyChange("position", null, null);
        }
    }

    public void goNext() {
        
        if (lanceCorrente < lances.size()) {
            
            lanceCorrente++;
            posicaoCorrente++;
            
            propertyChangeSupport.firePropertyChange("position", null, null);
        }
    }

    public void goPrevious() {
        
        if (lanceCorrente > 0) {
            
            lanceCorrente--;
            posicaoCorrente--;
            
            if (passoAPasso && estadoAtual != Estado.IN_PROGRESS) {
                estadoAtual = Estado.IN_PROGRESS;
            }
            
            propertyChangeSupport.firePropertyChange("position", null, null);
        }
    }
    
    public void setTimeLimit(long duracaoWhite, long bonusWhite, long duracaoBlack, long bonusBlack, ModoRelogio clockMode) {
        
        assert duracaoWhite > 0;
        assert bonusWhite  >= 0;
        assert duracaoBlack > 0;
        assert bonusBlack  >= 0;
        assert clockMode != null;
        
        modalidade = Modalidade.TEMPO_LIMITADO;
        
        this.modoRelogio = clockMode;
        
        jogadorBranco.setLimiteTempo(duracaoWhite, bonusWhite);
        jogadorPreto.setLimiteTempo(duracaoBlack, bonusBlack);
    }
    
    public void cancelLimitedTimeGame(){
        
        modalidade  = Modalidade.TEMPO_ILIMITADO;
        
        modoRelogio = null;
        
        jogadorBranco.setLimiteTempo(0,0);
        jogadorPreto.setLimiteTempo(0,0);
    }
    
    public synchronized void restart() {
        
        if (arquivoLog != null){
            iniciandoUsoArquivoLog();
        }
        
        stopTimerTask();
        stopEngineSearch();
                
        lances.clear();
        lancesSAN.clear();
        
        lanceCorrente = lances.size();
        
        posicoes.clear();
        posicoes.add(tabuleiroInicial);
        
        posicaoCorrente = posicoes.size();

        estadoAtual = Estado.IN_PROGRESS;

        jogadorBranco.reiniciaTempo();
        jogadorPreto.reiniciaTempo();
        
        tempoInicialNanossegundos = System.nanoTime();
        
        terminou = false;
        
        if (!passoAPasso && atualizacaoConstanteTempo) {
        
            tempoUltimoTickMilissegundos = System.currentTimeMillis();
            
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                
                @Override
                public void run() {
                    
                    if (getState() == Estado.IN_PROGRESS) {

                        final long currentTime = System.currentTimeMillis();
                        final long duracao     = currentTime - tempoUltimoTickMilissegundos;

                        if (getTabuleiro().isWhiteActive()) {
                            jogadorBranco.aumentaTempoGastoMilissegundosPartida(duracao);
                            jogadorBranco.adicionaTempoGastoMilissegundosMovimento(duracao);
                        } else {
                            jogadorPreto.aumentaTempoGastoMilissegundosPartida(duracao);
                            jogadorPreto.adicionaTempoGastoMilissegundosMovimento(duracao);
                        }

                        if (modoRelogio == ModoRelogio.RELOGIO_FISCHER) {

                            if (getTabuleiro().isWhiteActive()) {
                                jogadorBranco.diminuiTempoRestanteMilissegundos(duracao);
                                if (jogadorBranco.getTempoRestanteMilissegundos() <= 0) {
                                    stopTimerTask();
                                    stopEngineSearch();
                                }

                            } else {
                                jogadorPreto.diminuiTempoRestanteMilissegundos(duracao);
                                if (jogadorPreto.getTempoRestanteMilissegundos() <= 0) {
                                    stopTimerTask();
                                    stopEngineSearch();
                                }
                            }
                        }

                        if (modoRelogio == ModoRelogio.RELOGIO_BRONSTEIN) {

                            if (getTabuleiro().isWhiteActive()) {

                                if (jogadorBranco.getTempoRestanteMilissegundosMovimento() > 0) {
                                    
                                    if (jogadorBranco.getTempoRestanteMilissegundosMovimento() - duracao >= 0) {
                                        jogadorBranco.diminuiTempoRestanteMilissegundosMovimento(duracao);
                                    } else {
                                        long diferenca = duracao - jogadorBranco.getTempoRestanteMilissegundosMovimento();
                                        jogadorBranco.diminuiTempoRestanteMilissegundosMovimento(jogadorBranco.getTempoRestanteMilissegundosMovimento());
                                        jogadorBranco.diminuiTempoRestanteMilissegundos(diferenca);
                                    }
                                    
                                } else {
                                    jogadorBranco.diminuiTempoRestanteMilissegundos(duracao);
                                }
                                
                                if (jogadorBranco.getTempoRestanteMilissegundos() <= 0) {
                                    stopTimerTask();
                                    stopEngineSearch();
                                }

                            } else {

                                if (jogadorPreto.getTempoRestanteMilissegundosMovimento() > 0) {
                                    
                                    if (jogadorPreto.getTempoRestanteMilissegundosMovimento() - duracao >= 0) {
                                        jogadorPreto.diminuiTempoRestanteMilissegundosMovimento(duracao);
                                    } else {
                                        long diferenca = duracao - jogadorPreto.getTempoRestanteMilissegundosMovimento();
                                        jogadorPreto.diminuiTempoRestanteMilissegundosMovimento(jogadorPreto.getTempoRestanteMilissegundosMovimento());
                                        jogadorPreto.diminuiTempoRestanteMilissegundos(diferenca);
                                    }
                                    
                                } else {
                                    jogadorPreto.diminuiTempoRestanteMilissegundos(duracao);
                                }
                                
                                if (jogadorPreto.getTempoRestanteMilissegundos() <= 0) {
                                    stopTimerTask();
                                    stopEngineSearch();
                                }
                            }
                        }

                        tempoUltimoTickMilissegundos = currentTime;

                        propertyChangeSupport.firePropertyChange("timer", null, null);
                    }
                }
            }, 250, 1000);
        }

        propertyChangeSupport.firePropertyChange("state",    null, null);
        propertyChangeSupport.firePropertyChange("position", null, null);
    }
            
    public synchronized Estado getState() {
        
        if (estadoAtual != Estado.IN_PROGRESS){
            return estadoAtual;
        }

        Tabuleiro tabuleiroAtual = getTabuleiro();
        boolean isCurrentColorWhite = tabuleiroAtual.isWhiteActive();
        
        if (modalidade == Modalidade.TEMPO_LIMITADO) {
            
            if (isCurrentColorWhite) {
                
                if (jogadorBranco.getTempoRestanteMilissegundos() <= 0){
                    updateFinalState(Estado.WHITE_SUDDEN_DEATH,"0-1");
                    return estadoAtual;
                }
                
            } else {
                
                if (jogadorPreto.getTempoRestanteMilissegundos() <= 0){
                    updateFinalState(Estado.BLACK_SUDDEN_DEATH,"1-0");
                    return estadoAtual;
                }
            }
        }
                
        if (tabuleiroAtual.getMovimentosValidosJogador(isCurrentColorWhite).length == 0) {
            
            if (tabuleiroAtual.estaEmXeque(isCurrentColorWhite)) {
                if (isCurrentColorWhite) {
                    updateFinalState(Estado.BLACK_MATES,"0-1");
                } else {
                    updateFinalState(Estado.WHITE_MATES,"1-0");
                }
            } else {
                updateFinalState(Estado.STALEMATE,"1/2-1/2 {Rei Afogado}");
            }
            
        } else 
            if (tabuleiroAtual.podeEmpatar50Movimentos()) {
                updateFinalState(Estado.DRAWN_BY_50_MOVE_RULE,"1/2-1/2 {50 Movimentos}");
            } else
                if (tabuleiroAtual.podeEmpatarTriplaRepeticao()) {
                    updateFinalState(Estado.DRAWN_BY_TRIPLE_REPETITION,"1/2-1/2 {Tripla Repetição}");
                }            

        return estadoAtual;
    }
    
    private synchronized void updateFinalState(Estado state, String descricao){
        
        stopTimerTask();
        
        estadoAtual = state;
        lancesSAN.add(descricao);
        propertyChangeSupport.firePropertyChange("state", null, null);
                
        tempoTotalPartidalNanossegundos = System.nanoTime() - tempoInicialNanossegundos;
        
        terminou = true;
        
        if (passoAPasso){
            backupLances.clear();
            backupLances.addAll(lances);            
            backupLancesSAN.clear();
            backupLancesSAN.addAll(lancesSAN);
            backupPositions.clear();
            backupPositions.addAll(posicoes);            
        }
        
        if (arquivoLog != null){
            finalizaArquivoLog("Partida Terminada");        
        }
    }
    
    public synchronized void cancel(){
        
        stopTimerTask();
        
        estadoAtual = Estado.CANCELED;
        
        stopEngineSearch();
        
        tempoTotalPartidalNanossegundos = System.nanoTime() - tempoInicialNanossegundos;
        
        terminou = true;
        
        lancesSAN.add("0-0 {Cancelada}");
        propertyChangeSupport.firePropertyChange("state", null, null);
        
        if (arquivoLog != null){
            finalizaArquivoLog("Partida Cancelada");
        }
    }
    
    private void iniciandoUsoArquivoLog() {
        
        if (!arquivoLog.isOpen()){
            
            // Esta reiniciando uma nova partida //
            
            arquivoLog.createNewLogFile();
        }
            
        arquivoLog.registraMensagemTempo("Iniciando Partida");
        
        arquivoLog.registraMensagem("Brancas: " + jogadorBranco.getName());  
        
        if (jogadorBranco.getTipo() == Jogador.Tipo.ARTIFICIAL){
            
            arquivoLog.registraMensagem("Profundidade de Busca: " + jogadorBranco.getEngine().getProfundidadeBusca());
            ConjuntoHeuristico conjuntoBrancas = jogadorBranco.getConjuntoHeuristico();
            
            try {
                arquivoLog.registraMensagem("Autor: " + UsuarioDAO.buscaNomeUsuario(conjuntoBrancas.getIdAutor()));     
            } catch (Exception e) {
                HeuChess.registraExcecao(e);
                UtilsGUI.dialogoErro(null, "Erro ao recuperar nomde do Autor do Conjunto Heurístico no Banco de dados!");
            }
        }
        
        arquivoLog.registraMensagem("Pretas : " + jogadorPreto.getName());
        
        if (jogadorPreto.getTipo() == Jogador.Tipo.ARTIFICIAL){
            
            arquivoLog.registraMensagem("Profundidade de Busca: " + jogadorPreto.getEngine().getProfundidadeBusca());
            ConjuntoHeuristico conjuntoPretas = jogadorPreto.getConjuntoHeuristico();
            
            try {
                arquivoLog.registraMensagem("Autor: " + UsuarioDAO.buscaNomeUsuario(conjuntoPretas.getIdAutor()));
            } catch (Exception e) {
                HeuChess.registraExcecao(e);
                UtilsGUI.dialogoErro(null, "Erro ao recuperar nomde do Autor do Conjunto Heurístico no Banco de dados!");
            }
        }
    }
    
    private void finalizaArquivoLog(String texto){
        
        arquivoLog.registraLinhaSeparacao();
        arquivoLog.registraMensagem(texto);
        arquivoLog.registraLinhaSeparacao();
        
        if (passoAPasso){
            arquivoLog.registraMensagem("Partida realizada Passo a Passo - Não tem controle de tempo");
        }else{            
            arquivoLog.registraMensagem("Tempo gasto pelo jogador Branco: " + UtilsDataTempo.formataTempoMilissegundos(jogadorBranco.getTempoGastoMilissegundosPartida()));
            arquivoLog.registraMensagem("Tempo gasto pelo jogador Preto : " + UtilsDataTempo.formataTempoMilissegundos(jogadorPreto.getTempoGastoMilissegundosPartida()));
            arquivoLog.registraMensagem("Tempo da Partida               : " + UtilsDataTempo.formataTempoMilissegundos(getTempoNanossegundos()));
        }
        
        arquivoLog.registraLinhaSeparacao();
        arquivoLog.registraMensagem("Jogadas realizadas (Notação SAN)");
        
        for (String jogada : lancesSAN){
            arquivoLog.registraMensagem(jogada);
        }
        
        arquivoLog.registraCabecalhoFinal();
        arquivoLog.fechaArquivo();
    }
    
    private synchronized void stopTimerTask() {
        
        if (timer != null) {
            timer.cancel();
        }        
    }
    
    private synchronized void stopEngineSearch(){
        
        if (jogadorBranco.getEngine() != null){
            jogadorBranco.getEngine().cancelaBusca();
        }
        if (jogadorPreto.getEngine() != null){
            jogadorPreto.getEngine().cancelaBusca();
        }
    }
    
    public synchronized boolean applyMovement(final Lance novoMovimento) {
        
        if (getState() != Estado.IN_PROGRESS){
            return false;
        }
        
        assert novoMovimento != null;

        while (lances.size() > lanceCorrente) {
            lances.remove(lances.size() - 1);
        }
        
        while (lancesSAN.size() > lanceCorrente) {            
            lancesSAN.remove(lancesSAN.size() - 1);
        }
        
        while (posicoes.size() > posicaoCorrente) {
            posicoes.remove(posicoes.size() - 1);
        }

        Tabuleiro tabuleiroAtual = getTabuleiro();
        final boolean isCurrentColorWhite = tabuleiroAtual.isWhiteActive();
        
        final StringBuilder san = new StringBuilder();
        
        if (isCurrentColorWhite) {
            san.append(tabuleiroAtual.getQuantidadeJogadas()).append(". ");
        }
        
        Tabuleiro tabuleiroDerivado = tabuleiroAtual.derive(novoMovimento, true);
        
        san.append(novoMovimento.toSAN(tabuleiroAtual, tabuleiroDerivado));
        san.append(' ');
        
        posicoes.add(tabuleiroDerivado);
        posicaoCorrente = posicoes.size();
        
        lances.add(novoMovimento);
        lanceCorrente = lances.size();

        if (modalidade == Modalidade.TEMPO_LIMITADO && modoRelogio == ModoRelogio.RELOGIO_FISCHER) {
            if (isCurrentColorWhite) {
                jogadorBranco.adicionaBonusAoTempoRestanteMilissegundos();
            } else {
                jogadorPreto.adicionaBonusAoTempoRestanteMilissegundos();
            }
        } 
        
        if (isCurrentColorWhite) {
            jogadorBranco.movimentoFeito();
        } else {
            jogadorPreto.movimentoFeito();
        }
        
        lancesSAN.add(san.toString());

        assert lances.size()     == lancesSAN.size();
        assert posicoes.size() == (lances.size() + 1);
        
        propertyChangeSupport.firePropertyChange("position", null, null);
        
        if (arquivoLog != null){
            ////////////////////////////////////////
            // Força um arquivo de Log por jogada //
            ////////////////////////////////////////
            finalizaArquivoLog("Terminou Movimento");
            iniciandoUsoArquivoLog();
        }
        
        return true;
    }
}