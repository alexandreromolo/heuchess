package br.ufpr.inf.heuchess.representacao.heuristica;

import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.telas.editorheuristica.ColorIcon;
import java.awt.Color;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 31 de Julho de 2006, 17:20
 */
public class Regiao extends Componente {
    
    private ArrayList<Casa> casas;    
    private ArrayList<Casa> regiaoSimetrica;
    
    private ColorIcon colorIcon;
    private boolean   visivel;
            
    public Regiao(String nome, long idAutor, Tipo tipo) {
        
        super(nome,idAutor,tipo); 
        
        casas           = new ArrayList();
        regiaoSimetrica = new ArrayList();
    }
    
    public Regiao(String nome, long idAutor, Tipo tipo, String definicao) throws Exception {
        
        this(nome,idAutor,tipo);
        
        StringTokenizer tokens = new StringTokenizer(definicao, ",");
        
        if (!tokens.hasMoreTokens()){
            throw new IllegalArgumentException("Não foi encontrada nenhuma casa na definição da Região!");
        }
        
        while(tokens.hasMoreTokens()){
            String nomeCasa = tokens.nextToken();
            addCasa(Casa.porFEN(nomeCasa));
        }        
    }
    
    public final void addCasa(Casa casa){
        
        casas.add(casa);
        
        // Define casa Simétrica //
                
        Casa casaSimetrica = Casa.porIndices(7 - casa.getIndiceColuna(), 7 - casa.getIndiceLinha());
        
        regiaoSimetrica.add(casaSimetrica);
    }
    
    public void removeCasa(Casa casa){
        
        casas.remove(casa);
        
        // Define casa Simétrica //
        
        Casa casaSimetrica = Casa.porIndices(7 - casa.getIndiceColuna(), 7 - casa.getIndiceLinha());
        
        regiaoSimetrica.remove(casaSimetrica);
    }

    public void setColorIcon(ColorIcon colorIcon){
        this.colorIcon = colorIcon;
    }
    
    public ColorIcon getColorIcon(){
        return colorIcon;
    }

    public Color getColor() {
        
        if (colorIcon != null){
            return colorIcon.getCor();
        }else{
            return null;
        }
    }
     
    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }
    
    public ArrayList<Casa> getCasas(){
        return casas;
    }
    
    public ArrayList<Casa> getCasasSimetricas() {
         return regiaoSimetrica;
    }
    
    @Override
    public String getDescricaoDB(){  
        
        if (casas != null && casas.size() > 0) {
            
            StringBuilder builder = new StringBuilder();

            for (int x = 0; x < casas.size() - 1; x++) {
                builder.append(casas.get(x));
                builder.append(',');
            }
            
            builder.append(casas.get(casas.size() - 1));
            
            return builder.toString();
            
        } else {
            throw new RuntimeException("A Região não possui nenhuma Casa adicionada!");
        }
    }    
    
    @Override
    public String getDescricaoDHJOG() {
        
        StringBuilder builder = new StringBuilder();

        builder.append(DHJOG.TipoDado.CASAS);
        builder.append(" \"");
        builder.append(getNome());
        builder.append("\" ");
        builder.append(DHJOG.TXT_OPERADOR_ATRIBUICAO);
        builder.append(" {");

        if (casas != null && casas.size() > 0) {

            for (int x = 0; x < casas.size() - 1; x++) {
                builder.append(casas.get(x));
                builder.append(',');
            }

            builder.append(casas.get(casas.size() - 1));
        }

        builder.append('}');

        return builder.toString();
    }
    
    @Override
    public String getNomeTipoComponente() {
        return "Região";        
    }
        
    public boolean igual(Regiao regiao){
        
        if (regiao != null && getNome().equalsIgnoreCase(regiao.getNome())){
            
            if (casas.size() != regiao.getCasas().size()){
                return false;
            }
            
            for (int indice = 0; indice < casas.size(); indice++){
            
                if (!casas.get(indice).equals(regiao.getCasas().get(indice))){
                    return false;
                }
            }
                        
            return true;
        }else{            
            return false;
        }        
    }
    
    public Regiao geraClone() throws Exception {
        
        Regiao regiao;
        
        regiao = new Regiao(getNome(), getIdAutor(), getTipo(), getDescricaoDB());
        
        Componente.copiaAtributos(this, regiao, true);
        
        regiao.setColorIcon(getColorIcon());
        regiao.setVisivel(isVisivel());
        
        return regiao;
    }
}
