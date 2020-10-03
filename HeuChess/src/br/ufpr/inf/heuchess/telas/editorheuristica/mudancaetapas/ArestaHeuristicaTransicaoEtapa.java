package br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas;

import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import br.ufpr.inf.heuchess.representacao.heuristica.Heuristica;
import br.ufpr.inf.heuchess.representacao.heuristica.HeuristicaTransicaoEtapa;
import br.ufpr.inf.utils.UtilsString;
import java.util.ArrayList;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 6 de Agosto de 2006, 16:51
 */
public class ArestaHeuristicaTransicaoEtapa extends DefaultEdge {
    
    private ArrayList<VerticeEtapa>  verticesEtapas;
    private VerticeEtapa             verticeOrigem;
    private VerticeEtapa             verticeDestino;
    private HeuristicaTransicaoEtapa heuristicaTransicao;
    
    public ArestaHeuristicaTransicaoEtapa(ArrayList<VerticeEtapa> verticesEtapas, VerticeEtapa verticeOrigem, HeuristicaTransicaoEtapa heuristica) {
        //super(UtilsString.cortaTextoMaior(heuristica.getCondicaoDHJOG().trim().replaceAll("      "," "), 20, true));
        super(heuristica.getCondicaoDHJOG().trim().replaceAll("      "," "));
        
        this.heuristicaTransicao = heuristica;
        this.verticesEtapas      = verticesEtapas;
        this.verticeOrigem       = verticeOrigem;
        
        this.verticeDestino = processaHeuristica();
        
        if (verticeDestino != null){
            
            verticeOrigem.addPort();       
            
            setSource(verticeOrigem.getChildAt(verticeOrigem.getChildCount()-1));
            
            verticeDestino.addPort();
            
            setTarget(verticeDestino.getChildAt(verticeDestino.getChildCount()-1));                
            
            GraphConstants.setLineEnd(getAttributes(), GraphConstants.ARROW_CLASSIC);
            GraphConstants.setEndFill(getAttributes(), true);                                   
            
            //GraphConstants.setLabelAlongEdge(getAttributes(), true);                   
        }
    }
    
    private VerticeEtapa processaHeuristica() {

        Etapa etapa = heuristicaTransicao.getProximaEtapa();
        
        for (VerticeEtapa verticeEtapa : verticesEtapas) {

            if (etapa.getNome().equalsIgnoreCase(verticeEtapa.toString())) {
                return verticeEtapa;
            }
        }
        
        return null;
    }
    
    public Heuristica getHeuristica(){
        return heuristicaTransicao; 
    }
    
    public Etapa getEtapa(){
        return verticeOrigem.getEtapa();
    }
}


/*
 edge.setSource(source.getChildAt(sourceAnschluss));
             edge.setTarget(target.getChildAt(targetAnschluss));
             GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_ORTHOGONAL);
             GraphConstants.setRouting(edge.getAttributes(), Globals.RoutingScheme);
            
             Map map = new Hashtable();
             // Add a Line End Attribute
             GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
             //GraphConstants.setLineBegin(map, GraphConstants.ARROW_TECHNICAL);
             // Add a label along edge attribute
             GraphConstants.setLabelAlongEdge(map, true);
             GraphConstants.setEditable(map, false);
            edge.setAttributes(new AttributeMap(map)); 
 */