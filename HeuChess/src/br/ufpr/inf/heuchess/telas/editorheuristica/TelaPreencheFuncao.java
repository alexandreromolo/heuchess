package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.CardLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 28 de Junho de 2006, 14:55
 */
public class TelaPreencheFuncao extends javax.swing.JFrame implements AcessoTelaRegiao, AcessoTelaPreencheFuncao, AcessoTelaEscolheFuncao {
    
    private TelaHeuristica telaHeuristica;
    
    private AcessoTelaPreencheFuncao acessoTelaPreencheFuncao;
    private FuncaoPreenchida         funcaoPreenchida;
    private FuncaoPreenchida         funcaoPreenchidaOriginal;
    
    private CardLayout cardPrincipal;
    private CardLayout cardParametros;
    private int        passoAtual;
    private int        totalPassos;
    
    private Object  elemento;
    private boolean nova;
    
    /**
     * Construtor chamado quando não existe nada preenchido
     */
    public TelaPreencheFuncao(TelaHeuristica telaHeuristica, AcessoTelaPreencheFuncao acessoTelaPreencheFuncao, Funcao funcao, Object elem) {
        
        this.telaHeuristica           = telaHeuristica;
        this.acessoTelaPreencheFuncao = acessoTelaPreencheFuncao;
        this.elemento                 = elem;
        
        nova = true;
        
        funcaoPreenchida = new FuncaoPreenchida(telaHeuristica.heuristica,funcao);
        
        if (funcaoPreenchida.totalParametros() == 0){
            
            /////////////////////////////////
            // Não necessita de parâmetros //
            /////////////////////////////////
            
            dispose();
            acessoTelaPreencheFuncao.fechandoTelaPreencheFuncao(funcaoPreenchida, elemento);
            return;
        }
        
        try{
            tentaCompletarAutomaticamenteParametro();
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaPreencheFuncao.getFrame(),"Erro ao tentar completar parâmetro da Função!\n" + e.getLocalizedMessage());
            dispose();
            return;
        }
        
        inicializaInterface();
    }       
    
    /**
     * Construtor chamado quando já existe uma Funcao Preenchida
     */
    public TelaPreencheFuncao(TelaHeuristica telaHeuristica, AcessoTelaPreencheFuncao acessoTelaPreencheFuncao, FuncaoPreenchida funcaoOriginal) {
        
        this.telaHeuristica           = telaHeuristica;
        this.acessoTelaPreencheFuncao = acessoTelaPreencheFuncao;
        
        try{
            funcaoPreenchidaOriginal = funcaoOriginal;
            funcaoPreenchida         = funcaoOriginal.geraClone();
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(acessoTelaPreencheFuncao.getFrame(),"Erro ao gerar clone de função preenchida!\n" + e.getMessage());
            dispose();
            return;
        }
        
        inicializaInterface();
    }   
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaPreencheFuncao;
    }
    
    private void tentaCompletarAutomaticamenteParametro() throws Exception {
        
        if (elemento != null){            
            
            if (elemento instanceof TipoPeca){
                
                ParametroPreenchido parametro = funcaoPreenchida.localizaParametroPorTipo(DHJOG.TipoDado.TIPO_PECAS);
                    
                if (parametro != null){                    
                    TipoPeca[] tipos = {(TipoPeca) elemento};
                    parametro.setValor(tipos);
                    elemento = null;
                }
            
            }else
                if (elemento instanceof Regiao){
                    
                    ParametroPreenchido parametro = funcaoPreenchida.localizaParametroPorTipo(DHJOG.TipoDado.CASAS);
                    
                    if (parametro != null){
                        parametro.setValor(elemento);
                        elemento = null;
                    }
                    
                }else
                    if (elemento instanceof String){                
                    
                        String constante = (String) elemento;
                
                        if (constante.equalsIgnoreCase(DHJOG.TODO_TABULEIRO)){
                        
                            ParametroPreenchido parametro = funcaoPreenchida.localizaParametroPorTipo(DHJOG.TipoDado.CASAS);
                        
                            if (parametro != null){
                                parametro.setValor(constante);
                                elemento = null;
                            }
                            
                        }else{
                            throw new IllegalArgumentException("PreencheFuncao. Texto não suportado para arrastar e soltar [" + elemento.toString() + "]");
                        }
                    }else{
                        throw new IllegalArgumentException("PreencheFuncao. Tipo de Objeto não suportado para arrastar e soltar [" + elemento.getClass().getName() + "]");
                    }
        }
    }
       
    private void inicializaInterface(){
        
        initComponents();
        
        cardPrincipal  = (CardLayout) jPanelPrincipal.getLayout();
        cardParametros = (CardLayout) jPanelEntadaDados.getLayout();
                
        cardPrincipal.show(jPanelPrincipal,"DadosFuncao");
        
        jButtonAnterior.setVisible(false);
        
        passoAtual  = 1;
        totalPassos = (funcaoPreenchida.totalParametros() + 2);
        
        jLabelPassoInicial.setText("Passo 1 de " + totalPassos + " - Leia a descrição da Função que será preenchida");
        jLabelPassoFinal.setText("Passo " + totalPassos + " de " + totalPassos + " - Conferindo a Função Preenchida");
        
        jTextFieldNomeFuncao.setText(funcaoPreenchida.getFuncao().getNomeCurto());
        jTextFieldTipoRetornoFuncao.setText(funcaoPreenchida.getFuncao().getTipoRetorno().toString());
        jTextAreaDescricaoFuncao.setText(funcaoPreenchida.getFuncao().getDescricaoFuncao());
        jTextAreaDescricaoRetorno.setText(funcaoPreenchida.getFuncao().getDescricaoRetorno());
        
        setTitle("Preenchendo Função "+ funcaoPreenchida.getFuncao().getNomeCurto());
        
        ModalFrameUtil.showAsModalDontBlock(this);        
    }
    
    private void confirmarCancelar(){
        
        boolean sofreuAlteracao;
        
        if (passoAtual == 1){
            sofreuAlteracao = false;
        }else
            if (nova){
                sofreuAlteracao = true;
            }else{
            
                try{
                    salvarParametro();
                    
                    sofreuAlteracao = !funcaoPreenchidaOriginal.toString().equals(funcaoPreenchida.toString());
                    
                }catch(Exception e){
                    HeuChess.registraExcecao(e);                    
                    sofreuAlteracao = true;
                }
            }
        
        if (sofreuAlteracao){
            int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente cancelar as alterações feitas?","Confirmação Cancelamento");
            if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                return;
            }
        }
        
        dispose();        
        acessoTelaPreencheFuncao.fechandoTelaPreencheFuncao(null,null);
    }
    
    private void atualizaComboRegioes(){
        
        ArrayList<Regiao> regioes = telaHeuristica.panelEtapa.etapa.getRegioes();
        
        jComboBoxRegiao.removeAllItems();
        
        jComboBoxRegiao.addItem(DHJOG.TODO_TABULEIRO);
        
        Collections.sort(regioes);
        
        for (Regiao regiao: regioes){
            jComboBoxRegiao.addItem(regiao);
        }
        
        jComboBoxRegiao.setSelectedIndex(-1);
    }    
     
    @Override
    public void fechandoTelaRegiao(Regiao regiao){
        
        if (regiao != null){
            
            atualizaComboRegioes();
            
            jComboBoxRegiao.setSelectedItem(regiao);
            
            jButtonAbrirRegiao.setEnabled(true);
        }
    }

    @Override
    public void fechandoTelaPreencheFuncao(FuncaoPreenchida funcaoPreenchidaParametro, Object elemento) {
        
        if (funcaoPreenchidaParametro != null){
            
            ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);        
            
            if (parametro.isPreenchidaValor() == false){
                parametro.setValor(funcaoPreenchidaParametro);
            }
            
            montaPanelParametro();            
        } 
    }
    
    @Override
    public void fechandoTelaEscolheFuncao(final Funcao funcaoEscolhida){
        
        if (funcaoEscolhida != null){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheFuncao.this,funcaoEscolhida,null);         
                }
            });
        }
    }
    
    private void montaPanelParametro(){    
        
        ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);
        
        jLabelPassoParametro.setText("Passo " + passoAtual + " de " + totalPassos + " - Preenchendo parâmetro " + (passoAtual - 1));
        
        jTextAreaDescricaoParametro.setText(parametro.getDescricao());
        jTextFieldTipoParametro.setText(parametro.getTipo().toString());
        
        Object valor = parametro.getValor();
        
        if (parametro.isPreenchidaValor()){
            
            jRadioButtonPreencherValor.setSelected(true);
            
            switch(parametro.getTipo()){                
                
                case INTEIRO:
                    cardParametros.show(jPanelEntadaDados,"Inteiro");    
                    
                    if (valor instanceof Integer){                            
                        jSpinnerValorInteiro.setValue(valor);
                    }
                    break;
                    
                case REAL:
                    cardParametros.show(jPanelEntadaDados,"Real");    
                    
                    if (valor instanceof Double){                            
                        jSpinnerValorReal.setValue(valor);
                    }
                    break;
                    
                case LOGICO:
                    cardParametros.show(jPanelEntadaDados,"Logico");    
                    
                    if (valor instanceof DHJOG.VALOR_LOGICO){
                        jComboBoxValorLogico.setSelectedItem(valor);
                    }  
                    break;
                    
                case JOGADOR:
                    cardParametros.show(jPanelEntadaDados,"Jogador");    
                    
                    if (valor instanceof DHJOG.VALOR_JOGADOR){
                        jComboBoxValorJogador.setSelectedItem(valor);                    
                    }    
                    break;
                    
                case CASAS:
                    cardParametros.show(jPanelEntadaDados,"Regiao");    
                    
                    atualizaComboRegioes();
                    
                    if (valor != null){   
                        jComboBoxRegiao.setSelectedItem(valor);
                    }
                    break;
                    
                case TIPO_PECAS:
                    cardParametros.show(jPanelEntadaDados, "TipoPeca");
                    
                    if (valor instanceof TipoPeca[]) {
                        
                        TipoPeca[] tiposPecas = (TipoPeca[]) valor;
                        
                        for (TipoPeca tipoPeca : tiposPecas){
                        
                            switch(tipoPeca){
                                case PEAO:
                                    jCheckBoxTipoPeao.setSelected(true);
                                    break;
                                case TORRE:
                                    jCheckBoxTipoTorre.setSelected(true);
                                    break;
                                case CAVALO:
                                    jCheckBoxTipoCavalo.setSelected(true);
                                    break;
                                case BISPO:
                                    jCheckBoxTipoBispo.setSelected(true);
                                    break;
                                case DAMA:
                                    jCheckBoxTipoDama.setSelected(true);
                                    break;
                                case REI:
                                    jCheckBoxTipoRei.setSelected(true);
                                    break;    
                                default:
                                    throw new IllegalArgumentException("Tipo Peça DHJOG não suportada [" + tipoPeca + "]");
                            }
                        }
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo de dado não suportado [" + parametro.getTipo() + "]");
            }
            
        }else{
            jRadioButtonPreencherFuncao.setSelected(true);
            
            cardParametros.show(jPanelEntadaDados,"Funcao");
            
            if (valor instanceof FuncaoPreenchida){
                
                jTextAreaFuncaoPreenchidaParametro.setText(((FuncaoPreenchida)valor).toString());
                
                if (((FuncaoPreenchida)valor).totalParametros() != 0){
                    jButtonAlterarParametros.setEnabled(true);
                }else{
                    jButtonAlterarParametros.setEnabled(false);
                }
                
                jButtonEscolherFuncao.setText("Trocar Função");
                jButtonDetalhesFuncaoParametro.setEnabled(true);
            }else{
                jTextAreaFuncaoPreenchidaParametro.setText(null);
                jButtonAlterarParametros.setEnabled(false);
                jButtonEscolherFuncao.setText("Escolher Função");
                jButtonDetalhesFuncaoParametro.setEnabled(false);
            }
        }
    }       
      
    private void salvarParametro() throws Exception {    
        
        if (passoAtual > 1 && passoAtual < totalPassos){
            
            ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);
            
            if (parametro.isPreenchidaValor()){
                
                switch(parametro.getTipo()){
                    
                    case INTEIRO:
                        parametro.setValor((Integer) jSpinnerValorInteiro.getValue());
                        break;
                        
                    case REAL:
                        parametro.setValor((Double) jSpinnerValorReal.getValue());
                        break;
                    
                    case LOGICO:
                        parametro.setValor((DHJOG.VALOR_LOGICO) jComboBoxValorLogico.getSelectedItem());
                        break;
                        
                    case JOGADOR:
                        parametro.setValor((DHJOG.VALOR_JOGADOR) jComboBoxValorJogador.getSelectedItem());            
                        break;
                        
                    case CASAS:
                        if (jComboBoxRegiao.getSelectedIndex() != -1){                                            
                            parametro.setValor(jComboBoxRegiao.getSelectedItem());                    
                        }else{
                            throw new IllegalArgumentException("Nenhuma Região selecionada!");
                        }
                        break;
                        
                    case TIPO_PECAS:
                        ArrayList<TipoPeca> tiposPecas = new ArrayList();
                    
                        if (jCheckBoxTipoPeao.isSelected()){
                            tiposPecas.add(TipoPeca.PEAO);
                        }
                        if (jCheckBoxTipoTorre.isSelected()){
                            tiposPecas.add(TipoPeca.TORRE);
                        }
                        if (jCheckBoxTipoCavalo.isSelected()){
                            tiposPecas.add(TipoPeca.CAVALO);
                        }
                        if (jCheckBoxTipoBispo.isSelected()){
                            tiposPecas.add(TipoPeca.BISPO);
                        }
                        if (jCheckBoxTipoDama.isSelected()){
                            tiposPecas.add(TipoPeca.DAMA);
                        }
                        if (jCheckBoxTipoRei.isSelected()){
                            tiposPecas.add(TipoPeca.REI);
                        }
                        if (tiposPecas.isEmpty()){
                            throw new IllegalArgumentException("Nenhum Tipo de Peça selecionado!");
                        }
                        parametro.setValor(tiposPecas.toArray(new TipoPeca[tiposPecas.size()]));
                        break;    
                    
                    default:
                        throw new IllegalArgumentException("Tipo de dado não suportado [" + parametro.getTipo() + "]");
                }                
            }else{
                ////////////////////////////////////////////////////////////////
                // Função Preenchida - Já é salva quando se preenche a função //
                ////////////////////////////////////////////////////////////////                
            }
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
        jPanelDadosFuncao = new javax.swing.JPanel();
        jLabelPassoInicial = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldNomeFuncao = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescricaoFuncao = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldTipoRetornoFuncao = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaDescricaoRetorno = new javax.swing.JTextArea();
        jButtonDetalhesFuncaoPreenchendo = new javax.swing.JButton();
        jPanelParametro = new javax.swing.JPanel();
        jLabelPassoParametro = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldTipoParametro = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDescricaoParametro = new javax.swing.JTextArea();
        jPanelOpcoesPreenchimento = new javax.swing.JPanel();
        jRadioButtonPreencherValor = new javax.swing.JRadioButton();
        jRadioButtonPreencherFuncao = new javax.swing.JRadioButton();
        jPanelEntadaDados = new javax.swing.JPanel();
        jPanelLogico = new javax.swing.JPanel();
        jPanelCentraliza = new javax.swing.JPanel();
        jComboBoxValorLogico = new javax.swing.JComboBox();
        jPanelJogador = new javax.swing.JPanel();
        jPanelCentraliza1 = new javax.swing.JPanel();
        jComboBoxValorJogador = new javax.swing.JComboBox();
        jPanelInteiro = new javax.swing.JPanel();
        jPanelCentraliza2 = new javax.swing.JPanel();
        jSpinnerValorInteiro = new JSpinner(new SpinnerNumberModel(1, -10000, 10000, 1)); ;
        jLabel8 = new javax.swing.JLabel();
        jPanelReal = new javax.swing.JPanel();
        jPanelCentraliza3 = new javax.swing.JPanel();
        jSpinnerValorReal = new JSpinner(new SpinnerNumberModel(1.0, -10000.0, 10000.0, 0.1)); ;
        jLabel9 = new javax.swing.JLabel();
        jPanelTipoPeca = new javax.swing.JPanel();
        jPanelCentraliza4 = new javax.swing.JPanel();
        jLabelTipoPeao = new javax.swing.JLabel();
        jLabelTipoTorre = new javax.swing.JLabel();
        jLabelTipoCavalo = new javax.swing.JLabel();
        jLabelTipoBispo = new javax.swing.JLabel();
        jLabelTipoDama = new javax.swing.JLabel();
        jLabelTipoRei = new javax.swing.JLabel();
        jCheckBoxTipoPeao = new javax.swing.JCheckBox();
        jCheckBoxTipoCavalo = new javax.swing.JCheckBox();
        jCheckBoxTipoTorre = new javax.swing.JCheckBox();
        jCheckBoxTipoBispo = new javax.swing.JCheckBox();
        jCheckBoxTipoDama = new javax.swing.JCheckBox();
        jCheckBoxTipoRei = new javax.swing.JCheckBox();
        jPanelRegiao = new javax.swing.JPanel();
        jPanelCentraliza5 = new javax.swing.JPanel();
        jComboBoxRegiao = new javax.swing.JComboBox();
        jButtonNovaRegiao = new javax.swing.JButton();
        jButtonAbrirRegiao = new javax.swing.JButton();
        jPanelFuncao = new javax.swing.JPanel();
        jButtonEscolherFuncao = new javax.swing.JButton();
        jButtonAlterarParametros = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchidaParametro = new javax.swing.JTextArea();
        jButtonDetalhesFuncaoParametro = new javax.swing.JButton();
        jPanelFinaliza = new javax.swing.JPanel();
        jLabelPassoFinal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchida = new javax.swing.JTextArea();
        jButtonCancelar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Preenchendo Função");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_funcao.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPrincipal.setLayout(new java.awt.CardLayout());

        jLabelPassoInicial.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoInicial.setText("Passo 1 de 1 - Leia a descrição da Função que será preenchida");

        jLabel3.setText("Nome");

        jTextFieldNomeFuncao.setEditable(false);

        jLabel4.setText("Descrição");

        jTextAreaDescricaoFuncao.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoFuncao.setColumns(20);
        jTextAreaDescricaoFuncao.setEditable(false);
        jTextAreaDescricaoFuncao.setLineWrap(true);
        jTextAreaDescricaoFuncao.setRows(5);
        jTextAreaDescricaoFuncao.setWrapStyleWord(true);
        jTextAreaDescricaoFuncao.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jTextAreaDescricaoFuncao);

        jLabel5.setText("Tipo do Retorno");

        jTextFieldTipoRetornoFuncao.setEditable(false);

        jLabel12.setText("Descrição do Retorno");

        jTextAreaDescricaoRetorno.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoRetorno.setColumns(20);
        jTextAreaDescricaoRetorno.setEditable(false);
        jTextAreaDescricaoRetorno.setLineWrap(true);
        jTextAreaDescricaoRetorno.setRows(5);
        jTextAreaDescricaoRetorno.setWrapStyleWord(true);
        jTextAreaDescricaoRetorno.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane2.setViewportView(jTextAreaDescricaoRetorno);

        jButtonDetalhesFuncaoPreenchendo.setMnemonic('d');
        jButtonDetalhesFuncaoPreenchendo.setText("Detalhes da Função");
        jButtonDetalhesFuncaoPreenchendo.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncaoPreenchendo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoPreenchendoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDadosFuncaoLayout = new javax.swing.GroupLayout(jPanelDadosFuncao);
        jPanelDadosFuncao.setLayout(jPanelDadosFuncaoLayout);
        jPanelDadosFuncaoLayout.setHorizontalGroup(
            jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosFuncaoLayout.createSequentialGroup()
                        .addComponent(jLabelPassoInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDetalhesFuncaoPreenchendo))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosFuncaoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNomeFuncao, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(jPanelDadosFuncaoLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTipoRetornoFuncao, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelDadosFuncaoLayout.createSequentialGroup()
                        .addGroup(jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelDadosFuncaoLayout.setVerticalGroup(
            jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPassoInicial)
                    .addComponent(jButtonDetalhesFuncaoPreenchendo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldNomeFuncao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDadosFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTipoRetornoFuncao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelDadosFuncao, "DadosFuncao");

        jLabelPassoParametro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoParametro.setText("Passo 1 de 1 - Preenchendo parâmetro");

        jLabel1.setText("Descrição");

        jLabel2.setText("Tipo");

        jTextFieldTipoParametro.setEditable(false);

        jTextAreaDescricaoParametro.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoParametro.setColumns(20);
        jTextAreaDescricaoParametro.setEditable(false);
        jTextAreaDescricaoParametro.setLineWrap(true);
        jTextAreaDescricaoParametro.setRows(5);
        jTextAreaDescricaoParametro.setWrapStyleWord(true);
        jTextAreaDescricaoParametro.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane3.setViewportView(jTextAreaDescricaoParametro);

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

        jComboBoxValorLogico.addItem(DHJOG.VALOR_LOGICO.VERDADEIRO);
        jComboBoxValorLogico.addItem(DHJOG.VALOR_LOGICO.FALSO);
        jComboBoxValorLogico.setToolTipText("Escolha o Valor Lógico");
        jComboBoxValorLogico.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelCentralizaLayout = new javax.swing.GroupLayout(jPanelCentraliza);
        jPanelCentraliza.setLayout(jPanelCentralizaLayout);
        jPanelCentralizaLayout.setHorizontalGroup(
            jPanelCentralizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentralizaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxValorLogico, 0, 207, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCentralizaLayout.setVerticalGroup(
            jPanelCentralizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jComboBoxValorLogico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanelLogico.add(jPanelCentraliza);

        jPanelEntadaDados.add(jPanelLogico, "Logico");

        jComboBoxValorJogador.addItem(DHJOG.VALOR_JOGADOR.EU);
        jComboBoxValorJogador.addItem(DHJOG.VALOR_JOGADOR.OPONENTE);
        jComboBoxValorJogador.setToolTipText("Escolha o Jogador");
        jComboBoxValorJogador.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelCentraliza1Layout = new javax.swing.GroupLayout(jPanelCentraliza1);
        jPanelCentraliza1.setLayout(jPanelCentraliza1Layout);
        jPanelCentraliza1Layout.setHorizontalGroup(
            jPanelCentraliza1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentraliza1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxValorJogador, 0, 207, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCentraliza1Layout.setVerticalGroup(
            jPanelCentraliza1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jComboBoxValorJogador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanelJogador.add(jPanelCentraliza1);

        jPanelEntadaDados.add(jPanelJogador, "Jogador");

        jPanelCentraliza2.setPreferredSize(new java.awt.Dimension(260, 67));

        jSpinnerValorInteiro.setToolTipText("Entre com um Valor Inteiro");
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorInteiro, null);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Valores válidos de -10.000 a 10.000");

        javax.swing.GroupLayout jPanelCentraliza2Layout = new javax.swing.GroupLayout(jPanelCentraliza2);
        jPanelCentraliza2.setLayout(jPanelCentraliza2Layout);
        jPanelCentraliza2Layout.setHorizontalGroup(
            jPanelCentraliza2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza2Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jSpinnerValorInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelCentraliza2Layout.setVerticalGroup(
            jPanelCentraliza2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jSpinnerValorInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel8))
        );

        jPanelInteiro.add(jPanelCentraliza2);

        jPanelEntadaDados.add(jPanelInteiro, "Inteiro");

        jPanelCentraliza3.setPreferredSize(new java.awt.Dimension(260, 67));

        jSpinnerValorReal.setToolTipText("Entre com um Valor Real");
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorReal, "#,##0.0");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Valores válidos de -10.000,0 a 10.000,0");

        javax.swing.GroupLayout jPanelCentraliza3Layout = new javax.swing.GroupLayout(jPanelCentraliza3);
        jPanelCentraliza3.setLayout(jPanelCentraliza3Layout);
        jPanelCentraliza3Layout.setHorizontalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelCentraliza3Layout.setVerticalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel9))
        );

        jPanelReal.add(jPanelCentraliza3);

        jPanelEntadaDados.add(jPanelReal, "Real");

        jPanelTipoPeca.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        jLabelTipoPeao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/peao.png"))); // NOI18N
        jLabelTipoPeao.setText("Peão");
        jLabelTipoPeao.setEnabled(false);

        jLabelTipoTorre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/torre.png"))); // NOI18N
        jLabelTipoTorre.setText("Torre");
        jLabelTipoTorre.setEnabled(false);

        jLabelTipoCavalo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/cavalo.png"))); // NOI18N
        jLabelTipoCavalo.setText("Cavalo");
        jLabelTipoCavalo.setEnabled(false);

        jLabelTipoBispo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/bispo.png"))); // NOI18N
        jLabelTipoBispo.setText("Bispo");
        jLabelTipoBispo.setEnabled(false);

        jLabelTipoDama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/dama.png"))); // NOI18N
        jLabelTipoDama.setText("Dama");
        jLabelTipoDama.setEnabled(false);

        jLabelTipoRei.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/rei.png"))); // NOI18N
        jLabelTipoRei.setText("Rei");
        jLabelTipoRei.setEnabled(false);

        jCheckBoxTipoPeao.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoPeao.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoPeao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoPeaoItemStateChanged(evt);
            }
        });

        jCheckBoxTipoCavalo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoCavalo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoCavalo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoCavaloItemStateChanged(evt);
            }
        });

        jCheckBoxTipoTorre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoTorre.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoTorre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoTorreItemStateChanged(evt);
            }
        });

        jCheckBoxTipoBispo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoBispo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoBispo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoBispoItemStateChanged(evt);
            }
        });

        jCheckBoxTipoDama.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoDama.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoDama.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoDamaItemStateChanged(evt);
            }
        });

        jCheckBoxTipoRei.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoRei.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoRei.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoReiItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentraliza4Layout = new javax.swing.GroupLayout(jPanelCentraliza4);
        jPanelCentraliza4.setLayout(jPanelCentraliza4Layout);
        jPanelCentraliza4Layout.setHorizontalGroup(
            jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTipoPeao)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jCheckBoxTipoPeao)))
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoTorre))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jCheckBoxTipoTorre)))
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoCavalo))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBoxTipoCavalo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addComponent(jLabelTipoBispo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoDama)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelTipoRei)
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jCheckBoxTipoBispo)
                        .addGap(63, 63, 63)
                        .addComponent(jCheckBoxTipoDama)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jCheckBoxTipoRei)
                        .addGap(54, 54, 54))))
        );
        jPanelCentraliza4Layout.setVerticalGroup(
            jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelTipoCavalo)
                    .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addComponent(jLabelTipoTorre))
                        .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                            .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBoxTipoPeao)
                                    .addComponent(jCheckBoxTipoTorre)
                                    .addComponent(jCheckBoxTipoCavalo))
                                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBoxTipoBispo)
                                    .addComponent(jCheckBoxTipoDama)
                                    .addComponent(jCheckBoxTipoRei)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelTipoPeao)
                                .addComponent(jLabelTipoBispo)
                                .addComponent(jLabelTipoDama)
                                .addComponent(jLabelTipoRei)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelTipoPeca.add(jPanelCentraliza4);

        jPanelEntadaDados.add(jPanelTipoPeca, "TipoPeca");

        jPanelRegiao.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 25));

        jComboBoxRegiao.setToolTipText("Escolha uma Região");
        jComboBoxRegiao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxRegiao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRegiaoItemStateChanged(evt);
            }
        });

        jButtonNovaRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaRegiao.setText("Nova Região");
        jButtonNovaRegiao.setToolTipText("Cria uma Nova Região");
        jButtonNovaRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaRegiaoActionPerformed(evt);
            }
        });

        jButtonAbrirRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirRegiao.setText("Abrir Região");
        jButtonAbrirRegiao.setToolTipText("Abre a Região Selecionada");
        jButtonAbrirRegiao.setEnabled(false);
        jButtonAbrirRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirRegiaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentraliza5Layout = new javax.swing.GroupLayout(jPanelCentraliza5);
        jPanelCentraliza5.setLayout(jPanelCentraliza5Layout);
        jPanelCentraliza5Layout.setHorizontalGroup(
            jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentraliza5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxRegiao, 0, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonNovaRegiao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAbrirRegiao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelCentraliza5Layout.setVerticalGroup(
            jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                .addGroup(jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonNovaRegiao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAbrirRegiao))
                    .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jComboBoxRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRegiao.add(jPanelCentraliza5);

        jPanelEntadaDados.add(jPanelRegiao, "Regiao");

        jButtonEscolherFuncao.setMnemonic('e');
        jButtonEscolherFuncao.setText("Escoher Função");
        jButtonEscolherFuncao.setToolTipText("Escolhe a Função a ser Preenchida");
        jButtonEscolherFuncao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEscolherFuncaoActionPerformed(evt);
            }
        });

        jButtonAlterarParametros.setMnemonic('l');
        jButtonAlterarParametros.setText("Alterar Parâmetros");
        jButtonAlterarParametros.setToolTipText("Altera os Parâmetros da Função");
        jButtonAlterarParametros.setEnabled(false);
        jButtonAlterarParametros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarParametrosActionPerformed(evt);
            }
        });

        jLabel6.setText("Função Preenchida");

        jTextAreaFuncaoPreenchidaParametro.setBackground(java.awt.SystemColor.control);
        jTextAreaFuncaoPreenchidaParametro.setColumns(20);
        jTextAreaFuncaoPreenchidaParametro.setEditable(false);
        jTextAreaFuncaoPreenchidaParametro.setLineWrap(true);
        jTextAreaFuncaoPreenchidaParametro.setRows(3);
        jTextAreaFuncaoPreenchidaParametro.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchidaParametro.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane5.setViewportView(jTextAreaFuncaoPreenchidaParametro);

        jButtonDetalhesFuncaoParametro.setMnemonic('d');
        jButtonDetalhesFuncaoParametro.setText("Detalhes da Função");
        jButtonDetalhesFuncaoParametro.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncaoParametro.setEnabled(false);
        jButtonDetalhesFuncaoParametro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoParametroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFuncaoLayout = new javax.swing.GroupLayout(jPanelFuncao);
        jPanelFuncao.setLayout(jPanelFuncaoLayout);
        jPanelFuncaoLayout.setHorizontalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                        .addComponent(jButtonEscolherFuncao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarParametros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDetalhesFuncaoParametro))
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAlterarParametros, jButtonDetalhesFuncaoParametro, jButtonEscolherFuncao});

        jPanelFuncaoLayout.setVerticalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherFuncao)
                    .addComponent(jButtonAlterarParametros)
                    .addComponent(jButtonDetalhesFuncaoParametro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAlterarParametros, jButtonDetalhesFuncaoParametro, jButtonEscolherFuncao});

        jPanelEntadaDados.add(jPanelFuncao, "Funcao");

        javax.swing.GroupLayout jPanelParametroLayout = new javax.swing.GroupLayout(jPanelParametro);
        jPanelParametro.setLayout(jPanelParametroLayout);
        jPanelParametroLayout.setHorizontalGroup(
            jPanelParametroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelParametroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelParametroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelParametroLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTipoParametro))
                    .addComponent(jScrollPane3)
                    .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelParametroLayout.createSequentialGroup()
                        .addGroup(jPanelParametroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelPassoParametro)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelParametroLayout.setVerticalGroup(
            jPanelParametroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelParametroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoParametro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelParametroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldTipoParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPrincipal.add(jPanelParametro, "PreencheParametros");

        jLabelPassoFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoFinal.setText("Passo 2 de 3 - Conferindo a Função Preenchida");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Após o preenchimento dos Parâmetros a chamada a Função ficou desta forma");

        jTextAreaFuncaoPreenchida.setColumns(20);
        jTextAreaFuncaoPreenchida.setEditable(false);
        jTextAreaFuncaoPreenchida.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaFuncaoPreenchida.setLineWrap(true);
        jTextAreaFuncaoPreenchida.setRows(5);
        jTextAreaFuncaoPreenchida.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchida.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane4.setViewportView(jTextAreaFuncaoPreenchida);

        javax.swing.GroupLayout jPanelFinalizaLayout = new javax.swing.GroupLayout(jPanelFinaliza);
        jPanelFinaliza.setLayout(jPanelFinalizaLayout);
        jPanelFinalizaLayout.setHorizontalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(jLabelPassoFinal)
                    .addComponent(jLabel7))
                .addContainerGap())
        );
        jPanelFinalizaLayout.setVerticalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoFinal)
                .addGap(16, 16, 16)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelar)
                    .addComponent(jButtonProximo)
                    .addComponent(jButtonAnterior)
                    .addComponent(jButtonAjuda))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        setSize(new java.awt.Dimension(601, 406));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDetalhesFuncaoPreenchendoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoPreenchendoActionPerformed
    
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaFuncao tela = new TelaFuncao(TelaPreencheFuncao.this, 
                                                 funcaoPreenchida.getFuncao(),
                                                 telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());            
            }
        });
    }//GEN-LAST:event_jButtonDetalhesFuncaoPreenchendoActionPerformed

    private void jButtonDetalhesFuncaoParametroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoParametroActionPerformed
        
        final ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);        
        
        if (parametro.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoP = (FuncaoPreenchida) parametro.getValor();    
            
            if (funcaoPreenchida != null){            
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaFuncao tela = new TelaFuncao(TelaPreencheFuncao.this, funcaoP.getFuncao(),telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());             
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonDetalhesFuncaoParametroActionPerformed

    private void jRadioButtonPreencherFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherFuncaoActionPerformed
         
         ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);
         
         parametro.setPreenchidaValor(false);
         
         montaPanelParametro(); 
    }//GEN-LAST:event_jRadioButtonPreencherFuncaoActionPerformed

    private void jRadioButtonPreencherValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherValorActionPerformed
         
         ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);
         
         parametro.setPreenchidaValor(true);
         
         montaPanelParametro(); 
    }//GEN-LAST:event_jRadioButtonPreencherValorActionPerformed

    private void jButtonAlterarParametrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlterarParametrosActionPerformed
        
        final ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);        
        
        if (parametro.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoP = (FuncaoPreenchida) parametro.getValor();    
            
            if (funcaoPreenchida != null){            
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheFuncao.this,funcaoP);
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonAlterarParametrosActionPerformed

    private void jButtonEscolherFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEscolherFuncaoActionPerformed
        
        final ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);        
        
        if (parametro.isPreenchidaValor() == false){
            
            final FuncaoPreenchida valorFuncaoPreenchida = (FuncaoPreenchida) parametro.getValor();    
            
            if (valorFuncaoPreenchida != null){                     
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheFuncao.this,parametro.getTipo(),valorFuncaoPreenchida.getFuncao());
                    }
                });
                
                
            }else{                
            
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheFuncao.this,parametro.getTipo());
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonEscolherFuncaoActionPerformed

    private void jButtonAbrirRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirRegiaoActionPerformed
        
        final int indicePosicao = jComboBoxRegiao.getSelectedIndex();
        
        if (indicePosicao > 0){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaRegiao tela = new TelaRegiao(telaHeuristica.panelEtapa,
                                                     TelaPreencheFuncao.this,
                                                     (Regiao) jComboBoxRegiao.getSelectedItem(),
                                                     false);
                }
            });
        }
    }//GEN-LAST:event_jButtonAbrirRegiaoActionPerformed
    
    private void jButtonNovaRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaRegiaoActionPerformed
    
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaRegiao tela = new TelaRegiao(telaHeuristica.panelEtapa,TelaPreencheFuncao.this);
            }
        });
    }//GEN-LAST:event_jButtonNovaRegiaoActionPerformed

    private void jComboBoxRegiaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRegiaoItemStateChanged
        
        if (jComboBoxRegiao.getSelectedIndex() > 0){
            jButtonAbrirRegiao.setEnabled(true);
        }else{
            jButtonAbrirRegiao.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxRegiaoItemStateChanged

    private void jCheckBoxTipoReiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoReiItemStateChanged
        
        if (jCheckBoxTipoRei.isSelected()){            
            jLabelTipoRei.setEnabled(true);            
        }else{
            jLabelTipoRei.setEnabled(false);
        }        
    }//GEN-LAST:event_jCheckBoxTipoReiItemStateChanged

    private void jCheckBoxTipoDamaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoDamaItemStateChanged
        
        if (jCheckBoxTipoDama.isSelected()){            
            jLabelTipoDama.setEnabled(true);            
        }else{
            jLabelTipoDama.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoDamaItemStateChanged

    private void jCheckBoxTipoBispoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoBispoItemStateChanged
        
        if (jCheckBoxTipoBispo.isSelected()){            
            jLabelTipoBispo.setEnabled(true);            
        }else{
            jLabelTipoBispo.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoBispoItemStateChanged

    private void jCheckBoxTipoTorreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoTorreItemStateChanged
        
        if (jCheckBoxTipoTorre.isSelected()){            
            jLabelTipoTorre.setEnabled(true);            
        }else{
            jLabelTipoTorre.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoTorreItemStateChanged

    private void jCheckBoxTipoCavaloItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoCavaloItemStateChanged
        
        if (jCheckBoxTipoCavalo.isSelected()){            
            jLabelTipoCavalo.setEnabled(true);            
        }else{
            jLabelTipoCavalo.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoCavaloItemStateChanged

    private void jCheckBoxTipoPeaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoPeaoItemStateChanged
        
        if (jCheckBoxTipoPeao.isSelected()){            
            jLabelTipoPeao.setEnabled(true);            
        }else{
            jLabelTipoPeao.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoPeaoItemStateChanged
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmarCancelar();
    }//GEN-LAST:event_formWindowClosing
                  
    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
        passoAtual--;
        
        if (passoAtual == 1){
            
            // Dados da Função //
            
            jButtonAnterior.setVisible(false);
            cardPrincipal.show(jPanelPrincipal,"DadosFuncao");
            
        }else{
            
            // Parametros //            
            
            if (jButtonProximo.getText().equalsIgnoreCase("Concluir")){
                jButtonProximo.setText("Próximo");
                jButtonProximo.setMnemonic('p');
            }
            
            jButtonAnterior.setVisible(true);
            cardPrincipal.show(jPanelPrincipal,"PreencheParametros");
            
            montaPanelParametro();
        }
    }//GEN-LAST:event_jButtonAnteriorActionPerformed
    
    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmarCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed
    
    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        
        try{
            salvarParametro();
        }catch(Exception e){
            //Utils.registraException(e);
            UtilsGUI.dialogoErro(this,e.getMessage());
            return;
        }
        
        if (passoAtual > 1 && passoAtual < totalPassos){
            
            // Verifica se preencheu pelo menos o parâmetro //
            
            ParametroPreenchido parametro = funcaoPreenchida.getParametroPreenchido(passoAtual - 1 - 1);
            
            if (parametro.getValor() == null){
                UtilsGUI.dialogoErro(this,"É preciso preencher o parâmetro atual para pode continuar!");
                return;
            }        
        }
        
        passoAtual++;
        
        if (jButtonProximo.getText().equalsIgnoreCase("Próximo")){                                
            
            jButtonAnterior.setVisible(true);
            
            if (passoAtual < totalPassos){
                
                // Parâmetros  //
                
                cardPrincipal.show(jPanelPrincipal,"PreencheParametros");
                montaPanelParametro();                
                
            }else{
                
                // Passo Final //
                
                jTextAreaFuncaoPreenchida.setText(funcaoPreenchida.toString());
                cardPrincipal.show(jPanelPrincipal,"Finaliza");                
                jButtonProximo.setText("Concluir");
                jButtonProximo.setMnemonic('n');
            }
        }else{                      
            
            // Concluiu o preenchimento da Função //            
                 
            dispose();
            
            if (nova){
                acessoTelaPreencheFuncao.fechandoTelaPreencheFuncao(funcaoPreenchida,elemento);
            }else
                if (!funcaoPreenchidaOriginal.toString().equals(funcaoPreenchida.toString())){
                    acessoTelaPreencheFuncao.fechandoTelaPreencheFuncao(funcaoPreenchida,elemento);
                }else{
                    acessoTelaPreencheFuncao.fechandoTelaPreencheFuncao(null,elemento);
                }
        }
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaPreencheFuncao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupOpcaoPreenchimento;
    private javax.swing.JButton jButtonAbrirRegiao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAlterarParametros;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonDetalhesFuncaoParametro;
    private javax.swing.JButton jButtonDetalhesFuncaoPreenchendo;
    private javax.swing.JButton jButtonEscolherFuncao;
    private javax.swing.JButton jButtonNovaRegiao;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JCheckBox jCheckBoxTipoBispo;
    private javax.swing.JCheckBox jCheckBoxTipoCavalo;
    private javax.swing.JCheckBox jCheckBoxTipoDama;
    private javax.swing.JCheckBox jCheckBoxTipoPeao;
    private javax.swing.JCheckBox jCheckBoxTipoRei;
    private javax.swing.JCheckBox jCheckBoxTipoTorre;
    private javax.swing.JComboBox jComboBoxRegiao;
    private javax.swing.JComboBox jComboBoxValorJogador;
    private javax.swing.JComboBox jComboBoxValorLogico;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelPassoFinal;
    private javax.swing.JLabel jLabelPassoInicial;
    private javax.swing.JLabel jLabelPassoParametro;
    private javax.swing.JLabel jLabelTipoBispo;
    private javax.swing.JLabel jLabelTipoCavalo;
    private javax.swing.JLabel jLabelTipoDama;
    private javax.swing.JLabel jLabelTipoPeao;
    private javax.swing.JLabel jLabelTipoRei;
    private javax.swing.JLabel jLabelTipoTorre;
    private javax.swing.JPanel jPanelCentraliza;
    private javax.swing.JPanel jPanelCentraliza1;
    private javax.swing.JPanel jPanelCentraliza2;
    private javax.swing.JPanel jPanelCentraliza3;
    private javax.swing.JPanel jPanelCentraliza4;
    private javax.swing.JPanel jPanelCentraliza5;
    private javax.swing.JPanel jPanelDadosFuncao;
    private javax.swing.JPanel jPanelEntadaDados;
    private javax.swing.JPanel jPanelFinaliza;
    private javax.swing.JPanel jPanelFuncao;
    private javax.swing.JPanel jPanelInteiro;
    private javax.swing.JPanel jPanelJogador;
    private javax.swing.JPanel jPanelLogico;
    private javax.swing.JPanel jPanelOpcoesPreenchimento;
    private javax.swing.JPanel jPanelParametro;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelReal;
    private javax.swing.JPanel jPanelRegiao;
    private javax.swing.JPanel jPanelTipoPeca;
    private javax.swing.JRadioButton jRadioButtonPreencherFuncao;
    private javax.swing.JRadioButton jRadioButtonPreencherValor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSpinner jSpinnerValorInteiro;
    private javax.swing.JSpinner jSpinnerValorReal;
    private javax.swing.JTextArea jTextAreaDescricaoFuncao;
    private javax.swing.JTextArea jTextAreaDescricaoParametro;
    private javax.swing.JTextArea jTextAreaDescricaoRetorno;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchida;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchidaParametro;
    private javax.swing.JTextField jTextFieldNomeFuncao;
    private javax.swing.JTextField jTextFieldTipoParametro;
    private javax.swing.JTextField jTextFieldTipoRetornoFuncao;
    // End of variables declaration//GEN-END:variables
}
