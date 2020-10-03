package br.ufpr.inf.heuchess.representacao.heuristica;

import java.util.ArrayList;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 7 de Fevereiro de 2006, 09:25
 */
public class ConjuntoHeuristico extends Componente {
    
    public static  Tipo NIVEL_1_INICIANTE;
    public static  Tipo NIVEL_2_BASICO;
    public static  Tipo NIVEL_3_INTERMEDIARIO;
    public static  Tipo NIVEL_4_PLENO;
    public static  Tipo NIVEL_5_AVANCADO;
    public static  Tipo NIVEL_6_ESPECIALISTA;
        
    private Etapa            etapaInicial;
    private ArrayList<Etapa> etapas;    
    
    public ConjuntoHeuristico(String nome, long idAutor, Tipo tipo) {
        
        super(nome,idAutor,tipo);        
        
        etapas = new ArrayList();        
    } 
    
    public ConjuntoHeuristico(String nome, long idAutor, Tipo tipo, Etapa etapaInicial) {
        
        this(nome,idAutor,tipo);        
        
        setEtapaInicial(etapaInicial);            
    }    
        
    public Etapa getEtapaInicial() {
        return etapaInicial;
    }

    public final void setEtapaInicial(Etapa etapaInicial) {
        
        if (etapaInicial == null){
            throw new IllegalArgumentException("A etapa inicial de um ConjuntoHeuristico não pode ser nula");
        }
        
        this.etapaInicial = etapaInicial;
        
        if (etapas.contains(etapaInicial) == false){
            etapas.add(0,etapaInicial);
        }
    }
    
    public ArrayList<Etapa> getEtapas() {
        return etapas;
    }

    public Etapa getEtapa(String nomeEtapa) throws Exception {
        
        for (Etapa etapa : etapas) {
            
            if (etapa.getNome().equalsIgnoreCase(nomeEtapa)) {
                return etapa;
            }
        }

        throw new Exception("Etapa não encontrada [" + nomeEtapa + "]");
    }
    
    public void setDefinicao(String definicao) throws Exception {
        
        if (definicao != null && definicao.trim().length() > 0) {
            
            for (Etapa etapa : etapas) {
                
                if (etapa.getNome().equalsIgnoreCase(definicao)) {
                    setEtapaInicial(etapa);
                    return;
                }
            }
            
            throw new IllegalArgumentException("Não Achou a Etapa Inicial.\n" +
                                               "Definiçao inválida de ConjuntoHeurístico [" + definicao + "]");
            
        }else{
            throw new IllegalArgumentException("Definiçao inválida de Etapa Inicial do ConjuntoHeurístico [" + definicao + "]");
        }
    }

    @Override
    public String getDescricaoDB() {
        
        if (etapaInicial != null) {
            return etapaInicial.getNome();
        }else {
            throw new RuntimeException("O Conjunto Heurístico ainda não tem uma Etapa Inicial definida!");
        }
    }
    
    @Override
    public String getDescricaoDHJOG(){
        
        StringBuilder builder = new StringBuilder();

        builder.append(DHJOG.TXT_CONJUNTO_HEURISTICO);
        builder.append(" \"");
        builder.append(getNome());
        builder.append("\"\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_NIVEL_COMPLEXIDADE);
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(getTipo().getNome());
        builder.append("\"\n");
        
        builder.append("   ");
        builder.append(DHJOG.TXT_ETAPA_INICIAL );
        builder.append(" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" \"");
        builder.append(etapaInicial);
        builder.append("\"\n");
        
        builder.append(DHJOG.TXT_FIM);
        builder.append(" ");
        builder.append(DHJOG.TXT_CONJUNTO_HEURISTICO);

        return builder.toString();
    }
   
    @Override
    public String getNomeTipoComponente() {
        return "Conjunto Heurístico";
    }
     
    public String validaNomeUnicoComponente(String nome){
        
        String erro = DHJOG.validaNomeComponenteGeral(nome);
        
        if (erro != null){
            return erro;
        }
                    
        if (nome.equalsIgnoreCase(getNome())){
            return "O conjunto Heurístico já se chama \""+nome+"\"!\nEscolha outro nome para o componente.";
        }
        
        for (Etapa etapa : etapas){
            
            if (nome.equalsIgnoreCase(etapa.getNome())){
                return "Já existe uma Etapa chamada \""+nome+"\"!\nEscolha outro nome para o componente.";
            }
            
            for (Regiao regiao : etapa.getRegioes()){
                
                if (nome.equalsIgnoreCase(regiao.getNome())){
                    return "Já existe uma Região chamada \""+nome+"\"!\nEscolha outro nome para o componente.";
                }                   
            }
            
            for (HeuristicaTransicaoEtapa heuristicaTransicao : etapa.getHeuristicasTransicaoEtapa()){
                
                if (nome.equalsIgnoreCase(heuristicaTransicao.getNome())){
                    return "Já existe uma Heurística de Transição de Etapa chamada \""+nome+"\"!\nEscolha outro nome para o componente.";
                }                   
            }
            for (HeuristicaValorTabuleiro heuristicaValorTabuleiro : etapa.getHeuristicasValorTabuleiro()){
                
                if (nome.equalsIgnoreCase(heuristicaValorTabuleiro.getNome())){
                    return "Já existe uma Heurística de Valor de Tabuleiro chamada \""+nome+"\"!\nEscolha outro nome para o componente.";
                }                   
            }
            for (HeuristicaValorPeca heuristicaValorPeca : etapa.getHeuristicasValorPeca()){
                
                if (nome.equalsIgnoreCase(heuristicaValorPeca.getNome())){
                    return "Já existe uma Heurística de Valor de Peça chamada \""+nome+"\"!\nEscolha outro nome para o componente.";
                }                   
            }            
        }        
        
        return null;
    }
    
    public void preparaParaAnaliseHeuristica() throws Exception {

        for (Etapa etapa : etapas) {
            
            for (Heuristica heuristicaTransicaoEtapa : etapa.getHeuristicasTransicaoEtapa()) {
                heuristicaTransicaoEtapa.preparaParaAnaliseHeuristica();
            }
            
            for (Heuristica heuristicaValorPeca : etapa.getHeuristicasValorPeca()) {
                heuristicaValorPeca.preparaParaAnaliseHeuristica();
            }
            
            for (Heuristica heuristicaValorTabuleiro : etapa.getHeuristicasValorTabuleiro()) {
                heuristicaValorTabuleiro.preparaParaAnaliseHeuristica();
            }
        }
    }
}