package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.utils.gui.ElementoLista;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 3, 2012
 */
public class SituacaoJogoDAO {
    
    public static ArrayList<Tipo> tiposSituacaoJogo;
     
    public static void carregaTipos() throws Exception {
        tiposSituacaoJogo = TipoDAO.carregaTodos(Tipo.Classe.SITUACAO_JOGO);
    }
    
    public static Tipo tipo(long id){
        return TipoDAO.tipo(tiposSituacaoJogo, id);
    }
    
    public static int indiceTipo(Tipo tipo){
        return TipoDAO.indiceTipo(tiposSituacaoJogo, tipo);
    }
    
    public static Tipo tipoIndice(int indice){
        return TipoDAO.tipoIndice(tiposSituacaoJogo, indice);
    }  
     
    public static void adiciona(SituacaoJogo situacaoJogo) throws Exception {
        ComponenteDAO.adiciona((Componente) situacaoJogo);
    }    
    
    public static void atualiza(SituacaoJogo situacaoJogo) throws Exception {
        ComponenteDAO.atualiza((Componente) situacaoJogo);
    }    
    
    public static void apaga(long id) throws Exception {
        ComponenteDAO.apagaSemPai(id);        
    }    
    
    public static int apagaTodasUsuario(long idUsuario) throws Exception {
        
        int total = 0;
        
        String sql = "SELECT C.COD_COMPONENTE, C.COD_TIPO FROM HEU_COMPONENTE C, HEU_TIPO D WHERE" +
                     " C.COD_USUARIO = " + idUsuario + 
                     " AND C.COD_TIPO = D.COD_TIPO AND D.COD_CLASSE = " + Tipo.Classe.SITUACAO_JOGO.codigo();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            ComponenteDAO.apagaSemPai(dados.getLong("COD_COMPONENTE"));
            total++;
        }
        
        return total;
    }
    
    public static long existeNome(Usuario usuario, String nome) throws Exception {
        return ComponenteDAO.existeNome(usuario, nome, Tipo.Classe.SITUACAO_JOGO);        
    }
    
    public static SituacaoJogo busca(long id) throws Exception {        
   
        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + id);
        if (dados.next()) {
            
            long codAutor = dados.getLong("COD_USUARIO");
            long idTipo   = dados.getLong("COD_TIPO");
            String nome   = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            
            SituacaoJogo situacao = new SituacaoJogo(nome,codAutor,definicao,tipo(idTipo));
            
            situacao.setId(dados.getLong("COD_COMPONENTE"));
            situacao.setVersao(dados.getLong("NUM_VERSAO"));
            situacao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            situacao.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            situacao.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            situacao.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            situacao.setPermissoes(dados.getInt("BIT_PERMISSOES"));

            AnotacaoDAO.carregaTodas(situacao);
            return situacao;
        }else{
            throw new IllegalArgumentException("Não foi localizada a Situacao de Jogo com o código [" + id + "]");
        }   
    } 
        
    public static ArrayList<ElementoLista> lista(long idUsuario) throws Exception {

        ArrayList<ElementoLista> situacoesJogo = new ArrayList();

        String sql = "SELECT C.COD_COMPONENTE, C.TXT_NOME, C.TXT_DEFINICAO, C.COD_TIPO FROM HEU_COMPONENTE C, HEU_TIPO D WHERE " +
                     " C.COD_USUARIO = "  + idUsuario +
                     " AND C.COD_TIPO = D.COD_TIPO AND D.COD_CLASSE = " + Tipo.Classe.SITUACAO_JOGO.codigo() +
                     " ORDER BY C.TXT_NOME";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            
            long id          = dados.getLong("COD_COMPONENTE");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            long idTipo      = dados.getLong("COD_TIPO");

            ElementoLista elemento = new ElementoLista(id, nome, definicao, SituacaoJogo.class, tipo(idTipo));

            situacoesJogo.add(elemento);
        }
        
        return situacoesJogo;
    }
}
