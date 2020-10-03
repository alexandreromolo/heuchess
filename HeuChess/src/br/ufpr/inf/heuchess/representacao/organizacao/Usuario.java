package br.ufpr.inf.heuchess.representacao.organizacao;

import br.ufpr.inf.heuchess.representacao.heuristica.Situacao;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;
import java.util.Date;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 09:42
 */
public class Usuario {
    
    public static Tipo APRENDIZ;
    public static Tipo COORDENADOR;
    public static Tipo ADMINISTRADOR;
    
    public static Situacao BLOQUEADO;
    public static Situacao LIBERADO;
    public static Situacao TROCANDO_SENHA;
    
    private long           id;
    private Tipo           tipo;
    private String         login;
    private String         senha;
    private String         nome;
    private boolean        sexoMasculino;    
    private String         email;
    private Date           dataNascimento;
    private Date           dataCriacao;
    private boolean        estaOnline;
    private Situacao       situacao;
    
    // Dados ainda não usados
    
    private String         foto;    
    private long           quantidadeAnotacoesParaOutros;
    private long           quantidadeAcessosRealizados;
    private long           quantidadeCopiasRealizadas;
    private int            permissoes;
    private Date           dataCancelamento;
    
    public Usuario(){
    
    }
    
    public Usuario(long id, String nome) {
        setId(id);        
        setNome(nome);        
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }    

    public long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isSexoMasculino() {
        return sexoMasculino;
    }

    public void setSexoMasculino(boolean sexoMasculino) {
        this.sexoMasculino = sexoMasculino;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isOnline() {
        return estaOnline;
    }

    public void setOnline(boolean estaOnline) {
        this.estaOnline = estaOnline;
    }

    public long getQuantidadeAnotacoesParaOutros() {
        return quantidadeAnotacoesParaOutros;
    }

    public void setQuantidadeAnotacoesParaOutros(long quantidadeAnotacoesParaOutros) {
        this.quantidadeAnotacoesParaOutros = quantidadeAnotacoesParaOutros;
    }

    public long getQuantidadeAcessosRealizados() {
        return quantidadeAcessosRealizados;
    }

    public void setQuantidadeAcessosRealizados(long quantidadeAcessosRealizados) {
        this.quantidadeAcessosRealizados = quantidadeAcessosRealizados;
    }

    public long getQuantidadeCopiasRealizadas() {
        return quantidadeCopiasRealizadas;
    }

    public void setQuantidadeCopiasRealizadas(long quantidadeCopiasRealizadas) {
        this.quantidadeCopiasRealizadas = quantidadeCopiasRealizadas;
    }

    public int getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(int permissoes) {
        this.permissoes = permissoes;
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
    
    public void setSituacao(Situacao situacao){
        this.situacao = situacao;
    }
    
    public Usuario geraClone(){
    
        Usuario usuario = new Usuario(id,nome);
        
        usuario.setTipo(tipo);
        usuario.setLogin(login);
        usuario.setSenha(senha);
        usuario.setEmail(email);
        usuario.setSexoMasculino(sexoMasculino);
        usuario.setSituacao(situacao);
        usuario.setDataNascimento(dataNascimento);    
        usuario.setDataCriacao(dataCriacao);
        usuario.setOnline(estaOnline);
                        
        usuario.setFoto(foto);
        usuario.setQuantidadeAnotacoesParaOutros(quantidadeAnotacoesParaOutros);
        usuario.setQuantidadeAcessosRealizados(quantidadeAcessosRealizados);  
        usuario.setQuantidadeCopiasRealizadas(quantidadeCopiasRealizadas);
        usuario.setPermissoes(permissoes);
        usuario.setDataCancelamento(dataCancelamento);
        
        return usuario;    
    }
    
    public boolean igual(Usuario usuario){
    
        if (usuario != null           &&
            id == usuario.getId()     &&
            tipo == usuario.getTipo() && 
            situacao == usuario.getSituacao()          &&    
            sexoMasculino == usuario.isSexoMasculino() &&   
            login.equalsIgnoreCase(usuario.getLogin()) &&
            senha.equalsIgnoreCase(usuario.getSenha()) &&
            nome.equalsIgnoreCase(usuario.getNome())   &&            
            email.equalsIgnoreCase(usuario.getEmail()) &&        
            permissoes == usuario.getPermissoes()      &&
            dataNascimento.compareTo(usuario.getDataNascimento()) == 0){    
    
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