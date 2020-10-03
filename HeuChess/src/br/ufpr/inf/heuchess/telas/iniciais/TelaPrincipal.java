package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.heuchess.persistencia.*;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Turma;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.AcessoTelaParametrosPartida;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaCampeonato;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaParametrosPartida;
import br.ufpr.inf.heuchess.telas.competicaoheuristica.TelaPartidaXadrez;
import br.ufpr.inf.heuchess.telas.editorheuristica.TelaEditorConjuntoHeuristico;
import br.ufpr.inf.heuchess.telas.situacaojogo.TelaAvaliaSituacaoJogo;
import br.ufpr.inf.heuchess.telas.situacaojogo.TelaSituacaoJogo;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 10:53
 */
public class TelaPrincipal extends javax.swing.JFrame implements AcessoTelaUsuario, AcessoTelaTrocaSenha, AcessoTelaParametrosPartida {
    
    public  ArrayList<TelaEditorConjuntoHeuristico> telasEditorConjuntoHeuristico;
    public  ArrayList<TelaSituacaoJogo>             telasSituacaoJogo;
    public  ArrayList<TelaPartidaXadrez>            telasPartidaXadrez;
    
    private TreeModelObjetos      treeModelObjetos;
    private TreeModelInstituicoes treeModelInstituicoes;
    
    private void alteraLookAndFeel(String nome){
        
        for (int x = 0; x < HeuChess.looks.length; x++){
            
            if (HeuChess.looks[x].getName().equalsIgnoreCase(nome)){
                
                try {
                    javax.swing.UIManager.setLookAndFeel(HeuChess.looks[x].getClassName());                    
                    HeuChess.lookSelecionado = x;
                    return;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    HeuChess.registraExcecao(e);
                }
            }
        }
    }
    
    private void atualizaTodasTelas(){        
        
        Window windows[] = Window.getOwnerlessWindows();
        
        for (Window janela: windows){
            UtilsGUI.atualizaTela(janela);            
        }        
    }    

    private class AcaoTrocaLook implements ActionListener{
        
        private String nome;
        
        public AcaoTrocaLook(String nome){
            this.nome = nome;
        }
        
        @Override
        public void actionPerformed(ActionEvent e){
            alteraLookAndFeel(nome);
            atualizaTodasTelas();
        }
    }
    
    public TelaPrincipal() {
        
        telasEditorConjuntoHeuristico = new ArrayList<>(); 
        telasPartidaXadrez            = new ArrayList<>();
        telasSituacaoJogo             = new ArrayList<>();
        
        initComponents();
        
        treeModelObjetos = new TreeModelObjetos(jTreeObjetos);
        treeModelObjetos.expandMeusObjetos();
        
        ButtonGroup grupoAparencia = new ButtonGroup();
        
        for (int x = 0; x < HeuChess.looks.length; x++){
            
            JRadioButtonMenuItem opcao = new JRadioButtonMenuItem(HeuChess.looks[x].getName());
            opcao.addActionListener(new AcaoTrocaLook(HeuChess.looks[x].getName()));
            
            if (HeuChess.lookSelecionado == x){
                opcao.setSelected(true);
            }
            jMenuAparencia.add(opcao);
            grupoAparencia.add(opcao);
        }
        
        setaLabelsUsuarioConectado();        
        
        /////////////////////////////////////
        // Configura permissões gerenciais //
        /////////////////////////////////////
        
        boolean podeEditarTurma      = false;
        boolean podeCriarTurma       = false;
        boolean podeGerenciarUsuario = false;
        boolean podeCriarInstituicao = false;
        
        if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR) {
            podeEditarTurma      = true;
            podeCriarTurma       = true;
            podeGerenciarUsuario = true;
            podeCriarInstituicao = true;
        } else {

            try {
                
                if (UsuarioDAO.quantidadeInstituicoesCoordena(HeuChess.usuario.getId()) > 0) {
                    podeEditarTurma      = true;
                    podeCriarTurma       = true;
                    podeGerenciarUsuario = true;
                } else 
                    if (UsuarioDAO.quantidadeTurmasCoordena(HeuChess.usuario.getId()) > 0) {
                        podeEditarTurma = true;
                    }

            } catch (Exception e) {
                HeuChess.avisoFechaPrograma(e, "Erro ao recuperar informações de Coordenação do Usuário no Banco de Dados!", -2);
            }
        }
        
        if (podeEditarTurma){            
            
            try {
                treeModelInstituicoes = new TreeModelInstituicoes(jTreeInstituicoesTurmas);
                treeModelInstituicoes.forceUpdate();
                treeModelInstituicoes.expandInstituicaoes();
            } catch (Exception e) {  
                HeuChess.avisoFechaPrograma(e, "Erro ao iniciar Tree Instituições!",-3);
            }
                        
            jPanelInstituicoesTurmas.setVisible(true);   
            
        }else{
            jTabbedPanePrincipal.remove(jPanelInstituicoesTurmas);
        }
        
        if (podeCriarTurma){
            jSeparatorCriarInstituicaoTurmas.setVisible(true);
            jMenuItemCriarTurma.setVisible(true);
            
            jButtonCriarTurma.setVisible(true);
            jSeparatorToolBarAjuda.setVisible(true);
        }else{
            jSeparatorCriarInstituicaoTurmas.setVisible(false);
            jMenuItemCriarTurma.setVisible(false);
            
            jButtonCriarTurma.setVisible(false);
            jSeparatorToolBarAjuda.setVisible(false);
        }
        
        if (podeCriarInstituicao){
            jMenuItemCriarInstituicao.setVisible(true);
            jButtonCriarInstituicao.setVisible(true);
        }else{
            jMenuItemCriarInstituicao.setVisible(false);
            jButtonCriarInstituicao.setVisible(false);
        }
        
        if (podeGerenciarUsuario){
            jMenuItemCriarUsuario.setVisible(true);
            jMenuGerenciar.setVisible(true);
            
            jButtonCriarUsuario.setVisible(true);
        }else{
            jMenuItemCriarUsuario.setVisible(false);
            jMenuGerenciar.setVisible(false);
            
            jButtonCriarUsuario.setVisible(false);
        }
        
        //////////////////////////////////////////////
        // Registra a Entrada do Usuário no Sistema //
        //////////////////////////////////////////////
        
        try {
            
            UsuarioDAO.inciandoAcesso(HeuChess.usuario);        
            ConexaoDBHeuChess.commit(); 
            
        } catch (Exception e){
            HeuChess.avisoFechaPrograma(e, "Erro ao registrar inicio sessao do Usuário no Banco de Dados!", -2);
        }
            
        if (HeuChess.usuario.getSituacao() == Usuario.TROCANDO_SENHA){
        
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaTrocaSenha tela = new TelaTrocaSenha(TelaPrincipal.this, HeuChess.usuario);
                }
            });
            
        }else{
            setVisible(true);
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
    
    private void confirmaSaida(){                
        
        int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja realmente sair?","Confirmação de Saída");
        
        if (resposta == JOptionPane.YES_OPTION){
            HeuChess.fechaPrograma(0);
        }        
    }
    
    public void minimizaTodasJanelas() {
        
        setExtendedState(JFrame.ICONIFIED);

        for (TelaEditorConjuntoHeuristico tela : HeuChess.telaPrincipal.telasEditorConjuntoHeuristico) {
            tela.setExtendedState(JFrame.ICONIFIED);
        }

        for (TelaPartidaXadrez tela : HeuChess.telaPrincipal.telasPartidaXadrez) {
            tela.setExtendedState(JFrame.ICONIFIED);
        }
        
        for (TelaSituacaoJogo tela : telasSituacaoJogo) {
            tela.setExtendedState(JFrame.ICONIFIED);
        }
    }
    
    public void desvincultarTrazerOutraTelaFrente(JFrame jFrame) {
        
        if (jFrame instanceof TelaSituacaoJogo){
            
            telasSituacaoJogo.remove((TelaSituacaoJogo) jFrame);
            
            if (trazerFrenteTela(telasSituacaoJogo)){
                return;
            }else
                if (trazerFrenteTela(telasEditorConjuntoHeuristico)){
                    return;
                }else                    
                    if (trazerFrenteTela(telasPartidaXadrez)){
                        return;
                    }           
        }
        
        if (jFrame instanceof TelaEditorConjuntoHeuristico) {

            telasEditorConjuntoHeuristico.remove((TelaEditorConjuntoHeuristico) jFrame);
            
            if (trazerFrenteTela(telasEditorConjuntoHeuristico)){
                return;
            }else
                if (trazerFrenteTela(telasSituacaoJogo)){
                    return;
                }else                    
                    if (trazerFrenteTela(telasPartidaXadrez)){
                        return;
                    }
        }       
        
        if (jFrame instanceof TelaPartidaXadrez) {
            
            telasPartidaXadrez.remove((TelaPartidaXadrez) jFrame);
            
            if (trazerFrenteTela(telasPartidaXadrez)){
                return;
            }else
                if (trazerFrenteTela(telasEditorConjuntoHeuristico)){
                    return;
                }else
                    if (trazerFrenteTela(telasSituacaoJogo)){
                        return;
                    }
        }
        
        setExtendedState(JFrame.NORMAL);
        toFront();
    }
    
    private boolean trazerFrenteTela(ArrayList<? extends JFrame> arrayList) {
        
        int total = arrayList.size();

        if (total > 0) {
            JFrame jFrame = arrayList.get(total - 1);
            jFrame.setExtendedState(JFrame.NORMAL);
            jFrame.toFront();
            return true;
        }
        
        return false;
    }
     
    private void abrindoObjeto(ElementoLista elemento){  
        
        if (elemento != null){                  
            
            if (elemento.getClasse() == SituacaoJogo.class){
                    
                ///////////////////////////////////////////////////
                // Verifica se o Situacao de Jogo já esta aberta //
                ///////////////////////////////////////////////////

                for (TelaSituacaoJogo tela : telasSituacaoJogo) {

                    if (elemento.getId() == tela.situacaoJogo.getId()) {
                        tela.setExtendedState(JFrame.NORMAL);
                        tela.toFront();
                        return;
                    }
                }   
                    
                editarSituacaoJogo(elemento.getId());
                    
            }else
                if (elemento.getClasse() == ConjuntoHeuristico.class) {
                        
                    //////////////////////////////////////////////////////////
                    // Verifica se o Conjunto Heurístico já não está aberto //
                    //////////////////////////////////////////////////////////

                    for (TelaEditorConjuntoHeuristico tela : telasEditorConjuntoHeuristico) {

                        if (elemento.getId() == tela.idConjuntoHeuristico()) {
                            tela.setExtendedState(JFrame.NORMAL);
                            tela.toFront();
                            return;
                        }
                    }
                    
                    /////////////////////////////////
                    // Carrega Conjunto Heurístico //
                    /////////////////////////////////
                        
                    try {            
                        setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            
                        ConjuntoHeuristico conjuntoHeuristico = ConjuntoHeuristicoDAO.busca(elemento.getId());
                        
                        editarConjuntoHeuristico(conjuntoHeuristico, false);
                        
                    } catch (Exception ex) {                            
                        HeuChess.desfazTransacao(ex);
                                    
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        UtilsGUI.dialogoErro(this, "Erro ao carregar o Conjunto Heurístico!\n" + ex.getMessage());
                    }
                }else
                    if (elemento.getClasse() == Usuario.class) {
                                   
                        try {            
                            setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                
                            final Usuario usuario = UsuarioDAO.busca(elemento.getId());
                                                        
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    TelaUsuario tela = new TelaUsuario(TelaPrincipal.this, usuario);
                                }
                            });
                                
                        } catch (Exception ex) {
                            HeuChess.desfazTransacao(ex);
                                
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            UtilsGUI.dialogoErro(this, "Erro ao carregar o Usuário!\n" + ex.getMessage());
                        }
                    }else
                        if (elemento.getClasse() == Instituicao.class) {
                                
                            try {            
                                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                    
                                final Instituicao instituicao = InstituicaoDAO.busca(elemento.getId());
                                                        
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        TelaInstituicao tela = new TelaInstituicao(TelaPrincipal.this,instituicao);                        
                                    }
                                });
                                
                            } catch (Exception ex) {
                                HeuChess.desfazTransacao(ex);
                                    
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                UtilsGUI.dialogoErro(this, "Erro ao carregar a Instituição!\n" + ex.getMessage());
                            }
                        }else
                            if (elemento.getClasse() == Turma.class) {
                                    
                                try {            
                                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                        
                                    final Turma turma = TurmaDAO.busca(elemento.getId());
                                                        
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            TelaTurma tela = new TelaTurma(TelaPrincipal.this,turma);
                                        }
                                    });
                                
                                } catch (Exception ex) {
                                    HeuChess.desfazTransacao(ex);
                                    
                                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                    UtilsGUI.dialogoErro(this, "Erro ao carregar a Turma!\n" + ex.getMessage());
                                }
                            }
        }
    }    
   
    private void excluindoObjeto(ElementoLista elemento) {

        if (elemento != null) {

            if (elemento.getClasse() == ConjuntoHeuristico.class) {

                ///////////////////////////////////////////
                // Verifica se a heurística está em jogo //
                ///////////////////////////////////////////

                for (TelaPartidaXadrez obj : telasPartidaXadrez) {

                    final TelaPartidaXadrez telaPartida = obj;
                    
                    if ((telaPartida.getJogadorBrancas() != null && elemento.getId() == telaPartida.getJogadorBrancas().getId()) || 
                        (telaPartida.getJogadorPretas()  != null && elemento.getId() == telaPartida.getJogadorPretas().getId())) {
    
                        UtilsGUI.dialogoAtencao(this,"O Conjunto Heurístico \"" + elemento.getNome() + "\"\n" +
                                                     "está sendo usado em uma partida automática e não pode ser apagado no momento!\n\n" +
                                                     "Para poder apagá-lo primeiro cancele a partida.");

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setExtendedState(JFrame.ICONIFIED);
                                telaPartida.getFrame().setExtendedState(JFrame.NORMAL);
                                telaPartida.getFrame().toFront();
                            }
                        });
                        
                        return;
                    }
                }
                
                try {
                    if (!podeApagarConjuntoOuSituacao(elemento)) {
                        UtilsGUI.dialogoErro(this,"Você não tem permissão para apagar este Conjunto Heurístico\n\"" + 
                                                  elemento.getNome() + "\"!");
                        return;
                    }
                    
                    excluir(elemento, "o Conjunto Heurístico");
                
                } catch (Exception e) {
                    HeuChess.registraExcecao(e);
                    UtilsGUI.dialogoErro(this, "Erro a buscar permissões de Turma no Banco!\n" + e.getMessage());
                }

            } else 
                if (elemento.getClasse() == SituacaoJogo.class) {

                    try {
                        if (!podeApagarConjuntoOuSituacao(elemento)) {
                            UtilsGUI.dialogoErro(this,"Você não tem permissão para apagar esta Situação de Jogo\n\"" + 
                                                      elemento.getNome() + "\"!");
                            return;
                        }
                        
                        excluir(elemento, "a Situação de Jogo");
                
                    } catch (Exception e) {
                        HeuChess.registraExcecao(e);
                        UtilsGUI.dialogoErro(this, "Erro a buscar permissões de Turma no Banco!\n" + e.getMessage());
                    }
                    
                }else
                    if (elemento.getClasse() == Instituicao.class){
                        
                        // Somente um Administrador pode apagar uma Instituição
                        
                        if (HeuChess.usuario.getTipo() != Usuario.ADMINISTRADOR) {
                            UtilsGUI.dialogoErro(this,"Você não tem permissão para apagar Instituições!");
                            return;
                        }
                        
                        excluir(elemento, "a Instituição");    
                        
                    }else
                        if (elemento.getClasse() == Turma.class){
                         
                            boolean podeApagar = false;
                            
                            try {
                                // Uma turma pode ser apagada por um Administrador ou pelo Coordenador da Instituição a qual
                                // a turma está ligada
                                
                                if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR ||
                                    HeuChess.usuario.getId()   == UsuarioDAO.coordenadorInstituicaoTurma(elemento.getId())) {
                                    podeApagar = true;
                                }
                            
                                if (!podeApagar) {
                                    UtilsGUI.dialogoErro(this,"Você não tem permissão para apagar a Turma\n\"" + 
                                                              elemento.getNome() + "\"!");
                                    return;
                                }
                            
                                excluir(elemento, "a Turma");
                                
                            } catch (Exception e) {
                                HeuChess.desfazTransacao(e);
                                UtilsGUI.dialogoErro(this, "Erro a buscar permissões de Turma no Banco!\n" + e.getMessage());
                            }
                        }else
                            if (elemento.getClasse() == Usuario.class){
                                
                                if (HeuChess.usuario.getTipo() != Usuario.ADMINISTRADOR) {
                                    UtilsGUI.dialogoErro(this, "Você não tem permissão para apagar Usuários!");
                                    return;
                                }
                                
                                if (elemento.getId() == HeuChess.usuario.getId()){
                                    UtilsGUI.dialogoErro(this, "Você não pode apagar o Usuário que está conetado!");
                                    return;
                                }
                                
                                if (elemento.getId() == 1){
                                    UtilsGUI.dialogoErro(this, "Você não tem permissão para apagar este Usuário!");
                                    return;
                                }
                                
                                excluir(elemento, "o Usuário");
                            }
        }   
    }
     
    private void excluir(ElementoLista elemento, String mensagem) {
        
        int resposta = UtilsGUI.dialogoConfirmacao(this,"Deseja Realmente Apagar " + mensagem + "\n\"" + elemento.getNome() + "\"?",
                                                        "Confirmação Exclusão");
        if (resposta == JOptionPane.YES_OPTION) {

            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            try {
                if (elemento.getClasse() == Instituicao.class) {
                    InstituicaoDAO.apaga(elemento.getId());
                } else 
                    if (elemento.getClasse() == Turma.class) {                      
                        TurmaDAO.apaga(elemento.getId());
                    } else 
                        if (elemento.getClasse() == Usuario.class) {
                            UsuarioDAO.apaga(elemento.getId());
                        } else 
                            if (elemento.getClasse() == SituacaoJogo.class) {

                                //////////////////////////////////////////////////
                                // Fecha janela SituacaoJogo caso esteja aberta //
                                //////////////////////////////////////////////////

                                for (TelaSituacaoJogo tela : telasSituacaoJogo) {

                                    if (elemento.getId() == tela.situacaoJogo.getId()) {
                                        tela.dispose();
                                    }
                                }
    
                                SituacaoJogoDAO.apaga(elemento.getId());
                            } else 
                                if (elemento.getClasse() == ConjuntoHeuristico.class) {

                                    ////////////////////////////////////////////
                                    // Fecha janela editor caso esteja aberta //
                                    ////////////////////////////////////////////

                                    for (TelaEditorConjuntoHeuristico tela : telasEditorConjuntoHeuristico) {

                                        if (elemento.getId() == tela.idConjuntoHeuristico()) {
                                            tela.dispose();
                                        }
                                    }
                                    
                                    ConjuntoHeuristicoDAO.apaga(elemento.getId());
                                }else{
                                    throw new RuntimeException("Tipo de Classe não suportada por método [" + elemento.getClasse().getName() + "]");
                                }

                ConexaoDBHeuChess.commit();
                
                if (HeuChess.somAtivado) {
                    HeuChess.somApagar.play();
                }

                if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {
                    treeModelObjetos.forceUpdate();
                } else 
                    if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInstituicoesTurmas) {
                        treeModelInstituicoes.forceUpdate();
                    }

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            } catch (Exception e) {
                HeuChess.desfazTransacao(e);
                
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(this, "Erro ao tentar apagar " + mensagem + " no Banco de Dados\n" +
                                           "Operação Cancelada!");                
            }
        }
    }
    
    private boolean podeApagarConjuntoOuSituacao(ElementoLista elemento) throws Exception {

        // Pode apagar um Conjunto Heurístico ou Situação de Jogo caso seja Administrador, o próprio autor, 
        // o coordenador de uma turma dele, ou um companheiro de turma de acordo com as permissões da turma.

        if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR){
            return true;
        }
        
        long idAutor = UsuarioDAO.autorComponente(elemento.getId());

        if (HeuChess.usuario.getId() == idAutor) {
            return true;
        }  
        
        if (UsuarioDAO.coordenoTurma(HeuChess.usuario, idAutor) != -1) {
            return true;
        } 
        
        ArrayList<Integer> permissoes = TurmaDAO.listaPermissoes(HeuChess.usuario, idAutor);

        for (Integer inteiro : permissoes) {
            if (Permissao.EXCLUIR.existe(inteiro.intValue())) {
                return true;
            }
        }
        
        return false;
    }
    
    private void novoConjuntoHeuristico(){
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR)); 
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaNovoConjuntoHeuristico tela = new TelaNovoConjuntoHeuristico();
            }
        });
    }
    
    private void novaInstituicao(){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaInstituicao tela = new TelaInstituicao(TelaPrincipal.this);
            }
        });
    }
    
    private void novaTurma(){
        
        long idInstituicaoSelecionada = 0;
        
        if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInstituicoesTurmas){
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeInstituicoesTurmas.getLastSelectedPathComponent();
        
            if (node != null) {
                
                Object nodeInfo = node.getUserObject();
        
                if (nodeInfo instanceof ElementoLista){                    
                    
                    ElementoLista elemento = (ElementoLista) nodeInfo;
                    
                    if (elemento.getClasse() == Instituicao.class){
                        idInstituicaoSelecionada = elemento.getId();
                    }
                }
            }
        }
        
        if (idInstituicaoSelecionada == 0){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaTurma tela = new TelaTurma(TelaPrincipal.this);
                }
            });
            
        }else{
            
            final long inst = idInstituicaoSelecionada;
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaTurma tela = new TelaTurma(TelaPrincipal.this,inst);
                }
            });
        }
    }
    
    public boolean podeCriarNovaPartidaSimultanea(Component pai){
        
        if (telasPartidaXadrez.size() == TelaPartidaXadrez.MAXIMO_PARTIDAS_SIMULTANEAS){
                        
            UtilsGUI.dialogoAtencao(pai, "Só é possível ver simultaneamente " + TelaPartidaXadrez.MAXIMO_PARTIDAS_SIMULTANEAS + 
                                         " Partidas de Xadrez!\n\nFeche uma jas janelas para iniciar outra.");
            return false;
        }else{
            return true;
        }
    }
    
    private void novaPartida() {
        
        if (!podeCriarNovaPartidaSimultanea(this)){
            return;
        }        
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeObjetos.getLastSelectedPathComponent();
        
        if (node != null){
            
            Object nodeInfo = node.getUserObject();      
            
            if (nodeInfo instanceof ElementoLista){
                
                final ElementoLista elemento = (ElementoLista) nodeInfo;

                if (elemento.getClasse() == SituacaoJogo.class || elemento.getClasse() == ConjuntoHeuristico.class) {
                    
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaParametrosPartida tela = new TelaParametrosPartida(TelaPrincipal.this, elemento.getClasse(), elemento.getId());
                        }
                    });

                    return;
                }
            }   
        }
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaParametrosPartida tela = new TelaParametrosPartida(TelaPrincipal.this);
            }
        });
    }
    
    private void novoCampeonato(){
    
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeObjetos.getLastSelectedPathComponent();
        
        if (node != null){
            
            Object nodeInfo = node.getUserObject();      
            
            if (nodeInfo instanceof ElementoLista){
                
                final ElementoLista elemento = (ElementoLista) nodeInfo;

                if (elemento.getClasse() == SituacaoJogo.class || elemento.getClasse() == ConjuntoHeuristico.class) {
                    
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaCampeonato tela = new TelaCampeonato(TelaPrincipal.this, elemento.getClasse(), elemento.getId());
                        }
                    });

                    return;
                }
            }   
        }
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaCampeonato tela = new TelaCampeonato(TelaPrincipal.this);
            }
        });
    }
    
    public void novaSituacaoJogo(){

        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaSituacaoJogo tela = new TelaSituacaoJogo(TelaPrincipal.this);
            }
        });
    }
    
    public void editarConjuntoHeuristico(final ConjuntoHeuristico conjuntoHeuristico, boolean novo){        

        try {
            if (!novo) {
                Historico.registraComponenteAberto(conjuntoHeuristico);
                ConexaoDBHeuChess.commit();
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEditorConjuntoHeuristico tela = new TelaEditorConjuntoHeuristico(TelaPrincipal.this, conjuntoHeuristico);
                }
            });
            
        } catch (Exception e) {
            HeuChess.desfazTransacao(e);
            UtilsGUI.dialogoErro(this, "Erro ao registrar ação de abertura de Componente no Banco de Dados!");
        }
    }
    
    public void editarSituacaoJogo(long id){
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        try {
            final SituacaoJogo situacao = SituacaoJogoDAO.busca(id);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaSituacaoJogo tela = new TelaSituacaoJogo(TelaPrincipal.this, situacao);
                }
            });

        } catch (Exception ex) {
            HeuChess.desfazTransacao(ex);
            
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));            
            UtilsGUI.dialogoErro(this, "Erro ao abrir Situação de Jogo\n" + ex.getMessage());
        }
    }
    
    public void fechandoTelaNovoConjuntoHeuristico(final ConjuntoHeuristico novoConjunto) {

        if (novoConjunto != null) {
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {
                        treeModelObjetos.forceUpdate();
                        treeModelObjetos.selecionaTreeNode(ConjuntoHeuristico.class, novoConjunto.getId());
                    }
                }
            });
            
            editarConjuntoHeuristico(novoConjunto, true);
            
        }else{
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
        }
    }
    
    @Override
    public void fechandoTelaParametrosPartida(final Partida game){
    
        if (game != null) {
       
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPartidaXadrez telaAnalisePartidaXadrez = new TelaPartidaXadrez(TelaPrincipal.this, game);
                }
            });
              
        }else{
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
        }
    }
    
    public void fechandoTelaSituacaoJogo(SituacaoJogo situacaoJogo) {

        if (situacaoJogo != null) {

            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {
                
                treeModelObjetos.forceUpdate();
                treeModelObjetos.selecionaTreeNode(SituacaoJogo.class, situacaoJogo.getId());
            }
        }
    }
    
    public void fechandoTelaInstituicao(Instituicao instituicao, boolean nova){
        
        if (instituicao != null){
            
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInstituicoesTurmas) {
                
                treeModelInstituicoes.forceUpdate();
                treeModelInstituicoes.selecionaTreeNode(Instituicao.class,instituicao.getId());
            }
        }
    }
    
    public void fechandoTelaTurma(Turma turma, boolean nova){
        
        if (turma != null){
            
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {

                treeModelObjetos.forceUpdate();
                treeModelObjetos.selecionaTreeNode(Turma.class,turma.getId());
                
            } else 
                if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInstituicoesTurmas) {

                    treeModelInstituicoes.forceUpdate();
                    treeModelInstituicoes.selecionaTreeNode(Turma.class,turma.getId());
                }
        }
    }
    
    @Override
    public void fechandoTelaUsuario(Usuario usuario, boolean novo) {
        
        if (usuario != null && !novo) {

            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {
                
                treeModelObjetos.forceUpdate();
                treeModelObjetos.selecionaTreeNode(Usuario.class, usuario.getId());
            }
        }
    }
    
    @Override
    public void fechandoTelaTrocaSenha(String novaSenha) {
        
        if (novaSenha != null){
            
            try{
                HeuChess.usuario.setSenha(HeuChess.converteSenha(novaSenha));
            
                UsuarioDAO.trocaSenha(HeuChess.usuario);
                
                ConexaoDBHeuChess.commit();
            
                setVisible(true);
                toFront();
                
            }catch(Exception e){
                HeuChess.avisoFechaPrograma(e, "Erro ao salvar Nova senha no Banco de Dados!", -2);
            }
        }else{
            HeuChess.fechaPrograma(0);
        }
    }
         
    public void atualizaUsuarioConectado(Usuario usuario){
        
        HeuChess.usuario = usuario;
        
        setaLabelsUsuarioConectado();
    }
    
    private void setaLabelsUsuarioConectado(){
        jLabelNomeUsuario.setText(UtilsString.cortaTextoMaior(UtilsString.formataCaixaAltaBaixa(HeuChess.usuario.getNome()), 70, true));
        jLabelTipoUsuario.setText(UtilsString.formataCaixaAltaBaixa(HeuChess.usuario.getTipo().getNome())); 
    }
    
    public void atualizaTipoUsuarioConectado(Tipo tipo){
        HeuChess.usuario.setTipo(tipo);        
        jLabelTipoUsuario.setText(UtilsString.formataCaixaAltaBaixa(HeuChess.usuario.getTipo().getNome())); 
    }
    
    private void avaliarSituacaoJogo(){
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeObjetos.getLastSelectedPathComponent();
        
        if (node != null){
            
            Object nodeInfo = node.getUserObject();      
            
            if (nodeInfo instanceof ElementoLista){
                
                final ElementoLista elemento = (ElementoLista) nodeInfo;
                
                if (elemento.getClasse() == SituacaoJogo.class || elemento.getClasse() == ConjuntoHeuristico.class) {
                    
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaAvaliaSituacaoJogo tela = new TelaAvaliaSituacaoJogo(TelaPrincipal.this, elemento.getClasse(), elemento.getId());
                        }
                    });
                    return;
                }                
            }   
        }
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaAvaliaSituacaoJogo tela = new TelaAvaliaSituacaoJogo(TelaPrincipal.this);
            }
        });
    }
    
    private void telaUsuario(final boolean novo) {
        
        if (novo) {
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaUsuario tela = new TelaUsuario(TelaPrincipal.this);
                }
            });

        } else {

            try {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                
                HeuChess.usuario = UsuarioDAO.busca(HeuChess.usuario.getId());

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaUsuario tela = new TelaUsuario(TelaPrincipal.this, HeuChess.usuario);
                    }
                });

            } catch (Exception e) {
                HeuChess.desfazTransacao(e);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                UtilsGUI.dialogoErro(TelaPrincipal.this, "Erro ao carregar dados do Usuário\n" + e.getMessage());
            }
        }
    }
    
    private void ativaAtualizacoesTree(boolean ativar) {
        
        if (ativar) {
            
            if (jTabbedPanePrincipal.getSelectedComponent() == jPanelObjetos) {

                if (treeModelObjetos != null && !treeModelObjetos.isExecutando()) {
                    treeModelObjetos.start();
                }
                
                if (treeModelInstituicoes != null && treeModelInstituicoes.isExecutando()) {
                    treeModelInstituicoes.stop();
                }

            } else 
                if (jTabbedPanePrincipal.getSelectedComponent() == jPanelInstituicoesTurmas) {

                    if (treeModelObjetos.isExecutando()) {
                        treeModelObjetos.stop();
                    }
                    
                    if (!treeModelInstituicoes.isExecutando()) {
                        treeModelInstituicoes.start();
                    }
                }
        } else {
            
            if (treeModelObjetos.isExecutando()) {
                treeModelObjetos.stop();
            }
            
            if (treeModelInstituicoes != null && treeModelInstituicoes.isExecutando()) {
                treeModelInstituicoes.stop();
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

        jPanelTopo = new javax.swing.JPanel();
        jToolBarPrincipal = new javax.swing.JToolBar();
        jSeparator7 = new javax.swing.JSeparator();
        jButtonCriarConjuntoHeuristico = new javax.swing.JButton();
        jButtonCriarNovaSituacaoJogo = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jButtonTestarHeuristica = new javax.swing.JButton();
        jButtonCriaNovaPartida = new javax.swing.JButton();
        jButtonCriarCampeonato = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        jButtonCriarInstituicao = new javax.swing.JButton();
        jButtonCriarTurma = new javax.swing.JButton();
        jButtonCriarUsuario = new javax.swing.JButton();
        jSeparatorToolBarAjuda = new javax.swing.JSeparator();
        jButtonAjuda = new javax.swing.JButton();
        jLabelFotoPequena = new javax.swing.JLabel();
        jLabelNomeUsuario = new javax.swing.JLabel();
        jLabelTipoUsuario = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jPanelCentral = new javax.swing.JPanel();
        jTabbedPanePrincipal = new javax.swing.JTabbedPane();
        jPanelObjetos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelBotoesObjetos = new javax.swing.JPanel();
        jButtonAbrirObjeto = new javax.swing.JButton();
        jButtonExcluirObjeto = new javax.swing.JButton();
        jScrollPaneObjetos = new javax.swing.JScrollPane();
        jTreeObjetos = new javax.swing.JTree();
        jPanelInstituicoesTurmas = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanelBotoesInstituicoesTurmas = new javax.swing.JPanel();
        jButtonAbrirInstituicaoTurma = new javax.swing.JButton();
        jButtonExcluirInstituicaoTurma = new javax.swing.JButton();
        jScrollPaneInstituicoesTurmas = new javax.swing.JScrollPane();
        jTreeInstituicoesTurmas = new javax.swing.JTree();
        jMenuBarPrincipal = new javax.swing.JMenuBar();
        jMenuCriar = new javax.swing.JMenu();
        jMenuItemNovoConjuntoHeuristico = new javax.swing.JMenuItem();
        jMenuItemNovaSituacaoTabuleiro = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItemNovoCampeonato = new javax.swing.JMenuItem();
        jSeparatorCriarInstituicaoTurmas = new javax.swing.JPopupMenu.Separator();
        jMenuItemCriarInstituicao = new javax.swing.JMenuItem();
        jMenuItemCriarTurma = new javax.swing.JMenuItem();
        jMenuItemCriarUsuario = new javax.swing.JMenuItem();
        jMenuEditar = new javax.swing.JMenu();
        jMenuItemRecortar = new javax.swing.JMenuItem();
        jMenuItemCopiar = new javax.swing.JMenuItem();
        jMenuItemColar = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItemLocalizar = new javax.swing.JMenuItem();
        jMenuGerenciar = new javax.swing.JMenu();
        jMenuItemGerenciarUsuarios = new javax.swing.JMenuItem();
        jMenuFavoritos = new javax.swing.JMenu();
        jMenuItemAdicionarFavorito = new javax.swing.JMenuItem();
        jMenuItemRemoverDosFavoritos = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuConfigurar = new javax.swing.JMenu();
        jMenuAparencia = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMeusDados = new javax.swing.JMenuItem();
        jMenuAjuda = new javax.swing.JMenu();
        jMenuItemTopicosAjuda = new javax.swing.JMenuItem();
        jMenuItemPesquisarAjuda = new javax.swing.JMenuItem();
        jMenuItemSobreJanelaAtual = new javax.swing.JMenuItem();
        jMenuItemDicasRapidas = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemPaginaInternetProjeto = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemInformacoesSobreSistema = new javax.swing.JMenuItem();
        jMenuConexao = new javax.swing.JMenu();
        jMenuItemEncerrarPrograma = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemTrocarUsuario = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("HeuChess - Ambiente para Ensino de Xadrez");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone-principal.gif")).getImage());
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

        jToolBarPrincipal.setFloatable(false);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparator7);

        jButtonCriarConjuntoHeuristico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_conjunto_heuristico.png"))); // NOI18N
        jButtonCriarConjuntoHeuristico.setToolTipText("Cria um Novo Conjunto Heurístico");
        jButtonCriarConjuntoHeuristico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarConjuntoHeuristicoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarConjuntoHeuristico);

        jButtonCriarNovaSituacaoJogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"))); // NOI18N
        jButtonCriarNovaSituacaoJogo.setToolTipText("Cria Nova Situação de Partida");
        jButtonCriarNovaSituacaoJogo.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarNovaSituacaoJogo.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarNovaSituacaoJogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarNovaSituacaoJogoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarNovaSituacaoJogo);
        jToolBarPrincipal.add(jSeparator10);

        jButtonTestarHeuristica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/avaliar-heuristica.png"))); // NOI18N
        jButtonTestarHeuristica.setToolTipText("Avalia uma Situação de Jogo com um Conjunto Heurístico");
        jButtonTestarHeuristica.setFocusable(false);
        jButtonTestarHeuristica.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTestarHeuristica.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonTestarHeuristica.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonTestarHeuristica.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTestarHeuristica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestarHeuristicaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonTestarHeuristica);

        jButtonCriaNovaPartida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_competicao.png"))); // NOI18N
        jButtonCriaNovaPartida.setToolTipText("Criar uma Nova Competição");
        jButtonCriaNovaPartida.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriaNovaPartida.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriaNovaPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriaNovaPartidaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriaNovaPartida);

        jButtonCriarCampeonato.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_campeonato.png"))); // NOI18N
        jButtonCriarCampeonato.setToolTipText("Cria um Novo Campeonato");
        jButtonCriarCampeonato.setFocusable(false);
        jButtonCriarCampeonato.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCriarCampeonato.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarCampeonato.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarCampeonato.setPreferredSize(new java.awt.Dimension(31, 29));
        jButtonCriarCampeonato.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCriarCampeonato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarCampeonatoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarCampeonato);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparator8);

        jButtonCriarInstituicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/casa.png"))); // NOI18N
        jButtonCriarInstituicao.setToolTipText("Criar uma nova Instituição");
        jButtonCriarInstituicao.setMaximumSize(new java.awt.Dimension(29, 27));
        jButtonCriarInstituicao.setMinimumSize(new java.awt.Dimension(29, 27));
        jButtonCriarInstituicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarInstituicaoActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarInstituicao);

        jButtonCriarTurma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoas.png"))); // NOI18N
        jButtonCriarTurma.setToolTipText("Criar uma nova Turma");
        jButtonCriarTurma.setFocusable(false);
        jButtonCriarTurma.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCriarTurma.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCriarTurma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarTurmaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarTurma);

        jButtonCriarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoa.png"))); // NOI18N
        jButtonCriarUsuario.setToolTipText("Cria um novo Usuário");
        jButtonCriarUsuario.setFocusable(false);
        jButtonCriarUsuario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCriarUsuario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCriarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarUsuarioActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonCriarUsuario);

        jSeparatorToolBarAjuda.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorToolBarAjuda.setMaximumSize(new java.awt.Dimension(8, 32));
        jToolBarPrincipal.add(jSeparatorToolBarAjuda);

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });
        jToolBarPrincipal.add(jButtonAjuda);

        jLabelFotoPequena.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelFotoPequena.setAlignmentY(0.0F);
        jLabelFotoPequena.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelFotoPequena.setIconTextGap(0);
        jLabelFotoPequena.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabelNomeUsuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelNomeUsuario.setText("Nome");
        jLabelNomeUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelNomeUsuarioMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelNomeUsuarioMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelNomeUsuarioMouseExited(evt);
            }
        });

        jLabelTipoUsuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTipoUsuario.setText("Tipo Usuário");
        jLabelTipoUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelTipoUsuarioMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelTipoUsuarioMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelTipoUsuarioMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanelTopoLayout = new javax.swing.GroupLayout(jPanelTopo);
        jPanelTopo.setLayout(jPanelTopoLayout);
        jPanelTopoLayout.setHorizontalGroup(
            jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTopoLayout.createSequentialGroup()
                .addComponent(jToolBarPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
                .addComponent(jLabelFotoPequena, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelNomeUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTipoUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelTopoLayout.setVerticalGroup(
            jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBarPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabelFotoPequena, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTopoLayout.createSequentialGroup()
                    .addComponent(jLabelNomeUsuario)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabelTipoUsuario)))
        );

        jLabelFotoPequena.setVisible(false);

        getContentPane().add(jPanelTopo, java.awt.BorderLayout.NORTH);

        jLabelStatus.setText("    ");
        jLabelStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(jLabelStatus, java.awt.BorderLayout.SOUTH);

        jPanelCentral.setLayout(new java.awt.CardLayout());

        jTabbedPanePrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanePrincipalStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Lista dos objetos meus e dos meus colegas");

        jButtonAbrirObjeto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirObjeto.setMnemonic('a');
        jButtonAbrirObjeto.setText("Abrir");
        jButtonAbrirObjeto.setToolTipText("Abre o objeto selecionado");
        jButtonAbrirObjeto.setEnabled(false);
        jButtonAbrirObjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirObjetoActionPerformed(evt);
            }
        });
        jPanelBotoesObjetos.add(jButtonAbrirObjeto);

        jButtonExcluirObjeto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirObjeto.setMnemonic('e');
        jButtonExcluirObjeto.setText("Excluir");
        jButtonExcluirObjeto.setToolTipText("Exclui o objeto selecionado");
        jButtonExcluirObjeto.setEnabled(false);
        jButtonExcluirObjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirObjetoActionPerformed(evt);
            }
        });
        jPanelBotoesObjetos.add(jButtonExcluirObjeto);

        jTreeObjetos.setShowsRootHandles(true);
        jTreeObjetos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeObjetos.setCellRenderer(new RenderTreeObjetos(true));
        jTreeObjetos.setToggleClickCount(1);
        jTreeObjetos.setScrollsOnExpand(true);
        jTreeObjetos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeObjetosMouseClicked(evt);
            }
        });
        jTreeObjetos.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeObjetosValueChanged(evt);
            }
        });
        jScrollPaneObjetos.setViewportView(jTreeObjetos);

        javax.swing.GroupLayout jPanelObjetosLayout = new javax.swing.GroupLayout(jPanelObjetos);
        jPanelObjetos.setLayout(jPanelObjetosLayout);
        jPanelObjetosLayout.setHorizontalGroup(
            jPanelObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBotoesObjetos, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
            .addGroup(jPanelObjetosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addComponent(jScrollPaneObjetos, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelObjetosLayout.setVerticalGroup(
            jPanelObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelObjetosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneObjetos, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Objetos", new javax.swing.ImageIcon(getClass().getResource("/icones/pastas.png")), jPanelObjetos); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Lista de Instituições e Turmas que tenho acesso");

        jButtonAbrirInstituicaoTurma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirInstituicaoTurma.setMnemonic('a');
        jButtonAbrirInstituicaoTurma.setText("Abrir");
        jButtonAbrirInstituicaoTurma.setToolTipText("Abre o objeto selecionado");
        jButtonAbrirInstituicaoTurma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirInstituicaoTurmaActionPerformed(evt);
            }
        });
        jPanelBotoesInstituicoesTurmas.add(jButtonAbrirInstituicaoTurma);

        jButtonExcluirInstituicaoTurma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/menos.png"))); // NOI18N
        jButtonExcluirInstituicaoTurma.setMnemonic('e');
        jButtonExcluirInstituicaoTurma.setText("Excluir");
        jButtonExcluirInstituicaoTurma.setToolTipText("Exclui o objeto selecionado");
        jButtonExcluirInstituicaoTurma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExcluirInstituicaoTurmaActionPerformed(evt);
            }
        });
        jPanelBotoesInstituicoesTurmas.add(jButtonExcluirInstituicaoTurma);

        jTreeInstituicoesTurmas.setShowsRootHandles(true);
        jTreeInstituicoesTurmas.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeInstituicoesTurmas.setCellRenderer(new RenderTreeObjetos(false));
        jTreeInstituicoesTurmas.setToggleClickCount(1);
        jTreeInstituicoesTurmas.setScrollsOnExpand(true);
        jTreeInstituicoesTurmas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeInstituicoesTurmasMouseClicked(evt);
            }
        });
        jTreeInstituicoesTurmas.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeInstituicoesTurmasValueChanged(evt);
            }
        });
        jScrollPaneInstituicoesTurmas.setViewportView(jTreeInstituicoesTurmas);

        javax.swing.GroupLayout jPanelInstituicoesTurmasLayout = new javax.swing.GroupLayout(jPanelInstituicoesTurmas);
        jPanelInstituicoesTurmas.setLayout(jPanelInstituicoesTurmasLayout);
        jPanelInstituicoesTurmasLayout.setHorizontalGroup(
            jPanelInstituicoesTurmasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInstituicoesTurmasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInstituicoesTurmasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneInstituicoesTurmas)
                    .addGroup(jPanelInstituicoesTurmasLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 503, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanelBotoesInstituicoesTurmas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelInstituicoesTurmasLayout.setVerticalGroup(
            jPanelInstituicoesTurmasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInstituicoesTurmasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneInstituicoesTurmas, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelBotoesInstituicoesTurmas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPanePrincipal.addTab("Instituições e Turmas", new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoas.png")), jPanelInstituicoesTurmas); // NOI18N

        jPanelCentral.add(jTabbedPanePrincipal, "card2");

        getContentPane().add(jPanelCentral, java.awt.BorderLayout.CENTER);

        jMenuCriar.setMnemonic('C');
        jMenuCriar.setText("Criar");

        jMenuItemNovoConjuntoHeuristico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNovoConjuntoHeuristico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_conjunto_heuristico.png"))); // NOI18N
        jMenuItemNovoConjuntoHeuristico.setMnemonic('j');
        jMenuItemNovoConjuntoHeuristico.setText("Conjunto Heurístico");
        jMenuItemNovoConjuntoHeuristico.setToolTipText("Cria um Novo Conjunto Heurístico");
        jMenuItemNovoConjuntoHeuristico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovoConjuntoHeuristicoActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemNovoConjuntoHeuristico);

        jMenuItemNovaSituacaoTabuleiro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNovaSituacaoTabuleiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"))); // NOI18N
        jMenuItemNovaSituacaoTabuleiro.setMnemonic('s');
        jMenuItemNovaSituacaoTabuleiro.setText("Situação de Tabuleiro");
        jMenuItemNovaSituacaoTabuleiro.setToolTipText("Cria uma nova situação de Tabuleiro");
        jMenuItemNovaSituacaoTabuleiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovaSituacaoTabuleiroActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemNovaSituacaoTabuleiro);
        jMenuCriar.add(jSeparator9);

        jMenuItemNovoCampeonato.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNovoCampeonato.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_campeonato.png"))); // NOI18N
        jMenuItemNovoCampeonato.setMnemonic('p');
        jMenuItemNovoCampeonato.setText("Campeonato");
        jMenuItemNovoCampeonato.setToolTipText("Cria um novo Campeonato");
        jMenuItemNovoCampeonato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovoCampeonatoActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemNovoCampeonato);
        jMenuCriar.add(jSeparatorCriarInstituicaoTurmas);

        jMenuItemCriarInstituicao.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarInstituicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/casa.png"))); // NOI18N
        jMenuItemCriarInstituicao.setMnemonic('i');
        jMenuItemCriarInstituicao.setText("Instituição");
        jMenuItemCriarInstituicao.setToolTipText("Cria uma nova Instituição");
        jMenuItemCriarInstituicao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarInstituicaoActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarInstituicao);

        jMenuItemCriarTurma.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarTurma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoas.png"))); // NOI18N
        jMenuItemCriarTurma.setMnemonic('t');
        jMenuItemCriarTurma.setText("Turma");
        jMenuItemCriarTurma.setToolTipText("Cria uma nova Turma");
        jMenuItemCriarTurma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarTurmaActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarTurma);

        jMenuItemCriarUsuario.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCriarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoa.png"))); // NOI18N
        jMenuItemCriarUsuario.setText("Usuário");
        jMenuItemCriarUsuario.setToolTipText("Cria um novo Usuário");
        jMenuItemCriarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCriarUsuarioActionPerformed(evt);
            }
        });
        jMenuCriar.add(jMenuItemCriarUsuario);

        jMenuBarPrincipal.add(jMenuCriar);

        jMenuEditar.setMnemonic('E');
        jMenuEditar.setText("Editar");

        jMenuItemRecortar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRecortar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/recortar.png"))); // NOI18N
        jMenuItemRecortar.setText("Recortar");
        jMenuItemRecortar.setToolTipText("Recorta o Item Selecionado");
        jMenuEditar.add(jMenuItemRecortar);

        jMenuItemCopiar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/copiar.png"))); // NOI18N
        jMenuItemCopiar.setText("Copiar");
        jMenuItemCopiar.setToolTipText("Copia o Item Selecionado");
        jMenuEditar.add(jMenuItemCopiar);

        jMenuItemColar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemColar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/colar.png"))); // NOI18N
        jMenuItemColar.setText("Colar");
        jMenuItemColar.setToolTipText("Cola o Item anteriormente Copiado");
        jMenuEditar.add(jMenuItemColar);
        jMenuEditar.add(jSeparator4);

        jMenuItemLocalizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLocalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/pesquisar.png"))); // NOI18N
        jMenuItemLocalizar.setText("Localizar");
        jMenuItemLocalizar.setToolTipText("Localiza um Item");
        jMenuEditar.add(jMenuItemLocalizar);

        jMenuBarPrincipal.add(jMenuEditar);
        jMenuEditar.setVisible(false);

        jMenuGerenciar.setMnemonic('g');
        jMenuGerenciar.setText("Gerenciar");

        jMenuItemGerenciarUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pesquisa_pessoa.png"))); // NOI18N
        jMenuItemGerenciarUsuarios.setText("Usuários");
        jMenuItemGerenciarUsuarios.setToolTipText("Gerencia os Usuários do Sistema");
        jMenuItemGerenciarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGerenciarUsuariosActionPerformed(evt);
            }
        });
        jMenuGerenciar.add(jMenuItemGerenciarUsuarios);

        jMenuBarPrincipal.add(jMenuGerenciar);

        jMenuFavoritos.setMnemonic('F');
        jMenuFavoritos.setText("Favoritos");

        jMenuItemAdicionarFavorito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/favoritos.png"))); // NOI18N
        jMenuItemAdicionarFavorito.setText(" Adicionar aos Favoritos");
        jMenuItemAdicionarFavorito.setToolTipText("Adiciona o item selecionado aos Favoritos");
        jMenuFavoritos.add(jMenuItemAdicionarFavorito);

        jMenuItemRemoverDosFavoritos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemRemoverDosFavoritos.setText("Remover dos Favoritos");
        jMenuItemRemoverDosFavoritos.setToolTipText("Remove um item dos Favoritos");
        jMenuFavoritos.add(jMenuItemRemoverDosFavoritos);
        jMenuFavoritos.add(jSeparator1);

        jMenuBarPrincipal.add(jMenuFavoritos);
        jMenuFavoritos.setVisible(false);

        jMenuConfigurar.setMnemonic('n');
        jMenuConfigurar.setText("Configurar");
        jMenuConfigurar.setToolTipText("Opções de Configuração da Ferramenta");

        jMenuAparencia.setMnemonic('p');
        jMenuAparencia.setText("Aparência");
        jMenuAparencia.setToolTipText("Aparência da Tela da Aplicação");
        jMenuConfigurar.add(jMenuAparencia);
        jMenuAparencia.setVisible(false);

        jMenuConfigurar.add(jSeparator5);
        jSeparator5.setVisible(false);

        jMenuItemMeusDados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_pessoa.png"))); // NOI18N
        jMenuItemMeusDados.setMnemonic('d');
        jMenuItemMeusDados.setText("Meus Dados");
        jMenuItemMeusDados.setToolTipText("Edita os dados da conta do usuário");
        jMenuItemMeusDados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMeusDadosActionPerformed(evt);
            }
        });
        jMenuConfigurar.add(jMenuItemMeusDados);

        jMenuBarPrincipal.add(jMenuConfigurar);

        jMenuAjuda.setMnemonic('A');
        jMenuAjuda.setText("Ajuda");

        jMenuItemTopicosAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-topicos.png"))); // NOI18N
        jMenuItemTopicosAjuda.setText("Tópicos");
        jMenuAjuda.add(jMenuItemTopicosAjuda);
        jMenuItemTopicosAjuda.setVisible(false);

        jMenuItemPesquisarAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemPesquisarAjuda.setText("Pesquisar");
        jMenuAjuda.add(jMenuItemPesquisarAjuda);
        jMenuItemPesquisarAjuda.setVisible(false);

        jMenuItemSobreJanelaAtual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jMenuItemSobreJanelaAtual.setText("Sobre a Janela Atual");
        jMenuItemSobreJanelaAtual.setToolTipText("Consulta o texto de ajuda desta tela");
        jMenuItemSobreJanelaAtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSobreJanelaAtualActionPerformed(evt);
            }
        });
        jMenuAjuda.add(jMenuItemSobreJanelaAtual);

        jMenuItemDicasRapidas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemDicasRapidas.setText("Dicas Rápidas");
        jMenuAjuda.add(jMenuItemDicasRapidas);
        jMenuItemDicasRapidas.setVisible(false);

        jMenuAjuda.add(jSeparator2);

        jMenuItemPaginaInternetProjeto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/vazio.png"))); // NOI18N
        jMenuItemPaginaInternetProjeto.setText("Página na Internet do Projeto");
        jMenuItemPaginaInternetProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPaginaInternetProjetoActionPerformed(evt);
            }
        });
        jMenuAjuda.add(jMenuItemPaginaInternetProjeto);
        jMenuAjuda.add(jSeparator3);

        jMenuItemInformacoesSobreSistema.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/sobre.png"))); // NOI18N
        jMenuItemInformacoesSobreSistema.setText("Informações sobre o Sistema");
        jMenuAjuda.add(jMenuItemInformacoesSobreSistema);
        jMenuItemInformacoesSobreSistema.setVisible(false);

        jMenuBarPrincipal.add(jMenuAjuda);
        jMenuAjuda.setVisible(false);

        jMenuConexao.setMnemonic('S');
        jMenuConexao.setText("Conexão");

        jMenuItemEncerrarPrograma.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemEncerrarPrograma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png"))); // NOI18N
        jMenuItemEncerrarPrograma.setText("Sair do Programa");
        jMenuItemEncerrarPrograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEncerrarProgramaActionPerformed(evt);
            }
        });
        jMenuConexao.add(jMenuItemEncerrarPrograma);
        jMenuConexao.add(jSeparator6);

        jMenuItemTrocarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/reset16.png"))); // NOI18N
        jMenuItemTrocarUsuario.setText("Trocar Usuário");
        jMenuItemTrocarUsuario.setToolTipText("Volta para a tela de Entrada do Sistema");
        jMenuItemTrocarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTrocarUsuarioActionPerformed(evt);
            }
        });
        jMenuConexao.add(jMenuItemTrocarUsuario);

        jMenuBarPrincipal.add(jMenuConexao);

        setJMenuBar(jMenuBarPrincipal);

        setSize(new java.awt.Dimension(808, 627));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCriarNovaSituacaoJogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarNovaSituacaoJogoActionPerformed
        novaSituacaoJogo();
    }//GEN-LAST:event_jButtonCriarNovaSituacaoJogoActionPerformed

    private void jMenuItemNovaSituacaoTabuleiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovaSituacaoTabuleiroActionPerformed
        novaSituacaoJogo();
    }//GEN-LAST:event_jMenuItemNovaSituacaoTabuleiroActionPerformed

    private void jButtonCriarConjuntoHeuristicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarConjuntoHeuristicoActionPerformed
        novoConjuntoHeuristico();
    }//GEN-LAST:event_jButtonCriarConjuntoHeuristicoActionPerformed

    private void jButtonExcluirObjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirObjetoActionPerformed
        
        ElementoLista elemento = treeModelObjetos.recuperaElementoLista(null);
        
        if (elemento == null){
            jButtonAbrirObjeto.setEnabled(false);
            jButtonExcluirObjeto.setEnabled(false);
            return;
        }        
        
        excluindoObjeto(elemento);
    }//GEN-LAST:event_jButtonExcluirObjetoActionPerformed

    private void jButtonAbrirObjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirObjetoActionPerformed
        
        ElementoLista elemento = treeModelObjetos.recuperaElementoLista(null);
        
        if (elemento == null){
            jButtonAbrirObjeto.setEnabled(false);
            jButtonExcluirObjeto.setEnabled(false);
            return;
        }
        
        abrindoObjeto(elemento);
    }//GEN-LAST:event_jButtonAbrirObjetoActionPerformed

    private void jTreeObjetosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeObjetosMouseClicked
                
        if (treeModelObjetos.verificaDuploClique(evt, null)){
            
            abrindoObjeto(treeModelObjetos.recuperaElementoLista(null));
        }
    }//GEN-LAST:event_jTreeObjetosMouseClicked

    private void jTreeObjetosValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeObjetosValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeObjetos.getLastSelectedPathComponent();
        
        if (node == null) {
            jButtonAbrirObjeto.setEnabled(false);
            jButtonExcluirObjeto.setEnabled(false);
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof ElementoLista) {
            
            jButtonAbrirObjeto.setEnabled(true);
                
            if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR) {
                
                // Pode alterar e excluir tudo
                
                jButtonExcluirObjeto.setEnabled(true);
            }else{
                    
                ElementoLista elemento = (ElementoLista) nodeInfo;
                
                if (elemento.getClasse() == Usuario.class){
                    
                    // objeto alvo é Usuario e o usuário conectado não é administrador
                    
                    jButtonExcluirObjeto.setEnabled(false);
                    
                }else{
                    
                    // objeto alvo pode ser Turma, Conjunto Heurístico ou Situacao de Jogo
                    // A validação será feita quando o botão for clicado.
                    
                    jButtonExcluirObjeto.setEnabled(true);            
                }
            }
        }else{
            jButtonAbrirObjeto.setEnabled(false);
            jButtonExcluirObjeto.setEnabled(false);
        }
    }//GEN-LAST:event_jTreeObjetosValueChanged
        
    private void jMenuItemNovoConjuntoHeuristicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovoConjuntoHeuristicoActionPerformed
        novoConjuntoHeuristico();
    }//GEN-LAST:event_jMenuItemNovoConjuntoHeuristicoActionPerformed

    private void jMenuItemEncerrarProgramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEncerrarProgramaActionPerformed
        confirmaSaida();
    }//GEN-LAST:event_jMenuItemEncerrarProgramaActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaSaida();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this, "TelaPrincipal");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        ativaAtualizacoesTree(true);
    }//GEN-LAST:event_formWindowActivated

    private void jButtonCriaNovaPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriaNovaPartidaActionPerformed
        novaPartida();
    }//GEN-LAST:event_jButtonCriaNovaPartidaActionPerformed

    private void jButtonTestarHeuristicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestarHeuristicaActionPerformed
        avaliarSituacaoJogo();
    }//GEN-LAST:event_jButtonTestarHeuristicaActionPerformed

    private void jMenuItemCriarInstituicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarInstituicaoActionPerformed
        novaInstituicao();
    }//GEN-LAST:event_jMenuItemCriarInstituicaoActionPerformed

    private void jButtonCriarInstituicaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarInstituicaoActionPerformed
        novaInstituicao();
    }//GEN-LAST:event_jButtonCriarInstituicaoActionPerformed

    private void jTreeInstituicoesTurmasValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeInstituicoesTurmasValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTreeInstituicoesTurmas.getLastSelectedPathComponent();
        
        if (node == null) {
            jButtonAbrirInstituicaoTurma.setEnabled(false);
            jButtonExcluirInstituicaoTurma.setEnabled(false);
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        
        if (nodeInfo instanceof ElementoLista) {
            
            jButtonAbrirInstituicaoTurma.setEnabled(true);
                
            if (HeuChess.usuario.getTipo() == Usuario.ADMINISTRADOR) {
                
                // Pode alterar e excluir tudo
                
                jButtonExcluirInstituicaoTurma.setEnabled(true);
            }else{
            
                ElementoLista elemento = (ElementoLista) nodeInfo;
                
                if (elemento.getClasse() == Turma.class){
                    
                    // A verificação se pode ou não excluir será feita quando o botão for clicado
                    
                    jButtonExcluirInstituicaoTurma.setEnabled(true);
                    
                }else{
                    
                    // objeto alvo é Instituição e o usuário conectado não é Administrador
                    
                    jButtonExcluirInstituicaoTurma.setEnabled(false);            
                }
            }
        }else{
            jButtonAbrirInstituicaoTurma.setEnabled(false);
            jButtonExcluirInstituicaoTurma.setEnabled(false);
        }
    }//GEN-LAST:event_jTreeInstituicoesTurmasValueChanged

    private void jTreeInstituicoesTurmasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeInstituicoesTurmasMouseClicked
                
        if (treeModelInstituicoes.verificaDuploClique(evt, null)){
            
            abrindoObjeto(treeModelInstituicoes.recuperaElementoLista(null));
        }
    }//GEN-LAST:event_jTreeInstituicoesTurmasMouseClicked

    private void jButtonAbrirInstituicaoTurmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirInstituicaoTurmaActionPerformed
        
        ElementoLista elemento = treeModelInstituicoes.recuperaElementoLista(null);
        
        if (elemento == null){
            jButtonAbrirInstituicaoTurma.setEnabled(false);
            jButtonExcluirInstituicaoTurma.setEnabled(false);
            return;
        }
        
        abrindoObjeto(elemento);
    }//GEN-LAST:event_jButtonAbrirInstituicaoTurmaActionPerformed

    private void jButtonExcluirInstituicaoTurmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirInstituicaoTurmaActionPerformed
        
        ElementoLista elemento = treeModelInstituicoes.recuperaElementoLista(null);
        
        if (elemento == null){
            jButtonAbrirInstituicaoTurma.setEnabled(false);
            jButtonExcluirInstituicaoTurma.setEnabled(false);
            return;
        }        
        
        excluindoObjeto(elemento);
    }//GEN-LAST:event_jButtonExcluirInstituicaoTurmaActionPerformed

    private void jButtonCriarTurmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarTurmaActionPerformed
        novaTurma();
    }//GEN-LAST:event_jButtonCriarTurmaActionPerformed

    private void jMenuItemCriarTurmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarTurmaActionPerformed
        novaTurma();
    }//GEN-LAST:event_jMenuItemCriarTurmaActionPerformed

    private void jMenuItemMeusDadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMeusDadosActionPerformed
        telaUsuario(false);
    }//GEN-LAST:event_jMenuItemMeusDadosActionPerformed

    private void jLabelNomeUsuarioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelNomeUsuarioMouseEntered
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLabelNomeUsuarioMouseEntered

    private void jLabelNomeUsuarioMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelNomeUsuarioMouseExited
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLabelNomeUsuarioMouseExited

    private void jLabelNomeUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelNomeUsuarioMouseClicked
        telaUsuario(false);
    }//GEN-LAST:event_jLabelNomeUsuarioMouseClicked

    private void jMenuItemCriarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCriarUsuarioActionPerformed
        telaUsuario(true);
    }//GEN-LAST:event_jMenuItemCriarUsuarioActionPerformed

    private void jButtonCriarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarUsuarioActionPerformed
        telaUsuario(true);
    }//GEN-LAST:event_jButtonCriarUsuarioActionPerformed

    private void jMenuItemTrocarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTrocarUsuarioActionPerformed
        
        dispose();
        
        try {
            UsuarioDAO.finalizandoAcesso(HeuChess.usuario);
            ConexaoDBHeuChess.commit(); 
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaLogin telaLogin = new TelaLogin();
                    telaLogin.setVisible(true);
                }
            });
            
        } catch (Exception e){
            HeuChess.avisoFechaPrograma(e, "Erro ao registrar fim de sessão do Usuário no Banco de Dados!", -3);
        }
    }//GEN-LAST:event_jMenuItemTrocarUsuarioActionPerformed

    private void jMenuItemGerenciarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGerenciarUsuariosActionPerformed
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaGerenciaUsuarios tela = new TelaGerenciaUsuarios(TelaPrincipal.this);
            }
        });
    }//GEN-LAST:event_jMenuItemGerenciarUsuariosActionPerformed

    private void jTabbedPanePrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanePrincipalStateChanged
        ativaAtualizacoesTree(true);
    }//GEN-LAST:event_jTabbedPanePrincipalStateChanged

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        ativaAtualizacoesTree(false);
    }//GEN-LAST:event_formWindowDeactivated

    private void jLabelTipoUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTipoUsuarioMouseClicked
        telaUsuario(false);
    }//GEN-LAST:event_jLabelTipoUsuarioMouseClicked

    private void jLabelTipoUsuarioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTipoUsuarioMouseEntered
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLabelTipoUsuarioMouseEntered

    private void jLabelTipoUsuarioMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTipoUsuarioMouseExited
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLabelTipoUsuarioMouseExited

    private void jMenuItemSobreJanelaAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSobreJanelaAtualActionPerformed
        HeuChess.ajuda.abre(this, "TelaPrincipal");
    }//GEN-LAST:event_jMenuItemSobreJanelaAtualActionPerformed

    private void jMenuItemPaginaInternetProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPaginaInternetProjetoActionPerformed
        HeuChess.ajuda.abre(this, "PaginaProjeto");
    }//GEN-LAST:event_jMenuItemPaginaInternetProjetoActionPerformed

    private void jButtonCriarCampeonatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarCampeonatoActionPerformed
        novoCampeonato();
    }//GEN-LAST:event_jButtonCriarCampeonatoActionPerformed

    private void jMenuItemNovoCampeonatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovoCampeonatoActionPerformed
        novoCampeonato();
    }//GEN-LAST:event_jMenuItemNovoCampeonatoActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrirInstituicaoTurma;
    private javax.swing.JButton jButtonAbrirObjeto;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonCriaNovaPartida;
    private javax.swing.JButton jButtonCriarCampeonato;
    private javax.swing.JButton jButtonCriarConjuntoHeuristico;
    private javax.swing.JButton jButtonCriarInstituicao;
    private javax.swing.JButton jButtonCriarNovaSituacaoJogo;
    private javax.swing.JButton jButtonCriarTurma;
    private javax.swing.JButton jButtonCriarUsuario;
    private javax.swing.JButton jButtonExcluirInstituicaoTurma;
    private javax.swing.JButton jButtonExcluirObjeto;
    private javax.swing.JButton jButtonTestarHeuristica;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabelFotoPequena;
    private javax.swing.JLabel jLabelNomeUsuario;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTipoUsuario;
    private javax.swing.JMenu jMenuAjuda;
    private javax.swing.JMenu jMenuAparencia;
    private javax.swing.JMenuBar jMenuBarPrincipal;
    private javax.swing.JMenu jMenuConexao;
    private javax.swing.JMenu jMenuConfigurar;
    private javax.swing.JMenu jMenuCriar;
    private javax.swing.JMenu jMenuEditar;
    private javax.swing.JMenu jMenuFavoritos;
    private javax.swing.JMenu jMenuGerenciar;
    private javax.swing.JMenuItem jMenuItemAdicionarFavorito;
    private javax.swing.JMenuItem jMenuItemColar;
    private javax.swing.JMenuItem jMenuItemCopiar;
    private javax.swing.JMenuItem jMenuItemCriarInstituicao;
    private javax.swing.JMenuItem jMenuItemCriarTurma;
    private javax.swing.JMenuItem jMenuItemCriarUsuario;
    private javax.swing.JMenuItem jMenuItemDicasRapidas;
    private javax.swing.JMenuItem jMenuItemEncerrarPrograma;
    private javax.swing.JMenuItem jMenuItemGerenciarUsuarios;
    private javax.swing.JMenuItem jMenuItemInformacoesSobreSistema;
    private javax.swing.JMenuItem jMenuItemLocalizar;
    private javax.swing.JMenuItem jMenuItemMeusDados;
    private javax.swing.JMenuItem jMenuItemNovaSituacaoTabuleiro;
    private javax.swing.JMenuItem jMenuItemNovoCampeonato;
    private javax.swing.JMenuItem jMenuItemNovoConjuntoHeuristico;
    private javax.swing.JMenuItem jMenuItemPaginaInternetProjeto;
    private javax.swing.JMenuItem jMenuItemPesquisarAjuda;
    private javax.swing.JMenuItem jMenuItemRecortar;
    private javax.swing.JMenuItem jMenuItemRemoverDosFavoritos;
    private javax.swing.JMenuItem jMenuItemSobreJanelaAtual;
    private javax.swing.JMenuItem jMenuItemTopicosAjuda;
    private javax.swing.JMenuItem jMenuItemTrocarUsuario;
    private javax.swing.JPanel jPanelBotoesInstituicoesTurmas;
    private javax.swing.JPanel jPanelBotoesObjetos;
    private javax.swing.JPanel jPanelCentral;
    private javax.swing.JPanel jPanelInstituicoesTurmas;
    private javax.swing.JPanel jPanelObjetos;
    private javax.swing.JPanel jPanelTopo;
    private javax.swing.JScrollPane jScrollPaneInstituicoesTurmas;
    private javax.swing.JScrollPane jScrollPaneObjetos;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JPopupMenu.Separator jSeparatorCriarInstituicaoTurmas;
    private javax.swing.JSeparator jSeparatorToolBarAjuda;
    private javax.swing.JTabbedPane jTabbedPanePrincipal;
    private javax.swing.JToolBar jToolBarPrincipal;
    private javax.swing.JTree jTreeInstituicoesTurmas;
    private javax.swing.JTree jTreeObjetos;
    // End of variables declaration//GEN-END:variables
}
