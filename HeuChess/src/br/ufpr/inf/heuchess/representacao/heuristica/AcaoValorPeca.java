package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.persistencia.FuncaoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.TipoDado;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 24, 2012
 */
public class AcaoValorPeca {
    
    private ParametroPreenchido operando1;
    private ParametroPreenchido operando2;

    private DHJOG.OperadorMatematico operadorMatematico;
    
    private final Heuristica heuristica;
    
    private String textoAcaoToDHJOG;
    private String textoAcaoToJava;
    private String textoAcaoToJavaSimetrica;
    
    public AcaoValorPeca(Heuristica heuristica, FuncaoPreenchida funcaoPreenchida){        
        
        criaParametrosPreenchidos(funcaoPreenchida.getFuncao());        
        
        operando1.setValor(funcaoPreenchida);
        
        operadorMatematico = DHJOG.OperadorMatematico.MAIS;
        
        this.heuristica = heuristica;
    }
    
    public AcaoValorPeca(Heuristica heuristica, String linhaAcao) throws Exception {
        
        this.heuristica = heuristica;
            
        linhaAcao   = linhaAcao.trim();
        int posicao = linhaAcao.indexOf('(');        
        
        if (posicao == -1){
            throw new IllegalArgumentException("N�o foi encontrado o par�nteses da fun��o na heur�stica [" + heuristica + "]");
        }
        
        String nomeCurto = linhaAcao.substring(0,posicao);
        Funcao funcao    = FuncaoDAO.localiza(nomeCurto);
        
        if (funcao == null){
            throw new IllegalArgumentException("N�o foi localizada a fun��o chamada [" + nomeCurto + "] na heur�stica [" + heuristica + "]");
        }
        
        if (funcao.getTipoRetorno() != TipoDado.PECAS){
            throw new IllegalArgumentException("A fun��o usada [" + nomeCurto + "] n�o retorna PECA[] na heur�stica [" + heuristica + "]");
        }
       
        criaParametrosPreenchidos(funcao);
        
        int  posPar = posicao;       
        int  cont   = 1;                    
        int  pos    = posPar;
        char letra;                    
        
        do{
           pos++;
           letra = linhaAcao.charAt(pos);
           if (letra == '('){
               cont++;
           }else{
               if (letra == ')'){
                   cont--;
               }
           }                        
        }while((cont > 0) && (pos < linhaAcao.length()));
                                
        if (cont != 0){
            // faltou achar final de parenteses //
            throw new IllegalArgumentException("N�o foi encontrado parenteses fechando [" + linhaAcao + "] na heur�stica [" + heuristica + "]");
        }
                
        operando1.setValor(new FuncaoPreenchida(heuristica,linhaAcao.substring(0,pos+1)));
        
        linhaAcao = linhaAcao.substring(pos+1,linhaAcao.length()).trim();        
        
        posicao = linhaAcao.indexOf(' ');
        if (posicao == -1){
            throw new IllegalArgumentException("Era esperado o espa�o para entre o operador matem�tico e o segundo operando.\n"
                                             + "Na heur�stica [" + heuristica + "]");
        }        
        
        // Operador de incremento ou decremento
        
        operadorMatematico = DHJOG.converteTextoOperadorMatematico(linhaAcao.substring(0,posicao));
               
        // Valor do Incremento
        
        linhaAcao = linhaAcao.substring(posicao+1,linhaAcao.length());

        String valor = linhaAcao.trim();
        
        if (valor.indexOf('(') != -1){
            operando2.setValor(new FuncaoPreenchida(heuristica,valor));
        }else{
            operando2.setValor(valor);
        }
    }
    
    private void criaParametrosPreenchidos(Funcao funcao){
        
        Parametro tipoRetorno = new Parametro("RETORNO",funcao.getTipoRetorno(),funcao.getDescricaoRetorno());
        operando1 = new ParametroPreenchido(heuristica, tipoRetorno);
        
        Parametro tipoValor = new Parametro("VALOR",TipoDado.REAL,"Valor a ser aplicado a cada pe�a");
        operando2 = new ParametroPreenchido(heuristica, tipoValor);
    }
    
    public DHJOG.OperadorMatematico getOperadorMatematico(){
        return operadorMatematico;
    }
    
    public void setOperadorMatematico(DHJOG.OperadorMatematico operador){
        operadorMatematico = operador;
    }
    
    public ParametroPreenchido getParametroOperando1(){
        return operando1;
    }
    
    public ParametroPreenchido getParametroOperando2(){
        return operando2;
    }
    
    public Object getValorOperando1() {
        return operando1.getValor();
    }

    public void setValorOperando1(Object valor) throws Exception {
        
        if (!(valor instanceof FuncaoPreenchida)){
             throw new IllegalArgumentException("O primeiro operando deve ser obrigatoriamente uma Fun��o Preenchida!");
        }
        
        Funcao funcao = ((FuncaoPreenchida) valor).getFuncao();      
        
        if (funcao.getTipoRetorno() != operando1.getTipo()){
            throw new IllegalArgumentException("Tipo de dado da fun��o preenchida [" + funcao.getTipoRetorno() + "n�o � compat�vel com " +
                                               operando1.getTipo());
        }
        
        operando1.setValor(valor);        
    }

    public Object getValorOperando2() {
        return operando2.getValor();
    }

    public void setValorOperando2(Object valor) throws Exception {
        operando2.setValor(valor);
    }
    
    public AcaoValorPeca geraClone() throws Exception {
        
        FuncaoPreenchida funcaoPreenchidaOriginal = (FuncaoPreenchida) getValorOperando1();

        FuncaoPreenchida novaFuncaoPreenchida = funcaoPreenchidaOriginal.geraClone();

        AcaoValorPeca acaoValorPeca = new AcaoValorPeca(heuristica, novaFuncaoPreenchida);

        acaoValorPeca.setOperadorMatematico(getOperadorMatematico());

        Object valorOperando2 = getValorOperando2();

        if (valorOperando2 instanceof FuncaoPreenchida) {
            acaoValorPeca.setValorOperando2(((FuncaoPreenchida) valorOperando2).geraClone());
        } else {
            acaoValorPeca.setValorOperando2(valorOperando2);
        }
        
        return acaoValorPeca;
    }
    
    public void preparaParaAnaliseHeuristica() throws Exception {
        
        StringBuilder builder = new StringBuilder();
        
        // Regi�es Normais //
        
        builder.append("total = analise.aplicaIncrementoPecas(");
        builder.append(operando1.toJava(false));
        builder.append(",\'");
        builder.append(operadorMatematico.toChar());
        builder.append("\',");
        builder.append(operando2.toJava(false));
        builder.append(");\n");
        
        textoAcaoToJava = builder.toString();

        // Regi�es Sim�tricas //
        
        builder.delete(0,builder.length());
         
        builder.append("total = analise.aplicaIncrementoPecas(");
        builder.append(operando1.toJava(true));
        builder.append(",\'");
        builder.append(operadorMatematico.toChar());
        builder.append("\',");
        builder.append(operando2.toJava(true));
        builder.append(");\n");
         
        textoAcaoToJavaSimetrica = builder.toString();
        
        // Prepara texto para ser mostrado ao usu�rio
        
        textoAcaoToDHJOG = toString();
    }
    
    @Override
    public String toString(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(operando1);
        builder.append(' ');
        builder.append(operadorMatematico);
        builder.append(' ');
        builder.append(operando2);        
                
        return builder.toString();    
    }
    
    public String toDHJOG(){
        
        if (textoAcaoToDHJOG == null){
            textoAcaoToDHJOG = toString();
        }
        
        return textoAcaoToDHJOG;
    }
    
    public String toDB(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(operando1.toDB());
        builder.append(' ');
        builder.append(operadorMatematico);
        builder.append(' ');
        builder.append(operando2.toDB());
                
        return builder.toString();    
    }
     
    public String toJava(){
        return textoAcaoToJava;
    }    
    
    public String toJavaSimetrica(){
        return textoAcaoToJavaSimetrica;
    }
}