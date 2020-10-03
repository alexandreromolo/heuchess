package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.persistencia.FuncaoDAO;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 22 de Julho de 2006, 19:58
 */
public class FuncaoPreenchida {
    
    private Funcao                funcao;
    private ParametroPreenchido[] parametrosPreenchidos;
    
    private final Heuristica heuristica;
    
    /**
     * Cria uma FunçãoPreenchida apenas definindo qual função será preenchida
     */
    public FuncaoPreenchida(Heuristica heuristica, Funcao funcao){
        
        this.heuristica = heuristica;
        
        inicializaFuncaoParametros(funcao);
    }
    
    /**
     * Cria uma FuncaoPreenchida através de uma String com a função já preenchida
     */
    public FuncaoPreenchida(Heuristica heuristica, String funcaoPreenchida) throws Exception {
        
        this.heuristica = heuristica;
        
        int posicao = funcaoPreenchida.indexOf('(');
        
        if (posicao != -1){
            
            String nomeCurto = funcaoPreenchida.substring(0,posicao);
            funcao = FuncaoDAO.localiza(nomeCurto);
            
            if (funcao != null){
                
                inicializaFuncaoParametros(funcao);
                
                if (funcao.totalParametros() > 0){
                    
                    String nome = funcao.getNomeCurto();
                    
                    funcaoPreenchida = funcaoPreenchida.replaceAll(nome,"");
                    funcaoPreenchida = funcaoPreenchida.substring(1,funcaoPreenchida.length()-1);
                    
                    String  parametro;
                    boolean pegandoFuncao = false;
                    boolean pegandoLista  = false;
                    
                    int     contPar =  0;                    
                    int     contCha =  0;                    
                    int     pos     = -1;
                    int     indice  =  0;
                    char    letra;   
                                         
                    do{
                        pos++;
                        letra = funcaoPreenchida.charAt(pos);
                        if (letra == '('){
                            contPar++;
                            pegandoFuncao = true;
                        }else
                            if (letra == ')'){
                                contPar--;
                            }else
                                if (letra == '{' && !pegandoFuncao){
                                    contCha++;
                                    pegandoLista = true;
                                }else
                                    if (letra == '}' && !pegandoFuncao){
                                        contCha--;
                                    }            
                            
                      if (pegandoFuncao && (contPar == 0)){
                          
                          pegandoFuncao = false;
                          
                          parametro = funcaoPreenchida.substring(0,pos+1);                            
                          
                          parametrosPreenchidos[indice].setValor(parametro);
                          
                          indice++; 
                          if (pos+2 < funcaoPreenchida.length()){
                              funcaoPreenchida = funcaoPreenchida.substring(pos+2,funcaoPreenchida.length());
                              pos = -1;
                          }
                      }else
                          if (pegandoLista && (contCha == 0)){
                              
                              pegandoLista = false;
                              
                              parametro = funcaoPreenchida.substring(0,pos+1);
                              
                              parametrosPreenchidos[indice].setValor(parametro);                                
                              
                              indice++; 
                              if (pos+2 < funcaoPreenchida.length()){
                                  funcaoPreenchida = funcaoPreenchida.substring(pos+2,funcaoPreenchida.length());
                                  pos = -1;
                              }                                
                          }else
                              if (letra == ',' && !pegandoFuncao && !pegandoLista && pos != 0){
                                  
                                  parametro = funcaoPreenchida.substring(0,pos);
                                  
                                  parametrosPreenchidos[indice].setValor(parametro);                                
                                  
                                  indice++; 
                                  if (pos+1 < funcaoPreenchida.length()){
                                      funcaoPreenchida = funcaoPreenchida.substring(pos+1,funcaoPreenchida.length());
                                      pos = -1;
                                  }                                
                              }else
                                  if (pos == (funcaoPreenchida.length()-1)){
                                      
                                      parametrosPreenchidos[indice].setValor(funcaoPreenchida.trim());                                
                                      
                                      indice++; 
                                  }
                            
                    }while(pos < funcaoPreenchida.length() && indice < parametrosPreenchidos.length);
                }
            }else{
                throw new IllegalArgumentException("Não foi localizada uma função chamada [" + nomeCurto + 
                                                   "] que esta na heurística [" + heuristica + "]");
            }
        }else{
            throw new IllegalArgumentException("Era esperado a abre parenteses da funcao na heurística [" + heuristica + "]");
        }
    }       
    
    /**
     * Carrega a função e cria o vetor de parametros 
     */
    private void inicializaFuncaoParametros(Funcao funcao){
        
        this.funcao = funcao;
        
        if (funcao == null){
            throw new IllegalArgumentException("Funcao nula na inicializacao dos parâmetros da heurística [" + heuristica + "]");
        }
        
        if (funcao.totalParametros() > 0){
            
            parametrosPreenchidos = new ParametroPreenchido[funcao.totalParametros()];
            
            for (int x = 0; x < parametrosPreenchidos.length; x++){
                parametrosPreenchidos[x] = new ParametroPreenchido(heuristica,funcao.getParametro(x));
            }
        }
    }
    
    public Funcao getFuncao() {
        return funcao;
    }

    public int totalParametros(){
        
        if (parametrosPreenchidos == null){
            return 0;
        }else{
            return parametrosPreenchidos.length;
        }
    }
    
    public ParametroPreenchido getParametroPreenchido(int posicao){
        return parametrosPreenchidos[posicao];
    }
    
    public Object getValorParametro(int posicao){
        return parametrosPreenchidos[posicao].getValor();
    }
    
    public void setValorParametro(int posicao, Object valor) throws Exception {
        parametrosPreenchidos[posicao].setValor(valor);
    }    
    
    public ParametroPreenchido localizaParametroPorTipo(DHJOG.TipoDado tipo){        
        
        if (totalParametros() == 0){
            return null;
        }
        
        for (int x = 0; x <  parametrosPreenchidos.length; x++){
            
            if (parametrosPreenchidos[x].getTipo() == tipo){
                return parametrosPreenchidos[x];                
            }
        }
        
        return null;
    }
    
    public FuncaoPreenchida geraClone() throws Exception {                
        
        FuncaoPreenchida nova = new FuncaoPreenchida(heuristica,getFuncao());
        
        for (int x = 0; x < totalParametros(); x++){
            
            Object valor = getValorParametro(x);
            
            if (valor instanceof FuncaoPreenchida){
                nova.setValorParametro(x,((FuncaoPreenchida)valor).geraClone());
            }else{
                nova.setValorParametro(x,valor);
            }
        }
        
        return nova;
    }
    
    @Override
    public String toString(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(funcao.getNomeCurto());
        
        if (totalParametros() == 0){         
            builder.append("()");
            
            return builder.toString();
        }
        
        builder.append('(');
        
        // Inclui valor dos parametros //
        
        for (int x = 0; x < parametrosPreenchidos.length-1; x++){
            builder.append(parametrosPreenchidos[x].toString());
            builder.append(',');
        }
        
        // Ultimo Parâmetro //
        
        builder.append(parametrosPreenchidos[parametrosPreenchidos.length-1].toString());
        builder.append(')');
        
        return builder.toString();
    }
    
    public String toDB(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(funcao.getNomeCurto());
        
        if (totalParametros() == 0){         
            builder.append("()");
            
            return builder.toString();
        }
        
        builder.append('(');
        
        // Inclui valor dos parametros //
        
        for (int x = 0; x < parametrosPreenchidos.length-1; x++){
            builder.append(parametrosPreenchidos[x].toDB());
            builder.append(',');
        }
        
        // Ultimo Parâmetro //
        
        builder.append(parametrosPreenchidos[parametrosPreenchidos.length-1].toDB());
        builder.append(')');
        
        return builder.toString();
    }
    
    public String toJava(boolean simetrica) throws Exception {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("analise.");
        builder.append(funcao.getNomeCurto());
        
        if (totalParametros() == 0){         
            builder.append("()");
            
            return builder.toString();
        }
        
        builder.append('(');
        
        // Inclui valor dos parametros //
        
        for (int x = 0; x < parametrosPreenchidos.length-1; x++){
            builder.append(parametrosPreenchidos[x].toJava(simetrica));
            builder.append(',');
        }
        
        // Ultimo Parâmetro //
        
        builder.append(parametrosPreenchidos[parametrosPreenchidos.length-1].toJava(simetrica));
        builder.append(')');
        
        return builder.toString();
    }
}