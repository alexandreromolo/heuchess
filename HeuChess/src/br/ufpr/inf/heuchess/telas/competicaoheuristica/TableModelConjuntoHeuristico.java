package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.TableModelPadrao;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 4, 2013
 */
public class TableModelConjuntoHeuristico extends TableModelPadrao {

    private Campeonato campeonato;
    
    public TableModelConjuntoHeuristico(Campeonato campeonato) {        
        super(campeonato.conjuntosHeuristicos());
        this.campeonato = campeonato;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        ConjuntoHeuristico conjuntoHeuristico = (ConjuntoHeuristico) linhas.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return UtilsString.formataCaixaAltaBaixa(campeonato.autorConjuntoHeuristico(conjuntoHeuristico).getNome());
            case 2:
                return UtilsString.formataCaixaAltaBaixa(conjuntoHeuristico.getNome());
            case 3:
                return UtilsString.formataCaixaAltaBaixa(conjuntoHeuristico.getTipo().getNome());
            default:
                return null;
        }
    }

    @Override
    protected String[] criarColunas() {
        return new String[]{"Nr.", "Autor", "Nome", "Tipo"};
    }
}
