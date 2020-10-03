package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Julho de 2006, 16:12
 */
public class TipoDAO {
    
    public static ArrayList<Tipo> carregaTodos(Tipo.Classe classe) throws Exception {

        ArrayList<Tipo> tiposEncontrados = new ArrayList();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_TIPO WHERE COD_CLASSE = " + 
                          classe.codigo() + " ORDER BY COD_TIPO");
        
        while (dados.next()) {
            int id = dados.getInt("COD_TIPO");
            String nome = dados.getString("TXT_NOME");
            String descricao = dados.getString("TXT_DESCRICAO");
            tiposEncontrados.add(new Tipo(id, classe, nome, descricao));
        }
        
        if (tiposEncontrados.isEmpty()){ 
            throw new RuntimeException("Não foi localizada nenhum Tipo da classe [" + classe + "]");
        }

        return tiposEncontrados;
    }
    
    public static Tipo tipo(ArrayList<Tipo> tipos, long id){
        
        for (Tipo tipo : tipos){
            
            if (tipo.getId() == id){
                return tipo;
            }
        }
        
        return null;
    }
    
    public static int indiceTipo(ArrayList<Tipo> tipos, Tipo tipo){
        return tipos.indexOf(tipo) + 1;        
    }
    
    public static Tipo tipoIndice(ArrayList<Tipo> tipos, int indice){
        return tipos.get(indice-1);        
    }  
}