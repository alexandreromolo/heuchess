package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import java.sql.ResultSet;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Setembro de 2006, 15:20
 */
public final class ComponenteDAO {
    
    public static void adiciona(Componente componente) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT nextval('SEQ_HEU_COMPONENTE')");
        if (dados.next()) {

            componente.setId(dados.getLong(1));
            componente.setNome(UtilsString.preparaStringParaBD(componente.getNome(), true, Formato.TUDO_MAIUSCULO));

            String sql = "INSERT INTO HEU_COMPONENTE (COD_COMPONENTE, COD_USUARIO, TXT_NOME, COD_TIPO, TXT_DEFINICAO) VALUES (" +
                         componente.getId()           + ", "  +
                         componente.getIdAutor()      + ", '" +
                         componente.getNome()         + "', " +
                         componente.getTipo().getId() + ", '" +
                         componente.getDescricaoDB()  + "')";

            int total = ConexaoDBHeuChess.executaAlteracao(sql);
            if (total == 1) {
                
                sql = "SELECT DAT_CRIACAO, DAT_ULTIMA_MODIFICACAO FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + componente.getId();
                
                dados = ConexaoDBHeuChess.executaConsulta(sql);
                if (dados.next()) {
                    componente.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
                    componente.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
                }else{
                    throw new RuntimeException("Erro na hora de consultar datas do componente no Banco de Dados [" + componente + "]");
                }
                dados.close();

                Historico.registraComponenteCriado(componente);             
            }else{
                throw new RuntimeException("Erro na hora de adicionar componente no Banco de Dados [" + componente + "]");
            }
        }else{
            throw new RuntimeException("Erro na hora de recuperar novo valor da sequência de Banco SEQ_HEU_COMPONENTE!");
        }
    }
     
    public static void relaciona(Componente componentePrincipal, Componente componenteIncluido) throws Exception {

        String sql = "INSERT INTO HEU_COMPONENTE_COMPONENTE (COD_COMPONENTE_PRINCIPAL, COD_COMPONENTE_INCLUIDO) VALUES (" +
                      componentePrincipal.getId() + ", " +
                      componenteIncluido.getId()  + ")";

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            ComponenteDAO.atualizaVersaoDataModificacao(componentePrincipal);            
            Historico.registraComponenteRelacionado(componentePrincipal, componenteIncluido);            
        }else{
            throw new RuntimeException("Erro na hora de relacionar componente [" + componenteIncluido + 
                                       "] com o componente [" + componentePrincipal + "] no Banco de Dados!");
        }
    }
    
    public static void retiraRelacao(Componente componentePrincipal, Componente componenteIncluido) throws Exception {

        String sql = "DELETE FROM HEU_COMPONENTE_COMPONENTE WHERE COD_COMPONENTE_PRINCIPAL = " + componentePrincipal.getId() +
                     " AND COD_COMPONENTE_INCLUIDO = " + componenteIncluido.getId();
        
        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            atualizaVersaoDataModificacao(componentePrincipal);
        }else{
            throw new RuntimeException("Erro na hora de retirar relacionamento entre componente [" + componenteIncluido + 
                                       "] e componente [" + componentePrincipal + "] no Banco de Dados!");
        }
    }
    
    public static void atualizaVersaoDataModificacao(Componente componente) throws Exception {

        String sql = "UPDATE HEU_COMPONENTE SET NUM_VERSAO = " + (componente.getVersao() + 1) + 
                     ", DAT_ULTIMA_MODIFICACAO = 'NOW' WHERE COD_COMPONENTE = " + componente.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            componente.setVersao(componente.getVersao() + 1);
            
            recuperaDataUltimaModificacao(componente);        
        }else{
            throw new RuntimeException("Erro na hora de atualizar versão do componente [" + componente + "] no Banco de Dados!");
        }
    }
    
    private static void recuperaDataUltimaModificacao(Componente componente) throws Exception {

        String sql = "SELECT DAT_ULTIMA_MODIFICACAO FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + componente.getId();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            componente.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
        } else {
            throw new RuntimeException("Erro na hora de consultar Data Modificação do componente [" + componente + "] no Banco de Dados!");
        }
        dados.close();
    }
    
    public static void atualiza(Componente componente) throws Exception {

        componente.setNome(UtilsString.preparaStringParaBD(componente.getNome(), true, Formato.TUDO_MAIUSCULO));

        String sql = "UPDATE HEU_COMPONENTE SET " +
                     " COD_USUARIO = "      + componente.getIdAutor()      +
                     ", COD_TIPO = "        + componente.getTipo().getId() +
                     ", TXT_NOME = '"       + componente.getNome()         +
                     "', TXT_DEFINICAO = '" + componente.getDescricaoDB()  +
                     "', NUM_VERSAO = "     + (componente.getVersao() + 1) +
                     ", DAT_ULTIMA_MODIFICACAO = 'NOW'" +
                     ", QTD_ACESSOS = "     + componente.getQuantidadeAcessos()  +
                     ", QTD_COPIAS = "      + componente.getQuantidadeCopias()   +
                     ", QTD_ANOTACOES_RECEBIDAS = " + componente.getQuantidadeAnotacoesRecebidas() +
                     ", BIT_PERMISSOES = "          + componente.getPermissoes() +
                     " WHERE COD_COMPONENTE = "     + componente.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {            
            componente.setVersao(componente.getVersao() + 1);
            
            recuperaDataUltimaModificacao(componente);

            Historico.registraComponenteAlterado(componente);            
        }else{
            throw new RuntimeException("Erro na hora de atualizar o componente [" + componente + "] no Banco de Dados!");
        }
    }
    
    public static void apaga(Componente componenteFilho, Componente componentePai) throws Exception {        
            
        apagaEmCascata(componenteFilho.getId(), componentePai.getId());
            
        atualizaVersaoDataModificacao(componentePai);
        
        Historico.registraComponenteExcluido(componenteFilho.getId(), componenteFilho.getTipo().getId(), componenteFilho.getIdAutor());
    }
    
    public static void apagaSemPai(long idComponente) throws Exception {        
        
        String sql = "SELECT COD_USUARIO, COD_TIPO FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + idComponente;
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {            
            long idAutor = dados.getLong("COD_USUARIO");
            long idTipo  = dados.getLong("COD_TIPO");            
            
            apagaEmCascata(idComponente, 0);
            
            Historico.registraComponenteExcluido(idComponente, idTipo, idAutor);
        }else{
            throw new RuntimeException("Não foi localizado o Componente a ser apagado [" + idComponente + "]");
        }   
    }
    
    private static void apagaEmCascata(long idFilho, long idPai) throws Exception {

        if (idPai > 0) {

            /////////////////////////////////////////////////////////////////
            // Procura se o componente a ser apagado possui mais de um pai //
            /////////////////////////////////////////////////////////////////

            String sql = "SELECT * FROM HEU_COMPONENTE_COMPONENTE WHERE COD_COMPONENTE_INCLUIDO = " + idFilho +
                         " AND COD_COMPONENTE_PRINCIPAL <> " + idPai;

            ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
            if (dados.next()) {

                /////////////////////////////////////////////////////////////////////////////////
                // Possui mais de um Pai, apaga apenas a relação com o pai definido na chamada //
                /////////////////////////////////////////////////////////////////////////////////

                sql = "DELETE FROM HEU_COMPONENTE_COMPONENTE WHERE COD_COMPONENTE_PRINCIPAL = " + idPai +
                      " AND COD_COMPONENTE_INCLUIDO = " + idFilho;
                
                int total = ConexaoDBHeuChess.executaAlteracao(sql);
                if (total != 1) {
                    throw new RuntimeException("Erro na hora de apagar a relação do componente id [" + idFilho + 
                                               "] com o componente id [" + idPai + "]");
                }
            }
        }

        ////////////////////////////////////////////////////////////////////
        // Não tem pai para ser analizado ou apenas um pai, Apaga o filho //
        ////////////////////////////////////////////////////////////////////

        // Procura os filhos do componente que será apagado e chama recursivamente este método para apagá-los //

        String sql = "SELECT COD_COMPONENTE_INCLUIDO FROM HEU_COMPONENTE_COMPONENTE WHERE COD_COMPONENTE_PRINCIPAL = " + idFilho;
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            long idFilhoDoFilho = dados.getLong("COD_COMPONENTE_INCLUIDO");
            
            apagaEmCascata(idFilhoDoFilho, idFilho);            
        }

        // Apaga todos os relacionamento do componente //

        sql = "DELETE FROM HEU_COMPONENTE_COMPONENTE WHERE COD_COMPONENTE_PRINCIPAL = " + idFilho +
              " OR COD_COMPONENTE_INCLUIDO = " + idFilho;
        
        ConexaoDBHeuChess.executaAlteracao(sql);

        // Apaga o componente filho //

        sql = "DELETE FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + idFilho;
        
        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Erro ao apagar o componente id [" + idFilho + "] do Banco de Dados!");
        }
    }
    
    public static long existeNome(Usuario usuario, String nome, Tipo.Classe classe) throws Exception {

        nome = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);

        String sql = "SELECT C.COD_COMPONENTE, C.TXT_NOME FROM HEU_COMPONENTE C, HEU_TIPO D WHERE" +
                     " C.COD_USUARIO = " + usuario.getId() +
                     " AND C.COD_TIPO = D.COD_TIPO AND D.COD_CLASSE = " + classe.codigo() +
                     " AND C.TXT_NOME = '" + nome + "' ";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            return dados.getLong("COD_COMPONENTE");
        } else {
            return -1;
        }
    }
}