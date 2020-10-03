package br.ufpr.inf.heuchess.representacao.situacaojogo;

import br.ufpr.inf.heuchess.representacao.heuristica.Componente;
import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.heuristica.Tipo;

/**
 *
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Jul 3, 2012
 */
public class SituacaoJogo extends Componente {

    private String fen;
    
    public SituacaoJogo(String nome, long idAutor, String fen, Tipo tipo) throws Exception {
        
        super(nome,idAutor,tipo);
        
        setFEN(fen);                
    }
    
    private SituacaoJogo(String nome, long idAutor, Tipo tipo) {
        super(nome,idAutor,tipo);
    }
    
    public final void setFEN(String fen) throws Exception {
        
        Tabuleiro tabuleiro = new Tabuleiro(fen); // Usado para fazer a valida��o no FEN
        
        this.fen = tabuleiro.getFEN();
    }
    
    public String getFEN(){
        return fen;
    }
    
    @Override
    public String getDescricaoDB() {
        return fen;    
    }
    
    @Override
    public String getDescricaoDHJOG(){
    
        StringBuilder builder = new StringBuilder();
        
        builder.append(DHJOG.TXT_SITUACAO_JOGO);
        builder.append(" \"");
        builder.append(getNome());
        builder.append("\"\n");
        builder.append("   ");
        builder.append(DHJOG.TXT_VANTAGEM);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(getTipo());
        builder.append("\"\n");
        builder.append("   ");        
        builder.append(DHJOG.TXT_FEN);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(fen);
        builder.append("\"\n");
        builder.append(DHJOG.TXT_FIM);
        builder.append(" ");
        builder.append(DHJOG.TXT_SITUACAO_JOGO);
        
        return builder.toString();
    }
    
    @Override
    public String getNomeTipoComponente() {
        return "Situa��o de Jogo";
    }
    
    public SituacaoJogo geraClone() {
        
        SituacaoJogo situacao = new SituacaoJogo(getNome(), getIdAutor(), getTipo());
        
        Componente.copiaAtributos(this, situacao, true);
        
        // N�o � necess�rio fazer nova valida��o no FEN //
        
        situacao.fen = fen;
        
        return situacao;        
    }
}
