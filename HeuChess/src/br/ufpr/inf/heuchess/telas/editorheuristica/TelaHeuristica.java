package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.persistencia.HeuristicaDAO;
import br.ufpr.inf.heuchess.persistencia.UsuarioDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 21 de Julho de 2006, 14:19
 */
public class TelaHeuristica extends javax.swing.JFrame implements AcessoTelaAnotacao, AcessoTelaUsuario {
 
    public  Heuristica heuristica;    
    private Heuristica heuristicaOriginal;    
    
    ArrayList<AcaoValorPeca> acoesValorPeca;
            
    private Tipo       tipoSelecionado;
    
    public  PanelEtapa panelEtapa;
    private CardLayout card;
    
    private boolean nova;
    private boolean alteracao;
    private boolean criadaNovaBanco;
    
    private boolean iniciandoAutomaticoInvisivel = false;
            
   /**
    * Construtor chamado quando se é criada uma heuristica sem nenhuma condição associada
    */
    public TelaHeuristica(PanelEtapa panelEtapa){
        
        try {
            criacaoBasica(panelEtapa, false);
         
            ModalFrameUtil.showAsModalDontBlock(this);             
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(panelEtapa.getFrame(), "Erro ao carregar informações do Banco de Dados!");
            dispose();
        }
    }
    
    /**
     * Construtor chamado quando se quer criar uma Heurística utilizando uma função associada a um elemento
     */
    public TelaHeuristica(final PanelEtapa panelEtapa, final Funcao funcao, final Object tipo){
        
        try {
            criacaoBasica(panelEtapa, true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheCondicao tela = new TelaPreencheCondicao(TelaHeuristica.this,
                                                                        (ModelListaComponentes) jListCondicoes.getModel(),
                                                                         funcao,
                                                                         tipo);
                }
            });
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(panelEtapa.getFrame(), "Erro ao carregar informações do Banco de Dados!");
            dispose();
        }
    }
    
    /**
     * Construtor chamado quando se esta abrindo uma heurística já criada
     */
    public TelaHeuristica(PanelEtapa panelEtapa, DefaultMutableTreeNode node, boolean novaAnotacao) { 
        
        this.panelEtapa = panelEtapa;        
        
        acoesValorPeca = new ArrayList();
        
        nova = false;
        iniciandoAutomaticoInvisivel = novaAnotacao;
                
        try {
            heuristicaOriginal = (Heuristica) node.getUserObject();            
            heuristica         = heuristicaOriginal.geraClone();
            
            initComponents();       
            
            montarInterface();
        
            if (iniciandoAutomaticoInvisivel){            
                adicionaNovaAnotacao();
            }else{
                ModalFrameUtil.showAsModalDontBlock(this);             
                jTextFieldNomeHeuristica.requestFocus();
            
                Historico.registraComponenteAberto(heuristica);
                ConexaoDBHeuChess.commit();
            }        
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(panelEtapa.getFrame(), "Erro ao carregar informações do Banco de Dados!");
            dispose();           
        }
    }
   
    private void criacaoBasica(PanelEtapa panelEtapa, boolean invisivel) throws Exception {
        
        this.panelEtapa = panelEtapa;       
      
        acoesValorPeca = new ArrayList();
        
        nova = true;
        iniciandoAutomaticoInvisivel = invisivel;

        heuristica = new HeuristicaValorPeca(panelEtapa.etapa, "", HeuChess.usuario.getId());
        
        initComponents();                
        
        montarInterface();
        
        jTextFieldNomeHeuristica.requestFocus();
    }
    
    private void montarInterface() throws Exception {
        
        card = (CardLayout) jPanelAlteracoes.getLayout();          
        
        tipoSelecionado = heuristica.getTipo();
         
        if (heuristica instanceof HeuristicaValorPeca){
            
            card.show(jPanelAlteracoes,"Alterar Valor Peça");
            jRadioButtonAlterarValorPeca.setSelected(true);
            
            if (!nova){                
                ModelListaComponentes model = (ModelListaComponentes) jListAcoesValorPeca.getModel();
                        
                for (AcaoValorPeca acaoValorPeca : ((HeuristicaValorPeca) heuristica).getAcoesValorPeca()){
                    model.add(model.size(),acaoValorPeca);
                }
            }    
        }else
            if (heuristica instanceof HeuristicaValorTabuleiro){
                
                card.show(jPanelAlteracoes,"Alterar Valor Tabuleiro");
                jRadioButtonAlterarValorTabuleiro.setSelected(true);
                
                HeuristicaValorTabuleiro heuristicaValorTabuleiro = ((HeuristicaValorTabuleiro) heuristica);
                
                jComboBoxOperacaoValorTabuleiro.setSelectedItem(heuristicaValorTabuleiro.getOperadorMatematico());
                
                jSpinnerValorTabuleiro.setValue(heuristicaValorTabuleiro.getValorIncremento());
                
            }else
                if (heuristica instanceof HeuristicaTransicaoEtapa){
                    
                    card.show(jPanelAlteracoes,"Ir para Outra Etapa");
                    jRadioButtonIrParaOutraEtapa.setSelected(true);
                    
                    jComboBoxProximaEtapa.setSelectedItem(((HeuristicaTransicaoEtapa) heuristica).getProximaEtapa());
                    
                }else{
                    throw new IllegalArgumentException("Tipo inválido de Heurística [" + heuristica.getTipo()+ "]");
                }       
         
        if (panelEtapa.editor.conjuntoHeuristico.getEtapas().size() <= 1){
            jRadioButtonIrParaOutraEtapa.setVisible(false);            
        }else{
            jRadioButtonIrParaOutraEtapa.setVisible(true);            
        }
        
        jTextFieldNomeHeuristica.setText(heuristica.getNome());
        jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(heuristica.getIdAutor())));
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(heuristica.getDataCriacao()));
           
        setTitle("Heurística - " + jTextFieldNomeHeuristica.getText());        
        
        alteracao = false;
        
        atualizaInterfaceNivelComplexidade();        
        atualizaVersaoDataUltimaModificacao();
        Anotacoes.atualizaQuantidadeAnotacoes(this);
                
        if (!panelEtapa.editor.podeAlterar()){
            jTextFieldNomeHeuristica.setEditable(false);
            
            jButtonExcluirAnotacao.setVisible(false);
            
            jButtonNovaCondicao.setVisible(false);
            jButtonExcluirCondicao.setVisible(false);
            
            if (heuristica instanceof HeuristicaValorTabuleiro){
                jRadioButtonAlterarValorTabuleiro.setEnabled(false);
                jRadioButtonAlterarValorPeca.setVisible(false);
                jRadioButtonIrParaOutraEtapa.setVisible(false);
                
                jComboBoxOperacaoValorTabuleiro.setEnabled(false);
                jSpinnerValorTabuleiro.setEnabled(false);
            }else
                if (heuristica instanceof HeuristicaValorPeca){
                    jRadioButtonAlterarValorPeca.setEnabled(false);        
                    jRadioButtonAlterarValorTabuleiro.setVisible(false);
                    jRadioButtonIrParaOutraEtapa.setVisible(false);
                    
                    jButtonNovaAcaoValorPeca.setVisible(false);
                    jButtonExcluirAcaoValorPeca.setVisible(false);
                }else{
                    jRadioButtonIrParaOutraEtapa.setEnabled(false);
                    jRadioButtonAlterarValorPeca.setVisible(false);
                    jRadioButtonAlterarValorTabuleiro.setVisible(false);
                    
                    jComboBoxProximaEtapa.setEnabled(false);
                    jButtonNovaEtapa.setVisible(false);
                }
            
            jButtonConfirmar.setVisible(false);
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
        
        if (!panelEtapa.editor.podeAnotar()){
            jButtonNovaAnotacao.setVisible(false);
            
            if (heuristica.getAnotacoes().isEmpty()){
                jTabbedPanePrincipal.remove(jPanelAnotacoes);
            }
        }
    }
    
    @Override
    public Frame getFrame(){
        
        if (iniciandoAutomaticoInvisivel) {
            return panelEtapa.getFrame();
        } else {
            return this;
        }
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        
        if (iniciandoAutomaticoInvisivel) {
            return panelEtapa.getModalOwner();
        } else {
            return panelEtapa.editor;
        }
    }   
    
    private void confirmaCancelar(){
        
        if (panelEtapa.editor.podeAlterar() || panelEtapa.editor.podeAnotar()) {

            if (alteracao || criadaNovaBanco) {
                
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?",
                                                                 "Confirmação Cancelamento");

                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }

                if (criadaNovaBanco) {
                    try {
                        setCursor(new Cursor(Cursor.WAIT_CURSOR));

                        HeuristicaDAO.apaga(heuristica, panelEtapa.etapa);
                        ConexaoDBHeuChess.commit();

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                        dispose();        
                        panelEtapa.fechandoTelaHeuristica();                  
                        return;
                        
                    } catch (Exception e) {
                        HeuChess.desfazTransacao(e);

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Erro ao tentar desfazer alterações no Banco de Dados\nOperação Cancelada!");
                    }
                }
            }
        }
        
        verificaAlteracoesAnotacoesAoSair();
        
        dispose();        
        panelEtapa.fechandoTelaHeuristica();  
    }    
    
    private void verificaAlteracoesAnotacoesAoSair() {
        
        if (!nova && Anotacoes.anotacoesDiferentes(heuristica, heuristicaOriginal)) {

            /////////////////////////////////                    
            // Apenas alterou as Anotações //
            /////////////////////////////////

            panelEtapa.heuristicasTreeModel.remove(heuristicaOriginal);

            panelEtapa.heuristicasTreeModel.add(heuristica);
            panelEtapa.heuristicasTreeModel.getJTree().repaint();

            if (heuristica instanceof HeuristicaTransicaoEtapa) {
                panelEtapa.editor.editorMudancaEtapas.removeHeuristicaTransicao(heuristicaOriginal);
                panelEtapa.editor.editorMudancaEtapas.adicionaHeuristicaTransicao(panelEtapa.etapa, (HeuristicaTransicaoEtapa) heuristica);
            }

            panelEtapa.heuristicasTreeModel.selecionaHeuristica(heuristica);
        }
    }
    
    private void atualizaComboEtapas(){
        
        jComboBoxProximaEtapa.removeAllItems();
        
        for (Etapa etapa : panelEtapa.editor.conjuntoHeuristico.getEtapas()){
            
            if (etapa != panelEtapa.etapa){
                jComboBoxProximaEtapa.addItem(etapa);
            }
        }
        
        if (jComboBoxProximaEtapa.getItemCount() > 0){
            jComboBoxProximaEtapa.setSelectedIndex(0);
        }
    }

    private void atualizaInterfaceNivelComplexidade(){
        
        Tipo complexidade = panelEtapa.editor.conjuntoHeuristico.getTipo();
        
        if (complexidade == ConjuntoHeuristico.NIVEL_1_INICIANTE ||
            complexidade == ConjuntoHeuristico.NIVEL_2_BASICO){
            
            jRadioButtonAlterarValorTabuleiro.setVisible(false);
            jRadioButtonIrParaOutraEtapa.setVisible(false);
            jTabbedPanePrincipal.remove(jPanelCodigoGerado);
            
        }else
            if (complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO){
                
                jRadioButtonAlterarValorTabuleiro.setVisible(true);
                jRadioButtonIrParaOutraEtapa.setVisible(false);
                jTabbedPanePrincipal.remove(jPanelCodigoGerado);
                
            }else{
                
                jRadioButtonAlterarValorTabuleiro.setVisible(true);
                jRadioButtonIrParaOutraEtapa.setVisible(true);
                jTabbedPanePrincipal.remove(jPanelCodigoGerado);
                jTabbedPanePrincipal.add("Código Gerado",jPanelCodigoGerado);     
            }
    }
    
    private String montaTextoCondicoesDB(){        
        
        ArrayList<CondicaoHeuristica> condicoes = heuristica.getCondicoes();
        
        if (condicoes.size() > 0){
        
            StringBuilder builder = new StringBuilder();
            
            for (int x = 0; x < condicoes.size()-1; x++){
                builder.append(condicoes.get(x).toDB(true));
                builder.append('\n');
            }
            
            CondicaoHeuristica ultimaCondicao = condicoes.get(condicoes.size()-1);
            
            builder.append(ultimaCondicao.toDB(false));
            
            return builder.toString();
        }else{
            return null;
        }
    }
    
    private String montaTextoAcoesDB(){
        
        StringBuilder builder = new StringBuilder();
        
        if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_PECA){
            
            if (acoesValorPeca.size() > 0){
                
                for (int x = 0; x < acoesValorPeca.size()-1; x++){
                    builder.append(acoesValorPeca.get(x).toDB());
                    builder.append('\n');
                }    
                
                builder.append(acoesValorPeca.get(acoesValorPeca.size()-1).toDB());
            }
        }else
            if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_TABULEIRO){
                
                builder.append(jComboBoxOperacaoValorTabuleiro.getSelectedItem());
                builder.append(' ');
                builder.append(((Double) jSpinnerValorTabuleiro.getValue()).doubleValue());
                
            }else
                if (tipoSelecionado == Heuristica.HEURISTICA_TRANSICAO_ETAPA){
                    
                    if (jComboBoxProximaEtapa.getSelectedIndex() != -1){
                        builder.append(jComboBoxProximaEtapa.getSelectedItem());
                    }
                }
              
        return builder.toString();       
    }
    
    private String montaTextoHeuristicaDB(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(montaTextoCondicoesDB());
        builder.append('\n');
        builder.append(DHJOG.TXT_ENTAO);
        builder.append('\n');
        builder.append(montaTextoAcoesDB());
        
        return builder.toString();  
    }
    
    private boolean salvarEntrada(String complemento){
       
        String nome     = jTextFieldNomeHeuristica.getText();
        String condicao = montaTextoCondicoesDB();
        String acoes    = montaTextoAcoesDB();
        
        if (nome == null || nome.trim().length() == 0){
            UtilsGUI.dialogoErro(this,"O Nome da Heurística não esta preenchido.\n" +
                                      "Uma Heurística precisa ter um Nome definido para poder ser salva!" + complemento);
            return false;
        }
        
        String erro = panelEtapa.editor.conjuntoHeuristico.validaNomeUnicoComponente(nome);
        
        if ((erro != null) && ((nova && !criadaNovaBanco) || 
                               (!nova && !heuristicaOriginal.getNome().equalsIgnoreCase(nome)) ||
                               (nova && criadaNovaBanco && !heuristica.getNome().equalsIgnoreCase(nome)))){ 
            
            UtilsGUI.dialogoErro(this,erro+complemento);
            if (!nova){
                jTextFieldNomeHeuristica.setText(heuristica.getNome());
            }
            jTextFieldNomeHeuristica.selectAll();
            jTextFieldNomeHeuristica.requestFocus();
            return false;
        }
        
        if (condicao == null || condicao.trim().length() == 0){
            UtilsGUI.dialogoErro(this,"A Heurística não possui nenhuma Condição.\n" +
                                      "Uma Heurística precisa ter pelo menos uma Condição definida para poder ser salva!" + complemento);
            return false;
        }        
            
        if (acoes == null || acoes.trim().length() == 0){
            UtilsGUI.dialogoErro(this,"A Heurística não possui nenhuma Ação.\n" +
                                      "Uma Heurística precisa ter pelo menos uma Ação definida para poder ser salva!" + complemento);
            return false;
        }  
                
        try{
            heuristica.setNome(UtilsString.preparaStringParaBD(jTextFieldNomeHeuristica.getText(), true, Formato.TUDO_MAIUSCULO));
            
            if (tipoSelecionado != heuristica.getTipo()){
                
                heuristica = Heuristica.recriaNovoTipo(tipoSelecionado, heuristica, montaTextoHeuristicaDB());
                
            }else{            
                heuristica.setAcoesDB(acoes);
                heuristica.setCondicaoDB(condicao);
            }
            
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(this, "Erro ao atualizar Heurística!\n" + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    public final void adicionaNovaAnotacao(){
        
       if (nova && !criadaNovaBanco){
            
            if (!salvarEntrada("\n\nÉ preciso tornar a Heurística válida antes de criar uma Anotação para ela!")){
                return;
            }
            
            try{
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                HeuristicaDAO.adiciona(panelEtapa.etapa, heuristica);
                
                ConexaoDBHeuChess.commit();
                
                criadaNovaBanco = true;
                alteracao       = false;
                jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(heuristica.getDataCriacao()));
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
            }catch(Exception e){
                HeuChess.desfazTransacao(e);                
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar criar a Heurística no Banco de Dados\nOperação Cancelada!");
                return;
            }
        }        
        
        Anotacoes.novaAnotacao(this);        
    }
    
    public void fechandoTelaNovaEtapa(Etapa etapa){
        
        if (etapa != null){
            
            atualizaComboEtapas();
            
            jComboBoxProximaEtapa.setSelectedItem(etapa);
            
            alteracao = true;
        }        
    }
    
    public void fechandoTelaPreencheCondicao(boolean sucesso){        
        
        if (sucesso){
            alteracao = true;
        }
        
        if (iniciandoAutomaticoInvisivel){
            if (sucesso){
                iniciandoAutomaticoInvisivel = false;
                ModalFrameUtil.showAsModalDontBlock(this);
            }else{
                dispose();
                panelEtapa.fechandoTelaHeuristica();
            }            
        }
    }
    
    public void fechandoTelaPreencheAcaoValorPeca(boolean sucesso){
        
        if (sucesso){
            alteracao = true;
        }
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
    public JButton getJButtonAbrirAnotacao() {
        return jButtonAbrirAnotacao;
    }

    @Override
    public JButton getJButtonExcluirAnotacao() {
        return jButtonExcluirAnotacao;
    }
    
    @Override
    public Componente getComponente() {
        return heuristica;
    }

    @Override
    public boolean podeAlterar() {
        return panelEtapa.editor.podeAlterar();
    }
    
    @Override
    public void atualizaVersaoDataUltimaModificacao() {
        jTextFieldVersao.setText(Long.toString(heuristica.getVersao()));        
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(heuristica.getDataUltimaModificacao()));
    }
    
    @Override
    public void fechandoTelaAnotacao(boolean sucesso) {
        
        if (iniciandoAutomaticoInvisivel){
            
            if (sucesso){
                iniciandoAutomaticoInvisivel = false;
                ModalFrameUtil.showAsModalDontBlock(this);
                
                try {
                    Historico.registraComponenteAberto(heuristica);
                    ConexaoDBHeuChess.commit();
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);
                    UtilsGUI.dialogoErro(panelEtapa.getFrame(), "Erro ao registrar ação de abertura de Componente no Banco de Dados!");
                }
            }else{
                dispose();
                panelEtapa.fechandoTelaHeuristica();
                return;
            }            
            
        }
        
        jTabbedPanePrincipal.setSelectedComponent(jPanelAnotacoes);        
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null) {
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

        buttonGroupTipoAlteracao = new javax.swing.ButtonGroup();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelDadosPrincipais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldNomeHeuristica = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jButtonDadosAutor = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabelDataModificacao = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jPanelLogicaHeuristica = new javax.swing.JPanel();
        jLabelSE = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanelTipoAlteracao = new javax.swing.JPanel();
        jRadioButtonAlterarValorPeca = new javax.swing.JRadioButton();
        jRadioButtonAlterarValorTabuleiro = new javax.swing.JRadioButton();
        jRadioButtonIrParaOutraEtapa = new javax.swing.JRadioButton();
        jPanelAlteracoes = new javax.swing.JPanel();
        jPanelAlterarValorPeca = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListAcoesValorPeca = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(acoesValorPeca));
        jButtonNovaAcaoValorPeca = new javax.swing.JButton();
        jButtonAbrirAcaoValorPeca = new javax.swing.JButton();
        jButtonExcluirAcaoValorPeca = new javax.swing.JButton();
        jPanelAlterarValorTabuleiro = new javax.swing.JPanel();
        jPanelCentralizador2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxOperacaoValorTabuleiro = new javax.swing.JComboBox();
        jSpinnerValorTabuleiro = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 1000.0, 0.1)); ;
        jLabel2 = new javax.swing.JLabel();
        jPanelIrParaOutraEtapa = new javax.swing.JPanel();
        jPanelCentralizar3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBoxProximaEtapa = new javax.swing.JComboBox();
        jButtonNovaEtapa = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListCondicoes = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(heuristica.getCondicoes()));
        jButtonNovaCondicao = new javax.swing.JButton();
        jButtonAbrirCondicao = new javax.swing.JButton();
        jButtonExcluirCondicao = new javax.swing.JButton();
        jPanelAnotacoes = new javax.swing.JPanel();
        jPanelBotoesAnotacao = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonNovaAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(heuristica.getAnotacoes()));
        jLabelTituloListaAnotacoes = new javax.swing.JLabel();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jPanelCodigoGerado = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaCodigoFonte = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanelBotoes = new javax.swing.JPanel();
        jButtonConfirmar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Heurística -");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_heuristica.png")).getImage());
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

        jTextFieldNomeHeuristica.setDocument(new DocumentMasked(DHJOG.CARACTERES_VALIDOS,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNomeHeuristica.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNomeHeuristicaKeyReleased(evt);
            }
        });

        jLabel4.setText("Autor");

        jTextFieldNomeAutor.setEditable(false);

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        jLabel5.setText("Versão");

        jTextFieldVersao.setEditable(false);
        jTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel6.setText("Criação");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelDataModificacao.setText("Modificação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanelLogicaHeuristica.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelSE.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelSE.setText("SE");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ENTÃO");

        buttonGroupTipoAlteracao.add(jRadioButtonAlterarValorPeca);
        jRadioButtonAlterarValorPeca.setSelected(true);
        jRadioButtonAlterarValorPeca.setText("Alterar Valor de Peça   ");
        jRadioButtonAlterarValorPeca.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonAlterarValorPeca.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonAlterarValorPeca.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAlterarValorPecaItemStateChanged(evt);
            }
        });
        jPanelTipoAlteracao.add(jRadioButtonAlterarValorPeca);

        buttonGroupTipoAlteracao.add(jRadioButtonAlterarValorTabuleiro);
        jRadioButtonAlterarValorTabuleiro.setText("Alterar Valor Total do Tabuleiro   ");
        jRadioButtonAlterarValorTabuleiro.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonAlterarValorTabuleiro.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonAlterarValorTabuleiro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAlterarValorTabuleiroItemStateChanged(evt);
            }
        });
        jPanelTipoAlteracao.add(jRadioButtonAlterarValorTabuleiro);

        buttonGroupTipoAlteracao.add(jRadioButtonIrParaOutraEtapa);
        jRadioButtonIrParaOutraEtapa.setText("Ir para Outra Etapa");
        jRadioButtonIrParaOutraEtapa.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonIrParaOutraEtapa.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonIrParaOutraEtapa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonIrParaOutraEtapaItemStateChanged(evt);
            }
        });
        jPanelTipoAlteracao.add(jRadioButtonIrParaOutraEtapa);

        jPanelAlteracoes.setLayout(new java.awt.CardLayout());

        jListAcoesValorPeca.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jListAcoesValorPeca.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        DefaultListCellRenderer render = (DefaultListCellRenderer) jListAcoesValorPeca.getCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jListAcoesValorPeca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListAcoesValorPecaMouseClicked(evt);
            }
        });
        jListAcoesValorPeca.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAcoesValorPecaValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jListAcoesValorPeca);

        jButtonNovaAcaoValorPeca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaAcaoValorPeca.setText("Adicionar");
        jButtonNovaAcaoValorPeca.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonNovaAcaoValorPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaAcaoValorPecaActionPerformed(evt);
            }
        });

        jButtonAbrirAcaoValorPeca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirAcaoValorPeca.setText("Abrir");
        jButtonAbrirAcaoValorPeca.setEnabled(false);
        jButtonAbrirAcaoValorPeca.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonAbrirAcaoValorPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirAcaoValorPecaActionPerformed(evt);
            }
        });

        jButtonExcluirAcaoValorPeca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirAcaoValorPeca.setText("Excluir");
        jButtonExcluirAcaoValorPeca.setEnabled(false);
        jButtonExcluirAcaoValorPeca.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonExcluirAcaoValorPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirAcaoValorPecaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAlterarValorPecaLayout = new javax.swing.GroupLayout(jPanelAlterarValorPeca);
        jPanelAlterarValorPeca.setLayout(jPanelAlterarValorPecaLayout);
        jPanelAlterarValorPecaLayout.setHorizontalGroup(
            jPanelAlterarValorPecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlterarValorPecaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelAlterarValorPecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonAbrirAcaoValorPeca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonNovaAcaoValorPeca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonExcluirAcaoValorPeca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelAlterarValorPecaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAbrirAcaoValorPeca, jButtonExcluirAcaoValorPeca, jButtonNovaAcaoValorPeca});

        jPanelAlterarValorPecaLayout.setVerticalGroup(
            jPanelAlterarValorPecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAlterarValorPecaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAlterarValorPecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanelAlterarValorPecaLayout.createSequentialGroup()
                        .addComponent(jButtonNovaAcaoValorPeca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAbrirAcaoValorPeca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExcluirAcaoValorPeca)
                        .addGap(0, 4, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanelAlterarValorPecaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAbrirAcaoValorPeca, jButtonExcluirAcaoValorPeca, jButtonNovaAcaoValorPeca});

        jPanelAlteracoes.add(jPanelAlterarValorPeca, "Alterar Valor Peça");

        jPanelAlterarValorTabuleiro.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText(DHJOG.TXT_TABULEIRO + " " + DHJOG.TXT_OPERADOR_ATRIBUICAO + " " + DHJOG.TXT_TABULEIRO);

        jComboBoxOperacaoValorTabuleiro.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxOperacaoValorTabuleiro.addItem(DHJOG.OperadorMatematico.MAIS);
        jComboBoxOperacaoValorTabuleiro.addItem(DHJOG.OperadorMatematico.MENOS);
        jComboBoxOperacaoValorTabuleiro.addItem(DHJOG.OperadorMatematico.MULTIPLICACAO);
        jComboBoxOperacaoValorTabuleiro.addItem(DHJOG.OperadorMatematico.DIVISAO);
        jComboBoxOperacaoValorTabuleiro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxOperacaoValorTabuleiroItemStateChanged(evt);
            }
        });

        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorTabuleiro, "#,##0.0");
        jSpinnerValorTabuleiro.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorTabuleiroStateChanged(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Valores válidos de 0 até 1.000,0");

        javax.swing.GroupLayout jPanelCentralizador2Layout = new javax.swing.GroupLayout(jPanelCentralizador2);
        jPanelCentralizador2.setLayout(jPanelCentralizador2Layout);
        jPanelCentralizador2Layout.setHorizontalGroup(
            jPanelCentralizador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizador2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxOperacaoValorTabuleiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorTabuleiro, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelCentralizador2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBoxOperacaoValorTabuleiro, jSpinnerValorTabuleiro});

        jPanelCentralizador2Layout.setVerticalGroup(
            jPanelCentralizador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizador2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelCentralizador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelCentralizador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxOperacaoValorTabuleiro)
                        .addComponent(jSpinnerValorTabuleiro))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addComponent(jLabel2))
        );

        jPanelCentralizador2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxOperacaoValorTabuleiro, jSpinnerValorTabuleiro});

        jPanelAlterarValorTabuleiro.add(jPanelCentralizador2);

        jPanelAlteracoes.add(jPanelAlterarValorTabuleiro, "Alterar Valor Tabuleiro");

        jPanelIrParaOutraEtapa.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText(DHJOG.TXT_ETAPA_ATUAL + " " + DHJOG.TXT_OPERADOR_ATRIBUICAO);

        jComboBoxProximaEtapa.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        atualizaComboEtapas();
        jComboBoxProximaEtapa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxProximaEtapaItemStateChanged(evt);
            }
        });

        jButtonNovaEtapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaEtapa.setMnemonic('n');
        jButtonNovaEtapa.setText("Nova Etapa");
        jButtonNovaEtapa.setToolTipText("Cria uma Nova etapa");
        jButtonNovaEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaEtapaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralizar3Layout = new javax.swing.GroupLayout(jPanelCentralizar3);
        jPanelCentralizar3.setLayout(jPanelCentralizar3Layout);
        jPanelCentralizar3Layout.setHorizontalGroup(
            jPanelCentralizar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizar3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxProximaEtapa, 0, 322, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNovaEtapa)
                .addContainerGap())
        );
        jPanelCentralizar3Layout.setVerticalGroup(
            jPanelCentralizar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizar3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentralizar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNovaEtapa)
                    .addComponent(jComboBoxProximaEtapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelIrParaOutraEtapa.add(jPanelCentralizar3);

        jPanelAlteracoes.add(jPanelIrParaOutraEtapa, "Ir para Outra Etapa");

        jListCondicoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jListCondicoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCondicoes.setCellRenderer(new br.ufpr.inf.heuchess.telas.editorheuristica.RenderListaCondicoes(heuristica.getCondicoes()));
        jListCondicoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListCondicoesMouseClicked(evt);
            }
        });
        jListCondicoes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListCondicoesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jListCondicoes);

        jButtonNovaCondicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaCondicao.setText("Adicionar");
        jButtonNovaCondicao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonNovaCondicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaCondicaoActionPerformed(evt);
            }
        });

        jButtonAbrirCondicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirCondicao.setText("Abrir");
        jButtonAbrirCondicao.setEnabled(false);
        jButtonAbrirCondicao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonAbrirCondicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirCondicaoActionPerformed(evt);
            }
        });

        jButtonExcluirCondicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirCondicao.setText("Excluir");
        jButtonExcluirCondicao.setEnabled(false);
        jButtonExcluirCondicao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonExcluirCondicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirCondicaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelLogicaHeuristicaLayout = new javax.swing.GroupLayout(jPanelLogicaHeuristica);
        jPanelLogicaHeuristica.setLayout(jPanelLogicaHeuristicaLayout);
        jPanelLogicaHeuristicaLayout.setHorizontalGroup(
            jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelAlteracoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelLogicaHeuristicaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLogicaHeuristicaLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonExcluirCondicao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonAbrirCondicao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonNovaCondicao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanelTipoAlteracao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelLogicaHeuristicaLayout.createSequentialGroup()
                        .addGroup(jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSE)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanelLogicaHeuristicaLayout.setVerticalGroup(
            jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLogicaHeuristicaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelSE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLogicaHeuristicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLogicaHeuristicaLayout.createSequentialGroup()
                        .addComponent(jButtonNovaCondicao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAbrirCondicao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExcluirCondicao))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTipoAlteracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelAlteracoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanelDadosPrincipaisLayout = new javax.swing.GroupLayout(jPanelDadosPrincipais);
        jPanelDadosPrincipais.setLayout(jPanelDadosPrincipaisLayout);
        jPanelDadosPrincipaisLayout.setHorizontalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(jLabelDataModificacao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jTextFieldNomeAutor)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDadosAutor))
                            .addComponent(jTextFieldNomeHeuristica))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addComponent(jPanelLogicaHeuristica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))))
        );
        jPanelDadosPrincipaisLayout.setVerticalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldNomeHeuristica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jButtonDadosAutor)
                    .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDataModificacao)
                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelLogicaHeuristica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Dados Principais", jPanelDadosPrincipais);

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
        jListAnotacoes.setCellRenderer(new br.ufpr.inf.heuchess.telas.editorheuristica.RenderListaAnotacoes());
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
        jLabelTituloListaAnotacoes.setText("Anotações Gerais Sobre esta Heurística");

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAnotacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
                    .addComponent(jPanelBotoesAnotacao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
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
        jLabel8.setText("Código Gerado desta Heurística");

        javax.swing.GroupLayout jPanelCodigoGeradoLayout = new javax.swing.GroupLayout(jPanelCodigoGerado);
        jPanelCodigoGerado.setLayout(jPanelCodigoGeradoLayout);
        jPanelCodigoGeradoLayout.setHorizontalGroup(
            jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoGeradoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
                    .addComponent(jLabel8))
                .addContainerGap())
        );
        jPanelCodigoGeradoLayout.setVerticalGroup(
            jPanelCodigoGeradoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCodigoGeradoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanePrincipal.addTab("Código Gerado", jPanelCodigoGerado);

        jButtonConfirmar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_confirmar.png"))); // NOI18N
        jButtonConfirmar.setText("Confirmar");
        jButtonConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarActionPerformed(evt);
            }
        });

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
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
                .addComponent(jButtonAjuda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonConfirmar)
                .addGap(16, 16, 16)
                .addComponent(jButtonCancelar))
        );

        jPanelBotoesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        jPanelBotoesLayout.setVerticalGroup(
            jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBotoesLayout.createSequentialGroup()
                .addGroup(jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAjuda)
                    .addGroup(jPanelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonCancelar)
                        .addComponent(jButtonConfirmar)))
                .addGap(10, 10, 10))
        );

        jPanelBotoesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonCancelar, jButtonConfirmar});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPanePrincipal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelBotoes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jTabbedPanePrincipal)
                .addGap(5, 5, 5)
                .addComponent(jPanelBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(792, 548));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNovaEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaEtapaActionPerformed
    
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 TelaNovaEtapa tela = new TelaNovaEtapa(TelaHeuristica.this);
            }
        });
    }//GEN-LAST:event_jButtonNovaEtapaActionPerformed

    private void jTextFieldNomeHeuristicaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeHeuristicaKeyReleased
        alteracao = true;
        setTitle("Heurística - " + jTextFieldNomeHeuristica.getText());
    }//GEN-LAST:event_jTextFieldNomeHeuristicaKeyReleased
    
    private void jButtonConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarActionPerformed

        if (alteracao || (nova && !criadaNovaBanco)) {

            if (!salvarEntrada("")) {
                return;
            }

            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                if (nova && !criadaNovaBanco) {

                    HeuristicaDAO.adiciona(panelEtapa.etapa, heuristica);
                    ConexaoDBHeuChess.commit();
                    
                } else {
                    
                    HeuristicaDAO.atualiza(heuristica);
                    ConexaoDBHeuChess.commit();

                    ////////////////////////////////////
                    // Remove Heurística da Interface //
                    ////////////////////////////////////
                    
                    panelEtapa.heuristicasTreeModel.remove(heuristicaOriginal);
                    
                    if (heuristica instanceof HeuristicaTransicaoEtapa) {
                        panelEtapa.editor.editorMudancaEtapas.removeHeuristicaTransicao(heuristicaOriginal);
                    }
                }

                //////////////////////////////////////
                // Adiciona Heuristica a Interface  //
                //////////////////////////////////////
                
                panelEtapa.heuristicasTreeModel.add(heuristica);
                panelEtapa.heuristicasTreeModel.getJTree().repaint();

                if (heuristica instanceof HeuristicaTransicaoEtapa) {
                    panelEtapa.editor.editorMudancaEtapas.adicionaHeuristicaTransicao(panelEtapa.etapa, (HeuristicaTransicaoEtapa) heuristica);
                }

                panelEtapa.heuristicasTreeModel.selecionaHeuristica(heuristica);

            } catch (Exception e) {
                HeuChess.desfazTransacao(e);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar a Heuristica no Banco de Dados." +
                                           "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                
                panelEtapa.editor.fechar(true);
                return;
            }
        }else
            if (nova) {
                
                ///////////////////////////////////////////////////////////////////
                // Já foi salva no banco no momento do cadastro da nova Anotação //
                ///////////////////////////////////////////////////////////////////
                
                panelEtapa.heuristicasTreeModel.add(heuristica);
                panelEtapa.heuristicasTreeModel.getJTree().repaint();

                if (heuristica instanceof HeuristicaTransicaoEtapa) {
                    panelEtapa.editor.editorMudancaEtapas.adicionaHeuristicaTransicao(panelEtapa.etapa, (HeuristicaTransicaoEtapa) heuristica);
                }

                panelEtapa.heuristicasTreeModel.selecionaHeuristica(heuristica);
                
            }else{
                verificaAlteracoesAnotacoesAoSair();
            }
                
        dispose();
        panelEtapa.fechandoTelaHeuristica();
    }//GEN-LAST:event_jButtonConfirmarActionPerformed

    private void jButtonAbrirCondicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirCondicaoActionPerformed
        
        final int posicao = jListCondicoes.getSelectedIndex();
        
        if (posicao != -1){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheCondicao tela = new TelaPreencheCondicao(TelaHeuristica.this, (ModelListaComponentes) jListCondicoes.getModel(), posicao);
                }
            });
        }
    }//GEN-LAST:event_jButtonAbrirCondicaoActionPerformed

    private void jButtonExcluirCondicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirCondicaoActionPerformed
       
        int posicao = jListCondicoes.getSelectedIndex();
        
        if (posicao != -1){
            
            int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente apagar a Condição selecionada?","Confirmação de Exclusão");
            if (resposta == JOptionPane.YES_OPTION){                
                
                ModelListaComponentes model = (ModelListaComponentes) jListCondicoes.getModel();
                model.remove(posicao);                
                
                if (HeuChess.somAtivado){
                    HeuChess.somApagar.play();
                }
                
                alteracao = true;
            }            
        }
    }//GEN-LAST:event_jButtonExcluirCondicaoActionPerformed

    private void jListCondicoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListCondicoesMouseClicked
        
        if (jListCondicoes.isEnabled()){
            
            if (evt.getClickCount() == 2){
                
                final int posicao = jListCondicoes.locationToIndex(evt.getPoint());
                
                if (posicao != -1){
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaPreencheCondicao tela = new TelaPreencheCondicao(TelaHeuristica.this,
                                                                                (ModelListaComponentes) jListCondicoes.getModel(), posicao);
                        }
                    });
                }
            }
        }
    }//GEN-LAST:event_jListCondicoesMouseClicked

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelCodigoGerado){
            
            StringBuilder builder = new StringBuilder();
            
            builder.append(DHJOG.TXT_HEURISTICA);            
            builder.append(' ');
            
            if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_PECA){
                builder.append(DHJOG.TXT_VALOR_PECA);
            }else
                if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_TABULEIRO){
                    builder.append(DHJOG.TXT_VALOR_TABULEIRO);
                }else
                    if (tipoSelecionado == Heuristica.HEURISTICA_TRANSICAO_ETAPA){
                        builder.append(DHJOG.TXT_TRANSICAO_ETAPA);
                    } 
            
            builder.append(" \"");            
            builder.append(jTextFieldNomeHeuristica.getText());            
            builder.append("\"\n   ");
            builder.append(DHJOG.TXT_SE);
            builder.append('\n');
            
            ArrayList<CondicaoHeuristica> condicoes = heuristica.getCondicoes();
        
            if (condicoes.size() > 0){
            
                for (int x = 0; x < condicoes.size()-1; x++){
                
                    builder.append("      ");
                    builder.append(condicoes.get(x).toDHJOG(true));
                    builder.append('\n');
                }
            
                CondicaoHeuristica ultimaCondicao = condicoes.get(condicoes.size()-1);
                
                builder.append("      ");            
                builder.append(ultimaCondicao.toDHJOG(false));
            }
            
            builder.append("\n   ");
            builder.append(DHJOG.TXT_ENTAO);
            builder.append('\n');
            
            if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_PECA){
            
                if (acoesValorPeca.size() > 0){
                
                    for (int x = 0; x < acoesValorPeca.size()-1; x++){
                        builder.append("      ");
                        builder.append(acoesValorPeca.get(x));
                        builder.append('\n');
                    }    
                
                    builder.append("      ");
                    builder.append(acoesValorPeca.get(acoesValorPeca.size()-1));                    
                }
            }else
                if (tipoSelecionado == Heuristica.HEURISTICA_VALOR_TABULEIRO){
                
                    builder.append("      ");
                    builder.append(DHJOG.TXT_TABULEIRO);
                    builder.append(' ');
                    builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
                    builder.append(' ');
                    builder.append(DHJOG.TXT_TABULEIRO);
                    builder.append(' ');
                    builder.append(jComboBoxOperacaoValorTabuleiro.getSelectedItem());
                    builder.append(' ');
                    
                    double valor = ((Double) jSpinnerValorTabuleiro.getValue()).doubleValue();
                    builder.append(ParametroPreenchido.formata(valor));
                    
                }else
                    if (tipoSelecionado == Heuristica.HEURISTICA_TRANSICAO_ETAPA){
                    
                        if (jComboBoxProximaEtapa.getSelectedIndex() != -1){
                            
                            builder.append("      ");
                            builder.append(DHJOG.TXT_ETAPA_ATUAL);
                            builder.append(' ');
                            builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
                            builder.append(" \"");
                            builder.append(jComboBoxProximaEtapa.getSelectedItem());
                            builder.append("\"");
                        }
                    }
              
            builder.append('\n');            
            builder.append(DHJOG.TXT_FIM);
            builder.append(' ');
            builder.append(DHJOG.TXT_HEURISTICA);
            
            jTextAreaCodigoFonte.setText(builder.toString());
            
        }else
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelDadosPrincipais){
                
                jTextFieldVersao.setText(Long.toString(heuristica.getVersao()));
                jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(heuristica.getDataUltimaModificacao()));
            }
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged

    private void jButtonNovaCondicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaCondicaoActionPerformed

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaPreencheCondicao tela = new TelaPreencheCondicao(TelaHeuristica.this, (ModelListaComponentes) jListCondicoes.getModel());                
                jListCondicoes.setSelectedIndex(-1);
            }
        });
    }//GEN-LAST:event_jButtonNovaCondicaoActionPerformed

    private void jSpinnerValorTabuleiroStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorTabuleiroStateChanged
        alteracao = true;
    }//GEN-LAST:event_jSpinnerValorTabuleiroStateChanged

    private void jComboBoxOperacaoValorTabuleiroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxOperacaoValorTabuleiroItemStateChanged
        alteracao = true;
    }//GEN-LAST:event_jComboBoxOperacaoValorTabuleiroItemStateChanged

    private void jComboBoxProximaEtapaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxProximaEtapaItemStateChanged
        alteracao = true;
    }//GEN-LAST:event_jComboBoxProximaEtapaItemStateChanged

    private void jRadioButtonIrParaOutraEtapaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonIrParaOutraEtapaItemStateChanged
        
        if (jRadioButtonIrParaOutraEtapa.isSelected()){
            
            card.show(jPanelAlteracoes, "Ir para Outra Etapa"); 
            
            tipoSelecionado = Heuristica.HEURISTICA_TRANSICAO_ETAPA;
            alteracao = true;
        }
    }//GEN-LAST:event_jRadioButtonIrParaOutraEtapaItemStateChanged

    private void jRadioButtonAlterarValorTabuleiroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAlterarValorTabuleiroItemStateChanged
        
        if (jRadioButtonAlterarValorTabuleiro.isSelected()){
            
            card.show(jPanelAlteracoes, "Alterar Valor Tabuleiro"); 
            
            tipoSelecionado = Heuristica.HEURISTICA_VALOR_TABULEIRO;
            alteracao = true;
        }
    }//GEN-LAST:event_jRadioButtonAlterarValorTabuleiroItemStateChanged

    private void jRadioButtonAlterarValorPecaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAlterarValorPecaItemStateChanged
        
        if (jRadioButtonAlterarValorPeca.isSelected()){
            
            card.show(jPanelAlteracoes, "Alterar Valor Peça"); 
            
            tipoSelecionado = Heuristica.HEURISTICA_VALOR_PECA;
            alteracao = true;
        }
    }//GEN-LAST:event_jRadioButtonAlterarValorPecaItemStateChanged

    private void jListCondicoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListCondicoesValueChanged
        
        if (jListCondicoes.getSelectedIndex() != -1){
            jButtonExcluirCondicao.setEnabled(true);
            jButtonAbrirCondicao.setEnabled(true);
        }else{
            jButtonExcluirCondicao.setEnabled(false);
            jButtonAbrirCondicao.setEnabled(false);
        }
    }//GEN-LAST:event_jListCondicoesValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();        
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonNovaAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaAnotacaoActionPerformed
        adicionaNovaAnotacao();
    }//GEN-LAST:event_jButtonNovaAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);        
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jButtonExcluirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAnotacaoActionPerformed
        Anotacoes.confirmaApagarAnotacaoSelecionada(this);        
    }//GEN-LAST:event_jButtonExcluirAnotacaoActionPerformed

    private void jListAnotacoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnotacoesValueChanged
        Anotacoes.verificaSelecaoAnotacao(this);        
    }//GEN-LAST:event_jListAnotacoesValueChanged

    private void jListAnotacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnotacoesMouseClicked
        Anotacoes.verificaDuploCliqueAnotacao(this, evt);        
    }//GEN-LAST:event_jListAnotacoesMouseClicked

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaHeuristica");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jListAcoesValorPecaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAcoesValorPecaMouseClicked
        
        if (jListAcoesValorPeca.isEnabled()){
            
            if (evt.getClickCount() == 2){
                
                final int posicao = jListAcoesValorPeca.locationToIndex(evt.getPoint());
                
                if (posicao != -1){
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaPreencheAcaoValorPeca tela = new TelaPreencheAcaoValorPeca(TelaHeuristica.this,
                                                                                          (ModelListaComponentes) jListAcoesValorPeca.getModel(),
                                                                                          posicao);
                        }
                    });
                }
            }
        }        
    }//GEN-LAST:event_jListAcoesValorPecaMouseClicked

    private void jListAcoesValorPecaValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAcoesValorPecaValueChanged
        
        if (jListAcoesValorPeca.getSelectedIndex() != -1){
            jButtonExcluirAcaoValorPeca.setEnabled(true);
            jButtonAbrirAcaoValorPeca.setEnabled(true);
        }else{
            jButtonExcluirAcaoValorPeca.setEnabled(false);
            jButtonAbrirAcaoValorPeca.setEnabled(false);
        }        
    }//GEN-LAST:event_jListAcoesValorPecaValueChanged

    private void jButtonNovaAcaoValorPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaAcaoValorPecaActionPerformed
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaPreencheAcaoValorPeca tela = new TelaPreencheAcaoValorPeca(TelaHeuristica.this,
                                                                              (ModelListaComponentes) jListAcoesValorPeca.getModel());                
                jListAcoesValorPeca.setSelectedIndex(-1);
            }
        });        
    }//GEN-LAST:event_jButtonNovaAcaoValorPecaActionPerformed

    private void jButtonAbrirAcaoValorPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAcaoValorPecaActionPerformed
    
        final int posicao = jListAcoesValorPeca.getSelectedIndex();
        
        if (posicao != -1){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheAcaoValorPeca tela = new TelaPreencheAcaoValorPeca(TelaHeuristica.this,
                                                                                  (ModelListaComponentes) jListAcoesValorPeca.getModel(),
                                                                                   posicao);
                }
            });
        }        
    }//GEN-LAST:event_jButtonAbrirAcaoValorPecaActionPerformed

    private void jButtonExcluirAcaoValorPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAcaoValorPecaActionPerformed
        
        int posicao = jListAcoesValorPeca.getSelectedIndex();
        
        if (posicao != -1){
            
            int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente apagar a Ação de Valor de Peça selecionada?",
                                                             "Confirmação de Exclusão");
            if (resposta == JOptionPane.YES_OPTION){                
                
                ModelListaComponentes model = (ModelListaComponentes) jListAcoesValorPeca.getModel();
                model.remove(posicao);                
                
                if (HeuChess.somAtivado){
                    HeuChess.somApagar.play();
                }
                
                alteracao = true;
            }            
        }        
    }//GEN-LAST:event_jButtonExcluirAcaoValorPecaActionPerformed

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this,heuristica.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupTipoAlteracao;
    private javax.swing.JButton jButtonAbrirAcaoValorPeca;
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAbrirCondicao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonConfirmar;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonExcluirAcaoValorPeca;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JButton jButtonExcluirCondicao;
    private javax.swing.JButton jButtonNovaAcaoValorPeca;
    private javax.swing.JButton jButtonNovaAnotacao;
    private javax.swing.JButton jButtonNovaCondicao;
    private javax.swing.JButton jButtonNovaEtapa;
    private javax.swing.JComboBox jComboBoxOperacaoValorTabuleiro;
    private javax.swing.JComboBox jComboBoxProximaEtapa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDataModificacao;
    private javax.swing.JLabel jLabelSE;
    private javax.swing.JLabel jLabelTituloListaAnotacoes;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAcoesValorPeca;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JList jListCondicoes;
    private javax.swing.JPanel jPanelAlteracoes;
    private javax.swing.JPanel jPanelAlterarValorPeca;
    private javax.swing.JPanel jPanelAlterarValorTabuleiro;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelBotoes;
    private javax.swing.JPanel jPanelBotoesAnotacao;
    private javax.swing.JPanel jPanelCentralizador2;
    private javax.swing.JPanel jPanelCentralizar3;
    private javax.swing.JPanel jPanelCodigoGerado;
    private javax.swing.JPanel jPanelDadosPrincipais;
    private javax.swing.JPanel jPanelIrParaOutraEtapa;
    private javax.swing.JPanel jPanelLogicaHeuristica;
    private javax.swing.JPanel jPanelTipoAlteracao;
    private javax.swing.JRadioButton jRadioButtonAlterarValorPeca;
    private javax.swing.JRadioButton jRadioButtonAlterarValorTabuleiro;
    private javax.swing.JRadioButton jRadioButtonIrParaOutraEtapa;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner jSpinnerValorTabuleiro;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTextArea jTextAreaCodigoFonte;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldNomeHeuristica;
    private javax.swing.JTextField jTextFieldVersao;
    // End of variables declaration//GEN-END:variables
}
