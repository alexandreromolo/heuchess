package br.ufpr.inf.heuchess.representacao.situacaojogo;

import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Peca.*;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca.*;
import java.util.Random;

public class Tabuleiro implements Comparable<Tabuleiro> {
    
    public static final String TABULEIRO_INICIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final String TABULEIRO_MINIMO  = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";     
    public static final String TABULEIRO_VAZIO   = "8/8/8/8/8/8/8/8 w - - 0 1";
    
    public static final int  QUANTIDADE_MAXIMA_PECAS_POR_JOGADOR   = 16;
    public static final byte MAXIMO_TABULEIROS_REPETIDOS           =  3;
    public static final byte MAXIMO_SEM_CAPTURA_OU_MOVIMENTO_PEOES = 50;
        
    public static final int TOTAL_COLUNAS = 8;    
    public static final int TOTAL_LINHAS  = 8;
    public static final int TOTAL_CASAS   = TOTAL_COLUNAS * TOTAL_LINHAS;
    
    private boolean jogadorBrancoAtivo = true;
    
    private boolean possivelRoqueMenorPretas  = true;
    private boolean possivelRoqueMaiorPretas  = true;
    private boolean possivelRoqueMenorBrancas = true;
    private boolean possivelRoqueMaiorBrancas = true;
    
    private Casa casaEnPassant;
    
    private int quantidadeMovimentos;
    private int quantidadeJogadas = 1;
    
    private Casa casaReiBranco;
    private Casa casaReiPreto;
    
    private boolean jogadorPretoFezRoque;
    private boolean jogadorBrancoFezRoque;
    
    private final Peca[] pecas = new Peca[TOTAL_CASAS];
    
    private int hashCode;
    
    private Tabuleiro tabuleiroAnterior;
    
    private String  fen;
    private Lance[] movimentosValidosBranco, movimentosValidosPreto;
    private byte    quantidadeTabuleirosRepetidos;
        
    private static final Casa[] NENHUMA_CASA = new Casa[0];    
    private static final int[]  MOVIMENTOS_REI    = {-11, -10, -9, -1, 1, 9, 10, 11};    
    private static final int[]  MOVIMENTOS_CAVALO = {-21, -19, -12, -8, 8, 12, 19, 21,};
    
    /**
     * O método Zobrist de hashing é utilizado para poder identificar através de um único valor (hash)
     * a situação completa do tabuleiro (menos questões temporais, como quantidade de movimentos e tabuleiros repetidos).
     * Através desta identificação é possível utilizar as Tabelas de Transposição onde se armazena os valores dos tabuleiros
     * já calculados na busca heurística, e evita o recalculo.
     */
    private static final int     ZOBRIST_ROQUE_MAIOR_PRETAS;
    private static final int     ZOBRIST_ROQUE_MENOR_PRETAS;
    private static final int     ZOBRIST_JOGADOR_BRANCO_ATIVO;
    private static final int     ZOBRIST_ROQUE_MAIOR_BRANCAS;
    private static final int     ZOBRIST_ROQUE_MENOR_BRANCAS;
    private static final int[]   ZOBRIST_CASA_EN_PASSANT;
    private static final int[][] ZOBRIST_POSICAO_PECA;
    
    static {
        final Random rnd  = new Random(123456789L);
        final int nrPecas = Peca.values().length;
        
        ZOBRIST_POSICAO_PECA = new int[nrPecas][TOTAL_CASAS];
        
        for (int i = nrPecas; --i >= 0; ) {
            for (int j = TOTAL_CASAS; --j >= 0; ) {
                ZOBRIST_POSICAO_PECA[i][j] = rnd.nextInt();
            }
        }
        
        ZOBRIST_CASA_EN_PASSANT = new int[TOTAL_COLUNAS];
        
        for (int i = TOTAL_COLUNAS; --i >= 0; ) {
            ZOBRIST_CASA_EN_PASSANT[i] = rnd.nextInt();
        }
        
        ZOBRIST_ROQUE_MAIOR_PRETAS   = rnd.nextInt();
        ZOBRIST_ROQUE_MENOR_PRETAS   = rnd.nextInt();
        ZOBRIST_JOGADOR_BRANCO_ATIVO = rnd.nextInt();
        ZOBRIST_ROQUE_MAIOR_BRANCAS  = rnd.nextInt();
        ZOBRIST_ROQUE_MENOR_BRANCAS  = rnd.nextInt();
    }
    
    /**
     * Cria o tabuleiro inicial do Xadrez com todas as peças
     */
    public Tabuleiro() {

        for (int x = 0; x < TOTAL_COLUNAS; x++) {
            setPeca(x,1,PEAO_BRANCO);
            setPeca(x,TOTAL_LINHAS - 2,PEAO_PRETO);
        }
        
        setPeca(0,0,TORRE_BRANCA);
        setPeca(1,0,CAVALO_BRANCO);
        setPeca(2,0,BISPO_BRANCO);
        setPeca(3,0,DAMA_BRANCA);
        setPeca(TOTAL_COLUNAS - 4,0,REI_BRANCO);
        setPeca(TOTAL_COLUNAS - 3,0,BISPO_BRANCO);
        setPeca(TOTAL_COLUNAS - 2,0,CAVALO_BRANCO);
        setPeca(TOTAL_COLUNAS - 1,0,TORRE_BRANCA);
        setPeca(0,TOTAL_LINHAS - 1,TORRE_PRETA);
        setPeca(1,TOTAL_LINHAS - 1,CAVALO_PRETO);
        setPeca(2,TOTAL_LINHAS - 1,BISPO_PRETO);
        setPeca(3,TOTAL_LINHAS - 1,DAMA_PRETA);
        setPeca(TOTAL_COLUNAS - 4,TOTAL_LINHAS - 1,REI_PRETO);
        setPeca(TOTAL_COLUNAS - 3,TOTAL_LINHAS - 1,BISPO_PRETO);
        setPeca(TOTAL_COLUNAS - 2,TOTAL_LINHAS - 1,CAVALO_PRETO);
        setPeca(TOTAL_COLUNAS - 1,TOTAL_LINHAS - 1,TORRE_PRETA);
        
        casaReiPreto  = Casa.porIndices(4, 7);
        casaReiBranco = Casa.porIndices(4, 0);
        
        hashCode = hashCode();
        
        fen = TABULEIRO_INICIAL;
    }
    
    public Tabuleiro(final String textoFEN) throws Exception {
        
        if (textoFEN == null) {
            throw new NullPointerException("Texto FEN passado vale null!");
        }

        final String[] fields = textoFEN.split(" ");
        
        if (fields.length != 6) {
            throw new Exception("Texto FEN passado não é válido [" + textoFEN + ']', null);
        }
        
        ///////////
        // Peças //
        ///////////

        int rang = TOTAL_LINHAS - 1;
        int col  = 0;
        
        for (int i = 0; i < fields[0].length(); i++) {
            
            final char c = fields[0].charAt(i);
            
            if (c == '/') {
                
                if (col != TOTAL_COLUNAS || rang <= 0) {
                    throw new Exception("Identificação inválida de posição de peças para o padrão FEN [" + fields[0] + ']', null);
                }
                
                rang--;
                col = 0;
                
            } else 
                if ("12345678".indexOf(c) >= 0) {
                    
                    final int rep = c - '0';
                    
                    for (int j = 0; j < rep; j++) {                        
                        col++;
                    }
                    
                } else {
                    final Peca peca = Peca.porFEN(c);
                    
                    if (peca == null) {
                        throw new Exception("Identificação inválida de posição de peças para o padrão FEN [" + fields[0] + ']', null);
                    }
                    
                    try {
                        Casa casa = Casa.porIndices(col, rang);
                                        
                        setPeca(peca, casa);                        
                         
                        if (peca == Peca.REI_BRANCO){
                            casaReiBranco = casa;
                        }else
                            if (peca == Peca.REI_PRETO){
                                casaReiPreto = casa;    
                            }
                        
                    } catch (final IllegalArgumentException e) {
                        throw new Exception("Identificação inválida de posição de peças para o padrão FEN [" + fields[0] + ']', e);
                    }
                    
                    col++;
                }
            
            if (col > TOTAL_COLUNAS) {
                throw new Exception("Identificação inválida de posição de peças para o padrão FEN [" + fields[0] + ']', null);
            }
        }
        
        if (col != TOTAL_COLUNAS || rang != 0) {
            throw new Exception("Identificação inválida de posição de peças para o padrão FEN [" + fields[0] + ']', null);
        }
        
        ///////////////////
        // Jogador Ativo //
        ///////////////////
        
        if (fields[1].length() != 1 || "bw".indexOf(fields[1].charAt(0)) < 0) {
            throw new Exception("Identificação inválida de jogador ativo para o padrão FEN [" + fields[1] + ']', null);
        }

        jogadorBrancoAtivo = (fields[1].charAt(0) == 'w');
        
        /////////////////
        // Roqueamento //
        /////////////////
        
        int tamanho = fields[2].length();
        
        if (tamanho < 1 || tamanho > 4) {
            throw new Exception("Identificação inválida de possibilidade de Roque para o padrão FEN [" + fields[2] + ']', null);
        }

        possivelRoqueMaiorPretas  = false;
        possivelRoqueMaiorBrancas = false;
        possivelRoqueMenorPretas  = false;
        possivelRoqueMenorBrancas = false;

        if (tamanho == 1 && fields[2].charAt(0) == '-') {
            
           // Nenhum Roque Marcado //
            
        } else {
            
            for (int i = tamanho - 1; i >= 0; i--) {
                
                switch (fields[2].charAt(i)) {
                    case 'k':
                        possivelRoqueMenorPretas = true;
                        break;
                    case 'K':
                        possivelRoqueMenorBrancas = true;
                        break;
                    case 'q':
                        possivelRoqueMaiorPretas = true;
                        break;
                    case 'Q':
                        possivelRoqueMaiorBrancas = true;
                        break;
                    default:
                        throw new Exception("Identificação inválida de possibilidade de Roque para o padrão FEN [" + fields[2] + ']', null);
                }
            }
        }
        
        /////////////////////
        // Casa en Passant //
        /////////////////////
        
        tamanho = fields[3].length();
        
        if (tamanho < 1 || tamanho > 2) {
            throw new Exception("Identificação inválida de casa En Passant para o padrão FEN [" + fields[3] + ']', null);
        }

        if (tamanho != 1 || fields[3].charAt(0) != '-') {            
            try {
                casaEnPassant = validaCasaEnPassant(Casa.porFEN(fields[3]));
            } catch (final IllegalArgumentException e) {
                throw new Exception("Identificação inválida de casa En Passant para o padrão FEN [" + fields[3] + ']', e);
            }
        }
        
        //////////////////////////////
        // Quantidade de Movimentos //
        //////////////////////////////
        
        int quantidade;
        
        try {
            quantidade = Integer.parseInt(fields[4]);            
            
        } catch (final NumberFormatException e) {
            throw new Exception("Identificação de quantidade de movimentos não é um número inteiro válido [" + fields[4] + ']', e);
        }
        
        quantidadeMovimentos = validaQuantidadeMovimentos(quantidade);
        
        ///////////////////////////
        // Quantidade de Jogadas //
        ///////////////////////////
        
        try {
            quantidade = Integer.parseInt(fields[5]);
            
        } catch (final NumberFormatException e) {
            throw new Exception("Identificação de quantidade de jogadas não é um inteiro válido [" + fields[5] + ']', e);
        }
        
        quantidadeJogadas = validaQuantidadeJogadas(quantidade);
        
        ////////////////////////////////////////////
        // Realiza Validações do Tabuleiro Criado //
        ////////////////////////////////////////////
        
        validaPresencaReis();
        
        validaPossibilidadeRoqueMenor(true);
        validaPossibilidadeRoqueMenor(false);
        validaPossibilidadeRoqueMaior(true);
        validaPossibilidadeRoqueMaior(false);        
        
        validaQuantidadeTotalPecas(true,  quantidadePecasJogador(true));
        validaQuantidadeTotalPecas(false, quantidadePecasJogador(false));

        int quantPeoes = quantidadePecasJogadorDoTipo(true, TipoPeca.PEAO);

        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.PEAO,  quantidadePecasJogadorDoTipo(true, TipoPeca.PEAO));
        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.TORRE, quantidadePecasJogadorDoTipo(true, TipoPeca.TORRE));
        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.CAVALO,quantidadePecasJogadorDoTipo(true, TipoPeca.CAVALO));
        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.BISPO, quantidadePecasJogadorDoTipo(true, TipoPeca.BISPO));
        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.DAMA,  quantidadePecasJogadorDoTipo(true, TipoPeca.DAMA));
        validaQuantidadePecasDoTipo(true, quantPeoes, TipoPeca.REI,   quantidadePecasJogadorDoTipo(true, TipoPeca.REI));
        validaCorCasaBispos(true,quantPeoes);
                
        quantPeoes = quantidadePecasJogadorDoTipo(false, TipoPeca.PEAO);

        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.PEAO,  quantidadePecasJogadorDoTipo(false, TipoPeca.PEAO));
        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.TORRE, quantidadePecasJogadorDoTipo(false, TipoPeca.TORRE));
        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.CAVALO,quantidadePecasJogadorDoTipo(false, TipoPeca.CAVALO));
        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.BISPO, quantidadePecasJogadorDoTipo(false, TipoPeca.BISPO));
        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.DAMA,  quantidadePecasJogadorDoTipo(false, TipoPeca.DAMA));
        validaQuantidadePecasDoTipo(false, quantPeoes, TipoPeca.REI,   quantidadePecasJogadorDoTipo(false, TipoPeca.REI));
        validaCorCasaBispos(false,quantPeoes);
        
        validaPosicaoPeoes();
        validaPeaoEnPassant();
        
        hashCode = hashCode();
        
        fen = textoFEN;
    }
    
    private Tabuleiro(final Tabuleiro estado) {
        
        assert estado != null;

        jogadorBrancoAtivo        = estado.jogadorBrancoAtivo;
        
        possivelRoqueMenorBrancas = estado.possivelRoqueMenorBrancas;
        possivelRoqueMaiorBrancas = estado.possivelRoqueMaiorBrancas;
        possivelRoqueMenorPretas  = estado.possivelRoqueMenorPretas;
        possivelRoqueMaiorPretas  = estado.possivelRoqueMaiorPretas;
        
        casaEnPassant             = estado.casaEnPassant;
        
        quantidadeMovimentos      = estado.quantidadeMovimentos;
        quantidadeJogadas         = estado.quantidadeJogadas;

        casaReiPreto          = estado.casaReiPreto;
        jogadorPretoFezRoque  = estado.jogadorPretoFezRoque;
        casaReiBranco         = estado.casaReiBranco;
        jogadorBrancoFezRoque = estado.jogadorBrancoFezRoque;
        
        System.arraycopy(estado.pecas, 0, pecas, 0, TOTAL_CASAS);
        
        hashCode = estado.hashCode;
        
        tabuleiroAnterior = estado;
    }

    public Tabuleiro getTabuleiroAnterior(){
        return tabuleiroAnterior;
    }
    
    public boolean isWhiteActive() {
        return jogadorBrancoAtivo;
    }
    
    public boolean getPodeRoqueMenor(final boolean corJogadorBranco) {
        
        if (corJogadorBranco) {
            return possivelRoqueMenorBrancas;
        }else{
            return possivelRoqueMenorPretas;
        }
    }
    
    public boolean getPodeRoqueMaior(final boolean corJogadorBranco) {
        
        if (corJogadorBranco) {
            return possivelRoqueMaiorBrancas;
        }else{
            return possivelRoqueMaiorPretas;
        }
    }
    
    public Casa getCasaEnPassant() {
        return casaEnPassant;
    }
    
    public int getQuantidadeMovimentos() {
        
        assert quantidadeMovimentos >= 0;
        
        return quantidadeMovimentos;
    }
     
    public int getQuantidadeJogadas() {
        
        assert quantidadeJogadas > 0;
        
        return quantidadeJogadas;
    }

    public Peca getPeca(final Casa casa) {
        
        assert casa != null;

        return pecas[casa.getIndice()];
    }
    
    public Peca getPeca(final int indice) {
                
        assert (indice >= 0) && (indice < TOTAL_CASAS);
        
        return pecas[indice];
    }
    
    public Peca getPeca(final int indiceColuna, final int indiceLinha) {
        
        assert (indiceColuna >= 0) && (indiceColuna < TOTAL_COLUNAS);
        assert (indiceLinha >= 0)  && (indiceLinha < TOTAL_LINHAS);

        return pecas[indiceColuna + indiceLinha * TOTAL_COLUNAS];
    }
    
    private void setPeca(Peca peca, Casa casa){
        pecas[casa.getIndice()] = peca;
    }
    
    private void setPeca(final int coluna, final int linha, Peca peca) {
        
        assert (coluna >= 0) && (coluna < TOTAL_COLUNAS);
        assert (linha >= 0)  && (linha  < TOTAL_LINHAS);

        pecas[coluna + linha * TOTAL_COLUNAS] = peca;
    }
    
    public Casa getCasaRei(final boolean corJogadroBranco) {
        
        if (corJogadroBranco) {
            
            assert casaReiBranco != null;
            
            return casaReiBranco;
            
        }else{
            
            assert casaReiPreto != null;
        
            return casaReiPreto;
        }
    }
    
    public boolean getRoqueFeito(final boolean corJogadorBranco) {
        
        if (corJogadorBranco) {
            return jogadorBrancoFezRoque;
        }else{
            return jogadorPretoFezRoque;
        }
    }
    
    public Casa[] getMovimentosPeao(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] casasDestinos;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            addPawnTargets(casaOrigem.getIndice(), corJogadorBranco);

            casasDestinos = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                casasDestinos[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return casasDestinos;
    }
    
    public Casa[] getMovimentosTorre(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] res;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            addRookTargets(casaOrigem.getIndice(), corJogadorBranco);

            res = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                res[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return res;
    }
     
    public Casa[] getMovimentosCavalo(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] res;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            addKnightTargets(casaOrigem.getIndice(), corJogadorBranco);

            res = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                res[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return res;
    }
    
    public Casa[] getMovimentosBispo(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] res;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            addBishopTargets(casaOrigem.getIndice(), corJogadorBranco);

            res = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                res[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return res;
    }
   
    public Casa[] getMovimentosDama(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] res;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            final int idx = casaOrigem.getIndice();
            
            addBishopTargets(idx, corJogadorBranco);
            addRookTargets(idx, corJogadorBranco);

            res = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                res[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return res;
    }

    public Casa[] getMovimentosRei(final Casa casaOrigem, final boolean corJogadorBranco) {
        
        assert casaOrigem != null;

        final Casa[] res;

        synchronized (BUFFER_CASAS) {
            
            nrCasasBufferizadas = 0;

            addKingTargets(casaOrigem.getIndice(), corJogadorBranco);

            res = new Casa[nrCasasBufferizadas];
            
            for (int t = nrCasasBufferizadas; --t >= 0; ) {
                res[t] = Casa.porIndice(BUFFER_CASAS[t]);
            }
        }

        return res;
    }
    
    public Casa[] getMovimentosPeca(final Casa casaOrigem) {
        
        assert casaOrigem != null;

        final int indiceCasa = casaOrigem.getIndice();
        
        final Peca peca = pecas[indiceCasa];
        
        if (peca != null) {
            
            final Casa[] res;

            synchronized (BUFFER_CASAS) {
                
                nrCasasBufferizadas = 0;

                addAllTargets(indiceCasa);

                res = new Casa[nrCasasBufferizadas];
                
                for (int t = nrCasasBufferizadas; --t >= 0; ) {
                    res[t] = Casa.porIndice(BUFFER_CASAS[t]);
                }
            }

            return res;
        }

        return NENHUMA_CASA;
    }
    
    public Casa[] getMovimentosValidosPeca(final Casa casaOrigem) {

        assert casaOrigem != null;

        final int iSrc   = casaOrigem.getIndice();
        final Peca peca = pecas[iSrc];
        
        if (peca != null) {
            
            synchronized (BUFFER_CASAS) {
                
                nrCasasBufferizadas = 0;
                
                addAllTargets(iSrc);
                
                int nbFinal = nrCasasBufferizadas;
                
                final boolean corPecaBranca = peca.isWhite();
                
                for (int t = nrCasasBufferizadas; --t >= 0; ) {
                    
                    final int idxCible    = BUFFER_CASAS[t];
                    final Casa  cible     = Casa.porIndice(idxCible);
                    final Peca pecaPresa = pecas[idxCible];
                    
                    if (derive(new Lance(peca, casaOrigem, cible, pecaPresa), false).estaEmXeque(corPecaBranca)) {
                        
                        BUFFER_CASAS[t] = -1;
                        nbFinal--;
                        
                    } else 
                        if ((peca.getTipo() == REI) && (casaOrigem.getIndiceColuna() == 4)) {
                            
                            final int delta = 4 - cible.getIndiceColuna();
                            
                            if ((delta == 2) || (delta == -2)) {
                                
                                if (estaEmXeque(corPecaBranca) || derive(new Lance(peca, casaOrigem, Casa.porIndices(4 - (delta / 2), cible.getIndiceLinha())),false).estaEmXeque(corPecaBranca)) {
                                    BUFFER_CASAS[t] = -1;
                                    nbFinal--;
                                }
                        }
                    }
                }
                
                assert (nbFinal >= 0) && (nbFinal <= nrCasasBufferizadas);

                if (nbFinal == 0) {
                    return NENHUMA_CASA;
                }

                final Casa[] res = new Casa[nbFinal];
                for (int t = nrCasasBufferizadas; --t >= 0; ) {
                                        
                    final int idx = BUFFER_CASAS[t];
                    
                    if (idx >= 0) {
                        res[--nbFinal] = Casa.porIndice(idx);
                    }
                }

                return res;
            }
        }

        return NENHUMA_CASA;
    }
    
    public Lance[] getMovimentosValidosJogador(final boolean jogadorBranco) {
        
        if (jogadorBranco){
            if (movimentosValidosBranco != null){
                return movimentosValidosBranco;
            }
        }else{
            if (movimentosValidosPreto != null){
                return movimentosValidosPreto;
            }
        }
                
        Lance[] tmp = new Lance[45];
        
        int nb   = 0;
        int lTmp = tmp.length;
        
        for (int i = TOTAL_CASAS; --i >= 0; ) {
            
            final Peca peca = pecas[i];
            
            if ((peca != null) && (peca.isWhite() == jogadorBranco)) {
                
                final Casa casaOrigem = Casa.porIndice(i);
                
                for (final Casa casaDestino : getMovimentosValidosPeca(casaOrigem)) {
                    
                    final Peca pecaPresa;
                    
                    if (peca.getTipo() != PEAO || casaDestino != casaEnPassant) {
                        pecaPresa = pecas[casaDestino.getIndice()];
                    } else {
                        if (jogadorBranco) {
                            pecaPresa = pecas[casaDestino.getIndice() - TOTAL_COLUNAS];
                        } else {
                            pecaPresa = pecas[casaDestino.getIndice() + TOTAL_COLUNAS];
                        }
                    }
                    
                    tmp[nb++] = new Lance(peca, casaOrigem, casaDestino, pecaPresa);
                    
                    if (nb >= lTmp) {
                        
                        final Lance[] extension = new Lance[lTmp + 15];
                        
                        System.arraycopy(tmp, 0, extension, 0, lTmp);
                        
                        tmp  = extension;
                        lTmp = tmp.length;
                    }
                }
            }
        }

        final Lance[] res = new Lance[nb];
        
        System.arraycopy(tmp, 0, res, 0, nb);
        
        if (jogadorBranco){
            movimentosValidosBranco = res;
        }else{
            movimentosValidosPreto  = res;
        }
        
        return res;
    }
    
    public Tabuleiro derive(final Lance lance, final boolean alterarContagemMovimentos) {
        
        assert lance != null;

        final Tabuleiro novoEstado = new Tabuleiro(this);

        if (alterarContagemMovimentos) {
            
            final boolean proximoJogador = !jogadorBrancoAtivo;
            
            novoEstado.jogadorBrancoAtivo = proximoJogador;
            novoEstado.hashCode ^= ZOBRIST_JOGADOR_BRANCO_ATIVO;
            
            if (proximoJogador) {
                novoEstado.quantidadeJogadas = (quantidadeJogadas + 1);
            }
            
            if (lance.getPecaCapturada() == null) {
                novoEstado.quantidadeMovimentos = (quantidadeMovimentos + 1);
            } else {
                novoEstado.quantidadeMovimentos = 0;
            }
        }
        
        final Peca peca         = lance.getPeca();
        final TipoPeca tipoPeca = peca.getTipo();
        final boolean corPeca   = peca.isWhite();
        final Casa casaOrigem   = lance.getCasaOrigem();
        final int indiceOrigem  = casaOrigem.getIndice();
        final int colunaOrigem  = casaOrigem.getIndiceColuna();
        
        assert novoEstado.pecas[indiceOrigem] == peca;
        
        novoEstado.pecas[indiceOrigem] = null;
        
        final int pecaOrdinal = peca.ordinal();
        
        novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecaOrdinal][indiceOrigem];
        
        final Casa casaDestino  = lance.getCasaDestino();
        final int indiceDestino = casaDestino.getIndice();
        final int colunaDestino = casaDestino.getIndiceColuna();
        final int linhaDestino  = casaDestino.getIndiceLinha();
        
        final Peca pecaAlvo = pecas[indiceDestino];
        
        if (pecaAlvo != null) {
            novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecaAlvo.ordinal()][indiceDestino];
        }
        
        novoEstado.pecas[indiceDestino] = peca;
        novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecaOrdinal][indiceDestino];
        
        if (tipoPeca == REI) {
            
            if (corPeca) {
                novoEstado.casaReiBranco = casaDestino;
            } else {
                novoEstado.casaReiPreto  = casaDestino;
            }
            
            if (colunaOrigem == 4) {
                if (colunaDestino == 2) {
                    
                    final int i = linhaDestino * TOTAL_COLUNAS;
                    final Peca tour = novoEstado.pecas[i];
                    
                    assert tour != null;
                    assert tour.getTipo() == TORRE;
                    
                    novoEstado.pecas[i] = null;
                    
                    final int tourOrdinal = tour.ordinal();
                    
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[tourOrdinal][i];
                    novoEstado.pecas[i + 3] = tour;
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[tourOrdinal][i + 3];
                    
                    if (corPeca) {
                        novoEstado.jogadorBrancoFezRoque = true;
                    } else {
                        novoEstado.jogadorPretoFezRoque  = true;
                    }
                } else 
                    if (colunaDestino == 6) {
                    
                        final int i = TOTAL_COLUNAS - 1 + linhaDestino * TOTAL_COLUNAS;
                        final Peca tour = novoEstado.pecas[i];
                    
                        assert tour != null;
                        assert tour.getTipo() == TORRE;
                    
                        novoEstado.pecas[i] = null;
                    
                        final int tourOrdinal = tour.ordinal();
                    
                        novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[tourOrdinal][i];
                        novoEstado.pecas[i - 2] = tour;
                        novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[tourOrdinal][i - 2];                    
                    
                        if (corPeca) {
                            novoEstado.jogadorBrancoFezRoque = true;
                        } else {
                            novoEstado.jogadorPretoFezRoque  = true;
                        }
                    }
            }
        }
        
        if (getPodeRoqueMenor(corPeca)) {
            
            // Movimento tornou Roque Menor impossível //
            
            if (tipoPeca == REI || (tipoPeca == TORRE && (colunaOrigem == TOTAL_COLUNAS - 1))) {
                
                if (corPeca) {
                    novoEstado.possivelRoqueMenorBrancas = false;
                    novoEstado.hashCode ^= ZOBRIST_ROQUE_MENOR_BRANCAS;
                } else {
                    novoEstado.possivelRoqueMenorPretas  = false;
                    novoEstado.hashCode ^= ZOBRIST_ROQUE_MENOR_PRETAS;
                }
            }
        }
        
        if (getPodeRoqueMaior(corPeca)) {
            
            // Movimento tornou Roque Maior impossível //
            
            if (tipoPeca == REI || (tipoPeca == TORRE && (colunaOrigem == 0))) {
                
                if (corPeca) {
                    novoEstado.possivelRoqueMaiorBrancas = false;
                    novoEstado.hashCode ^= ZOBRIST_ROQUE_MAIOR_BRANCAS;
                } else {
                    novoEstado.possivelRoqueMaiorPretas  = false;
                    novoEstado.hashCode ^= ZOBRIST_ROQUE_MAIOR_PRETAS;
                }
            }
        }
        
        final Casa epOrig = casaEnPassant;
        novoEstado.casaEnPassant = null;
        
        if (tipoPeca == PEAO) {
            
            final int linhaOrigem = casaOrigem.getIndiceLinha();
            
            if (alterarContagemMovimentos) {
                novoEstado.quantidadeMovimentos = 0;
            }
            
            if (corPeca) {
                assert linhaDestino > linhaOrigem;
                
                if (linhaDestino == TOTAL_LINHAS - 1) {
                    novoEstado.pecas[indiceDestino] = DAMA_BRANCA;
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecaOrdinal][indiceDestino];
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[DAMA_BRANCA.ordinal()][indiceDestino];
                } else 
                    if (linhaOrigem == 1 && linhaDestino == 3) {
                        novoEstado.casaEnPassant = Casa.porIndices(colunaDestino, 2);
                    } else 
                        if (casaDestino == epOrig) {
                            final int epDst = indiceDestino - TOTAL_COLUNAS;
                            novoEstado.pecas[epDst] = null;
                            novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecas[epDst].ordinal()][epDst];
                        }
            } else {
                assert linhaDestino < linhaOrigem;
                
                if (linhaDestino == 0) {
                    novoEstado.pecas[indiceDestino] = DAMA_PRETA;
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecaOrdinal][indiceDestino];
                    novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[DAMA_PRETA.ordinal()][indiceDestino];
                } else 
                    if ((linhaOrigem == TOTAL_LINHAS - 2) && (linhaDestino == TOTAL_LINHAS - 4)) {
                        novoEstado.casaEnPassant = Casa.porIndices(colunaDestino, TOTAL_LINHAS - 3);
                    } else 
                        if (casaDestino == epOrig) {
                            final int epDst = indiceDestino + TOTAL_COLUNAS;
                            novoEstado.pecas[epDst] = null;
                            novoEstado.hashCode ^= ZOBRIST_POSICAO_PECA[pecas[epDst].ordinal()][epDst];
                        }
            }
        }

        final Casa epFinal = novoEstado.casaEnPassant;
        
        if (epOrig != null && (epFinal == null || (!epOrig.equals(epFinal)))) {
            novoEstado.hashCode ^= ZOBRIST_CASA_EN_PASSANT[epOrig.getIndiceColuna()];
        }
        if (epFinal != null && (epOrig == null || (!epFinal.equals(epOrig)))) {
            novoEstado.hashCode ^= ZOBRIST_CASA_EN_PASSANT[epFinal.getIndiceColuna()];
        }

        return novoEstado;
    }
    
    public boolean podeEmpatar50Movimentos(){
        
        if (quantidadeMovimentos >= MAXIMO_SEM_CAPTURA_OU_MOVIMENTO_PEOES){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean podeEmpatarTriplaRepeticao(){
        
        if (quantidadeTabuleirosRepetidos() >= MAXIMO_TABULEIROS_REPETIDOS){
            return true;
        }else{
            return false;
        }
    }
    
    public byte quantidadeTabuleirosRepetidos(){
    
        if (quantidadeTabuleirosRepetidos == 0) {
            
            // Calculando os Tabuleiro Repetidos pela Primeira Vez //
            
            Tabuleiro tabuleiroAvaliado = tabuleiroAnterior;

            quantidadeTabuleirosRepetidos = 1;

            while (tabuleiroAvaliado != null) {

                if (tabuleiroAvaliado.equals(this)) {
                    
                    quantidadeTabuleirosRepetidos++;
                    
                    if (quantidadeTabuleirosRepetidos >= MAXIMO_TABULEIROS_REPETIDOS) {
                        break;
                    }
                }

                tabuleiroAvaliado = tabuleiroAvaliado.tabuleiroAnterior;
            }
        }
        
        return quantidadeTabuleirosRepetidos;
    }
    
    public boolean estaEmXeque(final boolean corJogadorBranco) {
        return estaAmeacada(getCasaRei(corJogadorBranco), !corJogadorBranco);
    }
    
    public String getFEN() {
        
        if (fen != null){
            return fen;
        }
        
        final StringBuilder res = new StringBuilder();

        // Posição das peças //
        
        for (int y = TOTAL_LINHAS - 1; y >= 0; y--) {
            
            int vide = 0;
            
            for (int x = 0; x < TOTAL_COLUNAS; x++) {
                
                final Peca peca = getPeca(Casa.porIndices(x, y));
                
                if (peca == null) {
                    vide++;
                } else {
                    if (vide > 0) {
                        res.append((char) ('0' + vide));
                        vide = 0;
                    }
                    res.append(peca.getFEN());
                }
            }
            
            if (vide > 0) {
                res.append((char) ('0' + vide));
            }
            
            if (y != 0) {
                res.append('/');
            }
        }
        
        res.append(' ');

        // Cor jogador Ativo //
        
        if (jogadorBrancoAtivo) {
            res.append('w');
        } else {
            res.append('b');
        }
        
        res.append(' ');

        // Possibilidades de Roque //
        
        boolean roque = false;
        
        if (possivelRoqueMenorBrancas) {
            res.append('K');
            roque = true;
        }
        
        if (possivelRoqueMaiorBrancas) {
            res.append('Q');
            roque = true;
        }
        
        if (possivelRoqueMenorPretas) {
            res.append('k');
            roque = true;
        }
        
        if (possivelRoqueMaiorPretas) {
            res.append('q');
            roque = true;
        }
        
        if (!roque) {
            res.append('-');
        }
        
        res.append(' ');

        // Casa passível de captura En Passant //
        
        if (casaEnPassant != null) {
            res.append(casaEnPassant.getFEN());
        } else {
            res.append('-');
        }
        
        res.append(' ');
        
        // Quantidade de lances sem captura ou movimentação de peões //

        res.append(Integer.toString(quantidadeMovimentos));
        
        res.append(' ');

        // Quantidade de jogadas realizadas //
        
        res.append(Integer.toString(quantidadeJogadas));

        fen = res.toString();
        
        return fen;
    }
    
    @Override
    public final String toString() {
        return getFEN();
    }
    
    @Override
    public final int compareTo(final Tabuleiro tabuleiro) {
        
        int res = 0;
        
        for (int y = TOTAL_LINHAS; --y >= 0; ) {
            
            for (int x = TOTAL_COLUNAS; --x >= 0; ) {
                
                final Peca peca1 = getPeca(x, y);
                final Peca peca2 = tabuleiro.getPeca(x, y);
                
                if (peca1 != null || peca2 != null) {
                    
                    if (peca1 == null) {
                        return -1;
                    } else 
                        if (peca2 == null) {
                            return 1;
                        } else {
                            res = peca1.compareTo(peca2);
                            if (res != 0) {
                                return res;
                            }
                        }
                }
            }
        }

        final Casa casa = tabuleiro.casaEnPassant;
        
        if (casaEnPassant != null || casa != null) {
            
            if (casaEnPassant == null) {
                return -1;
            } else 
                if (casa == null) {
                    return 1;
                } else {
                    res = casaEnPassant.compareTo(casa);
                }
        }
        
        if (res == 0) {
            res = Boolean.valueOf(jogadorBrancoAtivo).compareTo(Boolean.valueOf(tabuleiro.jogadorBrancoAtivo));
        }
        
        if (res == 0) {
            res = Boolean.valueOf(possivelRoqueMaiorPretas).compareTo(Boolean.valueOf(tabuleiro.possivelRoqueMaiorPretas));
        }
        
        if (res == 0) {
            res = Boolean.valueOf(possivelRoqueMenorPretas).compareTo(Boolean.valueOf(tabuleiro.possivelRoqueMenorPretas));
        }
        
        if (res == 0) {
            res = Boolean.valueOf(possivelRoqueMaiorBrancas).compareTo(Boolean.valueOf(tabuleiro.possivelRoqueMaiorBrancas));
        }
        
        if (res == 0) {
            res = Boolean.valueOf(possivelRoqueMenorBrancas).compareTo(Boolean.valueOf(tabuleiro.possivelRoqueMenorBrancas));
        }

        return res;
    }
    
    @Override
    public boolean equals(final Object objeto) {
        
        if (objeto == this) {
            return true;
        }
        
        if (!(objeto instanceof Tabuleiro)) {
            return false;
        }
        
            
        if (hashCode() != objeto.hashCode()) {
            return false;
        }

        final Tabuleiro tabuleiro = (Tabuleiro) objeto;

        for (int i = 0; i < TOTAL_CASAS; i++) {
            
            if (pecas[i] != tabuleiro.pecas[i]) {
                return false;
            }
        }

        if (jogadorBrancoAtivo != tabuleiro.jogadorBrancoAtivo) {
            return false;
        } 
        if (casaEnPassant != tabuleiro.casaEnPassant) {
            return false;
        } 
        if (possivelRoqueMaiorPretas  != tabuleiro.possivelRoqueMaiorPretas) {
            return false;
        } 
        if (possivelRoqueMenorPretas  != tabuleiro.possivelRoqueMenorPretas) {
            return false;
        } 
        if (possivelRoqueMaiorBrancas != tabuleiro.possivelRoqueMaiorBrancas) {
            return false;
        } 
        if (possivelRoqueMenorBrancas != tabuleiro.possivelRoqueMenorBrancas) {
            return false;
        }
        
        return true;
    }
     
    public final int quantidadePecasJogador(boolean corJogadorBranco){
        
        int quantidade = 0;
                
        for (Peca peca : pecas){
            
            if (peca != null && peca.isWhite() == corJogadorBranco){
                quantidade++;
            } 
        }
        
        return quantidade;
    }
    
    public final int quantidadePecasJogadorDoTipo(boolean corJogadorBranco, TipoPeca tipoPeca){
        
        int quantidade = 0;
        
        for (Peca peca : pecas){
            
            if (peca != null && peca.isWhite() == corJogadorBranco && peca.getTipo() == tipoPeca){
                quantidade++;
            } 
        }
        
        return quantidade;
    }
    
    public static int quantidadeMaximaPorJogadorTipoPeca(int quantidadePeoes, TipoPeca tipoPeca) {

        if (quantidadePeoes < 0 || quantidadePeoes > 8) {
            throw new IllegalArgumentException("Quantidade Inválida de Peões");
        }

        int maximo;

        switch (tipoPeca) {

            case PEAO:
                maximo = 8;
                break;
            case REI:
                maximo = 1;
                break;
            case TORRE:
                maximo = 10 - quantidadePeoes;
                break;
            case CAVALO:
                maximo = 10 - quantidadePeoes;
                break;
            case BISPO:
                maximo = 10 - quantidadePeoes;
                break;
            case DAMA:
                maximo =  9 - quantidadePeoes;
                break;
            default:
                throw new IllegalArgumentException("Peça inválida");
        }
        
        return maximo;
    }
       
    private static int validaQuantidadeMovimentos(int quantidade) {
        
        if (quantidade < 0) {
            throw new IllegalArgumentException("Valor inválido de quantidade de Movimentos [" + quantidade + "]");
        }
        
        return quantidade;
    }
    
    private static int validaQuantidadeJogadas(int quantidade) {
        
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Valor inválido de quantidade de Jogadas [" + quantidade + "]");
        }
        
        return quantidade;
    }
   
    private void validaPresencaReis() throws Exception {
        
        if (casaReiBranco == null) {
            throw new Exception("Tabuleiro inválido pois não possui o Rei Branco!");
        }
        if (casaReiPreto == null) {
            throw new Exception("Tabuleiro inválido pois não possui o Rei Preto!");
        }
    }
    
    private void validaPossibilidadeRoqueMenor(boolean corJogadorBranco) throws Exception {
    
        if (!getPodeRoqueMenor(corJogadorBranco)){               
            return;
        }
        
        Casa casaRei;
        int  linha;
        
        if (corJogadorBranco){
            casaRei = casaReiBranco;            
            linha   = 0;            
        }else{
            casaRei = casaReiPreto;
            linha   = 7;
        }
        
        // Rei na Casa Original //
                
        if (casaRei.getIndiceColuna() != 4 || casaRei.getIndiceLinha() != linha){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "O Rei " + (corJogadorBranco ? "Branco" : "Preto") + " não está na casa inicial [" + Casa.porIndices(4,linha) + "]!");
        }
        
        // Torre na Casa Original para Roque Menor //
            
        Peca peca = getPeca(7, linha);
        
        if (peca == null || peca.getTipo() != TORRE || peca.isWhite() != corJogadorBranco){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A Torre " + (corJogadorBranco ? "Branca" : "Preta") + " a ser usada não está na casa inicial [" + Casa.porIndices(7,linha) + "]!");
        }
            
        // Casas entre Rei e Torre desocupadas //
        /*    
        if (getPieceAt(5,linha) != null) {
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(5,linha).getFEN() + "] não está desocupada!");
        }
         
        if (getPieceAt(6,linha) != null){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(6,linha).getFEN() + "] não está desocupada!");
        }
        
        // Rei não está em Xeque //
            
        if (estaEmXeque(corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "O Rei está em Xeque!");
        }
            
        // Casas usadas no movimento não estão sob ataque //
            
        if (estaAmeacada(Square.valueOf(5,linha), !corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(5,linha).getFEN() + "] está ameaçada pelo Oponente!");
        }
        
        if (estaAmeacada(Square.valueOf(6,linha), !corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Menor para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(6,linha).getFEN() + "] está ameaçada pelo Oponente!");
        }
        */ 
    }
    
    private void validaPossibilidadeRoqueMaior(boolean corJogadorBranco) throws Exception {
    
        if (!getPodeRoqueMaior(corJogadorBranco)){               
            return;
        }
        
        Casa casaRei;
        int  linha;
        
        if (corJogadorBranco){
            casaRei = casaReiBranco;            
            linha   = 0;            
        }else{
            casaRei = casaReiPreto;
            linha   = 7;
        }
        
        // Rei na Casa Original //
                
        if (casaRei.getIndiceColuna() != 4 || casaRei.getIndiceLinha() != linha){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "O Rei " + (corJogadorBranco ? "Branco" : "Preto") + " não está na casa inicial [" + Casa.porIndices(4,linha) + "]!");
        }
        
        // Torre na Casa Original para Roque Maior //
            
        Peca peca = getPeca(0, linha);
        
        if (peca == null || peca.getTipo() != TORRE || peca.isWhite() != corJogadorBranco){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A Torre " + (corJogadorBranco ? "Branca" : "Preta") + " a ser usada não está na casa inicial [" + Casa.porIndices(0,linha) + "]!");
        }
            
        // Casas entre Rei e Torre desocupadas //
        /*    
        if (getPieceAt(1,linha) != null){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(1,linha).getFEN() + "] não está desocupada!");
        }
        
        if (getPieceAt(2,linha) != null){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(2,linha).getFEN() + "] não está desocupada!");
        }
        
        if (getPieceAt(3,linha) != null){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(3,linha).getFEN() + "] não está desocupada!");
        }
            
        // Rei não está em Xeque //
            
        if (estaEmXeque(corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "O Rei está em Xeque!");
        }
            
        // Casas usadas no movimento não estão sob ataque //
            
        if (estaAmeacada(Square.valueOf(2,linha), !corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(2,linha).getFEN() + "] está ameaçada pelo Oponente!");
        }
        
        if (estaAmeacada(Square.valueOf(3,linha), !corJogadorBranco)){
            throw new Exception("Tabuleiro não permite Roque Maior para as " + (corJogadorBranco ? "Brancas\n" : "Pretas\n") + 
                                "A casa [" + Square.valueOf(3,linha).getFEN() + "] está ameaçada pelo Oponente!");
        }      
        */ 
    }
    
    private static int validaQuantidadePecasDoTipo(boolean corJogadorBranco, int quantidadePeoes, TipoPeca tipoPeca, int quantidadePeca){
        
        if (quantidadePeca > quantidadeMaximaPorJogadorTipoPeca(quantidadePeoes, tipoPeca)){
            throw new IllegalArgumentException("Quantidade inválida [" + quantidadePeca + "] de Peças do tipo " + tipoPeca + 
                                               " do jogador com as " + (corJogadorBranco ? "Brancas" : "Pretas") + "!");
        }
        
        return quantidadePeca;
    }
    
    private static int validaQuantidadeTotalPecas(boolean corJogadorBranco, int quantidade){
        
        if (quantidade > QUANTIDADE_MAXIMA_PECAS_POR_JOGADOR){
            throw new IllegalArgumentException("Cada Jogador pode ter no máximo " + QUANTIDADE_MAXIMA_PECAS_POR_JOGADOR + " peças\n" +
                                               "O jogador com as " + (corJogadorBranco ? "Brancas" : "Pretas") + " estorou este limite!");
        }
        
        return quantidade;
    }
    
    private static Casa validaCasaEnPassant(Casa casa){
        
        if (casa == null){
            throw new IllegalArgumentException("Cada definida en Passant não pode ser nula!");
        }
        if (casa.getIndiceLinha() != 2 && casa.getIndiceLinha() != 5){
            throw new IllegalArgumentException("A casa que esta en Passant deve estar na linhas 3 ou 6.\n" +
                                               "Foi passada uma que esta na linha " + casa.getIndiceLinha());
        }
        
        return casa;
    }
    
    private void validaPosicaoPeoes(){
    
        for (int coluna = 0; coluna < TOTAL_COLUNAS; coluna++){
            Peca peca = getPeca(coluna, 0);
            if (peca != null && peca.isWhite() && peca.getTipo() == PEAO){
                throw new IllegalArgumentException("Um Peão só se movimenta para frente então é impossível a existência de um\n" +
                                                   "Peão Branco na linha 1, como o da casa [" + Casa.porIndices(coluna,0) + "].");
            }
        }
        
        for (int coluna = 0; coluna < TOTAL_COLUNAS; coluna++){
            Peca peca = getPeca(coluna, 7);
            if (peca != null && !peca.isWhite() && peca.getTipo() == PEAO){
                throw new IllegalArgumentException("Um Peão só se movimenta para frente então é impossível a existência de um\n" +
                                                   "Peão Preto na linha 8, como o da casa [" + Casa.porIndices(coluna,7) + "].");
            }
        }
    }
    
    private void validaPeaoEnPassant(){
        
        Casa casaPassant = getCasaEnPassant();
        
        if (casaPassant == null){
            return;
        }
        
        int indiceLinha  = casaPassant.getIndiceLinha();
        int indiceColuna = casaPassant.getIndiceColuna();
        
        if (indiceLinha == 2){
            
            // BRANCAS //
            
            Peca peca = getPeca(indiceColuna, indiceLinha + 1);
            
            if (peca == null || peca.getTipo() != TipoPeca.PEAO || !peca.isWhite()){
                throw new IllegalArgumentException("Como está marcado a casa [" + casaPassant + "] como passível de captura En Passant.\n" +
                                                   "No Tabulerio deve existir um Peão Branco na casa " + Casa.porIndices(indiceColuna, indiceLinha + 1));
            }
        }else
            if (indiceLinha == 5){
                
                // PRETAS //
                
                Peca peca = getPeca(indiceColuna, indiceLinha - 1);
            
                if (peca == null || peca.getTipo() != TipoPeca.PEAO || peca.isWhite()){
                    throw new IllegalArgumentException("Como está marcado a casa [" + casaPassant + "] como passível de captura En Passant.\n" +
                                                       "No Tabulerio deve existir um Peão Preto na casa " + Casa.porIndices(indiceColuna, indiceLinha - 1));
                }
            }else{
                throw new IllegalArgumentException("Valor de linha de Casa en Passant inválido [" + indiceLinha + "]");
            }
    }
    
    private void validaCorCasaBispos(boolean corJogadorBranco, int quantidadePeoes) {
        
        if (quantidadePeoes < 8){
            // Não tem como validar pois pode ter ocorrido promoções de Bispos
            return;
        }
        
        int quantBisposCasaPreta  = 0;
        int quantBisposCasaBranca = 0;
        
        for (int indice = 0; indice < pecas.length; indice++){
            
            Peca peca = pecas[indice];
            
            if (peca != null && peca.isWhite() == corJogadorBranco && peca.getTipo() == BISPO){
                
                Casa casa = Casa.porIndice(indice);
                
                if (casa.getCor() == DHJOG.Cor.BRANCAS){
                    quantBisposCasaBranca++;
                }else{
                    quantBisposCasaPreta++;
                }
                
                if (quantBisposCasaBranca > 1){
                    throw new IllegalArgumentException("Como todos os Peões " + (corJogadorBranco ? "Brancos" : "Pretos") + " estão no Tabuleiro, não ocorreu nenhuma Promoção de Peça.\n" +
                                                       "Então é impossível para o jogador " + (corJogadorBranco ? "Branco" : "Preto") + " possuir mais de um Bispo em uma casa de Cor Branca.\n" +
                                                       "Como o Bispo que está em [" + casa + "].");
                }
                
                if (quantBisposCasaPreta > 1){
                    throw new IllegalArgumentException("Como todos os Peões " + (corJogadorBranco ? "Brancos" : "Pretos") + " estão no Tabuleiro, não ocorreu nenhuma Promoção de Peça.\n" +
                                                       "Então é impossível para o jogador " + (corJogadorBranco ? "Branco" : "Preto") + " possuir mais de um Bispo em uma casa de Cor Preta.\n" +
                                                       "Como o Bispo que está em [" + casa + "].");
                }
            }
        }
    }
    
    @Override
    public final int hashCode() {
        
        int res = 0;

        if (possivelRoqueMaiorPretas) {
            res ^= ZOBRIST_ROQUE_MAIOR_PRETAS;
        }
        if (possivelRoqueMenorPretas) {
            res ^= ZOBRIST_ROQUE_MENOR_PRETAS;
        }
        if (casaEnPassant != null) {
            res ^= ZOBRIST_CASA_EN_PASSANT[casaEnPassant.getIndiceColuna()];
        }
        if (jogadorBrancoAtivo) {
            res ^= ZOBRIST_JOGADOR_BRANCO_ATIVO;
        }
        if (possivelRoqueMaiorBrancas) {
            res ^= ZOBRIST_ROQUE_MAIOR_BRANCAS;
        }
        if (possivelRoqueMenorBrancas) {
            res ^= ZOBRIST_ROQUE_MENOR_BRANCAS;
        }
        
        for (int indice = 0; indice < pecas.length; indice++){
            
            final Peca peca = getPeca(indice);
            
            if (peca != null) {
                res ^= ZOBRIST_POSICAO_PECA[peca.ordinal()][indice];
            }
        }        

        return res;
    }
    
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    
    /**
     * Mailbox pour tester les dÃ©placements.
     */
    private static final int[] MAILBOX = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1,
        2, 3, 4, 5, 6, 7, -1, -1, 8, 9, 10, 11, 12, 13, 14, 15, -1, -1, 16, 17, 18, 19, 20, 21, 22,
        23, -1, -1, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, 32, 33, 34, 35, 36, 37, 38, 39, -1, -1,
        40, 41, 42, 43, 44, 45, 46, 47, -1, -1, 48, 49, 50, 51, 52, 53, 54, 55, -1, -1, 56, 57, 58,
        59, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1,};
    /**
     * Equivalences entre le plateau et la mailbox.
     */
    private static final int[] TO_MAILBOX = {21, 22, 23, 24, 25, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 41, 42, 43, 44, 45, 46, 47,
        48, 51, 52, 53, 54, 55, 56, 57, 58, 61, 62, 63, 64, 65, 66, 67, 68, 71, 72, 73, 74, 75, 76,
        77, 78, 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 96, 97, 98,};
       
    /**
     * Buffer de travail pour optimiser la recherche des cibles de mouvements.
     * <p> L'utilisation d'un buffer statique optimise les performances mais
     * nÃ©cessite de faire trÃ¨s attention Ã  la synchronisation pour que la
     * classe reste sÃ»re vis-Ã -vis des threads. </p>
     */
    // 27 est le nombre maximum de cases cibles pour une piÃ¨ce (une dame, dans le meilleur des cas).
    private static final int[] BUFFER_CASAS = new int[27];
    /**
     * Indice du dernier Ã©lÃ©ment valide dans les buffer de travail des cases.
     */
    private static int nrCasasBufferizadas;
   
    /**
     * Ajoute au buffer interne toutes les cases cibles des mouvements possibles
     * (y compris ceux mettant le roi en Ã©chec) pour la piÃ¨ce contenue par une
     * case.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     */
    private void addAllTargets(final int pOrigine) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final Peca peca = pecas[pOrigine];
        if (peca != null) {
            final boolean trait = peca.isWhite();
            switch (peca.getTipo()) {
                case BISPO:
                    addBishopTargets(pOrigine, trait);
                    break;
                case REI:
                    addKingTargets(pOrigine, trait);
                    break;
                case CAVALO:
                    addKnightTargets(pOrigine, trait);
                    break;
                case PEAO:
                    addPawnTargets(pOrigine, trait);
                    break;
                case DAMA:
                    addBishopTargets(pOrigine, trait);
                    addRookTargets(pOrigine, trait);
                    break;
                case TORRE:
                    addRookTargets(pOrigine, trait);
                    break;
                default:
                    assert false;
            }
        }
    }

    /**
     * Ajoute au buffer interne toutes les cases cibles possibles d'un mouvement
     * de type "fou" d'une certaine couleur (y compris ceux mettant le roi en
     * Ã©chec) Ã  partir d'une case.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     * @param pBlanc PositionnÃ© Ã  vrai si la recherche concerne les blancs.
     */
    private void addBishopTargets(final int pOrigine, final boolean pBlanc) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final int mbSrc = TO_MAILBOX[pOrigine];

        // Mouvements / prise vers le haut/gauche...
        int mbDst = mbSrc + 9;
        int dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst += 9;
            dst = MAILBOX[mbDst];
        }

        // Mouvements / prise vers le haut/droit...
        mbDst = mbSrc + 11;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst += 11;
            dst = MAILBOX[mbDst];
        }

        // Mouvements / prise vers le bas/gauche...
        mbDst = mbSrc - 11;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst -= 11;
            dst = MAILBOX[mbDst];
        }

        // Mouvements / prise vers le bas/droit...
        mbDst = mbSrc - 9;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst -= 9;
            dst = MAILBOX[mbDst];
        }
    }

    /**
     * Ajoute au buffer interne la liste des cases pouvant Ãªtre atteintes par
     * un mouvement de type roi.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     * @param pBlanc A vrai pour indiquer une recherche sur les blancs.
     */
    private void addKingTargets(final int pOrigine, final boolean pBlanc) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final int mbSrc = TO_MAILBOX[pOrigine];
        boolean testerRoque = false;
        for (final int km : MOVIMENTOS_REI) {
            final int dst = MAILBOX[mbSrc + km];
            if (dst >= 0) {
                final Peca p = pecas[dst];
                if ((p == null) || (p.isWhite() != pBlanc)) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                    testerRoque = true;
                }
            }
        }
        if (testerRoque && (Casa.porIndice(pOrigine).getIndiceColuna() == 4)) {
            final int dst = MAILBOX[mbSrc];
            if (getPodeRoqueMenor(pBlanc) && (pecas[dst + 1] == null) && (pecas[dst + 2] == null)) {
                final Peca t = pecas[dst + 3];
                if ((t != null) && (t.getTipo() == TORRE) && (t.isWhite() == pBlanc)) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst + 2;
                }
            }
            if (getPodeRoqueMaior(pBlanc) && (pecas[dst - 1] == null) && (pecas[dst - 2] == null)
                    && (pecas[dst - 3] == null)) {
                final Peca t = pecas[dst - 4];
                if ((t != null) && (t.getTipo() == TORRE) && (t.isWhite() == pBlanc)) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst - 2;
                }
            }
        }
    }

    /**
     * Ajoute au buffer interne la liste des cases pouvant Ãªtre atteintes par
     * un mouvement de type cavalier.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     * @param pBlanc A vrai pour indiquer une recherche sur les blancs.
     */
    private void addKnightTargets(final int pOrigine, final boolean pBlanc) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final int mbSrc = TO_MAILBOX[pOrigine];
        for (final int km : MOVIMENTOS_CAVALO) {
            final int dst = MAILBOX[mbSrc + km];
            if (dst >= 0) {
                final Peca p = pecas[dst];
                if ((p == null) || (p.isWhite() != pBlanc)) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
            }
        }
    }

    /**
     * Ajoute au buffer interne la liste des cases pouvant Ãªtre atteintes par
     * un mouvement de type pion.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     * @param pBlanc A vrai pour indiquer une recherche sur les blancs.
     */
    private void addPawnTargets(final int pOrigine, final boolean pBlanc) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final Casa cSrc = Casa.porIndice(pOrigine);
        final int ySrc = cSrc.getIndiceLinha();
        if (pBlanc) {
            if (ySrc < TOTAL_LINHAS - 1) {
                // Mouvement de 1...
                if (pecas[pOrigine + TOTAL_COLUNAS] == null) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = pOrigine + TOTAL_COLUNAS;
                    // Mouvement initial de 2
                    if ((ySrc == 1) && (pecas[pOrigine + TOTAL_COLUNAS * 2] == null)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = pOrigine + TOTAL_COLUNAS * 2;
                    }
                }
                final int xSrc = cSrc.getIndiceColuna();
                if (xSrc > 0) {
                    // Prise Ã  gauche (y compris en passant)...
                    final int iDest = pOrigine - 1 + TOTAL_COLUNAS;
                    final Peca pDest = pecas[iDest];
                    if (((pDest != null) && (!pDest.isWhite())) || (Casa.porIndice(iDest) == casaEnPassant)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = iDest;
                    }
                }
                if (xSrc < TOTAL_COLUNAS - 1) {
                    // Prise Ã  droite (y compris en passant)...
                    final int iDest = pOrigine + 1 + TOTAL_COLUNAS;
                    final Peca pDest = pecas[iDest];
                    if (((pDest != null) && (!pDest.isWhite())) || (Casa.porIndice(iDest) == casaEnPassant)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = iDest;
                    }
                }
            }
        } else {
            if (ySrc > 0) {
                // Mouvement de 1...
                if (pecas[pOrigine - TOTAL_COLUNAS] == null) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = pOrigine - TOTAL_COLUNAS;
                    // Mouvement initial de 2
                    if ((ySrc == TOTAL_LINHAS - 2) && (pecas[pOrigine - TOTAL_COLUNAS * 2] == null)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = pOrigine - TOTAL_COLUNAS * 2;
                    }
                }
                final int xSrc = cSrc.getIndiceColuna();
                if (xSrc > 0) {
                    // Prise Ã  gauche (y compris en passant)...
                    final int iDest = pOrigine - 1 - TOTAL_COLUNAS;
                    final Peca pDest = pecas[iDest];
                    if (((pDest != null) && pDest.isWhite()) || (Casa.porIndice(iDest) == casaEnPassant)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = iDest;
                    }
                }
                if (xSrc < TOTAL_COLUNAS - 1) {
                    // Prise Ã  droite (y compris en passant)...
                    final int iDest = pOrigine + 1 - TOTAL_COLUNAS;
                    final Peca pDest = pecas[iDest];
                    if (((pDest != null) && pDest.isWhite()) || (Casa.porIndice(iDest) == casaEnPassant)) {
                        BUFFER_CASAS[nrCasasBufferizadas++] = iDest;
                    }
                }
            }
        }
    }

    /**
     * Ajoute au buffer interne toutes les cases cibles possibles d'un mouvement
     * de type "tour" d'une certaine couleur (y compris ceux mettant le roi en
     * Ã©chec) Ã  partir d'une case.
     *
     * @param pOrigine Indice de la case Ã  l'origine du mouvement.
     * @param pBlanc Mis Ã  vrai pour rechercher pour les blancs.
     */
    private void addRookTargets(final int pOrigine, final boolean pBlanc) {
        
        assert (pOrigine >= 0) && (pOrigine < TOTAL_CASAS);

        final int mbSrc = TO_MAILBOX[pOrigine];

        // Mouvements / prise vers la gauche...
        int mbDst = mbSrc - 1;
        int dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            dst = MAILBOX[--mbDst];
        }

        // Mouvements / prise vers la droite...
        mbDst = mbSrc + 1;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            dst = MAILBOX[++mbDst];
        }

        // Mouvements / prise vers le haut...
        mbDst = mbSrc + 10;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst += 10;
            dst = MAILBOX[mbDst];
        }

        // Mouvements / prise vers le bas...
        mbDst = mbSrc - 10;
        dst = MAILBOX[mbDst];
        while (dst >= 0) {
            final Peca p = pecas[dst];
            if (p == null) {
                BUFFER_CASAS[nrCasasBufferizadas++] = dst;
            } else {
                if (p.isWhite() != pBlanc) {
                    BUFFER_CASAS[nrCasasBufferizadas++] = dst;
                }
                break;
            }
            mbDst -= 10;
            dst = MAILBOX[mbDst];
        }
    }

    public boolean estaAmeacada(final Casa casa, final boolean corJogadorBranco) {
        
        assert casa != null;

        final int mbSrc = TO_MAILBOX[casa.getIndice()];

        Peca p = null;
        int mbDst = mbSrc - 1;
        int dst = MAILBOX[mbDst];
        // Gauche
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            dst = MAILBOX[--mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == TORRE) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc + 1;
        dst = MAILBOX[mbDst];
        // Droite
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            dst = MAILBOX[++mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == TORRE) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc - 10;
        dst = MAILBOX[mbDst];
        // Bas
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst -= 10;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == TORRE) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc + 10;
        dst = MAILBOX[mbDst];
        // Haut
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst += 10;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == TORRE) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc - 11;
        dst = MAILBOX[mbDst];
        // Bas / Gauche
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst -= 11;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == BISPO) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc + 9;
        dst = MAILBOX[mbDst];
        // Haut / Gauche
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst += 9;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == BISPO) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc + 11;
        dst = MAILBOX[mbDst];
        // Haut / Droit
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst += 11;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == BISPO) || (t == DAMA)) {
                return true;
            }
        }

        p = null;
        mbDst = mbSrc - 9;
        dst = MAILBOX[mbDst];
        // Bas / Droit
        while ((dst >= 0) && (p == null)) {
            p = pecas[dst];
            mbDst -= 9;
            dst = MAILBOX[mbDst];
        }
        if ((p != null) && (p.isWhite() == corJogadorBranco)) {
            final TipoPeca t = p.getTipo();
            if ((t == BISPO) || (t == DAMA)) {
                return true;
            }
        }

        // Cavalier
        for (final int km : MOVIMENTOS_CAVALO) {
            dst = MAILBOX[mbSrc + km];
            if (dst >= 0) {
                p = pecas[dst];
                if ((p != null) && (p.isWhite() == corJogadorBranco) && (p.getTipo() == CAVALO)) {
                    return true;
                }
            }
        }

        // Roi
        for (final int km : MOVIMENTOS_REI) {
            dst = MAILBOX[mbSrc + km];
            if (dst >= 0) {
                p = pecas[dst];
                if ((p != null) && (p.isWhite() == corJogadorBranco) && (p.getTipo() == REI)) {
                    return true;
                }
            }
        }

        // Pions...
        if (corJogadorBranco) {
            final int ySrc = casa.getIndiceLinha();
            if (ySrc > 1) {
                final int xSrc = casa.getIndiceColuna();
                if (((xSrc > 0) && (pecas[MAILBOX[mbSrc - 11]] == PEAO_BRANCO))
                        || ((xSrc < TOTAL_COLUNAS - 1) && (pecas[MAILBOX[mbSrc - 9]] == PEAO_BRANCO))) {
                    return true;
                }
            }
        } else {
            final int ySrc = casa.getIndiceLinha();
            if (ySrc < TOTAL_LINHAS - 2) {
                final int xSrc = casa.getIndiceColuna();
                if (((xSrc > 0) && (pecas[MAILBOX[mbSrc + 9]] == PEAO_PRETO))
                        || ((xSrc < TOTAL_COLUNAS - 1) && (pecas[MAILBOX[mbSrc + 11]] == PEAO_PRETO))) {
                    return true;
                }
            }
        }

        return false;
    }
}