package br.ufpr.inf.utils.gui;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  May 28, 2012
 */
public class UtilsGUI {

    /**
        jTextFieldNomeConjuntoHeuristico.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                // text was changed
                verificaAtualizacaoNome();
            }

            public void removeUpdate(DocumentEvent e) {
                // text was deleted
                verificaAtualizacaoNome();
            }

            public void insertUpdate(DocumentEvent e) {
                // text was inserted
                verificaAtualizacaoNome();
            }
        });
        */
    
    public static void atualizaTela(Component c){
        
        try {
            javax.swing.SwingUtilities.updateComponentTreeUI(c);            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public static int dialogoConfirmacao(Component pai, String mensagem, String titulo){
       
        /**
          
        Toolkit.getDefaultToolkit().beep();
        
        JButton botoes[] = new JButton[2];
        botoes[0] = new JButton("Sim");
        botoes[0].setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png")));
        botoes[1] = new JButton("Não"); 
        botoes[1].setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png")));
        
        botoes[0].addActionListener(new ActionListener(){
                                     public void actionPerformed(ActionEvent e){
                                         System.exit(0);
                                     } 
                                    });
       
        botoes[1].addActionListener(new ActionListener(){
                                     public void actionPerformed(ActionEvent e){                                                 
                                         ativarInterface(true);                                         
                                         JButton botao = (JButton) e.getSource();
                                         Container container = botao.getFocusCycleRootAncestor();                                         
                                         container.setVisible(false);                                          
                                     } 
                                    });           
                                    
        int resposta = JOptionPane.showOptionDialog(this,
                                                    "Deseja Realmente Sair?",
                                                    "Confirmação de Saída",
                                                    JOptionPane.DEFAULT_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE,                                                    
                                                    null,
                                                    botoes,
                                                    botoes[0]);
         
        if (resposta == -1){
            ativarInterface(true);
        }
        */
        
        Toolkit.getDefaultToolkit().beep();
        
        String[] opcoes = {"Sim","Não"}; 
        
        int resposta = JOptionPane.showOptionDialog(pai,
                                                    mensagem,
                                                    titulo,
                                                    JOptionPane.DEFAULT_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE,                                                    
                                                    null,
                                                    opcoes,
                                                    opcoes[0]);        
        return resposta;
    }
    
    public static void dialogoErro(Component pai, String mensagem){        
        Toolkit.getDefaultToolkit().beep();        
        JOptionPane.showMessageDialog(pai, mensagem, "Aviso de Erro", JOptionPane.ERROR_MESSAGE);                
    }
    
    public static void dialogoAtencao(Component pai, String mensagem){        
        Toolkit.getDefaultToolkit().beep();        
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);                
    }
    
    /**
     * Desenha o texto centralizado dentro de uma caixa pré-fixada
     */
    public static int drawText(Graphics g, String msg, int x_box, int y_box,  int box_width, int box_height, int fixed_type_size_value, int relative_position) {
        
        boolean fixed_type_size = false;
        int type_size = 24;
        
        // Fixed to a particular type size
        
        if (fixed_type_size_value > 0) {
            fixed_type_size = true;
            type_size = fixed_type_size_value;
        }
        
        int type_size_min = 8;
        int x = x_box,y = y_box;
        
        do  {
            // Create the font and pass it to the  Graphics context
            
            g.setFont(new Font("Monospaced",Font.PLAIN,type_size));
            
            // Get measures needed to center the message
            
            FontMetrics fm = g.getFontMetrics();
            
            // How many pixels wide is the string
            
            int msg_width = fm.stringWidth(msg);
            
            // How tall is the text?
            
            int msg_height = fm.getHeight();
            
            // See if the text will fit in the allotted
            // vertical limits
            
            if (msg_height < box_height && msg_width < box_width) {
                y = y_box + box_height/2 + (msg_height/2);
                if (relative_position == SwingConstants.CENTER)
                    x = x_box + box_width/2 -  (msg_width/2);
                else if (relative_position == SwingConstants.RIGHT)
                    x = x_box + box_width - msg_width;
                else
                    x = x_box;
                break;
            }
            
            // If fixedTypeSize and wouldn't fit, don't draw.
            
            if (fixed_type_size) return -1;
            
            // Try smaller type
            
            type_size -= 2;
            
        } while (type_size >= type_size_min);
        
        // Don't display the numbers if they did not fit
        
        if (type_size < type_size_min){ 
          return -1;
        }
        
        // Otherwise, draw and return positive signal.
        
        g.drawString(msg,x,y);
        
        return type_size;
    }
    
    public static void centerFrame(JFrame frame) {
        
         /* Atualmente se usa o  método
            frame.setLocationRelativeTo(null);  */
        
         Dimension paneSize = frame.getSize();
         
         Dimension screenSize = frame.getToolkit().getScreenSize();
         frame.setLocation( (screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
    }
    
    public static void adicionaTextoComFormato(JTextPane pane, String palavra, AttributeSet set){  
        
        Document doc = pane.getStyledDocument();  
        
        try {  
            doc.insertString(doc.getLength(),palavra, set);  
        } catch (BadLocationException e) {  
            e.printStackTrace(System.err);
        }  
    }  
    
    public static void moverScrollbarInicio(JScrollPane jScrollPane){
        JScrollBar vertical = jScrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMinimum());
    }
    
    public static void moverScrollbarFim(JScrollPane jScrollPane){        
        JScrollBar vertical = jScrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    public static void centralizaAutoValidaValorJSpinner(JSpinner jSpinner, String mascara){
        
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) jSpinner.getEditor();
        JFormattedTextField field = editor.getTextField();
        
        field.setHorizontalAlignment(JFormattedTextField.CENTER);
        
        NumberFormatter formatter = (NumberFormatter) field.getFormatter();  
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        if (mascara != null){
            DecimalFormat decimalFormat = new DecimalFormat(mascara);
            formatter.setFormat(decimalFormat);
        }
    }
}