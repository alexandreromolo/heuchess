package br.ufpr.inf.utils.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;

public class TaskPanel {

    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    
    private final JComponent jComponent;
    private final JComponent taskPanel;
    private       JComponent lastContainer;

    public TaskPanel() {

        lastContainer = new JPanel(new BorderLayout(0, 8));
        lastContainer.setBackground(BACKGROUND_COLOR);
        lastContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        taskPanel = lastContainer;

        jComponent = new JScrollPane(taskPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public void add(final TaskItem pTache) {

        assert pTache != null;

        lastContainer.add(pTache.getComponent(), BorderLayout.NORTH);

        final Dimension taillePanel = jComponent.getSize();
        final int largeurComposant  = pTache.getBestWidth() + 24;

        if (largeurComposant > taillePanel.width) {
            taillePanel.width = largeurComposant;
            jComponent.setSize(taillePanel);
            jComponent.setPreferredSize(taillePanel);
        }

        final JPanel suivant = new JPanel(new BorderLayout(0, 4));// antes era 0, 8
        suivant.setBackground(BACKGROUND_COLOR);
        lastContainer.add(suivant, BorderLayout.CENTER);

        lastContainer = suivant;
    }

    public JComponent getComponent() {

        assert jComponent != null;

        return jComponent;
    }
}