package br.ufpr.inf.utils;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 1, 2012
 */
public class UtilsString {

    public static enum Formato {
        NAO_FORMATA,
        TUDO_MAIUSCULO,
        INICIA_MAIUSCULO,
        TUDO_MINUSCULO;
    }

    private static DecimalFormat formataNumeroReal = new DecimalFormat("#,##0.##");
    
    public static String formataCaixaAltaBaixa(String texto){
        
        //Veja classe StringUtils.capitalize(texto)
                
        boolean proximo = true;
        
        StringBuilder textoFormatado = new StringBuilder();
        
        for (int indice = 0; indice < texto.length(); indice++){
            
            char caracter = texto.charAt(indice);
            
            if (caracter == ' '){
                proximo = true;
                textoFormatado.append(caracter);
            }else{
                if (proximo){                    
                    caracter = Character.toUpperCase(caracter);
                    proximo = false;
                }else{
                    caracter = Character.toLowerCase(caracter);
                }
                textoFormatado.append(caracter);
            }
        }   
        
        return textoFormatado.toString();
    }
    
    public static String cortaTextoMaior(String texto, int maximo, boolean reticencias){
        
        if (maximo <= 0){
            throw new InvalidParameterException("O parâmetro máximo deve ser maior que zero!");
        }
        
        if (texto == null){
            return null;
        }
        
        if (texto.length() <= maximo){
            return texto;
        }
        
        if (reticencias){
            return texto.substring(0, maximo-3) + "...";
        }else{
            return texto.substring(0, maximo);
        }
    }
    
    public static String preparaStringParaBD(String texto, boolean cortarEspacos, Formato formato){
        
        if (texto == null){
            return null;
        }
        
        if (cortarEspacos){
            texto = texto.trim();
        }
        
        if (texto.indexOf("'") != -1){
            texto = texto.replaceAll("'"," ");
        }
        
        switch(formato){
            case NAO_FORMATA:
                 // Não faz nada //
                 break;
            case TUDO_MAIUSCULO:
                 texto = texto.toUpperCase();
                 break;
            case INICIA_MAIUSCULO:
                 texto = formataCaixaAltaBaixa(texto);
                 break;
            case TUDO_MINUSCULO:
                 texto = texto.toLowerCase();
                 break;
            default:
                throw new IllegalArgumentException("Parâmetro de formato inválido");
                
        }
        
        return texto;
    }
    
    public static String preparaStringParaBD(String texto, boolean cortarEspacos, int maximo, Formato formato){
        
        texto = preparaStringParaBD(texto, cortarEspacos, formato);
        
        if (texto == null){
            return null;
        }
        
        if (texto.length() > maximo){
            texto = texto.substring(0, maximo - 1);
        }
        
        return texto;
    }
    
    public static String substituiCharPosicao(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public static String removeChar(String s, char c) {

        String r = "";

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                r += s.charAt(i);
            }
        }

        return r;
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static String substituiPalavra(String palavraVelha, String palavraNova, String texto, String delimitadores){
        
        if (palavraVelha == null){
            throw new IllegalArgumentException("Palavra Velha não pode ser nulo");
        }
        if (palavraNova == null){
            throw new IllegalArgumentException("Palavra Nova não pode ser nulo");
        }        
        if (texto == null){
            throw new IllegalArgumentException("Texto não pode ser nulo");
        }
        if (delimitadores == null){
            throw new IllegalArgumentException("Delimitadores não podem ser nulos");
        }
        
        int posInicialPalavraVelha = texto.indexOf(palavraVelha);
        int posFinalPalavraVelha;
        int posFinalTexto;
                
        while(posInicialPalavraVelha != -1){
            
            posFinalTexto = texto.length()- 1;
            posFinalPalavraVelha = (posInicialPalavraVelha + palavraVelha.length()) - 1;
            
            if (posInicialPalavraVelha == 0 || verificaDelimitador(texto, posInicialPalavraVelha - 1, delimitadores)){
                
                if (posFinalPalavraVelha == posFinalTexto){
                                        
                    texto = texto.substring(0, posInicialPalavraVelha == 0 ? 0 : posInicialPalavraVelha) + palavraNova;
                    
                }else
                    if (verificaDelimitador(texto, posFinalPalavraVelha + 1, delimitadores)){
                        
                        texto = texto.substring(0, posInicialPalavraVelha == 0 ? 0 : posInicialPalavraVelha) + 
                        palavraNova + 
                        texto.substring(posFinalPalavraVelha + 1, texto.length());
                    }
            }
            
            posInicialPalavraVelha = texto.indexOf(palavraVelha, posFinalPalavraVelha + 1);
        }
        
        return texto;
    }
    
    public static boolean verificaDelimitador(String texto, int posicao, String delimitadores){
    
        if (texto == null){
            throw new IllegalArgumentException("Texto não pode ser nulo");
        }
        
        if (delimitadores == null){
            throw new IllegalArgumentException("Delimitadores não podem ser nulos");
        }
        
        if (posicao < 0 || posicao >= texto.length()){
            throw new IllegalArgumentException("Posicao inválida no texto [" + posicao + "]");
        }
        
        char caracter = texto.charAt(posicao);
        
        if (delimitadores.indexOf(caracter) != -1){
            return true;
        }else{
            return false;
        }        
    }
    
    public static boolean procuraPalavra(String palavra, String texto, String delimitadores){
        
        if (palavra == null){
            throw new IllegalArgumentException("Palavra Velha não pode ser nulo");
        }
        if (texto == null){
            throw new IllegalArgumentException("Texto não pode ser nulo");
        }
        if (delimitadores == null){
            throw new IllegalArgumentException("Delimitadores não podem ser nulos");
        }
        
        StringTokenizer tokens = new StringTokenizer(texto,delimitadores);
        
        while(tokens.hasMoreTokens()){
            
            String token = tokens.nextToken();
            if (token.equalsIgnoreCase(palavra)){
                return true;
            }
        }
        
        return false;
    }
    
    public static String formataDouble(String pattern, double value) {
        
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        
        String output = myFormatter.format(value);
        
        return output;
    }
    
    public static String tamanhoBytesFormatado(long tamanho, boolean abreviado) {

        if (tamanho < 1024) {
            return String.valueOf(tamanho) + (abreviado ? " B" : (tamanho > 1 ? " bytes" : " byte"));
        }
        
        double kilobytes = (double) tamanho / 1024;
        
        if (kilobytes < 1024) {
            return formataNumeroReal.format(kilobytes) + (abreviado ? " KB" : (kilobytes > 1 ? " Kilobytes" : " Kilobyte"));
        }
        
        double megas = kilobytes / 1024;
            
        if (megas < 1024) {
            return formataNumeroReal.format(megas) + (abreviado ? " MB" : (megas > 1 ? " Megabytes" : " Megabyte"));
        }
        
        double gigas = megas / 1024;
        
        if (gigas < 1024){
            return formataNumeroReal.format(gigas) + (abreviado ? " GB" : (gigas > 1 ? " Gigabytes" : " Gigabyte"));
        }
        
        double teras = gigas / 1024;
        
        return formataNumeroReal.format(teras) + (abreviado ? " TB" : (teras > 1 ? " Terabytes" : " Terabyte"));
    }
    
    public static String porcentagemEmRelacaoTotal(double feito, double total){
        return formataNumeroReal.format((feito/total)*100) + "%";        
    }
    
    public static String descricaoCompletaExcecao(Exception excecao){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("Exceção:\n");
        builder.append(excecao.getMessage());
        builder.append("\nPilha de Execucação:\n");
        
        for (StackTraceElement track : excecao.getStackTrace()) {            
            builder.append(track.toString());
            builder.append("\n");
        }
        
        return builder.toString();
    }
}