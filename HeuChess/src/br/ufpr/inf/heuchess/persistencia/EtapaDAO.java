package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 11 de Julho de 2006, 11:56
 */
public class EtapaDAO {
        
    public static ArrayList<Tipo> tiposEtapas;
     
    public static void carregaTipos() throws Exception {
        tiposEtapas = TipoDAO.carregaTodos(Tipo.Classe.ETAPA);
    }
    
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposEtapas, id);
    }    
    
    public static void adiciona(ConjuntoHeuristico conjuntoHeuristico, Etapa etapa) throws Exception {
        ComponenteDAO.adiciona((Componente) etapa);
        ComponenteDAO.relaciona((Componente)conjuntoHeuristico,(Componente) etapa);
    }        
    
    public static void atualiza(Etapa etapa) throws Exception {
        ComponenteDAO.atualiza((Componente) etapa);
    }    
    
    public static void apaga(Etapa etapa, ConjuntoHeuristico conjuntoHeuristico) throws Exception {
        ComponenteDAO.apaga((Componente) etapa, (Componente) conjuntoHeuristico);          
    }    

    public static void carregaTodas(ConjuntoHeuristico conjuntoHeuristico) throws Exception {

        String sql = "SELECT C.* FROM HEU_COMPONENTE C, HEU_COMPONENTE_COMPONENTE D, HEU_TIPO E WHERE "
                   + " D.COD_COMPONENTE_PRINCIPAL = " + conjuntoHeuristico.getId()
                   + " AND D.COD_COMPONENTE_INCLUIDO = C.COD_COMPONENTE AND C.COD_TIPO = E.COD_TIPO AND "
                   + " E.COD_CLASSE = " + Tipo.Classe.ETAPA.codigo() + " ORDER BY C.DAT_CRIACAO";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        conjuntoHeuristico.getEtapas().clear();
        
        while (dados.next()) {
            long codAutor    = dados.getLong("COD_USUARIO");
            long idTipo      = dados.getLong("COD_TIPO");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            
            Etapa etapa = new Etapa(conjuntoHeuristico,nome,codAutor,tipo(idTipo),definicao);
            
            etapa.setId(dados.getLong("COD_COMPONENTE"));
            etapa.setVersao(dados.getLong("NUM_VERSAO"));
            etapa.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            etapa.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            etapa.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            etapa.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            etapa.setPermissoes(dados.getInt("BIT_PERMISSOES"));
            
            RegiaoDAO.carregaTodas(etapa);
            
            AnotacaoDAO.carregaTodas(etapa);

            conjuntoHeuristico.getEtapas().add(etapa);
        }
    }
}