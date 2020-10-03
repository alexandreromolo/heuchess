package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.utils.gui.ElementoLista;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Julho de 2006, 14:44
 */
public class ConjuntoHeuristicoDAO {
    
    public static ArrayList<Tipo> tiposConjuntoHeuristico;

    public static void carregaTipos() throws Exception {
        
        tiposConjuntoHeuristico = TipoDAO.carregaTodos(Tipo.Classe.CONJUNTO_HEURISTICO);
        
        for (Tipo tipo : tiposConjuntoHeuristico){
            
            if (tipo.getNome().equalsIgnoreCase("INICIANTE")){
                ConjuntoHeuristico.NIVEL_1_INICIANTE = tipo;
            }else
                if (tipo.getNome().equalsIgnoreCase("BÁSICO")){
                    ConjuntoHeuristico.NIVEL_2_BASICO = tipo;
                }else
                    if (tipo.getNome().equalsIgnoreCase("INTERMEDIÁRIO")){
                        ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO = tipo;
                    }else
                        if (tipo.getNome().equalsIgnoreCase("PLENO")){
                            ConjuntoHeuristico.NIVEL_4_PLENO = tipo;
                        }else
                            if (tipo.getNome().equalsIgnoreCase("AVANÇADO")){
                                ConjuntoHeuristico.NIVEL_5_AVANCADO = tipo;
                            }else
                                if (tipo.getNome().equalsIgnoreCase("ESPECIALISTA")){
                                    ConjuntoHeuristico.NIVEL_6_ESPECIALISTA = tipo;
                                }else{
                                    throw new IllegalArgumentException("Tipo inválido de ConjuntoHeurístico [" + tipo.getNome() + "]");
                                }
        }
    }
    
    public static int ordemNivel(Tipo tipo){
        return tiposConjuntoHeuristico.indexOf(tipo) + 1;        
    }
    
    public static Tipo nivelOrdem(int ordem){
        return tiposConjuntoHeuristico.get(ordem-1);        
    }    
    
    public static Tipo tipo(long id){ 
        return TipoDAO.tipo(tiposConjuntoHeuristico, id);
    }
    
    public static void adiciona(ConjuntoHeuristico novoConjuntoHeuristico, Etapa etapaInicial) throws Exception {

        novoConjuntoHeuristico.setEtapaInicial(etapaInicial);

        ComponenteDAO.adiciona((Componente) novoConjuntoHeuristico);

        EtapaDAO.adiciona(novoConjuntoHeuristico, etapaInicial);
    }
    
    public static void atualiza(ConjuntoHeuristico conjuntoHeuristico) throws Exception {
        ComponenteDAO.atualiza((Componente) conjuntoHeuristico);
    }
     
    public static void apaga(long id) throws Exception {
        ComponenteDAO.apagaSemPai(id);
    }
    
    public static int apagaTodosUsuario(long idUsuario) throws Exception {
        
        int total = 0;
        
        String sql = "SELECT C.COD_COMPONENTE, C.COD_TIPO FROM HEU_COMPONENTE C, HEU_TIPO D WHERE "
                   + " C.COD_USUARIO = "    + idUsuario
                   + " AND C.COD_TIPO = D.COD_TIPO "
                   + " AND D.COD_CLASSE = " + Tipo.Classe.CONJUNTO_HEURISTICO.codigo();

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            ComponenteDAO.apagaSemPai(dados.getLong("COD_COMPONENTE"));
            total++;
        }
        
        return total;
    }
    
    public static long existeNome(Usuario usuario, String nome) throws Exception {
        return ComponenteDAO.existeNome(usuario, nome, Tipo.Classe.CONJUNTO_HEURISTICO);
    }
    
    public static ConjuntoHeuristico busca(long id) throws Exception {

        ResultSet dados = ConexaoDBHeuChess.executaConsulta("SELECT * FROM HEU_COMPONENTE WHERE COD_COMPONENTE = " + id);
        if (dados.next()) {
            
            long codAutor = dados.getLong("COD_USUARIO");
            long idTipo   = dados.getLong("COD_TIPO");
            String nome   = dados.getString("TXT_NOME");
            
            ConjuntoHeuristico conjunto = new ConjuntoHeuristico(nome, codAutor, tipo(idTipo));
            
            conjunto.setId(dados.getLong("COD_COMPONENTE"));
            conjunto.setVersao(dados.getLong("NUM_VERSAO"));
            conjunto.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            conjunto.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            conjunto.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            conjunto.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            conjunto.setPermissoes(dados.getInt("BIT_PERMISSOES"));
            
            EtapaDAO.carregaTodas(conjunto);
            
            conjunto.setDefinicao(dados.getString("TXT_DEFINICAO"));
            
            for (Etapa etapa : conjunto.getEtapas()){         
                
                /* Carrega as Heurísticas depois de carregar todas as Etapas e Regiões do Conjunto Heurístico
                   para poder realizar as validações necessárias */
                
                HeuristicaDAO.carregaTodas(etapa);
            }
            
            AnotacaoDAO.carregaTodas(conjunto);
                
            return conjunto;
            
        }else{
            throw new IllegalArgumentException("Não foi localizado nenhum Conjunto Heurístico com o id [" + id + "]");
        }
    }  
    
    public static ArrayList<ElementoLista> lista(long idUsuario) throws Exception {

        ArrayList<ElementoLista> conjuntosHeuristicos = new ArrayList();

        String sql = "SELECT C.COD_COMPONENTE, C.TXT_NOME, C.TXT_DEFINICAO, C.COD_TIPO FROM HEU_COMPONENTE C, HEU_TIPO D WHERE " +
                     " C.COD_USUARIO = " + idUsuario +
                     " AND C.COD_TIPO = D.COD_TIPO AND D.COD_CLASSE = " + Tipo.Classe.CONJUNTO_HEURISTICO.codigo() +
                     " ORDER BY C.TXT_NOME";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);        
        while (dados.next()) {
            
            long id          = dados.getLong("COD_COMPONENTE");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");
            long idTipo      = dados.getLong("COD_TIPO");

            ElementoLista elemento = new ElementoLista(id, nome, definicao, ConjuntoHeuristico.class, tipo(idTipo));

            conjuntosHeuristicos.add(elemento);
        }
        
        return conjuntosHeuristicos;
    }
}