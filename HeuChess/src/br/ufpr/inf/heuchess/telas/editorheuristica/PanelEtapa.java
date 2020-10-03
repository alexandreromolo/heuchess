package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.*;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao.DesenhaRegioes;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 18 de Julho de 2006, 14:11
 */
public class PanelEtapa extends javax.swing.JPanel implements AcessoTelaAnotacao, AcessoTelaUsuario, AcessoTelaRegiao {
    
    private CardLayout                   card;    
    private CardLayout                   cardPanelDireito;
    
    public  Etapa                        etapa;
    public  TelaEditorConjuntoHeuristico editor;
    
    public  ModelTreeHeuristicas         heuristicasTreeModel;
    private DesenhaRegioes               panelTabuleiro;    
    
    private MonitoraFocusNomeEtapa monitoraFocusNomeEtapa;
    
    private class MonitoraFocusNomeEtapa extends FocusAdapter {

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            verificaAtualizacaoNomeEtapa();
        }
    }
    
    public PanelEtapa(TelaEditorConjuntoHeuristico editor, Etapa etapa) {
        
        this.editor = editor;
        this.etapa  = etapa;
        
        panelTabuleiro = new DesenhaRegioes(this); 
        
        initComponents();
        
        try {
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(etapa.getIdAutor())));
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(editor, "Erro ao recuperar nome do autor da Etapa no Banco de Dados." +
                                         "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
            
            editor.fechar(true);
        }
                
        card = (CardLayout) getLayout();  
        cardPanelDireito = (CardLayout) jPanelLadoDireito.getLayout();
        
        mostraHeuristicas();
        
        // Oculta botão abrir descrição funcao //
        
        jPanelBotaoDescricaoFuncao.setVisible(false);        
        
        // Define cores e visibilidade das regiões criadas //
        
        for (int x = 0; x < etapa.getRegioes().size(); x++){
            Regiao regiao = etapa.getRegioes().get(x);
            regiao.setVisivel(true);
            regiao.setColorIcon(new ColorIcon(editor.colorList.nextColor()));            
        }
        
        atualizaInterfaceNivelComplexidade();
        Anotacoes.atualizaQuantidadeAnotacoes(this);   
        
        if (!editor.podeAlterar()){
            jFormattedTextFieldNomeEtapa.setEditable(false);
            
            jSpinnerValorPeao.setEnabled(false);
            jSpinnerValorTorre.setEnabled(false);
            jSpinnerValorCavalo.setEnabled(false);
            jSpinnerValorBispo.setEnabled(false);
            jSpinnerValorDama.setEnabled(false);
            
            jButtonNovaHeuristica.setVisible(false);
            jButtonExcluirHeuristica.setVisible(false);
            
            jButtonNovaRegiao.setVisible(false);
            jButtonExcluirRegiao.setVisible(false);
            
            jButtonExcluirAnotacao.setVisible(false);
            
            if (etapa.getRegioes().isEmpty()){
                jPanelBotoesRegiaoMovimenta.setVisible(false);
                jPanelBotoesRegiaoBaixo.setVisible(false);                
            }
                
        }else{
            monitoraFocusNomeEtapa = new MonitoraFocusNomeEtapa();
            jFormattedTextFieldNomeEtapa.addFocusListener(monitoraFocusNomeEtapa);
        
            FuncaoTreeDragSource source = new FuncaoTreeDragSource(jTreeFuncoesBasicas,DnDConstants.ACTION_COPY);
            FuncaoDropTarget    target1 = new FuncaoDropTarget(this,jLabelPeao,  TipoPeca.PEAO);
            FuncaoDropTarget    target2 = new FuncaoDropTarget(this,jLabelTorre, TipoPeca.TORRE);
            FuncaoDropTarget    target3 = new FuncaoDropTarget(this,jLabelCavalo,TipoPeca.CAVALO);
            FuncaoDropTarget    target4 = new FuncaoDropTarget(this,jLabelBispo, TipoPeca.BISPO);
            FuncaoDropTarget    target5 = new FuncaoDropTarget(this,jLabelDama,  TipoPeca.DAMA);
            FuncaoDropTarget    target6 = new FuncaoDropTarget(this,jLabelRei,   TipoPeca.REI);
            FuncaoDropTarget    target7 = new FuncaoDropTarget(this,jLabelRegiaoTodoTabuleiro,DHJOG.TODO_TABULEIRO);
            FuncaoDropTargetTabuleiro   target8 = new FuncaoDropTargetTabuleiro(this,panelTabuleiro);
            FuncaoDropTargetListRegioes target9 = new FuncaoDropTargetListRegioes(this,jListRegioes);
        }
        
        if (!editor.podeAnotar()){
            jButtonNovaAnotacao.setVisible(false);
            
            if (etapa.getAnotacoes().isEmpty()) {
                jButtonAbrirAnotacao.setVisible(false);
            }
        }
    }
    
    @Override
    public Frame getFrame(){
        return editor;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return null;
    }
    
    public final void atualizaInterfaceNivelComplexidade(){
        
        Tipo complexidade = editor.conjuntoHeuristico.getTipo();

        if (complexidade == ConjuntoHeuristico.NIVEL_1_INICIANTE){
            
            jTabbedPaneHeuristicasRegioes.setVisible(false);
            cardPanelDireito.show(jPanelLadoDireito,"ExplicacaoInicial");
            
        }else{
            
            jTabbedPaneHeuristicasRegioes.setVisible(true);
            cardPanelDireito.show(jPanelLadoDireito,"FuncoesBasicas");
            
            heuristicasTreeModel = new ModelTreeHeuristicas(etapa, complexidade);
            
            jTreeHeuristicasPorTipo.setModel(heuristicasTreeModel);
            heuristicasTreeModel.setJTree(jTreeHeuristicasPorTipo);
            UtilsTree.expandAll(jTreeHeuristicasPorTipo,true);
        }
    }
        
    public void mostraAbaHeuristicas(){
        
        mostraHeuristicas();
        
        jTabbedPaneHeuristicasRegioes.setSelectedComponent(jPanelTreeHeuristicas);
    }
    
    public void mostraAbaRegioes(){
        
        mostraHeuristicas();
        
        jTabbedPaneHeuristicasRegioes.setSelectedComponent(jPanelListRegioes);
    }
    
    public final void mostraHeuristicas(){
        
        card.show(this, "Heurísticas Etapa");
        
        jComboBoxVerHeuristicasEtapa.setSelectedIndex(0);        
    }
    
    public void mostraDadosPrincipais(){
        
        atualizaVersaoDataUltimaModificacao();        
        
        card.show(this,"Dados Principais");
        
        jComboBoxVerDadosPrincipais.setSelectedIndex(0);        
        jFormattedTextFieldNomeEtapa.requestFocus();
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null){
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(usuario.getNome()));
        }
    }
    
    @Override
    public void fechandoTelaRegiao(Regiao regiao){
        
    }
    
    public void fechandoTelaHeuristica() {
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeHeuristicasPorTipo.getLastSelectedPathComponent();
        
        if (node != null && (node.getUserObject() instanceof Heuristica)) {
            jButtonAbrirHeuristica.setEnabled(true);
            jButtonExcluirHeuristica.setEnabled(true);
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
        return etapa;
    }

    @Override
    public boolean podeAlterar() {
        return editor.podeAlterar();
    }
    
    @Override
    public void fechandoTelaAnotacao(boolean sucesso) {
              
    }    
        
    public void selecionaRegiao(int indice){
        
        jListRegioes.setSelectedIndex(indice);
        
        jPanelTabuleiro.repaint();
    }
    
    @Override
    public void atualizaVersaoDataUltimaModificacao(){
        jFormattedTextFieldVersao.setText(String.valueOf(etapa.getVersao()));
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(etapa.getDataUltimaModificacao()));                     
    }
    
    public void funcaoArrastada(final Funcao funcao, final Object tipo) {

        if (HeuChess.somAtivado) {
            HeuChess.somDragAndDrop.play();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaHeuristica tela = new TelaHeuristica(PanelEtapa.this, funcao, tipo);
            }
        });
    }
    
    public void adicionaNovaAnotacao(){
        
        mostraDadosPrincipais();
        
        Anotacoes.novaAnotacao(this);
    }
    
    public void adicionaNovaAnotacaoRegiao(final Regiao regiao) {

        mostraAbaRegioes();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaRegiao tela = new TelaRegiao(PanelEtapa.this, PanelEtapa.this, regiao, true);
            }
        });
    }
    
    public void adicionaNovaAnotacaoHeuristica(Heuristica heuristica){        
       
        mostraAbaHeuristicas();
        
        if (heuristica != null){            
            
            DefaultTreeModel       data = (DefaultTreeModel) jTreeHeuristicasPorTipo.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) data.getRoot();
            DefaultMutableTreeNode node = null;
            
            if (root != null){
                
                for (Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements(); ){
                    
                    DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
                    
                    if (heuristica.equals(current.getUserObject())){
                        node = current;
                        break;
                    }
                }
            }
            
            if (node != null){                
                
                TreePath path = new TreePath(data.getPathToRoot(node));
                
                jTreeHeuristicasPorTipo.setSelectionPath(path);
                jTreeHeuristicasPorTipo.scrollPathToVisible(path);
                
                Object nodeInfo = node.getUserObject();
                
                if (nodeInfo instanceof Heuristica){                    
                    
                    final DefaultMutableTreeNode nodePassado = node;
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaHeuristica tela = new TelaHeuristica(PanelEtapa.this,nodePassado,true);
                        }
                    });
                }                        
            }
        }
    }
    
    public void novaHeuristica() {
        
        mostraAbaHeuristicas();        
            
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaHeuristica tela = new TelaHeuristica(PanelEtapa.this);
            }
        });
    }
    
    public void novaRegiao() {
        
        mostraAbaRegioes();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaRegiao tela = new TelaRegiao(PanelEtapa.this, PanelEtapa.this);
            }
        });
    }
    
    public void abrirHeuristica(Heuristica heuristica){
        
        final DefaultMutableTreeNode node = heuristicasTreeModel.localizaNodeHeuristica(heuristica);
        
        if (node == null){
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaHeuristica tela = new TelaHeuristica(PanelEtapa.this, node, false); 
            }
        });
    }    
    
    private void abrirHeuristicaSelecionada(){
        
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeHeuristicasPorTipo.getLastSelectedPathComponent();
        
        if (node == null){
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof Heuristica){            
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaHeuristica tela = new TelaHeuristica(PanelEtapa.this, node, false); 
                }
            });
            
        }else
            if (nodeInfo instanceof ExpressaoCalculoHeuristico){
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                         TelaExpressaoHeuristica tela = new TelaExpressaoHeuristica(editor);   
                    }
                });
            }
    }
    
    public void abrirRegiao(final Regiao regiao) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaRegiao tela = new TelaRegiao(PanelEtapa.this, PanelEtapa.this, regiao, false);
            }
        });
    }
    
    private void abrirRegiaoSelecionada() {
        
        final int posicao = jListRegioes.getSelectedIndex();

        if (posicao != -1) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaRegiao tela = new TelaRegiao(PanelEtapa.this, PanelEtapa.this, (Regiao) jListRegioes.getSelectedValue(), false);
                }
            });
        }
    }
    
    private void abrirFuncaoSelecionada() {

        if (jTreeFuncoesBasicas.isEnabled()) {
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeFuncoesBasicas.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            final Object nodeInfo = node.getUserObject();

            if (nodeInfo instanceof Funcao) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaFuncao tela = new TelaFuncao(editor, (Funcao) nodeInfo, editor.conjuntoHeuristico.getTipo());
                    }
                });
            }
        }
    }
    
    public boolean confirmaApagarRegiao(Regiao regiao) {
        
        int resposta = UtilsGUI.dialogoConfirmacao(editor, "Deseja Realmente Apagar a Região\n" +
                                                           "\"" + regiao + "\" ?",
                                                           "Confirmação Exclusão");

        if (resposta == JOptionPane.YES_OPTION) {
            
            try{
                ///////////////////////////////////////////
                // Procura Heurísticas que usem a Região //
                /////////////////////////////////////////// 
                    
                int quantidade = 0;
        
                for (Etapa etapaAtual : editor.conjuntoHeuristico.getEtapas()){
                    quantidade += etapaAtual.quantidadeHeuristicasQueUsam(regiao);
                }
         
                if (quantidade > 0){
            
                    String mensagem;
                    
                    if (quantidade == 1){
                        mensagem = "Existe 1 Heurística que usa a Região que está sendo apagada.\n"+
                                   "Caso você realmente apague a Região \"" + regiao + "\" esta heurística também será apagada!\n\n"+
                                   "Deseja continuar a apagar a Região?";
                    }else{
                        mensagem = "Existem " + quantidade + " Heurísticas que usam a Região que está sendo apagada.\n"+
                                   "Caso você realmente apague a Região \"" + regiao + "\" estas heurísticas também serão apagadas!\n\n"+
                                   "Deseja continuar a apagar a Região?";
                    }
                    
                    int respostaApagarHeuristicas = UtilsGUI.dialogoConfirmacao(editor, mensagem, "Confirmação de Exclusão");
                
                    if (respostaApagarHeuristicas == JOptionPane.NO_OPTION || resposta == -1){
                        return false;
                    }
                }

                //////////////////
                // Apaga Região //
                //////////////////
                
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                RegiaoDAO.apaga(regiao, etapa);

                /////////////////////////////////////////
                // Apaga Heurísticas que usam a Região //
                /////////////////////////////////////////

                if (quantidade > 0) {
                    editor.apagaHeuristicasComOcorrencia(regiao.getNome());
                }

                ConexaoDBHeuChess.commit();

                ModelListaComponentes model = (ModelListaComponentes) jListRegioes.getModel();
                model.removeElement(regiao);

                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }

                jPanelTabuleiro.repaint();            
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return true;
                
            }catch(Exception e){
                HeuChess.desfazTransacao(e);
            
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(editor, "Erro ao tentar apagar a Região no Banco de Dados." +
                                             "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!"); 
                
                editor.fechar(true);
            }
        }

        return false;
    }
    
    public boolean confirmaApagarHeuristica(Heuristica heuristica) {

        int resposta = UtilsGUI.dialogoConfirmacao(editor,
                                                   "Deseja realmente Apagar a " + heuristica.getNomeTipoComponente() + "\n\"" + 
                                                    heuristica + "\"?",
                                                   "Confirmação de Exclusão");

        if (resposta == JOptionPane.YES_OPTION) {

            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                apagaHeuristica(heuristica);

                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return true;
                       
            } catch (Exception e) {
                HeuChess.desfazTransacao(e);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(editor, "Erro ao tentar apagar a Heuristica no Banco de Dados." +
                                             "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                
                editor.fechar(true);
            }
        }
        
        return false;
    }
    
    public void apagaHeuristica(Heuristica heuristica) throws Exception {
    
        HeuristicaDAO.apaga(heuristica, etapa);
        
        ConexaoDBHeuChess.commit();

        heuristicasTreeModel.remove(heuristica);

        if (heuristica instanceof HeuristicaTransicaoEtapa) {
            editor.editorMudancaEtapas.removeHeuristicaTransicao(heuristica);
        }
    } 
    
    private void atualizaValorPeca(TipoPeca tipo) {
        
        int valorAntigo = etapa.getValorDama();

        try {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));

            switch (tipo) {
                    
                case PEAO:
                    valorAntigo = etapa.getValorPeao();
                    etapa.setValorPeao(((Integer) jSpinnerValorPeao.getValue()).intValue());
                    break;

                case TORRE:
                    valorAntigo = etapa.getValorTorre();
                    etapa.setValorTorre(((Integer) jSpinnerValorTorre.getValue()).intValue());
                    break;

                case CAVALO:
                    valorAntigo = etapa.getValorCavalo();
                    etapa.setValorCavalo(((Integer) jSpinnerValorCavalo.getValue()).intValue());
                    break;
                                        
                case BISPO:
                    valorAntigo = etapa.getValorBispo();
                    etapa.setValorBispo(((Integer) jSpinnerValorBispo.getValue()).intValue());
                    break;
                                        
                case DAMA:
                    valorAntigo = etapa.getValorDama();
                    etapa.setValorDama(((Integer) jSpinnerValorDama.getValue()).intValue());
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo de peça não suportado pelo método [" + tipo + "]");
            }
            
            EtapaDAO.atualiza(etapa);
        
            ConexaoDBHeuChess.commit();
        
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        } catch (Exception e) {
            HeuChess.desfazTransacao(e);

            switch (tipo) {
                    
                case PEAO:
                    etapa.setValorPeao(valorAntigo);
                    break;

                case TORRE:
                    etapa.setValorTorre(valorAntigo);
                    break;

                case CAVALO:
                    etapa.setValorCavalo(valorAntigo);
                    break;
                                        
                case BISPO:
                    etapa.setValorBispo(valorAntigo);
                    break;
                                        
                case DAMA:
                    etapa.setValorDama(valorAntigo);
                    break;
            }

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(editor, "Erro ao tentar atualizar a Etapa no Banco de Dados." +
                                         "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
            
            editor.fechar(true);
        }
    }
    
    public ModelListaComponentes getModelRegioes() {
        return (ModelListaComponentes) jListRegioes.getModel();
    }
     
    public boolean verificaAtualizacaoNomeEtapa() {
        
        jFormattedTextFieldNomeEtapa.removeFocusListener(monitoraFocusNomeEtapa);
        
        String nome  = jFormattedTextFieldNomeEtapa.getText();
        nome         = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);
        String erro  = editor.conjuntoHeuristico.validaNomeUnicoComponente(nome);

        boolean ehNomeNovo = !etapa.getNome().equalsIgnoreCase(nome);
        boolean sucesso;
        
        if (ehNomeNovo) {

            if (erro != null) {

                editor.selecionaPanelEtapa(etapa);
                jFormattedTextFieldNomeEtapa.requestFocus();
                jFormattedTextFieldNomeEtapa.selectAll();

                UtilsGUI.dialogoErro(editor, erro + "\n\nAlteração será desfeita!");

                editor.setTitleTabbedPane(etapa.getNome(), this);
                jFormattedTextFieldNomeEtapa.setText(etapa.getNome());
                jFormattedTextFieldNomeEtapa.requestFocus();
                jFormattedTextFieldNomeEtapa.selectAll();

                sucesso = false;

            } else {

                String nomeVelho = etapa.getNome();
                etapa.setNome(nome);

                try {
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    EtapaDAO.atualiza(etapa);

                    ///////////////////////////////////////////////////////////
                    // Renomeia a Etapa em todas as Heurísticas de Transição //
                    ///////////////////////////////////////////////////////////

                    for (Etapa etapaAtual : editor.conjuntoHeuristico.getEtapas()) {

                        if (etapaAtual != etapa) {
                            etapaAtual.procuraRenomeia(etapa, nomeVelho);
                        }
                    }

                    //////////////////////////////////////////////////////////////////
                    // Verifica se a Etapa é a Etapa Inicial do Conjunto Heurístico //
                    //////////////////////////////////////////////////////////////////

                    if (editor.conjuntoHeuristico.getEtapaInicial().getId() == etapa.getId()) {
                        
                        ConjuntoHeuristicoDAO.atualiza(editor.conjuntoHeuristico);
                    }

                    ConexaoDBHeuChess.commit();

                    atualizaVersaoDataUltimaModificacao();

                    editor.setTitleTabbedPane(nome, this);

                    sucesso = true;

                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);

                    editor.selecionaPanelEtapa(etapa);
                    jFormattedTextFieldNomeEtapa.requestFocus();
                    jFormattedTextFieldNomeEtapa.selectAll();

                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar atualizar o Nome da Etapa no Banco de Dados." +
                                               "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");

                    etapa.setNome(nomeVelho);

                    editor.setTitleTabbedPane(etapa.getNome(), this);
                    jFormattedTextFieldNomeEtapa.setText(etapa.getNome());
                    jFormattedTextFieldNomeEtapa.requestFocus();
                    jFormattedTextFieldNomeEtapa.selectAll();

                    sucesso = false;
                    
                    editor.fechar(true);
                }
            }
        }else{
            sucesso = true;
        }
        
        jFormattedTextFieldNomeEtapa.addFocusListener(monitoraFocusNomeEtapa);
        return sucesso;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelHeuristicasEtapa = new javax.swing.JPanel();
        jPanelRegioes = new javax.swing.JPanel();
        jLabelRegiaoTodoTabuleiro = new javax.swing.JLabel();
        jPanelTabuleiro = new javax.swing.JPanel();
        jPanelPecas = new javax.swing.JPanel();
        jPanelPeao = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabelPeao = new javax.swing.JLabel();
        jSpinnerValorPeao = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); ;
        jPanelTorre = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelTorre = new javax.swing.JLabel();
        jSpinnerValorTorre = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); ;
        jPanelCavalo = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabelCavalo = new javax.swing.JLabel();
        jSpinnerValorCavalo = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); ;
        jPanelBispo = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabelBispo = new javax.swing.JLabel();
        jSpinnerValorBispo = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); ;
        jPanelDama = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelDama = new javax.swing.JLabel();
        jSpinnerValorDama = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); ;
        jPanelRei = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelRei = new javax.swing.JLabel();
        jTextFieldValorRei = new javax.swing.JTextField();
        jPanelLadoDireito = new javax.swing.JPanel();
        jPanelFuncoesBasicas = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jSplitPaneFuncoesBasicas = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTreeFuncoesBasicas = new JTree(HeuChess.treeFuncoesBasicas);
        jPanelDescricaoFuncao = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaDescricaoFuncao = new javax.swing.JTextArea();
        jPanelBotaoDescricaoFuncao = new javax.swing.JPanel();
        jButtonDetalhesFuncao = new javax.swing.JButton();
        jPanelExplicacaoInicial = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextAreaExplicacoesIniciais = new javax.swing.JTextArea();
        jComboBoxVerHeuristicasEtapa = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jTabbedPaneHeuristicasRegioes = new javax.swing.JTabbedPane();
        jPanelTreeHeuristicas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTreeHeuristicasPorTipo = new JTree();
        jPanelBotoesHeuristica = new javax.swing.JPanel();
        jButtonAbrirHeuristica = new javax.swing.JButton();
        jButtonNovaHeuristica = new javax.swing.JButton();
        jButtonExcluirHeuristica = new javax.swing.JButton();
        jPanelListRegioes = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListRegioes = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(etapa.getRegioes()));
        jPanelBotoesRegiaoBaixo = new javax.swing.JPanel();
        jButtonAbrirRegiao = new javax.swing.JButton();
        jButtonNovaRegiao = new javax.swing.JButton();
        jButtonExcluirRegiao = new javax.swing.JButton();
        jPanelBotoesRegiaoMovimenta = new javax.swing.JPanel();
        jButtonSubirRegiao = new javax.swing.JButton();
        jButtonDescerRegiao = new javax.swing.JButton();
        jPanelDadosPrincipais = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxVerDadosPrincipais = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonNovaAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes(etapa.getAnotacoes()));
        jLabelTituloListaAnotacoesEtapa = new javax.swing.JLabel();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButtonDadosAutor = new javax.swing.JButton();
        jFormattedTextFieldNomeEtapa = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextFieldVersao = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        jPanelRegioes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "  Regiões  ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabelRegiaoTodoTabuleiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"))); // NOI18N
        jLabelRegiaoTodoTabuleiro.setText("Todo_Tabuleiro");
        jLabelRegiaoTodoTabuleiro.setToolTipText("Região que representa Todo o Tabuleiro");
        jLabelRegiaoTodoTabuleiro.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jPanelTabuleiro.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanelRegioesLayout = new javax.swing.GroupLayout(jPanelRegioes);
        jPanelRegioes.setLayout(jPanelRegioesLayout);
        jPanelRegioesLayout.setHorizontalGroup(
            jPanelRegioesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRegioesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelRegiaoTodoTabuleiro)
                .addContainerGap())
            .addComponent(jPanelTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelRegioesLayout.setVerticalGroup(
            jPanelRegioesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegioesLayout.createSequentialGroup()
                .addComponent(jLabelRegiaoTodoTabuleiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelTabuleiro.add(panelTabuleiro);

        jPanelPecas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "  Peças  - Valores Iniciais da Etapa  "));
        jPanelPecas.setLayout(new java.awt.GridLayout(1, 6));

        jPanelPeao.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelPeao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/peao.png"))); // NOI18N
        jLabelPeao.setToolTipText("Peão");

        jSpinnerValorPeao.setMaximumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorPeao.setMinimumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorPeao.setPreferredSize(new java.awt.Dimension(40, 18));
        jSpinnerValorPeao.setValue(etapa.getValorPeao());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorPeao, null);
        jSpinnerValorPeao.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorPeaoStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelPeao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSpinnerValorPeao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabelPeao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorPeao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelPeao.add(jPanel8);

        jPanelPecas.add(jPanelPeao);

        jPanelTorre.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelTorre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/torre.png"))); // NOI18N
        jLabelTorre.setToolTipText("Torre");

        jSpinnerValorTorre.setMaximumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorTorre.setMinimumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorTorre.setPreferredSize(new java.awt.Dimension(40, 18));
        jSpinnerValorTorre.setValue(etapa.getValorTorre());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorTorre, null);
        jSpinnerValorTorre.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorTorreStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSpinnerValorTorre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelTorre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabelTorre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorTorre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelTorre.add(jPanel2);

        jPanelPecas.add(jPanelTorre);

        jPanelCavalo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelCavalo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/cavalo.png"))); // NOI18N
        jLabelCavalo.setToolTipText("Cavalo");

        jSpinnerValorCavalo.setMaximumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorCavalo.setMinimumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorCavalo.setPreferredSize(new java.awt.Dimension(40, 18));
        jSpinnerValorCavalo.setValue(etapa.getValorCavalo());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorCavalo, null);
        jSpinnerValorCavalo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorCavaloStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelCavalo)
            .addComponent(jSpinnerValorCavalo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabelCavalo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorCavalo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelCavalo.add(jPanel3);

        jPanelPecas.add(jPanelCavalo);

        jPanelBispo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelBispo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/bispo.png"))); // NOI18N
        jLabelBispo.setToolTipText("Bispo");

        jSpinnerValorBispo.setMaximumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorBispo.setMinimumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorBispo.setPreferredSize(new java.awt.Dimension(40, 18));
        jSpinnerValorBispo.setValue(etapa.getValorBispo());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorBispo, null);
        jSpinnerValorBispo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorBispoStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelBispo)
            .addComponent(jSpinnerValorBispo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabelBispo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorBispo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelBispo.add(jPanel4);

        jPanelPecas.add(jPanelBispo);

        jPanelDama.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelDama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/dama.png"))); // NOI18N
        jLabelDama.setToolTipText("Dama");

        jSpinnerValorDama.setMaximumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorDama.setMinimumSize(new java.awt.Dimension(40, 18));
        jSpinnerValorDama.setPreferredSize(new java.awt.Dimension(40, 18));
        jSpinnerValorDama.setValue(etapa.getValorDama());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorDama, null);
        jSpinnerValorDama.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerValorDamaStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelDama)
            .addComponent(jSpinnerValorDama, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabelDama)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorDama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelDama.add(jPanel5);

        jPanelPecas.add(jPanelDama);

        jPanelRei.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabelRei.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/rei.png"))); // NOI18N
        jLabelRei.setToolTipText("Rei");

        jTextFieldValorRei.setEditable(false);
        jTextFieldValorRei.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldValorRei.setText("+INF");
        jTextFieldValorRei.setMaximumSize(new java.awt.Dimension(40, 18));
        jTextFieldValorRei.setMinimumSize(new java.awt.Dimension(40, 18));
        jTextFieldValorRei.setPreferredSize(new java.awt.Dimension(40, 18));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelRei)
            .addComponent(jTextFieldValorRei, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabelRei)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldValorRei, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelRei.add(jPanel6);

        jPanelPecas.add(jPanelRei);

        jPanelLadoDireito.setLayout(new java.awt.CardLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Funções Básicas");

        jSplitPaneFuncoesBasicas.setDividerLocation(250);
        jSplitPaneFuncoesBasicas.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTreeFuncoesBasicas.setRootVisible(false);
        jTreeFuncoesBasicas.setShowsRootHandles(true);
        jTreeFuncoesBasicas.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeFuncoesBasicas.setCellRenderer(new RenderTreeFuncoes());
        jTreeFuncoesBasicas.setToggleClickCount(1);
        jTreeFuncoesBasicas.setScrollsOnExpand(true);
        jTreeFuncoesBasicas.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeFuncoesBasicasValueChanged(evt);
            }
        });
        jTreeFuncoesBasicas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeFuncoesBasicasMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTreeFuncoesBasicas);

        jSplitPaneFuncoesBasicas.setLeftComponent(jScrollPane4);

        jTextAreaDescricaoFuncao.setEditable(false);
        jTextAreaDescricaoFuncao.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoFuncao.setColumns(20);
        jTextAreaDescricaoFuncao.setLineWrap(true);
        jTextAreaDescricaoFuncao.setRows(4);
        jTextAreaDescricaoFuncao.setWrapStyleWord(true);
        jTextAreaDescricaoFuncao.setFocusable(false);
        jTextAreaDescricaoFuncao.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jScrollPane5.setViewportView(jTextAreaDescricaoFuncao);

        java.awt.FlowLayout flowLayout2 = new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5);
        flowLayout2.setAlignOnBaseline(true);
        jPanelBotaoDescricaoFuncao.setLayout(flowLayout2);

        jButtonDetalhesFuncao.setText("Detalhes da Função");
        jButtonDetalhesFuncao.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoActionPerformed(evt);
            }
        });
        jPanelBotaoDescricaoFuncao.add(jButtonDetalhesFuncao);

        javax.swing.GroupLayout jPanelDescricaoFuncaoLayout = new javax.swing.GroupLayout(jPanelDescricaoFuncao);
        jPanelDescricaoFuncao.setLayout(jPanelDescricaoFuncaoLayout);
        jPanelDescricaoFuncaoLayout.setHorizontalGroup(
            jPanelDescricaoFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBotaoDescricaoFuncao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
        );
        jPanelDescricaoFuncaoLayout.setVerticalGroup(
            jPanelDescricaoFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDescricaoFuncaoLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jPanelBotaoDescricaoFuncao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPaneFuncoesBasicas.setRightComponent(jPanelDescricaoFuncao);

        javax.swing.GroupLayout jPanelFuncoesBasicasLayout = new javax.swing.GroupLayout(jPanelFuncoesBasicas);
        jPanelFuncoesBasicas.setLayout(jPanelFuncoesBasicasLayout);
        jPanelFuncoesBasicasLayout.setHorizontalGroup(
            jPanelFuncoesBasicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneFuncoesBasicas)
            .addGroup(jPanelFuncoesBasicasLayout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelFuncoesBasicasLayout.setVerticalGroup(
            jPanelFuncoesBasicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncoesBasicasLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPaneFuncoesBasicas, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        jPanelLadoDireito.add(jPanelFuncoesBasicas, "FuncoesBasicas");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Explicações Iniciais");

        jTextAreaExplicacoesIniciais.setColumns(20);
        jTextAreaExplicacoesIniciais.setEditable(false);
        jTextAreaExplicacoesIniciais.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaExplicacoesIniciais.setLineWrap(true);
        jTextAreaExplicacoesIniciais.setRows(5);
        jTextAreaExplicacoesIniciais.setText("  Defina os valores iniciais de cada uma das Peças do Jogo. Se uma peça possui um valor inicial maior do que outra, o jogador automático de Xadrez dará preferência em manté-la durante a partida.\n\n  O Rei é a única Peça que não precisa de valor, pois se ela for eliminada o jogo acaba. Então ela sempre será a Peça de maior valor no jogo.\n\n  Inicialmente o valor Heurístico de uma situação de jogo é calculado como sendo a soma do valor de cada peça do jogador menos a soma das peças do adversário. Quanto maior o valor calculado melhor o tabuleiro para o jogador.");
        jTextAreaExplicacoesIniciais.setToolTipText("Explicações Iniciais");
        jTextAreaExplicacoesIniciais.setWrapStyleWord(true);
        jTextAreaExplicacoesIniciais.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane8.setViewportView(jTextAreaExplicacoesIniciais);

        javax.swing.GroupLayout jPanelExplicacaoInicialLayout = new javax.swing.GroupLayout(jPanelExplicacaoInicial);
        jPanelExplicacaoInicial.setLayout(jPanelExplicacaoInicialLayout);
        jPanelExplicacaoInicialLayout.setHorizontalGroup(
            jPanelExplicacaoInicialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );
        jPanelExplicacaoInicialLayout.setVerticalGroup(
            jPanelExplicacaoInicialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelExplicacaoInicialLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        jPanelLadoDireito.add(jPanelExplicacaoInicial, "ExplicacaoInicial");

        jComboBoxVerHeuristicasEtapa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Heurísticas", "Dados Principais" }));
        jComboBoxVerHeuristicasEtapa.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxVerHeuristicasEtapa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxVerHeuristicasEtapaItemStateChanged(evt);
            }
        });

        jLabel5.setText("Ver");

        jTreeHeuristicasPorTipo.setRootVisible(false);
        jTreeHeuristicasPorTipo.setShowsRootHandles(true);
        jTreeHeuristicasPorTipo.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeHeuristicasPorTipo.setCellRenderer(new RenderTreeHeuristicas());
        jTreeHeuristicasPorTipo.setToggleClickCount(1);
        jTreeHeuristicasPorTipo.setScrollsOnExpand(true);
        jTreeHeuristicasPorTipo.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeHeuristicasPorTipoValueChanged(evt);
            }
        });
        jTreeHeuristicasPorTipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeHeuristicasPorTipoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTreeHeuristicasPorTipo);

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0);
        flowLayout1.setAlignOnBaseline(true);
        jPanelBotoesHeuristica.setLayout(flowLayout1);

        jButtonAbrirHeuristica.setText("Abrir");
        jButtonAbrirHeuristica.setEnabled(false);
        jButtonAbrirHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirHeuristicaActionPerformed(evt);
            }
        });
        jPanelBotoesHeuristica.add(jButtonAbrirHeuristica);

        jButtonNovaHeuristica.setText("Nova");
        jButtonNovaHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaHeuristicaActionPerformed(evt);
            }
        });
        jPanelBotoesHeuristica.add(jButtonNovaHeuristica);

        jButtonExcluirHeuristica.setText("Excluir");
        jButtonExcluirHeuristica.setEnabled(false);
        jButtonExcluirHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirHeuristicaActionPerformed(evt);
            }
        });
        jPanelBotoesHeuristica.add(jButtonExcluirHeuristica);

        javax.swing.GroupLayout jPanelTreeHeuristicasLayout = new javax.swing.GroupLayout(jPanelTreeHeuristicas);
        jPanelTreeHeuristicas.setLayout(jPanelTreeHeuristicasLayout);
        jPanelTreeHeuristicasLayout.setHorizontalGroup(
            jPanelTreeHeuristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBotoesHeuristica, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );
        jPanelTreeHeuristicasLayout.setVerticalGroup(
            jPanelTreeHeuristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTreeHeuristicasLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesHeuristica, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPaneHeuristicasRegioes.addTab("Heurísticas", jPanelTreeHeuristicas);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(76, 132));

        jListRegioes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListRegioes.setCellRenderer(new br.ufpr.inf.heuchess.telas.editorheuristica.RenderListaRegioes());
        jListRegioes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListRegioesValueChanged(evt);
            }
        });
        jListRegioes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListRegioesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jListRegioes);

        jPanelBotoesRegiaoBaixo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButtonAbrirRegiao.setText("Abrir");
        jButtonAbrirRegiao.setEnabled(false);
        jButtonAbrirRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirRegiaoActionPerformed(evt);
            }
        });
        jPanelBotoesRegiaoBaixo.add(jButtonAbrirRegiao);

        jButtonNovaRegiao.setText("Nova");
        jButtonNovaRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaRegiaoActionPerformed(evt);
            }
        });
        jPanelBotoesRegiaoBaixo.add(jButtonNovaRegiao);

        jButtonExcluirRegiao.setText("Excluir");
        jButtonExcluirRegiao.setEnabled(false);
        jButtonExcluirRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirRegiaoActionPerformed(evt);
            }
        });
        jPanelBotoesRegiaoBaixo.add(jButtonExcluirRegiao);

        jPanelBotoesRegiaoMovimenta.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButtonSubirRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_seta_cima.png"))); // NOI18N
        jButtonSubirRegiao.setText("Frente");
        jButtonSubirRegiao.setEnabled(false);
        jButtonSubirRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubirRegiaoActionPerformed(evt);
            }
        });
        jPanelBotoesRegiaoMovimenta.add(jButtonSubirRegiao);

        jButtonDescerRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_seta_desce.png"))); // NOI18N
        jButtonDescerRegiao.setText("Traz");
        jButtonDescerRegiao.setEnabled(false);
        jButtonDescerRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescerRegiaoActionPerformed(evt);
            }
        });
        jPanelBotoesRegiaoMovimenta.add(jButtonDescerRegiao);

        javax.swing.GroupLayout jPanelListRegioesLayout = new javax.swing.GroupLayout(jPanelListRegioes);
        jPanelListRegioes.setLayout(jPanelListRegioesLayout);
        jPanelListRegioesLayout.setHorizontalGroup(
            jPanelListRegioesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBotoesRegiaoBaixo, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
            .addComponent(jPanelBotoesRegiaoMovimenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelListRegioesLayout.setVerticalGroup(
            jPanelListRegioesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelListRegioesLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesRegiaoMovimenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesRegiaoBaixo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPaneHeuristicasRegioes.addTab("Regiões", jPanelListRegioes);

        javax.swing.GroupLayout jPanelHeuristicasEtapaLayout = new javax.swing.GroupLayout(jPanelHeuristicasEtapa);
        jPanelHeuristicasEtapa.setLayout(jPanelHeuristicasEtapaLayout);
        jPanelHeuristicasEtapaLayout.setHorizontalGroup(
            jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeuristicasEtapaLayout.createSequentialGroup()
                .addComponent(jTabbedPaneHeuristicasRegioes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelRegioes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPecas, javax.swing.GroupLayout.PREFERRED_SIZE, 249, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelHeuristicasEtapaLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxVerHeuristicasEtapa, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanelLadoDireito, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelHeuristicasEtapaLayout.setVerticalGroup(
            jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeuristicasEtapaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHeuristicasEtapaLayout.createSequentialGroup()
                        .addGroup(jPanelHeuristicasEtapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxVerHeuristicasEtapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelLadoDireito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelHeuristicasEtapaLayout.createSequentialGroup()
                        .addComponent(jPanelRegioes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelPecas, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPaneHeuristicasRegioes)))
        );

        add(jPanelHeuristicasEtapa, "Heurísticas Etapa");

        jLabel6.setText("Ver");

        jComboBoxVerDadosPrincipais.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dados Principais", "Heurísticas" }));
        jComboBoxVerDadosPrincipais.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxVerDadosPrincipais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxVerDadosPrincipaisItemStateChanged(evt);
            }
        });

        jButtonAbrirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirAnotacao.setMnemonic('a');
        jButtonAbrirAnotacao.setText("Abrir");
        jButtonAbrirAnotacao.setEnabled(false);
        jButtonAbrirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirAnotacaoActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAbrirAnotacao);

        jButtonNovaAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaAnotacao.setMnemonic('d');
        jButtonNovaAnotacao.setText("Adicionar");
        jButtonNovaAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaAnotacaoActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonNovaAnotacao);

        jButtonExcluirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirAnotacao.setMnemonic('x');
        jButtonExcluirAnotacao.setText("Excluir");
        jButtonExcluirAnotacao.setEnabled(false);
        jButtonExcluirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirAnotacaoActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonExcluirAnotacao);

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

        jLabelTituloListaAnotacoesEtapa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTituloListaAnotacoesEtapa.setText("Anotações Gerais Sobre a Etapa");

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        jTextFieldNomeAutor.setEditable(false);

        jLabel7.setText("Autor");

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        jFormattedTextFieldNomeEtapa.setMaximumSize(new java.awt.Dimension(30, 410));
        jFormattedTextFieldNomeEtapa.setDocument(new DocumentMasked(DHJOG.CARACTERES_VALIDOS,DocumentMasked.ONLY_CAPITAL));
        jFormattedTextFieldNomeEtapa.setText(etapa.getNome());
        jFormattedTextFieldNomeEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldNomeEtapaActionPerformed(evt);
            }
        });
        jFormattedTextFieldNomeEtapa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldNomeEtapaKeyTyped(evt);
            }
        });

        jLabel2.setText("Nome Etapa");

        jFormattedTextFieldVersao.setEditable(false);
        jFormattedTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextFieldVersao.setText(Long.toString(etapa.getVersao()));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Versão");

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(etapa.getDataCriacao()));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Criação");

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(etapa.getDataUltimaModificacao()));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Modificação");

        javax.swing.GroupLayout jPanelDadosPrincipaisLayout = new javax.swing.GroupLayout(jPanelDadosPrincipais);
        jPanelDadosPrincipais.setLayout(jPanelDadosPrincipaisLayout);
        jPanelDadosPrincipaisLayout.setHorizontalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                            .addComponent(jFormattedTextFieldNomeEtapa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jButtonDadosAutor)
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxVerDadosPrincipais, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                        .addComponent(jLabelTituloListaAnotacoesEtapa)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelTotalAnotacoes))
                                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(29, 29, 29)
                                        .addComponent(jFormattedTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );

        jPanelDadosPrincipaisLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jFormattedTextFieldVersao, jTextFieldDataCriacao, jTextFieldDataModificacao});

        jPanelDadosPrincipaisLayout.setVerticalGroup(
            jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxVerDadosPrincipais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jFormattedTextFieldNomeEtapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDadosPrincipaisLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonDadosAutor))
                .addGap(17, 17, 17)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jFormattedTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelDadosPrincipaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTituloListaAnotacoesEtapa)
                    .addComponent(jLabelTotalAnotacoes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelDadosPrincipaisLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextFieldVersao, jTextFieldDataCriacao, jTextFieldDataModificacao});

        add(jPanelDadosPrincipais, "Dados Principais");
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDetalhesFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoActionPerformed
        abrirFuncaoSelecionada();
    }//GEN-LAST:event_jButtonDetalhesFuncaoActionPerformed

    private void jTreeFuncoesBasicasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeFuncoesBasicasMouseClicked
     
        if (evt.getClickCount() == 2) {
            abrirFuncaoSelecionada();
        }
    }//GEN-LAST:event_jTreeFuncoesBasicasMouseClicked
       
    private void jButtonAbrirRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirRegiaoActionPerformed
        abrirRegiaoSelecionada();
    }//GEN-LAST:event_jButtonAbrirRegiaoActionPerformed

    private void jButtonDescerRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescerRegiaoActionPerformed
        
        int posicao = jListRegioes.getSelectedIndex();
        if (posicao != -1){
            
            ModelListaComponentes model = (ModelListaComponentes) jListRegioes.getModel();
            
            if (model.moveUmParaFim(posicao)){                
                selecionaRegiao(++posicao);                
            }
        }
    }//GEN-LAST:event_jButtonDescerRegiaoActionPerformed

    private void jButtonSubirRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubirRegiaoActionPerformed
        
        int posicao = jListRegioes.getSelectedIndex();
        if (posicao != -1){
            
            ModelListaComponentes model = (ModelListaComponentes) jListRegioes.getModel();
            
            if (model.moveUmParaInicio(posicao)){                
                selecionaRegiao(--posicao);                
            }
        }
    }//GEN-LAST:event_jButtonSubirRegiaoActionPerformed

    private void jButtonExcluirRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirRegiaoActionPerformed

        int posicao = jListRegioes.getSelectedIndex();
        
        if (posicao != -1){  
            
            Regiao regiao = etapa.getRegioes().get(posicao);                        
            
            confirmaApagarRegiao(regiao);            
        }
    }//GEN-LAST:event_jButtonExcluirRegiaoActionPerformed
    
    private void jListRegioesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListRegioesValueChanged
        
        int posicao = jListRegioes.getSelectedIndex();
        
        if (posicao != -1){
            
            jButtonAbrirRegiao.setEnabled(true);
            jButtonExcluirRegiao.setEnabled(true);
            
            if (posicao == 0){
                jButtonSubirRegiao.setEnabled(false);
            }else{
                jButtonSubirRegiao.setEnabled(true);
            }
            
            if (posicao == jListRegioes.getModel().getSize()-1){
                jButtonDescerRegiao.setEnabled(false);
            }else{
                jButtonDescerRegiao.setEnabled(true);
            }
            
        }else{            
            jButtonExcluirRegiao.setEnabled(false);
            jButtonAbrirRegiao.setEnabled(false);
            jButtonSubirRegiao.setEnabled(false);
            jButtonDescerRegiao.setEnabled(false);
        }
    }//GEN-LAST:event_jListRegioesValueChanged
       
    private void jListRegioesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListRegioesMouseClicked
        
        if (jListRegioes.isEnabled()){
            
            final int indice = jListRegioes.locationToIndex(evt.getPoint());
            
            if (indice != -1){
                
                if (evt.getClickCount() == 2){
                    abrirRegiaoSelecionada();                    
                }else
                    if (RenderListaRegioes.dentroCheckBox(evt.getX())){
                        
                        Regiao atual = (Regiao) jListRegioes.getModel().getElementAt(indice);
                        atual.setVisivel(!atual.isVisivel());
                        
                        Rectangle rect = jListRegioes.getCellBounds(indice, indice);
                        jListRegioes.repaint(rect);
                        jPanelTabuleiro.repaint();
                    }
            }            
        }    
    }//GEN-LAST:event_jListRegioesMouseClicked
    
    private void jButtonNovaRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaRegiaoActionPerformed
        novaRegiao();
    }//GEN-LAST:event_jButtonNovaRegiaoActionPerformed

    private void jTreeHeuristicasPorTipoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeHeuristicasPorTipoMouseClicked
        
        if (jTreeHeuristicasPorTipo.isEnabled()){
            
            if (evt.getClickCount() == 2){
                abrirHeuristicaSelecionada();
            }
        }
    }//GEN-LAST:event_jTreeHeuristicasPorTipoMouseClicked
   
    private void jButtonExcluirHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirHeuristicaActionPerformed
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeHeuristicasPorTipo.getLastSelectedPathComponent();
        
        if (node == null){
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof Heuristica){
            confirmaApagarHeuristica((Heuristica) nodeInfo);            
        }
    }//GEN-LAST:event_jButtonExcluirHeuristicaActionPerformed
    
    private void jButtonAbrirHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirHeuristicaActionPerformed
        abrirHeuristicaSelecionada();
    }//GEN-LAST:event_jButtonAbrirHeuristicaActionPerformed

    private void jTreeHeuristicasPorTipoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeHeuristicasPorTipoValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeHeuristicasPorTipo.getLastSelectedPathComponent();
        
        if (node == null){
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof Heuristica){            
            jButtonAbrirHeuristica.setEnabled(true);
            jButtonExcluirHeuristica.setEnabled(true);
        }else
            if (nodeInfo instanceof ExpressaoCalculoHeuristico){
                jButtonAbrirHeuristica.setEnabled(true);
                jButtonExcluirHeuristica.setEnabled(false);
            }else{        
                jButtonAbrirHeuristica.setEnabled(false);
                jButtonExcluirHeuristica.setEnabled(false);
            }
    }//GEN-LAST:event_jTreeHeuristicasPorTipoValueChanged

    private void jButtonNovaHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaHeuristicaActionPerformed
        novaHeuristica();
    }//GEN-LAST:event_jButtonNovaHeuristicaActionPerformed
    
    private void jTreeFuncoesBasicasValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeFuncoesBasicasValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeFuncoesBasicas.getLastSelectedPathComponent();
        
        if (node == null){
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof Funcao){
            
            Funcao funcao = (Funcao) nodeInfo;
            
            jTextAreaDescricaoFuncao.setText(funcao.getDescricaoFuncao());
            jPanelBotaoDescricaoFuncao.setVisible(true);
            
        }else{
            
            if (nodeInfo instanceof Tipo){
                
                Tipo tipo = (Tipo) nodeInfo;
                jTextAreaDescricaoFuncao.setText(tipo.getDescricao());
            }
            
            jPanelBotaoDescricaoFuncao.setVisible(false);
        }
    }//GEN-LAST:event_jTreeFuncoesBasicasValueChanged
    
    private void jSpinnerValorDamaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorDamaStateChanged
        atualizaValorPeca(TipoPeca.DAMA);        
    }//GEN-LAST:event_jSpinnerValorDamaStateChanged

    private void jSpinnerValorBispoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorBispoStateChanged
        atualizaValorPeca(TipoPeca.BISPO);              
    }//GEN-LAST:event_jSpinnerValorBispoStateChanged

    private void jSpinnerValorCavaloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorCavaloStateChanged
        atualizaValorPeca(TipoPeca.CAVALO);              
    }//GEN-LAST:event_jSpinnerValorCavaloStateChanged

    private void jSpinnerValorTorreStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorTorreStateChanged
        atualizaValorPeca(TipoPeca.TORRE);              
    }//GEN-LAST:event_jSpinnerValorTorreStateChanged

    private void jSpinnerValorPeaoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerValorPeaoStateChanged
        atualizaValorPeca(TipoPeca.PEAO);              
    }//GEN-LAST:event_jSpinnerValorPeaoStateChanged
    
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
        adicionaNovaAnotacao();
    }//GEN-LAST:event_jButtonNovaAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);        
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jComboBoxVerHeuristicasEtapaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxVerHeuristicasEtapaItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED){
            
            if (jComboBoxVerHeuristicasEtapa.getSelectedIndex() == 1){
                mostraDadosPrincipais();            
            }
        }
    }//GEN-LAST:event_jComboBoxVerHeuristicasEtapaItemStateChanged

    private void jComboBoxVerDadosPrincipaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxVerDadosPrincipaisItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED){
            
            if (jComboBoxVerDadosPrincipais.getSelectedIndex() == 1){
                mostraHeuristicas();            
            }
        }
    }//GEN-LAST:event_jComboBoxVerDadosPrincipaisItemStateChanged

    private void jFormattedTextFieldNomeEtapaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldNomeEtapaKeyTyped
        editor.setTitleTabbedPane(jFormattedTextFieldNomeEtapa.getText(), this);
    }//GEN-LAST:event_jFormattedTextFieldNomeEtapaKeyTyped

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this, etapa.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed

    private void jFormattedTextFieldNomeEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldNomeEtapaActionPerformed
        verificaAtualizacaoNomeEtapa();
    }//GEN-LAST:event_jFormattedTextFieldNomeEtapaActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAbrirHeuristica;
    private javax.swing.JButton jButtonAbrirRegiao;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonDescerRegiao;
    private javax.swing.JButton jButtonDetalhesFuncao;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JButton jButtonExcluirHeuristica;
    private javax.swing.JButton jButtonExcluirRegiao;
    private javax.swing.JButton jButtonNovaAnotacao;
    private javax.swing.JButton jButtonNovaHeuristica;
    private javax.swing.JButton jButtonNovaRegiao;
    private javax.swing.JButton jButtonSubirRegiao;
    private javax.swing.JComboBox jComboBoxVerDadosPrincipais;
    private javax.swing.JComboBox jComboBoxVerHeuristicasEtapa;
    private javax.swing.JFormattedTextField jFormattedTextFieldNomeEtapa;
    private javax.swing.JFormattedTextField jFormattedTextFieldVersao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBispo;
    private javax.swing.JLabel jLabelCavalo;
    private javax.swing.JLabel jLabelDama;
    private javax.swing.JLabel jLabelPeao;
    private javax.swing.JLabel jLabelRegiaoTodoTabuleiro;
    private javax.swing.JLabel jLabelRei;
    private javax.swing.JLabel jLabelTituloListaAnotacoesEtapa;
    private javax.swing.JLabel jLabelTorre;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JList jListRegioes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelBispo;
    private javax.swing.JPanel jPanelBotaoDescricaoFuncao;
    private javax.swing.JPanel jPanelBotoesHeuristica;
    private javax.swing.JPanel jPanelBotoesRegiaoBaixo;
    private javax.swing.JPanel jPanelBotoesRegiaoMovimenta;
    private javax.swing.JPanel jPanelCavalo;
    private javax.swing.JPanel jPanelDadosPrincipais;
    private javax.swing.JPanel jPanelDama;
    private javax.swing.JPanel jPanelDescricaoFuncao;
    private javax.swing.JPanel jPanelExplicacaoInicial;
    private javax.swing.JPanel jPanelFuncoesBasicas;
    private javax.swing.JPanel jPanelHeuristicasEtapa;
    private javax.swing.JPanel jPanelLadoDireito;
    private javax.swing.JPanel jPanelListRegioes;
    private javax.swing.JPanel jPanelPeao;
    private javax.swing.JPanel jPanelPecas;
    private javax.swing.JPanel jPanelRegioes;
    private javax.swing.JPanel jPanelRei;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JPanel jPanelTorre;
    private javax.swing.JPanel jPanelTreeHeuristicas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSpinner jSpinnerValorBispo;
    private javax.swing.JSpinner jSpinnerValorCavalo;
    private javax.swing.JSpinner jSpinnerValorDama;
    private javax.swing.JSpinner jSpinnerValorPeao;
    private javax.swing.JSpinner jSpinnerValorTorre;
    private javax.swing.JSplitPane jSplitPaneFuncoesBasicas;
    private javax.swing.JTabbedPane jTabbedPaneHeuristicasRegioes;
    private javax.swing.JTextArea jTextAreaDescricaoFuncao;
    private javax.swing.JTextArea jTextAreaExplicacoesIniciais;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldValorRei;
    private javax.swing.JTree jTreeFuncoesBasicas;
    private javax.swing.JTree jTreeHeuristicasPorTipo;
    // End of variables declaration//GEN-END:variables
}
