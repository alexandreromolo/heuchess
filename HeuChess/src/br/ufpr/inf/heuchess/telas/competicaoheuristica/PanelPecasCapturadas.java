package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Peca.REI_PRETO;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class PanelPecasCapturadas implements PropertyChangeListener {

    private final JComponent   jComponent;
    private       JLabel[]     labels;
    
    private final Partida         game;
    private final DesenhaPecas desenhaPecas;
    
    private boolean whiteColor;
    
    private boolean usoCompartilhado;
    private int     quantidade;

    public PanelPecasCapturadas(Partida game, final DesenhaPecas desenhaPecas, boolean whiteColor) {

        assert game         != null;
        assert desenhaPecas != null;

        this.game         = game;
        this.desenhaPecas = desenhaPecas;
        this.whiteColor   = whiteColor;

        labels = new JLabel[16];

        JPanel jPanel = new JPanel(new GridLayout(1, labels.length)) {        
            @Override
            public Dimension getPreferredSize() {
                final Image img = desenhaPecas.getSmallImage(REI_PRETO);
                return new Dimension(img.getWidth(null), img.getHeight(null));
            }
        };
        
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            jPanel.add(labels[i]);
        }
        
        jPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        jComponent = jPanel;

        initCapturesPaint();

        game.addPropertyChangeListener("position", this);
    }
    
    public PanelPecasCapturadas(Partida game, final DesenhaPecas desenhaPecas) {

        assert game         != null;
        assert desenhaPecas != null;

        this.game = game;
        this.desenhaPecas = desenhaPecas;        
        
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            @Override
            public Dimension getPreferredSize() {
                return PanelPecasCapturadas.this.getPreferredSize();
            }
        };
        
        jPanel.setBorder(BorderFactory.createEtchedBorder());

        jComponent = jPanel;

        usoCompartilhado = true;
        
        game.addPropertyChangeListener("position", this);
    }

    public Dimension getPreferredSize() {        
        
        if (quantidade == 0){
           return new Dimension(desenhaPecas.getPecaWidth(), desenhaPecas.getPecaWidth());
        }else{            
           int maximo = jComponent.getWidth() / (desenhaPecas.getPecaWidth()/2 + 5);
           
           return new Dimension(desenhaPecas.getPecaWidth(), (quantidade <= maximo ? desenhaPecas.getPecaWidth() : desenhaPecas.getPecaWidth() * 2 - 5));
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent evento) {
        
        assert evento != null;
        
        initCapturesPaint();
    }

    public void setWhiteCaptured(boolean jogadorBranco) {
        
        whiteColor = jogadorBranco;
        
        initCapturesPaint();
    }
    
    public JComponent getComponent() {

        assert jComponent != null;

        return jComponent;
    }

    private void initCapturesPaint() {

        if (usoCompartilhado) {
            
            quantidade = 0;    
            
            jComponent.removeAll();
            
            for (Lance movimento : game.getLancesToCurrent()) {
                
                Peca pecaCapturada = movimento.getPecaCapturada();
                
                if (pecaCapturada != null && pecaCapturada.isWhite() == whiteColor) {                    
                    quantidade++;
                }
            }
            
            if (quantidade > 0){            
                
                labels = new JLabel[quantidade];
                
                int i = 0;

                for (Lance movimento : game.getLancesToCurrent()) {

                    Peca pecaCapturada = movimento.getPecaCapturada();

                    if (pecaCapturada != null && pecaCapturada.isWhite() == whiteColor) {
                        labels[i] = new JLabel();
                        labels[i].setIcon(new ImageIcon(desenhaPecas.getSmallImage(pecaCapturada)));
                        labels[i].setVisible(true);
                        jComponent.add(labels[i]);
                        i++;
                    }
                }
            }
            
            jComponent.repaint();
            
        } else {
            
            int i = 0;

            for (Lance m : game.getLancesToCurrent()) {

                Peca prise = m.getPecaCapturada();

                if ((prise != null) && (prise.isWhite() == whiteColor)) {
                    labels[i].setIcon(new ImageIcon(desenhaPecas.getSmallImage(prise)));
                    labels[i].setVisible(true);
                    i++;
                }
            }

            while (i < labels.length) {
                labels[i].setVisible(false);
                i++;
            }
        }
    }
}