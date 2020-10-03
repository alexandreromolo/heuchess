package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 12, 2012
 */
public class HeuristicaValorPeca extends Heuristica {
    
    private ArrayList<AcaoValorPeca> acoesValorPeca;
    
    public HeuristicaValorPeca(Etapa etapa, String nome, long idAutor) {
        super(etapa, nome, idAutor, Heuristica.HEURISTICA_VALOR_PECA);
    }
    
    public HeuristicaValorPeca(Etapa etapa, String nome, long idAutor, String definicao) throws Exception {
        super(etapa, nome, idAutor, Heuristica.HEURISTICA_VALOR_PECA, definicao);
    }

    @Override
    protected String parseAcoesDB() throws Exception {
        
        if (acoesValorPeca == null){
            acoesValorPeca = new ArrayList();
        }
        
        acoesValorPeca.clear();
        
        StringTokenizer tokens = new StringTokenizer(getAcoesDB(),"\n");
        
        if (tokens.countTokens() == 0){
            throw new IllegalArgumentException("Não foi encontrada nenhuma ação na heurística [" + getNome() + "]");
        }
        
        while (tokens.hasMoreTokens()) {
            
            String linhaCondicao = tokens.nextToken().trim();
            
            if (linhaCondicao.length() > 1) {
                acoesValorPeca.add(new AcaoValorPeca(this,linhaCondicao));
            }
        }
        
        //////////////////////////////////////////
        // Converte Entrada DB em formato DHJOG //
        //////////////////////////////////////////
        
        StringBuilder builder = new StringBuilder();

        for (int x = 0; x < acoesValorPeca.size() - 1; x++) {
            builder.append("      ");
            builder.append(acoesValorPeca.get(x));
            builder.append('\n');
        }

        builder.append("      ");
        builder.append(acoesValorPeca.get(acoesValorPeca.size() - 1));
        
        return builder.toString();
    }
    
    @Override
    public void preparaParaAnaliseHeuristica() throws Exception {
        
        super.preparaParaAnaliseHeuristica();
        
        if (acoesValorPeca.size() > 0){
            
            for (AcaoValorPeca acaoValorPeca : acoesValorPeca){
                acaoValorPeca.preparaParaAnaliseHeuristica();
            }
        }else{
            throw new IllegalArgumentException("A Heurística [" + getNome() + "] não possui nenhuma Ação de Valor de Peça");
        }
    }

    @Override
    public String getNomeTipoComponente() {
        return "Heurística de Valor de Peças";
    }
    
    public ArrayList<AcaoValorPeca> getAcoesValorPeca(){
        return acoesValorPeca;
    }
}
