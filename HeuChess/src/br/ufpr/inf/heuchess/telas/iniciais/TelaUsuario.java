package br.ufpr.inf.heuchess.telas.iniciais;

import br.net.sercomtel.eti.util.gui.JFMaskedTextField;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.DocumentMasked;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Frame;
import java.text.ParseException;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 2, 2012
 */
public class TelaUsuario extends javax.swing.JFrame implements AcessoTelaTrocaSenha {
    
    private Usuario   usuario;
    private Usuario   usuarioOriginal;    
        
    private AcessoTelaUsuario acessoTelaUsuario;
    
    private boolean novo;    
    private boolean senhaInicial;
    private boolean podeEditar;
    private boolean podeMudarSituacao;
    
    /**
     * Criando um novo Usuário
     */
    public TelaUsuario(AcessoTelaUsuario acessoTelaUsuario) {
        
        acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        this.acessoTelaUsuario = acessoTelaUsuario;
        
        initComponents();        
        
        usuario = new Usuario();
        usuario.setSituacao(Usuario.TROCANDO_SENHA);
        
        novo       = true;
        podeEditar = true;
                
        montarInterface(null);     
    }
    
    /**
     * Criando um novo Usuário de um tipo específico
     */
    public TelaUsuario(AcessoTelaUsuario acessoTelaUsuario, Tipo tipo) {
        
        acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        this.acessoTelaUsuario = acessoTelaUsuario;
        
        initComponents();        
        
        usuario = new Usuario();
        usuario.setSituacao(Usuario.TROCANDO_SENHA);
        
        novo       = true;
        podeEditar = true;
        
        montarInterface(tipo);
    }
    
    /**
     * Abrindo os Dados de Um Usuário já existente
     */
    public TelaUsuario(AcessoTelaUsuario acessoTelaUsuario, Usuario usuario) {
        
        this.acessoTelaUsuario = acessoTelaUsuario;
        usuarioOriginal        = usuario;
            
        this.usuario = usuario.geraClone();
        
        // Pode editar os dados de um usuário caso seja Administrador, o próprio usuário, o coordenador de uma turma do usuário,
        // ou coordenador de uma instituição ligada a uma turma do usuário
        
        if (usuario.getTipo() == Usuario.ADMINISTRADOR && HeuChess.usuario.getTipo() != Usuario.ADMINISTRADOR){
            
            // Caso o usuário a ser editado seja um Administrador, este só pode ser editado por outro Administrador     
            
        }else        
            if (usuario.getId() == 1 && usuario.getId() != HeuChess.usuario.getId()){
                
                // O usuário de código 1 (principal do sistema) só pode ser editado por ele mesmo
        
            }else
                if (HeuChess.usuario.getId() == usuario.getId()){
                    podeEditar = true;
                }else
                    if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR){
                        podeEditar        = true;
                        podeMudarSituacao = true;
                    }else{
                        try {
                            if (UsuarioDAO.coordenoTurma(HeuChess.usuario, usuario.getId()) != -1) {
                                podeEditar        = true;
                                podeMudarSituacao = true;
                            } else 
                                if (UsuarioDAO.coordenoInstituicaoTurma(HeuChess.usuario, usuario.getId()) != -1) {
                                    podeEditar        = true;
                                    podeMudarSituacao = true;
                                }
                        } catch (Exception e) {
                            HeuChess.desfazTransacao(e);
                            
                            acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            UtilsGUI.dialogoErro(acessoTelaUsuario.getFrame(), "Erro ao verificar permissões de Edição no Banco de Dados!");
                            
                            dispose();
                            acessoTelaUsuario.fechandoTelaUsuario(null, false);
                        }
                    }
                
        initComponents();                
        
        montarInterface(null);
    }
    
    private JFormattedTextField criaCampoDataMascarado(){
        
        JFMaskedTextField campoDataNascimento; 
        
        try {
            campoDataNascimento = new JFMaskedTextField("##/##/####",'_',20);
        } catch (ParseException ex) {
            HeuChess.registraExcecao(ex);
            UtilsGUI.dialogoErro(acessoTelaUsuario.getFrame(),"Erro ao construir interface para campo Data!");
            dispose();
            return null;
        }
        
        return (JFormattedTextField) campoDataNascimento;
    }
    
    @Override
    public Frame getFrame(){
        return this;        
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaUsuario;
    } 
    
    private void montarInterface(Tipo tipo){       
            
        if (novo){                 
        
            setTitle("Usuário - Novo");
            
            jButtonTrocarSenha.setEnabled(false);
            jTextFieldIdentificaoUsuario.setNextFocusableComponent(jPasswordFieldSenha);
            
            if (tipo != null){
            
                if (tipo == Usuario.APRENDIZ){
                    jComboBoxFuncao.addItem(Usuario.APRENDIZ);
                    jComboBoxFuncao.setSelectedItem(Usuario.APRENDIZ);
                    jComboBoxFuncao.setEnabled(false);
                }else
                    if (tipo == Usuario.COORDENADOR){
                        jComboBoxFuncao.addItem(Usuario.COORDENADOR);
                        jComboBoxFuncao.setSelectedItem(Usuario.COORDENADOR);
                        jComboBoxFuncao.setEnabled(false);
                    }else{                        
                        UtilsGUI.dialogoErro(this,"Tipo não suportado pela tela [" + tipo + "]");                        
                        dispose();
                        return;
                    }
            }else{
                 jComboBoxFuncao.addItem(Usuario.APRENDIZ);
        
                 if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR){
                     jComboBoxFuncao.addItem(Usuario.ADMINISTRADOR);
                 }
            }
            
            jLabelSituacao.setVisible(false);
            jComboBoxSituacao.setVisible(false);
            
        }else{
            
            setTitle("Usuário - " + jTextFieldNome.getText());            
            
            jTextFieldNome.setText(usuario.getNome());
            jTextFieldEmail.setText(usuario.getEmail());
            jTextFieldIdentificaoUsuario.setText(usuario.getLogin());
            jPasswordFieldSenha.setText(usuario.getSenha());
            jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(usuario.getDataCriacao()));
            jFormattedTextFieldDataNascimento.setText(UtilsDataTempo.formataData(usuario.getDataNascimento()));
       
            if (usuario.isSexoMasculino()){
                jRadioButtonSexoMasculino.setSelected(true);      
            }else{
                jRadioButtonSexoFeminino.setSelected(true);
            }
            
            jComboBoxFuncao.addItem(usuario.getTipo());  
            jComboBoxFuncao.setSelectedItem(usuario.getTipo());
            
            if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR) {
                
                if (usuario.getTipo() == Usuario.ADMINISTRADOR){
                    jComboBoxFuncao.addItem(Usuario.APRENDIZ);
                }else{
                    jComboBoxFuncao.addItem(Usuario.ADMINISTRADOR);
                }
             
                if (usuario.getId() == 1){
                    
                    // O usuário 1 só pode ser editado por ele mesmo //
                    
                    jComboBoxFuncao.setEnabled(false);
                }else{
                    jComboBoxFuncao.setEnabled(true);
                }
            }else{
                jComboBoxFuncao.setEnabled(false);
            }
            
            jComboBoxSituacao.setSelectedItem(usuario.getSituacao());
            
            if (podeMudarSituacao){
                
                jComboBoxSituacao.addItem(usuario.getSituacao());

                if (usuario.getSituacao() == Usuario.BLOQUEADO){
                    jComboBoxSituacao.addItem(Usuario.LIBERADO);
                }else{
                    jComboBoxSituacao.addItem(Usuario.BLOQUEADO);
                }
                
            }else{
                jLabelSituacao.setVisible(false);
                jComboBoxSituacao.setVisible(false);
            }
        }                
      
        if (HeuChess.usuario.getId() != usuario.getId()) {
            
            if (novo) {
                jLabelSenha.setText("Senha Inicial");
                jPasswordFieldSenha.setEditable(true);
                jPasswordFieldSenha.setEchoChar((char) 0);
                jButtonTrocarSenha.setVisible(false);
            } else {
                jButtonTrocarSenha.setText("Reinicar");
                jButtonTrocarSenha.setMnemonic('r');
            }
        }
     
        if (!podeEditar){
            
            jTextFieldNome.setEditable(false);
            jTextFieldEmail.setEditable(false);
            jFormattedTextFieldDataNascimento.setEditable(false);
            jRadioButtonSexoMasculino.setEnabled(false);
            jRadioButtonSexoFeminino.setEnabled(false);
            
            //jPanelDadosAcessoSistema.setVisible(false);
            //setSize(getWidth(),getHeight()-175);
            //setLocationRelativeTo(null);
            
            setSize(getWidth(),getHeight()-50);
            jLabelIdentificacao.setVisible(false);
            jTextFieldIdentificaoUsuario.setVisible(false);
            jLabelSenha.setVisible(false);
            jPasswordFieldSenha.setVisible(false);
            jButtonTrocarSenha.setVisible(false);
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
        
        acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        ModalFrameUtil.showAsModalDontBlock(this); 
        jTextFieldNome.requestFocus();
    }
    
    private void confirmaCancelar(){
        
        if (podeEditar){
            
            boolean sofreuAlteracao;
        
            if (novo) {
                sofreuAlteracao = true;
            } else {

                try {
                    if (salvarEntrada() != 0) {
                        sofreuAlteracao = true;
                    } else {
                        sofreuAlteracao = !usuario.igual(usuarioOriginal);
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
        acessoTelaUsuario.fechandoTelaUsuario(null, false);
    }
    
    @Override
    public void fechandoTelaTrocaSenha(String novaSenha){
    
        if (novaSenha != null){
            jPasswordFieldSenha.setText(HeuChess.converteSenha(novaSenha));
        }
    }
    
    private int salvarEntrada() throws Exception {
                
        String nome  = jTextFieldNome.getText();
        String email = jTextFieldEmail.getText();
        String login = jTextFieldIdentificaoUsuario.getText();
        String senha = new String(jPasswordFieldSenha.getPassword());
        
        if (nome == null || nome.trim().length() == 0) {
            return -1;
        }

        if (email == null || email.trim().length() == 0) {
            return -2;
        }

        if (login == null || login.trim().length() == 0) {
            return -3;
        }
        
        if (novo || senhaInicial) {
            
            if (senha == null || senha.trim().length() == 0) {
                return -4;
            }
            
            if (senha.trim().length() < 6) {
                return -5;
            }             
        }
        
        String nascimento = jFormattedTextFieldDataNascimento.getText();
        
        if (nascimento == null || nascimento.trim().length() == 0) {
            return -6;
        }
        
        Date dataNascimento;
        
        try{
            dataNascimento = UtilsDataTempo.converteToDate(nascimento,"dd/MM/yyyy");
        }catch(Exception e){
            return -7;
        }    
                
        if (novo){
            
            if (UsuarioDAO.existeEmail(email) != -1){
                return -8;
            }
            if (UsuarioDAO.existeLogin(login) != -1){
                return -9;
            }
        }else{
            long usuarioAchado = UsuarioDAO.existeEmail(email);
            
            if (usuarioAchado != -1 && usuarioAchado != usuario.getId()){
                return -8;                
            }
            
            usuarioAchado = UsuarioDAO.existeLogin(login);
            
            if (usuarioAchado != -1 && usuarioAchado != usuario.getId()){
                return -9;
            }
        }
        
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        
        if (novo || senhaInicial){
            usuario.setSenha(HeuChess.converteSenha(senha));
        }else{
            usuario.setSenha(senha);
        }        
        
        usuario.setDataNascimento(dataNascimento);
        usuario.setTipo((Tipo) jComboBoxFuncao.getSelectedItem());
        
        if (jRadioButtonSexoMasculino.isSelected()){
            usuario.setSexoMasculino(true);
        }else
            if (jRadioButtonSexoFeminino.isSelected()){
                usuario.setSexoMasculino(false);
            }else{
                return -10;
            }            
        
        if (podeMudarSituacao){
            usuario.setSituacao((Situacao) jComboBoxSituacao.getSelectedItem());
        }
        
        return 0;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSexo = new javax.swing.ButtonGroup();
        jPanelCentral = new javax.swing.JPanel();
        jPanelDadosPessoais = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jFormattedTextFieldDataNascimento = criaCampoDataMascarado();
        jLabel8 = new javax.swing.JLabel();
        jRadioButtonSexoMasculino = new javax.swing.JRadioButton();
        jRadioButtonSexoFeminino = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldNome = new javax.swing.JTextField();
        jPanelDadosAcessoSistema = new javax.swing.JPanel();
        jLabelIdentificacao = new javax.swing.JLabel();
        jTextFieldIdentificaoUsuario = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabelSenha = new javax.swing.JLabel();
        jPasswordFieldSenha = new javax.swing.JPasswordField();
        jLabel10 = new javax.swing.JLabel();
        jComboBoxFuncao = new javax.swing.JComboBox();
        jButtonTrocarSenha = new javax.swing.JButton();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabelSituacao = new javax.swing.JLabel();
        jComboBoxSituacao = new javax.swing.JComboBox();
        jPanelBase = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Usuário");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_pessoa.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelCentral.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanelDadosPessoais.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "  Dados Pessoais  "));

        jLabel5.setText("Data de Nascimento");

        jFormattedTextFieldDataNascimento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextFieldDataNascimento.setNextFocusableComponent(jComboBoxFuncao);

        jLabel8.setText("Sexo");

        buttonGroupSexo.add(jRadioButtonSexoMasculino);
        jRadioButtonSexoMasculino.setText("Masculino");
        jRadioButtonSexoMasculino.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonSexoMasculino.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSexoMasculino.setNextFocusableComponent(jRadioButtonSexoFeminino);

        buttonGroupSexo.add(jRadioButtonSexoFeminino);
        jRadioButtonSexoFeminino.setText("Feminino");
        jRadioButtonSexoFeminino.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonSexoFeminino.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonSexoFeminino.setNextFocusableComponent(jFormattedTextFieldDataNascimento);

        jLabel3.setText("e-mail");

        jTextFieldEmail.setNextFocusableComponent(jRadioButtonSexoMasculino);
        jTextFieldEmail.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_VERY_SMALL));

        jLabel2.setText("Nome");

        jTextFieldNome.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNome.setNextFocusableComponent(jTextFieldEmail);
        jTextFieldNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanelDadosPessoaisLayout = new javax.swing.GroupLayout(jPanelDadosPessoais);
        jPanelDadosPessoais.setLayout(jPanelDadosPessoaisLayout);
        jPanelDadosPessoaisLayout.setHorizontalGroup(
            jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jRadioButtonSexoMasculino, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButtonSexoFeminino, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextFieldDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 69, Short.MAX_VALUE))
                            .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldNome))))
                        .addContainerGap())
                    .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanelDadosPessoaisLayout.setVerticalGroup(
            jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPessoaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosPessoaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jRadioButtonSexoMasculino)
                    .addComponent(jRadioButtonSexoFeminino)
                    .addComponent(jLabel5)
                    .addComponent(jFormattedTextFieldDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanelDadosAcessoSistema.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "   Dados de Acesso ao Sistema  "));

        jLabelIdentificacao.setText("Identificação");

        jTextFieldIdentificaoUsuario.setNextFocusableComponent(jButtonTrocarSenha);
        jTextFieldIdentificaoUsuario.setDocument(new DocumentMasked(DocumentMasked.ENTRANCE_ANY_CHARACTER,DocumentMasked.ONLY_VERY_SMALL));

        jLabel7.setText("Data de Cadastro");

        jLabelSenha.setText("Senha");

        jPasswordFieldSenha.setEditable(false);
        jPasswordFieldSenha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPasswordFieldSenha.setNextFocusableComponent(jTextFieldNome);
        jPasswordFieldSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordFieldSenhaFocusGained(evt);
            }
        });

        jLabel10.setText("Função");

        jComboBoxFuncao.setNextFocusableComponent(jTextFieldIdentificaoUsuario);
        jComboBoxFuncao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        jButtonTrocarSenha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/reset16.png"))); // NOI18N
        jButtonTrocarSenha.setMnemonic('t');
        jButtonTrocarSenha.setText("Trocar Senha");
        jButtonTrocarSenha.setToolTipText("Troca a Senha de Acesso");
        jButtonTrocarSenha.setNextFocusableComponent(jTextFieldNome);
        jButtonTrocarSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTrocarSenhaActionPerformed(evt);
            }
        });

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelSituacao.setText("Situação");

        jComboBoxSituacao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelDadosAcessoSistemaLayout = new javax.swing.GroupLayout(jPanelDadosAcessoSistema);
        jPanelDadosAcessoSistema.setLayout(jPanelDadosAcessoSistemaLayout);
        jPanelDadosAcessoSistemaLayout.setHorizontalGroup(
            jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosAcessoSistemaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelIdentificacao, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabelSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelDadosAcessoSistemaLayout.createSequentialGroup()
                        .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jButtonTrocarSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                    .addComponent(jTextFieldIdentificaoUsuario)
                    .addComponent(jComboBoxFuncao, 0, 250, Short.MAX_VALUE))
                .addGap(25, 25, 25)
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosAcessoSistemaLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelDadosAcessoSistemaLayout.createSequentialGroup()
                        .addComponent(jLabelSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanelDadosAcessoSistemaLayout.setVerticalGroup(
            jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosAcessoSistemaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jComboBoxFuncao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelSituacao)
                        .addComponent(jComboBoxSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelIdentificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldIdentificaoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosAcessoSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSenha)
                    .addComponent(jPasswordFieldSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTrocarSenha))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanelCentralLayout = new javax.swing.GroupLayout(jPanelCentral);
        jPanelCentral.setLayout(jPanelCentralLayout);
        jPanelCentralLayout.setHorizontalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelDadosAcessoSistema, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelDadosPessoais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelCentralLayout.setVerticalGroup(
            jPanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDadosPessoais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelDadosAcessoSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

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
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(jPanelBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-723)/2, (screenSize.height-451)/2, 723, 451);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNomeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeKeyReleased
        setTitle("Usuário - " + jTextFieldNome.getText());
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
                    
                    UtilsGUI.dialogoErro(this, "O nome do Usuário não esta preenchido.\n" +
                                               "Um usuário precisa ter um nome definido para poder ser salvo!");
                    
                    jTextFieldNome.requestFocus();
                    return;

                case -2:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "O e-mail do Usuário não foi preenchido.\n" +
                                               "Um usuário precisa ter um e-mail definido para poder ser salvo!");
                    
                    jTextFieldEmail.requestFocus();
                    return;

                case -3:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A identificação única de acesso ao sistema não foi preenchida.\n" +
                                               "Um usuário precisa ter uma identificação única de acesso ao sistema para poder ser salvo!");
                    
                    jTextFieldIdentificaoUsuario.requestFocus();
                    return;

                case -4:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A senha de acesso ao sistema não foi preenchida.\n" +
                                               "Um usuário precisa ter uma senha para poder ser salvo!");
                    
                    jPasswordFieldSenha.requestFocus();
                    return;

                case -5:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A senha deve ter no mínimo 6 caracteres!");
                    
                    jPasswordFieldSenha.requestFocus();
                    return;

                case -6:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "A data de nascimento não foi preenchida.\n" +
                                               "Um usuário precisa ter uma data de nascimento preenchida para poder ser salvo!");
                    
                    jFormattedTextFieldDataNascimento.requestFocus();
                    return;

                case -7:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Erro na conversão da Data de Nascimento.\n" +
                                               "A data deve ser preenchida no formato dia, mês, e ano. DD/MM/AAAA!");
                    
                    jFormattedTextFieldDataNascimento.requestFocus();
                    return;

                case -8:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Já existe um usuário utilizando este e-mail!.\nEscolha outro.");
                    
                    jTextFieldEmail.requestFocus();
                    jTextFieldEmail.selectAll();
                    return;

                case -9:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Já existe um usuário utilizando esta identificação de acesso!.\nEscolha outra.");
                    
                    jTextFieldIdentificaoUsuario.requestFocus();
                    jTextFieldIdentificaoUsuario.selectAll();
                    return;

                case -10:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "O sexo do Usuário não esta preenchido.\n" +
                                               "Um usuário precisa ter o sexo especificado para poder ser salvo!");
                    
                    jRadioButtonSexoMasculino.requestFocus();
                    return;
                        
                default:
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                    UtilsGUI.dialogoErro(this, "Código de erro não tratado pela aplicação [" + erro + "]");
                    
                    IllegalArgumentException excecao = new IllegalArgumentException("Código de erro não tratado pela aplicação [" + erro + "]");
                    HeuChess.registraExcecao(excecao);
                    throw excecao;
            }
            
            if (novo || !usuario.getNome().equalsIgnoreCase(usuarioOriginal.getNome())) {

                if (UsuarioDAO.existeHomonimo(usuario) != -1) {
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    int resposta = UtilsGUI.dialogoConfirmacao(this, "Já existe um outro usuário com este mesmo nome.\nDeseja mesmo assim " +
                                                                     (novo ? "criar este novo usuário?" : "alterar o nome deste usuário?"),
                                                                     "Confirmação Cancelamento");

                    if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                        jTextFieldNome.requestFocus();
                        jTextFieldNome.selectAll();
                        return;
                    }
                }
            }
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao verificar os valores editados do Usuário!\nOperação Cancelada!");
            return;
        }
        
        if (novo) {
            
            try {
                UsuarioDAO.adiciona(usuario);
                
                ConexaoDBHeuChess.commit();
                
                dispose();
                acessoTelaUsuario.fechandoTelaUsuario(usuario, true); 
            
            }catch (Exception e){
                HeuChess.desfazTransacao(e);
                    
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar o Usuário no Banco de Dados\nOperação Cancelada!");
            }
        } else             
            if (!usuario.igual(usuarioOriginal)) {
                
                try {
                    UsuarioDAO.atualiza(usuario);
                    
                    ConexaoDBHeuChess.commit();
                
                    if (usuario.getId() == HeuChess.usuario.getId()){
                        HeuChess.telaPrincipal.atualizaUsuarioConectado(usuario);
                    }
                    
                    dispose();
                    acessoTelaUsuario.fechandoTelaUsuario(usuario, false); 
                    
                } catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this,"Erro ao tentar atualizar o Usuário no Banco de Dados\nOperação Cancelada!");
                }
            }else{
                dispose();
                acessoTelaUsuario.fechandoTelaUsuario(null,false);                 
            }        
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaUsuario");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonTrocarSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTrocarSenhaActionPerformed
        
        if (jButtonTrocarSenha.getText().equalsIgnoreCase("Reinicar")){
            
            jLabelSenha.setText("Senha Inicial");
            jPasswordFieldSenha.setEditable(true);
            jPasswordFieldSenha.setEchoChar((char)0);
            jPasswordFieldSenha.setText(null);
            jButtonTrocarSenha.setVisible(false);
            
            usuario.setSituacao(Usuario.TROCANDO_SENHA);
            
            if (podeMudarSituacao){
                
                jComboBoxSituacao.removeItem(Usuario.LIBERADO);
                
                boolean achou = false;
                
                for (int x = 0; x < jComboBoxSituacao.getItemCount(); x++){
                    
                    if (jComboBoxSituacao.getItemAt(x) == Usuario.TROCANDO_SENHA){
                        achou = true;
                        break;
                    }
                }
                
                if (!achou){
                    jComboBoxSituacao.addItem(Usuario.TROCANDO_SENHA);
                }
            }
            
            jComboBoxSituacao.setSelectedItem(Usuario.TROCANDO_SENHA);
            
            senhaInicial = true;
            
            jPasswordFieldSenha.requestFocus();
            
            jTextFieldIdentificaoUsuario.setNextFocusableComponent(jPasswordFieldSenha);
            
        }else{
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaTrocaSenha tela = new TelaTrocaSenha(TelaUsuario.this,usuario);
                }   
            });
        }
    }//GEN-LAST:event_jButtonTrocarSenhaActionPerformed

    private void jPasswordFieldSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordFieldSenhaFocusGained
        
        if (jPasswordFieldSenha.isEnabled() && jPasswordFieldSenha.isEditable()){
            HeuChess.verificaTeclaCapsLock(jPasswordFieldSenha);
        }
    }//GEN-LAST:event_jPasswordFieldSenhaFocusGained
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSexo;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonTrocarSenha;
    private javax.swing.JComboBox jComboBoxFuncao;
    private javax.swing.JComboBox jComboBoxSituacao;
    private javax.swing.JFormattedTextField jFormattedTextFieldDataNascimento;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelIdentificacao;
    private javax.swing.JLabel jLabelSenha;
    private javax.swing.JLabel jLabelSituacao;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JPanel jPanelDadosAcessoSistema;
    private javax.swing.JPanel jPanelDadosPessoais;
    private javax.swing.JPasswordField jPasswordFieldSenha;
    private javax.swing.JRadioButton jRadioButtonSexoFeminino;
    private javax.swing.JRadioButton jRadioButtonSexoMasculino;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldIdentificaoUsuario;
    private javax.swing.JTextField jTextFieldNome;
    // End of variables declaration//GEN-END:variables
}
