package br.ufpr.inf.heuchess;

import br.ufpr.inf.heuchess.persistencia.AnotacaoDAO;
import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.Anotacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.telas.editorheuristica.AcessoTelaAnotacao;
import br.ufpr.inf.heuchess.telas.editorheuristica.ModelListaComponentes;
import br.ufpr.inf.heuchess.telas.editorheuristica.TelaAnotacao;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 12, 2012
 */
public class Anotacoes {

    private static ImageIcon iconeExplicacao;
    private static ImageIcon iconeQuestao;
    private static ImageIcon iconeElogio;
    private static ImageIcon iconeCritica;
    private static ImageIcon iconeNormal;
    
    public static void carregaIcones() throws Exception {
        iconeExplicacao = new ImageIcon(Anotacoes.class.getResource("/icones/icone_explicacao.png"));
        iconeQuestao    = new ImageIcon(Anotacoes.class.getResource("/icones/icone_questao.png"));
        iconeElogio     = new ImageIcon(Anotacoes.class.getResource("/icones/icone_elogio.png"));
        iconeCritica    = new ImageIcon(Anotacoes.class.getResource("/icones/icone_critica.png"));
        iconeNormal     = new ImageIcon(Anotacoes.class.getResource("/icones/icone_informacao.png"));
    }
    
    public static ImageIcon retornaIconeAnotacao(Tipo tipoAnotacao){
        
        if (tipoAnotacao == Anotacao.EXPLICAO){
            return iconeExplicacao;
        }else
            if (tipoAnotacao == Anotacao.QUESTAO){
                return iconeQuestao;  
            }else
                if (tipoAnotacao == Anotacao.ELOGIO){
                    return iconeElogio; 
                }else
                    if (tipoAnotacao == Anotacao.CRITICA){
                        return iconeCritica; 
                    }else
                        if (tipoAnotacao == Anotacao.NORMAL){
                            return iconeNormal; 
                        }else{
                            throw new IllegalArgumentException("Tipo desconhecido de Anotação");
                        }
    }
    
    public static void abrirAnotacao(final AcessoTelaAnotacao acessoTelaAnotacao) {
        
        final int posicao = acessoTelaAnotacao.getJListAnotacoes().getSelectedIndex();

        if (posicao != -1) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaAnotacao tela = new TelaAnotacao(acessoTelaAnotacao, posicao);
                }
            });

        }
    }
    
    public static void novaAnotacao(final AcessoTelaAnotacao acessoTelaAnotacao){
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaAnotacao tela = new TelaAnotacao(acessoTelaAnotacao);
                acessoTelaAnotacao.getJListAnotacoes().setSelectedIndex(-1);
            }
        });
    }
    
    public static void verificaSelecaoAnotacao(AcessoTelaAnotacao acessoTelaAnotacao) {
        
        if (acessoTelaAnotacao.getJListAnotacoes().getSelectedIndex() != -1) {
            acessoTelaAnotacao.getJButtonAbrirAnotacao().setEnabled(true);
            acessoTelaAnotacao.getJButtonExcluirAnotacao().setEnabled(true);            
        } else {
            acessoTelaAnotacao.getJButtonAbrirAnotacao().setEnabled(false);
            acessoTelaAnotacao.getJButtonExcluirAnotacao().setEnabled(false);            
        }
    }
    
    public static void seleciona(AcessoTelaAnotacao acessoTelaAnotacao, int posicao) {        
        acessoTelaAnotacao.getJListAnotacoes().setSelectedIndex(posicao);
        acessoTelaAnotacao.getJButtonAbrirAnotacao().setEnabled(true);
        acessoTelaAnotacao.getJButtonExcluirAnotacao().setEnabled(true);        
    }
    
    public static void verificaDuploCliqueAnotacao(final AcessoTelaAnotacao acessoTelaAnotacao, MouseEvent evt) {
        
        if (acessoTelaAnotacao.getJListAnotacoes().isEnabled()) {

            if (evt.getClickCount() == 2) {

                final int posicao = acessoTelaAnotacao.getJListAnotacoes().locationToIndex(evt.getPoint());

                if (posicao != -1) {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaAnotacao tela = new TelaAnotacao(acessoTelaAnotacao, posicao);
                        }
                    });
                }
            }
        }
    }
    
    public static void confirmaApagarAnotacaoSelecionada(AcessoTelaAnotacao acessoTelaAnotacao) {

        int posicao = acessoTelaAnotacao.getJListAnotacoes().getSelectedIndex();

        if (posicao != -1) {

            Anotacao anotacao = acessoTelaAnotacao.getComponente().getAnotacoes().get(posicao);
                    
            int resposta = UtilsGUI.dialogoConfirmacao(acessoTelaAnotacao.getFrame(),
                                                       "Deseja realmente Apagar a Anotação " + anotacao.getNomeTipoComponente() + "\n\"" +
                                                       anotacao + "\" ?",
                                                       "Confirmação de Exclusão");
            if (resposta == JOptionPane.YES_OPTION) {

                try {
                    acessoTelaAnotacao.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    apagarComComponenteAberto(acessoTelaAnotacao, anotacao);
                    
                    if (HeuChess.somAtivado) {
                        HeuChess.somApagar.play();
                    }
                     
                    acessoTelaAnotacao.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (Exception e) {
                    HeuChess.desfazTransacao(e);

                    acessoTelaAnotacao.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    UtilsGUI.dialogoErro(acessoTelaAnotacao.getFrame(), "Erro ao tentar apagar a Anotacao no Banco de Dados\n" +
                                                                        "Operação Cancelada!");
                }
            }
        }
    }
    
    public static void apagarComComponenteAberto(AcessoTelaAnotacao acessoTelaAnotacao, Anotacao anotacao) throws Exception {
        
        AnotacaoDAO.apaga(anotacao);

        ConexaoDBHeuChess.commit();

        anotacao.getComponente().getAnotacoes().remove(anotacao);
        
        ModelListaComponentes model = (ModelListaComponentes) acessoTelaAnotacao.getJListAnotacoes().getModel();
        model.removeElement(anotacao);
        
        atualizaQuantidadeAnotacoes(acessoTelaAnotacao);
        acessoTelaAnotacao.atualizaVersaoDataUltimaModificacao();
    }
    
    public static void atualizaQuantidadeAnotacoes(AcessoTelaAnotacao acessoTelaAnotacao){
        acessoTelaAnotacao.getJLabelTotalAnotacoes().setText("- Total de " + acessoTelaAnotacao.getComponente().getAnotacoes().size());
    }
    
    public static boolean anotacoesDiferentes(Componente componente, Componente componenteOriginal) {

        if (componente.getAnotacoes().size() != componenteOriginal.getAnotacoes().size()) {            
            return true;
        } else {
            
            for (int posicao = 0; posicao < componente.getAnotacoes().size(); posicao++) {

                Anotacao anotacao         = componente.getAnotacoes().get(posicao);
                Anotacao anotacaoOriginal = componenteOriginal.getAnotacoes().get(posicao);

                if (anotacao.getId() != anotacaoOriginal.getId() || anotacao.getVersao() != anotacaoOriginal.getVersao()) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
