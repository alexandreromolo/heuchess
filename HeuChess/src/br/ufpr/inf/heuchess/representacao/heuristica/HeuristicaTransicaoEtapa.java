package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
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
                throw new IllegalArgumentException("Próxima Etapa [" + acoes + "] não foi localizada no Conjunto Heurístico [" + 
                                                   getEtapa().getConjuntoHeuristico() + 
                                                   "]\nUsado pela Heurística de Transição de Etapa [" + getNome() + "]");
            }
        }else{
            throw new IllegalArgumentException("Definiçao inválida de Próxima Etapa [" + acoes +
                                               "]\nUsado pela Heurística de Transição de Etapa [" + getNome() + "]");
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
        return "Heurística de Transição de Etapa";
    }
       
    public Etapa getProximaEtapa(){
        return etapaProxima;
    }
}