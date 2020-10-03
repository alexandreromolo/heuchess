package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.organizacao.InscricaoTurma;
import br.ufpr.inf.heuchess.representacao.organizacao.Turma;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.gui.ElementoLista;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public class InscricaoTurmaDAO {

    public static void adiciona(InscricaoTurma inscricaoTurma) throws Exception {

        String sql = "INSERT INTO HEU_TURMA_USUARIO (COD_TURMA, COD_USUARIO, COD_TIPO) VALUES (" +
                      inscricaoTurma.getIdTurma()         + ", " +
                      inscricaoTurma.getUsuario().getId() + ", " +
                      inscricaoTurma.getTipo().getId()    + ")";

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            
            sql = "SELECT DAT_CRIACAO FROM HEU_TURMA_USUARIO WHERE COD_TURMA = " + inscricaoTurma.getIdTurma() +
                  " AND COD_USUARIO = " + inscricaoTurma.getUsuario().getId();

            ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
            if (dados.next()) {
               inscricaoTurma.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            }else{
                throw new RuntimeException("Não conseguiu recuperar Data de Criação Inscrição Turma [" + inscricaoTurma + "] no Banco de Dados!");
            }
            dados.close();

            if (inscricaoTurma.getTipo() == Usuario.COORDENADOR) {
                UsuarioDAO.adicionouCoordenacao(inscricaoTurma.getUsuario().getId());
            }

        }else{
            throw new RuntimeException("Não conseguiu inserir Inscrição Turma [" + inscricaoTurma + "] no Banco de Dados!");
        }
    }
    
    public static void atualizaTodas(Turma turmaAtualizada, Turma turmaOriginal) throws Exception {
        
        ///////////////////////////////////        
        // Localiza quem ira ser apagado //
        ///////////////////////////////////
        
        ArrayList<InscricaoTurma> inscricoesLocalizadas = new ArrayList();
        
        for (InscricaoTurma inscricao : turmaOriginal.inscricoesCoordenadores()){
            
            boolean achou = false;
            
            for (InscricaoTurma insc : turmaAtualizada.inscricoesCoordenadores()){
            
                if (inscricao.igual(insc)){
                    achou = true;
                    break;
                }
            }
            
            if (!achou){
                inscricoesLocalizadas.add(inscricao);
            }
        }
        
        for (InscricaoTurma inscricao : turmaOriginal.inscricoesAprendizes()){
            
            boolean achou = false;
            
            for (InscricaoTurma insc : turmaAtualizada.inscricoesAprendizes()){
            
                if (inscricao.igual(insc)){
                    achou = true;
                    break;
                }
            }
            
            if (!achou){
                inscricoesLocalizadas.add(inscricao);
            }
        }
        
        // Apaga inscrições removidas //
        
        for (InscricaoTurma inscricao : inscricoesLocalizadas){
            apaga(inscricao);
        }
      
        ////////////////////////////////////        
        // Localiza quem ira ser incluido //
        ////////////////////////////////////
        
        inscricoesLocalizadas.clear();
        
        for (InscricaoTurma inscricao : turmaAtualizada.inscricoesCoordenadores()){
            
            boolean achou = false;
            
            for (InscricaoTurma insc : turmaOriginal.inscricoesCoordenadores()){
            
                if (inscricao.igual(insc)){
                    achou = true;
                    break;
                }
            }
            
            if (!achou){
                inscricoesLocalizadas.add(inscricao);
            }
        }
        
        for (InscricaoTurma inscricao : turmaAtualizada.inscricoesAprendizes()){
            
            boolean achou = false;
            
            for (InscricaoTurma insc : turmaOriginal.inscricoesAprendizes()){
            
                if (inscricao.igual(insc)){
                    achou = true;
                    break;
                }
            }
            
            if (!achou){
                inscricoesLocalizadas.add(inscricao);
            }
        }
        
        // Insere novas inscrições //
        
        for (InscricaoTurma inscricao : inscricoesLocalizadas){
            adiciona(inscricao);
        }        
    }
    
    public static void apaga(InscricaoTurma inscricaoTurma) throws Exception {

        String sql = "DELETE FROM HEU_TURMA_USUARIO WHERE COD_TURMA = " + inscricaoTurma.getIdTurma() +
                     " AND COD_USUARIO = " + inscricaoTurma.getUsuario().getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {

            if (inscricaoTurma.getTipo() == Usuario.COORDENADOR) {
                UsuarioDAO.retirouCoordenacao(inscricaoTurma.getUsuario().getId());
            }
            
        }else{
            throw new RuntimeException("Não conseguiu apagar Inscrição Turma [" + inscricaoTurma + "] no Banco de Dados!");
        }
    }
    
    public static int apagaTodasTurma(long idTurma) throws Exception {
    
        String sql = "SELECT COD_USUARIO FROM HEU_TURMA_USUARIO WHERE COD_TURMA = " + idTurma +
                     " AND COD_TIPO = " + Usuario.COORDENADOR.getId();
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        while (dados.next()) {
            UsuarioDAO.retirouCoordenacao(dados.getLong("COD_USUARIO"));
        }       
        
        sql = "DELETE FROM HEU_TURMA_USUARIO WHERE COD_TURMA = " + idTurma;
            
        return ConexaoDBHeuChess.executaAlteracao(sql);
    }
    
    public static int apagaTodasUsuario(long idUsuario) throws Exception {

        String sql = "DELETE FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + idUsuario;
            
        return ConexaoDBHeuChess.executaAlteracao(sql);
    }
    
    public static void carregaTodas(Turma turma) throws Exception {
    
        String sql = "SELECT COD_USUARIO, COD_TIPO, DAT_CRIACAO FROM HEU_TURMA_USUARIO WHERE COD_TURMA = " + turma.getId();
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        turma.inscricoesCoordenadores().clear();
        turma.inscricoesAprendizes().clear();
        
        while (dados.next()) {
            
            long idUsuario = dados.getLong("COD_USUARIO");
            long idTipo    = dados.getLong("COD_TIPO");
            
            InscricaoTurma inscricao = new InscricaoTurma(turma.getId(),UsuarioDAO.busca(idUsuario),UsuarioDAO.tipo(idTipo));
            
            inscricao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            
            turma.registraInscricao(inscricao);
        }        
    }
    
    public static ArrayList<ElementoLista> listaUsuarios(long idTurma) throws Exception {
    
        ArrayList<ElementoLista> usuarios = new ArrayList();
        
        String sql = "SELECT C.COD_USUARIO, C.COD_TIPO, D.TXT_NOME, D.LOG_ONLINE FROM HEU_TURMA_USUARIO C, HEU_USUARIO D " +
                     "WHERE COD_TURMA = " + idTurma + " AND D.COD_USUARIO = C.COD_USUARIO ORDER BY D.TXT_NOME";
        
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            
            long    idUsuario = dados.getLong("COD_USUARIO");
            long    idTipo    = dados.getLong("COD_TIPO");
            String  nome      = dados.getString("TXT_NOME");
            boolean online    = dados.getBoolean("LOG_ONLINE");
            
            ElementoLista elemento = new ElementoLista(idUsuario, 
                                                       UtilsString.formataCaixaAltaBaixa(nome), 
                                                       online ? "1" : "0",
                                                       Usuario.class, 
                                                       UsuarioDAO.tipo(idTipo));

            usuarios.add(elemento);
        }        
        
        return usuarios;
    }
}
