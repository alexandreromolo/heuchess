package br.ufpr.inf.heuchess.representacao.situacaojogo;

import java.util.HashMap;
import java.util.Map;

public enum Peca {

    PEAO_PRETO('p',  false, TipoPeca.PEAO),
    TORRE_PRETA('r', false, TipoPeca.TORRE),
    CAVALO_PRETO('n',false, TipoPeca.CAVALO),
    BISPO_PRETO('b', false, TipoPeca.BISPO),
    DAMA_PRETA('q',  false, TipoPeca.DAMA),
    REI_PRETO('k',   false, TipoPeca.REI),
    
    PEAO_BRANCO('P',  true, TipoPeca.PEAO),
    TORRE_BRANCA('R', true, TipoPeca.TORRE),
    CAVALO_BRANCO('N',true, TipoPeca.CAVALO),
    BISPO_BRANCO('B', true, TipoPeca.BISPO),
    DAMA_BRANCA('Q',  true, TipoPeca.DAMA),
    REI_BRANCO('K',   true, TipoPeca.REI);
    
    private static final Map<Character, Peca> hashMap = new HashMap();

    static {
        for (final Peca p : values()) {
            hashMap.put(Character.valueOf(p.getFEN()), p);
        }
    }

    private char     letraFEN;        
    private String   descricao;
    private TipoPeca tipoPeca;    
    private boolean  corBranca;    
    
    private br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.Cor cor;

    private Peca(final char letra, final boolean corBranca, final TipoPeca tipo) {
        
        assert tipo != null;

        this.letraFEN  = letra;
        this.tipoPeca  = tipo;
        this.corBranca = corBranca;
        this.cor       = (corBranca ? br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.Cor.BRANCAS : 
                                      br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.Cor.PRETAS);
                
        switch(tipoPeca){
            case PEAO:
            case CAVALO:
            case BISPO:
            case REI:
                descricao = tipoPeca.getNome() + " " + cor.toMasculino();
                break;
                
            case TORRE:
            case DAMA:
                descricao = tipoPeca.getNome() + " " + cor.toFeminino();
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de Peça [" + tipoPeca + "] não suportado!");
        }
    }

    public char getFEN() {
        return letraFEN;
    }

    public TipoPeca getTipo() {
        
        assert tipoPeca != null;
        
        return tipoPeca;
    }

    public boolean isWhite() {
        return corBranca;
    }
    
    public br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.Cor getCor(){
        return cor;        
    }
        
    @Override
    public String toString(){
        return descricao;
    }
    
    public boolean igual(br.ufpr.inf.heuchess.representacao.situacaojogo.Peca peca){
        
        if (peca != null &&  tipoPeca == peca.getTipo() && cor == peca.getCor()){
            return true;
        }else{
            return false;
        }
    }
    
    public static Peca porFEN(final char letra) {
        return hashMap.get(Character.valueOf(letra));
    }
}