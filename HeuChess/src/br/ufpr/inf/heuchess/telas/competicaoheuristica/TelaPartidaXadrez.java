package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.competicaoheuristica.CasoAvaliado;
import br.ufpr.inf.heuchess.competicaoheuristica.EngineAnalise;
import br.ufpr.inf.heuchess.competicaoheuristica.Jogador;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida.Estado;
import static br.ufpr.inf.heuchess.competicaoheuristica.Partida.Estado.IN_PROGRESS;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.editorheuristica.TelaEditorConjuntoHeuristico;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.TaskItem;
import br.ufpr.inf.utils.gui.TaskPanel;
import br.ufpr.inf.utils.gui.UtilsGUI;
import br.ufpr.inf.utils.gui.UtilsTree;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 12, 2012
 */
public class TelaPartidaXadrez extends javax.swing.JFrame implements ModalFrameHierarchy, TreeWillExpandListener, TreeSelectionListener {

    public static int MAXIMO_PARTIDAS_SIMULTANEAS = 2;
    
    private CardLayout cardPanelDetalhesLance, cardCentral;
    
    private Partida        partida;
    private Lance          movimentoEscolhido;
    private EngineAnalise  engineAtual;
    private Tabuleiro      tabuleiroInicial;    
    
    private DesenhaTabuleiro     desenhaTabuleiro;
    private PanelRelogio         panelRelogio;
    private PanelPecasCapturadas panelPecasCapturadas;
    private PanelPecasCapturadas panelCapturesUp;
    private PanelPecasCapturadas panelCapturesDown;
    
    private boolean flipTabuleiroPretas;    
    private long    tempoMinimoJogada = 500;    

    private boolean fechouTelaEditorBrancas;
    private boolean fechouTelaEditorPretas;
    
    private RenderTreeJogadas renderTreeJogadas;
    
    private static final ImageIcon   iconeBrancas;
    private static final ImageIcon   iconePretas;
    private static final ImageIcon[] iconesRelogio;
    
    static {
        iconesRelogio    = new ImageIcon[8];
        iconesRelogio[0] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio1.png"));
        iconesRelogio[1] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio2.png"));
        iconesRelogio[2] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio3.png"));
        iconesRelogio[3] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio4.png"));
        iconesRelogio[4] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio5.png"));
        iconesRelogio[5] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio6.png"));
        iconesRelogio[6] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio7.png"));
        iconesRelogio[7] = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/relogio8.png"));
        
        iconeBrancas = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/white22.png"));
        iconePretas  = new ImageIcon(TelaPartidaXadrez.class.getResource("/icones/black22.png"));    
    }
    
    private class AtualizaRelogio extends Thread {
        
        private boolean ativado;
        private boolean atualizarTempo;
                
        public AtualizaRelogio(boolean atualizarTempo){
            this.atualizarTempo = atualizarTempo;
            setDaemon(true);
        }
        
        public void inicia(){
            
            if (partida.isPassoAPasso()){
                jPanelDetalhesLance.setVisible(true);    
                cardPanelDetalhesLance.show(jPanelDetalhesLance,"Relogio");            
            }else{
                jLabelRelogioPartidaAutomatica.setVisible(true);
            }
            
            super.start();
        }
        
        public boolean isAtivado(){
            return ativado;
        }
        
        public void termina(){
            
            if (!partida.isPassoAPasso()){
                jLabelRelogioPartidaAutomatica.setIcon(null);
            }
            
            ativado = false;
        }
        
        @Override
        public void run(){
            
            int imagem = 0;
            
            ativado = true;
            
            while(ativado){
                
                if (partida.isPassoAPasso()){                    
                    
                    jLabelRelogioPartidaManual.setIcon(iconesRelogio[imagem]);                    
                    
                    if (atualizarTempo){
                        panelRelogio.setTexto(UtilsDataTempo.formataTempoNanossegundos(System.nanoTime() - engineAtual.getTempoInicial(), true));
                    }
                     
                }else{
                    jLabelRelogioPartidaAutomatica.setIcon(iconesRelogio[imagem]);
                }
                
                imagem = (++imagem < 8 ? imagem : 0);
                
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    
                }
            }
        }
    }
    
    private class AtualizaPartida extends Thread {

        public AtualizaPartida(){
            setDaemon(true);
        }
        
        private boolean buscaProximaLance() {
            
            movimentoEscolhido = null;

            try {
                movimentoEscolhido = engineAtual.getProximoLance(tabuleiroInicial, tabuleiroInicial.isWhiteActive());
            } catch (Exception e) {

                HeuChess.registraExcecao(e);

                partida.cancel();

                if (HeuChess.somAtivado) {
                    HeuChess.somPartidaCancelada.play();
                }

                UtilsGUI.dialogoErro(TelaPartidaXadrez.this, "Erro grave e a partida será interrompida!\n\n" +
                                                             UtilsString.cortaTextoMaior(e.getMessage(), 100, true));                
                return false;
            }
            
            return true;
        }
      
        private void tocarSomMovimento() {

            if (HeuChess.somAtivado) {
                if (movimentoEscolhido.getPecaCapturada() != null) {
                    HeuChess.somPecaCapturada.play();
                } else {
                    if (movimentoEscolhido.isPromocao()) {
                        HeuChess.somPecaPromovida.play();
                    } else {
                        HeuChess.somPecaMovida.play();
                    }
                }
            }
        }
        
        @Override
        public void run() {

            AtualizaRelogio atualizador = new AtualizaRelogio(true);
            atualizador.inicia();
        
            if (partida.isPassoAPasso()) {
                
                if (partida.getState() == IN_PROGRESS) {

                    if (!buscaProximaLance()){
                        return;
                    }

                    if (partida.applyMovement(movimentoEscolhido)) {
                        tocarSomMovimento();
                    }

                    imprimiFinalPartida();
                    mostraDadosAnaliticosJogadaEscolhida();
                }
            } else {
                
                long tempoInicial, tempoFinal;

                while (partida.getState() == IN_PROGRESS) {

                    tempoInicial = System.currentTimeMillis();

                    mostraDadosJogadorAtual();

                    if (!buscaProximaLance()){
                        return;
                    }

                    tempoFinal = System.currentTimeMillis();
                    
                    if (tempoFinal - tempoInicial < tempoMinimoJogada) {
                        try {
                            sleep(tempoMinimoJogada - (tempoFinal - tempoInicial));
                        } catch (Exception e) {
                            HeuChess.registraExcecao(e);
                        }
                    }

                    if (partida.applyMovement(movimentoEscolhido)) {
                        tocarSomMovimento();
                    }
                }
                 
                jButtonCancelarPartida.setEnabled(false);
                jButtonReiniciarPartida.setEnabled(true);
                
                imprimiFinalPartida();
                habilitaBotoesNavegacaoPartidaAutomatica();     
            }
            
            atualizador.termina();
        }
    }
    
    private class CriaNosArvoreLances extends Thread {

        private LanceTreeNode lanceTreeNode;        
        private CasoAvaliado  caso;
        
        public CriaNosArvoreLances(LanceTreeNode lanceTreeNode){
            this.lanceTreeNode = lanceTreeNode;
            setDaemon(true);
        }
        
        public CriaNosArvoreLances(CasoAvaliado caso){
            this.caso = caso;
            setDaemon(true);
        }
                
        private ArrayList<LanceTreeNode> criaNodesFilhos() {

            Tabuleiro tabuleiro = lanceTreeNode.getTabuleiroDerivado();

            try {
                EngineAnalise engine = engineAtual.geraClone();

                engine.setProfundidadeBusca(engine.getProfundidadeBusca() - lanceTreeNode.getLevel());

                // Realiza procura por jogada - Cria TreeNodes filhos do primeiro nível //
                
                engine.getProximoLance(tabuleiro, tabuleiroInicial.isWhiteActive());
                
                return engine.getRaizes();

            } catch (Exception e) {
                HeuChess.registraExcecao(e);
                UtilsGUI.dialogoErro(TelaPartidaXadrez.this, "Erro grave ao gerar estados filhos!\n\n" +
                                                             UtilsString.cortaTextoMaior(e.getMessage(), 100, true));
                return null;
            }
        }

        private void atualizaNode(ArrayList<LanceTreeNode> novosTreeNodes) {

            DefaultTreeModel model = (DefaultTreeModel) jTreeArvoreLances.getModel();

            if (caso == null) {
                // Apenas expandino nós - Retira nó que informava Calculando //                    
                model.removeNodeFromParent((DefaultMutableTreeNode) lanceTreeNode.getChildAt(0));
            }

            // Adiciona novos nós encontrado //

            for (LanceTreeNode node : novosTreeNodes) {
                model.insertNodeInto(node, lanceTreeNode, lanceTreeNode.getChildCount());
            }
        }
        
        @Override
        public void run() {

            final AtualizaRelogio atualizador = new AtualizaRelogio(false);
            atualizador.inicia();           
                
            habilitaBotoesNavegacaoPartidaManual(false);
            
            if (caso != null) {
                
                ArrayList<Lance> moves = caso.getLances();
                                
                DefaultMutableTreeNode nodeRoot = (DefaultMutableTreeNode) jTreeArvoreLances.getModel().getRoot();
                DefaultMutableTreeNode nodePai  = nodeRoot;

                for (int x = 0; x < moves.size() - 1; x++) {
                   
                    lanceTreeNode = localizaTreeNodeFilho(nodePai, moves.get(x), false);

                    if (lanceTreeNode != null) {                        
                         
                        if (lanceTreeNode.getChildCount() == 0){
                            
                            final ArrayList<LanceTreeNode> novosTreeNodes = criaNodesFilhos();
                            
                            if (novosTreeNodes == null){
                                atualizador.termina();                        
                                habilitaBotoesNavegacaoPartidaManual(true);  
                                return;
                            }
                            
                            atualizaNode(novosTreeNodes);
                        }
                        
                    } else {
                        
                        UtilsGUI.dialogoErro(TelaPartidaXadrez.this, "Não localizou o Nó do movimento\n\"" + moves.get(x) + 
                                                                     "\"\nna árvore de lances!");
                        atualizador.termina();                        
                        habilitaBotoesNavegacaoPartidaManual(true);  
                        return;
                    }

                    nodePai = lanceTreeNode;
                }

                atualizador.termina();                        
                habilitaBotoesNavegacaoPartidaManual(true); 

                Lance ultimoMovimento = moves.get(moves.size() - 1);

                if (localizaTreeNodeFilho(nodePai, ultimoMovimento, true) == null) {
                    UtilsGUI.dialogoErro(TelaPartidaXadrez.this, "Não localizou o Nó do movimento\n\"" + ultimoMovimento + 
                                                                 "\"\nna árvore de lances!");
                }
                
                // Força visualização do nó selecionado - Pois o JScrollPane não consegue atualizar corretamente a seleção //
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeArvoreLances.getLastSelectedPathComponent();

                if (node != null) {
                    TreePath path = new TreePath(node.getPath());                                        
                    jTreeArvoreLances.scrollRectToVisible(jTreeArvoreLances.getPathBounds(path));
                }    
                
                // Força exibição dos Dados do Nó Selecionado - Pois o evento não é ativado a tempo //            
                verificaTreeArvoreLances();           
                
            } else {
                
                final ArrayList<LanceTreeNode> novosTreeNodes = criaNodesFilhos();
                atualizador.termina();
                
                if (novosTreeNodes != null){
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override 
                        public void run() {                       
                            atualizaNode(novosTreeNodes);                            
                            habilitaBotoesNavegacaoPartidaManual(true);                                              
                            UtilsTree.selecionaTreeNode(jTreeArvoreLances, lanceTreeNode);
                            
                            // Força exibição dos Dados do Nó Selecionado - Pois o evento não é ativado a tempo //            
                            verificaTreeArvoreLances();
                        }
                    });
                }
            }
        }
    }
    
    public TelaPartidaXadrez(final ModalFrameHierarchy acessoTelaPartidaXadrez, Partida partida) {

        setEnabled(false);
        
        this.partida = partida;
        
        tabuleiroInicial = partida.getTabuleiro();
        
        renderTreeJogadas = new RenderTreeJogadas(false,true,false);
        
        initComponents();
        
        cardCentral = (CardLayout) jPanelCentro.getLayout(); 
               
        DesenhaPecas desenhoPecas = new DesenhaPecas();
        
        TaskPanel tp = new TaskPanel();
        
        ImageIcon iconePanelRelogio       = new ImageIcon(getClass().getResource("/icones/clock16.png"));
        ImageIcon iconePanelConfiguracoes = new ImageIcon(getClass().getResource("/icones/config16.png"));
                
        if (partida.isPassoAPasso()){
            
            cardCentral.show(jPanelCentro,"PartidaManual");
            
            cardPanelDetalhesLance = (CardLayout) jPanelDetalhesLance.getLayout(); 
                           
            desenhaTabuleiro = new DesenhaTabuleiro(desenhoPecas); 
            desenhaTabuleiro.setCellSideLength(26);
            jPanelTabuleiro.add(desenhaTabuleiro, BorderLayout.CENTER);        
       
            tp.add(new TaskItem("Configurações", true, new PanelConfiguracoes(this, renderTreeJogadas, true), iconePanelConfiguracoes, HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));
        
            panelRelogio = new PanelRelogio();
            tp.add(new TaskItem("Tempo Gasto no Cálculo", false, panelRelogio.getComponent(), iconePanelRelogio, HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));
        
            panelPecasCapturadas = new PanelPecasCapturadas(partida, desenhoPecas);
            tp.add(new TaskItem("Peças Capturadas", false, panelPecasCapturadas.getComponent(), new ImageIcon(getClass().getResource("/icones/icone_pecas_capturadas.png")), HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));
            
            jTreeArvoreLances.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("..calculando jogada...")));
            jTreeArvoreLances.addTreeWillExpandListener(this);
            jTreeArvoreLances.addTreeSelectionListener(this);
            
            jButtonCancelarPartida.setVisible(false);
            jButtonReiniciarPartida.setVisible(false);            
            
        }else{
            
            cardCentral.show(jPanelCentro,"PartidaAutomatica");
            
            desenhaTabuleiro = new DesenhaTabuleiro(partida, desenhoPecas);
            desenhaTabuleiro.setCellSideLength(47);
            desenhaTabuleiro.setHighlightLastMove(true);
            desenhaTabuleiro.setFlipView(flipTabuleiroPretas);                    
            
            panelCapturesUp = new PanelPecasCapturadas(partida, desenhoPecas, true);
            panelCapturesUp.setWhiteCaptured(!flipTabuleiroPretas);            
            panelCapturesUp.getComponent().setVisible(true);
            
            panelCapturesDown = new PanelPecasCapturadas(partida, desenhoPecas, false);
            panelCapturesDown.setWhiteCaptured(flipTabuleiroPretas);        
            panelCapturesDown.getComponent().setVisible(true);
        
            tp.add(new TaskItem("Configurações", false, new PanelConfiguracoes(this, renderTreeJogadas, false), iconePanelConfiguracoes, HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));

            panelRelogio = new PanelRelogio(partida);
            if (partida.getModalidade() == Partida.Modalidade.TEMPO_ILIMITADO){
                tp.add(new TaskItem("Tempo da Partida", false, panelRelogio.getComponent(), iconePanelRelogio, HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));        
            }else{
                tp.add(new TaskItem("Tempo dos Jogadores",false, panelRelogio.getComponent(), iconePanelRelogio, HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));
            }
        }
        
        tp.add(new TaskItem("Jogadas em Notação SAN", false, new PanelJogadas(partida).getComponent(),  new ImageIcon(getClass().getResource("/icones/sheet16.png")),HeuChess.iconeAbaFechada,HeuChess.iconeAbaAberta));
        jPanelDireito.add(tp.getComponent(), BorderLayout.EAST);

        desenhaTabuleiro.setCoordinatesPainted(true);
        desenhaTabuleiro.setTabuleiroLF(0);
        desenhaTabuleiro.setPecaLF(0);
        desenhaTabuleiro.setEnabled(false);
        
        //////////////////////
        // Minimiza Janelas //
        //////////////////////
        
        HeuChess.telaPrincipal.minimizaTodasJanelas();       
        HeuChess.telaPrincipal.telasPartidaXadrez.add(this);
        
        acessoTelaPartidaXadrez.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        ////////////////////////////////////////////
        // Fecha telas editor heurísticas abertas //
        ////////////////////////////////////////////
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                ConjuntoHeuristico conjuntoBrancas = getJogadorBrancas();
                
                if (conjuntoBrancas != null) {
                    
                    for (int indice = 0; indice < HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.size(); indice++) {

                        TelaEditorConjuntoHeuristico tela = HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.get(indice);

                        if (conjuntoBrancas.getId() == tela.idConjuntoHeuristico() && tela.podeAlterar()) {
                            HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.remove(tela);
                            fechouTelaEditorBrancas = true;
                            tela.dispose();
                            break;
                        }
                    }
                }
                
                ConjuntoHeuristico conjuntoPretas = getJogadorPretas();
                
                if (conjuntoPretas != null) {
                    
                    for (int indice = 0; indice < HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.size(); indice++) {

                        TelaEditorConjuntoHeuristico tela = HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.get(indice);

                        if (conjuntoPretas.getId() == tela.idConjuntoHeuristico() && tela.podeAlterar()) {
                            HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.remove(tela);
                            fechouTelaEditorPretas = true;
                            tela.dispose();
                            break;
                        }
                    }
                }
            }
        });
        
        /////////////////
        // Abre Janela //
        /////////////////
        
        setVisible(true);
        setEnabled(true);
        toFront();
        
        ////////////////////
        // Inicia Partida //
        ////////////////////
        
        if (partida.isPassoAPasso()){
            
            partida.restart();           
            proximoPassoAnalise();
            
        }else{
            
            getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
                
                @Override
                public void ancestorMoved(HierarchyEvent e) {
                    
                }

                @Override
                public void ancestorResized(HierarchyEvent e) {
                    
                    if (!TelaPartidaXadrez.this.partida.isPassoAPasso()) {                 
                        
                        int novoTamanho = jPanelTabuleiroAutomatico.getHeight() / 10;
                        desenhaTabuleiro.setCellSideLength(novoTamanho);
                        desenhaTabuleiro.repaint();
                        
                        JPanel panelBord = new JPanel(new BorderLayout());
                        panelBord.add(panelCapturesUp.getComponent(), BorderLayout.NORTH);
                        panelBord.add(desenhaTabuleiro, BorderLayout.CENTER);
                        panelBord.add(panelCapturesDown.getComponent(), BorderLayout.SOUTH);
                        
                        // Descobre-se primeiro o fator de crescimento da Célula, 
                        // e o aplica ao valor inicial de deslocamento vertical do FlowLayout
                        
                        int espacoVertical = (int) (novoTamanho/47.0 * 9);                        
                        
                        JPanel jCentraliza = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, espacoVertical));            
                        jCentraliza.add(panelBord);
                        jPanelTabuleiroAutomatico.removeAll();
                        jPanelTabuleiroAutomatico.add(jCentraliza, BorderLayout.CENTER);
                        jPanelTabuleiroAutomatico.revalidate();
                    }
                }
            });
            
            iniciaPartidaAutomatica();             
        }
        
        
    }
    
    public ConjuntoHeuristico getJogadorBrancas(){
        
        if (partida.getJogadorBranco().getTipo() == Jogador.Tipo.ARTIFICIAL){
            return partida.getJogadorBranco().getConjuntoHeuristico();
        }else{
            return null;
        }
    }

    public ConjuntoHeuristico getJogadorPretas(){
        
        if (partida.getJogadorPreto().getTipo() == Jogador.Tipo.ARTIFICIAL){
            return partida.getJogadorPreto().getConjuntoHeuristico();
        }else{
            return null;
        }
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return null;
    }
    
    public boolean isFlipTabuleiroPretas(){
        return flipTabuleiroPretas;
    }
    
    public void setFlipTabuleiroPretas(boolean flipTabuleiroPretas){
        
        this.flipTabuleiroPretas = flipTabuleiroPretas;
        
        if (!partida.isPassoAPasso()){
            
            panelCapturesUp.setWhiteCaptured(!flipTabuleiroPretas);
            desenhaTabuleiro.setFlipView(flipTabuleiroPretas);
            panelCapturesDown.setWhiteCaptured(flipTabuleiroPretas);
            
        } else {
            if (desenhaTabuleiro.getTabuleiro() != null) {

                boolean whiteColor = desenhaTabuleiro.getTabuleiro().isWhiteActive();

                Lance move = desenhaTabuleiro.getHighlightedMove();

                if (move == null) {
                    if (!whiteColor) {
                        desenhaTabuleiro.setFlipView(flipTabuleiroPretas);
                    }
                } else {
                    if (!move.getPeca().isWhite()) {
                        desenhaTabuleiro.setFlipView(flipTabuleiroPretas);
                    }
                }
            }
        }
    }
    
    private void confirmaSaida() {
        
        if (partida.getState() == IN_PROGRESS) {
            
            int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar e sair desta partida?", "Confirmação de Saída");
            if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                return;
            }
            
            partida.cancel();
            
            if (!partida.isPassoAPasso()){
                if (HeuChess.somAtivado) {
                    HeuChess.somPartidaCancelada.play();
                }
            }
        }
        
        dispose();        
                
        if (fechouTelaEditorBrancas) {
            reabrirTelaEditorConjuntoHeuristico(getJogadorBrancas());
        }
        
        if (fechouTelaEditorPretas) {
            reabrirTelaEditorConjuntoHeuristico(getJogadorPretas());
        }
        
        if (!fechouTelaEditorBrancas && !fechouTelaEditorPretas){
            HeuChess.telaPrincipal.desvincultarTrazerOutraTelaFrente(this);
        }else{
            HeuChess.telaPrincipal.telasPartidaXadrez.remove(this);
        }
    }
    
    private void reabrirTelaEditorConjuntoHeuristico(final ConjuntoHeuristico conjuntoHeuristico) {
        
        for (int indice = 0; indice < HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.size(); indice++) {

            TelaEditorConjuntoHeuristico tela = HeuChess.telaPrincipal.telasEditorConjuntoHeuristico.get(indice);

            if (conjuntoHeuristico.getId() == tela.idConjuntoHeuristico() && tela.abriuLeituraPorCausaPartida()) {
                tela.dispose();
                break;
            }
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                
                TelaEditorConjuntoHeuristico tela = new TelaEditorConjuntoHeuristico(TelaPartidaXadrez.this, conjuntoHeuristico);
            }
        });
    }
    
    private void proximoPassoAnalise() {
        
        mostraDadosJogadorAtual();

        ////////////////////////////////
        // Bloqueia e Limpa Interface //
        ////////////////////////////////

        jLabelArvoreLances.setText("Árvore de Lances Gerados");
        jTreeArvoreLances.setEnabled(false);
            
        mostrarTabuleiroAtual();
        
        habilitaBotoesNavegacaoPartidaManual(false);
            
        jTextFieldMelhorAvaliacao.setText(null);
        jTextFieldPiorAvaliacao.setText(null);
            
        if (partida.getState() == IN_PROGRESS) {
            
            jTreeArvoreLances.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("...Gerando Lances Possíveis...")));

            panelRelogio.setTexto("...Processando...");

            jTextFieldLanceEscolhido.setText("...Calculando Próximo Lance...");            
     
            AtualizaPartida atualizaPartida = new AtualizaPartida();
            atualizaPartida.start();
            
        } else {
            
            jTreeArvoreLances.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("...Nenhum Lance Possível...")));
            
            panelRelogio.setTexto("Nenhum");
            
            jTextFieldLanceEscolhido.setText("Nenhum Lance Possível");
           
            if (HeuChess.somAtivado) {
                HeuChess.somPartidaConcluida.play();
            }
             
            UtilsGUI.dialogoAtencao(this,"A partida já está terminada!");
        }
    }

    private void iniciaPartidaAutomatica(){
        
        jButtonReiniciarPartida.setEnabled(false);
        
        jButtonPrimeiro.setEnabled(false);
        jButtonAnterior.setEnabled(false);
        jButtonProximo.setEnabled(false);        
        jButtonUltimo.setEnabled(false);
        
        partida.restart();
        
        jButtonCancelarPartida.setEnabled(true);
        
        jLabelFENTabuleiro.setText("FEN do Tabuleiro Atual");
            
        AtualizaPartida atualizaPartida = new AtualizaPartida();
        atualizaPartida.start();
    }
    
    private void mostraDadosJogadorAtual() {
        
        tabuleiroInicial = partida.getTabuleiro();

        jTextFieldFENRecebido.setText(tabuleiroInicial.getFEN());

        if (partida.isPassoAPasso() && partida.isTerminou()) {
            jLabelNumeroJogadas.setText("Lance " + (partida.getCurrentLanceIndex() + 1) + " de " + partida.getTotalLancesPartida());
        } else {
            jLabelNumeroJogadas.setText("Lance " + (partida.getCurrentLanceIndex() + 1));
        }

        atualizarInformacaoJogador(tabuleiroInicial.isWhiteActive());
    }
    
    private void mostraDadosHistorico() {
        
        tabuleiroInicial = partida.getTabuleiro();
        
        boolean mostrarBranco;
        
        if (partida.getCurrentLanceIndex() == -1){
            jLabelFENTabuleiro.setText("FEN Tabuleiro Inicial");
            mostrarBranco = tabuleiroInicial.isWhiteActive();
        }else{
            jLabelFENTabuleiro.setText("FEN Resultado Lance");
            mostrarBranco = !tabuleiroInicial.isWhiteActive();
        }
        
        jTextFieldFENRecebido.setText(tabuleiroInicial.getFEN());

        jLabelNumeroJogadas.setText("Lance " + (partida.getCurrentLanceIndex() + 1) + " de " + partida.getMovesCount());

        atualizarInformacaoJogador(mostrarBranco);
    }
    
    private void atualizarInformacaoJogador(boolean corBranca){

        Jogador jogadorAtual = corBranca ? partida.getJogadorBranco() : partida.getJogadorPreto();
        
        engineAtual = (EngineAnalise) jogadorAtual.getEngine();   
        
        if (partida.isPassoAPasso()) {
            panelPecasCapturadas.setWhiteCaptured(!corBranca);
        }
        
        jLabelIconeCor.setIcon(corBranca ? iconeBrancas : iconePretas);        
        jTextFieldProfundidadeBusca.setText(Integer.toString(engineAtual.getProfundidadeBusca()));        
        
        setTitle("Confronto Heurístico - Vez das " + (corBranca ? "Brancas" : "Pretas") + " jogarem...");
        
        jTextFieldNomeConjuntoHeuristico.setText("Conjunto Heurístico - " + jogadorAtual.getConjuntoHeuristico().getNome() + " - " + 
                                                 engineAtual.getDescricao());
    }
    
    private void mostraDadosAnaliticosJogadaEscolhida() {
            
        if (partida.isTerminou()) {
            jLabelNumeroJogadas.setText("Lance " + partida.getMovesCount() + " de " + partida.getTotalLancesPartida());
        } else {
            jLabelNumeroJogadas.setText("Lance " + partida.getMovesCount());
        }

        jLabelArvoreLances.setText("Árvore de Lances Gerados - Total de " + UtilsString.formataDouble("###,###,###", engineAtual.getTotalMovimentos()));

        panelRelogio.setTexto(UtilsDataTempo.formataTempoNanossegundos(engineAtual.getTempoGastoNanossegundos(), true));

        DefaultMutableTreeNode treeTabuleiroAtual = new DefaultMutableTreeNode("Tabuleiro Atual");

        for (LanceTreeNode node : engineAtual.getRaizes()) {
            treeTabuleiroAtual.add(node);
        }
        
        jTreeArvoreLances.setModel(new DefaultTreeModel(treeTabuleiroAtual));
        jTreeArvoreLances.setEnabled(true);

        CasoAvaliado melhorCaso = engineAtual.getMelhorCaso();
        if (melhorCaso.getQuantidadeIguais() == 0) {
            jTextFieldMelhorAvaliacao.setText(DHJOG.textoValorTabuleiro(melhorCaso.getValor(), true));
        } else {
            jTextFieldMelhorAvaliacao.setText(DHJOG.textoValorTabuleiro(melhorCaso.getValor(), true) + " ("
                    + UtilsString.formataDouble("###,###,###", melhorCaso.getQuantidadeIguais() + 1) + " iguais)");
        }

        CasoAvaliado piorCaso = engineAtual.getPiorCaso();
        if (piorCaso.getQuantidadeIguais() == 0) {
            jTextFieldPiorAvaliacao.setText(DHJOG.textoValorTabuleiro(piorCaso.getValor(), true));
        } else {
            jTextFieldPiorAvaliacao.setText(DHJOG.textoValorTabuleiro(piorCaso.getValor(), true) + " ("
                    + UtilsString.formataDouble("###,###,###", piorCaso.getQuantidadeIguais() + 1) + " iguais)");
        }

        jButtonLocalizaMelhorAvaliacao.setEnabled(true);
        jButtonLocalizaPiorAvaliacao.setEnabled(true);
        jButtonLocalizarLanceEscolhido.setEnabled(true);

        if (movimentoEscolhido != null) {

            jTextFieldLanceEscolhido.setText(movimentoEscolhido.toString() + " (" + DHJOG.textoValorTabuleiro(engineAtual.getValor(), true) + ")");

            DefaultMutableTreeNode nodeRoot = (DefaultMutableTreeNode) jTreeArvoreLances.getModel().getRoot();
            localizaTreeNodeFilho(nodeRoot, movimentoEscolhido, true);
        }

        habilitaBotoesNavegacaoPartidaManual(true);
    }
    
    private Estado imprimiFinalPartida() {
        
        Partida.Estado estadoAtual = partida.getState();

        if (estadoAtual != IN_PROGRESS) {

            setTitle("Confronto Heurístico - " + estadoAtual.getDescricao());
            
            if (estadoAtual != Partida.Estado.CANCELED) {
                
                if (HeuChess.somAtivado) {
                    HeuChess.somPartidaConcluida.play();
                }
            }
        }
        
        return estadoAtual;
    }
    
    private void habilitaBotoesNavegacaoPartidaAutomatica() {        
        jButtonPrimeiro.setEnabled(partida.getCurrentLanceIndex() >= 0);
        jButtonAnterior.setEnabled(partida.getCurrentLanceIndex() >= 0);
        jButtonProximo.setEnabled(partida.getCurrentLanceIndex()  < partida.getMovesCount() - 1);
        jButtonUltimo.setEnabled(partida.getCurrentLanceIndex()   < partida.getMovesCount() - 1);
    }
    
    private void habilitaBotoesNavegacaoPartidaManual(boolean ativar){
        
        jButtonLocalizaMelhorAvaliacao.setEnabled(ativar);
        jButtonLocalizaPiorAvaliacao.setEnabled(ativar);
        jButtonLocalizarLanceEscolhido.setEnabled(ativar);
        
        jTreeArvoreLances.setEnabled(ativar);        
        
        if (!ativar){
            
            jButtonPrimeiro.setEnabled(false);
            jButtonAnterior.setEnabled(false);
            jButtonProximo.setEnabled(false);
            jButtonUltimo.setEnabled(false);
            
        } else {
            
            jButtonPrimeiro.setEnabled(partida.getCurrentLanceIndex() >= 1);
            jButtonAnterior.setEnabled(partida.getCurrentLanceIndex() >= 1);

            if (partida.getState() == IN_PROGRESS) {
                jButtonProximo.setEnabled(true);
                jButtonUltimo.setEnabled(partida.isTerminou());
            } else {
                jButtonProximo.setEnabled(false);
                jButtonUltimo.setEnabled(false);
            }
        }
    }
    
    private LanceTreeNode localizaTreeNodeFilho(DefaultMutableTreeNode nodeRoot, Lance move, boolean selecionar){
        
        TreeNode node;
        
        for (int x = 0; x < nodeRoot.getChildCount(); x++) {
            
            node = nodeRoot.getChildAt(x);
            
            if (node instanceof LanceTreeNode) {

                LanceTreeNode lanceTreeNode = (LanceTreeNode) node;

                if (lanceTreeNode.getLance().equals(move)) {

                    if (selecionar) {               
                        UtilsTree.selecionaTreeNode(jTreeArvoreLances, lanceTreeNode);
                        atualizaTreeArvoresLances();
                        
                        /*
                         if (nivel > 1){
                                int valor  = jScrollPaneArvoreLances.getVerticalScrollBar().getValue();
				int maximo = jScrollPaneArvoreLances.getVerticalScrollBar().getMaximum();
				int deslocamento = (valor + 50) < maximo ? (valor + 50) : maximo;
				jScrollPaneArvoreLances.getVerticalScrollBar().setValue(deslocamento);                    
                         }*/
                    }
                    
                    return lanceTreeNode;
                }
            }
        }
        
        return null;        
    }
    
    private void verificaTreeArvoreLances(){
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeArvoreLances.getLastSelectedPathComponent();

        if (node != null) {
            
            if (node instanceof LanceTreeNode){
                
                LanceTreeNode moveTreeNode = (LanceTreeNode) node;
                
                boolean whiteColor = moveTreeNode.getTabuleiro().isWhiteActive();
                
                Tabuleiro tabuleiro = moveTreeNode.getTabuleiroDerivado();
                
                if (whiteColor){
                    desenhaTabuleiro.setFlipView(false);                    
                }else{
                    desenhaTabuleiro.setFlipView(flipTabuleiroPretas);
                }
                
                desenhaTabuleiro.showTabuleiro(tabuleiro, moveTreeNode.getLance());
                
                jPanelDetalhesLance.setVisible(true);                
                
                if (!moveTreeNode.isLeaf()){
                    
                    cardPanelDetalhesLance.show(jPanelDetalhesLance,"LanceRaiz");
                    
                    if (moveTreeNode.getLevel() % 2 == 1){
                        jLabelDescricaoNo.setText("Recebe o Menor valor vindo dos Filhos");
                    }else{
                        jLabelDescricaoNo.setText("Recebe o Maior valor vindo dos Filhos");
                    }
                    
                    jTextFieldNoRaizValorHeuristico.setText(DHJOG.textoValorTabuleiro(moveTreeNode.getValor(),true));
                    jTextFieldNoRaizTotalFilhos.setText(UtilsString.formataDouble("###,###,###",moveTreeNode.getTotalFilhos()));
                    jTextFieldNoRaizTotalAvaliacoes.setText(UtilsString.formataDouble("###,###,###",moveTreeNode.getTotalAvaliacoes()));
                    
                }else{
                    cardPanelDetalhesLance.show(jPanelDetalhesLance,"LanceTerminal");
                                        
                    if (moveTreeNode.getValor() == DHJOG.EMPATE){
                        if (moveTreeNode.tipoEmpate() == Partida.Estado.DRAWN_BY_50_MOVE_RULE){
                            jLabelDescricaoNo.setText("Empate pela Regra de 50 Movimentos");
                        }else
                            if (moveTreeNode.tipoEmpate() == Partida.Estado.DRAWN_BY_TRIPLE_REPETITION){
                                jLabelDescricaoNo.setText("Empate pela Regra de Tripla Repetição");
                            }else
                                if (moveTreeNode.tipoEmpate() == Partida.Estado.STALEMATE){
                                    jLabelDescricaoNo.setText("Empate pois o Rei " + (tabuleiro.isWhiteActive() ? "Branco" : "Preto" ) + " ficou Afogado");
                                }
                    }else
                        if (moveTreeNode.getValor() == DHJOG.XEQUE_MATE_EU){
                            jLabelDescricaoNo.setText("Xeque-mate do Jogador " + (tabuleiroInicial.isWhiteActive() ? "Branco" : "Preto"));    
                        }else
                            if (moveTreeNode.getValor() == DHJOG.XEQUE_MATE_OPONENTE){
                                jLabelDescricaoNo.setText("Xeque-mate do Jogador " + (tabuleiroInicial.isWhiteActive() ? "Preto" : "Branco") + " (Adversário)");
                            }else
                                if (moveTreeNode.getLevel() == engineAtual.getProfundidadeBusca()){
                                    jLabelDescricaoNo.setText("Fim da Profundidade de Busca");
                                }else{
                                    jLabelDescricaoNo.setText(null);
                                }
                    
                    jTextFieldNoTerminalValorHeuristico.setText(DHJOG.textoValorTabuleiro(moveTreeNode.getValor(),true));
                    jTextFieldNoTerminalAltura.setText(String.valueOf(moveTreeNode.getLevel()));
                }
            }else{
                mostrarTabuleiroAtual();
            }
        }
    }
    
    public void atualizaTreeArvoresLances(){
        jTreeArvoreLances.invalidate();
        jTreeArvoreLances.revalidate();
        jTreeArvoreLances.repaint();
    }
    
    private void mostrarTabuleiroAtual() {
        
        if (tabuleiroInicial.isWhiteActive()) {
            desenhaTabuleiro.setFlipView(false);
        } else {
            desenhaTabuleiro.setFlipView(flipTabuleiroPretas);
        }

        desenhaTabuleiro.showTabuleiro(tabuleiroInicial, null);
        
        jLabelDescricaoNo.setText("Tabuleiro para Escolha do Lance");
        
        jPanelDetalhesLance.setVisible(false);       
    }
  
    @Override
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        
        Object obj = e.getPath().getLastPathComponent();
        
        if (obj instanceof LanceTreeNode){
        
            LanceTreeNode lanceTreeNode = (LanceTreeNode) obj;
            
            if (lanceTreeNode.getChildCount() == 0) {
                
                // Expandido pela primeira vez //
                
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode("Calculando..");
                lanceTreeNode.add(tempNode);
                
                CriaNosArvoreLances criaNosArvoresLances = new CriaNosArvoreLances(lanceTreeNode);
                criaNosArvoresLances.start();
            }
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent e) {
        
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        verificaTreeArvoreLances();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelIconeCor = new javax.swing.JLabel();
        jTextFieldNomeConjuntoHeuristico = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldProfundidadeBusca = new javax.swing.JTextField();
        jLabelFENTabuleiro = new javax.swing.JLabel();
        jTextFieldFENRecebido = new javax.swing.JTextField();
        jPanelDireito = new javax.swing.JPanel();
        jLabelNumeroJogadas = new javax.swing.JLabel();
        jPanelCentro = new javax.swing.JPanel();
        jPanelPartidaAutomatica = new javax.swing.JPanel();
        jLabelRelogioPartidaAutomatica = new javax.swing.JLabel();
        jPanelTabuleiroAutomatico = new javax.swing.JPanel();
        jPanelPartidaManual = new javax.swing.JPanel();
        jPanelLancesAnalisados = new javax.swing.JPanel();
        jLabelArvoreLances = new javax.swing.JLabel();
        jScrollPaneArvoreLances = new javax.swing.JScrollPane();
        jTreeArvoreLances = new javax.swing.JTree();
        jLabel2 = new javax.swing.JLabel();
        jPanelTabuleiro = new javax.swing.JPanel();
        jLabelDescricaoNo = new javax.swing.JLabel();
        jPanelDetalhesLance = new javax.swing.JPanel();
        jPanelLanceTerminal = new javax.swing.JPanel();
        jTextFieldNoTerminalValorHeuristico = new javax.swing.JTextField();
        jLabelValorHeuristicoTabuleiro = new javax.swing.JLabel();
        jPanelBotaoDetalhesAnalise = new javax.swing.JPanel();
        jButtonAbrirDetalhesAnaliseHeuristica = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldNoTerminalAltura = new javax.swing.JTextField();
        jPanelLanceRaiz = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldNoRaizTotalFilhos = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldNoRaizTotalAvaliacoes = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldNoRaizValorHeuristico = new javax.swing.JTextField();
        jPanelRelogio = new javax.swing.JPanel();
        jLabelRelogioPartidaManual = new javax.swing.JLabel();
        jPanelResultado = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldLanceEscolhido = new javax.swing.JTextField();
        jButtonLocalizarLanceEscolhido = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldMelhorAvaliacao = new javax.swing.JTextField();
        jButtonLocalizaMelhorAvaliacao = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPiorAvaliacao = new javax.swing.JTextField();
        jButtonLocalizaPiorAvaliacao = new javax.swing.JButton();
        jButtonFechar = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();
        jPanelBotoesMovimentacao = new javax.swing.JPanel();
        jButtonReiniciarPartida = new javax.swing.JButton();
        jButtonCancelarPartida = new javax.swing.JButton();
        jButtonPrimeiro = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonUltimo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/icones/icon16.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelIconeCor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelIconeCor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/white22.png"))); // NOI18N

        jTextFieldNomeConjuntoHeuristico.setEditable(false);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Profundidade de Busca");

        jTextFieldProfundidadeBusca.setEditable(false);
        jTextFieldProfundidadeBusca.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelFENTabuleiro.setText("FEN do Tabuleiro Atual");

        jTextFieldFENRecebido.setEditable(false);
        jTextFieldFENRecebido.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanelDireito.setLayout(new java.awt.BorderLayout());

        jLabelNumeroJogadas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNumeroJogadas.setText("Lance 1");

        jPanelCentro.setLayout(new java.awt.CardLayout());

        jPanelPartidaAutomatica.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelRelogioPartidaAutomatica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRelogioPartidaAutomatica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/relogio1.png"))); // NOI18N

        jPanelTabuleiroAutomatico.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanelPartidaAutomaticaLayout = new javax.swing.GroupLayout(jPanelPartidaAutomatica);
        jPanelPartidaAutomatica.setLayout(jPanelPartidaAutomaticaLayout);
        jPanelPartidaAutomaticaLayout.setHorizontalGroup(
            jPanelPartidaAutomaticaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPartidaAutomaticaLayout.createSequentialGroup()
                .addComponent(jPanelTabuleiroAutomatico, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jLabelRelogioPartidaAutomatica, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelPartidaAutomaticaLayout.setVerticalGroup(
            jPanelPartidaAutomaticaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPartidaAutomaticaLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelRelogioPartidaAutomatica, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(403, Short.MAX_VALUE))
            .addComponent(jPanelTabuleiroAutomatico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelCentro.add(jPanelPartidaAutomatica, "PartidaAutomatica");

        jPanelLancesAnalisados.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelArvoreLances.setText("Árvore de Lances Gerados");

        jTreeArvoreLances.setShowsRootHandles(true);
        jTreeArvoreLances.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeArvoreLances.setCellRenderer(renderTreeJogadas);
        jTreeArvoreLances.setToggleClickCount(1);
        jTreeArvoreLances.setScrollsOnExpand(true);
        jScrollPaneArvoreLances.setViewportView(jTreeArvoreLances);

        jLabel2.setText("Lance Selecionado na Árvore");

        jPanelTabuleiro.setLayout(new java.awt.BorderLayout());

        jLabelDescricaoNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDescricaoNo.setText("Descrição do Nó");

        jPanelDetalhesLance.setLayout(new java.awt.CardLayout());

        jTextFieldNoTerminalValorHeuristico.setEditable(false);
        jTextFieldNoTerminalValorHeuristico.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabelValorHeuristicoTabuleiro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelValorHeuristicoTabuleiro.setText(" Valor Heurístico");

        jPanelBotaoDetalhesAnalise.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jButtonAbrirDetalhesAnaliseHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_detalhes_avaliacao_heuristica.png"))); // NOI18N
        jButtonAbrirDetalhesAnaliseHeuristica.setMnemonic('d');
        jButtonAbrirDetalhesAnaliseHeuristica.setText("Detalhes da Análise");
        jButtonAbrirDetalhesAnaliseHeuristica.setToolTipText("Ver os detalhes da Análise Heurística desta Situação de Jogo");
        jButtonAbrirDetalhesAnaliseHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirDetalhesAnaliseHeuristicaActionPerformed(evt);
            }
        });
        jPanelBotaoDetalhesAnalise.add(jButtonAbrirDetalhesAnaliseHeuristica);

        jLabel10.setText(" Altura");

        jTextFieldNoTerminalAltura.setEditable(false);
        jTextFieldNoTerminalAltura.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanelLanceTerminalLayout = new javax.swing.GroupLayout(jPanelLanceTerminal);
        jPanelLanceTerminal.setLayout(jPanelLanceTerminalLayout);
        jPanelLanceTerminalLayout.setHorizontalGroup(
            jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanelLanceTerminalLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelBotaoDetalhesAnalise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelLanceTerminalLayout.createSequentialGroup()
                        .addGroup(jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelValorHeuristicoTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldNoTerminalValorHeuristico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldNoTerminalAltura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(3, 3, 3))
        );
        jPanelLanceTerminalLayout.setVerticalGroup(
            jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLanceTerminalLayout.createSequentialGroup()
                .addGroup(jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelValorHeuristicoTabuleiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldNoTerminalValorHeuristico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanelLanceTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldNoTerminalAltura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jPanelBotaoDetalhesAnalise, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelDetalhesLance.add(jPanelLanceTerminal, "LanceTerminal");

        jLabel4.setText(" Total de Filhos");

        jTextFieldNoRaizTotalFilhos.setEditable(false);
        jTextFieldNoRaizTotalFilhos.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel6.setText(" Total de Avaliações");

        jTextFieldNoRaizTotalAvaliacoes.setEditable(false);
        jTextFieldNoRaizTotalAvaliacoes.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setText(" Valor Heurístico");

        jTextFieldNoRaizValorHeuristico.setEditable(false);
        jTextFieldNoRaizValorHeuristico.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanelLanceRaizLayout = new javax.swing.GroupLayout(jPanelLanceRaiz);
        jPanelLanceRaiz.setLayout(jPanelLanceRaizLayout);
        jPanelLanceRaizLayout.setHorizontalGroup(
            jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLanceRaizLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLanceRaizLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextFieldNoRaizValorHeuristico, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelLanceRaizLayout.createSequentialGroup()
                        .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldNoRaizTotalFilhos, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(jTextFieldNoRaizTotalAvaliacoes))))
                .addGap(3, 3, 3))
        );
        jPanelLanceRaizLayout.setVerticalGroup(
            jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLanceRaizLayout.createSequentialGroup()
                .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldNoRaizValorHeuristico)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldNoRaizTotalFilhos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanelLanceRaizLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNoRaizTotalAvaliacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );

        jPanelDetalhesLance.add(jPanelLanceRaiz, "LanceRaiz");

        jPanelRelogio.setPreferredSize(new java.awt.Dimension(240, 70));
        jPanelRelogio.setLayout(new java.awt.BorderLayout());

        jLabelRelogioPartidaManual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRelogioPartidaManual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/relogio1.png"))); // NOI18N
        jPanelRelogio.add(jLabelRelogioPartidaManual, java.awt.BorderLayout.CENTER);

        jPanelDetalhesLance.add(jPanelRelogio, "Relogio");

        javax.swing.GroupLayout jPanelLancesAnalisadosLayout = new javax.swing.GroupLayout(jPanelLancesAnalisados);
        jPanelLancesAnalisados.setLayout(jPanelLancesAnalisadosLayout);
        jPanelLancesAnalisadosLayout.setHorizontalGroup(
            jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLancesAnalisadosLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneArvoreLances)
                    .addComponent(jLabelArvoreLances, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addGroup(jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDescricaoNo, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelDetalhesLance, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelTabuleiro, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanelLancesAnalisadosLayout.setVerticalGroup(
            jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLancesAnalisadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelArvoreLances)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5)
                .addGroup(jPanelLancesAnalisadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelLancesAnalisadosLayout.createSequentialGroup()
                        .addComponent(jPanelTabuleiro, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabelDescricaoNo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanelDetalhesLance, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                    .addComponent(jScrollPaneArvoreLances))
                .addGap(5, 5, 5))
        );

        jPanelResultado.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Lance Escolhido");

        jTextFieldLanceEscolhido.setEditable(false);
        jTextFieldLanceEscolhido.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButtonLocalizarLanceEscolhido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_localizar_lance_escolhido.png"))); // NOI18N
        jButtonLocalizarLanceEscolhido.setToolTipText("Localiza o lance escolhido pelo Jogador Automático");
        jButtonLocalizarLanceEscolhido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocalizarLanceEscolhidoActionPerformed(evt);
            }
        });

        jLabel1.setText("Melhor Avaliação");

        jTextFieldMelhorAvaliacao.setEditable(false);
        jTextFieldMelhorAvaliacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButtonLocalizaMelhorAvaliacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_localizar_melhor_avaliacao.png"))); // NOI18N
        jButtonLocalizaMelhorAvaliacao.setToolTipText("Localiza umas das Situações de Jogo calculada como mais vantajosa");
        jButtonLocalizaMelhorAvaliacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocalizaMelhorAvaliacaoActionPerformed(evt);
            }
        });

        jLabel3.setText("Pior Avaliação");

        jTextFieldPiorAvaliacao.setEditable(false);
        jTextFieldPiorAvaliacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPiorAvaliacao.setToolTipText("");

        jButtonLocalizaPiorAvaliacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_localizar_pior_avaliacao.png"))); // NOI18N
        jButtonLocalizaPiorAvaliacao.setToolTipText("Localiza umas das Situações de Jogo calculada como mais prejudicial");
        jButtonLocalizaPiorAvaliacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocalizaPiorAvaliacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelResultadoLayout = new javax.swing.GroupLayout(jPanelResultado);
        jPanelResultado.setLayout(jPanelResultadoLayout);
        jPanelResultadoLayout.setHorizontalGroup(
            jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelResultadoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelResultadoLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextFieldLanceEscolhido, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(jButtonLocalizarLanceEscolhido, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelResultadoLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextFieldMelhorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButtonLocalizaMelhorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPiorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButtonLocalizaPiorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)))
                .addGap(5, 5, 5))
        );
        jPanelResultadoLayout.setVerticalGroup(
            jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelResultadoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelResultadoLayout.createSequentialGroup()
                        .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonLocalizarLanceEscolhido, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jTextFieldLanceEscolhido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3)
                        .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelResultadoLayout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextFieldPiorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jTextFieldMelhorAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jButtonLocalizaPiorAvaliacao)))
                    .addComponent(jButtonLocalizaMelhorAvaliacao))
                .addGap(5, 5, 5))
        );

        jPanelResultadoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonLocalizaMelhorAvaliacao, jButtonLocalizaPiorAvaliacao});

        javax.swing.GroupLayout jPanelPartidaManualLayout = new javax.swing.GroupLayout(jPanelPartidaManual);
        jPanelPartidaManual.setLayout(jPanelPartidaManualLayout);
        jPanelPartidaManualLayout.setHorizontalGroup(
            jPanelPartidaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelLancesAnalisados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelPartidaManualLayout.setVerticalGroup(
            jPanelPartidaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPartidaManualLayout.createSequentialGroup()
                .addComponent(jPanelLancesAnalisados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelResultado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelCentro.add(jPanelPartidaManual, "PartidaManual");

        jButtonFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jButtonFechar.setMnemonic('f');
        jButtonFechar.setText("Fechar");
        jButtonFechar.setToolTipText("Cancela a partida e fecha a janela");
        jButtonFechar.setPreferredSize(new java.awt.Dimension(93, 25));
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

        jPanelBotoesMovimentacao.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButtonReiniciarPartida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/reset16.png"))); // NOI18N
        jButtonReiniciarPartida.setMnemonic('r');
        jButtonReiniciarPartida.setToolTipText("Reinicia a Partida");
        jButtonReiniciarPartida.setPreferredSize(new java.awt.Dimension(57, 31));
        jButtonReiniciarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReiniciarPartidaActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonReiniciarPartida);

        jButtonCancelarPartida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/exit16.png"))); // NOI18N
        jButtonCancelarPartida.setMnemonic('i');
        jButtonCancelarPartida.setToolTipText("Interrompe a Partida");
        jButtonCancelarPartida.setPreferredSize(new java.awt.Dimension(57, 31));
        jButtonCancelarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarPartidaActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonCancelarPartida);

        jButtonPrimeiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/first.png"))); // NOI18N
        jButtonPrimeiro.setToolTipText("Retorna para o início da partida");
        jButtonPrimeiro.setEnabled(false);
        jButtonPrimeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrimeiroActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonPrimeiro);

        jButtonAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/previous.png"))); // NOI18N
        jButtonAnterior.setToolTipText("Vai para o lance anterior (ply)");
        jButtonAnterior.setEnabled(false);
        jButtonAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnteriorActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonAnterior);

        jButtonProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/next.png"))); // NOI18N
        jButtonProximo.setToolTipText("Vai para o próximo lance (ply)");
        jButtonProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProximoActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonProximo);

        jButtonUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/last.png"))); // NOI18N
        jButtonUltimo.setToolTipText("Vai para o último lance (ply)");
        jButtonUltimo.setEnabled(false);
        jButtonUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUltimoActionPerformed(evt);
            }
        });
        jPanelBotoesMovimentacao.add(jButtonUltimo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelFENTabuleiro, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTextFieldFENRecebido))
                            .addComponent(jPanelCentro, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanelDireito, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelNumeroJogadas, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanelBotoesMovimentacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3)
                        .addComponent(jButtonFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabelIconeCor, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextFieldNomeConjuntoHeuristico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldProfundidadeBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldNomeConjuntoHeuristico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldProfundidadeBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(jLabelIconeCor))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelNumeroJogadas)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelFENTabuleiro)
                        .addComponent(jTextFieldFENRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelDireito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelCentro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonFechar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAjuda)
                    .addComponent(jPanelBotoesMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-808)/2, (screenSize.height-627)/2, 808, 627);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaPartidaXadrez");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        confirmaSaida();
    }//GEN-LAST:event_jButtonFecharActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaSaida();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        
        if (partida.isPassoAPasso()){
            proximoPassoAnalise();
        }else{
            partida.goNext();
            mostraDadosHistorico(); 
            habilitaBotoesNavegacaoPartidaAutomatica();
        }
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonPrimeiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrimeiroActionPerformed
        
        partida.goFirst();
        
        if (partida.isPassoAPasso()){
            proximoPassoAnalise();
        }else{
            mostraDadosHistorico();
            habilitaBotoesNavegacaoPartidaAutomatica();
        }
    }//GEN-LAST:event_jButtonPrimeiroActionPerformed

    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
        partida.goPrevious();
        
        if (partida.isPassoAPasso()){         
            partida.goPrevious(); // Necessário chamar outra vez pois a jogada já foi efetivada
            proximoPassoAnalise();
        }else{
            mostraDadosHistorico();
            habilitaBotoesNavegacaoPartidaAutomatica();
        }
    }//GEN-LAST:event_jButtonAnteriorActionPerformed

    private void jButtonUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUltimoActionPerformed
        
        partida.goLast();
        
        if (partida.isPassoAPasso()){
            proximoPassoAnalise();
        }else{
            mostraDadosHistorico();
            habilitaBotoesNavegacaoPartidaAutomatica();
        }
    }//GEN-LAST:event_jButtonUltimoActionPerformed

    private void jButtonLocalizarLanceEscolhidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocalizarLanceEscolhidoActionPerformed
        DefaultMutableTreeNode nodeRoot = (DefaultMutableTreeNode) jTreeArvoreLances.getModel().getRoot();
        localizaTreeNodeFilho(nodeRoot, movimentoEscolhido, true);
    }//GEN-LAST:event_jButtonLocalizarLanceEscolhidoActionPerformed

    private void jButtonLocalizaPiorAvaliacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocalizaPiorAvaliacaoActionPerformed
        CriaNosArvoreLances criaNosArvoresLances = new CriaNosArvoreLances(engineAtual.getPiorCaso());
        criaNosArvoresLances.start();
    }//GEN-LAST:event_jButtonLocalizaPiorAvaliacaoActionPerformed

    private void jButtonLocalizaMelhorAvaliacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocalizaMelhorAvaliacaoActionPerformed
        CriaNosArvoreLances criaNosArvoresLances = new CriaNosArvoreLances(engineAtual.getMelhorCaso());
        criaNosArvoresLances.start();
    }//GEN-LAST:event_jButtonLocalizaMelhorAvaliacaoActionPerformed

    private void jButtonAbrirDetalhesAnaliseHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirDetalhesAnaliseHeuristicaActionPerformed
     
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeArvoreLances.getLastSelectedPathComponent();

        if (!(node instanceof LanceTreeNode)) {
            return;
        }

        final Tabuleiro tabuleiro = ((LanceTreeNode) node).getTabuleiroDerivado();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaDetalhesAvaliacaoHeuristica tela = new TelaDetalhesAvaliacaoHeuristica(TelaPartidaXadrez.this, engineAtual, tabuleiro, tabuleiroInicial.isWhiteActive());
            }
        });   
    }//GEN-LAST:event_jButtonAbrirDetalhesAnaliseHeuristicaActionPerformed

    private void jButtonReiniciarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReiniciarPartidaActionPerformed

        partida.cancel();
        
        iniciaPartidaAutomatica();         
    }//GEN-LAST:event_jButtonReiniciarPartidaActionPerformed

    private void jButtonCancelarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarPartidaActionPerformed
        
         if (partida.getState() == IN_PROGRESS) {
             
            int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente cancelar a partida?", "Confirmação de Interrupção");
            
            if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                return;
            }

            jButtonCancelarPartida.setEnabled(false);
            
            partida.cancel();
            
            jButtonReiniciarPartida.setEnabled(true);
            
            if (HeuChess.somAtivado) {
                HeuChess.somPartidaCancelada.play();
            }
        }
    }//GEN-LAST:event_jButtonCancelarPartidaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirDetalhesAnaliseHeuristica;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelarPartida;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JButton jButtonLocalizaMelhorAvaliacao;
    private javax.swing.JButton jButtonLocalizaPiorAvaliacao;
    private javax.swing.JButton jButtonLocalizarLanceEscolhido;
    private javax.swing.JButton jButtonPrimeiro;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JButton jButtonReiniciarPartida;
    private javax.swing.JButton jButtonUltimo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelArvoreLances;
    private javax.swing.JLabel jLabelDescricaoNo;
    private javax.swing.JLabel jLabelFENTabuleiro;
    private javax.swing.JLabel jLabelIconeCor;
    private javax.swing.JLabel jLabelNumeroJogadas;
    private javax.swing.JLabel jLabelRelogioPartidaAutomatica;
    private javax.swing.JLabel jLabelRelogioPartidaManual;
    private javax.swing.JLabel jLabelValorHeuristicoTabuleiro;
    private javax.swing.JPanel jPanelBotaoDetalhesAnalise;
    private javax.swing.JPanel jPanelBotoesMovimentacao;
    private javax.swing.JPanel jPanelCentro;
    private javax.swing.JPanel jPanelDetalhesLance;
    private javax.swing.JPanel jPanelDireito;
    private javax.swing.JPanel jPanelLanceRaiz;
    private javax.swing.JPanel jPanelLanceTerminal;
    private javax.swing.JPanel jPanelLancesAnalisados;
    private javax.swing.JPanel jPanelPartidaAutomatica;
    private javax.swing.JPanel jPanelPartidaManual;
    private javax.swing.JPanel jPanelRelogio;
    private javax.swing.JPanel jPanelResultado;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JPanel jPanelTabuleiroAutomatico;
    private javax.swing.JScrollPane jScrollPaneArvoreLances;
    private javax.swing.JTextField jTextFieldFENRecebido;
    private javax.swing.JTextField jTextFieldLanceEscolhido;
    private javax.swing.JTextField jTextFieldMelhorAvaliacao;
    private javax.swing.JTextField jTextFieldNoRaizTotalAvaliacoes;
    private javax.swing.JTextField jTextFieldNoRaizTotalFilhos;
    private javax.swing.JTextField jTextFieldNoRaizValorHeuristico;
    private javax.swing.JTextField jTextFieldNoTerminalAltura;
    private javax.swing.JTextField jTextFieldNoTerminalValorHeuristico;
    private javax.swing.JTextField jTextFieldNomeConjuntoHeuristico;
    private javax.swing.JTextField jTextFieldPiorAvaliacao;
    private javax.swing.JTextField jTextFieldProfundidadeBusca;
    private javax.swing.JTree jTreeArvoreLances;
    // End of variables declaration//GEN-END:variables
}
