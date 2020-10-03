package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.TableModelPadrao;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 4, 2013
 */
public class TableModelPartidas extends TableModelPadrao {

    private Campeonato campeonato;
    
    public TableModelPartidas(Campeonato campeonato) {        
        super(campeonato.partidas());
        
        this.campeonato = campeonato;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Partida partida = (Partida) linhas.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
                
            case 1:{    
                    ConjuntoHeuristico conjuntoBrancas = partida.getJogadorBranco().getConjuntoHeuristico();
                    Usuario autorBrancas = campeonato.autorConjuntoHeuristico(conjuntoBrancas);
                    return UtilsString.formataCaixaAltaBaixa(autorBrancas.getNome());
                }
                
            case 2:
                ConjuntoHeuristico conjuntoBrancas = partida.getJogadorBranco().getConjuntoHeuristico();                
                return UtilsString.formataCaixaAltaBaixa(conjuntoBrancas.getNome());
                
            case 3:{
                    ConjuntoHeuristico conjuntoPretas = partida.getJogadorPreto().getConjuntoHeuristico();
                    Usuario autorPretas = campeonato.autorConjuntoHeuristico(conjuntoPretas);
                    return UtilsString.formataCaixaAltaBaixa(autorPretas.getNome());
                }
                
            case 4:    
                ConjuntoHeuristico conjuntoPretas = partida.getJogadorPreto().getConjuntoHeuristico();                
                return UtilsString.formataCaixaAltaBaixa(conjuntoPretas.getNome());
                                
            case 5:
                return partida.getState().getDescricao();
                
            default:
                return null;
        }
    }

    @Override
    protected String[] criarColunas() {
        return new String[]{"Nr.", "Autor Brancas", "Brancas", "Autor Pretas", "Pretas", "Situação"};
    }
}
