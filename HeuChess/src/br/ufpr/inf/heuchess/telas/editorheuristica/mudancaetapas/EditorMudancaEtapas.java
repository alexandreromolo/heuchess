package br.ufpr.inf.heuchess.telas.editorheuristica.mudancaetapas;

import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Etapa;
import br.ufpr.inf.heuchess.representacao.heuristica.Heuristica;
import br.ufpr.inf.heuchess.representacao.heuristica.HeuristicaTransicaoEtapa;
import com.jgraph.layout.SugiyamaLayoutAlgorithm;
import com.jgraph.layout.SugiyamaLayoutController;
import java.util.ArrayList;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 5 de Agosto de 2006, 18:18
 */
public class EditorMudancaEtapas {
    
    private ConjuntoHeuristico  conjuntoHeuristico;
    private JGraph              jGraph;
    
    private VerticeInicio       verticeInicio;
    private DefaultEdge         arestaInicial;
    private Etapa               etapaInicial;
    
    private ArrayList<VerticeEtapa>                   vertices;    
    private ArrayList<ArestaHeuristicaTransicaoEtapa> arestas;
    
    private GraphSelectionModel gsm;
    
    public EditorMudancaEtapas(ConjuntoHeuristico conjuntoHeuristico) {
        
        this.conjuntoHeuristico = conjuntoHeuristico;
        
        /////////////////////////////////////
        // Configurações Gerais do Grafico //
        /////////////////////////////////////
        
        // Define o modelo do gráfico como sendo o default //        
        GraphModel model = new DefaultGraphModel();
        
        // Cria o objeto principal do gráfico //        
        jGraph = new JGraph(model);
        
        // Define o Factory que especifica o View de cada tipo de célula //
        jGraph.getGraphLayoutCache().setFactory(new GPCellViewFactory());
        
        // Habilita o monitoramento dos eventos de mouse no Gráfico //
        jGraph.setEnabled(true);
        
        // Define número de cliques necessários para editar uma célula //
        //jGraph.setEditClickCount(2);
        
        // Desabilita a edição das celulas //
        jGraph.setEditable(false);
        
        // Define se é possível mover ou não as celulas e vértices //
        //jGraph.setMoveable(true);
        
        // Define quando é possível criar novas conecções entre vértices //
        jGraph.setConnectable(false);
        
        // Define quando é possível desconectar um arco de um vértice //
        jGraph.setDisconnectable(false);
        
        // Define quando um arco é desconectado de um vértice durante o movimento do vérice //
        jGraph.setDisconnectOnMove(false);
        
        // Determina se usa o grid para alinhas as celulas durante o movimento //
        //jGraph.setGridEnabled(true);
        
        // Define se o grid de edição é visivel //
        //jGraph.setGridVisible(true);
        
        // Define se é possível selecionar células //
        //jGraph.setSelectionEnabled(false);
        
        // Define se as células podem ter seus tamanhos alterados //
        //jGraph.setSizeable(false);
        
        // Desabilita a clonagem de células na movimentação com CTRL //
        jGraph.setCloneable(false);
        
        // Enable edit without final RETURN keystroke //
        //jGraph.setInvokesStopCellEditing(true);
        
        // When over a cell, jump to its default port (we only have one, anyway)
        jGraph.setJumpToDefaultPort(true);
        
        // Define a forma de seleção no gráfico //
        gsm = jGraph.getSelectionModel();
        gsm.setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
        
        configuraEtapas();
        
        atualiza();
    }
    
    public JGraph getGraph(){
        return jGraph;
    }
    
    public final void atualiza(){
        
        SugiyamaLayoutAlgorithm layout = new SugiyamaLayoutAlgorithm();
        layout.perform(jGraph,true,new SugiyamaLayoutController().getConfiguration());
        
        //SpringEmbeddedLayoutAlgorithm layout = new SpringEmbeddedLayoutAlgorithm();
        //layout.perform(jGraph,true,new SpringEmbeddedLayoutController().getConfiguration());
        
        // Limpa a seleção de células //
        gsm.clearSelection();
    }
    
    public final void configuraEtapas(){
        
        vertices = new ArrayList();
        arestas  = new ArrayList();
        
        /////////////////////////
        // Cria símbolo início //
        /////////////////////////
        
        verticeInicio = new VerticeInicio();
        
        //////////////////////////////
        // Adiciona a Etapa Inicial //
        //////////////////////////////
        
        etapaInicial = conjuntoHeuristico.getEtapaInicial();
        VerticeEtapa verticeEtapaInicial = new VerticeEtapa(etapaInicial);
        vertices.add(verticeEtapaInicial);
        
        /////////////////////////////////////////////////////////
        // Cria Aresta entre símbolo Inicial e a Etapa Inicial //
        /////////////////////////////////////////////////////////
                
        verticeInicio.addPort();       
        arestaInicial = new DefaultEdge("Etapa Inicial");
        arestaInicial.setSource(verticeInicio.getChildAt(verticeInicio.getChildCount()-1));
        verticeEtapaInicial.addPort();
        arestaInicial.setTarget(verticeEtapaInicial.getChildAt(verticeEtapaInicial.getChildCount()-1));                
        GraphConstants.setLineEnd(arestaInicial.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(arestaInicial.getAttributes(), true);  
            
        ////////////////////////////
        // Adiciona demais Etapas //
        ////////////////////////////
        
        for (Etapa etapa : conjuntoHeuristico.getEtapas()){
            
            if (etapa != conjuntoHeuristico.getEtapaInicial()){
                vertices.add(new VerticeEtapa(etapa));
            }
        }
        
        ///////////////////////////////////////////////////////
        // Adiciona as Heuristicas de Transicao entre Etapas //
        ///////////////////////////////////////////////////////
                
        for (VerticeEtapa verticeEtapa : vertices){
            
            ArrayList<HeuristicaTransicaoEtapa> heuristicasTransicao = verticeEtapa.getEtapa().getHeuristicasTransicaoEtapa();
            
            for(HeuristicaTransicaoEtapa heuristicaTransicao : heuristicasTransicao){
                
                arestas.add(new ArestaHeuristicaTransicaoEtapa(vertices, verticeEtapa,heuristicaTransicao));    
            }
        }
        
        ///////////////////////////////////
        // Insere os vertices via cache //
        ///////////////////////////////////
        
        jGraph.getGraphLayoutCache().insert(verticeInicio);
        jGraph.getGraphLayoutCache().insert(vertices.toArray());
        jGraph.getGraphLayoutCache().insert(arestaInicial);
        jGraph.getGraphLayoutCache().insert(arestas.toArray());
    }
    
    public void adicionaEtapa(Etapa etapa){
        
        VerticeEtapa verticeEtapa = new VerticeEtapa(etapa);
        
        vertices.add(verticeEtapa);
        jGraph.getGraphLayoutCache().insert(verticeEtapa);
        
        atualiza();
    }
    
    public void removeEtapa(Etapa etapa){
        
        for (VerticeEtapa verticeEtapa : vertices){
            
            if (etapa == verticeEtapa.getEtapa()){
                
                Object[] celulas = {verticeEtapa};
                
                jGraph.getGraphLayoutCache().remove(celulas,true,true);
                vertices.remove(verticeEtapa);
                
                atualiza();
                break;
            }
        }
    }
    
    public void adicionaHeuristicaTransicao(Etapa etapa, HeuristicaTransicaoEtapa heuristicaTransicao){
        
        for (VerticeEtapa verticeEtapa : vertices){
            
            if (etapa == verticeEtapa.getEtapa()){
                
                ArestaHeuristicaTransicaoEtapa aresta = new ArestaHeuristicaTransicaoEtapa(vertices, verticeEtapa, heuristicaTransicao);
                
                arestas.add(aresta);    
                jGraph.getGraphLayoutCache().insert(aresta);
                
                atualiza();
                break;
            }
        }                
    }
    
    public void removeHeuristicaTransicao(Heuristica heuristica){
        
        for (ArestaHeuristicaTransicaoEtapa arestaHeuristica : arestas){
            
            if (heuristica.getId() == arestaHeuristica.getHeuristica().getId()){
                
                Object[] celulas = {arestaHeuristica};
                
                jGraph.getGraphLayoutCache().remove(celulas,true,true);
                arestas.remove(arestaHeuristica);
                
                atualiza();
                break;
            }
        }
    }
    
    public void defineEtapaInicial(Etapa etapa){
        
        if (etapaInicial != etapa){
            
            Object[] inicio = {arestaInicial};
            jGraph.getGraphLayoutCache().remove(inicio,true,true);        
            
            for (VerticeEtapa verticeEtapa : vertices){
                
                if (etapa == verticeEtapa.getEtapa()){
                    
                    arestaInicial = new DefaultEdge("Etapa Inicial");
                    arestaInicial.setSource(verticeInicio.getChildAt(verticeInicio.getChildCount()-1));
                    
                    verticeEtapa.addPort();
                    
                    arestaInicial.setTarget(verticeEtapa.getChildAt(verticeEtapa.getChildCount()-1));                
                    
                    GraphConstants.setLineEnd(arestaInicial.getAttributes(), GraphConstants.ARROW_CLASSIC);
                    GraphConstants.setEndFill(arestaInicial.getAttributes(), true);  
                    
                    jGraph.getGraphLayoutCache().insert(arestaInicial);
                    etapaInicial = etapa;
                    
                    atualiza();
                    break;
                }
            }
        }
    }    
    
    public void seleciona(Object objeto){
        jGraph.setSelectionCell(objeto);
    }
}