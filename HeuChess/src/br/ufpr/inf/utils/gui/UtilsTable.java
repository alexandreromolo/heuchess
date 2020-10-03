package br.ufpr.inf.utils.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  May 28, 2012
 */
public class UtilsTable {

    /**
     * Configura todas as colunas da tabela para o menor tamanho possível
     */
    public static void packColumns(JTable table, int margin) {
        
        for (int c=0; c<table.getColumnCount(); c++) {
            packColumn(table, c, margin);
        }
    }

    /**
     * Define a largura da coluna como a menor possível, permitindo ver os dados
     */
    public static void packColumn(JTable table, int vColIndex, int margin) {
        
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width;
    
        // Get width of column header //
        
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(
            table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;
    
        // Get maximum width of column data //
        
        for (int r=0; r<table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }
    
        // Add margin //
        
        width += 2*margin;
    
        // Set the width //
        
        col.setPreferredWidth(width);
    }
    
    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Point pt       = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }
}
