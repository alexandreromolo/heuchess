package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato;
import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato.Pontuacao;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.TableModelPadrao;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 6, 2013
 */
public class TableModelClassificacao extends TableModelPadrao {

    private Campeonato campeonato;
    
    public TableModelClassificacao(Campeonato campeonato) {        
        
        super(campeonato.classificacao());
        
        this.campeonato = campeonato;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Pontuacao pontuacao = (Pontuacao) linhas.get(rowIndex);
        
        switch (columnIndex) {
            case 0: // Orden
                return rowIndex + 1;
                
            case 1: //Autor
                return UtilsString.formataCaixaAltaBaixa(campeonato.autorConjuntoHeuristico(pontuacao.conjuntoHeuristico).getNome());                
                
            case 2: //Conjunto Heuristico
                return UtilsString.formataCaixaAltaBaixa(pontuacao.conjuntoHeuristico.getNome());                
                                
            case 3: // Pontos
                return new Integer(pontuacao.pontos);
                
            case 4: // Partidas
                return new Integer(pontuacao.quantidadePartidas);
                
            case 5: // Vitórias
                return new Integer(pontuacao.quantidadeVitorias);
                
            case 6: // Empates
                return new Integer(pontuacao.quantidadeEmpates);
                
            case 7: // Derrotas
                return new Integer(pontuacao.quantidadeDerrotas);
                
            default:
                return null;
        }
    }

    @Override
    protected String[] criarColunas() {        
        return new String[]{"Ordem", "Autor", "Conjunto Heurístico", "Pontos", "Partidas", "Vitórias", "Empates", "Derrotas"};
    }
}
