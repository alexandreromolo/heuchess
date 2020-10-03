package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.persistencia.FuncaoDAO;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 22 de Julho de 2006, 20:03
 */
public class CondicaoHeuristica {
    
    private DHJOG.OperadorLogico     operadorLogico;    
    private DHJOG.OperadorRelacional operadorRelacional;
    private boolean                  operadorLogicoNao;
    
    private ParametroPreenchido operando1;
    private ParametroPreenchido operando2;
    
    private final Heuristica heuristica;
    
    public CondicaoHeuristica(Heuristica heuristica, FuncaoPreenchida funcaoPreenchida){        
        
        criaParametrosPreenchidos(funcaoPreenchida.getFuncao());        
        
        operando1.setValor(funcaoPreenchida);
        
        operadorLogico = DHJOG.OperadorLogico.E;
        
        this.heuristica = heuristica;
    }
    
    public CondicaoHeuristica(Heuristica heuristica, String linhaCondicao) throws Exception {
        
        this.heuristica = heuristica;
        
        if (linhaCondicao.indexOf("NAO") != -1){
            operadorLogicoNao = true;
            
            int pos = linhaCondicao.indexOf('(');
            linhaCondicao = linhaCondicao.substring(pos+1,linhaCondicao.length());            
            
            pos = linhaCondicao.lastIndexOf(')');
            linhaCondicao = linhaCondicao.substring(0,pos);
        }
        
        linhaCondicao = linhaCondicao.trim();
        int posicao   = linhaCondicao.indexOf('(');        
        
        if (posicao == -1){
            throw new IllegalArgumentException("Não foi encontrado o parênteses da função na heurística [" + heuristica + "]");
        }
        
        String nomeCurto = linhaCondicao.substring(0,posicao);
        Funcao funcao    = FuncaoDAO.localiza(nomeCurto);
        
        if (funcao == null){
            throw new IllegalArgumentException("Não foi localizada a função chamada [" + nomeCurto + "] da heurística [" + heuristica + "]");
        }
       
        criaParametrosPreenchidos(funcao);
                
        int  posPar = posicao;       
        int  cont   = 1;                    
        int  pos    = posPar;
        char letra;                    
        
        do{
           pos++;
           letra = linhaCondicao.charAt(pos);
           if (letra == '('){
               cont++;
           }else{
               if (letra == ')'){
                   cont--;
               }
           }                        
        }while((cont > 0) && (pos < linhaCondicao.length()));
                                
        if (cont != 0){
            // faltou achar final de parenteses //
            throw new IllegalArgumentException("Não foi encontrado parenteses fechando [" + linhaCondicao + "] na heurística [" + heuristica + "]");
        }
                
        operando1.setValor(new FuncaoPreenchida(heuristica,linhaCondicao.substring(0,pos+1)));                
        
        linhaCondicao = linhaCondicao.substring(pos+1,linhaCondicao.length()).trim();        
        
        posicao = linhaCondicao.indexOf(' ');
        if (posicao == -1){
            throw new IllegalArgumentException("Era esperado o espaço entre o operador relacional e o segundo operando.\n"
                                             + "Na heurística [" + heuristica + "]");
        }        
        
        operadorRelacional = DHJOG.converteTextoOperadorRelacional(linhaCondicao.substring(0,posicao));
               
        linhaCondicao = linhaCondicao.substring(posicao+1,linhaCondicao.length());
        posicao       = linhaCondicao.lastIndexOf(" E");        
        if (posicao != -1){
            linhaCondicao = linhaCondicao.substring(0,posicao);            
            operadorLogico = DHJOG.OperadorLogico.E;
        }else{
            posicao = linhaCondicao.lastIndexOf(" OU");
            if (posicao != -1){
                linhaCondicao = linhaCondicao.substring(0,posicao);            
                operadorLogico = DHJOG.OperadorLogico.OU;
            }else{
                operadorLogico = DHJOG.OperadorLogico.E;
            }
        }        
        
        String valor = linhaCondicao.trim();
        if (valor.indexOf('(') != -1){
            operando2.setValor(new FuncaoPreenchida(heuristica,valor));
        }else{
            operando2.setValor(valor);
        }
    }
    
    private void criaParametrosPreenchidos(Funcao funcao){
        
        Parametro tipoRetorno = new Parametro("RETORNO",funcao.getTipoRetorno(),funcao.getDescricaoRetorno());
        operando1 = new ParametroPreenchido(heuristica,tipoRetorno);
        
        Parametro tipoValor = new Parametro("VALOR",funcao.getTipoRetorno(),funcao.getDescricaoRetorno());
        operando2 = new ParametroPreenchido(heuristica,tipoValor);
    }
  
    public DHJOG.OperadorLogico getOperadorLogico() {
        return operadorLogico;
    }

    public void setOperadorLogico(DHJOG.OperadorLogico operadorLogico) {
        this.operadorLogico = operadorLogico;
    }

    public DHJOG.OperadorRelacional getOperadorRelacional() {
        return operadorRelacional;
    }

    public void setOperadorRelacional(DHJOG.OperadorRelacional operadorRelacional) {
        this.operadorRelacional = operadorRelacional;
    }
    
    public boolean isOperadorLogicoNao() {
        return operadorLogicoNao;
    }
   
    public void setOperadorLogicoNao(boolean operadorLogicoNao) {
        this.operadorLogicoNao = operadorLogicoNao;
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
             throw new IllegalArgumentException("O primeiro operando deve ser obrigatoriamente uma Função Preenchida!\n"
                                              + "Erro na heurística [" + heuristica + "]");
        }
        
        Funcao funcao = ((FuncaoPreenchida) valor).getFuncao();      
        
        if (funcao.getTipoRetorno() != operando1.getTipo()){
            ////////////////////////////////////////////
            // Mudou tipo do Operando 1 : Recria tudo //
            ////////////////////////////////////////////
            criaParametrosPreenchidos(funcao);            
        }
        
        operando1.setValor(valor);        
    }

    public Object getValorOperando2() {
        return operando2.getValor();
    }

    public void setValorOperando2(Object valor) throws Exception {
        operando2.setValor(valor);
    }
    
    public CondicaoHeuristica geraClone() throws Exception {
        
        FuncaoPreenchida funcaoPreenchidaOriginal = (FuncaoPreenchida) getValorOperando1();

        FuncaoPreenchida novaFuncaoPreenchida = funcaoPreenchidaOriginal.geraClone();

        CondicaoHeuristica condicao = new CondicaoHeuristica(heuristica, novaFuncaoPreenchida);

        condicao.setOperadorLogico(getOperadorLogico());
        condicao.setOperadorLogicoNao(isOperadorLogicoNao());
        condicao.setOperadorRelacional(getOperadorRelacional());

        Object valorOperando2 = getValorOperando2();

        if (valorOperando2 instanceof FuncaoPreenchida) {
            condicao.setValorOperando2(((FuncaoPreenchida) valorOperando2).geraClone());
        } else {
            condicao.setValorOperando2(valorOperando2);
        }
        
        return condicao;
    }
    
    @Override
    public String toString(){
        return toDHJOG(true);
    }
    
    public String toDB(boolean porOperadorLogico){
        
        StringBuilder builder = new StringBuilder();
        
        if (operadorLogicoNao){
            builder.append(DHJOG.OperadorLogico.NAO);
            builder.append(" (");
        }
        
        builder.append(operando1.toDB());
        builder.append(' ');
        builder.append(operadorRelacional);
        builder.append(' ');
        builder.append(operando2.toDB());
                
        if (operadorLogicoNao){
            builder.append(')');
        }
        
        if (porOperadorLogico){            
            builder.append(' ');
            builder.append(operadorLogico);
        }
        
        return builder.toString();    
    }
    
    public String toDHJOG(boolean porOperadorLogico){
        
        StringBuilder builder = new StringBuilder();
        
        if (operadorLogicoNao){
            builder.append(DHJOG.OperadorLogico.NAO);
            builder.append(" (");
        }
        
        builder.append(operando1);
        builder.append(' ');
        builder.append(operadorRelacional);
        builder.append(' ');
        builder.append(operando2);
                
        if (operadorLogicoNao){
            builder.append(')');
        }
        
        if (porOperadorLogico){            
            builder.append(' ');
            builder.append(operadorLogico);
        }
        
        return builder.toString();    
    }
    
    public String toJava(boolean porOperadorLogico, boolean simetrica) throws Exception {
        
        StringBuilder builder = new StringBuilder();
        
        if (operadorLogicoNao){
            builder.append(DHJOG.OperadorLogico.NAO.toJava());
            builder.append('(');
        }
        
        if (operando1.getTipo() == DHJOG.TipoDado.INTEIRO || 
            operando1.getTipo() == DHJOG.TipoDado.REAL    ||
            operando1.getTipo() == DHJOG.TipoDado.LOGICO  ||
            operando1.getTipo() == DHJOG.TipoDado.JOGADOR){
        
            builder.append(operando1.toJava(simetrica));
            builder.append(' ');
            builder.append(operadorRelacional.toJava());
            builder.append(' ');
            builder.append(operando2.toJava(simetrica));
            
        }else
            if (operando1.getTipo() == DHJOG.TipoDado.CASAS || 
                operando1.getTipo() == DHJOG.TipoDado.PECAS ||    
                operando1.getTipo() == DHJOG.TipoDado.TIPO_PECAS){
                
                switch (operadorRelacional) {

                    case IGUAL:
                        builder.append("analise.conjuntoIgual(");
                        break;

                    case DIFERENTE:
                        builder.append("analise.conjuntoDiferente(");
                        break;

                    case CONTEM:
                        builder.append("analise.conjuntoContem(");
                        break;

                    case CONTIDO:
                        builder.append("analise.conjuntoContido(");
                        break;

                    default:    
                        throw new IllegalArgumentException("Operador não suportado para este tipo de dado [" + operadorRelacional + 
                                                           "] na heurística [" + heuristica + "]");
                }           
          
                if (operando1.getTipo() == DHJOG.TipoDado.PECAS){
                    builder.append("true,EU,");
                }else{
                    builder.append("false,EU,");
                }
                
                builder.append(operando1.toJava(simetrica));
                builder.append(',');
                builder.append(operando2.toJava(simetrica));
                builder.append(')');
                
            }else{
                throw new IllegalArgumentException("Tipo de Dado não suportado [" + operando1.getTipo() + "] na heurística [" + heuristica + "]");
            }
        
        if (operadorLogicoNao){
            builder.append(')');
        }
        
        if (porOperadorLogico){            
            builder.append(' ');
            builder.append(operadorLogico.toJava());
        }
        
        return builder.toString();                
    }
}