package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.Anotacoes;
import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.persistencia.*;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.AcessoTelaParametrosPartida;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaParametrosPartida;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaPartidaXadrez;
import br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas.ArestaHeuristicaTransicaoEtapa;
import br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas.EditorMudancaEtapas;
import br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas.VerticeEtapa;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.heuchess.telas.iniciais.TelaPrincipal;
import br.ufpr.inf.heuchess.telas.situacaojogo.TelaAvaliaSituacaoJogo;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 29 de Junho de 2006, 16:24
 */
public class TelaEditorConjuntoHeuristico extends javax.swing.JFrame implements AcessoTelaAnotacao, AcessoTelaUsuario, AcessoTelaParametrosPartida {
           
    protected ConjuntoHeuristico conjuntoHeuristico;            
    
    private boolean podeAlterar;
    private boolean podeAnotar;
    private boolean podeUtilizar;
    
    private boolean recebendoFocoTreeComponentes;
    private boolean abriuLeituraPorCausaPartida;
    
    private   DefaultMutableTreeNode treeComponentesHeuristicos;    
    protected EditorMudancaEtapas    editorMudancaEtapas;    
    protected ColorList              colorList;  
    
    private MonitoraFocusNomeConjuntoHeuristico monitoraFocusNomeConjuntoHeuristico;
            
    private class MonitoraFocusNomeConjuntoHeuristico extends FocusAdapter {

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            verificaAtualizacaoNomeConjuntoHeuristico();
        }
    }
    
    public TelaEditorConjuntoHeuristico(final ModalFrameHierarchy acessoTelaEditorConjunto, ConjuntoHeuristico conjuntoHeuristico) {
        
        setEnabled(false);
        
        this.conjuntoHeuristico = conjuntoHeuristico;

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Pode editar os dados de um Conjunto Heurístico caso seja Administrador, o próprio autor,          //
        // o coordenador de uma turma dele, ou um companheiro de turma de acordo com as permissões da turma. //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        
        if ((HeuChess.usuario.getId()   == conjuntoHeuristico.getIdAutor()) ||
            (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR)){
            
            podeAlterar  = true;
            podeAnotar   = true;
            podeUtilizar = true;
            
        }else {            
            try {                
                
                if (UsuarioDAO.coordenoTurma(HeuChess.usuario,conjuntoHeuristico.getIdAutor()) != -1){                
                    podeAlterar  = true;
                    podeAnotar   = true;
                    podeUtilizar = true;                
                }else{
                    
                    ArrayList<Integer> permissoes = TurmaDAO.listaPermissoes(HeuChess.usuario,conjuntoHeuristico.getIdAutor());
                    
                    for (Integer inteiro : permissoes){
                        if (Permissao.ALTERAR.existe(inteiro.intValue())){
                            podeAlterar = true;
                            break;
                        }
                    }
                    
                    for (Integer inteiro : permissoes){
                        if (Permissao.ANOTAR.existe(inteiro.intValue())){
                            podeAnotar = true;
                            break;
                        }
                    }    
                    
                    for (Integer inteiro : permissoes){
                        if (Permissao.UTILIZAR.existe(inteiro.intValue())){
                            podeUtilizar = true;
                            break;
                        }
                    }
                }
                
            } catch (Exception e) {
                
                if (acessoTelaEditorConjunto instanceof TelaPrincipal){
                    acessoTelaEditorConjunto.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                    
                HeuChess.desfazTransacao(e);
                
                UtilsGUI.dialogoErro(acessoTelaEditorConjunto.getFrame(), "Erro ao carregar dados do permissões no Banco de Dados!");
                dispose();
                return;
            }
        }
            
        ///////////////////////////////////////////////////////////
        // Verifica se o Conjunto Heurístico está em uma Partida //
        ///////////////////////////////////////////////////////////

        if (podeAlterar) {
            
            for (TelaPartidaXadrez obj : HeuChess.telaPrincipal.telasPartidaXadrez) {

                final TelaPartidaXadrez telaPartida = obj;

                if ((telaPartida.getJogadorBrancas() != null && conjuntoHeuristico.getId() == telaPartida.getJogadorBrancas().getId()) || 
                    (telaPartida.getJogadorPretas()  != null && conjuntoHeuristico.getId() == telaPartida.getJogadorPretas().getId())) {
                    
                    int resposta = UtilsGUI.dialogoConfirmacao(this, "O Conjunto Heurístico \"" + conjuntoHeuristico.getNome() + "\"\n" +
                                         "está sendo usado em uma partida automática e não pode ser aberto para edição no momento!\n\n" +
                                         "Deseja abri-lo somente para leitura" + (podeAnotar ? " e anotação?" : "?"),
                                         "Confirmação de Abertura Somente Leitura"); 
                    
                    if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                        
                        dispose();                        
                        return;
                        
                    }else{
                        podeAlterar = false;                       
                        abriuLeituraPorCausaPartida = true;
                    }
                    
                    break;
                }
            }
        }
        
        //////////////////////////////
        // Cria Elementos Interface //
        //////////////////////////////
        
        colorList           = new ColorList();
        editorMudancaEtapas = new EditorMudancaEtapas(conjuntoHeuristico);
        
        // Ordena as etapas antes de inseri-las na Interface //
        
        Collections.sort(conjuntoHeuristico.getEtapas());
        
        initComponents();
        
        try {
            jTextFieldNomeAutor.setText(UtilsString.formataCaixaAltaBaixa(UsuarioDAO.buscaNomeUsuario(conjuntoHeuristico.getIdAutor())));
            
        } catch (Exception e) {
            
            if (acessoTelaEditorConjunto instanceof TelaPrincipal){
                acessoTelaEditorConjunto.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            HeuChess.desfazTransacao(e);
            
            UtilsGUI.dialogoErro(acessoTelaEditorConjunto.getFrame(), "Erro ao recuperar nome do Autor do Conjunto Heurístico no Banco de Dados!");
            dispose();
            return;
        }
        
        //////////////////////////////////////////////
        // Define tratamento para Clique em células //
        //////////////////////////////////////////////
        
        editorMudancaEtapas.getGraph().addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {                
                
               if (e.getClickCount() == 2) {                    
                   
                    Object cell = editorMudancaEtapas.getGraph().getFirstCellForLocation(e.getX(),e.getY());
                    
                    if (cell != null) {
                        
                        if (cell instanceof VerticeEtapa){
                            
                            Etapa etapa = ((VerticeEtapa) cell).getEtapa();                            
                            
                            selecionaPanelEtapa(etapa);                             
                        }else
                            if (cell instanceof ArestaHeuristicaTransicaoEtapa){
                                
                                 ArestaHeuristicaTransicaoEtapa aresta = (ArestaHeuristicaTransicaoEtapa) cell;
                                 
                                 selecionaPanelEtapa(aresta.getEtapa());                             
                                 
                                 PanelEtapa panel = localizaEtapa(aresta.getEtapa());
                                 
                                 if (panel != null){
                                    panel.abrirHeuristica(aresta.getHeuristica());
                                 }
                            }
                    }
                }
            }
        });
                
        ///////////////////////////////////////////////
        // Define tratamento para seleção de Células //
        ///////////////////////////////////////////////
        
        GraphSelectionListener gsl = new GraphSelectionListener(){
            @Override
            public void valueChanged(GraphSelectionEvent gse){
                
                Object cell = gse.getCell();                
                
                if (cell instanceof VerticeEtapa && gse.isAddedCell(cell)){
                    jButtonExcluirEtapa.setEnabled(true);                    
                }else{
                    jButtonExcluirEtapa.setEnabled(false);
                }
                
                if (cell instanceof ArestaHeuristicaTransicaoEtapa && gse.isAddedCell(cell)){
                    jButtonExcluirTransicao.setEnabled(true);                    
                }else{
                    jButtonExcluirTransicao.setEnabled(false);
                }
            }
        };
        editorMudancaEtapas.getGraph().addGraphSelectionListener(gsl);        
        
        atualizaNilveComplexidadeCompleto();        
        Anotacoes.atualizaQuantidadeAnotacoes(this);
        
        //////////////////////
        // Inclui as Etapas //
        //////////////////////
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()) {
            jTabbedPanePrincipal.add(formataNomeTab(etapa.getNome()), new PanelEtapa(this, etapa));
        }
        
        if (!podeAlterar){
            jTextFieldNomeConjuntoHeuristico.setEditable(false);
            
            jSliderNivelComplexidade.setEnabled(false);
            jComboBoxEtapaInicial.setEnabled(false);
            
            jButtonCriarEtapa.setVisible(false);
            jButtonCriarHeuristica.setVisible(false);
            jButtonCriarRegiao.setVisible(false);        
            
            jButtonNovaEtapa.setVisible(false);
            jButtonExcluirAnotacao.setVisible(false);
            jButtonExcluirEtapa.setVisible(false);
            jButtonExcluirTransicao.setVisible(false);
            jButtonExcluirComponente.setVisible(false);
            
            jMenuItemCriarEtapa.setVisible(false);
            jMenuItemCriarHeuristica.setVisible(false);
            jMenuItemCriarRegiao.setVisible(false);
            jSeparatorAnotacao.setVisible(false);
            
        }else{
            monitoraFocusNomeConjuntoHeuristico = new MonitoraFocusNomeConjuntoHeuristico();
            jTextFieldNomeConjuntoHeuristico.addFocusListener(monitoraFocusNomeConjuntoHeuristico);
        }
        
        if (!podeAnotar){
            jButtonCriarAnotacao.setVisible(false);
            jSeparatorButtonsCriar.setVisible(false);
            jMenuCriar.setVisible(false);
            jMenuItemCriarAnotacao.setVisible(false);
            jButtonNovaAnotacao.setVisible(false);            
            
            if (conjuntoHeuristico.getAnotacoes().isEmpty()) {
                jButtonAbrirAnotacao.setVisible(false);
            }
        }
        
        if (!podeUtilizar){
            jButtonDesafiarHeuristica.setVisible(false);
            jButtonTestarHeuristica.setVisible(false);
            jSeparatorButtonsUtilizar.setVisible(false);
        }
        
        //////////////////////
        // Minimiza Janelas //
        //////////////////////
        
        if (acessoTelaEditorConjunto instanceof TelaPrincipal){
            acessoTelaEditorConjunto.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        HeuChess.telaPrincipal.minimizaTodasJanelas();        
        HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.add(this);
        
        ///////////////////
        // Mostra Janela //
        ///////////////////
        
        setVisible(true);
        setEnabled(true);
        toFront();
        
        jTextFieldNomeConjuntoHeuristico.requestFocus();        
    }
    
    public long idConjuntoHeuristico(){
        return conjuntoHeuristico.getId();
    }
    
    @Override
    public boolean podeAlterar(){
        return podeAlterar;
    }
    
    public boolean podeAnotar(){
        return podeAnotar;
    }
    
    public boolean abriuLeituraPorCausaPartida(){
        return abriuLeituraPorCausaPartida;
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return null;
    }
       
    private void atualizaInterfaceNivelComplexidade(){
        
        Tipo complexidade = conjuntoHeuristico.getTipo();
        
        if (complexidade == ConjuntoHeuristico.NIVEL_1_INICIANTE){
            
            jMenuItemCriarEtapa.setVisible(false);
            jMenuItemCriarHeuristica.setVisible(false);
            jMenuItemCriarRegiao.setVisible(false);
                         
            jButtonCriarEtapa.setVisible(false);
            jButtonCriarHeuristica.setVisible(false);
            jButtonCriarRegiao.setVisible(false);        
                         
            jTabbedPanePrincipal.remove(jPanelMudancaEtapas);   
            jTabbedPanePrincipal.remove(jPanelTodosComponentes);         
            
        }else
            if (complexidade == ConjuntoHeuristico.NIVEL_2_BASICO ||
                complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO){
            
                jMenuItemCriarEtapa.setVisible(false);
                jMenuItemCriarHeuristica.setVisible(true);
                jMenuItemCriarRegiao.setVisible(true);
                         
                jButtonCriarEtapa.setVisible(false);
                jButtonCriarHeuristica.setVisible(true);
                jButtonCriarRegiao.setVisible(true);        
                         
                jTabbedPanePrincipal.remove(jPanelMudancaEtapas);   
                jTabbedPanePrincipal.remove(jPanelTodosComponentes); 
                
            }else{            
                
                jMenuItemCriarEtapa.setVisible(true);
                jMenuItemCriarHeuristica.setVisible(true);
                jMenuItemCriarRegiao.setVisible(true);
                         
                jButtonCriarEtapa.setVisible(true);
                jButtonCriarHeuristica.setVisible(true);
                jButtonCriarRegiao.setVisible(true);        
                        
                jTabbedPanePrincipal.remove(jPanelMudancaEtapas);                    
                jTabbedPanePrincipal.remove(jPanelTodosComponentes);     
                
                jTabbedPanePrincipal.insertTab("Mudança entre Etapas",null, jPanelMudancaEtapas,   null,1);
                jTabbedPanePrincipal.insertTab("Todos Componentes",   null, jPanelTodosComponentes,null,2);  
            }
        
        jLabelNomeNivel.setToolTipText(conjuntoHeuristico.getTipo().getDescricao());
        jLabelNomeNivel.setText(conjuntoHeuristico.getTipo().getNome());
    }
    
    private void atualizaNilveComplexidadeCompleto(){
        
        atualizaInterfaceNivelComplexidade();
        
        for (int x = 0; x < jTabbedPanePrincipal.getComponentCount(); x++){
            
            Component c = jTabbedPanePrincipal.getComponentAt(x);
            
            if (c instanceof PanelEtapa){
                ((PanelEtapa) c).atualizaInterfaceNivelComplexidade();
            }
        }
    }
    
    private void atualizaTituloEditor(){
        
        String texto = jTextFieldNomeConjuntoHeuristico.getText();
        
        if (texto != null){
            
            if (texto.length() > 50){
                texto = texto.substring(0,49);
                texto += "...";
            }
            
            setTitle("HeuChess - Editor de Conjunto Heurístico - " + texto);            
        }
    }
    
    @Override
    public void atualizaVersaoDataUltimaModificacao(){
        jTextFieldVersao.setText(String.valueOf(conjuntoHeuristico.getVersao()));
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(conjuntoHeuristico.getDataUltimaModificacao()));             
    }
        
    private String formataNomeTab(String texto){        
        
        if (texto != null){
            
            texto = UtilsString.formataCaixaAltaBaixa(texto);
            
            if (texto.length() > 15){
                texto = texto.substring(0,14);
                texto += "...";
            }
            return "Etapa - " + texto;
        }else{
            return "Etapa - ";
        }
    }
    
    public void setTitleTabbedPane(String texto, Component componente){
        
        int indice = jTabbedPanePrincipal.indexOfComponent(componente);
        if (indice != -1){            
            jTabbedPanePrincipal.setTitleAt(indice, formataNomeTab(texto));
        }
    }
    
    public PanelEtapa localizaEtapa(Etapa etapa){
        
        for (int x = 0; x < jTabbedPanePrincipal.getComponentCount(); x++){
            
            Component c = jTabbedPanePrincipal.getComponentAt(x);
            
            if (c instanceof PanelEtapa){
                
                if (((PanelEtapa) c).etapa == etapa){
                    return (PanelEtapa) c;
                }
            }
        }
        
        return null;
    }
    
    public void selecionaPanelEtapa(Etapa etapa){
        
         for (int x = 0; x < jTabbedPanePrincipal.getComponentCount(); x++){
             
             Component c = jTabbedPanePrincipal.getComponentAt(x);
             
             if (c instanceof PanelEtapa){
                 
                if (((PanelEtapa) c).etapa == etapa){          
                    
                    jTabbedPanePrincipal.setSelectedIndex(x);
                    return;
                }
             }
         }        
    }
    
    public synchronized void fechar(boolean fechouPorErro){
        
        if (fechouPorErro || verificaAtualizacaoTodosNomes()){
            
            SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dispose();            
                        HeuChess.telaPrincipal.desvincultarTrazerOutraTelaFrente(TelaEditorConjuntoHeuristico.this);
                    }
            });
        }
    }
    
    private void novaEtapa(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 TelaNovaEtapa tela = new TelaNovaEtapa(TelaEditorConjuntoHeuristico.this);
            }
        });
    }
    
    private void novaHeuristica(){        
        
        if (conjuntoHeuristico.getEtapas().size() == 1){
            
            final PanelEtapa panelEtapa = localizaEtapa(conjuntoHeuristico.getEtapas().get(0));
            
            jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
            
            panelEtapa.novaHeuristica();
                        
        }else{
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheEtapa tela = new TelaEscolheEtapa(TelaEditorConjuntoHeuristico.this,Heuristica.class);
                }
            });
        }
    }
    
    private void novaRegiao(){
        
        if (conjuntoHeuristico.getEtapas().size() == 1){
            
            final PanelEtapa panelEtapa = localizaEtapa(conjuntoHeuristico.getEtapas().get(0));
            
            jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
            
            panelEtapa.novaRegiao();            
            
        }else{
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheEtapa tela = new TelaEscolheEtapa(TelaEditorConjuntoHeuristico.this, Regiao.class);  
                }
            });
        }
    }
    
    private void novaAnotacao(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaEscolheComponenteHeuristico tela = new TelaEscolheComponenteHeuristico(TelaEditorConjuntoHeuristico.this); 
            }
        });
    }
    
    private void novaPartida(){
        
        if (!verificaAtualizacaoTodosNomes()){
            return;
        }
        
        if (!HeuChess.telaPrincipal.podeCriarNovaPartidaSimultanea(this)){
            return;
        } 
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaParametrosPartida tela = new TelaParametrosPartida(TelaEditorConjuntoHeuristico.this,
                                                                       ConjuntoHeuristico.class,
                                                                       conjuntoHeuristico.getId());
            }
        });
    }
    
    public DefaultMutableTreeNode criaTreeComponentesHeuristicos(boolean anotacoes){
        
        DefaultMutableTreeNode treeComponentes = new DefaultMutableTreeNode(conjuntoHeuristico);
        
        DefaultMutableTreeNode treeEtapas = new DefaultMutableTreeNode("Etapas");
        treeComponentes.add(treeEtapas);        
        
        if (anotacoes){
            incluirAnotacoes(treeComponentes);   
        }
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            DefaultMutableTreeNode treeEtapa = new DefaultMutableTreeNode(etapa);
            
            if (anotacoes){
                incluirAnotacoes(treeEtapa);        
            }    
            
            treeEtapas.add(treeEtapa);
            
            if (etapa.getHeuristicasTransicaoEtapa().size() > 0){
                
                DefaultMutableTreeNode treeHeuristicasTransicaoEtapa = new DefaultMutableTreeNode("Heurísticas de Transição de Etapa");
                treeEtapa.add(treeHeuristicasTransicaoEtapa);
                
                for (HeuristicaTransicaoEtapa heuristicaTransicao : etapa.getHeuristicasTransicaoEtapa()){
                    
                    DefaultMutableTreeNode treeHeuristica = new DefaultMutableTreeNode(heuristicaTransicao);
                    treeHeuristicasTransicaoEtapa.add(treeHeuristica);                             
                    
                    if (anotacoes){
                        incluirAnotacoes(treeHeuristica);
                    }
                }
            }
            
            if (etapa.getHeuristicasValorPeca().size() > 0){
                
                DefaultMutableTreeNode treeHeuristicasValorPeca = new DefaultMutableTreeNode("Heurísticas de Valor de Peça");
                
                treeEtapa.add(treeHeuristicasValorPeca);
                
                for (HeuristicaValorPeca heuristicaValorPeca : etapa.getHeuristicasValorPeca()){
                    
                    DefaultMutableTreeNode treeHeuristica = new DefaultMutableTreeNode(heuristicaValorPeca);
                    treeHeuristicasValorPeca.add(treeHeuristica);                             
                    
                    if (anotacoes){
                        incluirAnotacoes(treeHeuristica);
                    }
                }
            }
            
            if (etapa.getHeuristicasValorTabuleiro().size() > 0){
                
                DefaultMutableTreeNode treeHeuristicasValorTabuleiro = new DefaultMutableTreeNode("Heurísticas de Valor de Tabuleiro");
                
                treeEtapa.add(treeHeuristicasValorTabuleiro);
                
                for (HeuristicaValorTabuleiro heuristicaValorTabuleiro : etapa.getHeuristicasValorTabuleiro()){
                    
                    DefaultMutableTreeNode treeHeuristica = new DefaultMutableTreeNode(heuristicaValorTabuleiro);
                    treeHeuristicasValorTabuleiro.add(treeHeuristica);                             
                    
                    if (anotacoes){
                        incluirAnotacoes(treeHeuristica);
                    }
                }
            }
            
            if (etapa.getRegioes().size() > 0){
                
                DefaultMutableTreeNode treeRegioes = new DefaultMutableTreeNode("Regiões");            
                
                treeEtapa.add(treeRegioes);
                
                for (Regiao regiao : etapa.getRegioes()){
                    
                    DefaultMutableTreeNode treeRegiao = new DefaultMutableTreeNode(regiao);
                    treeRegioes.add(treeRegiao);                 
                    
                    if (anotacoes){
                        incluirAnotacoes(treeRegiao);
                    }
                }
            }
        }        
        
        return treeComponentes;
    }
    
    private void incluirAnotacoes(DefaultMutableTreeNode node){
        
         Componente componente = (Componente) node.getUserObject();
         
         if (componente.getAnotacoes().size() > 0){
             
            DefaultMutableTreeNode treeAnotacoes = new DefaultMutableTreeNode("Anotações");            
            node.add(treeAnotacoes);
            
            for (Anotacao anotacao : componente.getAnotacoes()){
                
                treeAnotacoes.add(new DefaultMutableTreeNode(anotacao));                                   
            }
         }
    }  
   
    public void incluiNovaEtapa(Etapa etapa){
        
        conjuntoHeuristico.getEtapas().add(etapa);        
                
        editorMudancaEtapas.adicionaEtapa(etapa);
        
        PanelEtapa novoPanelEtapa = new PanelEtapa(this,etapa);
        novoPanelEtapa.mostraDadosPrincipais();
    
        Collections.sort(conjuntoHeuristico.getEtapas());
        
        int indice = conjuntoHeuristico.getEtapas().indexOf(etapa); 
                
        jComboBoxEtapaInicial.insertItemAt(etapa, indice);
                
        jTabbedPanePrincipal.insertTab(formataNomeTab(etapa.getNome()), null, novoPanelEtapa, null, indice + 3);
        // O +3 é por causa das abas já inseridas padrões (Informações Gerais, Mudança entre Etapas, e Todos os Componentes).
        
        jTabbedPanePrincipal.setSelectedComponent(novoPanelEtapa);
    }
        
    private void abrindoComponente(final Object nodeInfo){      
        
        if (nodeInfo != null){                                                 
            
            if (nodeInfo instanceof Etapa){
                
                PanelEtapa panelEtapa = localizaEtapa((Etapa)nodeInfo);
                jTabbedPanePrincipal.setSelectedComponent(panelEtapa);                    
                return;
                
            }else
                if (nodeInfo instanceof Heuristica){
                    
                    for (Etapa etapa : conjuntoHeuristico.getEtapas()){
                        
                         if ((etapa.getHeuristicasTransicaoEtapa().indexOf(nodeInfo) != -1) ||
                             (etapa.getHeuristicasValorPeca().indexOf(nodeInfo)      != -1) ||    
                             (etapa.getHeuristicasValorTabuleiro().indexOf(nodeInfo) != -1)){
                             
                             PanelEtapa panelEtapa = localizaEtapa(etapa);
                             panelEtapa.abrirHeuristica((Heuristica) nodeInfo);
                             
                             recebendoFocoTreeComponentes = true;
                             return;
                         }
                    }
                }else
                    if (nodeInfo instanceof Regiao){
                        
                        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
                            
                             if (etapa.getRegioes().indexOf(nodeInfo) != -1){
                                 
                                 PanelEtapa panelEtapa = localizaEtapa(etapa);
                                 panelEtapa.abrirRegiao((Regiao)nodeInfo);
                                 
                                 recebendoFocoTreeComponentes = true;
                                 return;
                             }
                        }
                    }else
                        if (nodeInfo instanceof Anotacao){
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    TelaAnotacao tela = new TelaAnotacao(TelaEditorConjuntoHeuristico.this, (Anotacao) nodeInfo);
                                }
                            });
                            
                            recebendoFocoTreeComponentes = true;
                            return;
                        }
        }
        
        jButtonAbrirComponente.setEnabled(false);
        jButtonExcluirComponente.setEnabled(false);
    }
    
    private boolean possuiEtapas(int novoValor){
        
        if (conjuntoHeuristico.getEtapas().size() > 1){
            
            UtilsGUI.dialogoErro(this, "Não é possível diminuir o Nível de Complexidade para " + novoValor + "\n" + 
                                       "pois o Conjunto Heurístico possui mais de uma Etapa!");            
            
            jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
            return true;
        }else{
            return false;
        }
    }
    
    private boolean possuiRegioes(int novoValor){
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            if (etapa.getRegioes().size() > 0){
                
                UtilsGUI.dialogoErro(this, "Não é possível diminuir o Nível de Complexidade para " + novoValor + "\n" + 
                                           "pois o Conjunto Heurístico possui Regiões definidas!");                
                
                jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
                return true;
            }
        }
        
        return false;
    }
    
    private boolean possuiHeuristicasTransicaoEtapas(int novoValor){
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            if (etapa.getHeuristicasTransicaoEtapa().size() > 0){
                
                UtilsGUI.dialogoErro(this, "Não é possível diminuir o Nível de Complexidade para " + novoValor + "\n" +
                                           "pois o Conjunto Heurístico possui Heurísticas de Transição de Etapas!");                
                
                jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
                return true;
            }
        }
        
        return false;
    }    
    
    private boolean possuiHeuristicasValorTabuleiro(int novoValor){
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            if (etapa.getHeuristicasValorTabuleiro().size() > 0){
                
                UtilsGUI.dialogoErro(this, "Não é possível diminuir o Nível de Complexidade para " + novoValor + "\n" +
                                           "pois o Conjunto Heurístico possui Heurísticas de Valor de Tabuleiro!");                
                
                jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
                return true;
            }
        }
        
        return false;
    }    
    
    private boolean possuiHeuristicasValorPeca(int novoValor){
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            if (etapa.getHeuristicasValorPeca().size() > 0){
                
                UtilsGUI.dialogoErro(this, "Não é possível diminuir o Nível de Complexidade para " + novoValor + "\n" +
                                           "pois o Conjunto Heurístico possui Heurísticas de Valor de Peça!");                
                
                jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
                return true;
            }
        }
        
        return false;
    }
    
    public void constroiTreeComponentesHeuristicos(){
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelTodosComponentes){
            
            treeComponentesHeuristicos = criaTreeComponentesHeuristicos(true);            
            jTreeComponentesHeuristicos.setModel(new DefaultTreeModel(treeComponentesHeuristicos));
            
            UtilsTree.expandAll(jTreeComponentesHeuristicos,true);
        }
    }
   
    private void apagaHeuristicasComOcorrencia(Etapa etapa, ArrayList<? extends Heuristica> arrayList, String palavra) throws Exception {
        
        for (int y = 0; y < arrayList.size(); y++) {

            Heuristica heuristica = arrayList.get(y);
            boolean achou         = false;

            if (UtilsString.procuraPalavra(palavra, heuristica.getCondicaoDB(),DHJOG.DELIMITADORES) ||
                UtilsString.procuraPalavra(palavra, heuristica.getAcoesDB(),   DHJOG.DELIMITADORES)){
                achou = true;
            }
            
            if (achou) {
                PanelEtapa panelEtapa = localizaEtapa(etapa);
                panelEtapa.apagaHeuristica(heuristica);                
                --y;
            }
        }
    }
    
    public void apagaHeuristicasComOcorrencia(String palavra) throws Exception {
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()) {

            apagaHeuristicasComOcorrencia(etapa, etapa.getHeuristicasTransicaoEtapa(),palavra);
            apagaHeuristicasComOcorrencia(etapa, etapa.getHeuristicasValorPeca(),     palavra);
            apagaHeuristicasComOcorrencia(etapa, etapa.getHeuristicasValorTabuleiro(),palavra);            
        }
    }
    
    private boolean confirmaApagarEtapa(Etapa etapa) {

        if (conjuntoHeuristico.getEtapas().size() <= 1) {

            UtilsGUI.dialogoErro(this, "Um Conjunto Heurístico precisa ter obrigatoriamente pelo menos uma Etapa!\n" +
                                       "Operação Cancelada.");
        } else {

            int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente Apagar a Etapa\n" +
                                                             "\"" + etapa + "\"?",
                                                             "Confirmação de Exclusão");
            if (resposta == JOptionPane.YES_OPTION) {

                /////////////////////////////////////////////////
                // Procura Heurísticas de Transição de Etapas //
                ///////////////////////////////////////////////// 

                int quantidade = 0;

                for (Etapa etapaAtual : conjuntoHeuristico.getEtapas()) {

                    quantidade += etapaAtual.quantidadeHeuristicasQueUsam(etapa);
                }

                if (quantidade > 0) {

                    String mensagem;

                    if (quantidade == 1) {
                        mensagem = "Existe 1 Heurística de Transição de Etapa que aponta para a Etapa que está sendo apagada.\n" +
                                   "Caso você realmente apague a etapa \"" + etapa + "\" esta heurística também será apagada!\n\n" +
                                   "Deseja continuar a apagar a Etapa?";
                    } else {
                        mensagem = "Existem " + quantidade + 
                                   " Heurísticas de Transição de Etapa que apontam para a Etapa que está sendo apagada.\n" +
                                   "Caso você realmente apague a etapa \"" + etapa + "\" estas heurísticas também serão apagadas!\n\n" +
                                   "Deseja continuar a apagar a Etapa?";
                    }
                    
                    int respostaApagarHeuristicas = UtilsGUI.dialogoConfirmacao(this, mensagem, "Confirmação de Exclusão");
                    
                    if (respostaApagarHeuristicas == JOptionPane.NO_OPTION || resposta == -1) {
                        return false;
                    }
                }

                boolean eraEtapaInicial = false;
                
                try {
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    EtapaDAO.apaga(etapa, conjuntoHeuristico);

                    //////////////////////////////////////////////
                    // Apaga Heurísticas de Transição de Etapas //
                    //////////////////////////////////////////////

                    if (quantidade > 0) {

                        for (Etapa etapaAtual : conjuntoHeuristico.getEtapas()) {
                            apagaHeuristicasComOcorrencia(etapaAtual, etapaAtual.getHeuristicasTransicaoEtapa(), etapa.getNome());
                        }
                    }

                    conjuntoHeuristico.getEtapas().remove(etapa);
                    jComboBoxEtapaInicial.removeItem(etapa);

                    ////////////////////////////
                    // Apagou a etapa Inicial //
                    ////////////////////////////
                    
                    if (conjuntoHeuristico.getEtapaInicial() == etapa) {
                        
                        eraEtapaInicial = true;
                                
                        Etapa novaEtapaInicial = conjuntoHeuristico.getEtapas().get(0);

                        conjuntoHeuristico.setEtapaInicial(novaEtapaInicial);
                        
                        ConjuntoHeuristicoDAO.atualiza(conjuntoHeuristico);        
                        
                        editorMudancaEtapas.defineEtapaInicial(novaEtapaInicial);
                        jComboBoxEtapaInicial.setSelectedItem(novaEtapaInicial);
                    }

                    ConexaoDBHeuChess.commit();
                    
                    PanelEtapa panel = localizaEtapa(etapa);
                    if (panel != null) {
                        jTabbedPanePrincipal.remove(panel);
                    }

                    editorMudancaEtapas.removeEtapa(etapa);

                    if (HeuChess.somAtivado) {
                        HeuChess.somApagar.play();
                    }
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return true;
                    
                } catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    conjuntoHeuristico.getEtapas().add(etapa);
                    jComboBoxEtapaInicial.addItem(etapa);
                    
                    if (eraEtapaInicial){
                        conjuntoHeuristico.setEtapaInicial(etapa);
                        editorMudancaEtapas.defineEtapaInicial(etapa);
                        jComboBoxEtapaInicial.setSelectedItem(etapa);
                    }
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao tentar apagar a Etapa no Banco de Dados." +
                                               "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");                    
                    
                    fechar(true);
                }
            }
        }

        return false;
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
        return conjuntoHeuristico;
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
    
    @Override
    public void fechandoTelaParametrosPartida(final Partida game){
    
        if (game != null) {
       
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPartidaXadrez telaAnalisePartidaXadrez = new TelaPartidaXadrez(TelaEditorConjuntoHeuristico.this, game);
                }
            });
              
        }else{
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
        }
    }
    
    public void fechandoTelaEscolheEtapa(Etapa etapa, Class classe){
        
        if (etapa != null && classe != null){            
            
            if (classe == Heuristica.class){                
                
                final PanelEtapa panelEtapa = localizaEtapa(etapa);
                
                jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
                panelEtapa.novaHeuristica();               
                
            }else
                if (classe == Regiao.class){
                    
                    final PanelEtapa panelEtapa = localizaEtapa(etapa);
                    
                    jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
                    panelEtapa.novaRegiao();                    
                    
                }else{
                    throw new IllegalArgumentException("Tipo desconhecido de componente [" + classe + "]");
                }
        }
    }
       
    public void fechandoTelaEscolheComponenteHeuristico(Object nodeInfo){
        
        if (nodeInfo != null){             
            
            if (nodeInfo instanceof ConjuntoHeuristico){                
                
                jTabbedPanePrincipal.setSelectedComponent(jPanelInformacoesGerais);        
                Anotacoes.novaAnotacao(this);
                
            }else                    
               if (nodeInfo instanceof Etapa){
                   
                    PanelEtapa panelEtapa = localizaEtapa((Etapa)nodeInfo);
                    
                    jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
                    panelEtapa.adicionaNovaAnotacao();
                    
               }else
                   if (nodeInfo instanceof Heuristica){
                       
                       for (Etapa etapa : conjuntoHeuristico.getEtapas()){
                           
                            if ((etapa.getHeuristicasTransicaoEtapa().indexOf(nodeInfo) != -1) ||
                                (etapa.getHeuristicasValorPeca().indexOf(nodeInfo)      != -1) ||    
                                (etapa.getHeuristicasValorTabuleiro().indexOf(nodeInfo) != -1)){                                
                                
                                PanelEtapa panelEtapa = localizaEtapa(etapa);
                                
                                jTabbedPanePrincipal.setSelectedComponent(panelEtapa);
                                panelEtapa.adicionaNovaAnotacaoHeuristica((Heuristica) nodeInfo);
                                return;
                            }
                       }
                   }else
                       if (nodeInfo instanceof Regiao){
                           
                            for (Etapa etapa : conjuntoHeuristico.getEtapas()){
                                
                                if (etapa.getRegioes().indexOf(nodeInfo) != -1){
                                    
                                    PanelEtapa panelEtapa = localizaEtapa(etapa);
                                    
                                    jTabbedPanePrincipal.setSelectedComponent(panelEtapa);                                    
                                    panelEtapa.adicionaNovaAnotacaoRegiao((Regiao) nodeInfo);
                                    return;
                                }
                            }
                       }else
                           if (nodeInfo instanceof Funcao){  
                               // NÃO SUPORTADO AINDA //
                           }            
        }
    }
    
    private boolean verificaAtualizacaoNomeConjuntoHeuristico() {
        
        jTextFieldNomeConjuntoHeuristico.removeFocusListener(monitoraFocusNomeConjuntoHeuristico);
        
        String nome = jTextFieldNomeConjuntoHeuristico.getText();
        nome        = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);
        String erro  = conjuntoHeuristico.validaNomeUnicoComponente(nome);

        String nomeVelho = conjuntoHeuristico.getNome();
        
        boolean ehNomeNovo = !nomeVelho.equalsIgnoreCase(nome);
        boolean sucesso;
        
        if (ehNomeNovo){
        
            if (erro != null) {
            
                jTabbedPanePrincipal.setSelectedComponent(jPanelInformacoesGerais);
                jTextFieldNomeConjuntoHeuristico.requestFocus();
                jTextFieldNomeConjuntoHeuristico.selectAll();
            
                UtilsGUI.dialogoErro(this, erro + "\n\nAlteração será desfeita!");
                        
                jTextFieldNomeConjuntoHeuristico.setText(conjuntoHeuristico.getNome());
                jTextFieldNomeConjuntoHeuristico.requestFocus();
                jTextFieldNomeConjuntoHeuristico.selectAll();            
            
                atualizaTituloEditor();
            
                sucesso = false;
            
            } else {

                try {
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    long idConjuntoMesmoNome = ConjuntoHeuristicoDAO.existeNome(HeuChess.usuario, nome);

                    if (idConjuntoMesmoNome != -1) {

                        jTabbedPanePrincipal.setSelectedComponent(jPanelInformacoesGerais);
                        jTextFieldNomeConjuntoHeuristico.requestFocus();
                        jTextFieldNomeConjuntoHeuristico.selectAll();

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Já existe um Conjunto Heurístico seu com este nome!\n\"" + nome +
                                                   "\"\n\nAlteração será desfeita!");

                        jTextFieldNomeConjuntoHeuristico.setText(conjuntoHeuristico.getNome());
                        jTextFieldNomeConjuntoHeuristico.requestFocus();
                        jTextFieldNomeConjuntoHeuristico.selectAll();

                        atualizaTituloEditor();

                        sucesso = false;

                    } else {

                        conjuntoHeuristico.setNome(nome);

                        ConjuntoHeuristicoDAO.atualiza(conjuntoHeuristico);
        
                        ConexaoDBHeuChess.commit();
        
                        atualizaVersaoDataUltimaModificacao();
                        atualizaTituloEditor();

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                        sucesso = true;
                    }
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);
                   
                    jTabbedPanePrincipal.setSelectedComponent(jPanelInformacoesGerais);
                    jTextFieldNomeConjuntoHeuristico.requestFocus();
                    jTextFieldNomeConjuntoHeuristico.selectAll();
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao atualizar o nome do Conjunto Heurístico no Banco de Dados." +
                                               "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                    
                    conjuntoHeuristico.setNome(nomeVelho);

                    jTabbedPanePrincipal.setSelectedComponent(jPanelInformacoesGerais);
                    jTextFieldNomeConjuntoHeuristico.setText(nomeVelho);
                    jTextFieldNomeConjuntoHeuristico.requestFocus();
                    jTextFieldNomeConjuntoHeuristico.selectAll();

                    sucesso = false;
                    
                    fechar(true);
                }
            }
        }else{
            sucesso = true;
        }
        
        jTextFieldNomeConjuntoHeuristico.addFocusListener(monitoraFocusNomeConjuntoHeuristico);
        return sucesso;
    }
    
    private boolean verificaAtualizacaoTodosNomes(){
        
        if (!podeAlterar){
            return true;
        }
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInformacoesGerais) {
            return verificaAtualizacaoNomeConjuntoHeuristico();
        }else
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelMudancaEtapas){
                // Faz nada //
                return true;
            }else
                if (jTabbedPanePrincipal.getSelectedComponent() == jPanelTodosComponentes){
                    // Faz nada //
                    return true;
                }else{
                    /////////////////////////////////
                    // Aba selecionada é uma etapa //
                    /////////////////////////////////
                    
                    Component c = jTabbedPanePrincipal.getSelectedComponent();
                    
                    if (c instanceof PanelEtapa){
                        return ((PanelEtapa) c).verificaAtualizacaoNomeEtapa();
                    }else{
                        throw new RuntimeException("Era esperada um PanelEtapa");
                    }
                }
    }
    
    private void testarHeuristica(){
        
        if (!verificaAtualizacaoTodosNomes()){
            return;
        }
        
        /*
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaSeletorSimples telaS = new TelaSeletorSimples(HeuChess.instancia, TelaEditorConjuntoHeuristico.this.conjuntoHeuristico);
                telaS.setVisible(true);
            }
        });
        */
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaAvaliaSituacaoJogo tela = new TelaAvaliaSituacaoJogo(TelaEditorConjuntoHeuristico.this,
                                                                         ConjuntoHeuristico.class,
                                                                         conjuntoHeuristico.getId());
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCentral = new javax.swing.JPanel();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelInformacoesGerais = new javax.swing.JPanel();
        jTextFieldDataModificacao = new javax.swing.JTextField();
        jTextFieldDataCriacao = new javax.swing.JTextField();
        jTextFieldVersao = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonAbrirAnotacao = new javax.swing.JButton();
        jButtonNovaAnotacao = new javax.swing.JButton();
        jButtonExcluirAnotacao = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldNomeConjuntoHeuristico = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldNomeAutor = new javax.swing.JTextField();
        jLabelTituloListaAnotacoesCH = new javax.swing.JLabel();
        jButtonDadosAutor = new javax.swing.JButton();
        jLabelTotalAnotacoes = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListAnotacoes = new JList(new ModelListaComponentes(conjuntoHeuristico.getAnotacoes()));
        jPanelMudancaEtapas = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPaneEditorEtapas = new JScrollPane(editorMudancaEtapas.getGraph());
        jPanelBotoesEditorEtapas = new javax.swing.JPanel();
        jButtonNovaEtapa = new javax.swing.JButton();
        jButtonExcluirEtapa = new javax.swing.JButton();
        jButtonExcluirTransicao = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxEtapaInicial = new javax.swing.JComboBox();
        jPanelTodosComponentes = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPaneTodosComponentes = new javax.swing.JScrollPane();
        jTreeComponentesHeuristicos = new JTree(treeComponentesHeuristicos);
        jPanel1 = new javax.swing.JPanel();
        jButtonAbrirComponente = new javax.swing.JButton();
        jButtonExcluirComponente = new javax.swing.JButton();
        jLabelStatus = new javax.swing.JLabel();
        jPanelTopo = new javax.swing.JPanel();
        jToolBarPrincipal = new javax.swing.JToolBar();
        jSeparator7 = new javax.swing.JSeparator();
        jButtonCriarEtapa = new javax.swing.JButton();
        jButtonCriarHeuristica = new javax.swing.JButton();
        jButtonCriarRegiao = new javax.swing.JButton();
        jButtonCriarAnotacao = new javax.swing.JButton();
        jSeparatorButtonsCriar = new javax.swing.JSeparator();
        jButtonTestarHeuristica = new javax.swing.JButton();
        jButtonDesafiarHeuristica = new javax.swing.JButton();
        jSeparatorButtonsUtilizar = new javax.swing.JSeparator();
        jButtonConsultarAjuda = new javax.swing.JButton();
        jSliderNivelComplexidade = new javax.swing.JSlider();
        jLabelNivelComplexidade = new javax.swing.JLabel();
        jLabelNomeNivel = new javax.swing.JLabel();
        jMenuBarPrincipal = new javax.swing.JMenuBar();
        jMenuCriar = new javax.swing.JMenu();
        jMenuItemCriarEtapa = new javax.swing.JMenuItem();
        jMenuItemCriarHeuristica = new javax.swing.JMenuItem();
        jMenuItemCriarRegiao = new javax.swing.JMenuItem();
        jSeparatorAnotacao = new javax.swing.JSeparator();
        jMenuItemCriarAnotacao = new javax.swing.JMenuItem();
        jMenuEditar = new javax.swing.JMenu();
        jMenuItemRecortar = new javax.swing.JMenuItem();
        jMenuItemCopiar = new javax.swing.JMenuItem();
        jMenuItemColar = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItemLocalizar = new javax.swing.JMenuItem();
        jMenuAjuda = new javax.swing.JMenu();
        jMenuItemTopicosAjuda = new javax.swing.JMenuItem();
        jMenuItemPesquisarAjuda = new javax.swing.JMenuItem();
        jMenuItemSobreJanelaAtual = new javax.swing.JMenuItem();
        jMenuItemDicasRapidas = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemPaginaInternetProjeto = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemInformacoesSobreSistema = new javax.swing.JMenuItem();
        jMenuSair = new javax.swing.JMenu();
        jMenuItemFechaEditor = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("HeuChess - Editor de Conjunto Heurístico - " + UtilsString.formataCaixaAltaBaixa(conjuntoHeuristico.getNome()));
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_conjunto_heuristico.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });

        jPanelCentral.setLayout(new java.awt.CardLayout());

        jTabbedPanePrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanePrincipalStateChanged(evt);
            }
        });

        jTextFieldDataModificacao.setEditable(false);
        jTextFieldDataModificacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataModificacao.setText(UtilsDataTempo.formataData(conjuntoHeuristico.getDataUltimaModificacao()));

        jTextFieldDataCriacao.setEditable(false);
        jTextFieldDataCriacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDataCriacao.setText(UtilsDataTempo.formataData(conjuntoHeuristico.getDataCriacao()));

        jTextFieldVersao.setEditable(false);
        jTextFieldVersao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldVersao.setText(String.valueOf(conjuntoHeuristico.getVersao()));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Modificação");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Criação");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Versão");

        jButtonAbrirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirAnotacao.setText("Abrir");
        jButtonAbrirAnotacao.setEnabled(false);
        jButtonAbrirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirAnotacaoActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonAbrirAnotacao);

        jButtonNovaAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaAnotacao.setText("Adicionar");
        jButtonNovaAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaAnotacaoActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonNovaAnotacao);

        jButtonExcluirAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirAnotacao.setText("Excluir");
        jButtonExcluirAnotacao.setEnabled(false);
        jButtonExcluirAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirAnotacaoActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonExcluirAnotacao);

        jLabel8.setText("Nome do Conjunto Heurístico");

        jTextFieldNomeConjuntoHeuristico.setDocument(new DocumentMasked(DHJOG.CARACTERES_VALIDOS,DocumentMasked.ONLY_CAPITAL));
        jTextFieldNomeConjuntoHeuristico.setText(conjuntoHeuristico.getNome());
        jTextFieldNomeConjuntoHeuristico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNomeConjuntoHeuristicoActionPerformed(evt);
            }
        });
        jTextFieldNomeConjuntoHeuristico.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNomeConjuntoHeuristicoKeyTyped(evt);
            }
        });

        jLabel9.setText("Nome do Autor");

        jTextFieldNomeAutor.setEditable(false);

        jLabelTituloListaAnotacoesCH.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTituloListaAnotacoesCH.setText("Anotações Gerais sobre o Conjunto Heurístico");

        jButtonDadosAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_dados_autor.png"))); // NOI18N
        jButtonDadosAutor.setText("Dados do Autor");
        jButtonDadosAutor.setToolTipText("Mostra mais informações sobre o Autor");
        jButtonDadosAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDadosAutorActionPerformed(evt);
            }
        });

        jLabelTotalAnotacoes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTotalAnotacoes.setText("- Total de 0");

        jListAnotacoes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListAnotacoes.setCellRenderer(new RenderListaAnotacoes());
        jListAnotacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListAnotacoesMouseClicked(evt);
            }
        });
        jListAnotacoes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAnotacoesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jListAnotacoes);

        javax.swing.GroupLayout jPanelInformacoesGeraisLayout = new javax.swing.GroupLayout(jPanelInformacoesGerais);
        jPanelInformacoesGerais.setLayout(jPanelInformacoesGeraisLayout);
        jPanelInformacoesGeraisLayout.setHorizontalGroup(
            jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
            .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInformacoesGeraisLayout.createSequentialGroup()
                        .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldNomeConjuntoHeuristico, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(23, 23, 23)
                                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(22, 22, 22)
                                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jButtonDadosAutor)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                        .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                                .addComponent(jLabelTituloListaAnotacoesCH)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelTotalAnotacoes)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelInformacoesGeraisLayout.setVerticalGroup(
            jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDataModificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                            .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(jLabel8))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextFieldVersao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldNomeConjuntoHeuristico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanelInformacoesGeraisLayout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldDataCriacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(21, 21, 21)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNomeAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDadosAutor))
                .addGap(18, 18, 18)
                .addGroup(jPanelInformacoesGeraisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTituloListaAnotacoesCH)
                    .addComponent(jLabelTotalAnotacoes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Informações Gerais", jPanelInformacoesGerais);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Etapas Definidas que serão Avaliadas Durante uma Partida");

        jScrollPaneEditorEtapas.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jButtonNovaEtapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaEtapa.setText("Adicionar Etapa");
        jButtonNovaEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaEtapaActionPerformed(evt);
            }
        });
        jPanelBotoesEditorEtapas.add(jButtonNovaEtapa);

        jButtonExcluirEtapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirEtapa.setText("Excluir Etapa");
        jButtonExcluirEtapa.setEnabled(false);
        jButtonExcluirEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirEtapaActionPerformed(evt);
            }
        });
        jPanelBotoesEditorEtapas.add(jButtonExcluirEtapa);

        jButtonExcluirTransicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirTransicao.setText("Excluir Transição");
        jButtonExcluirTransicao.setEnabled(false);
        jButtonExcluirTransicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirTransicaoActionPerformed(evt);
            }
        });
        jPanelBotoesEditorEtapas.add(jButtonExcluirTransicao);

        jLabel5.setText("Etapa Inicial");

        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            jComboBoxEtapaInicial.addItem(etapa);
        }
        jComboBoxEtapaInicial.setSelectedItem(conjuntoHeuristico.getEtapaInicial());
        jComboBoxEtapaInicial.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxEtapaInicial.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxEtapaInicialItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelMudancaEtapasLayout = new javax.swing.GroupLayout(jPanelMudancaEtapas);
        jPanelMudancaEtapas.setLayout(jPanelMudancaEtapasLayout);
        jPanelMudancaEtapasLayout.setHorizontalGroup(
            jPanelMudancaEtapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMudancaEtapasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMudancaEtapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneEditorEtapas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addComponent(jPanelBotoesEditorEtapas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMudancaEtapasLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(22, 22, 22)
                        .addComponent(jComboBoxEtapaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelMudancaEtapasLayout.setVerticalGroup(
            jPanelMudancaEtapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMudancaEtapasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(12, 12, 12)
                .addGroup(jPanelMudancaEtapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxEtapaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneEditorEtapas, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesEditorEtapas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Mudança entre Etapas", jPanelMudancaEtapas);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Lista de Todos os Componentes Heurísticos criados");

        jTreeComponentesHeuristicos.setShowsRootHandles(true);
        jTreeComponentesHeuristicos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeComponentesHeuristicos.setCellRenderer(new RenderTreeComponentes());
        jTreeComponentesHeuristicos.setToggleClickCount(1);
        jTreeComponentesHeuristicos.setScrollsOnExpand(true);
        jTreeComponentesHeuristicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeComponentesHeuristicosMouseClicked(evt);
            }
        });
        jTreeComponentesHeuristicos.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeComponentesHeuristicosValueChanged(evt);
            }
        });
        jTreeComponentesHeuristicos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTreeComponentesHeuristicosFocusGained(evt);
            }
        });
        jScrollPaneTodosComponentes.setViewportView(jTreeComponentesHeuristicos);

        jButtonAbrirComponente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirComponente.setMnemonic('a');
        jButtonAbrirComponente.setText("Abrir");
        jButtonAbrirComponente.setEnabled(false);
        jButtonAbrirComponente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirComponenteActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAbrirComponente);

        jButtonExcluirComponente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirComponente.setMnemonic('e');
        jButtonExcluirComponente.setText("Excluir");
        jButtonExcluirComponente.setEnabled(false);
        jButtonExcluirComponente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirComponenteActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonExcluirComponente);

        javax.swing.GroupLayout jPanelTodosComponentesLayout = new javax.swing.GroupLayout(jPanelTodosComponentes);
        jPanelTodosComponentes.setLayout(jPanelTodosComponentesLayout);
        jPanelTodosComponentesLayout.setHorizontalGroup(
            jPanelTodosComponentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTodosComponentesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTodosComponentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneTodosComponentes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelTodosComponentesLayout.setVerticalGroup(
            jPanelTodosComponentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTodosComponentesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneTodosComponentes, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Todos Componentes", jPanelTodosComponentes);

        jPanelCentral.add(jTabbedPanePrincipal, "MudancaEtapas");

        getContentPane().add(jPanelCentral, java.awt.BorderLayout.CENTER);

        jLabelStatus.setText("   ");
        jLabelStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(jLabelStatus, java.awt.BorderLayout.SOUTH);

        jToolBarPrincipal.setFloatable(false);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparator7);

        jButtonCriarEtapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_etapa.png"))); // NOI18N
        jButtonCriarEtapa.setToolTipText("Cria uma Nova Etapa");
        jButtonCriarEtapa.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarEtapa.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarEtapaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarEtapa);

        jButtonCriarHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_heuristica.png"))); // NOI18N
        jButtonCriarHeuristica.setToolTipText("Cria uma Nova Heurística");
        jButtonCriarHeuristica.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarHeuristica.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarHeuristica.setVerifyInputWhenFocusTarget(false);
        jButtonCriarHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarHeuristicaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarHeuristica);

        jButtonCriarRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/retangulo.png"))); // NOI18N
        jButtonCriarRegiao.setToolTipText("Define uma Nova Região");
        jButtonCriarRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarRegiaoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarRegiao);

        jButtonCriarAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_anotacao.png"))); // NOI18N
        jButtonCriarAnotacao.setToolTipText("Cria uma Nova Anotação");
        jButtonCriarAnotacao.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarAnotacao.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarAnotacaoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarAnotacao);

        jSeparatorButtonsCriar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorButtonsCriar.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparatorButtonsCriar);

        jButtonTestarHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/avaliar-heuristica.png"))); // NOI18N
        jButtonTestarHeuristica.setToolTipText("Avalia uma Situação de Jogo com um Conjunto Heurístico");
        jButtonTestarHeuristica.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonTestarHeuristica.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonTestarHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestarHeuristicaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonTestarHeuristica);

        jButtonDesafiarHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_competicao.png"))); // NOI18N
        jButtonDesafiarHeuristica.setToolTipText("Desafia outro Conjunto Heurístico");
        jButtonDesafiarHeuristica.setFocusable(false);
        jButtonDesafiarHeuristica.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDesafiarHeuristica.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonDesafiarHeuristica.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonDesafiarHeuristica.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDesafiarHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDesafiarHeuristicaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonDesafiarHeuristica);

        jSeparatorButtonsUtilizar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorButtonsUtilizar.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparatorButtonsUtilizar);

        jButtonConsultarAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonConsultarAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonConsultarAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarAjudaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonConsultarAjuda);

        jSliderNivelComplexidade.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jSliderNivelComplexidade.setMaximum(4);
        jSliderNivelComplexidade.setMinimum(1);
        jSliderNivelComplexidade.setMinorTickSpacing(1);
        jSliderNivelComplexidade.setPaintLabels(true);
        jSliderNivelComplexidade.setPaintTicks(true);
        jSliderNivelComplexidade.setSnapToTicks(true);
        jSliderNivelComplexidade.setValue(ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo()));
        jSliderNivelComplexidade.setLabelTable(jSliderNivelComplexidade.createStandardLabels(1));
        jSliderNivelComplexidade.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderNivelComplexidadeStateChanged(evt);
            }
        });

        jLabelNivelComplexidade.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNivelComplexidade.setText("Nivel de Complexidade");

        jLabelNomeNivel.setForeground(new java.awt.Color(0, 0, 153));
        jLabelNomeNivel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNomeNivel.setText("INICIANTE");

        javax.swing.GroupLayout jPanelTopoLayout = new javax.swing.GroupLayout(jPanelTopo);
        jPanelTopo.setLayout(jPanelTopoLayout);
        jPanelTopoLayout.setHorizontalGroup(
            jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTopoLayout.createSequentialGroup()
                .addComponent(jToolBarPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                .addGroup(jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabelNomeNivel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelNivelComplexidade, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderNivelComplexidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelTopoLayout.setVerticalGroup(
            jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSliderNivelComplexidade, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTopoLayout.createSequentialGroup()
                .addGroup(jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTopoLayout.createSequentialGroup()
                        .addComponent(jLabelNivelComplexidade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(jLabelNomeNivel))
                    .addGroup(jPanelTopoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jToolBarPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8))
        );

        getContentPane().add(jPanelTopo, java.awt.BorderLayout.NORTH);

        jMenuCriar.setMnemonic('c');
        jMenuCriar.setText("Criar");

        jMenuItemCriarEtapa.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarEtapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_etapa.png"))); // NOI18N
        jMenuItemCriarEtapa.setMnemonic('e');
        jMenuItemCriarEtapa.setText("Etapa");
        jMenuItemCriarEtapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarEtapaActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarEtapa);

        jMenuItemCriarHeuristica.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_heuristica.png"))); // NOI18N
        jMenuItemCriarHeuristica.setMnemonic('h');
        jMenuItemCriarHeuristica.setText("Heurística");
        jMenuItemCriarHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarHeuristicaActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarHeuristica);

        jMenuItemCriarRegiao.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/retangulo.png"))); // NOI18N
        jMenuItemCriarRegiao.setMnemonic('r');
        jMenuItemCriarRegiao.setText("Região");
        jMenuItemCriarRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarRegiaoActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarRegiao);
        jMenuCriar.add(jSeparatorAnotacao);

        jMenuItemCriarAnotacao.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarAnotacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_anotacao.png"))); // NOI18N
        jMenuItemCriarAnotacao.setMnemonic('a');
        jMenuItemCriarAnotacao.setText("Anotação");
        jMenuItemCriarAnotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarAnotacaoActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarAnotacao);

        jMenuBarPrincipal.add(jMenuCriar);

        jMenuEditar.setMnemonic('E');
        jMenuEditar.setText("Editar");

        jMenuItemRecortar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRecortar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/recortar.png"))); // NOI18N
        jMenuItemRecortar.setText("Recortar");
        jMenuEditar.add(jMenuItemRecortar);

        jMenuItemCopiar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/copiar.png"))); // NOI18N
        jMenuItemCopiar.setText("Copiar");
        jMenuEditar.add(jMenuItemCopiar);

        jMenuItemColar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemColar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/colar.png"))); // NOI18N
        jMenuItemColar.setText("Colar");
        jMenuEditar.add(jMenuItemColar);
        jMenuEditar.add(jSeparator4);

        jMenuItemLocalizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLocalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/pesquisar.png"))); // NOI18N
        jMenuItemLocalizar.setText("Localizar");
        jMenuEditar.add(jMenuItemLocalizar);

        jMenuBarPrincipal.add(jMenuEditar);
        jMenuEditar.setVisible(false);

        jMenuAjuda.setMnemonic('A');
        jMenuAjuda.setText("Ajuda");

        jMenuItemTopicosAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-topicos.png"))); // NOI18N
        jMenuItemTopicosAjuda.setText("Tópicos");
        jMenuAjuda.add(jMenuItemTopicosAjuda);

        jMenuItemPesquisarAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemPesquisarAjuda.setText("Pesquisar");
        jMenuAjuda.add(jMenuItemPesquisarAjuda);

        jMenuItemSobreJanelaAtual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jMenuItemSobreJanelaAtual.setText("Sobre a Janela Atual");
        jMenuAjuda.add(jMenuItemSobreJanelaAtual);

        jMenuItemDicasRapidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemDicasRapidas.setText("Dicas Rápidas");
        jMenuAjuda.add(jMenuItemDicasRapidas);
        jMenuAjuda.add(jSeparator2);

        jMenuItemPaginaInternetProjeto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemPaginaInternetProjeto.setText("Página na Internet do Projeto");
        jMenuAjuda.add(jMenuItemPaginaInternetProjeto);
        jMenuAjuda.add(jSeparator3);

        jMenuItemInformacoesSobreSistema.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/sobre.png"))); // NOI18N
        jMenuItemInformacoesSobreSistema.setText("Informações sobre o Sistema");
        jMenuAjuda.add(jMenuItemInformacoesSobreSistema);

        jMenuBarPrincipal.add(jMenuAjuda);
        jMenuAjuda.setVisible(false);

        jMenuSair.setMnemonic('S');
        jMenuSair.setText("Sair");

        jMenuItemFechaEditor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFechaEditor.setText("Fechar Conjunto Heurístico");
        jMenuItemFechaEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFechaEditorActionPerformed(evt);
            }
        });
        jMenuSair.add(jMenuItemFechaEditor);

        jMenuBarPrincipal.add(jMenuSair);

        setJMenuBar(jMenuBarPrincipal);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-792)/2, (screenSize.height-583)/2, 792, 583);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonTestarHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestarHeuristicaActionPerformed
        testarHeuristica();
    }//GEN-LAST:event_jButtonTestarHeuristicaActionPerformed

    private void jSliderNivelComplexidadeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderNivelComplexidadeStateChanged
        
        if (!jSliderNivelComplexidade.getValueIsAdjusting()) {
            
            int valorNovo  = (int) jSliderNivelComplexidade.getValue();
            int valorAtual = ConjuntoHeuristicoDAO.ordemNivel(conjuntoHeuristico.getTipo());
            
            if (valorNovo == valorAtual){
                return;
            }
            
            if (valorNovo > valorAtual){
                
                jLabelNomeNivel.setText((ConjuntoHeuristicoDAO.nivelOrdem(valorNovo)).getNome());
            
                int resposta = UtilsGUI.dialogoConfirmacao(this, "O novo valor é maior que o valor atual. Valores maiores permitem maiores\n" +
                                                                 "opções na hora de definir as heurísticas, mas necessitam de mais\n"  +
                                                                 "conhecimento e experiência no uso da ferramenta.\n\n" +
                                                                 "Deseja realmente aumentar o Nível de Complexidade a ser utilizado neste\n" +
                                                                 "conjunto Heurístico?",
                                                                 "Confirma aumento de Complexidade");
                
                if (resposta == JOptionPane.NO_OPTION || resposta == -1){
                    
                    jSliderNivelComplexidade.setValue(valorAtual);
                    jLabelNomeNivel.setText(conjuntoHeuristico.getTipo().getNome());
                    
                    return;
                }
            } else {
                
                ////////////////////////////
                // valorNovo < valorAtual //
                ////////////////////////////
                
                if (valorNovo >= ConjuntoHeuristicoDAO.ordemNivel(ConjuntoHeuristico.NIVEL_4_PLENO)) {
                    ////////////////////////////////////
                    // Não tem nenhum diferença ainda //
                    ////////////////////////////////////
                } else 
                    if (valorNovo == ConjuntoHeuristicoDAO.ordemNivel(ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO)) {
                        
                        if (possuiEtapas(valorNovo)) {
                            return;
                        }
                        if (possuiHeuristicasTransicaoEtapas(valorNovo)) {
                            return;
                        }
                    } else 
                        if (valorNovo == ConjuntoHeuristicoDAO.ordemNivel(ConjuntoHeuristico.NIVEL_2_BASICO)) {
                            
                            if (possuiEtapas(valorNovo)) {
                                return;
                            }
                            if (possuiHeuristicasTransicaoEtapas(valorNovo)) {
                                return;
                            }
                            if (possuiHeuristicasValorTabuleiro(valorNovo)) {
                                return;
                            }
                        } else 
                            if (valorNovo == ConjuntoHeuristicoDAO.ordemNivel(ConjuntoHeuristico.NIVEL_1_INICIANTE)) {
                                
                                if (possuiEtapas(valorNovo)) {
                                    return;
                                }
                                if (possuiHeuristicasTransicaoEtapas(valorNovo)) {
                                    return;
                                }
                                if (possuiHeuristicasValorTabuleiro(valorNovo)) {
                                    return;
                                }
                                if (possuiHeuristicasValorPeca(valorNovo)) {
                                    return;
                                }
                                if (possuiRegioes(valorNovo)) {
                                    return;
                                }
                            }
            }

            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                conjuntoHeuristico.setTipo(ConjuntoHeuristicoDAO.nivelOrdem(valorNovo));

                ConjuntoHeuristicoDAO.atualiza(conjuntoHeuristico);
                
                ConexaoDBHeuChess.commit();
        
                atualizaVersaoDataUltimaModificacao();
                atualizaNilveComplexidadeCompleto();
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            } catch (Exception e) {
                HeuChess.desfazTransacao(e);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao atualizar no Banco o novo Nível de Complexidade." +
                                           "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                
                conjuntoHeuristico.setTipo(ConjuntoHeuristicoDAO.nivelOrdem(valorAtual));
                jSliderNivelComplexidade.setValue(valorAtual);
                jLabelNomeNivel.setText(conjuntoHeuristico.getTipo().getNome());
                
                fechar(true);
            }            
        }
    }//GEN-LAST:event_jSliderNivelComplexidadeStateChanged
    
    private void jTreeComponentesHeuristicosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTreeComponentesHeuristicosFocusGained
        
        if (recebendoFocoTreeComponentes && (evt.getOppositeComponent() != jButtonAbrirComponente)){
            
            constroiTreeComponentesHeuristicos();
            
            recebendoFocoTreeComponentes = false;
        }
    }//GEN-LAST:event_jTreeComponentesHeuristicosFocusGained

    private void jButtonExcluirComponenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirComponenteActionPerformed
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeComponentesHeuristicos.getLastSelectedPathComponent();
        
        if (node == null){
            jButtonAbrirComponente.setEnabled(false);
            jButtonExcluirComponente.setEnabled(false);
            return;
        }    
        
        Object nodeInfo = node.getUserObject();         
        
        if (nodeInfo instanceof Componente){
    
            boolean sucesso = false;
            
            if (nodeInfo instanceof Heuristica) {

                for (Etapa etapa : conjuntoHeuristico.getEtapas()) {

                    if ((etapa.getHeuristicasTransicaoEtapa().indexOf(nodeInfo) != -1) || 
                        (etapa.getHeuristicasValorPeca().indexOf(nodeInfo)      != -1) ||
                        (etapa.getHeuristicasValorTabuleiro().indexOf(nodeInfo) != -1)) {

                        PanelEtapa panelEtapa = localizaEtapa(etapa);
                        sucesso = panelEtapa.confirmaApagarHeuristica((Heuristica) nodeInfo);
                        break;
                    }
                }               
                
            } else 
                if (nodeInfo instanceof Regiao) {

                    for (Etapa etapa : conjuntoHeuristico.getEtapas()) {

                        if (etapa.getRegioes().indexOf(nodeInfo) != -1) {

                            PanelEtapa panelEtapa = localizaEtapa(etapa);
                            sucesso = panelEtapa.confirmaApagarRegiao((Regiao) nodeInfo);
                            break;
                        }
                    }
                    
                }else
                    if (nodeInfo instanceof Etapa){
                        sucesso = confirmaApagarEtapa((Etapa) nodeInfo);                                                
                    }else
                        if (nodeInfo instanceof Anotacao) {

                            Anotacao anotacao = (Anotacao) nodeInfo;

                            int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja Realmente Apagar a Anotação\n" +
                                                                             "\"" + anotacao + "\" ?",
                                                                             "Confirmação Exclusão");

                            if (resposta == JOptionPane.YES_OPTION) {
                             
                                try{       
                                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                    
                                    if (anotacao.getComponente() instanceof ConjuntoHeuristico) {
                                        Anotacoes.apagarComComponenteAberto(this, anotacao);
                                    }else
                                        if (anotacao.getComponente() instanceof Etapa) {

                                            Etapa etapa = (Etapa) anotacao.getComponente();
                                            PanelEtapa panelEtapa = localizaEtapa(etapa);

                                            Anotacoes.apagarComComponenteAberto(panelEtapa, anotacao);
                                        }else{
                                            /////////////////////////////////////////////////////
                                            // a janela não está aberta (Heurística ou Regiao) //
                                            /////////////////////////////////////////////////////
                                            
                                            AnotacaoDAO.apaga(anotacao);                             
                                            
                                            anotacao.getComponente().getAnotacoes().remove(anotacao);
                                        }
                                    
                                    ConexaoDBHeuChess.commit();
                                    
                                    if (HeuChess.somAtivado) {
                                        HeuChess.somApagar.play();
                                    }
                                    
                                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                    sucesso = true;
                                    
                                }catch (Exception e){
                                    HeuChess.desfazTransacao(e);
                                                                        
                                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                    UtilsGUI.dialogoErro(this, "Erro ao tentar apagar a Anotacao no Banco de Dados." +
                                                               "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                                    
                                    sucesso = false;
                                    
                                    fechar(true);
                                }                                
                            }
                        }
            
            if (sucesso){
                
                if (nodeInfo instanceof Anotacao){
                    
                    DefaultTreeModel model = (DefaultTreeModel) jTreeComponentesHeuristicos.getModel();
                    model.removeNodeFromParent(node);                
                    jScrollPaneTodosComponentes.repaint();
                    
                }else{
                    //////////////////////////////////////////////
                    // Pode ter apagado vários elementos filhos //
                    //////////////////////////////////////////////
                    constroiTreeComponentesHeuristicos();
                }
            }
        }
    }//GEN-LAST:event_jButtonExcluirComponenteActionPerformed

    private void jTreeComponentesHeuristicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeComponentesHeuristicosMouseClicked
        
        if (jTreeComponentesHeuristicos.isEnabled()){
            
            if (evt.getClickCount() == 2){
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeComponentesHeuristicos.getLastSelectedPathComponent();
                
                if (node == null){
                    return;
                }
                
                Object nodeInfo = node.getUserObject();
                
                abrindoComponente(nodeInfo);
            }
        }
    }//GEN-LAST:event_jTreeComponentesHeuristicosMouseClicked

    private void jButtonAbrirComponenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirComponenteActionPerformed
        
        jTreeComponentesHeuristicos.requestFocus();
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeComponentesHeuristicos.getLastSelectedPathComponent();
        
        if (node == null){
            jButtonAbrirComponente.setEnabled(false);
            jButtonExcluirComponente.setEnabled(false);
            return;
        }
        
        Object nodeInfo = node.getUserObject();        
        
        abrindoComponente(nodeInfo);
    }//GEN-LAST:event_jButtonAbrirComponenteActionPerformed
      
    private void jTreeComponentesHeuristicosValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeComponentesHeuristicosValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeComponentesHeuristicos.getLastSelectedPathComponent();
        
        if (node == null){
            jButtonAbrirComponente.setEnabled(false);
            jButtonExcluirComponente.setEnabled(false);
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if ((nodeInfo instanceof Etapa)     ||
            (nodeInfo instanceof Heuristica)||
            (nodeInfo instanceof Regiao)    ||
            (nodeInfo instanceof Anotacao)  ||
            (nodeInfo instanceof Funcao)){
            
            jButtonAbrirComponente.setEnabled(true);
            jButtonExcluirComponente.setEnabled(true);
        }else{
            jButtonAbrirComponente.setEnabled(false);
            jButtonExcluirComponente.setEnabled(false);
        }
    }//GEN-LAST:event_jTreeComponentesHeuristicosValueChanged

    private void jMenuItemCriarAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarAnotacaoActionPerformed
        novaAnotacao();
    }//GEN-LAST:event_jMenuItemCriarAnotacaoActionPerformed

    private void jButtonCriarAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarAnotacaoActionPerformed
        novaAnotacao();
    }//GEN-LAST:event_jButtonCriarAnotacaoActionPerformed

    private void jButtonCriarRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarRegiaoActionPerformed
        novaRegiao();
    }//GEN-LAST:event_jButtonCriarRegiaoActionPerformed

    private void jMenuItemCriarRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarRegiaoActionPerformed
        novaRegiao();
    }//GEN-LAST:event_jMenuItemCriarRegiaoActionPerformed

    private void jMenuItemCriarHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarHeuristicaActionPerformed
        novaHeuristica();
    }//GEN-LAST:event_jMenuItemCriarHeuristicaActionPerformed

    private void jButtonCriarHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarHeuristicaActionPerformed
        novaHeuristica();
    }//GEN-LAST:event_jButtonCriarHeuristicaActionPerformed

    private void jButtonCriarEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarEtapaActionPerformed
        novaEtapa();
    }//GEN-LAST:event_jButtonCriarEtapaActionPerformed

    private void jMenuItemCriarEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarEtapaActionPerformed
        novaEtapa();
    }//GEN-LAST:event_jMenuItemCriarEtapaActionPerformed

    private void jMenuItemFechaEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFechaEditorActionPerformed
        fechar(false);
    }//GEN-LAST:event_jMenuItemFechaEditorActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        fechar(false);        
    }//GEN-LAST:event_formWindowClosing
    
    private void jButtonExcluirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirAnotacaoActionPerformed
        Anotacoes.confirmaApagarAnotacaoSelecionada(this);                
    }//GEN-LAST:event_jButtonExcluirAnotacaoActionPerformed

    private void jButtonNovaAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaAnotacaoActionPerformed
        Anotacoes.novaAnotacao(this);
    }//GEN-LAST:event_jButtonNovaAnotacaoActionPerformed

    private void jButtonAbrirAnotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirAnotacaoActionPerformed
        Anotacoes.abrirAnotacao(this);        
    }//GEN-LAST:event_jButtonAbrirAnotacaoActionPerformed

    private void jListAnotacoesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAnotacoesValueChanged
        Anotacoes.verificaSelecaoAnotacao(this);        
    }//GEN-LAST:event_jListAnotacoesValueChanged

    private void jListAnotacoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAnotacoesMouseClicked
        Anotacoes.verificaDuploCliqueAnotacao(this, evt);        
    }//GEN-LAST:event_jListAnotacoesMouseClicked

    private void jButtonExcluirTransicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirTransicaoActionPerformed
        
        Object cell = editorMudancaEtapas.getGraph().getSelectionCell();
        
        if (cell instanceof ArestaHeuristicaTransicaoEtapa){
            
            ArestaHeuristicaTransicaoEtapa aresta = (ArestaHeuristicaTransicaoEtapa) cell;
            
            Heuristica heuristica = aresta.getHeuristica();
            
            PanelEtapa panel = localizaEtapa(aresta.getEtapa());
                
            if (panel != null){
                panel.confirmaApagarHeuristica(heuristica);
            }                
        }
    }//GEN-LAST:event_jButtonExcluirTransicaoActionPerformed

    private void jButtonExcluirEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirEtapaActionPerformed
        
        Object cell = editorMudancaEtapas.getGraph().getSelectionCell();
        
        if (cell instanceof VerticeEtapa){
            
            Etapa etapa  = ((VerticeEtapa) cell).getEtapa();            
            
            confirmaApagarEtapa(etapa);
        }
    }//GEN-LAST:event_jButtonExcluirEtapaActionPerformed
    
    private void jComboBoxEtapaInicialItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxEtapaInicialItemStateChanged
        
        if (evt.getStateChange() == ItemEvent.SELECTED) {

            Etapa etapaNova = (Etapa) jComboBoxEtapaInicial.getSelectedItem();

            if (etapaNova != null && etapaNova != conjuntoHeuristico.getEtapaInicial()) {

                Etapa etapaVelha = conjuntoHeuristico.getEtapaInicial();
                
                try{
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    conjuntoHeuristico.setEtapaInicial(etapaNova);
                    
                    ConjuntoHeuristicoDAO.atualiza(conjuntoHeuristico);                
                    
                    ConexaoDBHeuChess.commit();
                    
                    editorMudancaEtapas.defineEtapaInicial(etapaNova);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                }catch(Exception e){
                    HeuChess.desfazTransacao(e);
                    
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(this, "Erro ao atualizar a Etapa Inicial no Banco de Dados." +
                                               "\nOperação Cancelada.\n\nO Conjunto Heurístico será fechado!");
                    
                    conjuntoHeuristico.setEtapaInicial(etapaVelha);
                    jComboBoxEtapaInicial.setSelectedItem(etapaVelha);
                    
                    fechar(true);
                }                
            }
        }
    }//GEN-LAST:event_jComboBoxEtapaInicialItemStateChanged

    private void jButtonNovaEtapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaEtapaActionPerformed
        novaEtapa();
    }//GEN-LAST:event_jButtonNovaEtapaActionPerformed

    private void jButtonDesafiarHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDesafiarHeuristicaActionPerformed
        novaPartida();
    }//GEN-LAST:event_jButtonDesafiarHeuristicaActionPerformed

    private void jButtonConsultarAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaEditorConjuntoHeuristico");        
    }//GEN-LAST:event_jButtonConsultarAjudaActionPerformed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        verificaAtualizacaoTodosNomes();
    }//GEN-LAST:event_formFocusLost

    private void jButtonDadosAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDadosAutorActionPerformed
        HeuChess.dadosAutor(this, conjuntoHeuristico.getIdAutor());
    }//GEN-LAST:event_jButtonDadosAutorActionPerformed

    private void jTextFieldNomeConjuntoHeuristicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNomeConjuntoHeuristicoActionPerformed
        verificaAtualizacaoNomeConjuntoHeuristico();
    }//GEN-LAST:event_jTextFieldNomeConjuntoHeuristicoActionPerformed

    private void jTextFieldNomeConjuntoHeuristicoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNomeConjuntoHeuristicoKeyTyped
        atualizaTituloEditor();        
    }//GEN-LAST:event_jTextFieldNomeConjuntoHeuristicoKeyTyped

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelTodosComponentes){
            
            recebendoFocoTreeComponentes = false;
            
            constroiTreeComponentesHeuristicos();            
        }else
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInformacoesGerais){
                atualizaVersaoDataUltimaModificacao();
            }
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged
         
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirAnotacao;
    private javax.swing.JButton jButtonAbrirComponente;
    private javax.swing.JButton jButtonConsultarAjuda;
    private javax.swing.JButton jButtonCriarAnotacao;
    private javax.swing.JButton jButtonCriarEtapa;
    private javax.swing.JButton jButtonCriarHeuristica;
    private javax.swing.JButton jButtonCriarRegiao;
    private javax.swing.JButton jButtonDadosAutor;
    private javax.swing.JButton jButtonDesafiarHeuristica;
    private javax.swing.JButton jButtonExcluirAnotacao;
    private javax.swing.JButton jButtonExcluirComponente;
    private javax.swing.JButton jButtonExcluirEtapa;
    private javax.swing.JButton jButtonExcluirTransicao;
    private javax.swing.JButton jButtonNovaAnotacao;
    private javax.swing.JButton jButtonNovaEtapa;
    private javax.swing.JButton jButtonTestarHeuristica;
    private javax.swing.JComboBox jComboBoxEtapaInicial;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelNivelComplexidade;
    private javax.swing.JLabel jLabelNomeNivel;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTituloListaAnotacoesCH;
    private javax.swing.JLabel jLabelTotalAnotacoes;
    private javax.swing.JList jListAnotacoes;
    private javax.swing.JMenu jMenuAjuda;
    private javax.swing.JMenuBar jMenuBarPrincipal;
    private javax.swing.JMenu jMenuCriar;
    private javax.swing.JMenu jMenuEditar;
    private javax.swing.JMenuItem jMenuItemColar;
    private javax.swing.JMenuItem jMenuItemCopiar;
    private javax.swing.JMenuItem jMenuItemCriarAnotacao;
    private javax.swing.JMenuItem jMenuItemCriarEtapa;
    private javax.swing.JMenuItem jMenuItemCriarHeuristica;
    private javax.swing.JMenuItem jMenuItemCriarRegiao;
    private javax.swing.JMenuItem jMenuItemDicasRapidas;
    private javax.swing.JMenuItem jMenuItemFechaEditor;
    private javax.swing.JMenuItem jMenuItemInformacoesSobreSistema;
    private javax.swing.JMenuItem jMenuItemLocalizar;
    private javax.swing.JMenuItem jMenuItemPaginaInternetProjeto;
    private javax.swing.JMenuItem jMenuItemPesquisarAjuda;
    private javax.swing.JMenuItem jMenuItemRecortar;
    private javax.swing.JMenuItem jMenuItemSobreJanelaAtual;
    private javax.swing.JMenuItem jMenuItemTopicosAjuda;
    private javax.swing.JMenu jMenuSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelBotoesEditorEtapas;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JPanel jPanelInformacoesGerais;
    private javax.swing.JPanel jPanelMudancaEtapas;
    private javax.swing.JPanel jPanelTodosComponentes;
    private javax.swing.JPanel jPanelTopo;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneEditorEtapas;
    private javax.swing.JScrollPane jScrollPaneTodosComponentes;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparatorAnotacao;
    private javax.swing.JSeparator jSeparatorButtonsCriar;
    private javax.swing.JSeparator jSeparatorButtonsUtilizar;
    private javax.swing.JSlider jSliderNivelComplexidade;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JTextField jTextFieldDataCriacao;
    private javax.swing.JTextField jTextFieldDataModificacao;
    private javax.swing.JTextField jTextFieldNomeAutor;
    private javax.swing.JTextField jTextFieldNomeConjuntoHeuristico;
    private javax.swing.JTextField jTextFieldVersao;
    private javax.swing.JToolBar jToolBarPrincipal;
    private javax.swing.JTree jTreeComponentesHeuristicos;
    // End of variables declaration//GEN-END:variables
}

