package br.ufpr.inf.utils.gui;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Jul 31, 2012
 */
public abstract class TableModelPadrao extends AbstractTableModel {

    protected String[] colunas;
    protected List     linhas;

    public TableModelPadrao(List linhas) {
        this.colunas = criarColunas();
        this.linhas  = linhas;
    }

    protected abstract String[] criarColunas();

    public String[] getColunas() {
        return colunas;
    }

    public List getLinhas() {
        return linhas;
    }

    public void updateLinha(int linhaInicial, int linhaFinal){
        fireTableRowsUpdated(linhaInicial, linhaFinal);
    }
    
    public void update() {        
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {

        if (linhas != null) {

            return linhas.size();

        } else {

            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public String getColumnName(int col) {
        return colunas[col];
    }
}
