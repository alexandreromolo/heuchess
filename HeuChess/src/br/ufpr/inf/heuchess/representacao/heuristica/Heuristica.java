package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 17 de Junho de 2006, 18:40
 */
public abstract class Heuristica extends Componente {

    public static Tipo HEURISTICA_VALOR_PECA;
    public static Tipo HEURISTICA_VALOR_TABULEIRO;
    public static Tipo HEURISTICA_TRANSICAO_ETAPA;      
    
    private final Etapa etapa;
    
    private String textoCondicaoDB;    
    private String textoCondicaoDHJOG;
    
    private String textoAcoesDB;    
    private String textoAcoesDHJOG;
    
    private String condicoesToJava;
    private String condicoesToJavaSimetrica;
    
    private ArrayList<CondicaoHeuristica> condicoes;
    
    protected Heuristica(Etapa etapa, String nome, long idAutor, Tipo tipo) {
        
        super(nome,idAutor,tipo);             
        
        condicoes = new ArrayList();
        
        this.etapa = etapa;
    } 
            
    public Heuristica(Etapa etapa, String nome, long idAutor, Tipo tipo, String definicao) throws Exception {
        
        this(etapa,nome,idAutor,tipo);             
        
        int posicao = definicao.indexOf(DHJOG.TXT_ENTAO + "\n");
        
        if (posicao == -1){
            throw new IllegalArgumentException("Não foi localizado [" + DHJOG.TXT_ENTAO + "] na definição da Heurística no Banco");
        }
        
        setCondicaoDB(definicao.substring(0, posicao - 1));
        
        setAcoesDB(definicao.substring(posicao + 6, definicao.length()));
    }

    public Etapa getEtapa(){
        return etapa;
    }
    
    public final void setCondicaoDB(String condicao) throws Exception {
        
        textoCondicaoDB = condicao;
        
        condicoes.clear();
        
        StringTokenizer tokens = new StringTokenizer(textoCondicaoDB,"\n");
        
        if (tokens.countTokens() == 0){
            throw new IllegalArgumentException("Não foi encontrada nenhuma condição na heurística [" + getNome() + "]");
        }
        
        while (tokens.hasMoreTokens()) {
            
            String linhaCondicao = tokens.nextToken().trim();
            
            if (linhaCondicao.length() > 1) {
                condicoes.add(new CondicaoHeuristica(this,linhaCondicao));
            }
        }
        
        //////////////////////////////////////////
        // Converte Entrada DB em formato DHJOG //
        //////////////////////////////////////////
        
        StringBuilder builder = new StringBuilder();
        
        for (int x = 0; x < condicoes.size() - 1; x++) {
            builder.append("      ");
            builder.append(condicoes.get(x).toDHJOG(true));
            builder.append('\n');
        }

        CondicaoHeuristica ultimaCondicao = condicoes.get(condicoes.size() - 1);
            
        builder.append("      ");
        builder.append(ultimaCondicao.toDHJOG(false));
        
        textoCondicaoDHJOG = builder.toString();
    }
    
    public String getCondicaoDB() {
        return textoCondicaoDB;
    }

    public String getCondicaoDHJOG(){
        return textoCondicaoDHJOG;
    }
    
    public ArrayList<CondicaoHeuristica> getCondicoes(){
        return condicoes;
    }
    
    public final void setAcoesDB(String acoes) throws Exception {
        textoAcoesDB    = acoes;        
        textoAcoesDHJOG = parseAcoesDB();
    }
    
    public String getAcoesDB() {
        return textoAcoesDB;
    }

    public String getAcoesDHJOG(){
        return textoAcoesDHJOG;
    }
    
    protected abstract String parseAcoesDB() throws Exception;
    
    @Override
    public String getDescricaoDB() {        
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(getCondicaoDB());
        builder.append('\n');
        builder.append(DHJOG.TXT_ENTAO);
        builder.append('\n');
        builder.append(getAcoesDB());
        
        return builder.toString();        
    }
    
    @Override
    public String getDescricaoDHJOG(){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(DHJOG.TXT_HEURISTICA);
        builder.append(' ');        
                
        if (this instanceof HeuristicaValorPeca){
            builder.append(DHJOG.TXT_VALOR_PECA);                    
        }else
            if (this instanceof HeuristicaValorTabuleiro){
                builder.append(DHJOG.TXT_VALOR_TABULEIRO);
            }else
                if (this instanceof HeuristicaTransicaoEtapa){
                    builder.append(DHJOG.TXT_TRANSICAO_ETAPA);
                }else{
                    throw new IllegalArgumentException("Tipo de heurística não tratado na heurística [" + getNome() + "]");
                }
        
        builder.append(" \"");                
        builder.append(getNome());
        builder.append("\"\n ");
        
        builder.append(DHJOG.TXT_SE);
        builder.append('\n');

        builder.append(textoCondicaoDHJOG);
        
        builder.append("\n ");
        builder.append(DHJOG.TXT_ENTAO);
        builder.append('\n');
        builder.append(textoAcoesDHJOG);
        builder.append('\n');
        builder.append(DHJOG.TXT_FIM);
        builder.append(' ');
        builder.append(DHJOG.TXT_HEURISTICA);
        
        return builder.toString();
    }
    
    public void preparaParaAnaliseHeuristica() throws Exception {
        
        if (condicoes.size() > 0){
            
            // Regiões normais //
            
            StringBuilder builder = new StringBuilder();
            
            builder.append("condicao = (");
            
            for (int x = 0; x < condicoes.size()-1; x++){
                builder.append(condicoes.get(x).toJava(true,false));
                builder.append(' ');
            }
            
            CondicaoHeuristica ultimaCondicao = condicoes.get(condicoes.size()-1);
            
            builder.append(ultimaCondicao.toJava(false,false));
            
            builder.append(");");
            
            condicoesToJava = builder.toString();
            
            // Regiões Simétricas //
            
            builder.delete(0,builder.length());
                    
            builder.append("condicao = (");
            
            for (int x = 0; x < condicoes.size()-1; x++){
                builder.append(condicoes.get(x).toJava(true,true));
                builder.append(' ');
            }
            
            ultimaCondicao = condicoes.get(condicoes.size()-1);
            
            builder.append(ultimaCondicao.toJava(false,true));
            
            builder.append(");");
            
            condicoesToJavaSimetrica = builder.toString();
            
        }else{
            throw new IllegalArgumentException("A Heurística [" + getNome() + "] não possui nenhuma condição");
        }
    }
    
    public String textoCondicoesToJava(){
        return condicoesToJava;
    }
    
    public String textoCondicoesToJavaSimetrica(){
        return condicoesToJavaSimetrica;
    }
    
    public static Heuristica recriaNovoTipo(Tipo novoTipo, Heuristica heuristicaOriginal, String novaDefinicao) throws Exception {
        
        Heuristica heuristica;
            
        if (novoTipo == Heuristica.HEURISTICA_TRANSICAO_ETAPA) {
            heuristica = new HeuristicaTransicaoEtapa(heuristicaOriginal.getEtapa(), heuristicaOriginal.getNome(), heuristicaOriginal.getIdAutor(), novaDefinicao);
        } else 
            if (novoTipo == Heuristica.HEURISTICA_VALOR_TABULEIRO) {
                heuristica = new HeuristicaValorTabuleiro(heuristicaOriginal.getEtapa(), heuristicaOriginal.getNome(), heuristicaOriginal.getIdAutor(), novaDefinicao);
            } else 
                if (novoTipo == Heuristica.HEURISTICA_VALOR_PECA) {
                    heuristica = new HeuristicaValorPeca(heuristicaOriginal.getEtapa(), heuristicaOriginal.getNome(), heuristicaOriginal.getIdAutor(), novaDefinicao);
                } else {
                    throw new IllegalArgumentException("Tipo desconhecido de Heurística [" + novoTipo + 
                                                       "] usado na recriação da heurística [" + heuristicaOriginal.getNome() + "]");
                }

        Componente.copiaAtributos(heuristicaOriginal, heuristica, false);
        
        // Necessário pois o método Copia Atributos voltou o tipo Antigo //
        
        heuristica.setTipo(novoTipo); 
        
        // Apenas passou o vetor de Anotações pois a heurística já está em edição //
        
        heuristica.setAnotacoes(heuristicaOriginal.getAnotacoes()); 
        
        return heuristica;
    }
    
    public Heuristica geraClone() throws Exception {
        
        Heuristica heuristica;
        
            
        if (this instanceof HeuristicaTransicaoEtapa) {
            heuristica = new HeuristicaTransicaoEtapa(getEtapa(), getNome(), getIdAutor(), getDescricaoDB());
        } else 
            if (this instanceof HeuristicaValorTabuleiro) {
                heuristica = new HeuristicaValorTabuleiro(getEtapa(), getNome(), getIdAutor(), getDescricaoDB());
            } else 
                if (this instanceof HeuristicaValorPeca) {
                    heuristica = new HeuristicaValorPeca(getEtapa(), getNome(), getIdAutor(), getDescricaoDB());
                } else {
                    throw new IllegalArgumentException("Tipo desconhecido de Heurística [" + getTipo().getNome() + 
                                                       "] na heurística [" + getNome() + "]");
                }

        Componente.copiaAtributos(this, heuristica, true);
                
        return heuristica;
    }
}
