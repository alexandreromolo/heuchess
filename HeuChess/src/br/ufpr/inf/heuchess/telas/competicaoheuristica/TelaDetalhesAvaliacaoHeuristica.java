package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.competicaoheuristica.Engine;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Frame;
import javax.swing.ImageIcon;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 20, 2012
 */
public class TelaDetalhesAvaliacaoHeuristica extends javax.swing.JFrame implements ModalFrameHierarchy {
    
    private ModalFrameHierarchy modalFramerameAnterior;
        
    public TelaDetalhesAvaliacaoHeuristica(ModalFrameHierarchy modalFramerameAnterior, Engine engine, Tabuleiro tabuleiro, boolean whiteColor) {
        
        this.modalFramerameAnterior = modalFramerameAnterior;        
        
        initComponents();
        
        try {        
            jLabelAvaliacao.setText("Avalia��o da Situa��o de Jogo - \"" + tabuleiro.getFEN() + "\"");
        
            engine.getAvaliador().avalia(tabuleiro, jTextPaneAvaliacao, whiteColor);

            jTextPaneAvaliacao.setCaretPosition(0);            
            ModalFrameUtil.showAsModalDontBlock(this);
            
            jButtonFechar.requestFocus();
            
        } catch (Exception e) {
            HeuChess.registraExcecao(e);            
            
            int posicao = jTextPaneAvaliacao.getCaretPosition();
            
            jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n\nExce��o:\n\n" + e.getMessage());
            
            jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n\nPilha de Execuca��o:\n");
            for (StackTraceElement track : e.getStackTrace()){
                jTextPaneAvaliacao.setText(jTextPaneAvaliacao.getText() + "\n" + track.toString());
            }
            
            jTextPaneAvaliacao.setCaretPosition(posicao);
            ModalFrameUtil.showAsModalDontBlock(this);            
            
            UtilsGUI.dialogoErro(this, "Erro ao realizar Avalia��o Heur�stica do Lance Selecionado");            
        }        
    }
    
    @Override
    public Frame getFrame() {
        return this;
    }

    @Override
    public ModalFrameHierarchy getModalOwner() {
        return modalFramerameAnterior;
    }

    private void fechar(){        
        dispose();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonAjuda = new javax.swing.JButton();
        jButtonFechar = new javax.swing.JButton();
        jPanelCentral = new javax.swing.JPanel();
        jLabelAvaliacao = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneAvaliacao = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Detalhes da Avalia��o Heur�stica");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_detalhes_avaliacao_heuristica.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setMnemonic('a');
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        jButtonFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jButtonFechar.setMnemonic('f');
        jButtonFechar.setText("Fechar");
        jButtonFechar.setToolTipText("Fecha esta janela");
        jButtonFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
            }
        });

        jPanelCentral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelAvaliacao.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelAvaliacao.setText("Avalia��o da Situa��o de Jogo -");

        jTextPaneAvaliacao.setEditable(false);
        jScrollPane2.setViewportView(jTextPaneAvaliacao);

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabelAvaliacao, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelCentralLayout.setVerticalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAvaliacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonFechar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAjuda)
                    .addComponent(jButtonFechar))
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-708)/2, (screenSize.height-527)/2, 708, 527);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaDetalhesAvaliacaoHeuristica");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        fechar();
    }//GEN-LAST:event_jButtonFecharActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        fechar();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JLabel jLabelAvaliacao;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPaneAvaliacao;
    // End of variables declaration//GEN-END:variables
}
