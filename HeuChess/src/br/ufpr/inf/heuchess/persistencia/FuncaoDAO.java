package br.ufpr.inf.heuchess.persistencia;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.Funcao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 19 de Julho de 2006, 17:30
 */
public class FuncaoDAO {
    
    public static ArrayList<Tipo>   tiposFuncao;
    public static ArrayList<Funcao> funcoesBasicas;
     
    public static void carregaTipos() throws Exception {
        
        tiposFuncao = TipoDAO.carregaTodos(Tipo.Classe.FUNCAO);
        
        for (Tipo tipo : tiposFuncao){
            
            if (tipo.getNome().equalsIgnoreCase("FUNÇÃO BÁSICA TEMPO")){
                Funcao.FUNCAO_BASICA_TEMPO = tipo;
            }else
                if (tipo.getNome().equalsIgnoreCase("FUNÇÃO BÁSICA POSIÇÃO")){
                    Funcao.FUNCAO_BASICA_POSICAO = tipo;
                }else
                    if (tipo.getNome().equalsIgnoreCase("FUNÇÃO BÁSICA QUANTIDADE")){
                        Funcao.FUNCAO_BASICA_QUANTIDADE = tipo;
                    }else
                        if (tipo.getNome().equalsIgnoreCase("FUNÇÃO BÁSICA VALOR")){
                            Funcao.FUNCAO_BASICA_VALOR = tipo;
                        }else
                            if (tipo.getNome().equalsIgnoreCase("FUNÇÃO BÁSICA SITUAÇÃO")){
                                Funcao.FUNCAO_BASICA_SITUACAO = tipo;
                            }else{
                                throw new IllegalArgumentException("Tipo de Função Desconhecido [" + tipo.getNome() +"]");
                            }
        }
    }
        
    public static Tipo tipo(long id){        
        return TipoDAO.tipo(tiposFuncao, id);
    }
    
    public static void adiciona(Funcao funcao) throws Exception {
        ComponenteDAO.adiciona((Componente) funcao);
    }        
    
    public static void atualiza(Funcao funcao) throws Exception {
        ComponenteDAO.atualiza((Componente) funcao);
    }    
    
    public static void apaga(long id) throws Exception {
        ComponenteDAO.apagaSemPai(id);        
    }    
    
    public static void carregaTodas() throws Exception {

        funcoesBasicas = new ArrayList();

        String sql = "SELECT C.* FROM HEU_COMPONENTE C, HEU_TIPO D WHERE " +
                     " C.COD_TIPO = D.COD_TIPO AND D.COD_CLASSE = " + Tipo.Classe.FUNCAO.codigo() +
                     " ORDER BY C.TXT_NOME";

        ResultSet dados = ConexaoDBHeuChess.executaConsulta(sql);
        while (dados.next()) {
            long codAutor    = dados.getLong("COD_USUARIO");
            long idTipo      = dados.getLong("COD_TIPO");
            String nome      = dados.getString("TXT_NOME");
            String definicao = dados.getString("TXT_DEFINICAO");

            Funcao funcao = new Funcao(nome, codAutor, tipo(idTipo), definicao);

            funcao.setId(dados.getLong("COD_COMPONENTE"));
            funcao.setVersao(dados.getLong("NUM_VERSAO"));
            funcao.setDataCriacao(new java.util.Date(dados.getDate("DAT_CRIACAO").getTime()));
            funcao.setDataUltimaModificacao(new java.util.Date(dados.getDate("DAT_ULTIMA_MODIFICACAO").getTime()));
            funcao.setQuantidadeAcessos(dados.getLong("QTD_ACESSOS"));
            funcao.setQuantidadeCopias(dados.getLong("QTD_COPIAS"));
            funcao.setPermissoes(dados.getInt("BIT_PERMISSOES"));

            AnotacaoDAO.carregaTodas(funcao);

            funcoesBasicas.add(funcao);
        }
        
        if (funcoesBasicas.isEmpty()){ 
            throw new RuntimeException("Não foi localizada nenhuma função!");
        }
    }    
    
    public static Funcao localiza(String nomeCurto){
        
        for (int x = 0; x < funcoesBasicas.size(); x++){
            
            Funcao funcao = funcoesBasicas.get(x);
            
            if (funcao.getNomeCurto().equalsIgnoreCase(nomeCurto)){
                return funcao;
            }
        }
        
        return null;
    }
}