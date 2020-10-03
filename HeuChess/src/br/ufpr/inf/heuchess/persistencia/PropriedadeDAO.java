package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.Propriedade;
import java.sql.ResultSet;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 30, 2012
 */
public class PropriedadeDAO {

    public static String busca(long idProprietario, Propriedade propriedade) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_PROPRIEDADE WHERE COD_PROPRIETARIO = " + idProprietario +
                                                            " AND COD_TIPO = " + propriedade.getId());
        
        if (dados.next()) {            
            String valor = dados.getString("TXT_VALOR");
            
            if (valor == null || valor.trim().length() == 0){
                throw new RuntimeException("Propriedade [" + propriedade.getId() + "] do proprietário com Id [" + idProprietario + "] vazia!");
            }
            
            return valor;
        }else{
            throw new RuntimeException("Não encontrada propriedade [" + propriedade.getId() + "] do proprietário com Id [" + idProprietario + "]");
        }
    }
}
