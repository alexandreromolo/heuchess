package br.ufpr.inf.utils.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class TaskItem {

    private final Icon iconFold;
    private final Icon iconUnfold;
    
    private final JComponent component;
    private final JPanel titleBar;
    private final JLabel title;
    private final JLabel foldingIcon;
    private final JPanel body;
    
    private final int bestWidth;
    private boolean   folded;

    public TaskItem(String titulo, boolean folded, final JComponent pComposant, final Icon pIcone, Icon iconFold, Icon iconUnfold) {

        assert pComposant != null;
        
        this.iconFold   = iconFold;
        this.iconUnfold = iconUnfold;

        titleBar = new JPanel(new BorderLayout(8, 0));
        titleBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        title = new JLabel(titulo);
        title.setFont(UIManager.getFont("InternalFrame.titleFont"));
        title.setIcon(pIcone);

        titleBar.add(title, BorderLayout.WEST);

        body = new JPanel(new BorderLayout());
        body.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        body.add(pComposant, BorderLayout.CENTER);

        foldingIcon = new JLabel();

        setFolded(folded);
        titleBar.add(foldingIcon, BorderLayout.EAST);

        setMouseOver(false);

        component = new JPanel(new BorderLayout());
        component.add(titleBar, BorderLayout.NORTH);
        component.add(body, BorderLayout.CENTER);

        Dimension dim = titleBar.getPreferredSize();
        int largeur   = dim.width;
        dim = body.getPreferredSize();
        if (dim.width > largeur) {
            largeur = dim.width;
        }
        bestWidth = largeur;

        titleBar.addMouseListener(new TaskItemListener());
    }
    
    public void setIcon(final Icon pIcone){
        title.setIcon(pIcone);
    }

    public int getBestWidth() {
        return bestWidth;
    }

    public JComponent getComponent() {
        
        assert component != null;
        
        return component;
    }

    public boolean isFolded() {
        return folded;
    }

    public final void setFolded(final boolean pPlie) {

        folded = pPlie;

        if (folded) {
            foldingIcon.setIcon(iconUnfold);
            body.setVisible(false);
        } else {
            foldingIcon.setIcon(iconFold);
            body.setVisible(true);
        }
    }

    public final void setMouseOver(final boolean pSurvol) {

        if (pSurvol) {
            titleBar.setBackground(UIManager.getColor("InternalFrame.activeTitleBackground"));
            title.setForeground(UIManager.getColor("InternalFrame.activeTitleForeground"));
        } else {
            titleBar.setBackground(UIManager.getColor("InternalFrame.inactiveTitleBackground"));
            title.setForeground(UIManager.getColor("InternalFrame.inactiveTitleForeground"));
        }
    }

    private final class TaskItemListener extends MouseAdapter {

        @Override
        public void mouseEntered(final MouseEvent pEvent) {

            assert pEvent != null;

            setMouseOver(true);
        }

        @Override
        public void mouseExited(final MouseEvent pEvent) {

            assert pEvent != null;

            setMouseOver(false);
        }

        @Override
        public void mousePressed(final MouseEvent pEvent) {

            assert pEvent != null;

            setFolded(!isFolded());
        }
    }
}
