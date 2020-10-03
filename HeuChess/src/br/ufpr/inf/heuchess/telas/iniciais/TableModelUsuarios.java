package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.gui.TableModelPadrao;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Jul 31, 2012
 */
public class TableModelUsuarios extends TableModelPadrao {

    public TableModelUsuarios(ArrayList<Usuario> usuarios) {
        super(usuarios);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Usuario usuario = (Usuario) linhas.get(rowIndex);
        
        switch (columnIndex) {
            case 0: 
                return rowIndex + 1;
            case 1:
                return usuario.getNome();
            case 2:
                return (usuario.isSexoMasculino() ? "Masculino" : "Feminino");
            case 3:
                return UtilsDataTempo.formataData(usuario.getDataNascimento());                        
            default:
                return null;
        }
    }

    @Override
    protected String[] criarColunas() {
        return new String[]{"Nr.", "Nome", "Sexo", "Nascimento"};
    }    
}