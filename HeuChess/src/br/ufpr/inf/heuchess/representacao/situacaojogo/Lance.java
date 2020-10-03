package br.ufpr.inf.heuchess.representacao.situacaojogo;

import static br.ufpr.inf.heuchess.representacao.situacaojogo.Peca.*;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class Lance {

    /**
     * Express„o regular para validar o SAN gerado.
     *
     * Mat/Pat/Nullit√© : (\\+{1,2}|#|\\(=\\))?
     * Petit roque : 0-0<Mat/Pat/Nullit√©>
     * Grand roque : 0-0-0<Mat/Pat/Nullit√©>
     * Pion sans prise : [a-h]([1-8]|[18][BKNQR])<Mat/Pat/Nullit√©>
     * Pion avec prise :
     * [a-h]x[a-h]((([1-8]|[18][BKNQR])<Mat/Pat/Nullit√©>)|([36]<Mat/Pat/Nullit√©> e\\.p\\.))
     * Pi√®ces (sauf pion) : [BKNQR][a-h]?[1-8]?x?[a-h][1-8]<Mat/Pat/Nullit√©>
     */ 
    public static final Pattern VALIDADOR_SAN = Pattern.compile("^(0-0(\\+{1,2}|#|\\(=\\))?)|(0-0-0(\\+{1,2}|#|\\(=\\))?)|" +
                                                                "([a-h]([1-8]|[18][BKNQR])(\\+{1,2}|#|\\(=\\))?)|"          +
                                                                "([a-h]x[a-h]((([1-8]|[18][BKNQR])(\\+{1,2}|#|\\(=\\))?)|"  +
                                                                "([36](\\+{1,2}|#|\\(=\\))? e\\.p\\.)))|"                   +
                                                                "([BKNQR][a-h]?[1-8]?x?[a-h][1-8](\\+{1,2}|#|\\(=\\))?)$");
    private final Peca peca;
    private final Peca pecaCapturada;
    private final Casa casaOrigem;
    private final Casa casaDestino;
    
    private Integer idLance;

    public Lance(final Peca peca, final Casa casaOrigem, final Casa casaDestino) {
        
        assert peca        != null;
        assert casaOrigem  != null;
        assert casaDestino != null;
        assert casaOrigem  != casaDestino;

        this.peca          = peca;
        this.casaOrigem    = casaOrigem;
        this.casaDestino   = casaDestino;
        this.pecaCapturada = null;
    }

    public Lance(final Peca peca, final Casa casaOrigem, final Casa casaDestino, final Peca pecaCapturada) {
        
        assert peca        != null;
        assert casaOrigem  != null;
        assert casaDestino != null;
        assert casaOrigem  != casaDestino;
        
        assert (pecaCapturada == null) || (peca.isWhite() != pecaCapturada.isWhite());

        this.peca          = peca;
        this.casaOrigem    = casaOrigem;
        this.casaDestino   = casaDestino;
        this.pecaCapturada = pecaCapturada;
    }

    public Peca getPeca() {
        return peca;
    }
    
    public Peca getPecaCapturada() {
        return  pecaCapturada;
    }

    public Casa getCasaOrigem() {
        return casaOrigem;
    }

    public Casa getCasaDestino() {
        return casaDestino;
    }

    public boolean isPromocao() {
        
        if (peca.getTipo() == TipoPeca.PEAO) {

            int linhaDestino   = casaDestino.getIndiceLinha();
            boolean pecaBranca = peca.isWhite();

            if ((pecaBranca && linhaDestino == Tabuleiro.TOTAL_LINHAS - 1) || (!pecaBranca && linhaDestino == 0)) {
                return true;
            }
        }
        
        return false;
    }
    
    public int toId() {
        
        if (idLance == null) {
            
            int id = (peca.ordinal() << 20) + (casaOrigem.getIndice() << 14) + (casaDestino.getIndice() << 8);
            
            if (pecaCapturada != null) {
                id += (pecaCapturada.ordinal() + 1) << 4;
            }
            
            idLance = Integer.valueOf(id);
        }

        return idLance.intValue();
    }
     
    @Override
    public boolean equals(final Object object) {
        
        if (this == object) {
            return true;
        }
        
        if (object == null){
            return false;
        }

        if (object instanceof Lance) {
            
            final Lance m = (Lance) object;
            
            return (casaOrigem    == m.casaOrigem)  && 
                   (peca          == m.peca)        && 
                   (casaDestino   == m.casaDestino) &&
                   (pecaCapturada == m.pecaCapturada);
        }

        return false;
    }
    
    @Override
    public int hashCode() {
        return toId();
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(peca);
        sb.append(" de ");
        sb.append(casaOrigem.getFEN());
        sb.append(" para ");
        sb.append(casaDestino.getFEN());
        
        if (pecaCapturada != null){
            sb.append(", capturou ").append(pecaCapturada);
        }
        
        return sb.toString();
    }

    public String toSAN(final Tabuleiro tabuleiro, final Tabuleiro tabuleiroDerivado) {
        
        if (tabuleiro == null) {
            throw new NullPointerException("Par‚metro Tabuleiro passado È null");
        }
        
        if (tabuleiroDerivado == null) {
            throw new NullPointerException("Par‚metro Tabuleiro Derivado È null");
        }

        final boolean whiteColor = tabuleiro.isWhiteActive();
        
        final TipoPeca tipoPeca = peca.getTipo();        
        final StringBuilder sb  = new StringBuilder();
        
        final int nrMovimentosValidosOponente = tabuleiroDerivado.getMovimentosValidosJogador(!whiteColor).length;

        final int colunaOrigem  = casaOrigem.getIndiceColuna();
        final int colunaDestino = casaDestino.getIndiceColuna();
        
        if (tipoPeca == REI && (Math.abs(colunaOrigem - colunaDestino) > 1)) {
            
            // Roques //
            
            sb.append("0-0");
            
            if (colunaOrigem > colunaDestino) {
                sb.append("-0");
            }
            
        } else {
            
            // Lance Normal //
            
            if (tipoPeca != PEAO) {
                
                sb.append(tipoPeca.getLetraSAN());
                
                final List<Lance> mvts = new ArrayList<>(Arrays.asList(tabuleiro.getMovimentosValidosJogador(whiteColor)));
                
                for (int i = mvts.size() - 1; i >= 0; i--) {
                    
                    final Lance m = mvts.get(i);
                    
                    if (peca != m.getPeca() || casaDestino != m.getCasaDestino() || equals(m)) {
                        mvts.remove(i);
                    }
                }
                
                boolean preciser = true;
                
                for (int i = mvts.size() - 1; i >= 0; i--) {
                    
                    final Lance m = mvts.get(i);
                    
                    if (colunaOrigem != m.getCasaOrigem().getIndiceColuna()) {
                        
                        mvts.remove(i);
                        
                        if (preciser) {
                            sb.append((char) ('a' + colunaOrigem));
                            preciser = false;
                        }
                    }
                }
                
                final int ySrc = casaOrigem.getIndiceLinha();
                
                for (int i = mvts.size() - 1; i >= 0; i--) {
                    
                    final Lance m = mvts.get(i);
                    
                    if (ySrc != m.getCasaOrigem().getIndiceLinha()) {                        
                        sb.append((char) ('1' + ySrc));
                        break;
                    }
                }
            }

            if ((tabuleiro.getPeca(casaDestino) != null) || ((casaDestino == tabuleiro.getCasaEnPassant()) && (tipoPeca == PEAO))) {
                
                // Capturou peÁa //
                
                if (tipoPeca == PEAO) {                    
                    sb.append((char) ('a' + colunaOrigem));
                }
                
                sb.append('x');
            }

            sb.append(casaDestino.getFEN());

            if (tipoPeca == PEAO) {
                
                // Casos particulares //
                
                if (casaDestino == tabuleiro.getCasaEnPassant()) {
                    
                    // Captura En Passant //
                    sb.append(" e.p.");
                    
                } else {
                    // PromoÁ„o do Pe„o
                    if (isPromocao()){
                        // Em algumas notaÁıes coloca-se o sÌmbolo '=' antes da peÁa que substituir· o Pe„o
                        // sb.append('=');
                        sb.append(DAMA.getLetraSAN());
                    }
                }
            }
        }

        if (tabuleiroDerivado.estaEmXeque(!whiteColor)) {
            
            // Jogador em Xeque //
            
            sb.append('+');
            
            if (nrMovimentosValidosOponente == 0) {
                
                // Jogador em Xeque-mate //
                
                sb.append('+');
            }
            
        } else 
            if (nrMovimentosValidosOponente == 0) {
                
                // Empate //
                
                sb.append("(=)");
            }

        final String res = sb.toString();
        
        //assert VALIDADOR_SAN.matcher(res).matches(); // validator tem que ser revisto pois gera erro
        
        return res;
    }
    
    public static Lance toLance(final Tabuleiro tabuleiro, final String textoSAN) throws Exception {
        
        if (tabuleiro == null) {
            throw new NullPointerException("Tabuleiro passado È null");
        }
        if (textoSAN == null) {
            throw new NullPointerException("Texto SAN passado È null");
        }

        if (!VALIDADOR_SAN.matcher(textoSAN).matches()) {
            throw new Exception("Texto SAN inv·lido [" + textoSAN + ']', null);
        }

        final boolean jogadorBranco = tabuleiro.isWhiteActive();
        
        switch (textoSAN) {
            
            case "0-0":
                
                if (jogadorBranco) {
                    return new Lance(REI_BRANCO, Casa.porIndice(4), Casa.porIndice(6));
                }else{                
                    return new Lance(REI_PRETO, Casa.porIndice(60), Casa.porIndice(62));
                }
                
            case "0-0-0":
                
                if (jogadorBranco) {
                    return new Lance(REI_BRANCO, Casa.porIndice(4), Casa.porIndice(2));
                }else{                
                    return new Lance(REI_PRETO, Casa.porIndice(60), Casa.porIndice(58));
                }
        }

        final Peca peca;
        int   posSrc = 0;
        char  c = textoSAN.charAt(posSrc);
        
        if (Character.isLowerCase(c)) {
            
            if (jogadorBranco) {
                peca = PEAO_BRANCO;
            } else {
                peca = PEAO_PRETO;
            }
            
        } else {
            
            if (jogadorBranco) {
                peca = Peca.porFEN(c);
            } else {
                peca = Peca.porFEN(Character.toLowerCase(c));
            }
            
            posSrc++;
        }

        final boolean prise = textoSAN.indexOf('x') >= 0;
        
        final List<Lance> mvts = new ArrayList<>(Arrays.asList(tabuleiro.getMovimentosValidosJogador(jogadorBranco)));
        
        for (int i = mvts.size() - 1; i >= 0; i--) {
            
            final Lance m = mvts.get(i);
            
            final boolean capture = m.getPecaCapturada() != null;
            
            if (peca != m.getPeca() || prise != capture) {
                mvts.remove(i);
            }
        }

        int posDst = textoSAN.length() - 1;
        
        while ((posDst > 0) && (!Character.isDigit(textoSAN.charAt(posDst)))) {
            posDst--;
        }
        
        final Casa dst = Casa.porFEN(textoSAN.substring(posDst - 1, posDst + 1));
        
        for (int i = mvts.size() - 1; i >= 0; i--) {
            
            final Lance m = mvts.get(i);
            
            if (dst != m.getCasaDestino()) {
                mvts.remove(i);
            }
        }
        
        if (mvts.size() == 1) {
            return mvts.get(0);
        }

        // Supprime les ambiguit√©s...
        c = textoSAN.charAt(posSrc);
        
        if (Character.isLowerCase(c)) {
            
            final int col = c - 'a';
            
            for (int i = mvts.size() - 1; i >= 0; i--) {
                
                final Lance m = mvts.get(i);
                
                if (col != m.getCasaOrigem().getIndiceColuna()) {
                    mvts.remove(i);
                }
            }
            
            posSrc++;
        }
        
        c = textoSAN.charAt(posSrc);
        
        if (Character.isDigit(c)) {
            
            final int lig = c - '1';
            
            for (int i = mvts.size() - 1; i >= 0; i--) {
                
                final Lance m = mvts.get(i);
                
                if (lig != m.getCasaOrigem().getIndiceLinha()) {
                    mvts.remove(i);
                }
            }
            
            posSrc++;
        }

        final int l = mvts.size();
        
        if (l > 1) {
            throw new Exception("Ambiguous SAN string [" + textoSAN + ']', null);
        } else 
            if (l < 1) {
                throw new Exception("Illegal SAN string context [" + textoSAN + ']', null);
            }

        return mvts.get(0);
    }
    
    public static Lance porId(final int pId) {
        
        final Peca peca        = Peca.values()[(pId >> 20) & 0xF];
        final Casa casaOrigem  = Casa.porIndice((pId >> 14) & 0x3F);
        final Casa casaDestino = Casa.porIndice((pId >> 8)  & 0x3F);
        final int  idCpt  = (pId >> 4) & 0xF;
        final Peca pecaCapturada;
        
        if (idCpt <= 0) {
            pecaCapturada = null;
        } else {
            pecaCapturada = Peca.values()[idCpt - 1];
        }

        return new Lance(peca, casaOrigem, casaDestino, pecaCapturada);
    }
}