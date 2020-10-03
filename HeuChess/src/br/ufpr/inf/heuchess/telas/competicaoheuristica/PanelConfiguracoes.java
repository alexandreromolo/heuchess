package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public class PanelConfiguracoes extends javax.swing.JPanel {

    private RenderTreeJogadas    renderTreeJogadas;
    private TelaPartidaXadrez telaAnalisePartidaXadrez;
    
    public PanelConfiguracoes(TelaPartidaXadrez telaAnalisePartidaXadrez, RenderTreeJogadas renderTreeJogadas, boolean manual) {
        
        this.telaAnalisePartidaXadrez = telaAnalisePartidaXadrez;
        this.renderTreeJogadas        = renderTreeJogadas;
        
        initComponents();
        
        if (!manual){
            jCheckBoxMostrarSAN.setVisible(false);
            jCheckBoxMostrarTotalFilhos.setVisible(false);
            jCheckBoxMostrarValorNode.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBoxMostrarSAN = new javax.swing.JCheckBox();
        jCheckBoxInverterTabuleiro = new javax.swing.JCheckBox();
        jCheckBoxHabilitarTocarSons = new javax.swing.JCheckBox();
        jCheckBoxMostrarValorNode = new javax.swing.JCheckBox();
        jCheckBoxMostrarTotalFilhos = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jCheckBoxMostrarSAN.setSelected(renderTreeJogadas.getUsarNotacaoSAN());
        jCheckBoxMostrarSAN.setText("Lances em Nota��o Alg�brica");
        jCheckBoxMostrarSAN.setToolTipText("Exibe os movimentos da �rvore de Lances em Nota��o Alg�brica");
        jCheckBoxMostrarSAN.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMostrarSANItemStateChanged(evt);
            }
        });

        jCheckBoxInverterTabuleiro.setSelected(telaAnalisePartidaXadrez.isFlipTabuleiroPretas());
        jCheckBoxInverterTabuleiro.setText("Inverter Tabuleiro para Pretas");
        jCheckBoxInverterTabuleiro.setToolTipText("Inverte a exibi��o dos tabuleiros para lances realizados pelas Pretas");
        jCheckBoxInverterTabuleiro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxInverterTabuleiroItemStateChanged(evt);
            }
        });

        jCheckBoxHabilitarTocarSons.setSelected(HeuChess.somAtivado);
        jCheckBoxHabilitarTocarSons.setText("Tocar Sons");
        jCheckBoxHabilitarTocarSons.setToolTipText("Toca os sons associdados as movimenta��es das pe�as");
        jCheckBoxHabilitarTocarSons.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxHabilitarTocarSonsItemStateChanged(evt);
            }
        });

        jCheckBoxMostrarValorNode.setSelected(renderTreeJogadas.getMostrarValorNode());
        jCheckBoxMostrarValorNode.setText("Mostrar Valor das Ra�zes");
        jCheckBoxMostrarValorNode.setToolTipText("Mostra os valores encontrados para cada ramo da �rvore de Lances");
        jCheckBoxMostrarValorNode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMostrarValorNodeItemStateChanged(evt);
            }
        });

        jCheckBoxMostrarTotalFilhos.setSelected(renderTreeJogadas.getMostrarTotalFilhos());
        jCheckBoxMostrarTotalFilhos.setText("Mostrar Total de Filhos");
        jCheckBoxMostrarTotalFilhos.setToolTipText("Mostra o total de lances derivados (Filhos) em cada ramo da �rvore de Lances");
        jCheckBoxMostrarTotalFilhos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMostrarTotalFilhosItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxMostrarValorNode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxMostrarSAN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxInverterTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(jCheckBoxHabilitarTocarSons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxMostrarTotalFilhos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jCheckBoxMostrarSAN)
                .addGap(0, 0, 0)
                .addComponent(jCheckBoxMostrarValorNode)
                .addGap(0, 0, 0)
                .addComponent(jCheckBoxMostrarTotalFilhos)
                .addGap(0, 0, 0)
                .addComponent(jCheckBoxInverterTabuleiro)
                .addGap(0, 0, 0)
                .addComponent(jCheckBoxHabilitarTocarSons)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxMostrarSANItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMostrarSANItemStateChanged
        renderTreeJogadas.setUsarNotacaoSAN(jCheckBoxMostrarSAN.isSelected());
        telaAnalisePartidaXadrez.atualizaTreeArvoresLances();        
    }//GEN-LAST:event_jCheckBoxMostrarSANItemStateChanged

    private void jCheckBoxHabilitarTocarSonsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxHabilitarTocarSonsItemStateChanged
        HeuChess.somAtivado = jCheckBoxHabilitarTocarSons.isSelected();
    }//GEN-LAST:event_jCheckBoxHabilitarTocarSonsItemStateChanged

    private void jCheckBoxInverterTabuleiroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxInverterTabuleiroItemStateChanged
        telaAnalisePartidaXadrez.setFlipTabuleiroPretas(jCheckBoxInverterTabuleiro.isSelected());
    }//GEN-LAST:event_jCheckBoxInverterTabuleiroItemStateChanged

    private void jCheckBoxMostrarValorNodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMostrarValorNodeItemStateChanged
        renderTreeJogadas.setMostrarValorNode(jCheckBoxMostrarValorNode.isSelected());
        telaAnalisePartidaXadrez.atualizaTreeArvoresLances();        
    }//GEN-LAST:event_jCheckBoxMostrarValorNodeItemStateChanged

    private void jCheckBoxMostrarTotalFilhosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMostrarTotalFilhosItemStateChanged
        renderTreeJogadas.setMostrarTotalFilhos(jCheckBoxMostrarTotalFilhos.isSelected());
        telaAnalisePartidaXadrez.atualizaTreeArvoresLances();
    }//GEN-LAST:event_jCheckBoxMostrarTotalFilhosItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxHabilitarTocarSons;
    private javax.swing.JCheckBox jCheckBoxInverterTabuleiro;
    private javax.swing.JCheckBox jCheckBoxMostrarSAN;
    private javax.swing.JCheckBox jCheckBoxMostrarTotalFilhos;
    private javax.swing.JCheckBox jCheckBoxMostrarValorNode;
    // End of variables declaration//GEN-END:variables
}
