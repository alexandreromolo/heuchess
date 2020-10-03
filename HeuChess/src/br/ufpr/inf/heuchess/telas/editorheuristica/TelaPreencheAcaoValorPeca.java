package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG.TipoDado;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.CardLayout;
import java.awt.Frame;
import javax.swing.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 28 de Junho de 2006, 14:55
 */
public class TelaPreencheAcaoValorPeca extends javax.swing.JFrame implements AcessoTelaPreencheFuncao, AcessoTelaEscolheFuncao {
          
    public  TelaHeuristica telaHeuristica;   
    
    private AcaoValorPeca         acaoValorPeca;    
    private AcaoValorPeca         acaoValorPecaOriginal; 
    
    private ModelListaComponentes  model;
    private int                   posicaoOriginal;
    
    private boolean nova;
    
    private CardLayout           cardPrincipal;
    private CardLayout           cardParametros;
    private int                  passoAtual;
    
    private boolean iniciandoAutomaticoInvisivel = false;
    
    /**
     * Construtor chamado quando se está criando uma nova Ação de Valor de Peça
     */
    public TelaPreencheAcaoValorPeca(TelaHeuristica telaHeuristica, ModelListaComponentes model) {           
        
        this.telaHeuristica = telaHeuristica;
        this.model = model;
        
        nova       = true;
        passoAtual = 1;
        
        montarInterface();
        
        iniciandoAutomaticoInvisivel = true;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheAcaoValorPeca.this,TipoDado.PECAS);
            }
        });
    }
        
    /**
     * Construtor chamado quando se está abrindo uma Ação de Valor de Peça que já existente
     */
    public TelaPreencheAcaoValorPeca(TelaHeuristica telaHeuristica, ModelListaComponentes model, int posicao){
        
        this.telaHeuristica = telaHeuristica;
        this.model = model;
        
        nova       = false;
        passoAtual = 1;
        
        iniciandoAutomaticoInvisivel = false;
        
        //////////////////////////////////////////////////////
        // Copia os dados da Ação de Valor de Peca Original //
        //////////////////////////////////////////////////////
        
        posicaoOriginal = posicao;
        
        try{
            acaoValorPecaOriginal = (AcaoValorPeca) model.get(posicao);         
            acaoValorPeca = acaoValorPecaOriginal.geraClone();
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(telaHeuristica.getFrame(),"Erro ao criar cópia da Ação de Valor de Peça!\n" + e.getMessage());
            dispose();
            return;
        }
        
        montarInterface();
        
        adaptaInterface(); 
        
        ModalFrameUtil.showAsModalDontBlock(this);
    }
    
    private void montarInterface(){
        
        initComponents();
        
        cardPrincipal  = (CardLayout) jPanelPrincipal.getLayout();
        cardParametros = (CardLayout) jPanelEntadaDados.getLayout();
        
        cardPrincipal.show(jPanelPrincipal, "Operando1");
        
        jButtonAnterior.setVisible(false);
        
        if (!telaHeuristica.panelEtapa.editor.podeAlterar()){
            jButtonEscolherFuncaoOperando1.setVisible(false);
            jButtonAlterarParametrosOperando1.setVisible(false);
         
            jComboBoxOperadorMatematico.setEnabled(false);
            
            jRadioButtonPreencherValor.setEnabled(false);
            jRadioButtonPreencherFuncao.setEnabled(false);
            
            jSpinnerValorReal.setEnabled(false);
            
            jButtonEscolherFuncaoOperando2.setVisible(false);
            jButtonAlterarParametrosOperando2.setVisible(false);
            
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
    }
    
    private void adaptaInterface() {        

        if (passoAtual == 1) {

            jTextAreaFuncaoPreenchidaOperando1.setText(((FuncaoPreenchida) acaoValorPeca.getValorOperando1()).toString());
            
            if (((FuncaoPreenchida) acaoValorPeca.getValorOperando1()).totalParametros() != 0) {
                jButtonAlterarParametrosOperando1.setEnabled(true);
            } else {
                jButtonAlterarParametrosOperando1.setEnabled(false);
            }
            
        } else 
            if (passoAtual == 3) {
                montaPanelOperando2();
            }       
    }          
    
    @Override
    public Frame getFrame() {
        
        if (iniciandoAutomaticoInvisivel) {
            return telaHeuristica.getFrame();
        } else {
            return this;
        }
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner() {
        
        if (iniciandoAutomaticoInvisivel) {
            return telaHeuristica.getModalOwner();
        } else {
            return telaHeuristica;
        }
    }
    
    @Override
    public void fechandoTelaEscolheFuncao(final Funcao funcaoEscolhida){
        
        if (funcaoEscolhida != null) {
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica, TelaPreencheAcaoValorPeca.this, funcaoEscolhida, null);
                }
            });
            
        }else {
            if (iniciandoAutomaticoInvisivel) {
                dispose();
                telaHeuristica.fechandoTelaPreencheAcaoValorPeca(false);
            }
        }
    }
        
    @Override
    public void fechandoTelaPreencheFuncao(FuncaoPreenchida funcaoPreenchida, Object elemento){        
        
        if (funcaoPreenchida != null) {

            if (acaoValorPeca == null) {
                acaoValorPeca = new AcaoValorPeca(telaHeuristica.heuristica,funcaoPreenchida);
            } else {
                
                try{
                    if (passoAtual == 1) {
                        acaoValorPeca.setValorOperando1(funcaoPreenchida);
                    } else 
                        if (passoAtual == 3) {
                            acaoValorPeca.setValorOperando2(funcaoPreenchida);
                        }                    
                }catch(Exception e){
                    HeuChess.registraExcecao(e);
                    UtilsGUI.dialogoErro(this,"Erro ao preencher parâmetro da Ação Valor Peça com Função!\n" + e.getMessage());
                    return;
                }
            }

            adaptaInterface();
        }
        
        if (iniciandoAutomaticoInvisivel) {
            
            if (funcaoPreenchida == null) {
                dispose();
                telaHeuristica.fechandoTelaPreencheAcaoValorPeca(false);
            } else {
                iniciandoAutomaticoInvisivel = false;
                ModalFrameUtil.showAsModalDontBlock(this);
            }
        }
    }
    
    public void confirmaCancelar(){
        
        if (telaHeuristica.panelEtapa.editor.podeAlterar()) {
            
            boolean sofreuAlteracao;

            if (nova) {
                sofreuAlteracao = true;
            } else {

                try {
                    salvarEntrada();

                    sofreuAlteracao = !acaoValorPeca.toString().equals(acaoValorPecaOriginal.toString());

                } catch (Exception e) {
                    HeuChess.registraExcecao(e);
                    sofreuAlteracao = true;
                }
            }

            if (sofreuAlteracao) {
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?", "Confirmação Cancelamento");
                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }
            }
        }
        
        dispose();
        telaHeuristica.fechandoTelaPreencheAcaoValorPeca(false);        
    }
   
    private void configuraDescricaoOperador(){
        
        DHJOG.OperadorMatematico operador = (DHJOG.OperadorMatematico) jComboBoxOperadorMatematico.getSelectedItem();
        
        if (operador != null){
            
            switch(operador){
                
                case MAIS:
                    jTextAreaDescricaoOperadorMatematico.setText("Somar um valor a cada peça localizada");
                    break;
                    
                case MENOS:
                    jTextAreaDescricaoOperadorMatematico.setText("Subtrair um valor de cada peça localizada");
                    break;
                    
                case MULTIPLICACAO:
                    jTextAreaDescricaoOperadorMatematico.setText("Multiplicar o valor de cada peça localizada");
                    break;
                    
                case DIVISAO:
                    jTextAreaDescricaoOperadorMatematico.setText("Dividir o valor de cada peça localiza");
                    break;
                    
                default:    
                    throw new IllegalArgumentException("Operador Matemático não suportado [" + operador + "]");
            }
        }else{
            jTextAreaDescricaoOperadorMatematico.setText(null);
        }
    }
                     
    private void montaPanelOperando2(){    
        
        ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
        
        jTextFieldTipoOperando2.setText(operando2.getTipo().toString());
        
        Object valor = operando2.getValor();
        
        if (operando2.isPreenchidaValor()){
            
            jRadioButtonPreencherValor.setSelected(true);
            
            switch(operando2.getTipo()){
                    
                case REAL:
                    cardParametros.show(jPanelEntadaDados,"Real");    
                            
                    if (valor instanceof Double){                            
                        jSpinnerValorReal.setValue(valor);
                    }                    
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo de dado não suportado [" + operando2.getTipo() + "]");
            }
            
        }else{
            jRadioButtonPreencherFuncao.setSelected(true);
            
            cardParametros.show(jPanelEntadaDados,"Funcao");
            
            if (valor instanceof FuncaoPreenchida){
                
                jTextAreaFuncaoPreenchidaOperando2.setText(((FuncaoPreenchida)valor).toString());                
                
                if (((FuncaoPreenchida)valor).totalParametros() != 0){
                    jButtonAlterarParametrosOperando2.setEnabled(true);
                }else{
                    jButtonAlterarParametrosOperando2.setEnabled(false);
                }
                
                jButtonEscolherFuncaoOperando2.setText("Trocar Função");
                jButtonDetalhesFuncaoOperando2.setEnabled(true);
                
            }else{
                jTextAreaFuncaoPreenchidaOperando2.setText(null);
                jButtonAlterarParametrosOperando2.setEnabled(false);
                jButtonEscolherFuncaoOperando2.setText("Escolher Função");                
                jButtonDetalhesFuncaoOperando2.setEnabled(false);
            }
        }        
    }   
     
    private void salvarEntrada() throws Exception {

        switch (passoAtual) {
            
            case 1:// Operando 1 //

                if (acaoValorPeca.getValorOperando1() == null) {
                    throw new IllegalArgumentException("É preciso preencher uma Função para localizar as peças da Ação!");
                }
                break;

            case 2:// Operador Matemático //

                if (jComboBoxOperadorMatematico.getSelectedIndex() == -1) {
                    throw new IllegalArgumentException("É preciso escolher um Operador Matemático para aplicar a Ação!");
                }
                acaoValorPeca.setOperadorMatematico((DHJOG.OperadorMatematico) jComboBoxOperadorMatematico.getSelectedItem());
                break;

            case 3:// Operando 2 //

                ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();

                if (operando2.isPreenchidaValor()) {

                    switch (operando2.getTipo()) {

                        case REAL:
                            operando2.setValor((Double) jSpinnerValorReal.getValue());
                            break;

                        default:
                            throw new IllegalArgumentException("Tipo de dado não suportado [" + operando2.getTipo() + "]");
                    }
                } else {
                    ////////////////////////////////////////////////////////////////
                    // Função Preenchida - Já é salva quando se preenche a função //
                    ////////////////////////////////////////////////////////////////
                }

                if (acaoValorPeca.getValorOperando2() == null) {
                    throw new IllegalArgumentException("É preciso preencher um valor a ser usado na aplicação da Ação!");
                }
                break;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOpcaoPreenchimento = new javax.swing.ButtonGroup();
        jPanelPrincipal = new javax.swing.JPanel();
        jPanelOperando1 = new javax.swing.JPanel();
        jLabelPassoInicial = new javax.swing.JLabel();
        jButtonEscolherFuncaoOperando1 = new javax.swing.JButton();
        jButtonAlterarParametrosOperando1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchidaOperando1 = new javax.swing.JTextArea();
        jButtonDetalhesFuncaoOperando1 = new javax.swing.JButton();
        jPanelOperadorMatematico = new javax.swing.JPanel();
        jLabelPassoInicial1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanelComboOpMatematico = new javax.swing.JPanel();
        jPanelCentralizaRelacional = new javax.swing.JPanel();
        jComboBoxOperadorMatematico = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescricaoOperadorMatematico = new javax.swing.JTextArea();
        jPanelOperando2 = new javax.swing.JPanel();
        jLabelPassoParametro = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldTipoOperando2 = new javax.swing.JTextField();
        jPanelOpcoesPreenchimento = new javax.swing.JPanel();
        jRadioButtonPreencherValor = new javax.swing.JRadioButton();
        jRadioButtonPreencherFuncao = new javax.swing.JRadioButton();
        jPanelEntadaDados = new javax.swing.JPanel();
        jPanelReal = new javax.swing.JPanel();
        jPanelCentraliza3 = new javax.swing.JPanel();
        jSpinnerValorReal = new JSpinner(new SpinnerNumberModel(1.0, -100.0, 100.0, 0.1)); ;
        jLabel5 = new javax.swing.JLabel();
        jPanelFuncao = new javax.swing.JPanel();
        jButtonEscolherFuncaoOperando2 = new javax.swing.JButton();
        jButtonAlterarParametrosOperando2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchidaOperando2 = new javax.swing.JTextArea();
        jButtonDetalhesFuncaoOperando2 = new javax.swing.JButton();
        jPanelFinaliza = new javax.swing.JPanel();
        jLabelPassoFinal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaAcaoValorPecaResultante = new javax.swing.JTextArea();
        jButtonCancelar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Preenchendo Ação de Valor de Peça de uma Heurística");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_heuristica.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPrincipal.setLayout(new java.awt.CardLayout());

        jLabelPassoInicial.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoInicial.setText("Passo 1 de 4 - Preencha uma Função para localizar as Peças que terão o valor alterado");

        jButtonEscolherFuncaoOperando1.setMnemonic('t');
        jButtonEscolherFuncaoOperando1.setText("Trocar Função");
        jButtonEscolherFuncaoOperando1.setToolTipText("Escolhe a Função a ser Preenchida");
        jButtonEscolherFuncaoOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEscolherFuncaoOperando1ActionPerformed(evt);
            }
        });

        jButtonAlterarParametrosOperando1.setMnemonic('l');
        jButtonAlterarParametrosOperando1.setText("Alterar Parâmetros");
        jButtonAlterarParametrosOperando1.setToolTipText("Altera os Parâmetros da Função");
        jButtonAlterarParametrosOperando1.setEnabled(false);
        jButtonAlterarParametrosOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarParametrosOperando1ActionPerformed(evt);
            }
        });

        jLabel8.setText("Função Preenchida");

        jTextAreaFuncaoPreenchidaOperando1.setBackground(java.awt.SystemColor.control);
        jTextAreaFuncaoPreenchidaOperando1.setColumns(20);
        jTextAreaFuncaoPreenchidaOperando1.setEditable(false);
        jTextAreaFuncaoPreenchidaOperando1.setLineWrap(true);
        jTextAreaFuncaoPreenchidaOperando1.setRows(3);
        jTextAreaFuncaoPreenchidaOperando1.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchidaOperando1.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane2.setViewportView(jTextAreaFuncaoPreenchidaOperando1);

        jButtonDetalhesFuncaoOperando1.setMnemonic('d');
        jButtonDetalhesFuncaoOperando1.setText("Detalhes da Função");
        jButtonDetalhesFuncaoOperando1.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncaoOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoOperando1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelOperando1Layout = new javax.swing.GroupLayout(jPanelOperando1);
        jPanelOperando1.setLayout(jPanelOperando1Layout);
        jPanelOperando1Layout.setHorizontalGroup(
            jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addGroup(jPanelOperando1Layout.createSequentialGroup()
                        .addComponent(jButtonEscolherFuncaoOperando1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarParametrosOperando1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDetalhesFuncaoOperando1))
                    .addComponent(jLabel8)
                    .addComponent(jLabelPassoInicial))
                .addContainerGap())
        );

        jPanelOperando1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAlterarParametrosOperando1, jButtonDetalhesFuncaoOperando1, jButtonEscolherFuncaoOperando1});

        jPanelOperando1Layout.setVerticalGroup(
            jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoInicial)
                .addGap(46, 46, 46)
                .addGroup(jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherFuncaoOperando1)
                    .addComponent(jButtonAlterarParametrosOperando1)
                    .addComponent(jButtonDetalhesFuncaoOperando1))
                .addGap(24, 24, 24)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelOperando1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAlterarParametrosOperando1, jButtonDetalhesFuncaoOperando1, jButtonEscolherFuncaoOperando1});

        jPanelPrincipal.add(jPanelOperando1, "Operando1");

        jLabelPassoInicial1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoInicial1.setText("Passo 2 de 4 - Escolha o Operador Matemático que será aplicado");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Operador");

        jPanelComboOpMatematico.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jComboBoxOperadorMatematico.addItem(DHJOG.OperadorMatematico.MAIS);
        jComboBoxOperadorMatematico.addItem(DHJOG.OperadorMatematico.MENOS);
        jComboBoxOperadorMatematico.addItem(DHJOG.OperadorMatematico.MULTIPLICACAO);
        jComboBoxOperadorMatematico.addItem(DHJOG.OperadorMatematico.DIVISAO);
        jComboBoxOperadorMatematico.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxOperadorMatematico.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxOperadorMatematicoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralizaRelacionalLayout = new javax.swing.GroupLayout(jPanelCentralizaRelacional);
        jPanelCentralizaRelacional.setLayout(jPanelCentralizaRelacionalLayout);
        jPanelCentralizaRelacionalLayout.setHorizontalGroup(
            jPanelCentralizaRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaRelacionalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxOperadorMatematico, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCentralizaRelacionalLayout.setVerticalGroup(
            jPanelCentralizaRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaRelacionalLayout.createSequentialGroup()
                .addComponent(jComboBoxOperadorMatematico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelComboOpMatematico.add(jPanelCentralizaRelacional);

        jLabel4.setText("Descrição do funcionamento do Operador");

        jTextAreaDescricaoOperadorMatematico.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoOperadorMatematico.setColumns(20);
        jTextAreaDescricaoOperadorMatematico.setEditable(false);
        jTextAreaDescricaoOperadorMatematico.setLineWrap(true);
        jTextAreaDescricaoOperadorMatematico.setRows(5);
        jTextAreaDescricaoOperadorMatematico.setWrapStyleWord(true);
        jTextAreaDescricaoOperadorMatematico.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jTextAreaDescricaoOperadorMatematico);

        javax.swing.GroupLayout jPanelOperadorMatematicoLayout = new javax.swing.GroupLayout(jPanelOperadorMatematico);
        jPanelOperadorMatematico.setLayout(jPanelOperadorMatematicoLayout);
        jPanelOperadorMatematicoLayout.setHorizontalGroup(
            jPanelOperadorMatematicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOperadorMatematicoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperadorMatematicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jPanelComboOpMatematico, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jLabelPassoInicial1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelOperadorMatematicoLayout.setVerticalGroup(
            jPanelOperadorMatematicoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperadorMatematicoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoInicial1)
                .addGap(16, 16, 16)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelComboOpMatematico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelOperadorMatematico, "OperadorMatematico");

        jLabelPassoParametro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoParametro.setText("Passo 3 de 4 - Preencha o valor que será aplicado as peças");

        jLabel2.setText("Tipo de Valor Esperado");

        jTextFieldTipoOperando2.setEditable(false);

        buttonGroupOpcaoPreenchimento.add(jRadioButtonPreencherValor);
        jRadioButtonPreencherValor.setSelected(true);
        jRadioButtonPreencherValor.setText("Preencher com Valor   ");
        jRadioButtonPreencherValor.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPreencherValor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonPreencherValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPreencherValorActionPerformed(evt);
            }
        });
        jPanelOpcoesPreenchimento.add(jRadioButtonPreencherValor);

        buttonGroupOpcaoPreenchimento.add(jRadioButtonPreencherFuncao);
        jRadioButtonPreencherFuncao.setText("Preencher com Função");
        jRadioButtonPreencherFuncao.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPreencherFuncao.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonPreencherFuncao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPreencherFuncaoActionPerformed(evt);
            }
        });
        jPanelOpcoesPreenchimento.add(jRadioButtonPreencherFuncao);

        jPanelEntadaDados.setLayout(new java.awt.CardLayout());

        jPanelCentraliza3.setPreferredSize(new java.awt.Dimension(260, 67));

        jSpinnerValorReal.setToolTipText("Entre com um Valor Real");
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorReal, "#,##0.0");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Valores válidos de -100,0 a 100,0");

        javax.swing.GroupLayout jPanelCentraliza3Layout = new javax.swing.GroupLayout(jPanelCentraliza3);
        jPanelCentraliza3.setLayout(jPanelCentraliza3Layout);
        jPanelCentraliza3Layout.setHorizontalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelCentraliza3Layout.setVerticalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel5))
        );

        jPanelReal.add(jPanelCentraliza3);

        jPanelEntadaDados.add(jPanelReal, "Real");

        jButtonEscolherFuncaoOperando2.setMnemonic('e');
        jButtonEscolherFuncaoOperando2.setText("Escoher Função");
        jButtonEscolherFuncaoOperando2.setToolTipText("Escolhe a Função a ser Preenchida");
        jButtonEscolherFuncaoOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEscolherFuncaoOperando2ActionPerformed(evt);
            }
        });

        jButtonAlterarParametrosOperando2.setMnemonic('l');
        jButtonAlterarParametrosOperando2.setText("Alterar Parâmetros");
        jButtonAlterarParametrosOperando2.setToolTipText("Altera os Parâmetros da Função");
        jButtonAlterarParametrosOperando2.setEnabled(false);
        jButtonAlterarParametrosOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarParametrosOperando2ActionPerformed(evt);
            }
        });

        jLabel6.setText("Função Preenchida");

        jTextAreaFuncaoPreenchidaOperando2.setBackground(java.awt.SystemColor.control);
        jTextAreaFuncaoPreenchidaOperando2.setColumns(20);
        jTextAreaFuncaoPreenchidaOperando2.setEditable(false);
        jTextAreaFuncaoPreenchidaOperando2.setLineWrap(true);
        jTextAreaFuncaoPreenchidaOperando2.setRows(3);
        jTextAreaFuncaoPreenchidaOperando2.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchidaOperando2.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane3.setViewportView(jTextAreaFuncaoPreenchidaOperando2);

        jButtonDetalhesFuncaoOperando2.setMnemonic('d');
        jButtonDetalhesFuncaoOperando2.setText("Detalhes da Função");
        jButtonDetalhesFuncaoOperando2.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncaoOperando2.setEnabled(false);
        jButtonDetalhesFuncaoOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoOperando2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFuncaoLayout = new javax.swing.GroupLayout(jPanelFuncao);
        jPanelFuncao.setLayout(jPanelFuncaoLayout);
        jPanelFuncaoLayout.setHorizontalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                        .addComponent(jButtonEscolherFuncaoOperando2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarParametrosOperando2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDetalhesFuncaoOperando2))
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAlterarParametrosOperando2, jButtonDetalhesFuncaoOperando2, jButtonEscolherFuncaoOperando2});

        jPanelFuncaoLayout.setVerticalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherFuncaoOperando2)
                    .addComponent(jButtonAlterarParametrosOperando2)
                    .addComponent(jButtonDetalhesFuncaoOperando2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAlterarParametrosOperando2, jButtonDetalhesFuncaoOperando2, jButtonEscolherFuncaoOperando2});

        jPanelEntadaDados.add(jPanelFuncao, "Funcao");

        javax.swing.GroupLayout jPanelOperando2Layout = new javax.swing.GroupLayout(jPanelOperando2);
        jPanelOperando2.setLayout(jPanelOperando2Layout);
        jPanelOperando2Layout.setHorizontalGroup(
            jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelOperando2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOperando2Layout.createSequentialGroup()
                        .addComponent(jLabelPassoParametro)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelOperando2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTipoOperando2))
                    .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelOperando2Layout.setVerticalGroup(
            jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoParametro)
                .addGap(30, 30, 30)
                .addGroup(jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldTipoOperando2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPrincipal.add(jPanelOperando2, "Operando2");

        jLabelPassoFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoFinal.setText("Passo 4 de 4 - Finalize a criação");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Após o preenchimento a Ação de Valor de Peça ficou desta forma");

        jTextAreaAcaoValorPecaResultante.setColumns(20);
        jTextAreaAcaoValorPecaResultante.setEditable(false);
        jTextAreaAcaoValorPecaResultante.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaAcaoValorPecaResultante.setLineWrap(true);
        jTextAreaAcaoValorPecaResultante.setRows(5);
        jTextAreaAcaoValorPecaResultante.setWrapStyleWord(true);
        jTextAreaAcaoValorPecaResultante.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane4.setViewportView(jTextAreaAcaoValorPecaResultante);

        javax.swing.GroupLayout jPanelFinalizaLayout = new javax.swing.GroupLayout(jPanelFinaliza);
        jPanelFinaliza.setLayout(jPanelFinalizaLayout);
        jPanelFinalizaLayout.setHorizontalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jLabelPassoFinal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelFinalizaLayout.setVerticalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoFinal)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelFinaliza, "Finaliza");

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('c');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_avancar.png"))); // NOI18N
        jButtonProximo.setMnemonic('p');
        jButtonProximo.setText("Próximo");
        jButtonProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProximoActionPerformed(evt);
            }
        });

        jButtonAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_voltar.png"))); // NOI18N
        jButtonAnterior.setMnemonic('n');
        jButtonAnterior.setText("Anterior");
        jButtonAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnteriorActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProximo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancelar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelar)
                    .addComponent(jButtonProximo)
                    .addComponent(jButtonAnterior)
                    .addComponent(jButtonAjuda))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        setSize(new java.awt.Dimension(552, 365));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDetalhesFuncaoOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false) {
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();
            
            if (funcaoPreenchida != null) {
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaFuncao tela = new TelaFuncao(TelaPreencheAcaoValorPeca.this, funcaoPreenchida.getFuncao(), telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonDetalhesFuncaoOperando2ActionPerformed

    private void jButtonDetalhesFuncaoOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoOperando1ActionPerformed
        
        if (acaoValorPeca != null){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaFuncao tela = new TelaFuncao(TelaPreencheAcaoValorPeca.this,
                                                     ((FuncaoPreenchida)acaoValorPeca.getValorOperando1()).getFuncao(),
                                                     telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());  
                }
            });
        }                
    }//GEN-LAST:event_jButtonDetalhesFuncaoOperando1ActionPerformed

    private void jButtonEscolherFuncaoOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEscolherFuncaoOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();    
            
            if (funcaoPreenchida != null){                     
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheAcaoValorPeca.this, operando2.getTipo(), funcaoPreenchida.getFuncao());
                    }
                });

            }else{        
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheAcaoValorPeca.this, operando2.getTipo());
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonEscolherFuncaoOperando2ActionPerformed

    private void jButtonAlterarParametrosOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlterarParametrosOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();    
            
            if (funcaoPreenchida != null){            
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheAcaoValorPeca.this,funcaoPreenchida);
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonAlterarParametrosOperando2ActionPerformed

    private void jButtonAlterarParametrosOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlterarParametrosOperando1ActionPerformed
        
        if (acaoValorPeca != null){            
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheAcaoValorPeca.this,(FuncaoPreenchida) acaoValorPeca.getValorOperando1());
                }
            });
        }
    }//GEN-LAST:event_jButtonAlterarParametrosOperando1ActionPerformed

    private void jButtonEscolherFuncaoOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEscolherFuncaoOperando1ActionPerformed
        
        if (acaoValorPeca != null) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheAcaoValorPeca.this, TipoDado.PECAS, ((FuncaoPreenchida) acaoValorPeca.getValorOperando1()).getFuncao());
                }
            });

        } else {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheAcaoValorPeca.this, TipoDado.PECAS);
                }
            });
        }
    }//GEN-LAST:event_jButtonEscolherFuncaoOperando1ActionPerformed

    private void jComboBoxOperadorMatematicoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxOperadorMatematicoItemStateChanged
        configuraDescricaoOperador();
    }//GEN-LAST:event_jComboBoxOperadorMatematicoItemStateChanged
    
    private void jRadioButtonPreencherFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherFuncaoActionPerformed
         
         ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
        
         operando2.setPreenchidaValor(false);
         
         montaPanelOperando2(); 
    }//GEN-LAST:event_jRadioButtonPreencherFuncaoActionPerformed

    private void jRadioButtonPreencherValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherValorActionPerformed
         
         ParametroPreenchido operando2 = acaoValorPeca.getParametroOperando2();
         
         operando2.setPreenchidaValor(true);
         
         montaPanelOperando2();  
    }//GEN-LAST:event_jRadioButtonPreencherValorActionPerformed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing
            
    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
       passoAtual--;
        
       switch(passoAtual){
           case 1: // Operando 1 //
                  jButtonAnterior.setVisible(false);
                  cardPrincipal.show(jPanelPrincipal,"Operando1");
                  break;
               
           case 2: // Operador Relacional //
                  jButtonAnterior.setVisible(true);
                  cardPrincipal.show(jPanelPrincipal,"OperadorMatematico");   
                  break;
               
           case 3: // Operando 2 //
                  jButtonProximo.setText("Próximo");
                  jButtonProximo.setMnemonic('p');
                  jButtonProximo.setVisible(true);
                  cardPrincipal.show(jPanelPrincipal,"Operando2");   
                  break;
       }            
    }//GEN-LAST:event_jButtonAnteriorActionPerformed
    
    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed
    
    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        
        switch(passoAtual){
            
            case 1:// Operando 1 //
            case 2:// Operador Matemático //
            case 3:// Operando 2 //
                
                   try{
                       salvarEntrada();                        
                   }catch(Exception e){
                       //Utils.registraException(e);
                       UtilsGUI.dialogoErro(this,e.getMessage());
                       return;
                   }
                   break;
                
            case 4:// Finalizacao //                         
                               
                  if (nova){                                                                
                      model.add(model.size(),acaoValorPeca);
                  }else{
                      if (!acaoValorPeca.toString().equals(acaoValorPecaOriginal.toString())){
                          model.set(posicaoOriginal,acaoValorPeca);
                      }
                  }
                   
                  dispose();
                  telaHeuristica.fechandoTelaPreencheAcaoValorPeca(true);
                  return;                   
        }
        
        passoAtual++;
        
        switch(passoAtual){
            
            case 2:// Operador Matemático //
                
                   cardPrincipal.show(jPanelPrincipal,"OperadorMatematico");   
                   
                   jButtonAnterior.setVisible(true);
                   jComboBoxOperadorMatematico.setSelectedItem(acaoValorPeca.getOperadorMatematico());
                   
                   configuraDescricaoOperador();
                   break;
                
            case 3:// Operando 2 //
                
                   cardPrincipal.show(jPanelPrincipal,"Operando2");
                   
                   montaPanelOperando2();
                   break;
                
            case 4:// Finalizacao //                   
                
                   if (telaHeuristica.panelEtapa.editor.podeAlterar()) {
                       jButtonProximo.setText("Concluir");
                       jButtonProximo.setMnemonic('n');
                   }else{
                       jButtonProximo.setVisible(false);
                   }
                   
                   cardPrincipal.show(jPanelPrincipal,"Finaliza");                
                   
                   jTextAreaAcaoValorPecaResultante.setText(acaoValorPeca.toString());
                   break;
        }
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaPreencheAcaoValorPeca");
    }//GEN-LAST:event_jButtonAjudaActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupOpcaoPreenchimento;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAlterarParametrosOperando1;
    private javax.swing.JButton jButtonAlterarParametrosOperando2;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonDetalhesFuncaoOperando1;
    private javax.swing.JButton jButtonDetalhesFuncaoOperando2;
    private javax.swing.JButton jButtonEscolherFuncaoOperando1;
    private javax.swing.JButton jButtonEscolherFuncaoOperando2;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JComboBox jComboBoxOperadorMatematico;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelPassoFinal;
    private javax.swing.JLabel jLabelPassoInicial;
    private javax.swing.JLabel jLabelPassoInicial1;
    private javax.swing.JLabel jLabelPassoParametro;
    private javax.swing.JPanel jPanelCentraliza3;
    private javax.swing.JPanel jPanelCentralizaRelacional;
    private javax.swing.JPanel jPanelComboOpMatematico;
    private javax.swing.JPanel jPanelEntadaDados;
    private javax.swing.JPanel jPanelFinaliza;
    private javax.swing.JPanel jPanelFuncao;
    private javax.swing.JPanel jPanelOpcoesPreenchimento;
    private javax.swing.JPanel jPanelOperadorMatematico;
    private javax.swing.JPanel jPanelOperando1;
    private javax.swing.JPanel jPanelOperando2;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelReal;
    private javax.swing.JRadioButton jRadioButtonPreencherFuncao;
    private javax.swing.JRadioButton jRadioButtonPreencherValor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner jSpinnerValorReal;
    private javax.swing.JTextArea jTextAreaAcaoValorPecaResultante;
    private javax.swing.JTextArea jTextAreaDescricaoOperadorMatematico;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchidaOperando1;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchidaOperando2;
    private javax.swing.JTextField jTextFieldTipoOperando2;
    // End of variables declaration//GEN-END:variables
}
