package br.ufpr.inf.heuchess.representacao.heuristica;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Aug 21, 2012
 */
public enum Permissao {

    ACESSAR("Acessar", 1),
    UTILIZAR("Utilizar", 2),
    ANOTAR("Anotar", 4),
    ALTERAR("Alterar", 8),
    COPIAR("Copiar", 16),
    EXCLUIR("Excluir", 32);
    private final String descricao;
    private final int valor;

    Permissao(String descricao, int valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public int acrescentaPermissao(int valorAtual) {
        return valorAtual | getValor();
    }

    public int retiraPermissao(int valorAtual) {
        return valorAtual & (~getValor());
    }

    public boolean existe(int valorAtual) {
        
        if ((valorAtual & getValor()) == getValor()) {
            return true;
        } else {
            return false;
        }
    }
}