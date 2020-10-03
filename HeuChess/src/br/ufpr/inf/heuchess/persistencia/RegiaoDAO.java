package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Setembro de 2006, 03:46
 */
public class RegiaoDAO {
    
    public static ArrayList<Tipo> tiposRegiao;
     
    public static void carregaTipos() throws Exception {
        tiposRegiao = TipoDAO.carregaTodos(Tipo.Classe.REGIAO);
    }
    
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposRegiao, id);
    }
    
    public static void adiciona(Etapa etapa, Regiao regiao) throws Exception {
        ComponenteDAO.adiciona((Componente) regiao);
        ComponenteDAO.relaciona((Componente)etapa, (Componente)regiao);        
    }        
    
    public static void atualiza(Regiao regiao) throws Exception {
        ComponenteDAO.atualiza((Componente) regiao);
    }    
    
    public static void apaga(Regiao regiao, Etapa etapa) throws Exception {
        ComponenteDAO.apaga((Componente) regiao, (Componente) etapa);        
    }    
    
    public static void carregaTodas(Etapa etapa) throws Exception {
        
        String sql = "SELECT C.* FROM HEU_COMPONENTE C, HEU_COMPONENTE_COMPONENTE D, HEU_TIPO E WHERE "
                   + " D.COD_COMPONENTE_PRINCIPAL = " + etapa.getId()
                   + " AND D.COD_COMPONENTE_INCLUIDO = C.COD_COMPONENTE AND C.COD_TIPO = E.COD_TIPO AND "
                   + " E.COD_CLASSE = " + Tipo.Classe.REGIAO.codigo() + " ORDER BY C.TXT_NOME";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        etapa.getRegioes().clear();
        
        while (dados.next()) {
            long codAutor    = dados.getLong("COD_USUARIO");
            long idTipo      = dados.getLong("COD_TIPO");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            
            Regiao regiao = new Regiao(nome, codAutor, tipo(idTipo), definicao);
            
            regiao.setId(dados.getLong("COD_COMPONENTE"));
            regiao.setVersao(dados.getLong("NUM_VERSAO"));
            regiao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            regiao.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            regiao.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            regiao.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            regiao.setPermissoes(dados.getInt("BIT_PERMISSOES"));

            AnotacaoDAO.carregaTodas(regiao);

            etapa.getRegioes().add(regiao);
        }
    }
}