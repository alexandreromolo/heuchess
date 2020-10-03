package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 27 de Julho de 2006, 15:27
 */
public class HeuristicaDAO {
    
    public static ArrayList<Tipo> tiposHeuristicas;
     
    public static void carregaTipos() throws Exception {
        
        tiposHeuristicas = TipoDAO.carregaTodos(Tipo.Classe.HEURISTICA);
        
        for (Tipo tipo : tiposHeuristicas){
            
            if (tipo.getNome().equalsIgnoreCase("HEURÍSTICA DE VALOR DE PEÇA")){
                Heuristica.HEURISTICA_VALOR_PECA = tipo;
            }else
                if (tipo.getNome().equalsIgnoreCase("HEURÍSTICA DE VALOR DE TABULEIRO")){
                    Heuristica.HEURISTICA_VALOR_TABULEIRO = tipo;
                }else
                    if (tipo.getNome().equalsIgnoreCase("HEURÍSTICA DE TRANSIÇÃO DE ETAPA")){
                        Heuristica.HEURISTICA_TRANSICAO_ETAPA = tipo;
                    }else{
                        throw new IllegalArgumentException("Tipo desconhecido de Heurística");
                    }
        }
    }
    
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposHeuristicas, id);
    }

    public static void adiciona(Etapa etapa, Heuristica heuristica) throws Exception {
        ComponenteDAO.adiciona((Componente) heuristica);
        ComponenteDAO.relaciona((Componente) etapa, (Componente) heuristica);
    }        
    
    public static void atualiza(Heuristica heuristica) throws Exception {
        ComponenteDAO.atualiza((Componente) heuristica);
    }    
    
    public static void apaga(Heuristica heuristica, Etapa etapa) throws Exception {
        ComponenteDAO.apaga((Componente) heuristica, (Componente) etapa);        
    }    
    
    public static void carregaTodas(Etapa etapa) throws Exception {

        String sql = "SELECT C.* FROM HEU_COMPONENTE C, HEU_COMPONENTE_COMPONENTE D, HEU_TIPO E WHERE "
                   + " D.COD_COMPONENTE_PRINCIPAL = " + etapa.getId()
                   + " AND D.COD_COMPONENTE_INCLUIDO = C.COD_COMPONENTE AND C.COD_TIPO = E.COD_TIPO AND "
                   + " E.COD_CLASSE = " + Tipo.Classe.HEURISTICA.codigo() + " ORDER BY C.DAT_CRIACAO, C.COD_COMPONENTE"; 

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        etapa.getHeuristicasTransicaoEtapa().clear();
        etapa.getHeuristicasValorPeca().clear();
        etapa.getHeuristicasValorTabuleiro().clear();
        
        while (dados.next()) {
            long codAutor    = dados.getLong("COD_USUARIO");
            long idTipo      = dados.getLong("COD_TIPO");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            
            Heuristica heuristica;
            
            if (idTipo == Heuristica.HEURISTICA_TRANSICAO_ETAPA.getId()){
                heuristica = new HeuristicaTransicaoEtapa(etapa,nome,codAutor,definicao);
                etapa.getHeuristicasTransicaoEtapa().add((HeuristicaTransicaoEtapa) heuristica);
            }else            
                if (idTipo == Heuristica.HEURISTICA_VALOR_PECA.getId()){
                    heuristica = new HeuristicaValorPeca(etapa,nome,codAutor,definicao);
                    etapa.getHeuristicasValorPeca().add((HeuristicaValorPeca) heuristica);
                }else
                    if (idTipo == Heuristica.HEURISTICA_VALOR_TABULEIRO.getId()){
                        heuristica = new HeuristicaValorTabuleiro(etapa,nome,codAutor,definicao);
                        etapa.getHeuristicasValorTabuleiro().add((HeuristicaValorTabuleiro) heuristica);
                    }else{                
                        throw new IllegalArgumentException("Tipo de Heurística Inválido [" + idTipo + "]");
                    }
            
            heuristica.setId(dados.getLong("COD_COMPONENTE"));
            heuristica.setVersao(dados.getLong("NUM_VERSAO"));
            heuristica.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            heuristica.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            heuristica.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            heuristica.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            heuristica.setPermissoes(dados.getInt("BIT_PERMISSOES"));

            AnotacaoDAO.carregaTodas(heuristica);
        }
        
        Collections.sort(etapa.getHeuristicasTransicaoEtapa());
        Collections.sort(etapa.getHeuristicasValorPeca());
        Collections.sort(etapa.getHeuristicasValorTabuleiro());
    }
}
