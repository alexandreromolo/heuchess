package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 14, 2012
 */
public class DHJOG {
    
    public static final double XEQUE_MATE_EU       =  Double.POSITIVE_INFINITY;
    public static final double XEQUE_MATE_OPONENTE =  Double.NEGATIVE_INFINITY;
    public static final double EMPATE              = -Double.MAX_VALUE; // Valor negativo enorme mais ainda maior do que -Infinito
            
    private static final String TXT_E                 = "E";
    private static final String TXT_OU                = "OU";
    private static final String TXT_NAO               = "NAO";
    private static final String TXT_IGUAL             = "IGUAL";                
    private static final String TXT_DIFERENTE         = "DIFERENTE";
    private static final String TXT_CONTEM            = "CONTEM";
    private static final String TXT_CONTIDO           = "CONTIDO";
    private static final String TXT_LOGICO            = "LOGICO";
    private static final String TXT_JOGADOR           = "JOGADOR";
    private static final String TXT_INTEIRO           = "INTEIRO";
    private static final String TXT_REAL              = "REAL";
    private static final String TXT_CASAS             = "CASA[]";
    private static final String TXT_TIPO_PECAS        = "TIPO_PECA[]";
    private static final String TXT_PECAS             = "PECA[]";
    private static final String TXT_VERDADEIRO        = "VERDADEIRO";
    private static final String TXT_FALSO             = "FALSO";
    private static final String TXT_EU                = "EU";
    private static final String TXT_OPONENTE          = "OPONENTE";    
    private static final String TXT_MEU               = "MEU";
    private static final String TXT_MINHA             = "MINHA";
    private static final String TXT_PEAO              = "PEAO";
    private static final String TXT_TORRE             = "TORRE";
    private static final String TXT_CAVALO            = "CAVALO";
    private static final String TXT_BISPO             = "BISPO";    
    private static final String TXT_DAMA              = "DAMA";
    private static final String TXT_REI               = "REI";
    private static final String TXT_BRANCA            = "BRANCA";
    private static final String TXT_PRETA             = "PRETA";
    private static final String TXT_VALOR             = "VALOR";    
    private static final String TXT_OPERADOR_ATRIBUTO = ".";
    private static final String TXT_PEAO_MEU          = TXT_PEAO   + TXT_OPERADOR_ATRIBUTO + TXT_MEU; 
    private static final String TXT_TORRE_MINHA       = TXT_TORRE  + TXT_OPERADOR_ATRIBUTO + TXT_MINHA;
    private static final String TXT_CAVALO_MEU        = TXT_CAVALO + TXT_OPERADOR_ATRIBUTO + TXT_MEU;
    private static final String TXT_BISPO_MEU         = TXT_BISPO  + TXT_OPERADOR_ATRIBUTO + TXT_MEU;
    private static final String TXT_DAMA_MINHA        = TXT_DAMA   + TXT_OPERADOR_ATRIBUTO + TXT_MINHA;
    private static final String TXT_REI_MEU           = TXT_REI    + TXT_OPERADOR_ATRIBUTO + TXT_MEU;
    private static final String TXT_PEAO_OPONENTE     = TXT_PEAO   + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;
    private static final String TXT_TORRE_OPONENTE    = TXT_TORRE  + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;
    private static final String TXT_CAVALO_OPONENTE   = TXT_CAVALO + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;
    private static final String TXT_BISPO_OPONENTE    = TXT_BISPO  + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;
    private static final String TXT_DAMA_OPONENTE     = TXT_DAMA   + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;
    private static final String TXT_REI_OPONENTE      = TXT_REI    + TXT_OPERADOR_ATRIBUTO + TXT_OPONENTE;

    public static final String DELIMITADORES = " {},()<>=!-+*/\"\n\t";
    
    public static final String TODO_TABULEIRO          = "TODO_TABULEIRO";    
    public static final String TXT_SE                  = "SE";
    public static final String TXT_ENTAO               = "ENTAO";
    public static final String TXT_FIM                 = "FIM";
    public static final String TXT_HEURISTICA          = "HEURISTICA";
    public static final String TXT_VALOR_PECA          = "VALOR_PECA";
    public static final String TXT_TRANSICAO_ETAPA     = "TRANSICAO_ETAPA";
    public static final String TXT_VALOR_TABULEIRO     = "VALOR_TABULEIRO";
    public static final String TXT_TABULEIRO           = "TABULEIRO";
    public static final String TXT_ETAPA_ATUAL         = "ETAPA_ATUAL";
    public static final String TXT_ETAPA_INICIAL       = "ETAPA_INICIAL";
    public static final String TXT_SITUACAO_JOGO       = "SITUACAO_JOGO";
    public static final String TXT_VANTAGEM            = "VANTAGEM";
    public static final String TXT_FEN                 = "FEN";
    public static final String TXT_FUNCAO              = "FUNCAO";
    public static final String TXT_DESCRICAO           = "DESCRICAO";
    public static final String TXT_RETORNA             = "RETORNA";
    public static final String TXT_PARAMETROS          = "PARAMETROS";
    public static final String TXT_COMANDOS            = "COMANDOS";
    public static final String TXT_ANOTACAO            = "ANOTACAO";
    public static final String TXT_TIPO                = "TIPO";
    public static final String TXT_TEXTO               = "TEXTO";
    public static final String TXT_ETAPA               = "ETAPA";
    public static final String TXT_CONJUNTO_HEURISTICO = "CONJUNTO_HEURISTICO";
    public static final String TXT_NIVEL_COMPLEXIDADE  = "NIVEL_COMPLEXIDADE";
    public static final String TXT_OPERADOR_ATRIBUICAO = "<-"; 
    public static final String TXT_PEAO_VALOR          = TXT_PEAO   + TXT_OPERADOR_ATRIBUTO + TXT_VALOR;
    public static final String TXT_TORRE_VALOR         = TXT_TORRE  + TXT_OPERADOR_ATRIBUTO + TXT_VALOR;
    public static final String TXT_CAVALO_VALOR        = TXT_CAVALO + TXT_OPERADOR_ATRIBUTO + TXT_VALOR;
    public static final String TXT_BISPO_VALOR         = TXT_BISPO  + TXT_OPERADOR_ATRIBUTO + TXT_VALOR;
    public static final String TXT_DAMA_VALOR          = TXT_DAMA   + TXT_OPERADOR_ATRIBUTO + TXT_VALOR;
    
    public static final String[] PALAVRAS_RESERVADAS = {TXT_PEAO,TXT_TORRE,TXT_CAVALO,TXT_BISPO,TXT_DAMA,TXT_REI, // Usado pela classe TipoPeca
                                                        TXT_E,TXT_OU,TXT_NAO,TXT_IGUAL,TXT_DIFERENTE,TXT_CONTEM,TXT_CONTIDO,
                                                        TXT_LOGICO,TXT_JOGADOR,TXT_INTEIRO,TXT_REAL,TXT_CASAS,TXT_TIPO_PECAS,TXT_PECAS,
                                                        TXT_VERDADEIRO,TXT_FALSO,
                                                        TXT_EU,TXT_OPONENTE, TXT_MEU, TXT_MINHA, 
                                                        TXT_BRANCA,TXT_PRETA,                                                                                                                
                                                        TXT_PEAO_VALOR, TXT_TORRE_VALOR, TXT_CAVALO_VALOR, TXT_BISPO_VALOR, TXT_DAMA_VALOR,
                                                        TXT_PEAO_MEU,TXT_TORRE_MINHA,TXT_CAVALO_MEU,TXT_BISPO_MEU,TXT_DAMA_MINHA,TXT_REI_MEU,
                                                        TXT_PEAO_OPONENTE,TXT_TORRE_OPONENTE,TXT_CAVALO_OPONENTE,TXT_BISPO_OPONENTE,TXT_DAMA_OPONENTE,TXT_REI_OPONENTE,
                                                        TODO_TABULEIRO,
                                                        TXT_SITUACAO_JOGO, TXT_TABULEIRO, TXT_ETAPA_INICIAL, TXT_ETAPA_ATUAL,                                               
                                                        TXT_TEXTO, "POSICAO", "PECA", "TIPO_PECA", "CASA",
                                                        "NINGUEM",
                                                        "VAZIO",                                                        
                                                        "CONJUNTO", "COMPONENTE", "COMPONENTE_HEURISTICO", TXT_ETAPA, TXT_CONJUNTO_HEURISTICO, "DEFINICAO_GLOBAL", TXT_ANOTACAO, TXT_HEURISTICA,
                                                        TXT_TRANSICAO_ETAPA, TXT_VALOR_PECA, TXT_VALOR_TABULEIRO,                                                
                                                        TXT_VANTAGEM, TXT_FEN, TXT_VALOR,                                                         
                                                        TXT_RETORNA, TXT_FUNCAO, "AUTOR", "VERSAO", TXT_DESCRICAO, TXT_PARAMETROS, TXT_COMANDOS,
                                                        TXT_SE, TXT_ENTAO, TXT_FIM, "PARA", "DE", "ATE", "FACA", 
                                                        TXT_TIPO, TXT_NIVEL_COMPLEXIDADE,                                                
                                                        "EXPLICACAO", "QUESTAO", "POSITIVA", "NEGATIVA", "NORMAL",
                                                        "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8",
                                                        "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8",
                                                        "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8",
                                                        "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8",
                                                        "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8",
                                                        "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8",
                                                        "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8",
                                                        "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8"};
    
    public static final char[] CARACTERES_VALIDOS = {'_',
                                                     '0','1','2','3','4','5','6','7','8','9',                                                    
                                                     'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','x','w','y','z',
                                                     'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','X','W','Y','Z'};
    
    

    
    public enum Cor {
    
        BRANCAS(TXT_BRANCA,"Branco","Branca"),
        PRETAS(TXT_PRETA,  "Preto", "Preta");
        
        private final String textoDHJOG;
        private final String textoMasculino;
        private final String textoFeminino;
    
        private Cor(String textoDHJOG, String textoMasculino, String textoFeminino){
            this.textoDHJOG     = textoDHJOG;
            this.textoMasculino = textoMasculino;
            this.textoFeminino  = textoFeminino;
        }
        
        public String toFeminino(){
            return textoFeminino;
        }
        
        public String toMasculino(){
            return textoMasculino;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public enum OperadorLogico {
    
        E(TXT_E,"&&"),
        OU(TXT_OU,"||"),
        NAO(TXT_NAO,"!");
        
        private final String textoDHJOG;
        private final String textoJava;
    
        private OperadorLogico(String textoDHJOG, String textoJava){
            this.textoDHJOG = textoDHJOG;
            this.textoJava  = textoJava;
        }
        
        public String toJava(){
            return textoJava;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public enum OperadorRelacional {
    
        MAIOR(">",">","Retorna verdadeiro caso o primeiro operando seja maior que o segundo."),
        MAIOR_IGUAL(">=",">=","Retorna verdadeiro caso o primeiro operando seja maior ou igual ao segundo."),
        MENOR("<","<","Retorna verdadeiro caso o segundo operando seja menor que o primeiro."),
        MENOR_IGUAL("<=","<=","Retorna verdadeiro caso o segundo operando seja maior ou igual ao primeiro."),
        IGUAL(TXT_IGUAL,"==","Retorna verdadeiro apenas se o primeiro e o segundo operandos forem idênticos."),
        DIFERENTE(TXT_DIFERENTE,"!=","Retorna verdadeiro caso os dois operandos sejam diferentes."),
        CONTEM(TXT_CONTEM,null,"Retorna verdadeiro caso o conjunto que é o primeiro operando conter todos os elementos do conjunto que é o segundo operando."),
        CONTIDO(TXT_CONTIDO,null,"Retorna verdadeiro caso todos os elementos do conjunto que é o primeiro operando também fazerem parte do conjunto que é o segundo operando.");
        
        private final String textoDHJOG;        
        private final String textoJava;
        private final String explicacao;
    
        private OperadorRelacional(String textoDHJOG, String textoJava, String explicacao){
            this.textoDHJOG = textoDHJOG;
            this.textoJava  = textoJava;
            this.explicacao = explicacao;
        }
        
        public String explicacao(){
            return explicacao;
        }
        
        public String toJava(){
            return textoJava;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public static DHJOG.OperadorRelacional converteTextoOperadorRelacional(String texto){
    
        switch(texto){
            case ">":
                return OperadorRelacional.MAIOR;
                
            case ">=":
                return OperadorRelacional.MAIOR_IGUAL;
                
            case "<":
                return OperadorRelacional.MENOR;
                
            case "<=":
                return OperadorRelacional.MENOR_IGUAL;
                
            case TXT_IGUAL:
                return OperadorRelacional.IGUAL;
                
            case TXT_DIFERENTE:
                return OperadorRelacional.DIFERENTE;
                
            case TXT_CONTEM:
                return OperadorRelacional.CONTEM;
                
            case TXT_CONTIDO:
                return OperadorRelacional.CONTIDO;    
                
            default:
                throw new IllegalArgumentException("Operador Relacional Desconhecido da DHJOG [" + texto + "]");
        }
    }
    
    public enum OperadorMatematico {
    
        MAIS('+'),
        MENOS('-'),
        MULTIPLICACAO('*'),
        DIVISAO('/');      
                
        private final char simboloDHJOG;
            
        private OperadorMatematico(char simboloDHJOG){
            this.simboloDHJOG = simboloDHJOG;            
        }
        
        public char toChar(){
            return simboloDHJOG;
        }
        
        @Override
        public String toString(){
            return Character.toString(simboloDHJOG);
        }
    }
    
    public static DHJOG.OperadorMatematico converteTextoOperadorMatematico(String texto){
    
        switch (texto) {
            case "+":
                return OperadorMatematico.MAIS;
                
            case "-":
                return OperadorMatematico.MENOS;
                
            case "*":
                return OperadorMatematico.MULTIPLICACAO;
                
            case "/":
                return OperadorMatematico.DIVISAO;
                
            default:
                throw new IllegalArgumentException("Operador Matemático Desconhecido da DHJOG [" + texto + "]");
        }
    }
    
    public enum TipoDado {
        
        LOGICO(TXT_LOGICO),
        JOGADOR(TXT_JOGADOR),
        INTEIRO(TXT_INTEIRO),
        REAL(TXT_REAL),
        CASAS(TXT_CASAS),
        TIPO_PECAS(TXT_TIPO_PECAS),
        PECAS(TXT_PECAS);
                
        private final String textoDHJOG;
    
        private TipoDado(String textoDHJOG){
            this.textoDHJOG = textoDHJOG;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public static DHJOG.TipoDado converteTextoTipoDado(String texto){
    
        switch (texto) {
                
            case TXT_LOGICO:
                return TipoDado.LOGICO;
                
            case TXT_INTEIRO:
                return TipoDado.INTEIRO;
                
            case TXT_REAL:
                return TipoDado.REAL;
                
            case TXT_JOGADOR:
                return TipoDado.JOGADOR;    
                
            case TXT_CASAS:
                return TipoDado.CASAS;    
                
            case TXT_TIPO_PECAS:
                return TipoDado.TIPO_PECAS;    
                
            case TXT_PECAS:
                return TipoDado.PECAS;        
                
            default:
                throw new IllegalArgumentException("Tipo de Dado Desconhecido da DHJOG [" + texto + "]");
        }
    }
    
    public enum VALOR_LOGICO {
        
        VERDADEIRO(TXT_VERDADEIRO,"true"),
        FALSO(TXT_FALSO,"false");
        
        private final String textoDHJOG;
        private final String textoJava;
    
        private VALOR_LOGICO(String textoDHJOG, String textoJava){
            this.textoDHJOG = textoDHJOG;
            this.textoJava  = textoJava;
        }
        
        public String toJava(){
            return textoJava;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public static DHJOG.VALOR_LOGICO converteTextoValorLogico(final String texto) {

        switch (texto) {

            case TXT_VERDADEIRO:
                return VALOR_LOGICO.VERDADEIRO;

            case TXT_FALSO:
                return VALOR_LOGICO.FALSO;

            default:
                throw new IllegalArgumentException("Texto inválido de Valor Lógico da DHJOG [" + texto + "]");
        }
    }
    
    public enum VALOR_JOGADOR {
        
        EU(TXT_EU),
        OPONENTE(TXT_OPONENTE);
        
        private final String textoDHJOG;
    
        private VALOR_JOGADOR(String textoDHJOG){
            this.textoDHJOG = textoDHJOG;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
    }
    
    public static DHJOG.VALOR_JOGADOR converteTextoValorJogador(String texto) {

        switch (texto) {

            case TXT_EU:
                return VALOR_JOGADOR.EU;

            case TXT_OPONENTE:
                return VALOR_JOGADOR.OPONENTE;

            default:
                throw new IllegalArgumentException("Texto inválido de Valor Jogador da DHJOG [" + texto + "]");
        }
    }
    
    public enum Peca {
        
        PEAO_MEU(TXT_PEAO_MEU,      TipoPeca.PEAO,  VALOR_JOGADOR.EU),
        TORRE_MINHA(TXT_TORRE_MINHA,TipoPeca.TORRE, VALOR_JOGADOR.EU),
        CAVALO_MEU(TXT_CAVALO_MEU,  TipoPeca.CAVALO,VALOR_JOGADOR.EU),
        BISPO_MEU(TXT_BISPO_MEU,    TipoPeca.BISPO, VALOR_JOGADOR.EU),
        DAMA_MINHA(TXT_DAMA_MINHA,  TipoPeca.DAMA,  VALOR_JOGADOR.EU),
        REI_MEU(TXT_REI_MEU,        TipoPeca.REI,   VALOR_JOGADOR.EU),
        
        PEAO_OPONENTE(TXT_PEAO_OPONENTE,    TipoPeca.PEAO,  VALOR_JOGADOR.OPONENTE),
        TORRE_OPONENTE(TXT_TORRE_OPONENTE,  TipoPeca.TORRE, VALOR_JOGADOR.OPONENTE),
        CAVALO_OPONENTE(TXT_CAVALO_OPONENTE,TipoPeca.CAVALO,VALOR_JOGADOR.OPONENTE),
        BISPO_OPONENTE(TXT_BISPO_OPONENTE,  TipoPeca.BISPO, VALOR_JOGADOR.OPONENTE),
        DAMA_OPONENTE(TXT_DAMA_OPONENTE,    TipoPeca.DAMA,  VALOR_JOGADOR.OPONENTE),
        REI_OPONENTE(TXT_REI_OPONENTE,      TipoPeca.REI,   VALOR_JOGADOR.OPONENTE);
                        
        private final String        textoDHJOG;
        private final TipoPeca      tipoPeca;
        private final VALOR_JOGADOR jogador;   
    
        private Peca(String textoDHJOG, TipoPeca tipoPeca, VALOR_JOGADOR jogador){
            this.textoDHJOG = textoDHJOG;
            this.tipoPeca   = tipoPeca;
            this.jogador    = jogador;
        }
        
        public TipoPeca getTipo(){
            return tipoPeca;
        }
        
        public VALOR_JOGADOR getDono(){
            return jogador;
        }
        
        @Override
        public String toString(){
            return textoDHJOG;
        }
        
        public boolean igual(DHJOG.Cor jogador, br.ufpr.inf.heuchess.representacao.situacaojogo.Peca peca) {

            if (peca == null){
                return false;
            }
            
            if (getDono() == DHJOG.VALOR_JOGADOR.EU &&
                peca.getTipo() == getTipo()         &&
                peca.getCor()  == jogador) {                
                
                return true;

            } else 
                if (getDono() == DHJOG.VALOR_JOGADOR.OPONENTE && 
                    peca.getTipo() == getTipo()               && 
                    peca.getCor() == corJogadorOponente(jogador)) {
                    
                    return true;
                }
            
            return false;
        }
    }
    
    public static DHJOG.Peca converteTextoPeca(String texto) {

        switch (texto) {

            case TXT_PEAO_MEU:
                return Peca.PEAO_MEU;

            case TXT_TORRE_MINHA:
                return Peca.TORRE_MINHA;
                
            case TXT_CAVALO_MEU:
                return Peca.CAVALO_MEU;
                
            case TXT_BISPO_MEU:
                return Peca.BISPO_MEU;            
            
            case TXT_DAMA_MINHA:
                return Peca.DAMA_MINHA;
                
            case TXT_REI_MEU:
                return Peca.REI_MEU;
                
            case TXT_PEAO_OPONENTE:
                return Peca.PEAO_OPONENTE;
                
            case TXT_TORRE_OPONENTE:
                return Peca.TORRE_OPONENTE;
                
            case TXT_CAVALO_OPONENTE:
                return Peca.CAVALO_OPONENTE;
                
            case TXT_BISPO_OPONENTE:
                return Peca.BISPO_OPONENTE;
                
            case TXT_DAMA_OPONENTE:
                return Peca.DAMA_OPONENTE;
                
            case TXT_REI_OPONENTE:
                return Peca.REI_OPONENTE;

            default:
                throw new IllegalArgumentException("Texto inválido de Peca DHJOG [" + texto + "]");
        }
    }
    
    public static TipoPeca converteTextoTipoPeca(String texto) {

        switch (texto) {

            case TXT_PEAO:
                return TipoPeca.PEAO;

            case TXT_TORRE:
                return TipoPeca.TORRE;
                
            case TXT_CAVALO:
                return TipoPeca.CAVALO;
                
            case TXT_BISPO:
                return TipoPeca.BISPO;            
            
            case TXT_DAMA:
                return TipoPeca.DAMA;
                
            case TXT_REI:
                return TipoPeca.REI;
                
            default:
                throw new IllegalArgumentException("Texto inválido de Tipo de Peca DHJOG [" + texto + "]");
        }
    }
    
    public static String validaNomeComponenteGeral(String nome){
        
        if (nome == null){
            return "Nome não pode ser vazio!";
        }
        nome = nome.trim();
        
        if (nome.length() == 0){
            return "Nome não pode ser vazio ou possuir só espaços em branco!";
        }
        
        for (String palavraReservada : DHJOG.PALAVRAS_RESERVADAS){
            
            if (nome.equalsIgnoreCase(palavraReservada)){
                return "O nome escolhido \"" + nome + "\" é uma palavra reservada!\nEscolha outro nome para o componente.";
            }
        }
        
        for (int z = 0; z < nome.length(); z++){            
            
            char    letra = nome.charAt(z);
            boolean teste = false;            
            
            for (char caracter : DHJOG.CARACTERES_VALIDOS){
                
                if (letra == caracter){
                    teste = true;
                    break;
                }                   
            }
            
            if (teste == false){
                
                if (letra == ' '){
                    return "O nome de um componente não pode possuir espaços em branco!";
                }else{
                    return "O caracter \"" + letra + "\" não pode ser utilizado na definição de nomes de componentes.\n" +
                           "Só pode ser utilizado números, o caracter \"_\", e letras sem acentos.\n" +
                           "E não pode haver espaços em branco!";
                }
            }
        }        
        
        return null;
    }
    
    public static String textoValorTabuleiro(double valor, boolean abreviado) {
        
        if (valor == XEQUE_MATE_EU) {
            return (abreviado ? "+\u221E" : "+Infinito");
        } else 
            if (valor == XEQUE_MATE_OPONENTE) {
                return (abreviado ? "-\u221E": "-Infinito");
            } else
                if (valor == EMPATE){                    
                    // Caso de empate //                    
                    return (abreviado ? "-\u221E + 1": "-Infinito + 1");
                }else{
                    // Valor Heurístico Normal //                    
                    return String.valueOf(valor);
                }
    }
    
    public static DHJOG.Cor corJogadorOponente(DHJOG.Cor jogador) {
        return jogador == Cor.BRANCAS ? Cor.PRETAS : Cor.BRANCAS;
    }
}