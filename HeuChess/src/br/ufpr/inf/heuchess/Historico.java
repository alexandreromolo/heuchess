package br.ufpr.inf.heuchess;

import br.ufpr.inf.heuchess.persistencia.HistoricoDAO;
import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Nov 5, 2012
 */
public class Historico {
    
    public static enum Tipo {
        
        ERRO_SISTEMA(43),
        
        ENTROU_NO_SISTEMA(44),
        SAIU_DO_SISTEMA(45),
        
        BUSCOU_AJUDA_DO_SISTEMA(46),
        
        CRIOU_COMPONENTE(47),
        ABRIU_COMPONENTE(48),        
        ALTEROU_COMPONENTE(49),        
        EXCLUIU_COMPONENTE(50),
        RELACIONOU_COMPONENTES(51),
        USOU_COMPONENTE_PARTIDA(52),
        USOU_COMPONENTE_AVALIACAO(53);
                
        /**
         * Faltou logar no histórico
         * - criou partida (armazenar dados usados)
         * - Viu (Abriu) dados de uma Etapa
	 * - Ações em Instituição
	 * - Ações em Turma
	 * - Ações em Usuário
         */ 
        
        private final long id;
        
        private Tipo(long id){
            this.id = id;
        }
        
        public long getId(){
            return id;
        }
    }
    
    public static void registra(Historico.Tipo tipo, String descricao) throws Exception {
        HistoricoDAO.adiciona(HeuChess.usuario, tipo, descricao);
    }
    
    public static void registraComponenteAberto(Componente componente) throws Exception {
                
        HistoricoDAO.adiciona(HeuChess.usuario, 
                                  Historico.Tipo.ABRIU_COMPONENTE, 
                                  null,
                                  componente.getId(), 
                                  componente.getTipo().getId(),
                                  componente.getIdAutor());
    }
    
    public static void registraComponenteCriado(Componente componente) throws Exception {

        HistoricoDAO.adiciona(HeuChess.usuario,
                              Tipo.CRIOU_COMPONENTE,
                              componente.getNome() + "\n" + componente.getDescricaoDB(),
                              componente.getId(),
                              componente.getTipo().getId(),
                              componente.getIdAutor());
    }
    
    public static void registraComponenteAlterado(Componente componente) throws Exception {
                
        HistoricoDAO.adiciona(HeuChess.usuario,
                              Tipo.ALTEROU_COMPONENTE,
                              componente.getNome() + "\n" + componente.getDescricaoDB(), 
                              componente.getId(), 
                              componente.getTipo().getId(), 
                              componente.getIdAutor());
    }
    
    public static void registraComponenteRelacionado(Componente componentePrincipal, Componente componenteIncluido) throws Exception {

        String descricao = "Pai\nCod: "    + componentePrincipal.getId() +
                           "\nCod Tipo: "  + componentePrincipal.getTipo().getId() +
                           "\nCod Autor: " + componentePrincipal.getIdAutor();

        HistoricoDAO.adiciona(HeuChess.usuario,
                              Tipo.RELACIONOU_COMPONENTES,
                              descricao,
                              componenteIncluido.getId(),
                              componenteIncluido.getTipo().getId(),
                              componenteIncluido.getIdAutor());
    }
    
    public static void registraComponenteExcluido(long idComponente, long idTipoComponente, long idAutorComponente) throws Exception {

        HistoricoDAO.adiciona(HeuChess.usuario,
                              Historico.Tipo.EXCLUIU_COMPONENTE,
                              null,
                              idComponente,
                              idTipoComponente,
                              idAutorComponente);
    }
    
    public static void registraComponenteUsado(Historico.Tipo tipo, Componente componente, boolean jogadorBranco) throws Exception {

        if (tipo != Tipo.USOU_COMPONENTE_PARTIDA && tipo != Tipo.USOU_COMPONENTE_AVALIACAO){
            throw new IllegalArgumentException("O tipo de Histórico passado não é válido para este método");
        }
            
        String descricao = null;
        
        if (componente instanceof ConjuntoHeuristico){
            descricao = jogadorBranco ? "Brancas" : "Pretas";
        }

        HistoricoDAO.adiciona(HeuChess.usuario,
                              tipo,
                              descricao,
                              componente.getId(),
                              componente.getTipo().getId(),
                              componente.getIdAutor());
    }
}
