package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 09:31
 */
public class Anotacao extends Componente {
    
    public static Tipo EXPLICAO;
    public static Tipo QUESTAO;
    public static Tipo ELOGIO;
    public static Tipo CRITICA;
    public static Tipo NORMAL;
            
    private String     informacao;    
    private Componente componente;
    
    public Anotacao(long idAutor, Componente componente, Tipo tipo, String titulo, String informacao) {
        
        super(titulo, idAutor, tipo);        
        
        if (informacao == null || informacao.trim().length() == 0){
            throw new IllegalArgumentException("Não foi encontrada nenhuma Informação na definição da Anotação!");
        }
        
        this.informacao = informacao;
        this.componente = componente;        
    }    
    
    public String getInformacao() {
        return informacao;
    }
    
    public final void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public Componente getComponente() {
        return componente;
    }

    public void setComponente(Componente componente){
        this.componente = componente;
    }
    
    @Override
    public String getDescricaoDB() {
        return informacao;
    }
    
    @Override
    public String getDescricaoDHJOG(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(DHJOG.TXT_ANOTACAO);
        builder.append(" \"");
        builder.append(getNome());
        builder.append("\"\n");
        builder.append("   ");
        builder.append(DHJOG.TXT_TIPO);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(getTipo());
        builder.append("\"\n");        
        builder.append("   ");        
        builder.append(DHJOG.TXT_TEXTO);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(getInformacao());
        builder.append("\"\n");
        builder.append(DHJOG.TXT_FIM);
        builder.append(" ");
        builder.append(DHJOG.TXT_ANOTACAO);
        
        return builder.toString();
    }

    @Override
    public String getNomeTipoComponente() {
        return "Anotação";
    }
    
    public Anotacao geraClone(){
    
        Anotacao anotacao = new Anotacao(getIdAutor(), componente, getTipo(), getNome(), informacao);
                
        Componente.copiaAtributos(this, anotacao, false);
        
        return anotacao;        
    }
}