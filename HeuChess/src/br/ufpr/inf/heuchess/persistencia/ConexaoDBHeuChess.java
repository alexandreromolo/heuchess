package br.ufpr.inf.heuchess.persistencia;

import java.sql.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Junho de 2006, 16:11
 */
public class ConexaoDBHeuChess {
    
    private static Connection con;
    private static Statement  state;
    
    //private static String url  = "jdbc:postgresql://heuchess.cp.utfpr.edu.br:8080/heuchess";    
    private static String url  = "jdbc:postgresql://localhost:5432/heuchess2013v1";
    private static String user = "heuchess2013v1_cliente_java";                
    private static String pass = "cvG23aN2N6fg";        
    
    private static Connection getConexao() throws Exception {

        if (con == null || con.isClosed()) {

            Class.forName("org.postgresql.Driver");

            con = null;
            con = DriverManager.getConnection(url, user, pass);
            con.setAutoCommit(false);
        }

        return con;
    }
    
    private static Statement getStatement() throws Exception {

        con = getConexao();

        state = con.createStatement();

        return state;
    }
    
    private static void closeStatement() throws Exception {

        if (state != null) {
            state.close();
            state = null;
        }
    }   
    
    public static void commit() throws Exception {
        con = getConexao();
        con.commit();        
    }
    
    public static void rollback() throws Exception {
        con = getConexao();
        con.rollback();
    }
    
    public static void closeAll() throws Exception {

        closeStatement();

        if (con != null) {
            con.close();
            con = null;
        }
    }
    
    public static ResultSet executaConsulta(String sql) throws Exception {

        state = getStatement();

        return state.executeQuery(sql);
    }
    
    public static int executaAlteracao(String sql) throws Exception {
        
        state = getStatement();
        
        return state.executeUpdate(sql);
    }
    
    public static String getDataHora() throws Exception {

        ResultSet dados = executaConsulta("SELECT CURRENT_TIMESTAMP");
        
        if (dados.next()) {
            return dados.getString(1);
        } else {
            throw new RuntimeException("Não foi possível recuperar a Data e Hora atual do Banco de Dados!");
        }
    }
}