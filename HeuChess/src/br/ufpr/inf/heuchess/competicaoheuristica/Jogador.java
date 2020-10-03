package br.ufpr.inf.heuchess.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;

/**
 * Classe que armazena os dados de um jogador, que pode ser humano ou um Conjunto Heurístico
 */
public class Jogador {

    public static enum Tipo {
        HUMAN,
        ARTIFICIAL
    }
    
    private final boolean      jogadorBranco;    
    private final Tipo         tipo;    
    private String             nome;
    private ConjuntoHeuristico conjuntoHeuristico;
    private Engine             engine;    
    
    private long tempoGastoMilissegundosPartida;    
    private long tempoGastoMilissegundosMovimento;            
    private long tempoRestanteMilissegundos;    
    private long tempoRestanteMilissegundosMovimento;    
    private long tempoBonusMilissegundos;    
    private long duracaoPartida = 15 * 60 * 1000; // 15 minutos
    
    public Jogador(String name, boolean jogadorBranco) {
        
        assert name != null;
        
        this.nome          = name;
        this.jogadorBranco = jogadorBranco;
        this.tipo          = Tipo.HUMAN;
    }
     
    public Jogador(ConjuntoHeuristico conjuntoHeuristico, boolean jogadorBranco) {
        
        assert conjuntoHeuristico != null;
        
        this.conjuntoHeuristico = conjuntoHeuristico;
        this.jogadorBranco      = jogadorBranco;        
        this.tipo               = Tipo.ARTIFICIAL;        
    }
    
    public ConjuntoHeuristico getConjuntoHeuristico(){
        return conjuntoHeuristico;
    }
    
    public String getName() {
        
        if (conjuntoHeuristico != null){
            return conjuntoHeuristico.getNome();
        }else{
            return nome;
        }
    }
    
    public Tipo getTipo(){
        return tipo;
    }
    
    public void setEngine(Engine engine){
        this.engine = engine;
    }
    
    public Engine getEngine() {
        return engine;
    }
    
    public boolean isJogadorBranco() {
        return jogadorBranco;
    }
    
    public void setLimiteTempo(long duracao, long bonus){
        
        duracaoPartida          = duracao;
        tempoBonusMilissegundos = bonus;
        
        reiniciaTempo();
    }
    
    public void reiniciaTempo(){
        tempoRestanteMilissegundos = duracaoPartida;
        tempoGastoMilissegundosPartida      = 0;
        tempoGastoMilissegundosMovimento    = 0;
        tempoRestanteMilissegundosMovimento = tempoBonusMilissegundos;
    }
    
    public long getTempoBonusMilissegundos(){
        return tempoBonusMilissegundos;
    }
    
    public long getTempoGastoMilissegundosPartida(){
        return tempoGastoMilissegundosPartida;
    }
    
    public void aumentaTempoGastoMilissegundosPartida(long valor){
        tempoGastoMilissegundosPartida += valor;
    }
    
    public long getTempoRestanteMilissegundos(){
        return tempoRestanteMilissegundos;
    }
    
    public void diminuiTempoRestanteMilissegundos(long valor){
        tempoRestanteMilissegundos -= valor;
    }
    
    public void adicionaBonusAoTempoRestanteMilissegundos(){
        tempoRestanteMilissegundos += tempoBonusMilissegundos;
    }
    
    public long getTempoGastoMilissegundosMovimento(){
        return tempoGastoMilissegundosMovimento;
    }
    
    public void adicionaTempoGastoMilissegundosMovimento(long valor){
        tempoGastoMilissegundosMovimento += valor;
    }
    
    public long getTempoRestanteMilissegundosMovimento(){
        return tempoRestanteMilissegundosMovimento;
    }
    
    public void diminuiTempoRestanteMilissegundosMovimento(long valor){
        tempoRestanteMilissegundosMovimento -= valor;
    }
      
    public void movimentoFeito(){
        tempoGastoMilissegundosMovimento = 0;
        tempoRestanteMilissegundosMovimento = tempoBonusMilissegundos;
    }
}