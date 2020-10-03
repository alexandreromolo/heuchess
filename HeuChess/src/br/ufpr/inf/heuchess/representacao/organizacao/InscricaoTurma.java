package br.ufpr.inf.heuchess.representacao.organizacao;

import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 31, 2012
 */
public class InscricaoTurma {
    
    private long    idTurma;  
    private Usuario usuario;
    private Tipo    tipo;
    private Date    dataCriacao;
    private Date    dataCancelamento;

    public InscricaoTurma(long idTurma, Usuario usuario, Tipo tipo){
        this.idTurma = idTurma;
        this.usuario = usuario;
        this.tipo    = tipo;
    }    

    public long getIdTurma() {
        return idTurma;
    }
    
    public void setIdTurma(long idTurma) {
        this.idTurma = idTurma;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(Date dataCriacao){
        this.dataCriacao = dataCriacao;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }
    
    public InscricaoTurma geraClone(){
        
        InscricaoTurma inscricao = new InscricaoTurma(idTurma,usuario,tipo);
        
        inscricao.setDataCriacao(dataCriacao);        
        
        return inscricao;
    }
    
    public boolean igual(InscricaoTurma inscricao){
        
        if (inscricao != null                                 &&
            idTurma         == inscricao.getIdTurma()         &&    
            usuario.getId() == inscricao.getUsuario().getId() &&
            tipo.getId()    == inscricao.getTipo().getId()){
            
            return true;
        }else{
            return false;
        }
    }
    
    public static boolean igual(ArrayList<InscricaoTurma> inscricoes1, ArrayList<InscricaoTurma> inscricoes2){
        
        if (inscricoes1 == null && inscricoes2 != null){
            return false;
        }
        
        if (inscricoes1 != null && inscricoes2 == null){
            return false;
        }
        
        if (inscricoes1 == null && inscricoes2 == null){
            return true;
        }
        
        if (inscricoes1.size() != inscricoes2.size()){
            return false;
        }
        
        // Procura todas as incrições do grupo 1 dentro do grupo 2
        // Como devem ter a mesma quantidade para serem identicos, basta comparar uma vez.
        
        for (InscricaoTurma inscricao : inscricoes1){
            
            boolean achou = false;
            
            for (InscricaoTurma insc : inscricoes2){
                
                if (inscricao.igual(insc)){
                    achou = true;
                    break;
                }
            }
            
            if (!achou){
                return false;
            }
        }      
        
        return true;
    }
    
    @Override
    public String toString(){
        return "Id Turma = [" + idTurma + "] Usuário [" + usuario + "] Tipo Inscrição [" + tipo + "]";
    }
}
