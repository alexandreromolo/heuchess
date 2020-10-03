package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Aug 4, 2012
 */
public class Situacao {

    public enum Classe {
    
        SITUACAO_USUARIO("Situação do Usuário",10),
        SITUACAO_TURMA("Situação da Turma",11);
        
        private String descricao;
        private int    codigo;
        
        private Classe(String descricao, int codigo){
            this.descricao = descricao;
            this.codigo    = codigo;
        }
        
        public int codigo(){
            return codigo;
        }
        
        @Override
        public String toString(){
            return descricao;
        }
    }
    
    private long   id;
    private Classe classe;
    private String nome;
    private String descricao;
    
    public Situacao(long id, Classe classe, String nome, String descricao) {
        this.id        = id;
        this.classe    = classe;
        this.nome      = nome;
        this.descricao = descricao;
    }
    
    public long getId() {
        return id;
    }
    
    public Classe getClasse() {
        return classe;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString(){
        return nome;
    }
}
