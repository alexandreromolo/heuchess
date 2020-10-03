package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato;
import br.ufpr.inf.heuchess.competicaoheuristica.Campeonato.Situacao;
import br.ufpr.inf.heuchess.competicaoheuristica.EngineMiniMax;
import br.ufpr.inf.heuchess.persistencia.ConjuntoHeuristicoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.telas.iniciais.RenderTreeObjetos;
import br.ufpr.inf.heuchess.telas.situacaojogo.DesenhaSituacaoJogo;
import br.ufpr.inf.heuchess.telas.situacaojogo.TreeModelConjuntosHeuristicos;
import br.ufpr.inf.heuchess.telas.situacaojogo.TreeModelSituacoesJogo;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.gui.*;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Apr 4, 2013
 */
public class TelaCampeonato extends javax.swing.JFrame implements ModalFrameHierarchy {
    
    private CardLayout card;
    private int        etapaCriacao;    
    
    private Campeonato campeonato;
    
    private TreeModelConjuntosHeuristicos treeModelConjuntosHeuristicos;
    private TreeModelSituacoesJogo        treeModelSituacoesJogo;
    
    private ModalFrameHierarchy acessoTelaCampeonato;
    
    private DesenhaSituacaoJogo desenhaSituacaoJogo = new DesenhaSituacaoJogo(this);
    
    private TableModelConjuntoHeuristico tableModelConjuntosHeuristicos;
    private TableModelPartidas           tableModelPartidas;
    private TableModelClassificacao      tableModelClassificacao;
    
    private static final ImageIcon iconeParar, iconeReiniciar;
        
    static {
        iconeParar     = new ImageIcon(TelaCampeonato.class.getResource("/icones/exit16.png"));    
        iconeReiniciar = new ImageIcon(TelaCampeonato.class.getResource("/icones/reset16.png"));    
    }
        
    private class AtualizaTempo extends Thread {
        
        private boolean executando;
                
        public AtualizaTempo(){
            setDaemon(true);
        }
        
        public boolean isExecutando(){
            return executando;
        }
        
        public void parar(){
            executando = false;
        }
        
        @Override
        public void run(){
            
            executando = true;
            
            do{                
                jTextFieldTempoExecucao.setText(UtilsDataTempo.formataTempoMilissegundos(campeonato.getDuracaoMilissegundos(), true));
                
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    
                }
            }while(executando);
            
            // Última atualização para não perder o 1 segundo parado // 
            
            jTextFieldTempoExecucao.setText(UtilsDataTempo.formataTempoMilissegundos(campeonato.getDuracaoMilissegundos(), false));               
        }
    }
    
    private AtualizaTempo atualizadorTempo;
    
    /**
     * Construtor chamado sem nenhum ElementoLista selecionado
     */
    public TelaCampeonato(ModalFrameHierarchy acessoTelaCampeonato) {
        
        try{
            inicializacaoBasica(acessoTelaCampeonato);
        }catch(Exception e){            
            HeuChess.registraExcecao(e);
            
            acessoTelaCampeonato.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));            
            UtilsGUI.dialogoErro(acessoTelaCampeonato.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
        }
    }

    /**
     * Construtor chamado com um ElementoLista já escolhido
     */
    public TelaCampeonato(ModalFrameHierarchy acessoTelaCampeonato, Class classe, long id) {
        
        try{
            inicializacaoBasica(acessoTelaCampeonato);
        }catch(Exception e){
            HeuChess.registraExcecao(e);            
            
            acessoTelaCampeonato.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));            
            UtilsGUI.dialogoErro(acessoTelaCampeonato.getFrame(),"Erro ao carregar dados!\n" + e.getMessage());
            dispose();
            return;
        }
        
        if (classe == ConjuntoHeuristico.class){
            
            if (treeModelConjuntosHeuristicos.selecionaTreeNode(classe, id)){
                jButtonAdicionarConjuntoHeuristico.setEnabled(true);                
            }            
        }else
            if (classe == SituacaoJogo.class){                    
                treeModelSituacoesJogo.selecionaTreeNode(classe, id);        
            }else{                
                acessoTelaCampeonato.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
                UtilsGUI.dialogoErro(acessoTelaCampeonato.getFrame(),"Tipo de elemento da Lista não suportado por este Construtor [" + classe.getName() + "]");
                dispose();                
            }
    }
    
    private void inicializacaoBasica(ModalFrameHierarchy acessoTelaCampeonato) throws Exception {
        
        etapaCriacao = 1;
        
        this.acessoTelaCampeonato = acessoTelaCampeonato;        
        
        desenhaSituacaoJogo.setEditavel(false);
        
        campeonato = new Campeonato(this);
        
        initComponents();
        
        treeModelConjuntosHeuristicos = new TreeModelConjuntosHeuristicos(jTreeConjuntoHeuristicos);
        treeModelSituacoesJogo        = new TreeModelSituacoesJogo(jTreeSituacoesJogo);
        
        treeModelConjuntosHeuristicos.forceUpdate();
        treeModelConjuntosHeuristicos.expandMeusConjuntos();
        
        treeModelSituacoesJogo.forceUpdate();
        treeModelSituacoesJogo.expandPadrao();        
        treeModelSituacoesJogo.expandMinhasSituacoes();
        
        desenhaSituacaoJogo.setSize(jPanelTabuleiro.getWidth(),jPanelTabuleiro.getHeight());
        
        card = (CardLayout) jPanelPrincipal.getLayout();                
        
        jButtonAnterior.setVisible(false);
        jButtonExecutar.setVisible(false);
        jButtonGerarClassificacao.setVisible(false);
        
        jButtonAdicionarConjuntoHeuristico.setEnabled(false);
        jButtonRetirarConjuntoHeuristico.setEnabled(false);    
        
        jButtonProximo.setEnabled(false);
        
        tableModelConjuntosHeuristicos = new TableModelConjuntoHeuristico(campeonato);
        jTableConjuntosHeuristicos.setModel(tableModelConjuntosHeuristicos);
        jTableConjuntosHeuristicos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
                if (e.getValueIsAdjusting()){
                    return;
                }                
                ListSelectionModel rowSM = (ListSelectionModel)e.getSource();
                int selectedIndex = rowSM.getMinSelectionIndex();
         
                if (selectedIndex == -1){
                    jButtonRetirarConjuntoHeuristico.setEnabled(false);
                }else{
                    jButtonRetirarConjuntoHeuristico.setEnabled(true);
                }
            }                
        });
        
        tableModelPartidas = new TableModelPartidas(campeonato);
        jTablePartidas.setModel(tableModelPartidas);
        
        tableModelClassificacao = new TableModelClassificacao(campeonato);
        jTableClassificacao.setModel(tableModelClassificacao);
        
        acessoTelaCampeonato.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));            
        
        /////////////////////////////////////////////////////////////////////////////////////
        // Define como padrão a quantidade de Núcleos/Processadores Disponíveis para a JVM //
        /////////////////////////////////////////////////////////////////////////////////////
        
        jSpinnerQuantidadePartidasSimultaneas.setValue(new Integer(Runtime.getRuntime().availableProcessors()));
        
        ModalFrameUtil.showAsModalDontBlock(this);
        
        configuraTabelaConjuntos(jTableConjuntosHeuristicos);
        configuraTabelaPartidas(jTablePartidas);
        configuraTabelaClassificacao(jTableClassificacao);
        
        setEnabled(true);
    }
    
    private void configuraTabelaConjuntos(JTable jTable){
        
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer();
        cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer();
        cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable.getColumn(jTable.getColumnName(0)).setCellRenderer(cellRenderCentro);      
        jTable.getColumn(jTable.getColumnName(1)).setCellRenderer(cellRenderEsquerda);
        jTable.getColumn(jTable.getColumnName(2)).setCellRenderer(cellRenderEsquerda);
        jTable.getColumn(jTable.getColumnName(3)).setCellRenderer(cellRenderCentro);
        
        int largura = jTable.getWidth();
        
        jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth((int) (largura * 0.05));
        jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth((int) (largura * 0.40));
        jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth((int) (largura * 0.40));
        jTable.getColumn(jTable.getColumnName(3)).setPreferredWidth((int) (largura * 0.15));
    }
    
    private void configuraTabelaPartidas(JTable jTable){
        
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer();
        cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer();
        cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable.getColumn(jTable.getColumnName(0)).setCellRenderer(cellRenderCentro);   // Nr.    
        jTable.getColumn(jTable.getColumnName(1)).setCellRenderer(cellRenderEsquerda); // Autor Brancas
        jTable.getColumn(jTable.getColumnName(2)).setCellRenderer(cellRenderEsquerda); // Brancas
        jTable.getColumn(jTable.getColumnName(3)).setCellRenderer(cellRenderEsquerda); // Autor Pretas
        jTable.getColumn(jTable.getColumnName(4)).setCellRenderer(cellRenderEsquerda); // Pretas
        jTable.getColumn(jTable.getColumnName(5)).setCellRenderer(cellRenderCentro);   // Situação
        
        int largura = jTable.getWidth();
        
        jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth((int) (largura * 0.05)); // Nr.
        jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth((int) (largura * 0.20)); // Autor Brancas
        jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth((int) (largura * 0.20)); // Brancas
        jTable.getColumn(jTable.getColumnName(3)).setPreferredWidth((int) (largura * 0.20)); // Autor Pretas
        jTable.getColumn(jTable.getColumnName(4)).setPreferredWidth((int) (largura * 0.20)); // Pretas
        jTable.getColumn(jTable.getColumnName(5)).setPreferredWidth((int) (largura * 0.15)); // Situação
    }
    
    private void configuraTabelaClassificacao(JTable jTable){
        
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer cellRenderEsquerda = new DefaultTableCellRenderer();
        cellRenderEsquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderCentro = new DefaultTableCellRenderer();
        cellRenderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable.getColumn(jTable.getColumnName(0)).setCellRenderer(cellRenderCentro);   // Ordem   
        jTable.getColumn(jTable.getColumnName(1)).setCellRenderer(cellRenderEsquerda); // Autor
        jTable.getColumn(jTable.getColumnName(2)).setCellRenderer(cellRenderEsquerda); // Conjunto Heurístico
        jTable.getColumn(jTable.getColumnName(3)).setCellRenderer(cellRenderCentro);   // Pontos
        jTable.getColumn(jTable.getColumnName(4)).setCellRenderer(cellRenderCentro);   // Partidas
        jTable.getColumn(jTable.getColumnName(5)).setCellRenderer(cellRenderCentro);   // Vitórias
        jTable.getColumn(jTable.getColumnName(6)).setCellRenderer(cellRenderCentro);   // Empates
        jTable.getColumn(jTable.getColumnName(7)).setCellRenderer(cellRenderCentro);   // Derrotas
        
        
        int largura = jTable.getWidth();
        
        jTable.getColumn(jTable.getColumnName(0)).setPreferredWidth((int) (largura * 0.07)); // Ordem   
        jTable.getColumn(jTable.getColumnName(1)).setPreferredWidth((int) (largura * 0.29)); // Autor
        jTable.getColumn(jTable.getColumnName(2)).setPreferredWidth((int) (largura * 0.29)); // Conjunto Heurístico
        jTable.getColumn(jTable.getColumnName(3)).setPreferredWidth((int) (largura * 0.07)); // Pontos
        jTable.getColumn(jTable.getColumnName(4)).setPreferredWidth((int) (largura * 0.07)); // Partidas
        jTable.getColumn(jTable.getColumnName(5)).setPreferredWidth((int) (largura * 0.07)); // Vitórias
        jTable.getColumn(jTable.getColumnName(6)).setPreferredWidth((int) (largura * 0.07)); // Empates
        jTable.getColumn(jTable.getColumnName(7)).setPreferredWidth((int) (largura * 0.07)); // Derrotas
    }
    
    @Override
    public Frame getFrame(){
        return this;
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        return acessoTelaCampeonato;
    }
    
    private void fechar(){    
        
        if (campeonato.getSituacao() == Situacao.EXECUTANDO){
        
            int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar e sair deste Campeonato?", "Confirmação de Saída");
            
            if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                return;
            }
            
            campeonato.parar();     
            
            if (atualizadorTempo != null && atualizadorTempo.isExecutando()){
                atualizadorTempo.parar();
                atualizadorTempo = null;
            }
        }
        
        dispose();        
    }        
         
    private void verificaTreeConjuntoHeuristico() {

        if (treeModelConjuntosHeuristicos.recuperaElementoLista(ConjuntoHeuristico.class) != null) {
            jButtonAdicionarConjuntoHeuristico.setEnabled(true);
        }else{
            jButtonAdicionarConjuntoHeuristico.setEnabled(false);
        }
    }
    
    private void verificaTreeSituacoesJogo(){

        ElementoLista elementoSituacaoJogo = treeModelSituacoesJogo.recuperaElementoLista(SituacaoJogo.class);

        if (elementoSituacaoJogo != null) {
            
            if (etapaCriacao == 2){
                jButtonProximo.setEnabled(true);
            }

            desenhaSituacaoJogo.configura(elementoSituacaoJogo.getDescricao());

            jPanelTabuleiro.setVisible(true);
            jTextFieldFEN.setText(elementoSituacaoJogo.getDescricao());
            jLabelDescricaoVantagem.setText(elementoSituacaoJogo.getTipo().getDescricao());
            
        }else{
            jButtonProximo.setEnabled(false);
            jPanelTabuleiro.setVisible(false);
            jTextFieldFEN.setText("");
            jLabelDescricaoVantagem.setText("");
        }
    }
    
    private void avancarEtapa() {

        etapaCriacao++;

        ativaAtualizacoes(true);

        switch (etapaCriacao) {

            case 2: // Escolher Tabuleiro Inicial
                jButtonAnterior.setVisible(true);
                
                card.show(jPanelPrincipal, "Passo2");
                
                verificaTreeSituacoesJogo();
                break;

            case 3: // Campeonato                

                if (campeonato.getSituacao() == Situacao.AGUARDANDO) {

                    // Primeira Execução //

                    setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    try {
                        campeonato.setTabuleiroInicial(new Tabuleiro(jTextFieldFEN.getText()));
                    } catch (Exception e) {
                        HeuChess.registraExcecao(e);

                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Erro ao carregar o Tabuleiro Inicial das Partidas!\n" + e.getMessage());
                        return;
                    }

                    campeonato.gerarPartidas();
                    tableModelPartidas.update();

                    jTextFieldTotalParticipantes.setText(String.valueOf(campeonato.conjuntosHeuristicos().size()));
                    jTextFieldTotalPartidas.setText(String.valueOf(campeonato.partidas().size()));

                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                jButtonExecutar.setVisible(true);
                
                if (campeonato.getSituacao() == Situacao.TERMINADO){
                    jButtonProximo.setVisible(true);
                }else{
                    jButtonProximo.setVisible(false);
                }

                card.show(jPanelPrincipal, "Campeonato");
                break;

            case 4: // Classificação
                jButtonProximo.setVisible(false);
                jButtonExecutar.setVisible(false);
                
                jButtonAnterior.setVisible(true);
                jButtonGerarClassificacao.setVisible(true);                
                
                card.show(jPanelPrincipal, "Classificacao");
                break;
                
            default:
                UtilsGUI.dialogoErro(this, "Etapa inválida de criação!");
        }
    }
    
    private void incluirConjuntoHeuristico(){
        
        ElementoLista elementoLista = treeModelConjuntosHeuristicos.recuperaElementoLista(ConjuntoHeuristico.class);
        
        for (int pos = 0; pos < campeonato.conjuntosHeuristicos().size(); pos++) {

            ConjuntoHeuristico conjunto = campeonato.conjuntosHeuristicos().get(pos);
                    
            if (elementoLista.getId() == conjunto.getId()) {

                Usuario autor = campeonato.autorConjuntoHeuristico(conjunto);
                        
                UtilsGUI.dialogoErro(this, "O Conjunto Heurístico \"" + conjunto.getNome() + 
                                            (autor.isSexoMasculino() ? "\" do usuário\n\"" : "\" da usuária\n\"") +
                                             autor.getNome() + " já está incluido neste campeonato!");
                return;
            }
        }
        
        try {
            ConjuntoHeuristico conjunto = ConjuntoHeuristicoDAO.busca(elementoLista.getId());
        
            campeonato.adicionaConjuntoHeuristico(conjunto);
            tableModelConjuntosHeuristicos.update();

            if (campeonato.conjuntosHeuristicos().size() > 1){
                jButtonProximo.setEnabled(true);
            }else{
                jButtonProximo.setEnabled(false);
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    UtilsGUI.moverScrollbarFim(jScrollPaneConjuntosHeuristicos);
                }
            });  
            
        } catch (Exception ex) {
            HeuChess.registraExcecao(ex);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(this, "Erro ao carregar o Conjunto Heurístico!\n" + ex.getMessage());
        }
    }
    
    private void ativaAtualizacoes(boolean ativar) {
        
        if (ativar) {
            
            switch(etapaCriacao){
                
                case 1: // Escolhendo Conjuntos Heurísticos
                    if (treeModelConjuntosHeuristicos != null && !treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.start();
                    }    
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
                    
                    
                case 2: // Escolhendo Tabuleiro Inicial
                    if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.stop();
                    }    
                    if (treeModelSituacoesJogo != null && !treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.start();
                    }
                    break;
                    
                case 3: // Campeonato
                    
                    if (campeonato.getSituacao() == Situacao.EXECUTANDO){
                        
                        if (atualizadorTempo == null){
                            atualizadorTempo = new AtualizaTempo();
                            atualizadorTempo.start();
                        }
                    }
                    
                    break;
                    
                case 4: // Classificação
                    if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                        treeModelConjuntosHeuristicos.stop();
                    }            
                    if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                        treeModelSituacoesJogo.stop();
                    }
                    break;
            }
            
        } else {
            
            if (atualizadorTempo != null && atualizadorTempo.isExecutando()){
                atualizadorTempo.parar();
                atualizadorTempo = null;
            }
            
            if (treeModelConjuntosHeuristicos != null && treeModelConjuntosHeuristicos.isExecutando()) {
                treeModelConjuntosHeuristicos.stop();
            }            
            if (treeModelSituacoesJogo != null && treeModelSituacoesJogo.isExecutando()) {
                treeModelSituacoesJogo.stop();
            }
        }
    }
    
    public void iniciandoPartida(final int linha) {

        tableModelPartidas.updateLinha(linha, linha);

        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                if (linha != 0 && (linha + 1 < campeonato.partidas().size() - 1)) {
                    UtilsTable.scrollToVisible(jTablePartidas, linha + 1, 0);
                } else {
                    UtilsTable.scrollToVisible(jTablePartidas, linha, 0);
                }
            }
        });
    }
    
    public synchronized void partidaTerminou(int linha){
        
        jTextFieldPartidasRealizadas.setText(String.valueOf(campeonato.getTotalPartidasRealizadas()));
        jTextFieldPartidasRestantes.setText(String.valueOf(campeonato.partidas().size() - campeonato.getTotalPartidasRealizadas()));
        
        jTextFieldTempoUltimaPartida.setText(UtilsDataTempo.formataTempoNanossegundos(campeonato.partidas().get(linha).getTempoNanossegundos(), true));
        
        int porcentagemConcluida = (int) (campeonato.getTotalPartidasRealizadas() * 100.0 / campeonato.partidas().size());
                
        tableModelPartidas.updateLinha(linha, linha);
        
        jProgressBarProgresso.setValue(porcentagemConcluida);
    }
    
    public synchronized void campeonatoTerminou() {

        if (HeuChess.somAtivado) {
            HeuChess.somPartidaConcluida.play();
        }
        
        if (atualizadorTempo != null && atualizadorTempo.isExecutando()){
            atualizadorTempo.parar();
            atualizadorTempo = null;
        }
        
        ativarInterface(true);
        jButtonProximo.setVisible(true);        
        
        jButtonExecutar.setIcon(iconeReiniciar);
        jButtonExecutar.setText("Reiniciar");
        jButtonExecutar.setMnemonic('R');
        
        avancarEtapa();
        gerarClassificacao();
    }
        
    private void ativarInterface(boolean ativado) {
        
        jSliderProfundidadeBusca.setEnabled(ativado);
        jSpinnerQuantidadePartidasSimultaneas.setEnabled(ativado);
        jComboBoxEngine.setEnabled(ativado);        
        
        jButtonAnterior.setVisible(ativado);
    }
    
    private void gerarClassificacao() {
        
        campeonato.setValorVitoria(((Integer) jSpinnerValorVitoria.getValue()).intValue());
        campeonato.setValorEmpate(((Integer) jSpinnerValorEmpate.getValue()).intValue());
        campeonato.setValorDerrota(((Integer) jSpinnerValorDerrota.getValue()).intValue());

        campeonato.gerarClassificacao();

        tableModelClassificacao.update();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelPrincipal = new javax.swing.JPanel();
        jPanelPasso1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeConjuntoHeuristicos = new javax.swing.JTree();
        jLabel3 = new javax.swing.JLabel();
        jScrollPaneConjuntosHeuristicos = new javax.swing.JScrollPane();
        jTableConjuntosHeuristicos = new javax.swing.JTable();
        jButtonAdicionarConjuntoHeuristico = new javax.swing.JButton();
        jButtonRetirarConjuntoHeuristico = new javax.swing.JButton();
        jPanelPasso2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTreeSituacoesJogo = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFEN = new javax.swing.JTextField();
        jPanelLayoutNull = new javax.swing.JPanel();
        jPanelTabuleiro = new javax.swing.JPanel();
        jPanelTabuleiro.add(desenhaSituacaoJogo);
        jLabelDescricaoVantagem = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanelCampeonato = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanelModoExecucao = new javax.swing.JPanel();
        jComboBoxEngine = new javax.swing.JComboBox();
        jLabelBoxEngine = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSliderProfundidadeBusca = new javax.swing.JSlider();
        jLabel8 = new javax.swing.JLabel();
        jSpinnerQuantidadePartidasSimultaneas = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jScrollPanePartidas = new javax.swing.JScrollPane();
        jTablePartidas = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldTotalParticipantes = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldTotalPartidas = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldTempoExecucao = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jProgressBarProgresso = new javax.swing.JProgressBar();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldPartidasRestantes = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldPartidasRealizadas = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldTempoUltimaPartida = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextFieldTempoTermino = new javax.swing.JTextField();
        jPanelClassificacao = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jSpinnerValorEmpate = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jSpinnerValorVitoria = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jSpinnerValorDerrota = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        jScrollPaneClassificacao = new javax.swing.JScrollPane();
        jTableClassificacao = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jButtonFechar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();
        jButtonExecutar = new javax.swing.JButton();
        jButtonGerarClassificacao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Realiza Campeonato");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_campeonato.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPrincipal.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Passo 1 de 4 - Escolha os Conjuntos Heurísticos");

        jTreeConjuntoHeuristicos.setShowsRootHandles(true);
        jTreeConjuntoHeuristicos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeConjuntoHeuristicos.setCellRenderer(new RenderTreeObjetos(true));
        jTreeConjuntoHeuristicos.setToggleClickCount(1);
        jTreeConjuntoHeuristicos.setScrollsOnExpand(true);
        jTreeConjuntoHeuristicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeConjuntoHeuristicosMouseClicked(evt);
            }
        });
        jTreeConjuntoHeuristicos.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeConjuntoHeuristicosValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeConjuntoHeuristicos);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Conjuntos Heurísticos Selecionados para o Campeonato");

        jTableConjuntosHeuristicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPaneConjuntosHeuristicos.setViewportView(jTableConjuntosHeuristicos);

        jButtonAdicionarConjuntoHeuristico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonAdicionarConjuntoHeuristico.setMnemonic('a');
        jButtonAdicionarConjuntoHeuristico.setText("Adicionar");
        jButtonAdicionarConjuntoHeuristico.setToolTipText("Adiciona um coordenador a turma");
        jButtonAdicionarConjuntoHeuristico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarConjuntoHeuristicoActionPerformed(evt);
            }
        });

        jButtonRetirarConjuntoHeuristico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonRetirarConjuntoHeuristico.setMnemonic('r');
        jButtonRetirarConjuntoHeuristico.setText("Retirar");
        jButtonRetirarConjuntoHeuristico.setToolTipText("Retira o Coordenador selecionado da Turma");
        jButtonRetirarConjuntoHeuristico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetirarConjuntoHeuristicoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPasso1Layout = new javax.swing.GroupLayout(jPanelPasso1);
        jPanelPasso1.setLayout(jPanelPasso1Layout);
        jPanelPasso1Layout.setHorizontalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelPasso1Layout.createSequentialGroup()
                        .addGroup(jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jButtonAdicionarConjuntoHeuristico)
                            .addComponent(jButtonRetirarConjuntoHeuristico))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPaneConjuntosHeuristicos, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelPasso1Layout.setVerticalGroup(
            jPanelPasso1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAdicionarConjuntoHeuristico)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneConjuntosHeuristicos, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRetirarConjuntoHeuristico)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelPasso1, "Passo1");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Passo 2 de 4 - Defina o Tabuleiro Inicial para todas as Partidas");

        jTreeSituacoesJogo.setShowsRootHandles(true);
        jTreeSituacoesJogo.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeSituacoesJogo.setCellRenderer(new RenderTreeObjetos(false));
        jTreeSituacoesJogo.setToggleClickCount(1);
        jTreeSituacoesJogo.setScrollsOnExpand(true);
        jTreeSituacoesJogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeSituacoesJogoMouseClicked(evt);
            }
        });
        jTreeSituacoesJogo.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeSituacoesJogoValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTreeSituacoesJogo);

        jLabel1.setText("Notação FEN (Forsyth-Edwards Notation)");

        jTextFieldFEN.setHorizontalAlignment(JTextField.CENTER);
        jTextFieldFEN.setBackground(new java.awt.Color(255, 255, 204));
        jTextFieldFEN.setEditable(false);

        jPanelLayoutNull.setLayout(null);

        jPanelTabuleiro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelTabuleiro.setMaximumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.setMinimumSize(new java.awt.Dimension(330, 280));
        jPanelTabuleiro.setPreferredSize(new java.awt.Dimension(330, 280));

        javax.swing.GroupLayout jPanelTabuleiroLayout = new javax.swing.GroupLayout(jPanelTabuleiro);
        jPanelTabuleiro.setLayout(jPanelTabuleiroLayout);
        jPanelTabuleiroLayout.setHorizontalGroup(
            jPanelTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 326, Short.MAX_VALUE)
        );
        jPanelTabuleiroLayout.setVerticalGroup(
            jPanelTabuleiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        jPanelLayoutNull.add(jPanelTabuleiro);
        jPanelTabuleiro.setBounds(0, 0, 310, 280);

        jLabelDescricaoVantagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel7.setText("    ");

        javax.swing.GroupLayout jPanelPasso2Layout = new javax.swing.GroupLayout(jPanelPasso2);
        jPanelPasso2.setLayout(jPanelPasso2Layout);
        jPanelPasso2Layout.setHorizontalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 84, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldFEN))))
                .addContainerGap())
        );
        jPanelPasso2Layout.setVerticalGroup(
            jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPasso2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(6, 6, 6)
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPasso2Layout.createSequentialGroup()
                        .addComponent(jPanelLayoutNull, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPasso2Layout.createSequentialGroup()
                        .addGroup(jPanelPasso2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldFEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(5, 5, 5)
                        .addComponent(jLabelDescricaoVantagem, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanelPrincipal.add(jPanelPasso2, "Passo2");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Passo 3 de 4 - Defina as características e Execute o Campeonato");

        jPanelModoExecucao.setBorder(javax.swing.BorderFactory.createTitledBorder("Modo de Execução das Partidas"));

        jComboBoxEngine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Otimizada", "Completa" }));
        jComboBoxEngine.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        jLabelBoxEngine.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelBoxEngine.setText("Geração de Lances Possíveis");

        jLabel6.setText("Profundidade de Busca");

        jSliderProfundidadeBusca.setMaximum(EngineMiniMax.PROFUNDIDADE_MAXIMA_BUSCA);
        jSliderProfundidadeBusca.setMinimum(EngineMiniMax.PROFUNDIDADE_MINIMA_BUSCA);
        jSliderProfundidadeBusca.setMinorTickSpacing(1);
        jSliderProfundidadeBusca.setPaintLabels(true);
        jSliderProfundidadeBusca.setPaintTicks(true);
        jSliderProfundidadeBusca.setSnapToTicks(true);
        jSliderProfundidadeBusca.setToolTipText("");
        jSliderProfundidadeBusca.setLabelTable(jSliderProfundidadeBusca.createStandardLabels(1));
        jSliderProfundidadeBusca.setValue(1);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Partidas Simultâneas");

        jSpinnerQuantidadePartidasSimultaneas.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerQuantidadePartidasSimultaneas, null);

        javax.swing.GroupLayout jPanelModoExecucaoLayout = new javax.swing.GroupLayout(jPanelModoExecucao);
        jPanelModoExecucao.setLayout(jPanelModoExecucaoLayout);
        jPanelModoExecucaoLayout.setHorizontalGroup(
            jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelModoExecucaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelBoxEngine, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderProfundidadeBusca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                        .addComponent(jComboBoxEngine, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinnerQuantidadePartidasSimultaneas, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanelModoExecucaoLayout.setVerticalGroup(
            jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelModoExecucaoLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSliderProfundidadeBusca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanelModoExecucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBoxEngine)
                    .addComponent(jComboBoxEngine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jSpinnerQuantidadePartidasSimultaneas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanelModoExecucaoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxEngine, jSpinnerQuantidadePartidasSimultaneas});

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Sequência de Partidas");

        jTablePartidas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPanePartidas.setViewportView(jTablePartidas);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Estatísticas"));

        jLabel22.setText("Total de Participantes");

        jTextFieldTotalParticipantes.setEditable(false);
        jTextFieldTotalParticipantes.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel23.setText("Total de Partidas");

        jTextFieldTotalPartidas.setEditable(false);
        jTextFieldTotalPartidas.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setText("Tempo Execução");

        jTextFieldTempoExecucao.setEditable(false);
        jTextFieldTempoExecucao.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel24.setText("Progresso");

        jProgressBarProgresso.setStringPainted(true);

        jLabel14.setText("Partidas Restantes");

        jTextFieldPartidasRestantes.setEditable(false);
        jTextFieldPartidasRestantes.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel19.setText("Partidas Realizadas");

        jTextFieldPartidasRealizadas.setEditable(false);
        jTextFieldPartidasRealizadas.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel20.setText("Tempo Última Partida");

        jTextFieldTempoUltimaPartida.setEditable(false);
        jTextFieldTempoUltimaPartida.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel21.setText("Tempo para Término");

        jTextFieldTempoTermino.setEditable(false);
        jTextFieldTempoTermino.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTempoUltimaPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTotalPartidas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTotalParticipantes, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldPartidasRealizadas)
                            .addComponent(jTextFieldTempoExecucao, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldTempoTermino)
                            .addComponent(jTextFieldPartidasRestantes, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jProgressBarProgresso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTextFieldTotalParticipantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBarProgresso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextFieldTotalPartidas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jTextFieldPartidasRealizadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jTextFieldPartidasRestantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextFieldTempoUltimaPartida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldTempoExecucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldTempoTermino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jTextFieldTotalParticipantes, jTextFieldTotalPartidas});

        javax.swing.GroupLayout jPanelCampeonatoLayout = new javax.swing.GroupLayout(jPanelCampeonato);
        jPanelCampeonato.setLayout(jPanelCampeonatoLayout);
        jPanelCampeonatoLayout.setHorizontalGroup(
            jPanelCampeonatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCampeonatoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCampeonatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPanePartidas)
                    .addComponent(jPanelModoExecucao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelCampeonatoLayout.createSequentialGroup()
                        .addGroup(jPanelCampeonatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelCampeonatoLayout.setVerticalGroup(
            jPanelCampeonatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCampeonatoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelModoExecucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPanePartidas, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelCampeonato, "Campeonato");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Esquema de Pontuação"));

        jLabel9.setText("Valor Empate");

        jSpinnerValorEmpate.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorEmpate, null);

        jLabel10.setText("Valor Vitória");

        jSpinnerValorVitoria.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), null, null, Integer.valueOf(1)));
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorVitoria, null);

        jLabel11.setText("Valor Derrota");

        jSpinnerValorDerrota.setModel(new javax.swing.SpinnerNumberModel());
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorDerrota, null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorVitoria, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorEmpate, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinnerValorDerrota, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSpinnerValorDerrota, jSpinnerValorEmpate, jSpinnerValorVitoria});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jSpinnerValorVitoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinnerValorEmpate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jSpinnerValorDerrota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jSpinnerValorDerrota, jSpinnerValorEmpate, jSpinnerValorVitoria});

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Passo 4 de 4 - Classificação Final do Campeonato");

        jTableClassificacao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPaneClassificacao.setViewportView(jTableClassificacao);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Classificação");

        javax.swing.GroupLayout jPanelClassificacaoLayout = new javax.swing.GroupLayout(jPanelClassificacao);
        jPanelClassificacao.setLayout(jPanelClassificacaoLayout);
        jPanelClassificacaoLayout.setHorizontalGroup(
            jPanelClassificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClassificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelClassificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelClassificacaoLayout.createSequentialGroup()
                        .addGroup(jPanelClassificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPaneClassificacao))
                .addContainerGap())
        );
        jPanelClassificacaoLayout.setVerticalGroup(
            jPanelClassificacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClassificacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneClassificacao, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelClassificacao, "Classificacao");

        jButtonFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jButtonFechar.setMnemonic('f');
        jButtonFechar.setText("Fechar");
        jButtonFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
            }
        });

        jButtonProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_avancar.png"))); // NOI18N
        jButtonProximo.setMnemonic('p');
        jButtonProximo.setText("Próximo");
        jButtonProximo.setEnabled(false);
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

        jButtonExecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_inicia_campeonato.png"))); // NOI18N
        jButtonExecutar.setMnemonic('i');
        jButtonExecutar.setText("Iniciar");
        jButtonExecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExecutarActionPerformed(evt);
            }
        });

        jButtonGerarClassificacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_gerar_classificacao.png"))); // NOI18N
        jButtonGerarClassificacao.setMnemonic('g');
        jButtonGerarClassificacao.setText("Gerar");
        jButtonGerarClassificacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGerarClassificacaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExecutar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProximo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonGerarClassificacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFechar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonExecutar, jButtonFechar, jButtonGerarClassificacao, jButtonProximo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFechar)
                    .addComponent(jButtonProximo)
                    .addComponent(jButtonAnterior)
                    .addComponent(jButtonAjuda)
                    .addComponent(jButtonExecutar)
                    .addComponent(jButtonGerarClassificacao))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonExecutar, jButtonFechar, jButtonGerarClassificacao, jButtonProximo});

        setSize(new java.awt.Dimension(808, 627));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        fechar();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
        etapaCriacao--;
        
        ativaAtualizacoes(true);
        
        switch(etapaCriacao){
            
            case 1: // Escolhendo Conjuntos Heurísticos
                   jButtonAnterior.setVisible(false);
                   
                   jButtonProximo.setEnabled(true);
                   
                   verificaTreeConjuntoHeuristico();
                   
                   card.show(jPanelPrincipal,"Passo1");
                   break;
                
            case 2: // Escolhendo Tabuleiro Inicial
                   jButtonExecutar.setVisible(false);
                   
                   jButtonAnterior.setVisible(true);
                   jButtonProximo.setVisible(true);
                   
                   verificaTreeSituacoesJogo();
                   
                   card.show(jPanelPrincipal,"Passo2");
                   break;              
                
            case 3: // Campeonato
                   jButtonGerarClassificacao.setVisible(false);
                    
                   jButtonProximo.setVisible(true);
                   jButtonExecutar.setVisible(true);
                   
                   card.show(jPanelPrincipal,"Campeonato");
                   break;
        }        
    }//GEN-LAST:event_jButtonAnteriorActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        fechar();
    }//GEN-LAST:event_jButtonFecharActionPerformed

    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        avancarEtapa();
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaCampeonato");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jTreeConjuntoHeuristicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicosMouseClicked

        if (treeModelConjuntosHeuristicos.verificaDuploClique(evt, ConjuntoHeuristico.class)){
            incluirConjuntoHeuristico();
        }
    }//GEN-LAST:event_jTreeConjuntoHeuristicosMouseClicked

    private void jTreeConjuntoHeuristicosValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeConjuntoHeuristicosValueChanged
        verificaTreeConjuntoHeuristico();
    }//GEN-LAST:event_jTreeConjuntoHeuristicosValueChanged

    private void jTreeSituacoesJogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoMouseClicked
        
        if (treeModelSituacoesJogo.verificaDuploClique(evt, SituacaoJogo.class)){
            avancarEtapa();
        }
    }//GEN-LAST:event_jTreeSituacoesJogoMouseClicked

    private void jTreeSituacoesJogoValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeSituacoesJogoValueChanged
        verificaTreeSituacoesJogo();
    }//GEN-LAST:event_jTreeSituacoesJogoValueChanged

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        ativaAtualizacoes(true);
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        ativaAtualizacoes(false);
    }//GEN-LAST:event_formWindowDeactivated

    private void jButtonAdicionarConjuntoHeuristicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarConjuntoHeuristicoActionPerformed
        incluirConjuntoHeuristico();        
    }//GEN-LAST:event_jButtonAdicionarConjuntoHeuristicoActionPerformed

    private void jButtonRetirarConjuntoHeuristicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetirarConjuntoHeuristicoActionPerformed
        
        int linhaSelecionada = jTableConjuntosHeuristicos.getSelectedRow();
        
        if (linhaSelecionada != -1) {

            ConjuntoHeuristico conjunto = campeonato.conjuntosHeuristicos().get(linhaSelecionada);
            Usuario autor               = campeonato.autorConjuntoHeuristico(conjunto);
            
            String mensagem = "Deseja realmente retirar deste campeonato o Conjunto Heurístico \n\"" + 
                              conjunto.getNome() +  (autor.isSexoMasculino() ? "\" do usuário\n\"" : "\" da usuária\n\"") +
                              autor.getNome() + "?";

            int resposta = UtilsGUI.dialogoConfirmacao(this, mensagem, "Confirmação de Retirada");
            if (resposta == JOptionPane.YES_OPTION) {

                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }

                campeonato.removeConjuntoHeuristico(conjunto);

                tableModelConjuntosHeuristicos.update();
            }
        } else {
            jButtonRetirarConjuntoHeuristico.setEnabled(false);
        }
    }//GEN-LAST:event_jButtonRetirarConjuntoHeuristicoActionPerformed

    private void jButtonExecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExecutarActionPerformed
        
        if (campeonato.getSituacao() == Situacao.EXECUTANDO) {

                campeonato.parar();
                
                if (atualizadorTempo != null && atualizadorTempo.isExecutando()){
                    atualizadorTempo.parar();
                    atualizadorTempo = null;
                }
                
                ativarInterface(true);
                jButtonProximo.setVisible(false);   
 
                if (HeuChess.somAtivado) {
                    HeuChess.somPartidaCancelada.play();
                }

                jButtonExecutar.setIcon(iconeReiniciar);
                jButtonExecutar.setText("Reiniciar");
                jButtonExecutar.setMnemonic('R');
                
        }else {
            if (campeonato.getSituacao() != Situacao.AGUARDANDO){
                
                // Terminou ou foi Cancelado //
                
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                campeonato.gerarPartidas();
                tableModelPartidas.update();
                
                jTextFieldTotalParticipantes.setText(String.valueOf(campeonato.conjuntosHeuristicos().size()));
                jTextFieldTotalPartidas.setText(String.valueOf(campeonato.partidas().size()));    
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            campeonato.setProfundidadeBusca(jSliderProfundidadeBusca.getValue());
            campeonato.setQuantidadePartidasSimultaneas(((Integer) jSpinnerQuantidadePartidasSimultaneas.getValue()).intValue());

            if (jComboBoxEngine.getSelectedIndex() == 0) {
                campeonato.setGeracaoLancesOtimizada(true);
            } else {
                campeonato.setGeracaoLancesOtimizada(false);
            }

            ativarInterface(false);
            jProgressBarProgresso.setValue(0);
            
            jButtonProximo.setVisible(false);   
            
            jTextFieldPartidasRealizadas.setText(String.valueOf(campeonato.getTotalPartidasRealizadas()));
            jTextFieldPartidasRestantes.setText(String.valueOf(campeonato.partidas().size()));

            jButtonExecutar.setIcon(iconeParar);
            jButtonExecutar.setText("Parar");
            jButtonExecutar.setMnemonic('P');

            campeonato.iniciar();
            
            atualizadorTempo = new AtualizaTempo();
            atualizadorTempo.start();
        }            
    }//GEN-LAST:event_jButtonExecutarActionPerformed

    private void jButtonGerarClassificacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGerarClassificacaoActionPerformed
        gerarClassificacao();
    }//GEN-LAST:event_jButtonGerarClassificacaoActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarConjuntoHeuristico;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonExecutar;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JButton jButtonGerarClassificacao;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JButton jButtonRetirarConjuntoHeuristico;
    private javax.swing.JComboBox jComboBoxEngine;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBoxEngine;
    private javax.swing.JLabel jLabelDescricaoVantagem;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCampeonato;
    private javax.swing.JPanel jPanelClassificacao;
    private javax.swing.JPanel jPanelLayoutNull;
    private javax.swing.JPanel jPanelModoExecucao;
    private javax.swing.JPanel jPanelPasso1;
    private javax.swing.JPanel jPanelPasso2;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelTabuleiro;
    private javax.swing.JProgressBar jProgressBarProgresso;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneClassificacao;
    private javax.swing.JScrollPane jScrollPaneConjuntosHeuristicos;
    private javax.swing.JScrollPane jScrollPanePartidas;
    private javax.swing.JSlider jSliderProfundidadeBusca;
    private javax.swing.JSpinner jSpinnerQuantidadePartidasSimultaneas;
    private javax.swing.JSpinner jSpinnerValorDerrota;
    private javax.swing.JSpinner jSpinnerValorEmpate;
    private javax.swing.JSpinner jSpinnerValorVitoria;
    private javax.swing.JTable jTableClassificacao;
    private javax.swing.JTable jTableConjuntosHeuristicos;
    private javax.swing.JTable jTablePartidas;
    private javax.swing.JTextField jTextFieldFEN;
    private javax.swing.JTextField jTextFieldPartidasRealizadas;
    private javax.swing.JTextField jTextFieldPartidasRestantes;
    private javax.swing.JTextField jTextFieldTempoExecucao;
    private javax.swing.JTextField jTextFieldTempoTermino;
    private javax.swing.JTextField jTextFieldTempoUltimaPartida;
    private javax.swing.JTextField jTextFieldTotalParticipantes;
    private javax.swing.JTextField jTextFieldTotalPartidas;
    private javax.swing.JTree jTreeConjuntoHeuristicos;
    private javax.swing.JTree jTreeSituacoesJogo;
    // End of variables declaration//GEN-END:variables
}
