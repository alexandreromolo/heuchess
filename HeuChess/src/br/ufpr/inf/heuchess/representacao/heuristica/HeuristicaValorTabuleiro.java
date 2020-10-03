package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 12, 2012
 */
public class HeuristicaValorTabuleiro extends Heuristica {

    private DHJOG.OperadorMatematico  operadorMatematico;
    private double                    valorIncremento;
    
    public HeuristicaValorTabuleiro(Etapa etapa, String nome, long idAutor, String definicao) throws Exception {
        super(etapa, nome, idAutor, Heuristica.HEURISTICA_VALOR_TABULEIRO, definicao);
    }

    @Override
    protected String parseAcoesDB() throws Exception {
        
        StringTokenizer tokens = new StringTokenizer(getAcoesDB(), " ");
                
        // Operador de incremento ou decremento
                
        operadorMatematico = DHJOG.converteTextoOperadorMatematico(tokens.nextToken());
        
        // Valor do Incremento
        
        valorIncremento = Double.parseDouble(tokens.nextToken());
        
        //////////////////////////////////////////
        // Converte Entrada DB em formato DHJOG //
        //////////////////////////////////////////
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("      ");
        builder.append(DHJOG.TXT_TABULEIRO);
        builder.append(' ');
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(' ');
        builder.append(DHJOG.TXT_TABULEIRO);
        builder.append(' ');
        builder.append(operadorMatematico);
        builder.append(' ');

        builder.append(ParametroPreenchido.formata(valorIncremento));
                
        return builder.toString();
    }
    
    @Override
    public String getNomeTipoComponente() {
        return "Heurística de Valor de Tabuleiro";
    }
    
    public DHJOG.OperadorMatematico getOperadorMatematico(){
        return operadorMatematico;
    }
    
    public double getValorIncremento(){
        return valorIncremento;
    }
    
    public double aplicaIncremento(double valorAtual){
    
        switch(operadorMatematico){
            
            case MAIS:
                return valorAtual += valorIncremento;
                
            case MENOS:
                return valorAtual -= valorIncremento;
                
            case MULTIPLICACAO:
                return valorAtual *= valorIncremento;
                
            case DIVISAO:
                return valorAtual /= valorIncremento;
                
            default:
                throw new IllegalArgumentException("Operador Matemático desconhecido em Heurística de Valor de Tabuleiro [" + getNome() + "]");
        }
    }
}