package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Nov 5, 2012
 */
public class SituacaoDAO {

    public static ArrayList<Situacao> lista(Situacao.Classe classe) throws Exception {

        ArrayList<Situacao> situacoesEncontrados = new ArrayList();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_TIPO WHERE COD_CLASSE = " + classe.codigo() + " ORDER BY COD_TIPO");
        while (dados.next()) {
            int id = dados.getInt("COD_TIPO");
            String nome = dados.getString("TXT_NOME");
            String descricao = dados.getString("TXT_DESCRICAO");
            situacoesEncontrados.add(new Situacao(id, classe, nome, descricao));
        }

        if (situacoesEncontrados.isEmpty()){ 
            throw new RuntimeException("Não foi localizada nenhuma Situação da classe [" + classe + "]");
        }
          
        return situacoesEncontrados;
    }

    public static Situacao situacao(ArrayList<Situacao> situacoes, long id) {

        for (Situacao situacao : situacoes) {

            if (situacao.getId() == id) {
                return situacao;
            }
        }

        return null;
    }
}
