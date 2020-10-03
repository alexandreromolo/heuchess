package br.ufpr.inf.heuchess.representacao.organizacao;

import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 30, 2012
 */
public class Turma {

    public static Situacao BLOQUEADA;
    public static Situacao LIBERADA;    
    
    private long        id;
    private String      nome;
    private String      descricao;
    private long        idInstituicao;
    private Date        dataCriacao;
    private Date        dataCancelamento;   
    private int         permissoes;     
    private Situacao    situacao;
    
    private final ArrayList<InscricaoTurma> coordenadores;
    private final ArrayList<InscricaoTurma> aprendizes;
    
    public Turma(){
        coordenadores = new ArrayList();
        aprendizes    = new ArrayList();
    }
    
    public Turma(String nome, String descricao, long idInstituicao, Situacao situacao){
        this();
        
        setNome(nome);
        setDescricao(descricao);
        setIdInstituicao(idInstituicao);
        setSituacao(situacao);
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

    public long getIdInstituicao() {
        return idInstituicao;
    }

    public final void setIdInstituicao(long idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    public int getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(int permissoes) {
        this.permissoes = permissoes;
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
    
    public Situacao getSituacao(){
        return situacao;
    }
    
    public final void setSituacao(Situacao situacao){
        this.situacao = situacao;
    }
    
    public void adicionaCoordenador(Usuario usuario){
        coordenadores.add(new InscricaoTurma(id,usuario,Usuario.COORDENADOR));
    }
    
    public void adicionaAprendiz(Usuario usuario){
        aprendizes.add(new InscricaoTurma(id,usuario,Usuario.APRENDIZ));
    }
    
    public ArrayList<InscricaoTurma> inscricoesCoordenadores(){
        return coordenadores;
    }
    
    public ArrayList<InscricaoTurma> inscricoesAprendizes(){
        return aprendizes;
    }
    
    public void registraInscricao(InscricaoTurma inscricaoTurma){
        
        if (inscricaoTurma.getTipo() == Usuario.COORDENADOR){
            coordenadores.add(inscricaoTurma);
        }else
            if (inscricaoTurma.getTipo() == Usuario.APRENDIZ){
                aprendizes.add(inscricaoTurma);    
            }else{
                throw new IllegalArgumentException("Tipo de usuário não suportado em inscrição de Turma [" + inscricaoTurma.getTipo() + "]");
            }
    }
    
    public Turma geraClone(){
        
        Turma turma = new Turma(nome,descricao,idInstituicao,situacao);
        
        turma.setId(id);
        turma.setDataCriacao(dataCriacao);
        turma.setPermissoes(permissoes);        
        
        for (InscricaoTurma inscricao : coordenadores){
            turma.inscricoesCoordenadores().add(inscricao.geraClone());
        }
        
        for (InscricaoTurma inscricao : aprendizes){
            turma.inscricoesAprendizes().add(inscricao.geraClone());
        }
        
        return turma;        
    }
    
    public boolean igual(Turma turma){
        
        if (turma != null                                    &&
            id == turma.getId()                              &&                
            idInstituicao == turma.getIdInstituicao()        &&
            permissoes    == turma.getPermissoes()           &&    
            situacao      == turma.getSituacao()             && 
            nome.equalsIgnoreCase(turma.getNome())           &&
            descricao.equalsIgnoreCase(turma.getDescricao()) &&               
            InscricaoTurma.igual(coordenadores,turma.inscricoesCoordenadores()) && 
            InscricaoTurma.igual(aprendizes,turma.inscricoesAprendizes())){
            
            return true;
            
        }else{
            return false;
        }    
    }
    
    @Override
    public String toString(){
        return nome;
    }    
}
