package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.*;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 26 de Julho de 2006, 16:38
 */
public class ModelTreeHeuristicas extends DefaultTreeModel {
    
    private Etapa etapa;
    
    private DefaultMutableTreeNode treeHeuristicasPorTipo;
    private DefaultMutableTreeNode treeHeuristicasTransicaoEtapa;
    private DefaultMutableTreeNode treeHeuristicasValorPeca;
    private DefaultMutableTreeNode treeExpressaoCalculoHeuristico;
    private DefaultMutableTreeNode treeHeuristicasValorTabuleiro;
    
    private JTree jTree;
    
    public ModelTreeHeuristicas(Etapa etapa, Tipo complexidade) {
        
        super(new DefaultMutableTreeNode("Heurísticas por Tipo")); 
        
        treeHeuristicasPorTipo = (DefaultMutableTreeNode) getRoot();        
        
        if (complexidade == ConjuntoHeuristico.NIVEL_4_PLENO    ||
            complexidade == ConjuntoHeuristico.NIVEL_5_AVANCADO ||    
            complexidade == ConjuntoHeuristico.NIVEL_6_ESPECIALISTA){
            
            treeHeuristicasTransicaoEtapa = new DefaultMutableTreeNode("Transição de Etapa");
            treeHeuristicasPorTipo.add(treeHeuristicasTransicaoEtapa);
        }
        
        treeHeuristicasValorPeca = new DefaultMutableTreeNode("Valor de Peça");
        treeHeuristicasPorTipo.add(treeHeuristicasValorPeca); 
        
        treeExpressaoCalculoHeuristico = new DefaultMutableTreeNode("Expressão de Cálculo");
        treeExpressaoCalculoHeuristico.add(new DefaultMutableTreeNode(new ExpressaoCalculoHeuristico()));
        treeHeuristicasPorTipo.add(treeExpressaoCalculoHeuristico);            
        
        if (complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO ||
            complexidade == ConjuntoHeuristico.NIVEL_4_PLENO         ||
            complexidade == ConjuntoHeuristico.NIVEL_5_AVANCADO      ||    
            complexidade == ConjuntoHeuristico.NIVEL_6_ESPECIALISTA){
            
            treeHeuristicasValorTabuleiro = new DefaultMutableTreeNode("Valor de Tabuleiro");
            treeHeuristicasPorTipo.add(treeHeuristicasValorTabuleiro);    
        }
        
        this.etapa = etapa;        
        
        if (complexidade == ConjuntoHeuristico.NIVEL_4_PLENO    ||
            complexidade == ConjuntoHeuristico.NIVEL_5_AVANCADO ||    
            complexidade == ConjuntoHeuristico.NIVEL_6_ESPECIALISTA){
            
            for (int pos = 0; pos < etapa.getHeuristicasTransicaoEtapa().size(); pos++){
                
                super.insertNodeInto(new DefaultMutableTreeNode(etapa.getHeuristicasTransicaoEtapa().get(pos)),
                                     treeHeuristicasTransicaoEtapa, pos);
            }
        }
        
        for (int pos = 0; pos < etapa.getHeuristicasValorPeca().size(); pos++){
            
            super.insertNodeInto(new DefaultMutableTreeNode(etapa.getHeuristicasValorPeca().get(pos)),
                                 treeHeuristicasValorPeca, pos);            
        }
        
        if (complexidade == ConjuntoHeuristico.NIVEL_3_INTERMEDIARIO ||
            complexidade == ConjuntoHeuristico.NIVEL_4_PLENO         ||
            complexidade == ConjuntoHeuristico.NIVEL_5_AVANCADO      ||    
            complexidade == ConjuntoHeuristico.NIVEL_6_ESPECIALISTA){
            
            for (int pos = 0; pos < etapa.getHeuristicasValorTabuleiro().size(); pos++){
                
                super.insertNodeInto(new DefaultMutableTreeNode(etapa.getHeuristicasValorTabuleiro().get(pos)),
                                     treeHeuristicasValorTabuleiro, pos);            
            }
        }
    }        
    
    public void add(final Heuristica heuristica){
        
        ArrayList arrayList;
        final DefaultMutableTreeNode nodeRaiz;
                
        if (heuristica instanceof HeuristicaTransicaoEtapa){
            nodeRaiz  = treeHeuristicasTransicaoEtapa;
            arrayList = etapa.getHeuristicasTransicaoEtapa();
        }else
            if (heuristica instanceof HeuristicaValorPeca){
                nodeRaiz  = treeHeuristicasValorPeca;
                arrayList = etapa.getHeuristicasValorPeca();
            }else
                if (heuristica instanceof HeuristicaValorTabuleiro){
                    nodeRaiz  = treeHeuristicasValorTabuleiro;
                    arrayList = etapa.getHeuristicasValorTabuleiro();
                }else{
                    throw new IllegalArgumentException("Tipo Inválido de Heurística [" + heuristica.getTipo() + "]");
                }
        
        int pos = 0;

        for (; pos < arrayList.size(); pos++) {

            Heuristica heu = (Heuristica) arrayList.get(pos);

            if (heuristica.compareTo(heu) <= 0) {
                break;
            }
        }
        
        final int finalPos = pos;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ModelTreeHeuristicas.super.insertNodeInto(new DefaultMutableTreeNode(heuristica), nodeRaiz, finalPos);
            }
        });
         
        arrayList.add(pos, heuristica);
    }    
    
    public boolean remove(Heuristica heuristica){
        
        DefaultMutableTreeNode node = localizaNodeHeuristica(heuristica);
        
        if (node == null){
            return false;            
        }
            
        ArrayList arrayList;
        
        if (heuristica instanceof HeuristicaTransicaoEtapa){
            arrayList = etapa.getHeuristicasTransicaoEtapa();
        }else
            if (heuristica instanceof HeuristicaValorPeca){
                arrayList = etapa.getHeuristicasValorPeca();
            }else
                if (heuristica instanceof HeuristicaValorTabuleiro){
                    arrayList = etapa.getHeuristicasValorTabuleiro();
                }else{
                    throw new IllegalArgumentException("Tipo Inválido de Heurística [" + heuristica.getTipo() + "]");
                }
        
        super.removeNodeFromParent(node);           
        
        for (int x = 0; x < arrayList.size(); x++){
            
            if (((Heuristica) arrayList.get(x)).getId() == heuristica.getId()){
                return (arrayList.remove(x) != null ? true : false);
            }
        }
        
        return false;
    }    
    
    public DefaultMutableTreeNode localizaNodeHeuristica(Heuristica heuristica){
        
        if (heuristica != null){
            
            for (Enumeration e = treeHeuristicasPorTipo.breadthFirstEnumeration(); e.hasMoreElements(); ){
                
                DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
                
                if (current.getUserObject() instanceof Heuristica){
                    
                    if (heuristica.getId() == (((Heuristica)current.getUserObject()).getId())){
                        return current;
                    }
                }
            }
        }
        return null;
    }

    public void selecionaHeuristica(final Heuristica heuristica){        
        
        if (heuristica != null && jTree != null){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    
                    DefaultMutableTreeNode node = localizaNodeHeuristica(heuristica);

                    if (node != null) {

                        TreePath path = new TreePath(getPathToRoot(node));

                        jTree.setSelectionPath(path);
                        jTree.scrollPathToVisible(path);
                    }
                }
            });            
        }
    }

    public JTree getJTree() {
        return jTree;
    }

    public void setJTree(JTree jTree) {
        this.jTree = jTree;
    }
}