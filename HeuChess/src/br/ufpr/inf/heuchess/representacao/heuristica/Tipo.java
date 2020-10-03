package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 12 de Julho de 2006, 16:09
 */
public class Tipo {
    
    public enum Classe {
    
        USUARIO("Usu�rio",1),
        CONJUNTO_HEURISTICO("Conjunto Heur�stico",2),
        ANOTACAO("Anota��o",3),
        HEURISTICA("Heur�stica",4),
        ETAPA("Etapa",5),
        REGIAO("Regi�o",6),
        EXPRESSAO_CALCULO("Express�o de Calculo Heur�stico",7),
        FUNCAO("Fun��o",8),    
        SITUACAO_JOGO("Situa��o de Jogo",9),
        //SITUACAO_USUARIO("Situa��o do Usu�rio",10),
        //SITUACAO_TURMA("Situa��o da Turma",11),        
        HISTORICO("Hist�rico", 12);
        
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