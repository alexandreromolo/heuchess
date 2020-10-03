package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
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
            throw new IllegalArgumentException("N�o foi encontrada nenhuma a��o na heur�stica [" + getNome() + "]");
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
            throw new IllegalArgumentException("A Heur�stica [" + getNome() + "] n�o possui nenhuma A��o de Valor de Pe�a");
        }
    }

    @Override
    public String getNomeTipoComponente() {
        return "Heur�stica de Valor de Pe�as";
    }
    
    public ArrayList<AcaoValorPeca> getAcoesValorPeca(){
        return acoesValorPeca;
    }
}
