package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.InscricaoTurmaDAO;
import br.ufpr.inf.heuchess.persistencia.InstituicaoDAO;
import br.ufpr.inf.heuchess.persistencia.TurmaDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.organizacao.InscricaoTurma;
import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Turma;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public class TelaTurma extends javax.swing.JFrame implements AcessoTelaLocalizaUsuario, AcessoTelaUsuario {
    
    private ArrayList<Instituicao> instituicoes;
    
    private Turma turma;
    private Turma turmaOriginal;        
    
    private TableModelInscricaoTurma tableModelCoordenadores;
    private TableModelInscricaoTurma tableModelAprendizes;
    
    private TelaPrincipal telaPrincipal;    
    
    private boolean nova;    
    private boolean podeEditar, podeCriarUsuario;
    
    private enum Incluindo {
        COORDENADOR,
        APRENDIZ,
        NENHUM;
    }
    
    private Incluindo incluindo;
    
    /**
     * Criando nova Turma
     */
    public TelaTurma(TelaPrincipal telaPrincipal) {
        
        setEnabled(false);
        
        telaPrincipal.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        this.telaPrincipal = telaPrincipal;
                
        try {
            instituicoes = InstituicaoDAO.listaEditaveis(HeuChess.usuario);
            
            if (instituicoes.isEmpty()){
                telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(telaPrincipal, "Não existe nenhuma Instituição que você possa usar para criar uma nova Turma!");
                dispose();
                return;
            }
            
        } catch (Exception ex) {
            HeuChess.registraExcecao(ex);
            
            telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(telaPrincipal, "Erro ao carregar lista de Instituições do Banco!\n" + ex.getMessage());
            dispose();
            return;
        }
        
        turma = new Turma();
        
        nova             = true;
        podeEditar       = true;
        podeCriarUsuario = true;
        
        montarInterface();     
    }
    
    /**
     * Criando nova Turma passando uma Instituição
     */
    public TelaTurma(TelaPrincipal telaPrincipal, long idInstituicao) {
        
        this(telaPrincipal);

        selecionaInstituicao(idInstituicao);
    }
    
    /**
     * Abrindo uma Turma já existente
     */
    public TelaTurma(TelaPrincipal telaPrincipal, Turma turma) {
        
        this.telaPrincipal = telaPrincipal;
        turmaOriginal      = turma;
        
        try {
            instituicoes = InstituicaoDAO.listaEditaveis(HeuChess.usuario);
            
            if (instituicoes.isEmpty()){
                instituicoes.add(InstituicaoDAO.busca(turma.getIdInstituicao()));
            }
            
            InscricaoTurmaDAO.carregaTodas(turmaOriginal);            
                        
            this.turma = turmaOriginal.geraClone();                        
            
        } catch (Exception ex) {
            HeuChess.registraExcecao(ex);
            
            telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(telaPrincipal, "Erro ao carregar dados do Banco!\n" + ex.getMessage());
            dispose();
            return;
        }
        
        // Pode editar os dados de uma turma caso seja Administrador, o coordenador da turma, 
        // ou coordenador da instiuição que a turma está ligada
        
        if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR){
            
            podeEditar = true;
            podeCriarUsuario = true;
            
        }else{
            
            for (Instituicao instituicao : instituicoes) {

                if (instituicao.getId() == turma.getIdInstituicao()) {

                    if (instituicao.getIdCoordenador() == HeuChess.usuario.getId()) {
                        podeEditar = true;
                        podeCriarUsuario = true;
                    }
                    break;
                }
            } 
            
            if (!podeEditar) {
                for (InscricaoTurma inscricao : turma.inscricoesCoordenadores()) {

                    if (inscricao.getUsuario().getId() == HeuChess.usuario.getId()) {
                        podeEditar = true;
                        break;
                    }
                }
            }
        }
                  
        montarInterface();
    }
    
    @Override
    public Frame getFrame(){
        return this;        
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return telaPrincipal;
    } 
    
    private boolean selecionaInstituicao(long id){
        
        for (Instituicao ins : instituicoes){
            
            if (ins.getId() == id){
                jComboBoxInstituicao.setSelectedItem(ins);        
                return true;
            }        
        }
        
        return false;
    }
    
    private void montarInterface(){       
        
        initComponents();      
        
        if (nova){                            
            setTitle("Turma - Nova");
            
            jComboBoxInstituicao.setSelectedIndex(-1);
            
        }else{
            jTextFieldNome.setText(turma.getNome());
            jTextAreaTextoDescricao.setText(turma.getDescricao());
            jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(turma.getDataCriacao()));

            selecionaInstituicao(turma.getIdInstituicao());            
            
            atualizaTotalCaracteres();
            
            setTitle("Turma - " + jTextFieldNome.getText());            
            
            if (turma.getSituacao() == Turma.BLOQUEADA){
                jRadioButtonSituacaoTurmaBloqueada.setSelected(true);
            }else{
                jRadioButtonSituacaoTurmaLiberada.setSelected(true);
            }
        
            if (turma.pode(Permissao.EXCLUIR)){
                jRadioButtonPermitirExcluir.setSelected(true);
            }else
                if (turma.pode(Permissao.COPIAR)){
                    jRadioButtonPermitirCopiar.setSelected(true);
                }else
                    if (turma.pode(Permissao.ALTERAR)){
                        jRadioButtonPermitirAlterar.setSelected(true);
                    }else
                        if (turma.pode(Permissao.ANOTAR)){
                            jRadioButtonPermitirAnotar.setSelected(true);
                        }else
                            if (turma.pode(Permissao.UTILIZAR)){
                                jRadioButtonPermitirUtilizar.setSelected(true);
                            }else
                                if (turma.pode(Permissao.ACESSAR)){
                                    jRadioButtonPermitirAcessar.setSelected(true);
                                }else{
                                    jRadioButtonNenhumaPermissao.setSelected(true);
                                }
        }
                
        jButtonRetirarCoordenador.setEnabled(false);    
        jButtonRetirarAprendiz.setEnabled(false);        
                
        tableModelCoordenadores = new TableModelInscricaoTurma(turma.inscricoesCoordenadores());
        jTableCoordenadores.setModel(tableModelCoordenadores);
        
        tableModelAprendizes = new TableModelInscricaoTurma(turma.inscricoesAprendizes());
        jTableAprendizes.setModel(tableModelAprendizes);
        
        jTableCoordenadores.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting()){
                    return;
                }                
                ListSelectionModel rowSM = (ListSelectionModel)e.getSource();
                int selectedIndex = rowSM.getMinSelectionIndex();
         
                if (selectedIndex == -1){
                    jButtonRetirarCoordenador.setEnabled(false);
                }else{
                    jButtonRetirarCoordenador.setEnabled(true);
                }
            }                
        });
        jTableAprendizes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting()){
                    return;
                }                
                ListSelectionModel rowSM = (ListSelectionModel)e.getSource();
                int selectedIndex = rowSM.getMinSelectionIndex();
         
                if (selectedIndex == -1){
                    jButtonRetirarAprendiz.setEnabled(false);
                }else{
                    jButtonRetirarAprendiz.setEnabled(true);
                }
            }                
        });
        
        if (!podeEditar){
            
            jTextFieldNome.setEditable(false);
            jTextAreaTextoDescricao.setEditable(false);
            jComboBoxInstituicao.setEnabled(false);
            
            jTableCoordenadores.setColumnSelectionAllowed(false);
            jTableCoordenadores.setRowSelectionAllowed(false);
            jButtonAdicionarNovoCoordenador.setVisible(false);
            jButtonAdicionarCoordenador.setVisible(false);
            jButtonRetirarCoordenador.setVisible(false);
                        
            jTableAprendizes.setColumnSelectionAllowed(false);
            jTableAprendizes.setRowSelectionAllowed(false);
            jButtonAdicionarNovoAprendiz.setVisible(false);
            jButtonAdicionarAprendiz.setVisible(false);
            jButtonRetirarAprendiz.setVisible(false);            
            
            jTabbedPanePrincipal.remove(jPanelPermissoes);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }else{
            
            if (instituicoes.size() == 1){
                
                // Esta editando mas não é administrador e nem coordena mais de uma instituição
                
                jComboBoxInstituicao.setEnabled(false);
            }
            
            if (!podeCriarUsuario){
                
                // É apenas o coordenador da turma //
                
                jButtonAdicionarNovoAprendiz.setVisible(false);
                jButtonAdicionarNovoCoordenador.setVisible(false);
            }
        }
        
        telaPrincipal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        ModalFrameUtil.showAsModalDontBlock(this);
        
        configuraTabela(jTableCoordenadores);
        configuraTabela(jTableAprendizes);
        
        setEnabled(true);
        
        jTextFieldNome.requestFocus();
    }
    
    private void configuraTabela(JTable jTable){
        
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer();
        cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer();
        cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable.getColumn(jTable.getColumnName(0)).setCellRenderer(cellRenderCentro);      
        jTable.getColumn(jTable.getColumnName(1)).setCellRenderer(cellRenderEsquerda);
        jTable.getColumn(jTable.getColumnName(2)).setCellRenderer(cellRenderCentro);
        jTable.getColumn(jTable.getColumnName(3)).setCellRenderer(cellRenderCentro);
        
        int largura = jTable.getWidth();
        
        jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth((int) (largura * 0.05));
        jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth((int) (largura * 0.65));
        jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth((int) (largura * 0.15));
        jTable.getColumn(jTable.getColumnName(3)).setPreferredWidth((int) (largura * 0.15));
    }
    
    private void atualizaTotalCaracteres(){
        
        String texto = jTextAreaTextoDescricao.getText();
        if (texto != null){
            int totalTeclado = texto.length();
            jLabelTotalCaracteres.setText("Total de Caracteres = " + totalTeclado);
        }
    }

    private void confirmaCancelar(){

        if (podeEditar){
            
            boolean sofreuAlteracao;
        
            if (nova) {
                sofreuAlteracao = true;
            } else {
                
                try {
                    if (salvarEntrada() != 0) {
                        sofreuAlteracao = true;
                    } else {
                        sofreuAlteracao = !turma.igual(turmaOriginal);
                    }
                } catch (Exception e) {
                    HeuChess.registraExcecao(e);
                    sofreuAlteracao = true;
                }
            }
        
            if (sofreuAlteracao){
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?",
                                                                 "Confirmação Cancelamento");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                    return;
                }
            }        
        }
        
        dispose();
        telaPrincipal.fechandoTelaTurma(null, false);
    }
    
    private InscricaoTurma recuperaInscricaoSelecionada(JTable jTable){
        
        int linhaSelecionada = jTable.getSelectedRow();
        
        if (linhaSelecionada == -1){
            return null;
        }
       
        TableModelInscricaoTurma inscricaoTurmaTableModel = (TableModelInscricaoTurma) jTable.getModel();
        InscricaoTurma inscricao = (InscricaoTurma) inscricaoTurmaTableModel.getLinhas().get(linhaSelecionada);
        
        return inscricao;
    }
    
    private int salvarEntrada() throws Exception {

        String nome = jTextFieldNome.getText();
        
        if (nome == null || nome.trim().length() == 0) {
            return -1;
        }

        String descricao = jTextAreaTextoDescricao.getText();
        
        if (descricao == null || descricao.trim().length() == 0) {
            return -2;
        }
        
        if (jComboBoxInstituicao.getSelectedIndex() == -1){
            return -3;
        }
        
        if (turma.inscricoesCoordenadores().isEmpty()) {
            return -4;
        }

        nome = UtilsString.preparaStringParaBD(jTextFieldNome.getText(), true, Formato.TUDO_MAIUSCULO);

        Instituicao instituicao = (Instituicao) jComboBoxInstituicao.getSelectedItem();
        
        if (nova || !nome.equalsIgnoreCase(turmaOriginal.getNome())){
            
            long idAchado = TurmaDAO.existeNome(instituicao,nome);
        
            if (idAchado != -1 && turma.getId() != idAchado){            
                return -5;
            }    
        }          
                            
        turma.setNome(nome);
        turma.setDescricao(descricao);         
        turma.setIdInstituicao(instituicao.getId());
        
        if (jRadioButtonSituacaoTurmaBloqueada.isSelected()){
            turma.setSituacao(Turma.BLOQUEADA);
        }else{
            turma.setSituacao(Turma.LIBERADA);
        }
        
        turma.setPermissoes(0);
        
        if (jRadioButtonPermitirAcessar.isSelected()){
            turma.permitir(Permissao.ACESSAR);            
        }else
            if (jRadioButtonPermitirUtilizar.isSelected()){
                turma.permitir(Permissao.ACESSAR);            
                turma.permitir(Permissao.UTILIZAR);
            }else
                if (jRadioButtonPermitirAnotar.isSelected()){
                    turma.permitir(Permissao.ACESSAR);            
                    turma.permitir(Permissao.UTILIZAR);
                    turma.permitir(Permissao.ANOTAR);
                }else
                    if (jRadioButtonPermitirAlterar.isSelected()){
                        turma.permitir(Permissao.ACESSAR);            
                        turma.permitir(Permissao.UTILIZAR);
                        turma.permitir(Permissao.ANOTAR);
                        turma.permitir(Permissao.ALTERAR);
                    }else
                        if (jRadioButtonPermitirCopiar.isSelected()){
                            turma.permitir(Permissao.ACESSAR);            
                            turma.permitir(Permissao.UTILIZAR);
                            turma.permitir(Permissao.ANOTAR);
                            turma.permitir(Permissao.ALTERAR);
                            turma.permitir(Permissao.COPIAR);
                        }else
                            if (jRadioButtonPermitirExcluir.isSelected()){
                                turma.permitir(Permissao.ACESSAR);            
                                turma.permitir(Permissao.UTILIZAR);
                                turma.permitir(Permissao.ANOTAR);
                                turma.permitir(Permissao.ALTERAR);
                                turma.permitir(Permissao.COPIAR);
                                turma.permitir(Permissao.EXCLUIR);
                             }
            
        return 0;         
    }
    
    private boolean validaNovoUsuario(Usuario usuario) {
        
        for (InscricaoTurma inscricao : turma.inscricoesCoordenadores()) {

            if (inscricao.getUsuario().getId() == usuario.getId()) {

                if (usuario.isSexoMasculino()) {
                    UtilsGUI.dialogoErro(this, "O usuário \"" + usuario.getNome() + "\"\njá está registrado como coordenador desta turma!");
                } else {
                    UtilsGUI.dialogoErro(this, "A usuária \"" + usuario.getNome() + "\"\njá está registrada como coordenadora desta turma!");
                }
                return false;
            }
        }

        for (InscricaoTurma inscricao : turma.inscricoesAprendizes()) {

            if (inscricao.getUsuario().getId() == usuario.getId()) {

                if (usuario.isSexoMasculino()) {
                    UtilsGUI.dialogoErro(this, "O usuário \"" + usuario.getNome() + "\"\njá está registrado como aprendiz desta turma!");
                } else {
                    UtilsGUI.dialogoErro(this, "A usuária \"" + usuario.getNome() + "\"\njá está registrada como aprendiz desta turma!");
                }
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void fechandoTelaLocalizaUsuario(Usuario usuario) {
        
        if (usuario != null) {
            
            if (!validaNovoUsuario(usuario)){
                return;
            }
            
            incluindoUsuario(usuario);
        }
        
        incluindo = Incluindo.NENHUM;
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null && novo) {
            incluindoUsuario(usuario);
        }
        
        incluindo = Incluindo.NENHUM;
    }
    
    private void incluindoUsuario(Usuario usuario) {
        
        if (incluindo == Incluindo.COORDENADOR) {

            turma.adicionaCoordenador(usuario);
            tableModelCoordenadores.update();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    UtilsGUI.moverScrollbarFim(jScrollPaneCoordenadores);
                }
            });           
        } else 
            if (incluindo == Incluindo.APRENDIZ) {

                turma.adicionaAprendiz(usuario);
                tableModelAprendizes.update();
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        UtilsGUI.moverScrollbarFim(jScrollPaneAprendizes);
                    }
                });
            }
    }
     
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupPermissoes = new javax.swing.ButtonGroup();
        buttonGroupSituacaoTurma = new javax.swing.ButtonGroup();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelDadosGerais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxInstituicao = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldNome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaTextoDescricao = new javax.swing.JTextArea();
        jLabelTotalCaracteres = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPaneCoordenadores = new javax.swing.JScrollPane();
        jTableCoordenadores = new javax.swing.JTable();
        jPanelBotoesCoordenadores = new javax.swing.JPanel();
        jButtonAdicionarNovoCoordenador = new javax.swing.JButton();
        jButtonAdicionarCoordenador = new javax.swing.JButton();
        jButtonRetirarCoordenador = new javax.swing.JButton();
        jPanelAprendizes = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPaneAprendizes = new javax.swing.JScrollPane();
        jTableAprendizes = new javax.swing.JTable();
        jPanelBotoesAprendizes = new javax.swing.JPanel();
        jButtonAdicionarNovoAprendiz = new javax.swing.JButton();
        jButtonAdicionarAprendiz = new javax.swing.JButton();
        jButtonRetirarAprendiz = new javax.swing.JButton();
        jPanelPermissoes = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonPermitirAcessar = new javax.swing.JRadioButton();
        jRadioButtonPermitirAnotar = new javax.swing.JRadioButton();
        jRadioButtonPermitirAlterar = new javax.swing.JRadioButton();
        jRadioButtonPermitirExcluir = new javax.swing.JRadioButton();
        jRadioButtonPermitirCopiar = new javax.swing.JRadioButton();
        jRadioButtonNenhumaPermissao = new javax.swing.JRadioButton();
        jRadioButtonPermitirUtilizar = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonSituacaoTurmaBloqueada = new javax.swing.JRadioButton();
        jRadioButtonSituacaoTurmaLiberada = new javax.swing.JRadioButton();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Turma");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_pessoas.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Instituição");

        for(Instituicao instituicao : instituicoes){
            jComboBoxInstituicao.addItem(instituicao);
        }

        jLabel15.setText("Nome");

        jTextFieldNome.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeKeyReleased(evt);
            }
        });

        jLabel4.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel17.setText("Descrição");

        jTextAreaTextoDescricao.setColumns(20);
        jTextAreaTextoDescricao.setLineWrap(true);
        jTextAreaTextoDescricao.setRows(5);
        jTextAreaTextoDescricao.setWrapStyleWord(true);
        jTextAreaTextoDescricao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jTextAreaTextoDescricao.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaTextoDescricaoKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTextAreaTextoDescricao);

        jLabelTotalCaracteres.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotalCaracteres.setText("Total de Caracteres = 0");

        jLabel2.setText("Coordenadores");

        jTableCoordenadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPaneCoordenadores.setViewportView(jTableCoordenadores);

        jButtonAdicionarNovoCoordenador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoa.png"))); // NOI18N
        jButtonAdicionarNovoCoordenador.setMnemonic('n');
        jButtonAdicionarNovoCoordenador.setText("Novo");
        jButtonAdicionarNovoCoordenador.setToolTipText("Cria um novo usuário e adiciona ele a turma como Coordenador");
        jButtonAdicionarNovoCoordenador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarNovoCoordenadorActionPerformed(evt);
            }
        });
        jPanelBotoesCoordenadores.add(jButtonAdicionarNovoCoordenador);

        jButtonAdicionarCoordenador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonAdicionarCoordenador.setMnemonic('a');
        jButtonAdicionarCoordenador.setText("Adicionar");
        jButtonAdicionarCoordenador.setToolTipText("Adiciona um coordenador a turma");
        jButtonAdicionarCoordenador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarCoordenadorActionPerformed(evt);
            }
        });
        jPanelBotoesCoordenadores.add(jButtonAdicionarCoordenador);

        jButtonRetirarCoordenador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonRetirarCoordenador.setMnemonic('r');
        jButtonRetirarCoordenador.setText("Retirar");
        jButtonRetirarCoordenador.setToolTipText("Retira o Coordenador selecionado da Turma");
        jButtonRetirarCoordenador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetirarCoordenadorActionPerformed(evt);
            }
        });
        jPanelBotoesCoordenadores.add(jButtonRetirarCoordenador);

        javax.swing.GroupLayout jPanelDadosGeraisLayout = new javax.swing.GroupLayout(jPanelDadosGerais);
        jPanelDadosGerais.setLayout(jPanelDadosGeraisLayout);
        jPanelDadosGeraisLayout.setHorizontalGroup(
            jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneCoordenadores)
                    .addComponent(jPanelBotoesCoordenadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanelDadosGeraisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxInstituicao, 0, 673, Short.MAX_VALUE)
                            .addGroup(jPanelDadosGeraisLayout.createSequentialGroup()
                                .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosGeraisLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelTotalCaracteres, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelDadosGeraisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelDadosGeraisLayout.setVerticalGroup(
            jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxInstituicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalCaracteres)
                .addGap(1, 1, 1)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneCoordenadores, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesCoordenadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Dados Gerais", jPanelDadosGerais);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Aprendizes Inscritos na Turma");

        jTableAprendizes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPaneAprendizes.setViewportView(jTableAprendizes);

        jButtonAdicionarNovoAprendiz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoa.png"))); // NOI18N
        jButtonAdicionarNovoAprendiz.setMnemonic('n');
        jButtonAdicionarNovoAprendiz.setText("Novo");
        jButtonAdicionarNovoAprendiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarNovoAprendizActionPerformed(evt);
            }
        });
        jPanelBotoesAprendizes.add(jButtonAdicionarNovoAprendiz);

        jButtonAdicionarAprendiz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonAdicionarAprendiz.setMnemonic('a');
        jButtonAdicionarAprendiz.setText("Adicionar");
        jButtonAdicionarAprendiz.setToolTipText("Adiciona um Aprendiz a Turma");
        jButtonAdicionarAprendiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarAprendizActionPerformed(evt);
            }
        });
        jPanelBotoesAprendizes.add(jButtonAdicionarAprendiz);

        jButtonRetirarAprendiz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonRetirarAprendiz.setMnemonic('r');
        jButtonRetirarAprendiz.setText("Retirar");
        jButtonRetirarAprendiz.setToolTipText("Retira o Aprendiz selecionado da Turma");
        jButtonRetirarAprendiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetirarAprendizActionPerformed(evt);
            }
        });
        jPanelBotoesAprendizes.add(jButtonRetirarAprendiz);

        javax.swing.GroupLayout jPanelAprendizesLayout = new javax.swing.GroupLayout(jPanelAprendizes);
        jPanelAprendizes.setLayout(jPanelAprendizesLayout);
        jPanelAprendizesLayout.setHorizontalGroup(
            jPanelAprendizesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAprendizesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAprendizesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBotoesAprendizes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelAprendizesLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPaneAprendizes, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelAprendizesLayout.setVerticalGroup(
            jPanelAprendizesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAprendizesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneAprendizes, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesAprendizes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Aprendizes", jPanelAprendizes);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Permissões de uso dos Objetos válidas para os Membros desta Turma"));

        buttonGroupPermissoes.add(jRadioButtonPermitirAcessar);
        jRadioButtonPermitirAcessar.setText("Permitir apenas que VEJAM os objetos dos outros");

        buttonGroupPermissoes.add(jRadioButtonPermitirAnotar);
        jRadioButtonPermitirAnotar.setText("Permitir apenas que VEJAM, UTILIZEM e ANOTEM os objetos dos outros");

        buttonGroupPermissoes.add(jRadioButtonPermitirAlterar);
        jRadioButtonPermitirAlterar.setText("Permitir apenas que VEJAM, UTILIZEM, ANOTEM e ALTEREM os objetos dos outros");

        buttonGroupPermissoes.add(jRadioButtonPermitirExcluir);
        jRadioButtonPermitirExcluir.setText("Permitir TUDO. Que VEJAM, UTILIZEM, ANOTEM, ALTEREM, COPIEM e até EXCLUAM os objetos dos outros");
        jRadioButtonPermitirExcluir.setEnabled(false);
        jRadioButtonPermitirExcluir.setVisible(false);

        buttonGroupPermissoes.add(jRadioButtonPermitirCopiar);
        jRadioButtonPermitirCopiar.setText("Permitir apenas que VEJAM, UTILIZEM, ANOTEM, ALTEREM, e COPIEM os objetos dos outros");
        jRadioButtonPermitirCopiar.setEnabled(false);
        jRadioButtonPermitirCopiar.setVisible(false);

        buttonGroupPermissoes.add(jRadioButtonNenhumaPermissao);
        jRadioButtonNenhumaPermissao.setSelected(true);
        jRadioButtonNenhumaPermissao.setText("Nenhuma permissão. Cada um só pode acessar os seus objetos");

        buttonGroupPermissoes.add(jRadioButtonPermitirUtilizar);
        jRadioButtonPermitirUtilizar.setText("Permitir apenas que VEJAM e UTILIZEM os objetos dos outros");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonPermitirExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPermitirCopiar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPermitirAlterar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPermitirAnotar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPermitirUtilizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonPermitirAcessar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonNenhumaPermissao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jRadioButtonNenhumaPermissao, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirAcessar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirUtilizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirAnotar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirAlterar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirCopiar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonPermitirExcluir)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Situação da Turma"));

        buttonGroupSituacaoTurma.add(jRadioButtonSituacaoTurmaBloqueada);
        jRadioButtonSituacaoTurmaBloqueada.setSelected(true);
        jRadioButtonSituacaoTurmaBloqueada.setText("Bloqueada. Ninguém tem acesso a ela.");

        buttonGroupSituacaoTurma.add(jRadioButtonSituacaoTurmaLiberada);
        jRadioButtonSituacaoTurmaLiberada.setText("Liberada. Os membros podem acessá-la");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButtonSituacaoTurmaBloqueada, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jRadioButtonSituacaoTurmaLiberada, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonSituacaoTurmaBloqueada)
                    .addComponent(jRadioButtonSituacaoTurmaLiberada))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelPermissoesLayout = new javax.swing.GroupLayout(jPanelPermissoes);
        jPanelPermissoes.setLayout(jPanelPermissoesLayout);
        jPanelPermissoesLayout.setHorizontalGroup(
            jPanelPermissoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPermissoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPermissoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(181, Short.MAX_VALUE))
        );
        jPanelPermissoesLayout.setVerticalGroup(
            jPanelPermissoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPermissoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        jTabbedPanePrincipal.addTab("Permissões", jPanelPermissoes);

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setMnemonic('C');
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('n');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBaseLayout = new javax.swing.GroupLayout(jPanelBase);
        jPanelBase.setLayout(jPanelBaseLayout);
        jPanelBaseLayout.setHorizontalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonConfirmar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGroup(jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConfirmar)
                    .addComponent(jButtonAjuda)
                    .addComponent(jButtonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanelBaseLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPanePrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPanePrincipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-797)/2, (screenSize.height-554)/2, 797, 554);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextAreaTextoDescricaoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaTextoDescricaoKeyReleased
        atualizaTotalCaracteres();
    }//GEN-LAST:event_jTextAreaTextoDescricaoKeyReleased

    private void jTextFieldNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeKeyReleased
        setTitle("Turma - " + jTextFieldNome.getText());
    }//GEN-LAST:event_jTextFieldNomeKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed

        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            int erro = salvarEntrada();

            switch (erro) {

                case 0:// Nenhum erro //
                    break;

                case -1:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "O nome da Turma não esta preenchido.\n" +
                                               "Uma Turma precisa ter um nome definido para poder ser salva!");
                    
                    jTextFieldNome.requestFocus();
                    return;

                case -2:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A descrição da Turma não foi preenchida.\n" +
                                               "Uma Turma precisa ter um texto de descrição para poder ser salva!");
                    
                    jTextAreaTextoDescricao.requestFocus();
                    return;

                case -3:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A Instituição a qual a Turma está relacionada não foi preenchida.\n" +
                                               "Uma Turma precisa estar associada a uma Instituição para poder ser salva!");
                    
                    jComboBoxInstituicao.requestFocus();
                    return;

                case -4:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A Turma não possui nenhum Coordenador definido.\n" +
                                               "Uma Turma precisa ter pelo menos um coordenador escolhido para poder ser salva!");
                    
                    jButtonAdicionarCoordenador.requestFocus();
                    return;

                case -5:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    Instituicao instituicao = (Instituicao) jComboBoxInstituicao.getSelectedItem();

                    UtilsGUI.dialogoErro(this, "Você já possui uma Turma chamada \"" + jTextFieldNome.getText() +
                                               "\"\nrelacionada a Instituição \"" + instituicao + "\"!\n\nEscolha outro nome de Turma.");
                    
                    jTextFieldNome.requestFocus();
                    jTextFieldNome.selectAll();
                    return;

                default:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Código de erro não tratado pela aplicação [" + erro + "]");
                    
                    IllegalArgumentException excecao = new IllegalArgumentException("Código de erro não tratado pela aplicação [" + erro + "]");
                    HeuChess.registraExcecao(excecao);
                    throw excecao;
            }
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao verificar os valores editados da Turma!\nOperação Cancelada!");
            return;
        }
        
        if (nova) {
                        
            try{
                TurmaDAO.adiciona(turma);
                
                ConexaoDBHeuChess.commit();
            
                dispose();
                telaPrincipal.fechandoTelaTurma(turma, true); 
            
            }catch(Exception e){
                HeuChess.desfazTransacao(e);
                    
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Turma no Banco de Dados\nOperação Cancelada!");
            }
        } else             
            if (!turma.igual(turmaOriginal)) {
                
                try{
                    TurmaDAO.atualiza(turma,turmaOriginal);
                    
                    ConexaoDBHeuChess.commit();
                    
                    dispose();
                    telaPrincipal.fechandoTelaTurma(turma, false); 
                
                }catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Turma no Banco de Dados\nOperação Cancelada!");                    
                }
            }else{
                dispose();
                telaPrincipal.fechandoTelaTurma(null, false);                 
            }        
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaTurma");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonAdicionarCoordenadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarCoordenadorActionPerformed
        
        incluindo = Incluindo.COORDENADOR;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaLocalizaUsuario tela = new TelaLocalizaUsuario(TelaTurma.this);
            }
        });
    }//GEN-LAST:event_jButtonAdicionarCoordenadorActionPerformed

    private void jButtonAdicionarAprendizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarAprendizActionPerformed
        
        incluindo = Incluindo.APRENDIZ;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaLocalizaUsuario tela = new TelaLocalizaUsuario(TelaTurma.this);
            }
        });
    }//GEN-LAST:event_jButtonAdicionarAprendizActionPerformed

    private void jButtonRetirarCoordenadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetirarCoordenadorActionPerformed
        
        InscricaoTurma inscricao = recuperaInscricaoSelecionada(jTableCoordenadores);
        
        if (inscricao != null){

            String mensagem = "Deseja realmente retirar desta turma " + 
                              (inscricao.getUsuario().isSexoMasculino() ? "o Coordenador\n\"" : "a Coordenadora\n\"") +
                               inscricao.getUsuario().getNome() + "\"?";
                        
            int resposta = UtilsGUI.dialogoConfirmacao(this,mensagem,"Confirmação de Retirada");            
            if (resposta == JOptionPane.YES_OPTION) {
                
                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }
                
                turma.inscricoesCoordenadores().remove(inscricao);
                
                tableModelCoordenadores.update();
            }
            
        }else{
            jButtonRetirarCoordenador.setEnabled(false);
        }
    }//GEN-LAST:event_jButtonRetirarCoordenadorActionPerformed

    private void jButtonRetirarAprendizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetirarAprendizActionPerformed
  
        InscricaoTurma inscricao = recuperaInscricaoSelecionada(jTableAprendizes);
        
        if (inscricao != null){

            String mensagem = "Deseja realmente retirar desta turma " + 
                              (inscricao.getUsuario().isSexoMasculino() ? "o Aprendiz\n\"" : "a Aprendiz\n\"") +
                               inscricao.getUsuario().getNome() + "\"?";
            
            int resposta = UtilsGUI.dialogoConfirmacao(this,mensagem,"Confirmação de Retirada");            
            if (resposta == JOptionPane.YES_OPTION) {
                
                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }
                
                turma.inscricoesAprendizes().remove(inscricao);
                
                tableModelAprendizes.update();
            }
            
        }else{
            jButtonRetirarAprendiz.setEnabled(false);
        }
    }//GEN-LAST:event_jButtonRetirarAprendizActionPerformed

    private void jButtonAdicionarNovoCoordenadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarNovoCoordenadorActionPerformed

        incluindo = Incluindo.COORDENADOR;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaUsuario tela = new TelaUsuario(TelaTurma.this,Usuario.COORDENADOR);
            }
        });
    }//GEN-LAST:event_jButtonAdicionarNovoCoordenadorActionPerformed

    private void jButtonAdicionarNovoAprendizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarNovoAprendizActionPerformed
        
        incluindo = Incluindo.APRENDIZ;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaUsuario tela = new TelaUsuario(TelaTurma.this,Usuario.APRENDIZ);
            }
        });
    }//GEN-LAST:event_jButtonAdicionarNovoAprendizActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupPermissoes;
    private javax.swing.ButtonGroup buttonGroupSituacaoTurma;
    private javax.swing.JButton jButtonAdicionarAprendiz;
    private javax.swing.JButton jButtonAdicionarCoordenador;
    private javax.swing.JButton jButtonAdicionarNovoAprendiz;
    private javax.swing.JButton jButtonAdicionarNovoCoordenador;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonRetirarAprendiz;
    private javax.swing.JButton jButtonRetirarCoordenador;
    private javax.swing.JComboBox jComboBoxInstituicao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTotalCaracteres;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelAprendizes;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelBotoesAprendizes;
    private javax.swing.JPanel jPanelBotoesCoordenadores;
    private javax.swing.JPanel jPanelDadosGerais;
    private javax.swing.JPanel jPanelPermissoes;
    private javax.swing.JRadioButton jRadioButtonNenhumaPermissao;
    private javax.swing.JRadioButton jRadioButtonPermitirAcessar;
    private javax.swing.JRadioButton jRadioButtonPermitirAlterar;
    private javax.swing.JRadioButton jRadioButtonPermitirAnotar;
    private javax.swing.JRadioButton jRadioButtonPermitirCopiar;
    private javax.swing.JRadioButton jRadioButtonPermitirExcluir;
    private javax.swing.JRadioButton jRadioButtonPermitirUtilizar;
    private javax.swing.JRadioButton jRadioButtonSituacaoTurmaBloqueada;
    private javax.swing.JRadioButton jRadioButtonSituacaoTurmaLiberada;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneAprendizes;
    private javax.swing.JScrollPane jScrollPaneCoordenadores;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTable jTableAprendizes;
    private javax.swing.JTable jTableCoordenadores;
    private javax.swing.JTextArea jTextAreaTextoDescricao;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldNome;
    // End of variables declaration//GEN-END:variables
}
