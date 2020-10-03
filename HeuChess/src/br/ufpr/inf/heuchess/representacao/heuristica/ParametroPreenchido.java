package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 29 de Agosto de 2006, 13:00
 */
public class ParametroPreenchido {
        
    private static DecimalFormat formataDouble = new DecimalFormat("##0.0");
    
    private Parametro        parametro;
    
    private boolean          preenchidaValor;
    private Object           valor;
    private FuncaoPreenchida funcaoPreenchida;
    
    private final Heuristica heuristica;
    
    public ParametroPreenchido(Heuristica heuristica, Parametro parametro) {
        this.parametro  = parametro;
        this.heuristica = heuristica;
        
        preenchidaValor = true;
    }    

    public String getNome() {
        return parametro.getNome();
    }
    
    public DHJOG.TipoDado getTipo() {
        return parametro.getTipo();
    }
   
    public String getDescricao() {
        return parametro.getDescricao();
    }
     
    public boolean isPreenchidaValor() {
        return preenchidaValor;
    }

    public void setPreenchidaValor(boolean preenchidaValor) {
        this.preenchidaValor = preenchidaValor;
    }

    public Object getValor() {
        
        if (preenchidaValor){            
            return valor;               
        }else{            
            return funcaoPreenchida;            
        }        
    }

    public void setValor(Integer novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
         
        if (parametro.getTipo() != DHJOG.TipoDado.INTEIRO){
            throw new IllegalArgumentException("O parâmetro não é do tipo INTEIRO! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(Double novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
         
        if (parametro.getTipo() != DHJOG.TipoDado.REAL){
            throw new IllegalArgumentException("O parâmetro não é do tipo REAL! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(DHJOG.VALOR_LOGICO novoValor){
    
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
         
        if (parametro.getTipo() != DHJOG.TipoDado.LOGICO){
            throw new IllegalArgumentException("O parâmetro não é do tipo LOGICO! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(DHJOG.VALOR_JOGADOR novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (parametro.getTipo() != DHJOG.TipoDado.JOGADOR){
            throw new IllegalArgumentException("O parâmetro não é do tipo JOGADOR! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(Regiao novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (parametro.getTipo() != DHJOG.TipoDado.CASAS){
            throw new IllegalArgumentException("O parâmetro não é do tipo CASA[]! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(DHJOG.Peca[] novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (parametro.getTipo() != DHJOG.TipoDado.PECAS){
            throw new IllegalArgumentException("O parâmetro não é do tipo PECA[]! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(TipoPeca[] novoValor){
        
        if (novoValor == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (parametro.getTipo() != DHJOG.TipoDado.TIPO_PECAS){
            throw new IllegalArgumentException("O parâmetro não é do tipo TIPO_PECA[]! Heurística [" + heuristica + "]");
        }
        
        preenchidaValor = true;
        valor = novoValor;
    }
    
    public void setValor(FuncaoPreenchida funcPre){
         
        if (funcPre == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (funcPre.getFuncao().getTipoRetorno() != parametro.getTipo()){
            throw new IllegalArgumentException("Função retorna tipo diferente do Parâmetro [" + funcPre.getFuncao().getNomeCurto() +
                                               "] Heurística [" + heuristica + "]");
        }
        
        funcaoPreenchida = funcPre;
        preenchidaValor  = false;
    }
    
    public void setValor(String texto) throws Exception {
         
        if (texto == null){
            throw new IllegalArgumentException("Valor passado nulo no parâmetro da heurística [" + heuristica + "]");
        }
        
        if (texto.trim().length() == 0){
            throw new IllegalArgumentException("Passado String vazia! Heurística [" + heuristica + "]");
        }
        
        if (texto.indexOf("(") != -1){                    
            
            FuncaoPreenchida funcPre = new FuncaoPreenchida(heuristica,texto);
            setValor(funcPre);
        
        }else{
            
             switch(parametro.getTipo()){
                
                case INTEIRO:
                    valor = new Integer(texto);
                    break;
                    
                case REAL:
                    valor = new Double(texto);
                    break;
                    
                case LOGICO:
                    valor = DHJOG.converteTextoValorLogico(texto);
                    break;
                    
                case JOGADOR:
                    valor = DHJOG.converteTextoValorJogador(texto);
                    break;
                    
                case CASAS:
                    if (texto.equals(DHJOG.TODO_TABULEIRO)){
                        valor = texto;
                    }else{
                        valor = heuristica.getEtapa().getRegiao(texto);
                    }
                    break;
                    
                case PECAS:
                    if (texto.charAt(0) != '{'){
                        throw new IllegalArgumentException("Era esperado { no início de conjunto de PECA[] na heurística [" + heuristica + "]");
                    }
                    
                    if (texto.charAt(texto.length()-1) != '}'){
                        throw new IllegalArgumentException("Era esperado } no final de conjunto de PECA[] na heurística [" + heuristica + "]");
                    }
                    
                    texto = texto.substring(1,texto.length()-1);
                    
                    StringTokenizer tokensPecas = new StringTokenizer(texto,",");
                    
                    int totalPecas = tokensPecas.countTokens();
                    
                    if (totalPecas > 0){
                    
                        DHJOG.Peca[] pecas = new DHJOG.Peca[totalPecas];
                        
                        int pos = 0;
                        
                        while(tokensPecas.hasMoreTokens()){
                            pecas[pos] = DHJOG.converteTextoPeca(tokensPecas.nextToken());
                            pos++;
                        }
                        
                        valor = pecas;
                        
                    }else{
                        throw new IllegalArgumentException("Não foi encontrado nenhum token no parâmetro PECA[] na heurística [" + heuristica + "]");
                    }
                    break;
                    
                case TIPO_PECAS:
                    if (texto.charAt(0) != '{'){
                        throw new IllegalArgumentException("Era esperado { no início de conjunto de TIPO_PECA[] na heurística [" + heuristica + "]");
                    }
                    
                    if (texto.charAt(texto.length()-1) != '}'){
                        throw new IllegalArgumentException("Era esperado } no final de conjunto de TIPO_PECA[] na heurística [" + heuristica + "]");
                    }
                    
                    texto = texto.substring(1,texto.length()-1);
                    
                    StringTokenizer tokensTiposPecas = new StringTokenizer(texto,",");
                    
                    int totalTiposPecas = tokensTiposPecas.countTokens();
                    
                    if (totalTiposPecas > 0){
                    
                        TipoPeca[] tiposPecas = new TipoPeca[totalTiposPecas];
                        
                        int pos = 0;
                        
                        while(tokensTiposPecas.hasMoreTokens()){
                            tiposPecas[pos] = DHJOG.converteTextoTipoPeca(tokensTiposPecas.nextToken());
                            pos++;
                        }
                        
                        valor = tiposPecas;
                        
                    }else{
                        throw new IllegalArgumentException("Não foi encontrado nenhum token no parâmetro TIPO_PECA[] na heurística [" + heuristica + "]");
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo inválido de Parâmetro [" + parametro.getTipo() + "] Heurística [" + heuristica + "]");
            }
             
            preenchidaValor = true;    
        }
    }
    
    public void setValor(Object valor) throws Exception {
        
        if (valor instanceof String){
            setValor((String) valor);
        }else
            if (valor instanceof FuncaoPreenchida){
                setValor((FuncaoPreenchida) valor);
            }else            
                if (valor instanceof Integer){
                    setValor((Integer) valor);
                }else
                    if (valor instanceof Double){
                        setValor((Double) valor);
                    }else
                        if (valor instanceof DHJOG.VALOR_LOGICO){
                            setValor((DHJOG.VALOR_LOGICO) valor);
                        }else
                            if (valor instanceof DHJOG.VALOR_JOGADOR){
                                setValor((DHJOG.VALOR_JOGADOR) valor);
                            }else
                                if (valor instanceof Regiao){
                                    setValor((Regiao) valor);
                                }else
                                    if (valor instanceof DHJOG.Peca[]){
                                       setValor((DHJOG.Peca[]) valor); 
                                    }else
                                        if (valor instanceof TipoPeca[]){
                                            setValor((TipoPeca[]) valor); 
                                        }else{
                                            throw new IllegalArgumentException("Objeto passado nao é suportado [" + valor.getClass().getName() + 
                                                                               "] Heurística [" + heuristica + "]");
                                        }
    }
    
    @Override
    public String toString(){
        
        if (preenchidaValor){
            
            if (valor != null){
                
                switch(parametro.getTipo()){
                    
                    case PECAS:
                        StringBuilder builderPecas = new StringBuilder();
                    
                        builderPecas.append('{');
                    
                        DHJOG.Peca[] pecas = ((DHJOG.Peca[]) valor);
                    
                        for (int x = 0; x < pecas.length-1; x++){
                            builderPecas.append(pecas[x]);
                            builderPecas.append(',');
                        }
                    
                        builderPecas.append(pecas[pecas.length-1]);
                    
                        builderPecas.append('}');
                    
                        return builderPecas.toString();
                    
                    case TIPO_PECAS:
                        StringBuilder builderTiposPecas = new StringBuilder();
                    
                        builderTiposPecas.append('{');
                    
                        TipoPeca[] tiposPecas = ((TipoPeca[]) valor);
                    
                        for (int x = 0; x < tiposPecas.length-1; x++){
                            builderTiposPecas.append(tiposPecas[x].toDHJOG());
                            builderTiposPecas.append(',');
                        }
                    
                        builderTiposPecas.append(tiposPecas[tiposPecas.length-1].toDHJOG());
                    
                        builderTiposPecas.append('}');
                    
                        return builderTiposPecas.toString();
                                    
                    case REAL:
                        return formata(((Double) valor).doubleValue());
                        
                    default:
                        return valor.toString();
                }
            }   
        }else{
            if (funcaoPreenchida != null){
                return funcaoPreenchida.toString();
            }
        }
        
        return null;
    }
    
    public String toDB() {
        
        if (preenchidaValor) {

            if (valor != null) {

                switch (parametro.getTipo()) {

                    case PECAS:
                        StringBuilder builderPecas = new StringBuilder();

                        builderPecas.append('{');

                        DHJOG.Peca[] pecas = ((DHJOG.Peca[]) valor);

                        for (int x = 0; x < pecas.length - 1; x++) {
                            builderPecas.append(pecas[x]);
                            builderPecas.append(',');
                        }

                        builderPecas.append(pecas[pecas.length - 1]);

                        builderPecas.append('}');

                        return builderPecas.toString();

                    case TIPO_PECAS:
                        StringBuilder builderTiposPecas = new StringBuilder();

                        builderTiposPecas.append('{');

                        TipoPeca[] tiposPecas = ((TipoPeca[]) valor);

                        for (int x = 0; x < tiposPecas.length - 1; x++) {
                            builderTiposPecas.append(tiposPecas[x].toDHJOG());
                            builderTiposPecas.append(',');
                        }

                        builderTiposPecas.append(tiposPecas[tiposPecas.length - 1].toDHJOG());

                        builderTiposPecas.append('}');

                        return builderTiposPecas.toString();
                        
                    default:
                        return valor.toString();
                }
            }
        } else {
            if (funcaoPreenchida != null) {
                return funcaoPreenchida.toDB();
            }
        }

        return null;
    }
    
    public String toJava(boolean simetrica) throws Exception {
        
        if (preenchidaValor){
            
            if (valor != null){
                
                switch(parametro.getTipo()){
                
                    case INTEIRO:
                    case REAL:   
                    case JOGADOR: 
                        return valor.toString();
                        
                    case LOGICO:
                        return ((DHJOG.VALOR_LOGICO) valor).toJava();
                        
                    case CASAS:
                        if (valor instanceof String){
                            
                            String texto = valor.toString();
                            
                            if (texto.equals(DHJOG.TODO_TABULEIRO)){
                                return "analise.getCasas()";
                            }else{
                                throw new IllegalArgumentException("Valor de texto [" + texto + "] inválido para parâmetro tipo CASA[]!\n" +
                                                                   "Heurística [" + heuristica + "]");
                            }
                            
                        }else
                            if (valor instanceof Regiao){                                
                                if (simetrica){
                                    return "etapaAtual.getRegiao(\"" + valor + "\").getCasasSimetricas()";
                                }else{
                                    return "etapaAtual.getRegiao(\"" + valor + "\").getCasas()";
                                }
                            }else{
                                throw new IllegalArgumentException("Objeto [" + valor.getClass().getName() + "] inválido para tipo de dado CASA!\n"
                                                                 + "Heurística [" + heuristica + "]");
                            }
                        
                    case PECAS:
                        StringBuilder builderPecas = new StringBuilder();
                    
                        builderPecas.append("analise.montaArrayPecas(\"");
                    
                        DHJOG.Peca[] pecas = ((DHJOG.Peca[]) valor);
                    
                        for (int x = 0; x < pecas.length-1; x++){
                            builderPecas.append(pecas[x]);
                            builderPecas.append(',');
                        }
                    
                        builderPecas.append(pecas[pecas.length-1]);
                    
                        builderPecas.append("\")");
                    
                        return builderPecas.toString();
                        
                    case TIPO_PECAS:
                        StringBuilder builderTiposPecas = new StringBuilder();
                    
                        builderTiposPecas.append("analise.montaArrayTipos(\"");
                    
                        TipoPeca[] tiposPecas = ((TipoPeca[]) valor);
                    
                        for (int x = 0; x < tiposPecas.length-1; x++){
                            builderTiposPecas.append(tiposPecas[x].toDHJOG());
                            builderTiposPecas.append(',');
                        }
                    
                        builderTiposPecas.append(tiposPecas[tiposPecas.length-1].toDHJOG());
                    
                        builderTiposPecas.append("\")");
                    
                        return builderTiposPecas.toString();
                    
                    default:
                        throw new IllegalArgumentException("Tipo de Dado não suportado pela função [" + parametro.getTipo() +
                                                           "] Heurística [" + heuristica + "]");
                }
                
            }else{
                throw new IllegalArgumentException("O parâmetro está setado como preenchido por valor mais está nulo!\n" +
                                                   "Heurística [" + heuristica + "]");
            }   
        }else{
            if (funcaoPreenchida != null){
                return funcaoPreenchida.toJava(simetrica);
            }else{
                throw new IllegalArgumentException("O parâmetro está setado como preenchido por função mais está nulo!\n" +
                                                   "Heurística [" + heuristica + "]");
            }
        }
    }
    
    public static String formata(double valor){
        return formataDouble.format(valor);
    }
}