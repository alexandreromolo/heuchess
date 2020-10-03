package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 12, 2012
 */
public class HeuristicaTransicaoEtapa extends Heuristica {

    private Etapa etapaProxima;
    
    public HeuristicaTransicaoEtapa(Etapa etapa, String nome, long idAutor, String definicao) throws Exception {
        super(etapa, nome, idAutor, Heuristica.HEURISTICA_TRANSICAO_ETAPA, definicao);
    }

    @Override
    protected String parseAcoesDB() throws Exception {
        
        String acoes = getAcoesDB();

        if (acoes != null && acoes.trim().length() > 0) {
            
            etapaProxima = getEtapa().getConjuntoHeuristico().getEtapa(acoes);
        
            if (etapaProxima == null){
                throw new IllegalArgumentException("Pr�xima Etapa [" + acoes + "] n�o foi localizada no Conjunto Heur�stico [" + 
                                                   getEtapa().getConjuntoHeuristico() + 
                                                   "]\nUsado pela Heur�stica de Transi��o de Etapa [" + getNome() + "]");
            }
        }else{
            throw new IllegalArgumentException("Defini�ao inv�lida de Pr�xima Etapa [" + acoes +
                                               "]\nUsado pela Heur�stica de Transi��o de Etapa [" + getNome() + "]");
        }
        
        //////////////////////////////////////////
        // Converte Entrada DB em formato DHJOG //
        //////////////////////////////////////////
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("      ");
        builder.append(DHJOG.TXT_ETAPA_ATUAL);
        builder.append(' ');
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(etapaProxima.getNome());
        builder.append("\"");
        
        return builder.toString();
    }
    
    @Override
    public String getNomeTipoComponente() {
        return "Heur�stica de Transi��o de Etapa";
    }
       
    public Etapa getProximaEtapa(){
        return etapaProxima;
    }
}