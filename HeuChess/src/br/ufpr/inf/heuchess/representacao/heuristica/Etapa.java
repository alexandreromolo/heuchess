package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.persistencia.HeuristicaDAO;
import br.ufpr.inf.utils.UtilsString;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 09:22
 */
public class Etapa extends Componente {
        
    private ArrayList<HeuristicaTransicaoEtapa> heuristicasTransicaoEtapa;
    private ArrayList<HeuristicaValorPeca>      heuristicasValorPeca;
    private ArrayList<HeuristicaValorTabuleiro> heuristicasValorTabuleiro;    
    private ArrayList<Regiao>                   regioes;
            
    private final ConjuntoHeuristico conjuntoHeuristico;
    
    private int valorPeao;
    private int valorTorre;
    private int valorCavalo;
    private int valorBispo;
    private int valorDama;
    
    public Etapa(ConjuntoHeuristico conjunto, String nome, long idAutor, Tipo tipo) {
        
        super(nome,idAutor,tipo);
        
        heuristicasTransicaoEtapa = new ArrayList();
        heuristicasValorPeca      = new ArrayList();
        heuristicasValorTabuleiro = new ArrayList();        
        regioes                   = new ArrayList();
        
        conjuntoHeuristico = conjunto;
    }    

    public Etapa(ConjuntoHeuristico conjunto, String nome, long idAutor, Tipo tipo, String definicao) throws Exception {
        
        this(conjunto,nome,idAutor,tipo);        
        
        StringTokenizer tokens = new StringTokenizer(definicao, ",");
        
        if (!tokens.hasMoreTokens()){
            throw new IllegalArgumentException("Não foi encontrada nenhum valor de Tipo de Peça na definição da Etapa!");
        }
                       
        valorPeao   = Integer.parseInt(tokens.nextToken());
        valorTorre  = Integer.parseInt(tokens.nextToken());
        valorCavalo = Integer.parseInt(tokens.nextToken());
        valorBispo  = Integer.parseInt(tokens.nextToken());
        valorDama   = Integer.parseInt(tokens.nextToken());
    }    

    public ConjuntoHeuristico getConjuntoHeuristico(){
        return conjuntoHeuristico;
    }
    
    public ArrayList<HeuristicaTransicaoEtapa> getHeuristicasTransicaoEtapa() {
        return heuristicasTransicaoEtapa;
    }

    public ArrayList<HeuristicaValorPeca> getHeuristicasValorPeca() {
        return heuristicasValorPeca;
    }

    public ArrayList<HeuristicaValorTabuleiro> getHeuristicasValorTabuleiro() {
        return heuristicasValorTabuleiro;
    }

    public int getValorPeao() {
        return valorPeao;
    }

    public void setValorPeao(int valorPeao) {
        this.valorPeao = valorPeao;
    }

    public int getValorTorre() {
        return valorTorre;
    }

    public void setValorTorre(int valorTorre) {
        this.valorTorre = valorTorre;
    }

    public int getValorCavalo() {
        return valorCavalo;
    }

    public void setValorCavalo(int valorCavalo) {
        this.valorCavalo = valorCavalo;
    }

    public int getValorBispo() {
        return valorBispo;
    }

    public void setValorBispo(int valorBispo) {
        this.valorBispo = valorBispo;
    }

    public int getValorDama() {
        return valorDama;
    }

    public void setValorDama(int valorDama) {
        this.valorDama = valorDama;
    }
    
    public ArrayList<Regiao> getRegioes() {
        return regioes;
    }

    public Regiao getRegiao(String nomeRegiao) throws Exception {
        
        for(Regiao regiao : regioes){
            
            if (regiao.getNome().equalsIgnoreCase(nomeRegiao)) { 
                return regiao; 
            }
        }
        
        throw new IllegalArgumentException("Nome não localizado de Região [" + nomeRegiao + "] na Etapa [" + getNome() + "]");
    }
    
    @Override
    public String getDescricaoDB() {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(valorPeao);
        builder.append(',');
        builder.append(valorTorre);
        builder.append(',');
        builder.append(valorCavalo);
        builder.append(',');
        builder.append(valorBispo);
        builder.append(',');
        builder.append(valorDama);
                                 
        return builder.toString();
    }
    
    @Override
    public String getDescricaoDHJOG() {
        
        StringBuilder builder = new StringBuilder();

        builder.append(DHJOG.TXT_ETAPA);
        builder.append(" \"");
        builder.append(getNome());
        builder.append("\"\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_PEAO_VALOR);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" ");
        builder.append(valorPeao);
        builder.append("\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_TORRE_VALOR);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" ");
        builder.append(valorTorre);
        builder.append("\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_CAVALO_VALOR);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" ");
        builder.append(valorCavalo);
        builder.append("\n");
                
        builder.append("   ");
        builder.append(DHJOG.TXT_BISPO_VALOR);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" ");
        builder.append(valorBispo);
        builder.append("\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_DAMA_VALOR);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" ");
        builder.append(valorDama);
        builder.append("\n");
        
        builder.append(DHJOG.TXT_FIM);
        builder.append(" ");
        builder.append(DHJOG.TXT_ETAPA);

        return builder.toString();
    }

    @Override
    public String getNomeTipoComponente() {
        return "Etapa";
    }
        
    public int quantidadeHeuristicasQueUsam(Componente componente) {
        
        int quantidade = 0;
        
        for (Heuristica heuristica : heuristicasTransicaoEtapa) {

            if (UtilsString.procuraPalavra(componente.getNome(), heuristica.getCondicaoDB(), DHJOG.DELIMITADORES) || 
                UtilsString.procuraPalavra(componente.getNome(), heuristica.getAcoesDB(),    DHJOG.DELIMITADORES)) {
                quantidade++;
            }            
        }

        if (componente instanceof Regiao){
            
            for (Heuristica heuristica : heuristicasValorPeca){
            
                if (UtilsString.procuraPalavra(componente.getNome(), heuristica.getCondicaoDB(), DHJOG.DELIMITADORES) || 
                    UtilsString.procuraPalavra(componente.getNome(), heuristica.getAcoesDB(),    DHJOG.DELIMITADORES)) {
                    quantidade++;
                }                            
            } 
            
            for (Heuristica heuristica : heuristicasValorTabuleiro){
            
                if (UtilsString.procuraPalavra(componente.getNome(), heuristica.getCondicaoDB(), DHJOG.DELIMITADORES) ||
                    UtilsString.procuraPalavra(componente.getNome(), heuristica.getAcoesDB(),    DHJOG.DELIMITADORES)) {
                    quantidade++;
                }                            
            }    
        }
        
        return quantidade;
    }
    
    public void procuraRenomeia(Componente componente, String nomeVelho) throws Exception {
        
        if (componente instanceof Etapa){            
            procuraRenomeiaSalva(heuristicasTransicaoEtapa, nomeVelho, componente.getNome());
        }else{                                
            procuraRenomeiaSalva(heuristicasTransicaoEtapa, nomeVelho, componente.getNome());
            procuraRenomeiaSalva(heuristicasValorPeca,      nomeVelho, componente.getNome());              
            procuraRenomeiaSalva(heuristicasValorTabuleiro, nomeVelho, componente.getNome());
        }
    }
    
    private void procuraRenomeiaSalva(ArrayList<? extends Heuristica> arrayList, String nomeVelho, String nomeNovo) throws Exception {

        for (Heuristica heuristica : arrayList) {

            boolean trocarCondicao = false;
            boolean trocarAcoes    = false;

            String textoCondicaoVelho = heuristica.getCondicaoDB();
            String textoAcoesVelho    = heuristica.getAcoesDB();
            String textoCondicaoNovo, textoAcoesNovo;

            if (UtilsString.procuraPalavra(nomeVelho, textoCondicaoVelho, DHJOG.DELIMITADORES)) {

                textoCondicaoNovo = UtilsString.substituiPalavra(nomeVelho, nomeNovo, textoCondicaoVelho, DHJOG.DELIMITADORES);

                heuristica.setCondicaoDB(textoCondicaoNovo);
                trocarCondicao = true;
            }

            if (UtilsString.procuraPalavra(nomeVelho, textoAcoesVelho, DHJOG.DELIMITADORES)) {

                textoAcoesNovo = UtilsString.substituiPalavra(nomeVelho, nomeNovo, textoAcoesVelho, DHJOG.DELIMITADORES);

                heuristica.setAcoesDB(textoAcoesNovo);
                trocarAcoes = true;
            }

            if (trocarCondicao || trocarAcoes) {

                try{
                    HeuristicaDAO.atualiza(heuristica);                    
                }catch(Exception excecaoAtualizacao){                    
                    
                    try {
                        ///////////////////////////
                        // Volta valores Antigos //
                        //////////////////////////
                        
                        if (trocarCondicao) {
                            heuristica.setCondicaoDB(textoCondicaoVelho);
                        }
                        if (trocarAcoes) {
                            heuristica.setAcoesDB(textoAcoesVelho);
                        }
                    } catch (Exception excecaoVoltaValoresAntigos) {
                        HeuChess.registraExcecao(excecaoAtualizacao);
                        
                        throw excecaoVoltaValoresAntigos;
                    }
                    
                    throw excecaoAtualizacao;
                }
            }
        }
    }
}
