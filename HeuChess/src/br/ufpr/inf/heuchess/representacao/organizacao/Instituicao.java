package br.ufpr.inf.heuchess.representacao.organizacao;

import java.util.Date;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 30, 2012
 */
public class Instituicao {

    private long   id;
    private String nome;  
    private String descricao;
    private long   idCoordenador;    
    private Date   dataCriacao;
    private Date   dataCancelamento;

    public Instituicao(){
        
    }
    
    public Instituicao(String nome, String descricao, long idCoordenador){        
        setNome(nome);
        setDescricao(descricao);
        setIdCoordenador(idCoordenador);
    }
    
    public long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public final void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getIdCoordenador() {
        return idCoordenador;
    }

    public final void setIdCoordenador(long idCoordenador) {
        this.idCoordenador = idCoordenador;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }
    
    public boolean igual(Instituicao instituicao){
        
        if (instituicao != null                                    && 
            nome.equalsIgnoreCase(instituicao.getNome())           &&
            descricao.equalsIgnoreCase(instituicao.getDescricao()) &&
            idCoordenador == instituicao.getIdCoordenador()){
            
            return true;
        }else{            
            return false;
        }        
    }
    
    public Instituicao geraClone(){
        
        Instituicao instituicao = new Instituicao(nome,descricao,idCoordenador);
        
        instituicao.setId(id);
        instituicao.setDataCriacao(dataCriacao);
                        
        return instituicao;
    }
    
    @Override
    public String toString(){
        return nome;
    }
}