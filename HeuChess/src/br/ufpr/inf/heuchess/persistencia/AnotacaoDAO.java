package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Anotacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 14 de Julho de 2006, 14:28
 */
public class AnotacaoDAO {
    
    public static ArrayList<Tipo> tiposAnotacao;
    
    public static void carregaTipos() throws Exception {
        
        tiposAnotacao = TipoDAO.carregaTodos(Tipo.Classe.ANOTACAO);
        
        for (Tipo tipo : tiposAnotacao) {
            
            if (tipo.getNome().equalsIgnoreCase("EXPLICAÇÃO")) {
                Anotacao.EXPLICAO = tipo;
            } else 
                if (tipo.getNome().equalsIgnoreCase("QUESTÃO")) {
                    Anotacao.QUESTAO = tipo;
                } else 
                    if (tipo.getNome().equalsIgnoreCase("ELOGIO")) {
                        Anotacao.ELOGIO = tipo;
                    } else 
                        if (tipo.getNome().equalsIgnoreCase("CRÍTICA")) {
                            Anotacao.CRITICA = tipo;
                        } else 
                            if (tipo.getNome().equalsIgnoreCase("NORMAL")) {
                                Anotacao.NORMAL = tipo;
                            }else{
                                throw new IllegalArgumentException("Tipo inválido de Anotação");
                            }   
        }
    }
    
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposAnotacao, id);
    }
    
    public static void adiciona(Anotacao anotacao) throws Exception {        
        ComponenteDAO.adiciona((Componente) anotacao);
        ComponenteDAO.relaciona((Componente) anotacao.getComponente(), (Componente) anotacao);        
    }        
    
    public static void atualiza(Anotacao anotacao) throws Exception {
        ComponenteDAO.atualiza((Componente) anotacao);
    }    
    
    public static void apaga(Anotacao anotacao) throws Exception {        
        ComponenteDAO.apaga((Componente) anotacao, (Componente) anotacao.getComponente());                
    }    
    
    public static void carregaTodas(Componente componente) throws Exception {

        String sql = "SELECT C.* FROM HEU_COMPONENTE C, HEU_COMPONENTE_COMPONENTE D, HEU_TIPO E WHERE "
                   + " D.COD_COMPONENTE_PRINCIPAL = " + componente.getId()
                   + " AND D.COD_COMPONENTE_INCLUIDO = C.COD_COMPONENTE AND C.COD_TIPO = E.COD_TIPO AND "
                   + " E.COD_CLASSE = " + Tipo.Classe.ANOTACAO.codigo() + " ORDER BY C.DAT_ULTIMA_MODIFICACAO DESC";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        
        componente.getAnotacoes().clear();
        
        while (dados.next()) {
            
            long codAutor     = dados.getLong("COD_USUARIO");
            long idTipo       = dados.getLong("COD_TIPO");
            String titulo     = dados.getString("TXT_NOME");
            String informacao = dados.getString("TXT_DEFINICAO");
            
            Anotacao anotacao = new Anotacao(codAutor, componente, tipo(idTipo), titulo, informacao);
            anotacao.setId(dados.getLong("COD_COMPONENTE"));
            anotacao.setVersao(dados.getLong("NUM_VERSAO"));
            anotacao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            anotacao.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            anotacao.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            anotacao.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            anotacao.setPermissoes(dados.getInt("BIT_PERMISSOES"));
            
            componente.getAnotacoes().add(anotacao);
        }
    }
}