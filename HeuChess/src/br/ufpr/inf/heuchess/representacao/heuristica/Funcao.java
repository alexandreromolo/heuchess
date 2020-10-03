package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 19 de Julho de 2006, 11:42
 */
public class Funcao extends Componente {
        
    public static Tipo FUNCAO_BASICA_TEMPO;
    public static Tipo FUNCAO_BASICA_POSICAO;
    public static Tipo FUNCAO_BASICA_QUANTIDADE;
    public static Tipo FUNCAO_BASICA_VALOR;
    public static Tipo FUNCAO_BASICA_SITUACAO;
    
    private String         nomeCurto;
    private String         codigoFonte;    
    private String         descricaoFuncao;
    private DHJOG.TipoDado tipoRetorno;
    private String         descricaoRetorno;
    private String         comandos;
        
    private ArrayList<Parametro> parametros;    
                
    public Funcao(String nome, long idAutor, Tipo tipo, String definicao) throws Exception {
        
        super(nome,idAutor,tipo);   
        
        parametros  = new ArrayList();
        codigoFonte = definicao;
        
        parseDefinicaoFuncao();        
    }
    
    private void parseDefinicaoFuncao() throws Exception {
            
        codigoFonte = codigoFonte.trim();
        StringTokenizer tokens = new StringTokenizer(getDescricaoDB());
            
        String token;
        token = tokens.nextToken();
        if (!token.equalsIgnoreCase(DHJOG.TXT_FUNCAO)){
            throw new IllegalArgumentException("Era esperado o token [" + DHJOG.TXT_FUNCAO + "] e foi encontrado [" + token + "]");
        }
                
        token = tokens.nextToken();                
        if (!getNome().startsWith(token)){
            throw new IllegalArgumentException("O nome da FUNCAO na definição [" + token + "] não bate com o nome definido no banco [" + getNome() + "]");
        }
                
        token = tokens.nextToken();
        if (!token.equalsIgnoreCase(DHJOG.TXT_DESCRICAO)){
            throw new IllegalArgumentException("Era esperado o token [" + DHJOG.TXT_DESCRICAO + "] da Funcao e foi encontrado [" + token + "]");
        }
                
        tokens.nextToken("\"");
        descricaoFuncao = tokens.nextToken();
                
        tokens.nextToken(" \t\n\r\f");
        token = tokens.nextToken();                
        if (!token.equalsIgnoreCase(DHJOG.TXT_RETORNA)){
            throw new IllegalArgumentException("Era esperado o token [" + DHJOG.TXT_RETORNA + "] e foi encontrado [" + token + "]");
        }     
                
        tipoRetorno = DHJOG.converteTextoTipoDado(tokens.nextToken());
                
        token = tokens.nextToken();
        if (!token.equalsIgnoreCase(DHJOG.TXT_DESCRICAO)){
            throw new IllegalArgumentException("Era esperado o token [" + DHJOG.TXT_DESCRICAO + "] da Funcao e foi encontrado [" + token + "]");
        }
                
        tokens.nextToken("\"");
        descricaoRetorno = tokens.nextToken();
                        
        tokens.nextToken(" \t\n\r\f");
        token = tokens.nextToken();
                
        if (token.equalsIgnoreCase(DHJOG.TXT_PARAMETROS)){
                    
            token = tokens.nextToken();
            do{                         
                DHJOG.TipoDado tipo = DHJOG.converteTextoTipoDado(token);
                
                String nome = tokens.nextToken();
                    
                token = tokens.nextToken();
                if (!token.equalsIgnoreCase(DHJOG.TXT_DESCRICAO)){
                    throw new IllegalArgumentException("Era esperado o token [" + DHJOG.TXT_DESCRICAO + "] da Funcao e foi encontrado [" + token + "]");
                }
                        
                tokens.nextToken("\"");
                String descricao = tokens.nextToken();
                                                
                parametros.add(new Parametro(nome,tipo,descricao));
                        
                tokens.nextToken(" \t\n\r\f");
                token = tokens.nextToken();
                        
            }while(!token.equalsIgnoreCase(DHJOG.TXT_COMANDOS));
        }
                
        boolean procura = true;
        do{
            token = tokens.nextToken();            
            if (token.equalsIgnoreCase(DHJOG.TXT_FIM)){
                        
                token = tokens.nextToken();
                        
                if (token.equalsIgnoreCase(DHJOG.TXT_FUNCAO)){
                    procura = false;
                    if (tokens.hasMoreTokens()){
                        throw new IllegalArgumentException("Foram encontrados mais elementos apos o fechamento da Definicao de Funcao");
                    }
                }
            }
        }while(procura);
                
        int posicao    = getDescricaoDB().indexOf(DHJOG.TXT_COMANDOS);
        int posicaoFim = getDescricaoDB().lastIndexOf(DHJOG.TXT_FIM);
                
        comandos = getDescricaoDB().substring(posicao+8,posicaoFim).trim();
                        
        ///////////////////////
        // Define nome Curto //
        ///////////////////////
        
        posicao = getNome().indexOf("(");
        if (posicao != -1) {
            nomeCurto = getNome().substring(0,posicao);
        } else {
            throw new IllegalArgumentException("Erro na hora de definir nome Curto da Função [" + getNome() + "]");
        }
    }  

    public DHJOG.TipoDado getTipoRetorno() {
        return tipoRetorno;
    }

    public String getDescricaoRetorno() {
        return descricaoRetorno;
    }

    public String getComandos() {
        return comandos;
    }
    
    public int totalParametros(){
        
        if (parametros == null){
            return 0;
        }else{
            return parametros.size();
        }
    }
    
    public Parametro getParametro(int x) {
        return parametros.get(x);
    }

    public String getNomeCurto(){
        return nomeCurto;
    }

    public String getDescricaoFuncao() {
        return descricaoFuncao;
    }
    
    @Override
    public String getDescricaoDB() {
        return codigoFonte;
    }
    
    @Override
    public String getDescricaoDHJOG(){
        return codigoFonte;
    }

    @Override
    public String getNomeTipoComponente() {
        return "Função";
    }
}