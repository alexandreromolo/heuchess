package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 09:24
 */
public abstract class Componente implements Comparable<Componente> {
    
    private long   id;
    private String nome;
    private long   idAutor;
    private Tipo   tipo;
    private long   versao = 1;    
    private Date   dataCriacao;
    private Date   dataUltimaModificacao;
    private long   quantidadeAcessos;
    private long   quantidadeCopias;    
    private long   quantidadeAnotacoesRecebidas;
    private int    permissoes;   
    
    private ArrayList<Anotacao> anotacoes;
    
    public Componente(String nome, long idAutor, Tipo tipo) {
        this.nome    = nome;
        this.idAutor = idAutor;
        this.tipo    = tipo;
        anotacoes    = new ArrayList();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;        
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(long idAutor) {
        this.idAutor = idAutor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    
    public long getVersao() {
        return versao;
    }

    public void setVersao(long versao) {
        this.versao = versao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataUltimaModificacao() {
        return dataUltimaModificacao;
    }

    public void setDataUltimaModificacao(Date dataUltimaModificacao) {
        this.dataUltimaModificacao = dataUltimaModificacao;
    }

    public long getQuantidadeAcessos() {
        return quantidadeAcessos;
    }

    public void setQuantidadeAcessos(long quantidadeAcessos) {
        this.quantidadeAcessos = quantidadeAcessos;
    }

    public long getQuantidadeCopias() {
        return quantidadeCopias;
    }

    public void setQuantidadeCopias(long quantidadeCopias) {
        this.quantidadeCopias = quantidadeCopias;
    }   

    public int getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(int permissoesAcesso) {
        this.permissoes = permissoesAcesso;
    }
   
    public void permitir(Permissao permissao){
        permissoes = permissao.acrescentaPermissao(permissoes);
    }
    
    public void retirar(Permissao permissao){
        permissoes = permissao.retiraPermissao(permissoes);
    }
    
    public boolean pode(Permissao permissao){
        
        if (permissao.existe(permissoes)){
            return true;
        }else{
            return false;
        }
    }
    
    public long getQuantidadeAnotacoesRecebidas() {
        return quantidadeAnotacoesRecebidas;
    }

    public void setQuantidadeAnotacoesRecebidas(long quantidadeAnotacoesRecebidas) {
        this.quantidadeAnotacoesRecebidas = quantidadeAnotacoesRecebidas;
    }
    
    public ArrayList<Anotacao> getAnotacoes() {
        return anotacoes;
    }
    
    public void setAnotacoes(ArrayList<Anotacao> anotacoes){
        this.anotacoes = anotacoes;
    }

    @Override
    public String toString(){
        return nome;
    }
    
    @Override
    public int compareTo(Componente componente) {
        
        if (componente == null) {
            throw new NullPointerException();
        }

        return getNome().compareTo(componente.getNome());
    }
    
    public abstract String getDescricaoDB();
    
    public abstract String getDescricaoDHJOG();
    
    public abstract String getNomeTipoComponente();
    
    public static void copiaAtributos(Componente componenteOrigem, Componente componenteDestino, boolean clonarAnotacoes) {
        
        componenteDestino.setId(componenteOrigem.getId());
        componenteDestino.setNome(componenteOrigem.getNome());        
        componenteDestino.setIdAutor(componenteOrigem.getIdAutor());        
        componenteDestino.setTipo(componenteOrigem.getTipo());
        componenteDestino.setVersao(componenteOrigem.getVersao());
        componenteDestino.setDataCriacao(componenteOrigem.getDataCriacao());
        componenteDestino.setDataUltimaModificacao(componenteOrigem.getDataUltimaModificacao());                
        componenteDestino.setQuantidadeAcessos(componenteOrigem.getQuantidadeAcessos());
        componenteDestino.setQuantidadeCopias(componenteOrigem.getQuantidadeCopias());
        componenteDestino.setQuantidadeAnotacoesRecebidas(componenteOrigem.getQuantidadeAnotacoesRecebidas());
        componenteDestino.setPermissoes(componenteOrigem.getPermissoes());
        
        if (clonarAnotacoes) {
            
            componenteDestino.getAnotacoes().clear();

            for (Anotacao anotacaoOriginal : componenteOrigem.getAnotacoes()) {

                Anotacao anotacao = anotacaoOriginal.geraClone();

                anotacao.setComponente(componenteDestino);

                componenteDestino.getAnotacoes().add(anotacao);
            }
        }
    }
}