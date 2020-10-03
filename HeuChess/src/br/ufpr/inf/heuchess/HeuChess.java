package br.ufpr.inf.heuchess;

import br.ufpr.inf.heuchess.persistencia.*;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.telas.iniciais.AcessoTelaUsuario;
import br.ufpr.inf.heuchess.telas.iniciais.TelaLogin;
import br.ufpr.inf.heuchess.telas.iniciais.TelaPrincipal;
import br.ufpr.inf.heuchess.telas.iniciais.TelaUsuario;
import br.ufpr.inf.utils.ArquivoLog;
import br.ufpr.inf.utils.InstanceListener;
import br.ufpr.inf.utils.InstanceManager;
import br.ufpr.inf.utils.SoundFX;
import br.ufpr.inf.utils.Utils;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.TelaSplash;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.TimingUtils;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 10:18
 */
public class HeuChess {
        
    public static final int PROPRIEDADE_DO_SISTEMA = 0;
    
    public  static HeuChess      instancia;
    private static TelaSplash    telaSplash;
    private static TelaLogin     telaLogin;
    public  static TelaPrincipal telaPrincipal;
    public  static Usuario       usuario;    
            
    public static ImageIcon iconeAbaFechada;
    public static ImageIcon iconeAbaAberta;
    
    public static SoundFX   somApagar;    
    public static SoundFX   somDragAndDrop;
    public static SoundFX   somPartidaConcluida;
    public static SoundFX   somPartidaCancelada;
    public static SoundFX   somPecaColocada;
    public static SoundFX   somPecaMovida;
    public static SoundFX   somPecaCapturada;
    public static SoundFX   somPecaPromovida;
    
    public static DefaultMutableTreeNode treeFuncoesBasicas;
          
    public  static Ajuda      ajuda;
    private static ArquivoLog arquivoLog;
    
    ///////////////////////////////////////
    // Configurações Gerais da Aplicação //
    ///////////////////////////////////////
    
    public static boolean somAtivado = true;
                
    public static LookAndFeelInfo looks[];
    public static int             lookSelecionado;
    
    public HeuChess() {       
        
        telaSplash = new TelaSplash("/imagens/splash.jpg");        
        telaSplash.drawText("Iniciando...",Color.BLACK);     
        
        try {
            ajuda = new Ajuda("português_br");
        } catch (Exception e) {
            avisoFechaPrograma(e, "Não foi possível carrregar todos os arquivos de configuração!", -2);
        }
        
        try {
            Anotacoes.carregaIcones();
            
            iconeAbaFechada = new ImageIcon(getClass().getResource("/icones/fold.png"));
            iconeAbaAberta  = new ImageIcon(getClass().getResource("/icones/unfold.png"));
            
            somApagar           = new SoundFX("res/sons/somApagar.wav");
            somDragAndDrop      = new SoundFX("res/sons/somDragAndDrop.wav");
            somPartidaCancelada = new SoundFX("res/sons/somPartidaCancelada.wav");
            somPartidaConcluida = new SoundFX("res/sons/somPartidaConcluida.wav");
            somPecaColocada     = new SoundFX("res/sons/somPecaColocada.wav");
            somPecaMovida       = new SoundFX("res/sons/somPecaMovida.wav");
            somPecaCapturada    = new SoundFX("res/sons/somPecaCapturada.wav");
            somPecaPromovida    = new SoundFX("res/sons/somPecaPromovida.wav");
        
        } catch (Exception ex) {
            avisoFechaPrograma(ex, "Não foi possível carrregar todos os arquivos multimídia!",-2);
        }
    
        telaSplash.drawText("Conectando ao servidor...",Color.BLACK);        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                
                carregandoDados();
            }
        });
    }
    
    public static void carregandoDados(){
    
        try{
            System.err.println("Data e Hora do Banco de Dados [" + ConexaoDBHeuChess.getDataHora() + "]");
        }catch(Exception e){   
            avisoFechaPrograma(e, "Não foi possível criar conexão com o Banco de Dados!",-2);
        }
        
        telaSplash.setProgressBar(10,200);
        
        try{
            telaSplash.drawText("Carregando tipos de usuários...",Color.BLACK);        
            UsuarioDAO.carregaSituacoesTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Usuario!",-2);
        }
        
        telaSplash.setProgressBar(20,200);
        
        try{
            telaSplash.drawText("Carregando tipos de conjuntos heurísticos...",Color.BLACK);
            ConjuntoHeuristicoDAO.carregaTipos();        
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Conjunto Heuristico!",-2);
        }
        
        telaSplash.setProgressBar(30,200);
        
        try{
            telaSplash.drawText("Carregando tipos de etapas...",Color.BLACK);
            EtapaDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Etapa!",-2);
        }
        
        telaSplash.setProgressBar(40,200);

        try{
            telaSplash.drawText("Carregando tipos de heurísticas...",Color.BLACK);
            HeuristicaDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Heurísticas!",-2);
        }
        
        telaSplash.setProgressBar(50,200);
        
        try{
            telaSplash.drawText("Carregando tipos de regiões...",Color.BLACK);
            RegiaoDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Regiãos!",-2);
        }
        
        telaSplash.setProgressBar(60,200);
        
        try{
            telaSplash.drawText("Carregando tipos de anotações...",Color.BLACK);
            AnotacaoDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Anotação!",-2);
        }
        
        telaSplash.setProgressBar(70,200);
        
        try{
            telaSplash.drawText("Carregando tipos de Situações de Jogo...",Color.BLACK);
            SituacaoJogoDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Situações de Jogo!",-2);
        }
        
        telaSplash.setProgressBar(80,200);
        
        try{
            telaSplash.drawText("Carregando situações de Turma...",Color.BLACK);        
            TurmaDAO.carregaSituacoes();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar as situações de Turma!",-2);
        }
        
        telaSplash.setProgressBar(90,200);
        
        try{
            telaSplash.drawText("Carregando tipos de funções...",Color.BLACK);
            FuncaoDAO.carregaTipos();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar os tipos de Função!",-2);
        }
        
        telaSplash.setProgressBar(95,200);
        
        try{
            telaSplash.drawText("Carregando funções básicas...",Color.BLACK);        
            FuncaoDAO.carregaTodas();
        }catch(Exception e){
            avisoFechaPrograma(e, "Não foi possível carregar as Funções Básicas!",-2);
        }
        
        telaSplash.setProgressBar(98,200);
        
        telaSplash.drawText("Carregando LookAndFeels instalados...",Color.BLACK);
        looks = javax.swing.UIManager.getInstalledLookAndFeels();
        
        telaSplash.setProgressBar(100,200);        
        telaSplash.close();
        telaSplash = null;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                
                telaLogin  = new TelaLogin();    
                telaLogin.setVisible(true);
            }
        });
    }
    
    public static void inicializa(Usuario usuarioAtual){
        
        telaLogin = null;
        usuario   = usuarioAtual;               
        
        try {
            String valor = PropriedadeDAO.busca(PROPRIEDADE_DO_SISTEMA, Propriedade.SITUACAO_ACESSO_SISTEMA);

            if (!valor.equalsIgnoreCase("LIBERADO")) {
                
                UtilsGUI.dialogoAtencao(null, valor);
                
                if (usuario.getId() != 1) {            
                    
                    ////////////////////////////////////////////
                    // Usuário não é o responsável do Sistema //
                    ////////////////////////////////////////////
                    
                    fechaPrograma(0);                    
                }        
            }

        } catch (Exception e) {
            avisoFechaPrograma(e, "Não foi possível verificar a Situação de Acesso ao Sistema!", -2);
        }
        
        ////////////////////////////////
        // Cria a TreeFunções Básicas //
        ////////////////////////////////
        
        if (FuncaoDAO.funcoesBasicas != null){
            
            treeFuncoesBasicas = new DefaultMutableTreeNode("Funções Basicas");
            
            DefaultMutableTreeNode treeFuncoesBasicasTempo      = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_TEMPO);
            DefaultMutableTreeNode treeFuncoesBasicasPosicao    = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_POSICAO);
            DefaultMutableTreeNode treeFuncoesBasicasQuantidade = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_QUANTIDADE);
            DefaultMutableTreeNode treeFuncoesBasicasValor      = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_VALOR);
            DefaultMutableTreeNode treeFuncoesBasicasSituacao   = new DefaultMutableTreeNode(Funcao.FUNCAO_BASICA_SITUACAO);
            
            treeFuncoesBasicas.add(treeFuncoesBasicasTempo);
            treeFuncoesBasicas.add(treeFuncoesBasicasPosicao);
            treeFuncoesBasicas.add(treeFuncoesBasicasQuantidade);
            treeFuncoesBasicas.add(treeFuncoesBasicasValor);
            treeFuncoesBasicas.add(treeFuncoesBasicasSituacao);
            
            for (int x = 0; x < FuncaoDAO.funcoesBasicas.size(); x++){
                
                Funcao funcao = FuncaoDAO.funcoesBasicas.get(x);
                
                if (funcao.getTipo() == Funcao.FUNCAO_BASICA_TEMPO){
                    treeFuncoesBasicasTempo.add(new DefaultMutableTreeNode(funcao));
                }else
                    if (funcao.getTipo() == Funcao.FUNCAO_BASICA_POSICAO){
                        treeFuncoesBasicasPosicao.add(new DefaultMutableTreeNode(funcao));
                    }else
                        if (funcao.getTipo() == Funcao.FUNCAO_BASICA_QUANTIDADE){
                            treeFuncoesBasicasQuantidade.add(new DefaultMutableTreeNode(funcao));
                        }else
                            if (funcao.getTipo() == Funcao.FUNCAO_BASICA_VALOR){
                                treeFuncoesBasicasValor.add(new DefaultMutableTreeNode(funcao));
                            }else
                                if (funcao.getTipo() == Funcao.FUNCAO_BASICA_SITUACAO){
                                    treeFuncoesBasicasSituacao.add(new DefaultMutableTreeNode(funcao));
                                }
            }
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                telaPrincipal = new TelaPrincipal();
            }
        });
    }
         
    public static String converteSenha(String senha){
        return Utils.geraMD5("hx12wknOpzz1-" + senha);
    }
    
    public static boolean verificaSenhas(String senhaAberta, String senhaCriptografada){
        
        String senhaAbertaCriptografada = converteSenha(senhaAberta);
        
        return senhaAbertaCriptografada.equals(senhaCriptografada);
    }
    
    public static void dadosAutor(final AcessoTelaUsuario acessoTelaUsuario, long idAutor) {
        
        final Usuario usuarioAutor;

        try {
            acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            usuarioAutor = UsuarioDAO.busca(idAutor);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaUsuario tela = new TelaUsuario(acessoTelaUsuario, usuarioAutor);
                }
            });
            
        } catch (Exception e) {
            desfazTransacao(e);
            
            acessoTelaUsuario.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            UtilsGUI.dialogoErro(acessoTelaUsuario.getFrame(), "Erro ao carregar dados do Autor no Banco de Dado!");
        }
    }
    
    public static boolean verificaTeclaCapsLock(JComponent jComponent) {
        
        if (Utils.verificaEstadoTecla(KeyEvent.VK_CAPS_LOCK)) {
            
            BalloonTip tooltipBalloon = new BalloonTip(jComponent,
                                                       "Atenção pois a Tecla Caps Lock está ativada!");
            
            TimingUtils.showTimedBalloon(tooltipBalloon, 3000);
            
            return true;
        }else{
            return false;
        }
    }
    
    public static void registraExcecao(Exception excecao){
        
        if (arquivoLog == null){
            arquivoLog = ArquivoLog.createArquivoLog("./logs/","Erros do Sistema ","HeuChess", false);
        }
        
        arquivoLog.registraExcecao(excecao);

        ///////////////////////////////////////
        // Registra Exceção no Banco de Dados//
        ///////////////////////////////////////
        
        try {
            Historico.registra(Historico.Tipo.ERRO_SISTEMA, UtilsString.descricaoCompletaExcecao(excecao));
            ConexaoDBHeuChess.commit();
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public static void registraMensagem(String mensagem){
        
        if (arquivoLog == null){
            arquivoLog = ArquivoLog.createArquivoLog("./logs/","Erros do Sistema ","HeuChess", false);
        }
        
        arquivoLog.registraMensagemTempo(mensagem);
    }
    
    public static void desfazTransacao(Exception excecao){
        
        try{
            ConexaoDBHeuChess.rollback();
            
        }catch(Exception e){
            e.printStackTrace(System.err);
        }
        
        try{
            registraExcecao(excecao);
            
        }catch(Exception e){
            e.printStackTrace(System.err);
        }
    }
    
    public static void avisoFechaPrograma(Exception e, String mensagem, int status){
        
        registraMensagem(mensagem);
        
        if (telaSplash != null){
            telaSplash.close(); 
        }
        
        if (e != null){
            registraExcecao(e);
        }
        
        UtilsGUI.dialogoErro(null, mensagem + "\n\nA aplicação será fechada!");
        
        fechaPrograma(status);
    }                
        
    public static void fechaPrograma(int status){
        
        try{
            if (HeuChess.usuario != null){
                
                UsuarioDAO.finalizandoAcesso(HeuChess.usuario);
                ConexaoDBHeuChess.commit();
            }
            
            ConexaoDBHeuChess.closeAll();
            
        }catch(Exception e){
            e.printStackTrace(System.err);
        }
        
        if (arquivoLog != null){
            arquivoLog.fechaArquivo();
        }
        
        System.exit(status);
    }   
    
    public static void main(String args[]) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                if (!InstanceManager.registerInstance()) {                    
                    // Já existe uma instancia aberta. A nova será encerrada. // 
                    System.exit(-1);
                }
                
                InstanceManager.setListener(new InstanceListener() {
                    @Override
                    public void newInstanceCreated() {

                        // Nova instancia foi iniciada! // 

                        JFrame frame = null;
                        
                        if (telaPrincipal != null) {                            
                            frame = telaPrincipal;
                        }else
                            if (telaLogin != null){
                                frame = telaLogin;
                            }
                        
                        if (frame != null) {
                            frame.setExtendedState(JFrame.NORMAL);
                            frame.toFront();
                        }

                        UtilsGUI.dialogoErro(frame, "A aplicação já está em execução!\n" +
                                                    "Não é possível abrir mais de uma simultaneamente.");
                    }
                });
                
                instancia = new HeuChess();
            }
        });
    }
}