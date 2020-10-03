package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 6 de Setembro de 2006, 10:01
 */
public class TelaFuncao extends javax.swing.JFrame implements AcessoTelaAnotacao, AcessoTelaUsuario {
    
    private ModalFrameHierarchy modalFrameAnterior;
    private Funcao              funcao;    

    private boolean podeAlterar;
    
    public TelaFuncao(ModalFrameHierarchy modalFrameAnterior, Funcao funcao, Tipo complexidade) {
        
        this.modalFrameAnterior = modalFrameAnterior;
        this.funcao             = funcao;
        
        initComponents();
        
        jTextFieldNomeCurto.setText(funcao.getNomeCurto().toUpperCase());
        jTextFieldAssinatura.setText(funcao.getNome().toUpperCase());
        jTextAreaDescricaoFuncao.setText(funcao.getDescricaoFuncao());
        jTextFieldTipoRetorno.setText(funcao.getTipoRetorno().toString());
        jTextAreaDescricaoRetorno.setText(funcao.getDescricaoRetorno());        
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(funcao.getDataCriacao()));        
        
        atualizaVersaoDataUltimaModificacao();
        Anotacoes.atualizaQuantidadeAnotacoes(this);
                
        jTextAreaCodigoFonte.setText(funcao.getDescricaoDHJOG());
        jTextAreaCodigoFonte.setCaretPosition(0);
        
        if (funcao.totalParametros() == 0){
            
            jTabbedPanePrincipal.remove(jPanelDescricaoParametros);
            
        }else{
            
            Object[][] dados = new Object[funcao.totalParametros()][3];
            
            for (int x = 0; x < funcao.totalParametros(); x++){
                dados[x][0] = String.valueOf(x + 1);                
                dados[x][1] = funcao.getParametro(x).getTipo().toString();
                dados[x][2] = " " + funcao.getParametro(x).getDescricao();
            }
            
            Object[] nomeColunas = {"Nr.","Tipo","Descrição"};
            
            DefaultTableModel model = (DefaultTableModel) jTableParametros.getModel();
            model.setDataVector(dados, nomeColunas);
        }
        
        atualizaInterfaceNivelComplexidade(complexidade);
        
        setTitle("Descrição da Função " + funcao.getNomeCurto().toUpperCase());        
        
        //////////////////////////////////////////////////////////////////////////////////////////
        // Pode editar os dados de uma Função caso seja Administrador, ou o próprio autor dela. //
        //////////////////////////////////////////////////////////////////////////////////////////
                
        if ((HeuChess.usuario.getId()   == funcao.getIdAutor()) ||
            (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR)){
            
            podeAlterar = true;
        }
        
        if (!podeAlterar){
            
            if (funcao.getAnotacoes().isEmpty()){
                jTabbedPanePrincipal.remove(jPanelAnotacoes);
            }else{
                jButtonNovaAnotacao.setVisible(false);
                jButtonExcluirAnotacao.setVisible(false);
            }
        }
        
        try {
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(funcao.getIdAutor())));
        
            Historico.registraComponenteAberto(funcao);
            ConexaoDBHeuChess.commit();
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(modalFrameAnterior.getFrame(), "Erro ao recuperar informações do Banco de Dados!");
            dispose();
            return;
        }
        
        ModalFrameUtil.showAsModalDontBlock(this);         
        
        if (funcao.totalParametros() > 0){
            configuraTabela(jTableParametros);
        }
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return modalFrameAnterior;
    }
    
    private void atualizaInterfaceNivelComplexidade(Tipo complexidade){
        
        if (complexidade == ConjuntoHeuristico.NIVEL_1_INICIANTE ||
            complexidade == ConjuntoHeuristico.NIVEL_2_BASICO ||    
            complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO){
            
            jTabbedPanePrincipal.remove(jPanelCodigoFonte);           
            
        }else
            if (complexidade == ConjuntoHeuristico.NIVEL_4_PLENO ||
                complexidade == ConjuntoHeuristico.NIVEL_5_AVANCADO ||    
                complexidade == ConjuntoHeuristico.NIVEL_6_ESPECIALISTA){
            
                jTabbedPanePrincipal.remove(jPanelCodigoFonte);
                jTabbedPanePrincipal.add("Código Fonte",jPanelCodigoFonte);          
            }
    }    
    
    private void configuraTabela(JTable jTable){
        
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer();
        cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer();
        cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable.getColumn(jTable.getColumnName(0)).setCellRenderer(cellRenderCentro);      
        jTable.getColumn(jTable.getColumnName(1)).setCellRenderer(cellRenderCentro);
        jTable.getColumn(jTable.getColumnName(2)).setCellRenderer(cellRenderEsquerda);
        
        int largura = jTable.getWidth();
        
        jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth((int) (largura * 0.05));
        jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth((int) (largura * 0.20));
        jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth((int) (largura * 0.75));
    }
    
    private void confirmaCancelar(){ 
        dispose();
    }

    @Override
    public JList getJListAnotacoes() {
        return jListAnotacoes;
    }

    @Override
    public JLabel getJLabelTotalAnotacoes() {
        return jLabelTotalAnotacoes;
    }

    @Override
    public Componente getComponente() {
        return funcao;
    }

    @Override
    public boolean podeAlterar() {
        return podeAlterar;
    }
    
    @Override
    public JButton getJButtonAbrirAnotacao() {
        return jButtonAbrirAnotacao;
    }

    @Override
    public JButton getJButtonExcluirAnotacao() {
        return jButtonExcluirAnotacao;
    }
    
    @Override
    public final void atualizaVersaoDataUltimaModificacao() {
        jTextFieldVersao.setText(Long.toString(funcao.getVersao()));      
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(funcao.getDataUltimaModificacao()));
    }
    
    @Override
    public void fechandoTelaAnotacao(boolean sucesso) {
        
    }
            
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null){
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(usuario.getNome()));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelDadosPrincipais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNomeCurto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldAssinatura = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDescricaoFuncao = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldTipoRetorno = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaDescricaoRetorno = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jPanelDescricaoParametros = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPaneTabelaParametros = new javax.swing.JScrollPane();
        jTableParametros = new javax.swing.JTable();
        jPanelDadosAutoria = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jButtonDadosAutor = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jPanelAnotacoes = new javax.swing.JPanel();
        jPanelBotoesAnotacao = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonNovaAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new ModelListaComponentes(funcao.getAnotacoes()));
        jLabelTituloListaAnotacoes = new javax.swing.JLabel();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jPanelCodigoFonte = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaCodigoFonte = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanelBotoes = new javax.swing.JPanel();
        jButtonFechar = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Função");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_funcao.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPanePrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanePrincipalStateChanged(evt);
            }
        });

        jLabel1.setText("Nome");

        jTextFieldNomeCurto.setEditable(false);

        jLabel2.setText("Assinatura");

        jTextFieldAssinatura.setEditable(false);

        jLabel3.setText("Descrição");

        jTextAreaDescricaoFuncao.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoFuncao.setColumns(20);
        jTextAreaDescricaoFuncao.setEditable(false);
        jTextAreaDescricaoFuncao.setLineWrap(true);
        jTextAreaDescricaoFuncao.setRows(3);
        jTextAreaDescricaoFuncao.setWrapStyleWord(true);
        jTextAreaDescricaoFuncao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane3.setViewportView(jTextAreaDescricaoFuncao);

        jLabel4.setText("Tipo de Retorno");

        jTextFieldTipoRetorno.setEditable(false);

        jTextAreaDescricaoRetorno.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoRetorno.setColumns(20);
        jTextAreaDescricaoRetorno.setEditable(false);
        jTextAreaDescricaoRetorno.setLineWrap(true);
        jTextAreaDescricaoRetorno.setRows(3);
        jTextAreaDescricaoRetorno.setWrapStyleWord(true);
        jTextAreaDescricaoRetorno.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane4.setViewportView(jTextAreaDescricaoRetorno);

        jLabel5.setText("Descrição do Retorno");

        javax.swing.GroupLayout jPanelDadosPrincipaisLayout = new javax.swing.GroupLayout(jPanelDadosPrincipais);
        jPanelDadosPrincipais.setLayout(jPanelDadosPrincipaisLayout);
        jPanelDadosPrincipaisLayout.setHorizontalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNomeCurto, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAssinatura, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTipoRetorno, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelDadosPrincipaisLayout.setVerticalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNomeCurto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldAssinatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldTipoRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Dados Principais", jPanelDadosPrincipais);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Lista dos Parâmetros que devem ser passados na chamada desta Função");

        jTableParametros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Ordem", "Tipo", "Descrição"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneTabelaParametros.setViewportView(jTableParametros);

        javax.swing.GroupLayout jPanelDescricaoParametrosLayout = new javax.swing.GroupLayout(jPanelDescricaoParametros);
        jPanelDescricaoParametros.setLayout(jPanelDescricaoParametrosLayout);
        jPanelDescricaoParametrosLayout.setHorizontalGroup(
            jPanelDescricaoParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDescricaoParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDescricaoParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneTabelaParametros, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelDescricaoParametrosLayout.setVerticalGroup(
            jPanelDescricaoParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDescricaoParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneTabelaParametros, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Parâmetros", jPanelDescricaoParametros);

        jLabel6.setText("Autor");

        jTextFieldNomeAutor.setEditable(false);

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        jLabel7.setText("Versão");

        jTextFieldVersao.setEditable(false);
        jTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel10.setText("Modificação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanelDadosAutoriaLayout = new javax.swing.GroupLayout(jPanelDadosAutoria);
        jPanelDadosAutoria.setLayout(jPanelDadosAutoriaLayout);
        jPanelDadosAutoriaLayout.setHorizontalGroup(
            jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosAutoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosAutoriaLayout.createSequentialGroup()
                        .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDadosAutor))
                    .addGroup(jPanelDadosAutoriaLayout.createSequentialGroup()
                        .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelDadosAutoriaLayout.setVerticalGroup(
            jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosAutoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jButtonDadosAutor)
                    .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanelDadosAutoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(271, Short.MAX_VALUE))
        );

        jTabbedPanePrincipal.addTab("Autoria", jPanelDadosAutoria);

        jButtonAbrirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirAnotacao.setText("Abrir");
        jButtonAbrirAnotacao.setEnabled(false);
        jButtonAbrirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonAbrirAnotacao);

        jButtonNovaAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaAnotacao.setText("Adicionar");
        jButtonNovaAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonNovaAnotacao);

        jButtonExcluirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirAnotacao.setText("Excluir");
        jButtonExcluirAnotacao.setEnabled(false);
        jButtonExcluirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirAnotacaoActionPerformed(evt);
            }
        });
        jPanelBotoesAnotacao.add(jButtonExcluirAnotacao);

        jListAnotacoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListAnotacoes.setCellRenderer(new RenderListaAnotacoes());
        jListAnotacoes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAnotacoesValueChanged(evt);
            }
        });
        jListAnotacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListAnotacoesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListAnotacoes);

        jLabelTituloListaAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTituloListaAnotacoes.setText("Anotações Gerais Sobre esta Função");

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelAnotacoesLayout.createSequentialGroup()
                        .addComponent(jLabelTituloListaAnotacoes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTotalAnotacoes)))
                .addContainerGap())
        );
        jPanelAnotacoesLayout.setVerticalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTituloListaAnotacoes)
                    .addComponent(jLabelTotalAnotacoes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Anotações", jPanelAnotacoes);

        jTextAreaCodigoFonte.setColumns(20);
        jTextAreaCodigoFonte.setEditable(false);
        jTextAreaCodigoFonte.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaCodigoFonte.setLineWrap(true);
        jTextAreaCodigoFonte.setRows(5);
        jTextAreaCodigoFonte.setWrapStyleWord(true);
        jTextAreaCodigoFonte.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane2.setViewportView(jTextAreaCodigoFonte);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Código Fonte desta Função");

        javax.swing.GroupLayout jPanelCodigoFonteLayout = new javax.swing.GroupLayout(jPanelCodigoFonte);
        jPanelCodigoFonte.setLayout(jPanelCodigoFonteLayout);
        jPanelCodigoFonteLayout.setHorizontalGroup(
            jPanelCodigoFonteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoFonteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCodigoFonteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanelCodigoFonteLayout.setVerticalGroup(
            jPanelCodigoFonteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoFonteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Código Fonte", jPanelCodigoFonte);

        jButtonFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jButtonFechar.setMnemonic('f');
        jButtonFechar.setText("Fechar");
        jButtonFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
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

        javax.swing.GroupLayout jPanelBotoesLayout = new javax.swing.GroupLayout(jPanelBotoes);
        jPanelBotoes.setLayout(jPanelBotoesLayout);
        jPanelBotoesLayout.setHorizontalGroup(
            jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAjuda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonFechar)
                .addContainerGap())
        );

        jPanelBotoesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonFechar});

        jPanelBotoesLayout.setVerticalGroup(
            jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBotoesLayout.createSequentialGroup()
                .addGroup(jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFechar)
                    .addComponent(jButtonAjuda))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelBotoesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonFechar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPanePrincipal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelBotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPanePrincipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-625)/2, (screenSize.height-459)/2, 625, 459);
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelDadosAutoria){
            jTextFieldVersao.setText(String.valueOf(funcao.getVersao()));
            jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(funcao.getDataUltimaModificacao()));            
        }
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonFecharActionPerformed

    private void jListAnotacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnotacoesMouseClicked
        Anotacoes.verificaDuploCliqueAnotacao(this, evt);        
    }//GEN-LAST:event_jListAnotacoesMouseClicked

    private void jListAnotacoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnotacoesValueChanged
        Anotacoes.verificaSelecaoAnotacao(this);        
    }//GEN-LAST:event_jListAnotacoesValueChanged

    private void jButtonExcluirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAnotacaoActionPerformed
        Anotacoes.confirmaApagarAnotacaoSelecionada(this);
    }//GEN-LAST:event_jButtonExcluirAnotacaoActionPerformed

    private void jButtonNovaAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaAnotacaoActionPerformed
        Anotacoes.novaAnotacao(this);
    }//GEN-LAST:event_jButtonNovaAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);        
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaFuncao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this, funcao.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JButton jButtonNovaAnotacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelTituloListaAnotacoes;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelBotoes;
    private javax.swing.JPanel jPanelBotoesAnotacao;
    private javax.swing.JPanel jPanelCodigoFonte;
    private javax.swing.JPanel jPanelDadosAutoria;
    private javax.swing.JPanel jPanelDadosPrincipais;
    private javax.swing.JPanel jPanelDescricaoParametros;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneTabelaParametros;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTable jTableParametros;
    private javax.swing.JTextArea jTextAreaCodigoFonte;
    private javax.swing.JTextArea jTextAreaDescricaoFuncao;
    private javax.swing.JTextArea jTextAreaDescricaoRetorno;
    private javax.swing.JTextField jTextFieldAssinatura;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldNomeCurto;
    private javax.swing.JTextField jTextFieldTipoRetorno;
    private javax.swing.JTextField jTextFieldVersao;
    // End of variables declaration//GEN-END:variables
}
