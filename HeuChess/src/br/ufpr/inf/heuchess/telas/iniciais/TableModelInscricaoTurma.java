package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.representacao.organizacao.InscricaoTurma;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.gui.TableModelPadrao;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public class TableModelInscricaoTurma extends TableModelPadrao {

    public TableModelInscricaoTurma(ArrayList<InscricaoTurma> inscricoes) {
        super(inscricoes);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        InscricaoTurma inscricaoTurma = (InscricaoTurma) linhas.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return inscricaoTurma.getUsuario().getNome();
            case 2:
                return (inscricaoTurma.getUsuario().isSexoMasculino() ? "Masculino" : "Feminino");
            case 3:
                return UtilsDataTempo.formataData(inscricaoTurma.getDataCriacao());
            default:
                return null;
        }
    }

    @Override
    protected String[] criarColunas() {
        return new String[]{"Nr.", "Nome", "Sexo", "Data Inscrição"};
    }
}
