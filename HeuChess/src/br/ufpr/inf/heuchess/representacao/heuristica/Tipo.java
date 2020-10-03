package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Julho de 2006, 16:09
 */
public class Tipo {
    
    public enum Classe {
    
        USUARIO("Usuário",1),
        CONJUNTO_HEURISTICO("Conjunto Heurístico",2),
        ANOTACAO("Anotação",3),
        HEURISTICA("Heurística",4),
        ETAPA("Etapa",5),
        REGIAO("Região",6),
        EXPRESSAO_CALCULO("Expressão de Calculo Heurístico",7),
        FUNCAO("Função",8),    
        SITUACAO_JOGO("Situação de Jogo",9),
        //SITUACAO_USUARIO("Situação do Usuário",10),
        //SITUACAO_TURMA("Situação da Turma",11),        
        HISTORICO("Histórico", 12);
        
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
    
    public Tipo(long id, Classe classe, String nome, String descricao) {
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