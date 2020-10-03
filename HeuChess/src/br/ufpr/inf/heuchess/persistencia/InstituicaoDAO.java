package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import br.ufpr.inf.utils.gui.ElementoLista;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 30, 2012
 */
public class InstituicaoDAO {
    
    public static void adiciona(Instituicao instituicao) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT nextval('SEQ_HEU_USUARIO')");
        if (dados.next()) {

            instituicao.setId(dados.getLong(1));
            instituicao.setNome(UtilsString.preparaStringParaBD(instituicao.getNome(), true, Formato.TUDO_MAIUSCULO));

            String sql = "INSERT INTO HEU_INSTITUICAO (COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO, COD_USUARIO) VALUES (" +
                          instituicao.getId()        + ", '"  +
                          instituicao.getNome()      + "', '" +
                          instituicao.getDescricao() + "', "  +
                          instituicao.getIdCoordenador() + ")";

            int total = ConexaoDBHeuChess.executaAlteracao(sql);
            if (total == 1) {
                sql = "SELECT DAT_CRIACAO FROM HEU_INSTITUICAO WHERE COD_INSTITUICAO = " + instituicao.getId();
                
                dados = ConexaoDBHeuChess.executaConsulta(sql);
                while (dados.next()) {
                    instituicao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
                }
                dados.close();

                UsuarioDAO.adicionouCoordenacao(instituicao.getIdCoordenador());
            }else{
                throw new RuntimeException("Não conseguiu adicionar a nova Instituição [" + instituicao + "] no Banco de Dados!");
            }
        }else{
            throw new RuntimeException("Não conseguiu recuperar o próximo valor da sequência SEQ_HEU_USUARIO no Banco de Dados!");
        }
    }
    
    public static void atualiza(Instituicao instituicao, long idCoordenadorAntigo) throws Exception {

        instituicao.setNome(UtilsString.preparaStringParaBD(instituicao.getNome(), true, Formato.TUDO_MAIUSCULO));

        String sql = "UPDATE HEU_INSTITUICAO SET " +
                     " TXT_NOME = '"        + instituicao.getNome()          +
                     "', TXT_DESCRICAO = '" + instituicao.getDescricao()     +
                     "', COD_USUARIO = "    + instituicao.getIdCoordenador() +
                     " WHERE COD_INSTITUICAO = " + instituicao.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            if (instituicao.getIdCoordenador() != idCoordenadorAntigo) {
                UsuarioDAO.adicionouCoordenacao(instituicao.getIdCoordenador());
                UsuarioDAO.retirouCoordenacao(idCoordenadorAntigo);
            }
        }else{
            throw new RuntimeException("Não conseguiu atualizar a Instituicao [" + instituicao + "] no Banco de Dados!");
        }
    }
    
    public static void apaga(long idInstituicao) throws Exception {

        TurmaDAO.apagaTodas(idInstituicao);

        long idCoordenador = buscaIdCoordenador(idInstituicao);

        String sql = "DELETE FROM HEU_INSTITUICAO WHERE COD_INSTITUICAO = " + idInstituicao;

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            UsuarioDAO.retirouCoordenacao(idCoordenador);
        }else{
            throw new RuntimeException("Erro ao apagar Instituição id [" + idInstituicao + "] do Banco de Dados!");
        }
    }
    
    public static long buscaIdCoordenador(long idInstituicao) throws Exception {

        String sql = "SELECT COD_USUARIO FROM HEU_INSTITUICAO WHERE COD_INSTITUICAO = " + idInstituicao;
                  
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        }else{
            throw new RuntimeException("Não ocalizou o Usuário coordenador da Instituição id [" + idInstituicao + "] no Banco de Dados!");
        }
    }
    
    public static Instituicao busca(long idInstituicao) throws Exception {

        String sql = "SELECT TXT_NOME, TXT_DESCRICAO, COD_USUARIO, DAT_CRIACAO FROM HEU_INSTITUICAO WHERE COD_INSTITUICAO = " + idInstituicao;
                  
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            
            String nome          = dados.getString("TXT_NOME");
            String descricao     = dados.getString("TXT_DESCRICAO");
            long   idCoordenador = dados.getLong("COD_USUARIO");
            
            Instituicao instituicao = new Instituicao(nome, descricao, idCoordenador);
            
            instituicao.setId(idInstituicao);
            instituicao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            
            return instituicao;
        }else{
            throw new RuntimeException("Não localizou a Instituição id [" + idInstituicao + "] no Banco de Dados!");
        }
    }
    
    public static long existeNome(String nome) throws Exception {

        nome = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);

        String sql = "SELECT COD_INSTITUICAO FROM HEU_INSTITUICAO WHERE TXT_NOME = '" + nome + "'";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            return dados.getLong("COD_INSTITUICAO");
        } else {
            return -1;
        }
    }
    
    public static ArrayList<Instituicao> listaEditaveis(Usuario usuario) throws Exception {

        ArrayList<Instituicao> instituicoes = new ArrayList();

        String sql;
        
        if (usuario.getTipo() == Usuario.ADMINISTRADOR){
            
            // Traz todas as instituições //
            
            sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO, COD_USUARIO, DAT_CRIACAO FROM HEU_INSTITUICAO ORDER BY TXT_NOME";
        }else{
            
            // Traz apenas as que ele coordena //
            
            sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO, COD_USUARIO, DAT_CRIACAO FROM HEU_INSTITUICAO WHERE " +
                  "COD_USUARIO = " + usuario.getId() + " ORDER BY TXT_NOME";
        }
            
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            
            String nome          = dados.getString("TXT_NOME");
            String descricao     = dados.getString("TXT_DESCRICAO");
            long   idCoordenador = dados.getLong("COD_USUARIO");
            
            Instituicao instituicao = new Instituicao(nome, descricao, idCoordenador);
            
            instituicao.setId(dados.getLong("COD_INSTITUICAO"));
            instituicao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            
            instituicoes.add(instituicao);            
        }
        
        return instituicoes;
    }
    
    public static ArrayList<ElementoLista> lista(Usuario usuario) throws Exception {

        ArrayList<ElementoLista> instituicoes = new ArrayList();
        
        String sql;
        
        if (usuario.getTipo() == Usuario.ADMINISTRADOR){
            
            // Traz todas as instituições //
            
            sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO FROM HEU_INSTITUICAO ORDER BY TXT_NOME";
        }else            
            if (UsuarioDAO.quantidadeInstituicoesCoordena(usuario.getId()) > 0){
                
                // Traz apenas as que ele coordena //
                
                sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO FROM HEU_INSTITUICAO WHERE COD_USUARIO = " +
                      usuario.getId() + " ORDER BY TXT_NOME";
            }else{
                // Traz apenas as Instituições das Turmas que ele coordena //
                
                sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO FROM HEU_INSTITUICAO WHERE COD_INSTITUICAO IN " +
                      "(SELECT A.COD_INSTITUICAO FROM HEU_TURMA A, HEU_TURMA_USUARIO B WHERE A.COD_TURMA = B.COD_TURMA AND " +
                      "COD_USUARIO = " + usuario.getId() + " AND COD_TIPO = " + Usuario.COORDENADOR.getId() + ") ORDER BY TXT_NOME";
            }                
            
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            
            long   idInstituicao = dados.getLong("COD_INSTITUICAO");
            String nome          = dados.getString("TXT_NOME");
            String descricao     = dados.getString("TXT_DESCRICAO");
            
            ElementoLista elemento = new ElementoLista(idInstituicao, nome, descricao, Instituicao.class, null);
            
            instituicoes.add(elemento);            
        }
        
        return instituicoes;
    }
}