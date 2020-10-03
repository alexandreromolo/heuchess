package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 5, 2012
 */
public class HistoricoDAO {
    
    public static void adiciona(Usuario usuario, Historico.Tipo tipo, String descricao) throws Exception {

        String sql = "INSERT INTO HEU_HISTORICO (COD_USUARIO, COD_TIPO, TXT_DESCRICAO) VALUES (" +
                     usuario.getId() + ", "  +
                     tipo.getId()    + ", '" +
                     descricao       + "')";

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Não foi possível registrar o Histórico [" + descricao + "] no Banco de Dados!");
        }
    }
    
    public static void adiciona(Usuario usuario, Historico.Tipo tipo, String descricao, 
                                long idComponente, long idTipoComponente, long idAutorComponente) throws Exception {

        String sql;
        
        if (descricao != null){
            sql = "INSERT INTO HEU_HISTORICO (COD_USUARIO, COD_TIPO, TXT_DESCRICAO, COD_COMPONENTE, COD_TIPO_COMPONENTE, COD_AUTOR_COMPONENTE) VALUES (" +
                   usuario.getId()   + ", "  +
                   tipo.getId()      + ", '" +
                   descricao         + "', " +
                   idComponente      + ", "  +
                   idTipoComponente  + ", " +
                   idAutorComponente + ")";
        }else{
            sql = "INSERT INTO HEU_HISTORICO (COD_USUARIO, COD_TIPO, COD_COMPONENTE, COD_TIPO_COMPONENTE, COD_AUTOR_COMPONENTE) VALUES (" +
                   usuario.getId()   + ", " +
                   tipo.getId()      + ", " +                   
                   idComponente      + ", " +
                   idTipoComponente  + ", " +
                   idAutorComponente + ")";
        }

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Não foi possível registrar o Histórico [" + descricao + "] no Banco de Dados!");
        }
    }
}