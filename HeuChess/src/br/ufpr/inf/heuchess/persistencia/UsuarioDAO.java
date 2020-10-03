package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.Historico;
import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.UtilsDataTempo;
import br.ufpr.inf.utils.UtilsSistema;
import br.ufpr.inf.utils.UtilsString;
import br.ufpr.inf.utils.UtilsString.Formato;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 3 de Julho de 2006, 12:04
 */
public class UsuarioDAO {
    
    private static long tempoInicioSessao;
            
    public enum BuscaSexo {
        MASCULINO,
        FEMININO,
        AMBOS
    }
    
    public static ArrayList<Tipo>     tiposUsuarios;
    public static ArrayList<Situacao> situacoesUsuarios;
     
    public static void carregaSituacoesTipos() throws Exception {
        
        tiposUsuarios = TipoDAO.carregaTodos(Tipo.Classe.USUARIO);
        
        for (Tipo tipo : tiposUsuarios){
            
            if (tipo.getNome().equalsIgnoreCase("APRENDIZ")){
                Usuario.APRENDIZ = tipo;
            }else
                if (tipo.getNome().equalsIgnoreCase("COORDENADOR")){
                    Usuario.COORDENADOR = tipo;
                }else
                    if (tipo.getNome().equalsIgnoreCase("ADMINISTRADOR")){
                        Usuario.ADMINISTRADOR = tipo;
                    }else{
                       // Ignora tratamento especial para este tipo
                    }
        }
        
        situacoesUsuarios = SituacaoDAO.lista(Situacao.Classe.SITUACAO_USUARIO);
        
        for (Situacao situacao : situacoesUsuarios){
            
            if (situacao.getNome().equalsIgnoreCase("BLOQUEADO")){
                Usuario.BLOQUEADO = situacao;
            }else
                if (situacao.getNome().equalsIgnoreCase("LIBERADO")){
                    Usuario.LIBERADO = situacao;
                }else
                    if (situacao.getNome().equalsIgnoreCase("TROCANDO SENHA")){
                        Usuario.TROCANDO_SENHA = situacao;
                    }else{
                        throw new IllegalArgumentException("Situação de usuário não suportada [" + situacao + "]");
                    }
        }            
    }
    
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposUsuarios,id);
    }
    
    public static Situacao situacao(long id){
        return SituacaoDAO.situacao(situacoesUsuarios,id);
    }
    
    public static void adiciona(Usuario usuario) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT nextval('SEQ_HEU_USUARIO')");
        if (dados.next()) {

            usuario.setId(dados.getLong(1));
            usuario.setNome(UtilsString.preparaStringParaBD(usuario.getNome(), true, Formato.TUDO_MAIUSCULO));
            usuario.setEmail(UtilsString.preparaStringParaBD(usuario.getEmail(), true, Formato.TUDO_MINUSCULO));
            usuario.setLogin(UtilsString.preparaStringParaBD(usuario.getLogin(), true, Formato.TUDO_MINUSCULO));

            String sql = "INSERT INTO HEU_USUARIO (COD_USUARIO, TXT_NOME, COD_TIPO, TXT_LOGIN, TXT_SENHA, " +
                         "TXT_FOTO, LOG_SEXO_MASCULINO, DAT_NASCIMENTO, TXT_EMAIL, COD_SITUACAO, BIT_PERMISSOES) VALUES (" +
                         usuario.getId()           + ", '"  +
                         usuario.getNome()         + "', "  +
                         usuario.getTipo().getId() + ", '"  +
                         usuario.getLogin()        + "', '" +
                         usuario.getSenha()        + "', '" +
                         usuario.getFoto()         + "', "  +
                         usuario.isSexoMasculino() + ", TO_DATE('" +
                         UtilsDataTempo.formataData(usuario.getDataNascimento()) + "','DD/MM/YYYY'), '" +
                         usuario.getEmail()            + "', " +
                         usuario.getSituacao().getId() + ", "  +
                         usuario.getPermissoes()       + ")";

            int total = ConexaoDBHeuChess.executaAlteracao(sql);
            if (total == 1) {
                sql = "SELECT DAT_CRIACAO FROM HEU_USUARIO WHERE COD_USUARIO = " + usuario.getId();
                
                dados = ConexaoDBHeuChess.executaConsulta(sql);
                if (dados.next()) {
                    usuario.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
                }else{
                    throw new RuntimeException("Erro ao recuperar Data de Criação do novo Usuário [" + usuario + "] no Banco de Dados!");
                }
                dados.close();
                
            }else{
                throw new RuntimeException("Erro ao adicionar o novo Usuário [" + usuario + "] no Banco de Dados!");
            }
        }else{
            throw new RuntimeException("Erro ao recuperar próximo valor da sequência SEQ_HEU_USUARIO no Banco de Dados!");
        }
    }  
    
    public static void atualiza(Usuario usuario) throws Exception {

        usuario.setNome(UtilsString.preparaStringParaBD(usuario.getNome(), true, Formato.TUDO_MAIUSCULO));
        usuario.setEmail(UtilsString.preparaStringParaBD(usuario.getEmail(), true, Formato.TUDO_MINUSCULO));
        usuario.setLogin(UtilsString.preparaStringParaBD(usuario.getLogin(), true, Formato.TUDO_MINUSCULO));

        String sql = "UPDATE HEU_USUARIO SET " +
                     " TXT_NOME = '"    + usuario.getNome()         +
                     "', COD_TIPO = "   + usuario.getTipo().getId() +
                     ", TXT_LOGIN = '"  + usuario.getLogin()        +
                     "', TXT_SENHA = '" + usuario.getSenha()        +
                     "', TXT_FOTO = '"  + usuario.getFoto()         +
                     "', LOG_SEXO_MASCULINO = " + usuario.isSexoMasculino()  +
                     ", DAT_NASCIMENTO = TO_DATE('"   + UtilsDataTempo.formataData(usuario.getDataNascimento()) +
                     "','DD/MM/YYYY'), TXT_EMAIL = '" + usuario.getEmail()   +
                     "', BIT_PERMISSOES = "  + usuario.getPermissoes()       +
                     ",  COD_SITUACAO = "    + usuario.getSituacao().getId() +
                     " WHERE COD_USUARIO = " + usuario.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Erro ao atualizar o Usuário [" + usuario + "] no Banco de Dados!");
        }
    }
    
    public static void adicionouCoordenacao(long idUsuario) throws Exception {

        long idTipo = buscaCodTipoUsuario(idUsuario);

        if (idTipo == Usuario.COORDENADOR.getId() || idTipo == Usuario.ADMINISTRADOR.getId()) {
            return;
        }

        String sql = "UPDATE HEU_USUARIO SET COD_TIPO = " + Usuario.COORDENADOR.getId() +
                     " WHERE COD_USUARIO = " + idUsuario;

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            
            if (idUsuario == HeuChess.usuario.getId()){
                HeuChess.telaPrincipal.atualizaTipoUsuarioConectado(Usuario.COORDENADOR);
            }
            
        }else{
            throw new RuntimeException("Não conseguiu definir tipo Coordenador para o Usuário id [" + idUsuario + "] no Banco de Dados!");
        }
    }

    public static void retirouCoordenacao(long idUsuario) throws Exception {

        long idTipo = buscaCodTipoUsuario(idUsuario);

        if (idTipo == Usuario.ADMINISTRADOR.getId()) {
            return;
        }

        int totalCoordenacoes = 0;

        totalCoordenacoes += quantidadeInstituicoesCoordena(idUsuario);

        if (totalCoordenacoes > 1) {
            return;
        }

        totalCoordenacoes += quantidadeTurmasCoordena(idUsuario);

        if (totalCoordenacoes > 1) {
            return;
        }

        String sql = "UPDATE HEU_USUARIO SET COD_TIPO = " + Usuario.APRENDIZ.getId() +
                     " WHERE COD_USUARIO = " + idUsuario;

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            
            if (idUsuario == HeuChess.usuario.getId()){
                HeuChess.telaPrincipal.atualizaTipoUsuarioConectado(Usuario.APRENDIZ);
            }
             
        }else{
            throw new RuntimeException("Não conseguiu retirar tipo Coordenador do Usuário id [" + idUsuario + "] no Banco de Dados!");
        }
    }  
    
    public static void trocaSenha(Usuario usuario) throws Exception {

        String sql = "UPDATE HEU_USUARIO SET " +
                     " TXT_SENHA = '"          + usuario.getSenha()       +
                     "', COD_SITUACAO = "      + Usuario.LIBERADO.getId() +
                     " WHERE COD_USUARIO = "   + usuario.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Não conseguiu trocar a senha do Usuário [" + usuario + "] no Banco de Dados!");
        }
    }
    
    public static void inciandoAcesso(Usuario usuario) throws Exception {

        String sql = "UPDATE HEU_USUARIO SET "   +
                     "QTD_ACESSOS_REALIZADOS = " + (usuario.getQuantidadeAcessosRealizados() + 1) +
                     ", LOG_ONLINE = TRUE WHERE COD_USUARIO = " + usuario.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1){                   
            
            Historico.registra(Historico.Tipo.ENTROU_NO_SISTEMA, UtilsSistema.todasInformacoes());                        
            
            tempoInicioSessao = System.currentTimeMillis();
            
        }else {
            throw new RuntimeException("Erro ao registrar inico sessão do Usuário [" + usuario + "] no Banco de Dados!");
        }
    }
    
    public static void finalizandoAcesso(Usuario usuario) throws Exception {

        String sql = "UPDATE HEU_USUARIO SET LOG_ONLINE = FALSE WHERE COD_USUARIO = " + usuario.getId();

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total == 1) {
            
            long tempoSessao = System.currentTimeMillis() - tempoInicioSessao;
            
            Historico.registra(Historico.Tipo.SAIU_DO_SISTEMA, "Tempo " + UtilsDataTempo.formataTempoMilissegundos(tempoSessao));
            
        } else {
            throw new RuntimeException("Erro ao registrar fim sessão do Usuário [" + usuario + "] no Banco de Dados!");
        }
    }
    
    public static void apaga(long idUsuario) throws Exception {

        InscricaoTurmaDAO.apagaTodasUsuario(idUsuario);

        ConjuntoHeuristicoDAO.apagaTodosUsuario(idUsuario);

        SituacaoJogoDAO.apagaTodasUsuario(idUsuario);

        String sql = "DELETE FROM HEU_USUARIO WHERE COD_USUARIO = " + idUsuario;

        int total = ConexaoDBHeuChess.executaAlteracao(sql);
        if (total != 1) {
            throw new RuntimeException("Erro ao apagar o Usuário Id [" + idUsuario + "] do Banco de Dados!");
        }
    }
    
    public static long existeEmail(String email) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT COD_USUARIO FROM HEU_USUARIO WHERE TXT_EMAIL = '" + email + "'");
        
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        } else {
            return -1;
        }
    }
    
    public static long existeLogin(String login) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT COD_USUARIO FROM HEU_USUARIO WHERE TXT_LOGIN = '" + login + "'");
        
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        } else {
            return -1;
        }
    }
    
    public static long existeHomonimo(Usuario usuario) throws Exception {

        String sql = "SELECT COD_USUARIO FROM HEU_USUARIO WHERE TXT_NOME = '" + usuario.getNome() + "'";

        if (usuario.getId() != 0) {
            //////////////////////////////// 
            // Usuário já existe no Banco //
            ////////////////////////////////
            
            sql += " AND COD_USUARIO <> " + usuario.getId();
        }

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        } else {
            return -1;
        }
    }
    
    public static long coordenoTurma(Usuario coordenador, long idAprendiz) throws Exception {

        String sql = "SELECT COD_TURMA FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + idAprendiz +
                     " AND COD_TURMA IN (SELECT COD_TURMA FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + coordenador.getId() +
                     " AND COD_TIPO = " + Usuario.COORDENADOR.getId() + ")";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);

        if (dados.next()) {
            return dados.getLong("COD_TURMA");
        } else {
            return -1;
        }
    }
    
    public static long coordenoInstituicaoTurma(Usuario coordenador, long idAprendiz) throws Exception {

        String sql = "SELECT COD_TURMA FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + idAprendiz +
                     " AND COD_TURMA IN (SELECT COD_TURMA FROM HEU_TURMA WHERE COD_INSTITUICAO IN " +
                     "(SELECT COD_INSTITUICAO FROM HEU_INSTITUICAO WHERE COD_USUARIO = " + coordenador.getId() + "))";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);

        if (dados.next()) {
            return dados.getLong("COD_TURMA");
        } else {
            return -1;
        }
    }
    
    public static int quantidadeTurmasCoordena(long idUsuario) throws Exception {

        String sql = "SELECT COUNT(COD_TURMA) FROM HEU_TURMA_USUARIO WHERE COD_USUARIO = " + idUsuario +
                     " AND COD_TIPO = " + Usuario.COORDENADOR.getId();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        if (dados.next()) {
            return dados.getInt(1);
        } else {
            throw new RuntimeException("Erro ao recuperar quantidade de Turmas coordenadas pelo Usuário id [" + idUsuario + "]");
        }
    }
    
    public static int quantidadeInstituicoesCoordena(long idUsuario) throws Exception {

        String sql = "SELECT COUNT(COD_INSTITUICAO) FROM HEU_INSTITUICAO WHERE COD_USUARIO = " + idUsuario;

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        if (dados.next()) {
            return dados.getInt(1);
        } else {
            throw new RuntimeException("Erro ao recuperar quantidade de Instituições coordenadas pelo Usuário id [" + idUsuario + "]");
        }
    }
    
    public static long coordenadorInstituicaoTurma(long idTurma) throws Exception {

        String sql = "SELECT A.COD_USUARIO FROM HEU_INSTITUICAO A, HEU_TURMA B WHERE B.COD_TURMA = " + idTurma +
                     " AND A.COD_INSTITUICAO = B.COD_INSTITUICAO";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        } else {
            return -1;
        }
    }
    
    public static long autorComponente(long idComponente) throws Exception {

        String sql = "SELECT COD_USUARIO FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + idComponente;

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        if (dados.next()) {
            return dados.getLong("COD_USUARIO");
        } else {
            throw new RuntimeException("Erro ao recuperar Autor do Componente id [" + idComponente + "] no Banco de Dados!");
        }
    }
    
    private static Usuario criaUsuario(ResultSet dados) throws Exception {

        long id     = dados.getLong("COD_USUARIO");
        String nome = dados.getString("TXT_NOME");

        Usuario usuario = new Usuario(id, nome);

        usuario.setTipo(tipo(dados.getLong("COD_TIPO")));
        usuario.setLogin(dados.getString("TXT_LOGIN"));
        usuario.setSenha(dados.getString("TXT_SENHA"));
        usuario.setEmail(dados.getString("TXT_EMAIL"));
        usuario.setSexoMasculino(dados.getBoolean("LOG_SEXO_MASCULINO"));
        usuario.setDataNascimento(new java.util.Date(dados.getDate("DAT_NASCIMENTO").getTime()));
        usuario.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
        usuario.setSituacao(situacao(dados.getLong("COD_SITUACAO")));
        usuario.setOnline(dados.getBoolean("LOG_ONLINE"));

        usuario.setFoto(dados.getString("TXT_FOTO"));
        usuario.setQuantidadeAnotacoesParaOutros(dados.getLong("QTD_ANOTACOES_PARA_OUTROS"));
        usuario.setQuantidadeAcessosRealizados(dados.getLong("QTD_ACESSOS_REALIZADOS"));
        usuario.setQuantidadeCopiasRealizadas(dados.getLong("QTD_COPIAS_REALIZADAS"));
        usuario.setPermissoes(dados.getInt("BIT_PERMISSOES"));

        java.sql.Date dateSQL = dados.getDate("DAT_CANCELAMENTO");
        if (dateSQL != null) {
            usuario.setDataCancelamento(new java.util.Date(dateSQL.getTime()));
        }

        return usuario;
    }
    
    public static Usuario busca(String identificacao) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_USUARIO WHERE TXT_LOGIN = '" + 
                                                            identificacao.toLowerCase() + "'");
        if (dados.next()) {
            return criaUsuario(dados);
        }else{
            return null;
        }
    }
    
    public static Usuario busca(long idUsuario) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_USUARIO WHERE COD_USUARIO = " +
                                                            idUsuario);
        if (dados.next()) {
            return criaUsuario(dados);
        }else{
            throw new RuntimeException("Erro ao recuperar Usuário Id [" + idUsuario + "]");
        }
    }
    
    public static String buscaNomeUsuario(long idUsuario) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT TXT_NOME FROM HEU_USUARIO WHERE COD_USUARIO = " +
                                                            idUsuario);
        if (dados.next()) {
            return dados.getString("TXT_NOME");
        } else {
            throw new RuntimeException("Erro ao recuperar o nome do Usuário id [" + idUsuario + "]");
        }
    }
    
    public static long buscaCodTipoUsuario(long idUsuario) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT COD_TIPO FROM HEU_USUARIO WHERE COD_USUARIO = " + idUsuario);
        
        if (dados.next()) {
            return dados.getLong("COD_TIPO");
        }else{
            throw new RuntimeException("Erro ao recuperar código Tipo do Usuário id [" + idUsuario + "]");
        }
    }
    
    public static ArrayList<Usuario> lista(String textoProcura, BuscaSexo sexo, Tipo tipo, Situacao situacao) throws Exception {

        ArrayList<Usuario> usuarios = new ArrayList();
        
        String sql = "SELECT * FROM HEU_USUARIO";
        
        boolean colocouCriterio = false;
        
        if (textoProcura != null && textoProcura.length() > 0){
        
            colocouCriterio = true;
            
            sql += " WHERE TXT_NOME LIKE \'%" + textoProcura + "%\'"; 
        }
        
        if (tipo != null){
                    
            if (tipo == Usuario.APRENDIZ){
                sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_TIPO = " + Usuario.APRENDIZ.getId();
            }else
                if (tipo == Usuario.COORDENADOR){
                    sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_TIPO = " + Usuario.COORDENADOR.getId();
                }else
                    if (tipo == Usuario.ADMINISTRADOR){
                        sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_TIPO = " + Usuario.ADMINISTRADOR.getId();    
                    }else{
                        throw new IllegalArgumentException("Tipo não tratado no método de pesquisa [" + tipo + "]");
                    }
            
            colocouCriterio = true;
        }
        
        if (situacao != null){
                    
            if (situacao == Usuario.LIBERADO){
                sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_SITUACAO = " + Usuario.LIBERADO.getId();
            }else
                if (situacao == Usuario.BLOQUEADO){
                    sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_SITUACAO = " + Usuario.BLOQUEADO.getId();
                }else
                    if (situacao == Usuario.TROCANDO_SENHA){
                        sql += (colocouCriterio ? " AND " : " WHERE ") + "COD_SITUACAO = " + Usuario.TROCANDO_SENHA.getId();    
                    }else{
                        throw new IllegalArgumentException("Situação não tratada no método de pesquisa [" + situacao + "]");
                    }
            
            colocouCriterio = true;
        }
        
        switch(sexo){
            case MASCULINO:
                sql += (colocouCriterio ? " AND " : " WHERE ") + "LOG_SEXO_MASCULINO = TRUE ORDER BY TXT_NOME";
                break;
                
            case FEMININO:                
                sql += (colocouCriterio ? " AND " : " WHERE ") + "LOG_SEXO_MASCULINO = FALSE ORDER BY TXT_NOME";
                break;
                
            case AMBOS:
                sql += " ORDER BY TXT_NOME";
                break;
        }
                
        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            
            Usuario usuario = criaUsuario(dados);
            
            usuarios.add(usuario);
        }
        
        return usuarios;
    }
}