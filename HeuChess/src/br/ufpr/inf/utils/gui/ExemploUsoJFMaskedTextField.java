package br.net.sercomtel.eti.util.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExemploUsoJFMaskedTextField extends JFrame{
    
    JFMaskedTextField texto9;
    
    private static final long serialVersionUID = 1L;
    
    public ExemploUsoJFMaskedTextField(){
        super("Exemplo de Uso do JFMaskedTextField");
         
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8,2,10,10));
        
        JLabel label1 = new JLabel("Apenas Letras, Normais, Colunas = 20, Qtd. Máxima = Nenhum");
        JFMaskedTextField texto1 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_SOMENTE_LETRAS,JFMaskedTextField.NAO_CONVERTE_ENTRADA);
        texto1.setQuantidadeColunas(20);
        JLabel label2 = new JLabel("Apenas Letras, Maiúsculas, Colunas = 20, Qtd. Máxima = 15");
        JFMaskedTextField texto2 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_SOMENTE_LETRAS,JFMaskedTextField.CONVERTE_ENTRADA_MAIUSCULAS,15,20);
        JLabel label3 = new JLabel("Apenas Letras, Minúsculas, Colunas = 20, Qtd.Máxima = 15");
        JFMaskedTextField texto3 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_SOMENTE_LETRAS,JFMaskedTextField.CONVERTE_ENTRADA_MINUSCULAS,15,20);
        JLabel label4 = new JLabel("Apenas Números, Minusculas, Colunas = 20, Qtd. Máxima = 15");
        JFMaskedTextField texto4 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_SOMENTE_NUMEROS,JFMaskedTextField.CONVERTE_ENTRADA_MINUSCULAS,15,20);
        JLabel label5 = new JLabel("Apenas Letras e Números, Maísculas, Colunas = 20, Qtd.Máxima = 15");
        JFMaskedTextField texto5 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_SOMENTE_LETRAS_NUMEROS,JFMaskedTextField.CONVERTE_ENTRADA_MAIUSCULAS,15,20);
        JLabel label6 = new JLabel("Qualquer Entrada, Minúsculas, Colunas = 20, Qtd. Máxima = 15");
        JFMaskedTextField texto6 = new JFMaskedTextField(JFMaskedTextField.ENTRADA_QUALQUER_CARACTER,JFMaskedTextField.CONVERTE_ENTRADA_MINUSCULAS,15,20);
        
        panel.add(label1);
        panel.add(texto1);
        panel.add(label2);
        panel.add(texto2);
        panel.add(label3);
        panel.add(texto3);
        panel.add(label4);
        panel.add(texto4);
        panel.add(label5);
        panel.add(texto5);
        panel.add(label6);
        panel.add(texto6);
        
        try {
            JLabel label7 = new JLabel("Mascara de Entrada \"##/##/####\", Caracter Marcador = '_', Colunas = 20");
            JFMaskedTextField texto7 = new JFMaskedTextField("##/##/####",'_',20);
            panel.add(label7);
            panel.add(texto7);
        } catch (ParseException e1) {
            e1.printStackTrace(System.err);
        }
        
        try {
            JLabel label8 = new JLabel("Mascara de Entrada \"(##)####-####\", Caracter Marcador = ' ', Colunas = 20");
            JFMaskedTextField texto8 = new JFMaskedTextField("(AA)AAAA-AAAA",'*',20);
            texto8.setValorInicial("(43)9999-9999");
            texto8.setCaracteresValidosEntrada("0123456789");
            texto8.setHorizontalAlignment(JTextField.CENTER);
            panel.add(label8);
            panel.add(texto8);
        } catch (ParseException e) {
            e.printStackTrace(System.err);
        }
        
        //////////////////////////////////////////////////////////////////////
        // Exemplo de Uso tradiconal e direto da classe JFormattedTextField //
        //////////////////////////////////////////////////////////////////////
                /*
                JLabel label8 = new JLabel("Mascara de Entrada \"##/##/####\",Colunas = 20");
                JFormattedTextField texto8 = null;
                try{
                         MaskFormatter format = new MaskFormatter("##/##/####");
                         format.setPlaceholderCharacter('_');
                         format.setAllowsInvalid(false);
                         texto8 = new JFormattedTextField(format);
                }catch(Exception e){
                 
                }
                texto8.setHorizontalAlignment(JTextField.CENTER);
                 */
        
                /*
                JLabel label9 = new JLabel("simpledateformat");
                JFMaskedTextField texto9 = new JFMaskedTextField(new SimpleDateFormat("DD'/'MM'/'yyyy"));
                texto9.setHorizontalAlignment(JTextField.CENTER);
                panel.add(label9);
                panel.add(texto9);
                texto9.addKeyListener(new KeyAdapter(){
                        public void keyPressed(KeyEvent e) {
                                try {
                                        ExemploUsoJFMaskedTextField.this.texto9.commitEdit();
                                } catch (ParseException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace(System.err);
                                }
                        }
                });
                 
                 
                ////////
                                                SimpleDateFormat formato = new SimpleDateFormat("DD'/'MM'/'yyyy");
                                                try {
                                                        formato.parse(str);
                                                } catch (ParseException e) {
                                                        e.printStackTrace(System.err);
                                                        JOptionPane.showMessageDialog(null,"valor de data invalido");
                                                        Toolkit.getDefaultToolkit().beep();
                                                        return;
                                                }
                                                ////////
                 
                 */
        
        getContentPane().add(panel);
        setSize(800,300);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width/2)-(this.getWidth()/2),(dim.height/2)-(this.getHeight()/2));        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        ExemploUsoJFMaskedTextField principal = new ExemploUsoJFMaskedTextField();
        principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
