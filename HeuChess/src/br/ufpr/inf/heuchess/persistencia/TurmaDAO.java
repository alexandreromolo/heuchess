package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import br.ufpr.inf.heuchess.representacao.organizacao.InscricaoTurma;
import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Turma;
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
public class TurmaDAO {
    
    public static ArrayList<Situacao> situacoesTurma;
    
    public static void carregaSituacoes() throws Exception {
        
        situacoesTurma = SituacaoDAO.lista(Situacao.Classe.SITUACAO_TURMA);
        
        for (Situacao situacao : situacoesTurma){
            
            if (situacao.getNome().equalsIgnoreCase("BLOQUEADA")){
                Turma.BLOQUEADA = situacao;
            }else
                if (situacao.getNome().equalsIgnoreCase("LIBERADA")){
                    Turma.LIBERADA = situacao;
                }else{
                     throw new IllegalArgumentException("Situação de Turma não suportada [" + situacao + "]");
                }
        }            
    }
    
    public static Situacao situacao(long id){
        return SituacaoDAO.situacao(situacoesTurma,id);
    }
    
    public static void adiciona(Turma turma) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT nextval('SEQ_HEU_USUARIO')");
        if (dados.next()) {

            turma.setId(dados.getLong(1));
            turma.setNome(UtilsString.preparaStringParaBD(turma.getNome(), true, Formato.TUDO_MAIUSCULO));

            String sql = "INSERT INTO HEU_TURMA (COD_TURMA, TXT_NOME, TXT_DESCRICAO, COD_INSTITUICAO, BIT_PERMISSOES, COD_SITUACAO) VALUES (" +
                         turma.getId()            + ", '"  +
                         turma.getNome()          + "', '" +
                         turma.getDescricao()     + "', "  +
                         turma.getIdInstituicao() + ", "   +
                         turma.getPermissoes()    + ", "   +
                         turma.getSituacao().getId() + ")";

            int total = ConexaoDBHeuChess.executaAlteracao(sql);
            if (total == 1) {
                sql = "SELECT DAT_CRIACAO FROM HEU_TURMA WHERE COD_TURMA = " + turma.getId();
                
                dados = ConexaoDBHeuChess.executaConsulta(sql);
                while (dados.next()) {
                    turma.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
                }
                dados.close();

                for (InscricaoTurma inscricaoTurma : turma.inscricoesCoordenadores()) {

                    inscricaoTurma.setIdTurma(turma.getId());

                    InscricaoTurmaDAO.adiciona(inscricaoTurma);
                }

                for (InscricaoTurma inscricaoTurma : turma.inscricoesAprendizes()) {

                    inscricaoTurma.setIdTurma(turma.getId());

                    InscricaoTurmaDAO.adiciona(inscricaoTurma);
                }

            } else {
                throw new RuntimeException("Não conseguiu inserir a nova Turma [" + turma + "] no Banco de Dados!");
            }
        } else {
            throw new RuntimeException("Erro ao recuperar próximo valod da sequência SEQ_HEU_USUARIO no Banco de Dados!");
        }
    }  
    
    public static void atualiza(Turma turma, Turma turmaOriginal) throws Exception {

        turma.setNome(UtilsString.preparaStringParaBD(turma.getNome(), true, Formato.TUDO_MAIUSCULO));

        String sql = "UPDATE HEU_TURMA SET " +
                     " TXT_NOME = '"         + turma.getNome()             +
                     "', TXT_DESCRICAO = '"  + turma.getDescricao()        +
                     "', COD_INSTITUICAO = " + turma.getIdInstituicao()    +
                     ", BIT_PERMISSOES  = "  + turma.getPermissoes()       +
                     ", COD_SITUACAO = "     + turma.getSituacao().getId() +
                     " WHERE COD_TURMA = "   + turma.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Erro na atualização da Turma [" + turma + "] no Banco de Dados!");
        }

        InscricaoTurmaDAO.atualizaTodas(turma, turmaOriginal);
    }
    
    public static void apaga(long idTurma) throws Exception {

        InscricaoTurmaDAO.apagaTodasTurma(idTurma);

        String sql = "DELETE FROM HEU_TURMA WHERE COD_TURMA = " + idTurma;

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Erro ao apagar Turma id [" + idTurma + "] do Banco de Dados!");
        }
    }
    
    public static void apagaTodas(long idInstituicao) throws Exception {

        String    sql   = "SELECT COD_TURMA FROM HEU_TURMA WHERE COD_INSTITUICAO = " + idInstituicao;
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        while (dados.next()) {
            apaga(dados.getLong("COD_TURMA"));            
        }
    }
    
    public static Turma busca(long idTurma) throws Exception {

        String sql = "SELECT COD_INSTITUICAO, TXT_NOME, TXT_DESCRICAO, BIT_PERMISSOES, DAT_CRIACAO, COD_SITUACAO FROM HEU_TURMA "
                   + "WHERE COD_TURMA = " + idTurma;
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            
            String nome          = dados.getString("TXT_NOME");
            String descricao     = dados.getString("TXT_DESCRICAO");
            long   idSituacao    = dados.getLong("COD_SITUACAO");
            long   idInstituicao = dados.getLong("COD_INSTITUICAO");
                                
            Turma turma = new Turma(nome,descricao,idInstituicao,situacao(idSituacao));
            
            turma.setId(idTurma);
            turma.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            turma.setPermissoes(dados.getInt("BIT_PERMISSOES"));
            
            return turma;
        }else{
            throw new RuntimeException("Não localizou a Turma de id [" + idTurma + "] no Banco de Dados!");
        }
    }
    
    public static long existeNome(Instituicao instituicao, String nome) throws Exception {

        nome = UtilsString.preparaStringParaBD(nome, true, Formato.TUDO_MAIUSCULO);

        String sql = "SELECT COD_TURMA FROM HEU_TURMA WHERE COD_INSTITUICAO = " + instituicao.getId() +
                     " AND TXT_NOME = '" + nome + "'";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        if (dados.next()) {
            return dados.getLong("COD_TURMA");
        } else {
            return -1;
        }
    }
    
    private static ElementoLista criaElemento(ResultSet dados) throws Exception {
        
        long idTurma   = dados.getLong("COD_TURMA");
        String nome    = dados.getString("TXT_NOME");
        int permissoes = dados.getInt("BIT_PERMISSOES");

        ElementoLista elemento = new ElementoLista(idTurma, nome, String.valueOf(permissoes), Turma.class, null);

        return elemento;
    }
    
    public static ArrayList<Integer> listaPermissoes(Usuario usuarioAcessa, long idAutor) throws Exception {

        ArrayList<Integer> permissoes = new ArrayList();
        
        String sql = "SELECT BIT_PERMISSOES FROM HEU_TURMA WHERE COD_TURMA IN (" +
                     "SELECT COD_TURMA FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = "  + idAutor + " AND COD_TURMA IN" +
                     "(SELECT COD_TURMA FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + usuarioAcessa.getId() + "))";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            permissoes.add(new Integer(dados.getInt("BIT_PERMISSOES")));
        }
        
        return permissoes;
    }
    
    public static ArrayList<ElementoLista> lista(long idInstituicao) throws Exception {

        ArrayList<ElementoLista> turmas = new ArrayList();
        
        String sql = "SELECT COD_TURMA, TXT_NOME, TXT_DESCRICAO, BIT_PERMISSOES FROM HEU_TURMA " +
                     "WHERE COD_INSTITUICAO = " + idInstituicao + " ORDER BY TXT_NOME";
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            turmas.add(criaElemento(dados));
        }
        
        return turmas;
    }
    
    public static ArrayList<ElementoLista> lista(Usuario usuario) throws Exception {

        ArrayList<ElementoLista> turmas = new ArrayList();
        
        String sql = "SELECT A.COD_TURMA, A.COD_TIPO, B.TXT_NOME, B.BIT_PERMISSOES, B.COD_SITUACAO FROM HEU_TURMA_USUARIO A, HEU_TURMA B " +
                     "WHERE COD_USUARIO = " + usuario.getId() + " AND A.COD_TURMA = B.COD_TURMA " +
                     "AND (A.COD_TIPO = " + Usuario.COORDENADOR.getId() + " OR B.COD_SITUACAO = " + 
                     Turma.LIBERADA.getId() + ") ORDER BY TXT_NOME";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            turmas.add(criaElemento(dados));
        }
        
        return turmas;
    }
}