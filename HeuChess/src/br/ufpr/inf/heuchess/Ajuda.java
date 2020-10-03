package br.ufpr.inf.heuchess;

import br.ufpr.inf.heuchess.persistencia.ConexaoDBHeuChess;
import br.ufpr.inf.utils.PropertiesManager;
import br.ufpr.inf.utils.Utils;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.Cursor;
import java.awt.Window;

/**
 * Classe que gerencia a apresentação da ajuda para a aplicação
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 */
public final class Ajuda {

    private PropertiesManager arquivoProperties;

    public Ajuda(String idioma) {
        arquivoProperties = new PropertiesManager("config/ajuda_" + idioma);
    }

    public boolean abre(Window janela, String chave) {

        janela.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String valorChave = arquivoProperties.getString(chave);

        if (valorChave == null || valorChave.trim().length() == 0) {

            janela.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            return false;

        } else {
            
            try{
                Historico.registra(Historico.Tipo.BUSCOU_AJUDA_DO_SISTEMA, valorChave);
                ConexaoDBHeuChess.commit();
                
            }catch (Exception e){
                HeuChess.desfazTransacao(e);
                UtilsGUI.dialogoErro(janela, "Erro ao registrar ação de Abrir Ajuda no Banco de Dados!");
            }
                        
            boolean teste = Utils.abrePaginaWeb(valorChave);            
            
            janela.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            
            return teste;
        }
    }
}
